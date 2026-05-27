package engine.util;

import data.board.Block;

/**
 * This utility class allows to calculate distance between two {@link Block} or {@link data.element.Element}.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class DistanceCalculator {

    /**
     * Calculates the distance between two blocks.
     *
     * @param xA the x-coordinate of the first block.
     * @param yA the y-coordinate of the first block.
     * @param xB the x-coordinate of the second block.
     * @param yB the y-coordinate of the second block.
     * @return the distance between the two blocks
     */
    public static int calculateDistance(int xA, int yA, int xB, int yB) {
        return (int) Math.sqrt((xA - xB) * (xA - xB) + (yA - yB) * (yA - yB));
    }

    /**
     * Calculates the distance between two blocks.
     *
     * @param blockA the first block
     * @param blockB the second block
     * @return the distance between the two blocks, return 0 if at least one of {@link Block} is null.
     */
    public static int calculateDistance(Block blockA, Block blockB) {
        return blockA != null && blockB != null ? calculateDistance(blockA.getX(), blockA.getY(), blockA.getX(), blockB.getY()) : 0;
    }

}
