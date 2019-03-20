package pikachusrevenge.unit;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private Position nextPosition;
    protected Direction nextDirection;
    protected double speed;
    protected double collisionRadius;
    private Timer moveTimer;
    private final int MOVE_SPEED = 40;
    private Image img;
    protected final Model model;
    
    public MovingSprite(Model model){
        this.pos = new Position(0,0);
        this.nextPosition = new Position(0,0);
        this.startPosition = new Position(0,0);
        this.nextDirection = Direction.STOP;
        this.direction = Direction.STOP;
        this.model = model;     
        this.collisionRadius = 8;   
        this.moveTimer = new Timer(MOVE_SPEED, this);
    }
    
    protected void setImg(String filePath){
        try {this.img = Resource.loadImage(filePath);} 
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
    }
    

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == moveTimer){
            //System.out.println(String.format("Move (%.0f,%.0f): %s",nextPosition.x,nextPosition.y,new SimpleDateFormat("mm:ss.SSS").format(new Date())));
            if (nextDirection != Direction.STOP) {
                pos.x = nextPosition.x;
                pos.y = nextPosition.y;
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
    public Image getImg() {return img;}
    public double getCollisionRadius() {return collisionRadius;}
    public Position getPosition() {return pos;}
    
}
