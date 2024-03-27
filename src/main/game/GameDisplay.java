package main.game;

import configuration.GameplayConfig;
import configuration.IHMConfig;
import data.board.Block;
import data.board.GameMap;
import data.element.*;
import data.player.Player;
import ihm.listener.*;
import ihm.listener.shop.BuyAttackTower;
import ihm.listener.shop.BuyDefenseTower;
import ihm.listener.shop.BuyHouse;
import ihm.listener.shop.BuySoldier;
import ihm.paint.PaintElement;
import ihm.paint.PaintIndicator;
import ihm.paint.PaintMap;
import ihm.sidepanel.PlayersInformationPanel;
import ihm.sidepanel.SettingsPanel;
import ihm.util.IHMScaling;
import ihm.util.ImageUtility;
import log.LoggerUtility;
import main.MainGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;

public class GameDisplay extends JPanel {

    private static final org.apache.log4j.Logger logger = LoggerUtility.getLogger(GameDisplay.class);

    private final int TURN_BEFORE_CHARTS_ENABLED = 2;
    private final Dimension QUICK_ACTION_BUTTON_DIMENSION = new Dimension(IHMScaling.scale(75), IHMScaling.scale(75));

    //Button images path
    private final String SOLDIER_SHOP_IMAGE_PATH = "/res/images/gameplay/soldierRight.png";
    private final String HOUSE_SHOP_IMAGE_PATH = "/res/images/gameplay/house.png";
    private final String ATTACK_TOWER_SHOP_IMAGE_PATH = "/res/images/gameplay/attackTower.png";
    private final String DEFENSE_TOWER_SHOP_IMAGE_PATH = "/res/images/gameplay/defenseTower.png";
    private final String AUTO_MOVE_SOLDIERS_IMAGE_PATH = "/res/images/hud/autoMoveSoldiers.png";
    private final String MOVE_SOLDIERS_IN_DIRECTION_IMAGE_PATH = "/res/images/hud/moveInDirection.png";
    private final String DISABLED_MOVE_SOLDIERS_IN_DIRECTION_IMAGE_PATH = "/res/images/hud/disabledMoveInDirection.png";
    private final String WINNER_PICTURE = "/res/images/gameplay/winner.png";

    public final String SKIP_TURN_ICON_PATH = "/res/images/hud/passTurn.png";
    public final String SETTINGS_ICON_PATH = "/res/images/hud/echap.png";

    //Painters
    private PaintMap paintMap;
    private PaintElement paintElement;
    private PaintIndicator paintIndicator;

    //To paint
    private final GameMap map;
    private ArrayList<Block> specialBlocks;
    private Element selectedElement = null;
    private Block selectedBlock = null;
    private ArrayList<Block> possibleActionBlocks = null;
    private ArrayList<ForestTree> forestTrees;

    //To paint with specific color
    private Player currentPlayer;
    private final ArrayList<Player> playerList;

    //Action listeners
    private final HashMap<GameListener, JButton> listenersMap = new HashMap<>();
    ;

    //Panel (settings) > Buttons
    private final JPanel topPanel = new JPanel();
    private final BotLoadingAnimation botLoading = new BotLoadingAnimation();
    private final JButton skipTurnButton = new JButton();
    private final JButton settingsButton = new JButton();

    //Settings (left panel)
    private final SettingsPanel settingsPanel;

    //Quick actions
    private final JPanel quickActionPanel = new JPanel();
    private final JButton moveAllSoldierInDirection = new JButton();
    private final JButton autoMoveAllSoldier = new JButton();

    //Shop
    private final JPanel shopPanel = new JPanel();
    private final JButton buySoldier = new JButton("" + GameplayConfig.SOLDIER_PRICE + "$");
    private final JButton buyHouse = new JButton("  " + GameplayConfig.HOUSE_PRICE + "$");
    private final JButton buyAttackTower = new JButton("" + GameplayConfig.ATTACK_TOWER_PRICE + "$");
    private final JButton buyDefenseTower = new JButton("" + GameplayConfig.DEFENSE_TOWER_PRICE + "$");

    //Players information (top panel)
    private final PlayersInformationPanel playersInformation;

    public void disableListeners() {
        for (GameListener gameListener : listenersMap.keySet()) {
            JButton button = listenersMap.get(gameListener);
            if (button != null) {
                if (gameListener instanceof ActionListener) {
                    button.removeActionListener((ActionListener) gameListener);
                }
            } else if (gameListener instanceof PlayerSelection) {
                removeMouseListener((MouseListener) gameListener);
            }
        }
    }

    public void enableListeners() {
        disableListeners();
        for (GameListener gameListener : listenersMap.keySet()) {
            JButton button = listenersMap.get(gameListener);
            if (button != null) {
                if (gameListener instanceof ActionListener) {
                    button.addActionListener((ActionListener) gameListener);
                }
            } else if (gameListener instanceof PlayerSelection) {
                addMouseListener((MouseListener) gameListener);
            }
        }
    }

    private void initListeners(GameManager gameManager) {
        listenersMap.put(new PlayerSelection(gameManager, this), null);
        listenersMap.put(new SkipTurn(gameManager, this), skipTurnButton);
        listenersMap.put(new OpenSettings(this), settingsButton);
        listenersMap.put(new BuySoldier(gameManager, this), buySoldier);
        listenersMap.put(new BuyHouse(gameManager, this), buyHouse);
        listenersMap.put(new BuyAttackTower(gameManager, this), buyAttackTower);
        listenersMap.put(new BuyDefenseTower(gameManager, this), buyDefenseTower);
        listenersMap.put(new AutoMoveSoldiers(gameManager, this), autoMoveAllSoldier);
        listenersMap.put(new MoveSoldiersInDirection(gameManager, this), moveAllSoldierInDirection);

        enableListeners();
    }

    public void initStyles() {
        settingsButton.setFocusPainted(false);
        skipTurnButton.setFocusPainted(false);

        buyHouse.setFocusPainted(false);
        buySoldier.setFocusPainted(false);
        buyAttackTower.setFocusPainted(false);
        buyDefenseTower.setFocusPainted(false);
        autoMoveAllSoldier.setFocusPainted(false);
        moveAllSoldierInDirection.setFocusPainted(false);

        buyHouse.setFont(IHMConfig.BUTTON_FONT);
        buySoldier.setFont(IHMConfig.BUTTON_FONT);
        buyAttackTower.setFont(IHMConfig.BUTTON_FONT);
        buyDefenseTower.setFont(IHMConfig.BUTTON_FONT);
        autoMoveAllSoldier.setFont(IHMConfig.BUTTON_FONT);
        moveAllSoldierInDirection.setFont(IHMConfig.BUTTON_FONT);

        buyHouse.setForeground(Color.BLACK);
        buySoldier.setForeground(Color.BLACK);
        buyAttackTower.setForeground(Color.BLACK);
        buyDefenseTower.setForeground(Color.BLACK);

        buyHouse.setVerticalTextPosition(SwingConstants.CENTER);
        buySoldier.setVerticalTextPosition(SwingConstants.CENTER);
        buyAttackTower.setVerticalTextPosition(SwingConstants.CENTER);
        buyDefenseTower.setVerticalTextPosition(SwingConstants.CENTER);
        autoMoveAllSoldier.setVerticalTextPosition(SwingConstants.CENTER);
        moveAllSoldierInDirection.setVerticalTextPosition(SwingConstants.CENTER);

        buyHouse.setPreferredSize(IHMConfig.GAME_BUTTON_DIMENSION);
        buySoldier.setPreferredSize(IHMConfig.GAME_BUTTON_DIMENSION);
        buyAttackTower.setPreferredSize(IHMConfig.GAME_BUTTON_DIMENSION);
        buyDefenseTower.setPreferredSize(IHMConfig.GAME_BUTTON_DIMENSION);
        autoMoveAllSoldier.setPreferredSize(QUICK_ACTION_BUTTON_DIMENSION);
        moveAllSoldierInDirection.setPreferredSize(QUICK_ACTION_BUTTON_DIMENSION);

        buyHouse.setIcon(ImageUtility.getScaledIcon(HOUSE_SHOP_IMAGE_PATH, 50, 50));
        buySoldier.setIcon(ImageUtility.getScaledIcon(SOLDIER_SHOP_IMAGE_PATH, 50, 50));
        buyAttackTower.setIcon(ImageUtility.getScaledIcon(ATTACK_TOWER_SHOP_IMAGE_PATH, 50, 75));
        buyDefenseTower.setIcon(ImageUtility.getScaledIcon(DEFENSE_TOWER_SHOP_IMAGE_PATH, 50, 75));
        autoMoveAllSoldier.setIcon(ImageUtility.getScaledIcon(AUTO_MOVE_SOLDIERS_IMAGE_PATH, 64, 64));
        moveAllSoldierInDirection.setIcon(ImageUtility.getScaledIcon(MOVE_SOLDIERS_IN_DIRECTION_IMAGE_PATH, 64, 64));
        moveAllSoldierInDirection.setDisabledIcon(ImageUtility.getScaledIcon(DISABLED_MOVE_SOLDIERS_IN_DIRECTION_IMAGE_PATH, 64, 64));

        settingsButton.setIcon(ImageUtility.getScaledIcon(SETTINGS_ICON_PATH, 70, 70));
        skipTurnButton.setIcon(ImageUtility.getScaledIcon(SKIP_TURN_ICON_PATH, 92, 70));

        quickActionPanel.setOpaque(false);
        settingsPanel.setOpaque(false);

        quickActionPanel.setVisible(true);
        shopPanel.setVisible(false);
        settingsPanel.setVisible(false);

        autoMoveAllSoldier.setBackground(IHMConfig.BACKGROUND_GAME_DISPLAY_COLOR);
        moveAllSoldierInDirection.setBackground(IHMConfig.BACKGROUND_GAME_DISPLAY_COLOR);
        skipTurnButton.setBackground(IHMConfig.BACKGROUND_GAME_DISPLAY_COLOR);
        settingsButton.setBackground(IHMConfig.BACKGROUND_GAME_DISPLAY_COLOR);
        settingsPanel.setBackground(IHMConfig.BACKGROUND_GAME_DISPLAY_COLOR);
        shopPanel.setBackground(IHMConfig.BACKGROUND_GAME_DISPLAY_COLOR);
        topPanel.setBackground(IHMConfig.BACKGROUND_GAME_DISPLAY_COLOR);
        setBackground(IHMConfig.BACKGROUND_GAME_DISPLAY_COLOR);
    }

    public void initLayouts() {
        settingsPanel.setPreferredSize(new Dimension(IHMScaling.scale(200), 0));
        setLayout(new BorderLayout());
    }

    public GameDisplay(GameManager gameManager) {
        botLoading.setVisible(false);
        moveAllSoldierInDirection.setEnabled(false);

        paintMap = new PaintMap();
        paintElement = new PaintElement();
        paintIndicator = new PaintIndicator();

        map = gameManager.getMap();
        specialBlocks = gameManager.getSpecialsBlocks();
        playerList = gameManager.getPlayersManager().getPlayerList();
        currentPlayer = gameManager.getPlayersManager().getCurrentPlayer();
        forestTrees = gameManager.getElementsFinder().elementMapToList(ForestTree.class);

        settingsPanel = new SettingsPanel(gameManager);
        playersInformation = new PlayersInformationPanel(playerList, map);

        initListeners(gameManager);
        initLayouts();
        initStyles();

        shopPanel.add(buySoldier);
        shopPanel.add(buyHouse);
        shopPanel.add(buyAttackTower);
        shopPanel.add(buyDefenseTower);

        quickActionPanel.add(moveAllSoldierInDirection);
        quickActionPanel.add(autoMoveAllSoldier);

        topPanel.add(settingsButton);
        topPanel.add(playersInformation);
        topPanel.add(skipTurnButton);
        topPanel.add(botLoading);

        add(quickActionPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        add(shopPanel, BorderLayout.SOUTH);
        add(settingsPanel, BorderLayout.WEST);

    }

    /**
     * Displays all game data according to theirs coordinate.
     *
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        ArrayList<Block> coloredBlocks = new ArrayList<Block>();

        //Draws background map image
        paintMap.paint(g, map);

        //Color all player's block
        for (Player player : playerList) {
            for (Block block : player.getOwnedBlocksList()) {
                paintMap.paint(g, block, player.getColor());
                coloredBlocks.add(block);
            }
        }

        //Color all neutral blocks (blocks that have not already been colored)
        for (int i = 0; i < map.getColumns(); i++) {
            for (int j = 0; j < map.getLines(); j++) {
                if (!coloredBlocks.contains(map.getBlock(i, j)) && !map.getBlock(i, j).isRemoved()) {
                    paintMap.paint(g, map.getBlock(i, j), IHMConfig.NEUTRAL_BLOCK_COLOR);
                }
            }
        }

        //Draws trees
        for (ForestTree tree : forestTrees) {
            paintElement.paint(g, tree);
        }

        //Draws all player's Element (from top to bottom)
        for (int line = 0; line < map.getLines(); line++) {
            for (Player player : playerList) {
                for (Element element : player.getOwnedElementsList()) {
                    if (element.getIndexY() == line) {
                        if (currentPlayer.isBot()) {
                            paintElement.paint(g, element, player.getColorName(), false);
                        } else {
                            paintElement.paint(g, element, player.getColorName());
                        }
                    } else if (element.getIndexY() > line) {
                        continue;
                    }
                }
            }
        }

        //Draws selected block
        if (selectedBlock != null) {
            if (selectedElement != null) {
                if (selectedElement instanceof AttackTower) {
                    paintIndicator.paintRange(g, selectedBlock, currentPlayer.getColor(),
                            GameplayConfig.ATTACK_TOWER_RAYON);
                } else if (selectedElement instanceof DefenseTower) {
                    paintIndicator.paintRange(g, selectedBlock, currentPlayer.getColor(),
                            GameplayConfig.DEFENSE_TOWER_RAYON);
                }
            } else {
                paintIndicator.paintSelectedBlock(g, selectedBlock);
            }
        }

        //Draws possible action blocks and indicators
        if (possibleActionBlocks != null) {
            if (!possibleActionBlocks.isEmpty()) {
                for (Block block : possibleActionBlocks) {
                    if (!block.isEmpty()) {

                        Block blockTree = null;

                        for (ForestTree trees : forestTrees) {
                            if (currentPlayer.ownsBlock(map.getBlock(trees.getIndexX(), trees.getIndexY()))) {
                                if (block.equals(map.getBlock(trees.getIndexX(), trees.getIndexY()))) {
                                    blockTree = block;
                                    break;
                                }
                            }
                        }
                        Block soldierBlock = null;

                        for (Element element : currentPlayer.getOwnedElementsList()) {
                            if (element instanceof Soldier) {
                                if (currentPlayer.ownsBlock(map.getBlock(element.getIndexX(), element.getIndexY()))) {
                                    if (block.equals(map.getBlock(element.getIndexX(), element.getIndexY()))) {
                                        soldierBlock = block;
                                        break;
                                    }
                                }
                            }
                        }

                        if (!block.equals(selectedBlock)) {
                            if (blockTree != null) {
                                paintIndicator.paintAttackableIndicator(g, blockTree);

                            } else if (soldierBlock != null) {
                                paintIndicator.paintMergeIndicator(g, soldierBlock);

                            } else if (currentPlayer.ownsBlock(block)) {
                                paintIndicator.paintAlliesIndicator(g, block);

                            } else if (!currentPlayer.ownsBlock(block)) {
                                paintIndicator.paintAttackableIndicator(g, block);
                            }
                        }
                    }
                    paintIndicator.paintSelectedBlock(g, block);
                }
            }
        }

        //Draws all special blocks
        if (specialBlocks != null) {
            for (Block block : specialBlocks) {
                paintIndicator.paintSpecialBlockIndicator(g, block);
            }
        }
    }

    /**
     * @return the listener {@link SkipTurn } in this game.
     */
    public SkipTurn getSkipTurnAction() {
        for (GameListener gameListener : listenersMap.keySet()) {
            if (gameListener instanceof SkipTurn) {
                return (SkipTurn) gameListener;
            }
        }
        return null;
    }

    /**
     * An animated textual JLabel "Loading..." to show the bot playing turn time.
     */
    public class BotLoadingAnimation extends JLabel implements Runnable {

        public BotLoadingAnimation() {
            super();
            setForeground(Color.WHITE);
            setFont(new Font(Font.DIALOG, Font.BOLD, IHMConfig.PLAYER_INFORMATION_SIZE
                    + IHMConfig.PLAYER_INFORMATION_SIZE / 2 + IHMConfig.PLAYER_INFORMATION_SIZE / 8));
        }

        public void stopAnimation() {
            botLoading.setVisible(false);
            skipTurnButton.setVisible(true);
            getSkipTurnAction().skipTurn();
        }

        @Override
        public void run() {
            int i = 0;
            while (i <= 12) {

                if (i % 3 == 0) {
                    setText("   Bot playing.  ");
                } else if (i % 3 == 1) {
                    setText("   Bot playing.. ");
                } else {
                    setText("   Bot playing...");
                }

                try {
                    Thread.sleep(GameplayConfig.BOT_PLAYING_ANIMATION_TIME);
                } catch (InterruptedException e) {
                    logger.error("An error occurred during bot loading animation.");
                    e.printStackTrace();
                }
                i++;
            }
            stopAnimation();
        }
    }

    public Boolean settingsPanelIsVisible() {
        return settingsPanel.isVisible();
    }

    /**
     * Sets visible the bot loader animation and starting a new Thread to run animation.
     */
    public void putBotAnimation() {
        botLoading.setVisible(true);
        skipTurnButton.setVisible(false);

        Thread t = new Thread(botLoading);
        t.start();
    }

    /**
     * Sets invisible the settings panel.
     */
    public void hideSettingsPanel() {
        settingsPanel.setVisible(false);
    }

    /**
     * Sets visible the settings panel.
     */
    public void showSettingsPanel() {
        settingsPanel.setVisible(true);
    }

    /**
     * Sets invisible the shop panel.
     */
    public void hideShopPanel() {
        shopPanel.setVisible(false);
    }

    /**
     * Sets visible the shop panel with player's color.
     */
    public void showShopPanel() {
        buyAttackTower.setBackground(currentPlayer.getColor());
        buyDefenseTower.setBackground(currentPlayer.getColor());
        buyHouse.setBackground(currentPlayer.getColor());
        buySoldier.setBackground(currentPlayer.getColor());
        shopPanel.setVisible(true);
    }

    /**
     * Sets enable the move automatically all soldiers JButton.
     */
    public void enableMoveAllSoldiersInDirectionButton() {
        moveAllSoldierInDirection.setEnabled(true);
    }

    /**
     * Sets enable the move all soldiers in direction JButton.
     */
    public void disableMoveAllSoldiersInDirectionButton() {
        moveAllSoldierInDirection.setEnabled(false);
    }

    /**
     * Show an option dialog to leave the game when a player has won.
     */
    public void showPlayerHasWinOptions() {
        disableListeners();

        String[] options = {"Back to main menu", "Quit"};
        int choice = JOptionPane.showOptionDialog(null, "Congratulations, " + currentPlayer.getColorName() + " won the game !",
                "Victory", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                ImageUtility.getScaledIcon(WINNER_PICTURE, 64, 64), options, options[0]);

        if (choice == 0) {
            MainGUI.getInstance().switchToMainMenu();

        } else {
            System.exit(0);
        }
    }

    /**
     * Update data to display the game evolution, and show player options when a player has won.
     *
     * @param gameManager the containing data of game.
     */
    public void refreshDisplay(GameManager gameManager) {
        if (gameManager != null) {
            int turn = gameManager.getGameStatistic().getSkipTurnCount(currentPlayer);

            currentPlayer = gameManager.getPlayersManager().getCurrentPlayer();

            forestTrees = gameManager.getElementsFinder().elementMapToList(ForestTree.class);

            selectedBlock = gameManager.getSelectedBlock();
            selectedElement = gameManager.getElementsFinder().findElementOnBlock(selectedBlock);
            possibleActionBlocks = gameManager.getPossibleActionBlocks();

            settingsPanel.setTurnCountLabel(gameManager.getGameStatistic().getSkipTurnCount(currentPlayer));

            //Enable charts
            if (turn >= TURN_BEFORE_CHARTS_ENABLED) {
                settingsPanel.enablePowerArmyBarChart();
                settingsPanel.enableTerritoryEvolutionLineChart();
                settingsPanel.enableCurrentTerritoryPieChart();
                settingsPanel.enableEconomyBarChart();
            }

            //Verifying when a player has win
            if (gameManager.gameIsEnded()) {
                showPlayerHasWinOptions();
            }
            playersInformation.updateDisplay(playerList);
            repaint();
        }
    }


}
