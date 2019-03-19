package pikachusrevenge;

import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import org.mapeditor.core.Map;
import org.mapeditor.io.TMXMapReader;
import pikachusrevenge.model.Direction;
import pikachusrevenge.model.Model;

public class LevelWindow {
    
    private Map map;
    private Model model;
    public static final int GRIDSIZE = 16;
    
    public LevelWindow(int id){
        
        try {
            TMXMapReader mapReader = new TMXMapReader();
            map = mapReader.readMap(fileFromId(id));
        } catch (Exception e) {
            System.out.println("Error while reading the map:\n" + e.getMessage());
            return;
        }

        System.out.println(map.toString() + " loaded");
        
        Model model = new Model(map);
        MapView mainPanel = new MapView(map,model);
        mainPanel.setBorder(null);

        JFrame appFrame = new JFrame("Pikachu's Revenge");
        appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        appFrame.setContentPane(mainPanel);
        appFrame.pack();
        centerWindow(appFrame);
        appFrame.setVisible(true);
        
        appFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                
                //System.out.println(String.format("Key Pressed : %s",new SimpleDateFormat("mm:ss.SSS").format(new Date())));
                
                //super.keyPressed(ke); 
                //if (!game.isLevelLoaded()) return;
                //refreshGameStatLabel();
                
                Direction d = fromKey(ke.getKeyCode());
                if (Direction.addKeypress(d)) model.playerMoveTowards(Direction.getKeyDirection());
            }
            
            @Override
            public void keyReleased(KeyEvent ke) {
                //System.out.println(String.format("Key Released : %s",new SimpleDateFormat("mm:ss.SSS").format(new Date())));
                
                Direction d = fromKey(ke.getKeyCode());
                if (Direction.removeKeypress(d)) model.playerMoveTowards(Direction.getKeyDirection());
            }
            
            public Direction fromKey(int keyCode){
                switch (keyCode){
                    case KeyEvent.VK_LEFT :
                    case KeyEvent.VK_A :  return Direction.LEFT; 
                    case KeyEvent.VK_RIGHT: 
                    case KeyEvent.VK_D :  return Direction.RIGHT;
                    case KeyEvent.VK_UP:    
                    case KeyEvent.VK_W :  return Direction.UP;
                    case KeyEvent.VK_DOWN:  
                    case KeyEvent.VK_S :  return Direction.DOWN;
                    default : return Direction.STOP;
                    //case KeyEvent.VK_ESCAPE: game.loadGame(game.getGameID());
                }
            }
        });
        
        model.startNpcs();
    }
    
    private String fileFromId(int id){
        switch (id) {
            case 1 : return "MapTest.tmx";
            default: return "";
        }
    }
    
    protected void centerWindow(JFrame window) {
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - window.getWidth()) / 2;  
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - window.getHeight()-200) / 2;  
  
        window.setLocation(x, y);  
    }

 }
