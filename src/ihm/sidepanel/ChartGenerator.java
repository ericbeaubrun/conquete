package ihm.sidepanel;

import configuration.GameplayConfig;
import configuration.IHMConfig;
import data.board.Block;
import data.board.GameMap;
import data.element.*;
import data.player.Player;
import engine.datasearch.ElementsFinder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Utility class for generating {@link JFreeChart} panels for visualizing some game data evolution.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class ChartGenerator {

    public ChartGenerator() {

    }

    /**
     * Generates a chart panel displaying the territory size evolution for each player over the turns.
     *
     * @param territoryEvolution the HashMap containing the territory size evolution for each player each turn. (shouldn't be null)
     * @return a ChartPanel containing the territory size evolution chart.
     * @throws IllegalArgumentException if the given territoryEvolution map is null.
     */
    public ChartPanel generateTerritoryEvolutionChart(HashMap<Player, ArrayList<Integer>> territoryEvolution) throws IllegalArgumentException {

        if (territoryEvolution != null) {
            ArrayList<XYSeries> seriesList = new ArrayList<>();
            ArrayList<Color> colorList = new ArrayList<>();
            int maxTerritoryValue = 0;
            int maxTurnValue = 0;

            for (Player player : territoryEvolution.keySet()) {
                XYSeries series = new XYSeries(player.getColorName());
                colorList.add(player.getColor());

                if (territoryEvolution.get(player).size() > maxTurnValue) {
                    maxTurnValue = territoryEvolution.get(player).size();
                }

                for (int i = 0; i < territoryEvolution.get(player).size(); i++) {
                    series.add(i + 1, territoryEvolution.get(player).get(i));

                    if (territoryEvolution.get(player).get(i) > maxTerritoryValue) {
                        maxTerritoryValue = territoryEvolution.get(player).get(i);
                    }
                }
                seriesList.add(series);
            }

            XYSeriesCollection dataset = new XYSeriesCollection();
            for (XYSeries series : seriesList) {
                dataset.addSeries(series);
            }

            JFreeChart chart = ChartFactory.createXYLineChart("", "Turn",
                    "Territory Size", dataset, PlotOrientation.VERTICAL, true, true, false);

            //Scaled X axis based on the maximum value on the graph
            NumberAxis xAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();
            int xUnit;
            if (maxTurnValue < 10) {
                xUnit = 1;
            } else {
                xUnit = (maxTurnValue / 10);
            }
            xAxis.setTickUnit(new NumberTickUnit(xUnit));

            //Scaled Y axis based on the maximum value on the graph
            NumberAxis yAxis = ((NumberAxis) chart.getXYPlot().getRangeAxis());
            int yUnit = maxTerritoryValue < 10 ? 1 : (maxTerritoryValue / 10);
            yAxis.setTickUnit(new NumberTickUnit(yUnit));

            XYPlot plot = chart.getXYPlot();

            //Set player colors on graph
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            for (int j = 0; j < seriesList.size(); j++) {
                renderer.setSeriesPaint(j, colorList.get(j));
            }

            plot.setRenderer(renderer);
            return new ChartPanel(chart);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Generates a chart panel displaying the amount of blocks owned this turn.
     *
     * @param players the list of players in the game. (shouldn't be null)
     * @param map     the game map that contains player's blocks. (shouldn't be null)
     * @return a ChartPanel containing the quantity of {@link data.board.Block} owned by each {@link Player}.
     * @throws IllegalArgumentException if the given players list or map is null
     */
    public ChartPanel generateDistributionPieChart(ArrayList<Player> players, GameMap map) throws IllegalArgumentException {
        if (players != null && map != null) {
            DefaultPieDataset dataset = new DefaultPieDataset();
            JFreeChart chart = ChartFactory.createPieChart("Amount of block per player each turn", dataset, true, true, false);
            PiePlot plot = (PiePlot) chart.getPlot();

            int neutralBlocks = map.getTotalBlocks();

            for (int i = 0; i <= players.size(); i++) {

                if (i == players.size()) {
                    dataset.setValue("Neutral", neutralBlocks);
                    plot.setSectionPaint(i, IHMConfig.NEUTRAL_BLOCK_COLOR);
                    break;
                } else {
                    Player player = players.get(i);
                    dataset.setValue(player.getColorName(), player.getTotalOwnedBlocks());
                    plot.setSectionPaint(i, player.getColor());
                    neutralBlocks -= player.getTotalOwnedBlocks();
                }
            }
            return new ChartPanel(chart);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public ChartPanel generateEconomyBarChart(ArrayList<Player> players, ElementsFinder elementFinder) throws IllegalArgumentException {
        if (players != null) {

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            JFreeChart chart = ChartFactory.createStackedBarChart("", "", "Gold / Turn", dataset,
                    PlotOrientation.VERTICAL, true, true, false);

            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            BarRenderer renderer = (BarRenderer) plot.getRenderer();

            int maxValue = 0;

            for (Player player : players) {
                int armyCount = 0;
                int notEmptyCount = 0;
                int territoryCount = 0;
                int housesCount = 0;
                int playerIndex = 0;

                //Calculate values on graph
                for (Block block : player.getOwnedBlocksList()) {
                    if (block.isEmpty()) {
                        territoryCount++;

                    } else {
                        notEmptyCount++;
                        if (elementFinder.elementTypeExistsOnBlock(ForestTree.class, block)) {
                            continue;
                        } else if (elementFinder.elementTypeExistsOnBlock(House.class, block)) {
                            House house = (House) elementFinder.findElementOnBlock(block);
                            housesCount += block.isSpecial() ? house.getBonusGold()
                                    * GameplayConfig.SPECIAL_BLOCK_BONUS_MULTIPLIER : house.getBonusGold();

                        } else if (elementFinder.elementTypeExistsOnBlock(Soldier.class, block)) {
                            Soldier soldier = (Soldier) elementFinder.findElementOnBlock(block);
                            armyCount += soldier.getGoldPerTurnMalus();

                        } else if (elementFinder.elementTypeExistsOnBlock(AttackTower.class, block)) {
                            armyCount += GameplayConfig.MALUS_GOLD_ATTACK_TOWER;

                        } else if (elementFinder.elementTypeExistsOnBlock(DefenseTower.class, block)) {
                            armyCount += GameplayConfig.MALUS_GOLD_DEFENSE_TOWER;
                        }
                    }

                    dataset.addValue(GameplayConfig.GOLD_PER_TURN_INITIAL, "Default profits", player.getColorName() + " +");
                    dataset.addValue(territoryCount, "Empty territory profits", player.getColorName() + " +");
                    dataset.addValue(housesCount, "Houses profits", player.getColorName() + " +");
                    dataset.addValue(armyCount, "Army cost/turn", player.getColorName() + " -");
                    //dataset.addValue(notEmptyCount, "Other losses", player.getColorName() + " -");

                    //Find max value for scaling axis
                    int[] values = {armyCount, notEmptyCount, territoryCount, housesCount};
                    for (int value : values) {
                        if (value > maxValue) {
                            maxValue = value;
                        }
                    }
                }
            }

            //Gains by default
            renderer.setSeriesPaint(0, new Color(0, 153, 0));
            //Empty territory  gains bar color
            renderer.setSeriesPaint(1, new Color(128, 255, 0));
            //Houses gains  bar color
            renderer.setSeriesPaint(2, new Color(255, 255, 0));
            //Army taxes  bar color
            renderer.setSeriesPaint(3, new Color(204, 0, 0));
            //Other taxes bar color
            //renderer.setSeriesPaint(4, new Color(255, 128, 0));

            //Scaled the axis based on the maximum value on the graph

            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            maxValue = maxValue < 10 ? 1 : maxValue / 10;
            rangeAxis.setTickUnit(new NumberTickUnit(maxValue));

            renderer.setItemMargin(0.3);

            plot.setRenderer(renderer);
            return new ChartPanel(chart);
        } else {
            throw new IllegalArgumentException();
        }
    }
//    }public ChartPanel generateEconomyBarChart(ArrayList<Player> players, ElementsFinder elementFinder) throws IllegalArgumentException {
//        if (players != null) {
//
//            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//
//            JFreeChart chart = ChartFactory.createStackedBarChart("", "", "Gold / Turn", dataset,
//                    PlotOrientation.VERTICAL, true, true, false);
//
//            CategoryPlot plot = (CategoryPlot) chart.getPlot();
//            GroupedStackedBarRenderer renderer = (GroupedStackedBarRenderer) plot.getRenderer();
//
//            int maxValue = 0;
//
//            for (Player player : players) {
//                int armyCount = 0;
//                int treesCount = 0;
//                int territoryCount = 0;
//                int housesCount = 0;
//
//                //Calculate values on graph
//                for (Block block : player.getOwnedBlocksList()) {
//                    if (block.isEmpty()) {
//                        territoryCount++;
//
//                    } else if (elementFinder.elementTypeExistsOnBlock(ForestTree.class, block)) {
//                        treesCount++;
//
//                    } else if (elementFinder.elementTypeExistsOnBlock(House.class, block)) {
//                        House house = (House) elementFinder.findElementOnBlock(block);
//                        housesCount += block.isSpecial() ? house.getBonusGold()
//                                * GameplayConfig.SPECIAL_BLOCK_BONUS_MULTIPLIER : house.getBonusGold();
//
//                    } else if (elementFinder.elementTypeExistsOnBlock(Soldier.class, block)) {
//                        Soldier soldier = (Soldier) elementFinder.findElementOnBlock(block);
//                        armyCount += soldier.getGoldPerTurnMalus();
//
//                    } else if (elementFinder.elementTypeExistsOnBlock(AttackTower.class, block)) {
//                        armyCount += GameplayConfig.MALUS_GOLD_ATTACK_TOWER;
//
//                    } else if (elementFinder.elementTypeExistsOnBlock(DefenseTower.class, block)) {
//                        armyCount += GameplayConfig.MALUS_GOLD_DEFENSE_TOWER;
//                    }
//
//                    dataset.addValue(GameplayConfig.GOLD_PER_TURN_INITIAL, "Default profits", "profits");
//                    dataset.addValue(territoryCount, "Empty territory profits", "profits");
//                    dataset.addValue(housesCount, "Houses profits", "profits");
//                    dataset.addValue(armyCount, "Army cost/turn", "losses");
//                    dataset.addValue(treesCount, "Trees on territory", "losses");
//
//                    //Find max value for scaling axis
//                    int[] values = {armyCount, treesCount, territoryCount, housesCount};
//                    for (int value : values) {
//                        if (value > maxValue) {
//                            maxValue = value;
//                        }
//                    }
//                }
//            }
//
//            //Gains by default
//            renderer.setSeriesPaint(0, new Color(0, 153, 0));
//            //Empty territory  gains bar color
//            renderer.setSeriesPaint(1, new Color(128, 255, 0));
//            //Houses gains  bar color
//            renderer.setSeriesPaint(2, new Color(255, 255, 0));
//            //Army taxes  bar color
//            renderer.setSeriesPaint(3, new Color(204, 0, 0));
//            //Trees taxes bar color
//            renderer.setSeriesPaint(4, new Color(255, 128, 0));
//
//            //Scaled the axis based on the maximum value on the graph
//
//            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
//            maxValue = maxValue < 10 ? 1 : maxValue / 10;
//            rangeAxis.setTickUnit(new NumberTickUnit(maxValue));
//
//            renderer.setItemMargin(0.4);
//
//            plot.setRenderer(renderer);
//            return new ChartPanel(chart);
//        } else {
//            throw new IllegalArgumentException();
//        }
//    }


    /**
     * Generates a chart panel displaying the total attack and health points of each player's {@link Soldier}
     * (represents the power army).
     *
     * @param players the list of players in the game. (shouldn't be null)
     * @return a ChartPanel representing the power army chart for each {@link Player}.
     * @throws IllegalArgumentException if the given players list is null.
     */
    public ChartPanel generateArmyPowerBarChart(ArrayList<Player> players) throws IllegalArgumentException {
        if (players != null) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            JFreeChart chart = ChartFactory.createBarChart("", "", "Attack + Health", dataset,
                    PlotOrientation.VERTICAL, true, true, false);

            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            BarRenderer renderer = (BarRenderer) plot.getRenderer();

            int maxValue = 0;

            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);

                int count = 0;

                //Calculate values on graph
                for (Element element : player.getOwnedElementsList()) {
                    if (element instanceof Soldier soldier) {
                        count += soldier.getAttackPoint() + soldier.getHealthPoint();
                    }
                }

                //Set player colors on graph
                dataset.setValue(count, player.getColorName(), player.getColorName());
                renderer.setSeriesPaint(i, player.getColor());

                if (count > maxValue) {
                    maxValue = count;
                }
            }

            //Scaled the axis based on the maximum value on the graph
            maxValue = maxValue < 10 ? 1 : maxValue / 10;
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setTickUnit(new NumberTickUnit(maxValue));

            plot.setRenderer(renderer);
            return new ChartPanel(chart);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
