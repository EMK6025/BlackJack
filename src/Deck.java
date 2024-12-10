import java.util.ArrayList;
import java.util.Collections;

public class Deck {

  // An ArrayList to hold all 52 playing cards for blackjack
  private ArrayList<Card> cardPile;

  public Deck() {

    cardPile = new ArrayList<Card>();


    for (int suitIndex = 0; suitIndex < 4; suitIndex++) {
      for (int rankIndex = 0; rankIndex < 13; rankIndex++) {
        Card newCard;
        if (rankIndex == 0) {

          newCard = new Card(suitIndex, rankIndex, 11);
        } else if (rankIndex >= 10) {

          newCard = new Card(suitIndex, rankIndex, 10);
        } else {

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