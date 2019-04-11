package pikachusrevenge.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import pikachusrevenge.gui.dialog.HighscoreDialog;
import pikachusrevenge.gui.dialog.HelpDialog;
import pikachusrevenge.gui.dialog.LoadDialog;
import pikachusrevenge.gui.dialog.StatisticsDialog;
import pikachusrevenge.model.Database;
import pikachusrevenge.model.KeyPressHandler;
import pikachusrevenge.model.Level;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Model.Difficulty;
import pikachusrevenge.model.Position;
import pikachusrevenge.resources.Resource;
import pikachusrevenge.unit.Player;

/**
 * A játék ablakait kezelő Singleton.
 * @author Csaba Foltin
 */
public final class MainWindow extends JFrame {

    public static final int WINDOW_WIDTH = 448; // 28 tiles
    public static final int WINDOW_HEIGHT = 352; // 22 tiles
    public static final Color PIKACHU_RED = new Color(225, 67, 25);
    public static final boolean TESTING = false;
    
    private static MainWindow instance = null;
    private final HelpDialog helpDialog = new HelpDialog(this);
    private final MenuBar menu = new MenuBar(this);
    private final StatsPanel statsPanel = new StatsPanel(WINDOW_WIDTH);
    private final FooterPanel footerPanel = new FooterPanel();
    private final MainMenu mainMenuPanel = new MainMenu(this);
    
    private Model model;
    private MapView mapView;
    private JScrollPane gamePanel;

    public class MapLoadingException extends Exception {}
    
    public static MainWindow getInstance() {
        if (instance == null) instance = new MainWindow();
        return instance;
    }
    
    private MainWindow(){
        super("Pikachu's Revenge");
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        loadIcon("pikachu_small.png");
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                showExitConfirmation();
            }
        });
        
        setLayout(new BorderLayout());
        
        setResizable(false);
        setVisible(true);
    }
    
    /**
     * Leállítja a játékot és megjeleníti a játék főmenüjét
     */
    public void showMainMenu() {
        stopGameFrame();
        add(mainMenuPanel);
        pack();
        centerWindow();
    }
    
    /**
     * Elrejti a játék főmenüjét, ha éppen aktív és felépíti a játékot futtató 
     * panelt.
     */
    public void startGameFrame() {
        remove(mainMenuPanel);

        footerPanel.setModel(model);

        add(statsPanel, BorderLayout.NORTH);
        add(footerPanel, BorderLayout.SOUTH);

        setJMenuBar(menu);
        addKeyListener(keyAdapter);
    }
    
    /**
     * Leállítja a játékot, és elrejti a játékot futtató panelt.
     */
    public void stopGameFrame() {
        if (model != null) this.model.stopGame();
        if (gamePanel != null) remove(gamePanel);
        remove(footerPanel);
        remove(statsPanel);
        setJMenuBar(null);
        removeKeyListener(keyAdapter);     
    }
    
    /**
     * Újraindítja az éppen aktulális pályát
     */
    public void restartLevel() {
        if (this.model != null) {
            int id = model.getActualLevelId();
            model.rebuildLevel(id);
            loadLevel(id, true);
        }
    }
    
    /**
     * Leállítja az aktuális játékot, és betölt egy pályát a paraméterül megadott
     * modellel.
     * @param model az új játék modelje
     * @param fromStart true, ha a pálya a kezdő (vagy vég) pozícióból induljon
     */
    public void loadActualLevelWithNewModel(Model model, boolean fromStart){
        stopGameFrame();
        this.model = model;
        startGameFrame();
        loadLevel(model.getActualLevelId(), fromStart);
    }
      
    /**
     * Leállítja az aktuális játékot, és betölti az első pályát a paraméterül megadott
     * modellel.
     * @param model az új játék modelje
     */
    public void loadFirstLevelWithNewModel(Model model){
        stopGameFrame();
        this.model = model;
        startGameFrame();
        loadLevel(1, true);
    }
    
    /**
     * Betölt egy új pályát a kész játékpanelbe.
     * @param id az új pálya száma
     * @param fromStart true, ha a pálya a kezdő (vagy vég) pozícióból induljon
     */
    public void loadLevel(int id, boolean fromStart) {
        boolean forward = (model.getActualLevelId() <= id);
        Level level = model.buildLevelIfNotExists(id,0); 
        model.getPlayer().setStartingPostion(level.getPlayerStartingPosition(forward));
        
        if (gamePanel != null) remove(gamePanel);
        mapView = new MapView(level.getMap(),model);
        gamePanel = new JScrollPane(mapView);
        gamePanel.setBorder(null);
        setPreferredSize(null);
        gamePanel.setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
        gamePanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        gamePanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);   
        
        add(gamePanel, BorderLayout.CENTER);
        this.requestFocus();
        
        model.getPlayer().increaseAvailableLevels(id);
        menu.setAvailableLevels(model.getPlayer().getAvailableLevels());
        
        model.startGame(level,fromStart);   
        scrollTo(model.getPlayer().getPosition());  
        pack();
    }
    
    /**
     * Betölt egy ikont a megadott elérési útról
     * @param filePath az ikon elérési útja
     */
    private void loadIcon(String filePath){
        try {
            BufferedImage image = Resource.loadBufferedImage(filePath);
            this.setIconImage(image);
        } catch (IOException ex) {
            System.err.println("Can't load file");
        }     
    }
    
    /**
     * Középre helyezi az ablakot
     */
    private void centerWindow() {
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getWidth()) / 2;  
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - this.getHeight()-60) / 2;  
  
        this.setLocation(x, y);  
    }
    
    /**
     * A pályát tartalmazó JScrollPane-t a megadott pozícióra állítja.
     * @param position a beállítandó pozíciót
     */
    public void scrollTo(Position position){
        JViewport visible = gamePanel.getViewport();
        int scrollX = scrollPostion(position.x, WINDOW_WIDTH, model.mapRectangle.width);
        int scrollY = scrollPostion(position.y, WINDOW_HEIGHT, model.mapRectangle.height);
        visible.setViewPosition(new Point(scrollX,scrollY));
    }
    
    /**
     * Segítő metódus a {@link #scrollTo(Position position) scrollTo} metódushoz.
     * A játéktér és a pálya nagyságából kiszámolja hogy ténylegesen milyen pozícióba
     * kell állítani a panelt úgy, hogy ne legyen a pálya mellett üres hely a
     * panelen.
     * @param coord a kért beállítandó koordináta
     * @param visibleSize a játéktér maximum nagysága
     * @param mapSize a pálya maximum nagysága
     * @return a ténylegesen beállítandó koordináta
     */
    private int scrollPostion(double coord, int visibleSize, int mapSize){
        if (coord < visibleSize/2) return 0;
        else if (coord > mapSize - visibleSize/2) return mapSize - visibleSize;
        else return (int)coord - visibleSize/2;
    }
    
    /**
     * KeyAdapter ami figyeli a lenyomott és felengedett billentyűket és továbbítja
     * a {@link KeyPressHandler} osztálynak
     */
    private final KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent ke) {
            KeyPressHandler.keyPressed(MainWindow.this.model, ke.getKeyCode());
        }
        
        @Override
        public void keyReleased(KeyEvent ke) {
            KeyPressHandler.keyReleased(MainWindow.this.model, ke.getKeyCode());
        }
        
    };

    public void repaintMap() {
        mapView.repaint();        
    }
    
    /**
     * Feldob egy InputDialog-ot ahol be lehet írni a játékos nevét.
     * A dialog automatikusan ki lesz töltve aktuálisan futtatot játékos nevével,
     * ha már egyszer megadta.
     * @return a megadott név
     */
    public String getSaveName() {
        String playerName = model.getPlayer().getName();
        String name = (String)JOptionPane.showInputDialog(this, "What is your name?", "Saving..",JOptionPane.QUESTION_MESSAGE,null,null,playerName);
        if (name == null) return null;
        if (name.equals("")) name = Player.DEFAULTNAME;
        return name;
    }

    /**
     * Megjelníti a kilépést megerősítő dialogot.
     */
    protected void showExitConfirmation() {
        if (model != null) model.stopGame();
        
        JOptionPane opt = new JOptionPane(new JLabel("Are you sure you want to exit?",JLabel.CENTER),JOptionPane.PLAIN_MESSAGE,JOptionPane.YES_NO_OPTION);
        Dialog dialog = opt.createDialog(this, "Confirmation");
        dialog.setModal(true);
        dialog.setVisible(true);
        
        int value = (opt.getValue() == null) ? JOptionPane.CANCEL_OPTION : (Integer)opt.getValue();
        switch (value) {
            case JOptionPane.YES_OPTION: System.exit(0); break;
            default: if (model != null) model.resumeGame(); break;
        }
    }
    
    /**
     * Megjelenít egy adatbázishoz tartaozó hibaüzenetet.
     * @param error a hibaüzenet szövege
     */
    public void showDbError(String error) {
        JOptionPane.showMessageDialog(this,error,"Database Error",JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Megjelenít egy általános hibaüzenetet
     * @param error a hibaüzenet szövege
     */
    public void showError(String error) {
        JOptionPane.showMessageDialog(this,error,"Error",JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Megjelenít egy sikert visszajelző üzenetet
     * @param msg az üzenet szövege
     */
    public void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this,msg,"Success",JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Megjeleníti a játék végét jelző dialogot.
     * Három lehetséges opció van: Új játék kezdése, Az aktuáls pálya újrakezdése,
     * Visszalépés a főmenübe.
     */
    public void showGameOverPane() {     
        
        Object[] options = {"New Game","Restart Level","Main Menu"};
        JOptionPane opt = new JOptionPane(new JLabel("<html>You have been caught by the vicious trainers!<br>What do you want to do?</html>",JLabel.CENTER),JOptionPane.PLAIN_MESSAGE,0,null,options);
        Dialog dialog = opt.createDialog(this, "Game Over");
        dialog.setModal(true);
        dialog.setVisible(true);
        Difficulty difficulty = model.getDifficulty();
        
        String value = (opt.getValue() == null) ? "" : (String)opt.getValue();
        switch (value) {
            case "New Game": loadFirstLevelWithNewModel(new Model(difficulty)); break;
            case "Restart Level": restartLevel(); break;
            default: showMainMenu(); break;
        }
    }
    
    /**
     * Megjeleníti a nehézséget kiválasztó dialogot
     * @return a kiválasztott nehézségi fok
     */
    public Difficulty showDifficultySelector() {     
        
        Object[] options = {Difficulty.HARDCORE,Difficulty.CASUAL};
        JOptionPane opt = new JOptionPane(new JLabel("<html>Please select difficulty!</html>",JLabel.CENTER),JOptionPane.PLAIN_MESSAGE,0,null,options);
        Dialog dialog = opt.createDialog(this, "Start Game");
        dialog.setModal(true);
        dialog.setVisible(true);
        
        return (Difficulty)opt.getValue();
    }
    
    /**
     * Megjeleníti a főmenübe való visszalépést megerősítő dialogot.
     */
    public void showBackToMainMenuConfirmation() {    
        model.stopGame();
        
        JOptionPane opt = new JOptionPane(new JLabel("<html>Are you sure you want to go back to Main Menu?</html>",JLabel.CENTER),JOptionPane.PLAIN_MESSAGE,JOptionPane.YES_NO_OPTION);
        Dialog dialog = opt.createDialog(this, "Confirmation");
        dialog.setModal(true);
        dialog.setVisible(true);
        
        int value = (opt.getValue() == null) ? JOptionPane.CANCEL_OPTION : (Integer)opt.getValue();
        switch (value) {
            case JOptionPane.YES_OPTION: showMainMenu();; break;
            default: model.resumeGame();; break;
        }
    }
    
    /**
     * Megjeleníti a segítség dialogot.
     */
    public void showHelp() {
        helpDialog.showDialog();
    }
    
    /**
     * Megjeleníti a dicsőségtáblát.
     */
    public void showHighscores() {
        HighscoreDialog dialog = new HighscoreDialog(this);
        String msg = dialog.getLoadMessage();
        if (msg != null) showDbError(msg);
        else dialog.showDialog();
    }
    
    /**
     * Megjeleníti az aktuális játék statisztikáit tartalmazó dialogot.
     */
    public void showStatistics() {
        if (model != null) {
            String name = "Game Statistics - " + model.getPlayer().getName() + " [" + model.getDifficulty() + "] - Score: " + model.getScore();
            StatisticsDialog dialog = new StatisticsDialog(this, name);
            String msg = dialog.getLoadMessage();
            if (msg != null) showDbError(msg);
            else dialog.showDialog();
        }
    }
        
    /**
     * Megjeleníti az adatbázisból választható elmentett állásokat tartalmazó
     * dialogot.
     * @return true ha sikerült a betöltés
     */
    public boolean showLoadSelection() {
        LoadDialog dialog = new LoadDialog(this);
        String msg = dialog.getLoadMessage();
        if (msg != null) {
            showDbError(msg);
            return false;
        } else {
            dialog.showDialog();
            return Database.load(dialog.getSelectedId(), dialog.getSelectedDifficulty());
        }
    }
    
    public StatsPanel getStats() {return statsPanel;}
    public FooterPanel getFooter() {return footerPanel;}
    public Model getModel() {return model;}
    public MenuBar getGameMenu() {return menu;}
    public Level getActiveLevel() {return model.getActualLevel();}
}
