package engine.datasearch.pathfinder;

import data.board.Block;

/**
 * A class that represents a node in and adding additional properties and functionality
 * for use in the pathfinding algorithm.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class BlockNode extends Block {

    private int initialCost;
    private int realCost;
    private int heuristic;
    private BlockNode parentNode;

    /**
     * Constructs a new BlockNode with the specified position and initial cost.
     *
     * @param x    the x coordinate of the block
     * @param y    the y coordinate of the block
     * @param cost the initial cost to reach this block
     */
    public BlockNode(int x, int y, int cost) {
        super(x, y);
        this.initialCost = cost;
        this.realCost = Integer.MAX_VALUE;
        this.heuristic = 0;
        this.parentNode = null;
    }

    public int getInitialCost() {
        return initialCost;
    }

    public int getRealCost() {
        return realCost;
    }

    public BlockNode getParentNode() {
        return parentNode;
    }

    public int getScore() {
        return realCost + heuristic;
    }

    public void setInitialCost(int initialCost) {
        this.initialCost = initialCost;
    }

    public void setRealCost(int realCost) {
        this.realCost = realCost;
    }

    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    public void setParentNode(BlockNode parentNode) {
        this.parentNode = parentNode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BlockNode other = (BlockNode) obj;
        return getIndexX() == other.getIndexX() && getIndexY() == other.getIndexY();
    }
}
