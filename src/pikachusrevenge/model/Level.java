package pikachusrevenge.model;

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
import pikachusrevenge.unit.MovingSprite;
import pikachusrevenge.unit.NPC;
import pikachusrevenge.unit.PokeBall;
import pikachusrevenge.unit.Pokemon;

public class Level {
    
    private final int id;
    private final ArrayList<PokeBall> thrownBalls;
    private final ArrayList<MovingSprite> cleanUp;
    private final ArrayList<NPC> npcs;
    private final ArrayList<Pokemon> pokemons;
    private int time;
    private Map map = null;
    private final Model model;
    private List<MapLayer> layers;
    private int maxPokemonCount;
    private int foundPokemonCount;
    private Position playerStartingPosition;
    
    private static String[] mapName = {"MapTest","Level1"};
    
    public Level(Model model, int id, int time) {
        this.id = id;
        
        try {
            TMXMapReader mapReader = new TMXMapReader();
            this.map = mapReader.readMap("src\\pikachusrevenge\\resources\\level\\" + mapName[id-1] + ".tmx");
        } catch (Exception e) {
            System.out.println("Error while reading the map:\n" + e.getMessage());
        }
        
        this.time = time;
        this.pokemons = new ArrayList<>();
        this.thrownBalls = new ArrayList<>();
        this.cleanUp = new ArrayList<>();
        this.npcs = new ArrayList<>();
        this.layers = map.getLayers();
        this.model = model;
        
        findPokemonsOnMap();
        findUnitsOnMap();
    }

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
                            if (model.getAllPokemons().containsKey(tpos)) {
                                p = model.getAllPokemons().get(tpos);
                                if (p.isFound()) {
                                    clearTileWithProperty("Ball",tpos);
                                    foundPokemonCount++;
                                }
                            } else {
                                p = new Pokemon(model,tpos,0);
                                model.getAllPokemons().put(tpos,p);
                            }
                            pokemons.add(p);
                        }                  
                    }
                }
            }
        }
    }
    
    private void findUnitsOnMap() {
        for (MapLayer l : layers){
            if (l instanceof ObjectGroup){
                for (MapObject o : ((ObjectGroup)l).getObjects()){
                    if (o.getName().equals("Enter")){
                        this.playerStartingPosition = new Position(o.getX(),o.getY());
                    }else if (o.getName().equals("NPC")) {
                        Properties prop = o.getProperties();
                        
                        int level = Integer.parseInt(prop.getProperty("Level", "1"));
                        NPC npc = new NPC(o, level, model);
                        npcs.add(npc);
                    }
                }
            }
        }
    }
    
    public boolean canAdvanceToNextLevel() {
        if (maxPokemonCount == foundPokemonCount) return true;
        return true;
    }
    
    public void addCleanUp(MovingSprite sprite) {
        cleanUp.add(sprite);
    }
    
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
    
    public static boolean hasProperty(Tile t, String property) {
        if (t != null) {
            Properties prop = t.getProperties();
            if (!prop.isEmpty()) {
                if (Boolean.parseBoolean(prop.getProperty(property, "false"))) return true;
            }
        }     
        return false;
    }
    
    public void loop(){
        for (NPC npc : npcs) if (npc.isLooping()) npc.loop();
        for (Pokemon p : pokemons) if (p.isLooping() && p.getTilePosition().getLevel() == id) p.loop();
        for (PokeBall pb : thrownBalls) if (pb.isLooping()) pb.loop();
        cleanUp();
    }    
    
    private void cleanUp() {
        for (MovingSprite m : cleanUp) {
            if (m instanceof PokeBall) thrownBalls.remove(m);
            else if (m instanceof NPC) npcs.remove(m);
            else if (m instanceof Pokemon) pokemons.remove(m);
        }
        cleanUp.clear();
    }

    public int increaseTime() {return ++time;}
    
    public ArrayList<PokeBall> getThrownBalls() {return thrownBalls;}
    public ArrayList<NPC> getNpcs() {return npcs;}
    public ArrayList<Pokemon> getPokemons() {return pokemons;}
    public int getId() {return id;}
    public int getTime() {return time;}
    public Map getMap() {return map;}
    public Position getPlayerStartingPosition() {return playerStartingPosition;}
    
}
