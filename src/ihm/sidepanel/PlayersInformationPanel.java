package ihm.sidepanel;

import configuration.GameplayConfig;
import data.board.GameMap;
import data.player.Player;
import ihm.util.IHMScaling;
import ihm.util.ImageUtility;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is the display of some information about {@link Player} in the game.
 * It shows gold information and territory information with a player's colored background.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class PlayersInformationPanel extends JPanel {

    private final Dimension PLAYER_ICON_DIMENSION = new Dimension(70, 70);
    private final Dimension ICONS_DIMENSION = new Dimension(37, 37);

    private final Font LABEL_FONT = new Font(Font.DIALOG, Font.BOLD, IHMScaling.scale(18));
    private final Font ENLARGE_LABEL_FONT = new Font(Font.DIALOG, Font.BOLD, IHMScaling.scale(22));
    private final Color LABEL_FOREGROUND = Color.BLACK;

    public final String CURRENT_BOT_IMAGE_FILE_PATH = "/res/images/hud/currentBot.png";
    public final String BOT_IMAGE_FILE_PATH = "/res/images/hud/bot.png";
    public final String CURRENT_PLAYER_IMAGE_FILE_PATH = "/res/images/hud/currentPlayer.png";
    public final String PLAYER_IMAGE_FILE_PATH = "/res/images/hud/player.png";
    public final String BOT_LOST_IMAGE_FILE_PATH = "/res/images/hud/botLost.png";
    public final String PLAYER_LOST_IMAGE_FILE_PATH = "/res/images/hud/playerLost.png";

    public final String TOTAL_GOLD_ICON_PATH = "/res/images/hud/totalGold.png";
    public final String GOLD_PER_TURN_ICON_PATH = "/res/images/hud/goldPerTurn.png";
    public final String TERRITORY_SIZE_ICON_PATH = "/res/images/hud/territory.png";

    public final ImageIcon CURRENT_BOT_IMAGE_ICON = ImageUtility.getScaledIcon(CURRENT_BOT_IMAGE_FILE_PATH, PLAYER_ICON_DIMENSION);
    public final ImageIcon BOT_IMAGE_ICON = ImageUtility.getScaledIcon(BOT_IMAGE_FILE_PATH, PLAYER_ICON_DIMENSION);
    public final ImageIcon CURRENT_PLAYER_IMAGE_ICON = ImageUtility.getScaledIcon(CURRENT_PLAYER_IMAGE_FILE_PATH, PLAYER_ICON_DIMENSION);
    public final ImageIcon PLAYER_IMAGE_ICON = ImageUtility.getScaledIcon(PLAYER_IMAGE_FILE_PATH, PLAYER_ICON_DIMENSION);
    public final ImageIcon BOT_LOST_IMAGE_ICON = ImageUtility.getScaledIcon(BOT_LOST_IMAGE_FILE_PATH, PLAYER_ICON_DIMENSION);
    public final ImageIcon PLAYER_LOST_IMAGE_ICON = ImageUtility.getScaledIcon(PLAYER_LOST_IMAGE_FILE_PATH, PLAYER_ICON_DIMENSION);

    /**
     * This HashMap allows to hold all of labels information for each player in the game.
     */
    private final HashMap<Color, ArrayList<JLabel>> playerLabelsMap = new HashMap<>();

    /**
     * The total amount of {@link data.board.Block} presents in the map (without consider removed block).
     */
    private final int totalBlocks;

    /**
     * This method update updates all the player's icon in the game.
     *
     * @param player the label that displays the human player's icon
     */
    public void updatePlayerIcon(Player player) {
        if (player != null) {
            ArrayList<JLabel> playerLabelsList = playerLabelsMap.get(player.getColor());
            JLabel playerLabel = playerLabelsList.get(0);

            if (player.isBot()) {
                updateBotIcon(player, playerLabel);
            } else {
                updateHumanPlayerIcon(player, playerLabel);
            }
        }
    }

    /**
     * @param player      the bot whose icon is being updated
     * @param playerLabel the label that displays the human player's icon
     */
    private void updateBotIcon(Player player, JLabel playerLabel) {
        if (player != null && playerLabel != null) {
            if (player.hasLost()) {
                playerLabel.setIcon(BOT_LOST_IMAGE_ICON);
            } else {
                playerLabel.setIcon(player.canPlay() ? CURRENT_BOT_IMAGE_ICON : BOT_IMAGE_ICON);
            }
        }
    }

    /**
     * @param player      the human player whose icon is being updated
     * @param playerLabel the label that displays the human player's icon
     */
    private void updateHumanPlayerIcon(Player player, JLabel playerLabel) {
        if (player != null && playerLabel != null) {
            if (player.hasLost()) {
                playerLabel.setIcon(PLAYER_LOST_IMAGE_ICON);
            } else {
                playerLabel.setIcon(player.canPlay() ? CURRENT_PLAYER_IMAGE_ICON : PLAYER_IMAGE_ICON);
            }
        }
    }

    /**
     * This update all information about player in the game, including gold, gold per turn and territory percentage.
     *
     * @param players all players in the game.
     */
    public void updateDisplay(ArrayList<Player> players) {
        if (players != null) {
            for (Player player : players) {
                ArrayList<JLabel> playerLabelsList = playerLabelsMap.get(player.getColor());

                updatePlayerIcon(player);

                //Index 1 in playerLabelList is the total gold label
                String totalGoldLabelText = "    ";
                int totalGold = player.getTotalGold();
                totalGoldLabelText += totalGold >= GameplayConfig.TOTAL_GOLD_MAX ? "MAX" : totalGold;
                playerLabelsList.get(1).setText(totalGoldLabelText);

                //Index 2 in playerLabelList is the gold per turn label
                String goldPerTurnLabelText = "    ";
                int goldPerTurn = player.getGoldPerTurn();
                goldPerTurnLabelText += goldPerTurn < 0 ? " " : "+";
                goldPerTurnLabelText += goldPerTurn >= GameplayConfig.GOLD_PER_TURN_MAX ? "MAX" : goldPerTurn;
                playerLabelsList.get(2).setText(goldPerTurnLabelText);

                //Index 2 in playerLabelList is the  territory percentage label
                playerLabelsList.get(3).setText(totalBlocks > 1 ?
                        "    " + calculateTerritoryPercentage(player) + "%" : "?");
            }
        }
    }

    /**
     * @param players all players in the game.
     * @param map     the current map where players spawns.
     */
    public PlayersInformationPanel(ArrayList<Player> players, GameMap map) {

        //Retrieve total blocks to display percentage of territory conquered
        totalBlocks = map.getTotalBlocks();

        setLayout(new GridLayout(1, players.size()));

        for (Player player : players) {
            ArrayList<JLabel> playerLabelsList = new ArrayList<>();

            JPanel playerPanel = new JPanel();
            JLabel playerIcon = new JLabel();
            JLabel totalGoldLabel = new JLabel();
            JLabel territoryLabel = new JLabel();
            JLabel goldPerTurnLabel = new JLabel();

            totalGoldLabel.setIcon(ImageUtility.getScaledIcon(TOTAL_GOLD_ICON_PATH, ICONS_DIMENSION));
            goldPerTurnLabel.setIcon(ImageUtility.getScaledIcon(GOLD_PER_TURN_ICON_PATH, ICONS_DIMENSION));
            territoryLabel.setIcon(ImageUtility.getScaledIcon(TERRITORY_SIZE_ICON_PATH, ICONS_DIMENSION));

            totalGoldLabel.setFont(ENLARGE_LABEL_FONT);
            goldPerTurnLabel.setFont(LABEL_FONT);
            territoryLabel.setFont(LABEL_FONT);

            totalGoldLabel.setForeground(LABEL_FOREGROUND);
            goldPerTurnLabel.setForeground(LABEL_FOREGROUND);
            territoryLabel.setForeground(LABEL_FOREGROUND);

            totalGoldLabel.setHorizontalTextPosition(SwingConstants.LEFT);
            goldPerTurnLabel.setHorizontalTextPosition(SwingConstants.LEFT);
            territoryLabel.setHorizontalTextPosition(SwingConstants.LEFT);

            playerPanel.setBackground(player.getColor());

            //The order of adding is important to keep the good associated index in the list
            playerLabelsList.add(playerIcon);
            playerLabelsList.add(totalGoldLabel);
            playerLabelsList.add(goldPerTurnLabel);
            playerLabelsList.add(territoryLabel);


            playerPanel.add(playerIcon);
            playerPanel.add(totalGoldLabel);
            playerPanel.add(goldPerTurnLabel);
            playerPanel.add(territoryLabel);

            playerLabelsMap.put(player.getColor(), playerLabelsList);

            add(playerPanel);
        }
        updateDisplay(players);
    }

    /**
     * This is utility method to calculate the percentage of territory of a given player
     * based on the given {@link  GameMap} block information.
     *
     * @param player the player to calculate territory percentage
     * @return 0 if the given player is null or the total blocks is invalid.
     */
    public int calculateTerritoryPercentage(Player player) {
        return player != null && totalBlocks > 0 ? (player.getTotalOwnedBlocks() * 100) / totalBlocks : 0;
    }
}
