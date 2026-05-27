package engine.exception;

/**
 * This exception should be thrown when the map file has the wrong characters, is not readable or does not exist.
 *
 * @author Eric ADELAIDE-BEAUBRUN, William GABITA, Mya SOUDAIN.
 */
public class InvalidMapFileException extends Exception {
    public InvalidMapFileException(String fileName) {
        super(fileName + "is invalid");
    }
}
