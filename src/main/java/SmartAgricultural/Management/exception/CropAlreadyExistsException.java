
// CropAlreadyExistsException.java
package SmartAgricultural.Management.exception;

/**
 * Exception levée quand une culture existe déjà
 */
public class CropAlreadyExistsException extends RuntimeException {

    public CropAlreadyExistsException(String message) {
        super(message);
    }

    public CropAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    // Méthode statique pour créer une exception avec un ID
    public static CropAlreadyExistsException withId(String cropId) {
        return new CropAlreadyExistsException("Une culture avec l'ID '" + cropId + "' existe déjà");
    }

    // Méthode statique pour créer une exception avec un nom
    public static CropAlreadyExistsException withName(String cropName) {
        return new CropAlreadyExistsException("Une culture avec le nom '" + cropName + "' existe déjà");
    }
}
