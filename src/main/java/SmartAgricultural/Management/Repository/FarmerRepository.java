package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.Farmer;
import SmartAgricultural.Management.Model.Farmer.ExperienceLevel;
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
public interface FarmerRepository extends JpaRepository<Farmer, String> {

    // Basic finder methods
    Optional<Farmer> findByUserId(String userId);

    Optional<Farmer> findByFarmerCode(String farmerCode);

    boolean existsByUserId(String userId);

    boolean existsByFarmerCode(String farmerCode);

    // Location-based queries
    List<Farmer> findByProvince(String province);

    List<Farmer> findByDistrict(String district);

    List<Farmer> findBySector(String sector);

    List<Farmer> findByProvinceAndDistrict(String province, String district);

    List<Farmer> findByProvinceAndDistrictAndSector(String province, String district, String sector);

    Page<Farmer> findByProvince(String province, Pageable pageable);

    Page<Farmer> findByDistrict(String district, Pageable pageable);

    // Experience and certification queries
    List<Farmer> findByExperienceLevel(ExperienceLevel experienceLevel);

    Page<Farmer> findByExperienceLevel(ExperienceLevel experienceLevel, Pageable pageable);

    List<Farmer> findByCertificationLevelIsNotNull();

    List<Farmer> findByCertificationLevelIsNull();

    // Cooperative queries
    List<Farmer> findByCooperativeName(String cooperativeName);

    List<Farmer> findByCooperativeNameIsNotNull();

    List<Farmer> findByCooperativeNameIsNull();

    Page<Farmer> findByCooperativeName(String cooperativeName, Pageable pageable);

    // Land size queries
    List<Farmer> findByTotalLandSizeGreaterThan(BigDecimal landSize);

    List<Farmer> findByTotalLandSizeLessThan(BigDecimal landSize);

    List<Farmer> findByTotalLandSizeBetween(BigDecimal minSize, BigDecimal maxSize);

    // Date-based queries
    List<Farmer> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Farmer> findByCreatedAtAfter(LocalDateTime date);

    List<Farmer> findByCreatedAtBefore(LocalDateTime date);

    // Search queries
    @Query("SELECT f FROM Farmer f WHERE f.location LIKE %:location%")
    List<Farmer> findByLocationContaining(@Param("location") String location);

    @Query("SELECT f FROM Farmer f WHERE f.contactPerson LIKE %:contactPerson%")
    List<Farmer> findByContactPersonContaining(@Param("contactPerson") String contactPerson);

    // Geographic queries using coordinates
    @Query("SELECT f FROM Farmer f WHERE f.latitude BETWEEN :minLat AND :maxLat AND f.longitude BETWEEN :minLng AND :maxLng")
    List<Farmer> findFarmersInArea(@Param("minLat") BigDecimal minLatitude,
                                   @Param("maxLat") BigDecimal maxLatitude,
                                   @Param("minLng") BigDecimal minLongitude,
                                   @Param("maxLng") BigDecimal maxLongitude);

    // Statistical queries
    @Query("SELECT COUNT(f) FROM Farmer f WHERE f.experienceLevel = :level")
    Long countByExperienceLevel(@Param("level") ExperienceLevel experienceLevel);

    @Query("SELECT COUNT(f) FROM Farmer f WHERE f.province = :province")
    Long countByProvince(@Param("province") String province);

    @Query("SELECT COUNT(f) FROM Farmer f WHERE f.totalLandSize IS NOT NULL")
    Long countFarmersWithLandSize();

    @Query("SELECT SUM(f.totalLandSize) FROM Farmer f WHERE f.province = :province")
    BigDecimal getTotalLandSizeByProvince(@Param("province") String province);

    @Query("SELECT AVG(f.totalLandSize) FROM Farmer f WHERE f.totalLandSize IS NOT NULL")
    BigDecimal getAverageLandSize();

    // Complex queries
    @Query("SELECT f FROM Farmer f WHERE f.experienceLevel = :level AND f.province = :province")
    List<Farmer> findByExperienceLevelAndProvince(@Param("level") ExperienceLevel experienceLevel,
                                                  @Param("province") String province);

    @Query("SELECT f FROM Farmer f WHERE f.cooperativeName IS NOT NULL AND f.experienceLevel = :level")
    List<Farmer> findCooperativeFarmersByExperienceLevel(@Param("level") ExperienceLevel experienceLevel);

    @Query("SELECT DISTINCT f.province FROM Farmer f ORDER BY f.province")
    List<String> findAllDistinctProvinces();

    @Query("SELECT DISTINCT f.district FROM Farmer f WHERE f.province = :province ORDER BY f.district")
    List<String> findDistinctDistrictsByProvince(@Param("province") String province);

    @Query("SELECT DISTINCT f.sector FROM Farmer f WHERE f.province = :province AND f.district = :district ORDER BY f.sector")
    List<String> findDistinctSectorsByProvinceAndDistrict(@Param("province") String province,
                                                          @Param("district") String district);

    @Query("SELECT DISTINCT f.cooperativeName FROM Farmer f WHERE f.cooperativeName IS NOT NULL ORDER BY f.cooperativeName")
    List<String> findAllDistinctCooperatives();

    // Custom search with multiple criteria
    @Query("SELECT f FROM Farmer f WHERE " +
            "(:province IS NULL OR f.province = :province) AND " +
            "(:district IS NULL OR f.district = :district) AND " +
            "(:sector IS NULL OR f.sector = :sector) AND " +
            "(:experienceLevel IS NULL OR f.experienceLevel = :experienceLevel) AND " +
            "(:cooperativeName IS NULL OR f.cooperativeName LIKE %:cooperativeName%)")
    Page<Farmer> findByCriteria(@Param("province") String province,
                                @Param("district") String district,
                                @Param("sector") String sector,
                                @Param("experienceLevel") ExperienceLevel experienceLevel,
                                @Param("cooperativeName") String cooperativeName,
                                Pageable pageable);

    // Delete queries
    void deleteByUserId(String userId);

    void deleteByFarmerCode(String farmerCode);

    @Query("DELETE FROM Farmer f WHERE f.createdAt < :date")
    void deleteOldRecords(@Param("date") LocalDateTime date);
}