package pikachusrevenge.unit;

import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Position;

public class PokeBall extends MovingSprite {
    
    private NPC owner;
    private Position targetPosition;
    
    public final static int BALLSIZE = 24;
    
    public PokeBall(double x, double y, double speed, Model model, NPC owner) {
        super(model);
     
        this.owner = owner;
        this.speed = speed;
        setImg("object_ball.png");
        setStartingPostion(x, y);
        
        this.targetPosition = new Position(model.getPlayer().getPosition());
        this.nextDirection = Direction.getDirection(pos,targetPosition);
    }

    @Override
    protected void loadNextPosition() {
        if (targetPosition.distanceFrom(pos) <= speed) {
            model.ballReachedPlayer(this);
            stopMoving();
        } else {
            this.nextDirection = Direction.getDirection(pos,targetPosition);
            super.loadNextPosition();
        }
    }
    
    public NPC getOwner() {return owner;}
    
}
