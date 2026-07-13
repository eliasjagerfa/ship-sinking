package game;

import static java.lang.System.out;
import java.util.HashMap;
import java.util.Map;

public class Game {
  private final Battlefield playerOneBattlefield;
  private final Battlefield playerTwoBattlefield;
  private boolean isPlayerOneTurn;
  private String playerOneName;
  private String playerTwoName;
  private final IO io;
  private int totalShipsToPlace  = 0;
  private final Map<Integer, Integer> shipsToPlace;


  public Game(GameTypes.Config gameMode, IO io) {
    this.isPlayerOneTurn = true;
    this.io = io;
    shipsToPlace = new HashMap<>(gameMode.shipConfig());
    shipsToPlace.values().forEach((amount) -> {
      totalShipsToPlace += amount;
    });

    this.playerOneBattlefield = new Battlefield(gameMode.size(), shipsToPlace);
    this.playerTwoBattlefield = new Battlefield(gameMode.size(), shipsToPlace);
  }

  public void startShipPlacement() {
    for (int i = 0; i < 2; i++) {
      isPlayerOneTurn = i == 0;
      String currentPlayer = isPlayerOneTurn ? playerOneName : playerTwoName;
      Battlefield currentBattleField = isPlayerOneTurn ? playerOneBattlefield : playerTwoBattlefield;

      if (!isPlayerOneTurn) io.outputAllShipsSuccessfullyPlaced();
      io.outputPromptDeviceHandover(currentPlayer);
      Map<Integer, Integer> shipsLeftToPlace = new HashMap<>(shipsToPlace);
      int shipsPlaced = 0;
      int shipsPlacedBefore = 0;

      io.outputInitialShipPlacementMessage();

      while (totalShipsToPlace != shipsPlaced) {
        GameTypes.ShipPositionInput result = io.inputShipPositions(currentBattleField, shipsPlacedBefore, shipsPlaced, totalShipsToPlace);
        shipsPlacedBefore = shipsPlaced;

        switch (result.prefix()) {
          case "help" -> {
            io.outputShipPlacementHelp();
            continue;
          }
          case "help delete" -> {
            io.outputShipDeletionHelp();
            continue;
          }
          case "status" -> {
            io.outputShipPlacementStatus(shipsLeftToPlace);
            continue;
          }
          case "removal aborted" -> {
            continue;
          }
          case "delete" -> {
            //TODO: Outputhandling, when there is no ship or the coordinates are outOfBounds
            currentBattleField.removeShip(result.x(), result.y());
            continue;
          }
        }

        int amountShipOfLength = shipsLeftToPlace.getOrDefault(result.length(), 0);
        if (amountShipOfLength > 0) {
          Ship newShip = new Ship(result.x(), result.y(), result.length(), result.rotation().equals("h"));  

          GameTypes.ShipPositionValidationResult validationResult = currentBattleField.addShip(newShip, shipsPlaced);
          
          if (validationResult.isOutOfBounds()) {
            io.outputOutOfBounds();
            continue;
          } else if (validationResult.isOverlapping()) {
            io.outputOverlapping();
            continue;
          }

          shipsPlaced++;
          shipsLeftToPlace.merge(result.length(), -1, Integer::sum);

          if (shipsPlaced != totalShipsToPlace) io.outputSuccessfulShipPlacement(shipsLeftToPlace, result.length());
        } else {
          io.outputInvalidShipSize();
        }
      }
    }
    io.outputAllShipsSuccessfullyPlaced();
    isPlayerOneTurn = true;
  }

  public void startGame() {
    setPlayerNames();
    startShipPlacement();

    while(true){
      String currentPlayer = isPlayerOneTurn ? playerOneName : playerTwoName;

      io.outputPromptDeviceHandover(currentPlayer);

      boolean hasWon = doTurn();
      if (hasWon) break;

      isPlayerOneTurn = !isPlayerOneTurn;
    }

    String winner = isPlayerOneTurn ? playerOneName : playerTwoName;
    io.outputWinner(winner);
  }

  public boolean doTurn() {
    int shipsHitStreak = 0;

    out.println("(Type 'help' if you dont know how to hit ships)\n");
    while (true) {
      io.outputBattlefields(playerOneName, playerTwoName, playerOneBattlefield, playerTwoBattlefield, isPlayerOneTurn);
      GameTypes.FieldHitInput inputResult = io.inputFieldToHit();

      if (inputResult.prefix().equals("help")){
        io.outputShootingHelp();
        continue;
      }

      if (inputResult.prefix().equals("hit aborted")) {
        continue;
      }

      GameTypes.HitShipResult hitShipResult;

      try {
        if (isPlayerOneTurn) {
          hitShipResult = playerTwoBattlefield.hitField(inputResult.x(), inputResult.y());
        } else {
          hitShipResult = playerOneBattlefield.hitField(inputResult.x(), inputResult.y());
        }
      } catch (Exception e) {
        //TODO: adjust record to include isOutOfBounds and isAlreadyShotAt and handle the output accordingly
        io.outputInvalidCommand();
        continue;
      }

      io.outputSelectedFieldToShoot(inputResult.x(), inputResult.y());
      
      if(hitShipResult.newFieldValue().equals("emptyHit")) {
        io.outputEmptyHit();
        return false;

      } else if (hitShipResult.isShipHit()) {
        shipsHitStreak++;

        if (playerTwoBattlefield.allAreSunken() || playerOneBattlefield.allAreSunken()) return true;

        io.outputShipHit(hitShipResult.isShipSunken(), shipsHitStreak);
      }   
    }
  }

  //TODO: maybe characterlimit auf 10 setzen?
  public void setPlayerNames() {
    String[] playerNames = io.inputPlayerNames();

    playerOneName = playerNames[0];
    playerTwoName = playerNames[1];
  }
}