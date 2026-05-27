package data.element;

import configuration.GameplayConfig;

/**
 * Data Class keeping in memory position and health points of defense
 * tower.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class DefenseTower extends Element {

    /**
     * The health points deals to ally in range.
     */
    private int bonusHealthDeals;

    public DefenseTower(int x, int y) {
        super(x, y, GameplayConfig.DEFENSE_TOWER_INITIAL_HEALTH, GameplayConfig.DEFENSE_TOWER_INITIAL_HEALTH);
        bonusHealthDeals = GameplayConfig.BONUS_DEFENSE_TOWER;
    }

    public int getBonusHealthDeals() {
        return bonusHealthDeals;
    }

    public void setBonusHealthDeals(int bonusHealthDeals) {
        this.bonusHealthDeals = bonusHealthDeals;
    }
}
