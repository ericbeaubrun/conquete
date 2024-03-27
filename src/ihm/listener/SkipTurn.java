package ihm.listener;

import data.player.Player;
import engine.process.PlayersManager;
import log.LoggerUtility;
import main.game.GameManager;
import engine.process.BotAction;
import main.game.GameDisplay;
import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class allows to perform the skip turn action in the game by implementing {@link ActionListener}.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class SkipTurn extends GameListener implements ActionListener {


    /**
     * The algorithm which evaluates what action a {@link Player defines as bot will do.}
     */
    private BotAction botAction;

    public SkipTurn(GameManager gameManager, GameDisplay gameDisplay) {
        super(gameManager, gameDisplay);
        try {
            botAction = new BotAction(gameManager);
        } catch (IllegalArgumentException e) {
            LoggerUtility.getLogger(SkipTurn.class).error("Unable to create bot actions in this game");
            e.printStackTrace();
        }
    }

    public void skipTurn() {
        GameManager gameManager = getGameManager();
        GameDisplay gameDisplay = getGameDisplay();
        PlayersManager playersManager = getGameManager().getPlayersManager();
        Player currentPlayer;

        gameManager.skipTurn();
        currentPlayer = getGameManager().getPlayersManager().getCurrentPlayer();

        gameDisplay.hideShopPanel();
        gameManager.resetSelection();

        if (currentPlayer.isBot() && botAction != null) {
            //Bot player
            if (!playersManager.playerHasWin() && playersManager.existsHumanPlayerNotLost()) {
                gameDisplay.disableListeners();
                gameDisplay.putBotAnimation();
                botAction.runAction();
                gameDisplay.disableMoveAllSoldiersInDirectionButton();
                gameDisplay.refreshDisplay(getGameManager());

            } else {
                gameDisplay.repaint();
            }
        } else {
            //Human player
            gameDisplay.enableListeners();
            gameDisplay.disableMoveAllSoldiersInDirectionButton();
            gameDisplay.refreshDisplay(getGameManager());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        skipTurn();
    }
}
