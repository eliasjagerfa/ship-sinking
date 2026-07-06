import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    try (Scanner globalScanner = new Scanner(System.in)) {
    GameMode gameMode = setGameMode(globalScanner);
    System.out.println("game mode locked in \n\n");

    Game myGame = new Game(gameMode, globalScanner);
    myGame.setPlayerNames();

    myGame.startShipPlacement();
    
    myGame.startGame();
    }
  }


  public static GameMode setGameMode(Scanner globalScanner) {

    while(true) {
      System.out.println("\nType your desired game mode");
      System.out.println("The following options are available: small, medium, large");
      System.out.println("(If you want to know the specifications for any of the gameModes, type 'info <your gameMode>'. Example: 'info small')");
      String input = globalScanner.nextLine().trim().toLowerCase();
      System.out.println();
      
      if (input.startsWith("info ")) {
        String chosengameMode = input.split(" ")[1];
        ConsoleOutput.printGameModeInfo(chosengameMode);
        continue;
      }
      switch(input) {
        case "small" -> { return new GameMode(input); }
        
        case "medium" -> { return new GameMode(input); }
        
        case "large" -> { return new GameMode(input); }

        default -> ConsoleOutput.printInvalidCommand();
      }
    }
  }
}
