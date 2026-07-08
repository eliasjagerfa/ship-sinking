import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GameMode {
  private int battleFieldWidth;
  private int battleFieldHeight;
  private final Map<Integer, Integer> shipConfig = new HashMap<>(); //key is length of ship, value is amount of this length

  public GameMode(Scanner globalScanner) {
    setGameMode(globalScanner);
  }

  public int getWidth() {
    return battleFieldWidth;
  }

  public int getHeight() {
    return battleFieldHeight;
  }

  public Map<Integer, Integer> getConfig() {
    return shipConfig;
  }

  private void setGameMode(Scanner globalScanner) {
    boolean noGameModeSelected = true;
    while(noGameModeSelected) {
      System.out.println(ConsoleOutput.GREY + "\nType your desired game mode");
      System.out.println("The following options are available: small, medium, large");
      System.out.println("(If you want to know the specifications for any of the gameModes, type 'info <your gameMode>'. Example: 'info small')" + ConsoleOutput.RESET);
      String input = globalScanner.nextLine().trim().toLowerCase();
      System.out.println();
      
      if (input.startsWith("info ")) {
        String chosengameMode = input.split(" ")[1];
        ConsoleOutput.printGameModeInfo(chosengameMode);
        continue;
      }
      switch(input) {
        case "small" -> {
          battleFieldWidth = 5;
          battleFieldHeight = 5;
          shipConfig.put(1, 2);
          shipConfig.put(2, 1);
          noGameModeSelected = false;
        }
        case "medium"-> {
          battleFieldWidth = 10;
          battleFieldHeight = 10;
          shipConfig.put(2, 4);
          shipConfig.put(3, 3);
          shipConfig.put(4, 2);
          shipConfig.put(5, 1);
          noGameModeSelected = false;
        }
        case "large" -> {
          battleFieldWidth = 15;
          battleFieldHeight = 15;
          shipConfig.put(1, 2);
          shipConfig.put(2, 3);
          shipConfig.put(3, 3);
          shipConfig.put(4, 3);
          shipConfig.put(5, 2);
          shipConfig.put(6, 1);
          noGameModeSelected = false;
        }
        default -> ConsoleOutput.printInvalidCommand();
      }
    }
  }
}
