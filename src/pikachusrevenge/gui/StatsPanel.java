package pikachusrevenge.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import pikachusrevenge.resources.Resource;

public final class StatsPanel extends JPanel {

    private static final int STATS_HEIGHT = 30;
    public static final SimpleDateFormat TIMEFORMAT = new SimpleDateFormat("mm:ss");
    
    private final JPanel livesPane = new JPanel();
    private final JPanel pokemonPane = new JPanel();
    private final JLabel timerLabel = new JLabel("00:00");
    private final List<JLabel> lives = new ArrayList<>();
    private final List<JLabel> pokemons = new ArrayList<>();
     
    public StatsPanel(int width) {
  
        setPreferredSize(new Dimension(width, STATS_HEIGHT));

        livesPane.setPreferredSize(new Dimension(width/3,STATS_HEIGHT));
        livesPane.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        add(livesPane);
        
        pokemonPane.setPreferredSize(new Dimension(2*width/3-40,STATS_HEIGHT));
        pokemonPane.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        add(pokemonPane);
        
        timerLabel.setPreferredSize(new Dimension(40,30));
        timerLabel.setLayout(new FlowLayout(FlowLayout.RIGHT,0,0));
        add(timerLabel);

        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    }
    
    public void addLife() {
        
        Image image = null;
        try {image = Resource.loadImage("heart.png");} 
        catch (IOException e) {System.err.println("Can't load file");} 
        image = Resource.getScaledImage(image, 15, 15);
        
        JLabel label = new JLabel(new ImageIcon(image),JLabel.CENTER);
        label.setPreferredSize(new Dimension(STATS_HEIGHT,STATS_HEIGHT));    
        lives.add(label);
        livesPane.add(label);
    }
    
    public void removeLife() {
        if (lives.size() > 0) {
            JLabel label = lives.get(lives.size()-1);
            livesPane.remove(label);
            livesPane.revalidate();
            livesPane.repaint();
            lives.remove(label);
        } else {
            System.err.println("Cannot remove life from label!");
        }
    }
    
    public void updateTimeLabel(int time) {
        Date date = new Date(time * 1000);
        timerLabel.setText(TIMEFORMAT.format(date));
    }
    
    public JLabel addBall() {
        JLabel label = getBallLabel();
        pokemons.add(label);
        pokemonPane.add(label);
        
        return label;
    }
    
    public static JLabel getBallLabel() {
        Image image = null;
        try {image = Resource.loadImage("ball.png");} 
        catch (IOException e) {System.err.println("Can't load file");} 
        image = Resource.getScaledImage(image, 15, 15);
        
        JLabel label = new JLabel(new ImageIcon(image),JLabel.CENTER);
        label.setPreferredSize(new Dimension(STATS_HEIGHT,STATS_HEIGHT));  
        return label;
    }
    
    public void clearPane() {
        
        for (JLabel label : pokemons){
            pokemonPane.remove(label);
        }
        pokemons.clear();
        pokemonPane.revalidate();
        pokemonPane.repaint();
            
        for (JLabel label : lives){
            livesPane.remove(label);
        }
        lives.clear();
        livesPane.revalidate();
        livesPane.repaint();
    }
    
}
