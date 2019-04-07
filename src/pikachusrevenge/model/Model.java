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

public final class Model implements ActionListener {

    private static final int MAIN_LOOP = 40;  
    
    private final HashMap<TilePosition,Pokemon> pokemons = new HashMap<>();
    private final ArrayList<Level> levels = new ArrayList<>();
    private final Player player = new Player(this);
    private final Timer timer = new Timer(MAIN_LOOP, this);
    private final MainWindow mainWindow;
    private final Timer clock;
    private final Difficulty difficulty;
    private String fileName;
    private int dbId;
    
    private List<MapLayer> layers;
    private Level actualLevel;
    
    public Rectangle mapRectangle;
    
    public Model (Difficulty difficulty) {this(null, 0, difficulty);}
    public Model (int dbId, Difficulty difficulty) {this(null, dbId, difficulty);}
    public Model (String fileName, Difficulty difficulty) {this(fileName, 0, difficulty);}
    
    private Model (String fileName, int dbId, Difficulty difficulty){
        this.dbId = dbId;
        this.difficulty = difficulty;
        this.fileName = fileName;
        this.mainWindow = MainWindow.getInstance();
        this.clock = new Timer(1000, (ActionEvent e) -> {
            mainWindow.getStats().updateTimeLabel(actualLevel.increaseTime());
        });
    }
    
    public Level buildLevelIfNotExists(int id, int time) {
        Level level = null;
        for (Level l : levels) if (l.getId() == id) level = l;
        
        if (level == null) {
            level = new Level(this,id,time,player.getLives());
            this.levels.add(level);
        }
        
        return level;
    }
    
    public Level rebuildLevel(int id) {
        stopGame();
        Level level = null;
        for (Level l : levels) if (l.getId() == id) level = l;

        if (level != null) {
            int livesAtBegining = level.getLivesAtBegining();
            for (Pokemon p : actualLevel.getPokemons()) p.setNotFound();
            this.levels.remove(level);
            
            level = new Level(this,id,0,livesAtBegining);
            player.setLives(livesAtBegining);
            player.restartFromStratingPoint();
            this.levels.add(level);
            return level;
        } else {
            return buildLevelIfNotExists(id, 0);
        }
    }
    
    public void startGame(Level level, Position start) {

        this.actualLevel = level; 
        Map map = level.getMap();
        this.layers = map.getLayers();
        this.mapRectangle = new Rectangle(0, 0, map.getWidth() * GRIDSIZE, map.getHeight() * GRIDSIZE);       
         
        // MenuBar
        mainWindow.getGameMenu().getSaveMenu().setEnabled(fileName != null);
        mainWindow.getGameMenu().getSaveDbMenu().setEnabled(dbId != 0);
        mainWindow.getGameMenu().buildPokedexMenu(this);
        
        mainWindow.getStats().clearPane();
        // put lives on stats
        for (int i = 0; i < player.getLives(); ++i) mainWindow.getStats().addLife();
        
        // put pokemons and balls on stats
        for (Pokemon p : actualLevel.getPokemons()) {
            JLabel label = mainWindow.getStats().addBall();
            p.setLabel(label);
            if (p.isFound()) p.revealLabel(label);
        }
        mainWindow.getStats().updateTimeLabel(actualLevel.getTime());
        
        for (NPC npc : actualLevel.getNpcs()) npc.startLooping();
        for (Pokemon p : actualLevel.getPokemons()) {
            if (p.isFound()) {
                p.putToPosition((start == null) ? p.getStartPosition() : start);
                p.startLooping();
            }
        }
        player.putToPosition((start == null) ? player.getStartPosition() : start);
        resumeGame();
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
        } else if (player.isAtCarry() != null) {
            player.putOnCarry();
        } else if (player.isOnCarry()) {
            Position shorePosition = checkCarryOff(player.getPosition());
            if (shorePosition != null) {
                player.getOffCarry(shorePosition);
            } else {
                writeInfo("You can only get off near shore!");
            }  
        }
    }
    
    public void gameOver(){
        stopGame();
        MainWindow.getInstance().showGameOverPane();
        System.out.println("Game over");
    }
    
    public void stopGame() {
        KeyPressHandler.clearPressedKeys();
        player.stopLooping();
        clock.stop();
        timer.stop();
    }
    
    public void resumeGame() {
        player.startLooping();
        clock.restart();
        timer.restart();
    }

    public boolean checkSign(Position pos) {
        for (MapLayer l : layers){
            if (l instanceof TileLayer){
                Tile t = ((TileLayer)l).getTileAt(TilePosition.tileCoordFromMapCoord(pos.x),TilePosition.tileCoordFromMapCoord(pos.y));
                if (Level.hasProperty(t,"Sign")) return true;
            } 
        } 
        return false;
    }
    
    public NPC checkCarry(Position pos) {
        if (actualLevel.getId() != 8) return null; // only level 8 has carry
        for (NPC npc : actualLevel.getNpcs()){
            if (npc.getCarry() && npc.getPosition().distanceFrom(pos) < 25) return npc;
        } 
        return null;
    }
    
    public Position checkCarryOff(Position pos) {
        int x = TilePosition.tileCoordFromMapCoord(pos.x);
        int y = TilePosition.tileCoordFromMapCoord(pos.y + 12);
        for (MapLayer l : layers){
            if (l instanceof TileLayer){
                for (int i = x - 1; i <= x + 1; ++i){
                    for (int j = y - 1; j <= y + 1; ++j) {
                        Tile t = ((TileLayer)l).getTileAt(i,j);
                        if (actualLevel.hasProperty(t,"Carry")) {
                            return TilePosition.tileCenter(new TilePosition(i,j,0)).movePosition(0, -12);
                        } 
                    }
                }
            } 
        } 
        return null;
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
                        writeInfo("You have found <font color=black>" + p.getName() + "</font>");
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
    
    public int getScore() {
        int score = 0;
        for (Level l : levels) {
            if (l.canAdvanceToNextLevel()) {
                int time = l.getTime();
                if (time <= 30) score = 333;
                else score = (int)10000/time;
            }
        }
        for (Pokemon p : pokemons.values()) {
            if (p.isFound()) {
                score += (difficulty == Difficulty.CASUAL) ? 100 : 150;
            }
        }
        return score;
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
    
    public void writeInfo(String str){
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
    public HashMap<TilePosition,Pokemon> getAllPokemonsWithPosition() {return pokemons;}
    public ArrayList<PokeBall> getThrownBalls() {return actualLevel.getThrownBalls();}
    public Player getPlayer() {return player;}
    public Collection<Integer> getAllIds() {return pokemons.values().stream().map(p -> p.getId()).collect(Collectors.toList());}
    public Collection<Integer> getAllFoundIds() {return pokemons.values().stream().filter(p -> p.isFound()).map(p -> p.getId()).collect(Collectors.toList());}
    public ArrayList<Level> getLevels() {return levels;}
    public int getActualLevelId() {return (actualLevel == null) ? 0 :actualLevel.getId();}
    public Level getActualLevel() {return actualLevel;}
    public Difficulty getDifficulty() {return difficulty;}
    
    public String getFileName() {return fileName;}
    public int getDbId() {return dbId;}
    public void setFileName(String fileName) {this.fileName = fileName;}
    public void setDbId(int id) {this.dbId = id;}
    public boolean isSavedToDb() {return (dbId != 0 && fileName == null);}
    
    public enum Difficulty {
        HARDCORE(1, "90s challange"),
        CASUAL(0, "Casual");

        public int id;
        public String name;
        
        private Difficulty(int id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public static Difficulty fromId(int id) {
            for (Difficulty d : Difficulty.values()) if (d.id == id) return d;
            return CASUAL;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
}
