package configuration;

import data.player.PlayerColor;

/**
 * Declares various constants to configure gameplay experience.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class GameplayConfig {

    //Elements health points
    public static final int BASE_INITIAL_HEALTH = 20;
    public static final int ATTACK_TOWER_INITIAL_HEALTH = 4;
    public static final int DEFENSE_TOWER_INITIAL_HEALTH = 6;
    public static final int HOUSE_INITIAL_HEALTH = 2;
    public static final int SOLDIER_INITIAL_HEALTH = 2;
    public static final int SOLDIER_HEALTH_LIMIT = 12;
    public static final int FOREST_TREE_HEALTH = 1;
    public static final int BONUS_DEFENSE_TOWER = 1;

    //Elements attack points
    public static final int SOLDIER_INITIAL_ATTACK_POINTS = 1;
    public static final int SOLDIER_ATTACK_LIMIT = 6;
    public static final int BONUS_ATTACK_TOWER = 1;

    //Gold
    public static final int TOTAL_GOLD_INITIAL = 250;
    public static final int GOLD_PER_TURN_INITIAL = 10;
    public static final int SOLDIER_PRICE = 100;
    public static final int HOUSE_PRICE = 200;
    public static final int ATTACK_TOWER_PRICE = 250;
    public static final int DEFENSE_TOWER_PRICE = 250;
    public static final int BONUS_GOLD_HOUSE = 10;
    public static final int BONUS_GOLD_FOREST_TREE = 10;
    public static final int MALUS_GOLD_SOLDIER = 2;
    public static final int MALUS_GOLD_ATTACK_TOWER = 4;
    public static final int MALUS_GOLD_DEFENSE_TOWER = 6;
    public static final int GOLD_PER_TURN_MAX = 100;
    public static final int TOTAL_GOLD_MAX = 1000;

    //Elements other stats
    public static final int BLOCKS_RANGE_SOLDIER_MOVE = 4;
    public static final int SPECIAL_BLOCK_BONUS_MULTIPLIER = 2;
    public static final int ATTACK_TOWER_RAYON = 1;
    public static final int DEFENSE_TOWER_RAYON = 2;
    public static final int FOREST_TREE_SPAWN_LIMIT = 30;

    //probability of spawn in %
    public static final int PROBABILITIES_OF_SPAWN_1_TREE = 2;
    public static final int PROBABILITIES_OF_SPAWN_2_TREE = 8;
    public static final int PROBABILITIES_OF_SPAWN_3_TREE = 16;

    //Players
    public static final int MAX_PLAYERS = 4;
    public static final PlayerColor[] POSSIBLE_PLAYER_COLORS = {
            new PlayerColor("Blue", 50, 100, 150),
            new PlayerColor("Red", 170, 60, 70),
            new PlayerColor("White", 175, 175, 175),
            new PlayerColor("Purple", 110, 60, 200)};
    public static final long BOT_PLAYING_ANIMATION_TIME = 40;
}
