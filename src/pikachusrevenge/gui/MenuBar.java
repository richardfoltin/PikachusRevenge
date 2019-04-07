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
import javax.swing.filechooser.FileNameExtensionFilter;
import pikachusrevenge.model.Level;
import pikachusrevenge.model.Model;
import pikachusrevenge.model.Model.Difficulty;
import pikachusrevenge.model.TilePosition;
import pikachusrevenge.model.Database;
import pikachusrevenge.model.Position;
import pikachusrevenge.resources.Resource;
import pikachusrevenge.unit.Player;
import pikachusrevenge.unit.Pokemon;

public final class MenuBar extends JMenuBar {
    
    public static class IllegalFileException extends Exception {}
 
    private static final int MENUITEM_WIDTH = 180;
    private static final int MENUITEM_HEIGHT = 25;
    private static final String SAVEFILE_EXTENSION = "pikasave";
    private static final String SAVEFILE_DESCRIPTION = "Pikachu's Revenge Save File";
    
    private final MainWindow window;
    private final JMenu pokedexMenu;
    private final JMenu levelSelect;
    
    private final JMenuItem saveToFile;
    private final JMenuItem saveToDb;
    private final JMenuItem pauseMenu;
    private final JMenuItem resumeMenu;
    
    public MenuBar(MainWindow window) {
        this.window = window;
        
        JMenu menuFile = new JMenu("File");  
        menuFile.setMnemonic('F');
        
        JMenuItem newGameMenu = new JMenuItem(newGameAction);
        newGameMenu.setText("Start New Game");
        newGameMenu.setMnemonic('N');
        newGameMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
        newGameMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(newGameMenu);
        
        menuFile.addSeparator();
        
        JMenuItem loadMenu = new JMenuItem(loadAction);
        loadMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.ALT_MASK));
        loadMenu.setText("Load From File...");
        loadMenu.setMnemonic('L');
        loadMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(loadMenu);
        
        saveToFile = new JMenuItem(saveToFileAction);
        saveToFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.ALT_MASK));
        saveToFile.setText("Save To File");
        saveToFile.setMnemonic('S');
        saveToFile.setEnabled(false);
        saveToFile.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(saveToFile);
        
        JMenuItem saveAsMenu = new JMenuItem(saveToFileAsAction);
        saveAsMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.ALT_MASK));
        saveAsMenu.setText("Save To File As...");
        saveAsMenu.setMnemonic('v');
        saveAsMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(saveAsMenu); 
        
        menuFile.addSeparator();
        
        JMenuItem loadDbMenu = new JMenuItem(loadDbAction);
        loadDbMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK));
        loadDbMenu.setText("Load From Database...");
        loadDbMenu.setMnemonic('O');
        loadDbMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(loadDbMenu);
        
        saveToDb = new JMenuItem(saveDbAction);
        saveToDb.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
        saveToDb.setText("Save To Database");
        saveToDb.setEnabled(false);
        saveToDb.setMnemonic('D');
        saveToDb.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(saveToDb);
        
        JMenuItem saveDbAsMenu = new JMenuItem(saveDbAsAction);
        saveDbAsMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK));
        saveDbAsMenu.setText("New Save To Database");
        saveDbAsMenu.setMnemonic('W');
        saveDbAsMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(saveDbAsMenu);
        
        menuFile.addSeparator();

        JMenuItem backMenu = new JMenuItem(backToMainMenuAction);
        backMenu.setText("Back to Main Menu");
        backMenu.setMnemonic('B');    
        backMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(backMenu);        
        
        JMenuItem exitMenu = new JMenuItem(exitAction);
        exitMenu.setText("Exit");
        exitMenu.setMnemonic('E');    
        exitMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuFile.add(exitMenu);
        
        add(menuFile);
        
        JMenu menuGame = new JMenu("Game");  
        menuGame.setMnemonic('G');
        
        JMenuItem restartMenu = new JMenuItem(restartAction);
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
        
        menuGame.addSeparator();

        JMenuItem highscoreMenu = new JMenuItem(highscoreAction);
        highscoreMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.CTRL_MASK)); 
        highscoreMenu.setText("Highscores");
        highscoreMenu.setMnemonic('H'); 
        highscoreMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuGame.add(highscoreMenu); 
        
        JMenuItem statisticsMenu = new JMenuItem(statisticsAction);
        statisticsMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.ALT_MASK)); 
        statisticsMenu.setText("Game Statistics");
        statisticsMenu.setMnemonic('S'); 
        statisticsMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuGame.add(statisticsMenu); 
        
        JMenuItem infoMenu = new JMenuItem(helpAction);
        infoMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0)); 
        infoMenu.setText("Help");
        infoMenu.setMnemonic('H');  
        infoMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH,MENUITEM_HEIGHT));
        menuGame.add(infoMenu);
        
        add(menuGame);
        
        pokedexMenu = new JMenu("Pokédex");  
        pokedexMenu.setMnemonic('P');
        pokedexMenu.setEnabled(false);
        add(pokedexMenu);
        
    }

    public final Action loadAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            pause();
            load();
            resume();
        }
    };
    
    public final Action saveToFileAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            pause();
            save(window.getModel().getFileName());
            resume();
        }
    };
    
    public final Action saveToFileAsAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            pause();
            if (save(null)) saveToFile.setEnabled(true);
            resume();
        }
    };
    
    public final Action loadDbAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            pause();
            MainWindow.getInstance().showLoadSelection();
            resume();
        }
    };
    
    public final Action saveDbAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            pause();
            int dbId = window.getModel().getDbId();
            if (Database.save(dbId,window.getModel())) window.saveSuccessful("Game saved sucessfully to database.");
            resume();
        }
    };
    
    public final Action saveDbAsAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            pause();
            if (Database.save(0,window.getModel())) {
                window.saveSuccessful("Game saved sucessfully to database.");
                saveToDb.setEnabled(true);
            }
            resume();
        }
    };
    
    private final Action restartAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            window.restartLevel();
        }
    };
    
    private final Action highscoreAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            pause();
            window.showHighscores();
            resume();
        }
    };
    
    private final Action statisticsAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            pause();
            window.showStatistics();
            resume();
        }
    };
    
    private final Action newGameAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Difficulty d = window.showDifficultySelector();
            if (d != null) window.loadLevelWithNewModel(new Model(d),1);
        }
    };
    
    private final Action exitAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            window.showExitConfirmation();
        }
    };
    
    private final Action backToMainMenuAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            window.showBackToMainMenuConfirmation();
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
    
    private final Action helpAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            pause();
            window.showHelp();
            resume();
        }
    };
    
    private void resume() {
        window.getModel().resumeGame();
        resumeMenu.setEnabled(false);
        pauseMenu.setEnabled(true);  
    }
    
    private void pause() {
        window.getModel().stopGame();
        resumeMenu.setEnabled(true);
        pauseMenu.setEnabled(false); 
    }

    
    public final ActionListener startLevelAction(final int level) {
        return (ActionEvent e) -> {
            Model model = window.getModel();
            if (model != null) model.stopGame();
            window.loadLevel(level);
        };
    } 
    
    private final ActionListener openPokemon(int id) {
        return (ActionEvent e) -> {
            try {
                Desktop.getDesktop().browse(new URI("https://pokedex.org/#/pokemon/" + id));
            } catch (Exception ex) {
                System.err.println("Can't open pokedex entry");;
            }
        };
    }
    
    public static boolean load() {
        MainWindow window = MainWindow.getInstance();
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(SAVEFILE_DESCRIPTION,SAVEFILE_EXTENSION);
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Load Game");
        chooser.setApproveButtonText("Load");
        chooser.setApproveButtonMnemonic('L');
        File chosenFile = (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) ? chooser.getSelectedFile() : null;
        if (chosenFile != null) {
            try (final Scanner sc = new Scanner(chosenFile)) {
                Difficulty difficulty = Difficulty.fromId(loadInt(sc));
                Model model = new Model(chosenFile.getAbsolutePath(), difficulty);
                Player player = model.getPlayer();
                HashMap<TilePosition,Pokemon> pokemons = model.getAllPokemonsWithPosition();
                int actualLevel = loadInt(sc);
                player.setLives(loadInt(sc));
                double x = (double)loadInt(sc);
                double y = (double)loadInt(sc);
                
                int pokemonCount = loadInt(sc);
                for (int i = 0; i < pokemonCount; i++) {
                    int level = loadInt(sc);
                    int x1 = loadInt(sc);
                    int y1 = loadInt(sc);
                    int id = loadInt(sc);
                    boolean found = sc.nextBoolean();
                    TilePosition tpos = new TilePosition(x1, y1, level);
                    Pokemon p = new Pokemon(model,tpos,id,found);
                    pokemons.put(tpos,p);
                }
                int levelCount = loadInt(sc);
                player.increaseAvailableLevels(levelCount);
                for (int i = 0; i < levelCount; i++){
                    int id = loadInt(sc);
                    int time = loadInt(sc);
                    model.buildLevelIfNotExists(id,time);
                }
                window.loadLevelWithNewModel(model, actualLevel, new Position(x,y));
            }catch (IllegalFileException ex) {
                JOptionPane.showMessageDialog(window, "Not proper Pikachu's Revenge saved file.");
                return false;
            }catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(window, "File not found!");
                return false;
            }
        }  
        return true;
    }
    
    private static int loadInt(Scanner sc) throws IllegalFileException {
        if (sc.hasNextInt()) return sc.nextInt();
        else throw new IllegalFileException();    
    }
    
    private boolean save(String fileName){
        File file;
        if (fileName == null) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(SAVEFILE_DESCRIPTION,SAVEFILE_EXTENSION);
            chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            chooser.setFileFilter(filter);
            chooser.setDialogTitle("Save Game");
            chooser.setApproveButtonText("Save");
            chooser.setApproveButtonMnemonic('S');
            file = (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) ? new File(chooser.getSelectedFile() + "." + SAVEFILE_EXTENSION) : null;
        } else {
            file = new File(fileName);
        }
        if (file != null) {
            try (PrintWriter pw = new PrintWriter(file)){
                Model model = window.getModel();
                Player player = model.getPlayer();
                HashMap<TilePosition,Pokemon> pokemons = model.getAllPokemonsWithPosition();
                ArrayList<Level> levels = model.getLevels();
                int actualLevel = model.getActualLevelId();
                int difficulty = model.getDifficulty().id;

                pw.println(difficulty + " " +
                           actualLevel + " " + 
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
                
                window.saveSuccessful("Game saved sucessfully to " + file.getName());
                model.setFileName(fileName);
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(window, "File not found!");
                return false;
            }
        }
        return true;
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
                JMenu subMenu = new JMenu(String.format("%s - %s", from, to));
                subMenu.setPreferredSize(new Dimension(MENUITEM_WIDTH / 2,MENUITEM_HEIGHT));
                menus.add(subMenu);
                pokedexMenu.add(subMenu);
            }
        } else {
            menus.add(pokedexMenu);
        }
        
        for (int id : pokemonIds) {
            if (id  >= 1 && id <= Pokemon.POKEMON_NAME.length) {
                BufferedImage image = null;
                try {image = Resource.loadBufferedImage(String.format("pokemons/icon%03d.png",id));} 
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
    
    public void setAvailableLevels(int maxLevel){
        for (int i = 0; i < levelSelect.getItemCount(); ++i){
            levelSelect.getItem(i).setEnabled(i < maxLevel || MainWindow.TESTING);
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
    
    public JMenuItem getSaveMenu() {return saveToFile;}
    public JMenuItem getSaveDbMenu() {return saveToDb;}
}
