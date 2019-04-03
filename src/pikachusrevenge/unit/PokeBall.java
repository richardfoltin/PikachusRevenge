package pikachusrevenge.unit;

import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Position;

public final class PokeBall extends MovingSprite {

    public final static int BALLSIZE = 16;
    
    private final NPC owner;
    private Position targetPosition;
    
    public PokeBall(double x, double y, double speed, Model model, NPC owner) {
        super(model);
     
        this.owner = owner;
        this.speed = speed;
        this.moving = true;
        setImg("object_ball.png");
        setStartingPostion(x, y);
        
        this.targetPosition = new Position(model.getPlayer().getPosition());
        this.nextDirection = Direction.getDirection(pos,targetPosition);
    }

    @Override
    protected void loadNextPosition() {
        targetPosition = model.getPlayer().getPosition(); // homing ball
        double distance = targetPosition.distanceFrom(pos);
        if (distance <= speed) {
            model.ballReachedPlayer(this);
            stopLooping();
        } else {
            nextPosition.x = pos.x + ((targetPosition.x - pos.x) / (distance / speed));
            nextPosition.y = pos.y + ((targetPosition.y - pos.y) / (distance / speed));
        }
    }
    
    public NPC getOwner() {return owner;}
    
}
