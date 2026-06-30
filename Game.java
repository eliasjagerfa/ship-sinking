import java.io.IOException;
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
  final String playerOne;
  final String playerTwo;
  private final Scanner scanner;
  private int totalShipsToPlace  = 0;
  private final ArrayList<int[]>[] shipsPositions;
  private HashMap<Integer, Integer> shipsToPlace = new HashMap<>();


  public Game(GameTemplate template, String playerOneName, String playerTwoName) {
    this.width = template.getWidth();
    this.height = template.getHeight();
    this.isPlayerOneTurn = true;
    this.playerOne = playerOneName;
    this.playerTwo = playerTwoName;
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
      String currentPlayer = i == 0 ? playerOne : playerTwo;
      Battlefield currentPlayersBattlefield = i == 0 ? player1Battlefield : player2Battlefield;

      out.println("\nPlease hand the device to Player " + currentPlayer);
      out.println("Press enter to continue");
      scanner.nextLine();
      clearScreen();
      
      HashMap<Integer, Integer> shipsLeftToPlace = new HashMap<>(shipsToPlace);
      int shipsPlaced = 0;
      int shipsPlacedBefore = 0;

      out.println("You can now place ships on your battlefield.\n");

      while (totalShipsToPlace != shipsPlaced) {
        if(shipsPlacedBefore != shipsPlaced) {
          out.println("You have " + (totalShipsToPlace - shipsPlaced) + " ship(s) left to place in total");
          shipsPlacedBefore = shipsPlaced;
        }
        out.println("\nIf you dont know how to place ships, type 'help'");
        out.println("if you want to know each ship you have to place, type 'status'\n");

        System.out.println(currentPlayersBattlefield.convertBattlefieldToText(true));

        out.print("\nEnter your ships position: ");


        String input = scanner.nextLine().toLowerCase().trim();
        out.println();

        if (input.equals("help")) {
          out.println("To place a ship, type in the following format: 'x y length orientation'");
          out.println("x and y are the coordinates of the starting point of the ship, length is the length of the ship and orientation is either 'h' for horizontal or 'v' for vertical");
          out.println("Example: '0 0 3 h' would place a ship of length 3 horizontally starting at the bottom left corner of the battlefield");
          out.println("You can only place ships within the bounds of the battlefield and they cannot overlap\n");
          continue;
        }

        if (input.equals("status")) {
          shipsLeftToPlace.forEach((length, amount) -> {
            out.println("You have " + amount + "ship(s) of length " + length + " left to place");
          });
          
          continue;
        }

        Pattern pattern = Pattern.compile("^(\\d+) (\\d+) (\\d+) ([hv])$");
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
          int length = Integer.parseInt(matcher.group(3));
          int amountShipOfLength = shipsLeftToPlace.getOrDefault(length, 0);
          if (amountShipOfLength > 0) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            String rotation = matcher.group(4);
            Ship newShip = new Ship(x, y, length, rotation.equals("h"));

            boolean wasShipPlaced = (i == 0) 
              ? player1Battlefield.setShip(newShip, shipsPlaced)
              : player2Battlefield.setShip(newShip, shipsPlaced);

            if (wasShipPlaced) {
              shipsPlaced++;
              shipsLeftToPlace.merge(length, -1, Integer::sum);
              out.println("Ship placed successfully");
              out.println("You have " + shipsLeftToPlace.get(length) + " ship(s) of length " + length + " left to place");
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
      String currentPlayer = isPlayerOneTurn ? playerOne : playerTwo;
      // reminder to hand over the device
      out.println("Please hand the device to  " + currentPlayer);
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
      printBattlefields(playerOne, playerTwo);
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

  private void printBattlefields(String playerOneName, String playerTwoName) {
    String[] currentPlayersBoard;
    String[] enemyBoard;
    String currentPlayersBoardHeader;
    String enemyBoardHeader;

    if (isPlayerOneTurn) {
      currentPlayersBoard = player1Battlefield.convertBattlefieldToText().split("\n");
      enemyBoard = player2Battlefield.convertBattlefieldToText().split("\n");

      currentPlayersBoardHeader = playerOneName;
      enemyBoardHeader = playerTwoName;
    } else {
      currentPlayersBoard = player2Battlefield.convertBattlefieldToText().split("\n");
      enemyBoard = player1Battlefield.convertBattlefieldToText().split("\n");

      currentPlayersBoardHeader = playerTwoName;
      enemyBoardHeader = playerOneName;
    }

    currentPlayersBoardHeader += " (you)";
    enemyBoardHeader += " (enemy)";

    int distance = Math.max(width * 4, currentPlayersBoardHeader.length()) + 4;

    System.out.printf("%-" + (distance) + "s%s\n", currentPlayersBoardHeader, enemyBoardHeader);

    for(int i = 0; i < currentPlayersBoard.length; i++) {
      System.out.printf("%-" + (distance) + "s%s\n", currentPlayersBoard[i], enemyBoard[i]);
    }
  }

  public static void clearScreen() {
    try { 
    String os = System.getProperty("os.name").toLowerCase();
    ProcessBuilder pb;
    if (os.contains("win")) {
        pb = new ProcessBuilder("cmd", "/c", "cls");
    } else {
        pb = new ProcessBuilder("clear");
    }
    pb.inheritIO().start().waitFor();

    } catch (IOException | InterruptedException e) {
      out.print("\u001B[2J\u001B[3J");
    }
    
  }
}