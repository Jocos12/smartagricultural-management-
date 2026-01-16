package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.Crop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CropRepository extends JpaRepository<Crop, String> {

    /**
     * Find crop by name (case insensitive)
     */
    Optional<Crop> findByCropNameIgnoreCase(String cropName);

    /**
     * Check if crop name exists
     */
    boolean existsByCropNameIgnoreCase(String cropName);

    /**
     * Find crops by type
     */
    List<Crop> findByCropType(Crop.CropType cropType);

    /**
     * Find crops by market demand level
     */
    List<Crop> findByMarketDemandLevel(Crop.MarketDemandLevel marketDemandLevel);

    /**
     * Find crops by planting season
     */
    List<Crop> findByPlantingSeasonIgnoreCase(String plantingSeason);

    /**
     * Find crops by harvest season
     */
    List<Crop> findByHarvestSeasonIgnoreCase(String harvestSeason);

    /**
     * Find crops with growing period less than or equal to specified days
     */
    List<Crop> findByGrowingPeriodDaysLessThanEqual(Integer days);

    /**
     * Find crops with growing period between min and max days
     */
    List<Crop> findByGrowingPeriodDaysBetween(Integer minDays, Integer maxDays);

    /**
     * Find crops by variety
     */
    List<Crop> findByVarietyIgnoreCase(String variety);

    /**
     * Find crops by scientific name
     */
    Optional<Crop> findByScientificNameIgnoreCase(String scientificName);

    /**
     * Find crops with storage life greater than specified days
     */
    List<Crop> findByStorageLifeDaysGreaterThan(Integer days);

    /**
     * Find crops suitable for temperature range
     */
    @Query("SELECT c FROM Crop c WHERE " +
            "(c.temperatureMin IS NULL OR c.temperatureMin <= :temperature) AND " +
            "(c.temperatureMax IS NULL OR c.temperatureMax >= :temperature)")
    List<Crop> findBySuitableTemperature(@Param("temperature") BigDecimal temperature);

    /**
     * Find crops suitable for pH range
     */
    @Query("SELECT c FROM Crop c WHERE " +
            "(c.soilPhMin IS NULL OR c.soilPhMin <= :ph) AND " +
            "(c.soilPhMax IS NULL OR c.soilPhMax >= :ph)")
    List<Crop> findBySuitablePh(@Param("ph") BigDecimal ph);

    /**
     * Find crops with water requirement less than or equal to specified amount
     */
    List<Crop> findByWaterRequirementLessThanEqual(BigDecimal waterRequirement);

    /**
     * Find crops with rainfall requirement between min and max
     */
    List<Crop> findByRainfallRequirementBetween(BigDecimal minRainfall, BigDecimal maxRainfall);

    /**
     * Search crops by name or scientific name
     */
    @Query("SELECT c FROM Crop c WHERE " +
            "LOWER(c.cropName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.scientificName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.variety) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Crop> searchByNameOrScientificName(@Param("query") String query);

    /**
     * Find high demand crops
     */
    @Query("SELECT c FROM Crop c WHERE c.marketDemandLevel = 'HIGH'")
    List<Crop> findHighDemandCrops();

    /**
     * Find short-season crops (growing period <= 90 days)
     */
    @Query("SELECT c FROM Crop c WHERE c.growingPeriodDays <= 90")
    List<Crop> findShortSeasonCrops();

    /**
     * Find long-storage crops (storage life > 365 days)
     */
    @Query("SELECT c FROM Crop c WHERE c.storageLifeDays > 365")
    List<Crop> findLongStorageCrops();

    /**
     * Find crops by multiple criteria
     */
    @Query("SELECT c FROM Crop c WHERE " +
            "(:cropType IS NULL OR c.cropType = :cropType) AND " +
            "(:marketDemand IS NULL OR c.marketDemandLevel = :marketDemand) AND " +
            "(:plantingSeason IS NULL OR LOWER(c.plantingSeason) = LOWER(:plantingSeason)) AND " +
            "(:maxGrowingPeriod IS NULL OR c.growingPeriodDays <= :maxGrowingPeriod) AND " +
            "(:minStorageLife IS NULL OR c.storageLifeDays >= :minStorageLife)")
    Page<Crop> findWithFilters(@Param("cropType") Crop.CropType cropType,
                               @Param("marketDemand") Crop.MarketDemandLevel marketDemand,
                               @Param("plantingSeason") String plantingSeason,
                               @Param("maxGrowingPeriod") Integer maxGrowingPeriod,
                               @Param("minStorageLife") Integer minStorageLife,
                               Pageable pageable);

    /**
     * Get crop statistics by type
     */
    @Query("SELECT c.cropType, COUNT(c) FROM Crop c GROUP BY c.cropType")
    List<Object[]> getCropStatisticsByType();

    /**
     * Get crop statistics by market demand
     */
    @Query("SELECT c.marketDemandLevel, COUNT(c) FROM Crop c GROUP BY c.marketDemandLevel")
    List<Object[]> getCropStatisticsByMarketDemand();

    /**
     * Get average growing period by crop type
     */
    @Query("SELECT c.cropType, AVG(c.growingPeriodDays) FROM Crop c " +
            "WHERE c.growingPeriodDays IS NOT NULL GROUP BY c.cropType")
    List<Object[]> getAverageGrowingPeriodByType();

    /**
     * Get crops suitable for specific conditions
     */
    @Query("SELECT c FROM Crop c WHERE " +
            "(:temperature IS NULL OR " +
            " (c.temperatureMin IS NULL OR c.temperatureMin <= :temperature) AND " +
            " (c.temperatureMax IS NULL OR c.temperatureMax >= :temperature)) AND " +
            "(:ph IS NULL OR " +
            " (c.soilPhMin IS NULL OR c.soilPhMin <= :ph) AND " +
            " (c.soilPhMax IS NULL OR c.soilPhMax >= :ph)) AND " +
            "(:rainfall IS NULL OR " +
            " (c.rainfallRequirement IS NULL OR c.rainfallRequirement <= :rainfall))")
    List<Crop> findSuitableForConditions(@Param("temperature") BigDecimal temperature,
                                         @Param("ph") BigDecimal ph,
                                         @Param("rainfall") BigDecimal rainfall);

    /**
     * Find crops by planting and harvest season
     */
    @Query("SELECT c FROM Crop c WHERE " +
            "LOWER(c.plantingSeason) = LOWER(:plantingSeason) AND " +
            "LOWER(c.harvestSeason) = LOWER(:harvestSeason)")
    List<Crop> findByPlantingAndHarvestSeason(@Param("plantingSeason") String plantingSeason,
                                              @Param("harvestSeason") String harvestSeason);

    /**
     * Get most recent crops
     */
    @Query("SELECT c FROM Crop c ORDER BY c.createdAt DESC")
    List<Crop> findMostRecentCrops(Pageable pageable);

    /**
     * Count crops by type
     */
    Long countByCropType(Crop.CropType cropType);

    /**
     * Count high demand crops
     */
    @Query("SELECT COUNT(c) FROM Crop c WHERE c.marketDemandLevel = 'HIGH'")
    Long countHighDemandCrops();

    /**
     * Find crops with similar characteristics
     */
    @Query("SELECT c FROM Crop c WHERE c.id != :cropId AND " +
            "(c.cropType = :cropType OR " +
            " c.plantingSeason = :plantingSeason OR " +
            " c.harvestSeason = :harvestSeason OR " +
            " c.marketDemandLevel = :marketDemand)")
    List<Crop> findSimilarCrops(@Param("cropId") String cropId,
                                @Param("cropType") Crop.CropType cropType,
                                @Param("plantingSeason") String plantingSeason,
                                @Param("harvestSeason") String harvestSeason,
                                @Param("marketDemand") Crop.MarketDemandLevel marketDemand,
                                Pageable pageable);

    /**
     * Get seasonal crop distribution
     */
    @Query("SELECT c.plantingSeason, COUNT(c) FROM Crop c " +
            "WHERE c.plantingSeason IS NOT NULL " +
            "GROUP BY c.plantingSeason ORDER BY COUNT(c) DESC")
    List<Object[]> getSeasonalDistribution();

    /**
     * Find crops with missing information
     */
    @Query("SELECT c FROM Crop c WHERE " +
            "c.scientificName IS NULL OR " +
            "c.variety IS NULL OR " +
            "c.waterRequirement IS NULL OR " +
            "c.climaticRequirement IS NULL OR " +
            "c.nutritionalValue IS NULL")
    List<Crop> findCropsWithMissingInfo();

    /**
     * Get top varieties by type
     */
    @Query("SELECT c.variety, COUNT(c) FROM Crop c " +
            "WHERE c.cropType = :cropType AND c.variety IS NOT NULL " +
            "GROUP BY c.variety ORDER BY COUNT(c) DESC")
    List<Object[]> getTopVarietiesByType(@Param("cropType") Crop.CropType cropType, Pageable pageable);

    /**
     * Find drought resistant crops (low water requirement)
     */
    @Query("SELECT c FROM Crop c WHERE c.waterRequirement IS NOT NULL AND c.waterRequirement <= :maxWaterRequirement")
    List<Crop> findDroughtResistantCrops(@Param("maxWaterRequirement") BigDecimal maxWaterRequirement);

    /**
     * Find crops suitable for specific climate
     */
    @Query("SELECT c FROM Crop c WHERE " +
            "LOWER(c.climaticRequirement) LIKE LOWER(CONCAT('%', :climate, '%'))")
    List<Crop> findByClimateRequirement(@Param("climate") String climate);
}