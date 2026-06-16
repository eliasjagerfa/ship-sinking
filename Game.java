import static java.lang.System.out;
import java.util.ArrayList;
import java.util.HashMap;
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
  private int totalShipsToPlace  = 0;
  private HashMap<Integer, Integer> shipsToPlace = new HashMap<>();


  public Game(int width, int height, ArrayList<ShipConfig> shipConfigs) {
    this.width = width;
    this.height = height;
    this.player1Battlefield = new Battlefield(width, height);
    this.player2Battlefield = new Battlefield(width, height);
    this.isPlayerOneTurn = true;
    this.scanner = new Scanner(System.in);

    shipConfigs.forEach((sc) -> {
      totalShipsToPlace += sc.amount;
      shipsToPlace.put(sc.length, shipsToPlace.getOrDefault(sc.length, 0) + sc.amount);
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
      
      HashMap<Integer, Integer> shipsLeftToPlace = new HashMap<>(shipsToPlace);
      int shipsPlaced = 0;
      int shipsPlacedBefore = 0;

      out.println("You can now place ships on your battlefield. (if you dont know how to place ships, type 'help')");

      while (totalShipsToPlace != shipsPlaced) {
        if(shipsPlacedBefore != shipsPlaced) {
          out.println("Ship placed successfully");
          out.println("You have " + (totalShipsToPlace - shipsPlaced) + " left to place\n");
          shipsPlacedBefore = shipsPlaced;
        }
        out.print("Enter your ships position: ");

        String input = scanner.nextLine().toLowerCase().trim();

        // TODO: add a command to show which ships are left to place and how many of each size, which can be done by printing the shipsLeftToPlace hashmap in a nice format, which would be helpful for the user to keep track of which ships they have already placed and which ones they still have to place
        // Additionally after a ship has been placed successfully, it would be nice to say which ship has been placed and how many of that length are left
        // But you could also make it print automatically after every successful ship placement, although this might be a bit too much information for the user, so maybe only print it when the user types a command like "status" or something like that
        if (input.equals("help")) {
          out.println("To place a ship, type in the following format: 'x y length orientation'");
          out.println("x and y are the coordinates of the starting point of the ship, length is the length of the ship and orientation is either 'h' for horizontal or 'v' for vertical");
          out.println("Example: '0 0 3 h' would place a ship of length 3 horizontally starting at the bottom left corner of the battlefield");
          out.println("You can only place ships within the bounds of the battlefield and they cannot overlap\n");
          continue;
        }

        Pattern pattern = Pattern.compile("^(\\d+) (\\d+) (\\d+) ([hv])$");
        Matcher matcher = pattern.matcher(input);

        //TODO: add validator for battlefield size
        if (matcher.matches()) {
          int length = Integer.parseInt(matcher.group(3));
          int amountShipOfLength = shipsLeftToPlace.getOrDefault(length, 0);
          if (amountShipOfLength > 0) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
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
              shipsLeftToPlace.merge(length, -1, Integer::sum);
            } else {
              // TODO: add better error handling
              out.println("Ship couldnt be placed. Try again!");
            }
          } else {
            out.println("No ships of this size can be placed\n");
          }
        } else {
          out.println("Wrong command, try again\n");
        }
      }

      out.println("All ships sucessfully placed");
    }
    
    isPlayerOneTurn = true;
  }

  public void startGame() {
    while(isPlayerOneTurn ? !player1Battlefield.allAreSunken() : !player2Battlefield.allAreSunken()){
      startTurn();
    }

    String winner = isPlayerOneTurn ? "2" : "1";
    out.print("Player " + winner + " has won!");
    restartGame();
  }

  public boolean startTurn() {
    // TODO: Later make this an attribute to define the players display name
    String currentPlayer = isPlayerOneTurn ? "1" : "2";

    // reminder to hand over the device
    out.println("Please hand the device to Player " + currentPlayer);
    out.println("Press enter to continue");
    scanner.nextLine();

    //TODO: Only have this happen if the turn was actually valid and the player didnt hit any ship
    isPlayerOneTurn = !isPlayerOneTurn;
    return false;
  }
}