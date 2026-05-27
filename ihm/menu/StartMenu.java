package ihm.menu;

import configuration.MapsConfig;
import data.board.GameMap;
import ihm.sidepanel.GameMapSelector;
import ihm.util.IHMScaling;
import ihm.util.ImageUtility;
import main.MainGUI;
import main.game.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * This class is the entry point to a new {@link Game}, it allows to choice some parameter before
 * initializing the game by interact with {@link MainGUI}.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class StartMenu extends JPanel {

    private final Font LABEL_FONT = new Font(Font.DIALOG, Font.BOLD, IHMScaling.scale(35));
    private final Font BUTTON_FONT = new Font(Font.DIALOG, Font.BOLD, IHMScaling.scale(30));
    private final Color BACKGROUND_COLOR = new Color(75, 75, 75);
    private final Color FOREGROUND_COLOR = Color.WHITE;


    private final Dimension BUTTON_DIMENSION_120x120 = new Dimension(120, 120);
    private final Dimension BUTTON_DIMENSION_32x32 = new Dimension(32, 32);

    private final JPanel topPanel = new JPanel();
    private final JPanel playersChoicePanel = new JPanel();
    private final JPanel runGamePanel = new JPanel();
    private final JButton cancelButton = new JButton();
    private final JButton startButton = new JButton();

    private final JLabel playersChoiceLabel = new JLabel("Amount of players : ");
    private final ButtonGroup playerSelectionGroup = new ButtonGroup();
    private final JRadioButton twoPlayersSelect = new JRadioButton("2");
    private final JRadioButton threePlayersSelect = new JRadioButton("3");
    private final JRadioButton fourPlayersSelect = new JRadioButton("4");

    private final JPanel botChoicePanel = new JPanel();
    private final JLabel botChoiceLabel = new JLabel("Amount of bot among them : ");
    private final ButtonGroup botSelectGroup = new ButtonGroup();
    private final JRadioButton zeroBotSelect = new JRadioButton("0");
    private final JRadioButton oneBotSelect = new JRadioButton("1");
    private final JRadioButton twoBotSelect = new JRadioButton("2");
    private final JRadioButton threeBotSelect = new JRadioButton("3");

    private final JPanel modeChoicePanel = new JPanel();
    private final JLabel botTypeChoiceLabel = new JLabel("Bot(s) type  : ");
    private final ButtonGroup botTypeSelection = new ButtonGroup();
    private final JRadioButton normalButton = new JRadioButton("Normal");
    private final JRadioButton unfairButton = new JRadioButton("Unfair");

    private final JPanel globalPanel = new JPanel();

    private final GameMapSelector mapSelector = new GameMapSelector(MapsConfig.AVAILABLE_MAPS_LIST);
    private int mapIndex = 1;

    private final JPanel mapSelectionPanel = new JPanel();
    private final JLabel mapImage = new JLabel(mapSelector.getCurrentMapImageIcon());
    private final JLabel mapTitleLabel = new JLabel("Map : " + mapSelector.getCurrentMap().getTitle() + " (" + mapIndex + "/"
            + MapsConfig.AVAILABLE_MAPS_LIST.length + ")" + " ", SwingConstants.CENTER);
    private final JButton previousMapButton = new JButton("<");
    private final JButton nextMapButton = new JButton(">");

    private final JPanel bottomPanel = new JPanel();

    private final ImageIcon startIcon = ImageUtility.getScaledIcon("/res/images/menu/accept.png", BUTTON_DIMENSION_120x120);
    private final ImageIcon cancelIcon = ImageUtility.getScaledIcon("/res/images/menu/cancel.png", BUTTON_DIMENSION_120x120);
    private final ImageIcon enlargedStartIcon = ImageUtility.getScaledIcon("/res/images/menu/enlargedAccept.png", BUTTON_DIMENSION_120x120);
    private final ImageIcon enlargedCancelIcon = ImageUtility.getScaledIcon("/res/images/menu/enlargedCancel.png", BUTTON_DIMENSION_120x120);
    private final ImageIcon RADIO_ICON = ImageUtility.getScaledIcon("/res/images/menu/radio.png", BUTTON_DIMENSION_32x32);
    private final ImageIcon RADIO_SELECTED_ICON = ImageUtility.getScaledIcon("/res/images/menu/radioSelect.png", BUTTON_DIMENSION_32x32);

    public StartMenu() {

        initLayouts();

        initStyles();

        initActions();

        botTypeSelection.add(unfairButton);
        botTypeSelection.add(normalButton);

        playersChoicePanel.add(playersChoiceLabel);
        playerSelectionGroup.add(twoPlayersSelect);
        playerSelectionGroup.add(threePlayersSelect);
        playerSelectionGroup.add(fourPlayersSelect);

        botSelectGroup.add(zeroBotSelect);
        botSelectGroup.add(oneBotSelect);
        botSelectGroup.add(twoBotSelect);
        botSelectGroup.add(threeBotSelect);

        playersChoicePanel.add(twoPlayersSelect);
        playersChoicePanel.add(threePlayersSelect);
        playersChoicePanel.add(fourPlayersSelect);

        botChoicePanel.add(botChoiceLabel);
        botChoicePanel.add(zeroBotSelect);
        botChoicePanel.add(oneBotSelect);
        botChoicePanel.add(twoBotSelect);
        botChoicePanel.add(threeBotSelect);

        modeChoicePanel.add(botTypeChoiceLabel);
        modeChoicePanel.add(normalButton);
        modeChoicePanel.add(unfairButton);

        mapSelectionPanel.add(mapTitleLabel, BorderLayout.NORTH);
        mapSelectionPanel.add(mapImage, BorderLayout.CENTER);
        mapSelectionPanel.add(previousMapButton, BorderLayout.WEST);
        mapSelectionPanel.add(nextMapButton, BorderLayout.EAST);

        runGamePanel.add(cancelButton);
        runGamePanel.add(startButton);

        normalButton.setEnabled(true);
        fourPlayersSelect.setSelected(true);
        zeroBotSelect.setSelected(true);

        topPanel.add(playersChoicePanel);
        topPanel.add(botChoicePanel);
        topPanel.add(modeChoicePanel);

        bottomPanel.add(mapSelectionPanel);
        globalPanel.add(topPanel);
        globalPanel.add(bottomPanel);

        add(globalPanel, BorderLayout.CENTER);
        add(runGamePanel, BorderLayout.SOUTH);
    }

    public void initLayouts() {
        playersChoicePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        mapSelectionPanel.setLayout(new BorderLayout());
        runGamePanel.setLayout(new FlowLayout(FlowLayout.CENTER, IHMScaling.scale(550), 0));
        topPanel.setLayout(new GridLayout(3, 1));
        globalPanel.setLayout(new GridLayout(2, 1));
        setLayout(new BorderLayout());
    }

    public void initStyles() {
        runGamePanel.setPreferredSize(new Dimension(0, IHMScaling.scale(128)));
        mapSelectionPanel.setPreferredSize(new Dimension(IHMScaling.scale(762), IHMScaling.scale(438)));
        nextMapButton.setPreferredSize(new Dimension(IHMScaling.scale(120), 0));
        previousMapButton.setPreferredSize(new Dimension(IHMScaling.scale(120), 0));

        cancelButton.setIcon(cancelIcon);
        startButton.setIcon(startIcon);

        twoPlayersSelect.setIcon(RADIO_ICON);
        threePlayersSelect.setIcon(RADIO_ICON);
        fourPlayersSelect.setIcon(RADIO_ICON);

        twoPlayersSelect.setSelectedIcon(RADIO_SELECTED_ICON);
        threePlayersSelect.setSelectedIcon(RADIO_SELECTED_ICON);
        fourPlayersSelect.setSelectedIcon(RADIO_SELECTED_ICON);

        zeroBotSelect.setIcon(RADIO_ICON);
        oneBotSelect.setIcon(RADIO_ICON);
        twoBotSelect.setIcon(RADIO_ICON);
        threeBotSelect.setIcon(RADIO_ICON);

        zeroBotSelect.setSelectedIcon(RADIO_SELECTED_ICON);
        oneBotSelect.setSelectedIcon(RADIO_SELECTED_ICON);
        twoBotSelect.setSelectedIcon(RADIO_SELECTED_ICON);
        threeBotSelect.setSelectedIcon(RADIO_SELECTED_ICON);

        normalButton.setSelected(true);
        normalButton.setSelectedIcon(RADIO_SELECTED_ICON);
        normalButton.setIcon(RADIO_ICON);

        unfairButton.setSelectedIcon(RADIO_SELECTED_ICON);
        unfairButton.setIcon(RADIO_ICON);

        unfairButton.setFocusPainted(false);
        normalButton.setFocusPainted(false);

        twoPlayersSelect.setFocusPainted(false);
        threePlayersSelect.setFocusPainted(false);
        fourPlayersSelect.setFocusPainted(false);

        zeroBotSelect.setFocusPainted(false);
        oneBotSelect.setFocusPainted(false);
        twoBotSelect.setFocusPainted(false);
        threeBotSelect.setFocusPainted(false);

        nextMapButton.setFont(BUTTON_FONT);
        previousMapButton.setFont(BUTTON_FONT);
        mapTitleLabel.setFont(BUTTON_FONT);

        cancelButton.setFocusPainted(false);
        startButton.setFocusPainted(false);

        nextMapButton.setFocusPainted(false);
        previousMapButton.setFocusPainted(false);

        previousMapButton.setForeground(FOREGROUND_COLOR);
        nextMapButton.setForeground(FOREGROUND_COLOR);
        mapTitleLabel.setForeground(FOREGROUND_COLOR);

        playersChoiceLabel.setForeground(FOREGROUND_COLOR);
        botChoiceLabel.setForeground(FOREGROUND_COLOR);
        botTypeChoiceLabel.setForeground(FOREGROUND_COLOR);

        unfairButton.setForeground(FOREGROUND_COLOR);
        normalButton.setForeground(FOREGROUND_COLOR);

        zeroBotSelect.setBackground(BACKGROUND_COLOR);
        oneBotSelect.setBackground(BACKGROUND_COLOR);
        twoBotSelect.setBackground(BACKGROUND_COLOR);
        threeBotSelect.setBackground(BACKGROUND_COLOR);

        twoPlayersSelect.setBackground(BACKGROUND_COLOR);
        threePlayersSelect.setBackground(BACKGROUND_COLOR);
        fourPlayersSelect.setBackground(BACKGROUND_COLOR);

        zeroBotSelect.setForeground(FOREGROUND_COLOR);
        oneBotSelect.setForeground(FOREGROUND_COLOR);
        twoBotSelect.setForeground(FOREGROUND_COLOR);
        threeBotSelect.setForeground(FOREGROUND_COLOR);

        twoPlayersSelect.setForeground(FOREGROUND_COLOR);
        threePlayersSelect.setForeground(FOREGROUND_COLOR);
        fourPlayersSelect.setForeground(FOREGROUND_COLOR);

        zeroBotSelect.setBorderPainted(false);
        oneBotSelect.setBorderPainted(false);
        twoBotSelect.setBorderPainted(false);
        threeBotSelect.setBorderPainted(false);
        twoPlayersSelect.setBorderPainted(false);
        threePlayersSelect.setBorderPainted(false);
        fourPlayersSelect.setBorderPainted(false);

        zeroBotSelect.setContentAreaFilled(false);
        oneBotSelect.setContentAreaFilled(false);
        twoBotSelect.setContentAreaFilled(false);
        threeBotSelect.setContentAreaFilled(false);
        twoPlayersSelect.setContentAreaFilled(false);
        threePlayersSelect.setContentAreaFilled(false);
        fourPlayersSelect.setContentAreaFilled(false);

        cancelButton.setFont(BUTTON_FONT);
        startButton.setFont(BUTTON_FONT);

        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setContentAreaFilled(false);

        botChoiceLabel.setFont(LABEL_FONT);
        playersChoiceLabel.setFont(LABEL_FONT);
        botTypeChoiceLabel.setFont(LABEL_FONT);

        twoPlayersSelect.setFont(BUTTON_FONT);
        threePlayersSelect.setFont(BUTTON_FONT);
        fourPlayersSelect.setFont(BUTTON_FONT);

        zeroBotSelect.setFont(BUTTON_FONT);
        oneBotSelect.setFont(BUTTON_FONT);
        twoBotSelect.setFont(BUTTON_FONT);
        threeBotSelect.setFont(BUTTON_FONT);

        normalButton.setFont(BUTTON_FONT);
        normalButton.setBackground(BACKGROUND_COLOR);
        unfairButton.setFont(BUTTON_FONT);
        unfairButton.setBackground(BACKGROUND_COLOR);

        mapSelectionPanel.setBackground(BACKGROUND_COLOR);
        playersChoicePanel.setBackground(BACKGROUND_COLOR);
        botChoicePanel.setBackground(BACKGROUND_COLOR);
        modeChoicePanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setBackground(BACKGROUND_COLOR);

        previousMapButton.setBackground(Color.BLACK);
        nextMapButton.setBackground(Color.BLACK);
        runGamePanel.setBackground(Color.BLACK);
        setBackground(Color.BLACK);

    }

    public void initActions() {
        GoBackAction goBackAction = new GoBackAction();
        cancelButton.addMouseListener(goBackAction);
        cancelButton.addActionListener(goBackAction);

        StartAction startAction = new StartAction();
        startButton.addMouseListener(startAction);
        startButton.addActionListener(startAction);

        previousMapButton.addActionListener(new PreviousMapAction());
        nextMapButton.addActionListener(new NextMapAction());

        twoPlayersSelect.addActionListener(new TwoPlayersSelectAction());
        threePlayersSelect.addActionListener(new ThreePlayersSelectAction());
        fourPlayersSelect.addActionListener(new FourPlayersSelectAction());

    }

    /**
     * This method sets the visibility the amount of bot possible to choice when two player is selected.
     */
    class TwoPlayersSelectAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            zeroBotSelect.setVisible(true);

            oneBotSelect.setVisible(true);
            twoBotSelect.setVisible(false);
            threeBotSelect.setVisible(false);

            zeroBotSelect.setSelected(true);

            repaint();
        }
    }

    /**
     * This method sets the visibility the amount of bot possible to choice when three player is selected.
     */
    class ThreePlayersSelectAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            zeroBotSelect.setVisible(true);

            oneBotSelect.setVisible(true);
            twoBotSelect.setVisible(true);
            threeBotSelect.setVisible(false);

            zeroBotSelect.setSelected(true);

            repaint();
        }
    }

    /**
     * This method sets the visibility the amount of bot possible to choice when four player is selected.
     */
    class FourPlayersSelectAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            zeroBotSelect.setVisible(true);
            oneBotSelect.setVisible(true);
            twoBotSelect.setVisible(true);
            threeBotSelect.setVisible(true);

            zeroBotSelect.setSelected(true);

            repaint();
            setVisible(true);
        }
    }

    /**
     * Interact with {@link MainGUI} to initialize a new game with by collecting the current selection.
     * If the game initialized successfully it switches to the game display.
     */
    class StartAction implements ActionListener, MouseListener {

        public void actionPerformed(ActionEvent e) {

            int amountPlayers, amountHumanPlayers, amountBots, difficultLevel;

            //This is the total amount of player in the game
            if (twoPlayersSelect.isSelected()) {
                amountPlayers = 2;
            } else if (threePlayersSelect.isSelected()) {
                amountPlayers = 3;
            } else if (fourPlayersSelect.isSelected()) {
                amountPlayers = 4;
            } else {
                // Impossible case, by default the maximum of player
                amountPlayers = 4;
            }

            //This is only the amount of bot between the total amount of player
            if (zeroBotSelect.isSelected()) {
                amountBots = 0;
            } else if (oneBotSelect.isSelected()) {
                amountBots = 1;
            } else if (twoBotSelect.isSelected()) {
                amountBots = 2;
            } else if (threeBotSelect.isSelected()) {
                amountBots = 3;
            } else {
                // Impossible case, by default the minimum of bot
                amountBots = 0;
            }
            if (unfairButton.isSelected()) {
                 difficultLevel = 1;
            } else {
                difficultLevel = 0;
            }

            amountHumanPlayers = amountPlayers - amountBots;


            MainGUI.getInstance().initNewGame(amountHumanPlayers, amountBots, difficultLevel, mapSelector.getCurrentMap());
            MainGUI.getInstance().switchToGameDisplay();
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
            startButton.setIcon(enlargedStartIcon);
        }

        public void mouseExited(MouseEvent e) {
            startButton.setIcon(startIcon);
        }

        public void mousePressed(MouseEvent e) {
            startButton.setIcon(startIcon);
        }

        public void mouseReleased(MouseEvent e) {
        }
    }

    /**
     * Interact with {@link MainGUI} to switch to the {@link MainMenu} panel.
     */
    class GoBackAction implements ActionListener, MouseListener {

        public void actionPerformed(ActionEvent e) {
            MainGUI.getInstance().switchToMainMenu();
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
            cancelButton.setIcon(enlargedCancelIcon);
        }

        public void mouseExited(MouseEvent e) {
            cancelButton.setIcon(cancelIcon);
        }

        public void mousePressed(MouseEvent e) {
            cancelButton.setIcon(cancelIcon);
        }

        public void mouseReleased(MouseEvent e) {
        }
    }

    /**
     * Read {@link ImageIcon} of the previous {@link GameMap} chosen hold
     * in linked list with {@link GameMapSelector}.
     * Can be the cause of performance losses because map image are re-read each times.
     */
    class PreviousMapAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mapSelector.previousMap();
            mapImage.setIcon(mapSelector.getCurrentMapImageIcon());
            if (mapIndex <= 1) {
                mapIndex = MapsConfig.AVAILABLE_MAPS_LIST.length;
            } else {
                mapIndex--;
            }
            mapTitleLabel.setText("Map : " + mapSelector.getCurrentMap().getTitle() + " (" + mapIndex + "/"
                    + MapsConfig.AVAILABLE_MAPS_LIST.length + ")" + " ");
        }
    }

    /**
     * Read {@link ImageIcon} of the next {@link GameMap} chosen hold
     * in linked list with {@link GameMapSelector}.
     * Can be the cause of performance losses because map image are re-read each times.
     */
    class NextMapAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            mapSelector.nextMap();
            mapImage.setIcon(mapSelector.getCurrentMapImageIcon());

            if (mapIndex >= MapsConfig.AVAILABLE_MAPS_LIST.length) {
                mapIndex = 1;
            } else {
                mapIndex++;
            }
            mapTitleLabel.setText("Map : " + mapSelector.getCurrentMap().getTitle() + " (" + mapIndex + "/"
                    + MapsConfig.AVAILABLE_MAPS_LIST.length + ")" + " ");
        }
    }
}
