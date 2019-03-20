package pikachusrevenge.unit;

import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;

public class Player extends Unit {
    
    private int balls;
    private int lives;
    
    public Player(double x, double y, Model model){
        super(model);
        
        this.lives = 3;
        this.balls = 0;
        this.speed = 5.0;
        this.name = "Pikachu";
        
        setStartingPostion(x, y);
        setImg("025.png");
        
        this.direction = Direction.STOP;
        this.startDirection = Direction.STOP;
    }
    
    public void moveToDirection(Direction d){
        this.nextDirection = d;
        this.loadNextPosition();
    }
    
    public void caught() {
        lives--;
        System.out.println("Ball hit! " + lives);
        if (lives  <= 0) {
            model.gameOver();
        } else {
            restartFromStratingPoint();
        }
    }
    
    private void pickUpBalls() {
        boolean ball = model.isBallAt(pos.x,pos.y);
        if (ball){
            balls++;
            model.removeTile(pos.x,pos.y);
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
        if (model.canMoveTo(this, pos.x , pos.y, nextDirection)){
            super.loadNextPosition();
        } else {
            nextDirection = Direction.STOP;
        }
    }
}
