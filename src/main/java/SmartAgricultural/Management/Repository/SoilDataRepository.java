package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.SoilData;
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
public interface SoilDataRepository extends JpaRepository<SoilData, String> {

    // Basic finders
    Optional<SoilData> findBySampleCode(String sampleCode);

    List<SoilData> findByFarmId(String farmId);

    Page<SoilData> findByFarmId(String farmId, Pageable pageable);

    // Date-based queries
    List<SoilData> findByMeasurementDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<SoilData> findByFarmIdAndMeasurementDateBetween(
            String farmId, LocalDateTime startDate, LocalDateTime endDate);

    List<SoilData> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Next test due queries
    List<SoilData> findByNextTestDueBefore(LocalDate date);

    List<SoilData> findByNextTestDueBetween(LocalDate startDate, LocalDate endDate);

    List<SoilData> findByFarmIdAndNextTestDueBefore(String farmId, LocalDate date);

    @Query("SELECT s FROM SoilData s WHERE s.nextTestDue <= CURRENT_DATE")
    List<SoilData> findTestsDue();

    @Query("SELECT s FROM SoilData s WHERE s.farmId = :farmId AND s.nextTestDue <= CURRENT_DATE")
    List<SoilData> findTestsDueByFarmId(@Param("farmId") String farmId);

    @Query("SELECT s FROM SoilData s WHERE s.nextTestDue < CURRENT_DATE")
    List<SoilData> findOverdueTests();

    @Query("SELECT s FROM SoilData s WHERE s.farmId = :farmId AND s.nextTestDue < CURRENT_DATE")
    List<SoilData> findOverdueTestsByFarmId(@Param("farmId") String farmId);

    // pH level queries
    List<SoilData> findByPhLevelBetween(BigDecimal minPh, BigDecimal maxPh);

    List<SoilData> findByFarmIdAndPhLevelBetween(String farmId, BigDecimal minPh, BigDecimal maxPh);

    @Query("SELECT s FROM SoilData s WHERE s.phLevel < :acidPh")
    List<SoilData> findAcidicSoils(@Param("acidPh") BigDecimal acidPh);

    @Query("SELECT s FROM SoilData s WHERE s.phLevel > :alkalinePh")
    List<SoilData> findAlkalineSoils(@Param("alkalinePh") BigDecimal alkalinePh);

    @Query("SELECT s FROM SoilData s WHERE s.farmId = :farmId AND s.phLevel BETWEEN 6.0 AND 7.5")
    List<SoilData> findOptimalPhSoilsByFarmId(@Param("farmId") String farmId);

    // Nutrient level queries
    List<SoilData> findByNitrogenGreaterThanEqual(BigDecimal nitrogen);

    List<SoilData> findByNitrogenLessThan(BigDecimal nitrogen);

    List<SoilData> findByPhosphorusGreaterThanEqual(BigDecimal phosphorus);

    List<SoilData> findByPhosphorusLessThan(BigDecimal phosphorus);

    List<SoilData> findByPotassiumGreaterThanEqual(BigDecimal potassium);

    List<SoilData> findByPotassiumLessThan(BigDecimal potassium);

    @Query("SELECT s FROM SoilData s WHERE s.nitrogen < :lowN OR s.phosphorus < :lowP OR s.potassium < :lowK")
    List<SoilData> findLowNutrientSoils(
            @Param("lowN") BigDecimal lowN,
            @Param("lowP") BigDecimal lowP,
            @Param("lowK") BigDecimal lowK);

    @Query("SELECT s FROM SoilData s WHERE s.farmId = :farmId AND " +
            "(s.nitrogen < :lowN OR s.phosphorus < :lowP OR s.potassium < :lowK)")
    List<SoilData> findLowNutrientSoilsByFarmId(
            @Param("farmId") String farmId,
            @Param("lowN") BigDecimal lowN,
            @Param("lowP") BigDecimal lowP,
            @Param("lowK") BigDecimal lowK);

    // Organic matter queries
    List<SoilData> findByOrganicMatterLessThan(BigDecimal organicMatter);

    List<SoilData> findByOrganicMatterGreaterThanEqual(BigDecimal organicMatter);

    @Query("SELECT s FROM SoilData s WHERE s.farmId = :farmId AND s.organicMatter < :minOrganic")
    List<SoilData> findLowOrganicMatterSoilsByFarmId(
            @Param("farmId") String farmId,
            @Param("minOrganic") BigDecimal minOrganic);

    // Moisture queries
    List<SoilData> findByMoistureBetween(BigDecimal minMoisture, BigDecimal maxMoisture);

    List<SoilData> findByMoistureLessThan(BigDecimal moisture);

    List<SoilData> findByMoistureGreaterThan(BigDecimal moisture);

    // Soil texture queries
    List<SoilData> findBySoilTexture(String soilTexture);

    List<SoilData> findByFarmIdAndSoilTexture(String farmId, String soilTexture);

    @Query("SELECT s FROM SoilData s WHERE s.soilTexture IN :textureTypes")
    List<SoilData> findBySoilTextureIn(@Param("textureTypes") List<String> textureTypes);

    // Physical properties queries
    List<SoilData> findByBulkDensityGreaterThan(BigDecimal bulkDensity);

    List<SoilData> findByPorosityLessThan(BigDecimal porosity);

    @Query("SELECT s FROM SoilData s WHERE s.bulkDensity > :maxDensity OR s.porosity < :minPorosity")
    List<SoilData> findPoorDrainageSoils(
            @Param("maxDensity") BigDecimal maxDensity,
            @Param("minPorosity") BigDecimal minPorosity);

    // Salinity queries
    List<SoilData> findByElectricalConductivityGreaterThan(BigDecimal ec);

    @Query("SELECT s FROM SoilData s WHERE s.electricalConductivity > 2.0")
    List<SoilData> findSalineSoils();

    @Query("SELECT s FROM SoilData s WHERE s.farmId = :farmId AND s.electricalConductivity > 2.0")
    List<SoilData> findSalineSoilsByFarmId(@Param("farmId") String farmId);

    // Depth queries
    List<SoilData> findByDepthCm(Integer depthCm);

    List<SoilData> findByDepthCmBetween(Integer minDepth, Integer maxDepth);

    List<SoilData> findByFarmIdAndDepthCm(String farmId, Integer depthCm);

    // Laboratory queries
    List<SoilData> findByLaboratoryName(String laboratoryName);

    List<SoilData> findByTestingMethod(String testingMethod);

    // Latest/Most recent queries
    @Query("SELECT s FROM SoilData s WHERE s.farmId = :farmId " +
            "ORDER BY s.measurementDate DESC")
    List<SoilData> findByFarmIdOrderByMeasurementDateDesc(@Param("farmId") String farmId);

    @Query("SELECT s FROM SoilData s WHERE s.farmId = :farmId " +
            "ORDER BY s.measurementDate DESC LIMIT 1")
    Optional<SoilData> findLatestByFarmId(@Param("farmId") String farmId);

    @Query("SELECT s FROM SoilData s WHERE s.farmId = :farmId AND s.depthCm = :depthCm " +
            "ORDER BY s.measurementDate DESC LIMIT 1")
    Optional<SoilData> findLatestByFarmIdAndDepth(
            @Param("farmId") String farmId,
            @Param("depthCm") Integer depthCm);

    // Statistics queries
    @Query("SELECT AVG(s.phLevel) FROM SoilData s WHERE s.farmId = :farmId")
    BigDecimal getAveragePhLevelByFarmId(@Param("farmId") String farmId);

    @Query("SELECT AVG(s.nitrogen) FROM SoilData s WHERE s.farmId = :farmId AND s.nitrogen IS NOT NULL")
    BigDecimal getAverageNitrogenByFarmId(@Param("farmId") String farmId);

    @Query("SELECT AVG(s.phosphorus) FROM SoilData s WHERE s.farmId = :farmId AND s.phosphorus IS NOT NULL")
    BigDecimal getAveragePhosphorusByFarmId(@Param("farmId") String farmId);

    @Query("SELECT AVG(s.potassium) FROM SoilData s WHERE s.farmId = :farmId AND s.potassium IS NOT NULL")
    BigDecimal getAveragePotassiumByFarmId(@Param("farmId") String farmId);

    @Query("SELECT AVG(s.organicMatter) FROM SoilData s WHERE s.farmId = :farmId AND s.organicMatter IS NOT NULL")
    BigDecimal getAverageOrganicMatterByFarmId(@Param("farmId") String farmId);

    @Query("SELECT MIN(s.phLevel), MAX(s.phLevel) FROM SoilData s WHERE s.farmId = :farmId")
    List<Object[]> getPhLevelRangeByFarmId(@Param("farmId") String farmId);

    // Count queries
    long countByFarmId(String farmId);

    @Query("SELECT COUNT(s) FROM SoilData s WHERE s.farmId = :farmId AND s.nextTestDue <= CURRENT_DATE")
    long countTestsDueByFarmId(@Param("farmId") String farmId);

    @Query("SELECT COUNT(s) FROM SoilData s WHERE s.farmId = :farmId AND s.nextTestDue < CURRENT_DATE")
    long countOverdueTestsByFarmId(@Param("farmId") String farmId);

    @Query("SELECT COUNT(s) FROM SoilData s WHERE s.farmId = :farmId AND " +
            "(s.nitrogen < :lowN OR s.phosphorus < :lowP OR s.potassium < :lowK)")
    long countLowNutrientSoilsByFarmId(
            @Param("farmId") String farmId,
            @Param("lowN") BigDecimal lowN,
            @Param("lowP") BigDecimal lowP,
            @Param("lowK") BigDecimal lowK);

    @Query("SELECT COUNT(s) FROM SoilData s WHERE s.farmId = :farmId AND s.electricalConductivity > 2.0")
    long countSalineSoilsByFarmId(@Param("farmId") String farmId);

    @Query("SELECT COUNT(s) FROM SoilData s WHERE s.farmId = :farmId AND s.organicMatter < :minOrganic")
    long countLowOrganicMatterSoilsByFarmId(
            @Param("farmId") String farmId,
            @Param("minOrganic") BigDecimal minOrganic);

    // Group by queries
    @Query("SELECT s.soilTexture, COUNT(s) FROM SoilData s WHERE s.farmId = :farmId " +
            "GROUP BY s.soilTexture ORDER BY COUNT(s) DESC")
    List<Object[]> countBySoilTextureAndFarmId(@Param("farmId") String farmId);

    @Query("SELECT YEAR(s.measurementDate), COUNT(s) FROM SoilData s WHERE s.farmId = :farmId " +
            "GROUP BY YEAR(s.measurementDate) ORDER BY YEAR(s.measurementDate) DESC")
    List<Object[]> countByYearAndFarmId(@Param("farmId") String farmId);

    @Query("SELECT s.laboratoryName, COUNT(s) FROM SoilData s WHERE s.farmId = :farmId " +
            "GROUP BY s.laboratoryName ORDER BY COUNT(s) DESC")
    List<Object[]> countByLaboratoryAndFarmId(@Param("farmId") String farmId);

    // Complex analytical queries
    @Query("SELECT s FROM SoilData s WHERE s.farmId = :farmId AND " +
            "s.phLevel BETWEEN :minPh AND :maxPh AND " +
            "s.organicMatter >= :minOrganic AND " +
            "(s.nitrogen >= :minN OR s.phosphorus >= :minP OR s.potassium >= :minK)")
    List<SoilData> findHealthySoilsByFarmId(
            @Param("farmId") String farmId,
            @Param("minPh") BigDecimal minPh,
            @Param("maxPh") BigDecimal maxPh,
            @Param("minOrganic") BigDecimal minOrganic,
            @Param("minN") BigDecimal minN,
            @Param("minP") BigDecimal minP,
            @Param("minK") BigDecimal minK);

    @Query("SELECT s FROM SoilData s WHERE s.farmId = :farmId AND " +
            "(s.phLevel < 6.0 OR s.phLevel > 8.0 OR " +
            "s.organicMatter < 2.0 OR " +
            "s.electricalConductivity > 2.0 OR " +
            "(s.nitrogen < :lowN AND s.phosphorus < :lowP AND s.potassium < :lowK))")
    List<SoilData> findProblematicSoilsByFarmId(
            @Param("farmId") String farmId,
            @Param("lowN") BigDecimal lowN,
            @Param("lowP") BigDecimal lowP,
            @Param("lowK") BigDecimal lowK);

    // Search queries
    @Query("SELECT s FROM SoilData s WHERE s.farmId = :farmId AND " +
            "(LOWER(s.sampleCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.laboratoryName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.testingMethod) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<SoilData> searchByFarmIdAndTerm(
            @Param("farmId") String farmId,
            @Param("searchTerm") String searchTerm);

    @Query("SELECT s FROM SoilData s WHERE s.farmId = :farmId AND " +
            "(LOWER(s.sampleCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.laboratoryName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.testingMethod) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<SoilData> searchByFarmIdAndTerm(
            @Param("farmId") String farmId,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    // Existence checks
    boolean existsBySampleCode(String sampleCode);

    boolean existsByFarmIdAndSampleCode(String farmId, String sampleCode);

    // Recent data queries
    @Query("SELECT s FROM SoilData s WHERE s.farmId = :farmId AND " +
            "s.measurementDate >= :since ORDER BY s.measurementDate DESC")
    List<SoilData> findRecentByFarmId(
            @Param("farmId") String farmId,
            @Param("since") LocalDateTime since);

    // Trend analysis queries
    @Query("SELECT s FROM SoilData s WHERE s.farmId = :farmId AND s.depthCm = :depthCm " +
            "ORDER BY s.measurementDate ASC")
    List<SoilData> findTrendDataByFarmIdAndDepth(
            @Param("farmId") String farmId,
            @Param("depthCm") Integer depthCm);

    @Query("SELECT s FROM SoilData s WHERE s.farmId = :farmId AND " +
            "s.measurementDate BETWEEN :startDate AND :endDate " +
            "ORDER BY s.measurementDate ASC")
    List<SoilData> findTrendDataByFarmIdAndDateRange(
            @Param("farmId") String farmId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Quality assessment queries
    @Query("SELECT s FROM SoilData s WHERE s.farmId = :farmId AND " +
            "s.phLevel BETWEEN 6.0 AND 7.5 AND " +
            "s.organicMatter >= 3.0 AND " +
            "s.nitrogen >= 40 AND s.phosphorus >= 25 AND s.potassium >= 150")
    List<SoilData> findExcellentQualitySoilsByFarmId(@Param("farmId") String farmId);

    @Query("SELECT s FROM SoilData s WHERE s.farmId = :farmId AND " +
            "(s.phLevel < 5.5 OR s.phLevel > 8.5 OR " +
            "s.organicMatter < 1.0 OR " +
            "(s.nitrogen < 20 AND s.phosphorus < 10 AND s.potassium < 80))")
    List<SoilData> findPoorQualitySoilsByFarmId(@Param("farmId") String farmId);

    // Maintenance queries
    @Query("SELECT s FROM SoilData s WHERE s.nextTestDue BETWEEN :startDate AND :endDate")
    List<SoilData> findUpcomingTests(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("DELETE FROM SoilData s WHERE s.createdAt < :cutoffDate")
    int deleteOldRecords(@Param("cutoffDate") LocalDateTime cutoffDate);
}