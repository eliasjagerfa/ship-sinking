import static java.lang.System.out;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game {
  private final Battlefield player1Battlefield;
  private final Battlefield player2Battlefield;
  private boolean isPlayerOneTurn;
  private String playerOneName;
  private String playerTwoName;
  private final Scanner scanner;
  private int totalShipsToPlace  = 0;
  private final Map<Integer, Integer> shipsToPlace;


  public Game(GameMode gameMode, Scanner globalScanner) {
    int width = gameMode.getWidth();
    int height = gameMode.getHeight();
    this.isPlayerOneTurn = true;
    this.scanner = globalScanner;

    shipsToPlace = new HashMap<>(gameMode.getConfigs());
    shipsToPlace.values().forEach((amount) -> {
      totalShipsToPlace += amount;
    });

    this.player1Battlefield = new Battlefield(width, height, totalShipsToPlace);
    this.player2Battlefield = new Battlefield(width, height, totalShipsToPlace);
  }

  public void startShipPlacement() {
    for (int i = 0; i < 2; i++) {
      String currentPlayer = i == 0 ? playerOneName : playerTwoName;
      Battlefield currentPlayersBattlefield = i == 0 ? player1Battlefield : player2Battlefield;

      out.println("\nPlease hand the device to " + currentPlayer);
      out.println("Press enter to continue");
      scanner.nextLine();
      ConsoleOutput.clearScreen();
      
      Map<Integer, Integer> shipsLeftToPlace = new HashMap<>(shipsToPlace);
      int shipsPlaced = 0;
      int shipsPlacedBefore = 0;

      out.println("You can now place ships on your battlefield.\n");
      out.println("\nIf you dont know how to place ships, type 'help'");

      while (totalShipsToPlace != shipsPlaced) {
        if(shipsPlacedBefore != shipsPlaced) {
          out.println("You have " + (totalShipsToPlace - shipsPlaced) + " ship(s) left to place in total");
          shipsPlacedBefore = shipsPlaced;
        }
        out.println("if you want to know each ship you have to place, type 'status'\n");

        System.out.println(currentPlayersBattlefield.convertBattlefieldToText(true));

        out.print("\nEnter your ships position: ");


        String input = scanner.nextLine().toLowerCase().trim();
        out.println();

        if (input.equals("help")) {
          ConsoleOutput.printShipPlacementHelp();
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
        //TODO: Auslagern von den checks in eigene Methoden
        if (matcher.matches()) {
          int length = Integer.parseInt(matcher.group(3));
          int amountShipOfLength = shipsLeftToPlace.getOrDefault(length, 0);
          if (amountShipOfLength > 0) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            String rotation = matcher.group(4);
            //TODO: Davor sollten alle positionen schon valid sein
            Ship newShip = new Ship(x, y, length, rotation.equals("h"));  

            if (i == 0) {
              GameTypes.ShipPositionValidationResult isInvalid = player1Battlefield.validateShipPositions(newShip);
              if (isInvalid.isOutOfBounds()) {
                ConsoleOutput.clearScreen();
                System.out.println("Ship goes outside the battleship. Try again!");
                continue;
              } else if (isInvalid.isOverlapping()) {
                ConsoleOutput.clearScreen();
                System.out.println("Ship overlaps with another. Try again!");
                continue;
              }

              player1Battlefield.addShip(newShip, shipsPlaced);
            } else {
              GameTypes.ShipPositionValidationResult isInvalid = player2Battlefield.validateShipPositions(newShip);
              if (isInvalid.isOutOfBounds()) {
                ConsoleOutput.clearScreen();
                System.out.println("Ship goes outside the battleship. Try again!");
                continue;
              } else if (isInvalid.isOverlapping()) {
                ConsoleOutput.clearScreen();
                System.out.println("Ship overlaps with another. Try again!");
                continue;
              }
              player2Battlefield.addShip(newShip, shipsPlaced);
            }

            shipsPlaced++;
            shipsLeftToPlace.merge(length, -1, Integer::sum);
            ConsoleOutput.clearScreen();
            out.println("Ship placed successfully");
            out.println("You have " + shipsLeftToPlace.get(length) + " ship(s) of length " + length + " left to place");
          } else {
            out.println("No ships of this size can be placed\n");
          }
        } else {
          ConsoleOutput.printInvalidCommand();
          out.println("\nIf you dont know how to place ships, type 'help'");
        }
      }
      ConsoleOutput.clearScreen();
      out.println("All ships sucessfully placed");
    }
    isPlayerOneTurn = true;
  }

  public void startGame() {
    while(true){
      String currentPlayer = isPlayerOneTurn ? playerOneName : playerTwoName;
      // reminder to hand over the device
      out.println("Please hand the device to  " + currentPlayer);
      out.println("Press enter to continue");
      scanner.nextLine();

      ConsoleOutput.clearScreen();

      GameTypes.DoTurnResult result = doTurn();
      if (result.hasWon()) { break; }

      isPlayerOneTurn = !isPlayerOneTurn;
    }

    String winner = isPlayerOneTurn ? "1" : "2";
    System.out.println(ConsoleOutput.GREEN + "You sunk the last ship!\n");
    out.print("Player " + winner + " has won!" + ConsoleOutput.RESET);
  }

  public GameTypes.DoTurnResult doTurn() {
    int shipsHitStreak = 0;

    System.out.println("(Type 'help' if you dont know how to hit ships)\n");
    while (true) {
      printBattlefields(playerOneName, playerTwoName);
      out.println("Type the coordinates of the enemies field that you want to hit:");
      String input = scanner.nextLine().trim().toLowerCase();

      if(input.equals("help")){
        ConsoleOutput.clearScreen();
        ConsoleOutput.printShootingHelp();
        
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
          ConsoleOutput.clearScreen();
          out.printf("\n%s\nTry again!\n\n", e.getMessage());
          continue;
        }
        
        out.println("\nYou shot at " + input + "  *drumroll please*\n");

        if(result.newFieldValue().equals("emptyHit")) {
          ConsoleOutput.clearScreen();
          out.println("No ship was hit");
          return new GameTypes.DoTurnResult(isPlayerOneTurn, 0, false);

        } else if (result.isShipHit()) {
          shipsHitStreak++;

          if (player2Battlefield.allAreSunken() || player1Battlefield.allAreSunken()) { 

            return new GameTypes.DoTurnResult(isPlayerOneTurn, shipsHitStreak, true);
          }

          if (result.isShipSunken()) {
            ConsoleOutput.clearScreen();
            out.println("You have sunk a ship!");
          } else {
            ConsoleOutput.clearScreen();
            out.println("You hit a ship!");
          }

          
          out.println("You can shoot once more!\n");
        }
      } else {
        ConsoleOutput.printInvalidCommand();
        out.println("(Type 'help' if you dont know how to hit ships)\n");
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

    int distance = Math.max(player1Battlefield.getWidth() * 4, currentPlayersBoardHeader.length()) + 4;

    System.out.printf("%-" + (distance) + "s%s\n", currentPlayersBoardHeader, enemyBoardHeader);

    for(int i = 0; i < currentPlayersBoard.length; i++) {
      System.out.printf("%-" + (distance) + "s%s\n", currentPlayersBoard[i], enemyBoard[i]);
    }
  }

  

  public void setPlayerNames() {
    while (true) { 
      out.println("Do you want to use custom usernames? (y/n) ");
      String input = scanner.nextLine().trim().toLowerCase();

      if(input.startsWith("y")) {
        boolean isPlayerOneHappy = false;
        boolean isPlayerTwoHappy = false;
        while (!isPlayerOneHappy || !isPlayerTwoHappy) {
          if(!isPlayerOneHappy) {
            out.println("\nEnter the name of the first player: ");
            playerOneName = scanner.nextLine().trim();
            out.println("\n" + playerOneName + ". Are you happy with your name? (y/n)");

            input = scanner.nextLine().trim().toLowerCase();
            if(input.startsWith("y")) {
              isPlayerOneHappy = true;
              out.println();
            } else if(input.startsWith("n")) {
              continue;
            } else {
              ConsoleOutput.printInvalidCommand();
              continue;
            }
          }

          if(!isPlayerTwoHappy) {
            out.println("\nEnter the name of the second player: ");

            playerTwoName = scanner.nextLine().trim();
            if(playerTwoName.equals(playerOneName)) {
              out.println("\nCannot be the same name as the first players name");
              continue;
            }
            out.println("\n" + playerTwoName + ". Are you happy with your name? (y/n)");

            input = scanner.nextLine().trim().toLowerCase();
            if(input.startsWith("y")) {
              isPlayerTwoHappy = true;
            } else if(input.startsWith("n")) {
              continue;
            } else {
              ConsoleOutput.printInvalidCommand();
              continue;
            }
          }

          out.println("\nAre you sure you want to play with the following names (y/n)? First Player: " + playerOneName + ", Second Player: " + playerTwoName);
          input = scanner.nextLine().trim().toLowerCase();

          
          if(input.startsWith("n")) {
            isPlayerOneHappy = false;
            isPlayerTwoHappy = false;
          } else if (!input.startsWith("y")) {
            ConsoleOutput.printInvalidCommand();
          }
        }
      } else if(input.startsWith("n")) {
        playerOneName = "Player 1";
        playerTwoName = "Player 2";
        break;
      } else {
        ConsoleOutput.printInvalidCommand();
        continue;
      }
      break;
    }
  }
}