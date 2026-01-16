// Create this file: SmartAgricultural/Management/exception/ValidationException.java
package SmartAgricultural.Management.exception;

/**
 * Exception thrown when validation fails in the Smart Agricultural Management System
 *
 * @author SmartAgricultural Management System
 * @version 1.0
 * @since 2024-01-01
 */
public class ValidationException extends RuntimeException {

    private String field;
    private Object rejectedValue;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(String field, Object rejectedValue, String message) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    public ValidationException(String field, Object rejectedValue, String message, Throwable cause) {
        super(message, cause);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    public String getField() {
        return field;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    @Override
    public String toString() {
        if (field != null) {
            return String.format("ValidationException{field='%s', rejectedValue='%s', message='%s'}",
                    field, rejectedValue, getMessage());
        }
        return super.toString();
    }
}