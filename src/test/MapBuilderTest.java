package test;

import configuration.GameplayConfig;
import data.board.GameMap;
import engine.datasearch.BlockFinder;
import engine.process.MapBuilder;
import engine.exception.InvalidMapFileException;
import configuration.MapsConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class MapBuilderTest {

    @Test
    void mapsTest() {
        GameMap[] maps = MapsConfig.AVAILABLE_MAPS_LIST;
        assertNotNull(maps);
        for (GameMap map : maps) {
            assertNotNull(map);
        }
    }

    @Test
    void mapShapeFileParseTest() {
        GameMap[] maps = MapsConfig.AVAILABLE_MAPS_LIST;
        assertNotNull(maps);
        for (GameMap map : maps) {
            assertNotNull(map);
            try {
                MapBuilder.parseMapShapeFile(map.getShapeFilePath());
                assert true;
            } catch (InvalidMapFileException e) {
                assert false;
            }
        }
    }

    @Test
    void buildRectMapTest() {
        GameMap map = new GameMap();
        MapBuilder.buildRectMap(map, MapsConfig.DEFAULT_RECT_MAP_COLUMNS, MapsConfig.DEFAULT_RECT_MAP_LINES);
        assertNotNull(map.getBlocks());
    }

    @Test
    void buildMapTest() {
        GameMap[] maps = MapsConfig.AVAILABLE_MAPS_LIST;
        assertNotNull(maps);
        for (GameMap map : maps) {
            assertNotNull(maps);
            MapBuilder.buildMap(map);

            if ((map.getLines() == MapsConfig.DEFAULT_RECT_MAP_LINES) && (map.getColumns() == MapsConfig.DEFAULT_RECT_MAP_COLUMNS)) {
                int totalBlocks = ((MapsConfig.DEFAULT_RECT_MAP_COLUMNS * MapsConfig.DEFAULT_RECT_MAP_LINES) - MapsConfig.DEFAULT_RECT_MAP_COLUMNS);
                assertNotEquals(totalBlocks, map.getTotalBlocks());
            }
        }
    }

    /**
     * Vérifie s'il y a assez de spawn dans sur la map;
     */
    @Test
    void playabilityMapsTest() {
        BlockFinder blockFinder;
        GameMap[] maps = MapsConfig.AVAILABLE_MAPS_LIST;
        assertNotNull(maps);
        for (GameMap map : maps) {
            blockFinder = new BlockFinder(map);
            MapBuilder.buildMap(map);
            assertNotNull(map.getBlocks());
            assertTrue(blockFinder.findSpawns().size() <= GameplayConfig.MAX_PLAYERS);
        }
    }

}