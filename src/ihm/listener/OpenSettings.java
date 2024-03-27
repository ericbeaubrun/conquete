package ihm.listener;

import main.game.GameDisplay;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class allows to perform the opens and closes {@link ihm.sidepanel.SettingsPanel} in the {@link GameDisplay}
 * by implementing {@link ActionListener}.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class OpenSettings extends GameListener implements ActionListener {

    /**
     * @param gameDisplay the display where the {@link ihm.sidepanel.SettingsPanel} will be open.
     */
    public OpenSettings(GameDisplay gameDisplay) {
        super(gameDisplay);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (getGameDisplay().settingsPanelIsVisible()) {
            getGameDisplay().hideSettingsPanel();
        } else {
            getGameDisplay().showSettingsPanel();
        }
        getGameDisplay().repaint();
    }
}
