package game;

import static game.GameMode.*;
import game.GameMode.GameModeTypes;
import java.io.IOException;
import static java.lang.System.out;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleIO implements IO {
  Scanner scanner = new Scanner(System.in);
  public final String GREY = "\u001b[90m"; //Standard color
  public final String RESET = "\u001B[0m"; //Standard highlighter
  public final String RED = "\u001B[31m"; //Standard error
  public final String GREEN = "\u001B[32m"; //Standard success
  public final String YELLOW = "\u001b[33m"; //Standard Info highlighter


  @Override
  public GameTypes.Config inputGameConfig() {
    while(true) {
      System.out.println(GREY + "\nType your desired Gamemode");
      System.out.println("The following options are available: small, medium, large");
      System.out.println("(If you want to know the specifications for any of the Gamemodes, type 'info <your Gamemode>'. Example: 'info small')" + RESET);
      String input = scanner.nextLine().trim().toLowerCase();
      System.out.println();
      
      if (input.startsWith("info ")) {
        String chosengameMode = input.split(" ")[1];
        outputGameModeInfo(chosengameMode);
        continue;
      }
      
      switch(input) {
        case "small" -> {
          System.out.println(GREEN + "Gamemode locked in. \n\n" + GREY);
          return new GameTypes.Config(BOARD_SIZES.get(GameModeTypes.S), SHIP_CONFIGS.get(GameModeTypes.S));
        }
        case "medium"-> {
          System.out.println(GREEN + "Gamemode locked in. \n\n" + GREY);
          return new GameTypes.Config(BOARD_SIZES.get(GameModeTypes.M), SHIP_CONFIGS.get(GameModeTypes.M));
        }
        case "large" -> {
          System.out.println(GREEN + "Gamemode locked in. \n\n" + GREY);
          return new GameTypes.Config(BOARD_SIZES.get(GameModeTypes.L), SHIP_CONFIGS.get(GameModeTypes.L));
        }
        default -> outputInvalidCommand();
      }
    }
  }

  @Override
  public String[] inputPlayerNames() {
    while (true) { 
      out.println("Do you want to use custom usernames? (y/n) " + RESET);
      String input = scanner.nextLine().trim().toLowerCase();

      if(input.startsWith("y")) {
        String playerOneName = "";
        String playerTwoName = "";
        boolean isPlayerOneHappy = false;
        boolean isPlayerTwoHappy = false;
        while (!isPlayerOneHappy || !isPlayerTwoHappy) {
          if(!isPlayerOneHappy) {
            out.println(GREY + "\nEnter the name of the first player: " + RESET);
            playerOneName = scanner.nextLine().trim();
            out.println(GREY + "\n" + playerOneName + ". Are you happy with your name? (y/n)" + RESET);

            input = scanner.nextLine().trim().toLowerCase();
            if(input.startsWith("y")) {
              isPlayerOneHappy = true;
              out.println();
            } else if(input.startsWith("n")) {
              continue;
            } else {
              outputInvalidCommand();
              continue;
            }
          }

          if(!isPlayerTwoHappy) {
            out.println(GREY + "\nEnter the name of the second player: " + RESET);

            playerTwoName = scanner.nextLine().trim();
            if(playerTwoName.equals(playerOneName)) {
              out.println(RED + "\nCannot be the same name as the first players name" + GREY);
              continue;
            }
            out.println(GREY + "\n" + playerTwoName + ". Are you happy with your name? (y/n)" + RESET);

            input = scanner.nextLine().trim().toLowerCase();
            if(input.startsWith("y")) {
              isPlayerTwoHappy = true;
            } else {
              outputInvalidCommand();
            }
          }
        }
        return new String[] {playerOneName, playerTwoName};
      } else if(input.startsWith("n")) {
        return new String[] {"Player 1", "Player 2"};
      } else {
        outputInvalidCommand();
      }
    }
  }

  @Override
  public GameTypes.ShipPositionInput inputShipPositions(Battlefield battlefield, int shipsPlacedBefore, int shipsPlaced, int totalShipsToPlace) {
    if(shipsPlacedBefore != shipsPlaced) {
      out.println("You have " + (totalShipsToPlace - shipsPlaced) + " ship(s) left to place in total\n" + GREY);
    }
    out.println("if you want to know each ship you have to place, type 'status'");
    out.println("if you want to know how to remove a ship again, type 'help delete'\n");

    out.println(convertBattlefieldToText(true, battlefield));
    out.print("\nEnter your ships position: " + RESET);


    String input = scanner.nextLine().toLowerCase().trim();
    out.println();

    //TODO: add invalidCommand handling for prefix
    if (input.equals("help")) {
      return new GameTypes.ShipPositionInput("help", 0, 0, 0, "");
    }

    if (input.equals("help delete")) {
      return new GameTypes.ShipPositionInput("help delete", 0, 0 , 0, "");
    }

    if (input.equals("status")) {
      return new GameTypes.ShipPositionInput("status", 0, 0 , 0, "");
    }

    if (input.startsWith("delete ")) {
      while (true) {
        out.println("Are you sure you want to delete this ship? (y/n)");
        String confirmation = scanner.nextLine().toLowerCase().trim();
      
        if (confirmation.startsWith("y")) {
          String formattedInput = input.replace("delete ", "");
          Pattern pattern = Pattern.compile("^(\\d+) (\\d+)$");
          Matcher matcher = pattern.matcher(formattedInput);

          if (matcher.matches()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            return new GameTypes.ShipPositionInput("delete", x, y, 0, "");
          } else {
            outputInvalidCommand();
            out.println(YELLOW + "Removal process aborted" + GREY);
            return new GameTypes.ShipPositionInput("removal aborted", 0, 0, 0, "");
          }
        } else if (confirmation.startsWith("n")) {
          clearScreen();
          out.println(YELLOW + "Removal process aborted" + GREY);
          outputInvalidCommand();
          out.println("(Type 'help' if you dont know how to hit ships)\n");
          return new GameTypes.ShipPositionInput("removal aborted", 0, 0, 0, "");
        } else {
          outputInvalidCommand();
        }
      }
    }

    Pattern defaultPattern = Pattern.compile("^(\\d+) (\\d+) (\\d+) ([hv])$");
    Matcher defaultMatcher = defaultPattern.matcher(input);
    Pattern patternTwo = Pattern.compile("^(\\d+) (\\d+) ([1])$"); //only needed for ships of length 1
    Matcher matcherTwo = patternTwo.matcher(input);

    if (defaultMatcher.matches() || matcherTwo.matches()) {
      int x = defaultMatcher.matches() ? Integer.parseInt(defaultMatcher.group(1)) : Integer.parseInt(matcherTwo.group(1));
      int y = defaultMatcher.matches() ? Integer.parseInt(defaultMatcher.group(2)) : Integer.parseInt(matcherTwo.group(2));
      int length = defaultMatcher.matches() ? Integer.parseInt(defaultMatcher.group(3)) : Integer.parseInt(matcherTwo.group(3));
      String rotation = defaultMatcher.matches() ? defaultMatcher.group(4) : "h";

      return new GameTypes.ShipPositionInput("", x, y, length, rotation);
    } else {
      outputInvalidCommand();
      out.println("\nIf you dont know how to place ships, type 'help'");
      return new GameTypes.ShipPositionInput("", 0, 0, 0, "");
    }
  }

  @Override
  public GameTypes.FieldHitInput inputFieldToHit() {
    out.println("Type the coordinates of the enemies field that you want to hit: ");
    String input = scanner.nextLine().trim().toLowerCase();

    if (input.startsWith("help")) {
      return new GameTypes.FieldHitInput("help", 0, 0);
    }

    Pattern pattern = Pattern.compile("^(\\d+) (\\d+)$");
    Matcher matcher = pattern.matcher(input);
    if(matcher.matches()){
      int x = Integer.parseInt(matcher.group(1));
      int y = Integer.parseInt(matcher.group(2));
      return new GameTypes.FieldHitInput("", x, y);
    } else {
      return new GameTypes.FieldHitInput("hit aborted", 0, 0);
    }
  }

  @Override
  public void outputPromptDeviceHandover(String currentPlayer) {
    out.println( RESET + "\nPlease hand the device to " + currentPlayer);
    out.println("Press enter to continue" +  GREY);
    scanner.nextLine();
    clearScreen();
  }

  @Override
  public void outputInitialShipPlacementMessage() {
    out.println("You can now place ships on your battlefield.\n");
    out.println("If you dont know how to place ships, type 'help'");
  }

  @Override
  public void outputAllShipsSuccessfullyPlaced() {
    clearScreen();
    out.println(GREEN + "All ships sucessfully placed" + GREY);
  }

  @Override
  public void outputShipPlacementHelp() {
    clearScreen();
    out.println(YELLOW + "To place a ship, type in the following format: 'x y length orientation'");
    out.println("The Coordinatesystem starts at 1 1");
    out.println("x and y are the coordinates of the starting point of the ship, length is the length of the ship and orientation is either 'h' for horizontal or 'v' for vertical\n");
    out.println("Example: '1 1 3 h' would place a ship of length 3 horizontally starting at the bottom left corner of the battlefield");
    out.println("NOTE: you dont have to include the orientation when placing a ship of length 1");
    out.println("   Example: '3 3 1' would place a ship of length 1 at coordinates 3 3");
    out.println("You can only place ships within the bounds of the battlefield and they cannot overlap\n" + GREY);
  }
  
  @Override
  public void outputShipDeletionHelp() {
    clearScreen();
    out.println(YELLOW + "To delete a ship, type in the following format: 'x y' (x and y can be any position of the ship you want to delete)");
    out.println("Example: You placed a ship with the command '1 1 2 h', meaning the positions of the ship are '1 1' and '2 1'");
    out.println("To delete said ship, you can now type 'delete 1 1', or 'delete 2 1' (both work)\n" + GREY);
  }
  
  @Override
  public void outputShootingHelp() {
    clearScreen();
    out.println(YELLOW + "The format is as follows: 'x y'");
    out.println("The Coordinatesystem starts at 1 1");
    out.println("Example: You want to shoot at the field with x:1 y:1, then you just type '1 1'\n" + GREY);
  }

  @Override
  public void outputShipPlacementStatus(Map<Integer, Integer> shipsLeftToPlace) {
    clearScreen();
    out.print(YELLOW);
    shipsLeftToPlace.forEach((length, amount) -> {
      out.println("You have " + amount + " ship(s) of length " + length + " left to place");
    });
    out.println(GREY);
  }

  @Override
  public void outputOutOfBounds() {
    clearScreen();
    System.out.println(RED + "Coordinates go outside the battleship. Try again!" + GREY);
  }

  @Override
  public void outputOverlapping() {
    clearScreen();
    System.out.println(RED + "Ship overlaps with another. Try again!" + GREY);
  }

  @Override
  public void outputSuccessfulShipPlacement(Map<Integer, Integer> shipsLeftToPlace, int length) {
    clearScreen();
    out.println(GREEN + "Ship placed successfully" + YELLOW);
    //TODO: insert if, so it doesnt show when all ships have been placed
    out.println("You have " + shipsLeftToPlace.get(length) + " ship(s) of length " + length + " left to place");
  }

  @Override
  public void outputShipRemovalConfirmation() {
    clearScreen();
    out.println(GREEN + "Ship removed successfully\n" + GREY);
  }

  @Override
  public void outputInvalidShipSize() {
    clearScreen();
    out.println(RED + "No ships of this size can be placed\n" + GREY);
  }
  
  @Override
  public void outputBattlefields(String playerOneName, String playerTwoName, Battlefield playerOneBattlefield, Battlefield playerTwoBattlefield, boolean isPlayerOneTurn) {
    String[] currentPlayersBoard;
    String[] enemyBoard;
    String currentPlayersBoardHeader;
    String enemyBoardHeader;

    if (isPlayerOneTurn) {
      currentPlayersBoard = convertBattlefieldToText(playerOneBattlefield).split("\n");
      enemyBoard = convertBattlefieldToText(playerTwoBattlefield).split("\n");

      currentPlayersBoardHeader = playerOneName;
      enemyBoardHeader = playerTwoName;
    } else {
      currentPlayersBoard = convertBattlefieldToText(playerTwoBattlefield).split("\n");
      enemyBoard = convertBattlefieldToText(playerOneBattlefield).split("\n");

      currentPlayersBoardHeader = playerTwoName;
      enemyBoardHeader = playerOneName;
    }

    currentPlayersBoardHeader += " (you)";
    enemyBoardHeader += " (enemy)";

    int distance = Math.max(playerOneBattlefield.getWidth() * 4, currentPlayersBoardHeader.length()) + 10;

    out.printf("%-" + (distance) + "s%s\n", currentPlayersBoardHeader, enemyBoardHeader);

    for(int i = 0; i < currentPlayersBoard.length; i++) {
      out.printf("%-" + (distance) + "s%s\n", currentPlayersBoard[i], enemyBoard[i]);
    }
  }

  @Override
  public void outputSelectedFieldToShoot(int x, int y) {
    clearScreen();
    out.printf(RESET + "\nYou shot at %d %d *drumroll please*\n\n", x, y);
    try {Thread.sleep(1000); }
    catch (InterruptedException e) {}
  }

  @Override
  public void outputEmptyHit() {
    out.println("But no ship was hit :(\n" + GREY);
  }

  @Override
  public void outputShipHit(boolean wasSunken, int shipsHitStreak) {
    if (wasSunken) {
      out.println("You have sunk a ship!");
    } else {
      out.println("You hit a ship!");
    }

    out.println("Your current Hitstreak is " + shipsHitStreak);
    out.println("You can shoot once more!\n" + GREY);
  }

  @Override
  public void outputWinner(String winner) {
    out.println(GREEN + "You sunk the last ship!\n");
    out.print(YELLOW + winner + " has won!" + RESET);
  }
  
  private void clearScreen() {
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
      out.flush();
    }
  }

  @Override
  public void outputInvalidCommand() {
    clearScreen();
    out.println(RED + "Invalid Command. Try again!\n\n" + GREY);
  }

  private void outputGameModeInfo(String chosenGameMode) {
    switch (chosenGameMode) {
      case "small" -> {
        out.println(YELLOW + "This Gamemode is designed for short fights.");
        out.println("The battlefield is 5x5 and each player has the following ships:");
        out.println("2 ships of length 1");
        out.println("1 ship  of length 2\n\n" + GREY);
      }
      case "medium" -> {
        out.println(YELLOW + "This Gamemode is the standard version that most players know.");
        out.println("The battlefield is 10x10 and each player has the following ships:");
        out.println("4 ships of length 2");
        out.println("3 ships of length 3");
        out.println("2 ships of length 4");
        out.println("1 ship  of length 5 \n\n" + GREY);
      }
      case "large" -> {
        out.println(YELLOW + "This Gamemode is designed for long fights.");
        out.println("The battlefield is 15x15 and each player has the following ships:");
        out.println("2 ships of length 1");
        out.println("3 ships of length 2");
        out.println("3 ships of length 3");
        out.println("3 ships of length 4");
        out.println("2 ships of length 5");
        out.println("1 ship  of length 6 \n\n" + GREY);
      }
      default -> outputInvalidCommand();
    }  
  }

  //TODO: überlegen, ob vom eigenen Feld auch in der Hitphase noch die Schiffe gesehen werden können sollen oder nicht
  private String convertBattlefieldToText(boolean showHiddenShips, Battlefield battlefield) {
    Function<String, String> mapFieldToText = (field) -> {
      if ("emptyHit".equals(field)) return "x ";
      
      int parsedField = battlefield.parseShipId(field);
      if (showHiddenShips && parsedField >= 0) {
          return parsedField < 10 ? field + " " : field;
      }

      if (field.startsWith("shipHit_")) {
          field = field.replaceAll("[^0-9]", "");

          return parsedField < 10 ? field + " " : field;
      }

      return "  ";
    };

    String [][] coordinateSystem = battlefield.getCoordinateSystem();
    String[] mappedBattlefieldRows = new String[coordinateSystem.length];
    String[] textBlocks = {"+", "---", "|"};

    for (int i = 0; i < coordinateSystem.length; i++) {
      int rowIndex = coordinateSystem.length - i - 1;

      StringBuilder rowSb = new StringBuilder();

      rowSb.append(textBlocks[2]);

      for (int colIndex = 0; colIndex < coordinateSystem.length; colIndex++) {
          String convertedField = mapFieldToText.apply(coordinateSystem[colIndex][rowIndex]);
          rowSb.append(String.format(" %s %s", convertedField, textBlocks[2]));
      }

      mappedBattlefieldRows[i] = rowSb.toString();
    }

    String lineSeparator =
      textBlocks[0] +
      (textBlocks[1] + " " + textBlocks[0]).repeat(coordinateSystem.length);

    StringBuilder sb = new StringBuilder();

    sb.append(lineSeparator).append("\n");

    for (String mappedRow: mappedBattlefieldRows) {
      sb.append(mappedRow).append("\n");
      sb.append(lineSeparator).append("\n");
    }

    return sb.toString();
  }

  private String convertBattlefieldToText(Battlefield battlefield) {
    return convertBattlefieldToText(false, battlefield);
  }
}
