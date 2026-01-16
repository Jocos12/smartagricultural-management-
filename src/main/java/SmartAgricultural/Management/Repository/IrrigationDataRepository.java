package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.IrrigationData;
import SmartAgricultural.Management.Model.IrrigationData.IrrigationMethod;
import SmartAgricultural.Management.Model.IrrigationData.WaterSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IrrigationDataRepository extends JpaRepository<IrrigationData, String> {

    // Find by farm ID
    List<IrrigationData> findByFarmId(String farmId);
    Page<IrrigationData> findByFarmId(String farmId, Pageable pageable);

    // Find by crop production ID
    List<IrrigationData> findByCropProductionId(String cropProductionId);
    Page<IrrigationData> findByCropProductionId(String cropProductionId, Pageable pageable);

    // Find by date range
    List<IrrigationData> findByIrrigationDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    Page<IrrigationData> findByIrrigationDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // Find by farm and date range
    List<IrrigationData> findByFarmIdAndIrrigationDateBetween(String farmId, LocalDateTime startDate, LocalDateTime endDate);
    Page<IrrigationData> findByFarmIdAndIrrigationDateBetween(String farmId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // Find by irrigation method
    List<IrrigationData> findByIrrigationMethod(IrrigationMethod irrigationMethod);
    Page<IrrigationData> findByIrrigationMethod(IrrigationMethod irrigationMethod, Pageable pageable);

    // Find by water source
    List<IrrigationData> findByWaterSource(WaterSource waterSource);
    Page<IrrigationData> findByWaterSource(WaterSource waterSource, Pageable pageable);

    // Find by water amount range
    List<IrrigationData> findByWaterAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    Page<IrrigationData> findByWaterAmountBetween(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable);

    // Find by operator
    List<IrrigationData> findByOperatorNameContainingIgnoreCase(String operatorName);
    Page<IrrigationData> findByOperatorNameContainingIgnoreCase(String operatorName, Pageable pageable);

    // Find by fertilizer applied
    List<IrrigationData> findByFertilizerApplied(Boolean fertilizerApplied);
    Page<IrrigationData> findByFertilizerApplied(Boolean fertilizerApplied, Pageable pageable);

    // Find high water usage irrigations
    List<IrrigationData> findByWaterAmountGreaterThan(BigDecimal amount);

    // Find expensive irrigations
    List<IrrigationData> findByTotalCostGreaterThan(BigDecimal cost);

    // Find recent irrigations
    List<IrrigationData> findTop10ByOrderByIrrigationDateDesc();

    // Custom queries
    @Query("SELECT i FROM IrrigationData i WHERE i.farmId = :farmId AND i.irrigationDate >= :date ORDER BY i.irrigationDate DESC")
    List<IrrigationData> findRecentIrrigationsByFarm(@Param("farmId") String farmId, @Param("date") LocalDateTime date);

    @Query("SELECT SUM(i.waterAmount) FROM IrrigationData i WHERE i.farmId = :farmId AND i.irrigationDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalWaterUsageByFarmAndPeriod(@Param("farmId") String farmId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(i.totalCost) FROM IrrigationData i WHERE i.farmId = :farmId AND i.irrigationDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalCostByFarmAndPeriod(@Param("farmId") String farmId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(i.waterAmount) FROM IrrigationData i WHERE i.farmId = :farmId")
    BigDecimal getAverageWaterUsageByFarm(@Param("farmId") String farmId);

    @Query("SELECT i FROM IrrigationData i WHERE i.farmId = :farmId AND i.irrigationMethod = :method ORDER BY i.irrigationDate DESC")
    List<IrrigationData> findByFarmAndIrrigationMethod(@Param("farmId") String farmId, @Param("method") IrrigationMethod method);

    @Query("SELECT COUNT(i) FROM IrrigationData i WHERE i.farmId = :farmId AND i.irrigationDate BETWEEN :startDate AND :endDate")
    Long countIrrigationsByFarmAndPeriod(@Param("farmId") String farmId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT i.irrigationMethod, COUNT(i) FROM IrrigationData i WHERE i.farmId = :farmId GROUP BY i.irrigationMethod")
    List<Object[]> getIrrigationMethodStatsByFarm(@Param("farmId") String farmId);

    @Query("SELECT i.waterSource, SUM(i.waterAmount) FROM IrrigationData i WHERE i.farmId = :farmId GROUP BY i.waterSource")
    List<Object[]> getWaterUsageBySourceAndFarm(@Param("farmId") String farmId);

    @Query("SELECT EXTRACT(MONTH FROM i.irrigationDate), SUM(i.waterAmount) FROM IrrigationData i WHERE i.farmId = :farmId AND EXTRACT(YEAR FROM i.irrigationDate) = :year GROUP BY EXTRACT(MONTH FROM i.irrigationDate) ORDER BY EXTRACT(MONTH FROM i.irrigationDate)")
    List<Object[]> getMonthlyWaterUsageByFarm(@Param("farmId") String farmId, @Param("year") int year);

    @Query("SELECT i FROM IrrigationData i WHERE i.soilMoistureBefore IS NOT NULL AND i.soilMoistureAfter IS NOT NULL AND (i.soilMoistureAfter - i.soilMoistureBefore) / i.waterAmount < :threshold")
    List<IrrigationData> findLowEfficiencyIrrigations(@Param("threshold") BigDecimal threshold);

    @Query("SELECT i FROM IrrigationData i WHERE i.farmId = :farmId AND i.irrigationDate = (SELECT MAX(i2.irrigationDate) FROM IrrigationData i2 WHERE i2.farmId = :farmId)")
    Optional<IrrigationData> findLastIrrigationByFarm(@Param("farmId") String farmId);

    @Query("SELECT DISTINCT i.operatorName FROM IrrigationData i WHERE i.operatorName IS NOT NULL AND i.farmId = :farmId")
    List<String> findOperatorsByFarm(@Param("farmId") String farmId);

    @Query("SELECT i FROM IrrigationData i WHERE i.waterQuality = :quality")
    List<IrrigationData> findByWaterQuality(@Param("quality") String quality);

    @Query("SELECT i FROM IrrigationData i WHERE i.duration IS NOT NULL AND i.duration > :duration")
    List<IrrigationData> findLongDurationIrrigations(@Param("duration") Integer duration);

    // Native queries for complex statistics
    @Query(value = "SELECT DATE(irrigation_date) as date, SUM(water_amount) as total_water, COUNT(*) as irrigation_count " +
            "FROM irrigation_data WHERE farm_id = :farmId AND irrigation_date >= :startDate " +
            "GROUP BY DATE(irrigation_date) ORDER BY DATE(irrigation_date)", nativeQuery = true)
    List<Object[]> getDailyIrrigationStats(@Param("farmId") String farmId, @Param("startDate") LocalDateTime startDate);

    @Query(value = "SELECT irrigation_method, AVG(water_amount) as avg_water, AVG(total_cost) as avg_cost, COUNT(*) as usage_count " +
            "FROM irrigation_data WHERE farm_id = :farmId GROUP BY irrigation_method", nativeQuery = true)
    List<Object[]> getIrrigationMethodAnalysis(@Param("farmId") String farmId);

    @Query(value = "SELECT * FROM irrigation_data WHERE farm_id = :farmId AND " +
            "ABS(EXTRACT(DOY FROM irrigation_date) - EXTRACT(DOY FROM CURRENT_DATE)) <= 7 " +
            "ORDER BY irrigation_date DESC", nativeQuery = true)
    List<IrrigationData> findSimilarPeriodIrrigations(@Param("farmId") String farmId);
}