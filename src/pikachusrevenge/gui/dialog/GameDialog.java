package pikachusrevenge.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import pikachusrevenge.gui.MainWindow;

public abstract class GameDialog extends JDialog {
 
    protected final int DIALOG_WIDTH = 400;
    protected final int DIALOG_HEIGHT = 346;
    protected final int DIALOG_BORDER = 10;
    protected final int BUTTON_HEIGHT = 26;
    protected final int BUTTON_WIDTH = 100;
    protected final int IMAGE_SIZE = 32;
    protected final int ROW_HEIGHT = 28;
    protected MainWindow window;
    protected JPanel mainPanel;
    protected JPanel bottomPanel;
    protected String errorMessage;
    
    public GameDialog(MainWindow frame, String title) {
        super(frame,title,true);
        
        this.window = frame;
        this.setVisible(false);
        setLayout(new BorderLayout());
        
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(DIALOG_BORDER, DIALOG_BORDER, DIALOG_BORDER, DIALOG_BORDER));
        mainPanel.setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT - DIALOG_BORDER - BUTTON_HEIGHT));     
        fillMainPanel();
        add(mainPanel,BorderLayout.CENTER);
        
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, DIALOG_BORDER, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, DIALOG_BORDER, DIALOG_BORDER, DIALOG_BORDER));
        bottomPanel.setPreferredSize(new Dimension(DIALOG_WIDTH, BUTTON_HEIGHT + DIALOG_BORDER));   
        //bottomPanel.setBorder(BorderFactory.createLineBorder(Color.yellow));
        fillBottomPanel();
        add(bottomPanel,BorderLayout.SOUTH);
        
        pack();
        setResizable(false);
    }
    
    protected abstract void fillMainPanel();
    protected abstract void fillBottomPanel();
    
    public void showDialog() {
        Point windowLocation = window.getLocation();
        setLocation(new Point(windowLocation.x + window.getWidth() / 2 - DIALOG_WIDTH/2, windowLocation.y + window.getWidth()/2 - DIALOG_HEIGHT/2 - 20)); 
        setVisible(true);
    }

    public String getLoadMessage() {return errorMessage;} 
    
}
