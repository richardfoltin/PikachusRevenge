package pikachusrevenge.unit;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.Timer;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Position;
import pikachusrevenge.resources.Resource;


public class MovingSprite implements ActionListener {
    
    protected Position pos;
    protected Position startPosition;
    protected Direction direction;
    protected boolean animated;
    private Position nextPosition;
    protected Direction nextDirection;
    protected double speed;
    private Timer moveTimer;
    private BufferedImage img;
    protected int imageSize;
    protected final Model model;
    protected Rectangle collisionBox;
    protected Rectangle nextCollisionBox;
    protected int cOffsetX;
    protected int cOffsetY;
  
    private final static int MOVE_SPEED = 40;  
    
    public MovingSprite(Model model){
        this.pos = new Position(0,0);
        this.nextPosition = new Position(0,0);
        this.startPosition = new Position(0,0);
        this.collisionBox = new Rectangle();
        this.nextCollisionBox = new Rectangle();
        this.nextDirection = Direction.STOP;
        this.direction = Direction.STOP;
        this.model = model;     
        this.animated = true;
        this.moveTimer = new Timer(MOVE_SPEED, this);
    }
    
    protected void setImg(String filePath){
        try {this.img = Resource.loadBufferedImage(filePath);} 
        catch (IOException e) {System.err.println("Can't load file: " + filePath);} 
    }
    
    protected void setStartingPostion(double x, double y){
        this.pos.x = x;
        this.pos.y = y;
        this.startPosition.x = x;
        this.startPosition.y = y;
        this.nextPosition.x = x;
        this.nextPosition.y = y;
    }
    
    protected void loadNextPosition(){
        nextPosition.x = pos.x + nextDirection.x * speed;
        nextPosition.y = pos.y + nextDirection.y * speed;
        moveCollisionBoxTo(nextCollisionBox,nextPosition);
    }
    
    protected void moveCollisionBoxTo(Rectangle box, Position pos){
        box.setLocation((int)(pos.x + cOffsetX - box.width/2), (int)(pos.y + cOffsetY - box.height/2));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == moveTimer){
            //System.out.println(String.format("Move (%.0f,%.0f): %s",nextPosition.x,nextPosition.y,new SimpleDateFormat("mm:ss.SSS").format(new Date())));   
            if (nextDirection != Direction.STOP) {
                pos.x = nextPosition.x;
                pos.y = nextPosition.y;
                collisionBox.setLocation((int)nextCollisionBox.getX(),(int)nextCollisionBox.getY());
                direction = nextDirection;
            }
            loadNextPosition();
        }
    }
    
    public void startMoving() {
        moveTimer.start();
    }
    
    public void stopMoving() {
        moveTimer.stop();
    }
    
    public double getX() {return pos.x;}
    public double getY() {return pos.y;}
    public int getCornerX() {return (int)pos.x - imageSize/2;}
    public int getCornerY() {return (int)pos.y - imageSize/2;}
    public BufferedImage getImg() {return img;}
    public Position getPosition() {return pos;}
    public Timer getMoveTimer() {return moveTimer;}
    
    
}
