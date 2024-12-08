import java.util.ArrayList;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Font;
import java.util.concurrent.TimeUnit;

public class AllActions implements MouseListener{

  // Collections representing dealer's and player's hands
  public static ArrayList<Card> dealerCards;
  public static ArrayList<Card> playerCards;
  // Control flags indicating states in the game
  public static boolean cardFaceDown;
  public int outcome;
  public static boolean wagerPlaced = false;
  // Swing components and game objects
  JFrame mainFrame;
  Deck cardDeck;
  Display primaryVisuals;
  Display cardDisplay;

  // UI Buttons
  public JButton buttonHit;
  public JButton buttonStand;
  public JButton buttonDouble;
  public JButton buttonExit;

  public AllActions(JFrame frame) {
    cardDeck = new Deck();
    cardDeck.mixDeck();  // Shuffle the deck at the start
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

    // Button listeners
    buttonExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(mainFrame, "You have left the casino with " + OrderFlow.currentBalance + ".");
        System.exit(0);
      }
    });

    buttonHit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        appendCard(playerCards);
        evaluateHand(playerCards);
        mainFrame.repaint();
      }

    });

    buttonDouble.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        appendCard(playerCards);
        // double bet
        evaluateHand(playerCards);
        mainFrame.repaint();
        // Simulate a stand after doubling down
        buttonStand.doClick();
      }
    });

    buttonStand.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // Dealer draws until total >=17
        while (computeHandSum(dealerCards) < 17) {
          appendCard(dealerCards);
          evaluateHand(dealerCards);
          try {
            TimeUnit.SECONDS.sleep(1);
          } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
          }
          primaryVisuals.updateDisplay(OrderFlow.currentBalance, Display.playerWinsCount, Display.dealerWinsCount - 1, cardFaceDown);
        }
        // Determine winner if no bust and no blackjack
        if ((computeHandSum(dealerCards) < 21) && (computeHandSum(playerCards) < 21)) {
          if (computeHandSum(playerCards) > computeHandSum(dealerCards)) {
            cardFaceDown = false;
            outcome = 0;
            JOptionPane.showMessageDialog(mainFrame, "PLAYER HAS WON BECAUSE OF A BETTER HAND!");
            OrderFlow.gameReset();
          } else if (computeHandSum(playerCards) == computeHandSum(dealerCards)) {
            cardFaceDown = false;
            outcome = 2;
            JOptionPane.showMessageDialog(mainFrame, "PUSH! Both the dealer and player have the same hand!");
            OrderFlow.gameReset();
          } else {
            cardFaceDown = false;
            JOptionPane.showMessageDialog(mainFrame, "DEALER HAS WON BECAUSE OF A BETTER HAND!");
            OrderFlow.gameReset();
          }
        }
      }
    });
  }

  public void buildGameInterface() {
    System.out.println("INTERFACE BUILT");
    mainFrame.setTitle("BLACKJACK!");
    mainFrame.setSize(1130, 665);
    mainFrame.setLocationRelativeTo(null);
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setResizable(false);

    // Create and style buttons
    buttonHit.setBounds(390, 550, 100, 50);
    buttonHit.setFont(new Font("Comic Sans MS", Font.BOLD, 16));

    buttonStand.setBounds(520, 550, 100, 50);
    buttonStand.setFont(new Font("Comic Sans MS", Font.BOLD, 16));

    buttonDouble.setBounds(650, 550, 100, 50);
    buttonDouble.setFont(new Font("Comic Sans MS", Font.BOLD, 16));

    buttonExit.setBounds(930, 240, 190, 50);
    buttonExit.setFont(new Font("Comic Sans MS", Font.BOLD, 16));

    // Add buttons to the frame
    mainFrame.add(buttonHit);
    mainFrame.add(buttonStand);
    mainFrame.add(buttonDouble);
    mainFrame.add(buttonExit);

    // Add the general visual component that sets up the atmosphere
    primaryVisuals = new Display();
    primaryVisuals.setBounds(0, 0, 1130, 665);
    mainFrame.add(primaryVisuals);
    mainFrame.setVisible(true);
  }

  public void initiateGame() {
    // Initial dealing: first two cards to dealer, next two cards to player
    cardFaceDown = true;
    appendCard(dealerCards);
    appendCard(playerCards);
    appendCard(dealerCards);
    appendCard(playerCards);

    // Add component for card visuals
    cardDisplay = new Display(dealerCards, playerCards);
    cardDisplay.setBounds(0, 0, 1130, 665);
    mainFrame.add(cardDisplay);
    mainFrame.setVisible(true);

    // Initial checks for blackjack or bust scenarios
    evaluateHand(dealerCards);
    evaluateHand(playerCards);
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
      if (c.getValue() == 11) {
        return true;
      }
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
    return 22; // If no other return was valid, it's a bust scenario
  }

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
      int val = Integer.parseInt(betOptions[choice]);
      OrderFlow.currentBet = val;
      OrderFlow.currentBalance -= val;
      OrderFlow.newGame.initiateGame(); // Begin the round after placing a bet
    }
  }

  // The following MouseListener methods are unused but must be included
  public void mouseExited(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}
  public void mouseClicked(MouseEvent e) {}
}