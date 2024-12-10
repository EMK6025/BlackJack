import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameDisplay extends JComponent implements ActionListener, MouseListener {


  public BufferedImage tableImage;
  public BufferedImage gameLogo;
  public BufferedImage betChip;

  public JButton buttonHit = new JButton("HIT");
  public JButton buttonStand = new JButton("STAND");
  public JButton buttonDouble = new JButton("DOUBLE");
  public JButton buttonExit = new JButton("EXIT CASINO");


  // Tracking how many times dealer and player have won
  public static int dealerWinsCount = 0;
  public static int playerWinsCount = 0;
  public static boolean cardHidden = true;
  public static int outcome;

  public GameDisplay() {
    // Add and style the buttons
    buttonHit.setBounds(390, 550, 100, 50);
    buttonHit.setFont(new Font("SansSerif", Font.BOLD, 16));

    buttonStand.setBounds(520, 550, 100, 50);
    buttonStand.setFont(new Font("SansSerif", Font.BOLD, 16));

    buttonDouble.setBounds(650, 550, 100, 50);
    buttonDouble.setFont(new Font("SansSerif", Font.BOLD, 16));

    buttonExit.setBounds(930, 240, 190, 50);
    buttonExit.setFont(new Font("SansSerif", Font.BOLD, 16));

    buttonExit.addActionListener(this);
    buttonHit.addActionListener(this);
    buttonDouble.addActionListener(this);
    buttonStand.addActionListener(this);
    // Add buttons to the frame
    add(buttonHit);
    add(buttonStand);
    add(buttonDouble);
    add(buttonExit);
  }

  // Rendering the entire visual state (background, logos, cards, etc.)
  public void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;


    try {
      tableImage = ImageIO.read(new File("images/background.jpg"));
      gameLogo = ImageIO.read(new File("images/blackjackLogo.png"));
      betChip = ImageIO.read(new File("images/chip.png"));
    } catch (IOException e) {

    }

    g2.drawImage(tableImage, 0, 0, null);
    g2.drawImage(gameLogo, 510, 400, null);
    g2.drawImage(betChip, 50, 300, null);

    g2.setColor(Color.WHITE);
    g2.setFont(new Font("Montserrat", Font.BOLD, 30));
    g2.drawString("DEALER", 515, 50);
    g2.drawString("PLAYER", 515, 380);

    g2.drawString("DEALER WON: ", 50, 100);
    g2.drawString(Integer.toString(dealerWinsCount), 300, 100);

    g2.drawString("PLAYER WON: ", 50, 150);
    g2.drawString(Integer.toString(playerWinsCount), 300, 150);

    g2.setFont(new Font("Montserrat", Font.BOLD, 15));

    g2.setFont(new Font("Montserrat", Font.BOLD, 20));
    g2.drawString("CURRENT BALANCE: " + OrderFlow.currentBalance, 50, 570);

    try {
      for (int i = 0; i < AllActions.dealerCards.size(); i++) {
        if (i == 0 && GameDisplay.cardHidden) {

          AllActions.dealerCards.get(i).renderCard(g2, true, true, i);
        } else {
          AllActions.dealerCards.get(i).renderCard(g2, true, false, i);
        }
      }
    } catch (IOException e) {

    }

    try {
      for (int i = 0; i < AllActions.playerCards.size(); i++) {
        AllActions.playerCards.get(i).renderCard(g2, false, false, i);
      }
    } catch (IOException e) {

    }
  }
  public void actionPerformed(ActionEvent evt) {
    JButton triggered = (JButton) evt.getSource();
    if (triggered == buttonExit){
      AllActions.exit();
    }
    else if (triggered == buttonHit){
      AllActions.hit();
    }
    else if (triggered == buttonDouble){
      AllActions.doubleDown();
    }
    else if (triggered == buttonStand){
      AllActions.stand();
    }
  }

  @Override
  public void mousePressed(MouseEvent e) { // make bet
    int clickX = e.getX();
    int clickY = e.getY();
    AllActions.mousePressed(clickX, clickY);
  }

  @Override public void mouseExited(MouseEvent e) {}
  @Override public void mouseEntered(MouseEvent e) {}
  @Override public void mouseReleased(MouseEvent e) {}
  @Override public void mouseClicked(MouseEvent e) {}
}