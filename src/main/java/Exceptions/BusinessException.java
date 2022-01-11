package Exceptions;


public class BusinessException extends Exception {
    /**
     * @param message specific message regarding element id of an element related to the Business layer
     */
    public BusinessException(String message) {
        super(message);
    }
}

