package engine.datasearch;

import data.board.Block;
import data.element.Element;
import data.element.Soldier;
import data.player.Player;
import data.element.ElementsMap;
import engine.util.DistanceCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class provides methods for finding {@link Element} in a game.
 * It contains methods to retrieve, convert, and calculate elements in a map.
 */
public class ElementsFinder {

    private final ElementsMap elementsMap;

    public ElementsFinder(ElementsMap elementsManager) {
        this.elementsMap = elementsManager;
    }

    public Element findElementOnBlock(Block block) {
        return elementsMap.get(block);
    }

    /**
     * Converts a map of {@link Element} to an ArrayList.
     *
     * @param elementType the class of elements to retrieve
     * @return an ArrayList of elements of the specified type
     */
    public <T extends Element> ArrayList<T> elementMapToList(Class<T> elementType) {
        ArrayList<T> list = new ArrayList<>();
        for (Element element : elementsMap.getValues()) {
            if (elementType.isInstance(element)) {
                list.add((T) element);
            }
        }
        return list;
    }

    /**
     * Calculates the number of elements of the specified type in the map.
     *
     * @param elementType the class of elements to retrieve
     * @return the number of elements of the specified type in the map
     */
    public <T extends Element> int calculateAmountOfElement(Class<T> elementType) {
        int count = 0;
        for (Element element : elementsMap.getValues()) {
            if (elementType.isInstance(element)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks whether an element of the specified type exists on the given block.
     *
     * @param elementType the class of elements to retrieve
     * @param block       the block to check for the element on
     * @return true if an element of the specified type exists on the given block, false otherwise
     */
    public <T extends Element> Boolean elementTypeExistsOnBlock(Class<T> elementType, Block block) {
        if (block != null) {
            Element element = elementsMap.get(block);
            return elementType.isInstance(element);
        }
        return false;
    }

    /**
     * Checks whether an element of the specified type exists on the given block and is owned by the given player.
     *
     * @param elementType the class of elements to retrieve
     * @param player      the player to check ownership of the block
     * @param block       the block to check for the element on
     * @return true if an element of the specified type exists on the given block and is owned by the given player, false otherwise
     */
    public <T extends Element> Boolean elementTypeExistsOnBlock(Class<T> elementType, Player player, Block block) {
        if (block != null || player != null) {
            Element element = elementsMap.get(block);
            return elementType.isInstance(element) && player.ownsBlock(block);
        }
        return false;
    }

    /**
     * Finds the Soldiers that exist on the given zone.
     *
     * @param zone the zone to search for Soldiers on
     * @return an ArrayList of Soldiers that exist on the given zone
     */
    public ArrayList<Soldier> findSoldiersInZone(ArrayList<Block> zone) {
        ArrayList<Soldier> result = new ArrayList<>();
        if (zone != null) {
            for (Block block : zone) {
                if (elementsMap.containsBlock(block)) {
                    if (elementTypeExistsOnBlock(Soldier.class, block)) {
                        result.add((Soldier) findElementOnBlock(block));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Checks if a zone contains an element of a given type and ownership.
     *
     * @param elementType the type of element to check for
     * @param player      the player who owns the element to check for
     * @param zone        the zone to check
     * @param isOwned     true to check for owned elements, false to check for unowned elements
     * @return true if the zone contains an element of the specified type and ownership, false otherwise
     */
    public <T extends Element> Boolean zoneContainsElement(Class<T> elementType, Player player, ArrayList<Block> zone, Boolean isOwned) {
        Boolean result = false;
        if (player != null && zone != null) {
            for (Block block : zone) {
                if (elementsMap.containsBlock(block)) {
                    Element element = elementsMap.get(block);
                    if (elementType.isInstance(element) && (isOwned == player.ownsElement(element))) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Checks if a zone contains an element of a given type and ownership.
     *
     * @param player the player who owns the element to check for
     * @param zone   the zone to check
     * @return true if the zone contains an element of the specified type and ownership, false otherwise
     */
    public Boolean zoneContainsAllieSoldier(Player player, ArrayList<Block> zone) {
        return (player != null && zone != null) ? zoneContainsElement(Soldier.class, player, zone, true) : null;
    }

    /**
     * Checks if a zone contains an element of a given type and ownership.
     *
     * @param player the player who owns the element to check for
     * @param zone   the zone to check
     * @return true if the zone contains an element of the specified type and ownership, false otherwise
     */
    public Boolean zoneContainsEnemySoldier(Player player, ArrayList<Block> zone) {
        return (player != null && zone != null) ? zoneContainsElement(Soldier.class, player, zone, false) : null;
    }

    /**
     * Checks if a zone contains an element of a given type and ownership.
     *
     * @param elementType the type of element to check for
     * @param player      the player who owns the element to check for
     * @param zone        the zone to check
     * @return true if the zone contains an element of the specified type and ownership, false otherwise
     */
    public <T extends Element> Boolean zoneContainsEnemyElement(Class<T> elementType, Player player, ArrayList<Block> zone) {
        return (player != null && zone != null) ? zoneContainsElement(elementType, player, zone, false) : null;
    }

    /**
     * Finds the nearest owned element of a given type to a given element.
     *
     * @param element     the element for which to find the nearest owned element of the specified type
     * @param elementType the class of elements to retrieve
     * @param player      the player who owns the element to find
     * @param isOwned     true to search for owned elements, false to search for unowned elements
     * @return the nearest owned element of the specified type to the given element, or null if no such element is found
     * @throws NullPointerException if the player or the element is null
     */
    public <T extends Element> Element findNearstOwnedElement(Element element, Class<T> elementType, Player player, boolean isOwned) {
        Element closerElement = null;
        if (element != null && player != null) {
            ArrayList<T> list = elementMapToList(elementType);
            int minDistance = Integer.MAX_VALUE;

            for (Element candidate : list) {
                if (!candidate.equals(element) && (isOwned == player.ownsElement(candidate))) {
                    int distance = DistanceCalculator.calculateDistance(element.getX(), element.getY(),
                            candidate.getX(), candidate.getY());
                    if (distance < minDistance) {
                        minDistance = distance;
                        closerElement = candidate;
                    }
                }
            }
        }
        return closerElement;
    }

    /**
     * Finds the nearest owned elements of a given type to a given element, up to a specified number of elements.
     *
     * @param element            the element for which to find the nearest owned elements
     * @param elementType        the type of element to find
     * @param player             the player who owns the elements to find
     * @param isOwned            true to find elements owned by the player, false to find elements not owned by the player
     * @param numClosestElements the maximum number of closest elements to find
     * @return a List of the nearest owned elements of the specified type to the given element
     * @throws NullPointerException if the element or the player is null
     */
    public <T extends Element> List<Element> findNearestOwnedElements(Element element, Class<T> elementType, Player player, boolean isOwned, int numClosestElements) {
        List<Element> closestElements = new ArrayList<>();
        if (element != null && player != null && numClosestElements > 0) {
            ArrayList<T> list = elementMapToList(elementType);
            TreeMap<Integer, T> distanceMap = new TreeMap<>();

            for (T candidate : list) {
                if (!candidate.equals(element) && (isOwned == player.ownsElement(candidate))) {
                    int distance = DistanceCalculator.calculateDistance(element.getX(), element.getY(),
                            candidate.getX(), candidate.getY());
                    distanceMap.put(distance, candidate);
                }
            }

            int count = 0;
            for (Map.Entry<Integer, T> entry : distanceMap.entrySet()) {
                if (count < numClosestElements) {
                    closestElements.add(entry.getValue());
                    count++;
                } else {
                    break;
                }
            }
        }
        return closestElements;
    }

    /**
     * Finds the nearest owned elements of a given type to a given element, up to a specified number of elements.
     *
     * @param soldier
     * @param player  the player who owns the elements to find
     * @return a List of the nearest owned elements of the specified type to the given element
     * @throws NullPointerException if the element or the player is null
     */
    public Soldier findNearestEnemySoldier(Soldier soldier, Player player) {
        return (Soldier) findNearstOwnedElement(soldier, Soldier.class, player, false);
    }

    /**
     * Finds the nearest owned elements of a given type to a given element, up to a specified number of elements.
     *
     * @param soldier
     * @param player  the player who owns the elements to find
     * @return a List of the nearest owned elements of the specified type to the given element
     */
    public Soldier findNearestAllieSoldier(Soldier soldier, Player player) {
        return (Soldier) findNearstOwnedElement(soldier, Soldier.class, player, true);
    }

    /**
     * Finds the nearest owned elements of a given type to a given element, up to a specified number of elements.
     *
     * @param block  the block for which to find the nearest allie soldier
     * @param player the player who owns the elements to find
     * @return a List of the nearest owned elements of the specified type to the given element
     */
    public Soldier findNearestAllieSoldier(Block block, Player player) {
        return elementTypeExistsOnBlock(Soldier.class, block) ?
                (Soldier) findNearstOwnedElement(findElementOnBlock(block), Soldier.class, player, true) : null;
    }

    /**
     * Finds the nearest enemy soldier to a given block that is not owned by a player.
     *
     * @param block  the block for which to find the nearest enemy soldier
     * @param player the player who does not own the enemy soldier to find
     * @return the nearest enemy soldier to the given block that is not owned by the player, or null if no such soldier is found
     */
    public Soldier findNearestEnemySoldier(Block block, Player player) {
        return elementTypeExistsOnBlock(Soldier.class, block) ?
                (Soldier) findNearstOwnedElement(findElementOnBlock(block), Soldier.class, player, false) : null;
    }

    /**
     * Finds the nearest allies soldiers to a given block, up to a specified number of soldiers.
     *
     * @param block  the block for which to find the nearest allies soldiers
     * @param player the player who owns the soldiers to find
     * @param amount the maximum number of closest soldiers to find
     * @return the nearest allies soldiers to the given block, up to the specified number of soldiers, or null if no ally soldier is found
     */
    public Soldier findNearestAllySoldiers(Block block, Player player, int amount) {
        return elementTypeExistsOnBlock(Soldier.class, block) ?
                (Soldier) findNearestOwnedElements(findElementOnBlock(block), Soldier.class, player, true, amount) : null;
    }

    /**
     * Finds the weakest soldier owned by a player in a given zone.
     *
     * @param player  the player who owns the soldier
     * @param zone    the zone in which to search for the soldier
     * @param isOwned true to search for owned soldiers, false to search for unowned soldiers
     * @return the weakest soldier owned by the player in the specified zone, or null if no soldier is found
     */
    public Soldier findWeakestSoldierInZone(Player player, ArrayList<Block> zone, Boolean isOwned) {
        Soldier weakerSoldier = null;
        if (player != null && zone != null & isOwned != null) {
            for (Block block : zone) {
                if (elementTypeExistsOnBlock(Soldier.class, player, block)) {
                    Soldier soldier = (Soldier) findElementOnBlock(block);
                    if (player.ownsElement(soldier) && (isOwned == player.ownsElement(soldier))) {
                        if (weakerSoldier == null || (weakerSoldier.getAttackPoint() + weakerSoldier.getHealthPoint()
                                < soldier.getAttackPoint() + soldier.getHealthPoint()
                        )) {
                            weakerSoldier = soldier;
                        }
                    }
                }
            }
        }
        return weakerSoldier;
    }

    /**
     * Finds the strongest soldier owned by a player in a given zone.
     *
     * @param player  the player who owns the soldier
     * @param zone    the zone in which to search for the soldier
     * @param isOwned true to search for owned soldiers, false to search for unowned soldiers
     * @return the strongest soldier owned by the player in the specified zone, or null if no soldier is found
     */
    public Soldier findStrongestSoldierInZone(Player player, ArrayList<Block> zone, Boolean isOwned) {
        Soldier strongerSoldier = null;
        if (player != null && zone != null & isOwned != null) {
            for (Block block : zone) {
                if (elementTypeExistsOnBlock(Soldier.class, player, block)) {
                    Soldier soldier = (Soldier) findElementOnBlock(block);
                    if (player.ownsElement(soldier) && (isOwned == player.ownsElement(soldier))) {
                        if (strongerSoldier == null || (strongerSoldier.getAttackPoint() + strongerSoldier.getHealthPoint()
                                > soldier.getAttackPoint() + soldier.getHealthPoint())) {
                            strongerSoldier = soldier;
                        }
                    }
                }
            }
        }
        return strongerSoldier;
    }

    /**
     * Counts the number of elements of a given type owned by a player.
     *
     * @param elementType the type of element to count
     * @param player      the player who owns the elements to count
     * @return the number of elements of the specified type owned by the player
     * @throws NullPointerException if the player is null
     */
    public <T extends Element> int countElementType(Class<T> elementType, Player player) {
        int elementTypeCount = 0;
        ArrayList<T> elementTypeList = elementMapToList(elementType);

        for (T element : elementTypeList) {
            if (player.ownsElement(element)) {
                elementTypeCount++;
            }
        }
        return elementTypeCount;
    }
}
