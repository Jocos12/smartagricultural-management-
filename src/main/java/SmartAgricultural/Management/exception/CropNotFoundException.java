// CropNotFoundException.java
package SmartAgricultural.Management.exception;

/**
 * Exception levée quand une culture n'est pas trouvée
 */
public class CropNotFoundException extends RuntimeException {

    public CropNotFoundException(String message) {
        super(message);
    }

    public CropNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    // Méthode statique pour créer une exception avec un ID
    public static CropNotFoundException withId(String cropId) {
        return new CropNotFoundException("Culture avec l'ID '" + cropId + "' non trouvée");
    }

    // Méthode statique pour créer une exception avec un nom
    public static CropNotFoundException withName(String cropName) {
        return new CropNotFoundException("Culture avec le nom '" + cropName + "' non trouvée");
    }
}
