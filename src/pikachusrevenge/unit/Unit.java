package pikachusrevenge.unit;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Position;
import pikachusrevenge.resources.Resource;


public class Unit extends MovingSprite {

    protected String name;
    protected Direction startDirection;
    
    private Animation[] walk;
    private Animation animation;
        
    public final static int UNITSIZE = 64;
    public final static double FRAMEDELAY = 14;
    
    public Unit( Model model){
        super(model);
        walk = new Animation[4];
    }
    
    @Override
    protected void setImg(String filePath){
        super.setImg(filePath);
        if (animated) {
            for (int i = 0; i < 4; ++i){
                BufferedImage[] movement = new BufferedImage[4];
                for (int j = 0; j < 4; ++j){
                    movement[j] = Resource.getSprite(super.getImg(), j, i);
                }
                Animation movementAnimation = new Animation(movement,(int)(FRAMEDELAY / speed));
                walk[i] = movementAnimation;
            }
            animation = walk[0];
        }
    }

    @Override
    protected void loadNextPosition() {
        super.loadNextPosition();
        
        if (nextDirection != direction) {
            if (animation != null) animation.stop();
            switch (nextDirection) {
                case UP : animation = walk[3]; break;
                case LEFT :
                case UPLEFT :
                case DOWNLEFT : animation = walk[1]; break;
                case DOWN : animation = walk[0]; break;
                case RIGHT :
                case DOWNRIGHT :
                case UPRIGHT : animation = walk[2]; break;
            }
            if (nextDirection != Direction.STOP && animation != null) animation.start();
        }
    }

    @Override
    public void startMoving() {
        super.startMoving();
    }

    @Override
    public void stopMoving() {
        super.stopMoving(); 
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        animation.update();
    }

    @Override
    public BufferedImage getImg() {
        return animation.getSprite();
    }

    public void restartFromStratingPoint() {
        this.pos.x = startPosition.x;
        this.pos.y = startPosition.y;
        this.nextDirection = Direction.STOP;
        this.direction = startDirection;
    }
    
    public String getName() {return name;}

}
