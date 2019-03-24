package pikachusrevenge.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import pikachusrevenge.resources.Resource;

public class StatsPanel extends JPanel {

    private final JPanel livesPane;
    private final JPanel pokemonPane;
    private final JLabel timerLabel;
    private final List<JLabel> lives;
    private final List<JLabel> pokemons;
    private final SimpleDateFormat timeFormat;
    
    private static final int STATS_HEIGHT = 30;
    
    public StatsPanel(int width) {
  
        setPreferredSize(new Dimension(width, STATS_HEIGHT));
        
        this.lives = new ArrayList<>();
        this.livesPane = new JPanel();
        this.timeFormat = new SimpleDateFormat("mm:ss");
        
        livesPane.setPreferredSize(new Dimension(width/3,STATS_HEIGHT));
        livesPane.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        add(livesPane);
        
        this.pokemons = new ArrayList<>();
        this.pokemonPane = new JPanel();
        pokemonPane.setPreferredSize(new Dimension(2*width/3-40,STATS_HEIGHT));
        pokemonPane.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        add(pokemonPane);
        
        this.timerLabel = new JLabel("00:00");
        timerLabel.setPreferredSize(new Dimension(40,30));
        timerLabel.setLayout(new FlowLayout(FlowLayout.RIGHT,0,0));
        add(timerLabel);

        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    }
    
    public void addLife() {
        
        Image image = null;
        try {image = Resource.loadImage("item045.png");} 
        catch (IOException e) {System.err.println("Can't load file");} 
        image = Resource.getScaledImage(image, 26, 26);
        
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
    
    public void pokemonFound(int id) {
        
    }
    
    public void updateTimeLabel(int time) {
        Date date = new Date(time * 1000);
        timerLabel.setText(timeFormat.format(date));
    }
    
    public JLabel addBall() {
        
        Image image = null;
        try {image = Resource.loadImage("object_ball.png");} 
        catch (IOException e) {System.err.println("Can't load file");} 
        image = Resource.getScaledImage(image, 20, 20);
        
        JLabel label = new JLabel(new ImageIcon(image),JLabel.CENTER);
        label.setPreferredSize(new Dimension(STATS_HEIGHT,STATS_HEIGHT));    
        pokemons.add(label);
        pokemonPane.add(label);
        
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
    
    public void pokemonFound() {
        
    }
    
}
