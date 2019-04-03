package pikachusrevenge.gui;

import java.awt.BorderLayout;
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
import pikachusrevenge.model.KeyPressHandler;
import pikachusrevenge.model.Level;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Position;
import pikachusrevenge.resources.Resource;

public final class MainWindow extends JFrame {

    public static final int WINDOW_WIDTH = 448; // 28 tiles
    public static final int WINDOW_HEIGHT = 352; // 22 tiles
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
    
    public void showMainMenu() {
        stopGameFrame();
        add(mainMenuPanel);
        pack();
        centerWindow();
    }
    
    public void startGameFrame() {
        remove(mainMenuPanel);

        footerPanel.setModel(model);

        add(statsPanel, BorderLayout.NORTH);
        add(footerPanel, BorderLayout.SOUTH);

        setJMenuBar(menu);
        addKeyListener(keyAdapter);
    }
    
    public void stopGameFrame() {
        if (model != null) this.model.stopGame();
        if (gamePanel != null) remove(gamePanel);
        remove(footerPanel);
        remove(statsPanel);
        setJMenuBar(null);
        removeKeyListener(keyAdapter);     
    }
    
    public void restartLevel() {
        if (this.model != null) {
            int id = model.getActualLevelId();
            model.rebuildLevel(id);
            loadLevel(id);
        }
    }
    
    public void loadLevelWithNewModel(Model model, int id){
        stopGameFrame();
        this.model = model;
        startGameFrame();
        loadLevel(id);
    }
     
    public void loadLevel(int id) {
        boolean forward = (model.getActualLevelId() <= id);
        Level level = model.buildLevelIfNotExists(id,0);
        
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
        
        model.startGame(level,forward);   
        scrollTo(model.getPlayer().getPosition());  
        pack();
    }
    
    private void loadIcon(String filePath){
        try {
            BufferedImage image = Resource.loadBufferedImage(filePath);
            this.setIconImage(image);
        } catch (IOException ex) {
            System.err.println("Can't load file");
        }     
    }
      
    private void centerWindow() {
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getWidth()) / 2;  
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - this.getHeight()-60) / 2;  
  
        this.setLocation(x, y);  
    }
    
    public void scrollTo(Position position){
        JViewport visible = gamePanel.getViewport();
        int scrollX = scrollPostion(position.x, WINDOW_WIDTH, model.mapRectangle.width);
        int scrollY = scrollPostion(position.y, WINDOW_HEIGHT, model.mapRectangle.height);
        visible.setViewPosition(new Point(scrollX,scrollY));
    }
    
    private int scrollPostion(double coord, int visibleSize, int mapSize){
        if (coord < visibleSize/2) return 0;
        else if (coord > mapSize - visibleSize/2) return mapSize - visibleSize;
        else return (int)coord - visibleSize/2;
    }
    
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

    protected void showExitConfirmation() {
        if (model != null) model.stopGame();
        
        JOptionPane opt = new JOptionPane(new JLabel("Are you sure you want to exit?",JLabel.CENTER),JOptionPane.PLAIN_MESSAGE,JOptionPane.YES_NO_OPTION);
        Dialog dialog = opt.createDialog(this, "Confirmation");
        dialog.setModal(true);
        dialog.setVisible(true);
        
        switch ((Integer) opt.getValue()) {
            case JOptionPane.YES_OPTION: System.exit(0); break;
            default: if (model != null) model.resumeGame(); break;
        }
    }
    
    public void showGameOverPane() {     
        
        Object[] options = {"New Game","Restart Level","Main Menu"};
        JOptionPane opt = new JOptionPane(new JLabel("<html>You have been caught by the vicious trainers!<br>What do you want to do?</html>",JLabel.CENTER),JOptionPane.PLAIN_MESSAGE,0,null,options);
        Dialog dialog = opt.createDialog(this, "Game Over");
        dialog.setModal(true);
        dialog.setVisible(true);
        
        switch ((String)opt.getValue()) {
            case "New Game": loadLevelWithNewModel(new Model(),1); break;
            case "Restart Level": restartLevel(); break;
            default: showMainMenu(); break;
        }
        
    }
    
    public void showBackConfirmation() {    
        model.stopGame();
        
        JOptionPane opt = new JOptionPane(new JLabel("Are you sure you want to go back to Main Menu?",JLabel.CENTER),JOptionPane.PLAIN_MESSAGE,JOptionPane.YES_NO_OPTION);
        Dialog dialog = opt.createDialog(this, "Confirmation");
        dialog.setModal(true);
        dialog.setVisible(true);
        
        switch ((Integer) opt.getValue()) {
            case JOptionPane.YES_OPTION: showMainMenu();; break;
            default: model.resumeGame();; break;
        }
    }
    
    public void showHelp() {
        model.stopGame();
        helpDialog.showDialog();
        model.resumeGame();
    }
    
    public StatsPanel getStats() {return statsPanel;}
    public FooterPanel getFooter() {return footerPanel;}
    public Model getModel() {return model;}
    public MenuBar getGameMenu() {return menu;}
    public Level getActiveLevel() {return model.getActualLevel();}
}
