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

    // Apply consistent styling to buttons
    styleButton(playButton, new Color(34, 139, 34), Color.WHITE);
    styleButton(quitButton, new Color(178, 34, 34), Color.WHITE);
    styleButton(helpButton, new Color(70, 130, 180), Color.WHITE);
    styleButton(infoButton, new Color(128, 128, 128), Color.WHITE);

    setLayout(null);

    // Position and add the buttons
    // We'll arrange them in a column, centered horizontally.
    int buttonWidth = 200;
    int buttonHeight = 60;
    int centerX = 1130 / 2 - buttonWidth / 2; // Frame width is 1130
    int startY = 260; // Starting vertical position

    playButton.setBounds(centerX, startY, buttonWidth, buttonHeight);
    helpButton.setBounds(centerX, startY + 80, buttonWidth, buttonHeight);
    infoButton.setBounds(centerX, startY + 160, buttonWidth, buttonHeight);
    quitButton.setBounds(centerX, startY + 240, buttonWidth, buttonHeight);

    add(playButton);
    add(helpButton);
    add(infoButton);
    add(quitButton);
  }

  private void styleButton(JButton button, Color bg, Color fg) {
    button.setFont(new Font("Montserrat", Font.BOLD, 30));
    button.setFocusPainted(false);
    button.setBackground(bg);
    button.setForeground(fg);
    button.setOpaque(true);
    button.setBorderPainted(false);
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g.create();

    // Attempt to load the background image
    try {
      tableBackground = ImageIO.read(new File("images/background.jpg"));
    } catch (IOException e) {
      // If no image is found, fill with a solid color
      g2.setColor(Color.DARK_GRAY);
      g2.fillRect(0, 0, getWidth(), getHeight());
    }

    // Draw background if available
    if (tableBackground != null) {
      g2.drawImage(tableBackground, 0, 0, getWidth(), getHeight(), null);
    }

    // Prepare fonts and colors
    String title = "BlackJack!";
    String subtitle = "This game is brought to you by CompSci 221";
    Font titleFont = new Font("Montserrat", Font.BOLD, 100);
    Font subtitleFont = new Font("Arial", Font.BOLD, 25);

    // Center the title and subtitle
    FontMetrics fmTitle = g2.getFontMetrics(titleFont);
    FontMetrics fmSub = g2.getFontMetrics(subtitleFont);

    int titleWidth = fmTitle.stringWidth(title);
    int subtitleWidth = fmSub.stringWidth(subtitle);

    int centerX = getWidth() / 2;
    int titleX = centerX - titleWidth / 2;
    int titleY = 150; // Slightly lower so it’s not at the top edge

    int subtitleX = centerX - subtitleWidth / 2;
    int subtitleY = 200;

    // Draw a semi-transparent overlay behind the text for better readability
    g2.setColor(new Color(0, 0, 0, 120));
    int overlayWidth = Math.max(titleWidth, subtitleWidth) + 40;
    int overlayHeight = (subtitleY - titleY) + 80;
    int overlayX = centerX - (overlayWidth / 2);
    int overlayY = titleY - fmTitle.getAscent() - 20;
    g2.fillRect(overlayX, overlayY, overlayWidth, overlayHeight);

    // Draw title
    g2.setColor(Color.WHITE);
    g2.setFont(titleFont);
    g2.drawString(title, titleX, titleY);

    // Draw subtitle
    g2.setFont(subtitleFont);
    g2.drawString(subtitle, subtitleX, subtitleY);

    g2.dispose();
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
    } else if (triggered == helpButton) {
      // Display help info in a dialog box
      JOptionPane.showMessageDialog(
              this,
              "The goal of blackjack is to get as close to 21 as possible without going over.\n" +
                      "Face cards are worth 10. Aces are 1 or 11, whichever benefits you.\n" +
                      "You start with two cards, and one of the dealer's cards is hidden until the end.\n" +
                      "Click 'Hit' to draw another card, or 'Stand' to hold your total.\n" +
                      "If you exceed 21, you bust and lose immediately.\n" +
                      "A starting 21 (Ace & 10-value card) is a natural blackjack!",
              "BLACKJACK HELP",
              JOptionPane.INFORMATION_MESSAGE
      );
    } else if (triggered == infoButton) {
      // Display info about the program
      JOptionPane.showMessageDialog(
              this,
              "This project is the semester project for CMPSC 221\n" +
                      "by Ethan Kuo, Lakshay Kalra, and Chukwubikem David Dara.\n" +
                      "⚠DISCLAIMER: Gambling is HARAM, Stay Safe!",
              "INFORMATION",
              JOptionPane.INFORMATION_MESSAGE
      );
    }
  }
}