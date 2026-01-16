package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.ProductionPrediction;
import SmartAgricultural.Management.Model.ProductionPrediction.Season;
import SmartAgricultural.Management.Model.ProductionPrediction.PredictionType;
import SmartAgricultural.Management.Model.ProductionPrediction.ValidationStatus;
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
public interface ProductionPredictionRepository extends JpaRepository<ProductionPrediction, String> {

    // Basic finders
    Optional<ProductionPrediction> findByPredictionCode(String predictionCode);


    // ✅ REPLACE WITH THIS:
    @Query("SELECT p FROM ProductionPrediction p WHERE p.cropProductionId IN :cropProductionIds")
    Page<ProductionPrediction> findByCropProductionIdIn(
            @Param("cropProductionIds") List<String> cropProductionIds,
            Pageable pageable
    );

    List<ProductionPrediction> findByCropId(String cropId);

    List<ProductionPrediction> findByRegion(String region);

    List<ProductionPrediction> findByRegionAndDistrict(String region, String district);

    List<ProductionPrediction> findByYear(Integer year);

    List<ProductionPrediction> findBySeason(Season season);

    List<ProductionPrediction> findByPredictionType(PredictionType predictionType);

    List<ProductionPrediction> findByValidationStatus(ValidationStatus validationStatus);

    List<ProductionPrediction> findByPublished(Boolean published);

    List<ProductionPrediction> findByValidatedBy(String validatedBy);

    // Complex finders with multiple conditions
    List<ProductionPrediction> findByCropIdAndYear(String cropId, Integer year);

    List<ProductionPrediction> findByCropIdAndYearAndSeason(String cropId, Integer year, Season season);

    List<ProductionPrediction> findByRegionAndYearAndSeason(String region, Integer year, Season season);

    List<ProductionPrediction> findByCropIdAndRegionAndYear(String cropId, String region, Integer year);

    List<ProductionPrediction> findByPredictionTypeAndValidationStatus(PredictionType predictionType, ValidationStatus validationStatus);

    // Date-based queries
    List<ProductionPrediction> findByTargetDateBetween(LocalDate startDate, LocalDate endDate);

    List<ProductionPrediction> findByPredictionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<ProductionPrediction> findByTargetDateBefore(LocalDate date);

    List<ProductionPrediction> findByTargetDateAfter(LocalDate date);

    @Query("SELECT p FROM ProductionPrediction p WHERE p.targetDate < :currentDate AND p.actualValue IS NULL")
    List<ProductionPrediction> findOverduePredictions(@Param("currentDate") LocalDate currentDate);

    // Confidence level queries
    List<ProductionPrediction> findByConfidenceLevelGreaterThanEqual(BigDecimal confidenceLevel);

    List<ProductionPrediction> findByConfidenceLevelBetween(BigDecimal minConfidence, BigDecimal maxConfidence);

    @Query("SELECT p FROM ProductionPrediction p WHERE p.confidenceLevel >= 80.0")
    List<ProductionPrediction> findHighConfidencePredictions();

    @Query("SELECT p FROM ProductionPrediction p WHERE p.confidenceLevel < 60.0")
    List<ProductionPrediction> findLowConfidencePredictions();

    // Accuracy-based queries
    List<ProductionPrediction> findByAccuracyAchievedGreaterThanEqual(BigDecimal accuracy);

    @Query("SELECT p FROM ProductionPrediction p WHERE p.accuracyAchieved >= 75.0")
    List<ProductionPrediction> findAccuratePredictions();

    @Query("SELECT p FROM ProductionPrediction p WHERE p.actualValue IS NOT NULL")
    List<ProductionPrediction> findPredictionsWithActualValues();

    @Query("SELECT p FROM ProductionPrediction p WHERE p.actualValue IS NULL")
    List<ProductionPrediction> findPredictionsWithoutActualValues();

    // Model-based queries
    List<ProductionPrediction> findByModelUsed(String modelUsed);

    List<ProductionPrediction> findByModelUsedAndModelVersion(String modelUsed, String modelVersion);

    List<ProductionPrediction> findByAlgorithm(String algorithm);

    // Statistical queries
    @Query("SELECT AVG(p.accuracyAchieved) FROM ProductionPrediction p WHERE p.accuracyAchieved IS NOT NULL")
    BigDecimal findAverageAccuracy();

    @Query("SELECT AVG(p.accuracyAchieved) FROM ProductionPrediction p WHERE p.cropId = :cropId AND p.accuracyAchieved IS NOT NULL")
    BigDecimal findAverageAccuracyByCrop(@Param("cropId") String cropId);

    @Query("SELECT AVG(p.confidenceLevel) FROM ProductionPrediction p WHERE p.confidenceLevel IS NOT NULL")
    BigDecimal findAverageConfidenceLevel();

    @Query("SELECT COUNT(p) FROM ProductionPrediction p WHERE p.validationStatus = :status")
    Long countByValidationStatus(@Param("status") ValidationStatus status);

    @Query("SELECT COUNT(p) FROM ProductionPrediction p WHERE p.published = true")
    Long countPublishedPredictions();

    @Query("SELECT COUNT(p) FROM ProductionPrediction p WHERE p.targetDate < :currentDate AND p.actualValue IS NULL")
    Long countOverduePredictions(@Param("currentDate") LocalDate currentDate);

    // Pageable queries
    Page<ProductionPrediction> findByCropId(String cropId, Pageable pageable);

    Page<ProductionPrediction> findByRegion(String region, Pageable pageable);

    Page<ProductionPrediction> findByValidationStatus(ValidationStatus status, Pageable pageable);

    Page<ProductionPrediction> findByPublished(Boolean published, Pageable pageable);

    Page<ProductionPrediction> findByYear(Integer year, Pageable pageable);

    // Search queries
    @Query("SELECT p FROM ProductionPrediction p WHERE " +
            "LOWER(p.predictionCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.region) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.district) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.modelUsed) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<ProductionPrediction> searchPredictions(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Complex analytical queries
    @Query("SELECT p FROM ProductionPrediction p WHERE " +
            "p.cropId = :cropId AND " +
            "p.year = :year AND " +
            "p.season = :season AND " +
            "p.validationStatus = 'VALIDATED' " +
            "ORDER BY p.confidenceLevel DESC")
    List<ProductionPrediction> findValidatedPredictionsForCropYearSeason(
            @Param("cropId") String cropId,
            @Param("year") Integer year,
            @Param("season") Season season);

    @Query("SELECT p FROM ProductionPrediction p WHERE " +
            "p.region = :region AND " +
            "p.year BETWEEN :startYear AND :endYear AND " +
            "p.predictionType = :predictionType " +
            "ORDER BY p.year DESC, p.confidenceLevel DESC")
    List<ProductionPrediction> findRegionalTrendPredictions(
            @Param("region") String region,
            @Param("startYear") Integer startYear,
            @Param("endYear") Integer endYear,
            @Param("predictionType") PredictionType predictionType);

    @Query("SELECT p FROM ProductionPrediction p WHERE " +
            "p.modelUsed = :modelUsed AND " +
            "p.accuracyAchieved IS NOT NULL " +
            "ORDER BY p.accuracyAchieved DESC")
    List<ProductionPrediction> findModelPerformanceData(@Param("modelUsed") String modelUsed);

    // Recent predictions
    @Query("SELECT p FROM ProductionPrediction p WHERE " +
            "p.predictionDate >= :sinceDate " +
            "ORDER BY p.predictionDate DESC")
    List<ProductionPrediction> findRecentPredictions(@Param("sinceDate") LocalDateTime sinceDate);

    // Predictions needing updates
    @Query("SELECT p FROM ProductionPrediction p WHERE " +
            "p.lastUpdated < :cutoffDate AND " +
            "p.validationStatus != 'REJECTED'")
    List<ProductionPrediction> findPredictionsNeedingUpdate(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Top performing models
    @Query("SELECT p.modelUsed, AVG(p.accuracyAchieved) as avgAccuracy FROM ProductionPrediction p " +
            "WHERE p.accuracyAchieved IS NOT NULL " +
            "GROUP BY p.modelUsed " +
            "ORDER BY avgAccuracy DESC")
    List<Object[]> findTopPerformingModels();

    // Seasonal analysis
    @Query("SELECT p.season, COUNT(p), AVG(p.accuracyAchieved) FROM ProductionPrediction p " +
            "WHERE p.year = :year AND p.accuracyAchieved IS NOT NULL " +
            "GROUP BY p.season")
    List<Object[]> findSeasonalAnalysis(@Param("year") Integer year);

    // Regional performance
    @Query("SELECT p.region, COUNT(p), AVG(p.accuracyAchieved), AVG(p.confidenceLevel) FROM ProductionPrediction p " +
            "WHERE p.year = :year AND p.accuracyAchieved IS NOT NULL " +
            "GROUP BY p.region " +
            "ORDER BY AVG(p.accuracyAchieved) DESC")
    List<Object[]> findRegionalPerformance(@Param("year") Integer year);

    // Confidence vs Accuracy correlation
    @Query("SELECT p.confidenceLevel, p.accuracyAchieved FROM ProductionPrediction p " +
            "WHERE p.confidenceLevel IS NOT NULL AND p.accuracyAchieved IS NOT NULL")
    List<Object[]> findConfidenceAccuracyCorrelation();

    // Dashboard statistics
    @Query("SELECT " +
            "COUNT(p) as totalPredictions, " +
            "COUNT(CASE WHEN p.validationStatus = 'VALIDATED' THEN 1 END) as validatedCount, " +
            "COUNT(CASE WHEN p.published = true THEN 1 END) as publishedCount, " +
            "COUNT(CASE WHEN p.actualValue IS NOT NULL THEN 1 END) as completedCount, " +
            "AVG(p.accuracyAchieved) as avgAccuracy, " +
            "AVG(p.confidenceLevel) as avgConfidence " +
            "FROM ProductionPrediction p")
    Object[] getDashboardStatistics();

    // Custom delete methods
    void deleteByValidationStatus(ValidationStatus status);

    void deleteByCropIdAndYear(String cropId, Integer year);

    @Query("DELETE FROM ProductionPrediction p WHERE p.targetDate < :cutoffDate AND p.actualValue IS NULL")
    void deleteOldUncompletedPredictions(@Param("cutoffDate") LocalDate cutoffDate);

    // Exists methods
    boolean existsByPredictionCode(String predictionCode);

    boolean existsByCropIdAndYearAndSeasonAndPredictionType(String cropId, Integer year, Season season, PredictionType predictionType);

    // Count methods for specific conditions
    @Query("SELECT COUNT(p) FROM ProductionPrediction p WHERE " +
            "p.cropId = :cropId AND " +
            "p.year = :year AND " +
            "p.validationStatus = 'VALIDATED'")
    Long countValidatedPredictionsForCropAndYear(@Param("cropId") String cropId, @Param("year") Integer year);

    // Latest predictions
    @Query("SELECT p FROM ProductionPrediction p WHERE " +
            "p.cropId = :cropId " +
            "ORDER BY p.predictionDate DESC")
    List<ProductionPrediction> findLatestPredictionsForCrop(@Param("cropId") String cropId, Pageable pageable);

    // Batch operations support
    @Query("SELECT p.id FROM ProductionPrediction p WHERE p.validationStatus = 'PENDING' AND p.predictionDate < :cutoffDate")
    List<String> findPendingPredictionIds(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Performance monitoring
    @Query("SELECT p FROM ProductionPrediction p WHERE " +
            "p.modelUsed = :modelUsed AND " +
            "p.predictionDate BETWEEN :startDate AND :endDate " +
            "ORDER BY p.predictionDate DESC")
    List<ProductionPrediction> findModelPredictionsInPeriod(
            @Param("modelUsed") String modelUsed,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}