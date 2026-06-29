import static java.lang.System.out;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    GameTemplate template;
    
    outer: while(true) {
      out.println("Type your desired Gamemode");
      out.println("The following options are available: small, medium, large");
      out.println("(If you want to know the specifications for any of the templates, type 'info <your template>'. Example: 'info small')");
      String input = scanner.nextLine().trim().toLowerCase();
      out.println();
      
      switch(input) {
        case "info small" -> {
          out.println("This template is designed for short fights.");
          out.println("The battlefield is 5x5 and each player has the following ships:");
          out.println("2 ships of length 1");
          out.println("1 ship of length 2 \n\n");
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

    Game myGame = new Game(template); 
    myGame.startShipPlacement();
    
    myGame.startGame();
    scanner.close();
  }
}
