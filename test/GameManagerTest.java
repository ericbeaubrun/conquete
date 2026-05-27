package test;

import data.board.Block;
import data.board.GameMap;
import data.element.Soldier;
import data.player.Player;
import main.game.GameManager;
import engine.datasearch.ElementsFinder;
import engine.process.MapBuilder;
import engine.process.PlayersManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameManagerTest {

    GameManager gameManager;

    Player playerA;
    Player playerB;

    ElementsFinder elementsFinder;

    @BeforeEach
    void setUp() {
        GameMap map = MapBuilder.buildRectMap();
        PlayersManager playersManager = new PlayersManager(map);
        playersManager.addNewPlayerToQueue();
        playersManager.addNewPlayerToQueue();

        ArrayList<Player> players = playersManager.getPlayerList();
        playerA = players.get(0);
        playerB = players.get(1);

        gameManager = new GameManager(map, playersManager);
        elementsFinder = gameManager.getElementsFinder();

    }

    @Test
    void moveSoldierTest() {

        Block blockA = gameManager.getMap().getBlock(10, 10);
        Block blockB = gameManager.getMap().getBlock(11, 10);

        assertNotNull(blockA);
        assertNotNull(blockB);

        gameManager.buySoldier(playerA, blockA);
        gameManager.buySoldier(playerB, blockB);

        assertFalse(blockA.isEmpty());
        assertFalse(blockB.isEmpty());

        assertTrue(elementsFinder.elementTypeExistsOnBlock(Soldier.class, blockA));
        assertTrue(elementsFinder.elementTypeExistsOnBlock(Soldier.class, blockB));

        gameManager.moveSoldierToBlock(blockA, blockB);

        assertTrue(elementsFinder.elementTypeExistsOnBlock(Soldier.class, blockA));
        assertTrue(elementsFinder.elementTypeExistsOnBlock(Soldier.class, blockB));
    }

    @Test
    void moveSoldier3Test() {
        Block blockA = gameManager.getMap().getBlock(10, 10);
        Block blockB = gameManager.getMap().getBlock(11, 11);

        assertNotNull(blockA);
        assertNotNull(blockB);

        gameManager.buySoldier(playerA, blockA);
        gameManager.buySoldier(playerB, blockB);

        assertFalse(blockA.isEmpty());
        assertFalse(blockB.isEmpty());

        gameManager.moveSoldierToBlock(blockA, blockB);

        assertTrue(elementsFinder.elementTypeExistsOnBlock(Soldier.class, blockA));
        assertTrue(elementsFinder.elementTypeExistsOnBlock(Soldier.class, blockB));

    }
}
