package ihm.menu;

import configuration.IHMConfig;
import ihm.util.IHMScaling;
import ihm.util.ImageUtility;
import main.game.Game;
import main.MainGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is the entry point to the game, the first panel to display before starting a game.
 * It contains {@link JButton} that allow to interact with {@link MainGUI} methods to load or start a new game.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class MainMenu extends JPanel {

    public Box centerBox = Box.createVerticalBox();

    private final JLayeredPane layeredPane = new JLayeredPane();

    private final int BUTTONS_WIDTH = IHMScaling.scale(800);
    private final int BUTTONS_HEIGHT = IHMScaling.scale(110);
    private final Font BUTTONS_FONT = new Font(Font.DIALOG, Font.BOLD, IHMScaling.scale(40));

    private final ImageIcon backgroundImage = ImageUtility.getIcon("/res/images/menu/background.png",
            IHMScaling.SCREEN_SIZE.width, IHMScaling.SCREEN_SIZE.height);
    private final JLabel conquestLogoLabel = new JLabel(ImageUtility.getIcon("/res/images/menu/logo.png",
            IHMScaling.scale(765), IHMScaling.scale(159)));

    private final JLabel backgroundLabel = new JLabel(backgroundImage);

    private final JPanel centerPanel = new JPanel();
    private final JButton startButton = new JButton("Play");
    private final JButton loadSavedGameButton = new JButton("Load save");
    private final JButton quitButton = new JButton("Quit");


    public MainMenu() {
        initStyles();
        initActions();

        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(conquestLogoLabel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(startButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(loadSavedGameButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(quitButton, JLayeredPane.PALETTE_LAYER);

        centerPanel.add(layeredPane);

        centerBox.add(centerPanel);

        add(centerBox);
    }

    public void initStyles() {
        layeredPane.setPreferredSize(IHMScaling.SCREEN_SIZE);

        backgroundLabel.setBounds(0, 0, layeredPane.getPreferredSize().width, layeredPane.getPreferredSize().height);
        conquestLogoLabel.setBounds((layeredPane.getPreferredSize().width / 2) - BUTTONS_WIDTH / 2, 20, BUTTONS_WIDTH,
                BUTTONS_HEIGHT + 50);
        startButton.setBounds((layeredPane.getPreferredSize().width / 2) - BUTTONS_WIDTH / 2,
                IHMScaling.scale(300), BUTTONS_WIDTH, BUTTONS_HEIGHT);
        loadSavedGameButton.setBounds((layeredPane.getPreferredSize().width / 2) - BUTTONS_WIDTH / 2,
                IHMScaling.scale(500), BUTTONS_WIDTH, BUTTONS_HEIGHT);
        quitButton.setBounds((layeredPane.getPreferredSize().width / 2) - BUTTONS_WIDTH / 2,
                IHMScaling.scale(700), BUTTONS_WIDTH, BUTTONS_HEIGHT);

        startButton.setFocusPainted(false);
        loadSavedGameButton.setFocusPainted(false);
        quitButton.setFocusPainted(false);

        startButton.setBackground(Color.BLACK);
        loadSavedGameButton.setBackground(Color.BLACK);
        quitButton.setBackground(Color.BLACK);

        startButton.setForeground(Color.WHITE);
        loadSavedGameButton.setForeground(Color.WHITE);
        quitButton.setForeground(Color.WHITE);

        startButton.setFont(BUTTONS_FONT);
        loadSavedGameButton.setFont(BUTTONS_FONT);
        quitButton.setFont(BUTTONS_FONT);

        centerPanel.setBackground(IHMConfig.BACKGROUND_GAME_DISPLAY_COLOR);
        setBackground(IHMConfig.BACKGROUND_GAME_DISPLAY_COLOR);
    }

    public void initActions() {
        startButton.addActionListener(new StartAction());
        loadSavedGameButton.addActionListener(new LoadSavedGameAction());
        quitButton.addActionListener(new QuitAction());
    }

    /**
     * Interact with {@link  MainGUI} to display the {@link StartMenu} panel.
     * If MainGUI already contains an instance of {@link Game} it shows option dialog to choose to switch to
     * game display or not.
     */
    class StartAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            int result = 1;

            if (MainGUI.getInstance().gameHasBeenCreated() && !MainGUI.getInstance().currentGameEnded()) {
                result = JOptionPane.showOptionDialog(null, "You left a game without saving it, do you want to resume ?", null,
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            }

            if (result == 0) {
                MainGUI.getInstance().switchToGameDisplay();
            } else {
                MainGUI.getInstance().switchToStartMenu();
            }
        }
    }

    /**
     * Interact with {@link  MainGUI} to retrieve the game objects saved in file by using
     * {@link engine.util.GameSerializer} and switch to game display if the object is not null.
     * Retrieve only last game saved with specified path SAVED_GAME_PATH in {@link engine.util.GameSerializer}.
     */
    class LoadSavedGameAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            MainGUI.getInstance().loadSavedGame();
        }
    }

    /**
     * This action stop the program.
     */
    class QuitAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
}
