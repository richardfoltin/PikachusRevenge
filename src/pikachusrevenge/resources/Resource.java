package pikachusrevenge.resources;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import static pikachusrevenge.unit.Unit.UNITSIZE;

public class Resource {
    
    public static InputStream loadResource(String resName){
        return Resource.class.getResourceAsStream(resName);
    }
    
    public static Image loadImage(String resName) throws IOException{
        URL url = Resource.class.getResource(resName);
        return ImageIO.read(url);
    }
    
    public static BufferedImage loadBufferedImage(String resName) throws IOException{
        URL url = Resource.class.getResource(resName);
        return ImageIO.read(url);
    }
    
    public static BufferedImage getSprite(BufferedImage spriteSheet, int xGrid, int yGrid) {
        return spriteSheet.getSubimage(xGrid * UNITSIZE, yGrid * UNITSIZE, UNITSIZE, UNITSIZE);
    }
    
}
