package main.game;

import configuration.GameplayConfig;
import data.board.GameMap;
import data.player.Player;
import engine.util.GameStatistic;
import engine.exception.InvalidMapFileException;
import data.element.ElementsMap;
import engine.process.MapBuilder;
import engine.process.PlayersManager;
import log.LoggerUtility;
import org.apache.log4j.Logger;

/**
 * The Game class represents the main object in the game that contains all data and processes related to the game.
 * It manages all data and graphical elements, and performs all necessary actions during the game.
 * It has two constructors: used to create a new game and the other to load a new one.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class Game {

    private static final Logger logger = LoggerUtility.getLogger(Game.class);

    /**
     * Manage all data in the game to perform actions.
     */
    private final GameManager gameManager;

    /**
     * Manage all graphical element displaying in the game.
     */
    private final GameDisplay gameDisplay;


    /**
     * Create a new instance of the Game with the specified number of players and bots on the given {@link GameMap}.
     *
     * @param amountPlayers the number of human players to initialize (a total of 4 players maximum).
     * @param amountBots    the number of computer-controlled ({@link engine.process.BotAction} script) players to initialize.
     * @param map           the map to use for the game.
     * @throws InvalidMapFileException  if the map file is invalid.
     * @throws IllegalArgumentException if the total number of players exceeds the maximum allowed (4 max).
     */
    public Game(int amountPlayers, int amountBots, int difficultLevel, GameMap map) throws InvalidMapFileException, IllegalArgumentException {

        int totalAmountPlayers = amountBots + amountPlayers;
        if (totalAmountPlayers > GameplayConfig.MAX_PLAYERS || map == null) {
            logger.warn("Attempt to initialize too many players : (" + totalAmountPlayers + ") players");
            throw new IllegalArgumentException();
        }

        //Build the map if it was not
        if (map.getBlocks() == null) {
            MapBuilder.buildMap(map);
        }

        PlayersManager playersManager = new PlayersManager(map);

        for (int i = 0; i < amountPlayers; i++) {
            playersManager.addNewPlayerToQueue();
        }

        for (int i = 0; i < amountBots; i++) {
            playersManager.addNewBotToQueue();
        }

        logger.info("Player queue initialized " + playersManager.playerQueueToString());

        playersManager.randomizePlayerQueue();
        playersManager.sortToPutPlayerAtHead();

        gameManager = new GameManager(map, playersManager);
        gameDisplay = new GameDisplay(gameManager);

        //Defines difficult to bot
        for (Player player : gameManager.getPlayersManager().getPlayerList()) {
            if (player.isBot()) {
                player.setDifficultLevel(difficultLevel);
            }
        }

        gameDisplay.refreshDisplay(gameManager);

        logger.info("New game created with " + amountPlayers + " player(s) and " + amountBots + " bot(s) on map : " + map.getTitle() + ".");
    }

    /**
     * Create a new instance of the Game with the specified {@link PlayersManager}, {@link ElementsMap} and {@link GameStatistic} on the given {@link GameMap}.
     * This constructor can be used to load existing game by using {@link engine.util.GameSerializer}.
     *
     * @param map            the map to use for the game.
     * @param playersManager that contains all {@link Player}'s information.
     * @param elementsMap    that contains which {@link data.element.Element} is placed on which {@link data.board.Block}.
     * @param gameStatistic  that contains all game statistics during the time.
     * @throws IllegalArgumentException when the given playerManager or elementManager is null
     */
    public Game(GameMap map, PlayersManager playersManager, ElementsMap elementsMap, GameStatistic gameStatistic) throws IllegalArgumentException {

        if (playersManager == null) {
            logger.error("Attempt to initialize the game without players.");
            throw new IllegalArgumentException();
        }

        gameManager = new GameManager(map, playersManager);

        //Uses setters to load position of elements on the map
        if (elementsMap != null) {
            gameManager.setElementsMap(elementsMap);
        } else {
            logger.error("Elements statistics have not been found during game construction.");
            throw new IllegalArgumentException();
        }

        //Uses setters to load statistics
        if (gameStatistic != null) {
            gameManager.setGameStatistic(gameStatistic);
        } else {
            logger.error("Game statistics have not been found during game construction and will be reset.");
        }

        gameDisplay = new GameDisplay(gameManager);

        gameDisplay.hideSettingsPanel();
        gameDisplay.refreshDisplay(gameManager);


        logger.info("Game successfully created with " + playersManager.getPlayerAmount() + " players on map : " + map.getTitle() + ".");
    }

    public GameDisplay getGameDisplay() {
        return gameDisplay;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
