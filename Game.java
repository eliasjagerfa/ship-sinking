import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Scanner;

public class Game {
  final int width;
  final int height;
  private Battlefield player1Battlefield;
  private Battlefield player2Battlefield;
  private boolean isPlayerOneTurn;
  private boolean isPhaseOne;
  private final Scanner scanner;
  private final ArrayList<ShipConfig> shipConfigs;
  private Integer totalShipsToPlace  = 0;


  public Game(int width, int height, ArrayList<ShipConfig> shipConfigs) {
    this.width = width;
    this.height = height;
    this.player1Battlefield = new Battlefield(width, height);
    this.player2Battlefield = new Battlefield(width, height);
    this.isPlayerOneTurn = true;
    this.isPhaseOne = true;
    this.scanner = new Scanner(System.in);
    this.shipConfigs = shipConfigs;

    shipConfigs.forEach((sc) -> {
      totalShipsToPlace += sc.amount;
    });
  }

  public void restartGame() {
    this.player1Battlefield = new Battlefield(width, height);
    this.player2Battlefield = new Battlefield(width, height);
    this.isPlayerOneTurn = true;
    this.isPhaseOne = true;
  }

  public void closeScanner() {
    scanner.close();
  }

  public void startShipPlacement() {
    for (int i = 0; i < 2; i++) {
      String currentPlayer = i == 0 ? "1" : "2";

      out.println("Please hand the device to Player " + currentPlayer);
      out.println("Press any key to continue");
      scanner.nextLine();

      Integer shipsPlaced = 0;

      while (totalShipsToPlace != shipsPlaced) { 
          
      }

      out.println("All ships sucessfully placed");
    }
    
    isPlayerOneTurn = true;
  }

  public void startTurn() {
    // TODO: Later make this an attribute to define the players display name
    String currentPlayer = isPlayerOneTurn ? "1" : "2";

    // reminder to hand over the device
    out.println("Please hand the device to Player " + currentPlayer);
    out.println("Press any key to continue");
    scanner.nextLine();


    isPlayerOneTurn = !isPlayerOneTurn;
    return;
  }
}