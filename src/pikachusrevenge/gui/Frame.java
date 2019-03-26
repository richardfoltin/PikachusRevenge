package pikachusrevenge.gui;

import java.awt.image.BufferedImage;
import pikachusrevenge.unit.Unit;
import static pikachusrevenge.unit.Unit.SPRITE_SIZE;
import static pikachusrevenge.gui.MapView.GRIDSIZE;

public class Frame {

    private final BufferedImage topFrame;
    private final BufferedImage bottomFrame;
    private int duration;
    public static int CUT = SPRITE_SIZE/2 + (int)(Unit.C_BOX_OFFSET_Y - Unit.C_BOX_HEIGHT/2) - GRIDSIZE;


    public Frame(BufferedImage frame, int duration) {
        topFrame = frame.getSubimage(0, 0, SPRITE_SIZE, CUT);
        bottomFrame = frame.getSubimage(0, CUT, SPRITE_SIZE, SPRITE_SIZE - CUT);
        //topFrame = frame.getSubimage(0, 0, UNITSIZE, UNITSIZE/2);
        //bottomFrame = frame.getSubimage(0, UNITSIZE/2, UNITSIZE, UNITSIZE/2);
        this.duration = duration;
    }

    public BufferedImage getTop() {return topFrame;}
    public BufferedImage getBottom() {return bottomFrame;}

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

}