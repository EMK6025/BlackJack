import javax.swing.JComponent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JOptionPane;
import java.io.*;

public class Display extends JComponent{

  // Visual assets for the game layout
  public BufferedImage tableImage;
  public BufferedImage gameLogo;
  public BufferedImage betChip;

  // Tracking how many times dealer and player have won
  public static int dealerWinsCount = 0;
  public static int playerWinsCount = 0;

  public boolean cardHidden = true;

  public Display() {
    addMouseListener(OrderFlow.newGame);
  }

  public Display(ArrayList<Card> dealerSet, ArrayList<Card> playerSet) {
  }

  /**
   * Rendering the entire visual state (background, logos, cards, etc.)
   */
  public void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;

    // Load images for the table, logo, and chip
    try {
      tableImage = ImageIO.read(new File("images/background.jpg"));
      gameLogo = ImageIO.read(new File("images/blackjackLogo.png"));
      betChip = ImageIO.read(new File("images/chip.png"));
    } catch (IOException e) {
      // If images can't be loaded, no special handling here
    }

    // Draw the main scene elements
    g2.drawImage(tableImage, 0, 0, null);
    g2.drawImage(gameLogo, 510, 400, null);
    g2.drawImage(betChip, 50, 300, null);

    // Set text color and fonts, then draw labels and status information
    g2.setColor(Color.WHITE);
    g2.setFont(new Font("Montserrat", Font.BOLD, 30));
    g2.drawString("DEALER", 515, 50);
    g2.drawString("PLAYER", 515, 380);

    g2.drawString("DEALER WON: ", 50, 100);
    g2.drawString(Integer.toString(dealerWinsCount), 300, 100);

    g2.drawString("PLAYER WON: ", 50, 150);
    g2.drawString(Integer.toString(playerWinsCount), 300, 150);

    g2.setFont(new Font("Montserrat", Font.BOLD, 15));

    g2.setFont(new Font("Montserrat", Font.BOLD, 20));
    g2.drawString("CURRENT BALANCE: " + OrderFlow.currentBalance, 50, 570);

    // Render the dealer's cards
    try {
      for (int i = 0; i < AllActions.dealerCards.size(); i++) {
        if (i == 0 && cardHidden) {
          // The dealer's first card can be hidden if conditions apply
          AllActions.dealerCards.get(i).renderCard(g2, true, false, i);
        } else {
          AllActions.dealerCards.get(i).renderCard(g2, true, false, i);
        }
      }
    } catch (IOException e) {
      // If there's an error drawing cards, do nothing special here
    }

    // Render the player's cards
    try {
      for (int i = 0; i < AllActions.playerCards.size(); i++) {
        AllActions.playerCards.get(i).renderCard(g2, false, false, i);
      }
    } catch (IOException e) {
      // If there's an error drawing cards, do nothing special here
    }
  }

  public void updateDisplay(int balance, int playerWins, int dealerWins, boolean hideDealerCard) {
    OrderFlow.currentBalance = balance;
    playerWinsCount = playerWins;
    dealerWinsCount = dealerWins;
    cardHidden = hideDealerCard;
    this.repaint();
  }
}