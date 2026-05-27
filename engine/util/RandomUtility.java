package engine.util;

import java.util.ArrayList;
import java.util.Collections;

import data.board.Block;
import data.board.GameMap;

/**
 * This utility class provides several methods to perform random operations, such as getting a random number or
 * getting a random block from a list.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class RandomUtility {
    /**
     * Gets a random integer between the specified minimum and maximum values (inclusive).
     *
     * @param min the minimum value of the range (inclusive).
     * @param max the maximum value of the range (inclusive).
     * @return a random integer between the minimum and maximum values (inclusive).
     */
    public static int getRandom(int min, int max) {
        return (int) (Math.random() * (max + 1 - min)) + min;
    }

    /**
     * Gets a random empty block from the specified game map.
     *
     * @param map the {@link GameMap} to get a block from.
     * @return a random empty {@link Block} from the specified game map, or null if the map is null or full.
     */
    public static Block getRandomEmptyBlock(GameMap map) {

        if (map == null || map.mapIsFull()) {
            return null;
        } else {
            int col = getRandom(0, map.getColumns() - 1);
            int lin = getRandom(0, map.getLines() - 1);

            Block block = map.getBlock(col, lin);
            return !block.isRemoved() && block.isEmpty() ? block : getRandomEmptyBlock(map);
        }

    }

    /**
     * Gets a random block from the specified list of blocks.
     *
     * @param blocks the list of {@link  Block} to get a block from.
     * @return a random block from the specified list of blocks, or null if the list is null or empty.
     */
    public static Block getRandomBlock(ArrayList<Block> blocks) {
        if (blocks != null && !blocks.isEmpty()) {
            return blocks.get(getRandom(0, blocks.size() - 1));
        }
        return null;
    }

    public static Block getRandomEmptyBlock(ArrayList<Block> blocks) {
        Block block = null;
        if (blocks != null && !blocks.isEmpty()) {
            block = blocks.get(getRandom(0, blocks.size() - 1));
            if (block != null && !block.isEmpty()) {
                block = null;
            }
        }
        return block;
    }

    /**
     * Randomizes the order of the given list of blocks.
     *
     * @param blocks the list of blocks to be randomized.
     */
    public static void randomizeBlockList(ArrayList<Block> blocks) {
        Collections.shuffle(blocks);
    }
}
