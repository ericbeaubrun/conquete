package engine.process;

import java.util.ArrayList;

import configuration.GameplayConfig;
import data.element.AttackTower;
import data.board.Block;
import data.element.DefenseTower;
import data.element.Element;
import data.element.House;
import data.player.Player;
import data.element.Soldier;
import log.LoggerUtility;
import org.apache.log4j.Logger;

/**
 * This class is responsible for all gold calculation during a game.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class EconomyManager {

    private static final Logger logger = LoggerUtility.getLogger(EconomyManager.class);

    /**
     * List of {@link Player} which play the game.
     */
    private final ArrayList<Player> players;

    /**
     * @param players {@link Player} which play the game.
     */
    public EconomyManager(ArrayList<Player> players) {
        this.players = players;
    }

    /**
     * Calculation of gold per turn taking into account owned {@link Element} and owned {@link Block} of a given {@link Player}.
     *
     * @param player whose gold/turn is calculated.
     * @return amount of gold/turn, or return 0 if the given player is null.
     */
    public int calculateGoldPerTurn(Player player) {
        int result = 0;

        if (player != null) {
            result = GameplayConfig.GOLD_PER_TURN_INITIAL;
            for (Element element : player.getOwnedElementsList()) {
                if (element instanceof House house) {
                    result += house.getBonusGold();

                } else if (element instanceof Soldier soldier) {
                    result -= soldier.getGoldPerTurnMalus();

                } else if (element instanceof AttackTower) {
                    result -= GameplayConfig.MALUS_GOLD_ATTACK_TOWER;

                } else if (element instanceof DefenseTower) {
                    result -= GameplayConfig.MALUS_GOLD_DEFENSE_TOWER;
                }
            }
            for (Block block : player.getOwnedBlocksList()) {
                if (block.isEmpty()) {
                    result += 1;
                }
            }
            if (player.isBot() && player.getDifficultLevel() == 1) {
                //Gold multiplier for bot unfair difficult
                result *= 2;
            }
        }
        return result;
    }

    /**
     * Update the gold/turn of a given {@link Player} by calculating with calculateGoldPerTurn() method.
     * When the given player is null do nothing.
     *
     * @param player whose gold/turn is updated.
     */
    public void updatePlayerGoldPerTurn(Player player) {
        if (player != null) {
            player.setGoldPerTurn(calculateGoldPerTurn(player));
        }
    }

    /**
     * Directly update the gold/turn of all players by calculating with calculateGoldPerTurn() method.
     */
    public void recalculateAllPlayersGoldPerTurn() {
        for (Player player : players) {
            updatePlayerGoldPerTurn(player);
        }
    }

    /**
     * Gives the gold/turn amount to a given {@link Player}.
     * When the given player is null do nothing.
     *
     * @param player whose gold is given.
     */
    public void giveGoldPerTurn(Player player) {
        if (player != null) {
            player.incrementGoldPerTurnToTotalGold();
        }
    }
}
