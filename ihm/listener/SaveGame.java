package ihm.listener;

import main.game.GameManager;
import engine.util.GameSerializer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class allows to save a game by giving the {@link  GameManager}.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class SaveGame extends GameListener implements ActionListener {

    /**
     * @param gameManager the object that contains elements to save.
     */
    public SaveGame(GameManager gameManager) {
        super(gameManager);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GameSerializer.saveGame(GameSerializer.SAVED_GAME_PATH, getGameManager());
        JOptionPane.showMessageDialog(null, "Game saved !");
    }
}
