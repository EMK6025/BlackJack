import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AllActions {
  public static ArrayList<Card> dealerCards = new ArrayList<Card>();
  public static ArrayList<Card> playerCards = new ArrayList<Card>();
  public static boolean wagerPlaced = false;
  public static int i = 0;

  public static Deck cardDeck = new Deck();

  public AllActions() {
    cardDeck = new Deck();
    cardDeck.mixDeck();
  }
  public static void initiateGame() throws InterruptedException{
    cardDeck.mixDeck();
    GameDisplay.cardHidden = true;
    i = 0;
    Timer timer = new Timer(750, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (i++ < 4) {
          if (i % 2 == 1) {
            appendCard(dealerCards); // Add to dealer's cards
          } else {
            appendCard(playerCards); // Add to player's cards
          }
          OrderFlow.gameFrame.repaint(); // Repaint UI
        } else {
          ((Timer) e.getSource()).stop();
        }
      }
    });
    timer.start();
  }

  public static boolean evaluateHand(ArrayList<Card> hand) {
    if (hand.equals(playerCards)) {
      if (computeHandSum(hand) > 21) {
        GameDisplay.cardHidden = false;
        OrderFlow.gameFrame.repaint();
        GameDisplay.outcome = 0;
        JOptionPane.showMessageDialog(null, "PLAYER BUSTED! DEALER WINS!");
        OrderFlow.gameReset();
        return true;
      }
      return false;
    } else {
      if (computeHandSum(hand) > 21) {
        GameDisplay.cardHidden = false;
        OrderFlow.gameFrame.repaint();
        GameDisplay.outcome = 1;
        JOptionPane.showMessageDialog(null, "DEALER BUSTED! PLAYER WINS!");
        OrderFlow.gameReset();
        return true;
      }
      return false;
    }
  }

  private static void determineOutcome() {
    if (OrderFlow.currentBet == 0){
      return;
    }
    int dealerSum = computeHandSum(dealerCards);
    int playerSum = computeHandSum(playerCards);

    // Determine the outcome after revealing the card
    if ((dealerSum <= 21) && (playerSum <= 21)) {
      if (playerSum > dealerSum) {
        GameDisplay.outcome = 1;
        JOptionPane.showMessageDialog(null, "PLAYER HAS WON BECAUSE OF A BETTER HAND!");
      } else if (playerSum == dealerSum) {
        GameDisplay.outcome = 2;
        JOptionPane.showMessageDialog(null, "PUSH! Both have the same hand!");
      } else {
        GameDisplay.outcome = 0;
        JOptionPane.showMessageDialog(null, "DEALER HAS WON BECAUSE OF A BETTER HAND!");
      }
    }
    OrderFlow.gameReset();
  }



  public static void appendCard(ArrayList<Card> hand) {
    hand.add(cardDeck.fetchCard(0));
    cardDeck.extractCard(0);
  }

  public static boolean aceInSet(ArrayList<Card> hand) {
    for (Card c : hand) {
      if (c.getValue() == 11) return true;
    }
    return false;
  }

  public static int countAces(ArrayList<Card> hand) {
    int aceTotal = 0;
    for (Card c : hand) {
      if (c.getValue() == 11) {
        aceTotal++;
      }
    }
    return aceTotal;
  }

  public static int sumConsideringAcesHigh(ArrayList<Card> hand) {
    int total = 0;
    for (Card c : hand) {
      total += c.getValue();
    }
    return total;
  }

  public static int computeHandSum(ArrayList<Card> hand) {
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

  public static void exit(){
    JOptionPane.showMessageDialog(null, "You have left the casino with " + OrderFlow.currentBalance + ".");
    System.exit(0);
  }

  public static void hit(){
    if (OrderFlow.currentBet == 0) {
      JOptionPane.showMessageDialog(null, "PLACE A BET FIRST!");
      return;
    }
    appendCard(playerCards);
    evaluateHand(playerCards);
    OrderFlow.gameFrame.repaint();
  }

  public static void doubleDown(){
    if (OrderFlow.currentBet == 0) {
      JOptionPane.showMessageDialog(null, "PLACE A BET FIRST!");
      return;
    }
    OrderFlow.currentBalance -= OrderFlow.currentBet;
    OrderFlow.currentBet += OrderFlow.currentBet;
    appendCard(playerCards);
    evaluateHand(playerCards);
    OrderFlow.gameFrame.repaint();
    // Simulate a stand after doubling down
    stand();
  }

  public static void stand(){
    if (OrderFlow.currentBet == 0) {
      JOptionPane.showMessageDialog(null, "PLACE A BET FIRST!");
      return;
    }

    GameDisplay.cardHidden = false;
    OrderFlow.gameFrame.repaint();

    // Use a timer to deal dealer cards with a delay
    Timer dealerDrawTimer = new Timer(1000, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (computeHandSum(dealerCards) < 17) {
          appendCard(dealerCards);
          if (evaluateHand(dealerCards)) {
            OrderFlow.gameFrame.repaint();
            ((Timer) e.getSource()).stop();
            determineOutcome();
          }
          OrderFlow.gameFrame.repaint();
        } else {
          ((Timer) e.getSource()).stop();
          determineOutcome();
        }
      }
    });
    dealerDrawTimer.start();
  }

  public static void mousePressed(int clickX, int clickY){
    if (clickX >= 50 && clickX <= 200 && clickY >= 300 && clickY <= 450) {
      AllActions.wagerPlaced = true;
      String[] betOptions = {"1", "5", "10", "25", "100"};
      int choice = JOptionPane.showOptionDialog(
              null,
              "Please enter your betting amount:",
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
        AllActions.initiateGame(); // Begin the round after placing a bet
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }

}