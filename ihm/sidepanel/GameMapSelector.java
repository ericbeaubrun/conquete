package ihm.sidepanel;

import configuration.MapsConfig;
import data.board.GameMap;
import ihm.util.ImageUtility;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Provides a {@link LinkedList} of {@link  GameMap} to choose one.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class GameMapSelector {

    private final Logger logger = LoggerUtility.getLogger(GameMapSelector.class);

    private final Dimension MAP_CHOOSER_IMAGE_DIMENSION = new Dimension(512, 398);
    /**
     * A linked list to hold the available {@link  GameMap} in the game.
     */
    private final LinkedList<GameMap> availableMapsList = new LinkedList<>();

    private HashMap<String, ImageIcon> previewImagesMap = new HashMap<>();


    /**
     * @return {@link  GameMap} at the head of the list.
     */
    public GameMap getCurrentMap() {
        GameMap map = availableMapsList.peek();
        if (map != null) {
            return new GameMap(map.getTitle(), map.getShapeFilePath(), map.getBackgroundFilePath());
        }
        return null;
    }

    /**
     * Add all parameter {@link  GameMap} to the list of available maps which used to choose
     * one.
     *
     * @param maps
     */
    public GameMapSelector(GameMap[] maps) {
        if (maps != null) {
            availableMapsList.addAll(Arrays.asList(maps));
            for (GameMap map : MapsConfig.AVAILABLE_MAPS_LIST) {
                previewImagesMap.put(map.getTitle(), ImageUtility.getScaledIcon(getCurrentMap().getBackgroundFilePath(),
                        MAP_CHOOSER_IMAGE_DIMENSION));
                nextMap();
            }


        } else {
            logger.error("Unable to create map selector because no map is available.");
        }
    }

    public ImageIcon getCurrentMapImageIcon() {
        return previewImagesMap.get(getCurrentMap().getTitle());
    }

    /**
     * Get the next {@link  GameMap} in the list and update the list.
     */
    public void nextMap() {
        if (availableMapsList.size() > 1) {
            availableMapsList.addLast(availableMapsList.pollFirst());
        }
    }

    /**
     * Get the previous {@link  GameMap} in the list and update the list.
     */
    public void previousMap() {
        if (availableMapsList.size() > 1) {
            availableMapsList.addFirst(availableMapsList.pollLast());
        }
    }
}