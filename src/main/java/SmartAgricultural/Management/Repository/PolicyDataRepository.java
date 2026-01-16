package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.PolicyData;
import SmartAgricultural.Management.Model.PolicyData.PolicyType;
import SmartAgricultural.Management.Model.PolicyData.PolicyCategory;
import SmartAgricultural.Management.Model.PolicyData.PolicyStatus;
import SmartAgricultural.Management.Model.PolicyData.GeographicScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyDataRepository extends JpaRepository<PolicyData, String>, JpaSpecificationExecutor<PolicyData> {

    // Existing methods...
    Optional<PolicyData> findByPolicyCode(String policyCode);
    boolean existsByPolicyCode(String policyCode);
    List<PolicyData> findByPolicyNameContainingIgnoreCase(String policyName);
    List<PolicyData> findByPolicyType(PolicyType policyType);
    List<PolicyData> findByPolicyCategory(PolicyCategory policyCategory);
    List<PolicyData> findByStatus(PolicyStatus status);
    List<PolicyData> findByImplementingAgencyContainingIgnoreCase(String implementingAgency);
    List<PolicyData> findByMinistryResponsibleContainingIgnoreCase(String ministryResponsible);
    List<PolicyData> findByGeographicScope(GeographicScope geographicScope);
    List<PolicyData> findByCreatedBy(String createdBy);
    List<PolicyData> findByFundingSourceContainingIgnoreCase(String fundingSource);

    // ============ NOUVELLES MÉTHODES POUR LES STATISTICS ============

    // Count by status
    long countByStatus(PolicyStatus status);

    // Count by policy type
    long countByPolicyType(PolicyType policyType);

    // Count by policy category
    long countByPolicyCategory(PolicyCategory policyCategory);

    // Count by climate smart
    long countByClimateSmart(boolean climateSmart);

    // Count by youth focus
    long countByYouthFocus(boolean youthFocus);

    // ============ QUERIES EXISTANTES ============

    @Query("SELECT p FROM PolicyData p WHERE p.status = 'ACTIVE' AND " +
            "(p.effectiveDate IS NULL OR p.effectiveDate <= :currentDate) AND " +
            "(p.expiryDate IS NULL OR p.expiryDate > :currentDate)")
    List<PolicyData> findActivePolicies(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT p FROM PolicyData p WHERE p.status = 'ACTIVE' AND " +
            "p.expiryDate IS NOT NULL AND p.expiryDate <= :currentDate")
    List<PolicyData> findExpiredPolicies(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT p FROM PolicyData p WHERE p.status = 'ACTIVE' AND " +
            "p.expiryDate IS NOT NULL AND p.expiryDate BETWEEN :currentDate AND :futureDate")
    List<PolicyData> findPoliciesExpiringSoon(@Param("currentDate") LocalDate currentDate,
                                              @Param("futureDate") LocalDate futureDate);

    @Query("SELECT p FROM PolicyData p WHERE p.effectiveDate BETWEEN :startDate AND :endDate")
    List<PolicyData> findByEffectiveDateBetween(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM PolicyData p WHERE p.totalBudget BETWEEN :minBudget AND :maxBudget")
    List<PolicyData> findByBudgetRange(@Param("minBudget") BigDecimal minBudget,
                                       @Param("maxBudget") BigDecimal maxBudget);

    @Query("SELECT p FROM PolicyData p WHERE p.utilizationRate >= :minRate")
    List<PolicyData> findByUtilizationRateGreaterThanEqual(@Param("minRate") BigDecimal minRate);

    @Query("SELECT p FROM PolicyData p WHERE p.nextReviewDate IS NOT NULL AND " +
            "p.nextReviewDate <= :currentDate AND p.status = 'ACTIVE'")
    List<PolicyData> findPoliciesRequiringReview(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT p FROM PolicyData p WHERE p.climateSmart = true")
    List<PolicyData> findClimateSmartPolicies();

    @Query("SELECT p FROM PolicyData p WHERE p.youthFocus = true")
    List<PolicyData> findYouthFocusedPolicies();

    @Query("SELECT p FROM PolicyData p WHERE p.environmentalClearance = true")
    List<PolicyData> findPoliciesWithEnvironmentalClearance();

    @Query("SELECT p FROM PolicyData p WHERE p.createdDate BETWEEN :startDate AND :endDate")
    List<PolicyData> findByCreatedDateBetween(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p.status, COUNT(p) FROM PolicyData p GROUP BY p.status")
    List<Object[]> countPoliciesByStatus();

    @Query("SELECT p.policyType, COUNT(p) FROM PolicyData p GROUP BY p.policyType")
    List<Object[]> countPoliciesByType();

    @Query("SELECT p.policyCategory, COUNT(p) FROM PolicyData p GROUP BY p.policyCategory")
    List<Object[]> countPoliciesByCategory();

    @Query("SELECT SUM(p.totalBudget) FROM PolicyData p WHERE p.status = 'ACTIVE'")
    BigDecimal calculateTotalActiveBudget();

    @Query("SELECT AVG(p.utilizationRate) FROM PolicyData p WHERE p.utilizationRate IS NOT NULL")
    BigDecimal calculateAverageUtilizationRate();

    @Query("SELECT p FROM PolicyData p WHERE p.utilizationRate IS NOT NULL " +
            "ORDER BY p.utilizationRate DESC")
    List<PolicyData> findTopPerformingPolicies();

    @Query("SELECT p FROM PolicyData p WHERE p.utilizationRate IS NOT NULL AND " +
            "p.utilizationRate < :threshold")
    List<PolicyData> findLowPerformingPolicies(@Param("threshold") BigDecimal threshold);

    @Modifying
    @Transactional
    @Query("UPDATE PolicyData p SET p.status = :status, p.lastUpdated = :updateTime, " +
            "p.updatedBy = :updatedBy WHERE p.id = :id")
    int updatePolicyStatus(@Param("id") String id, @Param("status") PolicyStatus status,
                           @Param("updateTime") LocalDateTime updateTime, @Param("updatedBy") String updatedBy);

    @Modifying
    @Transactional
    @Query("UPDATE PolicyData p SET p.status = 'EXPIRED', p.lastUpdated = :updateTime " +
            "WHERE p.status = 'ACTIVE' AND p.expiryDate <= :currentDate")
    int updateExpiredPolicies(@Param("currentDate") LocalDate currentDate,
                              @Param("updateTime") LocalDateTime updateTime);

    @Query("SELECT p FROM PolicyData p WHERE " +
            "LOWER(p.policyName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.objectives) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<PolicyData> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM PolicyData p WHERE p.parliamentaryApproval = true")
    List<PolicyData> findPoliciesWithParliamentaryApproval();

    @Query("SELECT p FROM PolicyData p WHERE p.publicConsultation = true")
    List<PolicyData> findPoliciesWithPublicConsultation();

    @Query("SELECT p FROM PolicyData p WHERE p.beneficiariesCount BETWEEN :min AND :max")
    List<PolicyData> findByBeneficiariesCountBetween(@Param("min") Integer min, @Param("max") Integer max);

    @Query("SELECT p FROM PolicyData p WHERE p.farmersBenefited > 0")
    List<PolicyData> findPoliciesBenefitingFarmers();

    @Query("SELECT p FROM PolicyData p WHERE p.cooperativesBenefited > 0")
    List<PolicyData> findPoliciesBenefitingCooperatives();

    @Query("SELECT p FROM PolicyData p WHERE " +
            "(:policyType IS NULL OR p.policyType = :policyType) AND " +
            "(:policyCategory IS NULL OR p.policyCategory = :policyCategory) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:geographicScope IS NULL OR p.geographicScope = :geographicScope) AND " +
            "(:implementingAgency IS NULL OR LOWER(p.implementingAgency) LIKE LOWER(CONCAT('%', :implementingAgency, '%'))) AND " +
            "(:minBudget IS NULL OR p.totalBudget >= :minBudget) AND " +
            "(:maxBudget IS NULL OR p.totalBudget <= :maxBudget)")
    List<PolicyData> findByMultipleCriteria(
            @Param("policyType") PolicyType policyType,
            @Param("policyCategory") PolicyCategory policyCategory,
            @Param("status") PolicyStatus status,
            @Param("geographicScope") GeographicScope geographicScope,
            @Param("implementingAgency") String implementingAgency,
            @Param("minBudget") BigDecimal minBudget,
            @Param("maxBudget") BigDecimal maxBudget
    );

    @Query("SELECT " +
            "COUNT(p) as totalPolicies, " +
            "SUM(CASE WHEN p.status = 'ACTIVE' THEN 1 ELSE 0 END) as activePolicies, " +
            "SUM(CASE WHEN p.status = 'DRAFT' THEN 1 ELSE 0 END) as draftPolicies, " +
            "SUM(CASE WHEN p.status = 'EXPIRED' THEN 1 ELSE 0 END) as expiredPolicies, " +
            "COALESCE(SUM(p.totalBudget), 0) as totalBudget, " +
            "COALESCE(AVG(p.utilizationRate), 0) as avgUtilizationRate " +
            "FROM PolicyData p")
    Object[] getPolicyStatistics();

    @Query("SELECT p FROM PolicyData p WHERE p.createdDate >= :date ORDER BY p.createdDate DESC")
    List<PolicyData> findRecentPolicies(@Param("date") LocalDateTime date);

    @Query("SELECT p FROM PolicyData p ORDER BY p.lastUpdated DESC")
    List<PolicyData> findRecentlyUpdatedPolicies();
}