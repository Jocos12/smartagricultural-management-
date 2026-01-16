package SmartAgricultural.Management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EnvironmentalDataException extends RuntimeException {

    private String errorCode;
    private HttpStatus httpStatus;

    public EnvironmentalDataException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public EnvironmentalDataException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public EnvironmentalDataException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public EnvironmentalDataException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public EnvironmentalDataException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public EnvironmentalDataException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    // Static factory methods for common exceptions
    public static EnvironmentalDataException notFound(String id) {
        return new EnvironmentalDataException(
                "Environmental data not found with id: " + id,
                "ENV_DATA_NOT_FOUND",
                HttpStatus.NOT_FOUND
        );
    }

    public static EnvironmentalDataException notFoundByMonitoringCode(String monitoringCode) {
        return new EnvironmentalDataException(
                "Environmental data not found with monitoring code: " + monitoringCode,
                "ENV_DATA_NOT_FOUND_BY_CODE",
                HttpStatus.NOT_FOUND
        );
    }

    public static EnvironmentalDataException invalidData(String message) {
        return new EnvironmentalDataException(
                "Invalid environmental data: " + message,
                "ENV_DATA_INVALID",
                HttpStatus.BAD_REQUEST
        );
    }

    public static EnvironmentalDataException validationFailed(String message) {
        return new EnvironmentalDataException(
                "Environmental data validation failed: " + message,
                "ENV_DATA_VALIDATION_FAILED",
                HttpStatus.BAD_REQUEST
        );
    }

    public static EnvironmentalDataException duplicateMonitoringCode(String monitoringCode) {
        return new EnvironmentalDataException(
                "Environmental data with monitoring code already exists: " + monitoringCode,
                "ENV_DATA_DUPLICATE_CODE",
                HttpStatus.CONFLICT
        );
    }

    public static EnvironmentalDataException unauthorized(String message) {
        return new EnvironmentalDataException(
                "Unauthorized access: " + message,
                "ENV_DATA_UNAUTHORIZED",
                HttpStatus.UNAUTHORIZED
        );
    }

    public static EnvironmentalDataException forbidden(String message) {
        return new EnvironmentalDataException(
                "Access forbidden: " + message,
                "ENV_DATA_FORBIDDEN",
                HttpStatus.FORBIDDEN
        );
    }

    public static EnvironmentalDataException dataProcessingError(String message, Throwable cause) {
        return new EnvironmentalDataException(
                "Environmental data processing error: " + message,
                "ENV_DATA_PROCESSING_ERROR",
                cause
        );
    }

    public static EnvironmentalDataException serviceUnavailable(String message) {
        return new EnvironmentalDataException(
                "Environmental data service unavailable: " + message,
                "ENV_DATA_SERVICE_UNAVAILABLE",
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }
}