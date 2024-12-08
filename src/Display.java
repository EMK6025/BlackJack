import javax.swing.JComponent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JOptionPane;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Display extends JComponent implements MouseListener {

  // Visual assets for the game layout
  private BufferedImage tableImage;
  private BufferedImage gameLogo;
  private BufferedImage betChip;

  // Data structures for the dealer's and player's card sets
  private ArrayList<Card> dealerCards;
  private ArrayList<Card> playerCards;

  // Tracking how many times dealer and player have won
  private int dealerWinsCount;
  private int playerWinsCount;

  // Flags and player info
  public boolean cardHidden = true;
  public static boolean wagerPlaced = false;
  private int playerBalance;
  public static int playerBet;

  /**
   * Constructor for the component that takes the dealer and player hands.
   * @param dealerHand The dealer's card collection.
   * @param playerHand The player's card collection.
   */
  public Display(ArrayList<Card> dealerHand, ArrayList<Card> playerHand) {
    this.dealerCards = dealerHand;
    this.playerCards = playerHand;
    this.dealerWinsCount = 0;
    this.playerWinsCount = 0;
    this.playerBalance = 1000; // Starting funds
    addMouseListener(this);
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
    g2.drawString("To begin a round, click the chip below to place a bet.", 50, 250);
    g2.drawString("Experience is best with sound enabled!", 50, 270);

    g2.drawString("The best gaming experience is when", 830, 550);
    g2.drawString("you play with sound on!", 830, 570);

    g2.setFont(new Font("Montserrat", Font.BOLD, 20));
    g2.drawString("CURRENT BALANCE: " + playerBalance, 50, 570);

    // Show the current time in HH:mm:ss format at the top-right corner
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    g2.drawString(timeFormat.format(cal.getTime()), 1020, 20);

    // Render the dealer's cards
    try {
      for (int i = 0; i < dealerCards.size(); i++) {
        if (i == 0 && cardHidden) {
          // The dealer's first card can be hidden if conditions apply
          dealerCards.get(i).renderCard(g2, true, true, i);
        } else {
          dealerCards.get(i).renderCard(g2, true, false, i);
        }
      }
    } catch (IOException e) {
      // If there's an error drawing cards, do nothing special here
    }

    // Render the player's cards
    try {
      for (int i = 0; i < playerCards.size(); i++) {
        playerCards.get(i).renderCard(g2, false, false, i);
      }
    } catch (IOException e) {
      // If there's an error drawing cards, do nothing special here
    }
  }

  /**
   * Update and redraw the component with new status info.
   * @param balance Updated player balance.
   * @param playerWins Updated player win count.
   * @param dealerWins Updated dealer win count.
   * @param hideDealerCard Whether the dealer's first card is hidden or not.
   */
  public void updateDisplay(int balance, int playerWins, int dealerWins, boolean hideDealerCard) {
    playerBalance = balance;
    playerWinsCount = playerWins;
    dealerWinsCount = dealerWins;
    cardHidden = hideDealerCard;
    this.repaint();
  }

  /**
   * Handle mouse presses (e.g., when the player clicks the chip to place a bet).
   */
  public void mousePressed(MouseEvent e) {
    int clickX = e.getX();
    int clickY = e.getY();

    // Check if click coordinates are within the chip's area
    if (clickX >= 50 && clickX <= 200 && clickY >= 300 && clickY <= 450) {
      wagerPlaced = true;
      String[] betOptions = {"1", "5", "10", "25", "100"};
      int choice = JOptionPane.showOptionDialog(
              null,
              "Please enter your betting amount!",
              "BETTING",
              JOptionPane.DEFAULT_OPTION,
              JOptionPane.PLAIN_MESSAGE,
              null,
              betOptions,
              betOptions[0]
      );

      // Assign bets according to user's choice or default to 1 if no choice is made
      playerBet = choice;
      playerBalance -= choice;
      this.repaint();
      Runner.newGame.initiateGame(); // Begin the round after placing a bet
    }
  }

  // The following MouseListener methods are unused but must be included
  public void mouseExited(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}
  public void mouseClicked(MouseEvent e) {}
}