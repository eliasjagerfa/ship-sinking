import java.util.HashMap;
import java.util.Map;

public class GameMode {
  private int battleFieldWidth;
  private int battleFieldHeight;
  private Map<Integer, Integer> shipConfigs = new HashMap<>(); //key is length of ship, value is amount of this length

  public GameMode(String chosengameMode) {
    switch(chosengameMode) {
      case "small" -> {
        battleFieldWidth = 5;
        battleFieldHeight = 5;
        shipConfigs.put(1, 2);
        shipConfigs.put(2, 1);
      }
      case "medium"-> {
        battleFieldWidth = 10;
        battleFieldHeight = 10;
        shipConfigs.put(2, 4);
        shipConfigs.put(3, 3);
        shipConfigs.put(4, 2);
        shipConfigs.put(5, 1);
      }
      case "large" -> {
      battleFieldWidth = 15;
        battleFieldHeight = 15;
        shipConfigs.put(1, 2);
        shipConfigs.put(2, 3);
        shipConfigs.put(3, 3);
        shipConfigs.put(4, 3);
        shipConfigs.put(5, 2);
        shipConfigs.put(6, 1);
      }
    }
  }

  public int getWidth() {
    return battleFieldWidth;
  }

  public int getHeight() {
    return battleFieldHeight;
  }

  public Map<Integer, Integer> getConfigs() {
    return shipConfigs;
  }
}
