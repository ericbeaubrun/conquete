package configuration;

import ihm.util.IHMScaling;

import java.awt.*;

/**
 * Declares various constants to configure graphical interface of the game.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class IHMConfig {

    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    public static final Dimension GAME_BUTTON_DIMENSION = new Dimension(IHMScaling.scale(185), IHMScaling.scale(60));

    public static final int PLAYER_INFORMATION_SIZE = IHMScaling.scale(16);
    public static final int BLOCK_SIZE = IHMScaling.scale(48);
    public static final int BLOCK_ESCAPEMENT_SIZE = IHMScaling.scale(4);

    public static final Font BUTTON_FONT = new Font(Font.DIALOG, Font.BOLD, IHMScaling.scale(20));
    public static final Font ELEMENT_STATISTICS_FONT = new Font(Font.DIALOG, Font.BOLD, IHMScaling.scale(16));
    public static final Font STRUCTURE_STATISTICS_FONT = new Font(Font.DIALOG, Font.BOLD, IHMScaling.scale(18));

    public static final Color ELEMENT_STATISTICS_COLOR = Color.WHITE;
    public static final Color BACKGROUND_GAME_DISPLAY_COLOR = Color.BLACK;
    public static final Color NEUTRAL_BLOCK_COLOR = new Color(70, 70, 70);
    public static final Color SELECTED_BLOCK_COLOR = Color.WHITE;

    public static final float BLOCK_TRANSPARENCY = 0.5f;

}
