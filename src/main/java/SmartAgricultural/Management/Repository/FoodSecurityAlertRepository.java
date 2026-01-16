package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.FoodSecurityAlert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodSecurityAlertRepository extends JpaRepository<FoodSecurityAlert, String> {

    // Find by alert code
    Optional<FoodSecurityAlert> findByAlertCode(String alertCode);

    // Find active alerts
    Page<FoodSecurityAlert> findByIsActiveTrueAndExpiryDateAfter(
            LocalDateTime currentDate, Pageable pageable);

    // Count active alerts
    long countByIsActiveTrue();

    // Find by category
    Page<FoodSecurityAlert> findByAlertCategory(
            FoodSecurityAlert.AlertCategory category, Pageable pageable);

    // Count by category
    long countByAlertCategory(FoodSecurityAlert.AlertCategory category);

    // Find by alert level
    Page<FoodSecurityAlert> findByAlertLevel(
            FoodSecurityAlert.AlertLevel level, Pageable pageable);

    // Count by alert level
    long countByAlertLevel(FoodSecurityAlert.AlertLevel level);

    // Find critical alerts
    List<FoodSecurityAlert> findByAlertLevelAndIsActiveTrue(
            FoodSecurityAlert.AlertLevel level);

    // Count critical alerts
    long countByAlertLevelAndIsActiveTrue(FoodSecurityAlert.AlertLevel level);

    // Find by region
    Page<FoodSecurityAlert> findByAffectedRegionContainingIgnoreCase(
            String region, Pageable pageable);

    // Find by date range
    Page<FoodSecurityAlert> findByAlertDateBetween(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // Find by resolution status
    Page<FoodSecurityAlert> findByResolutionStatus(
            FoodSecurityAlert.ResolutionStatus status, Pageable pageable);

    // Find unresolved alerts
    Page<FoodSecurityAlert> findByResolutionStatusNot(
            FoodSecurityAlert.ResolutionStatus status, Pageable pageable);

    // Count by resolution status
    long countByResolutionStatus(FoodSecurityAlert.ResolutionStatus status);

    // Count unresolved alerts
    long countByResolutionStatusNot(FoodSecurityAlert.ResolutionStatus status);

    // Find overdue alerts
    List<FoodSecurityAlert> findByResponseRequiredTrueAndResponseDeadlineBefore(
            LocalDateTime currentDate);

    // Count overdue alerts
    long countByResponseRequiredTrueAndResponseDeadlineBefore(LocalDateTime currentDate);

    // Find by severity score range
    Page<FoodSecurityAlert> findBySeverityScoreBetween(
            int minScore, int maxScore, Pageable pageable);

    // Search in title and description
    Page<FoodSecurityAlert> findByAlertTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String titleKeyword, String descriptionKeyword, Pageable pageable);

    // Find by creator
    Page<FoodSecurityAlert> findByCreatedBy(String userId, Pageable pageable);

    // Find by population threshold
    Page<FoodSecurityAlert> findByAffectedPopulationGreaterThan(
            int threshold, Pageable pageable);

    // Find with media attention
    Page<FoodSecurityAlert> findByMediaCoverageTrueOrInternationalAttentionTrue(
            Pageable pageable);

    // Find recent alerts
    List<FoodSecurityAlert> findByAlertDateAfterOrderByAlertDateDesc(
            LocalDateTime startDate);

    // Check if alert code exists
    boolean existsByAlertCode(String alertCode);

    // Custom queries
    @Query("SELECT a FROM FoodSecurityAlert a WHERE a.isActive = true " +
            "AND a.alertLevel IN :levels ORDER BY a.alertDate DESC")
    List<FoodSecurityAlert> findActiveAlertsByLevels(
            @Param("levels") List<FoodSecurityAlert.AlertLevel> levels);

    @Query("SELECT a FROM FoodSecurityAlert a WHERE a.isActive = true " +
            "AND a.escalationLevel >= :minEscalationLevel ORDER BY a.escalationLevel DESC")
    List<FoodSecurityAlert> findEscalatedAlerts(
            @Param("minEscalationLevel") int minEscalationLevel);

    @Query("SELECT a FROM FoodSecurityAlert a WHERE a.affectedRegion = :region " +
            "AND a.alertDate BETWEEN :startDate AND :endDate " +
            "ORDER BY a.alertDate DESC")
    List<FoodSecurityAlert> findAlertsByRegionAndDateRange(
            @Param("region") String region,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(a) FROM FoodSecurityAlert a WHERE a.alertDate >= :startDate " +
            "AND a.alertCategory = :category")
    long countAlertsByCategoryAndDateAfter(
            @Param("category") FoodSecurityAlert.AlertCategory category,
            @Param("startDate") LocalDateTime startDate);

    @Query("SELECT a FROM FoodSecurityAlert a WHERE a.sourceReliability = :reliability " +
            "AND a.isActive = true ORDER BY a.alertDate DESC")
    List<FoodSecurityAlert> findBySourceReliability(
            @Param("reliability") FoodSecurityAlert.SourceReliability reliability);

    @Query("SELECT a FROM FoodSecurityAlert a WHERE " +
            "(a.alertTitle LIKE %:keyword% OR a.description LIKE %:keyword% " +
            "OR a.affectedRegion LIKE %:keyword%) " +
            "AND a.isActive = true ORDER BY a.alertDate DESC")
    List<FoodSecurityAlert> searchActiveAlerts(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT a.affectedRegion FROM FoodSecurityAlert a " +
            "WHERE a.affectedRegion IS NOT NULL ORDER BY a.affectedRegion")
    List<String> findDistinctAffectedRegions();

    @Query("SELECT a FROM FoodSecurityAlert a WHERE a.resolutionStatus = :status " +
            "AND a.alertLevel IN (:levels) ORDER BY a.alertDate DESC")
    List<FoodSecurityAlert> findByStatusAndLevels(
            @Param("status") FoodSecurityAlert.ResolutionStatus status,
            @Param("levels") List<FoodSecurityAlert.AlertLevel> levels);

    @Query("SELECT a FROM FoodSecurityAlert a WHERE a.economicImpact IS NOT NULL " +
            "AND a.economicImpact > 0 ORDER BY a.economicImpact DESC")
    List<FoodSecurityAlert> findAlertsWithEconomicImpact();

    @Query("SELECT AVG(a.severityScore) FROM FoodSecurityAlert a WHERE a.alertCategory = :category")
    Double getAverageSeverityByCategory(
            @Param("category") FoodSecurityAlert.AlertCategory category);

    @Query("SELECT COUNT(a) FROM FoodSecurityAlert a WHERE a.createdAt >= :startDate " +
            "AND a.createdAt < :endDate")
    long countAlertsCreatedBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM FoodSecurityAlert a WHERE a.expiryDate IS NOT NULL " +
            "AND a.expiryDate BETWEEN :now AND :futureDate " +
            "AND a.isActive = true ORDER BY a.expiryDate ASC")
    List<FoodSecurityAlert> findAlertsExpiringBetween(
            @Param("now") LocalDateTime now,
            @Param("futureDate") LocalDateTime futureDate);

    @Query("SELECT a FROM FoodSecurityAlert a WHERE " +
            "a.resolutionStatus = 'IN_PROGRESS' " +
            "AND a.createdAt < :thresholdDate ORDER BY a.createdAt ASC")
    List<FoodSecurityAlert> findStalledAlerts(@Param("thresholdDate") LocalDateTime thresholdDate);
}