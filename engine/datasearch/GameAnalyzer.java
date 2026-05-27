package engine.datasearch;

import configuration.GameplayConfig;
import data.board.Block;
import data.board.GameMap;
import data.element.Element;
import data.element.Soldier;
import data.player.Player;
import engine.process.PlayersManager;
import log.LoggerUtility;
import main.game.GameManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * The GameAnalyzer class provides methods to analyze different aspects of the game, such as army strength,
 * territory control, and economic advantage.
 * This class is used to determine actions to do in {@link engine.process.BotAction}.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class GameAnalyzer {

    private final Logger logger = LoggerUtility.getLogger(GameAnalyzer.class);

    private final GameMap map;
    private final PlayersManager playersManager;
    private final ElementsFinder elementsFinder;
    private final BlockFinder blockFinder;

    public GameAnalyzer(GameManager gameManager) throws IllegalArgumentException {
        if (gameManager != null) {
            map = gameManager.getMap();

            playersManager = gameManager.getPlayersManager();

            elementsFinder = gameManager.getElementsFinder();
            blockFinder = gameManager.getBlockFinder();
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Determines if a given soldier has advantage over enemies based on health points of allies and attack
     * points of enemies.
     *
     * @param soldier the soldier to be checked for advantage
     * @return true if the soldier has advantage, false otherwise
     */
    public Boolean soldierHasAdvantage(Soldier soldier) {
        int alliesHealth = 0;
        int enemiesAttack = 0;

        if (soldier != null) {
            Player player = playersManager.getPlayerOwnsElement(soldier);

            if (player != null) {
                ArrayList<Soldier> allies = new ArrayList<>();
                ArrayList<Soldier> enemies = new ArrayList<>();

                for (Block block : blockFinder.findAdjacentBlocks(soldier.getIndexX(), soldier.getIndexY(),
                        GameplayConfig.BLOCKS_RANGE_SOLDIER_MOVE, false)) {

                    if (!block.isEmpty()) {
                        Element element = elementsFinder.findElementOnBlock(block);
                        if (element instanceof Soldier otherSoldier) {
                            if (player.ownsElement(otherSoldier)) {
                                allies.add(otherSoldier);
                            } else {
                                enemies.add(otherSoldier);
                            }
                        }
                    }
                }

                for (Soldier allie : allies) {
                    alliesHealth += allie.getHealthPoint();
                }

                for (Soldier enemy : enemies) {
                    enemiesAttack += enemy.getAttackPoint();
                }
            } else {
                logger.warn("Probably trying to access to died soldier.");
            }
        }
        return enemiesAttack != 0 && enemiesAttack < alliesHealth;
    }

    /**
     * Determines if a given player has territory advantage over other players.
     *
     * @param player the player to be checked for territory advantage
     * @return true if the player has territory advantage, false otherwise
     */
    public Boolean playerHasTerritoryAdvantage(Player player) {
        if (player != null) {
            int playerTerritoryScore = player.getTotalOwnedBlocks();

            int othersPlayerTerritoryScore = 0;

            for (Player otherPlayer : playersManager.getPlayerList()) {
                if (!otherPlayer.equals(player)) {
                    othersPlayerTerritoryScore += otherPlayer.getTotalOwnedBlocks();
                }
            }
            return othersPlayerTerritoryScore < playerTerritoryScore * playersManager.getPlayerAmount();
        }
        return false;
    }

    /**
     * Determines if a given player has army advantage over other players.
     *
     * @param player the player to be checked for army advantage
     * @return true if the player has army advantage, false otherwise
     */
    public Boolean playerHasArmyAdvantage(Player player) {
        int playerArmyScore = 0;
        int othersPlayerArmyScore = 0;

        if (player != null) {
            for (Soldier soldier : elementsFinder.elementMapToList(Soldier.class)) {
                if (player.ownsElement(soldier)) {
                    playerArmyScore += soldier.getAttackPoint() + soldier.getHealthPoint();
                } else {
                    othersPlayerArmyScore += soldier.getAttackPoint() + soldier.getHealthPoint();
                }
            }
        }
        return othersPlayerArmyScore < playerArmyScore * playersManager.getPlayerAmount();
    }

    /**
     * Determines if a given player has economy advantage over other players.
     *
     * @param player the player to be checked for economy advantage
     * @return true if the player has economy advantage, false otherwise
     */
    public Boolean playerHasEconomyAdvantage(Player player) {

        int playerEconomyScore = 0;
        int othersPlayerEconomyScore = 0;

        if (player != null) {
            playerEconomyScore += (player.getGoldPerTurn() + (player.getTotalGold() / 10));
            for (Player otherPlayer : playersManager.getPlayerList()) {
                if (!otherPlayer.equals(player)) {
                    othersPlayerEconomyScore += (otherPlayer.getGoldPerTurn() + (otherPlayer.getTotalGold() / 10));
                }
            }
        }
        return othersPlayerEconomyScore < playerEconomyScore * playersManager.getPlayerAmount();
    }

    /**
     * Determines if it is favorable for a given attacking soldier to attack a given defending soldier.
     *
     * @param attackingSoldier the attacking soldier
     * @param defendingSoldier the defending soldier
     * @return true if it is favorable to attack, false otherwise
     */
    public Boolean isFavorableToAttack(Soldier attackingSoldier, Soldier defendingSoldier) {
        if (attackingSoldier != null && defendingSoldier != null) {
            return defendingSoldier.getHealthPoint() <= attackingSoldier.getAttackPoint();
        }
        return false;
    }

    /**
     * Determines if a given attacking soldier is stronger than a given defending soldier.
     *
     * @param attackingSoldier the attacking soldier
     * @param defendingSoldier the defending soldier
     * @return true if the attacking soldier is stronger, false otherwise
     */
    public Boolean isStronger(Soldier attackingSoldier, Soldier defendingSoldier) {
        if (attackingSoldier != null && defendingSoldier != null) {
            return defendingSoldier.getAttackPoint() + defendingSoldier.getHealthPoint()
                    < attackingSoldier.getAttackPoint() + attackingSoldier.getHealthPoint();
        }
        return false;
    }

    /**
     * Determines if it is favorable for a given moving soldier to merge with a given merged soldier to form a
     * stronger soldier that can attack.
     *
     * @param movingSoldier the moving soldier
     * @param mergedSoldier the merged soldier
     * @return true if it is favorable to merge for attack, false otherwise
     */
    public Boolean isFavorableToMergeToAttack(Soldier movingSoldier, Soldier mergedSoldier) {

        if (movingSoldier != null && mergedSoldier != null) {
            return (movingSoldier.getAttackPoint() + mergedSoldier.getAttackPoint())
                    < (GameplayConfig.SOLDIER_ATTACK_LIMIT + GameplayConfig.SOLDIER_ATTACK_LIMIT / 2);
        }
        return false;
    }

    /**
     * Determines if it is favorable for a given moving soldier to merge with a given merged soldier to form a
     * stronger soldier that can defend.
     *
     * @param movingSoldier the moving soldier
     * @param mergedSoldier the merged soldier
     * @return true if it is favorable to merge for defense, false otherwise
     */
    public Boolean isFavorableToMergeToDefend(Soldier movingSoldier, Soldier mergedSoldier) {
        if (movingSoldier != null && mergedSoldier != null) {
            return (movingSoldier.getHealthPoint() + mergedSoldier.getHealthPoint())
                    < (GameplayConfig.SOLDIER_ATTACK_LIMIT + GameplayConfig.SOLDIER_ATTACK_LIMIT / 2);
        }
        return false;
    }

    /**
     * Determines if a given player has army advantage in a given zone of the game map.
     *
     * @param player the player to be checked for army advantage
     * @param zone   the zone to be checked
     * @return true if the player has army advantage in the zone, false otherwise
     */
    public Boolean hasArmyAdvantageInZone(Player player, ArrayList<Block> zone) {
        int alliesForce = 0;
        int enemiesForce = 0;

        if (player != null && zone != null) {
            for (Block block : zone) {
                if (elementsFinder.elementTypeExistsOnBlock(Soldier.class, block)) {
                    Soldier soldier = (Soldier) elementsFinder.findElementOnBlock(block);
                    if (player.ownsElement(soldier)) {
                        alliesForce += soldier.getAttackPoint() + soldier.getHealthPoint();
                    } else {
                        enemiesForce += soldier.getAttackPoint() + soldier.getHealthPoint();
                    }
                }
            }
        }
        return alliesForce >= enemiesForce;
    }

    /**
     * Determines if a given player has territory disadvantage compared to other players.
     *
     * @param player the player to be checked for territory disadvantage
     * @return true if the player has territory disadvantage, false otherwise
     */
    public Boolean playerHasTerritoryDisadvantage(Player player) {
        if (player == null) {
            //logger message
            return false;
        }

        int minBlocksOwned = player.getTotalOwnedBlocks();

        for (Player otherPlayer : playersManager.getPlayerList()) {
            if (!otherPlayer.equals(player)) {
                int otherPlayerBlocksOwned = otherPlayer.getTotalOwnedBlocks();
                if (otherPlayerBlocksOwned < minBlocksOwned) {
                    return false;
                }
            }
        }
        return true;
    }
}

