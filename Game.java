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
  private final ArrayList<int[]>[] shipsPositions;
  private HashMap<Integer, Integer> shipsToPlace = new HashMap<>();


  public Game(GameTemplate template) {
    this.width = template.getWidth();
    this.height = template.getHeight();
    this.isPlayerOneTurn = true;
    this.scanner = new Scanner(System.in);

    template.getConfig().forEach((sc) -> {
      totalShipsToPlace += sc.amount;
      shipsToPlace.put(sc.length, sc.amount);
    });

    this.player1Battlefield = new Battlefield(width, height, totalShipsToPlace);
    this.player2Battlefield = new Battlefield(width, height, totalShipsToPlace);
    
    this.shipsPositions = new ArrayList[shipsToPlace.size()];
    for (int i = 0; i < shipsPositions.length; i++) {
      shipsPositions[i] = new ArrayList<>();
    }
  }

  public void restartGame() {
    this.player1Battlefield = new Battlefield(width, height, totalShipsToPlace);
    this.player2Battlefield = new Battlefield(width, height, totalShipsToPlace);
    this.isPlayerOneTurn = true;
  }

  public void closeScanner() {
    scanner.close();
  }

  public void startShipPlacement() {
    out.println("Template locked in");
    for (int i = 0; i < 2; i++) {
      String currentPlayer = i == 0 ? "1" : "2";
      out.println("\nPlease hand the device to Player " + currentPlayer);
      out.println("Press enter to continue");
      scanner.nextLine();
      clearScreen();
      
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
        out.println();

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

            if (i == 0) {
              if (player1Battlefield.areShipPositionsValid(newShip)) {
                player1Battlefield.setShip(newShip, shipsPlaced);
                shipsPlaced++;
                shipsLeftToPlace.merge(length, -1, Integer::sum);
              } else {
                out.println("Ship goes outside the battlefield. Try again!");
              }
            } else {
              if (player2Battlefield.areShipPositionsValid(newShip)) {
                player2Battlefield.setShip(newShip, shipsPlaced);
                shipsPlaced++;
                shipsLeftToPlace.merge(length, -1, Integer::sum);
              } else {
                //TODO: add differenciation between outOfBounds and overLapping
                out.println("Ship goes outside the battlefield. Try again!");
              }
            }
          } else {
            out.println("No ships of this size can be placed\n");
          }
        } else {
          out.println("Wrong command, try again\n");
        }
      }
      clearScreen();
      out.println("All ships sucessfully placed");
    }
    isPlayerOneTurn = true;
  }

  public void startGame() {
    while(true){
      //TODO: Later make this an attribute to define the players display name 
      String currentPlayer = isPlayerOneTurn ? "1" : "2";
      // reminder to hand over the device
      out.println("Please hand the device to Player " + currentPlayer);
      out.println("Press enter to continue");
      scanner.nextLine();

      clearScreen();

      GameTypes.DoTurnResult result = doTurn();
      if (result.hasWon()) { break; }

      isPlayerOneTurn = !isPlayerOneTurn;
    }

    String winner = isPlayerOneTurn ? "1" : "2";
    System.out.println("You sunk the last ship!\n");
    out.print("Player " + winner + " has won!");
    restartGame();
  }

  public GameTypes.DoTurnResult doTurn() {
    int shipsHitStreak = 0;

    System.out.println("(type 'help' if you dont know how to hit ships)\n");
    while (true) {
      printBattlefields();
      out.println("Type the coordinates of the enemies field that you want to hit:");
      String input = scanner.nextLine().trim().toLowerCase();

      if(input.equals("help")){
        clearScreen();
        out.println("The format is as follows: 'x y'");
        out.println("Example: You want to shoot at the field with x:1 y:1, then you just type '1 1'\n");
        
        continue;
      }
      
      Pattern pattern = Pattern.compile("^(\\d+) (\\d+)$");
      Matcher matcher = pattern.matcher(input);
      if(matcher.matches()){
        int x = Integer.parseInt(matcher.group(1));
        int y = Integer.parseInt(matcher.group(2));

        GameTypes.HitShipResult result;

        try {
          if (isPlayerOneTurn) {
            result = player2Battlefield.hitField(x, y);
          } else {
            result = player1Battlefield.hitField(x, y);
          }
        } catch (Exception e) {
          clearScreen();
          out.printf("\n%s\nTry again!\n\n", e.getMessage());
          continue;
        }
        
        out.println("\nYou shot at " + input + "  *drumroll please*\n");

        if(result.newFieldValue().equals("emptyHit")) {
          clearScreen();
          out.println("No ship was hit");
          return new GameTypes.DoTurnResult(isPlayerOneTurn, 0, false);

        } else if (result.isShipHit()) {
          shipsHitStreak++;

          if (player2Battlefield.allAreSunken() || player1Battlefield.allAreSunken()) { 

            return new GameTypes.DoTurnResult(isPlayerOneTurn, shipsHitStreak, true);
          }

          if (result.isShipSunken()) {
            clearScreen();
            out.println("You have sunk a ship!");
          } else {
            clearScreen();
            out.println("You hit a ship!");
          }

          
          out.println("You can shoot once more!\n");
        }
      } else {
        out.println("Invalid Command. Try again! (type 'help' if you dont know how to hit ships)\n");
      }      
    }
  }

  private void printBattlefields() {
    String[] currentPlayersBoard;
    String[] enemieBoard;
    String currentPlayersBoardHeader;
    String enemieBoardHeader;

    if (isPlayerOneTurn) {
      //TODO: fix ur damn spelling mistakes pls (enemy not enemie)
      currentPlayersBoard = player1Battlefield.convertBattlefieldToText().split("\n");
      enemieBoard = player2Battlefield.convertBattlefieldToText().split("\n");

      currentPlayersBoardHeader = "Player 1";
      enemieBoardHeader = "Player 2";
    } else {
      currentPlayersBoard = player2Battlefield.convertBattlefieldToText().split("\n");
      enemieBoard = player1Battlefield.convertBattlefieldToText().split("\n");

      currentPlayersBoardHeader = "Player 2";
      enemieBoardHeader = "Player 1";
    }

    currentPlayersBoardHeader += " (you)";
    enemieBoardHeader += " (enemy)";

    int distance = Math.max(width * 4, currentPlayersBoardHeader.length()) + 4;

    System.out.printf("%-" + (distance) + "s%s\n", currentPlayersBoardHeader, enemieBoardHeader);

    for(int i = 0; i < currentPlayersBoard.length; i++) {
      System.out.printf("%-" + (distance) + "s%s\n", currentPlayersBoard[i], enemieBoard[i]);
    }
  }

  public static void clearScreen() {
    System.out.print("\u001B[2J\u001B[3J");
  }
}