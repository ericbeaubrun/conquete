package ihm.sidepanel;

import ihm.util.IHMScaling;
import ihm.util.ImageUtility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class provides an independent panel to show some tutorial images to help players.
 * It possible to choose the image by clicking on next or previous.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class TutorialPanel extends JPanel {

    /**
     * All tutorial images resolution is : 612x613.
     */
    private final int IMAGES_SIZE = 612;

    /**
     * This array contains all tutorial images.
     */
    private final ImageIcon[] images = {
            ImageUtility.getScaledIcon("/res/images/tutorial/01_tuto_shop.png", IMAGES_SIZE, IMAGES_SIZE),
            ImageUtility.getScaledIcon("/res/images/tutorial/02_tuto_move.png", IMAGES_SIZE, IMAGES_SIZE),
            ImageUtility.getScaledIcon("/res/images/tutorial/03_tuto_attack.png", IMAGES_SIZE, IMAGES_SIZE),
            ImageUtility.getScaledIcon("/res/images/tutorial/04_tuto_merge.png", IMAGES_SIZE, IMAGES_SIZE),
            ImageUtility.getScaledIcon("/res/images/tutorial/05_tuto_buy_structures.png", IMAGES_SIZE, IMAGES_SIZE),
            ImageUtility.getScaledIcon("/res/images/tutorial/06_tuto_move_all.png", IMAGES_SIZE, IMAGES_SIZE),
            ImageUtility.getScaledIcon("/res/images/tutorial/07_tuto_auto_move.png", IMAGES_SIZE, IMAGES_SIZE),
            ImageUtility.getScaledIcon("/res/images/tutorial/08_tuto_special_blocks.png", IMAGES_SIZE, IMAGES_SIZE)
    };

    private int i = 0;

    /**
     * The current {@link ImageIcon} displayed.
     */
    private final JLabel imageLabel;

    public TutorialPanel() {

        JButton previousButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");
        previousButton.setForeground(Color.WHITE);
        nextButton.setForeground(Color.WHITE);
        previousButton.setFocusPainted(false);
        nextButton.setFocusPainted(false);
        previousButton.addActionListener(new PreviousButtonListener());
        nextButton.addActionListener(new NextButtonListener());
        previousButton.setBackground(Color.BLACK);
        nextButton.setBackground(Color.BLACK);
        previousButton.setPreferredSize(new Dimension(100, 30));
        nextButton.setPreferredSize(new Dimension(100, 30));

        imageLabel = new JLabel(images[i]);

        add(previousButton, BorderLayout.WEST);
        add(imageLabel, BorderLayout.CENTER);
        add(nextButton, BorderLayout.EAST);
    }

    /**
     * Change the {@link ImageIcon} displayed in the panel.
     */
    private class PreviousButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int amountOfImages = images.length;
            i = (i - 1 + amountOfImages) % amountOfImages;
            imageLabel.setIcon(images[i]);
        }
    }

    /**
     * Change the {@link ImageIcon} displayed in the panel.
     */
    private class NextButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int amountOfImages = images.length;
            i = (i + 1) % amountOfImages;
            imageLabel.setIcon(images[i]);
        }
    }
}

