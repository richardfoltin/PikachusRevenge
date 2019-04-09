package pikachusrevenge.unit;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Az egységek mozgása során az animációért felelős osztály
 * @author StackOverflow
 */
public final class Animation {

    private int frameCount;
    private int frameDelay;
    private int currentFrame;
    private final int animationDirection;
    private final int totalFrames; 

    private boolean stopped;

    private ArrayList<BufferedImage> frames = new ArrayList<>();

    public Animation(ArrayList<BufferedImage>  frames, int frameDelay) {
        this.frameDelay = frameDelay;
        this.stopped = true;
        this.frames = frames;
        this.frameCount = 0;
        this.frameDelay = frameDelay;
        this.currentFrame = 0;
        this.animationDirection = 1;
        this.totalFrames = this.frames.size();
    }

    public void start() {
        if (!stopped) return;
        if (frames.isEmpty()) return;
        stopped = false;
    }

    public void stop() {
        if (frames.isEmpty()) return;
        if (currentFrame != 2) currentFrame = 0;
        stopped = true;
    }

    public void restart() {
        if (frames.isEmpty()) return;
        stopped = false;
        currentFrame = 0;
    }

    public void reset() {
        this.stopped = true;
        this.frameCount = 0;
        this.currentFrame = 0;
    }

    public BufferedImage getFrame() {
        return frames.get(currentFrame);
    }

    public void update() {
        if (!stopped) {
            frameCount++;

            if (frameCount > frameDelay) {
                frameCount = 0;
                currentFrame += animationDirection;

                if (currentFrame > totalFrames - 1) {
                    currentFrame = 0;
                }
                else if (currentFrame < 0) {
                    currentFrame = totalFrames - 1;
                }
            }
        }

    }

}