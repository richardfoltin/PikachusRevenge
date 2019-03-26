package pikachusrevenge.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import pikachusrevenge.model.KeyPressHandler;
import pikachusrevenge.model.Level;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Position;
import pikachusrevenge.resources.Resource;

public class MainWindow extends JFrame {
    
    private static MainWindow instance = null;
    
    private Model model;
    private MapView mapView;
    private JScrollPane mainPanel;
    private JPanel startPanel;
    private GameMenu menu;
    private StatsPanel statsPanel;
    private FooterLabel footer;
    private KeyAdapter keyAdapter;
      
    public static final int WINDOW_WIDTH = 448; // 28 tiles
    public static final int WINDOW_HEIGHT = 352; // 22 tiles

    public class MapLoadingException extends Exception {}
    
    public static MainWindow getInstance() {
        if (instance == null) return new MainWindow();
        else return instance;
    }
    
    private MainWindow(){
        
        this.instance = this;
        this.model = new Model();
        
        setTitle("Pikachu's Revenge");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        loadIcon("pikachu_small.png");
        
        setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                showExitConfirmation();
            }

        });
        
        startPanel = new JPanel();
        JButton startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(300,100));
        startButton.addActionListener(new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) { 
                MainWindow.this.startGameFrame();
                MainWindow.this.loadLevel(1);
            } 
        } );
        
        startPanel.add(startButton);
        add(startPanel);
        
        
        pack();
        centerWindow(this);
        setResizable(false);
        setVisible(true);
    }
    
    public void startGameFrame() {
        
        if (startPanel != null) {
            remove(startPanel);

            // stats
            statsPanel = new StatsPanel(WINDOW_WIDTH);

            // footer
            JPanel footerPanel = new JPanel();
            footerPanel.setPreferredSize(new Dimension(WINDOW_WIDTH,25));
            footer = new FooterLabel(model);
            footerPanel.add(footer);

            // layout
            setLayout(new BorderLayout());
            add(statsPanel, BorderLayout.NORTH);
            add(footerPanel, BorderLayout.SOUTH);

            // menu
            this.menu = new GameMenu();
            setJMenuBar(menu);

            // keylistener
            this.keyAdapter = getKeyAdapter();
            addKeyListener(keyAdapter);
        }
    }
    
    public void loadLevelWithNewModel(Model model, int id){
        if (this.model != null) this.model.stopGame();
        if (this.keyAdapter != null) removeKeyListener(keyAdapter);
        
        this.model = model;
        startGameFrame();
        footer.setModel(model);
        loadLevel(id);
    }
     
    public void loadLevel(int id) {
        boolean forward = (model.getActualLevelId() <= id);
        Level level = model.buildLevelIfNotExists(id,0);
        
        if (mainPanel != null) remove(mainPanel);
        mapView = new MapView(level.getMap(),model);
        mainPanel = new JScrollPane(mapView);
        mainPanel.setBorder(null);
        setPreferredSize(null);
        mainPanel.setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
        mainPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);   
        
        add(mainPanel, BorderLayout.CENTER);
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
            setIconImage(image);
        } catch (IOException ex) {
            System.err.println("Can't load file");
        }     
    }
      
    private void centerWindow(JFrame window) {
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - window.getWidth()) / 2;  
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - window.getHeight()-100) / 2;  
  
        window.setLocation(x, y);  
    }
    
    public void scrollTo(Position position){
        JViewport visible = mainPanel.getViewport();
        int scrollX = scrollPostion(position.x, WINDOW_WIDTH, model.mapRectangle.width);
        int scrollY = scrollPostion(position.y, WINDOW_HEIGHT, model.mapRectangle.height);
        visible.setViewPosition(new Point(scrollX,scrollY));
    }
    
    private int scrollPostion(double coord, int visibleSize, int mapSize){
        if (coord < visibleSize/2) return 0;
        else if (coord > mapSize - visibleSize/2) return mapSize - visibleSize;
        else return (int)coord - visibleSize/2;
    }
    
    private KeyAdapter getKeyAdapter() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                KeyPressHandler.keyPressed(MainWindow.this.model, ke.getKeyCode());
            }
            
            @Override
            public void keyReleased(KeyEvent ke) {
                KeyPressHandler.keyReleased(MainWindow.this.model, ke.getKeyCode());
            }

        };
    }

    public void repaintMap() {
        mapView.repaint();        
    }

    protected void showExitConfirmation() {
        int n = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (n == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    public StatsPanel getStats() {return statsPanel;}
    public FooterLabel getFooter() {return footer;}
    public Model getModel() {return model;}
    public GameMenu getGameMenu() {return menu;}
    public Level getActiveLevel() {return model.getActualLevel();}
}
