import javax.swing.JFrame;

public class OrderFlow {

  public static JFrame menuFrame = new JFrame(); // Frame for the initial menu
  public static JFrame gameFrame = new JFrame(); // Frame for the actual game

  private static int playerScore = 0;
  private static int dealerScore = 0;
  public static int currentBet = 0;
  public static int currentBalance = 1000;

  public static AllActions newGame = new AllActions(gameFrame); // Controls the blackjack game

  public static enum STATE {
    MENU,
    GAME
  };

  public static STATE currentState = STATE.MENU;

  public static void main(String[] args) throws InterruptedException {
    if (currentState == STATE.MENU) {
      openMenu();
    }
  }

  public static void openMenu() {
    menuFrame.setTitle("BLACKJACK!");
    menuFrame.setSize(1130, 665);
    menuFrame.setLocationRelativeTo(null);
    menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    menuFrame.setResizable(false);

    OptionsComponent beginningComponent = new OptionsComponent();
    menuFrame.add(beginningComponent);
    menuFrame.setVisible(true);
  }

  public static void gameReset() {
    if (newGame.outcome == 1) {
      // Dealer wins
      dealerScore++;
    } else if (newGame.outcome == 0) {
      // Player wins
      playerScore++;
      currentBalance += currentBet * 2;
    } else {
      // Push
      currentBalance += currentBet;
    }
    currentBet = 0;
    newGame.primaryVisuals.updateDisplay(currentBalance, playerScore, dealerScore - 1, AllActions.cardHidden);
    gameFrame.getContentPane().removeAll();
    newGame = new AllActions(gameFrame);
    newGame.buildGameInterface(); // Updated from formGame() to buildGameInterface()

  }
}