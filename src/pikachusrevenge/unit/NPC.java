package pikachusrevenge.unit;

import java.awt.Image;
import pikachusrevenge.model.Position;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    
    private List<NpcRoute> route;
    private int routeTarget;
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
        loadNpcRoutePorperties(obj);
    }
    
    private void throwBall() {
        System.out.println("Ball thrown");
        states.get(NPC_STATE.STOP_THROW).active = true;
        states.get(NPC_STATE.STOP_EXCLAMATION).active = false;
        states.get(NPC_STATE.WALKING_CAUTIOUS).active = false;
        model.ballThrow(pos, throwSpeed, this);
    }
    
    @Override
    protected void loadNextPosition() {
        
        // check lookout and wait there
        if (targetPosition.distanceFrom(pos) <= speed) {
            int wait = route.get(routeTarget).wait;
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
        if (route.get(routeTarget).reverse) forward = !forward;
        if (forward) {
            if (routeTarget + 1 < route.size()) routeTarget++;
            else routeTarget = 0;
        } else {
            if (routeTarget - 1 >= 0) routeTarget--;
            else routeTarget = route.size();
        }
        return route.get(routeTarget).pos;
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
                    } else {
                    // egyébként indítson el egy várakozót és ha még közelebb jön dobjon
                        states.get(NPC_STATE.STOP_EXCLAMATION).active = true;
                        if (playerDistance < throwDistance*0.6) throwBall();
                    }
                }
            }
        }
        super.loop();
    }
    
    public boolean seesPlayer() {return states.get(NPC_STATE.STOP_EXCLAMATION).active || 
                                        states.get(NPC_STATE.WALKING_CAUTIOUS).active ||
                                        states.get(NPC_STATE.STOP_THROW).active;}
    public BufferedImage getExclamation() {return exclamation;}
    
    private void loadRoute(Shape shape){
        this.route = new ArrayList<>();
        PathIterator pi = shape.getPathIterator(null);
        
        while (!pi.isDone()) {
            double[] coords = new double[2];
            int type = pi.currentSegment(coords); // 0 - start, 1 - normal, 4 - close
            
            if (type!= PathIterator.SEG_CLOSE) {
                coords[1] -= (double)SPRITE_SIZE * 0.3; // route should be at feet, not in center
                Position position = new Position(coords);
                if (!(position.x == 0 && position.y == 0)) route.add(new NpcRoute(position));
            }
            pi.next();
        }
        
        setStartingPostion(route.get(0).pos.x, route.get(0).pos.y);
        this.targetPosition = route.get(1).pos;
        routeTarget = 1;
        
        loadNextPosition();
    }
    
    private void loadNpcRoutePorperties(MapObject obj){
        Properties prop = obj.getProperties();
        
        for (int i = 0; i < route.size(); ++i){
            String waitStr = prop.getProperty("Wait" + i, "0");
            route.get(i).wait = Integer.parseInt(waitStr);
        }
        int reverseId = Integer.parseInt(prop.getProperty("Reverse", "0"));
        if (reverseId != 0 && reverseId < route.size()){
            route.get(reverseId).reverse = true;
        }
    }
    
    private HashMap<NPC_STATE,NpcState> getStateArray() {
         HashMap<NPC_STATE,NpcState> states = new HashMap<>();
         states.put(NPC_STATE.STOP_LOOKOUT,new NpcState(0));
         states.put(NPC_STATE.STOP_EXCLAMATION,new NpcState(0));
         states.put(NPC_STATE.STOP_THROW,new NpcState(50));
         states.put(NPC_STATE.WALKING_CAUTIOUS,new NpcState(100));
         return states;
    }
        
    private void loadLevelProperties() {
        switch (level) {
            default:
            case 1 : 
                this.speed = 0.8; 
                this.throwDistance = 120; // 150 - easy, 300 - very hard
                this.throwSpeed = 8;
                this.name = "Green Girl";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 50;
                setImg("npc\\trchar153.png"); // green girl
                break;
            case 2 : 
                this.speed = 0.8; 
                this.throwDistance = 130; // 150 - easy, 300 - very hard
                this.throwSpeed = 10;
                this.name = "Red Hat Girl";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 50;
                setImg("npc\\trchar035.png"); // red hat girl
                break;
            case 3 : 
                this.speed = 0.8; 
                this.throwDistance = 130; // 150 - easy, 300 - very hard
                this.throwSpeed = 8;
                this.name = "Blue Cap Boy";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 50;
                setImg("npc\\trchar040.png"); // blue hat boy
                break;
            case 4 : 
                this.speed = 1; 
                this.throwDistance = 150; // 150 - easy, 300 - very hard
                this.throwSpeed = 8;
                this.name = "Green Hat Boy";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 50;
                setImg("npc\\trchar026.png"); // green hat boy
                break;
            case 10 : 
                this.speed = 2.5; 
                this.throwDistance = 200; // 150 - easy, 300 - very hard
                this.throwSpeed = 10;
                this.name = "Epix NPC";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 50;
                setImg("npc\\trchar183.png"); // red/purple boy
                break;
        }
    }
    
    private class NpcRoute{
        public boolean reverse;
        public Position pos;
        public int wait;

        public NpcRoute(Position pos) {
            this.pos = pos;
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
