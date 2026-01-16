// Create this file: SmartAgricultural/Management/exception/DuplicateResourceException.java
package SmartAgricultural.Management.exception;

/**
 * Exception thrown when attempting to create a resource that already exists
 *
 * @author SmartAgricultural Management System
 * @version 1.0
 * @since 2024-01-01
 */
public class DuplicateResourceException extends RuntimeException {

    private String resourceType;
    private String resourceId;
    private String conflictingField;

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateResourceException(String resourceType, String resourceId, String message) {
        super(message);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public DuplicateResourceException(String resourceType, String resourceId, String conflictingField, String message) {
        super(message);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.conflictingField = conflictingField;
    }

    public DuplicateResourceException(String resourceType, String resourceId, String message, Throwable cause) {
        super(message, cause);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getConflictingField() {
        return conflictingField;
    }

    @Override
    public String toString() {
        if (resourceType != null && resourceId != null) {
            return String.format("DuplicateResourceException{resourceType='%s', resourceId='%s', conflictingField='%s', message='%s'}",
                    resourceType, resourceId, conflictingField, getMessage());
        }
        return super.toString();
    }
}