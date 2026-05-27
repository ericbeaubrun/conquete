package main.game;

import configuration.GameplayConfig;
import data.board.Block;
import data.board.GameMap;
import data.element.*;
import data.player.Player;
import engine.util.GameStatistic;
import engine.datasearch.BlockFinder;
import engine.datasearch.ElementsFinder;
import engine.datasearch.GameAnalyzer;
import engine.process.EconomyManager;
import data.element.ElementsMap;
import engine.process.PlayersManager;
import engine.util.DistanceCalculator;
import engine.util.RandomUtility;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * This class allows to manage all games data, it declares various method to do in game actions.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class GameManager {

    private final Logger logger = LoggerUtility.getLogger(GameManager.class);

    //Map
    private final GameMap map;
    private final BlockFinder blockFinder;

    //Element
    private ElementsMap elementsMap;
    private ElementsFinder elementsFinder;

    //Player
    private final PlayersManager playersManager;
    private final EconomyManager economyManager;

    //Global game
    private GameStatistic gameStatistic;
    private final GameAnalyzer gameAnalyzer;

    private Block selectedBlock = null;
    private ArrayList<Block> possibleActionBlocks = null;
    private Boolean gameIsEnded = false;
    private Boolean elementIsSelected = false;

    /**
     * @param map            the map of this game.
     * @param playersManager the player manager of this game.
     * @throws IllegalArgumentException if map or playerManager is null.
     */
    public GameManager(GameMap map, PlayersManager playersManager) throws IllegalArgumentException {
        if (playersManager != null && map != null) {
            this.playersManager = playersManager;
            this.map = map;
            economyManager = new EconomyManager(playersManager.getPlayerList());
            elementsMap = new ElementsMap();
            elementsFinder = new ElementsFinder(elementsMap);

            //Fill ElementMap with player's Base
            for (Player player : playersManager.getPlayerList()) {
                for (Element element : player.getOwnedElementsList()) {
                    Block baseBlock = map.getBlock(element.getIndexX(), element.getIndexY());
                    elementsMap.putElementOnBlock(baseBlock, element);
                }
            }

            blockFinder = new BlockFinder(map, elementsFinder);
            gameAnalyzer = new GameAnalyzer(this);
            gameStatistic = new GameStatistic(playersManager.getPlayerList());
            economyManager.recalculateAllPlayersGoldPerTurn();
        } else {
            throw new IllegalArgumentException();
        }
    }


    /**
     * Generates ForestTree randomly on the game map if the map is not full.
     * The probability of generating a specific number of trees is determined by the input parameters.
     * If the map is already full, no trees will be generated.
     *
     * @param probSpawn3Trees the probability of generating 3 trees [0%-100%]
     * @param probSpawn2Trees the probability of generating 2 trees [0%-100%]
     * @param probSpawn1Tree  the probability of generating 1 tree [0%-100%]
     */
    private void generateRandomForestTree(int probSpawn3Trees, int probSpawn2Trees, int probSpawn1Tree) {
        if (!map.mapIsFull()) {
            int randomInt = RandomUtility.getRandom(1, 100);
            int amountGenerated = 0;

            // Probabilities
            if (randomInt <= probSpawn3Trees) {
                amountGenerated = 3;
            } else if (randomInt <= probSpawn3Trees + probSpawn2Trees) {
                amountGenerated = 2;
            } else if (randomInt <= probSpawn3Trees + probSpawn2Trees + probSpawn1Tree) {
                amountGenerated = 1;
            }

            // Generate all trees
            for (int i = 0; i < amountGenerated; i++) {
                Block block = RandomUtility.getRandomEmptyBlock(map);
                ForestTree tree = new ForestTree(block.getX(), block.getY());
                elementsMap.putElementOnBlock(block, tree);
                logger.info("New tree generated in " + tree.positionToString() + ".");
            }
        }
    }

    /**
     * Performs the special effects of all special blocks on the game map.
     * For the {@link House} it multiplies bonus gold.
     * For the {@link AttackTower} it multiplies damages deals.
     * For the {@link DefenseTower} it multiplies health points deals.
     * For the {@link Soldier} it increase attack points if it's lower than the health points.
     */
    private void performSpecialBlocksEffect() {

        for (Block block : getSpecialsBlocks()) {
            if (!block.isEmpty()) {
                Element element = elementsFinder.findElementOnBlock(block);
                if (element != null) {
                    Player player = playersManager.getPlayerOwnsElement(element);

                    if (element instanceof House house) {
                        player.setTotalGold(player.getTotalGold() + (house.getBonusGold() * GameplayConfig.SPECIAL_BLOCK_BONUS_MULTIPLIER));

                    } else if (element instanceof AttackTower attackTower) {
                        performAttackTowerEffect(attackTower);

                    } else if (element instanceof DefenseTower defenseTower) {
                        performDefenseTowerEffect(defenseTower);

                    } else if (element instanceof Soldier soldier) {
                        if (player.canPlay()) {
                            if (soldier.getHealthPoint() > soldier.getAttackPoint()) {
                                soldier.incrementAttackPoint(1);
                            }
                        }

                    } else if (element instanceof ForestTree forestTree) {
                        forestTree.setBonusGoldWhenDestroyed(forestTree.getBonusGoldWhenDestroyed() * GameplayConfig.SPECIAL_BLOCK_BONUS_MULTIPLIER);
                    }
                }
            }
        }
    }


    /**
     * Perform {@link AttackTower} effect : deal amount of damage on only one enemy soldier in the range.
     *
     * @param tower the {@link AttackTower} which perform the effect (it defines the range).
     */
    private void performAttackTowerEffect(AttackTower tower) {
        Player player = playersManager.getPlayerOwnsElement(tower);

        ArrayList<Block> range = blockFinder.findAdjacentBlocks(tower.getIndexX(), tower.getIndexY(), GameplayConfig.ATTACK_TOWER_RAYON, true);
        if (range != null && player != null) {
            for (Block block : range) {
                if (!block.isEmpty()) {
                    Element element = elementsFinder.findElementOnBlock(block);
                    if (element instanceof Soldier soldier) {
                        if (!player.ownsElement(element)) {
                            soldier.setHealthPoint(element.getHealthPoint() - tower.getDamageDeals());

                            logger.info("Attack tower effect performed on soldier in " + soldier.positionToString() + ".");

                            if (soldier.getHealthPoint() <= 0) {
                                //kill
                                Player playerOwnSoldier = playersManager.getPlayerOwnsElement(soldier);
                                if (playerOwnSoldier != null) {
                                    playerOwnSoldier.removeOwnedElement(soldier);
                                    getElementsMap().removeElementOnBlock(map.getBlock(soldier.getIndexX(), soldier.getIndexY()));
                                    logger.info("Soldier killed in " + soldier.positionToString() + ".");
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Perform {@link DefenseTower} effect : add amount of health point on only one allie soldier in the range.
     *
     * @param tower the {@link DefenseTower} which perform the effect (it defines the range).
     */
    private void performDefenseTowerEffect(DefenseTower tower) {

        Player player = playersManager.getPlayerOwnsElement(tower);
        ArrayList<Block> range = blockFinder.findAdjacentBlocks(tower.getIndexX(), tower.getIndexY(),
                GameplayConfig.DEFENSE_TOWER_RAYON, true);

        if (range != null && player != null) {
            for (Block block : range) {
                if (!block.isEmpty()) {
                    Element element = elementsFinder.findElementOnBlock(block);
                    if (element instanceof Soldier soldier) {
                        if (player.ownsElement(soldier)) {
                            if (soldier.getHealthPoint() < GameplayConfig.SOLDIER_HEALTH_LIMIT) {
                                soldier.setHealthPoint(soldier.getHealthPoint() + tower.getBonusHealthDeals());
                                logger.info("Defense tower effect performed on soldier in " + soldier.positionToString());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Perform all towers ({@link AttackTower} and {@link DefenseTower}) effect of a given {@link Player}.
     *
     * @param player which owns towers.
     */
    public void performTowersEffect(Player player) {

        ArrayList<AttackTower> attackTowersArray = elementsFinder.elementMapToList(AttackTower.class);
        ArrayList<DefenseTower> defenseTowersArray = elementsFinder.elementMapToList(DefenseTower.class);

        //Perform effect of player's attack towers
        if (attackTowersArray != null) {
            for (AttackTower attackTower : attackTowersArray) {
                if (player.ownsElement(attackTower)) {
                    performAttackTowerEffect(attackTower);
                }
            }
        }

        //Perform effect of player's defense towers
        if (defenseTowersArray != null) {
            for (DefenseTower defenseTower : defenseTowersArray) {
                if (player.ownsElement(defenseTower)) {
                    performDefenseTowerEffect(defenseTower);
                }
            }
        }
    }

    /**
     * Perform skip turn action to change the current {@link  Player} which can play, give gold per turn
     * for the new current Player and perform elements effect.
     */
    public void skipTurn() {

        gameStatistic.updateStatistics(playersManager.getCurrentPlayer());
        playersManager.turnToNextPlayer();
        Player currentPlayer = playersManager.getCurrentPlayer();

        performTowersEffect(currentPlayer);
        performSpecialBlocksEffect();

        economyManager.giveGoldPerTurn(currentPlayer);

        for (Soldier soldier : elementsFinder.elementMapToList(Soldier.class)) {
            if (currentPlayer.ownsElement(soldier)) {
                soldier.allowToMove();
            } else {
                soldier.preventToMove();
            }
        }

        economyManager.recalculateAllPlayersGoldPerTurn();

        if (elementsFinder.calculateAmountOfElement(ForestTree.class) < GameplayConfig.FOREST_TREE_SPAWN_LIMIT) {
            generateRandomForestTree(GameplayConfig.PROBABILITIES_OF_SPAWN_1_TREE,
                    GameplayConfig.PROBABILITIES_OF_SPAWN_2_TREE,
                    GameplayConfig.PROBABILITIES_OF_SPAWN_3_TREE);
        } else {
            logger.info("Forest tree spawn limit reached.");
        }

        logger.info("Current turn : " + (gameStatistic.getSkipTurnCount(playersManager.getCurrentPlayer()) + 1)
                + " / Current player : " + playersManager.getCurrentPlayer().getColorName() + " / Player queue : "
                + playersManager.playerQueueToString());
    }

    /**
     * Merging two soldiers by sacrificing one of them and adds its statistics (health and attack).
     *
     * @param lostSoldierBlock   the soldier which will disappear.
     * @param mergedSoldierBlock the soldier which will become more powerful.
     */
    private void mergeSoldier(Block lostSoldierBlock, Block mergedSoldierBlock) {

        if (!lostSoldierBlock.equals(mergedSoldierBlock) && elementsFinder.elementTypeExistsOnBlock(Soldier.class, mergedSoldierBlock)
                && elementsFinder.elementTypeExistsOnBlock(Soldier.class, lostSoldierBlock)) {
            Soldier lostSoldier = (Soldier) elementsFinder.findElementOnBlock(lostSoldierBlock);
            Soldier mergedSoldier = (Soldier) elementsFinder.findElementOnBlock(mergedSoldierBlock);

            if (lostSoldier != null && mergedSoldier != null) {
                if (!((mergedSoldier.getAttackPoint() >= GameplayConfig.SOLDIER_ATTACK_LIMIT && mergedSoldier.getHealthPoint() >= GameplayConfig.SOLDIER_HEALTH_LIMIT)
                        || (lostSoldier.getAttackPoint() >= GameplayConfig.SOLDIER_ATTACK_LIMIT && lostSoldier.getHealthPoint() >= GameplayConfig.SOLDIER_HEALTH_LIMIT))) {

                    mergedSoldier.incrementAttackPoint(lostSoldier.getAttackPoint());
                    mergedSoldier.incrementHealthPoint(lostSoldier.getHealthPoint());

                    Player player = playersManager.getPlayerOwnsElement(lostSoldier);
                    player.removeOwnedElement(lostSoldier);

                    //kill
                    elementsMap.removeElementOnBlock(lostSoldierBlock);

                    logger.info("Soldier [" + player.getColorName() + "] in position" + mergedSoldier.positionToString() + " merged with soldier in position " + lostSoldier.positionToString());


                } else {
                    logger.warn("Attempt to merge a soldier who has reached the limit in position " + mergedSoldier.positionToString() + ".");
                }
            }
        } else {
            logger.warn("Attempt to merge a soldier with himself in position " + lostSoldierBlock.positionToString() + ".");
        }
    }

    /**
     * Attack a structure {@link Element} with a given soldier {@link Block}.
     *
     * @param soldierAtkBlock the block where the soldier is.
     * @param structureBlock  the block where the structure is.
     */
    private void attackStructure(Block soldierAtkBlock, Block structureBlock) {

        Soldier soldierAtk = (Soldier) elementsFinder.findElementOnBlock(soldierAtkBlock);
        Element structure = elementsFinder.findElementOnBlock(structureBlock);

        Player playerDef = playersManager.getPlayerOwnsBlock(structureBlock);
        Player playerAtk = playersManager.getPlayerOwnsElement(soldierAtk);

        structure.setHealthPoint(structure.getHealthPoint() - soldierAtk.getAttackPoint());

        // Structure destroyed
        if (structure.getHealthPoint() <= 0) {

            if (playerDef != null) {
                //kill
                playerDef.removeOwnedElement(structure);
                playerDef.removeOwnedBlock(structureBlock);
            }

            if (structure instanceof ForestTree) {
                playerAtk.setTotalGold(playerAtk.getTotalGold() + GameplayConfig.BONUS_GOLD_FOREST_TREE);
            } else if (structure instanceof Base) {

                if (playerDef.isBot() && playerDef.getDifficultLevel() == 1) {
                    //don't give the territory to attacking player because bot level is unfair
                    playersManager.setPlayerLost(playerDef, null, elementsMap);
                } else {
                    //give the territory to attacking player
                    playersManager.setPlayerLost(playerDef, playerAtk, elementsMap);
                }

                if (playersManager.playerHasWin() || !playersManager.existsHumanPlayerNotLost()) {
                    gameIsEnded = true;
                }
            }

            //kill
            elementsMap.removeElementOnBlock(structureBlock);

            playerAtk.addOwnedBlock(structureBlock);

            elementsMap.changeBlock(soldierAtkBlock, structureBlock);

        } else { // Structure not destroyed
            Block destinationBlock = null;

            if (!blockFinder.findAdjacentBlocks(structureBlock, false).contains(soldierAtkBlock)) {
                //When soldier attack range contains a structure
                for (Block block : blockFinder.findAdjacentBlocks(structureBlock, false)) {

                    if (destinationBlock == null) {
                        destinationBlock = block;
                    }

                    if (playersManager.currentPlayerOwnsBlock(block) && block.isEmpty()) {
                        if (DistanceCalculator.calculateDistance(soldierAtkBlock, destinationBlock) > DistanceCalculator.calculateDistance(soldierAtkBlock, block)) {
                            destinationBlock = block;
                        }
                        if (DistanceCalculator.calculateDistance(soldierAtkBlock, destinationBlock) > DistanceCalculator.calculateDistance(soldierAtkBlock, block)) {
                            destinationBlock = block;
                        }
                    }
                }

                if (destinationBlock == null || destinationBlock.equals(structureBlock)) {
                    logger.warn("Attempt to attack structure in position " + structureBlock.positionToString() + " but no blocks are available to move in that direction.");
                    //

                } else {
                    elementsMap.changeBlock(soldierAtkBlock, destinationBlock);
                }

            } else {
                logger.warn("Attempt to perform a prohibited attack on structure in position " + structureBlock.positionToString() + ".");
            }
        }
        soldierAtk.preventToMove();
    }

    /**
     * Attack a {@link Soldier} on the given {@link Block} using another given {@link Block} where the enemy soldier is located.
     *
     * @param attackingSoldierBlock the block where the attacking soldier is located.
     * @param defendingSoldierBlock the block where the defending soldier is located.
     */
    private void attackSoldier(Block attackingSoldierBlock, Block defendingSoldierBlock) {
        Soldier attackingSoldier = (Soldier) elementsFinder.findElementOnBlock(attackingSoldierBlock);
        Soldier defendingSoldier = (Soldier) elementsFinder.findElementOnBlock(defendingSoldierBlock);

        if (attackingSoldier != null & defendingSoldier != null) {
            //Retrieve players owns soldiers
            Player attackingPlayer = playersManager.getPlayerOwnsElement(attackingSoldier);
            Player defendingPlayer = playersManager.getPlayerOwnsElement(defendingSoldier);

            if (attackingPlayer != null & defendingPlayer != null) {
                //Deal damage to both
                attackingSoldier.setHealthPoint(attackingSoldier.getHealthPoint() - defendingSoldier.getAttackPoint());
                defendingSoldier.setHealthPoint(defendingSoldier.getHealthPoint() - attackingSoldier.getAttackPoint());

                if (attackingSoldier.getHealthPoint() <= 0) {
                    //Attacking soldier die
                    attackingPlayer.removeOwnedElement(attackingSoldier);
                    elementsMap.removeElementOnBlock(attackingSoldierBlock);
                    attackingSoldier = null;
                }

                if (defendingSoldier.getHealthPoint() <= 0) {
                    //Defending soldier die
                    defendingPlayer.removeOwnedElement(defendingSoldier);
                    elementsMap.removeElementOnBlock(defendingSoldierBlock);
                    defendingSoldier = null;
                }

                if (attackingSoldier != null) {
                    //When attacking soldier is still alive
                    attackingSoldier.preventToMove();
                    if (defendingSoldier == null) {
                        //When defending soldier is die
                        //Attacking soldier move on defendingSoldierBlock
                        moveToEmptyBlock(attackingSoldierBlock, defendingSoldierBlock);
                    } else {
                        //When both soldier are still alive
                        //Attacking soldier should sometimes move in direction of defending soldier
                        if (!blockFinder.findAdjacentBlocks(defendingSoldierBlock, false).contains(attackingSoldierBlock)) {
                            //Finding a block to move in direction of defending soldier
                            Block destinationBlock = null;
                            for (Block block : blockFinder.findAdjacentBlocks(defendingSoldierBlock, false)) {
                                if (destinationBlock == null) {
                                    if (playersManager.currentPlayerOwnsBlock(block) && block.isEmpty()) {
                                        destinationBlock = block;
                                    }
                                } else {
                                    if (DistanceCalculator.calculateDistance(attackingSoldierBlock, destinationBlock) > DistanceCalculator.calculateDistance(attackingSoldierBlock, block) && playersManager.currentPlayerOwnsBlock(block)) {
                                        destinationBlock = block;
                                    }
                                }
                            }
                            if (destinationBlock == null) {
                                logger.warn("Attempt to attack soldier in position " + defendingSoldierBlock.positionToString() + " but no blocks are available to move in that direction.");
                            } else {
                                //Moves attacking soldier in direction of defending soldier
                                if (destinationBlock.isEmpty()) {
                                    elementsMap.changeBlock(attackingSoldierBlock, destinationBlock);
                                    Player playerLostBlock = playersManager.getPlayerOwnsBlock(destinationBlock);
                                    if (playerLostBlock != null) {
                                        playerLostBlock.removeOwnedBlock(destinationBlock);
                                    }
                                    attackingPlayer.addOwnedBlock(destinationBlock);
                                } else {
                                    mergeSoldier(attackingSoldierBlock, destinationBlock);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Moves a {@link Soldier} to an empty destination {@link Block}.
     *
     * @param currentBlock     the block where the soldier is currently located.
     * @param destinationBlock the empty block where the soldier will be moved.
     */
    private void moveToEmptyBlock(Block currentBlock, Block destinationBlock) {
        if (currentBlock != null && destinationBlock != null) {
            if (!currentBlock.isRemoved() && !destinationBlock.isRemoved() && destinationBlock.isEmpty()) {
                Soldier soldier = (Soldier) elementsFinder.findElementOnBlock(currentBlock);
                Player player = playersManager.getPlayerOwnsElement(soldier);

                if (destinationBlock.isEmpty() && soldier != null && player != null) {
                    logger.info("Soldier in " + soldier.positionToString() + " moved in " + destinationBlock.positionToString() + ".");
                    elementsMap.changeBlock(currentBlock, destinationBlock);

                    if (!player.ownsBlock(destinationBlock)) {
                        Player playerLostBlock = playersManager.getPlayerOwnsBlock(destinationBlock);
                        if (playerLostBlock != null) {
                            playerLostBlock.removeOwnedBlock(destinationBlock);
                        }
                        player.addOwnedBlock(destinationBlock);
                    }
                    soldier.preventToMove();
                }
            } else {
                logger.warn("Attempts to do prohibited movement from position " + currentBlock.positionToString()
                        + " to position " + destinationBlock.positionToString() + ".");
            }
        } else {
            logger.warn("Attempts to do movement on/from not-existent block.");
        }
    }

    /**
     * Moves a soldier from a block to another block.
     * If it's empty, the soldier will move to that block.
     * If it contains an allied soldier, the soldier will merge with the ally.
     * If it contains an enemy soldier, the soldier will attack the enemy.
     * If it contains a structure, the soldier will attack the structure.
     * This method also recalculates the gold per turn for all players after the movement.
     *
     * @param startingBlock the starting block of the soldier.
     * @param endingBlock   the ending destination block.
     */
    public void moveSoldierToBlock(Block startingBlock, Block endingBlock) {
        if (startingBlock != null && endingBlock != null) {
            if (elementsFinder.elementTypeExistsOnBlock(Soldier.class, startingBlock)) {

                Soldier soldier = (Soldier) elementsFinder.findElementOnBlock(startingBlock);
                Player player = playersManager.getPlayerOwnsElement(soldier);

                if (soldier != null && player != null) {
                    if (soldier.canMove()) {
                        if (endingBlock.getX() < startingBlock.getX()) {
                            soldier.setLastMoveLeft();
                        }
                        if (endingBlock.getX() > startingBlock.getX()) {
                            soldier.setLastMoveRight();
                        }
                        if (endingBlock.isEmpty()) {
                            moveToEmptyBlock(startingBlock, endingBlock);

                        } else {
                            if (elementsFinder.elementTypeExistsOnBlock(Soldier.class, endingBlock)) {
                                if (playersManager.currentPlayerOwnsBlock(endingBlock)) {
                                    mergeSoldier(startingBlock, endingBlock);
                                } else {
                                    attackSoldier(startingBlock, endingBlock);
                                }
                            } else if ((((elementsFinder.elementTypeExistsOnBlock(House.class, endingBlock)
                                    || elementsFinder.elementTypeExistsOnBlock(AttackTower.class, endingBlock)
                                    || elementsFinder.elementTypeExistsOnBlock(DefenseTower.class, endingBlock)
                                    || elementsFinder.elementTypeExistsOnBlock(Base.class, endingBlock))
                                    && !player.ownsBlock(endingBlock))
                                    || elementsFinder.elementTypeExistsOnBlock(ForestTree.class, endingBlock))) {
                                attackStructure(startingBlock, endingBlock);
                            } else {
                                logger.warn("Attempt to do a prohibited movement on element in position" + endingBlock.positionToString() + ".");
                            }
                        }
                    } else {
                        logger.warn((player.isBot() ? "Bot" : "Player") + " [" + player.getColorName() + "] attempting to do an prohibited move from position " + startingBlock.positionToString() + ".");
                    }
                    economyManager.recalculateAllPlayersGoldPerTurn();
                }
            } else {
                logger.warn("Attempt to move non-existent soldier in position " + startingBlock.positionToString() + ".");
            }
        }
    }

    /**
     * Moves a soldier from a block to another block.
     * If it's empty, the soldier will move to that block.
     * If it contains an allied soldier, the soldier will merge with the ally.
     * If it contains an enemy soldier, the soldier will attack the enemy.
     * If it contains a structure, the soldier will attack the structure.
     * This method also recalculates the gold per turn for all players after the movement.
     *
     * @param soldier     the soldier which will moves.
     * @param endingBlock the ending destination block.
     */
    public void moveSoldierToBlock(Soldier soldier, Block endingBlock) {
        if (soldier != null && endingBlock != null) {
            moveSoldierToBlock(map.getBlock(soldier.getIndexX(), soldier.getIndexY()),
                    map.getBlock(endingBlock.getIndexX(), endingBlock.getIndexY()));
        }
    }

    /**
     * Allows a player to buy a new {@link  Soldier} on a block.
     *
     * @param player the player who wants to buy a new soldier
     * @param block  the block on which the player wants to buy the soldier
     * @throws NullPointerException if either the player or the block is null
     */
    public void buySoldier(Player player, Block block) {
        if (player != null & block != null) {
            if (!block.isRemoved() && block.isEmpty() && player.ownsBlock(block)) {
                if (player.getTotalGold() >= GameplayConfig.SOLDIER_PRICE) {

                    Soldier soldier = new Soldier(block.getX(), block.getY());
                    elementsMap.putElementOnBlock(block, soldier);
                    player.decrementTotalGold(GameplayConfig.SOLDIER_PRICE);
                    player.addOwnedElement(soldier);

                    logger.info((player.isBot() ? "Bot" : "Player") + " [" + player.getColorName() + "] bought a new soldier in position " + block.positionToString() + ".");

                }
                economyManager.recalculateAllPlayersGoldPerTurn();
            } else {
                logger.warn("Attempt to buy a soldier in prohibited position " + block.positionToString() + ".");

            }
        } else {
            logger.warn("Attempt to buy a soldier on not-existent block / with not-existent player.");
        }
    }

    /**
     * Allows a player to buy a new {@link  House} on a block.
     *
     * @param player the player who wants to buy a new house
     * @param block  the block on which the player wants to buy the house
     * @throws NullPointerException if either the player or the block is null
     */
    public void buyHouse(Player player, Block block) {
        if (player != null & block != null) {
            if (!block.isRemoved() && block.isEmpty() && player.ownsBlock(block)) {
                if (player.getTotalGold() >= GameplayConfig.HOUSE_PRICE) {
                    House house = new House(block.getX(), block.getY());
                    elementsMap.putElementOnBlock(block, house);
                    player.decrementTotalGold(GameplayConfig.HOUSE_PRICE);
                    player.addOwnedElement(house);

                    logger.info((player.isBot() ? "Bot" : "Player") + " [" + player.getColorName() + "] bought a new house in position " + block.positionToString() + ".");
                }
                economyManager.recalculateAllPlayersGoldPerTurn();
            } else {
                logger.warn("Attempt to buy a house in prohibited position " + block.positionToString() + ".");
            }
        } else {
            logger.warn("Attempt to buy a house on not-existent block / with not-existent player.");
        }
    }

    /**
     * Allows a player to buy a new {@link  AttackTower} on a block.
     *
     * @param player the player who wants to buy a new AttackTower
     * @param block  the block on which the player wants to buy the AttackTower
     * @throws NullPointerException if either the player or the block is null
     */
    public void buyAttackTower(Player player, Block block) {
        if (player != null & block != null) {
            if (!block.isRemoved() && block.isEmpty() && player.ownsBlock(block)) {
                if (player.getTotalGold() >= GameplayConfig.ATTACK_TOWER_PRICE) {
                    AttackTower attackTower = new AttackTower(block.getX(), block.getY());
                    elementsMap.putElementOnBlock(block, attackTower);
                    player.decrementTotalGold(GameplayConfig.ATTACK_TOWER_PRICE);
                    player.addOwnedElement(attackTower);

                    logger.info((player.isBot() ? "Bot" : "Player") + " [" + player.getColorName() + "] bought a new attack tower in position " + block.positionToString() + ".");

                }
                economyManager.recalculateAllPlayersGoldPerTurn();
            } else {
                logger.warn("Attempt to buy a attack tower in prohibited position " + block.positionToString() + ".");
            }
        } else {
            logger.warn("Attempt to buy a attack tower on not-existent block / with not-existent player.");
        }
    }

    /**
     * Allows a player to buy a new {@link  DefenseTower} on a block.
     *
     * @param player the player who wants to buy a new DefenseTower
     * @param block  the block on which the player wants to buy the DefenseTower
     * @throws NullPointerException if either the player or the block is null
     */
    public void buyDefenseTower(Player player, Block block) {
        if (player != null & block != null) {
            if (!block.isRemoved() && block.isEmpty() && player.ownsBlock(block)) {
                if (player.getTotalGold() >= GameplayConfig.DEFENSE_TOWER_PRICE) {

                    DefenseTower defenseTower = new DefenseTower(block.getX(), block.getY());
                    elementsMap.putElementOnBlock(block, defenseTower);
                    player.decrementTotalGold(GameplayConfig.DEFENSE_TOWER_PRICE);
                    player.addOwnedElement(defenseTower);

                    logger.info((player.isBot() ? "Bot" : "Player") + " [" + player.getColorName() + "] bought a new defense tower in position " + block.positionToString() + ".");

                }
                economyManager.recalculateAllPlayersGoldPerTurn();
            } else {
                logger.warn("Attempt to buy a defense tower in prohibited position " + block.positionToString() + ".");
            }
        } else {
            logger.warn("Attempt to buy a defense tower on not-existent block / with not-existent player.");
        }
    }

    /**
     * Run an automatic move for all {@link Player}'s {@link Soldier}.
     * Soldiers will move on empty not owned block, or a block that contains {@link ForestTree}.
     *
     * @param player the player which moves these soldiers.
     */
    public void autoMoveSoldiers(Player player) {
        if (player != null) {
            for (Element element : player.getOwnedElementsList()) {

                if (element instanceof Soldier soldier) {
                    Block soldierBlock = map.getBlock(soldier.getIndexX(), soldier.getIndexY());
                    ArrayList<Block> blocks = blockFinder.findAdjacentBlocks(soldierBlock, false);

                    //Move on adjacent ForestTree
                    if (soldier.canMove()) {
                        for (Block block : blocks) {
                            if (elementsFinder.elementTypeExistsOnBlock(ForestTree.class, block)) {
                                moveSoldierToBlock(soldierBlock, block);
                                break;
                            }
                        }
                    }

                    //Move on empty adjacent owned block
                    if (soldier.canMove()) {
                        blocks = blockFinder.findAdjacentBlocks(soldierBlock, false);
                        blockFinder.findEmptyBlocks(blocks);
                        blocks = blockFinder.findEmptyBlocks(blocks);
                        blocks = blockFinder.findOwnedBlocks(player, blocks, false);

                        if (!blocks.isEmpty()) {
                            Block destinationBlock = RandomUtility.getRandomBlock(blocks);
                            moveSoldierToBlock(soldierBlock, destinationBlock);
                        }
                    }
                }
            }
        } else {
            logger.warn("Attempt to automatically move all soldiers with not-existent player.");
        }
    }


    /**
     * Run a move for all {@link Player}'s {@link Soldier} that put them on the closest block to go
     * specified direction block..
     *
     * @param player         the player which moves these soldiers.
     * @param directionBlock the block to go on.
     */
    public void moveAllSoldiersInDirection(Player player, Block directionBlock) {
        if (player != null && directionBlock != null) {
            for (Element element : player.getOwnedElementsList()) {
                if (element instanceof Soldier soldier && soldier.canMove()) {
                    Block soldierBlock = map.getBlock(soldier.getIndexX(), soldier.getIndexY());
                    ArrayList<Block> blocks = blockFinder.findPossibleMoveBlocksSoldier(soldier, player);
                    Block moveBlock = blockFinder.findBlockToDirection(blocks, player, soldierBlock, directionBlock);
                    moveSoldierToBlock(soldierBlock, moveBlock);
                }
            }
        } else {
            logger.warn("Attempt to move all soldiers in position with not-existent player / on not-existent block.");
        }

    }


    /**
     * Takes a soldier to move to the nearest block to go to a given block.
     *
     * @param soldier        the soldier which moves.
     * @param directionBlock the block that defines the direction the soldier is going.
     */
    public void moveSoldierToDirection(Soldier soldier, Block directionBlock) {
        Player player = playersManager.getPlayerOwnsElement(soldier);
        if (player != null && soldier != null && directionBlock != null) {
            ArrayList<Block> blocks = blockFinder.findPossibleMoveBlocksSoldier(soldier, player);
            Block soldierBlock = map.getBlock(soldier.getIndexX(), soldier.getIndexY());
            if (soldierBlock != null && !blocks.isEmpty()) {
                Block result = blockFinder.findBlockToDirection(blocks, player, soldierBlock, directionBlock);
                if (result != null) {
                    moveSoldierToBlock(soldier, result);
                } else {
                    logger.warn("Attempt to move a soldier in position " + soldier.positionToString()
                            + " to direction " + directionBlock.positionToString() + " but no block found.");
                }
            }
        } else {
            if (soldier == null && directionBlock != null) {
                logger.warn("Attempt to move not-existent soldier to direction " + directionBlock.positionToString() + ".");
            } else if (soldier != null && directionBlock == null) {
                logger.warn("Attempt to move soldier in position " + directionBlock.positionToString() + " to not-existent block direction.");
            }
        }
    }

    public void resetSelection() {
        possibleActionBlocks = null;
        elementIsSelected = false;
        selectedBlock = null;
    }

    public Block getSelectedBlock() {
        return selectedBlock;
    }

    public GameMap getMap() {
        return map;
    }

    public PlayersManager getPlayersManager() {
        return playersManager;
    }

    public GameAnalyzer getGameAnalyzer() {
        return gameAnalyzer;
    }

    public ArrayList<Block> getSpecialsBlocks() {
        return blockFinder.findSpecialBlocks();
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public ArrayList<Block> getPossibleActionBlocks() {
        return possibleActionBlocks;
    }

    public BlockFinder getBlockFinder() {
        return blockFinder;
    }

    public ElementsFinder getElementsFinder() {
        return elementsFinder;
    }

    public GameStatistic getGameStatistic() {
        return gameStatistic;
    }

    public ElementsMap getElementsMap() {
        return elementsMap;
    }

    public void setElementSelected(Boolean bool) {
        elementIsSelected = bool;
    }

    public void setPossibleActionBlocks(ArrayList<Block> list) {
        possibleActionBlocks = list;
    }

    public void setSelectedBlock(Block block) {
        selectedBlock = block;
    }

    public void setGameStatistic(GameStatistic gameStatistic) {
        this.gameStatistic = gameStatistic;
    }

    public void setElementsMap(ElementsMap elementsMap) {
        this.elementsMap = elementsMap;
        this.elementsFinder = new ElementsFinder(elementsMap);
    }

    public Boolean elementExists(Element element) {
        return (element != null && elementsMap.containsElement(element)
                && playersManager.getPlayerOwnsElement(element) != null);
    }

    /**
     * @return true when a player has selected an element on map, false otherwise.
     */
    public Boolean elementIsSelected() {
        return elementIsSelected;
    }

    /**
     * @return true if all players have lost except one.
     */
    public Boolean gameIsEnded() {
        return gameIsEnded;
    }

}
