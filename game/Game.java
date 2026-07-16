package game;

import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Game {
  private final Battlefield playerOneBattlefield;
  private final Battlefield playerTwoBattlefield;
  private boolean isPlayerOneTurn;
  private String playerOneName;
  private String playerTwoName;
  private final IO io;
  private int totalShipsToPlace  = 0;
  private final Map<Integer, Integer> shipsToPlace;

  GameTypes.Config gameMode;


  public Game(GameTypes.Config gameMode, IO io) {
    this.gameMode = gameMode;
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

      
      int nextShipId = 1;
      List<Ship> shipsLeftToPlace = new ArrayList<>();
      for (var c : gameMode.shipConfig().entrySet()) {
        for (var j = 0; j < c.getValue(); j++) {
          shipsLeftToPlace.add(new Ship(0, 0, c.getKey(), true, nextShipId++));
        }        
      }

      int shipsPlaced = 0;
      int shipsPlacedBefore = 0;

      io.outputInitialShipPlacementMessage();

      while (!shipsLeftToPlace.isEmpty()) {
        GameTypes.ShipPositionInput inputResult = io.inputShipPositions(currentBattleField, shipsPlacedBefore, shipsPlaced, totalShipsToPlace);

        shipsPlacedBefore = shipsPlaced;

        switch (inputResult.prefix()) {
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
          case "placement aborted" -> {
            continue;
          }
          case "delete" -> {
            GameTypes.RemovalResult removalResult = currentBattleField.removeShip(inputResult.x(), inputResult.y());

            if (removalResult.isOutOfBounds()) {
              io.outputOutOfBounds();
            } else if (removalResult.isEmptyField()) {
              io.outputNoShipToRemove();
            } else {
              shipsLeftToPlace.add(new Ship(0, 0, removalResult.length(), true, removalResult.freedShipId()));
              shipsLeftToPlace.sort(Comparator.comparingInt(ship -> ship.id));
              shipsPlaced--;
            }
            continue;
          }
        }

        if (inputResult.length() == 0) continue;

        Optional<Ship> currentShipOpt = shipsLeftToPlace.stream().filter(ship -> ship.length == inputResult.length()).findAny();
        if (currentShipOpt.isEmpty()) io.outputInvalidShipSize();
        Ship currentShip = currentShipOpt.get();          

        currentShip.setX(inputResult.x());
        currentShip.setY(inputResult.y());
        currentShip.setIsHorizontal(inputResult.rotation().equals("h"));

        GameTypes.ShipPositionValidationResult validationResult = currentBattleField.addShip(currentShip, shipsPlaced);

        if (validationResult.isOutOfBounds()) {
          io.outputOutOfBounds();
          continue;
        } else if (validationResult.isOverlapping()) {
          io.outputOverlapping();
          continue;
        }

        shipsPlaced++;
        shipsLeftToPlace.remove(currentShip);

        if (!shipsLeftToPlace.isEmpty()) io.outputSuccessfulShipPlacement(shipsLeftToPlace, inputResult.length());
        
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

      
      if (isPlayerOneTurn) {
        hitShipResult = playerTwoBattlefield.hitField(inputResult.x(), inputResult.y());
      } else {
        hitShipResult = playerOneBattlefield.hitField(inputResult.x(), inputResult.y());
      }
    
      if (hitShipResult.isOutOfBounds()) {
        io.outputOutOfBounds();
        continue;
      }

      if (hitShipResult.isAlreadyShotAt()) {
        io.outputAlreadyShotAt();
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

  public void setPlayerNames() {
    String[] playerNames = io.inputPlayerNames();

    playerOneName = playerNames[0];
    playerTwoName = playerNames[1];
  }
}