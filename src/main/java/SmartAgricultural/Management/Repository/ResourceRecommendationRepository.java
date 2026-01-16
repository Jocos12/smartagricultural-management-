package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.ResourceRecommendation;
import SmartAgricultural.Management.Model.ResourceRecommendation.ResourceType;
import SmartAgricultural.Management.Model.ResourceRecommendation.RecommendationCategory;
import SmartAgricultural.Management.Model.ResourceRecommendation.PriorityLevel;
import SmartAgricultural.Management.Model.ResourceRecommendation.RecommendationStatus;
import SmartAgricultural.Management.Model.ResourceRecommendation.ImplementationDifficulty;
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
public interface ResourceRecommendationRepository extends JpaRepository<ResourceRecommendation, String> {

    // Basic finders
    Optional<ResourceRecommendation> findByRecommendationCode(String recommendationCode);

    List<ResourceRecommendation> findByFarmId(String farmId);

    Page<ResourceRecommendation> findByFarmId(String farmId, Pageable pageable);

    List<ResourceRecommendation> findByCropProductionId(String cropProductionId);

    // Status-based queries
    List<ResourceRecommendation> findByStatus(RecommendationStatus status);

    Page<ResourceRecommendation> findByStatus(RecommendationStatus status, Pageable pageable);

    List<ResourceRecommendation> findByFarmIdAndStatus(String farmId, RecommendationStatus status);

    Page<ResourceRecommendation> findByFarmIdAndStatus(String farmId, RecommendationStatus status, Pageable pageable);

    // Resource type queries
    List<ResourceRecommendation> findByResourceType(ResourceType resourceType);

    List<ResourceRecommendation> findByFarmIdAndResourceType(String farmId, ResourceType resourceType);

    // Category-based queries
    List<ResourceRecommendation> findByRecommendationCategory(RecommendationCategory category);

    List<ResourceRecommendation> findByFarmIdAndRecommendationCategory(String farmId, RecommendationCategory category);

    // Priority-based queries
    List<ResourceRecommendation> findByPriorityLevel(PriorityLevel priorityLevel);

    List<ResourceRecommendation> findByFarmIdAndPriorityLevel(String farmId, PriorityLevel priorityLevel);

    @Query("SELECT r FROM ResourceRecommendation r WHERE r.priorityLevel IN ('HIGH', 'URGENT')")
    List<ResourceRecommendation> findHighPriorityRecommendations();

    @Query("SELECT r FROM ResourceRecommendation r WHERE r.farmId = :farmId AND r.priorityLevel IN ('HIGH', 'URGENT')")
    List<ResourceRecommendation> findHighPriorityRecommendationsByFarmId(@Param("farmId") String farmId);

    // Date-based queries
    List<ResourceRecommendation> findByGeneratedDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<ResourceRecommendation> findByValidUntilBefore(LocalDate date);

    List<ResourceRecommendation> findByValidUntilBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM ResourceRecommendation r WHERE r.validUntil <= :date AND r.status = 'ACTIVE'")
    List<ResourceRecommendation> findExpiredActiveRecommendations(@Param("date") LocalDate date);

    @Query("SELECT r FROM ResourceRecommendation r WHERE r.validUntil <= :date AND r.status = 'ACTIVE' AND r.farmId = :farmId")
    List<ResourceRecommendation> findExpiredActiveRecommendationsByFarmId(@Param("farmId") String farmId, @Param("date") LocalDate date);

    @Query("SELECT r FROM ResourceRecommendation r WHERE r.validUntil BETWEEN :startDate AND :endDate AND r.status = 'ACTIVE'")
    List<ResourceRecommendation> findExpiringSoonRecommendations(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Implementation queries
    List<ResourceRecommendation> findByImplementationDateBetween(LocalDate startDate, LocalDate endDate);

    List<ResourceRecommendation> findByImplementationDifficulty(ImplementationDifficulty difficulty);

    @Query("SELECT r FROM ResourceRecommendation r WHERE r.implementationDifficulty IN ('EASY', 'MODERATE')")
    List<ResourceRecommendation> findEasyToImplementRecommendations();

    // Confidence and effectiveness queries
    @Query("SELECT r FROM ResourceRecommendation r WHERE r.confidenceScore >= :minScore")
    List<ResourceRecommendation> findByConfidenceScoreGreaterThanEqual(@Param("minScore") BigDecimal minScore);

    @Query("SELECT r FROM ResourceRecommendation r WHERE r.effectivenessRating >= :minRating")
    List<ResourceRecommendation> findByEffectivenessRatingGreaterThanEqual(@Param("minRating") Integer minRating);

    // Cost-based queries
    @Query("SELECT r FROM ResourceRecommendation r WHERE r.estimatedCost BETWEEN :minCost AND :maxCost")
    List<ResourceRecommendation> findByEstimatedCostBetween(@Param("minCost") BigDecimal minCost, @Param("maxCost") BigDecimal maxCost);

    @Query("SELECT r FROM ResourceRecommendation r WHERE r.expectedRoi >= :minRoi")
    List<ResourceRecommendation> findByExpectedRoiGreaterThanEqual(@Param("minRoi") BigDecimal minRoi);

    // Follow-up queries
    @Query("SELECT r FROM ResourceRecommendation r WHERE r.followUpRequired = true AND r.followUpDate <= :date")
    List<ResourceRecommendation> findFollowUpDue(@Param("date") LocalDate date);

    @Query("SELECT r FROM ResourceRecommendation r WHERE r.followUpRequired = true AND r.followUpDate <= :date AND r.farmId = :farmId")
    List<ResourceRecommendation> findFollowUpDueByFarmId(@Param("farmId") String farmId, @Param("date") LocalDate date);

    // Review queries
    List<ResourceRecommendation> findByReviewedByIsNull();

    List<ResourceRecommendation> findByReviewedByIsNotNull();

    List<ResourceRecommendation> findByReviewedBy(String reviewedBy);

    // Complex queries
    @Query("SELECT r FROM ResourceRecommendation r WHERE r.farmId = :farmId " +
            "AND r.status = :status " +
            "AND r.resourceType = :resourceType " +
            "ORDER BY r.priorityLevel DESC, r.generatedDate DESC")
    List<ResourceRecommendation> findByFarmIdAndStatusAndResourceTypeOrderByPriorityAndDate(
            @Param("farmId") String farmId,
            @Param("status") RecommendationStatus status,
            @Param("resourceType") ResourceType resourceType);

    @Query("SELECT r FROM ResourceRecommendation r WHERE r.farmId = :farmId " +
            "AND r.status = 'ACTIVE' " +
            "AND r.validUntil > CURRENT_DATE " +
            "ORDER BY r.priorityLevel DESC, r.confidenceScore DESC")
    List<ResourceRecommendation> findActiveValidRecommendationsByFarmId(@Param("farmId") String farmId);

    @Query("SELECT r FROM ResourceRecommendation r WHERE r.timingStartDate <= :currentDate " +
            "AND r.timingEndDate >= :currentDate " +
            "AND r.status = 'ACTIVE' " +
            "AND r.farmId = :farmId")
    List<ResourceRecommendation> findCurrentTimingWindowRecommendations(
            @Param("farmId") String farmId,
            @Param("currentDate") LocalDate currentDate);

    // Statistics queries
    @Query("SELECT COUNT(r) FROM ResourceRecommendation r WHERE r.farmId = :farmId AND r.status = :status")
    Long countByFarmIdAndStatus(@Param("farmId") String farmId, @Param("status") RecommendationStatus status);

    @Query("SELECT r.resourceType, COUNT(r) FROM ResourceRecommendation r WHERE r.farmId = :farmId GROUP BY r.resourceType")
    List<Object[]> countByResourceTypeAndFarmId(@Param("farmId") String farmId);

    @Query("SELECT r.priorityLevel, COUNT(r) FROM ResourceRecommendation r WHERE r.farmId = :farmId GROUP BY r.priorityLevel")
    List<Object[]> countByPriorityLevelAndFarmId(@Param("farmId") String farmId);

    @Query("SELECT AVG(r.confidenceScore) FROM ResourceRecommendation r WHERE r.farmId = :farmId AND r.status = :status")
    BigDecimal getAverageConfidenceScoreByFarmIdAndStatus(@Param("farmId") String farmId, @Param("status") RecommendationStatus status);

    @Query("SELECT AVG(r.effectivenessRating) FROM ResourceRecommendation r WHERE r.farmId = :farmId AND r.effectivenessRating IS NOT NULL")
    Double getAverageEffectivenessRatingByFarmId(@Param("farmId") String farmId);

    @Query("SELECT SUM(r.estimatedCost) FROM ResourceRecommendation r WHERE r.farmId = :farmId AND r.status = 'ACTIVE'")
    BigDecimal getTotalEstimatedCostByFarmId(@Param("farmId") String farmId);

    @Query("SELECT SUM(r.actualCost) FROM ResourceRecommendation r WHERE r.farmId = :farmId AND r.actualCost IS NOT NULL")
    BigDecimal getTotalActualCostByFarmId(@Param("farmId") String farmId);

    // Search queries
    @Query("SELECT r FROM ResourceRecommendation r WHERE " +
            "(LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(r.recommendedAction) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND r.farmId = :farmId")
    List<ResourceRecommendation> searchByTitleDescriptionOrActionAndFarmId(
            @Param("searchTerm") String searchTerm,
            @Param("farmId") String farmId);

    @Query("SELECT r FROM ResourceRecommendation r WHERE " +
            "(LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(r.recommendedAction) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND r.farmId = :farmId")
    Page<ResourceRecommendation> searchByTitleDescriptionOrActionAndFarmId(
            @Param("searchTerm") String searchTerm,
            @Param("farmId") String farmId,
            Pageable pageable);

    // Custom finder methods
    @Query("SELECT r FROM ResourceRecommendation r WHERE r.farmId = :farmId " +
            "AND (:resourceType IS NULL OR r.resourceType = :resourceType) " +
            "AND (:category IS NULL OR r.recommendationCategory = :category) " +
            "AND (:priority IS NULL OR r.priorityLevel = :priority) " +
            "AND (:status IS NULL OR r.status = :status) " +
            "ORDER BY r.priorityLevel DESC, r.generatedDate DESC")
    Page<ResourceRecommendation> findRecommendationsWithFilters(
            @Param("farmId") String farmId,
            @Param("resourceType") ResourceType resourceType,
            @Param("category") RecommendationCategory category,
            @Param("priority") PriorityLevel priority,
            @Param("status") RecommendationStatus status,
            Pageable pageable);

    @Query("SELECT r FROM ResourceRecommendation r WHERE r.farmId = :farmId " +
            "AND r.status = 'ACTIVE' " +
            "AND (r.validUntil IS NULL OR r.validUntil > CURRENT_DATE) " +
            "AND (:minConfidence IS NULL OR r.confidenceScore >= :minConfidence) " +
            "AND (:maxCost IS NULL OR r.estimatedCost <= :maxCost) " +
            "ORDER BY r.priorityLevel DESC, r.confidenceScore DESC")
    List<ResourceRecommendation> findOptimalRecommendations(
            @Param("farmId") String farmId,
            @Param("minConfidence") BigDecimal minConfidence,
            @Param("maxCost") BigDecimal maxCost);

    // Maintenance queries
    @Query("SELECT r FROM ResourceRecommendation r WHERE r.status = 'ACTIVE' " +
            "AND r.validUntil < CURRENT_DATE " +
            "ORDER BY r.validUntil ASC")
    List<ResourceRecommendation> findRecommendationsToExpire();

    @Query("UPDATE ResourceRecommendation r SET r.status = 'EXPIRED' " +
            "WHERE r.status = 'ACTIVE' AND r.validUntil < CURRENT_DATE")
    int markExpiredRecommendations();

    // AI model version queries
    List<ResourceRecommendation> findByAiModelVersion(String aiModelVersion);

    @Query("SELECT DISTINCT r.aiModelVersion FROM ResourceRecommendation r ORDER BY r.aiModelVersion")
    List<String> findDistinctAiModelVersions();

    // Sustainability queries
    @Query("SELECT r FROM ResourceRecommendation r WHERE r.sustainabilityScore >= :minScore")
    List<ResourceRecommendation> findBySustainabilityScoreGreaterThanEqual(@Param("minScore") Integer minScore);

    @Query("SELECT r FROM ResourceRecommendation r WHERE r.farmId = :farmId " +
            "AND r.sustainabilityScore >= :minScore " +
            "AND r.status = 'ACTIVE' " +
            "ORDER BY r.sustainabilityScore DESC, r.priorityLevel DESC")
    List<ResourceRecommendation> findSustainableRecommendationsByFarmId(
            @Param("farmId") String farmId,
            @Param("minScore") Integer minScore);

    // Recent recommendations
    @Query("SELECT r FROM ResourceRecommendation r WHERE r.farmId = :farmId " +
            "AND r.generatedDate >= :since " +
            "ORDER BY r.generatedDate DESC")
    List<ResourceRecommendation> findRecentRecommendationsByFarmId(
            @Param("farmId") String farmId,
            @Param("since") LocalDateTime since);

    // Count methods
    long countByFarmId(String farmId);

    long countByFarmIdAndResourceType(String farmId, ResourceType resourceType);

    long countByFarmIdAndRecommendationCategory(String farmId, RecommendationCategory category);

    long countByFarmIdAndPriorityLevel(String farmId, PriorityLevel priorityLevel);

    @Query("SELECT COUNT(r) FROM ResourceRecommendation r WHERE r.farmId = :farmId " +
            "AND r.status = 'ACTIVE' " +
            "AND (r.validUntil IS NULL OR r.validUntil > CURRENT_DATE)")
    long countActiveValidRecommendationsByFarmId(@Param("farmId") String farmId);

    @Query("SELECT COUNT(r) FROM ResourceRecommendation r WHERE r.farmId = :farmId " +
            "AND r.followUpRequired = true " +
            "AND r.followUpDate <= :date")
    long countFollowUpDueByFarmId(@Param("farmId") String farmId, @Param("date") LocalDate date);

    // Existence checks
    boolean existsByRecommendationCode(String recommendationCode);

    boolean existsByFarmIdAndResourceTypeAndStatusAndValidUntilAfter(
            String farmId, ResourceType resourceType, RecommendationStatus status, LocalDate date);

    // Custom delete queries
    @Query("DELETE FROM ResourceRecommendation r WHERE r.status = 'EXPIRED' " +
            "AND r.validUntil < :cutoffDate")
    int deleteExpiredRecommendationsOlderThan(@Param("cutoffDate") LocalDate cutoffDate);

    @Query("DELETE FROM ResourceRecommendation r WHERE r.status = 'REJECTED' " +
            "AND r.updatedAt < :cutoffDate")
    int deleteOldRejectedRecommendations(@Param("cutoffDate") LocalDateTime cutoffDate);
}