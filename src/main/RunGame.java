package main;

import configuration.MapsConfig;
import data.board.GameMap;

/**
 * Run the game without passing through the menus.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class RunGame {

    public static void main(String[] args) {
        MainGUI.getInstance().initNewGame(1, 3, 1, MapsConfig.AVAILABLE_MAPS_LIST[0]);
        MainGUI.getInstance().switchToGameDisplay();
    }
}