package ihm.listener.chart;

import data.player.Player;
import ihm.listener.GameListener;
import ihm.sidepanel.ChartGenerator;
import log.LoggerUtility;
import main.game.GameManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class allows to perform the generate and displaying a new {@link org.jfree.chart.ChartPanel} in option dialog that
 * represents the territory evolution of all {@link Player} in the game by implementing {@link ActionListener}.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class ShowTerritoryEvolutionLineChart extends GameListener implements ActionListener {

    private final ChartGenerator chartGenerator = new ChartGenerator();

    public ShowTerritoryEvolutionLineChart(GameManager gameManager) {
        super(gameManager);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        HashMap<Player, ArrayList<Integer>> territoryEvolution = getGameManager().getGameStatistic().getTerritoryEvolution();
        try{
            JOptionPane.showOptionDialog(null, chartGenerator.generateTerritoryEvolutionChart(territoryEvolution), "Chart",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
        } catch (IllegalArgumentException ex) {
            LoggerUtility.getLogger(ShowTerritoryEvolutionLineChart.class).error("Null data entry to generate territory evolution line chart.");
        }
    }
}
