package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.ClimateImpact;
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
public interface ClimateImpactRepository extends JpaRepository<ClimateImpact, String> {

    // Find by unique fields
    Optional<ClimateImpact> findByImpactCode(String impactCode);

    boolean existsByImpactCode(String impactCode);

    // Find by basic filters
    List<ClimateImpact> findByRegion(String region);

    List<ClimateImpact> findByRegionAndDistrict(String region, String district);

    List<ClimateImpact> findByYear(Integer year);

    List<ClimateImpact> findByYearBetween(Integer startYear, Integer endYear);

    List<ClimateImpact> findBySeason(ClimateImpact.Season season);

    List<ClimateImpact> findByClimateEvent(ClimateImpact.ClimateEvent climateEvent);

    List<ClimateImpact> findByEventIntensity(ClimateImpact.EventIntensity eventIntensity);

    // Find by crop
    List<ClimateImpact> findByCropId(String cropId);

    List<ClimateImpact> findByCropIdAndYear(String cropId, Integer year);

    // Find by reporter
    List<ClimateImpact> findByReportedBy(String reportedBy);

    List<ClimateImpact> findByReportedByAndVerified(String reportedBy, Boolean verified);

    // Find by verification status
    List<ClimateImpact> findByVerified(Boolean verified);

    List<ClimateImpact> findByVerifiedAndYear(Boolean verified, Integer year);

    // Find by date ranges
    List<ClimateImpact> findByEventStartDateBetween(LocalDate startDate, LocalDate endDate);

    List<ClimateImpact> findByEventEndDateBetween(LocalDate startDate, LocalDate endDate);

    List<ClimateImpact> findByReportDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<ClimateImpact> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find ongoing events
    @Query("SELECT ci FROM ClimateImpact ci WHERE ci.eventEndDate IS NULL OR ci.eventEndDate >= CURRENT_DATE")
    List<ClimateImpact> findOngoingEvents();

    // Find recent events
    @Query("SELECT ci FROM ClimateImpact ci WHERE ci.eventStartDate >= :date")
    List<ClimateImpact> findRecentEvents(@Param("date") LocalDate date);

    // Find by economic impact
    List<ClimateImpact> findByEconomicLossGreaterThan(BigDecimal minLoss);

    List<ClimateImpact> findByEconomicLossBetween(BigDecimal minLoss, BigDecimal maxLoss);

    // Find by affected area
    List<ClimateImpact> findByAffectedAreaGreaterThan(BigDecimal minArea);

    List<ClimateImpact> findByCropAreaAffectedGreaterThan(BigDecimal minArea);

    // Find by population impact
    List<ClimateImpact> findByAffectedPopulationGreaterThan(Integer minPopulation);

    List<ClimateImpact> findByAffectedHouseholdsGreaterThan(Integer minHouseholds);

    // Find by effectiveness ratings
    List<ClimateImpact> findByEarlyWarningEffectiveness(ClimateImpact.WarningEffectiveness effectiveness);

    List<ClimateImpact> findByResponseEffectiveness(ClimateImpact.ResponseEffectiveness effectiveness);

    // Complex queries with multiple filters
    @Query("SELECT ci FROM ClimateImpact ci WHERE ci.region = :region AND ci.year = :year AND ci.climateEvent = :event")
    List<ClimateImpact> findByRegionYearAndEvent(@Param("region") String region,
                                                 @Param("year") Integer year,
                                                 @Param("event") ClimateImpact.ClimateEvent event);

    @Query("SELECT ci FROM ClimateImpact ci WHERE ci.region = :region AND ci.season = :season AND ci.eventIntensity = :intensity")
    List<ClimateImpact> findByRegionSeasonAndIntensity(@Param("region") String region,
                                                       @Param("season") ClimateImpact.Season season,
                                                       @Param("intensity") ClimateImpact.EventIntensity intensity);

    // Search queries
    @Query("SELECT ci FROM ClimateImpact ci WHERE " +
            "LOWER(ci.region) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(ci.district) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(ci.impactCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(ci.socialImpact) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(ci.environmentalImpact) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<ClimateImpact> searchByKeyword(@Param("query") String query);

    @Query("SELECT ci FROM ClimateImpact ci WHERE " +
            "(:region IS NULL OR ci.region = :region) AND " +
            "(:district IS NULL OR ci.district = :district) AND " +
            "(:year IS NULL OR ci.year = :year) AND " +
            "(:season IS NULL OR ci.season = :season) AND " +
            "(:event IS NULL OR ci.climateEvent = :event) AND " +
            "(:intensity IS NULL OR ci.eventIntensity = :intensity) AND " +
            "(:verified IS NULL OR ci.verified = :verified)")
    Page<ClimateImpact> findWithFilters(@Param("region") String region,
                                        @Param("district") String district,
                                        @Param("year") Integer year,
                                        @Param("season") ClimateImpact.Season season,
                                        @Param("event") ClimateImpact.ClimateEvent event,
                                        @Param("intensity") ClimateImpact.EventIntensity intensity,
                                        @Param("verified") Boolean verified,
                                        Pageable pageable);

    // Statistical queries
    @Query("SELECT COUNT(ci) FROM ClimateImpact ci WHERE ci.year = :year")
    Long countByYear(@Param("year") Integer year);

    @Query("SELECT COUNT(ci) FROM ClimateImpact ci WHERE ci.region = :region")
    Long countByRegion(@Param("region") String region);

    @Query("SELECT COUNT(ci) FROM ClimateImpact ci WHERE ci.climateEvent = :event")
    Long countByClimateEvent(@Param("event") ClimateImpact.ClimateEvent event);

    @Query("SELECT COUNT(ci) FROM ClimateImpact ci WHERE ci.verified = true")
    Long countVerified();

    @Query("SELECT COUNT(ci) FROM ClimateImpact ci WHERE ci.verified = false")
    Long countUnverified();

    @Query("SELECT SUM(ci.economicLoss) FROM ClimateImpact ci WHERE ci.year = :year AND ci.verified = true")
    BigDecimal sumEconomicLossByYear(@Param("year") Integer year);

    @Query("SELECT SUM(ci.affectedArea) FROM ClimateImpact ci WHERE ci.region = :region AND ci.year = :year")
    BigDecimal sumAffectedAreaByRegionAndYear(@Param("region") String region, @Param("year") Integer year);

    @Query("SELECT SUM(ci.affectedPopulation) FROM ClimateImpact ci WHERE ci.climateEvent = :event AND ci.year = :year")
    Long sumAffectedPopulationByEventAndYear(@Param("event") ClimateImpact.ClimateEvent event, @Param("year") Integer year);

    // Top queries
    @Query("SELECT ci FROM ClimateImpact ci WHERE ci.verified = true ORDER BY ci.economicLoss DESC")
    List<ClimateImpact> findTopByEconomicLoss(Pageable pageable);

    @Query("SELECT ci FROM ClimateImpact ci WHERE ci.verified = true ORDER BY ci.affectedArea DESC")
    List<ClimateImpact> findTopByAffectedArea(Pageable pageable);

    @Query("SELECT ci FROM ClimateImpact ci WHERE ci.verified = true ORDER BY ci.affectedPopulation DESC")
    List<ClimateImpact> findTopByAffectedPopulation(Pageable pageable);

    // Trend analysis queries
    @Query("SELECT ci.year, COUNT(ci), AVG(ci.economicLoss) FROM ClimateImpact ci " +
            "WHERE ci.climateEvent = :event AND ci.verified = true " +
            "GROUP BY ci.year ORDER BY ci.year")
    List<Object[]> getYearlyTrendsByEvent(@Param("event") ClimateImpact.ClimateEvent event);

    @Query("SELECT ci.region, COUNT(ci), SUM(ci.economicLoss) FROM ClimateImpact ci " +
            "WHERE ci.year = :year AND ci.verified = true " +
            "GROUP BY ci.region ORDER BY COUNT(ci) DESC")
    List<Object[]> getRegionalStatsByYear(@Param("year") Integer year);

    @Query("SELECT ci.season, COUNT(ci), AVG(ci.economicLoss) FROM ClimateImpact ci " +
            "WHERE ci.year = :year AND ci.verified = true " +
            "GROUP BY ci.season")
    List<Object[]> getSeasonalStatsByYear(@Param("year") Integer year);

    // Time-based analysis
    @Query("SELECT ci FROM ClimateImpact ci WHERE " +
            "ci.eventStartDate >= :startDate AND ci.eventStartDate <= :endDate AND " +
            "ci.climateEvent = :event ORDER BY ci.eventStartDate")
    List<ClimateImpact> findEventsByDateRange(@Param("event") ClimateImpact.ClimateEvent event,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    // Risk assessment queries
    @Query("SELECT ci FROM ClimateImpact ci WHERE " +
            "ci.eventIntensity IN ('SEVERE', 'EXTREME') AND " +
            "ci.economicLoss > :threshold AND " +
            "ci.verified = true " +
            "ORDER BY ci.economicLoss DESC")
    List<ClimateImpact> findHighRiskImpacts(@Param("threshold") BigDecimal threshold);

    @Query("SELECT ci.region, COUNT(ci) as frequency FROM ClimateImpact ci " +
            "WHERE ci.climateEvent = :event AND ci.year >= :startYear " +
            "GROUP BY ci.region " +
            "HAVING COUNT(ci) >= :minFrequency " +
            "ORDER BY frequency DESC")
    List<Object[]> findHighRiskRegions(@Param("event") ClimateImpact.ClimateEvent event,
                                       @Param("startYear") Integer startYear,
                                       @Param("minFrequency") Long minFrequency);

    // Recovery analysis
    @Query("SELECT ci FROM ClimateImpact ci WHERE " +
            "ci.recoveryTimeDays IS NOT NULL AND " +
            "ci.recoveryCost IS NOT NULL AND " +
            "ci.verified = true " +
            "ORDER BY ci.recoveryTimeDays DESC")
    List<ClimateImpact> findImpactsWithRecoveryData(Pageable pageable);

    // Financial analysis
    @Query("SELECT ci FROM ClimateImpact ci WHERE " +
            "ci.insurancePayout IS NOT NULL OR " +
            "ci.governmentAssistance IS NOT NULL OR " +
            "ci.internationalAid IS NOT NULL")
    List<ClimateImpact> findImpactsWithFinancialSupport();

    @Query("SELECT SUM(COALESCE(ci.insurancePayout, 0) + COALESCE(ci.governmentAssistance, 0) + COALESCE(ci.internationalAid, 0)) " +
            "FROM ClimateImpact ci WHERE ci.year = :year AND ci.verified = true")
    BigDecimal sumTotalAidByYear(@Param("year") Integer year);

    // Effectiveness analysis
    @Query("SELECT ci.earlyWarningEffectiveness, COUNT(ci), AVG(ci.economicLoss) " +
            "FROM ClimateImpact ci WHERE ci.verified = true " +
            "GROUP BY ci.earlyWarningEffectiveness")
    List<Object[]> getWarningEffectivenessStats();

    @Query("SELECT ci.responseEffectiveness, COUNT(ci), AVG(ci.economicLoss) " +
            "FROM ClimateImpact ci WHERE ci.verified = true " +
            "GROUP BY ci.responseEffectiveness")
    List<Object[]> getResponseEffectivenessStats();

    // Custom deletion methods
    @Query("DELETE FROM ClimateImpact ci WHERE ci.verified = false AND ci.createdAt < :cutoffDate")
    void deleteUnverifiedOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Latest entries
    List<ClimateImpact> findTop10ByOrderByCreatedAtDesc();

    List<ClimateImpact> findTop10ByVerifiedTrueOrderByCreatedAtDesc();

    // Find by multiple regions
    List<ClimateImpact> findByRegionIn(List<String> regions);

    // Find by multiple events
    List<ClimateImpact> findByClimateEventIn(List<ClimateImpact.ClimateEvent> events);

    // Find impacts requiring emergency response
    @Query("SELECT ci FROM ClimateImpact ci WHERE " +
            "ci.eventIntensity IN ('SEVERE', 'EXTREME') OR " +
            "ci.economicLoss >= :emergencyThreshold")
    List<ClimateImpact> findEmergencyResponseRequired(@Param("emergencyThreshold") BigDecimal emergencyThreshold);

    // Find impacts by crop and season
    @Query("SELECT ci FROM ClimateImpact ci WHERE " +
            "ci.cropId = :cropId AND ci.season = :season AND " +
            "ci.year >= :startYear ORDER BY ci.year DESC")
    List<ClimateImpact> findByCropAndSeasonSinceYear(@Param("cropId") String cropId,
                                                     @Param("season") ClimateImpact.Season season,
                                                     @Param("startYear") Integer startYear);
}