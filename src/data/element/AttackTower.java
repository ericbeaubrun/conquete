package data.element;

import configuration.GameplayConfig;

/**
 * Data Class keeping in memory position and health points of attack
 * tower.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class AttackTower extends Element {

    /**
     * The damage deals to enemy in range.
     */
    private int damageDeals;

    public AttackTower(int x, int y) {
        super(x, y, GameplayConfig.ATTACK_TOWER_INITIAL_HEALTH, GameplayConfig.ATTACK_TOWER_INITIAL_HEALTH);
        damageDeals = GameplayConfig.BONUS_ATTACK_TOWER;
    }

    public int getDamageDeals() {
        return damageDeals;
    }

    public void setDamageDeals(int damageDeals) {
        this.damageDeals = damageDeals;
    }
}
