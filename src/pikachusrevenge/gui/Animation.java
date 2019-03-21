package pikachusrevenge.gui;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


public class Animation {

    private int frameCount;
    private int frameDelay;
    private int currentFrame;
    private final int animationDirection;
    private final int totalFrames; 

    private boolean stopped;

    private List<Frame> frames = new ArrayList<Frame>();

    public Animation(BufferedImage[] frames, int frameDelay) {
        this.frameDelay = frameDelay;
        this.stopped = true;

        for (int i = 0; i < frames.length; i++) {
            this.frames.add(new Frame(frames[i], frameDelay));
        }

        this.frameCount = 0;
        this.frameDelay = frameDelay;
        this.currentFrame = 0;
        this.animationDirection = 1;
        this.totalFrames = this.frames.size();

    }

    public void start() {
        if (!stopped) return;
        if (frames.size() == 0) return;
        stopped = false;
    }

    public void stop() {
        if (frames.size() == 0) return;
        if (currentFrame != 2) currentFrame = 0;
        stopped = true;
    }

    public void restart() {
        if (frames.size() == 0) return;
        stopped = false;
        currentFrame = 0;
    }

    public void reset() {
        this.stopped = true;
        this.frameCount = 0;
        this.currentFrame = 0;
    }

    public Frame getFrame() {
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