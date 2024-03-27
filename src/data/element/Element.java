package data.element;

import data.board.Block;
import engine.util.ConversionUtility;

import java.io.Serializable;

/**
 * Data class that defines all objects that can be placed on a {@link Block} and have life point(s).
 *
 * @author William GABITA.
 */
public abstract class Element implements Serializable {

    /**
     * Position x of the Element in pixels.
     */
    private int x;

    /**
     * Position y of the Element in pixels.
     */
    private int y;

    /**
     * The life point(s) of the Element, when healthPoint are less than 0 the Element must be killed.
     */
    private int healthPoint;

    private int maxHealthPoint;

    /**
     * @param x           Position x of the Element in pixels.
     * @param y           Position y of the Element in pixels.
     * @param healthPoint Initial life point(s) of the Element.
     */
    public Element(int x, int y, int healthPoint, int maxHealthPoint) {
        this.x = x;
        this.y = y;
        this.healthPoint = healthPoint;
        this.maxHealthPoint = maxHealthPoint;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Change the position x and y of the Element to be put on a block.
     *
     * @param block the block on which element will be put
     */
    public void putOnBlock(Block block) {
        if (block != null) {
            x = block.getX();
            y = block.getY();
        }
    }

    public int getHealthPoint() {
        return healthPoint;
    }

    /**
     * @return the limit of health point of this Element.
     */
    public int getMaxHealthPoint() {
        return maxHealthPoint;
    }

    public void setHealthPoint(int healthPoint) {
        this.healthPoint = healthPoint;
    }

    /**
     * @return position x of the block converted in array index.
     */
    public int getIndexX() {
        return ConversionUtility.PixelToIndex(x);
    }

    /**
     * @return position y of the block converted in array index.
     */
    public int getIndexY() {
        return ConversionUtility.PixelToIndex(y);
    }

    public String positionToString() {
        return "(" + getIndexX() + ":" + getIndexY() + ")";
    }


}
