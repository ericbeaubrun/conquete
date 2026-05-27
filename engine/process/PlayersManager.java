package engine.process;

import configuration.GameplayConfig;
import data.board.Block;
import data.board.GameMap;
import data.element.Base;
import data.element.Element;
import data.element.ElementsMap;
import data.element.Soldier;
import data.player.Player;
import data.player.PlayerColor;
import engine.datasearch.BlockFinder;
import engine.exception.NotEnoughPlayerColorsException;
import engine.exception.NotEnoughSpawnsException;
import engine.util.RandomUtility;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class allow to initialize players on a given {@link GameMap}, managing the player queue and find and modify {@link Player}'s data.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class PlayersManager implements Serializable {

    private static final Logger logger = LoggerUtility.getLogger(PlayersManager.class);

    /**
     * The number of {@link Player} in the game.
     */
    private int playerCount = 0;

    /**
     * Representing the order in which players will take turns.
     */
    private Queue<Player> playerQueue = new LinkedList<>();

    /**
     * The {@link GameMap} on which players will spawn.
     */
    private final GameMap map;

    /**
     * Representing all available spawn {@link Block} ont the {@link GameMap}.
     */
    private final ArrayList<Block> availableSpawns;

    /**
     * Constructs new PlayerManager with empty {@link Player} Queue and getting spawns of given map.
     *
     * @param map The {@link GameMap} on which players will spawn.
     */
    public PlayersManager(GameMap map) {
        this.map = map;
        BlockFinder blockFinder = new BlockFinder(map);
        availableSpawns = blockFinder.findSpawns();
    }

    /**
     * Recursive method that switches to the next player's turn.
     *
     * @param maxAmountRecCall The maximum amount of recursive calls allowed to switch to the next player's turn.
     */
    public void turnToNextPlayerRec(int maxAmountRecCall) {
        if (maxAmountRecCall > 0) {
            if (playerQueue.peek() != null && playerQueue.size() > 1) {
                playerQueue.peek().preventToPlay();
                playerQueue.offer(playerQueue.poll());
                if (playerQueue.peek() != null) {
                    playerQueue.peek().allowToPlay();
                }
            }
            //skip players who have lost
            if (getCurrentPlayer().hasLost()) {
                if (playerCount > 1) {
                    turnToNextPlayerRec(maxAmountRecCall - 1);
                }
            }
        }
    }

    /**
     * Switch to the next player's turn.
     */
    public void turnToNextPlayer() {
        turnToNextPlayerRec(GameplayConfig.MAX_PLAYERS * 2);
    }

    /**
     * Adding a new {@link Player} to the queue.
     *
     * @param isBot Representing whether the new player is a bot or not.
     */
    public void addNewPlayerToQueue(Boolean isBot) {
        if (hasEnoughSpaceForPlayer()) {
            try {
                Player newPlayer = initPlayerWithVerifications(isBot);
                addPlayerToQueue(newPlayer);
            } catch (NotEnoughSpawnsException e1) {
                logger.warn("Attempt to initialize new bot but there are not enough spawns available.");

            } catch (NotEnoughPlayerColorsException e2) {
                logger.warn("Attempt to initialize new bot but there are not enough colors available.");
            }
        }
    }

    /**
     * Adding a new human {@link Player} to the queue.
     */
    public void addNewBotToQueue() {
        addNewPlayerToQueue(true);
    }


    /**
     * Adding a new bot {@link Player} to the queue.
     */
    public void addNewPlayerToQueue() {
        addNewPlayerToQueue(false);
    }

    /**
     * checks if there is enough space in the player queue to add a new player.
     *
     * @return true if there is enough space for a new player.
     */
    private boolean hasEnoughSpaceForPlayer() {
        return playerQueue.size() < GameplayConfig.MAX_PLAYERS;
    }

    /**
     * It prevents all other {@link Player} from playing until their turn.
     */
    private void allowToPlayOnlyPlayerAtHead() {
        for (Player player : playerQueue) {
            if (player != null) {
                player.preventToPlay();
            }
        }
        if (getCurrentPlayer() != null) {
            getCurrentPlayer().allowToPlay();
        }
    }

    /**
     * Adds a given instance of {@link Player} to the player queue.
     *
     * @param player The new {@link Player} object to add to the player queue.
     */
    private void addPlayerToQueue(Player player) {
        if (player != null) {
            playerQueue.offer(player);

            allowToPlayOnlyPlayerAtHead();

            logger.info("New " + (player.isBot() ? "bot" : "player") + " [" + player.getColorName() + "] successfully initialized in queue.");
        }
    }

    /**
     * Searches for an available {@link PlayerColor} to assign to a new player.
     *
     * @return An available {@link PlayerColor} player color or null if no colors are available.
     */
    private PlayerColor findAvailableColor() {
        if (GameplayConfig.POSSIBLE_PLAYER_COLORS.length >= GameplayConfig.MAX_PLAYERS) {
            for (PlayerColor color : GameplayConfig.POSSIBLE_PLAYER_COLORS) {
                if (isColorAvailable(color)) {
                    return color;
                }
            }
        }
        return null;
    }

    /**
     * Initialize a new {@link Player} object by checking if there are enough available spawns and {@link PlayerColor}.
     *
     * @param isBot Representing whether the new player is a bot or not.
     * @return The new {@link Player} object to add to the player queue.
     * @throws NotEnoughSpawnsException       If there are no available spawns to place the new player.
     * @throws NotEnoughPlayerColorsException If there are no available player colors to assign to the new player.
     */
    private Player initPlayerWithVerifications(Boolean isBot) throws NotEnoughSpawnsException, NotEnoughPlayerColorsException {

        if (!availableSpawnExist()) {
            throw new NotEnoughSpawnsException();
        }

        PlayerColor playerColor = findAvailableColor();
        if (playerColor == null) {
            throw new NotEnoughPlayerColorsException();
        }

        playerCount++;

        return initPlayer(playerColor, isBot);
    }

    /**
     * Initialize a new Player object by checking if there are enough available spawns {@link Block} and {@link PlayerColor}.
     * Randomly selects available spawn block for the new player and initializes a new {@link Base} object at this block.
     *
     * @param color {@link PlayerColor} to assign to the new player.
     * @param isBot Representing whether the new player is a bot or not.
     * @return The new {@link Player} object to add to the player queue.
     */
    private Player initPlayer(PlayerColor color, Boolean isBot) {
        //for getting a random spawn block
        int randomInt = RandomUtility.getRandom(0, availableSpawns.size() - 1);

        //init player's Base
        Base base = new Base(availableSpawns.get(randomInt).getX(), availableSpawns.get(randomInt).getY());
        Block baseBlock = map.getBlock(base.getIndexX(), base.getIndexY());
        Player player = new Player(baseBlock, color, isBot);
        baseBlock.setOccupied();
        baseBlock.setAsNotSpawn();
        player.addOwnedElement(base);
        player.addOwnedBlock(baseBlock);

        //this spawn block is define as not available anymore
        availableSpawns.remove(randomInt);

        //add apparition blocks around the player's Base
        BlockFinder blockFinder = new BlockFinder(map);
        for (Block block : blockFinder.findAdjacentBlocks(baseBlock, true)) {
            player.addOwnedBlock(block);
        }

        return player;
    }

    /**
     * Randomizes the order of {@link Player} in the queue.
     */
    public void randomizePlayerQueue() {
        //randomizing the queue
        ArrayList<Player> playerList = getPlayerList();
        Collections.shuffle(playerList);
        playerQueue = new LinkedList<>(playerList);

        //allow to play only player at head
        for (Player player : playerQueue) {
            player.preventToPlay();
        }
        if (getCurrentPlayer() != null) {
            getCurrentPlayer().allowToPlay();
        }

        logger.info("Player queue randomized " + playerQueueToString() + ".");
    }


    /**
     * Sorts the {@link Player} queue to start with a human player.
     */
    public void sortToPutPlayerAtHead() {
        for (int i = 0; i < playerCount; i++) {
            if (!getCurrentPlayer().isBot()) {
                logger.info("Player queue sorted to start with player " + playerQueueToString() + ".");
                break;
            }

            turnToNextPlayer();

        }
    }

    /**
     * Sets the given {@link Player} as lost.
     * It sets the player's total gold and gold per turn to zero.
     * It removes all owned elements and owned blocks from the player and sets the blocks as free.
     * It prevents the given player to play anymore.
     * Give the territory of killed player to the attacking player.
     *
     * @param player          The {@link Player} object to set as lost.
     * @param attackingPlayer The other player that killer player, when attackingPlayer is not null,
     *                        the owned territory is given of player is given to attacking player.
     * @param elementsMap     The ElementsMap object on which given player's elements are presents.
     */
    public void setPlayerLost(Player player, Player attackingPlayer, ElementsMap elementsMap) {
        if (player != null && elementsMap != null) {
            //remove everything the player owns and set gold to 0
            player.setTotalGold(0);
            player.setGoldPerTurn(0);

            for (Element element : player.getOwnedElementsList()) {
                player.removeOwnedElement(element);
            }
            for (Block block : player.getOwnedBlocksList()) {
                if (!block.isEmpty()) {
                    elementsMap.removeElementOnBlock(block);
                }

                player.removeOwnedBlock(block);
                if (attackingPlayer != null) {
                    attackingPlayer.addOwnedBlock(block);
                }
            }

            Block baseBlock = player.getBaseBlock();
            elementsMap.removeElementOnBlock(baseBlock);

            playerCount--;
            player.setLost();

            logger.info((player.isBot() ? "Bot" : "Player") + " [" + player.getColorName() + "] lost.");
        }
    }

    /**
     * Sets the given {@link Player} as lost.
     * It sets the player's total gold and gold per turn to zero.
     * It removes all owned elements and owned blocks from the player and sets the blocks as free.
     * It prevents the given player to play anymore.
     *
     * @param player      The {@link Player} object to set as lost.
     * @param elementsMap The ElementsMap object on which given player's elements are presents.
     */
    public void setPlayerLost(Player player, ElementsMap elementsMap) {
        setPlayerLost(player, null, elementsMap);
    }


    /**
     * @param amount of gold to increment to given {@link Player}.
     */
    public void incrementCurrentPlayerGold(int amount) {
        getCurrentPlayer().setTotalGold(getCurrentPlayer().getTotalGold() + amount);
    }

    /**
     * @param amount of gold to decrement to given {@link Player}.
     */
    public void decrementCurrentPlayerGold(int amount) {
        getCurrentPlayer().setTotalGold(getCurrentPlayer().getTotalGold() - amount);
    }

    public ArrayList<Player> getPlayerList() {
        return new ArrayList<>(playerQueue);
    }

    public Player getCurrentPlayer() {
        return playerQueue.peek();
    }

    public Player getPlayerOwnsElement(Element element) {
        if (element != null) {
            for (Player player : playerQueue) {
                if (player.ownsElement(element)) {
                    return player;
                }
            }
        }
        return null;
    }

    public Player getPlayerOwnsBlock(Block block) {
        for (Player player : getPlayerList()) {
            if (player.ownsBlock(block)) {
                return player;
            }
        }
        return null;
    }

    public int getPlayerAmount() {
        return playerQueue.size();
    }

    private Boolean availableSpawnExist() {
        if (availableSpawns != null) {
            return !availableSpawns.isEmpty();
        }
        return false;
    }

    private Boolean isColorAvailable(PlayerColor color) {
        for (Player player : playerQueue) {
            if (player.getColor().equals(color.getColor())) {
                return false;
            }
        }
        return true;
    }

    public Boolean playerHasWin() {
        int amountPlayerNotLost = 0;
        for (Player player : playerQueue) {
            if (!player.hasLost()) {
                amountPlayerNotLost++;
            }
            if (amountPlayerNotLost > 1) {
                return false;
            }
        }

        return true;
    }

    public Boolean existsHumanPlayerNotLost() {
        Boolean result = false;
        for (Player player : playerQueue) {
            if (!player.hasLost() && !player.isBot()) {
                result = true;
                break;
            }
        }
        return result;
    }

    public Boolean currentPlayerOwnsBlock(Block block) {
        return getCurrentPlayer().ownsBlock(block);
    }

    public Boolean currentPlayerOwnsElement(Element element) {
        return getCurrentPlayer().ownsElement(element);
    }

    public Boolean currentPlayerHasEnoughGold(int gold) {
        return getCurrentPlayer().getTotalGold() >= gold;
    }

    public Boolean playerCanMoveSoldier(Player player) {
        if (player != null) {
            for (Element element : player.getOwnedElementsList()) {
                if (element instanceof Soldier soldier && soldier.canMove()) {
                    return true;
                }
            }
        }
        return false;
    }

    public String playerQueueToString() {
        ArrayList<Player> playerList = getPlayerList();
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < playerList.size(); i++) {
            Player player = playerList.get(i);
            if (!player.hasLost()) {
                result.append(player.getColorName());
                if (i < playerList.size() - 1) {
                    result.append(", ");
                }
            }
        }
        result.append("]");
        return result.toString();
    }

    public Boolean isOwnedByPlayer(Element element) {
        for (Player player : playerQueue) {
            if (player.ownsElement(element)) {
                return true;
            }
        }
        return false;
    }
}
