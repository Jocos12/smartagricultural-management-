package SmartAgricultural.Management.dto;

import SmartAgricultural.Management.Model.EnvironmentalData.EnvironmentalRiskLevel;
import SmartAgricultural.Management.Model.EnvironmentalData.DataQuality;
import SmartAgricultural.Management.Model.EnvironmentalData.ValidationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EnvironmentalDataDTO {

    private String id;
    private String monitoringCode;
    private String region;
    private String district;
    private String sector;
    private BigDecimal latitude;
    private BigDecimal longitude;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordDate;

    private String dataSource;

    // Air Quality
    private Integer airQualityIndex;
    private String airPollutants;

    // Water Quality
    private Integer waterQualityIndex;
    private BigDecimal waterPh;
    private BigDecimal waterDissolvedOxygen;
    private BigDecimal waterTurbidity;
    private String waterContaminationLevel;

    // Forest Data
    private BigDecimal forestCoverage;
    private BigDecimal deforestationRate;
    private BigDecimal reforestationArea;

    // Carbon Data
    private BigDecimal carbonStock;
    private BigDecimal carbonEmission;
    private BigDecimal carbonSequestration;

    // Biodiversity
    private BigDecimal biodiversityIndex;
    private Integer speciesCount;
    private Integer endangeredSpeciesCount;

    // Land Use
    private BigDecimal landUseAgriculture;
    private BigDecimal landUseForest;
    private BigDecimal landUseUrban;
    private BigDecimal landUseWater;

    // Soil Data
    private BigDecimal soilErosionRate;
    private BigDecimal soilOrganicMatter;
    private String soilCompactionLevel;

    // Vegetation
    private BigDecimal vegetationHealthIndex;
    private Integer agriculturalIntensity;

    // Contamination
    private BigDecimal pesticideResidueLevel;
    private BigDecimal fertilizerRunoffLevel;

    // Water Resources
    private BigDecimal groundwaterLevel;
    private BigDecimal surfaceWaterAvailability;

    // Climate and Ecosystem
    private Integer climateResilienceScore;
    private BigDecimal ecosystemServicesValue;

    // Assessment Fields
    private EnvironmentalRiskLevel environmentalRiskLevel;
    private String sustainabilityIndicators;
    private String conservationMeasures;
    private String restorationNeeds;

    // Monitoring
    private String monitoringFrequency;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextMonitoringDate;

    // Data Management
    private DataQuality dataQuality;
    private ValidationStatus validationStatus;
    private String validatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validationDate;

    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // Constructors
    public EnvironmentalDataDTO() {}

    // Getters and Setters
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
}