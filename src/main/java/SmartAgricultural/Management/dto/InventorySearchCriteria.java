package SmartAgricultural.Management.dto;

import SmartAgricultural.Management.Model.Inventory.FacilityType;
import SmartAgricultural.Management.Model.Inventory.InventoryStatus;
import SmartAgricultural.Management.Model.Inventory.PestStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Search criteria for filtering inventory records
 * FIXED: Added String alternatives for enum fields to support both String and Enum inputs
 */
@Schema(description = "Search criteria for filtering inventory records")
public class InventorySearchCriteria {

    @Schema(description = "Filter by crop ID", example = "CROP001")
    private String cropId;

    @Schema(description = "Filter by farmer ID", example = "FARMER001")
    private String farmerId;

    @Schema(description = "Filter by buyer ID", example = "BUYER001")
    private String buyerId;

    // ============ FIXED: Support both String and Enum ============

    @Schema(description = "Filter by facility type (String)", example = "WAREHOUSE")
    private String facilityTypeStr;

    @Schema(description = "Filter by facility type (Enum)")
    private FacilityType facilityType;

    @Schema(description = "Filter by inventory status (String)", example = "AVAILABLE")
    private String statusStr;

    @Schema(description = "Filter by inventory status (Enum)")
    private InventoryStatus status;

    @Schema(description = "Filter by pest status (String)", example = "PEST_FREE")
    private String pestStatusStr;

    @Schema(description = "Filter by pest status (Enum)")
    private PestStatus pestStatus;

    // ============================================================

    @Schema(description = "Filter by quality grade", example = "Grade A")
    private String qualityGrade;

    @Schema(description = "Minimum quantity threshold", example = "50.00")
    @DecimalMin(value = "0.0", message = "Minimum quantity must be positive")
    @Digits(integer = 8, fraction = 2, message = "Minimum quantity format is invalid")
    private BigDecimal minQuantity;

    @Schema(description = "Maximum quantity threshold", example = "500.00")
    @DecimalMin(value = "0.0", message = "Maximum quantity must be positive")
    @Digits(integer = 8, fraction = 2, message = "Maximum quantity format is invalid")
    private BigDecimal maxQuantity;

    @Schema(description = "Minimum market value", example = "1000.00")
    @DecimalMin(value = "0.0", message = "Minimum value must be positive")
    @Digits(integer = 10, fraction = 2, message = "Minimum value format is invalid")
    private BigDecimal minValue;

    @Schema(description = "Maximum market value", example = "50000.00")
    @DecimalMin(value = "0.0", message = "Maximum value must be positive")
    @Digits(integer = 10, fraction = 2, message = "Maximum value format is invalid")
    private BigDecimal maxValue;

    @Schema(description = "Filter by storage start date", example = "2024-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "Filter by storage end date", example = "2024-12-31")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "Filter by expiry start date", example = "2024-06-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryStartDate;

    @Schema(description = "Filter by expiry end date", example = "2024-12-31")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryEndDate;

    @Schema(description = "Filter by organic certification", example = "true")
    private Boolean organic;

    @Schema(description = "Filter by fair trade certification", example = "true")
    private Boolean fairTrade;

    @Schema(description = "Filter by local sourcing", example = "true")
    private Boolean localSourcing;

    @Schema(description = "Search keyword for general search", example = "premium")
    private String keyword;

    @Schema(description = "Filter by storage location", example = "Warehouse A")
    private String storageLocation;

    @Schema(description = "Filter by facility name", example = "Central Warehouse")
    private String facilityName;

    @Schema(description = "Filter by certifications", example = "Organic")
    private String certifications;

    @Schema(description = "Filter by multiple crop IDs")
    private List<String> cropIds;

    @Schema(description = "Filter by multiple statuses")
    private List<InventoryStatus> statuses;

    @Schema(description = "Filter by multiple quality grades")
    private List<String> qualityGrades;

    @Schema(description = "Include only items expiring soon", example = "true")
    private Boolean expiringSoon;

    @Schema(description = "Include only low stock items", example = "true")
    private Boolean lowStock;

    @Schema(description = "Include only high value items", example = "true")
    private Boolean highValue;

    @Schema(description = "Minimum profit margin percentage", example = "15.0")
    private BigDecimal minProfitMargin;

    @Schema(description = "Maximum loss percentage", example = "5.0")
    private BigDecimal maxLossPercentage;

    // ==================== CONSTRUCTORS ====================

    public InventorySearchCriteria() {
    }

    public InventorySearchCriteria(String cropId, InventoryStatus status, String qualityGrade) {
        this.cropId = cropId;
        this.status = status;
        this.qualityGrade = qualityGrade;
    }

    // ==================== SMART GETTERS (Auto-convert String to Enum) ====================

    /**
     * Gets facility type - automatically converts from String if needed
     */
    public FacilityType getFacilityType() {
        if (facilityType != null) {
            return facilityType;
        }
        if (facilityTypeStr != null && !facilityTypeStr.trim().isEmpty()) {
            try {
                return FacilityType.valueOf(facilityTypeStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Gets status - automatically converts from String if needed
     */
    public InventoryStatus getStatus() {
        if (status != null) {
            return status;
        }
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            try {
                return InventoryStatus.valueOf(statusStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Gets pest status - automatically converts from String if needed
     */
    public PestStatus getPestStatus() {
        if (pestStatus != null) {
            return pestStatus;
        }
        if (pestStatusStr != null && !pestStatusStr.trim().isEmpty()) {
            try {
                return PestStatus.valueOf(pestStatusStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    // ==================== STANDARD GETTERS AND SETTERS ====================

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

    // String setters for flexibility
    public String getFacilityTypeStr() {
        return facilityTypeStr;
    }

    public void setFacilityTypeStr(String facilityTypeStr) {
        this.facilityTypeStr = facilityTypeStr;
    }

    public void setFacilityType(FacilityType facilityType) {
        this.facilityType = facilityType;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public void setStatus(InventoryStatus status) {
        this.status = status;
    }

    public String getPestStatusStr() {
        return pestStatusStr;
    }

    public void setPestStatusStr(String pestStatusStr) {
        this.pestStatusStr = pestStatusStr;
    }

    public void setPestStatus(PestStatus pestStatus) {
        this.pestStatus = pestStatus;
    }

    public String getQualityGrade() {
        return qualityGrade;
    }

    public void setQualityGrade(String qualityGrade) {
        this.qualityGrade = qualityGrade;
    }

    public BigDecimal getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(BigDecimal minQuantity) {
        this.minQuantity = minQuantity;
    }

    public BigDecimal getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(BigDecimal maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public BigDecimal getMinValue() {
        return minValue;
    }

    public void setMinValue(BigDecimal minValue) {
        this.minValue = minValue;
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getExpiryStartDate() {
        return expiryStartDate;
    }

    public void setExpiryStartDate(LocalDate expiryStartDate) {
        this.expiryStartDate = expiryStartDate;
    }

    public LocalDate getExpiryEndDate() {
        return expiryEndDate;
    }

    public void setExpiryEndDate(LocalDate expiryEndDate) {
        this.expiryEndDate = expiryEndDate;
    }

    public Boolean getOrganic() {
        return organic;
    }

    public void setOrganic(Boolean organic) {
        this.organic = organic;
    }

    public Boolean getFairTrade() {
        return fairTrade;
    }

    public void setFairTrade(Boolean fairTrade) {
        this.fairTrade = fairTrade;
    }

    public Boolean getLocalSourcing() {
        return localSourcing;
    }

    public void setLocalSourcing(Boolean localSourcing) {
        this.localSourcing = localSourcing;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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

    public String getCertifications() {
        return certifications;
    }

    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    public List<String> getCropIds() {
        return cropIds;
    }

    public void setCropIds(List<String> cropIds) {
        this.cropIds = cropIds;
    }

    public List<InventoryStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<InventoryStatus> statuses) {
        this.statuses = statuses;
    }

    public List<String> getQualityGrades() {
        return qualityGrades;
    }

    public void setQualityGrades(List<String> qualityGrades) {
        this.qualityGrades = qualityGrades;
    }

    public Boolean getExpiringSoon() {
        return expiringSoon;
    }

    public void setExpiringSoon(Boolean expiringSoon) {
        this.expiringSoon = expiringSoon;
    }

    public Boolean getLowStock() {
        return lowStock;
    }

    public void setLowStock(Boolean lowStock) {
        this.lowStock = lowStock;
    }

    public Boolean getHighValue() {
        return highValue;
    }

    public void setHighValue(Boolean highValue) {
        this.highValue = highValue;
    }

    public BigDecimal getMinProfitMargin() {
        return minProfitMargin;
    }

    public void setMinProfitMargin(BigDecimal minProfitMargin) {
        this.minProfitMargin = minProfitMargin;
    }

    public BigDecimal getMaxLossPercentage() {
        return maxLossPercentage;
    }

    public void setMaxLossPercentage(BigDecimal maxLossPercentage) {
        this.maxLossPercentage = maxLossPercentage;
    }

    // ==================== UTILITY METHODS ====================

    public boolean hasFilters() {
        return cropId != null || farmerId != null || buyerId != null ||
                facilityType != null || facilityTypeStr != null ||
                status != null || statusStr != null ||
                qualityGrade != null || pestStatus != null || pestStatusStr != null ||
                minQuantity != null || maxQuantity != null ||
                minValue != null || maxValue != null || startDate != null ||
                endDate != null || organic != null || fairTrade != null ||
                keyword != null || storageLocation != null ||
                (cropIds != null && !cropIds.isEmpty()) ||
                (statuses != null && !statuses.isEmpty());
    }

    public boolean isDateRangeValid() {
        if (startDate != null && endDate != null) {
            return !startDate.isAfter(endDate);
        }
        if (expiryStartDate != null && expiryEndDate != null) {
            return !expiryStartDate.isAfter(expiryEndDate);
        }
        return true;
    }

    public boolean isQuantityRangeValid() {
        if (minQuantity != null && maxQuantity != null) {
            return minQuantity.compareTo(maxQuantity) <= 0;
        }
        return true;
    }

    public boolean isValueRangeValid() {
        if (minValue != null && maxValue != null) {
            return minValue.compareTo(maxValue) <= 0;
        }
        return true;
    }

    @Override
    public String toString() {
        return "InventorySearchCriteria{" +
                "cropId='" + cropId + '\'' +
                ", farmerId='" + farmerId + '\'' +
                ", status=" + getStatus() +
                ", qualityGrade='" + qualityGrade + '\'' +
                ", facilityType=" + getFacilityType() +
                ", minQuantity=" + minQuantity +
                ", maxQuantity=" + maxQuantity +
                ", keyword='" + keyword + '\'' +
                ", organic=" + organic +
                ", fairTrade=" + fairTrade +
                '}';
    }
}