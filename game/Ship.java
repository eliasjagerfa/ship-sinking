package game;

public class Ship {
  public final int length;
  public final int id;
  
  private int x;
  private int y;
  private boolean isHorizontal;
  

  public Ship(int x, int y, int length, boolean isHorizontal, int id) {
    this.x = x - 1;
    this.y = y - 1;
    this.length = length;
    this.isHorizontal = isHorizontal;
    this.id = id;
  }

  public int[] getPositions() {
    int[] positions = new int[length];
    for (int i = 0; i < length; i++) {
      positions[i] = isHorizontal ? x + i : y + i;
    }
    return positions;
  }

  public int getX() {
      return x;
  }

  public void setX(int x) {
      this.x = x - 1;
  }

  public int getY() {
      return y;
  }

  public void setY(int y) {
      this.y = y - 1;
  }

  public boolean getIsHorizontal() {
    return isHorizontal;
  }

  public void setIsHorizontal(boolean isHorizontal) {
      this.isHorizontal = isHorizontal;
  }
}
