package configuration;

import data.board.GameMap;

/**
 * Declares available maps in the game.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class MapsConfig {

    public static final GameMap[] AVAILABLE_MAPS_LIST = {
            new GameMap("Straight Highland", "/res/maps/straight_highland", "/res/maps/straight_highland.jpg"),
            new GameMap("Deep Forest", "/res/maps/deep_forest", "/res/maps/deep_forest.jpg"),
            new GameMap("Arid Winter", "/res/maps/arid_winter", "/res/maps/arid_winter.jpg"),
            new GameMap("Plain Fantasy", "/res/maps/plain_fantasy", "/res/maps/plain_fantasy.jpg"),
            new GameMap("Middle Age Expedition", "/res/maps/middle_age_expedition", "/res/maps/middle_age_expedition.jpg"),
            new GameMap("Center Pike", "/res/maps/center_pike", "/res/maps/center_pike.jpg"),
            new GameMap("Crossing Highland", "/res/maps/crossing_highland", "/res/maps/crossing_highland.jpg"),
            new GameMap("Closed Circle", "/res/maps/closed_circle", "/res/maps/closed_circle.jpg"),
            new GameMap("Desertic Dunes", "/res/maps/desertic_dunes", "/res/maps/desertic_dunes.jpg"),
            new GameMap("Tropical Islands", "/res/maps/tropical_islands", "/res/maps/tropical_islands.jpg"),
            new GameMap("Frosting Highland", "/res/maps/frosting_highland", "/res/maps/frosting_highland.jpg")
    };

    public static final int DEFAULT_RECT_MAP_LINES = 21;
    public static final int DEFAULT_RECT_MAP_COLUMNS = 38;
}
