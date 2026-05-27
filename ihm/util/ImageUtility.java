package ihm.util;

import configuration.DevConfig;
import configuration.IHMConfig;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * This utility class provides several methods for reading and drawing images with scaling factor.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class ImageUtility {



    private static final Logger logger = LoggerUtility.getLogger(ImageUtility.class);

    /**
     * Reads an image from the specified file path.
     *
     * @param filePath the path of the image file to be read.
     * @return the {@link Image} Image object, return null if an exception occurs.
     */

    public static Image readImage(String filePath) {
        Image result = null;
        if (filePath != null) {
            try {
                if (DevConfig.READ_RESOURCE_AS_STREAM) {
                    URL url = IHMConfig.class.getResource(filePath);
                    if (url != null) {
                        result = ImageIO.read(url);
                    }
                } else {
                    result = ImageIO.read(new File("src"+filePath));
                }
            } catch (IllegalArgumentException | IOException e) {
                logger.error("Attempt to read image " + filePath + " but it failed.");
            }
        } else {
            logger.error("Bad path given to read image.");
        }
        return result;
    }

    /**
     * Reads an image icon from the specified file path.
     *
     * @param filePath the path of the image file to be read.
     * @param width    the width of the image.
     * @param height   the height of the image.
     * @return the {@link ImageIcon} Image object, return null if an exception occurs.
     */
    public static ImageIcon getIcon(String filePath, int width, int height) {
        ImageIcon result = null;
        Image image = null;
        if (filePath != null) {
            try {
                if (DevConfig.READ_RESOURCE_AS_STREAM) {
                    URL url = IHMConfig.class.getResource(filePath);
                    if (url != null) {
                        image = ImageIO.read(url);
                    }
                } else {
                    image = ImageIO.read(new File("src/"+filePath));
                }
                if (image != null) {
                    result = new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING));
                }
            } catch (IllegalArgumentException | IOException e) {
                logger.error("Attempt to read image " + filePath + " but it failed.");
            }
        } else {
            logger.error("Bad path given to read image.");
        }
        return result;
    }

    /**
     * Reads an image icon from the specified file path.
     *
     * @param filePath  the path of the image file to be read.
     * @param dimension the {@link Dimension} of the image.
     * @return the {@link ImageIcon} Image object, return null if an exception occurs.
     */
    public static ImageIcon getIcon(String filePath, Dimension dimension) {
        return dimension != null ? getIcon(filePath, dimension.width, dimension.height) : null;
    }

    /**
     * Reads an image from the specified file path and applying a scaling factor determined by the {@link IHMScaling}.
     *
     * @param filePath the path of the image file to be read.
     * @param width    the width of the image.
     * @param height   the height of the image.
     * @return scaled {@link ImageIcon}, or null if an exception occurs.
     */
    public static ImageIcon getScaledIcon(String filePath, int width, int height) {
        return width > 0 && height > 0 ? getIcon(filePath, IHMScaling.scale(width), IHMScaling.scale(height)) : null;
    }

    /**
     * Reads an image from the specified file path and applying a scaling factor determined by the {@link IHMScaling}.
     *
     * @param filePath  the path of the image file to be read.
     * @param dimension the {@link Dimension} of the image.
     * @return scaled {@link ImageIcon}, or null if an exception occurs.
     */
    public static ImageIcon getScaledIcon(String filePath, Dimension dimension) {
        return dimension != null ? getIcon(filePath, IHMScaling.scale(dimension.width),
                IHMScaling.scale(dimension.height)) : null;
    }

    /**
     * Takes an image to draw it with scaling factor determined by the {@link IHMScaling}.
     *
     * @param g       The graphics context in which the drawing will be performed.
     * @param image   The image to draw.
     * @param x       The x-coordinate of the image to draw.
     * @param y       The y-coordinate of the image to draw.
     * @param xOffset Adding x-offset to correctly placed image on the screen.
     * @param yOffset Adding y-offset to correctly placed image on the screen.
     * @param width   The width of the image that will be scaled.
     * @param height  The height of the image that will be scaled.
     */
    public static void drawScaledImage(Graphics g, Image image, int x, int y, int xOffset, int yOffset, int width,
                                       int height) {
        g.drawImage(image, x + IHMScaling.scale(xOffset), y + IHMScaling.scale(yOffset),
                IHMScaling.scale(width), IHMScaling.scale(height), null);
    }
}
