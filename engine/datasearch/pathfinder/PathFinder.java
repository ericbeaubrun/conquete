package engine.datasearch.pathfinder;

import data.board.Block;
import data.board.GameMap;
import data.element.*;
import data.player.Player;
import engine.datasearch.ElementsFinder;

import java.util.ArrayList;

/**
 * The PathFinder class implements the A* algorithm to find the shortest path between two given {@link Block} on a {@link GameMap}.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class PathFinder {

    private final ElementsFinder elementsFinder;
    private final GameMap map;

    /**
     * Create a new PathFinder on a given GameMap with specified element on blocks.
     *
     * @param elementsFinder elements presents on the map
     * @param map            the map to examine to find the shortest path.
     */
    public PathFinder(ElementsFinder elementsFinder, GameMap map) {
        this.elementsFinder = elementsFinder;
        this.map = map;
    }

    /**
     * Convert a blockNode to a {@link Block}.
     *
     * @param blockNode the block node to convert.
     * @return a Block if it exists on the map given in the constructor, null otherwise.
     */
    public Block blockNodeToBlock(BlockNode blockNode) {
        return blockNode != null ? map.getBlock(blockNode.getIndexX(), blockNode.getIndexY()) : null;
    }

    public void initCost(BlockNode[][] blocks, BlockNode startingBlock) {
        if (startingBlock != null) {
            for (int i = 0; i < blocks.length; i++) {
                for (int j = 0; j < blocks[0].length; j++) {
                    BlockNode block = blocks[i][j];
                    if (block.getInitialCost() != -1) {
                        int dx = Math.abs(block.getIndexX() - startingBlock.getIndexX());
                        int dy = Math.abs(block.getIndexY() - startingBlock.getIndexY());
                        int initialCost = dx + dy;
                        block.setInitialCost(initialCost);
                    }
                }
            }
        }
    }

    /**
     * Returns an ArrayList of adjacent BlockNodes to the given BlockNode, excluding Blocks with indicating that
     * they are "removed" or have with an element on them.
     *
     * @param blocks A two-dimensional of BlockNodes representing the game map.
     * @param block  The BlockNode to find adjacent BlockNodes to.
     * @return An ArrayList of adjacent BlockNodes to the given BlockNode, an empty ArrayList if there are not.
     */
    public ArrayList<BlockNode> getAdjacentBlockNode(BlockNode[][] blocks, BlockNode block) {
        ArrayList<BlockNode> result = new ArrayList<>();
        if (block != null) {
            int x = block.getIndexX();
            int y = block.getIndexY();
            int rows = blocks[0].length;
            int cols = blocks.length;

            if (x > 0 && y > 0 && blocks[x - 1][y].getInitialCost() != -1) {
                result.add(blocks[x - 1][y]);
            }
            if (y > 0 && x > 0 && blocks[x][y - 1].getInitialCost() != -1) {
                result.add(blocks[x][y - 1]);
            }
            if (y > 0 && x > 0 && x < cols - 1 && blocks[x + 1][y].getInitialCost() != -1) {
                result.add(blocks[x + 1][y]);
            }
            if (y < rows - 1 && x > 0 && y > 0 && blocks[x][y + 1].getInitialCost() != -1) {
                result.add(blocks[x][y + 1]);
            }
        }

        return result;
    }

    /**
     * Calculates the heuristic value for a given block node.
     *
     * @param block    the current block node
     * @param endBlock the end block node
     * @return the heuristic value for the current block node
     */
    public int calculateHeuristic(BlockNode block, BlockNode endBlock) {
        int dx = Math.abs(block.getIndexX() - endBlock.getIndexX());
        int dy = Math.abs(block.getIndexY() - endBlock.getIndexY());
        return dx + dy;
    }

    /**
     * Initializes the game map with BlockNodes, which contains information about their initial and real cost, as well as their parent and heuristic.
     *
     * @param player        The player that own the {@link Soldier} which navigate in the path.
     * @param startingBlock The beginning point of the path.
     * @param endingBlock   The destination point of the path.
     * @return A two-dimensional array of BlockNodes containing information about the initial and real cost, parent, and heuristic of each block.
     */
    public BlockNode[][] initBlocksNode(Player player, Block startingBlock, Block endingBlock) {

        BlockNode[][] blockNodes = new BlockNode[map.getColumns()][map.getLines()];
        for (int i = 0; i < map.getColumns(); i++) {
            for (int j = 0; j < map.getLines(); j++) {
                Block block = map.getBlock(i, j);
                BlockNode blockNode;

                if (!block.equals(startingBlock) && !block.equals(endingBlock) && (map.getBlock(i, j).isRemoved()
                        || elementsFinder.elementTypeExistsOnBlock(House.class, player, block)
                        || elementsFinder.elementTypeExistsOnBlock(AttackTower.class, player, block)
                        || elementsFinder.elementTypeExistsOnBlock(DefenseTower.class, player, block)
                        || elementsFinder.elementTypeExistsOnBlock(Base.class, player, block)
                        || elementsFinder.elementTypeExistsOnBlock(Soldier.class, player, block))) {
                    blockNode = new BlockNode(block.getX(), block.getY(), -1);
                } else {
                    blockNode = new BlockNode(block.getX(), block.getY(), 0);
                }

                blockNodes[i][j] = blockNode;
            }
        }
        return blockNodes;
    }

    /**
     * This method implements the A* algorithm to find the shortest path between two given {@link Block} on a {@link GameMap}.
     *
     * @param player        The player that needs to navigate the path.
     * @param startingBlock The starting block of the path.
     * @param endingBlock   The ending block of the path.
     * @return An ArrayList of BlockNodes representing the shortest path between the two given blocks, or null if no path was found.
     */
    public ArrayList<BlockNode> findDirectionAStar(Player player, Block startingBlock, Block endingBlock) {

        if (map != null && startingBlock != null && endingBlock != null) {

            BlockNode startingBlockNode = new BlockNode(startingBlock.getX(), startingBlock.getY(), 0);
            BlockNode endinfBlockNode = new BlockNode(endingBlock.getX(), endingBlock.getY(), 0);
            BlockNode[][] blockNodes = initBlocksNode(player, startingBlock, endingBlock);
            initCost(blockNodes, startingBlockNode);

            ArrayList<BlockNode> openList = new ArrayList<>();
            ArrayList<BlockNode> closedList = new ArrayList<>();

            // Add starting block to open list
            openList.add(startingBlockNode);

            while (!openList.isEmpty()) {

                // Find block with the lowest score in open list
                BlockNode currentBlock = openList.get(0);
                for (BlockNode block : openList) {
                    if (block.getScore() < currentBlock.getScore()) {
                        currentBlock = block;
                    }
                }

                // End block found, return path
                if (currentBlock.equals(endinfBlockNode)) {
                    ArrayList<BlockNode> path = new ArrayList<>();
                    BlockNode currentBlockNode = currentBlock;
                    while (currentBlockNode != null) {
                        path.add(currentBlockNode);
                        currentBlockNode = currentBlockNode.getParentNode();
                    }
                    return path;
                }

                // Remove current block from open list and add to closed list
                openList.remove(currentBlock);
                closedList.add(currentBlock);

                // Check adjacent blocks to current block
                for (BlockNode adjacentBlockNode : getAdjacentBlockNode(blockNodes, currentBlock)) {

                    // If adjacent block is already in closed list, skip it
                    if (closedList.contains(adjacentBlockNode)) {
                        continue;
                    }

                    int newRealCost = currentBlock.getRealCost() + adjacentBlockNode.getInitialCost();

                    // If adjacent block is not in open list, add it
                    if (!openList.contains(adjacentBlockNode)) {
                        adjacentBlockNode.setRealCost(newRealCost);
                        adjacentBlockNode.setHeuristic(calculateHeuristic(adjacentBlockNode, endinfBlockNode));
                        adjacentBlockNode.setParentNode(currentBlock);
                        openList.add(adjacentBlockNode);
                    }
                    // If adjacent block is already in open list, check if it's faster to go through current block
                    else {
                        if (newRealCost < adjacentBlockNode.getRealCost()) {
                            adjacentBlockNode.setRealCost(newRealCost);
                            adjacentBlockNode.setParentNode(currentBlock);
                        }
                    }
                }
            }
        }
        //No path found
        return null;
    }

    /**
     * Finds a block to move towards from a given set of starting blocks using the A* algorithm to find the shortest
     * path to an ending block. The method returns the closest block in the starting {@link Block} that is reachable from
     * the ending block using the shortest path.
     *
     * @param player         The player that owns the {@link Soldier} that needs to navigate the path.
     * @param startingBlocks An ArrayList of blocks to search for the closest reachable block from the ending block.
     * @param startingBlock  The starting block to begin the search from.
     * @param endingBlock    The ending block to find the shortest path to.
     * @return The closest reachable block to go in one direction, or null if no path is found or no reachable block is found.
     */
    public Block findBlockToDirection(Player player, ArrayList<Block> startingBlocks,
                                      Block startingBlock, Block endingBlock) {
        ArrayList<BlockNode> path = findDirectionAStar(player, endingBlock, startingBlock);
        if (path != null && !path.isEmpty()) {
            for (int i = path.size() - 1; i > 0; i--) {
                Block block = blockNodeToBlock(path.get(i));
                if (block != null && startingBlocks.contains(block)) {
                    return block;
                }
            }
        }
        return null;
    }
}

