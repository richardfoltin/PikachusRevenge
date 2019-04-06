package pikachusrevenge.gui.dialog;

import static java.awt.Component.CENTER_ALIGNMENT;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import pikachusrevenge.gui.MainWindow;
import pikachusrevenge.resources.Resource;

public final class HelpDialog extends GameDialog {
    
    public HelpDialog(MainWindow frame) {
        super(frame,"How to Play?");
    }

    @Override
    protected void fillMainPanel() {
        JLabel info = new JLabel();
        info.setAlignmentX(LEFT_ALIGNMENT);
        info.setText("<html>Pikachu is fed up with the pokémon trainers who always a catch innocent pokémon. Help him to rescure the caught pokémon from the pokéballs!</html>");
        mainPanel.add(info);
        mainPanel.add(Box.createRigidArea(new Dimension(0,DIALOG_BORDER)));
        
        JLabel ball = new JLabel();
        ball.setFont(ball.getFont().deriveFont(Font.PLAIN));
        ball.setAlignmentX(LEFT_ALIGNMENT);
        ball.setText("<html>Find pokéballs on the map! Some of them could be hidden behind objects!</html>");
        BufferedImage ballImage = null;
        try {ballImage = Resource.loadBufferedImage("ball_shadow.png");} 
        catch (IOException e) {System.err.println("Can't load file: ball_shadow.png");} 
        ballImage = Resource.getScaledImage(ballImage, IMAGE_SIZE, IMAGE_SIZE);
        ball.setIcon(new ImageIcon(ballImage));
        mainPanel.add(ball);
        mainPanel.add(Box.createRigidArea(new Dimension(0,DIALOG_BORDER)));
        
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
        mainPanel.add(trainer);
        mainPanel.add(Box.createRigidArea(new Dimension(0,DIALOG_BORDER)));
        
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
        mainPanel.add(exclamation);
        mainPanel.add(Box.createRigidArea(new Dimension(0,DIALOG_BORDER)));

        JLabel sign = new JLabel();
        sign.setFont(sign.getFont().deriveFont(Font.PLAIN));
        sign.setAlignmentX(LEFT_ALIGNMENT);
        sign.setText("<html>If you have found at least half of the pokémon on the map, you can advance to the next level at the sign!</html>");
        BufferedImage signImage = null;
        try {signImage = Resource.loadBufferedImage("sign.png");} 
        catch (IOException e) {System.err.println("Can't load file");} 
        signImage = Resource.getScaledImage(signImage, IMAGE_SIZE, IMAGE_SIZE);
        sign.setIcon(new ImageIcon(signImage));
        mainPanel.add(sign);
    }

    @Override
    protected void fillBottomPanel() {
        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(BUTTON_WIDTH,BUTTON_HEIGHT));
        okButton.setAlignmentX(CENTER_ALIGNMENT);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HelpDialog.this.setVisible(false);
            }
        });
        bottomPanel.add(okButton);
    }
}
