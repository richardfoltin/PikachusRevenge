package pikachusrevenge.unit;

import java.awt.Rectangle;
import pikachusrevenge.gui.MainWindow;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Position;

public class Player extends Unit {
    
    private int lives;
    private int availableLevels;
    private boolean atSign;
    
    public Player(Model model){
        super(model);
        
        this.lives = 3;
        this.availableLevels = 1;
        this.speed = 5.0;
        this.name = "Pikachu";
        
        setImg("pokemons\\025.png");
        
        this.direction = Direction.STOP;
    }
    
    public void moveToDirection(Direction d){
        this.nextDirection = d;
    }
    
    public void playerCaught() {
        lives--;
        MainWindow.getInstance().getStats().removeLife();
        System.out.println("Ball hit! " + lives);
        if (lives  <= 0) {
            lives = 0;
            model.gameOver();
        } else {
            restartFromStratingPoint();
            for (Pokemon p : model.getMapPokemons()) {
                p.restartFromStratingPoint();
            }
        }
    }
    
    @Override
    public void loop() {
        super.loop();
        model.checkBallPokemonAt(collisionBox);
        atSign = model.checkSign(pos);
    }
    
       
    @Override
    protected void loadNextPosition() {
        if (nextDirection != Direction.STOP) {
            Position targetPosition = new Position(pos.x + nextDirection.x * speed, pos.y + nextDirection.y * speed);
            Rectangle targetRectangle = new Rectangle(0, 0, C_BOX_WIDTH, C_BOX_HEIGHT);
            moveNextCollisionBoxTo(targetRectangle,targetPosition);
            if (model.canMoveTo(targetRectangle)){
                //System.out.println(String.format("Move to: %s (%.0f,%.0f) -> (%.0f,%.0f)",nextDirection.name(),collisionBox.getX(),collisionBox.getY(),targetRectangle.getX(),targetRectangle.getY()));
                super.loadNextPosition();      
            } else {
                //System.out.println(String.format("Stop at: %s (%.0f,%.0f) -> (%.0f,%.0f)",nextDirection.name(),collisionBox.getX(),collisionBox.getY(),targetRectangle.getX(),targetRectangle.getY()));
                nextDirection = Direction.STOP;
            }
        }
    }
    
    public void increaseAvailableLevels(int level) {
        if (level > availableLevels) availableLevels = level;
    }
    
    public boolean isAtSign() {return atSign;}
    public int getLives() {return lives;}
    public int getAvailableLevels() {return availableLevels;}
    public double getSpeed() {return speed;}
}
