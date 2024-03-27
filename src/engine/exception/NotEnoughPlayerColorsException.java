package engine.exception;

/**
 * This exception should be thrown when the amount of player in the game is higher than the colors available.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class NotEnoughPlayerColorsException extends Exception {
    public NotEnoughPlayerColorsException() {
        super();
    }
}
