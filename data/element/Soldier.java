package data.element;

import configuration.GameplayConfig;

/**
 * Data class keeping in memory position and health points of Houses.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class Soldier extends Element {


    /**
     * Defines when the soldier is allowed/prevented to move.
     */
    private Boolean canMove;

    /**
     * Define damage deals to enemy.
     */
    private int attackPoint;

    /**
     * Define last move direction.
     */
    private Boolean lastMoveRight;

    public Soldier(int x, int y) {
        super(x, y, GameplayConfig.SOLDIER_INITIAL_HEALTH, GameplayConfig.SOLDIER_HEALTH_LIMIT);
        attackPoint = GameplayConfig.SOLDIER_INITIAL_ATTACK_POINTS;
        lastMoveRight = false;
        canMove = true;
    }

    public void incrementAttackPoint(int amount) {
        attackPoint += amount;
        if (attackPoint > GameplayConfig.SOLDIER_ATTACK_LIMIT) {
            attackPoint = GameplayConfig.SOLDIER_ATTACK_LIMIT;
        }
    }

    public void incrementHealthPoint(int amount) {
        setHealthPoint(getHealthPoint() + amount);
        if (getHealthPoint() > GameplayConfig.SOLDIER_HEALTH_LIMIT) {
            setHealthPoint(GameplayConfig.SOLDIER_HEALTH_LIMIT);
        }
    }

    public Boolean lastMoveWasRight() {
        return lastMoveRight;
    }

    public int getGoldPerTurnMalus() {
        return getAttackPoint() + getHealthPoint() + GameplayConfig.MALUS_GOLD_SOLDIER;
    }

    public int getAttackPoint() {
        return attackPoint;
    }

    public Boolean canMove() {
        return canMove;
    }

    public void preventToMove() {
        canMove = false;
    }

    public void allowToMove() {
        canMove = true;
    }

    public void setLastMoveRight() {
        lastMoveRight = true;
    }

    public void setLastMoveLeft() {
        lastMoveRight = false;
    }
}
