import java.util.ArrayList;

public class Main {
  public static void main(String[] args) {
    ArrayList<ShipConfig> shipConfigs = new ArrayList<>();

    /*shipConfigs.add(new ShipConfig(1, 4));
    shipConfigs.add(new ShipConfig(2, 3));
    shipConfigs.add(new ShipConfig(3, 2));*/
    shipConfigs.add(new ShipConfig(4, 1));
    Game myGame = new Game(10, 10, shipConfigs); 
    myGame.startShipPlacement();
    
    myGame.startGame();
  }
}
