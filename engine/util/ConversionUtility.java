package engine.util;

import configuration.IHMConfig;

/**
 * This class provides methods for some conversions.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class ConversionUtility {

    /**
     * Takes an integer value representing the pixel and returns its corresponding
     * index value.
     *
     * @param pixel the position in pixel.
     * @return an integer usable to find block in array.
     */
    public static int PixelToIndex(int pixel) {
        return (pixel / IHMConfig.BLOCK_SIZE) - 1;
    }

    /**
     * Takes a double value representing the pixel and returns its corresponding
     * index value.
     *
     * @param pixel the position in pixel.
     * @return an integer usable to find block in array.
     */
    public static int PixelToIndex(double pixel) {
        return (((int) pixel) / IHMConfig.BLOCK_SIZE) - 1;
    }

}
