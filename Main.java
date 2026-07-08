import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    try (Scanner globalScanner = new Scanner(System.in)) {
    GameMode gameMode = new GameMode(globalScanner);
    System.out.println(ConsoleOutput.GREEN + "Gamemode locked in. \n\n" + ConsoleOutput.GREY);

    Game myGame = new Game(gameMode, globalScanner);
    myGame.setPlayerNames();

    myGame.startShipPlacement();
    
    myGame.startGame();
    }
  }


  
}
