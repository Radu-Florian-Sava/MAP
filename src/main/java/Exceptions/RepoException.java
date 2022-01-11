package Exceptions;

public class RepoException extends Exception {
    /**
     * @param message specific message regarding an element related to the Infrastructure layer
     *                (repository problems)
     */
    public RepoException(String message) {
        super(message);
    }
}
