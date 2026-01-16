package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.Farm;
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
public interface FarmRepository extends JpaRepository<Farm, String> {

    // Find by farmer ID
    List<Farm> findByFarmerId(String farmerId);
    Page<Farm> findByFarmerId(String farmerId, Pageable pageable);

    // Find by farm name
    List<Farm> findByFarmNameContainingIgnoreCase(String farmName);
    Optional<Farm> findByFarmNameIgnoreCase(String farmName);

    // Find by farm code
    Optional<Farm> findByFarmCode(String farmCode);

    // Find by soil type
    List<Farm> findBySoilTypeIgnoreCase(String soilType);
    Page<Farm> findBySoilTypeIgnoreCase(String soilType, Pageable pageable);

    // Find by irrigation system
    List<Farm> findByIrrigationSystem(Farm.IrrigationSystem irrigationSystem);

    // Find by road access quality
    List<Farm> findByRoadAccessQuality(Farm.RoadAccessQuality roadAccessQuality);

    // Find by electricity availability
    List<Farm> findByElectricityAvailable(Boolean electricityAvailable);

    // Find by topography
    List<Farm> findByTopography(String topography);

    // Find by farm size range
    List<Farm> findByFarmSizeBetween(BigDecimal minSize, BigDecimal maxSize);
    Page<Farm> findByFarmSizeBetween(BigDecimal minSize, BigDecimal maxSize, Pageable pageable);

    // Find by minimum farm size
    List<Farm> findByFarmSizeGreaterThanEqual(BigDecimal minSize);

    // Find by maximum farm size
    List<Farm> findByFarmSizeLessThanEqual(BigDecimal maxSize);

    // Find within coordinates range
    @Query("SELECT f FROM Farm f WHERE f.latitude BETWEEN :minLat AND :maxLat AND f.longitude BETWEEN :minLon AND :maxLon")
    List<Farm> findFarmsWithinCoordinates(@Param("minLat") BigDecimal minLatitude,
                                          @Param("maxLat") BigDecimal maxLatitude,
                                          @Param("minLon") BigDecimal minLongitude,
                                          @Param("maxLon") BigDecimal maxLongitude);

    // Find by water source containing
    List<Farm> findByWaterSourceContainingIgnoreCase(String waterSource);

    // Custom queries
    @Query("SELECT f FROM Farm f WHERE f.farmerId = :farmerId AND f.farmSize >= :minSize")
    List<Farm> findFarmerFarmsWithMinimumSize(@Param("farmerId") String farmerId,
                                              @Param("minSize") BigDecimal minSize);

    @Query("SELECT COUNT(f) FROM Farm f WHERE f.farmerId = :farmerId")
    Long countFarmsByFarmerId(@Param("farmerId") String farmerId);

    @Query("SELECT SUM(f.farmSize) FROM Farm f WHERE f.farmerId = :farmerId")
    BigDecimal getTotalFarmSizeByFarmerId(@Param("farmerId") String farmerId);

    @Query("SELECT AVG(f.farmSize) FROM Farm f WHERE f.farmerId = :farmerId")
    BigDecimal getAverageFarmSizeByFarmerId(@Param("farmerId") String farmerId);

    // Find large farms (> 10 hectares)
    @Query("SELECT f FROM Farm f WHERE f.farmSize > 10.0")
    List<Farm> findLargeFarms();

    // Find small farms (<= 2 hectares)
    @Query("SELECT f FROM Farm f WHERE f.farmSize <= 2.0")
    List<Farm> findSmallFarms();

    // Find medium farms (2-10 hectares)
    @Query("SELECT f FROM Farm f WHERE f.farmSize > 2.0 AND f.farmSize <= 10.0")
    List<Farm> findMediumFarms();

    // Find farms with specific characteristics
    @Query("SELECT f FROM Farm f WHERE f.irrigationSystem = :irrigation AND f.soilType = :soilType")
    List<Farm> findByIrrigationSystemAndSoilType(@Param("irrigation") Farm.IrrigationSystem irrigationSystem,
                                                 @Param("soilType") String soilType);

    // Find farms by multiple criteria
    @Query("SELECT f FROM Farm f WHERE " +
            "(:farmerId IS NULL OR f.farmerId = :farmerId) AND " +
            "(:soilType IS NULL OR f.soilType = :soilType) AND " +
            "(:irrigationSystem IS NULL OR f.irrigationSystem = :irrigationSystem) AND " +
            "(:electricityAvailable IS NULL OR f.electricityAvailable = :electricityAvailable) AND " +
            "(:minSize IS NULL OR f.farmSize >= :minSize) AND " +
            "(:maxSize IS NULL OR f.farmSize <= :maxSize)")
    Page<Farm> findFarmsByCriteria(@Param("farmerId") String farmerId,
                                   @Param("soilType") String soilType,
                                   @Param("irrigationSystem") Farm.IrrigationSystem irrigationSystem,
                                   @Param("electricityAvailable") Boolean electricityAvailable,
                                   @Param("minSize") BigDecimal minSize,
                                   @Param("maxSize") BigDecimal maxSize,
                                   Pageable pageable);

    // Statistical queries
    @Query("SELECT COUNT(f) FROM Farm f")
    Long getTotalFarmsCount();

    @Query("SELECT SUM(f.farmSize) FROM Farm f")
    BigDecimal getTotalFarmsArea();

    @Query("SELECT AVG(f.farmSize) FROM Farm f")
    BigDecimal getAverageFarmSize();

    @Query("SELECT f.soilType, COUNT(f) FROM Farm f GROUP BY f.soilType")
    List<Object[]> getFarmCountBySoilType();

    @Query("SELECT f.irrigationSystem, COUNT(f) FROM Farm f GROUP BY f.irrigationSystem")
    List<Object[]> getFarmCountByIrrigationSystem();

    // Check if farm code exists
    boolean existsByFarmCode(String farmCode);

    // Check if farmer has farms
    boolean existsByFarmerId(String farmerId);

    // Delete by farmer ID
    void deleteByFarmerId(String farmerId);

    // Find farms near location (within radius)
    @Query("SELECT f FROM Farm f WHERE " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(f.latitude)) * " +
            "cos(radians(f.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
            "sin(radians(f.latitude)))) <= :radiusKm")
    List<Farm> findFarmsNearLocation(@Param("latitude") BigDecimal latitude,
                                     @Param("longitude") BigDecimal longitude,
                                     @Param("radiusKm") Double radiusKm);
}