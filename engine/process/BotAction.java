package engine.process;

import configuration.GameplayConfig;
import data.board.Block;
import data.board.GameMap;
import data.element.*;
import data.player.Player;
import engine.datasearch.BlockFinder;
import engine.datasearch.ElementsFinder;
import engine.datasearch.GameAnalyzer;
import engine.util.RandomUtility;
import log.LoggerUtility;
import main.game.GameManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * This class provide a method to evaluates the state of the game to make automatic actions with {@link GameManager}.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class BotAction {

    private final Logger logger = LoggerUtility.getLogger(BotAction.class);

    private final GameManager gameManager;
    private final GameAnalyzer gameAnalyzer;

    private final GameMap map;

    private final PlayersManager playersManager;
    private Player bot;

    private final ElementsFinder elementsFinder;
    private final BlockFinder blockFinder;

    private final ArrayList<Block> specialBlocks;

    private Base base;
    private Block baseBlock;

    public BotAction(GameManager gameManager) {
        if (gameManager != null) {
            this.gameManager = gameManager;
            map = gameManager.getMap();
            playersManager = gameManager.getPlayersManager();
            elementsFinder = gameManager.getElementsFinder();
            blockFinder = gameManager.getBlockFinder();
            specialBlocks = blockFinder.findSpecialBlocks();

            gameAnalyzer = new GameAnalyzer(gameManager);

            if (map == null || playersManager == null || elementsFinder == null) {
                throw new IllegalArgumentException();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void updateData() {
        bot = gameManager.getPlayersManager().getCurrentPlayer();
        base = bot.getBase();
        baseBlock = map.getBlock(base.getIndexX(), base.getIndexY());
    }

    private void attackEnemyBasesWhenPossible() {
        for (Element element : bot.getOwnedElementsList()) {
            if (element instanceof Soldier soldier) {
                Block soldierBlock = map.getBlock(soldier.getIndexX(), soldier.getIndexY());
                if (soldier.canMove()) {
                    ArrayList<Block> soldierAdjacentBlocks = blockFinder.findPossibleMoveBlocksSoldier(soldier, bot);
                    for (Block block : soldierAdjacentBlocks) {
                        if (elementsFinder.elementTypeExistsOnBlock(Base.class, block) && !bot.ownsBlock(block)) {
                            gameManager.moveSoldierToBlock(soldier, block);
                            break;
                        }
                    }
                }

                if (soldier.canMove()) {
                    ArrayList<Block> soldierAdjacentBlocks = blockFinder.findAdjacentBlocks(soldierBlock, 5, true);
                    for (Block block : soldierAdjacentBlocks) {
                        if (elementsFinder.elementTypeExistsOnBlock(Base.class, block) && !bot.ownsBlock(block)) {
                            ArrayList<Block> adjacentBlocks = blockFinder.findEmptyBlocks(blockFinder.findAdjacentBlocks(block, 1, false));
                            for (Block destinationBlock : adjacentBlocks) {
                                gameManager.moveSoldierToDirection(soldier, destinationBlock);
                                break;
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private void defendBaseWhenPossible() {
        ArrayList<Block> adjacentBaseBlocks = blockFinder.findAdjacentBlocks(baseBlock, 3, true);
        ArrayList<Block> farAdjacentBaseBlocks = blockFinder.findAdjacentBlocks(baseBlock, 9, false);
        while (bot.getTotalGold() >= GameplayConfig.SOLDIER_PRICE && !adjacentBaseBlocks.isEmpty()
                && elementsFinder.zoneContainsEnemyElement(Soldier.class, bot, blockFinder.findAdjacentBlocks(baseBlock, 3, false))) {
            ArrayList<Soldier> soldiers = elementsFinder.findSoldiersInZone(adjacentBaseBlocks);

            if (elementsFinder.zoneContainsEnemySoldier(bot, adjacentBaseBlocks)) {
                ArrayList<Block> supportAlliesBlocks = blockFinder.findAdjacentBlocks(baseBlock, 6, true);
                ArrayList<Soldier> supportAlliesSoldiers = elementsFinder.findSoldiersInZone(supportAlliesBlocks);
                Soldier enemySoldier = null;
                for (Soldier soldier : supportAlliesSoldiers) {
                    if (bot.ownsElement(soldier)) {
                        if (enemySoldier != null) {
                            gameManager.moveSoldierToDirection(soldier, map.getBlock(enemySoldier.getIndexX(), enemySoldier.getIndexY()));
                        }
                    } else {
                        enemySoldier = soldier;
                    }
                }
            }

            if (gameAnalyzer.hasArmyAdvantageInZone(bot, farAdjacentBaseBlocks)) {
                for (Soldier preparedAllie : soldiers) {
                    if (bot.ownsElement(preparedAllie)) {
                        Soldier enemyToKill = elementsFinder.findNearestEnemySoldier(preparedAllie, bot);
                        if (enemyToKill != null) {
                            gameManager.moveSoldierToDirection(preparedAllie, map.getBlock(enemyToKill.getIndexX(), enemyToKill.getIndexY()));
                        }
                    }
                }
                break;
            } else {
                adjacentBaseBlocks = blockFinder.findEmptyBlocks(adjacentBaseBlocks);
                RandomUtility.randomizeBlockList(adjacentBaseBlocks);
                gameManager.buySoldier(bot, adjacentBaseBlocks.get(0));
                Soldier newSoldier = (Soldier) elementsFinder.findElementOnBlock(adjacentBaseBlocks.get(0));
                if (newSoldier != null && bot.ownsElement(newSoldier)) {
                    for (Soldier soldier : soldiers) {
                        if (bot.ownsElement(soldier) && gameAnalyzer.isFavorableToMergeToDefend(newSoldier, soldier)) {
                            gameManager.moveSoldierToBlock(newSoldier, map.getBlock(soldier.getIndexX(), soldier.getIndexY()));
                        }
                    }
                }
            }
        }
    }

    private void retreatToAllieWhenPossible() {

        for (Player player : playersManager.getPlayerList()) {
            if (!player.equals(bot)) {
                for (Element element : player.getOwnedElementsList()) {
                    if (element instanceof Soldier enemySoldier) {
                        ArrayList<Block> possibleMovesSoldier = blockFinder.findPossibleMoveBlocksSoldier(enemySoldier, bot);
                        ArrayList<Soldier> possibleAlliesSoldiers = elementsFinder.findSoldiersInZone(possibleMovesSoldier);
                        if (!possibleAlliesSoldiers.isEmpty()) {
                            for (Soldier possibleVulnerableAllieSoldier : possibleAlliesSoldiers) {
                                if (gameAnalyzer.isFavorableToAttack(enemySoldier, possibleVulnerableAllieSoldier) ||
                                        gameAnalyzer.isStronger(enemySoldier, possibleVulnerableAllieSoldier)) {
                                    Soldier allieSoldier = elementsFinder.findNearestAllieSoldier(possibleVulnerableAllieSoldier, bot);
                                    if (possibleVulnerableAllieSoldier.canMove()) {
                                        if (allieSoldier != null) {
                                            gameManager.moveSoldierToDirection(possibleVulnerableAllieSoldier,
                                                    map.getBlock(allieSoldier.getIndexX(), allieSoldier.getIndexY()));
                                        } else {
                                            gameManager.moveSoldierToDirection(possibleVulnerableAllieSoldier,
                                                    map.getBlock(base.getIndexX(), base.getIndexY()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void mergeAlliesWhenFavorable() {
        for (Element element : bot.getOwnedElementsList()) {
            if (element instanceof Soldier soldier) {
                if (gameAnalyzer.soldierHasAdvantage(soldier)) {

                    ArrayList<Soldier> soldiers = elementsFinder.findSoldiersInZone(
                            blockFinder.findPossibleMoveBlocksSoldier(soldier, bot));
                    soldiers.remove(soldier);

                    for (Soldier otherSoldier : soldiers) {
                        if (soldier.getAttackPoint() < GameplayConfig.SOLDIER_ATTACK_LIMIT) {
                            if (otherSoldier.canMove()) {
                                gameManager.moveSoldierToDirection(otherSoldier,
                                        map.getBlock(soldier.getIndexX(), soldier.getIndexY()));
                            }
                        } else {
                            break;
                        }
                    }
                    Soldier enemySoldier = elementsFinder.findNearestEnemySoldier(soldier, bot);

                    if (enemySoldier != null && soldier.canMove()) {
                        gameManager.moveSoldierToDirection(soldier,
                                map.getBlock(enemySoldier.getIndexX(), enemySoldier.getIndexY()));
                    }
                }
            }
        }
    }

    private void conquerSpecialBlocksWhenFavorable() {
        for (Block specialBlock : specialBlocks) {
            ArrayList<Block> adjacentSpecialBlock = blockFinder.findAdjacentBlocks(specialBlock, 5, true);
            if (bot.getTotalGold() >= GameplayConfig.HOUSE_PRICE
                    && !elementsFinder.zoneContainsEnemyElement(Soldier.class, bot, adjacentSpecialBlock)
                    && elementsFinder.zoneContainsAllieSoldier(bot, adjacentSpecialBlock)) {
                gameManager.buyHouse(bot, specialBlock);

            } else if (bot.getTotalGold() >= GameplayConfig.ATTACK_TOWER_PRICE
                    && !elementsFinder.zoneContainsAllieSoldier(bot, adjacentSpecialBlock)) {
                gameManager.buyAttackTower(bot, specialBlock);

            } else if (bot.getTotalGold() >= GameplayConfig.DEFENSE_TOWER_PRICE
                    && elementsFinder.zoneContainsAllieSoldier(bot, adjacentSpecialBlock)) {
                gameManager.buyDefenseTower(bot, specialBlock);
            }
            if (bot.ownsBlock(specialBlock) && specialBlock.isEmpty()) {
                //@TODO
            } else if (!bot.ownsBlock(specialBlock)) {
                ArrayList<Block> adjacentBlocks = blockFinder.findAdjacentBlocks(
                        specialBlock.getIndexX(), specialBlock.getIndexY(), 4, false);
                if (elementsFinder.zoneContainsAllieSoldier(bot, adjacentBlocks)) {
                    Soldier soldier = elementsFinder.findNearestAllieSoldier(specialBlock, bot);
                    if (!elementsFinder.zoneContainsEnemySoldier(bot, adjacentBlocks)) {
                        if (soldier != null && soldier.canMove()) {
                            gameManager.moveSoldierToDirection(soldier, specialBlock);
                        }
                    } else {
                        Soldier enemySoldier = elementsFinder.findNearestEnemySoldier(soldier, bot);
                        if (enemySoldier != null && gameAnalyzer.isFavorableToAttack(soldier, enemySoldier)) {
                            gameManager.moveSoldierToDirection(soldier, specialBlock);
                        } else {
                            Soldier allieSoldier = elementsFinder.findNearestAllieSoldier(soldier, bot);
                            if (allieSoldier != null && (gameAnalyzer.isFavorableToMergeToAttack(soldier, allieSoldier)
                                    || gameAnalyzer.isFavorableToMergeToDefend(soldier, allieSoldier))) {
                                gameManager.moveSoldierToDirection(soldier, map.getBlock(allieSoldier.getIndexX(), allieSoldier.getIndexY()));
                            }
                        }
                    }
                }
            }
        }
    }

    private void buySoldierCloseOfTreeWhenPossible() {
        for (ForestTree candidateForestTree : elementsFinder.elementMapToList(ForestTree.class)) {
            ArrayList<Block> adjacentOfTree = blockFinder.findAdjacentBlocks(candidateForestTree.getIndexX(),
                    candidateForestTree.getIndexY(), 2, false);
            adjacentOfTree = blockFinder.findEmptyBlocks(adjacentOfTree);
            adjacentOfTree = blockFinder.findOwnedBlocks(bot, adjacentOfTree, true);
            if (!adjacentOfTree.isEmpty()) {
                RandomUtility.randomizeBlockList(adjacentOfTree);
                if (bot.ownsBlock(adjacentOfTree.get(0))) {
                    gameManager.buySoldier(bot, adjacentOfTree.get(0));
                }
            }
        }
    }

    private void buySoldierCloseOfWeakEnemyWhenPossible() {
        for (Player player : playersManager.getPlayerList()) {
            if (!player.equals(bot)) {
                for (Element element : player.getOwnedElementsList()) {
                    if (element instanceof Soldier lowLifeEnemySoldier) {
                        Block enemyBlock = map.getBlock(lowLifeEnemySoldier.getIndexX(), lowLifeEnemySoldier.getIndexY());
                        if (enemyBlock != null && lowLifeEnemySoldier.getHealthPoint() == 1) {
                            ArrayList<Block> adjacentBlocks = blockFinder.findAdjacentBlocks(
                                    lowLifeEnemySoldier.getIndexX(), lowLifeEnemySoldier.getIndexY(), 1, false);
                            for (Block block : adjacentBlocks) {
                                if (bot.ownsBlock(block) && block.isEmpty()) {
                                    gameManager.buySoldier(bot, block);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void buyElementWhenFavorable() {
        ArrayList<Block> candidatesSoldierBlock = blockFinder.findNearFrontierBlocks(bot);
        int stopCount = 0;
        while (bot.getTotalGold() >= GameplayConfig.SOLDIER_PRICE) {
            for (Block candidateBlocks : candidatesSoldierBlock) {
                if (bot.ownsBlock(candidateBlocks) && candidateBlocks.isEmpty()) {
                    gameManager.buySoldier(bot, candidateBlocks);
                }
            }
            stopCount++;
            if (stopCount > 10) {
                break;
            }
        }
    }

    public void buyStructuresWhenFavorable() {
        int soldierCount = elementsFinder.countElementType(Soldier.class, bot);
        //Buy max 4 soldiers in one turn
        if (soldierCount < 4 && bot.getTotalGold() >= GameplayConfig.SOLDIER_PRICE) {
            ArrayList<Block> nearFrontierBlocks = blockFinder.findNearFrontierBlocks(bot);

            for (int i = bot.getTotalGold(); i >= GameplayConfig.SOLDIER_PRICE; i -= GameplayConfig.SOLDIER_PRICE) {
                Block nearFrontierBlock = RandomUtility.getRandomEmptyBlock(nearFrontierBlocks);
                Block territoryBlock = RandomUtility.getRandomBlock(bot.getOwnedBlocksList());
                if (nearFrontierBlock != null && nearFrontierBlock.isEmpty()) {
                    gameManager.buySoldier(bot, nearFrontierBlock);
                } else if (territoryBlock != null && territoryBlock.isEmpty()) {
                    gameManager.buySoldier(bot, territoryBlock);
                }
            }
        } else {
            if (bot.getTotalGold() >= GameplayConfig.TOTAL_GOLD_MAX / 2) {
                ArrayList<Block> territory = bot.getOwnedBlocksList();
                territory = blockFinder.findEmptyBlocks(territory);
                ArrayList<Block> nearBaseBlockBlocks = blockFinder.findAdjacentBlocks(baseBlock, 7, true);
                if (!territory.isEmpty() && !nearBaseBlockBlocks.isEmpty()) {
                    RandomUtility.randomizeBlockList(territory);
                    for (Block block : territory) {
                        ArrayList<Block> adjacentBlocks = blockFinder.findAdjacentBlocks(block, 2, true);
                        ArrayList<Block> farAdjacentBlocks = blockFinder.findAdjacentBlocks(block, 4, true);
                        if (elementsFinder.zoneContainsEnemySoldier(bot, nearBaseBlockBlocks)) {
                            for (Block buySoldierBlock : nearBaseBlockBlocks) {
                                if (block.isEmpty() && bot.ownsBlock(buySoldierBlock)) {
                                    gameManager.buySoldier(bot, buySoldierBlock);
                                    break;
                                }
                            }
                        } else {
                            int attackTowerCount = elementsFinder.countElementType(AttackTower.class, bot);
                            int defenseTowerCount = elementsFinder.countElementType(DefenseTower.class, bot);
                            int houseCount = elementsFinder.countElementType(House.class, bot);

                            if (!gameAnalyzer.playerHasTerritoryDisadvantage(bot)) {
                                if (!elementsFinder.zoneContainsAllieSoldier(bot, adjacentBlocks)
                                        && !elementsFinder.zoneContainsEnemySoldier(bot, adjacentBlocks) && houseCount < 8) {
                                    //Buy max 8 houses in one turn

                                    ArrayList<Block> adjacentHouseBlocks = blockFinder.findAdjacentBlocks(block, 1, true);
                                    for (int i = 0; i < adjacentHouseBlocks.size(); i++) {
                                        if (!adjacentHouseBlocks.get(i).isEmpty() || !bot.ownsBlock(adjacentHouseBlocks.get(i))) {
                                            break;
                                        } else if (i == adjacentHouseBlocks.size() - 1) {
                                            gameManager.buyHouse(bot, block);
                                            break;
                                        }
                                    }
                                } else if (elementsFinder.zoneContainsAllieSoldier(bot, adjacentBlocks) && defenseTowerCount < 8) {
                                    //Buy max 8 defense tower in one turn
                                    Soldier weakerAllieSoldier = elementsFinder.findWeakestSoldierInZone(bot, adjacentBlocks, true);
                                    if (weakerAllieSoldier != null && weakerAllieSoldier.getHealthPoint() < GameplayConfig.SOLDIER_HEALTH_LIMIT) {
                                        gameManager.buyDefenseTower(bot, block);
                                        break;
                                    }
                                } else if (elementsFinder.zoneContainsEnemySoldier(bot, adjacentBlocks) && attackTowerCount < 8) {
                                    //Buy max 8 attack tower in one turn
                                    Soldier strongerEnemySoldier = elementsFinder.findStrongestSoldierInZone(bot, farAdjacentBlocks, true);
                                    if (strongerEnemySoldier != null && strongerEnemySoldier.getAttackPoint() < GameplayConfig.ATTACK_TOWER_INITIAL_HEALTH) {
                                        gameManager.buyAttackTower(bot, block);
                                        break;
                                    }
                                }
                            } else if (bot.getTotalGold() >= GameplayConfig.SOLDIER_PRICE) {
                                //@TODO
                            }
                        }
                    }
                }
            }
        }

    }

    public void allieSystematicAllieConquerBlocksAroundBase() {
        for (Block block : blockFinder.findAdjacentBlocks(baseBlock, 2, true)) {
            if (!bot.ownsBlock(block)) {
                Soldier soldier = elementsFinder.findNearestAllieSoldier(block, bot);
                if (soldier != null && soldier.canMove()) {
                    gameManager.moveSoldierToDirection(soldier, block);
                    break;
                }
                if (bot.getTotalGold() >= GameplayConfig.SOLDIER_PRICE) {
                    for (Block adjacentBlock : blockFinder.findAdjacentBlocks(block, 1, false)) {
                        if (bot.ownsBlock(adjacentBlock) && adjacentBlock.isEmpty()) {
                            gameManager.buySoldier(bot, adjacentBlock);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void attackAttackTower() {
        for (AttackTower attackTower : elementsFinder.elementMapToList(AttackTower.class)) {
            if (!bot.ownsElement(attackTower)) {
                Block attackTowerBlock = map.getBlock(attackTower.getIndexX(), attackTower.getIndexY());
                if (attackTowerBlock != null) {
                    int attackTowerAttackPoints = attackTowerBlock.isSpecial() ?
                            attackTower.getDamageDeals() + 1 : attackTower.getDamageDeals();
                    ArrayList<Block> range = blockFinder.findAdjacentBlocks(attackTowerBlock,
                            GameplayConfig.ATTACK_TOWER_RAYON + 1, true);
                    for (Block block : range) {
                        if (elementsFinder.elementTypeExistsOnBlock(Soldier.class, bot, block)) {
                            Soldier soldier = (Soldier) elementsFinder.findElementOnBlock(block);
                            if (soldier != null) {
                                if (soldier.canMove()
                                        && soldier.getHealthPoint() > attackTowerAttackPoints
                                        && soldier.getAttackPoint() >= attackTower.getHealthPoint() / 2) {
                                    //Soldier enough powerful
                                    gameManager.moveSoldierToDirection(soldier, attackTowerBlock);

                                } else {
                                    ArrayList<Block> possibleMoveSoldier = blockFinder.findPossibleMoveBlocksSoldier(soldier, bot);
                                    if (elementsFinder.zoneContainsAllieSoldier(bot, possibleMoveSoldier)) {
                                        Soldier allieSoldier = elementsFinder.findNearestAllieSoldier(soldier, bot);
                                        if (allieSoldier != null) {
                                            //Soldier move to nearest allie
                                            gameManager.moveSoldierToDirection(allieSoldier, block);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void buySoldierWhenNoSoldierAtFrontier() {
        for (int i = 0; i < bot.getOwnedElementsList().size(); i++) {
            Element element = bot.getOwnedElementsList().get(i);
            if (element instanceof Soldier) {
                break;
            } else if (i == bot.getOwnedElementsList().size() - 1 && bot.getTotalGold() >= GameplayConfig.SOLDIER_PRICE) {
                ArrayList<Block> nearFrontierBlock = blockFinder.findNearFrontierBlocks(bot);
                RandomUtility.randomizeBlockList(nearFrontierBlock);
                gameManager.buySoldier(bot, nearFrontierBlock.get(0));
                break;
            }
        }
    }

    /**
     * This method evaluates the state of the game to make automatic actions with {@link GameManager}.
     */
    public void runAction() {
        updateData();

        defendBaseWhenPossible();
        attackEnemyBasesWhenPossible();
        retreatToAllieWhenPossible();
        mergeAlliesWhenFavorable();
        attackAttackTower();

        buySoldierWhenNoSoldierAtFrontier();
        allieSystematicAllieConquerBlocksAroundBase();
        conquerSpecialBlocksWhenFavorable();
        buySoldierCloseOfTreeWhenPossible();
        buySoldierCloseOfWeakEnemyWhenPossible();
        if (gameManager.getGameStatistic().getSkipTurnCount(bot) < 15) {
            buyElementWhenFavorable();
        } else {
            buyStructuresWhenFavorable();
        }

        for (Element element : bot.getOwnedElementsList()) {
            if (element instanceof Soldier soldier) {
                if (soldier.canMove()) {

                    Block soldierBlock = map.getBlock(soldier.getIndexX(), soldier.getIndexY());
                    ArrayList<Block> adjacentBlocks = blockFinder.findAdjacentBlocks(soldierBlock, false);
                    ArrayList<Block> targetSoldierRange = blockFinder.findAdjacentBlocks(soldierBlock,
                            GameplayConfig.BLOCKS_RANGE_SOLDIER_MOVE, false);
                    ArrayList<Block> possibleMoveBlocks = blockFinder.findPossibleMoveBlocksSoldier(soldier, bot);

                    //Moves in direction of tree, house or attack tower
                    if (soldier.canMove()) {
                        for (Block block : targetSoldierRange) {
                            if (elementsFinder.elementTypeExistsOnBlock(ForestTree.class, block)
                                    || elementsFinder.elementTypeExistsOnBlock(House.class, block)
                                    || elementsFinder.elementTypeExistsOnBlock(DefenseTower.class, block)) {
                                gameManager.moveSoldierToDirection(soldier, block);
                                break;
                            }
                        }
                    }

                    // Moves to special block
                    if (soldier.canMove() && soldier.getHealthPoint() > soldier.getAttackPoint()) {
                        for (Block specialBlock : specialBlocks) {
                            if (specialBlock.isEmpty() && bot.ownsBlock(specialBlock)
                                    && targetSoldierRange.contains(specialBlock)) {
                                gameManager.moveSoldierToDirection(soldier, specialBlock);
                            }
                        }
                    }

                    // Moves to empty block conquerable
                    if (soldier.canMove()) {
                        Boolean isDangerousBlock;
                        soldierBlock = map.getBlock(soldier.getIndexX(), soldier.getIndexY());
                        RandomUtility.randomizeBlockList(adjacentBlocks);

                        for (Block block : adjacentBlocks) {
                            if (block.isEmpty() && !bot.ownsBlock(block)) {
                                isDangerousBlock = false;
                                ArrayList<Block> adjacentBlocksMovingBlock = blockFinder.findAdjacentBlocks(block, 1, false);
                                RandomUtility.randomizeBlockList(adjacentBlocksMovingBlock);

                                for (Block adjacentBlock : adjacentBlocksMovingBlock) {
                                    if (!adjacentBlock.equals(block) && !adjacentBlock.equals(soldierBlock)
                                            && elementsFinder.elementTypeExistsOnBlock(Soldier.class, adjacentBlock)) {
                                        Soldier possibleEnemySoldier = (Soldier) elementsFinder.findElementOnBlock(adjacentBlock);
                                        if (!bot.ownsElement(possibleEnemySoldier) && gameAnalyzer.isStronger(possibleEnemySoldier, soldier)) {
                                            isDangerousBlock = true;
                                            break;
                                        }
                                    }
                                }

                                if (!isDangerousBlock) {
                                    gameManager.moveSoldierToBlock(soldierBlock, block);
                                    break;
                                }
                            }
                        }
                    }

                    if (soldier.canMove()) {
                        if (bot.getGoldPerTurn() <= 30 && bot.getTotalOwnedBlocks() <= 60) {
                            ArrayList<Block> nearOutFrontierBlocks = blockFinder.findNearOutFrontierBlocks(bot);
                            RandomUtility.randomizeBlockList(nearOutFrontierBlocks);
                            for (Block block : nearOutFrontierBlocks) {
                                if (block != null && !block.isRemoved() && !bot.ownsBlock(block)) {
                                    gameManager.moveSoldierToDirection(soldier, block);
                                }
                            }
                        }
                    }

                    // Move to near frontier when health and attack enough higher
                    if (soldier.canMove()) {
                        if (soldier.getAttackPoint() >= ((GameplayConfig.SOLDIER_ATTACK_LIMIT * 3) / 4)
                                && soldier.getHealthPoint() >= ((GameplayConfig.SOLDIER_HEALTH_LIMIT * 3) / 4)) {
                            ArrayList<Block> frontierBlocks = blockFinder.findNearOutFrontierBlocks(bot);
                            frontierBlocks = blockFinder.findEmptyBlocks(frontierBlocks);
                            if (!frontierBlocks.isEmpty()) {
                                Block destination = blockFinder.findClosestBlock(frontierBlocks, soldierBlock);
                                if (destination != null) {
                                    logger.debug("move vers near frontier out");
                                    gameManager.moveSoldierToDirection(soldier, destination);
                                }
                            }
                        }
                    }

                    // Move to conquerable block
                    if (soldier.canMove()) {
                        ArrayList<Block> adjacentBlocksMovingBlock = blockFinder.findPossibleMoveBlocksSoldier(soldier, bot);
                        soldierBlock = map.getBlock(soldier.getIndexX(), soldier.getIndexY());
                        blockFinder.findEmptyBlocks(adjacentBlocksMovingBlock);
                        RandomUtility.randomizeBlockList(adjacentBlocksMovingBlock);

                        if (!adjacentBlocksMovingBlock.isEmpty() && soldierBlock != null) {
                            gameManager.moveSoldierToBlock(soldierBlock, adjacentBlocksMovingBlock.get(0));
                        }
                    }

                    //Move in direction of farthest empty block
                    if (soldier.canMove()) {
                        soldierBlock = map.getBlock(soldier.getIndexX(), soldier.getIndexY());
                        ArrayList<Block> blocks = blockFinder.findOwnedBlocks(bot, possibleMoveBlocks, true);
                        blocks = blockFinder.findEmptyBlocks(blocks);
                        Block tartgetBlock = blockFinder.findFarthestBlock(blocks, soldierBlock);
                        if (tartgetBlock != null) {
                            gameManager.moveSoldierToDirection(soldier, tartgetBlock);
                        }
                    }

                    //Move in direction of Base
                    if (soldier.canMove()) {
                        ArrayList<Block> closeAdjacentBaseBlock = blockFinder.findAdjacentBlocks(baseBlock, 1, true);
                        closeAdjacentBaseBlock.remove(baseBlock);
                        if (!closeAdjacentBaseBlock.isEmpty()) {
                            blockFinder.findEmptyBlocks(adjacentBlocks);
                            RandomUtility.randomizeBlockList(adjacentBlocks);
                            gameManager.moveSoldierToDirection(soldier, closeAdjacentBaseBlock.get(0));
                        }
                    }
                } else if (soldier.getHealthPoint() == 1 && bot.getTotalGold() >= GameplayConfig.SOLDIER_PRICE * 2) {
                    //The soldier can't move
                    Block soldierBlock = map.getBlock(soldier.getIndexX(), soldier.getIndexY());
                    ArrayList<Block> adjacentSoldierBlock = blockFinder.findAdjacentBlocks(soldierBlock, 1, false);
                    for (Block block : adjacentSoldierBlock) {
                        if (block.isEmpty() && bot.ownsBlock(block)) {
                            gameManager.buySoldier(bot, block);
                            gameManager.moveSoldierToBlock(block, soldierBlock);
                            break;
                        }
                    }
                }
            } else if (element instanceof House house && bot.getTotalGold() >= GameplayConfig.SOLDIER_PRICE * 3) {
                //Protect house
                Block houseBlock = map.getBlock(house.getIndexX(), house.getIndexY());
                ArrayList<Block> adjacentSoldierBlock = blockFinder.findAdjacentBlocks(houseBlock, 2, false);
                if (!elementsFinder.zoneContainsAllieSoldier(bot, adjacentSoldierBlock)) {
                    blockFinder.findEmptyBlocks(adjacentSoldierBlock);
                    RandomUtility.randomizeBlockList(adjacentSoldierBlock);
                    if (!adjacentSoldierBlock.isEmpty()) {
                        gameManager.buySoldier(bot, adjacentSoldierBlock.get(0));
                    }
                }
            }
        }
    }
}
