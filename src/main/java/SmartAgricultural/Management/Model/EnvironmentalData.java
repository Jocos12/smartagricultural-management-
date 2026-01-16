package SmartAgricultural.Management.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Entity
@Table(name = "environmental_data")
public class EnvironmentalData {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "monitoring_code", length = 30, unique = true, nullable = false)
    @NotBlank(message = "Monitoring code is required")
    @Size(max = 30, message = "Monitoring code must not exceed 30 characters")
    private String monitoringCode;

    @Column(name = "region", length = 100, nullable = false)
    @NotBlank(message = "Region is required")
    @Size(max = 100, message = "Region must not exceed 100 characters")
    private String region;

    @Column(name = "district", length = 50)
    @Size(max = 50, message = "District must not exceed 50 characters")
    private String district;

    @Column(name = "sector", length = 50)
    @Size(max = 50, message = "Sector must not exceed 50 characters")
    private String sector;

    @Column(name = "latitude", precision = 10, scale = 8)
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Digits(integer = 2, fraction = 8, message = "Latitude format is invalid")
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Digits(integer = 3, fraction = 8, message = "Longitude format is invalid")
    private BigDecimal longitude;

    @Column(name = "record_date", nullable = false)
    @NotNull(message = "Record date is required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordDate;

    @Column(name = "data_source", length = 100, nullable = false)
    @NotBlank(message = "Data source is required")
    @Size(max = 100, message = "Data source must not exceed 100 characters")
    private String dataSource;

    @Column(name = "air_quality_index")
    @Min(value = 0, message = "Air quality index must be between 0 and 500")
    @Max(value = 500, message = "Air quality index must be between 0 and 500")
    private Integer airQualityIndex;

    @Column(name = "air_pollutants", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Air pollutants data must not exceed 3000 characters")
    private String airPollutants; // JSON: PM2.5, PM10, NO2, SO2, etc.

    @Column(name = "water_quality_index")
    @Min(value = 0, message = "Water quality index must be between 0 and 100")
    @Max(value = 100, message = "Water quality index must be between 0 and 100")
    private Integer waterQualityIndex;

    @Column(name = "water_ph", precision = 3, scale = 1)
    @DecimalMin(value = "0.0", message = "Water pH must be between 0 and 14")
    @DecimalMax(value = "14.0", message = "Water pH must be between 0 and 14")
    @Digits(integer = 2, fraction = 1, message = "Water pH format is invalid")
    private BigDecimal waterPh;

    @Column(name = "water_dissolved_oxygen", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Water dissolved oxygen must be positive")
    @Digits(integer = 3, fraction = 2, message = "Water dissolved oxygen format is invalid")
    private BigDecimal waterDissolvedOxygen; // mg/L

    @Column(name = "water_turbidity", precision = 6, scale = 2)
    @DecimalMin(value = "0.0", message = "Water turbidity must be positive")
    @Digits(integer = 4, fraction = 2, message = "Water turbidity format is invalid")
    private BigDecimal waterTurbidity; // NTU

    @Column(name = "water_contamination_level", length = 50)
    @Size(max = 50, message = "Water contamination level must not exceed 50 characters")
    private String waterContaminationLevel;

    @Column(name = "forest_coverage", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Forest coverage must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Forest coverage must be between 0 and 100")
    @Digits(integer = 3, fraction = 2, message = "Forest coverage format is invalid")
    private BigDecimal forestCoverage; // %

    @Column(name = "deforestation_rate", precision = 6, scale = 3)
    @DecimalMin(value = "0.0", message = "Deforestation rate must be positive")
    @Digits(integer = 3, fraction = 3, message = "Deforestation rate format is invalid")
    private BigDecimal deforestationRate; // %/year

    @Column(name = "reforestation_area", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Reforestation area must be positive")
    @Digits(integer = 6, fraction = 2, message = "Reforestation area format is invalid")
    private BigDecimal reforestationArea; // hectares

    @Column(name = "carbon_stock", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Carbon stock must be positive")
    @Digits(integer = 8, fraction = 2, message = "Carbon stock format is invalid")
    private BigDecimal carbonStock; // tonnes CO2/hectare

    @Column(name = "carbon_emission", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Carbon emission must be positive")
    @Digits(integer = 8, fraction = 2, message = "Carbon emission format is invalid")
    private BigDecimal carbonEmission; // tonnes CO2

    @Column(name = "carbon_sequestration", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Carbon sequestration must be positive")
    @Digits(integer = 6, fraction = 2, message = "Carbon sequestration format is invalid")
    private BigDecimal carbonSequestration; // tonnes CO2/year

    @Column(name = "biodiversity_index", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Biodiversity index must be positive")
    @Digits(integer = 3, fraction = 2, message = "Biodiversity index format is invalid")
    private BigDecimal biodiversityIndex; // Shannon index or similar

    @Column(name = "species_count")
    @Min(value = 0, message = "Species count must be positive")
    private Integer speciesCount;

    @Column(name = "endangered_species_count")
    @Min(value = 0, message = "Endangered species count must be positive")
    private Integer endangeredSpeciesCount;

    @Column(name = "land_use_agriculture", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Land use agriculture must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Land use agriculture must be between 0 and 100")
    @Digits(integer = 3, fraction = 2, message = "Land use agriculture format is invalid")
    private BigDecimal landUseAgriculture; // %

    @Column(name = "land_use_forest", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Land use forest must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Land use forest must be between 0 and 100")
    @Digits(integer = 3, fraction = 2, message = "Land use forest format is invalid")
    private BigDecimal landUseForest; // %

    @Column(name = "land_use_urban", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Land use urban must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Land use urban must be between 0 and 100")
    @Digits(integer = 3, fraction = 2, message = "Land use urban format is invalid")
    private BigDecimal landUseUrban; // %

    @Column(name = "land_use_water", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Land use water must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Land use water must be between 0 and 100")
    @Digits(integer = 3, fraction = 2, message = "Land use water format is invalid")
    private BigDecimal landUseWater; // %

    @Column(name = "soil_erosion_rate", precision = 6, scale = 2)
    @DecimalMin(value = "0.0", message = "Soil erosion rate must be positive")
    @Digits(integer = 4, fraction = 2, message = "Soil erosion rate format is invalid")
    private BigDecimal soilErosionRate; // tonnes/hectare/year

    @Column(name = "soil_organic_matter", precision = 4, scale = 2)
    @DecimalMin(value = "0.0", message = "Soil organic matter must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Soil organic matter must be between 0 and 100")
    @Digits(integer = 2, fraction = 2, message = "Soil organic matter format is invalid")
    private BigDecimal soilOrganicMatter; // %

    @Column(name = "soil_compaction_level", length = 50)
    @Size(max = 50, message = "Soil compaction level must not exceed 50 characters")
    private String soilCompactionLevel;

    @Column(name = "vegetation_health_index", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Vegetation health index must be between 0 and 1")
    @DecimalMax(value = "1.0", message = "Vegetation health index must be between 0 and 1")
    @Digits(integer = 1, fraction = 2, message = "Vegetation health index format is invalid")
    private BigDecimal vegetationHealthIndex; // NDVI or similar

    @Column(name = "agricultural_intensity")
    @Min(value = 1, message = "Agricultural intensity must be between 1 and 10")
    @Max(value = 10, message = "Agricultural intensity must be between 1 and 10")
    private Integer agriculturalIntensity;

    @Column(name = "pesticide_residue_level", precision = 8, scale = 4)
    @DecimalMin(value = "0.0", message = "Pesticide residue level must be positive")
    @Digits(integer = 4, fraction = 4, message = "Pesticide residue level format is invalid")
    private BigDecimal pesticideResidueLevel; // mg/kg

    @Column(name = "fertilizer_runoff_level", precision = 6, scale = 2)
    @DecimalMin(value = "0.0", message = "Fertilizer runoff level must be positive")
    @Digits(integer = 4, fraction = 2, message = "Fertilizer runoff level format is invalid")
    private BigDecimal fertilizerRunoffLevel; // mg/L nitrates

    @Column(name = "groundwater_level", precision = 6, scale = 2)
    @DecimalMin(value = "0.0", message = "Groundwater level must be positive")
    @Digits(integer = 4, fraction = 2, message = "Groundwater level format is invalid")
    private BigDecimal groundwaterLevel; // meters

    @Column(name = "surface_water_availability", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Surface water availability must be positive")
    @Digits(integer = 6, fraction = 2, message = "Surface water availability format is invalid")
    private BigDecimal surfaceWaterAvailability; // m³

    @Column(name = "climate_resilience_score")
    @Min(value = 1, message = "Climate resilience score must be between 1 and 100")
    @Max(value = 100, message = "Climate resilience score must be between 1 and 100")
    private Integer climateResilienceScore;

    @Column(name = "ecosystem_services_value", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "Ecosystem services value must be positive")
    @Digits(integer = 10, fraction = 2, message = "Ecosystem services value format is invalid")
    private BigDecimal ecosystemServicesValue; // USD/hectare/year

    @Enumerated(EnumType.STRING)
    @Column(name = "environmental_risk_level")
    private EnvironmentalRiskLevel environmentalRiskLevel = EnvironmentalRiskLevel.LOW;

    @Column(name = "sustainability_indicators", columnDefinition = "TEXT")
    @Size(max = 5000, message = "Sustainability indicators must not exceed 5000 characters")
    private String sustainabilityIndicators; // JSON

    @Column(name = "conservation_measures", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Conservation measures must not exceed 3000 characters")
    private String conservationMeasures;

    @Column(name = "restoration_needs", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Restoration needs must not exceed 3000 characters")
    private String restorationNeeds;

    @Column(name = "monitoring_frequency", length = 50)
    @Size(max = 50, message = "Monitoring frequency must not exceed 50 characters")
    private String monitoringFrequency;

    @Column(name = "next_monitoring_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextMonitoringDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_quality")
    private DataQuality dataQuality = DataQuality.GOOD;

    @Enumerated(EnumType.STRING)
    @Column(name = "validation_status")
    private ValidationStatus validationStatus = ValidationStatus.PENDING;

    @Column(name = "validated_by", length = 20)
    private String validatedBy;

    @Column(name = "validation_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validationDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Notes must not exceed 3000 characters")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by", insertable = false, updatable = false)
    @JsonIgnore
    private User validator;

    // Enums
    public enum EnvironmentalRiskLevel {
        LOW("Low Risk", 1),
        MEDIUM("Medium Risk", 2),
        HIGH("High Risk", 3),
        CRITICAL("Critical Risk", 4);

        private final String displayName;
        private final int riskLevel;

        EnvironmentalRiskLevel(String displayName, int riskLevel) {
            this.displayName = displayName;
            this.riskLevel = riskLevel;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getRiskLevel() {
            return riskLevel;
        }

        public boolean isHighRisk() {
            return this == HIGH || this == CRITICAL;
        }

        public boolean isCritical() {
            return this == CRITICAL;
        }

        public boolean requiresAction() {
            return this == MEDIUM || this == HIGH || this == CRITICAL;
        }
    }

    public enum DataQuality {
        EXCELLENT("Excellent", 4),
        GOOD("Good", 3),
        FAIR("Fair", 2),
        POOR("Poor", 1);

        private final String displayName;
        private final int qualityScore;

        DataQuality(String displayName, int qualityScore) {
            this.displayName = displayName;
            this.qualityScore = qualityScore;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getQualityScore() {
            return qualityScore;
        }

        public boolean isReliable() {
            return this == EXCELLENT || this == GOOD;
        }

        public boolean needsImprovement() {
            return this == FAIR || this == POOR;
        }
    }

    public enum ValidationStatus {
        VALIDATED("Validated"),
        PENDING("Pending Validation"),
        REJECTED("Rejected");

        private final String displayName;

        ValidationStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isValidated() {
            return this == VALIDATED;
        }

        public boolean isPending() {
            return this == PENDING;
        }

        public boolean isRejected() {
            return this == REJECTED;
        }
    }

    public enum DataSource {
        SATELLITE("Satellite Data"),
        GROUND_STATION("Ground Station"),
        SURVEY("Field Survey"),
        REMOTE_SENSING("Remote Sensing"),
        AUTOMATED_SENSORS("Automated Sensors"),
        MANUAL_COLLECTION("Manual Collection");

        private final String displayName;

        DataSource(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isAutomated() {
            return this == SATELLITE || this == AUTOMATED_SENSORS || this == REMOTE_SENSING;
        }

        public boolean isManual() {
            return this == SURVEY || this == MANUAL_COLLECTION;
        }
    }

    public enum MonitoringFrequency {
        DAILY("Daily"),
        WEEKLY("Weekly"),
        MONTHLY("Monthly"),
        QUARTERLY("Quarterly"),
        ANNUALLY("Annually"),
        ON_DEMAND("On Demand");

        private final String displayName;

        MonitoringFrequency(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isRegular() {
            return this != ON_DEMAND;
        }

        public int getDaysInterval() {
            switch (this) {
                case DAILY:
                    return 1;
                case WEEKLY:
                    return 7;
                case MONTHLY:
                    return 30;
                case QUARTERLY:
                    return 90;
                case ANNUALLY:
                    return 365;
                default:
                    return -1;
            }
        }
    }

    // Constructors
    public EnvironmentalData() {
        this.id = generateAlphanumericId();
        this.monitoringCode = generateMonitoringCode();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.recordDate = LocalDateTime.now();
    }

    public EnvironmentalData(String region, String dataSource, LocalDateTime recordDate) {
        this();
        this.region = region;
        this.dataSource = dataSource;
        this.recordDate = recordDate;
    }

    // Method to generate alphanumeric ID with mixed letters and numbers
    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Add "ED" prefix for Environmental Data
        sb.append("ED");

        // Timestamp-based part to ensure uniqueness (6 characters from timestamp)
        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 6);
        sb.append(shortTimestamp);

        // Add random mixed characters (letters and numbers)
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    // Method to generate monitoring code
    private String generateMonitoringCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Add "ENV" prefix
        sb.append("ENV");

        // Add date part (YYMMDD)
        LocalDateTime now = LocalDateTime.now();
        sb.append(String.format("%02d%02d%02d",
                now.getYear() % 100,
                now.getMonthValue(),
                now.getDayOfMonth()));

        // Add timestamp (4 characters)
        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 4);
        sb.append(shortTimestamp);

        // Add random characters
        for (int i = 0; i < 4; i++) {
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
        if (this.monitoringCode == null) {
            this.monitoringCode = generateMonitoringCode();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.recordDate == null) {
            this.recordDate = LocalDateTime.now();
        }

        // Calculate environmental risk level based on available data
        calculateEnvironmentalRiskLevel();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();

        // Recalculate environmental risk level
        calculateEnvironmentalRiskLevel();

        // Set validation date when status changes to validated or rejected
        if ((validationStatus == ValidationStatus.VALIDATED || validationStatus == ValidationStatus.REJECTED)
                && validationDate == null && validatedBy != null) {
            this.validationDate = LocalDateTime.now();
        }
    }

    // Utility methods
    private void calculateEnvironmentalRiskLevel() {
        int riskScore = 0;
        int factorsConsidered = 0;

        // Air quality assessment
        if (airQualityIndex != null) {
            factorsConsidered++;
            if (airQualityIndex > 150) riskScore += 3;
            else if (airQualityIndex > 100) riskScore += 2;
            else if (airQualityIndex > 50) riskScore += 1;
        }

        // Water quality assessment
        if (waterQualityIndex != null) {
            factorsConsidered++;
            if (waterQualityIndex < 40) riskScore += 3;
            else if (waterQualityIndex < 60) riskScore += 2;
            else if (waterQualityIndex < 80) riskScore += 1;
        }

        // Deforestation rate assessment
        if (deforestationRate != null) {
            factorsConsidered++;
            if (deforestationRate.compareTo(new BigDecimal("5.0")) > 0) riskScore += 3;
            else if (deforestationRate.compareTo(new BigDecimal("2.0")) > 0) riskScore += 2;
            else if (deforestationRate.compareTo(new BigDecimal("0.5")) > 0) riskScore += 1;
        }

        // Soil erosion assessment
        if (soilErosionRate != null) {
            factorsConsidered++;
            if (soilErosionRate.compareTo(new BigDecimal("10.0")) > 0) riskScore += 3;
            else if (soilErosionRate.compareTo(new BigDecimal("5.0")) > 0) riskScore += 2;
            else if (soilErosionRate.compareTo(new BigDecimal("2.0")) > 0) riskScore += 1;
        }

        // Biodiversity assessment
        if (endangeredSpeciesCount != null && speciesCount != null && speciesCount > 0) {
            factorsConsidered++;
            double endangeredRatio = (double) endangeredSpeciesCount / speciesCount;
            if (endangeredRatio > 0.3) riskScore += 3;
            else if (endangeredRatio > 0.2) riskScore += 2;
            else if (endangeredRatio > 0.1) riskScore += 1;
        }

        if (factorsConsidered > 0) {
            double averageRisk = (double) riskScore / factorsConsidered;
            if (averageRisk >= 2.5) {
                this.environmentalRiskLevel = EnvironmentalRiskLevel.CRITICAL;
            } else if (averageRisk >= 1.5) {
                this.environmentalRiskLevel = EnvironmentalRiskLevel.HIGH;
            } else if (averageRisk >= 0.5) {
                this.environmentalRiskLevel = EnvironmentalRiskLevel.MEDIUM;
            } else {
                this.environmentalRiskLevel = EnvironmentalRiskLevel.LOW;
            }
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMonitoringCode() {
        return monitoringCode;
    }

    public void setMonitoringCode(String monitoringCode) {
        this.monitoringCode = monitoringCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public Integer getAirQualityIndex() {
        return airQualityIndex;
    }

    public void setAirQualityIndex(Integer airQualityIndex) {
        this.airQualityIndex = airQualityIndex;
    }

    public String getAirPollutants() {
        return airPollutants;
    }

    public void setAirPollutants(String airPollutants) {
        this.airPollutants = airPollutants;
    }

    public Integer getWaterQualityIndex() {
        return waterQualityIndex;
    }

    public void setWaterQualityIndex(Integer waterQualityIndex) {
        this.waterQualityIndex = waterQualityIndex;
    }

    public BigDecimal getWaterPh() {
        return waterPh;
    }

    public void setWaterPh(BigDecimal waterPh) {
        this.waterPh = waterPh;
    }

    public BigDecimal getWaterDissolvedOxygen() {
        return waterDissolvedOxygen;
    }

    public void setWaterDissolvedOxygen(BigDecimal waterDissolvedOxygen) {
        this.waterDissolvedOxygen = waterDissolvedOxygen;
    }

    public BigDecimal getWaterTurbidity() {
        return waterTurbidity;
    }

    public void setWaterTurbidity(BigDecimal waterTurbidity) {
        this.waterTurbidity = waterTurbidity;
    }

    public String getWaterContaminationLevel() {
        return waterContaminationLevel;
    }

    public void setWaterContaminationLevel(String waterContaminationLevel) {
        this.waterContaminationLevel = waterContaminationLevel;
    }

    public BigDecimal getForestCoverage() {
        return forestCoverage;
    }

    public void setForestCoverage(BigDecimal forestCoverage) {
        this.forestCoverage = forestCoverage;
    }

    public BigDecimal getDeforestationRate() {
        return deforestationRate;
    }

    public void setDeforestationRate(BigDecimal deforestationRate) {
        this.deforestationRate = deforestationRate;
    }

    public BigDecimal getReforestationArea() {
        return reforestationArea;
    }

    public void setReforestationArea(BigDecimal reforestationArea) {
        this.reforestationArea = reforestationArea;
    }

    public BigDecimal getCarbonStock() {
        return carbonStock;
    }

    public void setCarbonStock(BigDecimal carbonStock) {
        this.carbonStock = carbonStock;
    }

    public BigDecimal getCarbonEmission() {
        return carbonEmission;
    }

    public void setCarbonEmission(BigDecimal carbonEmission) {
        this.carbonEmission = carbonEmission;
    }

    public BigDecimal getCarbonSequestration() {
        return carbonSequestration;
    }

    public void setCarbonSequestration(BigDecimal carbonSequestration) {
        this.carbonSequestration = carbonSequestration;
    }

    public BigDecimal getBiodiversityIndex() {
        return biodiversityIndex;
    }

    public void setBiodiversityIndex(BigDecimal biodiversityIndex) {
        this.biodiversityIndex = biodiversityIndex;
    }

    public Integer getSpeciesCount() {
        return speciesCount;
    }

    public void setSpeciesCount(Integer speciesCount) {
        this.speciesCount = speciesCount;
    }

    public Integer getEndangeredSpeciesCount() {
        return endangeredSpeciesCount;
    }

    public void setEndangeredSpeciesCount(Integer endangeredSpeciesCount) {
        this.endangeredSpeciesCount = endangeredSpeciesCount;
    }

    public BigDecimal getLandUseAgriculture() {
        return landUseAgriculture;
    }

    public void setLandUseAgriculture(BigDecimal landUseAgriculture) {
        this.landUseAgriculture = landUseAgriculture;
    }

    public BigDecimal getLandUseForest() {
        return landUseForest;
    }

    public void setLandUseForest(BigDecimal landUseForest) {
        this.landUseForest = landUseForest;
    }

    public BigDecimal getLandUseUrban() {
        return landUseUrban;
    }

    public void setLandUseUrban(BigDecimal landUseUrban) {
        this.landUseUrban = landUseUrban;
    }

    public BigDecimal getLandUseWater() {
        return landUseWater;
    }

    public void setLandUseWater(BigDecimal landUseWater) {
        this.landUseWater = landUseWater;
    }

    public BigDecimal getSoilErosionRate() {
        return soilErosionRate;
    }

    public void setSoilErosionRate(BigDecimal soilErosionRate) {
        this.soilErosionRate = soilErosionRate;
    }

    public BigDecimal getSoilOrganicMatter() {
        return soilOrganicMatter;
    }

    public void setSoilOrganicMatter(BigDecimal soilOrganicMatter) {
        this.soilOrganicMatter = soilOrganicMatter;
    }

    public String getSoilCompactionLevel() {
        return soilCompactionLevel;
    }

    public void setSoilCompactionLevel(String soilCompactionLevel) {
        this.soilCompactionLevel = soilCompactionLevel;
    }

    public BigDecimal getVegetationHealthIndex() {
        return vegetationHealthIndex;
    }

    public void setVegetationHealthIndex(BigDecimal vegetationHealthIndex) {
        this.vegetationHealthIndex = vegetationHealthIndex;
    }

    public Integer getAgriculturalIntensity() {
        return agriculturalIntensity;
    }

    public void setAgriculturalIntensity(Integer agriculturalIntensity) {
        this.agriculturalIntensity = agriculturalIntensity;
    }

    public BigDecimal getPesticideResidueLevel() {
        return pesticideResidueLevel;
    }

    public void setPesticideResidueLevel(BigDecimal pesticideResidueLevel) {
        this.pesticideResidueLevel = pesticideResidueLevel;
    }

    public BigDecimal getFertilizerRunoffLevel() {
        return fertilizerRunoffLevel;
    }

    public void setFertilizerRunoffLevel(BigDecimal fertilizerRunoffLevel) {
        this.fertilizerRunoffLevel = fertilizerRunoffLevel;
    }

    public BigDecimal getGroundwaterLevel() {
        return groundwaterLevel;
    }

    public void setGroundwaterLevel(BigDecimal groundwaterLevel) {
        this.groundwaterLevel = groundwaterLevel;
    }

    public BigDecimal getSurfaceWaterAvailability() {
        return surfaceWaterAvailability;
    }

    public void setSurfaceWaterAvailability(BigDecimal surfaceWaterAvailability) {
        this.surfaceWaterAvailability = surfaceWaterAvailability;
    }

    public Integer getClimateResilienceScore() {
        return climateResilienceScore;
    }

    public void setClimateResilienceScore(Integer climateResilienceScore) {
        this.climateResilienceScore = climateResilienceScore;
    }

    public BigDecimal getEcosystemServicesValue() {
        return ecosystemServicesValue;
    }

    public void setEcosystemServicesValue(BigDecimal ecosystemServicesValue) {
        this.ecosystemServicesValue = ecosystemServicesValue;
    }

    public EnvironmentalRiskLevel getEnvironmentalRiskLevel() {
        return environmentalRiskLevel;
    }

    public void setEnvironmentalRiskLevel(EnvironmentalRiskLevel environmentalRiskLevel) {
        this.environmentalRiskLevel = environmentalRiskLevel;
    }

    public String getSustainabilityIndicators() {
        return sustainabilityIndicators;
    }

    public void setSustainabilityIndicators(String sustainabilityIndicators) {
        this.sustainabilityIndicators = sustainabilityIndicators;
    }

    public String getConservationMeasures() {
        return conservationMeasures;
    }

    public void setConservationMeasures(String conservationMeasures) {
        this.conservationMeasures = conservationMeasures;
    }

    public String getRestorationNeeds() {
        return restorationNeeds;
    }

    public void setRestorationNeeds(String restorationNeeds) {
        this.restorationNeeds = restorationNeeds;
    }

    public String getMonitoringFrequency() {
        return monitoringFrequency;
    }

    public void setMonitoringFrequency(String monitoringFrequency) {
        this.monitoringFrequency = monitoringFrequency;
    }

    public LocalDate getNextMonitoringDate() {
        return nextMonitoringDate;
    }

    public void setNextMonitoringDate(LocalDate nextMonitoringDate) {
        this.nextMonitoringDate = nextMonitoringDate;
    }

    public DataQuality getDataQuality() {
        return dataQuality;
    }

    public void setDataQuality(DataQuality dataQuality) {
        this.dataQuality = dataQuality;
    }

    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(ValidationStatus validationStatus) {
        this.validationStatus = validationStatus;
    }

    public String getValidatedBy() {
        return validatedBy;
    }

    public void setValidatedBy(String validatedBy) {
        this.validatedBy = validatedBy;
    }

    public LocalDateTime getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(LocalDateTime validationDate) {
        this.validationDate = validationDate;
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

    public User getValidator() {
        return validator;
    }

    public void setValidator(User validator) {
        this.validator = validator;
    }
}