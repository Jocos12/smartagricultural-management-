// CropValidationException.java
package SmartAgricultural.Management.exception;

/**
 * Exception levée lors d'erreurs de validation des cultures
 */
public class CropValidationException extends RuntimeException {

    public CropValidationException(String message) {
        super(message);
    }

    public CropValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}