package pikachusrevenge.gui;

import java.awt.Color;
import javax.swing.JLabel;
import pikachusrevenge.model.Model;

public class FooterLabel extends JLabel {

    private Model model;
    private int infoCounter;
    private boolean info;
    private final int INFO_MAX = 60;
    
    public FooterLabel(Model model) {
        this.model = model;
        this.info = false;
        
        setForeground(Color.GRAY);
    }

    public void setHelpText() {
        if (model.getActualLevelId() == 10) {
            setText("Interact with the fountain to finish the game!");
        } else {
            if (model.canMoveToNextLevel()) setText("Interact with the sign to move to the next level!");
            else setText("Find pokeballs to free the pokémon!");
        }
    }
    
    public void loop() {
        if (!info) {
            if (model.getPlayer().isAtSign()) {
                if (model.getActualLevelId() == 10) {
                    setText("Press SPACE to finish the game!");
                } else {
                    if (model.canMoveToNextLevel()) setText("Press SPACE to move to the next level!");
                    else setText("You have to find at least " + MainWindow.getInstance().getActiveLevel().minimumFoundPokemon() + " pokémon to move to the next level!");
                }
            } else setHelpText();
        } else {
            infoCounter++;
            if (infoCounter >= INFO_MAX) {
                info = false;
            }
        }
    }
    
    public void write(String str) {
        infoCounter = 0;
        info = true;
        setText(str);
    }
    
    public void setModel(Model model) {this.model = model;}
}
