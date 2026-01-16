package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.WeatherData;
import SmartAgricultural.Management.Model.WeatherData.DataQuality;
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
public interface WeatherDataRepository extends JpaRepository<WeatherData, String> {

    // Basic finders
    Optional<WeatherData> findByStationId(String stationId);
    List<WeatherData> findByDataSource(String dataSource);
    List<WeatherData> findByDataQuality(DataQuality dataQuality);

    // Location-based queries
    @Query("SELECT w FROM WeatherData w WHERE " +
            "(:latitude - :radius) <= w.latitude AND w.latitude <= (:latitude + :radius) AND " +
            "(:longitude - :radius) <= w.longitude AND w.longitude <= (:longitude + :radius)")
    List<WeatherData> findByLocationRadius(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radius") BigDecimal radius);

    @Query("SELECT w FROM WeatherData w WHERE w.latitude BETWEEN :minLat AND :maxLat " +
            "AND w.longitude BETWEEN :minLon AND :maxLon")
    List<WeatherData> findByLocationBounds(
            @Param("minLat") BigDecimal minLatitude,
            @Param("maxLat") BigDecimal maxLatitude,
            @Param("minLon") BigDecimal minLongitude,
            @Param("maxLon") BigDecimal maxLongitude);

    // Date-based queries
    List<WeatherData> findByRecordDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<WeatherData> findByRecordDateAfter(LocalDateTime date);
    List<WeatherData> findByRecordDateBefore(LocalDateTime date);

    @Query("SELECT w FROM WeatherData w WHERE DATE(w.recordDate) = DATE(:date)")
    List<WeatherData> findByRecordDate(@Param("date") LocalDateTime date);

    @Query("SELECT w FROM WeatherData w WHERE w.recordDate >= :startDate ORDER BY w.recordDate DESC")
    List<WeatherData> findRecentWeatherData(@Param("startDate") LocalDateTime startDate);

    // Temperature-based queries
    List<WeatherData> findByTemperatureBetween(BigDecimal minTemp, BigDecimal maxTemp);
    List<WeatherData> findByTemperatureGreaterThan(BigDecimal temperature);
    List<WeatherData> findByTemperatureLessThan(BigDecimal temperature);

    @Query("SELECT w FROM WeatherData w WHERE w.temperatureMax >= :threshold")
    List<WeatherData> findHotWeatherData(@Param("threshold") BigDecimal threshold);

    @Query("SELECT w FROM WeatherData w WHERE w.temperatureMin <= :threshold")
    List<WeatherData> findColdWeatherData(@Param("threshold") BigDecimal threshold);

    // Humidity-based queries
    List<WeatherData> findByHumidityBetween(BigDecimal minHumidity, BigDecimal maxHumidity);
    List<WeatherData> findByHumidityGreaterThan(BigDecimal humidity);
    List<WeatherData> findByHumidityLessThan(BigDecimal humidity);

    // Rainfall-based queries
    List<WeatherData> findByRainfallGreaterThan(BigDecimal rainfall);

    @Query("SELECT w FROM WeatherData w WHERE w.rainfall > 0 ORDER BY w.rainfall DESC")
    List<WeatherData> findRainyWeatherData();

    @Query("SELECT w FROM WeatherData w WHERE w.rainfall >= :threshold ORDER BY w.rainfall DESC")
    List<WeatherData> findHeavyRainData(@Param("threshold") BigDecimal threshold);

    // Wind-based queries
    List<WeatherData> findByWindSpeedGreaterThan(BigDecimal windSpeed);
    List<WeatherData> findByWindDirectionBetween(Integer minDirection, Integer maxDirection);

    @Query("SELECT w FROM WeatherData w WHERE w.windSpeed >= :threshold ORDER BY w.windSpeed DESC")
    List<WeatherData> findWindyWeatherData(@Param("threshold") BigDecimal threshold);

    // Weather condition queries
    List<WeatherData> findByWeatherConditionContainingIgnoreCase(String condition);
    List<WeatherData> findByWeatherCondition(String condition);

    // Complex filtering
    @Query("SELECT w FROM WeatherData w WHERE " +
            "(:dataSource IS NULL OR w.dataSource = :dataSource) AND " +
            "(:dataQuality IS NULL OR w.dataQuality = :dataQuality) AND " +
            "(:minTemp IS NULL OR w.temperature >= :minTemp) AND " +
            "(:maxTemp IS NULL OR w.temperature <= :maxTemp) AND " +
            "(:minHumidity IS NULL OR w.humidity >= :minHumidity) AND " +
            "(:maxHumidity IS NULL OR w.humidity <= :maxHumidity) AND " +
            "(:startDate IS NULL OR w.recordDate >= :startDate) AND " +
            "(:endDate IS NULL OR w.recordDate <= :endDate) AND " +
            "(:latitude IS NULL OR :longitude IS NULL OR :radius IS NULL OR " +
            "(ABS(w.latitude - :latitude) <= :radius AND ABS(w.longitude - :longitude) <= :radius))")
    Page<WeatherData> findWithFilters(
            @Param("dataSource") String dataSource,
            @Param("dataQuality") DataQuality dataQuality,
            @Param("minTemp") BigDecimal minTemperature,
            @Param("maxTemp") BigDecimal maxTemperature,
            @Param("minHumidity") BigDecimal minHumidity,
            @Param("maxHumidity") BigDecimal maxHumidity,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radius") BigDecimal radius,
            Pageable pageable);

    // Search functionality
    @Query("SELECT w FROM WeatherData w WHERE " +
            "LOWER(w.weatherCondition) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(w.dataSource) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(w.stationId) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<WeatherData> searchByTerm(@Param("searchTerm") String searchTerm);

    // Statistical queries
    @Query("SELECT AVG(w.temperature) FROM WeatherData w WHERE w.recordDate BETWEEN :startDate AND :endDate")
    BigDecimal getAverageTemperature(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT MAX(w.temperature) FROM WeatherData w WHERE w.recordDate BETWEEN :startDate AND :endDate")
    BigDecimal getMaxTemperature(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT MIN(w.temperature) FROM WeatherData w WHERE w.recordDate BETWEEN :startDate AND :endDate")
    BigDecimal getMinTemperature(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(w.humidity) FROM WeatherData w WHERE w.recordDate BETWEEN :startDate AND :endDate")
    BigDecimal getAverageHumidity(@Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(w.rainfall) FROM WeatherData w WHERE w.recordDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRainfall(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(w.windSpeed) FROM WeatherData w WHERE w.recordDate BETWEEN :startDate AND :endDate")
    BigDecimal getAverageWindSpeed(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    // Count queries
    long countByDataSource(String dataSource);
    long countByDataQuality(DataQuality dataQuality);
    long countByRecordDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(w) FROM WeatherData w WHERE w.rainfall > 0")
    long countRainyDays();

    @Query("SELECT COUNT(w) FROM WeatherData w WHERE w.temperature >= :threshold")
    long countHotDays(@Param("threshold") BigDecimal threshold);

    @Query("SELECT COUNT(w) FROM WeatherData w WHERE w.temperature <= :threshold")
    long countColdDays(@Param("threshold") BigDecimal threshold);

    // Grouped statistics
    @Query("SELECT w.dataSource, COUNT(w) FROM WeatherData w GROUP BY w.dataSource")
    List<Object[]> countByDataSourceGrouped();

    @Query("SELECT w.dataQuality, COUNT(w) FROM WeatherData w GROUP BY w.dataQuality")
    List<Object[]> countByDataQualityGrouped();

    @Query("SELECT w.weatherCondition, COUNT(w) FROM WeatherData w " +
            "WHERE w.weatherCondition IS NOT NULL GROUP BY w.weatherCondition")
    List<Object[]> countByWeatherConditionGrouped();

    @Query("SELECT YEAR(w.recordDate), MONTH(w.recordDate), COUNT(w) FROM WeatherData w " +
            "GROUP BY YEAR(w.recordDate), MONTH(w.recordDate) ORDER BY YEAR(w.recordDate), MONTH(w.recordDate)")
    List<Object[]> getMonthlyWeatherDataCount();

    @Query("SELECT DATE(w.recordDate), COUNT(w) FROM WeatherData w " +
            "WHERE w.recordDate BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(w.recordDate) ORDER BY DATE(w.recordDate)")
    List<Object[]> getDailyWeatherDataCount(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    // Weather trends
    @Query("SELECT DATE(w.recordDate), AVG(w.temperature), AVG(w.humidity), SUM(w.rainfall) " +
            "FROM WeatherData w WHERE w.recordDate BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(w.recordDate) ORDER BY DATE(w.recordDate)")
    List<Object[]> getWeatherTrends(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    // Location-based statistics
    @Query("SELECT w.latitude, w.longitude, AVG(w.temperature), AVG(w.humidity) " +
            "FROM WeatherData w GROUP BY w.latitude, w.longitude")
    List<Object[]> getLocationBasedAverages();

    // Data quality and source analysis
    @Query("SELECT w.dataSource, w.dataQuality, COUNT(w) FROM WeatherData w " +
            "GROUP BY w.dataSource, w.dataQuality")
    List<Object[]> getDataQualityBySource();

    // Latest weather data
    @Query("SELECT w FROM WeatherData w WHERE " +
            "w.latitude = :latitude AND w.longitude = :longitude " +
            "ORDER BY w.recordDate DESC")
    List<WeatherData> findLatestByLocation(@Param("latitude") BigDecimal latitude,
                                           @Param("longitude") BigDecimal longitude,
                                           Pageable pageable);

    @Query("SELECT w FROM WeatherData w WHERE w.stationId = :stationId " +
            "ORDER BY w.recordDate DESC")
    List<WeatherData> findLatestByStationId(@Param("stationId") String stationId,
                                            Pageable pageable);

    // Cleanup operations
    @Query("DELETE FROM WeatherData w WHERE w.recordDate < :cutoffDate AND w.dataQuality = 'POOR'")
    int deletePoorQualityOldData(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Query("DELETE FROM WeatherData w WHERE w.recordDate < :cutoffDate")
    int deleteOldWeatherData(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Extreme weather conditions
    @Query("SELECT w FROM WeatherData w WHERE " +
            "w.temperature >= :hotThreshold OR w.temperature <= :coldThreshold OR " +
            "w.windSpeed >= :windThreshold OR w.rainfall >= :rainThreshold " +
            "ORDER BY w.recordDate DESC")
    List<WeatherData> findExtremeWeatherConditions(
            @Param("hotThreshold") BigDecimal hotThreshold,
            @Param("coldThreshold") BigDecimal coldThreshold,
            @Param("windThreshold") BigDecimal windThreshold,
            @Param("rainThreshold") BigDecimal rainThreshold);

    // Weather alerts
    @Query("SELECT w FROM WeatherData w WHERE " +
            "(w.temperature >= 35 OR w.temperature <= 0 OR " +
            "w.windSpeed >= 25 OR w.rainfall >= 50) AND " +
            "w.recordDate >= :since ORDER BY w.recordDate DESC")
    List<WeatherData> findWeatherAlerts(@Param("since") LocalDateTime since);
}