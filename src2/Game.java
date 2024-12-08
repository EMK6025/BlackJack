import java.util.ArrayList;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Game {

  // Collections representing dealer's and player's hands
  private ArrayList<Card> dealerSet;
  private ArrayList<Card> playerSet;

  // Control flags indicating states in the game
  public boolean cardFaceDown;
  public boolean dealerVictory;
  public volatile boolean matchEnded;
  // Volatile ensures visibility of changes across threads

  // Swing components and game objects
  JFrame mainFrame;
  Deck cardDeck;
  GameComponent primaryVisuals;
  GameComponent cardDisplay;

  // UI Buttons
  private JButton buttonHit;
  private JButton buttonStand;
  private JButton buttonDouble;
  private JButton buttonExit;

  /**
   * Constructor that sets up key objects for the game.
   *
   * @param frame  The JFrame that the game will use for display.
   */
  public Game(JFrame frame) {
    cardDeck = new Deck();
    cardDeck.mixDeck();  // Shuffle the deck at the start
    dealerSet = new ArrayList<Card>();
    playerSet = new ArrayList<Card>();

    primaryVisuals = new GameComponent(dealerSet, playerSet);
    mainFrame = frame;

    cardFaceDown = true;
    dealerVictory = true;
    matchEnded = false;
  }

  /**
   * Set up the main game interface: window title, size, buttons, etc.
   */
  public void buildGameInterface() {
    System.out.println("INTERFACE BUILT");
    mainFrame.setTitle("BLACKJACK!");
    mainFrame.setSize(1130, 665);
    mainFrame.setLocationRelativeTo(null);
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setResizable(false);

    // Create and style buttons
    buttonHit = new JButton("HIT");
    buttonHit.setBounds(390, 550, 100, 50);
    buttonHit.setFont(new Font("Comic Sans MS", Font.BOLD, 16));

    buttonStand = new JButton("STAND");
    buttonStand.setBounds(520, 550, 100, 50);
    buttonStand.setFont(new Font("Comic Sans MS", Font.BOLD, 16));

    buttonDouble = new JButton("DOUBLE");
    buttonDouble.setBounds(650, 550, 100, 50);
    buttonDouble.setFont(new Font("Comic Sans MS", Font.BOLD, 16));

    buttonExit = new JButton("EXIT CASINO");
    buttonExit.setBounds(930, 240, 190, 50);
    buttonExit.setFont(new Font("Comic Sans MS", Font.BOLD, 16));

    // Add buttons to the frame
    mainFrame.add(buttonHit);
    mainFrame.add(buttonStand);
    mainFrame.add(buttonDouble);
    mainFrame.add(buttonExit);

    // Exit button listener
    buttonExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(mainFrame, "You have left the casino with " + Tester.currentBalance + ".");
        System.exit(0);
      }
    });

    // Add the general visual component that sets up the atmosphere
    primaryVisuals = new GameComponent(dealerSet, playerSet);
    primaryVisuals.setBounds(0, 0, 1130, 665);
    mainFrame.add(primaryVisuals);
    mainFrame.setVisible(true);
  }

  /**
   * Begin the game: initial card distribution and checks.
   */
  public void initiateGame() {
    // Initial dealing: first two cards to dealer, next two cards to player
    for (int i = 0; i < 2; i++) {
      dealerSet.add(cardDeck.fetchCard(i));
    }
    for (int i = 2; i < 4; i++) {
      playerSet.add(cardDeck.fetchCard(i));
    }

    // Remove these four cards from the deck
    for (int i = 0; i < 4; i++) {
      cardDeck.extractCard(0);
    }

    // Add component for card visuals
    cardDisplay = new GameComponent(dealerSet, playerSet);
    cardDisplay.setBounds(0, 0, 1130, 665);
    mainFrame.add(cardDisplay);
    mainFrame.setVisible(true);

    // Initial checks for blackjack or bust scenarios
    evaluateHand(dealerSet);
    evaluateHand(playerSet);

    // Button listeners
    buttonHit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        playDealSound();
        appendCard(playerSet);
        evaluateHand(playerSet);

        // If needed, dealer also draws
        if ((computeHandSum(playerSet) < 17) && (computeHandSum(dealerSet) < 17)) {
          appendCard(dealerSet);
          evaluateHand(dealerSet);
        }
      }
    });

    buttonDouble.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        playDealSound();
        appendCard(playerSet);
        evaluateHand(playerSet);
        // Simulate a stand after doubling down
        buttonStand.doClick();

        if (computeHandSum(dealerSet) < 17) {
          evaluateHand(dealerSet);
          appendCard(dealerSet);
        }
      }
    });

    buttonStand.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // Dealer draws until total >=17
        while (computeHandSum(dealerSet) < 17) {
          appendCard(dealerSet);
          evaluateHand(dealerSet);
        }

        // Determine winner if no bust and no blackjack
        if ((computeHandSum(dealerSet) < 21) && (computeHandSum(playerSet) < 21)) {
          if (computeHandSum(playerSet) > computeHandSum(dealerSet)) {
            cardFaceDown = false;
            dealerVictory = false;
            JOptionPane.showMessageDialog(mainFrame, "PLAYER HAS WON BECAUSE OF A BETTER HAND!");
            pauseGame();
            matchEnded = true;
          } else if (computeHandSum(playerSet) == computeHandSum(dealerSet)) {
            cardFaceDown = false;
            dealerVictory = false;
            JOptionPane.showMessageDialog(mainFrame, "PUSH! Both the dealer and player have the same hand!");
            pauseGame();
            matchEnded = true;
          } else {
            cardFaceDown = false;
            JOptionPane.showMessageDialog(mainFrame, "DEALER HAS WON BECAUSE OF A BETTER HAND!");
            pauseGame();
            matchEnded = true;
          }
        }
      }
    });
  }

  /**
   * Evaluate a hand for blackjack or bust conditions.
   */
  public void evaluateHand(ArrayList<Card> hand) {
    if (hand.equals(playerSet)) {
      if (computeHandSum(hand) == 21) {
        cardFaceDown = false;
        dealerVictory = false;
        JOptionPane.showMessageDialog(mainFrame, "PLAYER BLACKJACK! PLAYER WINS!");
        pauseGame();
        matchEnded = true;
      } else if (computeHandSum(hand) > 21) {
        cardFaceDown = false;
        JOptionPane.showMessageDialog(mainFrame, "PLAYER BUSTED! DEALER WINS!");
        pauseGame();
        matchEnded = true;
      }
    } else {
      if (computeHandSum(hand) == 21) {
        cardFaceDown = false;
        JOptionPane.showMessageDialog(mainFrame, "DEALER BLACKJACK! DEALER WINS!");
        pauseGame();
        matchEnded = true;
      } else if (computeHandSum(hand) > 21) {
        cardFaceDown = false;
        dealerVictory = false;
        JOptionPane.showMessageDialog(mainFrame, "DEALER BUSTED! PLAYER WINS!");
        pauseGame();
        matchEnded = true;
      }
    }
  }

  /**
   * Append a new card from the deck to the specified hand.
   */
  public void appendCard(ArrayList<Card> hand) {
    hand.add(cardDeck.fetchCard(0));
    cardDeck.extractCard(0);
    cardFaceDown = true;
  }

  /**
   * Check if the given hand contains any ace valued as 11.
   */
  public boolean aceInSet(ArrayList<Card> hand) {
    for (Card c : hand) {
      if (c.getValue() == 11) {
        return true;
      }
    }
    return false;
  }

  /**
   * Count how many aces (value=11) are present in the given hand.
   */
  public int countAces(ArrayList<Card> hand) {
    int aceTotal = 0;
    for (Card c : hand) {
      if (c.getValue() == 11) {
        aceTotal++;
      }
    }
    return aceTotal;
  }

  /**
   * Compute the sum of the hand's values, treating all aces as high (value=11).
   */
  public int sumConsideringAcesHigh(ArrayList<Card> hand) {
    int total = 0;
    for (Card c : hand) {
      total += c.getValue();
    }
    return total;
  }

  /**
   * Compute the best possible sum of the given hand under Blackjack rules.
   */
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

  /**
   * Small utility to pause game logic flow.
   */
  public static void pauseGame() {
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {}
  }

  /**
   * Play a sound effect representing card drawing.
   */
  public static void playDealSound() {
    try {
      InputStream in = new FileInputStream("sounds/cardDraw.wav");
      // Audio logic commented out as original code
      // AudioStream audio = new AudioStream(in);
      // AudioPlayer.player.start(audio);
    } catch (IOException e) {}
  }
}