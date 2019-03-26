package pikachusrevenge.resources;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import static pikachusrevenge.unit.Unit.SPRITE_SIZE;

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
        return spriteSheet.getSubimage(xGrid * SPRITE_SIZE, yGrid * SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE);
    }
    
    public static BufferedImage getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }
    
}
