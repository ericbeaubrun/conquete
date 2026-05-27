package ihm.util;

import java.awt.*;

/**
 * Provides scaling utilities to adjust UI elements to fit different screen
 * resolutions.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public final class IHMScaling {

    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    /**
     * The scaling value to adjust elements on screen based on the resolution.
     * For a 1080p screen, the value will be ~1, which represents 100% native display.
     * For a 720p screen, the value will be ~0.83, which scales the display down to 83%.
     */
    public static final double SCALING_VALUE = SCREEN_SIZE.width * 1.035 / 2002;

    /**
     * Scales a double value based on the scaling value.
     *
     * @param value the value to be scaled.
     * @return the scaled value as an integer.
     */
    public static int scale(double value) {
        value *= SCALING_VALUE;
        return (int) value;
    }

    /**
     * Scales an integer value based on the scaling value.
     *
     * @param value the value to be scaled.
     * @return the scaled value as an integer.
     */
    public static int scale(int value) {
        value = (int) (value * SCALING_VALUE);
        return value;
    }
}