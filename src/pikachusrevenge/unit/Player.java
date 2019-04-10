package pikachusrevenge.unit;

import java.awt.image.BufferedImage;
import java.io.IOException;
import pikachusrevenge.gui.MainWindow;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Position;
import pikachusrevenge.resources.Resource;

/**
 * A játékost leíró osztály
 * @author Csaba Foltin
 */
public class Player extends Unit {
    
    public static final String DEFAULTNAME = "Pikachu";
    public static final double PLAYERSPEED = 3.0;
    
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
        
        this.lives = 3;
        this.availableLevels = 1;
        this.speed = PLAYERSPEED;
        this.name = DEFAULTNAME;
        this.caughtWaitMax = 30;
        
        setImg("pokemons/025.png");
        
        try {this.caughtImage = Resource.loadBufferedImage("ball_shadow.png");} 
        catch (IOException e) {System.err.println("Can't load file: ball_shadow.png");} 
        
        this.direction = Direction.STOP;
    }
    
    /**
     * A játékos billentyűlenyomásával meghívott metódus, mely beállítja az adott
     * irányt ha a játékos éppen nem egy szállítóeszközön van.
     * @param d a kívánt irány
     */
    public void moveToDirection(Direction d){
        if (!isOnCarry()) {
            this.nextDirection = d;
            if (nextDirection != Direction.STOP) startWalking();
        } else {
            model.writeInfo("You can only get off by pressing <font color=black>SPACE</font> near shore!");
        }
    }
    
    /**
     * A játékost elkapta egy NPC. Csökken eggyel az életereje, elindul egy várakozás
     * amíg újra nem éled.
     */
    public void playerCaught() {
        if (!insideBall()) {
            lives--;
            MainWindow.getInstance().getStats().removeLife();
            System.out.println("Ball hit! " + lives);
            caughtWait = 1;
        }
    }
    
    /**
     * A játékos újraéledésekor, ha már nincs több élete, akkor meghívja a játék
     * vége dialogot, vagy visszarakja a játékost a kezdőpozícióba.
     */
    private void restartOrGameOver() {
        if (MainWindow.getInstance().TESTING) MainWindow.getInstance().restartLevel();
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
    
    /**
     * A játék fő ciklusában meghívott metódus, mely beállítja a játékos pozícióját
     * ha szállítóeszközön van, és leellenőrzi, hogy nem-e tud valamilyen interakcióba
     * lépni a pályával az adott helyen.
     */
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
    
    /**
     * A következő pozíció betöltése előtt leellenőrzi, hogy tud-e egyáltalán oda
     * esetleges collision miatt haladni a játékos.
     */
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
    
    /**
     * A játékos felszáll a hordozóeszközre (level 8)
     */
    public void putOnCarry() {
        onCarry = atCarry;
        atCarry = null;
    }
    
    /**
     * A játékos leszáll a hordozóeszközről (level 8)
     * @param shorePosition 
     */
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
