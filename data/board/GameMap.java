package data.board;

import engine.process.MapBuilder;

import java.io.Serializable;

/**
 * This class allow to keep in memory all{@link Block} Should be built with {@link MapBuilder}.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class GameMap implements Serializable {

    /**
     * Total amount of available {@link Block} in a game (only blocks not removed).
     */
    private int totalBlocks;

    /**
     * Sentence to define context of the Map.
     */
    private final String title;

    /**
     * The path of the text file parsed to build blocks of the map with {@link MapBuilder}.
     */
    private final String shapeFilePath;

    /**
     * The image path of the background style.
     */
    private final String backgroundFilePath;

    /**
     * The two-dimensional array to define which {@link Block} will be removed or not.
     */
    private Block[][] blocks;

    /**
     * This constructor only defines the shape the Map should have without build it.
     * It's necessary to use {@link MapBuilder} to fill Block[][] array.
     *
     * @param title              to define context of the Map.
     * @param shapeFilePath      path of the text file parsed to build blocks of the map with {@link MapBuilder}.
     * @param backgroundFilePath path of the background style.
     */
    public GameMap(String title, String shapeFilePath, String backgroundFilePath) {

        if (title == null) {
            title = "";
        }
        if (shapeFilePath == null) {
            shapeFilePath = "";
        }
        if (backgroundFilePath == null) {
            backgroundFilePath = "";
        }

        this.title = title;
        this.shapeFilePath = shapeFilePath;
        this.backgroundFilePath = backgroundFilePath;

    }

    /**
     * This constructor only defines the shape the Map should have without build it.
     * It's necessary to use {@link MapBuilder} to fill Block[][] array.
     */
    public GameMap() {
        this.title = "";
        this.shapeFilePath = "";
        this.backgroundFilePath = "";

    }

    /**
     * @return two-dimensional array of {@link Block} that define the map.
     */
    public Block[][] getBlocks() {
        return blocks;
    }


    /**
     * Retrieve a block in this map by giving x and y index of the block.
     *
     * @param indexX the x coordinate in the two-dimensional array of {@link Block}.
     * @param indexY the y coordinate in the two-dimensional array of {@link Block}.
     * @return the block that have these coordinate, when the block doesn't exist return null.
     */
    public Block getBlock(int indexX, int indexY) {
        if (indexX >= 0 && indexY >= 0 && indexY < getLines() && indexX < getColumns()) {
            return blocks[indexX][indexY];
        }
        return null;
    }

    /**
     * @return true if the map is full.
     */
    public Boolean mapIsFull() {
        for (int i = 0; i < getColumns(); i++) {
            for (int j = 0; j < getLines(); j++) {
                if (getBlock(i, j) != null) {
                    if (!getBlock(i, j).isRemoved()) {
                        if (getBlock(i, i).isEmpty()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void setBlocks(Block[][] blocks) {
        this.blocks = blocks;
    }

    public void setTotalBlocks(int totalBlocks) {
        this.totalBlocks = totalBlocks;
    }

    public int getLines() {
        return blocks[0].length;
    }

    public int getColumns() {
        return blocks.length;
    }

    public int getTotalBlocks() {
        return totalBlocks;
    }

    public String getShapeFilePath() {
        return shapeFilePath;
    }

    public String getBackgroundFilePath() {
        return backgroundFilePath;
    }

    public String getTitle() {
        return title;
    }
}
