package pikachusrevenge.unit;

import pikachusrevenge.gui.Animation;
import java.awt.image.BufferedImage;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;
import pikachusrevenge.resources.Resource;


public class Unit extends MovingSprite {

    protected String name;
    
    private final Animation[] walk;
    private Animation animation;
        
    public final static int UNITSIZE = 64;
    public final static int C_BOX_WIDTH = 10;
    public final static int C_BOX_HEIGHT = 12;
    public final static int C_BOX_OFFSET_X = 0;
    public final static int C_BOX_OFFSET_Y = 24;
    public final static double FRAMEDELAY = 8;
    
    public Unit(Model model){
        super(model);
        walk = new Animation[4];
        imageSize = UNITSIZE;
        collisionBox.setSize(C_BOX_WIDTH, C_BOX_HEIGHT);
        nextCollisionBox.setSize(C_BOX_WIDTH, C_BOX_HEIGHT);
        cOffsetX = C_BOX_OFFSET_X;
        cOffsetY = C_BOX_OFFSET_Y;
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
    public void loop() {     
        if (nextDirection != direction) {
            if (nextDirection == Direction.STOP && animation != null) animation.stop();
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
        } else {
            animation.update();
        }
        super.loop();
    }

    public BufferedImage getTopSprite() {
        return animation.getFrame().getTop();
    }

    public BufferedImage getBottomSprite() {
        return animation.getFrame().getBottom();
    }
    
    public void restartFromStratingPoint() {
        this.pos.x = startPosition.x;
        this.pos.y = startPosition.y;
        this.nextDirection = Direction.STOP;
        this.direction = Direction.STOP;
        super.loadNextPosition();
        animation = walk[0];
        animation.stop();
    }
    
    public String getName() {return name;}

}
