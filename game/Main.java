package game;

public class Main {
  IO io = new ConsoleIO();

  private void run() {
    GameTypes.Config gameMode = io.inputGameConfig();
    Game myGame = new Game(gameMode, io); //TODO: Mit Tomas besprechen (des io Thema)
    myGame.startGame();
  }
    
  public static void main(String[] args) {
    new Main().run();  
  } 
}
