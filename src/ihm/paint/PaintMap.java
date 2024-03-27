package ihm.paint;

import configuration.IHMConfig;
import data.board.Block;
import data.board.GameMap;
import ihm.util.ImageUtility;

import java.awt.*;

/**
 * This class provides methods for painting the game map and its blocks.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class PaintMap {

    private final int DEFAULT_MAP_IMAGE_WIDTH = 2048;
    private final int DEFAULT_MAP_IMAGE_HEIGHT = 1590;

    private String backgroundImagePath = null;
    private Image backgroundImage = null;


    /**
     * This method allows to draw the background of given GameMap instance.
     *
     * @param map The GameMap to draw.
     * @param g   The graphics context in which the drawing will be performed.
     */
    public void paint(Graphics g, GameMap map) {

        if (backgroundImagePath == null || !backgroundImagePath.equals(map.getBackgroundFilePath())) {
            backgroundImagePath = map.getBackgroundFilePath();
            backgroundImage = ImageUtility.readImage(backgroundImagePath);
        }

        ImageUtility.drawScaledImage(g, backgroundImage, 0, 0, 0, 0, DEFAULT_MAP_IMAGE_WIDTH, DEFAULT_MAP_IMAGE_HEIGHT);
    }

    /**
     * This method allows to draw a Block using the following parameters.
     *
     * @param block The Block to draw.
     * @param color The player's color or default block color if no players own this block.
     * @param g     The graphics context in which the drawing will be performed.
     */
    public void paint(Graphics g, Block block, Color color) {
        int x = block.getX();
        int y = block.getY();
        int length = IHMConfig.BLOCK_SIZE - IHMConfig.BLOCK_ESCAPEMENT_SIZE;

        g.setColor(color);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, IHMConfig.BLOCK_TRANSPARENCY));

        g.fillRect(x, y, length, length);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
}
