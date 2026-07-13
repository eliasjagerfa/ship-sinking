package game;

public class Main {
  IO io = new ConsoleIO();

  private void run() {
    GameTypes.Config gameMode = io.inputGameConfig();
    Game myGame = new Game(gameMode, io);
    myGame.startGame();
  }
    
  public static void main(String[] args) {
    new Main().run();  
  } 
}
