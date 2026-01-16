package SmartAgricultural.Management.exception;

import SmartAgricultural.Management.dto.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Gestionnaire global des exceptions pour l'application
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gérer les exceptions de culture non trouvée
     */
    @ExceptionHandler(CropNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCropNotFoundException(CropNotFoundException ex) {
        ApiResponse<Void> response = new ApiResponse<>(false, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Gérer les exceptions de culture déjà existante
     */
    @ExceptionHandler(CropAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleCropAlreadyExistsException(CropAlreadyExistsException ex) {
        ApiResponse<Void> response = new ApiResponse<>(false, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Gérer les exceptions de validation des cultures
     */
    @ExceptionHandler(CropValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleCropValidationException(CropValidationException ex) {
        ApiResponse<Void> response = new ApiResponse<>(false, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Gérer les erreurs de validation des arguments de méthode
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = new ApiResponse<>(false,
                "Erreurs de validation", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Gérer les violations de contraintes
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

        for (ConstraintViolation<?> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        }

        ApiResponse<Map<String, String>> response = new ApiResponse<>(false,
                "Violations de contraintes", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Gérer les erreurs de type d'argument
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        String message = String.format("Le paramètre '%s' doit être de type '%s'",
                ex.getName(), ex.getRequiredType().getSimpleName());

        ApiResponse<Void> response = new ApiResponse<>(false, message, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Gérer les violations d'intégrité des données
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {
        String message = "Violation de l'intégrité des données";
        if (ex.getMessage().contains("Duplicate entry")) {
            message = "Cette entrée existe déjà dans la base de données";
        }

        ApiResponse<Void> response = new ApiResponse<>(false, message, null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Gérer les arguments illégaux
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        ApiResponse<Void> response = new ApiResponse<>(false, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Gérer toutes les autres exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        String message = "Une erreur interne s'est produite";

        // En mode développement, on peut inclure plus de détails
        // if (isDevelopmentMode()) {
        //     message = ex.getMessage();
        // }

        ApiResponse<Void> response = new ApiResponse<>(false, message, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}