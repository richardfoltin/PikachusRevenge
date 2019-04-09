package pikachusrevenge.gui;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static pikachusrevenge.gui.MainWindow.WINDOW_WIDTH;
import pikachusrevenge.model.Model;

/**
 * A játékablak alján lévő panel, ahol a játékosnak szóló információkat lehet
 * megjeleníteni
 * @author Csaba Foltin
 */
public final class FooterPanel extends JPanel {

    private Model model;
    private JLabel label = new JLabel();
    private int infoCounter;
    private boolean info;
    private final int INFO_MAX = 60;
    
    public FooterPanel() {
        this.info = false;
        
        add(label);
        setPreferredSize(new Dimension(WINDOW_WIDTH,25));
        label.setForeground(Color.GRAY);
    }

    /**
     * A játékost segítő alapüzenetet megjelenítő metódus
     */
    public void setHelpText() {
        if (model.getActualLevelId() == 10) {
            label.setText("Interact with the fountain to finish the game!");
        } else {
            if (model.canMoveToNextLevel()) label.setText("Interact with the sign to move to the next level!");
            else label.setText("Find pokeballs to free the pokémon!");
        }
    }
    
    /**
     * A játék fő frissítő ciklusában meghívott metódus.
     * A játékos pozíciójától függően üzeneteket jelenít meg, hogy mit tud ott a
     * játékos csinálni
     * 
     * Ezen kívül a rövid ideig megjelenített információk elrejtéséért felelős
     */
    public void loop() {
        if (!info) {
            if (model.getPlayer().isAtSign()) {
                if (model.getActualLevelId() == 10) {
                    label.setText("<html>Press <font color=black>SPACE</font> to finish the game!</html>");
                } else {
                    if (model.canMoveToNextLevel()) label.setText("<html>Press <font color=black>SPACE</font> to move to the next level!</html>");
                    else label.setText("<html>You have to find at least <font color=black>" + 
                            MainWindow.getInstance().getActiveLevel().minimumFoundPokemon() + 
                            " pokémon</font> to move to the next level!</html>");
                }
            } else if (model.getPlayer().isAtCarry() != null) {
                label.setText("<html>Press <font color=black>SPACE</font> to carry yourself!</html>");
            } else if (model.getPlayer().isOnCarry()) {
                label.setText("<html>Press <font color=black>SPACE</font> to leave carry!</html>");
            } else setHelpText();
        } else {
            infoCounter++;
            if (infoCounter >= INFO_MAX) {
                info = false;
            }
        }
    }
    
    /**
     * Kiír a címkére a rövid ideig megjelenített információkat
     * @param str az infromáció szövege
     */
    public void write(String str) {
        infoCounter = 0;
        info = true;
        label.setText("<html>" + str + "</html>");
    }
    
    public void setModel(Model model) {this.model = model;}
}
