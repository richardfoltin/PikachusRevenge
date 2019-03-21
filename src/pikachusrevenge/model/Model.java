package pikachusrevenge.model;

import java.awt.Rectangle;
import java.awt.geom.PathIterator;
import static java.lang.Math.floor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import org.mapeditor.core.Map;
import org.mapeditor.core.MapLayer;
import org.mapeditor.core.MapObject;
import org.mapeditor.core.ObjectGroup;
import org.mapeditor.core.Properties;
import org.mapeditor.core.Tile;
import org.mapeditor.core.TileLayer;
import static pikachusrevenge.LevelWindow.GRIDSIZE;
import pikachusrevenge.gui.StatsPanel;
import pikachusrevenge.unit.NPC;
import pikachusrevenge.unit.Player;
import pikachusrevenge.unit.PokeBall;
import pikachusrevenge.unit.Pokemon;

public class Model {
    
    private final List<MapLayer> layers;
    private final ArrayList<NPC> npcs;
    private final ArrayList<Pokemon> pokemons;
    private final Map map;
    private final StatsPanel stats;
    private Player player;
    private final ArrayList<PokeBall> thrownBalls;
    private int ballcount;
    public final Rectangle MAP_RECTANGLE;
    
    public Model (Map map, StatsPanel stats){
        this.layers = map.getLayers();
        this.npcs = new ArrayList<>();
        this.thrownBalls = new ArrayList<>();
        this.pokemons = new ArrayList<>();
        this.map = map;
        this.stats = stats;
        MAP_RECTANGLE = new Rectangle(0, 0, map.getWidth() * GRIDSIZE, map.getHeight() * GRIDSIZE);
        
        addUnits();
        countPokemons();
    }
    
    public boolean canMoveTo(Rectangle target){
        if (!MAP_RECTANGLE.contains(target)) return false;
        
        PathIterator pi = target.getPathIterator(null);
        
        while (!pi.isDone()) {
            double[] coords = new double[2];
            int type = pi.currentSegment(coords);
            if (type!= PathIterator.SEG_CLOSE) {
                if (collisionOnTileAt(tileCoordFromMapCoord(coords[0]), tileCoordFromMapCoord(coords[1]))) return false;
            }
            pi.next();
        }
        
        return true;
    }
    
    private boolean collisionOnTileAt(int tileX, int tileY) {                 
        boolean water = false;
        boolean collision = false;
        boolean bridge = false;
        boolean stairs = false;
        
        for (MapLayer l : layers){
            if (l instanceof TileLayer){
                Tile t = ((TileLayer)l).getTileAt(tileX, tileY);
                if (t != null) {
                    Properties prop = t.getProperties();
                    if (!prop.isEmpty()) {
                        water = water || Boolean.parseBoolean(prop.getProperty("Water", "false"));
                        collision = collision || Boolean.parseBoolean(prop.getProperty("Collision", "false"));
                        bridge = bridge || Boolean.parseBoolean(prop.getProperty("Bridge", "false"));
                        stairs = stairs || Boolean.parseBoolean(prop.getProperty("Stairs", "false"));
                    }
                }
            } 
        }
        
        
        if (bridge) water = false;
        if (stairs) collision = false;
        
        if (water || collision) return true;
        else return false;
    }
    
    public void ballThrow(Position from, double speed, NPC owner){
        PokeBall ball = new PokeBall(from.x, from.y, speed, this, owner);
        thrownBalls.add(ball);
        ball.startMoving();
    }
    
    public void ballReachedPlayer(PokeBall ball) {
        thrownBalls.remove(ball);
        player.caught();
    }
    
    public void gameOver(){
        System.out.println("Game over");
    }
    
    public void startMoving() {
        for (NPC npc : npcs) npc.startMoving();
        player.startMoving();
    }
    
    public boolean checkBallAt(Rectangle target){
        
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
                                    p.found();
                                }
                            }
                            return true;
                        }
                    } 
                }
            }
            pi.next();
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
    
    private void countPokemons() {
        for (MapLayer l : layers){
            if (l instanceof TileLayer){
                for (int i = 0; i < map.getWidth(); ++i) {
                    for (int j = 0; j < map.getHeight(); ++j){
                        Tile t = ((TileLayer)l).getTileAt(i, j);
                        if (hasProperty(t,"Ball")) {
                            ballcount++;
                            JLabel label = stats.addBall();
                            Pokemon p = new Pokemon(i,j,this,0);
                            p.setLabel(label);
                            pokemons.add(p);
                        }                  
                    }
                }
            }
        }
    }
    
    private void addUnits() {
        for (MapLayer l : layers){
            if (l instanceof ObjectGroup){
                for (MapObject o : ((ObjectGroup)l).getObjects()){
                    if (o.getName().equals("Enter")){
                        player = new Player(o.getX(),o.getY(),this);
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
    
    public boolean canThrow(NPC npc){
        if (thrownBalls.size() == 0) return true;
        for (PokeBall b : thrownBalls) {
            if (b.getOwner() == npc) return false;
        }
        return true;
    }
    
    public ArrayList<NPC> getNpcs() {return npcs;}
    public ArrayList<Pokemon> getPokemons() {return pokemons;}
    public ArrayList<PokeBall> getThrownBalls() {return thrownBalls;}
    public int getBallCount() {return ballcount;}
    public Player getPlayer() {return player;}
    public StatsPanel getStats() {return stats;}


}
