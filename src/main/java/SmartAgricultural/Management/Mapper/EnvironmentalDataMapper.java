/*
package SmartAgricultural.Management.Mapper;

import SmartAgricultural.Management.Model.EnvironmentalData;
import SmartAgricultural.Management.dto.EnvironmentalDataDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EnvironmentalDataMapper {

    */
/**
     * Convert EnvironmentalData entity to DTO
     *//*

    public EnvironmentalDataDTO toDTO(EnvironmentalData entity) {
        if (entity == null) {
            return null;
        }

        EnvironmentalDataDTO dto = new EnvironmentalDataDTO();

        // Basic information
        dto.setId(entity.getId());
        dto.setMonitoringCode(entity.getMonitoringCode());
        dto.setRegion(entity.getRegion());
        dto.setDistrict(entity.getDistrict());
        dto.setSector(entity.getSector());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setRecordDate(entity.getRecordDate());
        dto.setDataSource(entity.getDataSource());

        // Air Quality
        dto.setAirQualityIndex(entity.getAirQualityIndex());
        dto.setAirPollutants(entity.getAirPollutants());

        // Water Quality
        dto.setWaterQualityIndex(entity.getWaterQualityIndex());
        dto.setWaterPh(entity.getWaterPh());
        dto.setWaterDissolvedOxygen(entity.getWaterDissolvedOxygen());
        dto.setWaterTurbidity(entity.getWaterTurbidity());
        dto.setWaterContaminationLevel(entity.getWaterContaminationLevel());

        // Forest Data
        dto.setForestCoverage(entity.getForestCoverage());
        dto.setDeforestationRate(entity.getDeforestationRate());
        dto.setReforestationArea(entity.getReforestationArea());

        // Carbon Data
        dto.setCarbonStock(entity.getCarbonStock());
        dto.setCarbonEmission(entity.getCarbonEmission());
        dto.setCarbonSequestration(entity.getCarbonSequestration());

        // Biodiversity
        dto.setBiodiversityIndex(entity.getBiodiversityIndex());
        dto.setSpeciesCount(entity.getSpeciesCount());
        dto.setEndangeredSpeciesCount(entity.getEndangeredSpeciesCount());

        // Land Use
        dto.setLandUseAgriculture(entity.getLandUseAgriculture());
        dto.setLandUseForest(entity.getLandUseForest());
        dto.setLandUseUrban(entity.getLandUseUrban());
        dto.setLandUseWater(entity.getLandUseWater());

        // Soil Data
        dto.setSoilErosionRate(entity.getSoilErosionRate());
        dto.setSoilOrganicMatter(entity.getSoilOrganicMatter());
        dto.setSoilCompactionLevel(entity.getSoilCompactionLevel());

        // Vegetation
        dto.setVegetationHealthIndex(entity.getVegetationHealthIndex());
        dto.setAgriculturalIntensity(entity.getAgriculturalIntensity());

        // Contamination
        dto.setPesticideResidueLevel(entity.getPesticideResidueLevel());
        dto.setFertilizerRunoffLevel(entity.getFertilizerRunoffLevel());

        // Water Resources
        dto.setGroundwaterLevel(entity.getGroundwaterLevel());
        dto.setSurfaceWaterAvailability(entity.getSurfaceWaterAvailability());

        // Climate and Ecosystem
        dto.setClimateResilienceScore(entity.getClimateResilienceScore());
        dto.setEcosystemServicesValue(entity.getEcosystemServicesValue());

        // Assessment Fields
        dto.setEnvironmentalRiskLevel(entity.getEnvironmentalRiskLevel());
        dto.setSustainabilityIndicators(entity.getSustainabilityIndicators());
        dto.setConservationMeasures(entity.getConservationMeasures());
        dto.setRestorationNeeds(entity.getRestorationNeeds());

        // Monitoring
        dto.setMonitoringFrequency(entity.getMonitoringFrequency());
        dto.setNextMonitoringDate(entity.getNextMonitoringDate());

        // Data Management
        dto.setDataQuality(entity.getDataQuality());
        dto.setValidationStatus(entity.getValidationStatus());
        dto.setValidatedBy(entity.getValidatedBy());
        dto.setValidationDate(entity.getValidationDate());
        dto.setNotes(entity.getNotes());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    */
/**
     * Convert DTO to EnvironmentalData entity
     *//*

    public EnvironmentalData toEntity(EnvironmentalDataDTO dto) {
        if (dto == null) {
            return null;
        }

        EnvironmentalData entity = new EnvironmentalData();

        // Basic information
        entity.setId(dto.getId());
        entity.setMonitoringCode(dto.getMonitoringCode());
        entity.setRegion(dto.getRegion());
        entity.setDistrict(dto.getDistrict());
        entity.setSector(dto.getSector());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setRecordDate(dto.getRecordDate());
        entity.setDataSource(dto.getDataSource());

        // Air Quality
        entity.setAirQualityIndex(dto.getAirQualityIndex());
        entity.setAirPollutants(dto.getAirPollutants());

        // Water Quality
        entity.setWaterQualityIndex(dto.getWaterQualityIndex());
        entity.setWaterPh(dto.getWaterPh());
        entity.setWaterDissolvedOxygen(dto.getWaterDissolvedOxygen());
        entity.setWaterTurbidity(dto.getWaterTurbidity());
        entity.setWaterContaminationLevel(dto.getWaterContaminationLevel());

        // Forest Data
        entity.setForestCoverage(dto.getForestCoverage());
        entity.setDeforestationRate(dto.getDeforestationRate());
        entity.setReforestationArea(dto.getReforestationArea());

        // Carbon Data
        entity.setCarbonStock(dto.getCarbonStock());
        entity.setCarbonEmission(dto.getCarbonEmission());
        entity.setCarbonSequestration(dto.getCarbonSequestration());

        // Biodiversity
        entity.setBiodiversityIndex(dto.getBiodiversityIndex());
        entity.setSpeciesCount(dto.getSpeciesCount());
        entity.setEndangeredSpeciesCount(dto.getEndangeredSpeciesCount());

        // Land Use
        entity.setLandUseAgriculture(dto.getLandUseAgriculture());
        entity.setLandUseForest(dto.getLandUseForest());
        entity.setLandUseUrban(dto.getLandUseUrban());
        entity.setLandUseWater(dto.getLandUseWater());

        // Soil Data
        entity.setSoilErosionRate(dto.getSoilErosionRate());
        entity.setSoilOrganicMatter(dto.getSoilOrganicMatter());
        entity.setSoilCompactionLevel(dto.getSoilCompactionLevel());

        // Vegetation
        entity.setVegetationHealthIndex(dto.getVegetationHealthIndex());
        entity.setAgriculturalIntensity(dto.getAgriculturalIntensity());

        // Contamination
        entity.setPesticideResidueLevel(dto.getPesticideResidueLevel());
        entity.setFertilizerRunoffLevel(dto.getFertilizerRunoffLevel());

        // Water Resources
        entity.setGroundwaterLevel(dto.getGroundwaterLevel());
        entity.setSurfaceWaterAvailability(dto.getSurfaceWaterAvailability());

        // Climate and Ecosystem
        entity.setClimateResilienceScore(dto.getClimateResilienceScore());
        entity.setEcosystemServicesValue(dto.getEcosystemServicesValue());

        // Assessment Fields
        entity.setEnvironmentalRiskLevel(dto.getEnvironmentalRiskLevel());
        entity.setSustainabilityIndicators(dto.getSustainabilityIndicators());
        entity.setConservationMeasures(dto.getConservationMeasures());
        entity.setRestorationNeeds(dto.getRestorationNeeds());

        // Monitoring
        entity.setMonitoringFrequency(dto.getMonitoringFrequency());
        entity.setNextMonitoringDate(dto.getNextMonitoringDate());

        // Data Management
        entity.setDataQuality(dto.getDataQuality());
        entity.setValidationStatus(dto.getValidationStatus());
        entity.setValidatedBy(dto.getValidatedBy());
        entity.setValidationDate(dto.getValidationDate());
        entity.setNotes(dto.getNotes());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }

    */
/**
     * Convert list of entities to list of DTOs
     *//*

    public List<EnvironmentalDataDTO> toDTOList(List<EnvironmentalData> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    */
/**
     * Convert list of DTOs to list of entities
     *//*

    public List<EnvironmentalData> toEntityList(List<EnvironmentalDataDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    */
/**
     * Update entity with DTO data (excluding ID and audit fields)
     *//*

    public void updateEntityFromDTO(EnvironmentalData entity, EnvironmentalDataDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        // Don't update ID, createdAt, or system-generated fields
        entity.setRegion(dto.getRegion());
        entity.setDistrict(dto.getDistrict());
        entity.setSector(dto.getSector());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setRecordDate(dto.getRecordDate());
        entity.setDataSource(dto.getDataSource());

        // Air Quality
        entity.setAirQualityIndex(dto.getAirQualityIndex());
        entity.setAirPollutants(dto.getAirPollutants());

        // Water Quality
        entity.setWaterQualityIndex(dto.getWaterQualityIndex());
        entity.setWaterPh(dto.getWaterPh());
        entity.setWaterDissolvedOxygen(dto.getWaterDissolvedOxygen());
        entity.setWaterTurbidity(dto.getWaterTurbidity());
        entity.setWaterContaminationLevel(dto.getWaterContaminationLevel());

        // Forest Data
        entity.setForestCoverage(dto.getForestCoverage());
        entity.setDeforestationRate(dto.getDeforestationRate());
        entity.setReforestationArea(dto.getReforestationArea());

        // Carbon Data
        entity.setCarbonStock(dto.getCarbonStock());
        entity.setCarbonEmission(dto.getCarbonEmission());
        entity.setCarbonSequestration(dto.getCarbonSequestration());

        // Biodiversity
        entity.setBiodiversityIndex(dto.getBiodiversityIndex());
        entity.setSpeciesCount(dto.getSpeciesCount());
        entity.setEndangeredSpeciesCount(dto.getEndangeredSpeciesCount());

        // Land Use
        entity.setLandUseAgriculture(dto.getLandUseAgriculture());
        entity.setLandUseForest(dto.getLandUseForest());
        entity.setLandUseUrban(dto.getLandUseUrban());
        entity.setLandUseWater(dto.getLandUseWater());

        // Soil Data
        entity.setSoilErosionRate(dto.getSoilErosionRate());
        entity.setSoilOrganicMatter(dto.getSoilOrganicMatter());
        entity.setSoilCompactionLevel(dto.getSoilCompactionLevel());

        // Vegetation
        entity.setVegetationHealthIndex(dto.getVegetationHealthIndex());
        entity.setAgriculturalIntensity(dto.getAgriculturalIntensity());

        // Contamination
        entity.setPesticideResidueLevel(dto.getPesticideResidueLevel());
        entity.setFertilizerRunoffLevel(dto.getFertilizerRunoffLevel());

        // Water Resources
        entity.setGroundwaterLevel(dto.getGroundwaterLevel());
        entity.setSurfaceWaterAvailability(dto.getSurfaceWaterAvailability());

        // Climate and Ecosystem
        entity.setClimateResilienceScore(dto.getClimateResilienceScore());
        entity.setEcosystemServicesValue(dto.getEcosystemServicesValue());

        // Assessment Fields
        entity.setEnvironmentalRiskLevel(dto.getEnvironmentalRiskLevel());
        entity.setSustainabilityIndicators(dto.getSustainabilityIndicators());
        entity.setConservationMeasures(dto.getConservationMeasures());
        entity.setRestorationNeeds(dto.getRestorationNeeds());

        // Monitoring
        entity.setMonitoringFrequency(dto.getMonitoringFrequency());
        entity.setNextMonitoringDate(dto.getNextMonitoringDate());

        // Data Management (excluding validation fields which should be handled separately)
        entity.setDataQuality(dto.getDataQuality());
        entity.setNotes(dto.getNotes());
    }

    */
/**
     * Create a summary DTO with only essential fields
     *//*

    public EnvironmentalDataDTO toSummaryDTO(EnvironmentalData entity) {
        if (entity == null) {
            return null;
        }

        EnvironmentalDataDTO dto = new EnvironmentalDataDTO();

        // Essential identification fields
        dto.setId(entity.getId());
        dto.setMonitoringCode(entity.getMonitoringCode());
        dto.setRegion(entity.getRegion());
        dto.setDistrict(entity.getDistrict());
        dto.setSector(entity.getSector());
        dto.setRecordDate(entity.getRecordDate());

        // Key environmental indicators
        dto.setAirQualityIndex(entity.getAirQualityIndex());
        dto.setWaterQualityIndex(entity.getWaterQualityIndex());
        dto.setForestCoverage(entity.getForestCoverage());
        dto.setEnvironmentalRiskLevel(entity.getEnvironmentalRiskLevel());
        dto.setValidationStatus(entity.getValidationStatus());
        dto.setDataQuality(entity.getDataQuality());

        return dto;
    }

    */
/**
     * Create a list of summary DTOs
     *//*

    public List<EnvironmentalDataDTO> toSummaryDTOList(List<EnvironmentalData> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }
}*/
