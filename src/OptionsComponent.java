import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class OptionsComponent extends JComponent implements ActionListener {

  // Four menu buttons: start game, exit, help, and info
  private JButton playButton = new JButton("PLAY");
  private JButton quitButton = new JButton("EXIT");
  private JButton helpButton = new JButton("HELP");
  private JButton infoButton = new JButton("INFO");

  // Background image for the menu screen
  private static BufferedImage tableBackground;

  public OptionsComponent() {
    // Attach action listeners to menu buttons
    playButton.addActionListener(this);
    quitButton.addActionListener(this);
    helpButton.addActionListener(this);
    infoButton.addActionListener(this);
  }

  @Override
  public void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;

    // Attempt to load the background image
    try {
      tableBackground = ImageIO.read(new File("images/background.jpg"));
    } catch (IOException e) {
      // If no image is found, no special handling needed here
    }

    // Draw background
    g2.drawImage(tableBackground, 0, 0, null);

    // Title and subtitle styling
    g2.setFont(new Font("Montserrat", Font.BOLD, 100));
    g2.setColor(Color.WHITE);
    g2.drawString("BlackJack!", 300, 100);

    g2.setFont(new Font("Arial", Font.BOLD, 30));
    g2.drawString("This game is brought to you by BlackJack365", 220, 580);

    // Position and style the buttons
    playButton.setBounds(500, 300, 150, 80);
    quitButton.setBounds(500, 400, 150, 80);
    helpButton.setBounds(80, 75, 150, 80);
    infoButton.setBounds(900, 75, 150, 80);

    playButton.setFont(new Font("Montserrat", Font.BOLD, 40));
    quitButton.setFont(new Font("Montserrat", Font.BOLD, 40));
    helpButton.setFont(new Font("Montserrat", Font.BOLD, 40));
    infoButton.setFont(new Font("Montserrat", Font.BOLD, 40));

    // Add the buttons to the component
    super.add(playButton);
    super.add(quitButton);
    super.add(helpButton);
    super.add(infoButton);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    JButton triggered = (JButton) evt.getSource();

    if (triggered == quitButton) {
      // Exit button: close the application
      System.exit(0);
    } else if (triggered == playButton) {
      // Play button: transition from menu to the game state
      OrderFlow.currentState = OrderFlow.STATE.GAME;
      OrderFlow.menuFrame.dispose();
      OrderFlow.gameReset();
      // playBackgroundAmbience(); // Could be re-enabled if desired
    } else if (triggered == helpButton) {
      // Display help info in a dialog box
      JOptionPane.showMessageDialog(
              this,
              "The goal of blackjack is to beat the dealer's hand without going over 21.\n" +
                      "Face cards are worth 10. Aces are worth 1 or 11, whichever is more advantageous.\n" +
                      "Each player starts with two cards; one of the dealer's cards is hidden until the end.\n" +
                      "To 'Hit' is to request another card. To 'Stand' is to end your turn.\n" +
                      "If you go over 21, you bust and lose immediately.\n" +
                      "A starting 21 (Ace & 10-value card) is a blackjack.",
              "QUICK&EASY BLACKJACK HELP",
              JOptionPane.INFORMATION_MESSAGE
      );
    } else if (triggered == infoButton) {
      // Display info about the program
      JOptionPane.showMessageDialog(
              this,
              "This project was completed as a final assignment for Advanced Programming\n" +
                      "by Ongun Uzay Macar in January 2016 under the guidance of Cengiz Agalar.",
              "INFORMATION",
              JOptionPane.INFORMATION_MESSAGE
      );
    }
  }
}