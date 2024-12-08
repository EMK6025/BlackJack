import java.util.ArrayList;
import java.util.Collections;

public class Deck {

  // An ArrayList to hold all 52 playing cards for blackjack
  private ArrayList<Card> cardPile;

  public Deck() {
    // Initialize the container for the card objects
    cardPile = new ArrayList<Card>();

    // Populate the deck with all standard cards, assigning appropriate blackjack values
    for (int suitIndex = 0; suitIndex < 4; suitIndex++) {
      for (int rankIndex = 0; rankIndex < 13; rankIndex++) {
        Card newCard;
        if (rankIndex == 0) {
          // Rank 0 corresponds to an Ace, which initially counts as 11 in blackjack
          newCard = new Card(suitIndex, rankIndex, 11);
        } else if (rankIndex >= 10) {
          // Ranks 10, 11, 12 correspond to J, Q, K – each valued as 10
          newCard = new Card(suitIndex, rankIndex, 10);
        } else {
          // All other cards are worth their rankIndex+1 (e.g., rankIndex=1 is '2', value=2)
          newCard = new Card(suitIndex, rankIndex, rankIndex + 1);
        }
        cardPile.add(newCard);
      }
    }
  }

  public void mixDeck() {
    Collections.shuffle(cardPile);
  }

  public Card fetchCard(int position) {
    return cardPile.get(position);
  }

  public void extractCard(int position) { cardPile.remove(position);
  }
}