import static java.lang.System.out;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    GameTemplate template;
    String playerOneName = "Player 1";
    String playerTwoName = "Player 2";
    
    while (true) { 
      out.println("Do you want to use custom usernames? (y/n) ");
      String input = scanner.nextLine().trim().toLowerCase();

      if(input.startsWith("y")) {
        boolean isPlayerOneHappy = false;
        boolean isPlayerTwoHappy = false;
        while (true) {
          if(!isPlayerOneHappy) {
            out.println("\nEnter the name of the first player: ");
            playerOneName = scanner.nextLine().trim();
            out.println("\n" + playerOneName + ". Are you happy with your name? (y/n)");

            input = scanner.nextLine().trim().toLowerCase();
            if(input.startsWith("y")) {
              isPlayerOneHappy = true;
              out.println();
            } else if(input.startsWith("n")) {
              continue;
            } else {
              out.println("Wrong input. Try again!");
              continue;
            }
          }

          if(!isPlayerTwoHappy) {
            out.println("\nEnter the name of the second player: ");
            playerTwoName = scanner.nextLine().trim();
            if(playerTwoName.equals(playerOneName)) {
              out.println("Cannot be the same name as the first players name");
              continue;
            }
            out.println("\n" + playerTwoName + ". Are you happy with your name? (y/n)");

            input = scanner.nextLine().trim().toLowerCase();
            if(input.startsWith("y")) {
              isPlayerTwoHappy = true;
            } else if(input.startsWith("n")) {
              continue;
            } else {
              out.println("Wrong input. Try again!");
              continue;
            }
          }

          out.println("Are you sure you want to play with the following names (y/n)? First Player: " + playerOneName + ", Second Player: " + playerTwoName);
          input = scanner.nextLine().trim().toLowerCase();

          if(input.startsWith("y")) {
            break;
          } else if(input.startsWith("n")) {
            isPlayerOneHappy = false;
            isPlayerTwoHappy = false;
          } else {
            break;
          }
        }
      } else if(input.startsWith("n")) {
        playerOneName = "Player 1";
        playerTwoName = "Player 2";
        break;
      } else {
        out.println("Wrong command. Try again!");
      }
    break;
    }

    outer: while(true) {
      out.println("\nType your desired Gamemode");
      out.println("The following options are available: small, medium, large");
      out.println("(If you want to know the specifications for any of the templates, type 'info <your template>'. Example: 'info small')");
      String input = scanner.nextLine().trim().toLowerCase();
      out.println();
      
      switch(input) {
        case "info small" -> {
          out.println("This template is designed for short fights.");
          out.println("The battlefield is 5x5 and each player has the following ships:");
          out.println("2 ships of length 1");
          out.println("1 ship  of length 2 \n\n");
        }
        case "info medium" -> {
          out.println("This is the standard battlefield that most players know.");
          out.println("The battlefield is 10x10 and each player has the following ships:");
          out.println("4 ships of length 2");
          out.println("3 ships of length 3");
          out.println("2 ships of length 4");
          out.println("1 ship  of length 5 \n\n");
        }
        case "info large" -> {
          out.println("This template is designed for long fights.");
          out.println("The battlefield is 15x15 and each player has the following ships:");
          out.println("2 ships of length 1");
          out.println("3 ships of length 2");
          out.println("3 ships of length 3");
          out.println("3 ships of length 4");
          out.println("2 ships of length 5");
          out.println("1 ship  of length 6 \n\n");
        }
        case "small" -> {
          template = new GameTemplate(input);
          break outer;
        }
        case "medium" -> {
          template = new GameTemplate(input);
          break outer;
        }
        case "large" -> {
          template = new GameTemplate(input);
          break outer;
        }
        default -> {
          out.println("Invalid Command. Try again!\n\n");
        }
      }
    }

    Game myGame = new Game(template, playerOneName, playerTwoName); 
    myGame.startShipPlacement();
    
    myGame.startGame();
    scanner.close();
  }
}
