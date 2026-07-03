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

  public record Point(
    int x,
    int y
  ) {}
}
