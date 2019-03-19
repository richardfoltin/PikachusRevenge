package pikachusrevenge.unit;

import pikachusrevenge.model.Position;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import org.mapeditor.core.MapObject;
import org.mapeditor.core.Properties;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;

public class NPC extends Unit {
    
    private final int level;
    private final int id;
    private List<Position> route;
    private List<Double> routeWait;
    private ListIterator<Position> routeIterator;
    private Position targetPosition;
    private boolean forward = true;
    private double routePointCollision;
    private Timer attentionTimer;
    private double throwDistance;
    private final int ATTENTIONSPEED = 40;
    private final int DISTANCEMULTIPLIER = 40;
    
    public NPC(MapObject obj, int level, int id, double speed, Model model){
        super(0,0,"NPC.png",model);
        
        loadRoute(obj.getShape());
        loadWait(obj);
        this.speed = speed;
        this.level = level;
        this.throwDistance = (double)this.level * DISTANCEMULTIPLIER;
        this.id = id;
        this.name = "NPC";
        this.collisionRadius = 8;
        this.routePointCollision = speed * 2;
        this.attentionTimer = new Timer(ATTENTIONSPEED, this);
    }
    
    public void start() {
        startMovingTowards(nextDirection);
        attentionTimer.start();
    }
    
    @Override
    protected boolean loadNextPosition(Direction d) {
        if (targetPosition.distanceFrom(pos) < routePointCollision) {
            double wait = routeWait.get(route.indexOf(targetPosition));
            //if (wait != 0) pauseMoving((long)wait);
            targetPosition = nextPosition();
        }
        nextDirection = Direction.getDirection(pos,targetPosition);
        return super.loadNextPosition(nextDirection);
    }
    
    private void loadRoute(Shape shape){
        this.route = new ArrayList<>();
        PathIterator pi = shape.getPathIterator(null);
        
        while (!pi.isDone()) {
            double[] coords = new double[2];
            pi.currentSegment(coords);
            Position position = new Position(coords);
            if (!(position.x == 0 && position.y == 0)) route.add(position);
            pi.next();
        }
        
        this.routeIterator = route.listIterator();
        if (routeIterator.hasNext()) this.pos = new Position(routeIterator.next());
        if (routeIterator.hasNext()) this.targetPosition = routeIterator.next();
        this.startPosition = new Position(pos);
        this.nextDirection = Direction.getDirection(pos,targetPosition);
    }
    
    private void loadWait(MapObject obj){
        this.routeWait = new ArrayList<>();
        Properties prop = obj.getProperties();
        
        for (int i = 0; i < route.size(); ++i){
            String waitStr = prop.getProperty("Wait" + i, "0");
            routeWait.add(Double.parseDouble(waitStr));
        }
    }
    
    private Position nextPosition() {
        if ((!routeIterator.hasNext() && forward) || (!routeIterator.hasPrevious() && !forward)) {
            forward = !forward;
        }
        
        if (forward) return routeIterator.next();
        else return routeIterator.previous();
    }
    
    private void throwBall() {
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (e.getSource() == attentionTimer){
            Position playerPostion = model.getPlayer().getPosition();
            double distance = playerPostion.distanceFrom(pos);
            Direction playerDirection = Direction.getDirection(pos, playerPostion);
            if (playerDirection == nextDirection) {
                System.out.println(String.format("Player is in LOS : %s (%.0f)",playerDirection.name(),distance));
                if (distance < throwDistance) {
                    throwBall();
                }
            }
        }
    }

}
