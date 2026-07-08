import java.io.IOException;
import static java.lang.System.out;

public class ConsoleOutput {
  public static final String GREY = "\u001b[90m"; //Standard color
  public static final String RESET = "\u001B[0m";
  public static final String RED = "\u001B[31m";
  public static final String GREEN = "\u001B[32m";
  public static final String YELLOW = "\u001b[33m";
  
    
  public static void printGameModeInfo(String chosengameMode) {
    switch (chosengameMode) {
      case "small" -> {
        out.println(YELLOW + "This Gamemode is designed for short fights.");
        out.println("The battlefield is 5x5 and each player has the following ships:");
        out.println("2 ships of length 1");
        out.println("1 ship  of length 2 \n\n" + GREY);
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
        out.println(YELLOW + "This game mode is designed for long fights.");
        out.println("The battlefield is 15x15 and each player has the following ships:");
        out.println("2 ships of length 1");
        out.println("3 ships of length 2");
        out.println("3 ships of length 3");
        out.println("3 ships of length 4");
        out.println("2 ships of length 5");
        out.println("1 ship  of length 6 \n\n" + GREY);
      }
      default -> {
        printInvalidCommand();
      }
    }
  }

  public static void printShipPlacementHelp() {
    out.println(YELLOW + "To place a ship, type in the following format: 'x y length orientation'");
    out.println("The coordinates start at 1");
    out.println("x and y are the coordinates of the starting point of the ship, length is the length of the ship and orientation is either 'h' for horizontal or 'v' for vertical\n");
    out.println("Example: '1 1 3 h' would place a ship of length 3 horizontally starting at the bottom left corner of the battlefield");
    out.println("NOTE: you dont have to include the orientation when placing a ship of length 1");
    out.println("   Example: '3 3 1' would place a ship of length 1 at coordinates 3 3");
    out.println("You can only place ships within the bounds of the battlefield and they cannot overlap\n" + GREY);
  }

  public static void printShootingHelp() {
    out.println(YELLOW + "The format is as follows: 'x y'");
    out.println("The coordinate system starts at 1 1");
    out.println("Example: You want to shoot at the field with x:1 y:1, then you just type '1 1'\n" + GREY);
  }

  public static void printInvalidCommand() {
    out.println(ConsoleOutput.RED + "Invalid Command. Try again!\n\n" + ConsoleOutput.GREY);
  }

  static void clearScreen() {
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
