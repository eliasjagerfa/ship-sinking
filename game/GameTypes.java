package game;

import java.util.Map;

public class GameTypes {
  public record HitShipResult(
    String oldFieldValue,
    String newFieldValue,
    boolean isShipHit,
    boolean isShipSunken
  ) {}

  public record ShipPositionInput(
    String prefix,
    int x,
    int y,
    int length,
    String rotation
  ) {}

  public record FieldHitInput(
    String prefix,
    int x,
    int y
  ) {}

  public record ShipPositionValidationResult(
    boolean isOutOfBounds,
    boolean isOverlapping
  ) {}

  public record shipCoordinate(
    int x,
    int y
  ) {}

  public record Config(
    int size,
    Map<Integer, Integer> shipConfig
  ) {}
}
