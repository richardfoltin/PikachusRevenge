package pikachusrevenge;

import java.awt.BorderLayout;
import pikachusrevenge.gui.MapView;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.mapeditor.core.Map;
import org.mapeditor.io.TMXMapReader;
import pikachusrevenge.gui.GameMenu;
import pikachusrevenge.gui.StatsPanel;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.KeyPressHandler;
import pikachusrevenge.model.Model;
import pikachusrevenge.resources.Resource;

public class LevelWindow {
    
    private Map map;
    private final Model model;
    private final JFrame appFrame;
    private final StatsPanel statsPanel;
    
    public static final int GRIDSIZE = 16;
    
    public LevelWindow(int id){
        
        appFrame = new JFrame("Pikachu's Revenge");
        appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        loadIcon("pokemons\\icon025.png");

        loadMap(id);    
        statsPanel = new StatsPanel(map.getWidth() * GRIDSIZE);
        
        model = new Model(map,statsPanel);
        MapView mainPanel = new MapView(map,model);
        mainPanel.setBorder(null);
        
        appFrame.setLayout(new BorderLayout());
        //appFrame.setContentPane(mainPanel);
        appFrame.add(statsPanel, BorderLayout.NORTH);
        appFrame.add(mainPanel, BorderLayout.SOUTH);
        
        appFrame.setJMenuBar(new GameMenu());
        
        appFrame.addKeyListener(getKeyAdapter(model));

        //appFrame.setLocationRelativeTo(null);
        appFrame.pack();
        centerWindow(appFrame);
        appFrame.setVisible(true);
        model.startMoving();
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
    
    private KeyAdapter getKeyAdapter(Model model) {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {              
                //System.out.println(String.format("Key Pressed : %s",new SimpleDateFormat("mm:ss.SSS").format(new Date())));        
                //super.keyPressed(ke); 
                //if (!game.isLevelLoaded()) return;
                
                Direction d = KeyPressHandler.directionFromKeyCode(ke.getKeyCode());
                if (KeyPressHandler.addKeypress(d)) model.playerMoveTowards(KeyPressHandler.getKeyDirection());
            }
            
            @Override
            public void keyReleased(KeyEvent ke) {
                //System.out.println(String.format("Key Released : %s",new SimpleDateFormat("mm:ss.SSS").format(new Date())));
                
                Direction d = KeyPressHandler.directionFromKeyCode(ke.getKeyCode());
                if (KeyPressHandler.removeKeypress(d)) model.playerMoveTowards(KeyPressHandler.getKeyDirection());
            }

        };
    }

 }
