package pikachusrevenge.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import pikachusrevenge.resources.Resource;

public class StatsPanel extends JPanel {

    private final JPanel livesPane;
    private final JPanel pokemonPane;
    private final JLabel timerLabel;
    private final List<JLabel> lives;
    private final List<JLabel> pokemons;
    
    public StatsPanel(int width) {
  
        setPreferredSize(new Dimension(width ,30));
        
        lives = new ArrayList<>();
        livesPane = new JPanel();
        livesPane.setPreferredSize(new Dimension(width/3,30));
        livesPane.setLayout(new FlowLayout(FlowLayout.LEFT));
        //livesPane.setAlignmentY(JLabel.CENTER_ALIGNMENT);
        //livesPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(livesPane);
        
        pokemons = new ArrayList<>();
        pokemonPane = new JPanel();
        pokemonPane.setPreferredSize(new Dimension(width/3,30));
        pokemonPane.setLayout(new FlowLayout(FlowLayout.LEFT));
        add(pokemonPane);
        
        timerLabel = new JLabel("Timer:");
        timerLabel.setPreferredSize(new Dimension(width/3,30));
        add(timerLabel);
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    }
    
    public void addLife() {
        
        Image image = null;
        try {image = Resource.loadImage("item045.png");} 
        catch (IOException e) {System.err.println("Can't load file");} 
        image = Resource.getScaledImage(image, 26, 26);
        
        JLabel label = new JLabel(new ImageIcon(image),JLabel.CENTER);
        label.setAlignmentY(JLabel.CENTER_ALIGNMENT);
        label.setPreferredSize(new Dimension(26,26));    
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
    
    public JLabel addBall() {
        
        Image image = null;
        try {image = Resource.loadImage("object_ball.png");} 
        catch (IOException e) {System.err.println("Can't load file");} 
        image = Resource.getScaledImage(image, 20, 20);
        
        JLabel label = new JLabel(new ImageIcon(image),JLabel.CENTER);
        label.setAlignmentY(JLabel.CENTER_ALIGNMENT);
        label.setPreferredSize(new Dimension(26,26));    
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
