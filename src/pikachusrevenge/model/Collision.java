package pikachusrevenge.model;

import java.util.ArrayList;
import java.util.List;
import org.mapeditor.core.MapLayer;
import org.mapeditor.core.Properties;
import org.mapeditor.core.Tile;
import org.mapeditor.core.TileLayer;

public enum Collision {
    WATER,
    STAIRS,
    COLLISION,
    BRIDGE,
    EMPTY;
    
    public static Collision collisionOnTileAt(List<MapLayer> layers, int tileX, int tileY) {                 
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
         
        if (bridge) return BRIDGE;
        if (stairs) return STAIRS;
        if (collision) return COLLISION;
        if (water) return WATER;
        
        return EMPTY;
    }
    
    public static boolean canMoveToCollisions(ArrayList<Collision> collisions){
        boolean stairs = false;
        boolean bridge = false;
        boolean collision = false;
        boolean water = false;
        
        for (Collision c : collisions){
            if (c == BRIDGE) bridge = true;
            if (c == COLLISION) collision = true;
            if (c == WATER) water = true;
            if (c == STAIRS) stairs = true;
        }
        
        if (stairs) return true; // stairs overwrites others
        if (collision) return false;
        if (water) return false;
        if (bridge) return true;
       
        return true;
    }
    
}
