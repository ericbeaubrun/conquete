package data.element;

import data.board.Block;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

/**
 * The ElementsMap class provides a way to reference elements on blocks on the game map.
 * The class contains methods to add, change and remove an element to a block.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class ElementsMap implements Serializable {

    private final HashMap<Block, Element> elementsMap = new HashMap<>();

    public ElementsMap() {
    }

    /**
     * Adds an element to a block on the game map.
     *
     * @param block   The block to add the element to.
     * @param element The element to add to the block.
     */
    public void putElementOnBlock(Block block, Element element) {
        if (block != null && element != null) {
            elementsMap.put(block, element);
            element.putOnBlock(block);
            block.setOccupied();
        }
    }

    /**
     * Removes the element on a block on the game map.
     *
     * @param block The block to remove the element from.
     */
    public void removeElementOnBlock(Block block) {
        if (block != null && elementsMap.containsKey(block)) {
            block.setFree();
            elementsMap.remove(block);
        }
    }

    /**
     * Changes the block of the element in the ElementsMap.
     *
     * @param lostBlock The block the element is currently on.
     * @param newBlock  The block to move the element to.
     */
    public void changeBlock(Block lostBlock, Block newBlock) {
        Element element = elementsMap.get(lostBlock);
        if (element != null && lostBlock != null && newBlock != null) {
            elementsMap.put(newBlock, element);
            element.putOnBlock(newBlock);
            newBlock.setOccupied();
            elementsMap.remove(lostBlock);
            lostBlock.setFree();
        }
    }

    public Element get(Block block) {
        return elementsMap.get(block);
    }

    public Boolean containsBlock(Block block) {
        return elementsMap.containsKey(block);
    }

    public Boolean containsElement(Element element) {
        return elementsMap.containsValue(element);
    }

    /**
     * @return Collection of Element presents in the ElementMap
     */
    public Collection<Element> getValues() {
        return elementsMap.values();
    }
}
