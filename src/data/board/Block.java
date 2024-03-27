package data.board;

import data.element.Base;
import data.element.Element;
import engine.util.ConversionUtility;

import java.io.Serializable;

/**
 * Representing a block on {@link GameMap}.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class Block implements Serializable {

    /**
     * Position x of the Block in pixels.
     */
    private final int x;

    /**
     * Position y of the Block in pixels.
     */
    private final int y;

    /**
     * When true it prevents existence of {@link Element} on this Block.
     */
    private Boolean isRemoved;

    /**
     * When true a {@link Base} can spawn on this Block at the start of the game.
     */
    private Boolean isAvailableSpawn;

    /**
     * When true there are no {@link Element} on this Block.
     */
    private Boolean isEmpty;

    /**
     * When true {@link Element} on this block obtains bonus.
     */
    private Boolean isSpecial;

    /**
     * By default, a new Block is defines as empty as not spawn, as not removed and as not special.
     *
     * @param x Position x of the Block in pixels.
     * @param y Position x of the Block in pixels.
     */
    public Block(int x, int y) {
        this.x = x;
        this.y = y;
        isRemoved = false;
        isAvailableSpawn = false;
        isEmpty = true;
        isSpecial = false;
    }

    /**
     * Prevents existence of {@link Element} on this Block.
     */
    public void remove() {
        isAvailableSpawn = false;
        isRemoved = true;
    }


    /**
     * Define this block as spawn to allow {@link Base} to spawn on this Block at the start of the game.
     */
    public void setAsSpawn() {
        isAvailableSpawn = true;
        isRemoved = false;
    }

    /**
     * Define this block as not spawn, no {@link Base} can spawns on this block at the start of the game.
     */
    public void setAsNotSpawn() {
        isAvailableSpawn = false;
        isRemoved = false;
    }

    public void setOccupied() {
        isEmpty = false;
    }

    public void setFree() {
        isEmpty = true;
    }

    public void setSpecial(Boolean bool) {
        isSpecial = bool;
    }

    /**
     * @return true when existence of {@link Element} on this Block is prevents.
     */
    public Boolean isRemoved() {
        return isRemoved;
    }

    /**
     * @return true when a player's {@link Base} can spawn on this Block at the start of the game.
     */
    public Boolean isSpawn() {
        return isAvailableSpawn;
    }

    /**
     * @return true when no {@link Element} exists on this Block.
     */
    public Boolean isEmpty() {
        return isEmpty;
    }

    /**
     * @return true when this {@link Block give a bonus multiplier to {@link Element}} on it.
     */
    public Boolean isSpecial() {
        return isSpecial;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
