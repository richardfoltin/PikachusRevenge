package pikachusrevenge.unit;

import java.awt.Rectangle;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Position;

public class Player extends Unit {
    
    private int balls;
    private int lives;
    
    public Player(double x, double y, Model model){
        super(model);
        
        this.lives = 3;
        addLives();
        this.balls = 0;
        this.speed = 5.0;
        this.name = "Pikachu";
        
        setStartingPostion(x, y);
        setImg("pokemons/025.png");
        
        this.direction = Direction.STOP;
        this.startDirection = Direction.STOP;
    }
    
    public void moveToDirection(Direction d){
        this.nextDirection = d;
        //this.loadNextPosition();
    }
    
    public void caught() {
        lives--;
        model.getStats().removeLife();
        System.out.println("Ball hit! " + lives);
        if (lives  <= 0) {
            lives = 0;
            model.gameOver();
        } else {
            restartFromStratingPoint();
        }
    }
    
    private void pickUpBalls() {
        boolean ball = model.checkBallAt(collisionBox);
        if (ball){
            balls++;
            System.out.println(String.format("Ball found at (%.0f,%.0f)",pos.x,pos.y));
            checkWin();
        }
    }
    
    private void checkWin() {
        if (balls == model.getBallCount()) {
            System.out.println("Winner");
        }
    }
       
    @Override
    protected void loadNextPosition() {
        pickUpBalls();
        if (nextDirection != Direction.STOP) {
            Position targetPosition = new Position(pos.x + nextDirection.x * speed, pos.y + nextDirection.y * speed);
            Rectangle targetRectangle = new Rectangle(0, 0, C_BOX_WIDTH, C_BOX_HEIGHT);
            moveCollisionBoxTo(targetRectangle,targetPosition);
            if (model.canMoveTo(targetRectangle)){
                //System.out.println(String.format("Move to: %s (%.0f,%.0f) -> (%.0f,%.0f)",nextDirection.name(),collisionBox.getX(),collisionBox.getY(),targetRectangle.getX(),targetRectangle.getY()));
                super.loadNextPosition();            
            } else {
                //System.out.println(String.format("Stop at: %s (%.0f,%.0f) -> (%.0f,%.0f)",nextDirection.name(),collisionBox.getX(),collisionBox.getY(),targetRectangle.getX(),targetRectangle.getY()));
                nextDirection = Direction.STOP;
            }
        }
    }
    
    public void addLives() {
        for (int i = 0; i < lives; ++i){
            model.getStats().addLife();
        }
    }
    
}
