package data.element;

import configuration.GameplayConfig;

/**
 * Data class keeping in memory position and health points of Houses.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class House extends Element {

    /**
     * The bonus of gold/turn given to player when buy it.
     */
    private int bonusGoldPerTurn = GameplayConfig.BONUS_GOLD_HOUSE;

    public House(int x, int y) {
        super(x, y, GameplayConfig.HOUSE_INITIAL_HEALTH, GameplayConfig.HOUSE_INITIAL_HEALTH);
    }

    public int getBonusGold() {
        return bonusGoldPerTurn;
    }

    public void setBonusGold(int bonusGold) {
        this.bonusGoldPerTurn = bonusGold;
    }
}
