package pikachusrevenge.unit;

import java.awt.Image;
import java.io.IOException;
import pikachusrevenge.resources.Resource;

public class PokeBall {
    
    private Image img;

    public PokeBall() {
        try {
            this.img = Resource.loadImage("NPC.png");
        } catch (IOException e) {
            System.err.println("Can't load pokeball");
        }
    }

    public Image getImg() {
        return img;
    }
    
}
