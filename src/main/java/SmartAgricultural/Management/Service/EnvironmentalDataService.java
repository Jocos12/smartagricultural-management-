package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.EnvironmentalData;
import SmartAgricultural.Management.Model.EnvironmentalData.EnvironmentalRiskLevel;
import SmartAgricultural.Management.Model.EnvironmentalData.DataQuality;
import SmartAgricultural.Management.Model.EnvironmentalData.ValidationStatus;
import SmartAgricultural.Management.Repository.EnvironmentalDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EnvironmentalDataService {

    @Autowired
    private EnvironmentalDataRepository environmentalDataRepository;

    // CRUD Operations
    public EnvironmentalData save(EnvironmentalData environmentalData) {
        return environmentalDataRepository.save(environmentalData);
    }

    public List<EnvironmentalData> saveAll(List<EnvironmentalData> environmentalDataList) {
        return environmentalDataRepository.saveAll(environmentalDataList);
    }

    public Optional<EnvironmentalData> findById(String id) {
        return environmentalDataRepository.findById(id);
    }

    public Optional<EnvironmentalData> findByMonitoringCode(String monitoringCode) {
        return environmentalDataRepository.findByMonitoringCode(monitoringCode);
    }

    public List<EnvironmentalData> findAll() {
        return environmentalDataRepository.findAll();
    }

    public Page<EnvironmentalData> findAll(Pageable pageable) {
        return environmentalDataRepository.findAll(pageable);
    }

    public void deleteById(String id) {
        environmentalDataRepository.deleteById(id);
    }

    public void delete(EnvironmentalData environmentalData) {
        environmentalDataRepository.delete(environmentalData);
    }

    public boolean existsById(String id) {
        return environmentalDataRepository.existsById(id);
    }

    public long count() {
        return environmentalDataRepository.count();
    }

    // Geographic Queries
    public List<EnvironmentalData> findByRegion(String region) {
        return environmentalDataRepository.findByRegion(region);
    }

    public Page<EnvironmentalData> findByRegion(String region, Pageable pageable) {
        return environmentalDataRepository.findByRegion(region, pageable);
    }

    public List<EnvironmentalData> findByRegionAndDistrict(String region, String district) {
        return environmentalDataRepository.findByRegionAndDistrict(region, district);
    }

    public List<EnvironmentalData> findByRegionAndDistrictAndSector(String region, String district, String sector) {
        return environmentalDataRepository.findByRegionAndDistrictAndSector(region, district, sector);
    }

    public List<EnvironmentalData> findByLocationBounds(BigDecimal minLatitude, BigDecimal maxLatitude,
                                                        BigDecimal minLongitude, BigDecimal maxLongitude) {
        return environmentalDataRepository.findByLocationBounds(minLatitude, maxLatitude, minLongitude, maxLongitude);
    }

    // Risk Assessment Methods
    public List<EnvironmentalData> findByRiskLevel(EnvironmentalRiskLevel riskLevel) {
        return environmentalDataRepository.findByEnvironmentalRiskLevel(riskLevel);
    }

    public Page<EnvironmentalData> findByRiskLevel(EnvironmentalRiskLevel riskLevel, Pageable pageable) {
        return environmentalDataRepository.findByEnvironmentalRiskLevel(riskLevel, pageable);
    }

    public List<EnvironmentalData> findHighRiskAreas() {
        return environmentalDataRepository.findHighRiskAreas();
    }

    public List<EnvironmentalData> findCriticalRiskAreas() {
        return environmentalDataRepository.findCriticalRiskAreas();
    }

    public List<EnvironmentalData> findValidatedHighRiskAreas() {
        return environmentalDataRepository.findValidatedHighRiskAreas();
    }

    // Data Quality Management
    public List<EnvironmentalData> findByDataQuality(DataQuality dataQuality) {
        return environmentalDataRepository.findByDataQuality(dataQuality);
    }

    public List<EnvironmentalData> findReliableData() {
        return environmentalDataRepository.findReliableData();
    }

    public List<EnvironmentalData> findIncompleteRecords() {
        return environmentalDataRepository.findIncompleteRecords();
    }

    // Validation Management
    public List<EnvironmentalData> findByValidationStatus(ValidationStatus status) {
        return environmentalDataRepository.findByValidationStatus(status);
    }

    public Page<EnvironmentalData> findByValidationStatus(ValidationStatus status, Pageable pageable) {
        return environmentalDataRepository.findByValidationStatus(status, pageable);
    }

    public List<EnvironmentalData> findPendingValidation() {
        return environmentalDataRepository.findPendingValidation();
    }

    public EnvironmentalData validateData(String id, String validatorId, ValidationStatus status) {
        Optional<EnvironmentalData> optionalData = environmentalDataRepository.findById(id);
        if (optionalData.isPresent()) {
            EnvironmentalData data = optionalData.get();
            data.setValidationStatus(status);
            data.setValidatedBy(validatorId);
            data.setValidationDate(LocalDateTime.now());
            return environmentalDataRepository.save(data);
        }
        throw new RuntimeException("Environmental data not found with id: " + id);
    }

    // Date Range Queries
    public List<EnvironmentalData> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return environmentalDataRepository.findByRecordDateBetween(startDate, endDate);
    }

    public List<EnvironmentalData> findAfterDate(LocalDateTime date) {
        return environmentalDataRepository.findByRecordDateAfter(date);
    }

    public List<EnvironmentalData> findBeforeDate(LocalDateTime date) {
        return environmentalDataRepository.findByRecordDateBefore(date);
    }

    public List<EnvironmentalData> findValidatedDataByRegionAndDateRange(String region,
                                                                         LocalDateTime startDate,
                                                                         LocalDateTime endDate) {
        return environmentalDataRepository.findValidatedDataByRegionAndDateRange(region, startDate, endDate);
    }

    // Environmental Quality Queries
    public List<EnvironmentalData> findByAirQualityThreshold(Integer threshold) {
        return environmentalDataRepository.findByAirQualityIndexGreaterThan(threshold);
    }

    public List<EnvironmentalData> findByAirQualityRange(Integer min, Integer max) {
        return environmentalDataRepository.findByAirQualityIndexRange(min, max);
    }

    public List<EnvironmentalData> findByWaterQualityThreshold(Integer threshold) {
        return environmentalDataRepository.findByWaterQualityIndexLessThan(threshold);
    }

    public List<EnvironmentalData> findByWaterPhRange(BigDecimal minPh, BigDecimal maxPh) {
        return environmentalDataRepository.findByWaterPhRange(minPh, maxPh);
    }

    // Forest and Biodiversity
    public List<EnvironmentalData> findByLowForestCoverage(BigDecimal threshold) {
        return environmentalDataRepository.findByLowForestCoverage(threshold);
    }

    public List<EnvironmentalData> findByHighDeforestationRate(BigDecimal threshold) {
        return environmentalDataRepository.findByHighDeforestationRate(threshold);
    }

    public List<EnvironmentalData> findByHighEndangeredSpeciesCount(Integer threshold) {
        return environmentalDataRepository.findByHighEndangeredSpeciesCount(threshold);
    }

    // Climate and Carbon
    public List<EnvironmentalData> findByHighCarbonEmission(BigDecimal threshold) {
        return environmentalDataRepository.findByHighCarbonEmission(threshold);
    }

    public List<EnvironmentalData> findByLowClimateResilience(Integer threshold) {
        return environmentalDataRepository.findByLowClimateResilience(threshold);
    }

    // Agricultural Impact
    public List<EnvironmentalData> findByHighSoilErosion(BigDecimal threshold) {
        return environmentalDataRepository.findByHighSoilErosion(threshold);
    }

    public List<EnvironmentalData> findByHighPesticideResidue(BigDecimal threshold) {
        return environmentalDataRepository.findByHighPesticideResidue(threshold);
    }

    // Monitoring Management
    public List<EnvironmentalData> findDueForMonitoring() {
        return environmentalDataRepository.findDueForMonitoring(LocalDate.now());
    }

    public List<EnvironmentalData> findDueForMonitoring(LocalDate date) {
        return environmentalDataRepository.findDueForMonitoring(date);
    }

    // Statistical Methods
    public Double getAverageAirQualityByRegion(String region) {
        return environmentalDataRepository.getAverageAirQualityByRegion(region);
    }

    public Double getAverageWaterQualityByRegion(String region) {
        return environmentalDataRepository.getAverageWaterQualityByRegion(region);
    }

    public Double getAverageForestCoverageByRegion(String region) {
        return environmentalDataRepository.getAverageForestCoverageByRegion(region);
    }

    public Long countByRiskLevel(EnvironmentalRiskLevel riskLevel) {
        return environmentalDataRepository.countByRiskLevel(riskLevel);
    }

    // Geographic Reference Methods
    public List<String> getAllRegions() {
        return environmentalDataRepository.findAllRegions();
    }

    public List<String> getDistrictsByRegion(String region) {
        return environmentalDataRepository.findDistrictsByRegion(region);
    }

    public List<String> getSectorsByRegionAndDistrict(String region, String district) {
        return environmentalDataRepository.findSectorsByRegionAndDistrict(region, district);
    }

    // Trend Analysis
    public List<EnvironmentalData> getTrendDataByRegion(String region, LocalDateTime fromDate) {
        return environmentalDataRepository.findTrendDataByRegion(region, fromDate);
    }

    // Business Logic Methods
    public Map<EnvironmentalRiskLevel, Long> getRiskLevelStatistics() {
        Map<EnvironmentalRiskLevel, Long> stats = new HashMap<>();
        for (EnvironmentalRiskLevel level : EnvironmentalRiskLevel.values()) {
            stats.put(level, countByRiskLevel(level));
        }
        return stats;
    }

    public Map<String, Double> getRegionalAirQualityAverages() {
        return getAllRegions().stream()
                .collect(Collectors.toMap(
                        region -> region,
                        this::getAverageAirQualityByRegion,
                        (existing, replacement) -> existing
                )).entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    public Map<String, Double> getRegionalWaterQualityAverages() {
        return getAllRegions().stream()
                .collect(Collectors.toMap(
                        region -> region,
                        this::getAverageWaterQualityByRegion,
                        (existing, replacement) -> existing
                )).entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    public List<EnvironmentalData> getEnvironmentalAlerts() {
        List<EnvironmentalData> alerts = new ArrayList<>();
        alerts.addAll(findCriticalRiskAreas());
        alerts.addAll(findByAirQualityThreshold(150)); // Unhealthy air quality
        alerts.addAll(findByWaterQualityThreshold(40)); // Poor water quality
        alerts.addAll(findByHighDeforestationRate(new BigDecimal("5.0")));
        alerts.addAll(findByHighSoilErosion(new BigDecimal("10.0")));

        // Remove duplicates
        return alerts.stream().distinct().collect(Collectors.toList());
    }

    public Map<String, Object> getEnvironmentalSummaryByRegion(String region) {
        Map<String, Object> summary = new HashMap<>();

        List<EnvironmentalData> regionData = findByRegion(region);
        summary.put("totalRecords", regionData.size());

        // Risk level distribution
        Map<EnvironmentalRiskLevel, Long> riskDistribution = regionData.stream()
                .collect(Collectors.groupingBy(
                        EnvironmentalData::getEnvironmentalRiskLevel,
                        Collectors.counting()
                ));
        summary.put("riskDistribution", riskDistribution);

        // Quality averages
        summary.put("averageAirQuality", getAverageAirQualityByRegion(region));
        summary.put("averageWaterQuality", getAverageWaterQualityByRegion(region));
        summary.put("averageForestCoverage", getAverageForestCoverageByRegion(region));

        // Validation status
        Map<ValidationStatus, Long> validationDistribution = regionData.stream()
                .collect(Collectors.groupingBy(
                        EnvironmentalData::getValidationStatus,
                        Collectors.counting()
                ));
        summary.put("validationDistribution", validationDistribution);

        return summary;
    }

    public List<EnvironmentalData> getRecommendationsForImprovement() {
        List<EnvironmentalData> recommendations = new ArrayList<>();

        // Areas with high risk that need immediate attention
        recommendations.addAll(findCriticalRiskAreas().stream()
                .filter(data -> data.getValidationStatus() == ValidationStatus.VALIDATED)
                .collect(Collectors.toList()));

        // Areas with poor environmental indicators
        recommendations.addAll(findByLowForestCoverage(new BigDecimal("30.0")));
        recommendations.addAll(findByHighSoilErosion(new BigDecimal("5.0")));
        recommendations.addAll(findByLowClimateResilience(30));

        return recommendations.stream().distinct().collect(Collectors.toList());
    }

    public boolean updateNextMonitoringDate(String id, LocalDate nextMonitoringDate) {
        Optional<EnvironmentalData> optionalData = environmentalDataRepository.findById(id);
        if (optionalData.isPresent()) {
            EnvironmentalData data = optionalData.get();
            data.setNextMonitoringDate(nextMonitoringDate);
            environmentalDataRepository.save(data);
            return true;
        }
        return false;
    }

    public EnvironmentalData updateDataQuality(String id, DataQuality dataQuality) {
        Optional<EnvironmentalData> optionalData = environmentalDataRepository.findById(id);
        if (optionalData.isPresent()) {
            EnvironmentalData data = optionalData.get();
            data.setDataQuality(dataQuality);
            return environmentalDataRepository.save(data);
        }
        throw new RuntimeException("Environmental data not found with id: " + id);
    }
}