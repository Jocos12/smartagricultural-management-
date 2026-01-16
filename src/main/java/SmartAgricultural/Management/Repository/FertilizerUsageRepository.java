package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.FertilizerUsage;
import SmartAgricultural.Management.Model.FertilizerUsage.FertilizerType;
import SmartAgricultural.Management.Model.FertilizerUsage.Unit;
import SmartAgricultural.Management.Model.FertilizerUsage.ApplicationMethod;
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
public interface FertilizerUsageRepository extends JpaRepository<FertilizerUsage, String> {

    // Basic finder methods
    List<FertilizerUsage> findByCropProductionId(String cropProductionId);

    Page<FertilizerUsage> findByCropProductionIdIn(List<String> cropProductionIds, Pageable pageable);

    // ⭐ CORRECTION CRITIQUE: Changé farmerId en farmId
// Dans FertilizerUsageRepository.java, ligne ~28
    @Query("SELECT f FROM FertilizerUsage f " +
            "WHERE f.cropProductionId IN " +
            "(SELECT cp.id FROM CropProduction cp WHERE cp.farmId = :farmerId)")
    Page<FertilizerUsage> findByFarmerId(@Param("farmerId") String farmerId, Pageable pageable);
    // ⭐ AJOUT: Méthode alternative plus performante avec JOIN
    @Query("SELECT f FROM FertilizerUsage f " +
            "JOIN CropProduction c ON f.cropProductionId = c.id " +
            "WHERE c.farmId = :farmerId")
    Page<FertilizerUsage> findByFarmerIdWithJoin(@Param("farmerId") String farmerId, Pageable pageable);




    // ⭐ AJOUT: Compter les enregistrements par fermier
    @Query("SELECT COUNT(f) FROM FertilizerUsage f WHERE f.cropProductionId IN " +
            "(SELECT c.id FROM CropProduction c WHERE c.farmId = :farmerId)")
    Long countByFarmerId(@Param("farmerId") String farmerId);

    Page<FertilizerUsage> findByCropProductionId(String cropProductionId, Pageable pageable);

    Optional<FertilizerUsage> findByIdAndCropProductionId(String id, String cropProductionId);

    // Fertilizer type queries
    List<FertilizerUsage> findByFertilizerType(FertilizerType fertilizerType);

    Page<FertilizerUsage> findByFertilizerType(FertilizerType fertilizerType, Pageable pageable);

    List<FertilizerUsage> findByFertilizerTypeAndCropProductionId(FertilizerType fertilizerType, String cropProductionId);

    // Fertilizer name and brand queries
    List<FertilizerUsage> findByFertilizerName(String fertilizerName);

    List<FertilizerUsage> findByFertilizerNameContaining(String fertilizerName);

    List<FertilizerUsage> findByBrand(String brand);

    List<FertilizerUsage> findByBrandContaining(String brand);

    List<FertilizerUsage> findByFertilizerNameAndBrand(String fertilizerName, String brand);

    // Application method queries
    List<FertilizerUsage> findByApplicationMethod(ApplicationMethod applicationMethod);

    Page<FertilizerUsage> findByApplicationMethod(ApplicationMethod applicationMethod, Pageable pageable);

    List<FertilizerUsage> findByApplicationMethodAndCropProductionId(ApplicationMethod applicationMethod, String cropProductionId);

    // Application stage queries
    List<FertilizerUsage> findByApplicationStage(String applicationStage);

    List<FertilizerUsage> findByApplicationStageContaining(String applicationStage);

    // Date-based queries
    List<FertilizerUsage> findByApplicationDate(LocalDate applicationDate);

    List<FertilizerUsage> findByApplicationDateBetween(LocalDate startDate, LocalDate endDate);

    List<FertilizerUsage> findByApplicationDateAfter(LocalDate date);

    List<FertilizerUsage> findByApplicationDateBefore(LocalDate date);

    List<FertilizerUsage> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Quantity and cost queries
    List<FertilizerUsage> findByQuantityGreaterThan(BigDecimal quantity);

    List<FertilizerUsage> findByQuantityLessThan(BigDecimal quantity);

    List<FertilizerUsage> findByQuantityBetween(BigDecimal minQuantity, BigDecimal maxQuantity);

    List<FertilizerUsage> findByUnit(Unit unit);

    List<FertilizerUsage> findByTotalCostGreaterThan(BigDecimal cost);

    List<FertilizerUsage> findByTotalCostLessThan(BigDecimal cost);

    List<FertilizerUsage> findByTotalCostBetween(BigDecimal minCost, BigDecimal maxCost);

    List<FertilizerUsage> findByCostPerUnitGreaterThan(BigDecimal costPerUnit);

    // Effectiveness queries
    List<FertilizerUsage> findByEffectivenessRating(Integer rating);

    List<FertilizerUsage> findByEffectivenessRatingGreaterThan(Integer rating);

    List<FertilizerUsage> findByEffectivenessRatingLessThan(Integer rating);

    List<FertilizerUsage> findByEffectivenessRatingBetween(Integer minRating, Integer maxRating);

    List<FertilizerUsage> findByEffectivenessRatingIsNull();

    List<FertilizerUsage> findByEffectivenessRatingIsNotNull();

    // Supplier queries
    List<FertilizerUsage> findBySupplier(String supplier);

    List<FertilizerUsage> findBySupplierContaining(String supplier);

    Page<FertilizerUsage> findBySupplier(String supplier, Pageable pageable);

    // Expiry date queries
    List<FertilizerUsage> findByExpiryDate(LocalDate expiryDate);

    List<FertilizerUsage> findByExpiryDateBefore(LocalDate date);

    List<FertilizerUsage> findByExpiryDateAfter(LocalDate date);

    List<FertilizerUsage> findByExpiryDateBetween(LocalDate startDate, LocalDate endDate);

    List<FertilizerUsage> findByExpiryDateIsNull();

    List<FertilizerUsage> findByExpiryDateIsNotNull();

    // Batch number queries
    List<FertilizerUsage> findByBatchNumber(String batchNumber);

    List<FertilizerUsage> findByBatchNumberContaining(String batchNumber);

    Optional<FertilizerUsage> findByBatchNumberAndSupplier(String batchNumber, String supplier);

    // Weather and soil conditions
    List<FertilizerUsage> findByWeatherConditionsContaining(String weatherConditions);

    List<FertilizerUsage> findBySoilConditionsContaining(String soilConditions);

    // Operator queries
    List<FertilizerUsage> findByOperatorName(String operatorName);

    List<FertilizerUsage> findByOperatorNameContaining(String operatorName);

    // Statistical queries
    @Query("SELECT COUNT(f) FROM FertilizerUsage f WHERE f.fertilizerType = :fertilizerType")
    Long countByFertilizerType(@Param("fertilizerType") FertilizerType fertilizerType);

    @Query("SELECT COUNT(f) FROM FertilizerUsage f WHERE f.cropProductionId = :cropProductionId")
    Long countByCropProductionId(@Param("cropProductionId") String cropProductionId);

    @Query("SELECT COUNT(f) FROM FertilizerUsage f WHERE f.applicationMethod = :method")
    Long countByApplicationMethod(@Param("method") ApplicationMethod applicationMethod);

    @Query("SELECT COUNT(f) FROM FertilizerUsage f WHERE f.effectivenessRating >= :rating")
    Long countByEffectivenessRatingGreaterThanEqual(@Param("rating") Integer rating);

    @Query("SELECT COUNT(f) FROM FertilizerUsage f WHERE f.supplier = :supplier")
    Long countBySupplier(@Param("supplier") String supplier);

    // Sum and average queries
    @Query("SELECT SUM(f.quantity) FROM FertilizerUsage f WHERE f.cropProductionId = :cropProductionId AND f.unit = :unit")
    BigDecimal getTotalQuantityByCropProductionIdAndUnit(@Param("cropProductionId") String cropProductionId,
                                                         @Param("unit") Unit unit);

    @Query("SELECT SUM(f.totalCost) FROM FertilizerUsage f WHERE f.cropProductionId = :cropProductionId")
    BigDecimal getTotalCostByCropProductionId(@Param("cropProductionId") String cropProductionId);

    @Query("SELECT SUM(f.quantity) FROM FertilizerUsage f WHERE f.fertilizerType = :fertilizerType AND f.unit = :unit")
    BigDecimal getTotalQuantityByFertilizerTypeAndUnit(@Param("fertilizerType") FertilizerType fertilizerType,
                                                       @Param("unit") Unit unit);

    @Query("SELECT SUM(f.totalCost) FROM FertilizerUsage f WHERE f.fertilizerType = :fertilizerType")
    BigDecimal getTotalCostByFertilizerType(@Param("fertilizerType") FertilizerType fertilizerType);

    @Query("SELECT AVG(f.effectivenessRating) FROM FertilizerUsage f WHERE f.fertilizerType = :fertilizerType AND f.effectivenessRating IS NOT NULL")
    Double getAverageEffectivenessRatingByFertilizerType(@Param("fertilizerType") FertilizerType fertilizerType);

    @Query("SELECT AVG(f.costPerUnit) FROM FertilizerUsage f WHERE f.fertilizerType = :fertilizerType AND f.costPerUnit IS NOT NULL")
    BigDecimal getAverageCostPerUnitByFertilizerType(@Param("fertilizerType") FertilizerType fertilizerType);

    @Query("SELECT AVG(f.totalCost) FROM FertilizerUsage f WHERE f.totalCost IS NOT NULL")
    BigDecimal getAverageTotalCost();

    // Complex queries with multiple criteria
    @Query("SELECT f FROM FertilizerUsage f WHERE f.cropProductionId = :cropProductionId AND f.fertilizerType = :fertilizerType")
    List<FertilizerUsage> findByCropProductionIdAndFertilizerType(@Param("cropProductionId") String cropProductionId,
                                                                  @Param("fertilizerType") FertilizerType fertilizerType);

    @Query("SELECT f FROM FertilizerUsage f WHERE f.fertilizerType = :fertilizerType AND f.applicationDate BETWEEN :startDate AND :endDate")
    List<FertilizerUsage> findByFertilizerTypeAndApplicationDateBetween(@Param("fertilizerType") FertilizerType fertilizerType,
                                                                        @Param("startDate") LocalDate startDate,
                                                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT f FROM FertilizerUsage f WHERE f.supplier = :supplier AND f.applicationDate BETWEEN :startDate AND :endDate")
    List<FertilizerUsage> findBySupplierAndApplicationDateBetween(@Param("supplier") String supplier,
                                                                  @Param("startDate") LocalDate startDate,
                                                                  @Param("endDate") LocalDate endDate);

    @Query("SELECT f FROM FertilizerUsage f WHERE f.effectivenessRating >= :minRating AND f.totalCost <= :maxCost")
    List<FertilizerUsage> findEffectiveAndAffordableFertilizers(@Param("minRating") Integer minRating,
                                                                @Param("maxCost") BigDecimal maxCost);

    // Expired and expiring fertilizers
    @Query("SELECT f FROM FertilizerUsage f WHERE f.expiryDate < CURRENT_DATE")
    List<FertilizerUsage> findExpiredFertilizers();

    @Query("SELECT f FROM FertilizerUsage f WHERE f.expiryDate BETWEEN CURRENT_DATE AND :futureDate")
    List<FertilizerUsage> findExpiringSoonFertilizers(@Param("futureDate") LocalDate futureDate);

    // High cost and low effectiveness fertilizers
    @Query("SELECT f FROM FertilizerUsage f WHERE f.totalCost > :costThreshold")
    List<FertilizerUsage> findHighCostFertilizers(@Param("costThreshold") BigDecimal costThreshold);

    @Query("SELECT f FROM FertilizerUsage f WHERE f.effectivenessRating <= :ratingThreshold AND f.effectivenessRating IS NOT NULL")
    List<FertilizerUsage> findLowEffectivenessFertilizers(@Param("ratingThreshold") Integer ratingThreshold);

    // Distinct value queries
    @Query("SELECT DISTINCT f.fertilizerName FROM FertilizerUsage f ORDER BY f.fertilizerName")
    List<String> findDistinctFertilizerNames();

    @Query("SELECT DISTINCT f.brand FROM FertilizerUsage f WHERE f.brand IS NOT NULL ORDER BY f.brand")
    List<String> findDistinctBrands();

    @Query("SELECT DISTINCT f.supplier FROM FertilizerUsage f WHERE f.supplier IS NOT NULL ORDER BY f.supplier")
    List<String> findDistinctSuppliers();

    @Query("SELECT DISTINCT f.applicationStage FROM FertilizerUsage f WHERE f.applicationStage IS NOT NULL ORDER BY f.applicationStage")
    List<String> findDistinctApplicationStages();

    @Query("SELECT DISTINCT f.operatorName FROM FertilizerUsage f WHERE f.operatorName IS NOT NULL ORDER BY f.operatorName")
    List<String> findDistinctOperatorNames();

    // Advanced search with multiple criteria
    @Query("SELECT f FROM FertilizerUsage f WHERE " +
            "(:cropProductionId IS NULL OR f.cropProductionId = :cropProductionId) AND " +
            "(:fertilizerType IS NULL OR f.fertilizerType = :fertilizerType) AND " +
            "(:applicationMethod IS NULL OR f.applicationMethod = :applicationMethod) AND " +
            "(:supplier IS NULL OR f.supplier LIKE %:supplier%) AND " +
            "(:startDate IS NULL OR f.applicationDate >= :startDate) AND " +
            "(:endDate IS NULL OR f.applicationDate <= :endDate) AND " +
            "(:minCost IS NULL OR f.totalCost >= :minCost) AND " +
            "(:maxCost IS NULL OR f.totalCost <= :maxCost) AND " +
            "(:minRating IS NULL OR f.effectivenessRating >= :minRating)")
    Page<FertilizerUsage> findByCriteria(@Param("cropProductionId") String cropProductionId,
                                         @Param("fertilizerType") FertilizerType fertilizerType,
                                         @Param("applicationMethod") ApplicationMethod applicationMethod,
                                         @Param("supplier") String supplier,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate,
                                         @Param("minCost") BigDecimal minCost,
                                         @Param("maxCost") BigDecimal maxCost,
                                         @Param("minRating") Integer minRating,
                                         Pageable pageable);

    // Top performing fertilizers
    @Query("SELECT f FROM FertilizerUsage f WHERE f.effectivenessRating IS NOT NULL ORDER BY f.effectivenessRating DESC, f.totalCost ASC")
    Page<FertilizerUsage> findTopPerformingFertilizers(Pageable pageable);

    // Most cost-effective fertilizers
    @Query("SELECT f FROM FertilizerUsage f WHERE f.totalCost IS NOT NULL AND f.quantity IS NOT NULL AND f.quantity > 0 ORDER BY (f.totalCost / f.quantity) ASC")
    Page<FertilizerUsage> findMostCostEffectiveFertilizers(Pageable pageable);

    // Recent applications
    @Query("SELECT f FROM FertilizerUsage f WHERE f.applicationDate >= :date ORDER BY f.applicationDate DESC")
    List<FertilizerUsage> findRecentApplications(@Param("date") LocalDate date);

    // Monthly usage statistics
    @Query("SELECT EXTRACT(YEAR FROM f.applicationDate) as year, EXTRACT(MONTH FROM f.applicationDate) as month, " +
            "COUNT(f) as usageCount, SUM(f.totalCost) as totalCost " +
            "FROM FertilizerUsage f WHERE f.applicationDate BETWEEN :startDate AND :endDate " +
            "GROUP BY EXTRACT(YEAR FROM f.applicationDate), EXTRACT(MONTH FROM f.applicationDate) " +
            "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyUsageStatistics(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    // Fertilizer performance by type
    @Query("SELECT f.fertilizerType, COUNT(f) as usageCount, AVG(f.effectivenessRating) as avgRating, SUM(f.totalCost) as totalCost " +
            "FROM FertilizerUsage f WHERE f.effectivenessRating IS NOT NULL " +
            "GROUP BY f.fertilizerType " +
            "ORDER BY avgRating DESC")
    List<Object[]> getFertilizerPerformanceByType();

    // Delete queries
    void deleteByCropProductionId(String cropProductionId);

    void deleteByApplicationDateBefore(LocalDate date);

    @Query("DELETE FROM FertilizerUsage f WHERE f.expiryDate < CURRENT_DATE")
    void deleteExpiredFertilizers();

    @Query("DELETE FROM FertilizerUsage f WHERE f.createdAt < :date")
    void deleteOldRecords(@Param("date") LocalDateTime date);
}