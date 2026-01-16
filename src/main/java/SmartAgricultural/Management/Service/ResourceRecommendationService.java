package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.ResourceRecommendation;
import SmartAgricultural.Management.Model.ResourceRecommendation.ResourceType;
import SmartAgricultural.Management.Model.ResourceRecommendation.RecommendationCategory;
import SmartAgricultural.Management.Model.ResourceRecommendation.PriorityLevel;
import SmartAgricultural.Management.Model.ResourceRecommendation.RecommendationStatus;
import SmartAgricultural.Management.Model.ResourceRecommendation.ImplementationDifficulty;
import SmartAgricultural.Management.Repository.ResourceRecommendationRepository;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.exception.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResourceRecommendationService {

    @Autowired
    private ResourceRecommendationRepository resourceRecommendationRepository;

    // Basic CRUD operations
    public ResourceRecommendation save(ResourceRecommendation recommendation) {
        validateRecommendation(recommendation);
        return resourceRecommendationRepository.save(recommendation);
    }

    public ResourceRecommendation create(ResourceRecommendation recommendation) {
        // Clear any auto-generated IDs from the constructor
        recommendation.setId(null);
        recommendation.setRecommendationCode(null);

        // The @PrePersist will generate new ones
        return save(recommendation);
    }

    public ResourceRecommendation update(String id, ResourceRecommendation recommendation) {
        ResourceRecommendation existing = findById(id);

        // Update fields while preserving immutable ones
        updateRecommendationFields(existing, recommendation);

        return save(existing);
    }

    @Transactional(readOnly = true)
    public ResourceRecommendation findById(String id) {
        return resourceRecommendationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource recommendation not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<ResourceRecommendation> findByIdOptional(String id) {
        return resourceRecommendationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public ResourceRecommendation findByRecommendationCode(String recommendationCode) {
        return resourceRecommendationRepository.findByRecommendationCode(recommendationCode)
                .orElseThrow(() -> new ResourceNotFoundException("Resource recommendation not found with code: " + recommendationCode));
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findAll() {
        return resourceRecommendationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<ResourceRecommendation> findAll(Pageable pageable) {
        return resourceRecommendationRepository.findAll(pageable);
    }

    public void deleteById(String id) {
        if (!resourceRecommendationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource recommendation not found with id: " + id);
        }
        resourceRecommendationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(String id) {
        return resourceRecommendationRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByRecommendationCode(String recommendationCode) {
        return resourceRecommendationRepository.existsByRecommendationCode(recommendationCode);
    }

    @Transactional(readOnly = true)
    public long count() {
        return resourceRecommendationRepository.count();
    }

    // Farm-specific operations
    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findByFarmId(String farmId) {
        return resourceRecommendationRepository.findByFarmId(farmId);
    }

    @Transactional(readOnly = true)
    public Page<ResourceRecommendation> findByFarmId(String farmId, Pageable pageable) {
        return resourceRecommendationRepository.findByFarmId(farmId, pageable);
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findByCropProductionId(String cropProductionId) {
        return resourceRecommendationRepository.findByCropProductionId(cropProductionId);
    }

    @Transactional(readOnly = true)
    public long countByFarmId(String farmId) {
        return resourceRecommendationRepository.countByFarmId(farmId);
    }

    // Status-based operations
    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findByStatus(RecommendationStatus status) {
        return resourceRecommendationRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public Page<ResourceRecommendation> findByStatus(RecommendationStatus status, Pageable pageable) {
        return resourceRecommendationRepository.findByStatus(status, pageable);
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findByFarmIdAndStatus(String farmId, RecommendationStatus status) {
        return resourceRecommendationRepository.findByFarmIdAndStatus(farmId, status);
    }

    @Transactional(readOnly = true)
    public Page<ResourceRecommendation> findByFarmIdAndStatus(String farmId, RecommendationStatus status, Pageable pageable) {
        return resourceRecommendationRepository.findByFarmIdAndStatus(farmId, status, pageable);
    }

    @Transactional(readOnly = true)
    public long countByFarmIdAndStatus(String farmId, RecommendationStatus status) {
        return resourceRecommendationRepository.countByFarmIdAndStatus(farmId, status);
    }

    // Active recommendations
    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findActiveRecommendations() {
        return findByStatus(RecommendationStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findActiveRecommendationsByFarmId(String farmId) {
        return resourceRecommendationRepository.findActiveValidRecommendationsByFarmId(farmId);
    }

    @Transactional(readOnly = true)
    public long countActiveValidRecommendationsByFarmId(String farmId) {
        return resourceRecommendationRepository.countActiveValidRecommendationsByFarmId(farmId);
    }

    // Priority-based operations
    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findByPriorityLevel(PriorityLevel priorityLevel) {
        return resourceRecommendationRepository.findByPriorityLevel(priorityLevel);
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findHighPriorityRecommendations() {
        return resourceRecommendationRepository.findHighPriorityRecommendations();
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findHighPriorityRecommendationsByFarmId(String farmId) {
        return resourceRecommendationRepository.findHighPriorityRecommendationsByFarmId(farmId);
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findUrgentRecommendationsByFarmId(String farmId) {
        return resourceRecommendationRepository.findByFarmIdAndPriorityLevel(farmId, PriorityLevel.URGENT);
    }

    // Resource type operations
    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findByResourceType(ResourceType resourceType) {
        return resourceRecommendationRepository.findByResourceType(resourceType);
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findByFarmIdAndResourceType(String farmId, ResourceType resourceType) {
        return resourceRecommendationRepository.findByFarmIdAndResourceType(farmId, resourceType);
    }

    @Transactional(readOnly = true)
    public long countByFarmIdAndResourceType(String farmId, ResourceType resourceType) {
        return resourceRecommendationRepository.countByFarmIdAndResourceType(farmId, resourceType);
    }

    // Category operations
    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findByRecommendationCategory(RecommendationCategory category) {
        return resourceRecommendationRepository.findByRecommendationCategory(category);
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findByFarmIdAndRecommendationCategory(String farmId, RecommendationCategory category) {
        return resourceRecommendationRepository.findByFarmIdAndRecommendationCategory(farmId, category);
    }

    // Date-based operations
    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findExpiredRecommendations() {
        return resourceRecommendationRepository.findExpiredActiveRecommendations(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findExpiredRecommendationsByFarmId(String farmId) {
        return resourceRecommendationRepository.findExpiredActiveRecommendationsByFarmId(farmId, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findExpiringSoonRecommendations(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(daysAhead);
        return resourceRecommendationRepository.findExpiringSoonRecommendations(today, futureDate);
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findCurrentTimingWindowRecommendations(String farmId) {
        return resourceRecommendationRepository.findCurrentTimingWindowRecommendations(farmId, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findRecentRecommendationsByFarmId(String farmId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return resourceRecommendationRepository.findRecentRecommendationsByFarmId(farmId, since);
    }

    // Implementation operations
    public ResourceRecommendation implementRecommendation(String id, String implementationNotes) {
        ResourceRecommendation recommendation = findById(id);

        if (!recommendation.canImplement()) {
            throw new ValidationException("Recommendation cannot be implemented in current state: " + recommendation.getStatus());
        }

        recommendation.implement(implementationNotes);
        return save(recommendation);
    }

    public ResourceRecommendation rejectRecommendation(String id) {
        ResourceRecommendation recommendation = findById(id);

        if (!recommendation.canReject()) {
            throw new ValidationException("Recommendation cannot be rejected in current state: " + recommendation.getStatus());
        }

        recommendation.reject();
        return save(recommendation);
    }

    public ResourceRecommendation supersedeRecommendation(String id) {
        ResourceRecommendation recommendation = findById(id);

        if (!recommendation.canSupersede()) {
            throw new ValidationException("Recommendation cannot be superseded in current state: " + recommendation.getStatus());
        }

        recommendation.supersede();
        return save(recommendation);
    }

    public ResourceRecommendation addEffectivenessRating(String id, int rating, String feedback) {
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Effectiveness rating must be between 1 and 5");
        }

        ResourceRecommendation recommendation = findById(id);
        recommendation.addEffectivenessRating(rating, feedback);
        return save(recommendation);
    }

    public ResourceRecommendation updateActualCost(String id, BigDecimal actualCost) {
        if (actualCost != null && actualCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Actual cost cannot be negative");
        }

        ResourceRecommendation recommendation = findById(id);
        recommendation.updateActualCost(actualCost);
        return save(recommendation);
    }

    public ResourceRecommendation scheduleFollowUp(String id, LocalDate followUpDate) {
        if (followUpDate != null && followUpDate.isBefore(LocalDate.now())) {
            throw new ValidationException("Follow-up date cannot be in the past");
        }

        ResourceRecommendation recommendation = findById(id);
        recommendation.scheduleFollowUp(followUpDate);
        return save(recommendation);
    }

    public ResourceRecommendation completeFollowUp(String id) {
        ResourceRecommendation recommendation = findById(id);
        recommendation.completeFollowUp();
        return save(recommendation);
    }

    // Follow-up operations
    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findFollowUpDue() {
        return resourceRecommendationRepository.findFollowUpDue(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findFollowUpDueByFarmId(String farmId) {
        return resourceRecommendationRepository.findFollowUpDueByFarmId(farmId, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public long countFollowUpDueByFarmId(String farmId) {
        return resourceRecommendationRepository.countFollowUpDueByFarmId(farmId, LocalDate.now());
    }

    // Review operations
    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findUnreviewedRecommendations() {
        return resourceRecommendationRepository.findByReviewedByIsNull();
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findReviewedRecommendations() {
        return resourceRecommendationRepository.findByReviewedByIsNotNull();
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findByReviewedBy(String reviewerId) {
        return resourceRecommendationRepository.findByReviewedBy(reviewerId);
    }

    public ResourceRecommendation reviewRecommendation(String id, String reviewerId) {
        ResourceRecommendation recommendation = findById(id);
        recommendation.setReviewedBy(reviewerId);
        recommendation.setReviewDate(LocalDateTime.now());
        return save(recommendation);
    }

    // Search and filtering operations
    @Transactional(readOnly = true)
    public List<ResourceRecommendation> searchRecommendations(String farmId, String searchTerm) {
        if (!StringUtils.hasText(searchTerm)) {
            return findByFarmId(farmId);
        }
        return resourceRecommendationRepository.searchByTitleDescriptionOrActionAndFarmId(searchTerm.trim(), farmId);
    }

    @Transactional(readOnly = true)
    public Page<ResourceRecommendation> searchRecommendations(String farmId, String searchTerm, Pageable pageable) {
        if (!StringUtils.hasText(searchTerm)) {
            return findByFarmId(farmId, pageable);
        }
        return resourceRecommendationRepository.searchByTitleDescriptionOrActionAndFarmId(searchTerm.trim(), farmId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ResourceRecommendation> findRecommendationsWithFilters(
            String farmId,
            ResourceType resourceType,
            RecommendationCategory category,
            PriorityLevel priority,
            RecommendationStatus status,
            Pageable pageable) {

        return resourceRecommendationRepository.findRecommendationsWithFilters(
                farmId, resourceType, category, priority, status, pageable);
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findOptimalRecommendations(
            String farmId,
            BigDecimal minConfidence,
            BigDecimal maxCost) {

        return resourceRecommendationRepository.findOptimalRecommendations(farmId, minConfidence, maxCost);
    }

    // Advanced filtering
    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findHighConfidenceRecommendations(String farmId, BigDecimal minConfidence) {
        return resourceRecommendationRepository.findByConfidenceScoreGreaterThanEqual(minConfidence)
                .stream()
                .filter(r -> r.getFarmId().equals(farmId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findLowCostRecommendations(String farmId, BigDecimal maxCost) {
        return resourceRecommendationRepository.findByEstimatedCostBetween(BigDecimal.ZERO, maxCost)
                .stream()
                .filter(r -> r.getFarmId().equals(farmId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findHighROIRecommendations(String farmId, BigDecimal minRoi) {
        return resourceRecommendationRepository.findByExpectedRoiGreaterThanEqual(minRoi)
                .stream()
                .filter(r -> r.getFarmId().equals(farmId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findEasyToImplementRecommendations(String farmId) {
        return resourceRecommendationRepository.findEasyToImplementRecommendations()
                .stream()
                .filter(r -> r.getFarmId().equals(farmId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findSustainableRecommendations(String farmId, Integer minSustainabilityScore) {
        return resourceRecommendationRepository.findSustainableRecommendationsByFarmId(farmId, minSustainabilityScore);
    }

    // Statistics and analytics
    @Transactional(readOnly = true)
    public Map<ResourceType, Long> getResourceTypeStatistics(String farmId) {
        List<Object[]> results = resourceRecommendationRepository.countByResourceTypeAndFarmId(farmId);
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (ResourceType) result[0],
                        result -> (Long) result[1]
                ));
    }

    @Transactional(readOnly = true)
    public Map<PriorityLevel, Long> getPriorityLevelStatistics(String farmId) {
        List<Object[]> results = resourceRecommendationRepository.countByPriorityLevelAndFarmId(farmId);
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (PriorityLevel) result[0],
                        result -> (Long) result[1]
                ));
    }

    @Transactional(readOnly = true)
    public BigDecimal getAverageConfidenceScore(String farmId, RecommendationStatus status) {
        return resourceRecommendationRepository.getAverageConfidenceScoreByFarmIdAndStatus(farmId, status);
    }

    @Transactional(readOnly = true)
    public Double getAverageEffectivenessRating(String farmId) {
        return resourceRecommendationRepository.getAverageEffectivenessRatingByFarmId(farmId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalEstimatedCost(String farmId) {
        BigDecimal total = resourceRecommendationRepository.getTotalEstimatedCostByFarmId(farmId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalActualCost(String farmId) {
        BigDecimal total = resourceRecommendationRepository.getTotalActualCostByFarmId(farmId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getCostVariance(String farmId) {
        BigDecimal estimated = getTotalEstimatedCost(farmId);
        BigDecimal actual = getTotalActualCost(farmId);
        return actual.subtract(estimated);
    }


    // Fix for the getFarmRecommendationSummary method in ResourceRecommendationService
    @Transactional(readOnly = true)
    public Map<String, Object> getFarmRecommendationSummary(String farmId) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalRecommendations", countByFarmId(farmId));
        summary.put("activeRecommendations", countActiveValidRecommendationsByFarmId(farmId));
        summary.put("highPriorityCount", findHighPriorityRecommendationsByFarmId(farmId).size());
        summary.put("followUpDueCount", countFollowUpDueByFarmId(farmId));
        summary.put("averageConfidence", getAverageConfidenceScore(farmId, RecommendationStatus.ACTIVE));
        summary.put("averageEffectiveness", getAverageEffectivenessRating(farmId));
        summary.put("totalEstimatedCost", getTotalEstimatedCost(farmId));
        summary.put("totalActualCost", getTotalActualCost(farmId));
        summary.put("costVariance", getCostVariance(farmId));
        summary.put("resourceTypeStats", getResourceTypeStatistics(farmId));
        summary.put("priorityLevelStats", getPriorityLevelStatistics(farmId));
        return summary;
    }

    // Maintenance operations
    public int expireOverdueRecommendations() {
        List<ResourceRecommendation> overdueRecommendations =
                resourceRecommendationRepository.findRecommendationsToExpire();

        int count = 0;
        for (ResourceRecommendation recommendation : overdueRecommendations) {
            recommendation.markAsExpired();
            save(recommendation);
            count++;
        }

        return count;
    }

    public int cleanupExpiredRecommendations(int daysOld) {
        LocalDate cutoffDate = LocalDate.now().minusDays(daysOld);
        return resourceRecommendationRepository.deleteExpiredRecommendationsOlderThan(cutoffDate);
    }

    public int cleanupOldRejectedRecommendations(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        return resourceRecommendationRepository.deleteOldRejectedRecommendations(cutoffDate);
    }

    // Bulk operations
    public List<ResourceRecommendation> createRecommendations(List<ResourceRecommendation> recommendations) {
        for (ResourceRecommendation recommendation : recommendations) {
            validateRecommendation(recommendation);
        }
        return resourceRecommendationRepository.saveAll(recommendations);
    }

    public List<ResourceRecommendation> updateRecommendations(List<ResourceRecommendation> recommendations) {
        List<ResourceRecommendation> updatedRecommendations = recommendations.stream()
                .map(recommendation -> {
                    if (recommendation.getId() == null) {
                        throw new ValidationException("Cannot update recommendation without ID");
                    }
                    validateRecommendation(recommendation);
                    return recommendation;
                })
                .collect(Collectors.toList());

        return resourceRecommendationRepository.saveAll(updatedRecommendations);
    }

    public void deleteRecommendations(List<String> ids) {
        List<ResourceRecommendation> recommendations = ids.stream()
                .map(this::findById)
                .collect(Collectors.toList());

        resourceRecommendationRepository.deleteAll(recommendations);
    }

    // AI model operations
    @Transactional(readOnly = true)
    public List<ResourceRecommendation> findByAiModelVersion(String aiModelVersion) {
        return resourceRecommendationRepository.findByAiModelVersion(aiModelVersion);
    }

    @Transactional(readOnly = true)
    public List<String> getAllAiModelVersions() {
        return resourceRecommendationRepository.findDistinctAiModelVersions();
    }

    // Pagination helpers
    @Transactional(readOnly = true)
    public Page<ResourceRecommendation> findActiveRecommendationsPaged(String farmId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "priorityLevel", "generatedDate"));
        return findByFarmIdAndStatus(farmId, RecommendationStatus.ACTIVE, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ResourceRecommendation> findRecommendationsByPriority(String farmId, PriorityLevel priority, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "generatedDate"));
        return findRecommendationsWithFilters(farmId, null, null, priority, null, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ResourceRecommendation> findRecommendationsByResourceType(String farmId, ResourceType resourceType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "priorityLevel", "generatedDate"));
        return findRecommendationsWithFilters(farmId, resourceType, null, null, null, pageable);
    }

    // Validation methods
    private void validateRecommendation(ResourceRecommendation recommendation) {
        if (!StringUtils.hasText(recommendation.getFarmId())) {
            throw new ValidationException("Farm ID is required");
        }

        if (recommendation.getResourceType() == null) {
            throw new ValidationException("Resource type is required");
        }

        if (recommendation.getRecommendationCategory() == null) {
            throw new ValidationException("Recommendation category is required");
        }

        if (recommendation.getPriorityLevel() == null) {
            throw new ValidationException("Priority level is required");
        }

        if (!StringUtils.hasText(recommendation.getTitle())) {
            throw new ValidationException("Title is required");
        }

        if (!StringUtils.hasText(recommendation.getDescription())) {
            throw new ValidationException("Description is required");
        }

        if (!StringUtils.hasText(recommendation.getRecommendedAction())) {
            throw new ValidationException("Recommended action is required");
        }

        if (!StringUtils.hasText(recommendation.getRationale())) {
            throw new ValidationException("Rationale is required");
        }

        if (!StringUtils.hasText(recommendation.getAiModelVersion())) {
            throw new ValidationException("AI model version is required");
        }

        if (recommendation.getConfidenceScore() == null) {
            throw new ValidationException("Confidence score is required");
        }

        if (recommendation.getConfidenceScore().compareTo(BigDecimal.ZERO) < 0 ||
                recommendation.getConfidenceScore().compareTo(new BigDecimal("100")) > 0) {
            throw new ValidationException("Confidence score must be between 0 and 100");
        }

        if (recommendation.getRecommendedQuantity() != null &&
                recommendation.getRecommendedQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Recommended quantity cannot be negative");
        }

        if (recommendation.getEstimatedCost() != null &&
                recommendation.getEstimatedCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Estimated cost cannot be negative");
        }

        if (recommendation.getExpectedRoi() != null &&
                recommendation.getExpectedRoi().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Expected ROI cannot be negative");
        }

        if (recommendation.getSustainabilityScore() != null &&
                (recommendation.getSustainabilityScore() < 1 || recommendation.getSustainabilityScore() > 10)) {
            throw new ValidationException("Sustainability score must be between 1 and 10");
        }

        if (recommendation.getEffectivenessRating() != null &&
                (recommendation.getEffectivenessRating() < 1 || recommendation.getEffectivenessRating() > 5)) {
            throw new ValidationException("Effectiveness rating must be between 1 and 5");
        }

        if (recommendation.getTimingStartDate() != null && recommendation.getTimingEndDate() != null &&
                recommendation.getTimingStartDate().isAfter(recommendation.getTimingEndDate())) {
            throw new ValidationException("Timing start date cannot be after end date");
        }

        if (recommendation.getFollowUpDate() != null && recommendation.getFollowUpRequired() != null &&
                !recommendation.getFollowUpRequired()) {
            throw new ValidationException("Follow-up date set but follow-up not required");
        }
    }

    private void updateRecommendationFields(ResourceRecommendation existing, ResourceRecommendation updated) {
        // Update mutable fields only
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }

        if (StringUtils.hasText(updated.getTitle())) {
            existing.setTitle(updated.getTitle());
        }

        if (StringUtils.hasText(updated.getDescription())) {
            existing.setDescription(updated.getDescription());
        }

        if (StringUtils.hasText(updated.getRecommendedAction())) {
            existing.setRecommendedAction(updated.getRecommendedAction());
        }

        if (updated.getRecommendedQuantity() != null) {
            existing.setRecommendedQuantity(updated.getRecommendedQuantity());
        }

        if (StringUtils.hasText(updated.getUnit())) {
            existing.setUnit(updated.getUnit());
        }

        if (StringUtils.hasText(updated.getOptimalTiming())) {
            existing.setOptimalTiming(updated.getOptimalTiming());
        }

        if (updated.getTimingStartDate() != null) {
            existing.setTimingStartDate(updated.getTimingStartDate());
        }

        if (updated.getTimingEndDate() != null) {
            existing.setTimingEndDate(updated.getTimingEndDate());
        }

        if (StringUtils.hasText(updated.getFrequency())) {
            existing.setFrequency(updated.getFrequency());
        }

        if (updated.getEstimatedCost() != null) {
            existing.setEstimatedCost(updated.getEstimatedCost());
        }

        if (StringUtils.hasText(updated.getCurrency())) {
            existing.setCurrency(updated.getCurrency());
        }

        if (StringUtils.hasText(updated.getExpectedBenefit())) {
            existing.setExpectedBenefit(updated.getExpectedBenefit());
        }

        if (updated.getExpectedRoi() != null) {
            existing.setExpectedRoi(updated.getExpectedRoi());
        }

        if (StringUtils.hasText(updated.getRationale())) {
            existing.setRationale(updated.getRationale());
        }

        if (StringUtils.hasText(updated.getScientificBasis())) {
            existing.setScientificBasis(updated.getScientificBasis());
        }

        if (updated.getConfidenceScore() != null) {
            existing.setConfidenceScore(updated.getConfidenceScore());
        }

        if (StringUtils.hasText(updated.getDataSources())) {
            existing.setDataSources(updated.getDataSources());
        }

        if (StringUtils.hasText(updated.getEnvironmentalImpact())) {
            existing.setEnvironmentalImpact(updated.getEnvironmentalImpact());
        }

        if (updated.getSustainabilityScore() != null) {
            existing.setSustainabilityScore(updated.getSustainabilityScore());
        }

        if (updated.getImplementationDifficulty() != null) {
            existing.setImplementationDifficulty(updated.getImplementationDifficulty());
        }

        if (StringUtils.hasText(updated.getPrerequisites())) {
            existing.setPrerequisites(updated.getPrerequisites());
        }

        if (StringUtils.hasText(updated.getAlternativeOptions())) {
            existing.setAlternativeOptions(updated.getAlternativeOptions());
        }

        if (StringUtils.hasText(updated.getSuccessIndicators())) {
            existing.setSuccessIndicators(updated.getSuccessIndicators());
        }

        if (StringUtils.hasText(updated.getMonitoringParameters())) {
            existing.setMonitoringParameters(updated.getMonitoringParameters());
        }

        if (updated.getValidUntil() != null) {
            existing.setValidUntil(updated.getValidUntil());
        }

        if (updated.getImplementationDate() != null) {
            existing.setImplementationDate(updated.getImplementationDate());
        }

        if (StringUtils.hasText(updated.getImplementationNotes())) {
            existing.setImplementationNotes(updated.getImplementationNotes());
        }

        if (updated.getEffectivenessRating() != null) {
            existing.setEffectivenessRating(updated.getEffectivenessRating());
        }

        if (StringUtils.hasText(updated.getFarmerFeedback())) {
            existing.setFarmerFeedback(updated.getFarmerFeedback());
        }

        if (updated.getActualCost() != null) {
            existing.setActualCost(updated.getActualCost());
        }

        if (StringUtils.hasText(updated.getActualBenefit())) {
            existing.setActualBenefit(updated.getActualBenefit());
        }

        if (updated.getFollowUpRequired() != null) {
            existing.setFollowUpRequired(updated.getFollowUpRequired());
        }

        if (updated.getFollowUpDate() != null) {
            existing.setFollowUpDate(updated.getFollowUpDate());
        }

        if (StringUtils.hasText(updated.getReviewedBy())) {
            existing.setReviewedBy(updated.getReviewedBy());
        }

        if (updated.getReviewDate() != null) {
            existing.setReviewDate(updated.getReviewDate());
        }
    }
}