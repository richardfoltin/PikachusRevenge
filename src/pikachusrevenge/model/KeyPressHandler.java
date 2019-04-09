package pikachusrevenge.model;

import java.awt.event.KeyEvent;
import static java.lang.Math.signum;
import java.util.ArrayList;
import java.util.List;

/**
 * A billentyűzet gombnyomásait kezelő statikus metódusokat tartalmazó osztály.
 * @author Csaba Foltin
 */
public final class KeyPressHandler {

    private final static List<Direction> PRESSEDKEYS = new ArrayList<Direction>();;
    
    private KeyPressHandler() {}
    
    /**
     * A legutóbb lenyomott billentyűket tároló listát üríti
     */
    public static void clearPressedKeys() {
        PRESSEDKEYS.clear();
    }
    
    /**
     * Feldolgozza a lenyomott billentyűt.
     * Ha a billentyű nyomása új irányt generál, akkor meghívja a játékos 
     * {@link pikachusrevenge.unit.Player#moveToDirection(Direction) moveToDirection} metódusát.
     * @param model a játék modelje
     * @param key a lenyomott billentyű kódja
     */
    public static void keyPressed(Model model, int key) {
        Direction d = directionFromKeyCode(key);
        if (d == Direction.STOP) {
            switch (key){
                case KeyEvent.VK_SPACE : model.playerInteraction();
            }
        }
        if (newDirectionWith(d)) model.getPlayer().moveToDirection(getMovingDirection());
    }
    
    /**
     * Feldolgozza a felengedett billentyűt.
     * Ha a billentyű felengedése új irányt generál, akkor meghívja a játékos 
     * {@link pikachusrevenge.unit.Player#moveToDirection(Direction) moveToDirection} metódusát.
     * @param model a játék modelje
     * @param key a felengedett billentyű kódja
     */
    public static void keyReleased(Model model, int key) {
        Direction d = directionFromKeyCode(key);
        if (newDirectionWithout(d)) model.getPlayer().moveToDirection(getMovingDirection());     
    }
    
    /**
     * Kiszámolja hogy új irány hozzáadásával az eltárolt irányokhoz, új mozgási
     * irány keletkezik-e.
     * A listában mindig csak legfejlebb 2 irányt tárolunk
     * @param d az új irány
     * @return true, ha új a mozgási irány
     */
    private static boolean newDirectionWith(Direction d){
        if (!PRESSEDKEYS.contains(d)) {
            if (PRESSEDKEYS.size() == 2) {
                //System.out.println("CHANGE : " + pressedKeys.get(0) + " -> " + d);
                PRESSEDKEYS.remove(0);
                PRESSEDKEYS.add(d);
            } else {
                //System.out.println("ADD : " + d);
                PRESSEDKEYS.add(d);
            }
            return true;       
        }
        return false;
    }
    
    /**
     * Kiszámolja hogy egy irány eltávolításával az eltárolt irányokból, új mozgási
     * irány keletkezik-e.
     * @param d az irány
     * @return true, ha új a mozgási irány
     */
    private static boolean newDirectionWithout(Direction d){
        //System.out.println("REMOVE : " + d);
        if (PRESSEDKEYS.contains(d)) {
            PRESSEDKEYS.remove(d);
            return true;
        } 
        return false;     
    }
    
    /**
     * Visszaadja, hogy a lista szerint, mi az aktuális mozgási irány.
     * @return az irány
     */
    private static Direction getMovingDirection(){
        double x = 0;
        double y = 0;
        for (Direction d : PRESSEDKEYS) {
            x += d.x;
            y += d.y;
        }
        Direction d = Direction.getDirection(signum(x),signum(y));
        //System.out.println("DIRECTION : " + d);
        return d;
    }
    
    /**
     * A lenyomott billentyű alapján megmondja hogy az milyen irány.
     * @param keyCode a lenyomott billentyű kódja
     * @return az irány
     */      
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
