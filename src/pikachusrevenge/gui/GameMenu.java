package pikachusrevenge.gui;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class GameMenu  extends JMenuBar {

    private final JMenuItem loadMenu;
    private final JMenuItem saveMenu;
    private final JMenuItem exitSaveMenu;
    private final JMenuItem exitMenu;
    private JMenuItem pauseMenu;
    private JMenuItem resumeMenu;
    
    private static final int MENU_WIDTH = 180;
    private static final int MENU_HEIGHT = 25;
    
    public GameMenu() {

        JMenu menuFile = new JMenu("File");  
        menuFile.setMnemonic('F');
        
        loadMenu = new JMenuItem(loadAction);
        loadMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK));
        loadMenu.setText("Load Game...");
        loadMenu.setMnemonic('L');
        loadMenu.setPreferredSize(new Dimension(MENU_WIDTH,MENU_HEIGHT));
        menuFile.add(loadMenu);
        
        saveMenu = new JMenuItem(saveAction);
        saveMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
        saveMenu.setText("Save Game...");
        saveMenu.setMnemonic('S');
        saveMenu.setPreferredSize(new Dimension(MENU_WIDTH,MENU_HEIGHT));
        menuFile.add(saveMenu);
        
        exitSaveMenu = new JMenuItem(exitAction);
        exitSaveMenu.setText("Exit and Save");
        exitSaveMenu.setMnemonic('E');    
        exitSaveMenu.setPreferredSize(new Dimension(MENU_WIDTH,MENU_HEIGHT));
        menuFile.add(exitSaveMenu);  
        
        menuFile.addSeparator();
        
        exitMenu = new JMenuItem(exitAction);
        exitMenu.setText("Exit Without Saving");
        exitMenu.setMnemonic('x');    
        exitMenu.setPreferredSize(new Dimension(MENU_WIDTH,MENU_HEIGHT));
        menuFile.add(exitMenu);
        
        add(menuFile);
        
        JMenu menuGame = new JMenu("Game");  
        menuGame.setMnemonic('G');
        
        pauseMenu = new JMenuItem(pauseAction);
        pauseMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0)); 
        pauseMenu.setText("Pause");
        pauseMenu.setMnemonic('P'); 
        pauseMenu.setPreferredSize(new Dimension(MENU_WIDTH,MENU_HEIGHT));
        menuGame.add(pauseMenu); 
        
        resumeMenu = new JMenuItem(resumeAction);
        resumeMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0)); 
        resumeMenu.setText("Resume");
        resumeMenu.setMnemonic('R');  
        resumeMenu.setPreferredSize(new Dimension(MENU_WIDTH,MENU_HEIGHT));
        resumeMenu.setEnabled(false);
        menuGame.add(resumeMenu);

        add(menuGame);
        
    }
    
    private final Action saveAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
//            stopTimers();
//
//            FileChooser chooser = new FileChooser("Mentés");
//            File chosenFile = chooser.open();
//            if (chosenFile != null) {
//                try {
//                    persistence.save(chosenFile, model);
//                    changed = false;
//                } catch (SudokuIOException ex) {
//                    JOptionPane.showMessageDialog(MainWindow.this,
//                            "Hiba történt a fájl mentésekor.");
//                }
//            }
//
//            startTimers();
        }
    };

    private final Action loadAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
//            stopTimers();
//
//            FileChooser chooser = new FileChooser("Betöltés");
//            File chosenFile = chooser.open();
//            if (chosenFile != null) {
//                try {
//                    model = persistence.load(chosenFile);
//                    saveAction.setEnabled(true);
//                    checkAction.setEnabled(true);
//                    resetGridAndTimers();
//                } catch (SudokuIOException ex) {
//                    JOptionPane.showMessageDialog(MainWindow.this,
//                            "Hiba történt a fájl betöltésekor.");
//                }
//            }
//
//            startTimers();
        }
    };

    private final Action startNewGameAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
//            stopTimers();
//
//            Difficulty difficulty = menuBar.getDifficulty();
//            try {
//                model = persistence.loadNewGame(difficulty);
//                changed = true;
//                saveAction.setEnabled(true);
//                checkAction.setEnabled(true);
//                resetGridAndTimers();
//            } catch (SudokuIOException ex) {
//                JOptionPane.showMessageDialog(MainWindow.this,
//                        "Hiba történt új játék betöltésekor.");
//            }
//
//            startTimers();
        }
    };
    
    
    private final Action exitAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
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
