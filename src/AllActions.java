import java.util.ArrayList;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Font;
import java.util.concurrent.TimeUnit;

public class AllActions implements MouseListener {

  public static ArrayList<Card> dealerCards;
  public static ArrayList<Card> playerCards;
  public static boolean cardFaceDown;
  public int outcome;
  public static boolean wagerPlaced = false;

  JFrame mainFrame;
  Deck cardDeck;
  Display primaryVisuals;
  Display cardDisplay;

  public JButton buttonHit;
  public JButton buttonStand;
  public JButton buttonDouble;
  public JButton buttonExit;

  // This will store which cards to deal and in what order
  private ArrayList<ArrayList<Card>> dealingSequence;

  public AllActions(JFrame frame) {
    cardDeck = new Deck();
    cardDeck.mixDeck();
    dealerCards = new ArrayList<Card>();
    playerCards = new ArrayList<Card>();

    buttonHit = new JButton("HIT");
    buttonStand = new JButton("STAND");
    buttonDouble = new JButton("DOUBLE");
    buttonExit = new JButton("EXIT CASINO");

    primaryVisuals = new Display();
    mainFrame = frame;

    cardFaceDown = true;
    outcome = 1;

    buttonExit.addActionListener(e -> {
      JOptionPane.showMessageDialog(mainFrame, "You have left the casino with " + OrderFlow.currentBalance + ".");
      System.exit(0);
    });

    buttonHit.addActionListener(e -> {
      if (OrderFlow.currentBet == 0) {
        JOptionPane.showMessageDialog(mainFrame, "PLACE A BET FIRST!");
        return;
      }
      appendCard(playerCards);
      evaluateHand(playerCards);
      primaryVisuals.updateDisplay(OrderFlow.currentBalance, Display.playerWinsCount, Display.dealerWinsCount - 1, cardFaceDown);
    });

    buttonDouble.addActionListener(e -> {
      if (OrderFlow.currentBet == 0) {
        JOptionPane.showMessageDialog(mainFrame, "PLACE A BET FIRST!");
        return;
      }

      appendCard(playerCards);
      // double bet logic here if needed
      evaluateHand(playerCards);
      primaryVisuals.updateDisplay(OrderFlow.currentBalance, Display.playerWinsCount, Display.dealerWinsCount - 1, cardFaceDown);
      // Simulate a stand after doubling down
      buttonStand.doClick();
    });

    buttonStand.addActionListener(e -> {
      if (OrderFlow.currentBet == 0) {
        JOptionPane.showMessageDialog(mainFrame, "PLACE A BET FIRST!");
        return;
      }

      // Keep the card faceDown until the dealer finishes drawing
      cardFaceDown = true;

      // Use a timer to deal dealer cards with a delay
      Timer dealerDrawTimer = new Timer(1000, null);
      dealerDrawTimer.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
          if (computeHandSum(dealerCards) < 17) {
            appendCard(dealerCards);
            evaluateHand(dealerCards);
            primaryVisuals.repaint();
          } else {
            dealerDrawTimer.stop();

            // All dealer cards drawn, now determine winner
            int dealerSum = computeHandSum(dealerCards);
            int playerSum = computeHandSum(playerCards);

            // Reveal dealer's facedown card
            cardFaceDown = false;
            // Update the display to show the revealed dealer card before showing the result
            primaryVisuals.updateDisplay(OrderFlow.currentBalance, Display.playerWinsCount, Display.dealerWinsCount, cardFaceDown);
            primaryVisuals.repaint();

            // Determine the outcome after revealing the card
            if ((dealerSum < 21) && (playerSum < 21)) {
              if (playerSum > dealerSum) {
                outcome = 0;
                JOptionPane.showMessageDialog(mainFrame, "PLAYER HAS WON BECAUSE OF A BETTER HAND!");
              } else if (playerSum == dealerSum) {
                outcome = 2;
                JOptionPane.showMessageDialog(mainFrame, "PUSH! Both have the same hand!");
              } else {
                JOptionPane.showMessageDialog(mainFrame, "DEALER HAS WON BECAUSE OF A BETTER HAND!");
              }
            }
            // If dealerSum > 21 or playerSum > 21, those cases are handled in evaluateHand().

            // Finally, reset the game after showing the final state
            OrderFlow.gameReset();
          }
        }
      });
      dealerDrawTimer.setInitialDelay(0);
      dealerDrawTimer.start();
    });
  }

  public void buildGameInterface() {
    System.out.println("INTERFACE BUILT");
    mainFrame.setTitle("BLACKJACK!");
    mainFrame.setSize(1130, 665);
    mainFrame.setLocationRelativeTo(null);
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setResizable(false);

    // Adjust button styling as needed
    buttonHit.setBounds(390, 550, 100, 50);
    buttonHit.setFont(new Font("SansSerif", Font.BOLD, 16));

    buttonStand.setBounds(520, 550, 100, 50);
    buttonStand.setFont(new Font("SansSerif", Font.BOLD, 16));

    buttonDouble.setBounds(650, 550, 100, 50);
    buttonDouble.setFont(new Font("SansSerif", Font.BOLD, 16));

    buttonExit.setBounds(930, 240, 190, 50);
    buttonExit.setFont(new Font("SansSerif", Font.BOLD, 16));

    // Add buttons to the frame
    mainFrame.add(buttonHit);
    mainFrame.add(buttonStand);
    mainFrame.add(buttonDouble);
    mainFrame.add(buttonExit);

    primaryVisuals = new Display();
    primaryVisuals.setBounds(0, 0, 1130, 665);
    mainFrame.add(primaryVisuals);
    mainFrame.setVisible(true);
  }

  public void initiateGame() {
    cardFaceDown = true;
    cardDisplay = new Display(dealerCards, playerCards);
    cardDisplay.setBounds(0, 0, 1130, 665);
    mainFrame.add(cardDisplay);
    mainFrame.setVisible(true);

    // Set up a sequence of deals: dealer, player, dealer, player
    dealerCards.clear();
    playerCards.clear();

    // Use a timer to deal the initial four cards one by one
    ArrayList<Runnable> dealSteps = new ArrayList<>();
    dealSteps.add(() -> appendCard(dealerCards));
    dealSteps.add(() -> appendCard(playerCards));
    dealSteps.add(() -> appendCard(dealerCards));
    dealSteps.add(() -> appendCard(playerCards));

    Timer dealTimer = new Timer(1000, null);
    dealTimer.addActionListener(new ActionListener() {
      int stepIndex = 0;
      @Override
      public void actionPerformed(ActionEvent e) {
        if (stepIndex < dealSteps.size()) {
          dealSteps.get(stepIndex).run();
          primaryVisuals.repaint();
          stepIndex++;
        } else {
          dealTimer.stop();
          // After dealing all four cards, evaluate hands
          evaluateHand(dealerCards);
          evaluateHand(playerCards);
        }
      }
    });
    dealTimer.setInitialDelay(0);
    dealTimer.start();
  }

  public void evaluateHand(ArrayList<Card> hand) {
    if (hand.equals(playerCards)) {
      if (computeHandSum(hand) > 21) {
        cardFaceDown = false;
        JOptionPane.showMessageDialog(mainFrame, "PLAYER BUSTED! DEALER WINS!");
        OrderFlow.gameReset();
      }
    } else {
      if (computeHandSum(hand) > 21) {
        cardFaceDown = false;
        outcome = 0;
        JOptionPane.showMessageDialog(mainFrame, "DEALER BUSTED! PLAYER WINS!");
        OrderFlow.gameReset();
      }
    }
  }

  public void appendCard(ArrayList<Card> hand) {
    hand.add(cardDeck.fetchCard(0));
    cardDeck.extractCard(0);
  }

  public boolean aceInSet(ArrayList<Card> hand) {
    for (Card c : hand) {
      if (c.getValue() == 11) return true;
    }
    return false;
  }

  public int countAces(ArrayList<Card> hand) {
    int aceTotal = 0;
    for (Card c : hand) {
      if (c.getValue() == 11) {
        aceTotal++;
      }
    }
    return aceTotal;
  }

  public int sumConsideringAcesHigh(ArrayList<Card> hand) {
    int total = 0;
    for (Card c : hand) {
      total += c.getValue();
    }
    return total;
  }

  public int computeHandSum(ArrayList<Card> hand) {
    if (aceInSet(hand)) {
      if (sumConsideringAcesHigh(hand) <= 21) {
        return sumConsideringAcesHigh(hand);
      } else {
        for (int i = 0; i < countAces(hand); i++) {
          int adjusted = sumConsideringAcesHigh(hand) - (i + 1) * 10;
          if (adjusted <= 21) {
            return adjusted;
          }
        }
      }
    } else {
      int simpleSum = 0;
      for (Card c : hand) {
        simpleSum += c.getValue();
      }
      return simpleSum;
    }
    return 22; // bust scenario
  }

  @Override
  public void mousePressed(MouseEvent e) {
    int clickX = e.getX();
    int clickY = e.getY();

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

      int val = Integer.parseInt(betOptions[choice]);
      OrderFlow.currentBet = val;
      OrderFlow.currentBalance -= val;
      try {
        OrderFlow.newGame.initiateGame(); // Begin the round after placing a bet
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  // Unused MouseListener methods
  @Override public void mouseExited(MouseEvent e) {}
  @Override public void mouseEntered(MouseEvent e) {}
  @Override public void mouseReleased(MouseEvent e) {}
  @Override public void mouseClicked(MouseEvent e) {}
}