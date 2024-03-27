package ihm.listener;

import main.game.GameManager;
import main.game.GameDisplay;

/**
 * This class is used for creating listeners for the game, it provides possibility to interact with the game
 * by giving {@link GameManager} and {@link GameDisplay}.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public abstract class GameListener {

    /**
     * Used to perform some actions in the game.
     */
    private GameManager gameManager;

    /**
     * Used to display actions performed.
     */
    private GameDisplay gameDisplay;

    /**
     * @param gameManager the IHM manager.
     * @param gameDisplay the process manager.
     */
    public GameListener(GameManager gameManager, GameDisplay gameDisplay) {
        this.gameManager = gameManager;
        this.gameDisplay = gameDisplay;
    }

    /**
     * This constructor allows to instantiate a new GameListener without giving {@link GameManager}.
     * This means the action will have only visual impact in the game.
     *
     * @param gameDisplay the IHM manager.
     */
    public GameListener(GameDisplay gameDisplay) {
        this.gameDisplay = gameDisplay;
    }

    /**
     * This constructor allows to instantiate a new GameListener without giving {@link GameDisplay}.
     * This means that the action will have no visual impact in the game.
     *
     * @param gameManager the process manager.
     */
    public GameListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public GameDisplay getGameDisplay() {
        return gameDisplay;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void setGameDisplay(GameDisplay gameDisplay) {
        this.gameDisplay = gameDisplay;
    }
}
