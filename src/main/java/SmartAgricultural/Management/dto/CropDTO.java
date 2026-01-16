package SmartAgricultural.Management.dto;

import SmartAgricultural.Management.Model.Crop;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object pour l'entité Crop
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CropDTO {

    private String id;

    @NotBlank(message = "Crop name is required")
    @Size(max = 100, message = "Crop name must not exceed 100 characters")
    private String cropName;
    private String imageUrl;
    @NotNull(message = "Crop type is required")
    private Crop.CropType cropType;

    @Size(max = 100, message = "Scientific name must not exceed 100 characters")
    private String scientificName;

    @Size(max = 100, message = "Variety must not exceed 100 characters")
    private String variety;

    @NotNull(message = "Growing period is required")
    @Min(value = 1, message = "Growing period must be at least 1 day")
    @Max(value = 3650, message = "Growing period must not exceed 3650 days (10 years)")
    private Integer growingPeriodDays;

    @NotBlank(message = "Planting season is required")
    @Size(max = 50, message = "Planting season must not exceed 50 characters")
    private String plantingSeason;

    @NotBlank(message = "Harvest season is required")
    @Size(max = 50, message = "Harvest season must not exceed 50 characters")
    private String harvestSeason;

    @DecimalMin(value = "0.0", message = "Water requirement must be positive")
    @Digits(integer = 4, fraction = 2, message = "Water requirement format is invalid")
    private BigDecimal waterRequirement;

    @Size(max = 2000, message = "Climatic requirement must not exceed 2000 characters")
    private String climaticRequirement;

    @DecimalMin(value = "0.0", message = "Minimum pH must be positive")
    @DecimalMax(value = "14.0", message = "Minimum pH must not exceed 14.0")
    private BigDecimal soilPhMin;

    @DecimalMin(value = "0.0", message = "Maximum pH must be positive")
    @DecimalMax(value = "14.0", message = "Maximum pH must not exceed 14.0")
    private BigDecimal soilPhMax;

    @DecimalMin(value = "-50.0", message = "Minimum temperature must be realistic")
    @DecimalMax(value = "70.0", message = "Minimum temperature must be realistic")
    private BigDecimal temperatureMin;

    @DecimalMin(value = "-50.0", message = "Maximum temperature must be realistic")
    @DecimalMax(value = "70.0", message = "Maximum temperature must be realistic")
    private BigDecimal temperatureMax;

    @DecimalMin(value = "0.0", message = "Rainfall requirement must be positive")
    @Digits(integer = 4, fraction = 2, message = "Rainfall requirement format is invalid")
    private BigDecimal rainfallRequirement;

    private Crop.MarketDemandLevel marketDemandLevel;

    @Size(max = 5000, message = "Nutritional value must not exceed 5000 characters")
    private String nutritionalValue;

    @Min(value = 1, message = "Storage life must be at least 1 day")
    @Max(value = 3650, message = "Storage life must not exceed 3650 days")
    private Integer storageLifeDays;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Champs calculés (read-only)
    private String growingPeriodDisplay;
    private String storageLifeDisplay;
    private String temperatureRange;
    private String phRange;
    private String waterRequirementDisplay;
    private String rainfallRequirementDisplay;
    private String fullName;

    // Constructeurs
    public CropDTO() {}

    public CropDTO(String cropName, Crop.CropType cropType, Integer growingPeriodDays,
                   String plantingSeason, String harvestSeason) {
        this.cropName = cropName;
        this.cropType = cropType;
        this.growingPeriodDays = growingPeriodDays;
        this.plantingSeason = plantingSeason;
        this.harvestSeason = harvestSeason;
    }

    // Méthodes de conversion
    public static CropDTO fromEntity(Crop crop) {
        if (crop == null) return null;

        CropDTO dto = new CropDTO();
        dto.setId(crop.getId());
        dto.setCropName(crop.getCropName());
        dto.setCropType(crop.getCropType());
        dto.setScientificName(crop.getScientificName());
        dto.setVariety(crop.getVariety());
        dto.setGrowingPeriodDays(crop.getGrowingPeriodDays());
        dto.setPlantingSeason(crop.getPlantingSeason());
        dto.setHarvestSeason(crop.getHarvestSeason());
        dto.setWaterRequirement(crop.getWaterRequirement());
        dto.setClimaticRequirement(crop.getClimaticRequirement());
        dto.setSoilPhMin(crop.getSoilPhMin());
        dto.setSoilPhMax(crop.getSoilPhMax());
        dto.setTemperatureMin(crop.getTemperatureMin());
        dto.setTemperatureMax(crop.getTemperatureMax());
        dto.setRainfallRequirement(crop.getRainfallRequirement());
        dto.setMarketDemandLevel(crop.getMarketDemandLevel());
        dto.setNutritionalValue(crop.getNutritionalValue());
        dto.setStorageLifeDays(crop.getStorageLifeDays());
        dto.setCreatedAt(crop.getCreatedAt());
        dto.setUpdatedAt(crop.getUpdatedAt());

        // Champs calculés
        dto.setGrowingPeriodDisplay(crop.getGrowingPeriodInMonths());
        dto.setStorageLifeDisplay(crop.getStorageLifeDisplay());
        dto.setTemperatureRange(crop.getTemperatureRange());
        dto.setPhRange(crop.getPhRange());
        dto.setWaterRequirementDisplay(crop.getWaterRequirementDisplay());
        dto.setRainfallRequirementDisplay(crop.getRainfallRequirementDisplay());
        dto.setFullName(crop.getFullName());

        return dto;
    }

    public Crop toEntity() {
        Crop crop = new Crop();
        crop.setId(this.id);
        crop.setCropName(this.cropName);
        crop.setCropType(this.cropType);
        crop.setScientificName(this.scientificName);
        crop.setVariety(this.variety);
        crop.setGrowingPeriodDays(this.growingPeriodDays);
        crop.setPlantingSeason(this.plantingSeason);
        crop.setHarvestSeason(this.harvestSeason);
        crop.setWaterRequirement(this.waterRequirement);
        crop.setClimaticRequirement(this.climaticRequirement);
        crop.setSoilPhMin(this.soilPhMin);
        crop.setSoilPhMax(this.soilPhMax);
        crop.setTemperatureMin(this.temperatureMin);
        crop.setTemperatureMax(this.temperatureMax);
        crop.setRainfallRequirement(this.rainfallRequirement);
        crop.setMarketDemandLevel(this.marketDemandLevel);
        crop.setNutritionalValue(this.nutritionalValue);
        crop.setStorageLifeDays(this.storageLifeDays);
        crop.setCreatedAt(this.createdAt);
        crop.setUpdatedAt(this.updatedAt);

        return crop;
    }

    // Méthode pour mettre à jour une entité existante
    public void updateEntity(Crop crop) {
        if (crop == null) return;

        // Ne pas mettre à jour l'ID et les timestamps de création
        if (this.cropName != null) crop.setCropName(this.cropName);
        if (this.cropType != null) crop.setCropType(this.cropType);
        if (this.scientificName != null) crop.setScientificName(this.scientificName);
        if (this.variety != null) crop.setVariety(this.variety);
        if (this.growingPeriodDays != null) crop.setGrowingPeriodDays(this.growingPeriodDays);
        if (this.plantingSeason != null) crop.setPlantingSeason(this.plantingSeason);
        if (this.harvestSeason != null) crop.setHarvestSeason(this.harvestSeason);
        if (this.waterRequirement != null) crop.setWaterRequirement(this.waterRequirement);
        if (this.climaticRequirement != null) crop.setClimaticRequirement(this.climaticRequirement);
        if (this.soilPhMin != null) crop.setSoilPhMin(this.soilPhMin);
        if (this.soilPhMax != null) crop.setSoilPhMax(this.soilPhMax);
        if (this.temperatureMin != null) crop.setTemperatureMin(this.temperatureMin);
        if (this.temperatureMax != null) crop.setTemperatureMax(this.temperatureMax);
        if (this.rainfallRequirement != null) crop.setRainfallRequirement(this.rainfallRequirement);
        if (this.marketDemandLevel != null) crop.setMarketDemandLevel(this.marketDemandLevel);
        if (this.nutritionalValue != null) crop.setNutritionalValue(this.nutritionalValue);
        if (this.storageLifeDays != null) crop.setStorageLifeDays(this.storageLifeDays);
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public Crop.CropType getCropType() { return cropType; }
    public void setCropType(Crop.CropType cropType) { this.cropType = cropType; }

    public String getScientificName() { return scientificName; }
    public void setScientificName(String scientificName) { this.scientificName = scientificName; }

    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }

    public Integer getGrowingPeriodDays() { return growingPeriodDays; }
    public void setGrowingPeriodDays(Integer growingPeriodDays) { this.growingPeriodDays = growingPeriodDays; }

    public String getPlantingSeason() { return plantingSeason; }
    public void setPlantingSeason(String plantingSeason) { this.plantingSeason = plantingSeason; }

    public String getHarvestSeason() { return harvestSeason; }
    public void setHarvestSeason(String harvestSeason) { this.harvestSeason = harvestSeason; }

    public BigDecimal getWaterRequirement() { return waterRequirement; }
    public void setWaterRequirement(BigDecimal waterRequirement) { this.waterRequirement = waterRequirement; }

    public String getClimaticRequirement() { return climaticRequirement; }
    public void setClimaticRequirement(String climaticRequirement) { this.climaticRequirement = climaticRequirement; }

    public BigDecimal getSoilPhMin() { return soilPhMin; }
    public void setSoilPhMin(BigDecimal soilPhMin) { this.soilPhMin = soilPhMin; }

    public BigDecimal getSoilPhMax() { return soilPhMax; }
    public void setSoilPhMax(BigDecimal soilPhMax) { this.soilPhMax = soilPhMax; }

    public BigDecimal getTemperatureMin() { return temperatureMin; }
    public void setTemperatureMin(BigDecimal temperatureMin) { this.temperatureMin = temperatureMin; }

    public BigDecimal getTemperatureMax() { return temperatureMax; }
    public void setTemperatureMax(BigDecimal temperatureMax) { this.temperatureMax = temperatureMax; }

    public BigDecimal getRainfallRequirement() { return rainfallRequirement; }
    public void setRainfallRequirement(BigDecimal rainfallRequirement) { this.rainfallRequirement = rainfallRequirement; }

    public Crop.MarketDemandLevel getMarketDemandLevel() { return marketDemandLevel; }
    public void setMarketDemandLevel(Crop.MarketDemandLevel marketDemandLevel) { this.marketDemandLevel = marketDemandLevel; }

    public String getNutritionalValue() { return nutritionalValue; }
    public void setNutritionalValue(String nutritionalValue) { this.nutritionalValue = nutritionalValue; }

    public Integer getStorageLifeDays() { return storageLifeDays; }
    public void setStorageLifeDays(Integer storageLifeDays) { this.storageLifeDays = storageLifeDays; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Getters et Setters pour les champs calculés
    public String getGrowingPeriodDisplay() { return growingPeriodDisplay; }
    public void setGrowingPeriodDisplay(String growingPeriodDisplay) { this.growingPeriodDisplay = growingPeriodDisplay; }

    public String getStorageLifeDisplay() { return storageLifeDisplay; }
    public void setStorageLifeDisplay(String storageLifeDisplay) { this.storageLifeDisplay = storageLifeDisplay; }

    public String getTemperatureRange() { return temperatureRange; }
    public void setTemperatureRange(String temperatureRange) { this.temperatureRange = temperatureRange; }

    public String getPhRange() { return phRange; }
    public void setPhRange(String phRange) { this.phRange = phRange; }

    public String getWaterRequirementDisplay() { return waterRequirementDisplay; }
    public void setWaterRequirementDisplay(String waterRequirementDisplay) { this.waterRequirementDisplay = waterRequirementDisplay; }

    public String getRainfallRequirementDisplay() { return rainfallRequirementDisplay; }
    public void setRainfallRequirementDisplay(String rainfallRequirementDisplay) { this.rainfallRequirementDisplay = rainfallRequirementDisplay; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "CropDTO{" +
                "id='" + id + '\'' +
                ", cropName='" + cropName + '\'' +
                ", cropType=" + cropType +
                ", variety='" + variety + '\'' +
                ", growingPeriodDays=" + growingPeriodDays +
                ", plantingSeason='" + plantingSeason + '\'' +
                ", harvestSeason='" + harvestSeason + '\'' +
                ", marketDemandLevel=" + marketDemandLevel +
                '}';
    }
}