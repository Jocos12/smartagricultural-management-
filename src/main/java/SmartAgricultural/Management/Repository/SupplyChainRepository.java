package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.SupplyChain;
import SmartAgricultural.Management.Model.SupplyChain.Stage;
import SmartAgricultural.Management.Model.SupplyChain.QualityStatus;
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
public interface SupplyChainRepository extends JpaRepository<SupplyChain, String> {

    // Basic finders
    Optional<SupplyChain> findByTrackingCode(String trackingCode);

    Optional<SupplyChain> findByTransactionId(String transactionId);

    List<SupplyChain> findByCropProductionId(String cropProductionId);

    Page<SupplyChain> findByCropProductionId(String cropProductionId, Pageable pageable);

    // Stage-based queries
    List<SupplyChain> findByStage(Stage stage);

    Page<SupplyChain> findByStage(Stage stage, Pageable pageable);

    List<SupplyChain> findByCropProductionIdAndStage(String cropProductionId, Stage stage);

    List<SupplyChain> findByStageOrderBetween(Integer minOrder, Integer maxOrder);

    @Query("SELECT s FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId ORDER BY s.stageOrder ASC")
    List<SupplyChain> findByCropProductionIdOrderByStageOrder(@Param("cropProductionId") String cropProductionId);
    // Ajouter juste après la méthode existante findByCropProductionIdOrderByStageOrder
    @Query("SELECT s FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId ORDER BY s.stageOrder ASC")
    List<SupplyChain> findByCropProductionIdOrderByStageOrderAsc(@Param("cropProductionId") String cropProductionId);
    @Query("SELECT s FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId AND s.stageOrder = :stageOrder")
    Optional<SupplyChain> findByCropProductionIdAndStageOrder(
            @Param("cropProductionId") String cropProductionId,
            @Param("stageOrder") Integer stageOrder);

    // Date-based queries
    List<SupplyChain> findByStageStartDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<SupplyChain> findByStageEndDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<SupplyChain> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT s FROM SupplyChain s WHERE s.stageStartDate <= :date AND (s.stageEndDate IS NULL OR s.stageEndDate >= :date)")
    List<SupplyChain> findActiveStagesAtDate(@Param("date") LocalDateTime date);

    @Query("SELECT s FROM SupplyChain s WHERE s.stageEndDate IS NULL")
    List<SupplyChain> findIncompleteStages();

    @Query("SELECT s FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId AND s.stageEndDate IS NULL")
    List<SupplyChain> findIncompleteStageByCropProduction(@Param("cropProductionId") String cropProductionId);

    @Query("SELECT s FROM SupplyChain s WHERE s.stageEndDate IS NOT NULL")
    List<SupplyChain> findCompletedStages();

    // Quality-based queries
    List<SupplyChain> findByQualityStatus(QualityStatus qualityStatus);

    Page<SupplyChain> findByQualityStatus(QualityStatus qualityStatus, Pageable pageable);

    List<SupplyChain> findByCropProductionIdAndQualityStatus(String cropProductionId, QualityStatus qualityStatus);

    @Query("SELECT s FROM SupplyChain s WHERE s.qualityStatus IN ('POOR', 'REJECTED')")
    List<SupplyChain> findQualityIssues();

    @Query("SELECT s FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId AND s.qualityStatus IN ('POOR', 'REJECTED')")
    List<SupplyChain> findQualityIssuesByCropProduction(@Param("cropProductionId") String cropProductionId);

    @Query("SELECT s FROM SupplyChain s WHERE s.qualityStatus IN ('EXCELLENT', 'GOOD')")
    List<SupplyChain> findGoodQualityStages();

    // Loss-based queries
    @Query("SELECT s FROM SupplyChain s WHERE s.lossQuantity > 0")
    List<SupplyChain> findStagesWithLosses();

    @Query("SELECT s FROM SupplyChain s WHERE s.lossPercentage > :threshold")
    List<SupplyChain> findStagesWithHighLosses(@Param("threshold") BigDecimal threshold);

    @Query("SELECT s FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId AND s.lossQuantity > 0")
    List<SupplyChain> findStagesWithLossesByCropProduction(@Param("cropProductionId") String cropProductionId);

    @Query("SELECT s FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId AND s.lossPercentage > :threshold")
    List<SupplyChain> findStagesWithHighLossesByCropProduction(
            @Param("cropProductionId") String cropProductionId,
            @Param("threshold") BigDecimal threshold);

    // Location-based queries
    List<SupplyChain> findByLocation(String location);

    List<SupplyChain> findByFacilityName(String facilityName);

    @Query("SELECT s FROM SupplyChain s WHERE LOWER(s.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<SupplyChain> findByLocationContaining(@Param("location") String location);

    @Query("SELECT s FROM SupplyChain s WHERE LOWER(s.facilityName) LIKE LOWER(CONCAT('%', :facilityName, '%'))")
    List<SupplyChain> findByFacilityNameContaining(@Param("facilityName") String facilityName);

    // Responsible party queries
    List<SupplyChain> findByResponsibleParty(String responsibleParty);

    @Query("SELECT s FROM SupplyChain s WHERE LOWER(s.responsibleParty) LIKE LOWER(CONCAT('%', :responsibleParty, '%'))")
    List<SupplyChain> findByResponsiblePartyContaining(@Param("responsibleParty") String responsibleParty);

    // Cost-based queries
    @Query("SELECT s FROM SupplyChain s WHERE s.costIncurred BETWEEN :minCost AND :maxCost")
    List<SupplyChain> findByCostIncurredBetween(@Param("minCost") BigDecimal minCost, @Param("maxCost") BigDecimal maxCost);

    @Query("SELECT s FROM SupplyChain s WHERE s.costIncurred > :threshold")
    List<SupplyChain> findHighCostStages(@Param("threshold") BigDecimal threshold);

    @Query("SELECT s FROM SupplyChain s WHERE s.costIncurred IS NULL OR s.costIncurred = 0")
    List<SupplyChain> findStagesWithNoCost();

    // Quantity-based queries
    @Query("SELECT s FROM SupplyChain s WHERE s.quantityIn BETWEEN :minQuantity AND :maxQuantity")
    List<SupplyChain> findByQuantityInBetween(@Param("minQuantity") BigDecimal minQuantity, @Param("maxQuantity") BigDecimal maxQuantity);

    @Query("SELECT s FROM SupplyChain s WHERE s.quantityOut BETWEEN :minQuantity AND :maxQuantity")
    List<SupplyChain> findByQuantityOutBetween(@Param("minQuantity") BigDecimal minQuantity, @Param("maxQuantity") BigDecimal maxQuantity);

    // Insurance and compliance queries
    @Query("SELECT s FROM SupplyChain s WHERE s.insuranceCoverage = true")
    List<SupplyChain> findInsuredStages();

    @Query("SELECT s FROM SupplyChain s WHERE s.insuranceCoverage = false OR s.insuranceCoverage IS NULL")
    List<SupplyChain> findUninsuredStages();

    @Query("SELECT s FROM SupplyChain s WHERE s.complianceCertificates IS NOT NULL AND s.complianceCertificates != ''")
    List<SupplyChain> findStagesWithCertificates();

    @Query("SELECT s FROM SupplyChain s WHERE s.complianceCertificates IS NULL OR s.complianceCertificates = ''")
    List<SupplyChain> findStagesWithoutCertificates();

    // Performance analysis queries
    @Query("SELECT s FROM SupplyChain s WHERE (s.quantityOut / s.quantityIn) < :efficiencyThreshold")
    List<SupplyChain> findLowEfficiencyStages(@Param("efficiencyThreshold") BigDecimal efficiencyThreshold);

    @Query("SELECT s FROM SupplyChain s WHERE (s.quantityOut / s.quantityIn) >= :efficiencyThreshold")
    List<SupplyChain> findHighEfficiencyStages(@Param("efficiencyThreshold") BigDecimal efficiencyThreshold);

    // Duration analysis queries
    @Query("SELECT s FROM SupplyChain s WHERE s.stageEndDate IS NOT NULL AND " +
            "TIMESTAMPDIFF(HOUR, s.stageStartDate, s.stageEndDate) > :hoursThreshold")
    List<SupplyChain> findLongDurationStages(@Param("hoursThreshold") Long hoursThreshold);

    @Query("SELECT s FROM SupplyChain s WHERE s.stageEndDate IS NOT NULL AND " +
            "TIMESTAMPDIFF(HOUR, s.stageStartDate, s.stageEndDate) <= :hoursThreshold")
    List<SupplyChain> findShortDurationStages(@Param("hoursThreshold") Long hoursThreshold);

    // Statistics queries
    @Query("SELECT s.stage, COUNT(s) FROM SupplyChain s GROUP BY s.stage ORDER BY COUNT(s) DESC")
    List<Object[]> countByStage();

    @Query("SELECT s.stage, COUNT(s) FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId GROUP BY s.stage")
    List<Object[]> countByStageAndCropProduction(@Param("cropProductionId") String cropProductionId);

    @Query("SELECT s.qualityStatus, COUNT(s) FROM SupplyChain s GROUP BY s.qualityStatus ORDER BY COUNT(s) DESC")
    List<Object[]> countByQualityStatus();

    @Query("SELECT s.responsibleParty, COUNT(s) FROM SupplyChain s GROUP BY s.responsibleParty ORDER BY COUNT(s) DESC")
    List<Object[]> countByResponsibleParty();

    @Query("SELECT s.location, COUNT(s) FROM SupplyChain s GROUP BY s.location ORDER BY COUNT(s) DESC")
    List<Object[]> countByLocation();

    // Average calculations
    @Query("SELECT AVG(s.lossPercentage) FROM SupplyChain s WHERE s.lossPercentage IS NOT NULL")
    BigDecimal getAverageLossPercentage();

    @Query("SELECT AVG(s.lossPercentage) FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId AND s.lossPercentage IS NOT NULL")
    BigDecimal getAverageLossPercentageByCropProduction(@Param("cropProductionId") String cropProductionId);

    @Query("SELECT AVG(s.costIncurred) FROM SupplyChain s WHERE s.costIncurred IS NOT NULL")
    BigDecimal getAverageCostIncurred();

    @Query("SELECT AVG(s.costIncurred) FROM SupplyChain s WHERE s.stage = :stage AND s.costIncurred IS NOT NULL")
    BigDecimal getAverageCostByStage(@Param("stage") Stage stage);

    @Query("SELECT SUM(s.quantityIn) FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId")
    BigDecimal getTotalQuantityInByCropProduction(@Param("cropProductionId") String cropProductionId);

    @Query("SELECT SUM(s.quantityOut) FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId")
    BigDecimal getTotalQuantityOutByCropProduction(@Param("cropProductionId") String cropProductionId);

    @Query("SELECT SUM(s.lossQuantity) FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId AND s.lossQuantity IS NOT NULL")
    BigDecimal getTotalLossQuantityByCropProduction(@Param("cropProductionId") String cropProductionId);

    @Query("SELECT SUM(s.costIncurred) FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId AND s.costIncurred IS NOT NULL")
    BigDecimal getTotalCostByCropProduction(@Param("cropProductionId") String cropProductionId);

    // Chain analysis queries
    @Query("SELECT s FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId AND s.stageOrder = 1")
    Optional<SupplyChain> findFirstStage(@Param("cropProductionId") String cropProductionId);

    @Query("SELECT s FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId ORDER BY s.stageOrder DESC LIMIT 1")
    Optional<SupplyChain> findLastStage(@Param("cropProductionId") String cropProductionId);

    @Query("SELECT s FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId AND s.stage IN ('PROCESSING', 'PACKAGING')")
    List<SupplyChain> findProcessingStages(@Param("cropProductionId") String cropProductionId);

    @Query("SELECT s FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId AND s.stage IN ('TRANSPORT', 'DISTRIBUTION')")
    List<SupplyChain> findLogisticsStages(@Param("cropProductionId") String cropProductionId);

    // Search queries
    @Query("SELECT s FROM SupplyChain s WHERE " +
            "LOWER(s.trackingCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.location) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.facilityName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.responsibleParty) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<SupplyChain> searchByTerm(@Param("searchTerm") String searchTerm);

    @Query("SELECT s FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId AND (" +
            "LOWER(s.trackingCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.location) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.facilityName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.responsibleParty) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<SupplyChain> searchByTermAndCropProduction(
            @Param("cropProductionId") String cropProductionId,
            @Param("searchTerm") String searchTerm);

    @Query("SELECT s FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId AND (" +
            "LOWER(s.trackingCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.location) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.facilityName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.responsibleParty) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<SupplyChain> searchByTermAndCropProduction(
            @Param("cropProductionId") String cropProductionId,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    // Complex filtering
    @Query("SELECT s FROM SupplyChain s WHERE " +
            "(:stage IS NULL OR s.stage = :stage) AND " +
            "(:qualityStatus IS NULL OR s.qualityStatus = :qualityStatus) AND " +
            "(:location IS NULL OR LOWER(s.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:responsibleParty IS NULL OR LOWER(s.responsibleParty) LIKE LOWER(CONCAT('%', :responsibleParty, '%'))) AND " +
            "(:minCost IS NULL OR s.costIncurred >= :minCost) AND " +
            "(:maxCost IS NULL OR s.costIncurred <= :maxCost)")
    Page<SupplyChain> findWithFilters(
            @Param("stage") Stage stage,
            @Param("qualityStatus") QualityStatus qualityStatus,
            @Param("location") String location,
            @Param("responsibleParty") String responsibleParty,
            @Param("minCost") BigDecimal minCost,
            @Param("maxCost") BigDecimal maxCost,
            Pageable pageable);

    @Query("SELECT s FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId AND " +
            "(:stage IS NULL OR s.stage = :stage) AND " +
            "(:qualityStatus IS NULL OR s.qualityStatus = :qualityStatus) AND " +
            "(:location IS NULL OR LOWER(s.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:responsibleParty IS NULL OR LOWER(s.responsibleParty) LIKE LOWER(CONCAT('%', :responsibleParty, '%'))) AND " +
            "(:minCost IS NULL OR s.costIncurred >= :minCost) AND " +
            "(:maxCost IS NULL OR s.costIncurred <= :maxCost)")
    Page<SupplyChain> findWithFiltersByCropProduction(
            @Param("cropProductionId") String cropProductionId,
            @Param("stage") Stage stage,
            @Param("qualityStatus") QualityStatus qualityStatus,
            @Param("location") String location,
            @Param("responsibleParty") String responsibleParty,
            @Param("minCost") BigDecimal minCost,
            @Param("maxCost") BigDecimal maxCost,
            Pageable pageable);

    // Count methods
    long countByCropProductionId(String cropProductionId);

    long countByStage(Stage stage);

    long countByQualityStatus(QualityStatus qualityStatus);

    @Query("SELECT COUNT(s) FROM SupplyChain s WHERE s.stageEndDate IS NULL")
    long countIncompleteStages();

    @Query("SELECT COUNT(s) FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId AND s.stageEndDate IS NULL")
    long countIncompleteStageByCropProduction(@Param("cropProductionId") String cropProductionId);

    @Query("SELECT COUNT(s) FROM SupplyChain s WHERE s.lossQuantity > 0")
    long countStagesWithLosses();

    @Query("SELECT COUNT(s) FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId AND s.lossQuantity > 0")
    long countStagesWithLossesByCropProduction(@Param("cropProductionId") String cropProductionId);

    @Query("SELECT COUNT(s) FROM SupplyChain s WHERE s.qualityStatus IN ('POOR', 'REJECTED')")
    long countQualityIssues();

    @Query("SELECT COUNT(s) FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId AND s.qualityStatus IN ('POOR', 'REJECTED')")
    long countQualityIssuesByCropProduction(@Param("cropProductionId") String cropProductionId);

    // Existence checks
    boolean existsByTrackingCode(String trackingCode);

    boolean existsByTransactionId(String transactionId);

    boolean existsByCropProductionIdAndStage(String cropProductionId, Stage stage);

    boolean existsByCropProductionIdAndStageOrder(String cropProductionId, Integer stageOrder);

    // Recent data queries
    @Query("SELECT s FROM SupplyChain s WHERE s.createdAt >= :since ORDER BY s.createdAt DESC")
    List<SupplyChain> findRecentEntries(@Param("since") LocalDateTime since);

    @Query("SELECT s FROM SupplyChain s WHERE s.cropProductionId = :cropProductionId AND s.createdAt >= :since ORDER BY s.createdAt DESC")
    List<SupplyChain> findRecentEntriesByCropProduction(
            @Param("cropProductionId") String cropProductionId,
            @Param("since") LocalDateTime since);

    // Maintenance queries
    @Query("DELETE FROM SupplyChain s WHERE s.createdAt < :cutoffDate AND s.stageEndDate IS NOT NULL")
    int deleteOldCompletedEntries(@Param("cutoffDate") LocalDateTime cutoffDate);
}