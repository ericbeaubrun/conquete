package test;

import configuration.GameplayConfig;
import data.board.GameMap;
import engine.exception.InvalidMapFileException;
import main.game.Game;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void initGameTest() {
        try {
            Game game = new Game(GameplayConfig.MAX_PLAYERS, 0, 0, new GameMap());
            assertNotNull(game);
            assertNotNull(game.getGameDisplay());
            assertNotNull(game.getGameManager());
        } catch (InvalidMapFileException | IllegalArgumentException e) {
            assert false;
        }
    }

    @Test
    void initGame2Test() {
        try {

            Game game = new Game(GameplayConfig.MAX_PLAYERS + 1, 0, 0, new GameMap());
            assert false;
        } catch (InvalidMapFileException | IllegalArgumentException e) {
            assert true;
        }
    }

    @Test
    void initGame3Test() {
        try {
            Game game = new Game(GameplayConfig.MAX_PLAYERS, 0, 0, null);
            assert false;
        } catch (InvalidMapFileException | IllegalArgumentException e) {
            assert true;
        }
    }
}