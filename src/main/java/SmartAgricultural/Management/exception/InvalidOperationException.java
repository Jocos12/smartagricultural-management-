package SmartAgricultural.Management.exception;

/**
 * Custom exception for invalid operations in the Smart Agricultural Management System
 */
public class InvalidOperationException extends RuntimeException {

    /**
     * Constructs a new InvalidOperationException with null as its detail message.
     */
    public InvalidOperationException() {
        super();
    }

    /**
     * Constructs a new InvalidOperationException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidOperationException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidOperationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidOperationException with the specified cause.
     *
     * @param cause the cause
     */
    public InvalidOperationException(Throwable cause) {
        super(cause);
    }
}