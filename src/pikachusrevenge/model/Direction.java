package pikachusrevenge.model;

import static java.lang.Math.floor;
import static java.lang.Math.signum;
import java.util.Arrays;
import java.util.Random;

/**
 * A játékban a játékos és az egységek által használt irányvektorokat, és a 
 * hozzájuk tartozó statikus metódusokat összegyűjtő enum.
 * @author Csaba Foltin
 */
public enum Direction {
    UPLEFT(-1,-1),
    UP(0, -1), 
    UPRIGHT(1,-1),
    LEFT(-1, 0), 
    STOP(0, 0),
    RIGHT(1, 0),
    DOWNLEFT(-1,1),
    DOWN(0, 1), 
    DOWNRIGHT(1,1);
    
    public final double x;
    public final double y;

    Direction(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    /**
     * Az irányvektorok koordinátáiból visszagenerálja a {@link Direction}-t
     * @param x x
     * @param y y
     * @return az irány
     */
    public static Direction getDirection(double x, double y){
        int id = ((int)x+1) + ((int)y+1)*3; // linux chown trükk
        Direction d = Arrays.asList(Direction.values()).get(id);
        return d;
    }
    
    /**
     * Megmondja hogy milyen irányba található a térképen az egyik pozíció a
     * másikhoz képest
     * @param from honnan
     * @param to hova
     * @return az irány
     */
    public static Direction getDirection(Position from, Position to){
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        int eighth = (int)floor(Math.abs(Math.atan(dy/dx)) / (Math.PI / 8));
        switch (eighth) {
            case 0 : return getDirection(signum(dx),0);
            case 1 : 
            case 2 : return getDirection(signum(dx),signum(dy));
            case 3 : 
            case 4 : return getDirection(0,signum(dy));
            default: return Direction.STOP;
        }
    }
    
    /**
     * Megmondja - pusztán csak a 4 főirányt használva - hogy milyen irányba
     * próbálkozzon az egység az előtte álló akadály kikerülésével.
     * Tulajdonképpen visszaadja a bezárt szög rövidebb átfogója által meghatározott
     * irányt.
     * @param from honnan
     * @param to hova
     * @return az irány
     */
    public static Direction getSecondDirection(Position from, Position to){
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        int fouth = (int)floor(Math.abs(Math.atan(dy/dx)) / (Math.PI / 4));
        switch (fouth) {
            case 0 : return getDirection(0,signum(dy));
            case 1 : 
            case 2 : return getDirection(signum(dx),0);
            default: return Direction.STOP;
        }
    }
    
    /**
     * Visszaadja hogy egy egység látószögében van-e egy irány.
     * Akkor van a látószögében, ha ugyanabban, vagy egy eggyel mellette lévő 
     * irányban van. (135° összesen)
     * @param from honnan
     * @param to hova
     * @return true, ha a látószögben van
     */
    public static boolean isInDirectionOfSight(Direction from, Direction to) {
        if (from == to) return true;
        if (from == STOP || to == STOP) return false;
        if (Math.abs(from.x - to.x) + Math.abs(from.y - to.y) <= 1) return true;
        return false;
    }
    
    /**
     * Az egység látószögét megjelnítő körcikk kezdősugarának szögét adja vissza.
     * @param d az egység nézésének iránya
     * @return a körcikk kezdősugarának szöge fokban
     */
    public static int directionAngleStart(Direction d) {
        double radian = Math.atan2(-d.y, d.x);
        int degree = (int)Math.toDegrees(radian);
        // System.out.println(String.format("%s : %.2f : %d", d, radian, degree));
        return (degree + 360 - 68) % 360 ;
    }
}
