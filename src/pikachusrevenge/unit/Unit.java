package pikachusrevenge.unit;

import pikachusrevenge.model.Position;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.Timer;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;

import pikachusrevenge.resources.Resource;

public class Unit implements ActionListener  {

    protected Position pos;
    protected Position startPosition;
    private Position nextPosition;
    protected Direction nextDirection;
    protected double speed;
    protected double collisionRadius;
    private Image img;
    protected String name;
    protected final Model model;
    private Timer moveTimer;
    private final int MOVESPEED = 40;
    
    public final static int UNITSIZE = 32;
    
    
    public Unit(double startX, double startY, String filePath, Model model){
        this.pos = new Position(startX,startY);
        this.nextPosition = new Position(startX,startY);
        this.startPosition = new Position(startX,startY);
        this.nextDirection = Direction.STOP;
        this.model = model;        
        this.moveTimer = new Timer(MOVESPEED, this);
        
        try {
            this.img = Resource.loadImage(filePath);
        } catch (IOException e) {
            System.err.println("Can't load file: " + filePath);
        }
    }
    
    public void startMovingTowards(Direction d){
        if (d == Direction.STOP || d == null) {
            stopMoving();
        } else {
            if (loadNextPosition(d)) moveTimer.restart();;
        }
    }
    
    protected boolean loadNextPosition(Direction d){
        nextPosition.x = pos.x + d.x * speed;
        nextPosition.y = pos.y + d.y * speed;
        nextDirection = d;
        return (!(this instanceof Player) || model.canMoveTo(this, nextPosition.x , nextPosition.y, d));
    }
    

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == moveTimer){
            //System.out.println(String.format("Move (%.0f,%.0f): %s",nextPosition.x,nextPosition.y,new SimpleDateFormat("mm:ss.SSS").format(new Date())));
            pos.x = nextPosition.x;
            pos.y = nextPosition.y;
            if (!loadNextPosition(nextDirection)) stopMoving();
        }
    }
    
    public void stopMoving() {
        //System.out.println("--STOP--");
        moveTimer.stop();
    }
    
    public void restartFromStratingPoint() {
        this.pos.x = startPosition.x;
        this.pos.y = startPosition.y;
        this.nextDirection = Direction.STOP;
    }
    
    public double getX() {return pos.x;}
    public double getY() {return pos.y;}
    public Image getImg() {return img;}
    public String getName() {return name;}
    public double getCollisionRadius() {return collisionRadius;}
    public Position getPosition() {return pos;}
    

    protected void setX(double x) {
        this.pos.x = x;
    }
    protected void setY(double y) {
        this.pos.y = y;
    }

}
