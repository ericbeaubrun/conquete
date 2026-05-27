package ihm.listener.shop;

import configuration.GameplayConfig;
import ihm.listener.GameListener;
import main.game.GameManager;
import main.game.GameDisplay;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class allows to perform the buy {@link  data.element.DefenseTower} action in the game by implementing {@link ActionListener}.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class BuyDefenseTower extends GameListener implements ActionListener {

    /**
     * @param gameManager the manager of the game to interact with game data.
     * @param gameDisplay the display of the game to refresh the {@link data.element.DefenseTower} position on the map.
     */
    public BuyDefenseTower(GameManager gameManager, GameDisplay gameDisplay) {
        super(gameManager, gameDisplay);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (getGameManager().getPlayersManager().currentPlayerHasEnoughGold(GameplayConfig.DEFENSE_TOWER_PRICE)) {
            getGameManager().buyDefenseTower(getGameManager().getPlayersManager().getCurrentPlayer(), getGameManager().getSelectedBlock());

            getGameDisplay().hideShopPanel();
            getGameManager().resetSelection();
            getGameDisplay().refreshDisplay(getGameManager());
        } else {
            JOptionPane.showMessageDialog(null, "Not enough gold");
        }
    }
}
