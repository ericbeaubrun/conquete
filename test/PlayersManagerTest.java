package test;

import configuration.GameplayConfig;
import data.board.GameMap;
import data.player.Player;
import engine.process.MapBuilder;
import engine.process.PlayersManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayersManagerTest {

    GameMap map;
    PlayersManager playersManager;
    ArrayList<Player> players;

    @BeforeEach
    void setUp() {
        map = MapBuilder.buildRectMap();
        playersManager = new PlayersManager(map);
        players = playersManager.getPlayerList();
    }

    @Test
    void initPlayerTest() {

        playersManager.addNewPlayerToQueue();

        assertNotNull(players);
        Player player = playersManager.getCurrentPlayer();
        assertNotNull(player);
        assertTrue(player.canPlay());
        assertFalse(player.hasLost());
    }

    @Test
    void initMaxPlayersTest() {

        for (int i = 0; i < GameplayConfig.MAX_PLAYERS; i++) {
            playersManager.addNewPlayerToQueue();
        }

        assertNotNull(players);

        for (Player player : players) {
            assertNotNull(player);

            if (player.equals(playersManager.getCurrentPlayer())) {
                assertTrue(player.canPlay());
            }
        }
    }

    @Test
    void initTooManyPlayersTest() {

        for (int i = 0; i < GameplayConfig.MAX_PLAYERS + 10; i++) {
            playersManager.addNewPlayerToQueue();
        }

        assertEquals(GameplayConfig.MAX_PLAYERS, playersManager.getPlayerAmount());

    }

    @Test
    void turnToNextPlayerTest() {

        for (int i = 0; i < GameplayConfig.MAX_PLAYERS; i++) {
            playersManager.addNewPlayerToQueue();
        }

        assertNotNull(players);

        for (int i = 0; i < GameplayConfig.MAX_PLAYERS; i++) {
            playersManager.turnToNextPlayer();
            assertNotNull(playersManager.getCurrentPlayer());

            for (Player player : players) {
                assertNotNull(player);
                if (playersManager.getCurrentPlayer().equals(player)) {
                    assertTrue(player.canPlay());
                } else {
                    assertFalse(player.canPlay());
                }
            }
        }
    }
}