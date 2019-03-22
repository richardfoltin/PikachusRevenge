package pikachusrevenge.model;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import static java.lang.Math.signum;
import java.util.ArrayList;
import java.util.List;

public  class KeyPressHandler {

    private static List<Direction> pressedKeys = new ArrayList<Direction>();;
    
    public static void keyPressed(Model model, int key) {
        Direction d = directionFromKeyCode(key);
        if (d == Direction.STOP) {
            switch (key){
                case KeyEvent.VK_SPACE : model.playerInteraction();
            }
        }
        if (newDirectionWith(d)) model.playerMoveTowards(getMovingDirection());
    }
    
    public static void keyReleased(Model model, int key) {
        Direction d = directionFromKeyCode(key);
        if (newDirectionWithout(d)) model.playerMoveTowards(getMovingDirection());     
    }
    
    private static boolean newDirectionWith(Direction d){
        if (!pressedKeys.contains(d)) {
            if (pressedKeys.size() == 2) {
                //System.out.println("CHANGE : " + pressedKeys.get(0) + " -> " + d);
                pressedKeys.remove(0);
                pressedKeys.add(d);
            } else {
                //System.out.println("ADD : " + d);
                pressedKeys.add(d);
            }
            return true;       
        }
        return false;
    }
    
    private static boolean newDirectionWithout(Direction d){
        //System.out.println("REMOVE : " + d);
        if (pressedKeys.contains(d)) {
            pressedKeys.remove(d);
            return true;
        } 
        return false;     
    }
    
    private static Direction getMovingDirection(){
        double x = 0;
        double y = 0;
        for (Direction d : pressedKeys) {
            x += d.x;
            y += d.y;
        }
        Direction d = Direction.getDirection(signum(x),signum(y));
        //System.out.println("DIRECTION : " + d);
        return d;
    }
    
                
    private static Direction directionFromKeyCode(int keyCode){
        switch (keyCode){
            case KeyEvent.VK_LEFT :
            case KeyEvent.VK_A :  return Direction.LEFT; 
            case KeyEvent.VK_RIGHT: 
            case KeyEvent.VK_D :  return Direction.RIGHT;
            case KeyEvent.VK_UP:    
            case KeyEvent.VK_W :  return Direction.UP;
            case KeyEvent.VK_DOWN:  
            case KeyEvent.VK_S :  return Direction.DOWN;
            default : return Direction.STOP;
            //case KeyEvent.VK_ESCAPE: game.loadGame(game.getGameID());
        }
    }
    
}
