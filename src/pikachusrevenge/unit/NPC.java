package pikachusrevenge.unit;

import java.awt.Image;
import pikachusrevenge.model.Position;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.mapeditor.core.MapObject;
import org.mapeditor.core.Properties;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;
import pikachusrevenge.resources.Resource;

public class NPC extends Unit {
    
    private final int level;
    private double throwDistance;
    private double throwSpeed;
    private final BufferedImage exclamation;
    private HashMap<NPC_STATE,NpcState> states;
    
    private List<Position> route;
    private List<Integer> routeWait;
    private ListIterator<Position> routeIterator;
    private Position targetPosition;
    private boolean forward = true;
    
    public static final int EXCLAMATION_SIZE = 40;
    
    private enum NPC_STATE {
        STOP_LOOKOUT,
        STOP_EXCLAMATION,
        STOP_THROW,
        WALKING_CAUTIOUS
    }

    
    public NPC(MapObject obj, int level, Model model){
        super(model);
        this.level = level;
        this.states = getStateArray();
        
        Image image = null;
        try {image = Resource.loadImage("exclamation.png");} 
        catch (IOException e) {System.err.println("Can't load file");} 
        exclamation = Resource.getScaledImage(image, EXCLAMATION_SIZE, EXCLAMATION_SIZE);
        
        loadLevelProperties();
        loadRoute(obj.getShape());
        loadWait(obj);
    }
    
    private void throwBall() {
        System.out.println("Ball thrown");
        model.ballThrow(pos, throwSpeed, this);
    }
    
    @Override
    protected void loadNextPosition() {
        
        // check lookout and wait there
        if (targetPosition.distanceFrom(pos) <= speed) {
            int wait = routeWait.get(route.indexOf(targetPosition));
            if (wait != 0) {
                states.get(NPC_STATE.STOP_LOOKOUT).max = wait;
                states.get(NPC_STATE.STOP_LOOKOUT).active = true;
            }
            targetPosition = nextRouteTarget();
        }
        
        // increase
        states.get(NPC_STATE.STOP_LOOKOUT).increase();
        states.get(NPC_STATE.STOP_THROW).increase();
        states.get(NPC_STATE.WALKING_CAUTIOUS).increase();
        
        // if nothing stops go to next direction
        if (states.get(NPC_STATE.STOP_LOOKOUT).active ||
            states.get(NPC_STATE.STOP_EXCLAMATION).active ||
            states.get(NPC_STATE.STOP_THROW).active) stopWalking();
        else startWalking(); 
        
        // ha épp befejeződött az álló várakozás akkor még ne induljon el
        if (states.get(NPC_STATE.STOP_EXCLAMATION).increase()) {
            states.get(NPC_STATE.WALKING_CAUTIOUS).active = true;
            stopWalking();
        }
        if (!states.get(NPC_STATE.STOP_LOOKOUT).active) {
            nextDirection = Direction.getDirection(pos,targetPosition);
        }
        
        super.loadNextPosition();
    }
    
    private Position nextRouteTarget() {
        if ((!routeIterator.hasNext() && forward) || (!routeIterator.hasPrevious() && !forward)) forward = !forward;

        if (forward) return routeIterator.next();
        else return routeIterator.previous();
    }
    
    @Override
    public void loop() {
        Position playerPostion = model.getPlayer().getPosition();
        double playerDistance = playerPostion.distanceFrom(pos);
        Direction playerDirection = Direction.getDirection(pos, playerPostion);
        if (Direction.isInLineOfSight(direction, playerDirection)) {
            //System.out.println(String.format("Player is in LOS : %s - %s (%.0f)",direction,playerDirection.name(),distance));
            
            // Ha LOS-ban van és nem éppen dob
            if (!states.get(NPC_STATE.STOP_THROW).active){
                // Ha dobási távolságon belül van
                if (playerDistance < throwDistance) {
                    // Ha figyelmesen halad, akkor dobjon
                    if (states.get(NPC_STATE.WALKING_CAUTIOUS).active) {
                        throwBall();
                        states.get(NPC_STATE.STOP_THROW).active = true;
                        states.get(NPC_STATE.STOP_EXCLAMATION).active = false;
                        states.get(NPC_STATE.WALKING_CAUTIOUS).active = false;
                    } else {
                    // egyébként indítson el egy várakozót
                        states.get(NPC_STATE.STOP_EXCLAMATION).active = true;
                    }
                }
            }
        }
        super.loop();
    }
    
    public boolean seesPlayer() {return states.get(NPC_STATE.STOP_EXCLAMATION).active || states.get(NPC_STATE.WALKING_CAUTIOUS).active;}
    public BufferedImage getExclamation() {return exclamation;}
    
    private void loadRoute(Shape shape){
        this.route = new ArrayList<>();
        PathIterator pi = shape.getPathIterator(null);
        
        while (!pi.isDone()) {
            double[] coords = new double[2];
            int type = pi.currentSegment(coords);
            
            if (type!= PathIterator.SEG_CLOSE) {
                coords[1] -= (double)UNITSIZE * 0.4; // route should be at feet, not in center
                Position position = new Position(coords);
                if (!(position.x == 0 && position.y == 0)) route.add(position);
            }
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
            routeWait.add(Integer.parseInt(waitStr));
        }
    }
    
    private HashMap<NPC_STATE,NpcState> getStateArray() {
         HashMap<NPC_STATE,NpcState> states = new HashMap<>();
         states.put(NPC_STATE.STOP_LOOKOUT,new NpcState(0));
         states.put(NPC_STATE.STOP_EXCLAMATION,new NpcState(0));
         states.put(NPC_STATE.STOP_THROW,new NpcState(45));
         states.put(NPC_STATE.WALKING_CAUTIOUS,new NpcState(99));
         return states;
    }
        
    private void loadLevelProperties() {
        switch (level) {
            default:
            case 1 : 
                this.speed = 2; 
                this.throwDistance = 150; // 150 - easy, 300 - very hard
                this.throwSpeed = 10;
                this.name = "Noob NPC";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 49;
                setImg("trchar035.png");
                break;
            case 2 : 
                this.speed = 2; 
                this.throwDistance = 150; // 150 - easy, 300 - very hard
                this.throwSpeed = 10;
                this.name = "Noobest NPC";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 49;
                setImg("trchar026.png");
                break;
        }
    }
    
    private class NpcState{
        public boolean active;
        private int counter;
        public int max;

        public NpcState(int max) {
            this.max = max;
        }
        
        // returns true if reached max
        public boolean increase() {
            if (active) {
                counter++;
                if (counter == max) {
                    active = false;
                    counter = 0;
                    return true;
                }
            }
            return false;
        }
        
    }

}
