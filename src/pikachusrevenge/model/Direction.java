package pikachusrevenge.model;

import static java.lang.Math.floor;
import static java.lang.Math.signum;
import java.util.Arrays;

public enum Direction {
    UPLEFT(-1,-1),
    UP(0, -1), 
    UPRIGHT(1,-1),
    LEFT(-1, 0), 
    STOP(0, 0),
    RIGHT(1, 0),
    DOWNLEFT(-1,1),
    DOWN(0, 1), 
    DOWNRIGHT(1,1);
    
    public final double x;
    public final double y;

    Direction(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    public static Direction getDirection(double x, double y){
        int id = ((int)x+1) + ((int)y+1)*3; // linux chown trükk
        Direction d = Arrays.asList(Direction.values()).get(id);
        return d;
    }
    
    public static Direction getDirection(Position from, Position to){
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        int sixth = (int)floor(Math.abs(Math.atan(dy/dx)) / (Math.PI / 6));
        switch (sixth) {
            case 0 : return getDirection(signum(dx),0);
            case 1 : return getDirection(signum(dx),signum(dy));
            case 2 : 
            case 3 : return getDirection(0,signum(dy));
            default: return Direction.STOP;
        }
    }
}
