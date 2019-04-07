package pikachusrevenge.unit;

import java.awt.image.BufferedImage;
import java.io.IOException;
import pikachusrevenge.gui.MainWindow;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Position;
import pikachusrevenge.resources.Resource;

public class Player extends Unit {
    
    public static final String DEFAULTNAME = "Pikachu";
    
    private BufferedImage caughtImage;
    private NPC atCarry;
    private NPC onCarry;
    
    private int lives;
    private int availableLevels;
    private int caughtWait;
    private int caughtWaitMax;
    private boolean atSign;
    
    public Player(Model model){
        super(model);
        
        this.lives = (MainWindow.getInstance().TESTING) ? 1 : 3;
        this.availableLevels = 1;
        this.speed = 3.0;
        this.name = DEFAULTNAME;
        this.caughtWaitMax = 30;
        
        setImg("pokemons/025.png");
        
        try {this.caughtImage = Resource.loadBufferedImage("ball_shadow.png");} 
        catch (IOException e) {System.err.println("Can't load file: ball_shadow.png");} 
        
        this.direction = Direction.STOP;
    }
    
    public void moveToDirection(Direction d){
        if (!isOnCarry()) {
            this.nextDirection = d;
            if (nextDirection != Direction.STOP) startWalking();
        } else {
            model.writeInfo("You can only get off by pressing <font color=black>SPACE</font> near shore!");
        }
    }
    
    public void playerCaught() {
        if (!insideBall()) {
            lives--;
            MainWindow.getInstance().getStats().removeLife();
            System.out.println("Ball hit! " + lives);
            caughtWait = 1;
        }
    }
    
    private void restartOrGameOver() {
        if (lives  <= 0) {
            lives = 0;
            if (MainWindow.getInstance().TESTING) MainWindow.getInstance().restartLevel();
            else model.gameOver();
        } else {
            restartFromStratingPoint();
            for (Pokemon p : model.getMapPokemons()) {
                if (p.isFound()) p.restartFromStratingPoint();
            }
        }   
        caughtWait = 0;
    }
    
    
    @Override
    public void loop() {
        if (insideBall()) {
            caughtWait++;
            if (caughtWait >= caughtWaitMax) restartOrGameOver();
        } else if (isOnCarry()) {
            pos.x = onCarry.getX();
            pos.y = onCarry.getY() - 7;
        } else {
            super.loop();
            model.checkBallPokemonAt(collisionBox);
            atSign = model.checkSign(pos);
            atCarry = model.checkCarry(pos);
        }
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
    
    @Override
    public void restartFromStratingPoint() {
        getOutBall();
        super.restartFromStratingPoint();
    }
    
    public void increaseAvailableLevels(int level) {
        if (level > availableLevels) availableLevels = level;
    }
    
    public void putOnCarry() {
        onCarry = atCarry;
        atCarry = null;
    }
    public void getOffCarry(Position shorePosition) {
        pos.x = shorePosition.x;
        pos.y = shorePosition.y;
        nextPosition.x = pos.x;
        nextPosition.y = pos.y;
        onCarry = null;
    }

    @Override
    public BufferedImage getImg() {return insideBall() ? caughtImage : super.getImg();}
    public boolean insideBall() {return (caughtWait != 0);}
    public boolean isAtSign() {return atSign;}
    public boolean isOnCarry() {return onCarry != null;}
    public NPC isAtCarry() {return atCarry;}
    public int getLives() {return lives;}
    public int getAvailableLevels() {return availableLevels;}
    public double getSpeed() {return speed;}
    
    public void getOutBall() {this.caughtWait = 0;}
    public void setLives(int lives) {this.lives = lives;}
}
