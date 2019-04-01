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
import java.util.Random;
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
    private int startRoute;
    private HashMap<NPC_STATE,NpcState> states;
    
    private List<NpcRoute> route;
    private int routeTarget;
    private Position targetPosition;
    private boolean forward = true;
    private boolean carry;
    
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
        
        loadNpcProperties();
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
        boolean teleport = route.get(routeTarget).teleportToNext;
        if (route.get(routeTarget).reverse) forward = !forward;
        if (forward) {
            if (routeTarget + 1 < route.size()) routeTarget++;
            else {
                routeTarget = 0;
            }
        } else {
            if (routeTarget - 1 >= 0) routeTarget--;
            else routeTarget = route.size()-1;
        }
        if (teleport) {
            pos = new Position(route.get(0).pos);
            nextPosition = new Position(route.get(0).pos);
        }
        return route.get(routeTarget).pos;
    }
    
    @Override
    public void loop() {
        Position playerPostion = model.getPlayer().getPosition();
        double playerDistance = playerPostion.distanceFrom(pos);
        Direction playerDirection = Direction.getDirection(pos, playerPostion);
        if (!states.get(NPC_STATE.STOP_THROW).active &&                         // nem éppen dob
            !model.getPlayer().insideBall() &&                                  // player nincs már elkapva
            playerDistance < throwDistance &&                                   // player dobási távolságon belül van
            Direction.isInDirectionOfSight(direction, playerDirection) &&       // player a megfelelő irányban van
            model.getActualLevel().isInLineOfSight(pos,playerPostion) &&        // player LOS-ban van
            !model.getPlayer().isOnCarry()){                                    // player nem épp carry-n van
            //System.out.println(String.format("Player is in LOS : %s - %s (%.0f)",direction,playerDirection.name(),distance));
            
            // Ha figyelmesen halad, akkor dobjon
            if (states.get(NPC_STATE.WALKING_CAUTIOUS).active) {
                throwBall();
            } else {
                // egyébként indítson el egy várakozót és ha még közelebb jön dobjon
                states.get(NPC_STATE.STOP_EXCLAMATION).active = true;
                if (playerDistance < throwDistance*0.6) throwBall();
            }
            
       }
        super.loop();
    }
    
    public boolean seesPlayer() {return states.get(NPC_STATE.STOP_EXCLAMATION).active || 
                                        states.get(NPC_STATE.WALKING_CAUTIOUS).active ||
                                        states.get(NPC_STATE.STOP_THROW).active;}
    public BufferedImage getExclamation() {return exclamation;}
    public boolean getCarry() {return carry;}
    
    private void loadRoute(Shape shape){
        this.route = new ArrayList<>();
        PathIterator pi = shape.getPathIterator(null);
        
        while (!pi.isDone()) {
            double[] coords = new double[2];
            int type = pi.currentSegment(coords); // 0 - start, 1 - normal, 4 - close
            
            if (type!= PathIterator.SEG_CLOSE) {
                coords[1] -= 5; // route should be at feet, not in center
                Position position = new Position(coords);
                if (!(position.x == 0 && position.y == 0)) route.add(new NpcRoute(position));
            }
            pi.next();
        }
    }
    
    private void loadNpcRoutePorperties(MapObject obj){
        Properties prop = obj.getProperties();
        
        carry = Boolean.parseBoolean(prop.getProperty("Carry","false"));
        
        for (int i = 0; i < route.size(); ++i){
            String waitStr = prop.getProperty("Wait" + i, "0");
            route.get(i).wait = Integer.parseInt(waitStr);
        }
        int reverseId = Integer.parseInt(prop.getProperty("Reverse", "0"));
        if (reverseId != 0 && reverseId < route.size()){
            route.get(reverseId).reverse = true;
            route.get(0).reverse = true;
        }
        int teleportId = Integer.parseInt(prop.getProperty("Teleport", "0"));
        if (teleportId != 0 && teleportId < route.size()){
            route.get(teleportId).teleportToNext = true;
        }
        startRoute = Integer.parseInt(prop.getProperty("Start", "0"));
        if (startRoute >= route.size()) startRoute = 0;
        if (startRoute == -1) startRoute = new Random().nextInt(route.size()-1);
        setStartingPostion(route.get(startRoute).pos.x, route.get(startRoute).pos.y);
        this.targetPosition = route.get(startRoute + 1).pos;
        routeTarget = startRoute + 1;
        
        loadNextPosition();
    }
    
    private HashMap<NPC_STATE,NpcState> getStateArray() {
         HashMap<NPC_STATE,NpcState> states = new HashMap<>();
         states.put(NPC_STATE.STOP_LOOKOUT,new NpcState(0));
         states.put(NPC_STATE.STOP_EXCLAMATION,new NpcState(0));
         states.put(NPC_STATE.STOP_THROW,new NpcState(50));
         states.put(NPC_STATE.WALKING_CAUTIOUS,new NpcState(100));
         return states;
    }
        
    private void loadNpcProperties() {
        switch (level) {
            default:
            case 1 : 
                this.speed = 0.8; 
                this.throwDistance = 120; // 100 - easy, 200 - very hard
                this.throwSpeed = 8;
                this.name = "Green Girl";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 70;
                setImg("npc\\trchar153.png"); // green girl
                break;
            case 2 : 
                this.speed = 1; 
                this.throwDistance = 140; // 100 - easy, 200 - very hard
                this.throwSpeed = 8;
                this.name = "Blonde Girl";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 50;
                setImg("npc\\trchar066.png"); // blonde girl
                break;
            case 3 : 
                this.speed = 1; 
                this.throwDistance = 140; // 100 - easy, 200 - very hard
                this.throwSpeed = 8;
                this.name = "Farmer";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 50;
                //setImg("npc\\trchar026.png"); // green hat boy
                setImg("npc\\HGSS_229.png"); // Farmer
                break;
            case 4 : 
                this.speed = 0.6; 
                this.throwDistance = 150; // 100 - easy, 200 - very hard
                this.throwSpeed = 6;
                this.name = "Old Fart";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 70;
                setImg("npc\\trchar060.png"); // old boy
                break;
            case 5 : 
                this.speed = 1; 
                this.throwDistance = 150; // 100 - easy, 200 - very hard
                this.throwSpeed = 8;
                this.name = "Red Hat Girl";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 50;
                setImg("npc\\trchar035.png"); // red hat girl
                break;
            case 6 : 
                this.speed = 1; 
                this.throwDistance = 150; // 100 - easy, 200 - very hard
                this.throwSpeed = 8;
                this.name = "Blue Cap Boy";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 40;
                setImg("npc\\trchar040.png"); // blue hat boy
                break;
            case 7 : 
                this.speed = 1.2; 
                this.throwDistance = 170; // 100 - easy, 200 - very hard
                this.throwSpeed = 12;
                this.name = "Ginger Boy";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 40;
                setImg("npc\\trchar075.png"); // red boy
                break;
            case 8 : 
                this.speed = 0.8; 
                this.throwDistance = 100; // 100 - easy, 200 - very hard
                this.throwSpeed = 8;
                this.name = "Cultist";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 50;
                setImg("npc\\trchar163.png"); // cultist
                break;
            case 9 : 
                this.speed = 1.0; 
                this.throwDistance = 80; // 100 - easy, 200 - very hard
                this.throwSpeed = 12;
                this.name = "Cultist Boss";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 40;
                setImg("npc\\trchar164.png"); // boss cultist
                break;
            case 10 : 
                this.speed = 2.6; 
                this.throwDistance = 70; // 100 - easy, 200 - very hard
                this.throwSpeed = 12;
                this.name = "Snowboard Boy";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 10;
                setImg("npc\\trchar013.png"); // snowboard boy
                break;
            case 11 : 
                this.speed = 2.2; 
                this.throwDistance = 90; // 100 - easy, 200 - very hard
                this.throwSpeed = 12;
                this.name = "Ski Girl";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 10;
                setImg("npc\\trchar014.png"); // ski girl
                break;
            case 12 : 
                this.speed = 2.5; 
                this.throwDistance = 70; // 100 - easy, 200 - very hard
                this.throwSpeed = 12;
                this.name = "Snowboard Boy";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 10;
                setImg("npc\\trchar013_mod.png"); // snowboard boy 2
                break;
            case 13 : 
                this.speed = 2.2; 
                this.throwDistance = 90; // 100 - easy, 200 - very hard
                this.throwSpeed = 12;
                this.name = "Ski Girl";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 10;
                setImg("npc\\trchar014_mod.png"); // ski girl 2
                break;
            case 14 : 
                this.speed = 1.0; 
                this.throwDistance = 200; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "Fat Evil Guy";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 20;
                setImg("npc\\Leader_Chuck.png"); // at Evil Guy
                break;
            case 15 : 
                this.speed = 1.0; 
                this.throwDistance = 200; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "White Hair Evil Guy";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 20;
                setImg("npc\\Leader_Damian.png"); // White Hair Evil Guy
                break;
            case 16 : 
                this.speed = 1.0; 
                this.throwDistance = 200; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "Purple Hair Evil Girl";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 20;
                setImg("npc\\Leader_Janine.png"); // Purple Hair Evil Girl
                break;
            case 17 : 
                this.speed = 1.3; 
                this.throwDistance = 200; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "Purple Evil Skirt Girl";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 20;
                setImg("npc\\Leader_Fantina.png"); // Purple Evil Skirt Girl
                break;
            case 18 : 
                this.speed = 1.0; 
                this.throwDistance = 200; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "Old Evil Guy";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 20;
                setImg("npc\\Leader_Pryce.png"); // Old Evil Guy
                break;
            case 19 : 
                this.speed = 1.3; 
                this.throwDistance = 200; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "Epic Fat Evil Guy";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 20;
                setImg("npc\\Leader_Wake.png"); // Epic Fat Evil Guy
                break;
            case 20 : 
                this.speed = 1.0; 
                this.throwDistance = 120; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "Monopoly Guy";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 50;
                setImg("npc\\Leader_Blaine.png"); // Monopoly Guy
                break;
            case 21 : 
                this.speed = 2.0; 
                this.throwDistance = 90; // 100 - easy, 200 - very hard
                this.throwSpeed = 12;
                this.name = "Motor Guy with Hat";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 40;
                setImg("npc\\trchar058.png"); // Motor Guy
                break;
            case 22 : 
                this.speed = 1.0; 
                this.throwDistance = 210; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "Cameraman";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 60;
                setImg("npc\\trchar069_0.png"); // Cameraman
                break;
            case 23 : 
                this.speed = 0.8; 
                this.throwDistance = 140; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "Navy Guy";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 50;
                setImg("npc\\trchar027.png"); // Navy Guy
                break;
            case 24 : 
                this.speed = 1.0; 
                this.throwDistance = 140; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "Policeman";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 50;
                setImg("npc\\trchar062.png"); // Policeman
                break;
            case 25 : 
                this.speed = 1.0; 
                this.throwDistance = 80; // 100 - easy, 200 - very hard
                this.throwSpeed = 8;
                this.name = "Little Girl";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 60;
                setImg("npc\\trchar051.png"); // Little Girl
                break;
            case 26 : 
                this.speed = 0.8; 
                this.throwDistance = 100; // 100 - easy, 200 - very hard
                this.throwSpeed = 8;
                this.name = "Old Monk";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 50;
                setImg("npc\\NPC_10.png"); // Old Monk
                break;
            case 27 : 
                this.speed = 2.7; 
                this.throwDistance = 70; // 100 - easy, 200 - very hard
                this.throwSpeed = 12;
                this.name = "Blonde Motor Guy";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 40;
                setImg("npc\\trchar008.png"); // Blonde Motor Guy
                break;
            case 28 : 
                this.speed = 1.0; 
                this.throwDistance = 120; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "Green Girl With a Ball";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 50;
                setImg("npc\\trchar069_1.png"); // Green Girl With a Ball
                break;
                //////
            case 30 : 
                this.speed = 1.0; 
                this.throwDistance = 100; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "Light Blue Hair Guy";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 20;
                setImg("npc\\trchar081.png"); // Light Blue Hair Guy
                break;
            case 31 : 
                this.speed = 5.0; 
                this.throwDistance = 60; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "red/purple boy";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 20;
                setImg("npc\\trchar183.png"); // red/purple boy
                break;
            case 32 : 
                this.speed = 1.0; 
                this.throwDistance = 100; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "Pink Hair Girl";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 50;
                setImg("npc\\Leader_Whitney.png"); // Pink Hair Girl
                break;
            case 33 : 
                this.speed = 1.0; 
                this.throwDistance = 100; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "Black Girl";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 50;
                setImg("npc\\trchar272.png"); // Black Girl
                break;
            case 34 : 
                this.speed = 1.0; 
                this.throwDistance = 150; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "Black Mage Girl";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 50;
                setImg("npc\\trchar275.png"); // Black Mage Girl
                break;
            case 35 : 
                this.speed = 1.2; 
                this.throwDistance = 100; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "Island Cultist 1";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 40;
                setImg("npc\\trchar167.png"); // Island Cultist 1
                break;
            case 36 : 
                this.speed = 1.2; 
                this.throwDistance = 100; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "Island Cultist 2";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 40;
                setImg("npc\\trchar172.png"); // Island Cultist 2
                break;
            case 37 : 
                this.speed = 1.2; 
                this.throwDistance = 100; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "Island Cultist 3";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 40;
                setImg("npc\\trchar174.png"); // Island Cultist 3
                break;
            case 90 : 
                this.speed = 0.8; 
                this.throwDistance = 180; // 100 - easy, 200 - very hard
                this.throwSpeed = 10;
                this.name = "Ice Yeti";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 40;
                setImg("npc\\delta_regice.png"); // Ice Yeti
                break;
            case 91 : 
                this.speed = 0.8; 
                this.throwDistance = 0; // 100 - easy, 200 - very hard
                this.throwSpeed = 0;
                this.name = "Lapras";
                this.states.get(NPC_STATE.STOP_EXCLAMATION).max = 0;
                setImg("npc\\HGSS_002.png"); // Lapras
                break;
        }
    }
    
    private class NpcRoute{
        public boolean reverse;
        public Position pos;
        public int wait;
        public boolean teleportToNext;

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
