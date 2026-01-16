package SmartAgricultural.Management.dto;

import SmartAgricultural.Management.Model.Inventory.FacilityType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for creating a new inventory item
 * Contains all necessary information to create an inventory entry
 *
 * @author Smart Agricultural Management System
 * @version 1.0
 */
@Schema(description = "Request object for creating new inventory")
public class CreateInventoryRequest {

    @Schema(description = "ID of the crop being stored", example = "CROP001", required = true)
    @NotBlank(message = "Crop ID is required")
    @Size(max = 20, message = "Crop ID must not exceed 20 characters")
    private String cropId;

    @Schema(description = "ID of the farmer who owns the inventory", example = "FARMER001")
    @Size(max = 20, message = "Farmer ID must not exceed 20 characters")
    private String farmerId;

    @Schema(description = "Type of storage facility", required = true)
    @NotNull(message = "Facility type is required")
    private FacilityType facilityType;

    @Schema(description = "Physical storage location", example = "Warehouse A, Section 3, Row 5", required = true)
    @NotBlank(message = "Storage location is required")
    @Size(max = 255, message = "Storage location must not exceed 255 characters")
    private String storageLocation;

    @Schema(description = "Name of the storage facility", example = "Central Warehouse Kigali")
    @Size(max = 150, message = "Facility name must not exceed 150 characters")
    private String facilityName;

    @Schema(description = "Total storage capacity in tonnes", example = "1000.00")
    @DecimalMin(value = "0.0", message = "Storage capacity must be positive")
    @Digits(integer = 8, fraction = 2, message = "Storage capacity format is invalid")
    private BigDecimal storageCapacity;

    @Schema(description = "Current quantity being stored", example = "250.50", required = true)
    @NotNull(message = "Current quantity is required")
    @DecimalMin(value = "0.01", message = "Current quantity must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Current quantity format is invalid")
    private BigDecimal currentQuantity;

    @Schema(description = "Unit of measurement", example = "KG")
    @Size(max = 20, message = "Unit must not exceed 20 characters")
    private String unit = "KG";

    @Schema(description = "Quality grade of the inventory", example = "Grade A", required = true)
    @NotBlank(message = "Quality grade is required")
    @Size(max = 50, message = "Quality grade must not exceed 50 characters")
    private String qualityGrade;

    @Schema(description = "Date when the crop was harvested", example = "2024-01-10")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate harvestDate;

    @Schema(description = "Expected shelf life in days", example = "90")
    @Min(value = 1, message = "Expected shelf life must be at least 1 day")
    @Max(value = 3650, message = "Expected shelf life cannot exceed 10 years")
    private Integer expectedShelfLifeDays;

    @Schema(description = "Type of packaging used", example = "Jute bags")
    @Size(max = 100, message = "Packaging type must not exceed 100 characters")
    private String packagingType;

    @Schema(description = "Market value per unit", example = "150.00")
    @DecimalMin(value = "0.0", message = "Market value per unit must be positive")
    @Digits(integer = 6, fraction = 2, message = "Market value per unit format is invalid")
    private BigDecimal marketValuePerUnit;

    @Schema(description = "Purchase price per unit", example = "120.00")
    @DecimalMin(value = "0.0", message = "Purchase price per unit must be positive")
    @Digits(integer = 6, fraction = 2, message = "Purchase price per unit format is invalid")
    private BigDecimal purchasePricePerUnit;

    @Schema(description = "Certifications held", example = "Organic, Fair Trade")
    @Size(max = 255, message = "Certifications must not exceed 255 characters")
    private String certifications;

    @Schema(description = "Whether the inventory is organic certified", example = "true")
    private Boolean organicCertified = false;

    @Schema(description = "Whether the inventory is fair trade certified", example = "true")
    private Boolean fairTradeCertified = false;

    @Schema(description = "Storage conditions in JSON format", example = "{\"temperature\": \"15-20°C\", \"humidity\": \"60-70%\"}")
    @Size(max = 2000, message = "Storage conditions must not exceed 2000 characters")
    private String storageConditions;

    @Schema(description = "Special handling instructions", example = "Keep dry and away from direct sunlight")
    @Size(max = 2000, message = "Handling instructions must not exceed 2000 characters")
    private String handlingInstructions;

    @Schema(description = "Minimum stock level for reordering", example = "50.00")
    @DecimalMin(value = "0.0", message = "Minimum stock level must be positive")
    @Digits(integer = 6, fraction = 2, message = "Minimum stock level format is invalid")
    private BigDecimal minimumStockLevel;

    @Schema(description = "Maximum stock level", example = "500.00")
    @DecimalMin(value = "0.0", message = "Maximum stock level must be positive")
    @Digits(integer = 8, fraction = 2, message = "Maximum stock level format is invalid")
    private BigDecimal maximumStockLevel;

    // ==================== CONSTRUCTORS ====================

    /**
     * Default constructor
     */
    public CreateInventoryRequest() {
    }

    /**
     * Constructor with essential fields
     */
    public CreateInventoryRequest(String cropId, FacilityType facilityType,
                                  String storageLocation, BigDecimal currentQuantity,
                                  String qualityGrade) {
        this.cropId = cropId;
        this.facilityType = facilityType;
        this.storageLocation = storageLocation;
        this.currentQuantity = currentQuantity;
        this.qualityGrade = qualityGrade;
    }

    // ==================== GETTERS AND SETTERS ====================

    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    public String getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
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

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public BigDecimal getStorageCapacity() {
        return storageCapacity;
    }

    public void setStorageCapacity(BigDecimal storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    public BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(BigDecimal currentQuantity) {
        this.currentQuantity = currentQuantity;
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

    public LocalDate getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(LocalDate harvestDate) {
        this.harvestDate = harvestDate;
    }

    public Integer getExpectedShelfLifeDays() {
        return expectedShelfLifeDays;
    }

    public void setExpectedShelfLifeDays(Integer expectedShelfLifeDays) {
        this.expectedShelfLifeDays = expectedShelfLifeDays;
    }

    public String getPackagingType() {
        return packagingType;
    }

    public void setPackagingType(String packagingType) {
        this.packagingType = packagingType;
    }

    public BigDecimal getMarketValuePerUnit() {
        return marketValuePerUnit;
    }

    public void setMarketValuePerUnit(BigDecimal marketValuePerUnit) {
        this.marketValuePerUnit = marketValuePerUnit;
    }

    public BigDecimal getPurchasePricePerUnit() {
        return purchasePricePerUnit;
    }

    public void setPurchasePricePerUnit(BigDecimal purchasePricePerUnit) {
        this.purchasePricePerUnit = purchasePricePerUnit;
    }

    public String getCertifications() {
        return certifications;
    }

    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    public Boolean getOrganicCertified() {
        return organicCertified;
    }

    public void setOrganicCertified(Boolean organicCertified) {
        this.organicCertified = organicCertified;
    }

    public Boolean getFairTradeCertified() {
        return fairTradeCertified;
    }

    public void setFairTradeCertified(Boolean fairTradeCertified) {
        this.fairTradeCertified = fairTradeCertified;
    }

    public String getStorageConditions() {
        return storageConditions;
    }

    public void setStorageConditions(String storageConditions) {
        this.storageConditions = storageConditions;
    }

    public String getHandlingInstructions() {
        return handlingInstructions;
    }

    public void setHandlingInstructions(String handlingInstructions) {
        this.handlingInstructions = handlingInstructions;
    }

    public BigDecimal getMinimumStockLevel() {
        return minimumStockLevel;
    }

    public void setMinimumStockLevel(BigDecimal minimumStockLevel) {
        this.minimumStockLevel = minimumStockLevel;
    }

    public BigDecimal getMaximumStockLevel() {
        return maximumStockLevel;
    }

    public void setMaximumStockLevel(BigDecimal maximumStockLevel) {
        this.maximumStockLevel = maximumStockLevel;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Validates that maximum stock level is greater than minimum stock level
     */
    public boolean isStockLevelValid() {
        if (minimumStockLevel != null && maximumStockLevel != null) {
            return maximumStockLevel.compareTo(minimumStockLevel) > 0;
        }
        return true;
    }

    /**
     * Checks if the request contains sustainability certifications
     */
    public boolean hasSustainabilityCertifications() {
        return Boolean.TRUE.equals(organicCertified) ||
                Boolean.TRUE.equals(fairTradeCertified) ||
                (certifications != null && !certifications.trim().isEmpty());
    }

    @Override
    public String toString() {
        return "CreateInventoryRequest{" +
                "cropId='" + cropId + '\'' +
                ", facilityType=" + facilityType +
                ", storageLocation='" + storageLocation + '\'' +
                ", currentQuantity=" + currentQuantity +
                ", qualityGrade='" + qualityGrade + '\'' +
                ", organicCertified=" + organicCertified +
                ", fairTradeCertified=" + fairTradeCertified +
                '}';
    }
}