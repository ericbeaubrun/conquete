package ihm.sidepanel;

import configuration.IHMConfig;
import ihm.listener.*;
import ihm.listener.chart.ShowArmyPowerBarChart;
import ihm.listener.chart.ShowEconomyBarChart;
import ihm.listener.chart.ShowTerritoryDistributionPieChart;
import ihm.listener.chart.ShowTerritoryEvolutionLineChart;
import main.game.GameManager;
import ihm.util.IHMScaling;
import ihm.util.ImageUtility;
import main.MainGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This panel contains several buttons that allows to perform alternative actions in the game such as
 * show game information or leave the game.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class SettingsPanel extends JPanel {

    private final Dimension IMAGE_DIMENSION = new Dimension(IHMScaling.scale(32), IHMScaling.scale(32));
    private final Font LABEL_FONT = new Font(Font.DIALOG, Font.BOLD, IHMScaling.scale(30));
    private final Color FOREGROUND_COLOR = Color.WHITE;

    private final String TUTO_IMAGE_PATH = "/res/images/hud/tuto.png";
    private final String LEAVE_IMAGE_PATH = "/res/images/hud/leave.png";
    private final String SAVE_IMAGE_PATH = "/res/images/hud/save.png";
    private final String PIE_CHART_IMAGE_PATH = "/res/images/hud/pieChart.png";
    private final String BAR_CHART_IMAGE_PATH = "/res/images/hud/barChart.png";
    private final String BAR_CHART_2_IMAGE_PATH = "/res/images/hud/barChart2.png";
    private final String LINE_CHART_IMAGE_PATH = "/res/images/hud/lineChart.png";

    private final JLabel turnCountLabel = new JLabel();

    private final JButton showTerritoryEvolutionChartButton = new JButton(" Evolution");
    private final JButton showPowerArmyChartButton = new JButton(" Army");
    private final JButton showTerritoryDistributionChartButton = new JButton(" Distribution");
    private final JButton showEconomyChartButton = new JButton(" Economy");
    private final JButton tutorialButton = new JButton(" Tutorial");
    private final JButton saveGameButton = new JButton(" Save");
    private final JButton leaveGameButton = new JButton(" Leave");

    class LeaveGame implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            MainGUI.getInstance().switchToMainMenu();
        }
    }

    class OpenTutorial implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, new TutorialPanel(), "Tutorial", JOptionPane.PLAIN_MESSAGE);
        }
    }

    public void initActions(GameManager gameManager) {
        tutorialButton.addActionListener(new OpenTutorial());
        leaveGameButton.addActionListener(new LeaveGame());
        saveGameButton.addActionListener(new SaveGame(gameManager));
        showTerritoryEvolutionChartButton.addActionListener(new ShowTerritoryEvolutionLineChart(gameManager));
        showPowerArmyChartButton.addActionListener(new ShowArmyPowerBarChart(gameManager));
        showTerritoryDistributionChartButton.addActionListener(new ShowTerritoryDistributionPieChart(gameManager));
        showEconomyChartButton.addActionListener(new ShowEconomyBarChart(gameManager));
    }

    public void initStyles() {
        turnCountLabel.setOpaque(true);
        showEconomyChartButton.setEnabled(false);
        showPowerArmyChartButton.setEnabled(false);
        showTerritoryEvolutionChartButton.setEnabled(false);
        showTerritoryDistributionChartButton.setEnabled(false);

        saveGameButton.setIcon(ImageUtility.getScaledIcon(SAVE_IMAGE_PATH, IMAGE_DIMENSION));
        tutorialButton.setIcon(ImageUtility.getScaledIcon(TUTO_IMAGE_PATH, IMAGE_DIMENSION));
        leaveGameButton.setIcon(ImageUtility.getScaledIcon(LEAVE_IMAGE_PATH, IMAGE_DIMENSION));
        showEconomyChartButton.setIcon(ImageUtility.getScaledIcon(BAR_CHART_2_IMAGE_PATH, IMAGE_DIMENSION));
        showPowerArmyChartButton.setIcon(ImageUtility.getScaledIcon(BAR_CHART_IMAGE_PATH, IMAGE_DIMENSION));
        showTerritoryEvolutionChartButton.setIcon(ImageUtility.getScaledIcon(LINE_CHART_IMAGE_PATH, IMAGE_DIMENSION));
        showTerritoryDistributionChartButton.setIcon(ImageUtility.getScaledIcon(PIE_CHART_IMAGE_PATH, IMAGE_DIMENSION));

        saveGameButton.setPreferredSize(IHMConfig.GAME_BUTTON_DIMENSION);
        tutorialButton.setPreferredSize(IHMConfig.GAME_BUTTON_DIMENSION);
        leaveGameButton.setPreferredSize(IHMConfig.GAME_BUTTON_DIMENSION);
        showEconomyChartButton.setPreferredSize(IHMConfig.GAME_BUTTON_DIMENSION);
        showPowerArmyChartButton.setPreferredSize(IHMConfig.GAME_BUTTON_DIMENSION);
        showTerritoryEvolutionChartButton.setPreferredSize(IHMConfig.GAME_BUTTON_DIMENSION);
        showTerritoryDistributionChartButton.setPreferredSize(IHMConfig.GAME_BUTTON_DIMENSION);

        tutorialButton.setVerticalTextPosition(SwingConstants.CENTER);
        saveGameButton.setVerticalTextPosition(SwingConstants.CENTER);
        leaveGameButton.setVerticalTextPosition(SwingConstants.CENTER);
        showEconomyChartButton.setVerticalTextPosition(SwingConstants.CENTER);
        showPowerArmyChartButton.setVerticalTextPosition(SwingConstants.CENTER);
        showTerritoryEvolutionChartButton.setVerticalTextPosition(SwingConstants.CENTER);
        showTerritoryDistributionChartButton.setVerticalTextPosition(SwingConstants.CENTER);

        tutorialButton.setFocusPainted(false);
        saveGameButton.setFocusPainted(false);
        leaveGameButton.setFocusPainted(false);
        showEconomyChartButton.setFocusPainted(false);
        showPowerArmyChartButton.setFocusPainted(false);
        showTerritoryEvolutionChartButton.setFocusPainted(false);
        showTerritoryDistributionChartButton.setFocusPainted(false);

        saveGameButton.setFont(IHMConfig.BUTTON_FONT);
        tutorialButton.setFont(IHMConfig.BUTTON_FONT);
        leaveGameButton.setFont(IHMConfig.BUTTON_FONT);
        showEconomyChartButton.setFont(IHMConfig.BUTTON_FONT);
        showPowerArmyChartButton.setFont(IHMConfig.BUTTON_FONT);
        showTerritoryEvolutionChartButton.setFont(IHMConfig.BUTTON_FONT);
        showTerritoryDistributionChartButton.setFont(IHMConfig.BUTTON_FONT);

        turnCountLabel.setForeground(FOREGROUND_COLOR);
        turnCountLabel.setFont(LABEL_FONT);
        tutorialButton.setForeground(FOREGROUND_COLOR);
        saveGameButton.setForeground(FOREGROUND_COLOR);
        leaveGameButton.setForeground(FOREGROUND_COLOR);
        showEconomyChartButton.setForeground(FOREGROUND_COLOR);
        showPowerArmyChartButton.setForeground(FOREGROUND_COLOR);
        showTerritoryEvolutionChartButton.setForeground(FOREGROUND_COLOR);
        showTerritoryDistributionChartButton.setForeground(FOREGROUND_COLOR);

        turnCountLabel.setBackground(IHMConfig.BACKGROUND_GAME_DISPLAY_COLOR);
        saveGameButton.setBackground(IHMConfig.BACKGROUND_GAME_DISPLAY_COLOR);
        tutorialButton.setBackground(IHMConfig.BACKGROUND_GAME_DISPLAY_COLOR);
        leaveGameButton.setBackground(IHMConfig.BACKGROUND_GAME_DISPLAY_COLOR);
        showEconomyChartButton.setBackground(IHMConfig.BACKGROUND_GAME_DISPLAY_COLOR);
        showPowerArmyChartButton.setBackground(IHMConfig.BACKGROUND_GAME_DISPLAY_COLOR);
        showTerritoryEvolutionChartButton.setBackground(IHMConfig.BACKGROUND_GAME_DISPLAY_COLOR);
        showTerritoryDistributionChartButton.setBackground(IHMConfig.BACKGROUND_GAME_DISPLAY_COLOR);
    }

    public SettingsPanel(GameManager gameManager) {

        setLayout(new FlowLayout(FlowLayout.CENTER, IHMScaling.scale(50), IHMScaling.scale(50)));
        initActions(gameManager);
        initStyles();

        //Information directly displayed
        add(turnCountLabel);

        //Information actions
        add(showTerritoryEvolutionChartButton);
        add(showTerritoryDistributionChartButton);
        add(showPowerArmyChartButton);
        add(showEconomyChartButton);
        add(tutorialButton);

        //Game action
        add(saveGameButton);
        add(leaveGameButton);
    }

    public void setTurnCountLabel(int turn) {
        turnCountLabel.setText(" Turn " + (turn + 1)+" ");
    }

    public void enablePowerArmyBarChart() {
        if (showPowerArmyChartButton != null) {
            showPowerArmyChartButton.setEnabled(true);
        }
    }

    public void enableCurrentTerritoryPieChart() {
        if (showTerritoryDistributionChartButton != null) {
            showTerritoryDistributionChartButton.setEnabled(true);
        }
    }

    public void enableTerritoryEvolutionLineChart() {
        if (showTerritoryEvolutionChartButton != null) {
            showTerritoryEvolutionChartButton.setEnabled(true);
        }
    }

    public void enableEconomyBarChart() {
        if (showEconomyChartButton != null) {
            showEconomyChartButton.setEnabled(true);
        }
    }
}
