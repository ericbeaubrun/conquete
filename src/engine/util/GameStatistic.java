package engine.util;

import data.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class allows to keep statistics of the game during the time.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class GameStatistic implements Serializable {

    /**
     * Keeps how many turns played by each player.
     * Key = the {@link  Player}.
     * Value = the turn.
     */
    private final HashMap<Player, Integer> playersSkipTurnCount;

    /**
     * Keeps amount of {@link data.board.Block} owned by each player each turns to track the evolution.
     */
    private final HashMap<Player, ArrayList<Integer>> territoryEvolution;

    /**
     * Creates {@link HashMap} to keep some statistics of game each turns, it initializes playersSkipTurnCount
     * and territoryEvolution.
     *
     * @param players all players in the game.
     */
    public GameStatistic(ArrayList<Player> players) {

        playersSkipTurnCount = new HashMap<>();
        territoryEvolution = new HashMap<>();

        //init evolution for turn 0
        for (Player player : players) {
            playersSkipTurnCount.put(player, 0);
            territoryEvolution.put(player, new ArrayList<>());
            territoryEvolution.get(player).add(player.getTotalOwnedBlocks());
        }
    }

    /**
     * Adds amount of {@link data.board.Block} owned by given {@link Player} to the territoryEvolution {@link HashMap}.
     *
     * @param player the player which owns the territory.
     */
    public void updateTerritoryEvolution(Player player) {
        if (player != null) {
            territoryEvolution.get(player).add(player.getTotalOwnedBlocks());
        }
    }

    /**
     * @param player the player which played this turn.
     */
    public void incrementSkipTurnCount(Player player) {
        Integer count = playersSkipTurnCount.get(player);

        if (count != null) {
            count++;
            playersSkipTurnCount.put(player, count);
        }
    }

    /**
     * This method should be used to update all GameStatistic attributes.
     *
     * @param player the player which played this turn.
     */
    public void updateStatistics(Player player) {
        updateTerritoryEvolution(player);
        incrementSkipTurnCount(player);
    }

    public Integer getSkipTurnCount(Player player) {
        return playersSkipTurnCount.get(player);
    }

    public HashMap<Player, ArrayList<Integer>> getTerritoryEvolution() {
        return territoryEvolution;
    }
}
