package SmartAgricultural.Management.dto;

import SmartAgricultural.Management.Model.Crop;
import SmartAgricultural.Management.Model.CropProduction;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO pour les productions de cultures
 *
 * @author SmartAgricultural Management System
 * @version 1.0
 * @since 2024-01-01
 */
public class CropProductionDTO {

    private String id;

    @NotBlank(message = "Farm ID is required")
    private String farmId;

    @NotBlank(message = "Crop ID is required")
    private String cropId;

    @NotBlank(message = "Production code is required")
    @Size(max = 30, message = "Production code must not exceed 30 characters")
    private String productionCode;

    @NotNull(message = "Planting date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate plantingDate;

    @NotNull(message = "Expected harvest date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedHarvestDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate actualHarvestDate;

    @NotNull(message = "Area planted is required")
    @DecimalMin(value = "0.01", message = "Area planted must be greater than 0")
    @Digits(integer = 6, fraction = 2, message = "Area planted format is invalid")
    private BigDecimal areaPlanted;

    @DecimalMin(value = "0.0", message = "Expected yield must be positive")
    @Digits(integer = 6, fraction = 2, message = "Expected yield format is invalid")
    private BigDecimal expectedYield;

    @DecimalMin(value = "0.0", message = "Actual yield must be positive")
    @Digits(integer = 6, fraction = 2, message = "Actual yield format is invalid")
    private BigDecimal actualYield;

    @DecimalMin(value = "0.0", message = "Estimated price must be positive")
    private BigDecimal estimatedPrice;

    @DecimalMin(value = "0.0", message = "Price per kg must be positive")
    private BigDecimal pricePerKg;

    @DecimalMin(value = "0.0", message = "Total production must be positive")
    @Digits(integer = 8, fraction = 2, message = "Total production format is invalid")
    private BigDecimal totalProduction;

    private CropProduction.ProductionStatus productionStatus;

    @NotNull(message = "Season is required")
    private CropProduction.Season season;

    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be 2000 or later")
    @Max(value = 2100, message = "Year must not exceed 2100")
    private Integer year;

    @Size(max = 100, message = "Seed variety must not exceed 100 characters")
    private String seedVariety;

    @Size(max = 100, message = "Seed source must not exceed 100 characters")
    private String seedSource;

    private CropProduction.ProductionMethod productionMethod;

    @Size(max = 50, message = "Certification must not exceed 50 characters")
    private String certification;

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // ==================== CROP INFORMATION FIELDS ====================
    // These fields are populated from the Crop model
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String cropName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Crop.CropType cropType;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String cropImageUrl;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String cropVariety;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String cropScientificName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer cropGrowingPeriodDays;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String cropPlantingSeason;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String cropHarvestSeason;

    // ==================== FARMER INFORMATION FIELDS ====================
    // These fields are populated from the User model
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String farmerName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String farmerUsername;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String farmerEmail;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String farmerPhone;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String farmerProfileImageUrl;

    // ==================== CALCULATED FIELDS ====================
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isHarvested;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isActive;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isOverdue;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isOrganic;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean hasCertification;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long daysToHarvest;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long daysSincePlanting;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String productionCycle;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal yieldEfficiency;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String plantingDateFormatted;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String expectedHarvestDateFormatted;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String actualHarvestDateFormatted;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String areaPlantedDisplay;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String expectedYieldDisplay;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String actualYieldDisplay;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String totalProductionDisplay;

    // ==================== CONSTRUCTORS ====================

    public CropProductionDTO() {
    }

    public CropProductionDTO(String farmId, String cropId, String productionCode,
                             LocalDate plantingDate, LocalDate expectedHarvestDate,
                             BigDecimal areaPlanted, CropProduction.Season season, Integer year) {
        this.farmId = farmId;
        this.cropId = cropId;
        this.productionCode = productionCode;
        this.plantingDate = plantingDate;
        this.expectedHarvestDate = expectedHarvestDate;
        this.areaPlanted = areaPlanted;
        this.season = season;
        this.year = year;
        this.productionStatus = CropProduction.ProductionStatus.PLANNED;
        this.productionMethod = CropProduction.ProductionMethod.CONVENTIONAL;
    }

    // ==================== CONVERSION METHODS ====================

    /**
     * Créer un DTO à partir d'une entité
     */
    public static CropProductionDTO fromEntity(CropProduction production) {
        CropProductionDTO dto = new CropProductionDTO();

        // Champs de base
        dto.setId(production.getId());
        dto.setFarmId(production.getFarmId());
        dto.setCropId(production.getCropId());
        dto.setProductionCode(production.getProductionCode());
        dto.setPlantingDate(production.getPlantingDate());
        dto.setExpectedHarvestDate(production.getExpectedHarvestDate());
        dto.setActualHarvestDate(production.getActualHarvestDate());
        dto.setAreaPlanted(production.getAreaPlanted());
        dto.setExpectedYield(production.getExpectedYield());
        dto.setActualYield(production.getActualYield());
        dto.setEstimatedPrice(production.getEstimatedPrice());
        dto.setPricePerKg(production.getPricePerKg());
        dto.setTotalProduction(production.getTotalProduction());
        dto.setProductionStatus(production.getProductionStatus());
        dto.setSeason(production.getSeason());
        dto.setYear(production.getYear());
        dto.setSeedVariety(production.getSeedVariety());
        dto.setSeedSource(production.getSeedSource());
        dto.setProductionMethod(production.getProductionMethod());
        dto.setCertification(production.getCertification());
        dto.setNotes(production.getNotes());
        dto.setCreatedAt(production.getCreatedAt());
        dto.setUpdatedAt(production.getUpdatedAt());

        // Champs calculés
        dto.setIsHarvested(production.isHarvested());
        dto.setIsActive(production.isActive());
        dto.setIsOverdue(production.isOverdue());
        dto.setIsOrganic(production.isOrganic());
        dto.setHasCertification(production.hasCertification());
        dto.setDaysToHarvest(production.getDaysToHarvest());
        dto.setDaysSincePlanting(production.getDaysSincePlanting());
        dto.setProductionCycle(production.getProductionCycle());
        dto.setYieldEfficiency(production.getYieldEfficiency());
        dto.setPlantingDateFormatted(production.getPlantingDateFormatted());
        dto.setExpectedHarvestDateFormatted(production.getExpectedHarvestDateFormatted());
        dto.setActualHarvestDateFormatted(production.getActualHarvestDateFormatted());
        dto.setAreaPlantedDisplay(production.getAreaPlantedDisplay());
        dto.setExpectedYieldDisplay(production.getExpectedYieldDisplay());
        dto.setActualYieldDisplay(production.getActualYieldDisplay());
        dto.setTotalProductionDisplay(production.getTotalProductionDisplay());

        return dto;
    }

    /**
     * Convertir le DTO en entité
     */
    public CropProduction toEntity() {
        CropProduction production = new CropProduction();

        if (this.id != null) {
            production.setId(this.id);
        }
        production.setFarmId(this.farmId);
        production.setCropId(this.cropId);
        production.setProductionCode(this.productionCode);
        production.setPlantingDate(this.plantingDate);
        production.setExpectedHarvestDate(this.expectedHarvestDate);
        production.setActualHarvestDate(this.actualHarvestDate);
        production.setAreaPlanted(this.areaPlanted);
        production.setExpectedYield(this.expectedYield);
        production.setActualYield(this.actualYield);
        production.setEstimatedPrice(this.estimatedPrice);
        production.setPricePerKg(this.pricePerKg);
        production.setTotalProduction(this.totalProduction);
        production.setProductionStatus(this.productionStatus != null ?
                this.productionStatus : CropProduction.ProductionStatus.PLANNED);
        production.setSeason(this.season);
        production.setYear(this.year);
        production.setSeedVariety(this.seedVariety);
        production.setSeedSource(this.seedSource);
        production.setProductionMethod(this.productionMethod != null ?
                this.productionMethod : CropProduction.ProductionMethod.CONVENTIONAL);
        production.setCertification(this.certification);
        production.setNotes(this.notes);

        if (this.createdAt != null) {
            production.setCreatedAt(this.createdAt);
        }
        if (this.updatedAt != null) {
            production.setUpdatedAt(this.updatedAt);
        }

        return production;
    }

    /**
     * Mettre à jour une entité existante avec les données du DTO
     */
    public void updateEntity(CropProduction production) {
        if (this.farmId != null) {
            production.setFarmId(this.farmId);
        }
        if (this.cropId != null) {
            production.setCropId(this.cropId);
        }
        if (this.productionCode != null) {
            production.setProductionCode(this.productionCode);
        }
        if (this.plantingDate != null) {
            production.setPlantingDate(this.plantingDate);
        }
        if (this.expectedHarvestDate != null) {
            production.setExpectedHarvestDate(this.expectedHarvestDate);
        }
        if (this.actualHarvestDate != null) {
            production.setActualHarvestDate(this.actualHarvestDate);
        }
        if (this.areaPlanted != null) {
            production.setAreaPlanted(this.areaPlanted);
        }
        if (this.expectedYield != null) {
            production.setExpectedYield(this.expectedYield);
        }
        if (this.actualYield != null) {
            production.setActualYield(this.actualYield);
        }
        if (this.estimatedPrice != null) {
            production.setEstimatedPrice(this.estimatedPrice);
        }
        if (this.totalProduction != null) {
            production.setTotalProduction(this.totalProduction);
        }
        if (this.productionStatus != null) {
            production.setProductionStatus(this.productionStatus);
        }
        if (this.season != null) {
            production.setSeason(this.season);
        }
        if (this.year != null) {
            production.setYear(this.year);
        }
        if (this.seedVariety != null) {
            production.setSeedVariety(this.seedVariety);
        }
        if (this.seedSource != null) {
            production.setSeedSource(this.seedSource);
        }
        if (this.productionMethod != null) {
            production.setProductionMethod(this.productionMethod);
        }
        if (this.certification != null) {
            production.setCertification(this.certification);
        }
        if (this.notes != null) {
            production.setNotes(this.notes);
        }
    }

    // ==================== GETTERS AND SETTERS - BASE FIELDS ====================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFarmId() {
        return farmId;
    }

    public void setFarmId(String farmId) {
        this.farmId = farmId;
    }

    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    public String getProductionCode() {
        return productionCode;
    }

    public void setProductionCode(String productionCode) {
        this.productionCode = productionCode;
    }

    public LocalDate getPlantingDate() {
        return plantingDate;
    }

    public void setPlantingDate(LocalDate plantingDate) {
        this.plantingDate = plantingDate;
    }

    public LocalDate getExpectedHarvestDate() {
        return expectedHarvestDate;
    }

    public void setExpectedHarvestDate(LocalDate expectedHarvestDate) {
        this.expectedHarvestDate = expectedHarvestDate;
    }

    public LocalDate getActualHarvestDate() {
        return actualHarvestDate;
    }

    public void setActualHarvestDate(LocalDate actualHarvestDate) {
        this.actualHarvestDate = actualHarvestDate;
    }

    public BigDecimal getAreaPlanted() {
        return areaPlanted;
    }

    public void setAreaPlanted(BigDecimal areaPlanted) {
        this.areaPlanted = areaPlanted;
    }

    public BigDecimal getExpectedYield() {
        return expectedYield;
    }

    public void setExpectedYield(BigDecimal expectedYield) {
        this.expectedYield = expectedYield;
    }

    public BigDecimal getActualYield() {
        return actualYield;
    }

    public void setActualYield(BigDecimal actualYield) {
        this.actualYield = actualYield;
    }

    public BigDecimal getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(BigDecimal estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }


    public BigDecimal getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(BigDecimal pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public BigDecimal getTotalProduction() {
        return totalProduction;
    }

    public void setTotalProduction(BigDecimal totalProduction) {
        this.totalProduction = totalProduction;
    }

    public CropProduction.ProductionStatus getProductionStatus() {
        return productionStatus;
    }

    public void setProductionStatus(CropProduction.ProductionStatus productionStatus) {
        this.productionStatus = productionStatus;
    }

    public CropProduction.Season getSeason() {
        return season;
    }

    public void setSeason(CropProduction.Season season) {
        this.season = season;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getSeedVariety() {
        return seedVariety;
    }

    public void setSeedVariety(String seedVariety) {
        this.seedVariety = seedVariety;
    }

    public String getSeedSource() {
        return seedSource;
    }

    public void setSeedSource(String seedSource) {
        this.seedSource = seedSource;
    }

    public CropProduction.ProductionMethod getProductionMethod() {
        return productionMethod;
    }

    public void setProductionMethod(CropProduction.ProductionMethod productionMethod) {
        this.productionMethod = productionMethod;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ==================== CROP INFORMATION GETTERS AND SETTERS ====================

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public Crop.CropType getCropType() {
        return cropType;
    }

    public void setCropType(Crop.CropType cropType) {
        this.cropType = cropType;
    }

    public String getCropImageUrl() {
        return cropImageUrl;
    }

    public void setCropImageUrl(String cropImageUrl) {
        this.cropImageUrl = cropImageUrl;
    }

    public String getCropVariety() {
        return cropVariety;
    }

    public void setCropVariety(String cropVariety) {
        this.cropVariety = cropVariety;
    }

    public String getCropScientificName() {
        return cropScientificName;
    }

    public void setCropScientificName(String cropScientificName) {
        this.cropScientificName = cropScientificName;
    }

    public Integer getCropGrowingPeriodDays() {
        return cropGrowingPeriodDays;
    }

    public void setCropGrowingPeriodDays(Integer cropGrowingPeriodDays) {
        this.cropGrowingPeriodDays = cropGrowingPeriodDays;
    }

    public String getCropPlantingSeason() {
        return cropPlantingSeason;
    }

    public void setCropPlantingSeason(String cropPlantingSeason) {
        this.cropPlantingSeason = cropPlantingSeason;
    }

    public String getCropHarvestSeason() {
        return cropHarvestSeason;
    }

    public void setCropHarvestSeason(String cropHarvestSeason) {
        this.cropHarvestSeason = cropHarvestSeason;
    }

    // ==================== FARMER INFORMATION GETTERS AND SETTERS ====================

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getFarmerUsername() {
        return farmerUsername;
    }

    public void setFarmerUsername(String farmerUsername) {
        this.farmerUsername = farmerUsername;
    }

    public String getFarmerEmail() {
        return farmerEmail;
    }

    public void setFarmerEmail(String farmerEmail) {
        this.farmerEmail = farmerEmail;
    }

    public String getFarmerPhone() {
        return farmerPhone;
    }

    public void setFarmerPhone(String farmerPhone) {
        this.farmerPhone = farmerPhone;
    }

    public String getFarmerProfileImageUrl() {
        return farmerProfileImageUrl;
    }

    public void setFarmerProfileImageUrl(String farmerProfileImageUrl) {
        this.farmerProfileImageUrl = farmerProfileImageUrl;
    }

    // ==================== CALCULATED FIELDS GETTERS AND SETTERS ====================

    public Boolean getIsHarvested() {
        return isHarvested;
    }

    public void setIsHarvested(Boolean isHarvested) {
        this.isHarvested = isHarvested;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsOverdue() {
        return isOverdue;
    }

    public void setIsOverdue(Boolean isOverdue) {
        this.isOverdue = isOverdue;
    }

    public Boolean getIsOrganic() {
        return isOrganic;
    }

    public void setIsOrganic(Boolean isOrganic) {
        this.isOrganic = isOrganic;
    }

    public Boolean getHasCertification() {
        return hasCertification;
    }

    public void setHasCertification(Boolean hasCertification) {
        this.hasCertification = hasCertification;
    }

    public Long getDaysToHarvest() {
        return daysToHarvest;
    }

    public void setDaysToHarvest(Long daysToHarvest) {
        this.daysToHarvest = daysToHarvest;
    }

    public Long getDaysSincePlanting() {
        return daysSincePlanting;
    }

    public void setDaysSincePlanting(Long daysSincePlanting) {
        this.daysSincePlanting = daysSincePlanting;
    }

    public String getProductionCycle() {
        return productionCycle;
    }

    public void setProductionCycle(String productionCycle) {
        this.productionCycle = productionCycle;
    }

    public BigDecimal getYieldEfficiency() {
        return yieldEfficiency;
    }

    public void setYieldEfficiency(BigDecimal yieldEfficiency) {
        this.yieldEfficiency = yieldEfficiency;
    }

    public String getPlantingDateFormatted() {
        return plantingDateFormatted;
    }

    public void setPlantingDateFormatted(String plantingDateFormatted) {
        this.plantingDateFormatted = plantingDateFormatted;
    }

    public String getExpectedHarvestDateFormatted() {
        return expectedHarvestDateFormatted;
    }

    public void setExpectedHarvestDateFormatted(String expectedHarvestDateFormatted) {
        this.expectedHarvestDateFormatted = expectedHarvestDateFormatted;
    }

    public String getActualHarvestDateFormatted() {
        return actualHarvestDateFormatted;
    }

    public void setActualHarvestDateFormatted(String actualHarvestDateFormatted) {
        this.actualHarvestDateFormatted = actualHarvestDateFormatted;
    }

    public String getAreaPlantedDisplay() {
        return areaPlantedDisplay;
    }

    public void setAreaPlantedDisplay(String areaPlantedDisplay) {
        this.areaPlantedDisplay = areaPlantedDisplay;
    }

    public String getExpectedYieldDisplay() {
        return expectedYieldDisplay;
    }

    public void setExpectedYieldDisplay(String expectedYieldDisplay) {
        this.expectedYieldDisplay = expectedYieldDisplay;
    }

    public String getActualYieldDisplay() {
        return actualYieldDisplay;
    }

    public void setActualYieldDisplay(String actualYieldDisplay) {
        this.actualYieldDisplay = actualYieldDisplay;
    }

    public String getTotalProductionDisplay() {
        return totalProductionDisplay;
    }

    public void setTotalProductionDisplay(String totalProductionDisplay) {
        this.totalProductionDisplay = totalProductionDisplay;
    }

    // ==================== UTILITY METHODS ====================

    @Override
    public String toString() {
        return "CropProductionDTO{" +
                "id='" + id + '\'' +
                ", farmId='" + farmId + '\'' +
                ", cropId='" + cropId + '\'' +
                ", productionCode='" + productionCode + '\'' +
                ", plantingDate=" + plantingDate +
                ", expectedHarvestDate=" + expectedHarvestDate +
                ", areaPlanted=" + areaPlanted +
                ", productionStatus=" + productionStatus +
                ", season=" + season +
                ", year=" + year +
                ", productionMethod=" + productionMethod +
                ", cropName='" + cropName + '\'' +
                ", farmerName='" + farmerName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CropProductionDTO that = (CropProductionDTO) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}