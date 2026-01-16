package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.AIRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AIRecommendationRepository extends JpaRepository<AIRecommendation, String> {

    // Find by farmer
    List<AIRecommendation> findByFarmerIdOrderByCreatedAtDesc(String farmerId);

    // Find by farmer and active status
    List<AIRecommendation> findByFarmerIdAndIsActiveOrderByCreatedAtDesc(String farmerId, Boolean isActive);

    // Find by farmer and read status
    List<AIRecommendation> findByFarmerIdAndIsReadOrderByCreatedAtDesc(String farmerId, Boolean isRead);

    // Find by farm
    List<AIRecommendation> findByFarmIdOrderByCreatedAtDesc(String farmId);

    // Find by crop production
    List<AIRecommendation> findByCropProductionIdOrderByCreatedAtDesc(String cropProductionId);

    // Find by recommendation type
    List<AIRecommendation> findByFarmerIdAndRecommendationTypeOrderByCreatedAtDesc(
            String farmerId,
            AIRecommendation.RecommendationType recommendationType
    );

    // Find by priority
    List<AIRecommendation> findByFarmerIdAndPriorityOrderByCreatedAtDesc(
            String farmerId,
            AIRecommendation.Priority priority
    );

    // Find unread recommendations
    List<AIRecommendation> findByFarmerIdAndIsReadFalseOrderByCreatedAtDesc(String farmerId);

    // Find urgent recommendations
    @Query("SELECT r FROM AIRecommendation r WHERE r.farmerId = :farmerId " +
            "AND r.priority IN ('URGENT', 'HIGH') " +
            "AND r.isRead = false " +
            "AND r.isActive = true " +
            "ORDER BY r.priority DESC, r.createdAt DESC")
    List<AIRecommendation> findUrgentRecommendations(@Param("farmerId") String farmerId);

    // Find valid recommendations (within validity period)
    @Query("SELECT r FROM AIRecommendation r WHERE r.farmerId = :farmerId " +
            "AND r.isActive = true " +
            "AND r.validFrom <= :now " +
            "AND (r.validUntil IS NULL OR r.validUntil >= :now) " +
            "ORDER BY r.priority DESC, r.createdAt DESC")
    List<AIRecommendation> findValidRecommendations(
            @Param("farmerId") String farmerId,
            @Param("now") LocalDateTime now
    );

    // Find implemented recommendations
    List<AIRecommendation> findByFarmerIdAndIsImplementedTrueOrderByImplementationDateDesc(String farmerId);

    // Count unread recommendations
    Long countByFarmerIdAndIsReadFalse(String farmerId);

    // Count urgent recommendations
    @Query("SELECT COUNT(r) FROM AIRecommendation r WHERE r.farmerId = :farmerId " +
            "AND r.priority IN ('URGENT', 'HIGH') " +
            "AND r.isRead = false " +
            "AND r.isActive = true")
    Long countUrgentRecommendations(@Param("farmerId") String farmerId);

    // Find recommendations by type and priority
    List<AIRecommendation> findByFarmerIdAndRecommendationTypeAndPriorityOrderByCreatedAtDesc(
            String farmerId,
            AIRecommendation.RecommendationType recommendationType,
            AIRecommendation.Priority priority
    );

    // Statistics: Average effectiveness rating
    @Query("SELECT AVG(r.effectivenessRating) FROM AIRecommendation r " +
            "WHERE r.farmerId = :farmerId AND r.effectivenessRating IS NOT NULL")
    Double getAverageEffectivenessRating(@Param("farmerId") String farmerId);

    // Statistics: Implementation rate
    @Query("SELECT (COUNT(CASE WHEN r.isImplemented = true THEN 1 END) * 100.0 / COUNT(*)) " +
            "FROM AIRecommendation r WHERE r.farmerId = :farmerId")
    Double getImplementationRate(@Param("farmerId") String farmerId);

    // Delete old inactive recommendations
    @Query("DELETE FROM AIRecommendation r WHERE r.isActive = false " +
            "AND r.updatedAt < :cutoffDate")
    void deleteOldInactiveRecommendations(@Param("cutoffDate") LocalDateTime cutoffDate);
}