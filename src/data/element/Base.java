package data.element;

import configuration.GameplayConfig;

/**
 * Data Class keeping in memory position and health points of Base.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class Base extends Element {
    public Base(int x, int y) {
        super(x, y, GameplayConfig.BASE_INITIAL_HEALTH, GameplayConfig.BASE_INITIAL_HEALTH);
    }
}
