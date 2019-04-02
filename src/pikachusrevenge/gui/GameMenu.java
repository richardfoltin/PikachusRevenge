package pikachusrevenge.gui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import pikachusrevenge.model.Level;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.TilePosition;
import pikachusrevenge.resources.Resource;
import pikachusrevenge.unit.Player;
import pikachusrevenge.unit.Pokemon;

public class GameMenu  extends JMenuBar {

    private final JMenuItem newGameMenu;
    private final JMenuItem loadMenu;
    private final JMenuItem loadDbMenu;
    private final JMenuItem saveAsMenu;
    private final JMenuItem saveMenu;
    private final JMenuItem saveDbMenu;
    private final JMenuItem exitSaveMenu;
    private final JMenuItem exitMenu;
    private final JMenu pokedexMenu;
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
        newGameMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
        newGameMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(newGameMenu);
        
        menuFile.addSeparator();
        
        loadMenu = new JMenuItem();
        loadMenu.addActionListener(loadAction());
        loadMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.ALT_MASK));
        loadMenu.setText("Load...");
        loadMenu.setMnemonic('o');
        loadMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(loadMenu);
        
        loadDbMenu = new JMenuItem();
        loadDbMenu.addActionListener(loadDbAction());
        loadDbMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK));
        loadDbMenu.setText("Load From Database...");
        loadDbMenu.setMnemonic('L');
        loadDbMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(loadDbMenu);

        saveMenu = new JMenuItem();
        saveMenu.addActionListener(saveAction());
        saveMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
        saveMenu.setText("Save");
        saveMenu.setMnemonic('S');
        saveMenu.setEnabled(false);
        saveMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(saveMenu);
        
        saveAsMenu = new JMenuItem();
        saveAsMenu.addActionListener(saveAsAction());
        saveAsMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.ALT_MASK));
        saveAsMenu.setText("Save As...");
        saveAsMenu.setMnemonic('A');
        saveAsMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(saveAsMenu);
        
        saveDbMenu = new JMenuItem();
        saveDbMenu.addActionListener(saveDbAction());
        saveDbMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK));
        saveDbMenu.setText("Save To Database");
        saveDbMenu.setMnemonic('D');
        saveDbMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(saveDbMenu);
        
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
        
        pokedexMenu = new JMenu("Pokédex");  
        pokedexMenu.setMnemonic('P');
        pokedexMenu.setEnabled(false);
        add(pokedexMenu);
        
    }
    
    public void setAvailableLevels(int maxLevel){
        for (int i = 0; i < levelSelect.getItemCount(); ++i){
            levelSelect.getItem(i).setEnabled(i < maxLevel-1);
            levelSelect.getItem(i).setEnabled(true); // testing
        }
    }
    
    private void addLevels(JMenu menu){
        
        for (int i = 1; i <= 10; ++i){
            JMenuItem menuItem = new JMenuItem();
            menuItem.addActionListener(startLevelAction(i));
            menuItem.setText("Level " + i);
            menuItem.setEnabled(false);
            menuItem.setPreferredSize(new Dimension(MENUITEM_WIDTH / 2,MENUITEM_HEIGHT));
            menu.add(menuItem); 
        }
    }  
    
    public final ActionListener loadAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pause();
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Load Game");
                chooser.setApproveButtonText("Load");
                chooser.setApproveButtonMnemonic('L');
                File chosenFile = (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) ? chooser.getSelectedFile() : null;
                if (chosenFile != null) {
                    try (Scanner sc = new Scanner(chosenFile)){
                        Model model = new Model(chosenFile.getAbsolutePath());
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
                        resume();
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(MainWindow.getInstance(), "File not found!");
                        resume();
                    }
                }
            }
        };
    }
    
    public final ActionListener loadDbAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pause();
                File chosenFile = null;
                int dbId = 1;
                if (chosenFile != null) {
                    try (Scanner sc = new Scanner(chosenFile)){
                        Model model = new Model(dbId);
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
                        resume();
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(MainWindow.getInstance(), "File not found!");
                        resume();
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
    private final void save(boolean withExit, String fileName){
        File file;
        if (fileName == null) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Game");
            chooser.setApproveButtonText("Save");
            chooser.setApproveButtonMnemonic('S');
            file = (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) ? chooser.getSelectedFile() : null;
        } else {
            file = new File(fileName);
        }
        if (file != null) {
            try (PrintWriter pw = new PrintWriter(file)){
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
    
   private final void saveToDb(boolean withExit, int dbId){
       File file = null;
        if (file != null) {
            try (PrintWriter pw = new PrintWriter(file)){
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
    public final ActionListener saveAsAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pause();
                save(false,null);
                resume();
            }
        };
    }
    
    public final ActionListener saveAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pause();
                Model model = MainWindow.getInstance().getModel();
                if (model.isSavedToDb()) saveToDb(false,model.getDbId());
                else save(false,model.getFileName());
                resume();
            }
        };
    }
    
    public final ActionListener saveDbAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pause();
                Model model = MainWindow.getInstance().getModel();
                saveToDb(false,model.getDbId());
                resume();
            }
        };
    }
    
    /**
     * A játék mentéséhez és kilépéshez szükséges eseménykezelőt hoz létre
     * 
     * @param game a játék ablaka
     * @return a létrehozott eseménykezelő
     */
    public final ActionListener exitAndSaveAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Model model = MainWindow.getInstance().getModel();
                if (model.isSavedToDb()) saveToDb(true,model.getDbId());
                else save(true,model.getFileName());
            }
        };
    }
    
    public final ActionListener startLevelAction(final int level) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Model model = MainWindow.getInstance().getModel();
                if (model != null) model.stopGame();
                MainWindow.getInstance().loadLevel(level);
            }
        };
    } 
    
    private final Action restartAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            MainWindow.getInstance().restartLevel();
        }
    };
    
    private final Action newGameAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            MainWindow.getInstance().loadLevelWithNewModel(new Model(), 1);
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
            pause();
        }
    };
    
    private final Action resumeAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            resume();
        }
    };
    
    private void resume() {
        MainWindow.getInstance().getModel().restartGame();
        resumeMenu.setEnabled(false);
        pauseMenu.setEnabled(true);  
    }
    
    private void pause() {
        MainWindow.getInstance().getModel().stopGame();
        resumeMenu.setEnabled(true);
        pauseMenu.setEnabled(false); 
    }
    
    public final ActionListener openPokemon(int id) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://pokedex.org/#/pokemon/" + id));
                } catch (Exception ex) {
                    System.err.println("Can't open pokedex entry");;
                }
            }
        };
    }
    
    public void buildPokedexMenu(Model model) {
        ArrayList<Integer> pokemonIds = (ArrayList<Integer>) model.getAllFoundIds();
        Collections.sort(pokemonIds);
        pokedexMenu.setEnabled(pokemonIds.size() > 0);
        pokedexMenu.removeAll();
        
        ArrayList<JMenu> menus = new ArrayList<>();
        int max = Pokemon.POKEMON_NAME.length;
        int clusters = pokemonIds.size() / 20 + 1;
        
        if (clusters > 1) {
            for (int i = 0; i < clusters; ++i){
                int from = i * (max / clusters) + 1;
                int to = (i == clusters - 1) ? max : (i + 1) * (max / clusters);
                JMenu menu = new JMenu(String.format("%s - %s", from, to));
                menu.setPreferredSize(new Dimension(MENUITEM_WIDTH / 2,MENUITEM_HEIGHT));
                menus.add(menu);
                pokedexMenu.add(menu);
            }
        } else {
            menus.add(pokedexMenu);
        }
        
        for (int id : pokemonIds) {
            if (id  >= 1 && id <= Pokemon.POKEMON_NAME.length) {
                BufferedImage image = null;
                try {image = Resource.loadBufferedImage(String.format("pokemons\\icon%03d.png",id));} 
                catch (IOException e) {System.err.println("Can't load file");} 
                image = Resource.getSprite(image, 0, 0);
                image = Resource.getScaledImage(image, MENUITEM_HEIGHT, MENUITEM_HEIGHT);
                JMenuItem item = new JMenuItem(Pokemon.POKEMON_NAME[id-1], new ImageIcon(image));
                item.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
                item.addActionListener(openPokemon(id));
                
                menus.get(id / ((max / clusters) + 1)).add(item);
            }
        }
    }
    
    
    public JMenuItem getSaveMenu() {return saveMenu;}
}
