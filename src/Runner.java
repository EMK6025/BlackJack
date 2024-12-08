import javax.swing.JFrame;

public class Runner {

  public static JFrame menuFrame = new JFrame(); // Frame for the initial menu
  public static JFrame gameFrame = new JFrame(); // Frame for the actual game

  private static int playerScore = 0;
  private static int dealerScore = 0;
  public static int currentBalance = 1000;

  public static OrderFlow newGame = new OrderFlow(gameFrame); // Controls the blackjack game
  private static boolean isFirstTime = true;

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

  public static Thread gameCheckThread = new Thread() {
    public void run() {
      while (true) {
        if (isFirstTime || newGame.matchEnded) {
          System.out.println("Lets refresh the game!");
          if (newGame.dealerVictory) {
            // Dealer wins
            dealerScore++;
          } else {
            // Player wins
            playerScore++;
            currentBalance += Display.playerBet * 2;
          }
          newGame.primaryVisuals.updateDisplay(currentBalance, playerScore, dealerScore - 1, newGame.cardFaceDown);
          gameFrame.getContentPane().removeAll();
          newGame = new OrderFlow(gameFrame);
          newGame.buildGameInterface(); // Updated from formGame() to buildGameInterface()

          isFirstTime = false;
        }
      }
    }
  };
}