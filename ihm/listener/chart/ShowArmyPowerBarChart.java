package ihm.listener.chart;

import data.element.Soldier;
import data.player.Player;
import ihm.listener.GameListener;
import ihm.sidepanel.ChartGenerator;
import log.LoggerUtility;
import main.game.GameManager;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * This class allows to perform the generate and displaying a new {@link org.jfree.chart.ChartPanel} in option dialog that
 * represents the total quantity of health and attack point  of {@link Player}'s {@link Soldier} by implementing {@link ActionListener}.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class ShowArmyPowerBarChart extends GameListener implements ActionListener {


    private final ChartGenerator chartGenerator = new ChartGenerator();

    public ShowArmyPowerBarChart(GameManager gameManager) {
        super(gameManager);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayList<Player> players = getGameManager().getPlayersManager().getPlayerList();
        try {
            ChartPanel chart = chartGenerator.generateArmyPowerBarChart(players);
            JOptionPane.showOptionDialog(null, chart, "Chart",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
        } catch (IllegalArgumentException ex) {
            LoggerUtility.getLogger(ShowArmyPowerBarChart.class).error("Null data entry to generate army power chart.");
        }
    }
}
