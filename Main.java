
import java.util.ArrayList;

public class Main {
  public static void main(String[] args) {
      ArrayList<ShipConfig> shipConfigs = new ArrayList<>();

      shipConfigs.add(new ShipConfig(3, 2));

      Game myGame = new Game(4, 4, shipConfigs);

      myGame.startShipPlacement();
  }
}
