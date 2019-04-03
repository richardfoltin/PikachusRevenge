package pikachusrevenge.gui;

import static java.awt.Component.CENTER_ALIGNMENT;
import static java.awt.Component.LEFT_ALIGNMENT;
import static java.awt.Component.TOP_ALIGNMENT;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static pikachusrevenge.gui.MainWindow.WINDOW_HEIGHT;
import static pikachusrevenge.gui.MainWindow.WINDOW_WIDTH;
import pikachusrevenge.resources.Resource;

public final class HelpDialog extends JDialog {
 
    private final int DIALOG_WIDTH = 400;
    private final int DIALOG_HEIGHT = 346;
    private final int DIALOG_BORDER = 10;
    private final int IMAGE_SIZE = 32;
    private final MainWindow window;
    
    public HelpDialog(MainWindow frame) {
        super(frame,"How to Play?",true);
        
        this.window = frame;
        this.setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));  
        this.setVisible(false);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setAlignmentY(TOP_ALIGNMENT);
        panel.setBorder(BorderFactory.createEmptyBorder(DIALOG_BORDER, DIALOG_BORDER, DIALOG_BORDER, DIALOG_BORDER));
        add(panel);
        
        JLabel info = new JLabel();
        info.setAlignmentX(LEFT_ALIGNMENT);
        info.setText("<html>Pikachu is fed up with the pokémon trainers who always a catch innocent pokémon. Help him to rescure the caught pokémon from the pokéballs!</html>");
        panel.add(info);
        panel.add(Box.createRigidArea(new Dimension(0,DIALOG_BORDER)));
        
        JLabel ball = new JLabel();
        ball.setFont(ball.getFont().deriveFont(Font.PLAIN));
        ball.setAlignmentX(LEFT_ALIGNMENT);
        ball.setText("<html>Find pokéballs on the map! Some of them could be hidden behind objects!</html>");
        BufferedImage ballImage = null;
        try {ballImage = Resource.loadBufferedImage("ball_shadow.png");} 
        catch (IOException e) {System.err.println("Can't load file: ball_shadow.png");} 
        ballImage = Resource.getScaledImage(ballImage, IMAGE_SIZE, IMAGE_SIZE);
        ball.setIcon(new ImageIcon(ballImage));
        panel.add(ball);
        panel.add(Box.createRigidArea(new Dimension(0,DIALOG_BORDER)));
        
        JLabel trainer = new JLabel();
        trainer.setFont(trainer.getFont().deriveFont(Font.PLAIN));
        trainer.setAlignmentX(LEFT_ALIGNMENT);
        trainer.setText("<html>Try to avoid vengeful trainers! They catch you instantly when you go very close in their line of sight!</html>");
        BufferedImage trainerImage = null;
        try {trainerImage = Resource.loadBufferedImage("npc\\trchar035.png");} 
        catch (IOException e) {System.err.println("Can't load file");} 
        trainerImage = Resource.getSprite(trainerImage, 0, 0);
        trainerImage = Resource.getScaledImage(trainerImage, IMAGE_SIZE, IMAGE_SIZE);
        trainer.setIcon(new ImageIcon(trainerImage));
        panel.add(trainer);
        panel.add(Box.createRigidArea(new Dimension(0,DIALOG_BORDER)));
        
        JLabel exclamation = new JLabel();
        exclamation.setFont(exclamation.getFont().deriveFont(Font.PLAIN));
        exclamation.setAlignmentX(LEFT_ALIGNMENT);
        exclamation.setText("<html>When they just see you in the distance, they will stop to prepare to catch you! "
                            + "You have some time to run away, but watch out! When they are prepared, they can catch you even from a distance!</html>");
        BufferedImage exclamationImage = null;
        try {exclamationImage = Resource.loadBufferedImage("exclamation.png");} 
        catch (IOException e) {System.err.println("Can't load file");} 
        exclamationImage = Resource.getScaledImage(exclamationImage, IMAGE_SIZE, IMAGE_SIZE);
        exclamation.setIcon(new ImageIcon(exclamationImage));
        panel.add(exclamation);
        panel.add(Box.createRigidArea(new Dimension(0,DIALOG_BORDER)));

        JLabel sign = new JLabel();
        sign.setFont(sign.getFont().deriveFont(Font.PLAIN));
        sign.setAlignmentX(LEFT_ALIGNMENT);
        sign.setText("<html>If you have found at least half of the pokémon on the map, you can advance to the next level at the sign!</html>");
        BufferedImage signImage = null;
        try {signImage = Resource.loadBufferedImage("sign.png");} 
        catch (IOException e) {System.err.println("Can't load file");} 
        signImage = Resource.getScaledImage(signImage, IMAGE_SIZE, IMAGE_SIZE);
        sign.setIcon(new ImageIcon(signImage));
        panel.add(sign);
        panel.add(Box.createRigidArea(new Dimension(0,DIALOG_BORDER)));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
        buttonPanel.setPreferredSize(new Dimension(DIALOG_WIDTH - DIALOG_BORDER * 2, 30));  
        panel.add(buttonPanel);
        
        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(100,30));
        okButton.setAlignmentX(CENTER_ALIGNMENT);
        okButton.addActionListener(new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) { 
                HelpDialog.this.setVisible(false);
            } 
        });    
        buttonPanel.add(okButton);
        
        pack();
        setResizable(false);
    }
    
    public void showDialog() {
        Point windowLocation = window.getLocation();
        setLocation(new Point(windowLocation.x + window.getWidth() / 2 - DIALOG_WIDTH/2, windowLocation.y + window.getWidth()/2 - DIALOG_HEIGHT/2 - 20)); 
        setVisible(true);
    }
}
