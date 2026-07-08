import java.util.stream.IntStream;

public class Ship {
  final int x;
  final int y;
  final int length;
  final boolean isHorizontal;

  public Ship(int x, int y, int length, boolean isHorizontal) {
    this.x = x - 1;
    this.y = y - 1;
    this.length = length;
    this.isHorizontal = isHorizontal;
  }

  public int[] getPositions() {
    int[] positions = new int[length];
    IntStream.range(0, length)
            .forEach(i -> positions[i] = isHorizontal ? x + i : y + i);
    return positions;
  }
}
