package SmartAgricultural.Management.dto;

import SmartAgricultural.Management.Model.Inventory.FacilityType;
import SmartAgricultural.Management.Model.Inventory.InventoryStatus;
import SmartAgricultural.Management.Model.Inventory.PestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Request DTO for updating an existing inventory item
 * Contains fields that can be modified after inventory creation
 *
 * @author Smart Agricultural Management System
 * @version 1.0
 */
@Schema(description = "Request object for updating existing inventory")
public class UpdateInventoryRequest {

    @Schema(description = "Updated current quantity", example = "275.50")
    @DecimalMin(value = "0.0", message = "Current quantity must be positive")
    @Digits(integer = 8, fraction = 2, message = "Current quantity format is invalid")
    private BigDecimal currentQuantity;

    @Schema(description = "Updated quality grade", example = "Grade A+")
    @Size(max = 50, message = "Quality grade must not exceed 50 characters")
    private String qualityGrade;

    @Schema(description = "Updated market value per unit", example = "165.00")
    @DecimalMin(value = "0.0", message = "Market value per unit must be positive")
    @Digits(integer = 6, fraction = 2, message = "Market value per unit format is invalid")
    private BigDecimal marketValuePerUnit;

    @Schema(description = "Updated storage location", example = "Warehouse B, Section 1, Row 3")
    @Size(max = 255, message = "Storage location must not exceed 255 characters")
    private String storageLocation;

    @Schema(description = "Updated facility type")
    private FacilityType facilityType;

    @Schema(description = "Updated inventory status")
    private InventoryStatus status;

    @Schema(description = "Updated pest status")
    private PestStatus pestStatus;

    @Schema(description = "Updated moisture content percentage", example = "12.5")
    @DecimalMin(value = "0.0", message = "Moisture content must be positive")
    @DecimalMax(value = "100.0", message = "Moisture content must not exceed 100%")
    @Digits(integer = 2, fraction = 2, message = "Moisture content format is invalid")
    private BigDecimal moistureContent;

    @Schema(description = "Updated storage conditions", example = "{\"temperature\": \"18-22°C\", \"humidity\": \"65-70%\"}")
    @Size(max = 2000, message = "Storage conditions must not exceed 2000 characters")
    private String storageConditions;

    @Schema(description = "Updated handling instructions", example = "Handle with care, avoid moisture exposure")
    @Size(max = 2000, message = "Handling instructions must not exceed 2000 characters")
    private String handlingInstructions;

    @Schema(description = "Updated minimum stock level", example = "75.00")
    @DecimalMin(value = "0.0", message = "Minimum stock level must be positive")
    @Digits(integer = 6, fraction = 2, message = "Minimum stock level format is invalid")
    private BigDecimal minimumStockLevel;

    @Schema(description = "Updated maximum stock level", example = "750.00")
    @DecimalMin(value = "0.0", message = "Maximum stock level must be positive")
    @Digits(integer = 8, fraction = 2, message = "Maximum stock level format is invalid")
    private BigDecimal maximumStockLevel;

    @Schema(description = "Updated certifications", example = "Organic, Fair Trade, GlobalGAP")
    @Size(max = 255, message = "Certifications must not exceed 255 characters")
    private String certifications;

    @Schema(description = "Updated organic certification status", example = "true")
    private Boolean organicCertified;

    @Schema(description = "Updated fair trade certification status", example = "true")
    private Boolean fairTradeCertified;

    // ==================== CONSTRUCTORS ====================

    /**
     * Default constructor
     */
    public UpdateInventoryRequest() {
    }

    // ==================== GETTERS AND SETTERS ====================

    public BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(BigDecimal currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public String getQualityGrade() {
        return qualityGrade;
    }

    public void setQualityGrade(String qualityGrade) {
        this.qualityGrade = qualityGrade;
    }

    public BigDecimal getMarketValuePerUnit() {
        return marketValuePerUnit;
    }

    public void setMarketValuePerUnit(BigDecimal marketValuePerUnit) {
        this.marketValuePerUnit = marketValuePerUnit;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public FacilityType getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(FacilityType facilityType) {
        this.facilityType = facilityType;
    }

    public InventoryStatus getStatus() {
        return status;
    }

    public void setStatus(InventoryStatus status) {
        this.status = status;
    }

    public PestStatus getPestStatus() {
        return pestStatus;
    }

    public void setPestStatus(PestStatus pestStatus) {
        this.pestStatus = pestStatus;
    }

    public BigDecimal getMoistureContent() {
        return moistureContent;
    }

    public void setMoistureContent(BigDecimal moistureContent) {
        this.moistureContent = moistureContent;
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

    // ==================== UTILITY METHODS ====================

    /**
     * Checks if any field has been provided for update
     */
    public boolean hasUpdates() {
        return currentQuantity != null ||
                qualityGrade != null ||
                marketValuePerUnit != null ||
                storageLocation != null ||
                facilityType != null ||
                status != null ||
                pestStatus != null ||
                moistureContent != null ||
                storageConditions != null ||
                handlingInstructions != null ||
                minimumStockLevel != null ||
                maximumStockLevel != null ||
                certifications != null ||
                organicCertified != null ||
                fairTradeCertified != null;
    }

    /**
     * Validates that maximum stock level is greater than minimum stock level
     */
    public boolean isStockLevelValid() {
        if (minimumStockLevel != null && maximumStockLevel != null) {
            return maximumStockLevel.compareTo(minimumStockLevel) > 0;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UpdateInventoryRequest{" +
                "currentQuantity=" + currentQuantity +
                ", qualityGrade='" + qualityGrade + '\'' +
                ", marketValuePerUnit=" + marketValuePerUnit +
                ", storageLocation='" + storageLocation + '\'' +
                ", facilityType=" + facilityType +
                ", status=" + status +
                ", pestStatus=" + pestStatus +
                ", moistureContent=" + moistureContent +
                '}';
    }
}