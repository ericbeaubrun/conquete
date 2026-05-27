package data.player;

import configuration.GameplayConfig;
import data.board.Block;
import data.element.Base;
import data.element.Element;
import data.element.Soldier;
import engine.process.BotAction;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Data class keeping in memory player/bot color, territory, owned elements, gold and rights.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class Player implements Serializable {

    /**
     * Define the player's color (a player color must have a unique color to prevent a player from being considered on the same side).
     */
    private final PlayerColor color;

    /**
     * When true the Player can't play anymore.
     */
    private Boolean hasLost;

    /**
     * When true the Player can select {@link Block} to do actions.
     */
    private Boolean canPlay;

    /**
     * The amount of total gold accumulated.
     */
    private int totalGold;

    /**
     * The amount of gold that increases each turn.
     */
    private int goldPerTurn;

    /**
     * When true, Player's actions will be performed automatically by algorithm {@link BotAction}.
     */
    private final Boolean isBot;

    /**
     * List of {@link Block} to keep in memory the player's territory.
     */
    private final ArrayList<Block> ownedBlocks = new ArrayList<>();


    /**
     * List of {@link Element} to keep in memory the player's army.
     */
    private final ArrayList<Element> ownedElements = new ArrayList<>();

    /**
     * The block where the player spawns on the map.
     */
    private final Block baseBlock;


    /**
     * Define difficult level of bot player, when this value equal 0 no difficult level is defined.
     */
    private int difficultLevel = 0;
    public int getDifficultLevel() {
        return difficultLevel;
    }
    public void setDifficultLevel(int difficultLevel) {
        this.difficultLevel = difficultLevel;
    }

    /**
     * @param color defines the unique player color.
     * @param isBot defines if the player is human or nor, when true Player's actions will be performed automatically
     *              by algorithm {@link BotAction}.
     */
    public Player(Block baseBlock, PlayerColor color, Boolean isBot) {
        this.baseBlock = baseBlock;
        this.color = color;
        this.isBot = isBot;

        hasLost = false;
        canPlay = false;
        goldPerTurn = GameplayConfig.GOLD_PER_TURN_INITIAL;
        totalGold = GameplayConfig.TOTAL_GOLD_INITIAL;
    }

    public Boolean ownsBlock(Block block) {
        return ownedBlocks.contains(block);
    }

    public Boolean ownsElement(Element element) {
        return ownedElements.contains(element);
    }

    public Boolean isBot() {
        return isBot;
    }

    public Boolean canPlay() {
        return canPlay;
    }

    public Boolean hasLost() {
        return hasLost;
    }

    public ArrayList<Element> getOwnedElementsList() {
        return new ArrayList<>(ownedElements);
    }

    public ArrayList<Block> getOwnedBlocksList() {
        return new ArrayList<>(ownedBlocks);
    }

    public void addOwnedElement(Element element) {
        if (!ownedElements.contains(element)) {
            ownedElements.add(element);
        }
    }

    public void allowToPlay() {
        canPlay = true;
    }

    public void preventToPlay() {
        canPlay = false;
    }

    public void setLost() {
        hasLost = true;
    }

    public void removeOwnedElement(Element element) {
        ownedElements.remove(element);
    }

    public void addOwnedBlock(Block block) {
        if (!ownedBlocks.contains(block)) {
            ownedBlocks.add(block);
        }
    }

    public void removeOwnedBlock(Block block) {
        ownedBlocks.remove(block);
    }

    public void decrementTotalGold(int amount) {
        totalGold -= amount;
    }

    public void incrementGoldPerTurnToTotalGold() {
        setTotalGold(totalGold + goldPerTurn);
    }

    public void setTotalGold(int totalGold) {
        this.totalGold = Math.min(totalGold, GameplayConfig.TOTAL_GOLD_MAX);
    }

    public void setGoldPerTurn(int goldPerTurn) {
        this.goldPerTurn = Math.min(goldPerTurn, GameplayConfig.GOLD_PER_TURN_MAX);
    }

    public int getTotalOwnedElements() {
        return ownedElements.size();
    }

    public int getTotalGold() {
        return totalGold;
    }

    public int getTotalOwnedBlocks() {
        return ownedBlocks.size();
    }

    public int getTotalOwnedSoldier() {
        int count = 0;
        for (Element element : ownedElements) {
            if (element instanceof Soldier) {
                count++;
            }
        }
        return count;
    }

    public int getGoldPerTurn() {
        return goldPerTurn;
    }

    public Base getBase() {
        for (Element element : ownedElements) {
            if (element instanceof Base) {
                return (Base) element;
            }
        }
        return null;
    }

    public Block getBaseBlock() {
        return baseBlock;
    }

    public Color getColor() {
        return color.getColor();
    }

    public String getColorName() {
        return color.getColorName();
    }
}
