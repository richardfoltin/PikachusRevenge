package pikachusrevenge.model;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JLabel;
import javax.swing.Timer;
import org.mapeditor.core.Map;
import org.mapeditor.core.MapLayer;
import org.mapeditor.core.Tile;
import org.mapeditor.core.TileLayer;
import pikachusrevenge.gui.MainWindow;
import static pikachusrevenge.gui.MapView.GRIDSIZE;
import static pikachusrevenge.gui.MapView.ZOOM;
import pikachusrevenge.unit.MovingSprite;
import pikachusrevenge.unit.NPC;
import pikachusrevenge.unit.Player;
import pikachusrevenge.unit.PokeBall;
import pikachusrevenge.unit.Pokemon;
import static pikachusrevenge.unit.Unit.C_BOX_HEIGHT;
import static pikachusrevenge.unit.Unit.C_BOX_WIDTH;

public class Model implements ActionListener {
        
    private final HashMap<TilePosition,Pokemon> pokemons;
    private final ArrayList<Level> levels;
    private List<MapLayer> layers;
    private Level actualLevel;
    private final MainWindow mainWindow;
    private final Player player;
    private final Timer timer;
    private final Timer clock;
    public Rectangle mapRectangle;
    private final String fileName;
    private final int dbId;

    private static final int MAIN_LOOP = 40;  
    
    
    public Model () {this(null, 0);}
    public Model (int dbId) {this(null, dbId);}
    public Model (String fileName) {this(fileName, 0);}
    
    private Model (String fileName, int dbId){
        this.dbId = dbId;
        this.fileName = fileName;
        this.pokemons = new HashMap<>();
        this.levels = new ArrayList<>();
        this.player = new Player(this);
        this.mainWindow = MainWindow.getInstance();
        this.timer = new Timer(MAIN_LOOP, this);
        this.clock = new Timer(1000, (ActionEvent e) -> {
            mainWindow.getStats().updateTimeLabel(actualLevel.increaseTime());
        });
    }
    
    public Level buildLevelIfNotExists(int id, int time) {
        Level level = null;
        for (Level l : levels) if (l.getId() == id) level = l;
        
        if (level == null) {
            level = new Level(this,id,time);
            this.levels.add(level);
        }
        
        return level;
    }
    
    public void startGame(Level level, boolean forward) {

        this.actualLevel = level; 
        Map map = level.getMap();
        this.layers = map.getLayers();
        this.mapRectangle = new Rectangle(0, 0, map.getWidth() * GRIDSIZE, map.getHeight() * GRIDSIZE);       
         
        // MenuBar
        boolean canBeSaved = (dbId != 0 || fileName != null);
        mainWindow.getGameMenu().getSaveMenu().setEnabled(canBeSaved);
        mainWindow.getGameMenu().buildPokedexMenu(this);
        
        mainWindow.getStats().clearPane();
        // put lives on stats
        for (int i = 0; i < player.getLives(); ++i) mainWindow.getStats().addLife();
        
        // put pokemons and balls on stats
        for (Pokemon p : actualLevel.getPokemons()) {
            JLabel label = mainWindow.getStats().addBall();
            p.setLabel(label);
            if (p.isFound()) p.revealLabel();
        }
        
        player.setStartingPostion(actualLevel.getPlayerStartingPosition(forward));
        for (NPC npc : actualLevel.getNpcs()) npc.startLooping();
        for (Pokemon p : actualLevel.getPokemons()) if (p.isFound()) p.restartFromStratingPoint();
        player.startLooping();
        clock.start();
        timer.start();
    }
    
    public boolean canMoveTo(Position from, Direction nextDirection, double speed){
        if (nextDirection == Direction.STOP) return false;
        Position targetPosition = new Position(from.x + nextDirection.x * speed, from.y + nextDirection.y * speed);
        Rectangle target = new Rectangle(0, 0, (int)(C_BOX_WIDTH*ZOOM), (int)(C_BOX_HEIGHT*ZOOM));
        MovingSprite.moveCollisionBoxTo(target,targetPosition);
        if (!mapRectangle.contains(target)) return false;
        
        PathIterator pi = target.getPathIterator(null);
        ArrayList<Collision> collisions = new ArrayList<>();
        
        while (!pi.isDone()) {
            double[] coords = new double[2];
            int type = pi.currentSegment(coords);
            if (type!= PathIterator.SEG_CLOSE) {
                collisions.add(Collision.collisionOnTileAt(layers,new Position(coords))) ;
            }
            pi.next();
        }  
        return Collision.canMoveToCollisions(collisions);
    }
    
    public void ballThrow(Position from, double speed, NPC owner){
        PokeBall ball = new PokeBall(from.x, from.y, speed, this, owner);
        actualLevel.getThrownBalls().add(ball);
        ball.startLooping();
    }
    
    public void ballReachedPlayer(PokeBall ball) {
        actualLevel.addCleanUp(ball);
        player.playerCaught();
        writeInfo(String.format("<font color=black>%s</font> caught you!",ball.getOwner().getName()));
    }

    public void playerInteraction(){
        if (player.isAtSign()){
            if (actualLevel.getId() == 10) {
                System.out.println("Game finished");
            } else {
                if (canMoveToNextLevel()) {
                    stopGame();
                    mainWindow.loadLevel(actualLevel.getId() + 1);
                }else {
                    writeInfo("<font color=black>Can't</font> move to next level yet!");
                }
            }
        }
    }
    
    public void gameOver(){
        System.out.println("Game over");
    }
    
    public void stopGame() {
        player.stopLooping();
        clock.stop();
        timer.stop();
    }
    
    public void restartGame() {
        clock.restart();
        timer.restart();
    }

    public boolean checkSign(Position pos) {
        for (MapLayer l : layers){
            if (l instanceof TileLayer){
                Tile t = ((TileLayer)l).getTileAt(TilePosition.tileCoordFromMapCoord(pos.x),TilePosition.tileCoordFromMapCoord(pos.y));
                if (actualLevel.hasProperty(t,"Sign")) return true;
            } 
        } 
        return false;
    }
    
    public void checkBallPokemonAt(Rectangle target){
        
        PathIterator pi = target.getPathIterator(null);
        
         while (!pi.isDone()) {
            double[] coords = new double[2];
            int type = pi.currentSegment(coords);
            if (type!= PathIterator.SEG_CLOSE) {
                TilePosition tpos = TilePosition.fromMapPosition(new Position(coords),actualLevel.getId());
                if (pokemons.containsKey(tpos)) {
                    Pokemon p = pokemons.get(tpos);
                    if (!p.isFound()) {
                        writeInfo("You have found <font color=black>" + Pokemon.POKEMON_NAME[p.getId()-1] + "</font>");
                        actualLevel.clearTileWithProperty("Ball", tpos);
                        actualLevel.increaseFoundPokemonCount();
                        p.found();
                        mainWindow.getGameMenu().buildPokedexMenu(this);
                    }
                }
            }
            pi.next();
        }  
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer){
            if (player.isLooping()) player.loop();
            actualLevel.loop();
            mainWindow.getFooter().loop();
            mainWindow.repaintMap();
            movePanelTo(player.getPosition());
        }
    }    
    
    private void writeInfo(String str){
        mainWindow.getFooter().write(str);
    }   
    
    public void movePanelTo(Position position){
        mainWindow.scrollTo(position);
    }
    
    public boolean canMoveToNextLevel() {
        return actualLevel.canAdvanceToNextLevel();
    }
    
    public ArrayList<NPC> getNpcs() {return actualLevel.getNpcs();}
    public ArrayList<Pokemon> getMapPokemons() {return actualLevel.getPokemons();}
    public HashMap<TilePosition,Pokemon> getAllPokemons() {return pokemons;}
    public ArrayList<PokeBall> getThrownBalls() {return actualLevel.getThrownBalls();}
    public Player getPlayer() {return player;}
    public Collection<Integer> getAllIds() {return pokemons.values().stream().map(p -> p.getId()).collect(Collectors.toList());}
    public Collection<Integer> getAllFoundIds() {return pokemons.values().stream().filter(p -> p.isFound()).map(p -> p.getId()).collect(Collectors.toList());}
    public ArrayList<Level> getLevels() {return levels;}
    public int getActualLevelId() {return (actualLevel == null) ? 0 :actualLevel.getId();}
    public Level getActualLevel() {return actualLevel;}
    public String getFileName() {return fileName;}
    public int getDbId() {return dbId;}
    public boolean isSavedToDb() {return (dbId != 0 && fileName == null);}
}
