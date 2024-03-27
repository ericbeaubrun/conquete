package engine.util;

import data.board.GameMap;
import main.game.GameManager;
import data.element.ElementsMap;
import engine.process.PlayersManager;
import log.LoggerUtility;
import main.game.Game;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * This class provides method for saving and loading {@link Game}.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class GameSerializer {

    private static final Logger logger = LoggerUtility.getLogger(GameSerializer.class);

    public static final String SAVED_GAME_PATH = "lastSave.ser";

    /**
     * @param path        the saved game file path.
     * @param gameManager saves the {@link Game} instance in binary file from the given path.
     */
    public static void saveGame(String path, GameManager gameManager) {

        try {
            OutputStream outputStream = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(gameManager.getMap());
            objectOutputStream.writeObject(gameManager.getPlayersManager());
            objectOutputStream.writeObject(gameManager.getElementsMap());
            objectOutputStream.writeObject(gameManager.getGameStatistic());
            objectOutputStream.close();
            outputStream.close();

            logger.info("Game successfully saved in " + path + " .");

        } catch (IOException e) {
            logger.warn("Failed to save game.");
        }
    }

    /**
     * @param path the saved game file path.
     * @return the {@link Game} instance read with the given file path.
     */
    public static Game getSavedGame(String path) {
        Game game = null;

        GameMap map;
        PlayersManager playersManager;
        ElementsMap elementsMap;
        GameStatistic gameStatistic;

        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            map = (GameMap) objectInputStream.readObject();
            playersManager = (PlayersManager) objectInputStream.readObject();
            elementsMap = (ElementsMap) objectInputStream.readObject();
            gameStatistic = (GameStatistic) objectInputStream.readObject();

            if (map != null && playersManager != null && elementsMap != null) {
                game = new Game(map, playersManager, elementsMap, gameStatistic);
            }
            objectInputStream.close();
            fileInputStream.close();

            logger.info("Game successfully load.");

        } catch (IOException | IllegalArgumentException | ClassNotFoundException e) {
            logger.warn("Failed to load game.");
        }
        return game;
    }
}
