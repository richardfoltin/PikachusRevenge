package pikachusrevenge.model;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.signum;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.mapeditor.core.Map;
import org.mapeditor.core.MapLayer;
import org.mapeditor.core.MapObject;
import org.mapeditor.core.ObjectGroup;
import org.mapeditor.core.Properties;
import org.mapeditor.core.Tile;
import org.mapeditor.core.TileLayer;
import org.mapeditor.io.TMXMapReader;
import pikachusrevenge.gui.MainWindow;
import static pikachusrevenge.model.TilePosition.tileCoordFromMapCoord;
import pikachusrevenge.resources.Resource;
import pikachusrevenge.unit.MovingSprite;
import pikachusrevenge.unit.NPC;
import pikachusrevenge.unit.PokeBall;
import pikachusrevenge.unit.Pokemon;

/**
 * A pályát leíró osztály
 * @author Csaba Foltin
 */
public class Level {
    
    private final int id;
    private final ArrayList<PokeBall> thrownBalls = new ArrayList<>();
    private final ArrayList<MovingSprite> cleanUp = new ArrayList<>();
    private final ArrayList<NPC> npcs = new ArrayList<>();
    private final ArrayList<Pokemon> pokemons = new ArrayList<>();
    private final Model model;
    private List<MapLayer> layers;
    
    private Map map = null;
    private int time;
    private int maxPokemonCount;
    private int foundPokemonCount;
    private int livesAtBegining;
    private Position playerStartingPosition;
    private Position playerBackStartingPosition;
        
    private static final String[] MAP_NAME = {"Level1","Level2","Level3","Level4","Level5","Level6","Level7","Level8","Level9","Level10"};
    
    public Level(Model model, int id, int time, int lives) {
        this.id = id;
        this.livesAtBegining = lives;
        this.model = model;
        this.time = time;
        
        if (MAP_NAME.length < id) {
            System.err.println("No Level");
            id = 1;
        }
        
        try {
            TMXMapReader mapReader = new TMXMapReader();
            //InputStream mapStream = Resource.loadResource("level/" + MAP_NAME[id-1] + ".tmx");
            //this.map = mapReader.readMap(mapStream);
            URL mapURL = Resource.class.getResource("level/" + MAP_NAME[id-1] + ".tmx");
            this.map = mapReader.readMap(mapURL.getPath());
            this.layers = map.getLayers();

            findPokemonsOnMap();
            findUnitsOnMap();
        } catch (Exception e) {
            System.out.println("Error while reading the map:\n" + e.getMessage());
            e.printStackTrace();
            MainWindow.getInstance().showError("Can't load map!");
        }
    }

    /**
     * Megkeresi a térképen labdaként tárol pokémonokat, hozzáadja a modelhez és
     * a felső panelhez. Ha már létezett a modelben a pokémon és már meg volt
     * találva, akkor felfedi a felső panelen a képét, és eltávolítja a labdát
     * tartalmazó csempét a térképről.
     */
    private void findPokemonsOnMap() {
        for (MapLayer l : layers){
            if (l instanceof TileLayer){
                for (int i = 0; i < map.getWidth(); ++i) {
                    for (int j = 0; j < map.getHeight(); ++j){
                        Tile t = ((TileLayer)l).getTileAt(i, j);
                        if (hasProperty(t,"Ball")) {
                            maxPokemonCount++;
                            TilePosition tpos = new TilePosition(i,j,id);
                            Pokemon p;
                            if (model.getAllPokemonsWithPosition().containsKey(tpos)) {
                                p = model.getAllPokemonsWithPosition().get(tpos);
                                if (p.isFound()) {
                                    clearTileWithProperty("Ball",tpos);
                                    foundPokemonCount++;
                                }
                            } else {
                                p = new Pokemon(model,tpos,0);
                                model.getAllPokemonsWithPosition().put(tpos,p);
                            }
                            pokemons.add(p);
                        }                  
                    }
                }
            }
        }
    }
    
    /**
     * Megkeresi a térképen található be- és kilépési pontontot, és az NPC-ket.
     */
    private void findUnitsOnMap() {
        for (MapLayer l : layers){
            if (l instanceof ObjectGroup){
                for (MapObject o : ((ObjectGroup)l).getObjects()){
                    switch (o.getName()) {
                        case "Enter":
                            this.playerStartingPosition = new Position(o.getX(),o.getY());
                            break;
                        case "Back":
                            this.playerBackStartingPosition = new Position(o.getX(),o.getY());
                            break;
                        case "NPC":
                            Properties prop = o.getProperties();
                            int level = Integer.parseInt(prop.getProperty("Level", "1"));
                            NPC npc = new NPC(o, level, model);
                            npcs.add(npc);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
    
    /**
     * A 9. pályához használt metódus, ahol a játékban lévő line-of-sight szabályok
     * kicsit másak, mert a falon nem látnak át az NPC-k. A metódus kiszámolja,
     * hogy az NPC és a játékos között található-e fal.
     * @param npcPosition az NPC pozíciója
     * @param playerPosition a játékos pozíciója
     * @return true, ha nem található fal köztük
     */
    public boolean isInLineOfSight(Position npcPosition, Position playerPosition) {
        if (id != 9) return true; // csak a 9. pályán van LOS
        
        Position pos = new Position(npcPosition);
        double dx = playerPosition.x - npcPosition.x;
        double dy = playerPosition.y - npcPosition.y;
        double incX = (abs(dx) >= abs(dy)) ? (double)signum(dx) : dx/abs(dy); 
        double incY = (abs(dx) >= abs(dy)) ? dy/abs(dx) : (double)signum(dy); 
        int longer = (int)max(abs(dx),abs(dy));
        for (int i = 0; i <= longer; ++i) {
            int x = tileCoordFromMapCoord(pos.x += incX);
            int y = tileCoordFromMapCoord(pos.y += incY);
            for (MapLayer l : layers){
                if (l instanceof TileLayer){
                    Tile t = ((TileLayer)l).getTileAt(x, y);
                    if (hasProperty(t,"LOS")) {
                        return false;
                    }
                }
          }
        }
        
        return true;
    }
    
    public void increaseFoundPokemonCount() {
        foundPokemonCount++;
    }
    
    /**
     * Visszaadja, hogy a játékos megtalált-e már annyi pokémont a pályán,
     * hogy a következő pályára léphet.
     * @return true, ha a következő pályára léphet
     */
    public boolean canAdvanceToNextLevel() {
        if (foundPokemonCount >= minimumFoundPokemon()) return true;
        else return false;
    }
    
    public int minimumFoundPokemon() {
        return (maxPokemonCount + 1) / 2;
    }
    
    /**
     * Letörli a megadott pozícióban és tulajdonsággal rendelkező csempét a térképről
     * @param prop a tulajdonság
     * @param tpos a csempe pozíciója
     */
    public void clearTileWithProperty(String prop, TilePosition tpos){
        for (MapLayer l : layers){
            if (l instanceof TileLayer){
                Tile t = ((TileLayer)l).getTileAt(tpos.getX(),tpos.getY());
                if (hasProperty(t,prop)) {
                    ((TileLayer)l).setTileAt(tpos.getX(), tpos.getY(), null);
                }
            }
        }
    }
    
    /**
     * Megmondja, hogy a csempe tartalmazza-e a megadott tulajdonságot
     * @param t a csempe
     * @param property a tulajdonság
     * @return true, ha tartalmazza
     */
    public static boolean hasProperty(Tile t, String property) {
        if (t != null) {
            Properties prop = t.getProperties();
            if (!prop.isEmpty()) {
                if (Boolean.parseBoolean(prop.getProperty(property, "false"))) return true;
            }
        }     
        return false;
    }
    
    /**
     * A játék fő ciklusában meghívott metódus.
     * Továbbítja a fő ciklushívást a pályán található egységekhez, ha ők mozognak,
     * és eltávolítja az eltávolítandó listában található elemeket a térképről.
     */
    public void loop(){
        for (NPC npc : npcs) if (npc.isLooping()) npc.loop();
        for (Pokemon p : pokemons) if (p.isLooping() && p.getTilePosition().getLevel() == id) p.loop();
        for (PokeBall pb : thrownBalls) if (pb.isLooping()) pb.loop();
        
        //cleanUp
        for (MovingSprite m : cleanUp) {
            if (m instanceof PokeBall) thrownBalls.remove((PokeBall)m);
            else if (m instanceof NPC) npcs.remove((NPC)m);
            else if (m instanceof Pokemon) pokemons.remove((Pokemon)m);
        }
        cleanUp.clear();
    }    
    
    public void addCleanUp(MovingSprite sprite) {
        cleanUp.add(sprite);
    }
    
    public int increaseTime() {return ++time;}
    
    public ArrayList<PokeBall> getThrownBalls() {return thrownBalls;}
    public ArrayList<NPC> getNpcs() {return npcs;}
    public ArrayList<Pokemon> getPokemons() {return pokemons;}
    public int getId() {return id;}
    public int getTime() {return time;}
    public int getLivesAtBegining() {return livesAtBegining;}
    public Map getMap() {return map;}
    public Position getPlayerStartingPosition(boolean forward) {
        return (forward) ? playerStartingPosition : playerBackStartingPosition;
    }
    
}
