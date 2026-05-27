package engine.process;

import configuration.DevConfig;
import configuration.IHMConfig;
import configuration.MapsConfig;
import data.board.Block;
import data.board.GameMap;
import engine.exception.InvalidMapFileException;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;

/**
 * This class provides some methods for building maps from different shapes.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class MapBuilder {

    private static final Logger logger = LoggerUtility.getLogger(MapBuilder.class);

    private static final char BLOCK_CHAR = '.';
    private static final char SPAWN_BLOCK_CHAR = 'S';
    private static final char REMOVED_BLOCK_CHAR = 'X';
    private static final char SPECIAL_BLOCK_CHAR = 'C';

    private MapBuilder() {
        //private constructor to prevent instantiation.
    }

    /**
     * Allows to get an array of character that represents the shape of the map by giving the path of the file.
     *
     * @param path the path of the file to parse.
     * @return a character array that represents the shape of the map.
     * @throws InvalidMapFileException when any error occurred during open or reading the file.
     */
    public static char[][] parseMapShapeFile(String path) throws InvalidMapFileException {

        if (path == null) {
            throw new InvalidMapFileException("");
        }
        try {
            InputStream inputStream = DevConfig.READ_RESOURCE_AS_STREAM ?
                    MapBuilder.class.getResourceAsStream(path) : new FileInputStream("src"+path);

            if (inputStream == null) {
                throw new InvalidMapFileException("");
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            ArrayList<String> lines = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            reader.close();

            int totalColumns = lines.get(0).length();
            int totalLines = lines.size();

            char[][] chars = new char[totalColumns][totalLines];

            for (int indexY = 0; indexY < totalLines; indexY++) {

                if (lines.get(indexY).length() != totalColumns) {
                    logger.warn("Irregular line length (line " + (indexY + 1) + ") finds in " + path + ".");
                    throw new InvalidMapFileException(path);
                }

                for (int indexX = 0; indexX < totalColumns; indexX++) {

                    char type = lines.get(indexY).charAt(indexX);

                    if (type == BLOCK_CHAR || type == SPAWN_BLOCK_CHAR || type == REMOVED_BLOCK_CHAR || type == SPECIAL_BLOCK_CHAR) {
                        chars[indexX][indexY] = type;

                    } else {
                        logger.warn("Unrecognized character '" + type + "' finds in " + path + ".");
                        throw new InvalidMapFileException(path);
                    }
                }
            }

            logger.info("Map shape file " + path + " parsed successfully.");
            return chars;

        } catch (IOException e) {
            throw new InvalidMapFileException(path);
        }
    }

    /**
     * Builds a rectangular {@link GameMap} with the specified dimensions.
     *
     * @param map          the GameMap to build.
     * @param totalColumns the total number of columns for the map.
     * @param totalLines   the total number of lines for the map.
     */
    public static void buildRectMap(GameMap map, int totalColumns, int totalLines) {

        if (map != null) {
            int y, x;
            int totalBlocksAvailable = 0;

            Block[][] blocks = new Block[totalColumns][totalLines];
            y = IHMConfig.BLOCK_SIZE;

            for (int indexY = 0; indexY < totalLines; indexY++) {

                x = IHMConfig.BLOCK_SIZE;

                for (int indexX = 0; indexX < totalColumns; indexX++) {
                    blocks[indexX][indexY] = new Block(x, y);

                    if (indexY == 0) {
                        blocks[indexX][indexY].remove();

                    } else if (((indexY == (totalLines - 1)) && (indexX == 0)) ||
                            ((indexY == 1) && (indexX == 0)) ||
                            ((indexX == (totalColumns - 1)) && (indexY == 1)) ||
                            (((totalColumns - 1) == indexX) && (indexY == (totalLines - 1)))) {
                        blocks[indexX][indexY].setAsSpawn();
                        totalBlocksAvailable++;

                    } else {
                        totalBlocksAvailable++;
                    }

                    x += IHMConfig.BLOCK_SIZE;
                }
                y += IHMConfig.BLOCK_SIZE;
            }
            map.setBlocks(blocks);
            map.setTotalBlocks(totalBlocksAvailable);
            logger.info("Rectangle map built successfully.");
        }
    }

    /**
     * Builds a rectangular {@link GameMap} with the default dimensions.
     */
    public static GameMap buildRectMap() {
        GameMap map = new GameMap();
        buildRectMap(map, MapsConfig.DEFAULT_RECT_MAP_COLUMNS, MapsConfig.DEFAULT_RECT_MAP_LINES);
        return map;
    }

    /**
     * Builds a map object from a given shape file.
     * If the shape file is invalid, a default rectangular map will be created instead.
     *
     * @param map the {@link GameMap} to build.
     */
    public static void buildMap(GameMap map) {

        if (map != null) {
            try {
                int x, y;
                int totalColumns, totalLines, totalBlocksAvailable;
                char[][] characters = parseMapShapeFile(map.getShapeFilePath());

                totalBlocksAvailable = 0;

                totalLines = characters[0].length;
                totalColumns = characters.length;

                Block[][] blocks = new Block[totalColumns][totalLines];

                y = IHMConfig.BLOCK_SIZE;

                for (int indexY = 0; indexY < totalLines; indexY++) {

                    x = IHMConfig.BLOCK_SIZE;

                    for (int indexX = 0; indexX < totalColumns; indexX++) {

                        Block block = new Block(x, y);
                        char character = characters[indexX][indexY];

                        if (character == BLOCK_CHAR) {
                            totalBlocksAvailable++;

                        } else if (character == SPAWN_BLOCK_CHAR) {
                            block.setAsSpawn();
                            totalBlocksAvailable++;
                            logger.info("Available spawn detected in position " + block.positionToString() + " during map building.");

                        } else if (character == REMOVED_BLOCK_CHAR) {
                            block.remove();

                        } else if (character == SPECIAL_BLOCK_CHAR) {
                            block.setSpecial(true);
                            logger.info("Special block detected in position " + block.positionToString() + " during map building.");

                        } else {
                            block.remove();
                            logger.fatal("Bad character '" + character + "' detected in map file " + map.getShapeFilePath() + " during map building.");
                        }

                        blocks[indexX][indexY] = block;
                        x += IHMConfig.BLOCK_SIZE;
                    }
                    y += IHMConfig.BLOCK_SIZE;
                }

                map.setBlocks(blocks);
                map.setTotalBlocks(totalBlocksAvailable);

            } catch (InvalidMapFileException e) {
                //Build a rectangular map by default if an error occurred
                logger.warn("Map can't be build, shape file " + map.getShapeFilePath() + " is corrupted, rectangle map will be build as a replacement.");
                buildRectMap(map, MapsConfig.DEFAULT_RECT_MAP_COLUMNS, MapsConfig.DEFAULT_RECT_MAP_LINES);
            }
        }
    }
}


