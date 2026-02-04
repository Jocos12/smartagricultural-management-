package SmartAgricultural.Management.dto;

import SmartAgricultural.Management.Model.Inventory.FacilityType;
import SmartAgricultural.Management.Model.Inventory.InventoryStatus;
import SmartAgricultural.Management.Model.Inventory.PestStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Inventory entity
 * Contains complete inventory information for API responses
 *
 * @author Smart Agricultural Management System
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Complete inventory information")
public class InventoryDTO {

    @Schema(description = "Unique identifier for the inventory", example = "INV123456789ABC")
    private String id;

    @Schema(description = "Human-readable inventory code", example = "STOCK2401151234ABC")
    private String inventoryCode;

    @Schema(description = "ID of the associated crop", example = "CROP001")
    private String cropId;

    @Schema(description = "ID of the farmer who owns the inventory", example = "FARMER001")
    private String farmerId;

    @Schema(description = "ID of the buyer if reserved", example = "BUYER001")
    private String buyerId;

    @Schema(description = "Type of storage facility")
    private FacilityType facilityType;

    @Schema(description = "Physical storage location", example = "Warehouse A, Section 3, Row 5")
    private String storageLocation;

    @Schema(description = "Name of the storage facility", example = "Central Warehouse Kigali")
    private String facilityName;

    @Schema(description = "Total storage capacity in tonnes", example = "1000.00")
    private BigDecimal storageCapacity;

    @Schema(description = "Current quantity in storage", example = "250.50")
    private BigDecimal currentQuantity;

    @Schema(description = "Quantity reserved for buyers", example = "50.00")
    private BigDecimal reservedQuantity;

    @Schema(description = "Available quantity for sale", example = "200.50")
    private BigDecimal availableQuantity;

    @Schema(description = "Unit of measurement", example = "KG")
    private String unit;

    @Schema(description = "Quality grade of the inventory", example = "Grade A")
    private String qualityGrade;

    @Schema(description = "Date when the crop was harvested")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate harvestDate;

    @Schema(description = "Date when inventory was stored")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate storageDate;

    @Schema(description = "Expected expiry date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    @Schema(description = "Current status of the inventory")
    private InventoryStatus status;

    @Schema(description = "Pest inspection status")
    private PestStatus pestStatus;

    @Schema(description = "Market value per unit", example = "150.00")
    private BigDecimal marketValuePerUnit;

    @Schema(description = "Total market value", example = "37575.00")
    private BigDecimal totalMarketValue;

    @Schema(description = "Profit margin percentage", example = "25.50")
    private BigDecimal profitMargin;

    @Schema(description = "Loss percentage", example = "2.30")
    private BigDecimal lossPercentage;

    @Schema(description = "Storage quality score", example = "Excellent")
    private String storageQualityScore;

    @Schema(description = "Date when inventory was created")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @Schema(description = "Date when inventory was last updated")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;




    private Boolean organicCertified;
    private Boolean fairTradeCertified;
    // ==================== CONSTRUCTORS ====================

    public InventoryDTO() {
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

    public String getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
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

    public BigDecimal getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(BigDecimal reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
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

    public LocalDate getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(LocalDate harvestDate) {
        this.harvestDate = harvestDate;
    }

    public LocalDate getStorageDate() {
        return storageDate;
    }

    public void setStorageDate(LocalDate storageDate) {
        this.storageDate = storageDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
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

    public BigDecimal getMarketValuePerUnit() {
        return marketValuePerUnit;
    }

    public void setMarketValuePerUnit(BigDecimal marketValuePerUnit) {
        this.marketValuePerUnit = marketValuePerUnit;
    }

    public BigDecimal getTotalMarketValue() {
        return totalMarketValue;
    }

    public void setTotalMarketValue(BigDecimal totalMarketValue) {
        this.totalMarketValue = totalMarketValue;
    }

    public BigDecimal getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(BigDecimal profitMargin) {
        this.profitMargin = profitMargin;
    }

    public BigDecimal getLossPercentage() {
        return lossPercentage;
    }

    public void setLossPercentage(BigDecimal lossPercentage) {
        this.lossPercentage = lossPercentage;
    }

    public String getStorageQualityScore() {
        return storageQualityScore;
    }

    public void setStorageQualityScore(String storageQualityScore) {
        this.storageQualityScore = storageQualityScore;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // ==================== UTILITY METHODS ====================

    @Override
    public String toString() {
        return "InventoryDTO{" +
                "id='" + id + '\'' +
                ", inventoryCode='" + inventoryCode + '\'' +
                ", cropId='" + cropId + '\'' +
                ", facilityType=" + facilityType +
                ", currentQuantity=" + currentQuantity +
                ", status=" + status +
                ", totalMarketValue=" + totalMarketValue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InventoryDTO that = (InventoryDTO) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
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
}