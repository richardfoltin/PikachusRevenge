package pikachusrevenge.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.WindowConstants;
import org.mapeditor.core.Map;
import org.mapeditor.io.TMXMapReader;
import pikachusrevenge.PikachusRevenge;
import pikachusrevenge.model.KeyPressHandler;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Position;
import pikachusrevenge.resources.Resource;

public class MainWindow extends JFrame {
    
    private static MainWindow instance = null;
    
    private int level;
    private Map map;
    private Model model;
    private MapView mapView;
    private JScrollPane mainPanel;
    private JPanel startPanel;
    private GameMenu menu;
    private StatsPanel statsPanel;
    private FooterLabel footer;
      
    public static final int WINDOW_WIDTH = 450;
    public static final int WINDOW_HEIGHT = 350;

    public class MapLoadingException extends Exception {}
    
    public static MainWindow getInstance() {
        if (instance == null) return new MainWindow();
        else return instance;
    }
    
    private MainWindow(){
        
        this.instance = this;
        
        setTitle("Pikachu's Revenge");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        loadIcon("pokemons\\icon025.png");
        
        setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
        
        startPanel = new JPanel();
        JButton startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(300,100));
        startButton.addActionListener(new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) { 
                try {
                    MainWindow.this.startGameWindow(1);
                } catch (MainWindow.MapLoadingException ex) {
                    Logger.getLogger(PikachusRevenge.class.getName()).log(Level.SEVERE, null, ex);
                }
            } 
        } );
        
        startPanel.add(startButton);
        add(startPanel);
        
        
        pack();
        centerWindow(this);
        setResizable(false);
        setVisible(true);
    }
    
    public void startGameWindow(int id) throws MapLoadingException {
        this.level = id;
        this.map = loadMap(id);
        this.model = new Model(map,this);
        
        if (startPanel != null) remove(startPanel);

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
        this.menu = new GameMenu(model);
        setJMenuBar(menu);
        menu.setAvailableLevels(model.getPlayer().getAvailableLevels());
        
        // keylistener
        addKeyListener(getKeyAdapter(model));

        //appFrame.setLocationRelativeTo(null);
        addMapToMainPanel();
        pack();
        model.startGame(); 
    }
    
    public void loadNextLevel() {
        loadLevel(level + 1);
    }
    
    public void restartLevel() {
        loadLevel(level);
    }
    
    public void loadLevel(int id) {

        try {
            this.map = loadMap(id);
        } catch (MapLoadingException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        model.changeMap(map);
        this.level = id;
        
        remove(mainPanel);
        addMapToMainPanel();
        
        model.getPlayer().increaseAvailableLevels(id);
        menu.setAvailableLevels(model.getPlayer().getAvailableLevels());
        
        pack();
        model.setTime(0);
        model.startGame();     
    }
    
    private void addMapToMainPanel(){
        mapView = new MapView(map,model);
        mainPanel = new JScrollPane(mapView);
        mainPanel.setBorder(null);
        setPreferredSize(null);
        mainPanel.setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
        mainPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);   
        
        add(mainPanel, BorderLayout.CENTER);
        scrollTo(model.getPlayer().getPosition());
        this.requestFocus();
    }
    
    private Map loadMap(int id) throws MapLoadingException {  
        Map map = null;
        try {
            TMXMapReader mapReader = new TMXMapReader();
            map = mapReader.readMap(fileFromId(id));
        } catch (Exception e) {
            System.out.println("Error while reading the map:\n" + e.getMessage());
            throw new MapLoadingException();
        }
        System.out.println(map.toString() + " loaded");
        return map;
    }
    
    private void loadIcon(String filePath){
        try {
            BufferedImage image = Resource.loadBufferedImage(filePath);
            image = Resource.getSprite(image, 0, 0);
            setIconImage(image);
        } catch (IOException ex) {
            System.err.println("Can't load file");
        }     
    }
    
    private String fileFromId(int id){    
        String mapName = "";
        
        switch (id) {
            case 1 : mapName = "MapTest"; break;
            case 2 : mapName = "Level1"; break;
        }
        
        return "src\\pikachusrevenge\\resources\\level\\" + mapName + ".tmx";
    }
    
    protected void centerWindow(JFrame window) {
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - window.getWidth()) / 2;  
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - window.getHeight()-100) / 2;  
  
        window.setLocation(x, y);  
    }
    
    public void scrollTo(Position position){
        JViewport visible = mainPanel.getViewport();
        int scrollX = scrollPostion(position.x, WINDOW_WIDTH, model.MAP_RECTANGLE.width);
        int scrollY = scrollPostion(position.y, WINDOW_HEIGHT, model.MAP_RECTANGLE.height);
        visible.setViewPosition(new Point(scrollX,scrollY));
    }
    
    private int scrollPostion(double coord, int visibleSize, int mapSize){
        if (coord < visibleSize/2) return 0;
        else if (coord > mapSize - visibleSize/2) return mapSize - visibleSize;
        else return (int)coord - visibleSize/2;
    }
    
    private KeyAdapter getKeyAdapter(Model model) {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                KeyPressHandler.keyPressed(model, ke.getKeyCode());
            }
            
            @Override
            public void keyReleased(KeyEvent ke) {
                KeyPressHandler.keyReleased(model, ke.getKeyCode());
            }

        };
    }

    public void repaintMap() {
        mapView.repaint();        
    }

    public StatsPanel getStats() {return statsPanel;}
    public Map getMap() {return map;}
    public FooterLabel getFooter() {return footer;}
    public int getLevel() {return level;}

}
