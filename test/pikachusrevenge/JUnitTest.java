package pikachusrevenge;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import pikachusrevenge.model.Direction;

public class JUnitTest {

    public JUnitTest() {
    }
    
    
    @Before
    public void setUp() {
    }
    
    @Test
    public void directionTest() {
        Direction d1 = Direction.LEFT;
        Direction d2 = Direction.UPLEFT;
        
//        assertEquals(String.format("%s + %s",d1,Direction.RIGHT),d1.add(Direction.RIGHT),null);
//        assertEquals(String.format("%s + %s",d1,Direction.LEFT),d1.add(Direction.LEFT),Direction.LEFT);
//        assertEquals(String.format("%s + %s",d1,Direction.UP),d1.add(Direction.UP),Direction.UPLEFT);
//        assertEquals(String.format("%s + %s",d2,Direction.UPLEFT),d2.add(Direction.UPLEFT),Direction.UPLEFT);
//        assertEquals(String.format("%s + %s",d2,Direction.LEFT),d2.add(Direction.LEFT),Direction.UPLEFT);
//        assertEquals(String.format("%s + %s",d2,Direction.RIGHT),d2.add(Direction.RIGHT),Direction.UP);
//        assertEquals(String.format("%s + %s",d2,Direction.DOWN),d2.add(Direction.DOWN),Direction.LEFT);
//        assertEquals(String.format("%s + %s",d2,Direction.DOWNRIGHT),d2.add(Direction.DOWNRIGHT),null);
    }
    
            
}
