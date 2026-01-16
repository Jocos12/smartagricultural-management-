package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.MarketPrice;
import SmartAgricultural.Management.Model.MarketPrice.MarketType;
import SmartAgricultural.Management.Model.MarketPrice.DemandLevel;
import SmartAgricultural.Management.Model.MarketPrice.SupplyLevel;
import SmartAgricultural.Management.Model.MarketPrice.PriceTrend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MarketPriceRepository extends JpaRepository<MarketPrice, String> {

    // Find by crop
    List<MarketPrice> findByCropId(String cropId);
    Page<MarketPrice> findByCropId(String cropId, Pageable pageable);

    // Find by market
    List<MarketPrice> findByMarketNameContainingIgnoreCase(String marketName);
    Page<MarketPrice> findByMarketNameContainingIgnoreCase(String marketName, Pageable pageable);

    // Find by market type
    List<MarketPrice> findByMarketType(MarketType marketType);
    Page<MarketPrice> findByMarketType(MarketType marketType, Pageable pageable);

    // Find by location
    List<MarketPrice> findByLocationContainingIgnoreCase(String location);
    Page<MarketPrice> findByLocationContainingIgnoreCase(String location, Pageable pageable);

    // Find by date range
    List<MarketPrice> findByPriceDateBetween(LocalDate startDate, LocalDate endDate);
    Page<MarketPrice> findByPriceDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    // Find by crop and date range
    List<MarketPrice> findByCropIdAndPriceDateBetween(String cropId, LocalDate startDate, LocalDate endDate);
    Page<MarketPrice> findByCropIdAndPriceDateBetween(String cropId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    // Find by price range
    List<MarketPrice> findByPricePerKgBetween(BigDecimal minPrice, BigDecimal maxPrice);
    Page<MarketPrice> findByPricePerKgBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // Find by demand level
    List<MarketPrice> findByDemandLevel(DemandLevel demandLevel);
    Page<MarketPrice> findByDemandLevel(DemandLevel demandLevel, Pageable pageable);

    // Find by supply level
    List<MarketPrice> findBySupplyLevel(SupplyLevel supplyLevel);
    Page<MarketPrice> findBySupplyLevel(SupplyLevel supplyLevel, Pageable pageable);

    // Find by price trend
    List<MarketPrice> findByPriceTrend(PriceTrend priceTrend);
    Page<MarketPrice> findByPriceTrend(PriceTrend priceTrend, Pageable pageable);

    // Find by reliability score
    List<MarketPrice> findByReliabilityScoreGreaterThanEqual(Integer score);
    Page<MarketPrice> findByReliabilityScoreGreaterThanEqual(Integer score, Pageable pageable);

    // Find by data source
    List<MarketPrice> findByDataSourceContainingIgnoreCase(String dataSource);

    // Find high prices
    List<MarketPrice> findByPricePerKgGreaterThan(BigDecimal price);

    // Find low prices
    List<MarketPrice> findByPricePerKgLessThan(BigDecimal price);

    // Find recent prices
    List<MarketPrice> findByPriceDateAfter(LocalDate date);

    // Find outdated prices
    List<MarketPrice> findByPriceDateBefore(LocalDate date);

    // Find latest prices
    List<MarketPrice> findTop10ByOrderByPriceDateDesc();

    // Custom queries
    @Query("SELECT m FROM MarketPrice m WHERE m.cropId = :cropId ORDER BY m.priceDate DESC")
    List<MarketPrice> findLatestPricesByCrop(@Param("cropId") String cropId);

    @Query("SELECT m FROM MarketPrice m WHERE m.cropId = :cropId AND m.priceDate = (SELECT MAX(m2.priceDate) FROM MarketPrice m2 WHERE m2.cropId = :cropId)")
    Optional<MarketPrice> findLatestPriceByCrop(@Param("cropId") String cropId);

    @Query("SELECT AVG(m.pricePerKg) FROM MarketPrice m WHERE m.cropId = :cropId AND m.priceDate BETWEEN :startDate AND :endDate")
    BigDecimal getAveragePriceByCropAndPeriod(@Param("cropId") String cropId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT MIN(m.pricePerKg) FROM MarketPrice m WHERE m.cropId = :cropId AND m.priceDate BETWEEN :startDate AND :endDate")
    BigDecimal getMinPriceByCropAndPeriod(@Param("cropId") String cropId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT MAX(m.pricePerKg) FROM MarketPrice m WHERE m.cropId = :cropId AND m.priceDate BETWEEN :startDate AND :endDate")
    BigDecimal getMaxPriceByCropAndPeriod(@Param("cropId") String cropId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT m FROM MarketPrice m WHERE m.cropId = :cropId AND m.marketType = :marketType ORDER BY m.priceDate DESC")
    List<MarketPrice> findByCropAndMarketType(@Param("cropId") String cropId, @Param("marketType") MarketType marketType);

    @Query("SELECT m FROM MarketPrice m WHERE m.cropId = :cropId AND m.location = :location ORDER BY m.priceDate DESC")
    List<MarketPrice> findByCropAndLocation(@Param("cropId") String cropId, @Param("location") String location);

    @Query("SELECT COUNT(m) FROM MarketPrice m WHERE m.cropId = :cropId AND m.priceDate BETWEEN :startDate AND :endDate")
    Long countPricesByCropAndPeriod(@Param("cropId") String cropId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT m.marketType, AVG(m.pricePerKg) FROM MarketPrice m WHERE m.cropId = :cropId GROUP BY m.marketType")
    List<Object[]> getAveragePriceByMarketType(@Param("cropId") String cropId);

    @Query("SELECT m.location, AVG(m.pricePerKg) FROM MarketPrice m WHERE m.cropId = :cropId GROUP BY m.location")
    List<Object[]> getAveragePriceByLocation(@Param("cropId") String cropId);

    @Query("SELECT EXTRACT(MONTH FROM m.priceDate), AVG(m.pricePerKg) FROM MarketPrice m WHERE m.cropId = :cropId AND EXTRACT(YEAR FROM m.priceDate) = :year GROUP BY EXTRACT(MONTH FROM m.priceDate) ORDER BY EXTRACT(MONTH FROM m.priceDate)")
    List<Object[]> getMonthlyAveragePrices(@Param("cropId") String cropId, @Param("year") int year);

    @Query("SELECT m FROM MarketPrice m WHERE m.demandLevel = :demandLevel AND m.supplyLevel = :supplyLevel")
    List<MarketPrice> findByDemandAndSupplyLevels(@Param("demandLevel") DemandLevel demandLevel, @Param("supplyLevel") SupplyLevel supplyLevel);

    @Query("SELECT m FROM MarketPrice m WHERE m.demandLevel IN ('HIGH', 'VERY_HIGH') AND m.supplyLevel = 'LOW' AND m.priceTrend = 'INCREASING'")
    List<MarketPrice> findMarketOpportunities();

    @Query("SELECT DISTINCT m.marketName FROM MarketPrice m WHERE m.cropId = :cropId")
    List<String> findMarketNamesByCrop(@Param("cropId") String cropId);

    @Query("SELECT DISTINCT m.location FROM MarketPrice m WHERE m.cropId = :cropId")
    List<String> findLocationsByCrop(@Param("cropId") String cropId);

    @Query("SELECT m FROM MarketPrice m WHERE m.reliabilityScore >= :minScore AND m.priceDate >= :minDate")
    List<MarketPrice> findReliableRecentPrices(@Param("minScore") Integer minScore, @Param("minDate") LocalDate minDate);

    @Query("SELECT m FROM MarketPrice m WHERE m.cropId = :cropId AND m.priceDate >= :startDate AND m.reliabilityScore >= 4 ORDER BY m.priceDate DESC")
    List<MarketPrice> findReliableRecentPricesByCrop(@Param("cropId") String cropId, @Param("startDate") LocalDate startDate);

    // Price comparison queries
    @Query("SELECT m1.marketName, m1.pricePerKg, m2.marketName, m2.pricePerKg, (m1.pricePerKg - m2.pricePerKg) as priceDiff " +
            "FROM MarketPrice m1, MarketPrice m2 " +
            "WHERE m1.cropId = :cropId AND m2.cropId = :cropId AND m1.id != m2.id AND m1.priceDate = :date AND m2.priceDate = :date " +
            "ORDER BY priceDiff DESC")
    List<Object[]> comparePricesByDate(@Param("cropId") String cropId, @Param("date") LocalDate date);

    @Query("SELECT m FROM MarketPrice m WHERE m.cropId = :cropId AND ABS(DATEDIFF(m.priceDate, :targetDate)) <= 7 ORDER BY ABS(DATEDIFF(m.priceDate, :targetDate))")
    List<MarketPrice> findPricesNearDate(@Param("cropId") String cropId, @Param("targetDate") LocalDate targetDate);

    // Statistical queries
    @Query("SELECT STDDEV(m.pricePerKg) FROM MarketPrice m WHERE m.cropId = :cropId AND m.priceDate BETWEEN :startDate AND :endDate")
    Double getPriceStandardDeviation(@Param("cropId") String cropId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT (MAX(m.pricePerKg) - MIN(m.pricePerKg)) / AVG(m.pricePerKg) * 100 FROM MarketPrice m WHERE m.cropId = :cropId AND m.priceDate BETWEEN :startDate AND :endDate")
    Double getPriceVolatility(@Param("cropId") String cropId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Native queries for complex analysis
    @Query(value = "SELECT DATE(price_date) as date, AVG(price_per_kg) as avg_price, COUNT(*) as price_count " +
            "FROM market_prices WHERE crop_id = :cropId AND price_date >= :startDate " +
            "GROUP BY DATE(price_date) ORDER BY DATE(price_date)", nativeQuery = true)
    List<Object[]> getDailyPriceStats(@Param("cropId") String cropId, @Param("startDate") LocalDate startDate);

    @Query(value = "SELECT market_type, location, AVG(price_per_kg) as avg_price, COUNT(*) as price_count " +
            "FROM market_prices WHERE crop_id = :cropId GROUP BY market_type, location " +
            "ORDER BY avg_price DESC", nativeQuery = true)
    List<Object[]> getMarketAnalysis(@Param("cropId") String cropId);

    @Query(value = "SELECT * FROM market_prices WHERE crop_id = :cropId AND " +
            "ABS(DAYOFYEAR(price_date) - DAYOFYEAR(CURRENT_DATE)) <= 14 " +
            "ORDER BY price_date DESC", nativeQuery = true)
    List<MarketPrice> findSimilarSeasonalPrices(@Param("cropId") String cropId);

    @Query(value = "SELECT demand_level, supply_level, AVG(price_per_kg) as avg_price, COUNT(*) as count " +
            "FROM market_prices WHERE crop_id = :cropId " +
            "GROUP BY demand_level, supply_level ORDER BY avg_price DESC", nativeQuery = true)
    List<Object[]> getDemandSupplyPriceAnalysis(@Param("cropId") String cropId);

    @Query(value = "SELECT price_trend, COUNT(*) as trend_count, AVG(price_per_kg) as avg_price " +
            "FROM market_prices WHERE crop_id = :cropId AND price_date >= :startDate " +
            "GROUP BY price_trend", nativeQuery = true)
    List<Object[]> getPriceTrendAnalysis(@Param("cropId") String cropId, @Param("startDate") LocalDate startDate);
}