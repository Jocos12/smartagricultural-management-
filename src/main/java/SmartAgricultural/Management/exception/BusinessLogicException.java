package SmartAgricultural.Management.exception;

/**
 * Exception thrown when business logic validation fails
 * Used for domain-specific validation errors and business rule violations
 *
 * @author Smart Agricultural Management System
 * @version 1.0
 */
public class BusinessLogicException extends RuntimeException {

    private String errorCode;
    private Object details;

    /**
     * Default constructor
     */
    public BusinessLogicException() {
        super();
    }

    /**
     * Constructor with message
     *
     * @param message Error message
     */
    public BusinessLogicException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     *
     * @param message Error message
     * @param cause Root cause
     */
    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with message and error code
     *
     * @param message Error message
     * @param errorCode Application-specific error code
     */
    public BusinessLogicException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor with message, error code, and additional details
     *
     * @param message Error message
     * @param errorCode Application-specific error code
     * @param details Additional error details
     */
    public BusinessLogicException(String message, String errorCode, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    /**
     * Get the error code
     *
     * @return Error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Set the error code
     *
     * @param errorCode Error code to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Get additional error details
     *
     * @return Error details
     */
    public Object getDetails() {
        return details;
    }

    /**
     * Set additional error details
     *
     * @param details Error details to set
     */
    public void setDetails(Object details) {
        this.details = details;
    }
}