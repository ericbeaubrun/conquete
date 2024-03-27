package data.player;

import java.awt.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * The color associated with a player.
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class PlayerColor implements Serializable {

    private final Color color;

    /**
     * Defines a String representation of this color to name players.
     */
    private final String colorName;

    /**
     * @param name  the name of the player's color.
     * @param red   the red component of the color.
     * @param green the green component of the color.
     * @param blue  the blue component of the color.
     */
    public PlayerColor(String name, int red, int green, int blue) {
        this.colorName = name;
        this.color = new Color(red, green, blue);
    }

    /**
     * @param name the name of the player's color.
     * @param color the color to recognize player.
     */
    public PlayerColor(String name, Color color) {
        this.colorName = name;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public String getColorName() {
        return colorName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PlayerColor other = (PlayerColor) obj;
        return Objects.equals(color, other.color) && Objects.equals(colorName, other.colorName);
    }
}
