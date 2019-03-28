package pikachusrevenge.unit;

import java.awt.image.BufferedImage;
import java.io.IOException;
import pikachusrevenge.gui.MainWindow;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;
import pikachusrevenge.resources.Resource;

public class Player extends Unit {
    
    private int lives;
    private int availableLevels;
    private boolean atSign;
    private BufferedImage caughtImage;
    private int caughtWait;
    private int caughtWaitMax;
    
    public Player(Model model){
        super(model);
        
        this.lives = 1;
        this.availableLevels = 1;
        this.speed = 3.0;
        this.name = "Pikachu";
        this.caughtWaitMax = 30;
        
        setImg("pokemons\\025.png");
        
        try {this.caughtImage = Resource.loadBufferedImage("ball_shadow.png");} 
        catch (IOException e) {System.err.println("Can't load file: ball_shadow.png");} 
        
        this.direction = Direction.STOP;
    }
    
    public void moveToDirection(Direction d){
        this.nextDirection = d;
        if (nextDirection != Direction.STOP) startWalking();
    }
    
    public void playerCaught() {
        lives--;
        MainWindow.getInstance().getStats().removeLife();
        System.out.println("Ball hit! " + lives);
        caughtWait = 1;
    }
    
    private void restartOrGameOver() {
        if (lives  <= 0) {
            lives = 0;
            model.gameOver();
        } else {
            restartFromStratingPoint();
            for (Pokemon p : model.getMapPokemons()) {
                if (p.isFound()) p.restartFromStratingPoint();
            }
        }   
        caughtWait = 0;
    }
    
    public boolean insideBall() {
        return (caughtWait != 0);
    }
    
    @Override
    public void loop() {
        if (insideBall()) {
            caughtWait++;
            if (caughtWait >= caughtWaitMax) restartOrGameOver();
        } else {
            super.loop();
            model.checkBallPokemonAt(collisionBox);
            atSign = model.checkSign(pos);
        }
    }

    @Override
    public BufferedImage getImg() {
        return insideBall() ? caughtImage : super.getImg();
    }
    
       
    @Override
    protected void loadNextPosition() {
        if (nextDirection != Direction.STOP && model.canMoveTo(pos,nextDirection,speed)) {
            startWalking();
            super.loadNextPosition();    
        } else {
            stopWalking();
        }
    }
    
    public void increaseAvailableLevels(int level) {
        if (level > availableLevels) availableLevels = level;
    }
    
    public void setLives(int lives) {this.lives = lives;}
    
    public boolean isAtSign() {return atSign;}
    public int getLives() {return lives;}
    public int getAvailableLevels() {return availableLevels;}
    public double getSpeed() {return speed;}
}
