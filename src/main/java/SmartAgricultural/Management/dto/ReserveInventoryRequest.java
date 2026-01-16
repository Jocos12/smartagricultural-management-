package SmartAgricultural.Management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Request DTO for reserving inventory quantities
 *
 * @author Smart Agricultural Management System
 * @version 1.0
 */
@Schema(description = "Request object for reserving inventory")
public class ReserveInventoryRequest {

    @Schema(description = "Quantity to reserve", example = "50.00", required = true)
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Quantity format is invalid")
    private BigDecimal quantity;

    @Schema(description = "ID of the buyer making the reservation", example = "BUYER001", required = true)
    @NotBlank(message = "Buyer ID is required")
    private String buyerId;

    @Schema(description = "Reason for reservation", example = "Bulk purchase for restaurant chain")
    private String reason;

    @Schema(description = "Expected pickup/delivery date", example = "2024-02-15")
    private String expectedDate;

    // ==================== CONSTRUCTORS ====================

    public ReserveInventoryRequest() {
    }

    public ReserveInventoryRequest(BigDecimal quantity, String buyerId) {
        this.quantity = quantity;
        this.buyerId = buyerId;
    }

    // ==================== GETTERS AND SETTERS ====================

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(String expectedDate) {
        this.expectedDate = expectedDate;
    }

    @Override
    public String toString() {
        return "ReserveInventoryRequest{" +
                "quantity=" + quantity +
                ", buyerId='" + buyerId + '\'' +
                ", reason='" + reason + '\'' +
                ", expectedDate='" + expectedDate + '\'' +
                '}';
    }
}