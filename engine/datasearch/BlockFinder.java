package engine.datasearch;

import configuration.GameplayConfig;
import data.board.Block;
import data.board.GameMap;
import data.element.Soldier;
import data.player.Player;
import engine.datasearch.pathfinder.PathFinder;
import engine.util.DistanceCalculator;

import java.util.ArrayList;

/**
 * This class allows to search and filter {@link Block}
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class BlockFinder {

    private final GameMap map;
    private final PathFinder pathFinder;

    public BlockFinder(GameMap map, ElementsFinder elementsFinder) throws IllegalArgumentException {
        this.map = map;
        this.pathFinder = new PathFinder(elementsFinder, map);
    }

    public BlockFinder(GameMap map) {
        this.map = map;
        this.pathFinder = null;
    }

    /**
     * Determines if a block exists at the specified coordinates relative to the given x and y coordinates.
     *
     * @param x       the x-coordinate of the reference block
     * @param y       the y-coordinate of the reference block
     * @param xOffset the horizontal offset from the reference block
     * @param yOffset the vertical offset from the reference block
     * @return true if the given block exists.
     */
    public Boolean blockExists(int x, int y, int xOffset, int yOffset) {
        int targetX = x + xOffset;
        int targetY = y + yOffset;

        if (targetX >= 0 && targetX < map.getColumns() && targetY >= 0 && targetY < map.getLines()) {
            Block block = map.getBlock(targetX, targetY);
            if (block != null) {
                return !block.isRemoved();
            }
        }
        return false;
    }

    /**
     * @param x       the x-coordinate of the reference block.
     * @param y       the y-coordinate of the reference block.
     * @param xOffset the horizontal offset from the reference block.
     * @param yOffset the vertical offset from the reference block.
     * @return the block at the specified coordinates, or null if it does not exist.
     */
    public Block getBlock(int x, int y, int xOffset, int yOffset) {
        int targetX = x + xOffset;
        int targetY = y + yOffset;

        if (blockExists(x, y, xOffset, yOffset)) {
            return map.getBlock(targetX, targetY);
        }
        return null;
    }

    /**
     * @return an ArrayList of all spawns blocks in the map.
     */
    public ArrayList<Block> findSpawns() {
        ArrayList<Block> spawns = new ArrayList<Block>();
        for (int i = 0; i < map.getColumns(); i++) {
            for (int j = 0; j < map.getLines(); j++) {
                if (map.getBlock(i, j) != null) {
                    if (!map.getBlock(i, j).isRemoved() && map.getBlock(i, j).isSpawn()) {
                        spawns.add(map.getBlock(i, j));
                    }
                }
            }
        }
        return spawns;
    }

    /**
     * @return an ArrayList of all special blocks in the map.
     */
    public ArrayList<Block> findSpecialBlocks() {
        ArrayList<Block> spawns = new ArrayList<Block>();
        for (int i = 0; i < map.getColumns(); i++) {
            for (int j = 0; j < map.getLines(); j++) {
                if (map.getBlock(i, j) != null) {
                    if (!map.getBlock(i, j).isRemoved() && map.getBlock(i, j).isSpecial()) {
                        spawns.add(map.getBlock(i, j));
                    }
                }
            }
        }
        return spawns;
    }

    /**
     * @param x             the x-coordinate of the block to find adjacent blocks for.
     * @param y             the y-coordinate of the block to find adjacent blocks for.
     * @param amount        the range of adjacent blocks to find.
     * @param withDiagonals when true include diagonal blocks in the search.
     * @return an ArrayList of adjacent blocks to the specified block, up to the given amount.
     */
    public ArrayList<Block> findAdjacentBlocks(int x, int y, int amount, boolean withDiagonals) {
        ArrayList<Block> adjacentBlocks = new ArrayList<>();

        //Keeping the current block in result list
        adjacentBlocks.add(map.getBlock(x, y));

        //Find adjacent blocks by combinations of offset from current block
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int yOffset = -1; yOffset <= 1; yOffset++) {
                if ((xOffset == 0 && yOffset == 0) || !withDiagonals && xOffset != 0 && yOffset != 0
                        || !blockExists(x + xOffset, y + yOffset, 0, 0)) {
                    continue;
                } else {
                    Block block = map.getBlock(x + xOffset, y + yOffset);

                    if (block != null && !adjacentBlocks.contains(block)) {
                        adjacentBlocks.add(getBlock(x + xOffset, y + yOffset, 0, 0));
                    }
                }
            }
        }
        if (amount > 1) {
            ArrayList<Block> result = new ArrayList<>(adjacentBlocks);
            for (Block block : adjacentBlocks) {

                for (Block resultBlock : findAdjacentBlocks(block.getIndexX(), block.getIndexY(), amount - 1, withDiagonals)) {
                    if (!result.contains(resultBlock)) {
                        result.add(resultBlock);
                    }
                }
            }
            return result;
        } else {
            return adjacentBlocks;
        }
    }

    /**
     * @param block         the reference block to find adjacent blocks for
     * @param amount        the range of adjacent blocks to find.
     * @param withDiagonals when true, include diagonal blocks in the search.
     * @return an ArrayList of adjacent blocks to the specified block, up to the given amount
     */
    public ArrayList<Block> findAdjacentBlocks(Block block, int amount, boolean withDiagonals) {
        if (block != null) {

            return findAdjacentBlocks(block.getIndexX(), block.getIndexY(), amount, withDiagonals);
        }
        return new ArrayList<>();
    }

    /**
     * @param block the reference block to find adjacent blocks for
     * @return an ArrayList of adjacent blocks to the specified block, including diagonal blocks
     */
    public ArrayList<Block> findAdjacentBlocks(Block block, boolean withDiagonals) {
        if (block != null) {
            return findAdjacentBlocks(block.getIndexX(), block.getIndexY(), 1, withDiagonals);
        }
        return new ArrayList<>();
    }

    /**
     * Finds the blocks on which a soldier can be moved.
     *
     * @param x             the x coordinate of the soldier
     * @param y             the y coordinate of the soldier
     * @param amount        the remaining amount of moves the soldier has
     * @param player        the player who owns the soldier
     * @param withDiagonals true if diagonals blocks should be included, false otherwise
     * @return an ArrayList of blocks on which the soldier can be moved.
     * @throws NullPointerException if the player is null
     */
    public ArrayList<Block> findPossibleMoveBlocksSoldier(int x, int y, int amount, Player player,
                                                          boolean withDiagonals) {
        ArrayList<Block> adjacentBlocks = new ArrayList<>();

        if (player != null) {
            //Keeping the current block in result list
            adjacentBlocks.add(map.getBlock(x, y));

            //Find adjacent blocks by combinations of offset from current block
            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                for (int yOffset = -1; yOffset <= 1; yOffset++) {

                    if ((xOffset == 0 && yOffset == 0) || !withDiagonals && xOffset != 0 && yOffset != 0
                            || !blockExists(x + xOffset, y + yOffset, 0, 0)) {
                        //Current block is always in result list, keeping or not diagonals block
                        continue;
                    } else {
                        Block block = map.getBlock(x + xOffset, y + yOffset);

                        if (block != null && !adjacentBlocks.contains(block)) {
                            //Filter
                            if (block.isEmpty() && player.ownsBlock(block)) {
                                adjacentBlocks.add(getBlock(x + xOffset, y + yOffset, 0, 0));
                            }
                        }
                    }
                }
            }
            if (amount > 1) {
                ArrayList<Block> result = new ArrayList<>(adjacentBlocks);
                for (Block block : adjacentBlocks) {
                    //Recursive call for all blocks filtered
                    for (Block resultBlock : findPossibleMoveBlocksSoldier(block.getIndexX(), block.getIndexY(), amount - 1, player,
                            withDiagonals)) {
                        if (!result.contains(resultBlock)) {
                            result.add(resultBlock);
                        }
                    }
                }

                for (Block block : adjacentBlocks) {
                    for (Block block2 : findAdjacentBlocks(block, false)) {
                        if (!adjacentBlocks.contains(block2)) {
                            result.add(block2);
                        }
                    }
                }

                return result;

            } else {
                return adjacentBlocks;
            }
        } else {
            //return an Empty ArrayList
            return adjacentBlocks;
        }
    }

    public ArrayList<Block> findPossibleMoveBlocksSoldier(Block block, int amount, Player player, Boolean withDiagonals) {
        if (pathFinder != null && player != null) {
            return findPossibleMoveBlocksSoldier(block.getIndexX(), block.getIndexY(), amount, player, withDiagonals);
        }
        return new ArrayList<>();
    }

    public ArrayList<Block> findPossibleMoveBlocksSoldier(Soldier soldier, Player player) {
        if (player != null && soldier != null) {
            return findPossibleMoveBlocksSoldier(soldier.getIndexX(), soldier.getIndexY(), GameplayConfig.BLOCKS_RANGE_SOLDIER_MOVE, player, false);
        }
        return new ArrayList<>();
    }

    /**
     * Finds the closest block from a given list of blocks to a specified ending block.
     *
     * @param blockList   the list of blocks to search
     * @param endingBlock the ending block to calculate the distance from
     * @return the closest block from the list to the ending block, or null if the blockList is null or empty
     */
    public Block findClosestBlock(ArrayList<Block> blockList, Block endingBlock) {

        Block resultBlock = null;

        if (blockList != null) {
            if (blockList.size() >= 1) {
                resultBlock = blockList.get(0);

                for (Block block : blockList) {
                    if (DistanceCalculator.calculateDistance(block, endingBlock) < DistanceCalculator.calculateDistance(resultBlock, endingBlock)) {
                        resultBlock = block;
                    }
                }
            }
        }
        return resultBlock;
    }

    /**
     * Finds the farthest block from a given list of blocks to a specified destination block.
     *
     * @param blockList        the list of blocks to search
     * @param destinationBlock the destination block to calculate the distance from
     * @return the farthest block from the list to the destination block
     */
    public Block findFarthestBlock(ArrayList<Block> blockList, Block destinationBlock) {

        Block resultBlock = null;

        if (blockList != null && destinationBlock != null) {
            if (blockList.size() >= 1) {
                for (Block block : blockList) {
                    if (DistanceCalculator.calculateDistance(block, destinationBlock) >=
                            DistanceCalculator.calculateDistance(resultBlock, destinationBlock)) {
                        resultBlock = block;
                    }
                }
            }
        }

        return resultBlock;
    }


    /**
     * Finds the frontier blocks that are adjacent to the territory of a given player.
     *
     * @param player the player to find the owned blocks and frontier blocks for
     * @return an ArrayList of the frontier blocks adjacent to the owned blocks of the player,
     * or an empty ArrayList if the player's owned block list is empty.
     */
    public ArrayList<Block> findNearOutFrontierBlocks(Player player) {
        ArrayList<Block> frontierBlocks = new ArrayList<>();
        if (player != null) {
            for (Block block : player.getOwnedBlocksList()) {
                for (Block block2 : findAdjacentBlocks(block, false)) {
                    if (!block2.isRemoved() && !player.ownsBlock(block2)) {
                        frontierBlocks.add(block2);
                    }
                }
            }
        }
        return frontierBlocks;
    }

    /**
     * Finds the out frontier blocks that are adjacent to the territory of a given player.
     *
     * @param player the player to find the owned blocks and frontier blocks for
     * @return an ArrayList of the frontier blocks adjacent to the owned blocks of the player,
     * or an empty ArrayList if the player's owned block list is empty.
     */
    public ArrayList<Block> findNearFrontierBlocks(Player player) {
        ArrayList<Block> frontierBlocks = new ArrayList<>();
        if (player != null) {
            for (Block block : player.getOwnedBlocksList()) {
                for (Block block2 : findAdjacentBlocks(block, false)) {
                    if (!block2.isRemoved() && !player.ownsBlock(block2)) {
                        if (!frontierBlocks.contains(block)) {
                            frontierBlocks.add(block);
                        }
                    }
                }
            }
        }
        return frontierBlocks;
    }


    /**
     * Finds all owned or unowned blocks from a given list of blocks, for a specified player.
     *
     * @param player the player to find the owned or unowned blocks for
     * @param blocks the list of blocks to search
     * @param owned  indicating whether to find owned or unowned blocks
     * @return an ArrayList of owned or unowned blocks, depending on the Boolean value,
     * or an empty ArrayList if the player is null or the input list is null or empty
     */
    public ArrayList<Block> findOwnedBlocks(Player player, ArrayList<Block> blocks, Boolean owned) {
        ArrayList<Block> result = new ArrayList<>();
        if (player != null) {
            for (Block block : blocks) {
                if (owned && player.ownsBlock(block)) {
                    result.add(block);
                } else if (!owned && !player.ownsBlock(block)) {
                    result.add(block);
                }
            }
        }
        return result;
    }


    /**
     * Finds all empty blocks from a given list of blocks.
     *
     * @param blocks the list of blocks to search
     * @return an ArrayList of empty blocks from the input list, or an empty ArrayList if the input list is null or empty
     */
    public ArrayList<Block> findEmptyBlocks(ArrayList<Block> blocks) {

        ArrayList<Block> result = new ArrayList<>();
        if (blocks != null) {
            for (Block block : blocks) {
                if (block.isEmpty()) {
                    result.add(block);
                }
            }
        }
        return result;
    }


    /**
     * Finds a block in the given direction from a starting block, for a specified player.
     *
     * @param blockList      the list of blocks to search
     * @param player         the player to find the block for
     * @param startingBlock  the starting block to search from
     * @param directionBlock the block indicating the direction to search towards
     * @return a block in the direction of the direction block relative to the starting block, or null if the pathFinder is null
     */
    public Block findBlockToDirection(ArrayList<Block> blockList, Player player, Block startingBlock, Block directionBlock) {
        if (pathFinder != null && player != null && blockList != null
                && startingBlock != null && directionBlock != null) {
            return pathFinder.findBlockToDirection(player, blockList, startingBlock, directionBlock);
        }
        return null;
    }
}
