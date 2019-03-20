package pikachusrevenge.model;

import static java.lang.Math.floor;
import java.util.ArrayList;
import java.util.List;
import org.mapeditor.core.Map;
import org.mapeditor.core.MapLayer;
import org.mapeditor.core.MapObject;
import org.mapeditor.core.ObjectGroup;
import org.mapeditor.core.Properties;
import org.mapeditor.core.Tile;
import org.mapeditor.core.TileLayer;
import static pikachusrevenge.LevelWindow.GRIDSIZE;
import pikachusrevenge.unit.MovingSprite;
import pikachusrevenge.unit.NPC;
import pikachusrevenge.unit.Player;
import pikachusrevenge.unit.PokeBall;
import pikachusrevenge.unit.Unit;

public class Model {
    
    private List<MapLayer> layers;
    private ArrayList<NPC> npcs;
    private Map map;
    private Player player;
    private ArrayList<PokeBall> thrownBalls;
    private int ballCount;
    public final int MAPWIDTH;
    public final int MAPHEIGHT;
    
    public Model (Map map){
        this.layers = map.getLayers();
        this.npcs = new ArrayList<>();
        this.thrownBalls = new ArrayList<>();
        this.map = map;
        MAPWIDTH = map.getWidth() * GRIDSIZE;
        MAPHEIGHT = map.getHeight() * GRIDSIZE;
        
        addUnits();
        countBalls();
    }
    
    public boolean canMoveTo(MovingSprite unit, double x, double y, Direction d){
        if (x > MAPWIDTH || x < 0) return false;
        if (y > MAPHEIGHT || y < 0) return false;
        
        // négyzetnek tekintve
        int newEdgeX = tileCoordFromMapCoord(x + unit.getCollisionRadius() * d.x);
        int newEdgeY = tileCoordFromMapCoord(y + unit.getCollisionRadius() * d.y);
           
        if (newEdgeX >= map.getWidth() || x < 0) return false;
        if (newEdgeY >= map.getHeight() || y < 0) return false;
        
        //System.out.println("Checking : " + newEdgeX + "," + newEdgeY );
        
        boolean water = false;
        boolean collision = false;
        boolean bridge = false;
        boolean stairs = false;
        for (MapLayer l : layers){
            if (l instanceof TileLayer){
                Tile t = ((TileLayer)l).getTileAt(newEdgeX, newEdgeY);
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
        if (water || collision) return false;
        
        return true;
    }
    
    public void removeTile(double x, double y) {
        int tileX = tileCoordFromMapCoord(x);
        int tileY = tileCoordFromMapCoord(y);  
        
        for (MapLayer l : layers){
            if (l instanceof TileLayer){
                Tile t = ((TileLayer)l).getTileAt(tileX, tileY);
                if (hasProperty(t,"Ball")) {
                    ((TileLayer)l).setTileAt(tileX, tileY, null);
                    break;
                }
            } 
        } 
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
    
    public boolean isBallAt(double x, double y){
        int tileX = tileCoordFromMapCoord(x);
        int tileY = tileCoordFromMapCoord(y);  
        
        for (MapLayer l : layers){
            if (l instanceof TileLayer){
                Tile t = ((TileLayer)l).getTileAt(tileX, tileY);
                if (hasProperty(t,"Ball")) return true;
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
    
    public void playerMoveTowards(Direction d){
        player.moveToDirection(d);
    }
    
    private void countBalls() {
        ballCount = 0;
        for (MapLayer l : layers){
            if (l instanceof TileLayer){
                for (int i = 0; i < map.getWidth(); ++i) {
                    for (int j = 0; j < map.getHeight(); ++j){
                        Tile t = ((TileLayer)l).getTileAt(i, j);
                        if (hasProperty(t,"Ball")) ballCount++;                  
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
    public ArrayList<PokeBall> getThrownBalls() {return thrownBalls;}
    public int getBallCount() {return ballCount;}
    public Player getPlayer() {return player;}


}
