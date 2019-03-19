package pikachusrevenge.unit;

import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;

public class Player extends Unit {
    
    private int balls;
    
    public Player(double startX, double startY, Model model){
        super(startX,startY,"Player.png",model);
        
        this.balls = 0;
        this.collisionRadius = 8;
        this.speed = 5.0;
        this.name = "Pikachu";
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
    protected boolean loadNextPosition(Direction d) {
        pickUpBalls();
        return super.loadNextPosition(d);
    }
    
    
 
}
