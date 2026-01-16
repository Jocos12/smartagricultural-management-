package SmartAgricultural.Management.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "crops")
public class Crop {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "crop_name", length = 100, nullable = false)
    @NotBlank(message = "Crop name is required")
    @Size(max = 100, message = "Crop name must not exceed 100 characters")
    private String cropName;

    @Enumerated(EnumType.STRING)
    @Column(name = "crop_type", nullable = false)
    @NotNull(message = "Crop type is required")
    private CropType cropType;
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "scientific_name", length = 100)
    @Size(max = 100, message = "Scientific name must not exceed 100 characters")
    private String scientificName;

    @Column(name = "variety", length = 100)
    @Size(max = 100, message = "Variety must not exceed 100 characters")
    private String variety;

    @Column(name = "growing_period_days", nullable = false)
    @NotNull(message = "Growing period is required")
    @Min(value = 1, message = "Growing period must be at least 1 day")
    @Max(value = 3650, message = "Growing period must not exceed 3650 days (10 years)")
    private Integer growingPeriodDays;

    @Column(name = "planting_season", length = 50, nullable = false)
    @NotBlank(message = "Planting season is required")
    @Size(max = 50, message = "Planting season must not exceed 50 characters")
    private String plantingSeason;

    @Column(name = "harvest_season", length = 50, nullable = false)
    @NotBlank(message = "Harvest season is required")
    @Size(max = 50, message = "Harvest season must not exceed 50 characters")
    private String harvestSeason;

    @Column(name = "water_requirement", precision = 6, scale = 2)
    @DecimalMin(value = "0.0", message = "Water requirement must be positive")
    @Digits(integer = 4, fraction = 2, message = "Water requirement format is invalid")
    private BigDecimal waterRequirement; // mm/saison

    @Column(name = "climatic_requirement", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Climatic requirement must not exceed 2000 characters")
    private String climaticRequirement;

    @Column(name = "soil_ph_min", precision = 3, scale = 1)
    @DecimalMin(value = "0.0", message = "Minimum pH must be positive")
    @DecimalMax(value = "14.0", message = "Minimum pH must not exceed 14.0")
    private BigDecimal soilPhMin;

    @Column(name = "soil_ph_max", precision = 3, scale = 1)
    @DecimalMin(value = "0.0", message = "Maximum pH must be positive")
    @DecimalMax(value = "14.0", message = "Maximum pH must not exceed 14.0")
    private BigDecimal soilPhMax;

    @Column(name = "temperature_min", precision = 4, scale = 1)
    @DecimalMin(value = "-50.0", message = "Minimum temperature must be realistic")
    @DecimalMax(value = "70.0", message = "Minimum temperature must be realistic")
    private BigDecimal temperatureMin; // °C

    @Column(name = "temperature_max", precision = 4, scale = 1)
    @DecimalMin(value = "-50.0", message = "Maximum temperature must be realistic")
    @DecimalMax(value = "70.0", message = "Maximum temperature must be realistic")
    private BigDecimal temperatureMax; // °C

    @Column(name = "rainfall_requirement", precision = 6, scale = 2)
    @DecimalMin(value = "0.0", message = "Rainfall requirement must be positive")
    @Digits(integer = 4, fraction = 2, message = "Rainfall requirement format is invalid")
    private BigDecimal rainfallRequirement; // mm/an

    @Enumerated(EnumType.STRING)
    @Column(name = "market_demand_level")
    private MarketDemandLevel marketDemandLevel = MarketDemandLevel.MEDIUM;

    @Column(name = "nutritional_value", columnDefinition = "TEXT")
    @Size(max = 5000, message = "Nutritional value must not exceed 5000 characters")
    private String nutritionalValue; // JSON des valeurs nutritionnelles

    @Column(name = "storage_life_days")
    @Min(value = 1, message = "Storage life must be at least 1 day")
    @Max(value = 3650, message = "Storage life must not exceed 3650 days")
    private Integer storageLifeDays;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enums
    public enum CropType {
        CEREALS("Cereals"),
        VEGETABLES("Vegetables"),
        FRUITS("Fruits"),
        LEGUMES("Legumes"),
        TUBERS("Tubers"),
        CASH_CROPS("Cash Crops");

        private final String displayName;

        CropType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum MarketDemandLevel {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High");

        private final String displayName;

        MarketDemandLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Crop() {
        this.id = generateAlphanumericId();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Crop(String cropName, CropType cropType, Integer growingPeriodDays,
                String plantingSeason, String harvestSeason) {
        this();
        this.cropName = cropName;
        this.cropType = cropType;
        this.growingPeriodDays = growingPeriodDays;
        this.plantingSeason = plantingSeason;
        this.harvestSeason = harvestSeason;
    }

    // Method to generate alphanumeric ID
    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Add "CR" prefix for Crop
        sb.append("CR");

        // Timestamp-based prefix to ensure uniqueness
        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 6);
        sb.append(shortTimestamp);

        // Add random characters
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    // JPA lifecycle methods
    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = generateAlphanumericId();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public CropType getCropType() {
        return cropType;
    }

    public void setCropType(CropType cropType) {
        this.cropType = cropType;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public Integer getGrowingPeriodDays() {
        return growingPeriodDays;
    }

    public void setGrowingPeriodDays(Integer growingPeriodDays) {
        this.growingPeriodDays = growingPeriodDays;
    }

    public String getPlantingSeason() {
        return plantingSeason;
    }

    public void setPlantingSeason(String plantingSeason) {
        this.plantingSeason = plantingSeason;
    }

    public String getHarvestSeason() {
        return harvestSeason;
    }

    public void setHarvestSeason(String harvestSeason) {
        this.harvestSeason = harvestSeason;
    }

    public BigDecimal getWaterRequirement() {
        return waterRequirement;
    }

    public void setWaterRequirement(BigDecimal waterRequirement) {
        this.waterRequirement = waterRequirement;
    }

    public String getClimaticRequirement() {
        return climaticRequirement;
    }

    public void setClimaticRequirement(String climaticRequirement) {
        this.climaticRequirement = climaticRequirement;
    }

    public BigDecimal getSoilPhMin() {
        return soilPhMin;
    }

    public void setSoilPhMin(BigDecimal soilPhMin) {
        this.soilPhMin = soilPhMin;
    }

    public BigDecimal getSoilPhMax() {
        return soilPhMax;
    }

    public void setSoilPhMax(BigDecimal soilPhMax) {
        this.soilPhMax = soilPhMax;
    }

    public BigDecimal getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(BigDecimal temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public BigDecimal getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(BigDecimal temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public BigDecimal getRainfallRequirement() {
        return rainfallRequirement;
    }

    public void setRainfallRequirement(BigDecimal rainfallRequirement) {
        this.rainfallRequirement = rainfallRequirement;
    }

    public MarketDemandLevel getMarketDemandLevel() {
        return marketDemandLevel;
    }

    public void setMarketDemandLevel(MarketDemandLevel marketDemandLevel) {
        this.marketDemandLevel = marketDemandLevel;
    }

    public String getNutritionalValue() {
        return nutritionalValue;
    }

    public void setNutritionalValue(String nutritionalValue) {
        this.nutritionalValue = nutritionalValue;
    }

    public Integer getStorageLifeDays() {
        return storageLifeDays;
    }

    public void setStorageLifeDays(Integer storageLifeDays) {
        this.storageLifeDays = storageLifeDays;
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


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility methods
    public String getGrowingPeriodInMonths() {
        if (growingPeriodDays == null) return "Unknown";

        double months = growingPeriodDays / 30.0;
        if (months < 1) {
            return growingPeriodDays + " days";
        } else if (months < 12) {
            return String.format("%.1f months", months);
        } else {
            return String.format("%.1f years", months / 12);
        }
    }

    public String getStorageLifeDisplay() {
        if (storageLifeDays == null) return "Unknown";

        if (storageLifeDays < 30) {
            return storageLifeDays + " days";
        } else if (storageLifeDays < 365) {
            return String.format("%.1f months", storageLifeDays / 30.0);
        } else {
            return String.format("%.1f years", storageLifeDays / 365.0);
        }
    }

    public String getTemperatureRange() {
        if (temperatureMin == null && temperatureMax == null) {
            return "Not specified";
        } else if (temperatureMin == null) {
            return "Max: " + temperatureMax + "°C";
        } else if (temperatureMax == null) {
            return "Min: " + temperatureMin + "°C";
        } else {
            return temperatureMin + "°C - " + temperatureMax + "°C";
        }
    }

    public String getPhRange() {
        if (soilPhMin == null && soilPhMax == null) {
            return "Not specified";
        } else if (soilPhMin == null) {
            return "Max pH: " + soilPhMax;
        } else if (soilPhMax == null) {
            return "Min pH: " + soilPhMin;
        } else {
            return "pH " + soilPhMin + " - " + soilPhMax;
        }
    }

    public boolean isHighDemand() {
        return marketDemandLevel == MarketDemandLevel.HIGH;
    }

    public boolean isLongStorageLife() {
        return storageLifeDays != null && storageLifeDays > 365;
    }

    public boolean isShortGrowingPeriod() {
        return growingPeriodDays != null && growingPeriodDays <= 90;
    }

    public boolean isLongGrowingPeriod() {
        return growingPeriodDays != null && growingPeriodDays >= 365;
    }

    public String getWaterRequirementDisplay() {
        return waterRequirement != null ? waterRequirement + " mm/season" : "Not specified";
    }

    public String getRainfallRequirementDisplay() {
        return rainfallRequirement != null ? rainfallRequirement + " mm/year" : "Not specified";
    }

    public String getFullName() {
        if (variety != null && !variety.trim().isEmpty()) {
            return cropName + " (" + variety + ")";
        }
        return cropName;
    }

    // toString, equals and hashCode
    @Override
    public String toString() {
        return "Crop{" +
                "id='" + id + '\'' +
                ", cropName='" + cropName + '\'' +
                ", cropType=" + cropType +
                ", variety='" + variety + '\'' +
                ", growingPeriodDays=" + growingPeriodDays +
                ", plantingSeason='" + plantingSeason + '\'' +
                ", harvestSeason='" + harvestSeason + '\'' +
                ", marketDemandLevel=" + marketDemandLevel +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crop crop = (Crop) o;
        return id != null ? id.equals(crop.id) : crop.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}