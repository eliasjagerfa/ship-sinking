public class GameTypes {
  public record DoTurnResult(
    boolean wasPlayerOneTurn,
    int shipsHitStreak,
    boolean hasWon
  ) {}

  public record HitShipResult(
    String oldFieldValue,
    String newFieldValue,
    boolean isShipHit,
    boolean isShipSunken
  ) {}

  public record shipCoordinate(
    int x,
    int y
  ) {}

  public record ShipPositionValidationResult(
    boolean isOutOfBounds,
    boolean isOverlapping
  ) {}
}
