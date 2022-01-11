package Exceptions;

public class ValidateException extends Exception {
    /**
     * @param message specific message regarding the an invalid element introduced in the aplication
     *                (not really introduced, but there was an attempt)
     */
    public ValidateException(String message) {
        super(message);
    }
}
