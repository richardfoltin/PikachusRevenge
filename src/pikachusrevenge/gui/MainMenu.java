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
import static pikachusrevenge.gui.MainWindow.WINDOW_HEIGHT;
import static pikachusrevenge.gui.MainWindow.WINDOW_WIDTH;
import pikachusrevenge.model.Model;
import pikachusrevenge.resources.Resource;

public final class MainMenu extends JPanel {

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 35;
    private static final Color BUTTON_FONT_COLOR = new Color(225, 67, 25);
    private static final Color BUTTON_FOCUS_COLOR = new Color(220, 220, 220);
            
    private final MainWindow window;
    private BufferedImage backgroundImage = null;
    
    public MainMenu(MainWindow frame) {
        this.window = frame;
        
        setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT)); 
        setBorder(BorderFactory.createEmptyBorder(160, 260, 0, 0));
        //setLayout(new GridBagLayout());
        
        try {backgroundImage = Resource.loadBufferedImage("main.png");} 
        catch (IOException e) {System.err.println("Can't load file: main.png");} 
         
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        //panel.setBorder(BorderFactory.createLineBorder(Color.red,2));

        JButton startButton = new MenuButton("Start New Game", panel);
        startButton.addActionListener(startAction());
        panel.add(Box.createRigidArea(new Dimension(0,90)));
        startButton.requestFocus();
            
        JButton loadButton = new MenuButton("Load From File...", panel);
        loadButton.addActionListener(loadAction());
        panel.add(Box.createRigidArea(new Dimension(0,8)));
            
        JButton loadDbButton = new MenuButton("Load From Database...", panel);
        loadDbButton.addActionListener(loadDbAction());
        
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.drawImage(backgroundImage, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, this);      
    }
    
    private final class MenuButton extends JButton{
    
        public MenuButton(String title, JPanel panel) {
            super(title);
            setMaximumSize(new Dimension(BUTTON_WIDTH,BUTTON_HEIGHT));
            setAlignmentX(Component.CENTER_ALIGNMENT);  
            setForeground(BUTTON_FONT_COLOR);
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
    
    private final ActionListener startAction() {
        return (ActionEvent e) -> {
            window.loadLevelWithNewModel(new Model(),1);
        };
    }
    
    private final ActionListener loadAction() {
        return (ActionEvent e) -> {
            MenuBar.load();
        };
    }
        
    private final ActionListener loadDbAction() {
        return (ActionEvent e) -> {
            MenuBar.loadFromDb();
        };
    }
}
