package pikachusrevenge.unit;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;
import pikachusrevenge.resources.Resource;

public class Pokemon extends Unit {
    
    private int id;
    private final int tileX;
    private final int tileY;
    private boolean found;
    private JLabel label;
    private int movingCounter;
    private final int movingCounterMax;
    
    public Pokemon(int tileX, int tileY, Model model, JLabel label) {
        this(tileX,tileY,model);
        this.label = label;
        this.id = new Random().nextInt(150) + 1;
        setIdImg();
    }
    
    public Pokemon(int tileX, int tileY, Model model, int id) {
        this(tileX,tileY,model);
        this.id = id;
    }
    
    private Pokemon(int tileX, int tileY, Model model) {
        super(model);
        
        this.found = false;
        this.speed = 8.0;
        this.movingCounterMax = 100;
        this.movingCounter = 0;
        this.tileX = tileX;
        this.tileY = tileY;
        
        setStartingPostion(Model.tileCenterFromTileCoord(tileX), Model.tileCenterFromTileCoord(tileY));
        this.direction = Direction.STOP;
    }
    
    public void found() {
        this.nextDirection = Direction.randomMove();
        this.found = true;
        revealLabel();
        startMoving();
    }
    
    public boolean isDrawn() {
        return found && nextDirection != Direction.STOP;
    }
    
    private void revealLabel(){
        BufferedImage image = null;
        try {image = Resource.loadBufferedImage(String.format("pokemons\\icon%03d.png",this.id));} 
        catch (IOException e) {System.err.println("Can't load file");} 
        image = Resource.getSprite(image, 0, 0);
        image = Resource.getScaledImage(image, 30, 30);
        
        label.setIcon(new ImageIcon(image));
    }
    
    private void setIdImg() {
        setImg(String.format("pokemons\\%03d.png",this.id));
    }

    @Override
    public void loop() {
        super.loop();
        if (nextDirection != Direction.STOP) {
            if (movingCounter < movingCounterMax) {
                movingCounter++;
            } else {
                stopMoving();
            }   
        }
    }
    
    @Override
    public void stopMoving() {
        nextDirection = Direction.STOP;
        super.stopMoving();
    }

    public int getId() {return id;}
    public int getTileX() {return tileX;}
    public int getTileY() {return tileY;}
    
    
}
