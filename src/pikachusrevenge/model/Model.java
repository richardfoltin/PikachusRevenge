package pikachusrevenge.model;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.PathIterator;
import static java.lang.Math.floor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.Timer;
import org.mapeditor.core.Map;
import org.mapeditor.core.MapLayer;
import org.mapeditor.core.MapObject;
import org.mapeditor.core.ObjectGroup;
import org.mapeditor.core.Properties;
import org.mapeditor.core.Tile;
import org.mapeditor.core.TileLayer;
import pikachusrevenge.gui.MainWindow;
import static pikachusrevenge.gui.MapView.GRIDSIZE;
import pikachusrevenge.unit.MovingSprite;
import pikachusrevenge.unit.NPC;
import pikachusrevenge.unit.Player;
import pikachusrevenge.unit.PokeBall;
import pikachusrevenge.unit.Pokemon;

public class Model implements ActionListener {
        
    private List<MapLayer> layers;
    private final ArrayList<NPC> npcs;
    private final ArrayList<Pokemon> pokemons;
    private Map map;
    private MainWindow mainWindow;
    private final Player player;
    private final ArrayList<PokeBall> thrownBalls;
    private final ArrayList<MovingSprite> cleanUp;
    private int ballCount;
    private final Timer timer;
    private final Timer clock;
    private int time;
    public Rectangle MAP_RECTANGLE;

    private final static int MAIN_LOOP = 40;  
    
    public Model (Map map, MainWindow mainWindow){
        this.layers = map.getLayers();
        this.npcs = new ArrayList<>();
        this.thrownBalls = new ArrayList<>();
        this.pokemons = new ArrayList<>();
        this.cleanUp = new ArrayList<>();
        this.player = new Player(this);
        this.mainWindow = mainWindow;
        this.map = map;
        MAP_RECTANGLE = new Rectangle(0, 0, map.getWidth() * GRIDSIZE, map.getHeight() * GRIDSIZE);
        this.timer = new Timer(MAIN_LOOP, this);
        this.clock = new Timer(1000, (ActionEvent e) -> {
            ++time;
            mainWindow.getStats().updateTimeLabel(time);
        });
        
        addUnitsToMap();
    }
    
    public void changeMap(Map map) {
        this.layers = map.getLayers();
        this.map = map;
        MAP_RECTANGLE = new Rectangle(0, 0, map.getWidth() * GRIDSIZE, map.getHeight() * GRIDSIZE);
        
        npcs.clear();
        thrownBalls.clear();
        pokemons.clear();
        cleanUp.clear();
        mainWindow.getStats().clearPane();
        
        addUnitsToMap();  
    }
    

    public boolean canMoveTo(Rectangle target){
        if (!MAP_RECTANGLE.contains(target)) return false;
        
        PathIterator pi = target.getPathIterator(null);
        ArrayList<Collision> collisions = new ArrayList<>();
        
        while (!pi.isDone()) {
            double[] coords = new double[2];
            int type = pi.currentSegment(coords);
            if (type!= PathIterator.SEG_CLOSE) {
                collisions.add(Collision.collisionOnTileAt(layers,tileCoordFromMapCoord(coords[0]), tileCoordFromMapCoord(coords[1]))) ;
            }
            pi.next();
        }
        
        return Collision.canMoveToCollisions(collisions);
    }
    
    public void ballThrow(Position from, double speed, NPC owner){
        PokeBall ball = new PokeBall(from.x, from.y, speed, this, owner);
        thrownBalls.add(ball);
        ball.startMoving();
    }
    
    public void ballReachedPlayer(PokeBall ball) {
        cleanUp.add(ball);
        for (Pokemon p : pokemons) p.stopMoving();
        player.caught();
        writeInfo(String.format("%s caught you!",ball.getOwner().getName()));
    }

    public void playerInteraction(){
        if (player.isAtSign()){
            if (canMoveToNextLevel()) {
                mainWindow.loadNextLevel();
            }
            else writeInfo("Can't move to next level yet!");
        }
    }
    
    public void gameOver(){
        System.out.println("Game over");
    }
    
    public void startGame() {
        for (int i = 0; i < player.getLives(); ++i){
            mainWindow.getStats().addLife();
        }
        clock.start();
        timer.start();
        countPokemonsAndAddToStats();
        for (NPC npc : npcs) npc.startMoving();
        player.startMoving();
    }
    
    public void stopGame() {
        clock.stop();
        timer.stop();
        player.stopMoving();
    }
    
    public Pokemon checkBallPokemonAt(Rectangle target){
        
        PathIterator pi = target.getPathIterator(null);
        
         while (!pi.isDone()) {
            double[] coords = new double[2];
            int type = pi.currentSegment(coords);
            if (type!= PathIterator.SEG_CLOSE) {
                int x = tileCoordFromMapCoord(coords[0]);
                int y = tileCoordFromMapCoord(coords[1]);
                for (MapLayer l : layers){
                    if (l instanceof TileLayer){
                        Tile t = ((TileLayer)l).getTileAt(x,y);
                        if (hasProperty(t,"Ball")) {
                            ((TileLayer)l).setTileAt(x, y, null);
                            for (Pokemon p : pokemons) {
                                if (p.getTileX() == x && p.getTileY() == y) {
                                    writeInfo("You have found " + Pokemon.pokemonName[p.getId()-1]);
                                    p.found();
                                    return p;
                                }
                            }
                        }
                    } 
                }
            }
            pi.next();
        }  
         
        return null;
    }
    
    public boolean checkSign(Position pos) {
        for (MapLayer l : layers){
            if (l instanceof TileLayer){
                Tile t = ((TileLayer)l).getTileAt(tileCoordFromMapCoord(pos.x),tileCoordFromMapCoord(pos.y));
                if (hasProperty(t,"Sign")) return true;
            } 
        } 
        return false;
    }
    
    private static boolean hasProperty(Tile t, String property) {
        if (t != null) {
            Properties prop = t.getProperties();
            if (!prop.isEmpty()) {
                if (Boolean.parseBoolean(prop.getProperty(property, "false"))) return true;
            }
        }     
        return false;
    }
    
    private static int tileCoordFromMapCoord(double coord){
        return (int)floor((coord) / GRIDSIZE);
    }
    
    public static double tileCenterFromTileCoord(int mapCoord){
        return (double)mapCoord * GRIDSIZE + GRIDSIZE/2;
    }
    
    public void playerMoveTowards(Direction d){
        player.moveToDirection(d);
    }
    
    private void writeInfo(String str){
        mainWindow.getFooter().write(str);
    }
    
    private void countPokemonsAndAddToStats() {
        for (MapLayer l : layers){
            if (l instanceof TileLayer){
                for (int i = 0; i < map.getWidth(); ++i) {
                    for (int j = 0; j < map.getHeight(); ++j){
                        Tile t = ((TileLayer)l).getTileAt(i, j);
                        if (hasProperty(t,"Ball")) {
                            ballCount++;
                            JLabel label = mainWindow.getStats().addBall();
                            Pokemon p = new Pokemon(i,j,this,label);
                            pokemons.add(p);
                        }                  
                    }
                }
            }
        }
    }
    
    private void addUnitsToMap() {
        for (MapLayer l : layers){
            if (l instanceof ObjectGroup){
                for (MapObject o : ((ObjectGroup)l).getObjects()){
                    if (o.getName().equals("Enter")){
                        player.setStartingPostion(o.getX(),o.getY());
                    }else if (o.getName().equals("NPC")) {
                        Properties prop = o.getProperties();
                        
                        int level = Integer.parseInt(prop.getProperty("Level", "1"));
                        NPC npc = new NPC(o, level, this);
                        npcs.add(npc);
                    }
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer){
            if (player.isMoving()) player.loop();
            for (NPC npc : npcs) if (npc.isMoving()) npc.loop();
            for (Pokemon p : pokemons) if (p.isMoving()) p.loop();
            for (PokeBall pb : thrownBalls) if (pb.isMoving()) pb.loop();
            cleanUp();
            mainWindow.getFooter().loop();
            mainWindow.repaintMap();
            movePanelTo(player.getPosition());
        }
    }    
    
    private void cleanUp() {
        for (MovingSprite m : cleanUp) {
            if (m instanceof PokeBall) thrownBalls.remove(m);
            else if (m instanceof NPC) npcs.remove(m);
            else if (m instanceof Pokemon) pokemons.remove(m);
        }
        cleanUp.clear();
    }
    
    public void movePanelTo(Position position){
        mainWindow.scrollTo(position);
    }

    public boolean canThrow(NPC npc){
        if (thrownBalls.isEmpty()) return true;
        for (PokeBall b : thrownBalls) {
            if (b.getOwner() == npc) return false;
        }
        return true;
    }
    
    public boolean canMoveToNextLevel() {
        if (player.getFreedCount() == ballCount) return true;
        return true;
    }
    
    public void setTime(int time){this.time = time;}
    
    public ArrayList<NPC> getNpcs() {return npcs;}
    public ArrayList<Pokemon> getPokemons() {return pokemons;}
    public ArrayList<PokeBall> getThrownBalls() {return thrownBalls;}
    public int getBallCount() {return ballCount;}
    public Player getPlayer() {return player;}
}
