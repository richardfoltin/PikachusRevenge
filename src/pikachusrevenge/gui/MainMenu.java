package pikachusrevenge.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import static pikachusrevenge.gui.MainWindow.PIKACHU_RED;
import static pikachusrevenge.gui.MainWindow.WINDOW_HEIGHT;
import static pikachusrevenge.gui.MainWindow.WINDOW_WIDTH;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Model.Difficulty;
import pikachusrevenge.resources.Resource;

/**
 * A játék főmenüjét tartalmazó panel.
 * @author Csaba Foltin
 */
public final class MainMenu extends JPanel {

    private static final int BUTTON_WIDTH = 160;
    private static final int BUTTON_HEIGHT = 20;
    private static final Color BUTTON_FOCUS_COLOR = new Color(220, 220, 220);
            
    private final MainWindow window;
    private BufferedImage backgroundImage = null;
    
    public MainMenu(MainWindow frame) {
        this.window = frame;
        
        setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT)); 
        setBorder(BorderFactory.createEmptyBorder(150, 250, 0, 0));
        
        try {backgroundImage = Resource.loadBufferedImage("main.png");} 
        catch (IOException e) {System.err.println("Can't load file: main.png");} 
         
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JButton startButton = new MenuButton("Start New Game", panel, true);
        startButton.addActionListener(startAction());
        panel.add(Box.createRigidArea(new Dimension(0,80)));
        //startButton.requestFocus();
            
        JButton loadButton = new MenuButton("Load From File...", panel, false);
        loadButton.addActionListener(loadAction());
        panel.add(Box.createRigidArea(new Dimension(0,5)));
            
        JButton loadDbButton = new MenuButton("Load From Database...", panel, false);
        loadDbButton.addActionListener(loadDbAction());
        panel.add(Box.createRigidArea(new Dimension(0,5)));
            
        JButton highscoreButton = new MenuButton("Highscores", panel, false);
        highscoreButton.addActionListener(highscoreAction());
        
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.drawImage(backgroundImage, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, this);      
    }
    
    private final class MenuButton extends JButton{
    
        public MenuButton(String title, JPanel panel, boolean big) {
            super(title);
            Dimension size = (big) ? new Dimension(BUTTON_WIDTH + 20,BUTTON_HEIGHT + 20) : new Dimension(BUTTON_WIDTH,BUTTON_HEIGHT);
            setPreferredSize(size);
            setMaximumSize(size);
            setAlignmentX(Component.CENTER_ALIGNMENT);  
            setForeground(PIKACHU_RED);
            setBackground(Color.WHITE);
            setFocusPainted(false);
            //setOpaque(false);
            //setContentAreaFilled(false);
            //setBorderPainted(false);
            addFocusListener(focus());
            
            Font font = getFont();
            setFont(font);
            
            panel.add(this);
        }
        
        /**
         * A gomb fokószba kerülése esetén megváltoztatja a gomb hátterét
         * @return FocusListener
         */
        private final FocusListener focus() {
            return new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    MenuButton.this.setBackground(BUTTON_FOCUS_COLOR);
                }
                
                @Override
                public void focusLost(FocusEvent e) {
                    MenuButton.this.setBackground(Color.WHITE);
                }
            };
        }
        
    }
    
    /**
     * A játékot indító action
     * @return ActionListener
     */
    private ActionListener startAction() {
        return (ActionEvent e) -> {
            Difficulty d = window.showDifficultySelector();
            if (d != null) window.loadLevelWithNewModel(new Model(d),1);
        };
    }
    
    /**
     * A dicsőségtáblát megjelnítő action
     * @return ActionListener
     */
    private ActionListener highscoreAction() {
        return (ActionEvent e) -> {
            window.showHighscores();
        };
    }
    
    /**
     * A játékot file-ból betöltő panelt megjelenítő action
     * @return ActionListener
     */
    private ActionListener loadAction() {
        return (ActionEvent e) -> {
            MenuBar.load();
        };
    }
    
    /**
     * A játékot adatbázisból betöltő panelt megjelenítő action
     * @return ActionListener
     */
    private ActionListener loadDbAction() {
        return (ActionEvent e) -> {
            window.showLoadSelection();
        };
    }
}
