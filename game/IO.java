package game;

import java.util.Map;

interface IO {
    GameTypes.Config inputGameConfig();

    String[] inputPlayerNames();

    GameTypes.ShipPositionInput inputShipPositions(Battlefield battlefield, int shipsPlacedBefore, int shipsPlaced, int totalShipsToPlace);

    GameTypes.FieldHitInput inputFieldToHit();

    void outputPromptDeviceHandover(String currentPlayer);

    void outputInitialShipPlacementMessage();

    void outputAllShipsSuccessfullyPlaced();

    void outputShipPlacementHelp();

    void outputShipDeletionHelp();

    void outputShootingHelp();

    void outputShipPlacementStatus(Map<Integer, Integer> shipsLeftToPlace);

    void outputOutOfBounds();

    void outputOverlapping();

    void outputNoShipToRemove();

    void outputSuccessfulShipPlacement(Map<Integer, Integer> shipsLeftToPlace, int length);

    void outputShipRemovalConfirmation();

    void outputInvalidShipSize();

    void outputBattlefields(String playerOneName, String playerTwoName, Battlefield playerOneBattlefield, Battlefield playerTwoBattlefield, boolean isPlayerOneTurn);

    void outputSelectedFieldToShoot(int x, int y);

    void outputEmptyHit();

    void outputShipHit(boolean wasSUnken, int shipsHitStreak);

    void outputWinner(String winner);

    void outputInvalidCommand();
}
