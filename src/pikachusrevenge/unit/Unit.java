package pikachusrevenge.unit;

import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;


public class Unit extends MovingSprite {

    protected String name;
    protected Direction startDirection;
    
    public final static int UNITSIZE = 32;
    
    public Unit( Model model){
        super(model);
    }

    public void restartFromStratingPoint() {
        this.pos.x = startPosition.x;
        this.pos.y = startPosition.y;
        this.nextDirection = Direction.STOP;
        this.direction = startDirection;
    }
    
    public String getName() {return name;}

}
