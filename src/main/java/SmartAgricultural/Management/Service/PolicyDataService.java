package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.PolicyData;
import SmartAgricultural.Management.Model.PolicyData.PolicyType;
import SmartAgricultural.Management.Model.PolicyData.PolicyCategory;
import SmartAgricultural.Management.Model.PolicyData.PolicyStatus;
import SmartAgricultural.Management.Model.PolicyData.GeographicScope;
import SmartAgricultural.Management.Model.PolicyData.PolicyEffectiveness;
import SmartAgricultural.Management.Repository.PolicyDataRepository;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.exception.DuplicateResourceException;
import SmartAgricultural.Management.exception.InvalidOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PolicyDataService {

    @Autowired
    private PolicyDataRepository policyDataRepository;
    private static final Logger logger = LoggerFactory.getLogger(PolicyDataService.class);

    // CRUD Operations

    @Transactional
    public PolicyData createPolicy(PolicyData policyData) {
        validatePolicyData(policyData);

        if (StringUtils.hasText(policyData.getPolicyCode()) &&
                policyDataRepository.existsByPolicyCode(policyData.getPolicyCode())) {
            throw new DuplicateResourceException("Policy with code " + policyData.getPolicyCode() + " already exists");
        }

        policyData.setCreatedDate(LocalDateTime.now());
        policyData.setLastUpdated(LocalDateTime.now());

        return policyDataRepository.save(policyData);
    }

    @Transactional
    public PolicyData updatePolicy(String id, PolicyData updatedPolicyData) {
        PolicyData existingPolicy = getPolicyById(id);

        // Preserve system fields
        updatedPolicyData.setId(existingPolicy.getId());
        updatedPolicyData.setCreatedDate(existingPolicy.getCreatedDate());
        updatedPolicyData.setCreatedBy(existingPolicy.getCreatedBy());
        updatedPolicyData.setLastUpdated(LocalDateTime.now());

        // Check for policy code conflicts (excluding current policy)
        if (StringUtils.hasText(updatedPolicyData.getPolicyCode()) &&
                !updatedPolicyData.getPolicyCode().equals(existingPolicy.getPolicyCode()) &&
                policyDataRepository.existsByPolicyCode(updatedPolicyData.getPolicyCode())) {
            throw new DuplicateResourceException("Policy with code " + updatedPolicyData.getPolicyCode() + " already exists");
        }

        validatePolicyData(updatedPolicyData);

        return policyDataRepository.save(updatedPolicyData);
    }

    @Transactional(readOnly = true)
    public PolicyData getPolicyById(String id) {
        return policyDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public PolicyData getPolicyByCode(String policyCode) {
        return policyDataRepository.findByPolicyCode(policyCode)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with code: " + policyCode));
    }

    @Transactional(readOnly = true)
    public List<PolicyData> getAllPolicies() {
        return policyDataRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<PolicyData> getAllPolicies(Pageable pageable) {
        return policyDataRepository.findAll(pageable);
    }

    @Transactional
    public void deletePolicy(String id) {
        PolicyData policy = getPolicyById(id);

        // Check if policy can be deleted (business rules)
        if (policy.getStatus() == PolicyStatus.ACTIVE) {
            throw new InvalidOperationException("Cannot delete an active policy. Please suspend or expire it first.");
        }

        policyDataRepository.delete(policy);
    }

    // Status Management

    @Transactional
    public PolicyData updatePolicyStatus(String id, PolicyStatus newStatus, String updatedBy) {
        PolicyData policy = getPolicyById(id);

        validateStatusTransition(policy.getStatus(), newStatus);

        policyDataRepository.updatePolicyStatus(id, newStatus, LocalDateTime.now(), updatedBy);

        policy.setStatus(newStatus);
        return policy;
    }

    @Transactional
    public int updateExpiredPolicies() {
        return policyDataRepository.updateExpiredPolicies(LocalDate.now(), LocalDateTime.now());
    }

    // Search and Filter Operations

    @Transactional(readOnly = true)
    public List<PolicyData> searchPolicies(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return getAllPolicies();
        }
        return policyDataRepository.searchByKeyword(keyword);
    }

    @Transactional(readOnly = true)
    public List<PolicyData> getPoliciesByType(PolicyType policyType) {
        return policyDataRepository.findByPolicyType(policyType);
    }

    @Transactional(readOnly = true)
    public List<PolicyData> getPoliciesByCategory(PolicyCategory policyCategory) {
        return policyDataRepository.findByPolicyCategory(policyCategory);
    }

    @Transactional(readOnly = true)
    public List<PolicyData> getPoliciesByStatus(PolicyStatus status) {
        return policyDataRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<PolicyData> getActivePolicies() {
        return policyDataRepository.findActivePolicies(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<PolicyData> getExpiredPolicies() {
        return policyDataRepository.findExpiredPolicies(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<PolicyData> getPoliciesExpiringSoon(int days) {
        LocalDate currentDate = LocalDate.now();
        LocalDate futureDate = currentDate.plusDays(days);
        return policyDataRepository.findPoliciesExpiringSoon(currentDate, futureDate);
    }

    @Transactional(readOnly = true)
    public List<PolicyData> getPoliciesByImplementingAgency(String agency) {
        return policyDataRepository.findByImplementingAgencyContainingIgnoreCase(agency);
    }

    @Transactional(readOnly = true)
    public List<PolicyData> getPoliciesByGeographicScope(GeographicScope scope) {
        return policyDataRepository.findByGeographicScope(scope);
    }

    @Transactional(readOnly = true)
    public List<PolicyData> getPoliciesByBudgetRange(BigDecimal minBudget, BigDecimal maxBudget) {
        return policyDataRepository.findByBudgetRange(minBudget, maxBudget);
    }

    @Transactional(readOnly = true)
    public List<PolicyData> getPoliciesRequiringReview() {
        return policyDataRepository.findPoliciesRequiringReview(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<PolicyData> getClimateSmartPolicies() {
        return policyDataRepository.findClimateSmartPolicies();
    }

    @Transactional(readOnly = true)
    public List<PolicyData> getYouthFocusedPolicies() {
        return policyDataRepository.findYouthFocusedPolicies();
    }

    // Performance Analysis

    @Transactional(readOnly = true)
    public List<PolicyData> getHighPerformingPolicies(BigDecimal minUtilizationRate) {
        return policyDataRepository.findByUtilizationRateGreaterThanEqual(minUtilizationRate);
    }

    @Transactional(readOnly = true)
    public List<PolicyData> getLowPerformingPolicies(BigDecimal threshold) {
        return policyDataRepository.findLowPerformingPolicies(threshold);
    }

    @Transactional(readOnly = true)
    public List<PolicyData> getTopPerformingPolicies() {
        return policyDataRepository.findTopPerformingPolicies();
    }

    @Transactional(readOnly = true)
    public PolicyEffectiveness assessPolicyEffectiveness(String id) {
        PolicyData policy = getPolicyById(id);
        return policy.calculateEffectiveness();
    }

    // Statistics and Dashboard Data


    @Transactional(readOnly = true)
    public Map<String, Object> getPolicyStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // Total policies count
            long totalPolicies = policyDataRepository.count();
            statistics.put("totalPolicies", totalPolicies);

            // Active policies count
            long activePolicies = policyDataRepository.countByStatus(PolicyStatus.ACTIVE);
            statistics.put("activePolicies", activePolicies);

            // Draft policies count
            long draftPolicies = policyDataRepository.countByStatus(PolicyStatus.DRAFT);
            statistics.put("draftPolicies", draftPolicies);

            // Expired policies count
            long expiredPolicies = policyDataRepository.countByStatus(PolicyStatus.EXPIRED);
            statistics.put("expiredPolicies", expiredPolicies);

            // Total budget calculation with null safety
            BigDecimal totalBudget = BigDecimal.ZERO;
            try {
                totalBudget = policyDataRepository.calculateTotalActiveBudget();
                if (totalBudget == null) {
                    totalBudget = BigDecimal.ZERO;
                }
            } catch (Exception e) {
                logger.warn("Error calculating total budget: {}", e.getMessage());
                totalBudget = BigDecimal.ZERO;
            }
            statistics.put("totalBudget", totalBudget);

            // Total beneficiaries with null safety
            int totalBeneficiaries = 0;
            try {
                List<PolicyData> allPolicies = policyDataRepository.findAll();
                totalBeneficiaries = allPolicies.stream()
                        .filter(p -> p.getBeneficiariesCount() != null)
                        .mapToInt(PolicyData::getBeneficiariesCount)
                        .sum();
            } catch (Exception e) {
                logger.warn("Error calculating total beneficiaries: {}", e.getMessage());
                totalBeneficiaries = 0;
            }
            statistics.put("totalBeneficiaries", totalBeneficiaries);

            // ✅ CORRECTION: Subsidy programs count
            long subsidyPrograms = 0;
            try {
                subsidyPrograms = policyDataRepository.countByPolicyType(PolicyType.SUBSIDY);
            } catch (Exception e) {
                logger.warn("Error counting subsidy programs: {}", e.getMessage());
                subsidyPrograms = 0L;
            }
            statistics.put("subsidyPrograms", subsidyPrograms);

            // Credit programs count
            long creditPrograms = 0;
            try {
                creditPrograms = policyDataRepository.countByPolicyType(PolicyType.CREDIT_PROGRAM);
            } catch (Exception e) {
                logger.warn("Error counting credit programs: {}", e.getMessage());
                creditPrograms = 0L;
            }
            statistics.put("creditPrograms", creditPrograms);

            // Average utilization rate with null safety
            BigDecimal avgUtilization = BigDecimal.ZERO;
            try {
                avgUtilization = policyDataRepository.calculateAverageUtilizationRate();
                if (avgUtilization == null) {
                    avgUtilization = BigDecimal.ZERO;
                }
            } catch (Exception e) {
                logger.warn("Error calculating average utilization: {}", e.getMessage());
                avgUtilization = BigDecimal.ZERO;
            }
            statistics.put("averageUtilization", avgUtilization);

            // Policies by status with error handling
            Map<PolicyStatus, Long> statusCounts = new HashMap<>();
            try {
                for (PolicyStatus status : PolicyStatus.values()) {
                    try {
                        long count = policyDataRepository.countByStatus(status);
                        if (count > 0) {
                            statusCounts.put(status, count);
                        }
                    } catch (Exception e) {
                        logger.warn("Error counting status {}: {}", status, e.getMessage());
                    }
                }
            } catch (Exception e) {
                logger.warn("Error counting policies by status: {}", e.getMessage());
            }
            statistics.put("policiesByStatus", statusCounts);

            // Policies by type with error handling
            Map<PolicyType, Long> typeCounts = new HashMap<>();
            try {
                for (PolicyType type : PolicyType.values()) {
                    try {
                        long count = policyDataRepository.countByPolicyType(type);
                        if (count > 0) {
                            typeCounts.put(type, count);
                        }
                    } catch (Exception e) {
                        logger.warn("Error counting type {}: {}", type, e.getMessage());
                    }
                }
            } catch (Exception e) {
                logger.warn("Error counting policies by type: {}", e.getMessage());
            }
            statistics.put("policiesByType", typeCounts);

            // Policies by category with error handling
            Map<PolicyCategory, Long> categoryCounts = new HashMap<>();
            try {
                for (PolicyCategory category : PolicyCategory.values()) {
                    try {
                        long count = policyDataRepository.countByPolicyCategory(category);
                        if (count > 0) {
                            categoryCounts.put(category, count);
                        }
                    } catch (Exception e) {
                        logger.warn("Error counting category {}: {}", category, e.getMessage());
                    }
                }
            } catch (Exception e) {
                logger.warn("Error counting policies by category: {}", e.getMessage());
            }
            statistics.put("policiesByCategory", categoryCounts);

            // Policies requiring review
            long policiesRequiringReview = 0;
            try {
                List<PolicyData> reviewPolicies = policyDataRepository.findPoliciesRequiringReview(LocalDate.now());
                policiesRequiringReview = reviewPolicies != null ? reviewPolicies.size() : 0;
            } catch (Exception e) {
                logger.warn("Error counting policies requiring review: {}", e.getMessage());
                policiesRequiringReview = 0L;
            }
            statistics.put("policiesRequiringReview", policiesRequiringReview);

            // Climate smart policies
            long climateSmartCount = 0;
            try {
                climateSmartCount = policyDataRepository.countByClimateSmart(true);
            } catch (Exception e) {
                logger.warn("Error counting climate smart policies: {}", e.getMessage());
                climateSmartCount = 0L;
            }
            statistics.put("climateSmartPolicies", climateSmartCount);

            // Youth focused policies
            long youthFocusedCount = 0;
            try {
                youthFocusedCount = policyDataRepository.countByYouthFocus(true);
            } catch (Exception e) {
                logger.warn("Error counting youth focused policies: {}", e.getMessage());
                youthFocusedCount = 0L;
            }
            statistics.put("youthFocusedPolicies", youthFocusedCount);

            logger.info("✅ Policy statistics calculated successfully");
            return statistics;

        } catch (Exception e) {
            logger.error("❌ Critical error calculating policy statistics: {}", e.getMessage(), e);

            // Return safe fallback statistics
            Map<String, Object> fallbackStats = new HashMap<>();
            fallbackStats.put("totalPolicies", 0L);
            fallbackStats.put("activePolicies", 0L);
            fallbackStats.put("draftPolicies", 0L);
            fallbackStats.put("expiredPolicies", 0L);
            fallbackStats.put("totalBudget", BigDecimal.ZERO);
            fallbackStats.put("totalBeneficiaries", 0);
            fallbackStats.put("subsidyPrograms", 0L);
            fallbackStats.put("creditPrograms", 0L);
            fallbackStats.put("averageUtilization", BigDecimal.ZERO);
            fallbackStats.put("policiesByStatus", new HashMap<>());
            fallbackStats.put("policiesByType", new HashMap<>());
            fallbackStats.put("policiesByCategory", new HashMap<>());
            fallbackStats.put("policiesRequiringReview", 0L);
            fallbackStats.put("climateSmartPolicies", 0L);
            fallbackStats.put("youthFocusedPolicies", 0L);
            fallbackStats.put("error", "Error loading statistics");

            return fallbackStats;
        }
    }




    @Transactional(readOnly = true)
    public Map<PolicyStatus, Long> getPolicyCountByStatus() {
        List<Object[]> results = policyDataRepository.countPoliciesByStatus();
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (PolicyStatus) row[0],
                        row -> (Long) row[1]
                ));
    }

    @Transactional(readOnly = true)
    public Map<PolicyType, Long> getPolicyCountByType() {
        List<Object[]> results = policyDataRepository.countPoliciesByType();
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (PolicyType) row[0],
                        row -> (Long) row[1]
                ));
    }

    @Transactional(readOnly = true)
    public Map<PolicyCategory, Long> getPolicyCountByCategory() {
        List<Object[]> results = policyDataRepository.countPoliciesByCategory();
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (PolicyCategory) row[0],
                        row -> (Long) row[1]
                ));
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalActiveBudget() {
        BigDecimal total = policyDataRepository.calculateTotalActiveBudget();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getAverageUtilizationRate() {
        BigDecimal average = policyDataRepository.calculateAverageUtilizationRate();
        return average != null ? average : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public List<PolicyData> getRecentPolicies(int days) {
        LocalDateTime date = LocalDateTime.now().minusDays(days);
        return policyDataRepository.findRecentPolicies(date);
    }

    // Complex Search

    @Transactional(readOnly = true)
    public List<PolicyData> searchPoliciesWithCriteria(
            PolicyType policyType,
            PolicyCategory policyCategory,
            PolicyStatus status,
            GeographicScope geographicScope,
            String implementingAgency,
            BigDecimal minBudget,
            BigDecimal maxBudget) {

        return policyDataRepository.findByMultipleCriteria(
                policyType, policyCategory, status, geographicScope,
                implementingAgency, minBudget, maxBudget
        );
    }

    @Transactional(readOnly = true)
    public Page<PolicyData> searchPoliciesWithSpecification(Specification<PolicyData> spec, Pageable pageable) {
        return policyDataRepository.findAll(spec, pageable);
    }

    // Business Logic Methods

    @Transactional
    public PolicyData renewPolicy(String id, LocalDate newExpiryDate, String updatedBy) {
        PolicyData policy = getPolicyById(id);

        if (policy.getStatus() != PolicyStatus.ACTIVE && policy.getStatus() != PolicyStatus.EXPIRED) {
            throw new InvalidOperationException("Only active or expired policies can be renewed");
        }

        policy.setExpiryDate(newExpiryDate);
        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setLastUpdated(LocalDateTime.now());
        policy.setUpdatedBy(updatedBy);

        // Reset next review date
        policy.setNextReviewDate(LocalDate.now().plusYears(1));

        return policyDataRepository.save(policy);
    }

    @Transactional
    public PolicyData suspendPolicy(String id, String reason, String updatedBy) {
        PolicyData policy = getPolicyById(id);

        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            throw new InvalidOperationException("Only active policies can be suspended");
        }

        policy.setStatus(PolicyStatus.SUSPENDED);
        policy.setLastUpdated(LocalDateTime.now());
        policy.setUpdatedBy(updatedBy);

        // Add suspension reason to modification history
        String suspensionNote = String.format("Policy suspended on %s. Reason: %s",
                LocalDate.now(), reason);
        String existingHistory = policy.getModificationHistory();
        String newHistory = existingHistory != null ?
                existingHistory + "; " + suspensionNote : suspensionNote;
        policy.setModificationHistory(newHistory);

        return policyDataRepository.save(policy);
    }

    @Transactional
    public PolicyData activatePolicy(String id, String updatedBy) {
        PolicyData policy = getPolicyById(id);

        if (policy.getStatus() != PolicyStatus.DRAFT && policy.getStatus() != PolicyStatus.SUSPENDED) {
            throw new InvalidOperationException("Only draft or suspended policies can be activated");
        }

        // Validation before activation
        validatePolicyForActivation(policy);

        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setLastUpdated(LocalDateTime.now());
        policy.setUpdatedBy(updatedBy);

        return policyDataRepository.save(policy);
    }

    // Validation Methods

    private void validatePolicyData(PolicyData policyData) {
        if (!StringUtils.hasText(policyData.getPolicyName())) {
            throw new IllegalArgumentException("Policy name is required");
        }

        if (policyData.getPolicyType() == null) {
            throw new IllegalArgumentException("Policy type is required");
        }

        if (policyData.getPolicyCategory() == null) {
            throw new IllegalArgumentException("Policy category is required");
        }

        if (!StringUtils.hasText(policyData.getDescription())) {
            throw new IllegalArgumentException("Policy description is required");
        }

        if (!StringUtils.hasText(policyData.getImplementingAgency())) {
            throw new IllegalArgumentException("Implementing agency is required");
        }

        if (policyData.getEffectiveDate() == null) {
            throw new IllegalArgumentException("Effective date is required");
        }

        if (policyData.getExpiryDate() != null &&
                policyData.getExpiryDate().isBefore(policyData.getEffectiveDate())) {
            throw new IllegalArgumentException("Expiry date must be after effective date");
        }

        if (policyData.getTotalBudget() != null &&
                policyData.getTotalBudget().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total budget cannot be negative");
        }

        if (policyData.getBudgetAllocated() != null && policyData.getTotalBudget() != null &&
                policyData.getBudgetAllocated().compareTo(policyData.getTotalBudget()) > 0) {
            throw new IllegalArgumentException("Budget allocated cannot exceed total budget");
        }

        if (policyData.getBudgetUtilized() != null && policyData.getBudgetAllocated() != null &&
                policyData.getBudgetUtilized().compareTo(policyData.getBudgetAllocated()) > 0) {
            throw new IllegalArgumentException("Budget utilized cannot exceed budget allocated");
        }
    }

    private void validatePolicyForActivation(PolicyData policy) {
        if (policy.getGeographicScope() == null) {
            throw new InvalidOperationException("Geographic scope must be set before activation");
        }

        if (!StringUtils.hasText(policy.getObjectives())) {
            throw new InvalidOperationException("Policy objectives must be defined before activation");
        }

        if (policy.getEffectiveDate().isBefore(LocalDate.now())) {
            throw new InvalidOperationException("Cannot activate policy with past effective date");
        }
    }

    private void validateStatusTransition(PolicyStatus currentStatus, PolicyStatus newStatus) {
        // Define valid status transitions
        Map<PolicyStatus, Set<PolicyStatus>> validTransitions = Map.of(
                PolicyStatus.DRAFT, Set.of(PolicyStatus.ACTIVE, PolicyStatus.CANCELLED),
                PolicyStatus.ACTIVE, Set.of(PolicyStatus.SUSPENDED, PolicyStatus.EXPIRED, PolicyStatus.UNDER_REVIEW),
                PolicyStatus.SUSPENDED, Set.of(PolicyStatus.ACTIVE, PolicyStatus.CANCELLED, PolicyStatus.EXPIRED),
                PolicyStatus.UNDER_REVIEW, Set.of(PolicyStatus.ACTIVE, PolicyStatus.DRAFT, PolicyStatus.CANCELLED),
                PolicyStatus.EXPIRED, Set.of(PolicyStatus.ACTIVE), // For renewal
                PolicyStatus.CANCELLED, Set.of() // Terminal state
        );

        Set<PolicyStatus> allowedTransitions = validTransitions.getOrDefault(currentStatus, Set.of());

        if (!allowedTransitions.contains(newStatus)) {
            throw new InvalidOperationException(
                    String.format("Invalid status transition from %s to %s",
                            currentStatus.getDisplayName(), newStatus.getDisplayName())
            );
        }
    }
}