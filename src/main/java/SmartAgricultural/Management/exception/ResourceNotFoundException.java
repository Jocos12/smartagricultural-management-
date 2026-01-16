package SmartAgricultural.Management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found in the system.
 * This exception is automatically mapped to HTTP 404 (Not Found) status code.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    /**
     * Constructor with custom message
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for resource not found with specific field details
     *
     * @param resourceName the name of the resource (e.g., "User", "Staff")
     * @param fieldName the name of the field used for search (e.g., "id", "email")
     * @param fieldValue the value of the field that was searched for
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Constructor for resource not found with specific field details and cause
     *
     * @param resourceName the name of the resource (e.g., "User", "Staff")
     * @param fieldName the name of the field used for search (e.g., "id", "email")
     * @param fieldValue the value of the field that was searched for
     * @param cause the cause of this exception
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue, Throwable cause) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue), cause);
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Get the resource name
     *
     * @return the resource name
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Get the field name
     *
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Get the field value
     *
     * @return the field value
     */
    public Object getFieldValue() {
        return fieldValue;
    }

    /**
     * Static factory method for creating user not found exception
     *
     * @param fieldName the field name used for search
     * @param fieldValue the field value that was searched for
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException userNotFound(String fieldName, Object fieldValue) {
        return new ResourceNotFoundException("User", fieldName, fieldValue);
    }

    /**
     * Static factory method for creating user not found by ID exception
     *
     * @param id the user ID that was not found
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException userNotFoundById(String id) {
        return new ResourceNotFoundException("User", "id", id);
    }

    /**
     * Static factory method for creating user not found by email exception
     *
     * @param email the email that was not found
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException userNotFoundByEmail(String email) {
        return new ResourceNotFoundException("User", "email", email);
    }

    /**
     * Static factory method for creating user not found by username exception
     *
     * @param username the username that was not found
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException userNotFoundByUsername(String username) {
        return new ResourceNotFoundException("User", "username", username);
    }

    /**
     * Static factory method for creating staff not found exception
     *
     * @param fieldName the field name used for search
     * @param fieldValue the field value that was searched for
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException staffNotFound(String fieldName, Object fieldValue) {
        return new ResourceNotFoundException("Staff", fieldName, fieldValue);
    }

    /**
     * Static factory method for creating staff not found by ID exception
     *
     * @param id the staff ID that was not found
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException staffNotFoundById(Long id) {
        return new ResourceNotFoundException("Staff", "id", id);
    }

    /**
     * Static factory method for creating staff not found by email exception
     *
     * @param email the email that was not found
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException staffNotFoundByEmail(String email) {
        return new ResourceNotFoundException("Staff", "email", email);
    }

    /**
     * Static factory method for creating generic resource not found exception
     *
     * @param resourceName the name of the resource
     * @param id the ID that was not found
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException resourceNotFoundById(String resourceName, Object id) {
        return new ResourceNotFoundException(resourceName, "id", id);
    }

    /**
     * Static factory method for creating custom resource not found exception
     *
     * @param message the custom error message
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException withMessage(String message) {
        return new ResourceNotFoundException(message);
    }
}