package main;

import configuration.IHMConfig;
import data.board.GameMap;
import engine.exception.InvalidMapFileException;
import engine.util.GameSerializer;
import ihm.menu.MainMenu;
import ihm.menu.StartMenu;
import log.LoggerUtility;
import main.game.Game;
import org.apache.log4j.Logger;

import javax.swing.*;

/**
 * This is the Main class, that contains the {@link Game} object to play a game.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class MainGUI extends JFrame {

    private final Logger logger = LoggerUtility.getLogger(MainGUI.class);

    private JPanel currentPanel = null;

    private final MainMenu mainMenu;

    private final StartMenu startMenu;

    private Game game = null;

    /**
     * Switches the current panel to the specified panel.
     *
     * @param panel the panel to switch to.
     */
    public void switchToPanel(JPanel panel) {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        if (panel != null) {
            add(panel);
            currentPanel = panel;
            repaint();
            setVisible(true);
        }
    }

    /**
     * Switches the current panel to the {@link main.game.GameDisplay}.
     */
    public void switchToGameDisplay() {
        switchToPanel(game.getGameDisplay());
    }

    /**
     * Switches the current panel to the {@link MainMenu}.
     */
    public void switchToMainMenu() {
        switchToPanel(mainMenu);
    }

    /**
     * Switches the current panel to the {@link StartMenu}.
     */
    public void switchToStartMenu() {
        switchToPanel(startMenu);
    }

    private MainGUI() {

        mainMenu = new MainMenu();
        startMenu = new StartMenu();

        setSize(IHMConfig.SCREEN_SIZE);
        setLocationRelativeTo(null);

        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
    }

    private static final MainGUI instance = new MainGUI();

    public static MainGUI getInstance() {
        return instance;
    }

    /**
     * Initializes a new game with the specified amount of players, amount of bots, and game map.
     *
     * @param amountPlayers the amount of human players in the game.
     * @param amountBots    the amount of bots in the game.
     * @param map           the game map to use.
     */
    public void initNewGame(int amountPlayers, int amountBots, int difficultLevel, GameMap map) {

        try {
            game = new Game(amountPlayers, amountBots, difficultLevel, map);
            logger.info("A new game starts successfully from now.");
        } catch (IllegalArgumentException e) {
            logger.error("An error occurred while launching the game : amount of player/bot is invalid.");
        } catch (InvalidMapFileException e) {
            logger.error("An error occurred while launching the game : map file is corrupted.");
        } catch (Exception e) {
            logger.fatal("An unknown error occurred while launching the game.");
        }
    }

    /**
     * Loads a saved game by using from the saved game file {@link GameSerializer}  and switches to the game display
     * if the saved {@link Game} object was successfully re-created.
     */
    public void loadSavedGame() {
        game = GameSerializer.getSavedGame(GameSerializer.SAVED_GAME_PATH);
        if (game != null) {
            switchToGameDisplay();
        }
    }

    /**
     * @return true if a {@link Game} object existing, false otherwise
     */
    public Boolean gameHasBeenCreated() {
        return game != null;
    }

    /**
     * @return true if a {@link Game} object existing and a player has won, false otherwise
     */
    public Boolean currentGameEnded() {
        if (game != null) {
            return game.getGameManager().gameIsEnded();
        }
        return false;
    }
}
