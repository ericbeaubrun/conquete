package data.element;

import configuration.GameplayConfig;

/**
 * Data Class keeping in memory position and health points of Tree.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class ForestTree extends Element {

    /**
     * The bonus of gold given to player directly when destroyed it.
     */
    private int bonusGoldWhenDestroyed;

    public ForestTree(int x, int y) {
        super(x, y, GameplayConfig.FOREST_TREE_HEALTH,GameplayConfig.FOREST_TREE_HEALTH);
        bonusGoldWhenDestroyed = GameplayConfig.BONUS_GOLD_FOREST_TREE;
    }

    public int getBonusGoldWhenDestroyed() {
        return bonusGoldWhenDestroyed;
    }

    public void setBonusGoldWhenDestroyed(int bonusGoldWhenDestroyed) {
        this.bonusGoldWhenDestroyed = bonusGoldWhenDestroyed;
    }
}
