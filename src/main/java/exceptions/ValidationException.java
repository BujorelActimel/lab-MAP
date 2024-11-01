package exceptions;

/**
 * Custom exception for validation errors
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}