import java.util.ArrayList;

public class GameTemplate {
  private int battleFieldWidth;
  private int battleFieldHeight;
  private ArrayList<ShipConfig> shipConfig = new ArrayList<>();

  public GameTemplate(String chosenTemplate) {
    switch(chosenTemplate) {
      case "small" -> {
        battleFieldWidth = 5;
        battleFieldHeight = 5;
        shipConfig.add(new ShipConfig(1, 2));
        shipConfig.add(new ShipConfig(2, 1));
      }
      case "medium"-> {
        battleFieldWidth = 10;
        battleFieldHeight = 10;
        shipConfig.add(new ShipConfig(2, 4));
        shipConfig.add(new ShipConfig(3, 3));
        shipConfig.add(new ShipConfig(4, 2));
        shipConfig.add(new ShipConfig(5, 1));
      }
      case "large" -> {
      battleFieldWidth = 15;
        battleFieldHeight = 15;
        shipConfig.add(new ShipConfig(1, 2));
        shipConfig.add(new ShipConfig(2, 3));
        shipConfig.add(new ShipConfig(3, 3));
        shipConfig.add(new ShipConfig(4, 3));
        shipConfig.add(new ShipConfig(5, 2));
        shipConfig.add(new ShipConfig(6, 1));
      }
    }
  }

  public int getWidth() {
    return battleFieldWidth;
  }

  public int getHeight() {
    return battleFieldHeight;
  }

  public ArrayList<ShipConfig> getConfig() {
    return shipConfig;
  }
}
