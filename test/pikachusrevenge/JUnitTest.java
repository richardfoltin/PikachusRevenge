package pikachusrevenge;

import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import pikachusrevenge.model.Direction;
import pikachusrevenge.model.KeyPressHandler;
import pikachusrevenge.model.Position;
import pikachusrevenge.model.TilePosition;

public class JUnitTest {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(JUnitTest.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
    }
    
    @Test
    public void positionTest() {
        Position pos = new Position(3,4);
        
        double distance = pos.distanceFrom(new Position(0,0));
        assertEquals("(3,4) position distance from (0,0) shoud be 5",5,(int)distance);
    
        Position targetPosition = new Position(5,6);
        assertEquals("Moving (3,4) with (+2,+2) shoud be (5,6)",targetPosition,pos.movePosition(2, 2));
    }
    
    @Test
    public void tilePositionTest() {
        TilePosition tpos = new TilePosition(1, 1, 1);
        Position pos = new Position(24,24);  // 16 + 16/2
        
        assertEquals("(1,1) tileposition center should be at (24,24) position", pos, TilePosition.tileCenter(tpos));
        
        assertEquals("29 position should be at 1 tileposition", 1, TilePosition.tileCoordFromMapCoord(29));
        assertEquals("16 position should be at 1 tileposition", 1, TilePosition.tileCoordFromMapCoord(16));
        assertEquals("15 position should be at 0 tileposition", 0, TilePosition.tileCoordFromMapCoord(15));
        
        assertEquals("(24,24) position should be at (1,1) tileposition", tpos, TilePosition.fromMapPosition(pos,1)); 
    }
    
    @Test
    public void keyPressTest() {
        try {
            // reference: https://stackoverflow.com/questions/34571/how-do-i-test-a-private-function-or-a-class-that-has-private-methods-fields-or
            Method directionFromKeyCode = KeyPressHandler.class.getDeclaredMethod("directionFromKeyCode", int.class);
            directionFromKeyCode.setAccessible(true);
            
            assertEquals("Pressing left arrow should be LEFT", Direction.LEFT, directionFromKeyCode.invoke(null, KeyEvent.VK_LEFT));
            assertEquals("Pressing W should be UP", Direction.UP, directionFromKeyCode.invoke(null, KeyEvent.VK_W));
            assertEquals("Pressing Q (or any other) should be STOP", Direction.STOP, directionFromKeyCode.invoke(null, KeyEvent.VK_Q));
            
            
            Method newDirectionWith = KeyPressHandler.class.getDeclaredMethod("newDirectionWith", Direction.class);
            newDirectionWith.setAccessible(true);
            Method newDirectionWithout = KeyPressHandler.class.getDeclaredMethod("newDirectionWithout", Direction.class);
            newDirectionWithout.setAccessible(true);
            Method getMovingDirection = KeyPressHandler.class.getDeclaredMethod("getMovingDirection");
            getMovingDirection.setAccessible(true);
            
            newDirectionWith.invoke(null, Direction.LEFT);
            newDirectionWith.invoke(null, Direction.UP);
            assertEquals("After LEFT and UP, moving direction should be UPLEFT", Direction.UPLEFT, getMovingDirection.invoke(null));
            
            newDirectionWithout.invoke(null, Direction.LEFT);
            assertEquals("After releasing LEFT should remain UP", Direction.UP, getMovingDirection.invoke(null));
            
            newDirectionWithout.invoke(null, Direction.RIGHT);
            assertEquals("After releasing RIGHT should remain UP", Direction.UP, getMovingDirection.invoke(null));
            
            newDirectionWithout.invoke(null, Direction.UP);
            assertEquals("After releasing UP should remain STOP", Direction.STOP, getMovingDirection.invoke(null));
 
        } catch (Exception e) {e.printStackTrace();}
    }
    
    @Test
    public void directionTest() {
        
        assertEquals("(1,1) direction should be DOWNRIGHT", Direction.DOWNRIGHT, Direction.getDirection(1, 1));
        
        Position pos1 = new Position(2,2);
        Position pos2 = new Position(3,3);
        Position pos3 = new Position(1,2);
        Position pos4 = new Position(10,3);
        
        assertEquals("From (2,2) to (3,3) should be DOWNRIGHT", Direction.DOWNRIGHT, Direction.getDirection(pos1, pos2));
        assertEquals("From (2,2) to (1,2) should be LEFT", Direction.LEFT, Direction.getDirection(pos1, pos3));
        assertEquals("From (2,2) to (10,3) should be RIGHT", Direction.RIGHT, Direction.getDirection(pos1, pos4));
        
        assertEquals("From (2,2) to (10,3) second direction should be DOWN", Direction.DOWN, Direction.getSecondDirection(pos1, pos4));
        
        assertTrue("DOWN should be in direction of sight of DOWNLEFT", Direction.isInDirectionOfSight(Direction.DOWNLEFT, Direction.DOWN));
        assertTrue("LEFT should be in direction of sight of DOWNLEFT", Direction.isInDirectionOfSight(Direction.DOWNLEFT, Direction.LEFT));
        assertFalse("DOWNRIGHT shouldn't be in direction of sight of DOWNLEFT", Direction.isInDirectionOfSight(Direction.DOWNLEFT, Direction.DOWNRIGHT));
        
        assertEquals("DOWNRIGHT direction angle start should be 247°", 247, Direction.directionAngleStart(Direction.DOWNRIGHT));
        assertEquals("DOWN direction angle start should be 247°", 202, Direction.directionAngleStart(Direction.DOWN));
        assertEquals("RIGHT direction angle start should be 247°", 292, Direction.directionAngleStart(Direction.RIGHT));
        assertEquals("UP direction angle start should be 247°", 22, Direction.directionAngleStart(Direction.UP));
        assertEquals("LEFT direction angle start should be 247°", 112, Direction.directionAngleStart(Direction.LEFT));
        assertEquals("UPLEFT direction angle start should be 247°", 67, Direction.directionAngleStart(Direction.UPLEFT));
    }
             
}
