package pikachusrevenge.model;

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
    
    public void movePosition(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }
    
    public void movePosition(Direction d, double speed){
        movePosition(d.x * speed,d.y * speed);
    }
    
    public double distanceFrom(Position d){
        return Math.sqrt(Math.pow(d.y - this.y,2) + Math.pow(d.x - this.x,2));
    }

    @Override
    public String toString() {
        return "Position{" + "x=" + x + ", y=" + y + '}';
    }

}
