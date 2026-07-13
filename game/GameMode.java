package game;

import java.util.Map;

public class GameMode {
    public enum GameModeTypes {S, M, L}

    public static final Map<GameModeTypes, Integer> BOARD_SIZES = Map.of(
        GameModeTypes.S, 5,
        GameModeTypes.M, 10,
        GameModeTypes.L, 15
    );

    //key is length of ship, value is amount of this length
    public static final Map<GameModeTypes, Map<Integer, Integer>> SHIP_CONFIGS = Map.of(
        GameModeTypes.S,
            Map.of(
                1, 2,
                2, 1
            ),
        GameModeTypes.M,
            Map.of(
                2, 4,
                3, 3,
                4, 2,
                5, 1
            ),
        GameModeTypes.L,
            Map.of(
                1, 2,
                2, 3,
                3, 3,
                4, 3,
                5, 2,
                6, 1
            )
    );
}
