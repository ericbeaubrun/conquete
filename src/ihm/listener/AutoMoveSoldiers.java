package ihm.listener;

import data.player.Player;
import main.game.GameManager;
import main.game.GameDisplay;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class allows to perform the automatic move soldiers action in the game by implementing {@link ActionListener}.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class AutoMoveSoldiers extends GameListener implements ActionListener {

    /**
     * @param gameManager the manager of the game to interact with game data.
     * @param gameDisplay the display of the game to refresh the {@link data.element.Soldier} position on the map.
     */
    public AutoMoveSoldiers(GameManager gameManager, GameDisplay gameDisplay) {
        super(gameManager, gameDisplay);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Player player = getGameManager().getPlayersManager().getCurrentPlayer();
        getGameManager().autoMoveSoldiers(player);
        getGameDisplay().refreshDisplay(getGameManager());
    }
}
