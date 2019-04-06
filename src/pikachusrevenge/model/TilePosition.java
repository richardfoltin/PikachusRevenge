package pikachusrevenge.model;

import static java.lang.Math.floor;
import static pikachusrevenge.gui.MapView.GRIDSIZE;

public class TilePosition {
    private final int x;
    private final int y;
    private final int level;

    public TilePosition(int x, int y, int level) {
        this.x = x;
        this.y = y;
        this.level = level;
    }
    
    public static TilePosition fromMapPosition(Position p, int level){
        return new TilePosition((int)floor((p.x) / GRIDSIZE),(int)floor((p.y) / GRIDSIZE), level);
    }
    
    public static int tileCoordFromMapCoord(double coord){
        return (int)floor((coord) / GRIDSIZE);
    }
    
    public static Position tileCenter(TilePosition tpos){
        return new Position((double)tpos.getX() * GRIDSIZE + GRIDSIZE/2,(double)tpos.getY() * GRIDSIZE + GRIDSIZE/2);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        
        final TilePosition other = (TilePosition) obj;
        
        if (this.x != other.x) return false;
        if (this.y != other.y) return false;
        if (this.level != other.level) return false;
        return true;
    }

    @Override
    public String toString() {
        return "TilePosition{" + "x=" + x + ", y=" + y + ", level=" + level + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.x;
        hash = 83 * hash + this.y;
        hash = 83 * hash + this.level;
        return hash;
    }
    
    public int getX() {return x;}
    public int getY() {return y;}
    public int getLevel() {return level;}
}
