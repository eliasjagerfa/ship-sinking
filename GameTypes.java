public class GameTypes {
  public static class PhaseOneInfo {
    final Player player;

    public PhaseOneInfo(Player player) {
      this.player = player;
    }
  }

  public static class Player {
    final boolean isPlayerOne;
    final String name;

    public Player(boolean isPlayerOne, String name) {
      this.isPlayerOne = isPlayerOne;
      this.name = name;
    }
  }
}
