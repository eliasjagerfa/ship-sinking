import java.util.ArrayList;
import java.util.Scanner;


public class Main {
  public static void main(String[] args) {
    //TODO: battlefieldconfigs have to be added and be done BEFORE the shipconfigs (smallest battlefield should be at least 20 units in both directions, maybe even more)
    Scanner scanner = new Scanner(System.in);
    int totalBattleFieldCount = 0; //TODO: add better naming, its supposed to be the fields of the battlefields
    int totalShipFields = 0;
    ArrayList<ShipConfig> shipConfigs = new ArrayList<>();

    /*while(true) { 
      out.println("Enter the ship(s) you want to play with. (if you dont know how to configure ships, type 'help')"); //TODO: give better instructions
      String input = scanner.nextLine().trim().toLowerCase();

      if(input.equals("help")) {
        out.println("To configure a ship, type the following format: 'length of the ship, amount of the ships with this length'");
        out.println("Example: '3 2'  would mean 2 ships with the length of 3 that have to be placed later \n");
        continue;
      }

    if(totalShipFields > totalBattleFieldCount - 20) { //TODO: add logic for totalShipFields and the -x shouldnt be too high and not too low --> user doesnt accidentally brick himself with placing the ships to clog the battlefield but should have enough freedom
        out.println("Max Fields potentially occupied by ships reached"); //TODO: if it overdoes it, then the last config shouldnt be saved, therefore the check needs to happen somewhere else 
        break;
      }
      
      Pattern pattern = Pattern.compile("^(\\d+) (\\d+)$");
      Matcher matcher = pattern.matcher(input);

      if(matcher.matches()) {
        shipConfigs.add(new ShipConfig(
          Integer.parseInt(matcher.group(1)), 
          Integer.parseInt(matcher.group(2))
        ));
      } else {
        out.println("Wrong command. Try again!");
      }

      out.println("Do you want to go on with the configurations (y/n)? "); //TODO: Add error handling in case of a typo
      boolean wantsToStopConfiguration = scanner.nextLine().trim().toLowerCase().equals("n");

      if(wantsToStopConfiguration) {
        if(shipConfigs.isEmpty()) {
          out.print("Configure at least one ship, so the game can start");
        } else {
          break;
        }
      }
    }*/
    //scanner.close();

    shipConfigs.add(new ShipConfig(2, 2));
    shipConfigs.add(new ShipConfig(3, 3));
    shipConfigs.add(new ShipConfig(2, 3));
    Game myGame = new Game(20, 20, shipConfigs); 
    myGame.startShipPlacement();
  }
}
