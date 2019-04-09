package pikachusrevenge.model;

/**
 * A játékban található koordinátarendszer x,y koordinátáit aggregáló osztály
 * @author Csaba Foltin
 */
public class Position {
    public double x;
    public double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }   
   
    public Position(double[] coords){
        this.x = coords[0];
        this.y = coords[1];
    }
    
    public Position (Position pos){
        this.x = pos.x;
        this.y = pos.y;
    }
    
    public Position movePosition(double dx, double dy) {
        this.x += dx;
        this.y += dy;
        return this;
    }
    
    public void movePosition(Direction d, double speed){
        movePosition(d.x * speed,d.y * speed);
    }
    
    /**
     * Pithagorasz tétel alapján kiszámolja két pont távolságát
     * @param d a másik pont
     * @return a távolság
     */
    public double distanceFrom(Position d){
        return Math.sqrt(Math.pow(d.y - this.y,2) + Math.pow(d.x - this.x,2));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        
        final Position other = (Position) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) return false;
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) return false;
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("(%.1f,%.1f)",x,y);
    }

}
