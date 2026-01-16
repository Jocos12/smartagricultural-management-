package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.EnvironmentalData;
import SmartAgricultural.Management.Model.EnvironmentalData.EnvironmentalRiskLevel;
import SmartAgricultural.Management.Model.EnvironmentalData.DataQuality;
import SmartAgricultural.Management.Model.EnvironmentalData.ValidationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnvironmentalDataRepository extends JpaRepository<EnvironmentalData, String> {

    // Basic finders
    Optional<EnvironmentalData> findByMonitoringCode(String monitoringCode);

    List<EnvironmentalData> findByRegion(String region);

    List<EnvironmentalData> findByRegionAndDistrict(String region, String district);

    List<EnvironmentalData> findByRegionAndDistrictAndSector(String region, String district, String sector);

    // Data source queries
    List<EnvironmentalData> findByDataSource(String dataSource);

    @Query("SELECT e FROM EnvironmentalData e WHERE e.dataSource IN :dataSources")
    List<EnvironmentalData> findByDataSourceIn(@Param("dataSources") List<String> dataSources);

    // Date range queries
    List<EnvironmentalData> findByRecordDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<EnvironmentalData> findByRecordDateAfter(LocalDateTime date);

    List<EnvironmentalData> findByRecordDateBefore(LocalDateTime date);

    // Risk level queries
    List<EnvironmentalData> findByEnvironmentalRiskLevel(EnvironmentalRiskLevel riskLevel);

    @Query("SELECT e FROM EnvironmentalData e WHERE e.environmentalRiskLevel IN ('HIGH', 'CRITICAL')")
    List<EnvironmentalData> findHighRiskAreas();

    @Query("SELECT e FROM EnvironmentalData e WHERE e.environmentalRiskLevel = 'CRITICAL'")
    List<EnvironmentalData> findCriticalRiskAreas();

    // Data quality queries
    List<EnvironmentalData> findByDataQuality(DataQuality dataQuality);

    @Query("SELECT e FROM EnvironmentalData e WHERE e.dataQuality IN ('EXCELLENT', 'GOOD')")
    List<EnvironmentalData> findReliableData();

    // Validation status queries
    List<EnvironmentalData> findByValidationStatus(ValidationStatus validationStatus);

    List<EnvironmentalData> findByValidatedBy(String validatedBy);

    @Query("SELECT e FROM EnvironmentalData e WHERE e.validationStatus = 'PENDING'")
    List<EnvironmentalData> findPendingValidation();

    // Air quality queries
    @Query("SELECT e FROM EnvironmentalData e WHERE e.airQualityIndex > :threshold")
    List<EnvironmentalData> findByAirQualityIndexGreaterThan(@Param("threshold") Integer threshold);

    @Query("SELECT e FROM EnvironmentalData e WHERE e.airQualityIndex BETWEEN :min AND :max")
    List<EnvironmentalData> findByAirQualityIndexRange(@Param("min") Integer min, @Param("max") Integer max);

    // Water quality queries
    @Query("SELECT e FROM EnvironmentalData e WHERE e.waterQualityIndex < :threshold")
    List<EnvironmentalData> findByWaterQualityIndexLessThan(@Param("threshold") Integer threshold);

    @Query("SELECT e FROM EnvironmentalData e WHERE e.waterPh BETWEEN :minPh AND :maxPh")
    List<EnvironmentalData> findByWaterPhRange(@Param("minPh") BigDecimal minPh, @Param("maxPh") BigDecimal maxPh);

    // Forest and biodiversity queries
    @Query("SELECT e FROM EnvironmentalData e WHERE e.forestCoverage < :threshold")
    List<EnvironmentalData> findByLowForestCoverage(@Param("threshold") BigDecimal threshold);

    @Query("SELECT e FROM EnvironmentalData e WHERE e.deforestationRate > :threshold")
    List<EnvironmentalData> findByHighDeforestationRate(@Param("threshold") BigDecimal threshold);

    @Query("SELECT e FROM EnvironmentalData e WHERE e.endangeredSpeciesCount > :threshold")
    List<EnvironmentalData> findByHighEndangeredSpeciesCount(@Param("threshold") Integer threshold);

    // Carbon and climate queries
    @Query("SELECT e FROM EnvironmentalData e WHERE e.carbonEmission > :threshold")
    List<EnvironmentalData> findByHighCarbonEmission(@Param("threshold") BigDecimal threshold);

    @Query("SELECT e FROM EnvironmentalData e WHERE e.climateResilienceScore < :threshold")
    List<EnvironmentalData> findByLowClimateResilience(@Param("threshold") Integer threshold);

    // Agricultural impact queries
    @Query("SELECT e FROM EnvironmentalData e WHERE e.soilErosionRate > :threshold")
    List<EnvironmentalData> findByHighSoilErosion(@Param("threshold") BigDecimal threshold);

    @Query("SELECT e FROM EnvironmentalData e WHERE e.pesticideResidueLevel > :threshold")
    List<EnvironmentalData> findByHighPesticideResidue(@Param("threshold") BigDecimal threshold);

    // Geographic queries
    @Query("SELECT e FROM EnvironmentalData e WHERE " +
            "e.latitude BETWEEN :minLat AND :maxLat AND " +
            "e.longitude BETWEEN :minLon AND :maxLon")
    List<EnvironmentalData> findByLocationBounds(
            @Param("minLat") BigDecimal minLatitude,
            @Param("maxLat") BigDecimal maxLatitude,
            @Param("minLon") BigDecimal minLongitude,
            @Param("maxLon") BigDecimal maxLongitude
    );

    // Statistical queries
    @Query("SELECT AVG(e.airQualityIndex) FROM EnvironmentalData e WHERE e.region = :region")
    Double getAverageAirQualityByRegion(@Param("region") String region);

    @Query("SELECT AVG(e.waterQualityIndex) FROM EnvironmentalData e WHERE e.region = :region")
    Double getAverageWaterQualityByRegion(@Param("region") String region);

    @Query("SELECT AVG(e.forestCoverage) FROM EnvironmentalData e WHERE e.region = :region")
    Double getAverageForestCoverageByRegion(@Param("region") String region);

    @Query("SELECT COUNT(e) FROM EnvironmentalData e WHERE e.environmentalRiskLevel = :riskLevel")
    Long countByRiskLevel(@Param("riskLevel") EnvironmentalRiskLevel riskLevel);

    // Monitoring queries
    List<EnvironmentalData> findByNextMonitoringDateBefore(LocalDate date);

    @Query("SELECT e FROM EnvironmentalData e WHERE e.nextMonitoringDate <= :date")
    List<EnvironmentalData> findDueForMonitoring(@Param("date") LocalDate date);

    // Complex queries with multiple conditions
    @Query("SELECT e FROM EnvironmentalData e WHERE " +
            "e.region = :region AND " +
            "e.validationStatus = 'VALIDATED' AND " +
            "e.dataQuality IN ('EXCELLENT', 'GOOD') AND " +
            "e.recordDate BETWEEN :startDate AND :endDate")
    List<EnvironmentalData> findValidatedDataByRegionAndDateRange(
            @Param("region") String region,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT e FROM EnvironmentalData e WHERE " +
            "e.environmentalRiskLevel IN ('HIGH', 'CRITICAL') AND " +
            "e.validationStatus = 'VALIDATED' " +
            "ORDER BY e.recordDate DESC")
    List<EnvironmentalData> findValidatedHighRiskAreas();

    // Pagination queries
    Page<EnvironmentalData> findByRegion(String region, Pageable pageable);

    Page<EnvironmentalData> findByEnvironmentalRiskLevel(EnvironmentalRiskLevel riskLevel, Pageable pageable);

    Page<EnvironmentalData> findByValidationStatus(ValidationStatus validationStatus, Pageable pageable);

    // Custom native queries for complex operations
    @Query(value = "SELECT * FROM environmental_data e WHERE " +
            "ST_DWithin(ST_Point(e.longitude, e.latitude), ST_Point(:longitude, :latitude), :radiusKm * 1000)",
            nativeQuery = true)
    List<EnvironmentalData> findWithinRadius(
            @Param("longitude") Double longitude,
            @Param("latitude") Double latitude,
            @Param("radiusKm") Double radiusKm
    );

    // Trend analysis queries
    @Query("SELECT e FROM EnvironmentalData e WHERE " +
            "e.region = :region AND " +
            "e.recordDate >= :fromDate " +
            "ORDER BY e.recordDate ASC")
    List<EnvironmentalData> findTrendDataByRegion(
            @Param("region") String region,
            @Param("fromDate") LocalDateTime fromDate
    );

    // Summary queries for reporting
    @Query("SELECT DISTINCT e.region FROM EnvironmentalData e ORDER BY e.region")
    List<String> findAllRegions();

    @Query("SELECT DISTINCT e.district FROM EnvironmentalData e WHERE e.region = :region ORDER BY e.district")
    List<String> findDistrictsByRegion(@Param("region") String region);

    @Query("SELECT DISTINCT e.sector FROM EnvironmentalData e WHERE e.region = :region AND e.district = :district ORDER BY e.sector")
    List<String> findSectorsByRegionAndDistrict(@Param("region") String region, @Param("district") String district);

    // Data quality and completeness queries
    @Query("SELECT COUNT(e) FROM EnvironmentalData e WHERE " +
            "e.airQualityIndex IS NOT NULL OR " +
            "e.waterQualityIndex IS NOT NULL OR " +
            "e.forestCoverage IS NOT NULL")
    Long countRecordsWithData();

    @Query("SELECT e FROM EnvironmentalData e WHERE " +
            "e.airQualityIndex IS NULL AND " +
            "e.waterQualityIndex IS NULL AND " +
            "e.forestCoverage IS NULL")
    List<EnvironmentalData> findIncompleteRecords();
}