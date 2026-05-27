package ihm.listener;

import configuration.GameplayConfig;
import data.board.Block;
import data.board.GameMap;
import data.element.AttackTower;
import data.element.Base;
import data.element.DefenseTower;
import data.element.Soldier;
import data.player.Player;
import main.game.GameManager;
import engine.datasearch.BlockFinder;
import engine.datasearch.ElementsFinder;
import engine.process.PlayersManager;
import engine.util.ConversionUtility;
import main.game.GameDisplay;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * This class collect the player selection on the map during the game.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class PlayerSelection extends GameListener implements MouseListener {

    private static final Logger logger = LoggerUtility.getLogger(PlayerSelection.class);

    public PlayerSelection(GameManager gameManager, GameDisplay gameDisplay) {
        super(gameManager, gameDisplay);
    }

    /**
     * Determining the selected block and deciding which action perform.
     *
     * @param e The MouseEvent that triggered the event.
     */
    @Override
    public void mousePressed(MouseEvent e) {

        int indexX = ConversionUtility.PixelToIndex(e.getPoint().getX());
        int indexY = ConversionUtility.PixelToIndex(e.getPoint().getY());

        //System.out.println("x= " + indexX + " y= " + indexY);

        ElementsFinder elementsFinder = getGameManager().getElementsFinder();
        PlayersManager playersManager = getGameManager().getPlayersManager();
        GameMap map = getGameManager().getMap();
        Block selectedBlock = getGameManager().getSelectedBlock();
        BlockFinder blockFinder = getGameManager().getBlockFinder();
        Player currentPlayer = playersManager.getCurrentPlayer();

        getGameDisplay().hideShopPanel();

        if (!getGameManager().elementIsSelected()) {

            selectedBlock = map.getBlock(indexX, indexY);
            getGameManager().setSelectedBlock(selectedBlock);

            getGameManager().setPossibleActionBlocks(null);


            if (selectedBlock != null) {
                if (!selectedBlock.isRemoved()) {
                    //The selected block exists
                    if (selectedBlock.isEmpty()) {
                        getGameDisplay().enableMoveAllSoldiersInDirectionButton();
                        //The player select his territory
                        if (playersManager.currentPlayerOwnsBlock(selectedBlock)) {
                            getGameDisplay().showShopPanel();
                            logger.info("Player [" + playersManager.getCurrentPlayer().getColorName() + "] selected an owned empty block in position " + selectedBlock.positionToString() + ".");
                        }
                    } else {
                        //The selected block contains an Element
                        getGameManager().setElementSelected(true);
                        if (elementsFinder.elementTypeExistsOnBlock(Soldier.class, selectedBlock)) { // element

                            Soldier soldier = (Soldier) elementsFinder.findElementOnBlock(getGameManager().getSelectedBlock());
                            logger.info("Player [" + playersManager.getCurrentPlayer().getColorName() + "] selected an owned soldier in position " + selectedBlock.positionToString() + ".");

                            if (soldier.canMove()) {
                                getGameManager().setPossibleActionBlocks(
                                        blockFinder.findPossibleMoveBlocksSoldier(selectedBlock, GameplayConfig.BLOCKS_RANGE_SOLDIER_MOVE,
                                                playersManager.getCurrentPlayer(), false));
                            } else {
                                getGameManager().resetSelection();
                            }
                        } else if (elementsFinder.elementTypeExistsOnBlock(AttackTower.class, selectedBlock)) {
                            logger.info("Player [" + currentPlayer.getColorName() + "] selected a attack tower in position " + selectedBlock.positionToString() + ".");

                        } else if (elementsFinder.elementTypeExistsOnBlock(DefenseTower.class, selectedBlock)) {
                            logger.info("Player [" + currentPlayer.getColorName() + "] selected a defense tower in position " + selectedBlock.positionToString() + ".");

                        } else if (elementsFinder.elementTypeExistsOnBlock(Base.class, selectedBlock)) {
                            logger.info("Player [" + currentPlayer.getColorName() + "] selected a Base in position " + selectedBlock.positionToString() + ".");

                        } else {
                            logger.error("Player [" + currentPlayer.getColorName() + "] selected an unidentified element in position " + selectedBlock.positionToString() + ".");
                            getGameManager().resetSelection();
                        }
                        getGameDisplay().disableMoveAllSoldiersInDirectionButton();
                    }
                } else {
                    getGameDisplay().disableMoveAllSoldiersInDirectionButton();
                    getGameManager().resetSelection();
                }
            }
        } else {


            if (getGameManager().getPossibleActionBlocks() != null) {
                //When a Soldier is selected

                //Keep the current block selected in memory
                Block lastSelectedBlock = selectedBlock;
                Block currentSelectedBlock = map.getBlock(indexX, indexY);
                getGameManager().setSelectedBlock(currentSelectedBlock);

                if (getGameManager().getPossibleActionBlocks().contains(currentSelectedBlock)) {
                    if (!selectedBlock.equals(currentSelectedBlock)) {
                        //When select another block
                        getGameManager().moveSoldierToBlock(lastSelectedBlock, currentSelectedBlock);
                        getGameManager().resetSelection();
                    }

                } else {
                    lastSelectedBlock = null;
                }
            }
            getGameManager().resetSelection();
        }
        getGameDisplay().refreshDisplay(getGameManager());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
