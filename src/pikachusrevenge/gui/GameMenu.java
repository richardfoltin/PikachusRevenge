package pikachusrevenge.gui;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import pikachusrevenge.model.Level;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.TilePosition;
import pikachusrevenge.unit.Player;
import pikachusrevenge.unit.Pokemon;

public class GameMenu  extends JMenuBar {

    private final JMenuItem newGameMenu;
    private final JMenuItem loadMenu;
    private final JMenuItem saveMenu;
    private final JMenuItem exitSaveMenu;
    private final JMenuItem exitMenu;
    private final JMenu levelSelect;
    private final JMenuItem restartMenu;
    private JMenuItem pauseMenu;
    private JMenuItem resumeMenu;
    
    private static final int MENUITEM_WIDTH = 180;
    private static final int MENUITEM_HEIGHT = 25;

    public static class IllegalFileException extends Exception {}
    
    public GameMenu() {
        
        JMenu menuFile = new JMenu("File");  
        menuFile.setMnemonic('F');
        
        newGameMenu = new JMenuItem(newGameAction);
        newGameMenu.setText("New Game");
        newGameMenu.setMnemonic('N');
        newGameMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(newGameMenu);
        
        loadMenu = new JMenuItem();
        loadMenu.addActionListener(loadAction());
        loadMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK));
        loadMenu.setText("Load Game...");
        loadMenu.setMnemonic('L');
        loadMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(loadMenu);
        
        saveMenu = new JMenuItem();
        saveMenu.addActionListener(saveAction());
        saveMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
        saveMenu.setText("Save Game...");
        saveMenu.setMnemonic('S');
        saveMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(saveMenu);

        menuFile.addSeparator();
        
        exitSaveMenu = new JMenuItem(exitAction);
        exitSaveMenu.setText("Exit and Save");
        exitSaveMenu.setMnemonic('E');    
        exitSaveMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(exitSaveMenu);  
        
        
        exitMenu = new JMenuItem(exitAction);
        exitMenu.setText("Exit Without Saving");
        exitMenu.setMnemonic('x');    
        exitMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(exitMenu);
        
        add(menuFile);
        
        JMenu menuGame = new JMenu("Game");  
        menuGame.setMnemonic('G');
        
        restartMenu = new JMenuItem(restartAction);
        restartMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK)); 
        restartMenu.setText("Restart Level");
        restartMenu.setMnemonic('R'); 
        restartMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuGame.add(restartMenu); 
        
        levelSelect = new JMenu("Go back to level..."); 
        levelSelect.setMnemonic('G'); 
        levelSelect.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuGame.add(levelSelect); 
        addLevels(levelSelect);
        
        menuGame.addSeparator();
        
        pauseMenu = new JMenuItem(pauseAction);
        pauseMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0)); 
        pauseMenu.setText("Pause");
        pauseMenu.setMnemonic('P'); 
        pauseMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuGame.add(pauseMenu); 
        
        resumeMenu = new JMenuItem(resumeAction);
        resumeMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0)); 
        resumeMenu.setText("Resume");
        resumeMenu.setMnemonic('e');  
        resumeMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        resumeMenu.setEnabled(false);
        menuGame.add(resumeMenu);

        add(menuGame);
        
    }
    
    public void setAvailableLevels(int maxLevel){
        for (int i = 0; i < levelSelect.getItemCount(); ++i){
            levelSelect.getItem(i).setEnabled(i < maxLevel);
        }
    }
    
    private void addLevels(JMenu menu){
        
        for (int i = 1; i <= 10; ++i){
            JMenuItem menuItem = new JMenuItem();
            menuItem.addActionListener(startLevelAction(i));
            menuItem.setText("Level " + i);
            if (i > 5) menuItem.setEnabled(false);
            menuItem.setPreferredSize(new Dimension(MENUITEM_WIDTH / 2,MENUITEM_HEIGHT));
            menu.add(menuItem); 
        }
    }  
    
    public static final ActionListener loadAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Load Game");
                chooser.setApproveButtonText("Load");
                chooser.setApproveButtonMnemonic('L');
                File chosenFile = (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) ? chooser.getSelectedFile() : null;
                if (chosenFile != null) {
                    try (Scanner sc = new Scanner(chosenFile)){
                        Model model = new Model();
                        Player player = model.getPlayer();
                        HashMap<TilePosition,Pokemon> pokemons = model.getAllPokemons();
                        
                        int actualLevel = loadInt(sc);
                        player.setLives(loadInt(sc));
                        player.getPosition().x = (double)loadInt(sc);
                        player.getPosition().y = (double)loadInt(sc); 

                        int pokemonCount = loadInt(sc);      
                        for (int i = 0; i < pokemonCount; i++) {
                            int level = loadInt(sc);
                            int x = loadInt(sc);
                            int y = loadInt(sc);
                            int id = loadInt(sc);
                            boolean found = sc.nextBoolean();
                            TilePosition tpos = new TilePosition(x,y,level);
                            Pokemon p = new Pokemon(model,tpos,id,found);
                            pokemons.put(tpos,p);
                        }
                        
                        int levelCount = loadInt(sc);
                        for (int i = 0; i < levelCount; i++){
                            int id = loadInt(sc);
                            int time = loadInt(sc);
                            model.buildLevelIfNotExists(id,time);
                        }
                        
                        MainWindow.getInstance().loadLevelWithNewModel(model, actualLevel);

                    } catch (IllegalFileException ex) {
                        JOptionPane.showMessageDialog(MainWindow.getInstance(), "Not proper Pikachu's Revenge saved file.");
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(MainWindow.getInstance(), "File not found!");
                    }
                }
            }
        };
    }
    
    private static final int loadInt(Scanner sc) throws IllegalFileException {
        if (sc.hasNextInt()) return sc.nextInt();
        else throw new IllegalFileException();    
    }
    
    /**
     * A mentéshez szükséges metódus. Feldob egy file-választó ablakot 
     * és a kiválasztott file-ba elmenti a játékot
     * 
     * @param game a játék ablaka
     * @param withExit true, ha a mentés után ki is kell lépni a játékból
     */
    private static final void save(boolean withExit){
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Game");
        chooser.setApproveButtonText("Save");
        chooser.setApproveButtonMnemonic('S');
        File chosenFile = (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) ? chooser.getSelectedFile() : null;
        if (chosenFile != null) {
            try (PrintWriter pw = new PrintWriter(chosenFile)){
                Model model = MainWindow.getInstance().getModel();
                Player player = model.getPlayer();
                HashMap<TilePosition,Pokemon> pokemons = model.getAllPokemons();
                ArrayList<Level> levels = model.getLevels();
                int actualLevel = model.getActualLevelId();

                pw.println(actualLevel + " " + 
                           player.getLives() + " " + 
                           (int)player.getPosition().x + " " + 
                           (int)player.getPosition().y);
                pw.println(pokemons.size());
                
                for (HashMap.Entry<TilePosition,Pokemon> p : pokemons.entrySet()) {
                    pw.println(p.getKey().getLevel() + " " + 
                               p.getKey().getX() + " " + 
                               p.getKey().getY() + " " + 
                               p.getValue().getId() + " " + 
                               p.getValue().isFound());
                }
                
                pw.println(levels.size());
                for (Level level : levels){
                    pw.println(level.getId() + " " +
                               level.getTime());
                }

            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(MainWindow.getInstance(), "File not found!");
            }
        }  
        
        if (withExit) System.exit(0);
    }
    
    /**
     * A játék mentéséhez szükséges eseménykezelőt hoz létre
     * 
     * @param game a játék ablaka
     * @return a létrehozott eseménykezelő
     */
    public static final ActionListener saveAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save(false);
            }
        };
    }

    /**
     * A játék mentéséhez és kilépéshez szükséges eseménykezelőt hoz létre
     * 
     * @param game a játék ablaka
     * @return a létrehozott eseménykezelő
     */
    public static final ActionListener exitAndSaveAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save(true);
                
            }
        };
    }
    
    public static final ActionListener startLevelAction(final int level) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    MainWindow.getInstance().loadLevel(level);
            }
        };
    } 
    
    private final Action restartAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            MainWindow.getInstance().loadLevel(MainWindow.getInstance().getModel().getActualLevelId());
        }
    };
    
    private final Action newGameAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            MainWindow.getInstance().restartNewGame();
        }
    };
    
    private final Action exitAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            MainWindow.getInstance().showExitConfirmation();
        }
    };
    
    private final Action pauseAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            resumeMenu.setEnabled(true);
            pauseMenu.setEnabled(false);
        }
    };
    
    private final Action resumeAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            resumeMenu.setEnabled(false);
            pauseMenu.setEnabled(true);
        }
    };
}
