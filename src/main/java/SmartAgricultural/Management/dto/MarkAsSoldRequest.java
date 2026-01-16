package SmartAgricultural.Management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Request DTO for marking inventory as sold
 *
 * @author Smart Agricultural Management System
 * @version 1.0
 */
@Schema(description = "Request object for marking inventory as sold")
public class MarkAsSoldRequest {

    @Schema(description = "Quantity sold", example = "100.00", required = true)
    @NotNull(message = "Sold quantity is required")
    @DecimalMin(value = "0.01", message = "Sold quantity must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Sold quantity format is invalid")
    private BigDecimal soldQuantity;

    @Schema(description = "Price per unit at which it was sold", example = "175.00", required = true)
    @NotNull(message = "Sold price is required")
    @DecimalMin(value = "0.01", message = "Sold price must be greater than 0")
    @Digits(integer = 6, fraction = 2, message = "Sold price format is invalid")
    private BigDecimal soldPrice;

    @Schema(description = "ID of the buyer", example = "BUYER001")
    private String buyerId;

    @Schema(description = "Transaction reference", example = "TXN-2024-001")
    private String transactionReference;

    @Schema(description = "Payment method used", example = "Bank Transfer")
    private String paymentMethod;

    @Schema(description = "Additional notes about the sale", example = "Bulk sale with discount applied")
    private String notes;

    // ==================== CONSTRUCTORS ====================

    public MarkAsSoldRequest() {
    }

    public MarkAsSoldRequest(BigDecimal soldQuantity, BigDecimal soldPrice) {
        this.soldQuantity = soldQuantity;
        this.soldPrice = soldPrice;
    }

    public MarkAsSoldRequest(BigDecimal soldQuantity, BigDecimal soldPrice, String buyerId) {
        this.soldQuantity = soldQuantity;
        this.soldPrice = soldPrice;
        this.buyerId = buyerId;
    }

    // ==================== GETTERS AND SETTERS ====================

    public BigDecimal getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(BigDecimal soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public BigDecimal getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(BigDecimal soldPrice) {
        this.soldPrice = soldPrice;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Calculate total sale amount
     */
    public BigDecimal getTotalSaleAmount() {
        if (soldQuantity != null && soldPrice != null) {
            return soldQuantity.multiply(soldPrice);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "MarkAsSoldRequest{" +
                "soldQuantity=" + soldQuantity +
                ", soldPrice=" + soldPrice +
                ", buyerId='" + buyerId + '\'' +
                ", transactionReference='" + transactionReference + '\'' +
                ", totalAmount=" + getTotalSaleAmount() +
                '}';
    }
}