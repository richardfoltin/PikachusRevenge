package pikachusrevenge.unit;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import static pikachusrevenge.gui.MapView.ZOOM;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Position;
import pikachusrevenge.resources.Resource;
import static pikachusrevenge.unit.Unit.C_BOX_OFFSET_X;
import static pikachusrevenge.unit.Unit.C_BOX_OFFSET_Y;


public class MovingSprite {
    
    protected final Model model;
    
    protected Position pos = new Position(0,0);
    protected Position startPosition = new Position(0,0);
    protected Position nextPosition = new Position(0,0);
    protected Direction direction = Direction.STOP;
    protected Direction nextDirection = Direction.STOP;
    protected Rectangle collisionBox = new Rectangle();
    protected Rectangle nextCollisionBox = new Rectangle();
    
    private BufferedImage img;
    protected double speed;
    protected boolean animated = true;
    protected boolean moving;
    private boolean looping = false;
    
    public MovingSprite(Model model){
        this.model = model;    
    }
    
    protected void setImg(String filePath){
        try {this.img = Resource.loadBufferedImage(filePath);} 
        catch (IOException e) {System.err.println("Can't load file: " + filePath);} 
    }
    
    public final void setStartingPostion(Position pos) {
        setStartingPostion(pos.x, pos.y);
    }
    
    public final void setStartingPostion(double x, double y){
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
    
    public static void moveCollisionBoxTo(Rectangle box, Position pos){
        box.setLocation((int)(pos.x + C_BOX_OFFSET_X*ZOOM - box.width/2), (int)(pos.y + C_BOX_OFFSET_Y*ZOOM - box.height/2));
    }
    
    public void loop() {
        //System.out.println(String.format("Move (%.0f,%.0f): %s",nextPosition.x,nextPosition.y,new SimpleDateFormat("mm:ss.SSS").format(new Date())));   
        if (moving) {
            pos.x = nextPosition.x;
            pos.y = nextPosition.y;
            collisionBox.setLocation((int)nextCollisionBox.getX(),(int)nextCollisionBox.getY());
        }
        direction = nextDirection;
        loadNextPosition();
    }
    
    public void startLooping() {
        looping = true;
    }
    
    public void stopLooping() {
        looping = false;
    }
    
    public double getX() {return pos.x;}
    public double getY() {return pos.y;}
    public BufferedImage getImg() {return img;}
    public Position getPosition() {return pos;}
    public boolean isLooping() {return looping;}
    
    
}
