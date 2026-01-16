package SmartAgricultural.Management.dto;

import SmartAgricultural.Management.Model.Inventory.FacilityType;
import SmartAgricultural.Management.Model.Inventory.InventoryStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Summary Data Transfer Object for Inventory entity
 * Contains essential inventory information for listing views
 *
 * @author Smart Agricultural Management System
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Summary inventory information for listings")
public class InventorySummaryDTO {

    @Schema(description = "Unique identifier for the inventory", example = "INV123456789ABC")
    private String id;

    @Schema(description = "Human-readable inventory code", example = "STOCK2401151234ABC")
    private String inventoryCode;

    @Schema(description = "ID of the associated crop", example = "CROP001")
    private String cropId;

    @Schema(description = "Type of storage facility")
    private FacilityType facilityType;

    @Schema(description = "Physical storage location", example = "Warehouse A, Section 3")
    private String storageLocation;

    @Schema(description = "Current quantity in storage", example = "250.50")
    private BigDecimal currentQuantity;

    @Schema(description = "Available quantity for sale", example = "200.50")
    private BigDecimal availableQuantity;

    @Schema(description = "Unit of measurement", example = "KG")
    private String unit;

    @Schema(description = "Quality grade of the inventory", example = "Grade A")
    private String qualityGrade;

    @Schema(description = "Current status of the inventory")
    private InventoryStatus status;

    @Schema(description = "Total market value", example = "37575.00")
    private BigDecimal totalMarketValue;

    @Schema(description = "Expected expiry date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    @Schema(description = "Date when inventory was stored")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate storageDate;

    @Schema(description = "Formatted quantity with unit", example = "250.50 KG")
    private String formattedQuantity;

    @Schema(description = "Formatted market value", example = "RWF 37,575.00")
    private String formattedMarketValue;

    // ==================== CONSTRUCTORS ====================

    /**
     * Default constructor
     */
    public InventorySummaryDTO() {
    }

    /**
     * Constructor with essential fields
     */
    public InventorySummaryDTO(String id, String inventoryCode, String cropId,
                               BigDecimal currentQuantity, String qualityGrade,
                               InventoryStatus status) {
        this.id = id;
        this.inventoryCode = inventoryCode;
        this.cropId = cropId;
        this.currentQuantity = currentQuantity;
        this.qualityGrade = qualityGrade;
        this.status = status;
    }

    // ==================== GETTERS AND SETTERS ====================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    public FacilityType getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(FacilityType facilityType) {
        this.facilityType = facilityType;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(BigDecimal currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(BigDecimal availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getQualityGrade() {
        return qualityGrade;
    }

    public void setQualityGrade(String qualityGrade) {
        this.qualityGrade = qualityGrade;
    }

    public InventoryStatus getStatus() {
        return status;
    }

    public void setStatus(InventoryStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalMarketValue() {
        return totalMarketValue;
    }

    public void setTotalMarketValue(BigDecimal totalMarketValue) {
        this.totalMarketValue = totalMarketValue;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDate getStorageDate() {
        return storageDate;
    }

    public void setStorageDate(LocalDate storageDate) {
        this.storageDate = storageDate;
    }

    public String getFormattedQuantity() {
        return formattedQuantity;
    }

    public void setFormattedQuantity(String formattedQuantity) {
        this.formattedQuantity = formattedQuantity;
    }

    public String getFormattedMarketValue() {
        return formattedMarketValue;
    }

    public void setFormattedMarketValue(String formattedMarketValue) {
        this.formattedMarketValue = formattedMarketValue;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Checks if inventory is expiring soon (within 7 days)
     */
    public boolean isExpiringSoon() {
        if (expiryDate == null) return false;
        return expiryDate.isBefore(LocalDate.now().plusDays(7));
    }

    /**
     * Checks if inventory is available for sale
     */
    public boolean isAvailable() {
        return status == InventoryStatus.AVAILABLE &&
                availableQuantity != null &&
                availableQuantity.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Gets a short description of the inventory
     */
    public String getShortDescription() {
        return String.format("%s - %s (%s)",
                inventoryCode,
                qualityGrade,
                status != null ? status.getDisplayName() : "Unknown");
    }

    @Override
    public String toString() {
        return "InventorySummaryDTO{" +
                "id='" + id + '\'' +
                ", inventoryCode='" + inventoryCode + '\'' +
                ", cropId='" + cropId + '\'' +
                ", currentQuantity=" + currentQuantity +
                ", qualityGrade='" + qualityGrade + '\'' +
                ", status=" + status +
                ", totalMarketValue=" + totalMarketValue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InventorySummaryDTO that = (InventorySummaryDTO) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}