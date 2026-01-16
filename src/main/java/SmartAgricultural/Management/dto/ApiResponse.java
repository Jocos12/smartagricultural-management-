package SmartAgricultural.Management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Generic API Response wrapper for all REST API responses
 * Provides consistent response format across the application
 *
 * @param <T> The type of data being returned
 * @author Smart Agricultural Management System
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Generic API response wrapper")
public class ApiResponse<T> {

    @Schema(description = "Indicates if the request was successful", example = "true")
    private boolean success;

    @Schema(description = "Human readable message describing the result", example = "Operation completed successfully")
    private String message;

    @Schema(description = "The actual data payload")
    private T data;

    @Schema(description = "Timestamp when the response was generated", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Error details if the request failed", example = "Validation failed for field 'name'")
    private String error;

    @Schema(description = "HTTP status code", example = "200")
    private Integer statusCode;

    /**
     * Default constructor
     */
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor with basic parameters
     *
     * @param success Indicates if the operation was successful
     * @param message Human readable message
     * @param data    The response data
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor with all parameters
     *
     * @param success    Indicates if the operation was successful
     * @param message    Human readable message
     * @param data       The response data
     * @param statusCode HTTP status code
     */
    public ApiResponse(boolean success, String message, T data, Integer statusCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Creates a successful response with data
     *
     * @param message Success message
     * @param data    Response data
     * @param <T>     Data type
     * @return ApiResponse with success=true
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, 200);
    }

    /**
     * Creates a successful response without data
     *
     * @param message Success message
     * @param <T>     Data type
     * @return ApiResponse with success=true and null data
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, 200);
    }

    /**
     * Creates an error response
     *
     * @param message Error message
     * @param error   Detailed error description
     * @param <T>     Data type
     * @return ApiResponse with success=false
     */
    public static <T> ApiResponse<T> error(String message, String error) {
        ApiResponse<T> response = new ApiResponse<>(false, message, null, 400);
        response.setError(error);
        return response;
    }

    /**
     * Creates an error response with custom status code
     *
     * @param message    Error message
     * @param error      Detailed error description
     * @param statusCode HTTP status code
     * @param <T>        Data type
     * @return ApiResponse with success=false
     */
    public static <T> ApiResponse<T> error(String message, String error, Integer statusCode) {
        ApiResponse<T> response = new ApiResponse<>(false, message, null, statusCode);
        response.setError(error);
        return response;
    }

    /**
     * Creates a not found error response
     *
     * @param message Error message
     * @param <T>     Data type
     * @return ApiResponse with 404 status
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(false, message, null, 404);
    }

    /**
     * Creates an unauthorized error response
     *
     * @param message Error message
     * @param <T>     Data type
     * @return ApiResponse with 401 status
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(false, message, null, 401);
    }

    /**
     * Creates a forbidden error response
     *
     * @param message Error message
     * @param <T>     Data type
     * @return ApiResponse with 403 status
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(false, message, null, 403);
    }

    /**
     * Creates an internal server error response
     *
     * @param message Error message
     * @param <T>     Data type
     * @return ApiResponse with 500 status
     */
    public static <T> ApiResponse<T> internalServerError(String message) {
        return new ApiResponse<>(false, message, null, 500);
    }

    // ==================== GETTERS AND SETTERS ====================

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Checks if this is an error response
     *
     * @return true if success is false
     */
    public boolean isError() {
        return !success;
    }

    /**
     * Checks if response has data
     *
     * @return true if data is not null
     */
    public boolean hasData() {
        return data != null;
    }

    /**
     * Checks if response has error details
     *
     * @return true if error field is not null and not empty
     */
    public boolean hasErrorDetails() {
        return error != null && !error.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                ", error='" + error + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApiResponse<?> that = (ApiResponse<?>) o;

        if (success != that.success) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;
        if (error != null ? !error.equals(that.error) : that.error != null) return false;
        return statusCode != null ? statusCode.equals(that.statusCode) : that.statusCode == null;
    }

    @Override
    public int hashCode() {
        int result = (success ? 1 : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (error != null ? error.hashCode() : 0);
        result = 31 * result + (statusCode != null ? statusCode.hashCode() : 0);
        return result;
    }
}