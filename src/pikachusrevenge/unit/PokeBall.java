package pikachusrevenge.unit;

import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Position;

/**
 * Az NPC-k által eldobott labdákat leíró osztály
 * @author Csaba Foltin
 */
public final class PokeBall extends MovingSprite {

    public final static int BALLSIZE = 16;
    
    private final NPC owner;              // az NPC aki eldobta a labdát
    private Position targetPosition;      // a labda célpontja (ami felé halad)
    
    public PokeBall(double x, double y, double speed, Model model, NPC owner) {
        super(model);
     
        this.owner = owner;
        this.speed = speed;
        this.moving = true;
        setImg("object_ball.png");
        setStartingPostion(x, y);
        putToPosition(startPosition);
        
        this.targetPosition = new Position(model.getPlayer().getPosition());
        this.nextDirection = Direction.getDirection(pos,targetPosition);
    }

    /**
     * Kiszámolja a ladba következő pozícióját. A labda folyamatoan a játékos
     * felé halad. Ha elérte a játékost, meghívja a 
     * {@link Model#ballReachedPlayer(pikachusrevenge.unit.PokeBall) ballReachedPlayer} metódust.
     */
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
