package ihm.paint;

import configuration.IHMConfig;
import data.element.*;
import ihm.util.IHMScaling;
import ihm.util.ImageUtility;

import java.awt.*;

/**
 * This class provides  methods for painting Element on the map in the game.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class PaintElement {

    private final String LEFT_SOLDIER_IMAGE_FILE_PATH = "/res/images/gameplay/soldierLeft.png";
    private final String RIGHT_SOLDIER_IMAGE_FILE_PATH = "/res/images/gameplay/soldierRight.png";
    private final String BASE_IMAGE_FILE_PATH = "/res/images/gameplay/base.png";
    private final String HOUSE_IMAGE_FILE_PATH = "/res/images/gameplay/house.png";
    private final String ATTACK_TOWER_IMAGE_FILE_PATH = "/res/images/gameplay/attackTower.png";
    private final String DEFENSE_TOWER_IMAGE_FILE_PATH = "/res/images/gameplay/defenseTower.png";
    private final String FOREST_TREE_IMAGE_FILE_PATH = "/res/images/gameplay/forestTree.png";

    //Read image before all to ensure reduce performance loss
    private final Image leftSoldierImage = ImageUtility.readImage(LEFT_SOLDIER_IMAGE_FILE_PATH);
    private final Image rightSoldierImage = ImageUtility.readImage(RIGHT_SOLDIER_IMAGE_FILE_PATH);
    private final Image baseImage = ImageUtility.readImage(BASE_IMAGE_FILE_PATH);
    private final Image houseImage = ImageUtility.readImage(HOUSE_IMAGE_FILE_PATH);
    private final Image attackTowerImage = ImageUtility.readImage(ATTACK_TOWER_IMAGE_FILE_PATH);
    private final Image defenseTowerImage = ImageUtility.readImage(DEFENSE_TOWER_IMAGE_FILE_PATH);
    private final Image forestTreeImage = ImageUtility.readImage(FOREST_TREE_IMAGE_FILE_PATH);

    private final PaintIndicator paintIndicator = new PaintIndicator();

    /**
     * This method allows to draw House on block using the following parameters.
     *
     * @param house The player's House to draw.
     * @param color The player's color.
     * @param g     The graphics context in which the drawing will be performed.
     */
    private void paint(Graphics g, House house, String color) {
        int x = house.getX();
        int y = house.getY();
        int health = house.getHealthPoint();

        ImageUtility.drawScaledImage(g, houseImage, x, y, 0, 2, 45, 45);

//        paintIndicator.paintElementStatistic(g, health, x, y, 37, 44, IHMConfig.STRUCTURE_STATISTICS_FONT,
//                IHMConfig.ELEMENT_STATISTICS_COLOR);
        paintIndicator.paintHealthBar(g, house);
    }

    /**
     * This method allows to draw AttackTower on block using the following parameters.
     *
     * @param tower The player's AttackTower to draw.
     * @param color The player's color.
     * @param g     The graphics context in which the drawing will be performed.
     */
    private void paint(Graphics g, AttackTower tower, String color) {
        int x = tower.getX();
        int y = tower.getY();
        int health = tower.getHealthPoint();

        ImageUtility.drawScaledImage(g, attackTowerImage, x, y, -6, -22, 56, 75);

//        paintIndicator.paintElementStatistic(g, health, x, y, 34, 43, IHMConfig.STRUCTURE_STATISTICS_FONT,
//                IHMConfig.ELEMENT_STATISTICS_COLOR);
        paintIndicator.paintHealthBar(g, tower);
    }

    /**
     * This method allows to draw DefenseTower on block using the following parameters.
     *
     * @param tower The player's DefenseTower to draw.
     * @param color The player's color.
     * @param g     The graphics context in which the drawing will be performed.
     */
    private void paint(Graphics g, DefenseTower tower, String color) {

        int x = tower.getX();
        int y = tower.getY();
        int health = tower.getHealthPoint();

        ImageUtility.drawScaledImage(g, defenseTowerImage, x, y, -6, -20, 56, 75);
        paintIndicator.paintHealthBar(g, tower);
    }

    /**
     * This method allows to draw Tree on block using the following parameters.
     *
     * @param tree The Tree to draw.
     * @param g    The graphics context in which the drawing will be performed.
     */
    private void paint(Graphics g, ForestTree tree) {
        ImageUtility.drawScaledImage(g, forestTreeImage, tree.getX(), tree.getY(), -3, -3, 48, 48);
    }

    /**
     * This method allows to draw Base on block using the following parameters.
     *
     * @param base  The player's Base.
     * @param color The player's color.
     * @param g     The graphics context in which the drawing will be performed.
     */
    private void paint(Graphics g, Base base, String color) {
        int x = base.getX();
        int y = base.getY();

        // New style colored style for bases
        Image coloredBaseImage = ImageUtility.readImage("/res/images/gameplay/base" + color + ".png");
        if (coloredBaseImage != null) {
            ImageUtility.drawScaledImage(g, coloredBaseImage, x, y, -22, -21, 88, 66);
        } else {
            ImageUtility.drawScaledImage(g, baseImage, x, y, -22, -21, 88, 66);
        }

        paintIndicator.paintHealthBar(g, base);

    }

    /**
     * This method allows to draw Soldier on block using the following parameters.
     *
     * @param soldier The player's Soldier to draw.
     * @param color   The player's color.
     * @param g       The graphics context in which the drawing will be performed.
     */
    private void paint(Graphics g, Soldier soldier, Boolean withAvailableMoveIndicator, String color) {
        int x = soldier.getX();
        int y = soldier.getY();
        int attackPoint = soldier.getAttackPoint();
        int health = soldier.getHealthPoint();

        if (soldier.lastMoveWasRight()) {
            ImageUtility.drawScaledImage(g, rightSoldierImage, x, y, -3, -5, 52, 52);

        } else {
            ImageUtility.drawScaledImage(g, leftSoldierImage, x, y, -3, -5, 52, 52);
        }

        //draw health bar
        paintIndicator.paintHealthBar(g, soldier);

        // draw canMove indicator
        if (withAvailableMoveIndicator && soldier.canMove()) {
            paintIndicator.paintAvailableMoveIndicator(g, x, y);
        }
        //draw attack points
        paintIndicator.paintElementStatistic(g, attackPoint, x, y, 37, 15, new Font(Font.DIALOG, Font.BOLD, IHMScaling.scale(18)),
                IHMConfig.ELEMENT_STATISTICS_COLOR);
    }

    /**
     * This method allows to draw any following Element on block using the following parameters.
     *
     * @param element The Element to draw.
     * @param color   The player's color.
     * @param g       The graphics context in which the drawing will be performed.
     */
    public void paint(Graphics g, Element element, String color, Boolean withIndicator) {
        if (element instanceof Base) {
            paint(g, (Base) element, color);

        } else if (element instanceof Soldier) {
            paint(g, (Soldier) element, withIndicator, color);

        } else if (element instanceof House) {
            paint(g, (House) element, color);

        } else if (element instanceof AttackTower) {
            paint(g, (AttackTower) element, color);

        } else if (element instanceof DefenseTower) {
            paint(g, (DefenseTower) element, color);

        } else if (element instanceof ForestTree) {
            paint(g, (ForestTree) element);

        } else {
            // log
        }
    }

    public void paint(Graphics g, Element element, String color) {
        paint(g, element, color,true);
    }

    public void paint(Graphics g, Element element) {
        paint(g, element, "");
    }
}
