import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Card {
  // Attributes describing the card's suit, rank, value, and position on the screen
  private final int cardSuit;   // 0: Clubs, 1: Diamonds, 2: Hearts, 3: Spades
  private final int cardRank;   // 0: Ace, 1: 2, 2: 3, ..., 9: 10, 10: Jack, 11: Queen, 12: King
  private final int cardValue;  // Blackjack value of the card (1 to 11)

    // Default constructor initializes a meaningless card (used for initial setup)
  public Card() {
    cardSuit = 0;
    cardRank = 0;
    cardValue = 0;
  }

  // Primary constructor allowing direct specification of suit, rank, and value
  public Card(int suit, int rank, int value) {
    this.cardSuit = suit;
    this.cardRank = rank;
    this.cardValue = value;
  }

  // Accessors for suit, rank, and value
  public int getSuit() {
    return cardSuit;
  }

  public int getRank() {
    return cardRank;
  }

  public int getValue() {
    return cardValue;
  }

  /**
   * Renders the card onto the given Graphics2D context.
   *
   * @param g2         The Graphics2D object for rendering.
   * @param dealerTurn  True if this card is the dealer’s card; false if player’s.
   * @param faceDown    True if card should be drawn face-down; false if face-up.
   * @param cardIndex   The index of this card in the current line (to space them properly).
   * @throws IOException If image files cannot be loaded.
   */
  public void renderCard(Graphics2D g2, boolean dealerTurn, boolean faceDown, int cardIndex) throws IOException {
    // Load the master sprite sheet for all card faces.
    BufferedImage fullDeck = ImageIO.read(new File("images/cardSpriteSheet.png"));
    BufferedImage reverseSide = ImageIO.read(new File("images/backsideOfACard.jpg"));

    // Dimensions of the sprite sheet (entire image)
    int cardW = 950 / 13;
    int cardH = 392 / 4;

    // Slice the sprite sheet into a 4x13 array of card images
    BufferedImage[][] cardFaces = new BufferedImage[4][13];
    for (int suit = 0; suit < 4; suit++) {
      for (int rank = 0; rank < 13; rank++) {
        cardFaces[suit][rank] = fullDeck.getSubimage(rank * cardW, suit * cardH, cardW, cardH);
      }
    }

    // Decide vertical placement: dealer's cards appear at the top, player's cards at the bottom
      // Vertical coordinate for drawing the card
      int posY = dealerTurn ? 75 : 400;
    // Horizontal placement: space each subsequent card horizontally
      // Horizontal coordinate for drawing the card
      int posX = 500 + 75 * cardIndex;

    // Draw either the card face or the back side, depending on the faceDown flag
    if (faceDown) {
      g2.drawImage(reverseSide, posX, posY, null);
    } else {
      g2.drawImage(cardFaces[cardSuit][cardRank], posX, posY, null);
    }
  }
}