package pikachusrevenge.unit;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import static pikachusrevenge.gui.MapView.ZOOM;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;
import pikachusrevenge.resources.Resource;


public class Unit extends MovingSprite {

    protected String name;
    
    private final Animation[] walk;
    private Animation animation;
        
    public final static int SPRITE_SIZE = 64;
    public final static double C_BOX_WIDTH = 8;
    public final static double C_BOX_HEIGHT = 8;
    public final static double C_BOX_OFFSET_X = 0;
    public final static double C_BOX_OFFSET_Y = 20;
    public final static double FRAMEDELAY = 4;
    
    public Unit(Model model){
        super(model);
        walk = new Animation[4];
        collisionBox.setSize((int)(C_BOX_WIDTH*ZOOM), (int)(C_BOX_HEIGHT*ZOOM));
        nextCollisionBox.setSize((int)(C_BOX_WIDTH*ZOOM), (int)(C_BOX_HEIGHT*ZOOM));
    }
    
    @Override
    protected final void setImg(String filePath){
        super.setImg(filePath);
        if (animated) {
            for (int i = 0; i < 4; ++i){
                ArrayList<BufferedImage> movement = new ArrayList<>();
                for (int j = 0; j < 4; ++j){
                    movement.add(j,Resource.getSprite(super.getImg(), j, i));
                }
                Animation movementAnimation = new Animation(movement,(int)(FRAMEDELAY / speed));
                walk[i] = movementAnimation;
            }
            animation = walk[0];
        }
    }

    @Override
    public void loop() { 
        switch (getFacingDirection()) {
            case UP : animation = walk[3]; break;
            case LEFT :
            case UPLEFT :
            case DOWNLEFT : animation = walk[1]; break;
            case DOWN : animation = walk[0]; break;
            case RIGHT :
            case DOWNRIGHT :
            case UPRIGHT : animation = walk[2]; break;
        }
        if (moving) animation.update();
        super.loop();
    }
    
    public Direction getFacingDirection(){
        //return (nextDirection == direction) ? nextDirection : direction;
        return direction;
   }
    
    public void startWalking(){
        animation.start();
        moving = true;
    }
    
    public void stopWalking() {
        animation.stop();
        moving = false;
    }
    
    @Override
    public BufferedImage getImg() {
        return animation.getFrame();
    }
    
    public void restartFromStratingPoint() {
        this.pos.x = startPosition.x;
        this.pos.y = startPosition.y;
        this.nextDirection = Direction.STOP;
        this.direction = Direction.STOP;
        moveCollisionBoxTo(collisionBox, pos);
        moveCollisionBoxTo(nextCollisionBox, pos);
        super.loadNextPosition();
        animation = walk[0];
        animation.stop();
    }
    
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

}
