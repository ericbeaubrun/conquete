package ihm.listener.chart;

import data.player.Player;
import engine.datasearch.ElementsFinder;
import ihm.listener.GameListener;
import ihm.sidepanel.ChartGenerator;
import log.LoggerUtility;
import main.game.GameManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


/**
 * This class allows to perform the generate and displaying a new {@link org.jfree.chart.ChartPanel} in option dialog that
 * represents the origin of {@link Player}'s gold by implementing {@link ActionListener}.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class ShowEconomyBarChart extends GameListener implements ActionListener {

    private final ChartGenerator chartGenerator = new ChartGenerator();

    public ShowEconomyBarChart(GameManager gameManager) {
        super(gameManager);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        ArrayList<Player> players = getGameManager().getPlayersManager().getPlayerList();
        ElementsFinder elementsFinder = getGameManager().getElementsFinder();
        try {
            JOptionPane.showOptionDialog(null, chartGenerator.generateEconomyBarChart(players, elementsFinder), "Chart",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
        } catch (IllegalArgumentException ex) {
            LoggerUtility.getLogger(ShowEconomyBarChart.class).error("Null data entry to generate economy bar chart.");
        }

    }
}
