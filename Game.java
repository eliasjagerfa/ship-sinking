import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game {
  final int width;
  final int height;
  private Battlefield player1Battlefield;
  private Battlefield player2Battlefield;
  private boolean isPlayerOneTurn;
  private final Scanner scanner;
  private final ArrayList<ShipConfig> shipConfigs;
  private int totalShipsToPlace  = 0; //int reicht glaube ich schon?


  public Game(int width, int height, ArrayList<ShipConfig> shipConfigs) {
    this.width = width;
    this.height = height;
    this.player1Battlefield = new Battlefield(width, height);
    this.player2Battlefield = new Battlefield(width, height);
    this.isPlayerOneTurn = true;
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
  }

  public void closeScanner() {
    scanner.close();
  }

  public void startShipPlacement() {
    for (int i = 0; i < 2; i++) {
      String currentPlayer = i == 0 ? "1" : "2";

      out.println("Please hand the device to Player " + currentPlayer);
      out.println("Press enter to continue");
      scanner.nextLine();

      int shipsPlaced = 0; //int reicht doch?
      int shipsPlacedBefore = 0; //int reicht doch?


      out.println("You can now place ships on your battlefield. (if you dont know how to place ships, type 'help')");

      //TODO: Falls wir uns auf CLI konzentrieren, wäre ein "gebe die Schiffspositionen an: " glaube ganz gut
      while (totalShipsToPlace != shipsPlaced) {
        out.print("Enter your ships position: ");
        if(shipsPlacedBefore != shipsPlaced) {
          out.println("Ship placed successfully");
          out.println("You have " + (totalShipsToPlace - shipsPlaced) + " left to place\n");
          shipsPlacedBefore = shipsPlaced;
        }

        String input = scanner.nextLine().toLowerCase().trim();

        if (input.equals("help")) {
          out.println("To place a ship, type in the following format: 'x y length orientation'");
          out.println("x and y are the coordinates of the starting point of the ship, length is the length of the ship and orientation is either 'h' for horizontal or 'v' for vertical");
          out.println("Example: '0 0 3 h' would place a ship of length 3 horizontally starting at the bottom left corner of the battlefield");
          out.println("You can only place ships within the bounds of the battlefield and they cannot overlap\n");
          continue;
        }

        Pattern pattern = Pattern.compile("^(\\d+) (\\d+) (\\d+) ([hv])$");
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
          // TODO: add proper input validation. as an example dont allow negative or 0 values, aswell as ones going out of length
          int x = Integer.parseInt(matcher.group(1));
          int y = Integer.parseInt(matcher.group(2));
          int length = Integer.parseInt(matcher.group(3));
          String rotation = matcher.group(4);

          Ship newShip = new Ship(x, y, length, rotation.equals("h"));

          boolean isShipPlaced;

          if (i == 0) {
            isShipPlaced = player1Battlefield.setShip(newShip);
          } else {
            isShipPlaced = player2Battlefield.setShip(newShip);
          }

          if (isShipPlaced) {
            shipsPlaced++;
          } else {
            // TODO: add better error handling
            out.println("Ship couldnt be placed. Try again!");
          }

        } else {
          out.println("Wrong command, try again\n");
        }
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