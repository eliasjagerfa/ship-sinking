public class GameTypes {
  public record DoTurnResult(
    boolean wasPlayerOneTurn,
    int shipsHitStreak,
    boolean hasWon
  ) {}
}
