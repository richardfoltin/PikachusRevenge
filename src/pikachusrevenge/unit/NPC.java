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
    private double throwDistance;
    private double throwSpeed;
    
    private List<Position> route;
    private List<Double> routeWait;
    private ListIterator<Position> routeIterator;
    private Position targetPosition;
    private boolean forward = true;
    private Timer attentionTimer;
    private final int ATTENTION_SPEED = 40;
    
    public NPC(MapObject obj, int level, Model model){
        super(model);
        this.level = level;
        
        loadLevelProperties();
        loadRoute(obj.getShape());
        loadWait(obj);
        
        this.attentionTimer = new Timer(ATTENTION_SPEED, this);
    }
    
    @Override
    public void startMoving() {
        super.startMoving();
        attentionTimer.start();
    }
    
    @Override
    protected void loadNextPosition() {
        if (targetPosition.distanceFrom(pos) <= speed) {
            double wait = routeWait.get(route.indexOf(targetPosition));
            //if (wait != 0) pauseMoving((long)wait);
            targetPosition = nextTarget();
        }
        this.nextDirection = Direction.getDirection(pos,targetPosition);
        super.loadNextPosition();
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
        if (routeIterator.hasNext()) {
            Position pos = routeIterator.next();
            setStartingPostion(pos.x, pos.y);
        }
        if (routeIterator.hasNext()) this.targetPosition = routeIterator.next();
        
        loadNextPosition();
    }
    
    private void loadWait(MapObject obj){
        this.routeWait = new ArrayList<>();
        Properties prop = obj.getProperties();
        
        for (int i = 0; i < route.size(); ++i){
            String waitStr = prop.getProperty("Wait" + i, "0");
            routeWait.add(Double.parseDouble(waitStr));
        }
    }
    
    private Position nextTarget() {
        if ((!routeIterator.hasNext() && forward) || (!routeIterator.hasPrevious() && !forward)) {
            forward = !forward;
        }
        
        if (forward) return routeIterator.next();
        else return routeIterator.previous();
    }
    
    private void throwBall() {
        System.out.println("Ball thrown");
        model.ballThrow(pos, throwSpeed, this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (e.getSource() == attentionTimer){
            Position playerPostion = model.getPlayer().getPosition();
            double distance = playerPostion.distanceFrom(pos);
            Direction playerDirection = Direction.getDirection(pos, playerPostion);
            if (playerDirection == direction) {
                //System.out.println(String.format("Player is in LOS : %s (%.0f)",playerDirection.name(),distance));
                if (distance < throwDistance && model.canThrow(this)) {
                    throwBall();
                }
            }
        }
    }
    
    private void loadLevelProperties() {
        switch (level) {
            default:
            case 1 : 
                this.speed = 2; 
                this.throwDistance = 300;
                this.throwSpeed = 12;
                this.name = "Noob NPC";
                setImg("trchar035.png");
                break;
        }
        
    }

}
