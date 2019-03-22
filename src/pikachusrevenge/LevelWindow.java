package pikachusrevenge;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import pikachusrevenge.gui.MapView;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.WindowConstants;
import org.mapeditor.core.Map;
import org.mapeditor.io.TMXMapReader;
import pikachusrevenge.gui.FooterLabel;
import pikachusrevenge.gui.GameMenu;
import pikachusrevenge.gui.StatsPanel;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.KeyPressHandler;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Position;
import pikachusrevenge.resources.Resource;

public class LevelWindow {
    
    private Map map;
    private final Model model;
    private final JFrame appFrame;
    private final JScrollPane mainPanel;
    private final StatsPanel statsPanel;
    private final FooterLabel footer;
    private final MapView mapView;
      
    public static final int GRIDSIZE = 16;
    public static final int WINDOW_WIDTH = 450;
    public static final int WINDOW_HEIGHT = 350;
    
    
    public LevelWindow(int id){
        
        appFrame = new JFrame("Pikachu's Revenge");
        appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        loadIcon("pokemons\\icon025.png");

        loadMap(id);    
        statsPanel = new StatsPanel(WINDOW_WIDTH);
        
        model = new Model(map,statsPanel,this);
        mapView = new MapView(map,model);
        mainPanel = new JScrollPane(mapView);
        mainPanel.setBorder(null);
        mainPanel.setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
        mainPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        
        JPanel footerPanel = new JPanel();
        footerPanel.setPreferredSize(new Dimension(WINDOW_WIDTH,25));
        footer = new FooterLabel(model);
        footerPanel.add(footer);
        
        appFrame.setLayout(new BorderLayout());
        //appFrame.setContentPane(mainPanel);
        appFrame.add(statsPanel, BorderLayout.NORTH);
        appFrame.add(mainPanel, BorderLayout.CENTER);
        appFrame.add(footerPanel, BorderLayout.SOUTH);
        
        appFrame.setJMenuBar(new GameMenu());
        
        appFrame.addKeyListener(getKeyAdapter(model));

        //appFrame.setLocationRelativeTo(null);
        appFrame.pack();
        centerWindow(appFrame);
        scrollTo(model.getPlayer().getPosition());
        appFrame.setVisible(true);
        model.startGame();
    }
    
    private void loadMap(int id) {              
        try {
            TMXMapReader mapReader = new TMXMapReader();
            map = mapReader.readMap(fileFromId(id));
        } catch (Exception e) {
            System.out.println("Error while reading the map:\n" + e.getMessage());
            return;
        }
        System.out.println(map.toString() + " loaded");
    }
    
    private void loadIcon(String filePath){
        try {
            BufferedImage image = Resource.loadBufferedImage(filePath);
            image = Resource.getSprite(image, 0, 0);
            appFrame.setIconImage(image);
        } catch (IOException ex) {
            System.err.println("Can't load file");
        }     
    }
    
    private String fileFromId(int id){    
        String mapName = "";
        
        switch (id) {
            case 1 : mapName = "MapTest"; break;
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


    public MapView getMapView() {return mapView;}
    public FooterLabel getFooter() {return footer;}

 }
