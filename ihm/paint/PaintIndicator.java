package ihm.paint;

import configuration.IHMConfig;
import data.board.Block;
import data.element.Element;
import ihm.util.IHMScaling;
import ihm.util.ImageUtility;

import java.awt.*;

/**
 * This class provides  methods for painting various indicators and statistics in the game.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class PaintIndicator {

    private final String POSSIBLE_ACTION_IMAGE_PATH = "/res/images/gameplay/possibleAction.png";
    private final String ALLIES_INDICATOR_IMAGE_PATH = "/res/images/gameplay/alliesIndicator.png";
    private final String ENNEMIES_INDICATOR_IMAGE_PATH = "/res/images/gameplay/enemiesIndicator.png";
    private final String MERGE_INDICATOR_IMAGE_PATH = "/res/images/gameplay/mergeIndicator.png";
    private final String DIE_INDICATOR_IMAGE_PATH = "/res/images/gameplay/dieIndicator.png";
    private final String SPECIAL_BLOCK_IMAGE_PATH = "/res/images/gameplay/specialBlockIndicator.png";
    private final String SPECIAL_BLOCK_REDUCED_IMAGE_PATH = "/res/images/gameplay/specialBlockIndicator.png";

    //read image before all to ensure reduce performance loss
    private final Image possibleActionImage = ImageUtility.readImage(POSSIBLE_ACTION_IMAGE_PATH);
    private final Image allieIndicatorImage = ImageUtility.readImage(ALLIES_INDICATOR_IMAGE_PATH);
    private final Image ennemieIndicatorImage = ImageUtility.readImage(ENNEMIES_INDICATOR_IMAGE_PATH);
    private final Image mergeIndicatorImage = ImageUtility.readImage(MERGE_INDICATOR_IMAGE_PATH);
    private final Image dieIndicatorImage = ImageUtility.readImage(DIE_INDICATOR_IMAGE_PATH);
    private final Image specialBlockIndicatorImage = ImageUtility.readImage(SPECIAL_BLOCK_IMAGE_PATH);
    private final Image specialBlockReducedIndicatorImage = ImageUtility.readImage(SPECIAL_BLOCK_REDUCED_IMAGE_PATH);

    /**
     * This method allows to draw special indicator on block define as special using the following parameters.
     *
     * @param g     The graphics context in which the drawing will be performed.
     * @param block The special block.
     */
    public void paintSpecialBlockIndicator(Graphics g, Block block) {
        if (block.isEmpty()) {
            ImageUtility.drawScaledImage(g, specialBlockIndicatorImage, block.getX(),
                    block.getY(), 13, 13, 20, 20);
        } else {
            ImageUtility.drawScaledImage(g, specialBlockReducedIndicatorImage, block.getX(),
                    block.getY(), 16, -7, 20, 20);
        }
    }

    /**
     * This method allows to paint a range of blocks around a given block, using the following parameters:
     *
     * @param g      The graphics context in which the painting will be performed.
     * @param block  The central block around which to paint the range.
     * @param color  The color to use for painting the range.
     * @param length The length of the range, in number of blocks.
     */
    public void paintRange(Graphics g, Block block, Color color, int length) {
        if (length >= 1) {

            int x = block.getX() - length * IHMConfig.BLOCK_SIZE;
            int y = block.getY() - length * IHMConfig.BLOCK_SIZE;

            int width = IHMConfig.BLOCK_SIZE * (2 * length + 1);
            int height = IHMConfig.BLOCK_SIZE * (2 * length + 1);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(4));


            g2d.drawRect(x - IHMScaling.scale(4), y - IHMScaling.scale(4), width, height);
        }
    }




    /**
     * This method allows to draw selected indicator on block that contains not attackable using the following parameters.
     *
     * @param g     The graphics context in which the drawing will be performed.
     * @param block The block that contains the soldier that can't be attacked.
     */
    public void paintAlliesIndicator(Graphics g, Block block) {
        ImageUtility.drawScaledImage(g, allieIndicatorImage, block.getX(), block.getY(), 8, 0, 32, 32);
    }

    /**
     * This method allows to draw selected indicator on block that contains attackable element using the following parameters.
     *
     * @param g     The graphics context in which the drawing will be performed.
     * @param block The block that contains the soldier that can be attacked.
     */
    public void paintAttackableIndicator(Graphics g, Block block) {
        ImageUtility.drawScaledImage(g, ennemieIndicatorImage, block.getX(), block.getY(), 10, 0, 32, 32);
    }

    /**
     * This method allows to draw merged indicator on block using the following parameters.
     *
     * @param g     The graphics context in which the drawing will be performed.
     * @param block The block that contains the soldier that can be merged.
     */
    public void paintMergeIndicator(Graphics g, Block block) {
        ImageUtility.drawScaledImage(g, mergeIndicatorImage, block.getX(), block.getY(), 8, 6, 32, 32);
    }

    /**
     * This method allows to draw selected indicator on a block using the following parameters.
     *
     * @param g     The graphics context in which the drawing will be performed.
     * @param block The block which is selected.
     */
    public void paintSelectedBlock(Graphics g, Block block) {
        int length = IHMConfig.BLOCK_SIZE - IHMConfig.BLOCK_ESCAPEMENT_SIZE;
        g.setColor(IHMConfig.SELECTED_BLOCK_COLOR);
        g.drawRect(block.getX(), block.getY(), length, length);
    }

    /**
     * This method allows to draw available move indicator on soldier using the following parameters:
     *
     * @param g The graphics context in which the drawing will be performed.
     * @param x The x-coordinate of the element to draw.
     * @param y The y-coordinate of the element to draw.
     */
    public void paintAvailableMoveIndicator(Graphics g, int x, int y) {
        ImageUtility.drawScaledImage(g, possibleActionImage, x, y, -3, -4, 18, 18);
    }

    /**
     * This method allows to draw a statistic for a given element, using the following parameters:
     *
     * @param g       The graphics context in which the drawing will be performed.
     * @param value   The value of the statistic to draw.
     * @param x       The x-coordinate of the element to draw.
     * @param y       The y-coordinate of the element to draw.
     * @param xOffset The distance in pixels to add to the x-coordinate of the element to draw the statistic.
     * @param yOffset The distance in pixels to add to the y-coordinate of the element to draw the statistic.
     * @param font    The font to use for drawing the statistic.
     * @param color   The color to use for drawing the statistic.
     */
    public void paintElementStatistic(Graphics g, int value, int x, int y, int xOffset, int yOffset, Font font, Color color) {
        g.setFont(font);
        g.setColor(color);
        g.drawString(Integer.toString(value), x + IHMScaling.scale(xOffset), y + IHMScaling.scale(yOffset));
    }

    public void paintHealthBar(Graphics g, Element element) {
        int pathIndex = ((6 * element.getHealthPoint()) / element.getMaxHealthPoint()) == 0 ? 1 : (6 * element.getHealthPoint()) / element.getMaxHealthPoint();

        ImageUtility.drawScaledImage(g, ImageUtility.readImage("/res/images/gameplay/health" + pathIndex + ".png"),
                element.getX(), element.getY(), 0, 39, 44, 10);
    }

}
