import static java.lang.System.out;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game {
  private final Battlefield playerOneBattleField;
  private final Battlefield playerTwoBattleField;
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

    shipsToPlace = new HashMap<>(gameMode.getConfig());
    shipsToPlace.values().forEach((amount) -> {
      totalShipsToPlace += amount;
    });

    this.playerOneBattleField = new Battlefield(width, height, shipsToPlace);
    this.playerTwoBattleField = new Battlefield(width, height, shipsToPlace);
  }

  public void startShipPlacement() {
    for (int i = 0; i < 2; i++) {
      String currentPlayer = i == 0 ? playerOneName : playerTwoName;
      Battlefield currentPlayersBattlefield = i == 0 ? playerOneBattleField : playerTwoBattleField;
      isPlayerOneTurn = i == 0;

      if (!isPlayerOneTurn) { out.println(ConsoleOutput.GREEN + "All ships sucessfully placed" + ConsoleOutput.GREY); }
      out.println(ConsoleOutput.RESET + "\nPlease hand the device to " + currentPlayer);
      out.println("Press enter to continue" + ConsoleOutput.GREY);
      scanner.nextLine();
      ConsoleOutput.clearScreen();
      
      Map<Integer, Integer> shipsLeftToPlace = new HashMap<>(shipsToPlace);
      int shipsPlaced = 0;
      int shipsPlacedBefore = 0;

      out.println("You can now place ships on your battlefield.\n");
      out.println("\nIf you dont know how to place ships, type 'help'");

      while (totalShipsToPlace != shipsPlaced) {
        if(shipsPlacedBefore != shipsPlaced) {
          out.println("You have " + (totalShipsToPlace - shipsPlaced) + " ship(s) left to place in total\n" + ConsoleOutput.GREY);
          shipsPlacedBefore = shipsPlaced;
        }
        out.println("if you want to know each ship you have to place, type 'status'\n");

        System.out.println(currentPlayersBattlefield.convertBattlefieldToText(true));

        out.print("\nEnter your ships position: " + ConsoleOutput.RESET);


        String input = scanner.nextLine().toLowerCase().trim();
        out.println();

        if (input.equals("help")) {
          ConsoleOutput.printShipPlacementHelp();
          continue;
        }

        if (input.equals("status")) {
          ConsoleOutput.clearScreen();
          shipsLeftToPlace.forEach((length, amount) -> {
            out.println(ConsoleOutput.YELLOW + "You have " + amount + " ship(s) of length " + length + " left to place");
          });
          out.println(ConsoleOutput.GREY);
          
          continue;
        }

        Pattern defaultPattern = Pattern.compile("^(\\d+) (\\d+) (\\d+) ([hv])$");
        Matcher defaultMatcher = defaultPattern.matcher(input);
        Pattern patternTwo = Pattern.compile("^(\\d+) (\\d+) ([1])$"); //only needed for ships of length 1
        Matcher matcherTwo = patternTwo.matcher(input);
        //TODO: Auslagern von den checks in eigene Methoden
        if (defaultMatcher.matches() || matcherTwo.matches()) {
          //TODO: prüfen, ob es auch sauberer als ? : geht
          int length = defaultMatcher.matches() ? Integer.parseInt(defaultMatcher.group(3)) : Integer.parseInt(matcherTwo.group(3));
          int amountShipOfLength = shipsLeftToPlace.getOrDefault(length, 0);

          if (amountShipOfLength > 0) {
            int x = defaultMatcher.matches() ? Integer.parseInt(defaultMatcher.group(1)) : Integer.parseInt(matcherTwo.group(1));
            int y = defaultMatcher.matches() ? Integer.parseInt(defaultMatcher.group(2)) : Integer.parseInt(matcherTwo.group(2));
            String rotation = defaultMatcher.matches() ? defaultMatcher.group(4) : "h";
            //TODO: Davor sollten alle positionen schon valid sein
            Ship newShip = new Ship(x, y, length, rotation.equals("h"));  

            if (i == 0) {
              GameTypes.ShipPositionValidationResult validationResult = playerOneBattleField.validateShipPositions(newShip);
              if (validationResult.isOutOfBounds()) {
                ConsoleOutput.clearScreen();
                System.out.println(ConsoleOutput.RED + "Ship goes outside the battleship. Try again!" + ConsoleOutput.GREY);
                continue;
              } else if (validationResult.isOverlapping()) {
                ConsoleOutput.clearScreen();
                System.out.println(ConsoleOutput.RED + "Ship overlaps with another. Try again!" + ConsoleOutput.GREY);
                continue;
              }

              playerOneBattleField.addShip(newShip, shipsPlaced);
            } else {
              GameTypes.ShipPositionValidationResult validationResult = playerTwoBattleField.validateShipPositions(newShip);
              if (validationResult.isOutOfBounds()) {
                ConsoleOutput.clearScreen();
                System.out.println(ConsoleOutput.RED + "Ship goes outside the battleship. Try again!" + ConsoleOutput.GREY);
                continue;
              } else if (validationResult.isOverlapping()) {
                ConsoleOutput.clearScreen();
                System.out.println(ConsoleOutput.RED + "Ship overlaps with another. Try again!" + ConsoleOutput.GREY);
                continue;
              }
              playerTwoBattleField.addShip(newShip, shipsPlaced);
            }

            shipsPlaced++;
            shipsLeftToPlace.merge(length, -1, Integer::sum);
            ConsoleOutput.clearScreen();
            out.println(ConsoleOutput.GREEN + "Ship placed successfully" + ConsoleOutput.YELLOW);
            out.println("You have " + shipsLeftToPlace.get(length) + " ship(s) of length " + length + " left to place");
          } else {
            ConsoleOutput.clearScreen();
            out.println(ConsoleOutput.RED + "No ships of this size can be placed\n" + ConsoleOutput.GREY);
          }
        } else {
          ConsoleOutput.clearScreen();
          ConsoleOutput.printInvalidCommand();
          out.println("\nIf you dont know how to place ships, type 'help'");
        }
      }
      ConsoleOutput.clearScreen();
      
    }
    isPlayerOneTurn = true;
  }

  public void startGame() {
    while(true){
      String currentPlayer = isPlayerOneTurn ? playerOneName : playerTwoName;
      // reminder to hand over the device
      out.println(ConsoleOutput.RESET + "Please hand the device to " + currentPlayer);
      out.println("Press enter to continue" + ConsoleOutput.GREY);
      scanner.nextLine();

      ConsoleOutput.clearScreen();

      GameTypes.DoTurnResult result = doTurn();
      if (result.hasWon()) { break; }

      isPlayerOneTurn = !isPlayerOneTurn;
    }

    String winner = isPlayerOneTurn ? "1" : "2";
    System.out.println(ConsoleOutput.GREEN + "You sunk the last ship!\n" + ConsoleOutput.GREY);
    out.print(ConsoleOutput.YELLOW + "Player " + winner + " has won!" + ConsoleOutput.RESET);
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
            result = playerTwoBattleField.hitField(x, y);
          } else {
            result = playerOneBattleField.hitField(x, y);
          }
        } catch (Exception e) {
          ConsoleOutput.clearScreen();
          out.printf(ConsoleOutput.RED + "\n%s\nTry again!\n\n", e.getMessage() + ConsoleOutput.GREY);
          continue;
        }
        
        ConsoleOutput.clearScreen();
        out.println(ConsoleOutput.RESET + "\nYou shot at " + input + "  *drumroll please*\n");
        
        if(result.newFieldValue().equals("emptyHit")) {
          out.println("But no ship was hit :(\n" + ConsoleOutput.GREY);
          return new GameTypes.DoTurnResult(isPlayerOneTurn, 0, false);

        } else if (result.isShipHit()) {
          shipsHitStreak++;

          if (playerTwoBattleField.allAreSunken() || playerOneBattleField.allAreSunken()) { 

            return new GameTypes.DoTurnResult(isPlayerOneTurn, shipsHitStreak, true);
          }

          if (result.isShipSunken()) {
            out.println("You have sunk a ship!");
          } else {
            out.println("You hit a ship!");
          }

          
          out.println("You can shoot once more!\n" + ConsoleOutput.GREY);
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
      currentPlayersBoard = playerOneBattleField.convertBattlefieldToText().split("\n");
      enemyBoard = playerTwoBattleField.convertBattlefieldToText().split("\n");

      currentPlayersBoardHeader = playerOneName;
      enemyBoardHeader = playerTwoName;
    } else {
      currentPlayersBoard = playerTwoBattleField.convertBattlefieldToText().split("\n");
      enemyBoard = playerOneBattleField.convertBattlefieldToText().split("\n");

      currentPlayersBoardHeader = playerTwoName;
      enemyBoardHeader = playerOneName;
    }

    currentPlayersBoardHeader += " (you)";
    enemyBoardHeader += " (enemy)";

    int distance = Math.max(playerOneBattleField.getWidth() * 4, currentPlayersBoardHeader.length()) + 4;

    System.out.printf("%-" + (distance) + "s%s\n", currentPlayersBoardHeader, enemyBoardHeader);

    for(int i = 0; i < currentPlayersBoard.length; i++) {
      System.out.printf("%-" + (distance) + "s%s\n", currentPlayersBoard[i], enemyBoard[i]);
    }
  }

  

  public void setPlayerNames() {
    while (true) { 
      out.println("Do you want to use custom usernames? (y/n) " + ConsoleOutput.RESET);
      String input = scanner.nextLine().trim().toLowerCase();

      if(input.startsWith("y")) {
        boolean isPlayerOneHappy = false;
        boolean isPlayerTwoHappy = false;
        while (!isPlayerOneHappy || !isPlayerTwoHappy) {
          if(!isPlayerOneHappy) {
            out.println(ConsoleOutput.GREY + "\nEnter the name of the first player: " + ConsoleOutput.RESET);
            playerOneName = scanner.nextLine().trim();
            out.println(ConsoleOutput.GREY + "\n" + playerOneName + ". Are you happy with your name? (y/n)" + ConsoleOutput.RESET);

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
            out.println(ConsoleOutput.GREY + "\nEnter the name of the second player: " + ConsoleOutput.RESET);

            playerTwoName = scanner.nextLine().trim();
            if(playerTwoName.equals(playerOneName)) {
              out.println(ConsoleOutput.RED + "\nCannot be the same name as the first players name" + ConsoleOutput.GREY);
              continue;
            }
            out.println(ConsoleOutput.GREY + "\n" + playerTwoName + ". Are you happy with your name? (y/n)" + ConsoleOutput.RESET);

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
          out.println(ConsoleOutput.GREY + "\nFirst Player: " + playerOneName);
          out.println("Second Player: " + playerTwoName);
          out.println("\nAre you sure you want to play with the following names (y/n)? " + ConsoleOutput.RESET);
          
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