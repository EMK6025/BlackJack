import javax.swing.*;
import java.awt.*;

public class OrderFlow {

  public static JFrame Frame = new JFrame(); // Frame for the initial menu
  public static JFrame gameFrame = new JFrame(); // Frame for the game menu

  public static int currentBet = 0;
  public static int currentBalance = 1000;
  public static GameDisplay primaryVisuals;

  public static void main(String[] args) throws InterruptedException {
    SwingUtilities.invokeLater(OrderFlow::openMenu);
  }

  public static void openMenu() {
    Frame.setTitle("BLACKJACK!");
    Frame.setSize(1130, 665);
    Frame.setLocationRelativeTo(null);
    Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Frame.setResizable(false);

    MenuDisplay beginningComponent = new MenuDisplay();
    Frame.add(beginningComponent);
    Frame.setVisible(true);
  }

  public static void gameReset() {
    if (GameDisplay.outcome == 0) {

      GameDisplay.dealerWinsCount++;
    } else if (GameDisplay.outcome == 1) {

      GameDisplay.playerWinsCount++;
      currentBalance += currentBet * 2;
    } else {
      
      currentBalance += currentBet;
    }
    currentBet = 0;
    AllActions.dealerCards.clear();
    AllActions.playerCards.clear();
    primaryVisuals.repaint();
  }

  public static void gameStart() {
    primaryVisuals = new GameDisplay();
    primaryVisuals.setPreferredSize(new Dimension(1130, 665));
    primaryVisuals.addMouseListener(primaryVisuals);
    gameFrame.setLayout(new BorderLayout());
    gameFrame.add(primaryVisuals, BorderLayout.CENTER);

    gameFrame.setTitle("BLACKJACK!");
    gameFrame.setSize(1130, 665);
    gameFrame.setLocationRelativeTo(null);
    gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    gameFrame.setResizable(false);

    GameDisplay.cardHidden = true;
    GameDisplay.outcome = -1;

    gameFrame.setVisible(true);  // Show the frame
  }

}