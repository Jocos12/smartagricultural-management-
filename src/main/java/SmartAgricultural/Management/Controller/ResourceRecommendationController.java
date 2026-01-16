package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.ResourceRecommendation;
import SmartAgricultural.Management.Model.ResourceRecommendation.ResourceType;
import SmartAgricultural.Management.Model.ResourceRecommendation.RecommendationCategory;
import SmartAgricultural.Management.Model.ResourceRecommendation.PriorityLevel;
import SmartAgricultural.Management.Model.ResourceRecommendation.RecommendationStatus;
import SmartAgricultural.Management.Service.ResourceRecommendationService;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.exception.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/resource-recommendations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ResourceRecommendationController {

    @Autowired
    private ResourceRecommendationService resourceRecommendationService;

    // ===== BASIC CRUD OPERATIONS =====


    @PostMapping
    public ResponseEntity<?> createRecommendation(
            @Valid @RequestBody ResourceRecommendation recommendation) {
        try {
            // Add logging
            System.out.println("Received recommendation: " + recommendation);
            System.out.println("Farm ID: " + recommendation.getFarmId());
            System.out.println("Resource Type: " + recommendation.getResourceType());

            ResourceRecommendation created = resourceRecommendationService.create(recommendation);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (ValidationException e) {
            System.err.println("Validation error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Validation error", "message", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error", "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecommendation(
            @PathVariable String id,
            @Valid @RequestBody ResourceRecommendation recommendation) {
        try {
            ResourceRecommendation updated = resourceRecommendationService.update(id, recommendation);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Not found", "message", e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Validation error", "message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceRecommendation> getRecommendationById(@PathVariable String id) {
        try {
            ResourceRecommendation recommendation = resourceRecommendationService.findById(id);
            return ResponseEntity.ok(recommendation);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/code/{recommendationCode}")
    public ResponseEntity<ResourceRecommendation> getRecommendationByCode(
            @PathVariable String recommendationCode) {
        try {
            ResourceRecommendation recommendation =
                    resourceRecommendationService.findByRecommendationCode(recommendationCode);
            return ResponseEntity.ok(recommendation);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable String id) {
        try {
            resourceRecommendationService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<Page<ResourceRecommendation>> getAllRecommendations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "generatedDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ResourceRecommendation> recommendations = resourceRecommendationService.findAll(pageable);
        return ResponseEntity.ok(recommendations);
    }

    // ===== FARM-SPECIFIC OPERATIONS =====

    @GetMapping("/farm/{farmId}")
    public ResponseEntity<Page<ResourceRecommendation>> getRecommendationsByFarm(
            @PathVariable String farmId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "generatedDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ResourceRecommendation> recommendations =
                resourceRecommendationService.findByFarmId(farmId, pageable);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/active")
    public ResponseEntity<List<ResourceRecommendation>> getActiveRecommendationsByFarm(
            @PathVariable String farmId) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findActiveRecommendationsByFarmId(farmId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/active/paged")
    public ResponseEntity<Page<ResourceRecommendation>> getActiveRecommendationsByFarmPaged(
            @PathVariable String farmId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ResourceRecommendation> recommendations =
                resourceRecommendationService.findActiveRecommendationsPaged(farmId, page, size);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/count")
    public ResponseEntity<Long> getRecommendationCountByFarm(@PathVariable String farmId) {
        long count = resourceRecommendationService.countByFarmId(farmId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/crop-production/{cropProductionId}")
    public ResponseEntity<List<ResourceRecommendation>> getRecommendationsByCropProduction(
            @PathVariable String cropProductionId) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findByCropProductionId(cropProductionId);
        return ResponseEntity.ok(recommendations);
    }

    // ===== STATUS-BASED OPERATIONS =====

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<ResourceRecommendation>> getRecommendationsByStatus(
            @PathVariable RecommendationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "priorityLevel", "generatedDate"));
        Page<ResourceRecommendation> recommendations =
                resourceRecommendationService.findByStatus(status, pageable);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/status/{status}")
    public ResponseEntity<Page<ResourceRecommendation>> getRecommendationsByFarmAndStatus(
            @PathVariable String farmId,
            @PathVariable RecommendationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "priorityLevel", "generatedDate"));
        Page<ResourceRecommendation> recommendations =
                resourceRecommendationService.findByFarmIdAndStatus(farmId, status, pageable);
        return ResponseEntity.ok(recommendations);
    }

    // ===== PRIORITY-BASED OPERATIONS =====

    @GetMapping("/priority/high")
    public ResponseEntity<List<ResourceRecommendation>> getHighPriorityRecommendations() {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findHighPriorityRecommendations();
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/priority/high")
    public ResponseEntity<List<ResourceRecommendation>> getHighPriorityRecommendationsByFarm(
            @PathVariable String farmId) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findHighPriorityRecommendationsByFarmId(farmId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/priority/urgent")
    public ResponseEntity<List<ResourceRecommendation>> getUrgentRecommendationsByFarm(
            @PathVariable String farmId) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findUrgentRecommendationsByFarmId(farmId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/priority/{priority}/paged")
    public ResponseEntity<Page<ResourceRecommendation>> getRecommendationsByFarmAndPriority(
            @PathVariable String farmId,
            @PathVariable PriorityLevel priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ResourceRecommendation> recommendations =
                resourceRecommendationService.findRecommendationsByPriority(farmId, priority, page, size);
        return ResponseEntity.ok(recommendations);
    }

    // ===== RESOURCE TYPE OPERATIONS =====

    @GetMapping("/resource-type/{resourceType}")
    public ResponseEntity<List<ResourceRecommendation>> getRecommendationsByResourceType(
            @PathVariable ResourceType resourceType) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findByResourceType(resourceType);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/resource-type/{resourceType}")
    public ResponseEntity<List<ResourceRecommendation>> getRecommendationsByFarmAndResourceType(
            @PathVariable String farmId,
            @PathVariable ResourceType resourceType) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findByFarmIdAndResourceType(farmId, resourceType);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/resource-type/{resourceType}/paged")
    public ResponseEntity<Page<ResourceRecommendation>> getRecommendationsByFarmAndResourceTypePaged(
            @PathVariable String farmId,
            @PathVariable ResourceType resourceType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ResourceRecommendation> recommendations =
                resourceRecommendationService.findRecommendationsByResourceType(farmId, resourceType, page, size);
        return ResponseEntity.ok(recommendations);
    }

    // ===== CATEGORY OPERATIONS =====

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ResourceRecommendation>> getRecommendationsByCategory(
            @PathVariable RecommendationCategory category) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findByRecommendationCategory(category);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/category/{category}")
    public ResponseEntity<List<ResourceRecommendation>> getRecommendationsByFarmAndCategory(
            @PathVariable String farmId,
            @PathVariable RecommendationCategory category) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findByFarmIdAndRecommendationCategory(farmId, category);
        return ResponseEntity.ok(recommendations);
    }

    // ===== DATE-BASED OPERATIONS =====

    @GetMapping("/expired")
    public ResponseEntity<List<ResourceRecommendation>> getExpiredRecommendations() {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findExpiredRecommendations();
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/expired")
    public ResponseEntity<List<ResourceRecommendation>> getExpiredRecommendationsByFarm(
            @PathVariable String farmId) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findExpiredRecommendationsByFarmId(farmId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/expiring-soon")
    public ResponseEntity<List<ResourceRecommendation>> getExpiringSoonRecommendations(
            @RequestParam(defaultValue = "7") int daysAhead) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findExpiringSoonRecommendations(daysAhead);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/current-timing")
    public ResponseEntity<List<ResourceRecommendation>> getCurrentTimingWindowRecommendations(
            @PathVariable String farmId) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findCurrentTimingWindowRecommendations(farmId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/recent")
    public ResponseEntity<List<ResourceRecommendation>> getRecentRecommendationsByFarm(
            @PathVariable String farmId,
            @RequestParam(defaultValue = "30") int days) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findRecentRecommendationsByFarmId(farmId, days);
        return ResponseEntity.ok(recommendations);
    }

    // ===== IMPLEMENTATION OPERATIONS =====

    @PutMapping("/{id}/implement")
    public ResponseEntity<ResourceRecommendation> implementRecommendation(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, String> requestBody) {
        try {
            String notes = requestBody != null ? requestBody.get("implementationNotes") : null;
            ResourceRecommendation recommendation =
                    resourceRecommendationService.implementRecommendation(id, notes);
            return ResponseEntity.ok(recommendation);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ResourceRecommendation> rejectRecommendation(@PathVariable String id) {
        try {
            ResourceRecommendation recommendation =
                    resourceRecommendationService.rejectRecommendation(id);
            return ResponseEntity.ok(recommendation);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/supersede")
    public ResponseEntity<ResourceRecommendation> supersedeRecommendation(@PathVariable String id) {
        try {
            ResourceRecommendation recommendation =
                    resourceRecommendationService.supersedeRecommendation(id);
            return ResponseEntity.ok(recommendation);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/effectiveness-rating")
    public ResponseEntity<ResourceRecommendation> addEffectivenessRating(
            @PathVariable String id,
            @RequestBody Map<String, Object> requestBody) {
        try {
            Integer rating = (Integer) requestBody.get("rating");
            String feedback = (String) requestBody.get("feedback");

            ResourceRecommendation recommendation =
                    resourceRecommendationService.addEffectivenessRating(id, rating, feedback);
            return ResponseEntity.ok(recommendation);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/actual-cost")
    public ResponseEntity<ResourceRecommendation> updateActualCost(
            @PathVariable String id,
            @RequestBody Map<String, Object> requestBody) {
        try {
            BigDecimal actualCost = new BigDecimal(requestBody.get("actualCost").toString());
            ResourceRecommendation recommendation =
                    resourceRecommendationService.updateActualCost(id, actualCost);
            return ResponseEntity.ok(recommendation);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/follow-up")
    public ResponseEntity<ResourceRecommendation> scheduleFollowUp(
            @PathVariable String id,
            @RequestBody Map<String, String> requestBody) {
        try {
            LocalDate followUpDate = LocalDate.parse(requestBody.get("followUpDate"));
            ResourceRecommendation recommendation =
                    resourceRecommendationService.scheduleFollowUp(id, followUpDate);
            return ResponseEntity.ok(recommendation);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/complete-follow-up")
    public ResponseEntity<ResourceRecommendation> completeFollowUp(@PathVariable String id) {
        try {
            ResourceRecommendation recommendation =
                    resourceRecommendationService.completeFollowUp(id);
            return ResponseEntity.ok(recommendation);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // ===== FOLLOW-UP OPERATIONS =====

    @GetMapping("/follow-up/due")
    public ResponseEntity<List<ResourceRecommendation>> getFollowUpDue() {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findFollowUpDue();
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/follow-up/due")
    public ResponseEntity<List<ResourceRecommendation>> getFollowUpDueByFarm(
            @PathVariable String farmId) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findFollowUpDueByFarmId(farmId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/follow-up/count")
    public ResponseEntity<Long> getFollowUpDueCountByFarm(@PathVariable String farmId) {
        long count = resourceRecommendationService.countFollowUpDueByFarmId(farmId);
        return ResponseEntity.ok(count);
    }

    // ===== REVIEW OPERATIONS =====

    @GetMapping("/unreviewed")
    public ResponseEntity<List<ResourceRecommendation>> getUnreviewedRecommendations() {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findUnreviewedRecommendations();
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/reviewed")
    public ResponseEntity<List<ResourceRecommendation>> getReviewedRecommendations() {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findReviewedRecommendations();
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/reviewer/{reviewerId}")
    public ResponseEntity<List<ResourceRecommendation>> getRecommendationsByReviewer(
            @PathVariable String reviewerId) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findByReviewedBy(reviewerId);
        return ResponseEntity.ok(recommendations);
    }

    @PutMapping("/{id}/review")
    public ResponseEntity<ResourceRecommendation> reviewRecommendation(
            @PathVariable String id,
            @RequestBody Map<String, String> requestBody) {
        try {
            String reviewerId = requestBody.get("reviewerId");
            ResourceRecommendation recommendation =
                    resourceRecommendationService.reviewRecommendation(id, reviewerId);
            return ResponseEntity.ok(recommendation);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // ===== SEARCH AND FILTERING =====

    @GetMapping("/farm/{farmId}/search")
    public ResponseEntity<Page<ResourceRecommendation>> searchRecommendations(
            @PathVariable String farmId,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "generatedDate"));
        Page<ResourceRecommendation> recommendations =
                resourceRecommendationService.searchRecommendations(farmId, searchTerm, pageable);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/filter")
    public ResponseEntity<Page<ResourceRecommendation>> getRecommendationsWithFilters(
            @PathVariable String farmId,
            @RequestParam(required = false) ResourceType resourceType,
            @RequestParam(required = false) RecommendationCategory category,
            @RequestParam(required = false) PriorityLevel priority,
            @RequestParam(required = false) RecommendationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "priorityLevel", "generatedDate"));
        Page<ResourceRecommendation> recommendations =
                resourceRecommendationService.findRecommendationsWithFilters(
                        farmId, resourceType, category, priority, status, pageable);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/optimal")
    public ResponseEntity<List<ResourceRecommendation>> getOptimalRecommendations(
            @PathVariable String farmId,
            @RequestParam(required = false) BigDecimal minConfidence,
            @RequestParam(required = false) BigDecimal maxCost) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findOptimalRecommendations(farmId, minConfidence, maxCost);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/high-confidence")
    public ResponseEntity<List<ResourceRecommendation>> getHighConfidenceRecommendations(
            @PathVariable String farmId,
            @RequestParam BigDecimal minConfidence) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findHighConfidenceRecommendations(farmId, minConfidence);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/low-cost")
    public ResponseEntity<List<ResourceRecommendation>> getLowCostRecommendations(
            @PathVariable String farmId,
            @RequestParam BigDecimal maxCost) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findLowCostRecommendations(farmId, maxCost);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/high-roi")
    public ResponseEntity<List<ResourceRecommendation>> getHighROIRecommendations(
            @PathVariable String farmId,
            @RequestParam BigDecimal minRoi) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findHighROIRecommendations(farmId, minRoi);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/easy-to-implement")
    public ResponseEntity<List<ResourceRecommendation>> getEasyToImplementRecommendations(
            @PathVariable String farmId) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findEasyToImplementRecommendations(farmId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/farm/{farmId}/sustainable")
    public ResponseEntity<List<ResourceRecommendation>> getSustainableRecommendations(
            @PathVariable String farmId,
            @RequestParam(defaultValue = "7") Integer minSustainabilityScore) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findSustainableRecommendations(farmId, minSustainabilityScore);
        return ResponseEntity.ok(recommendations);
    }

    // ===== STATISTICS AND ANALYTICS =====

    @GetMapping("/farm/{farmId}/stats/resource-type")
    public ResponseEntity<Map<ResourceType, Long>> getResourceTypeStatistics(
            @PathVariable String farmId) {
        Map<ResourceType, Long> stats = resourceRecommendationService.getResourceTypeStatistics(farmId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/farm/{farmId}/stats/priority")
    public ResponseEntity<Map<PriorityLevel, Long>> getPriorityLevelStatistics(
            @PathVariable String farmId) {
        Map<PriorityLevel, Long> stats = resourceRecommendationService.getPriorityLevelStatistics(farmId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/farm/{farmId}/stats/average-confidence")
    public ResponseEntity<BigDecimal> getAverageConfidenceScore(
            @PathVariable String farmId,
            @RequestParam(defaultValue = "ACTIVE") RecommendationStatus status) {
        BigDecimal avgConfidence =
                resourceRecommendationService.getAverageConfidenceScore(farmId, status);
        return ResponseEntity.ok(avgConfidence);
    }

    @GetMapping("/farm/{farmId}/stats/average-effectiveness")
    public ResponseEntity<Double> getAverageEffectivenessRating(@PathVariable String farmId) {
        Double avgEffectiveness = resourceRecommendationService.getAverageEffectivenessRating(farmId);
        return ResponseEntity.ok(avgEffectiveness);
    }

    @GetMapping("/farm/{farmId}/stats/total-estimated-cost")
    public ResponseEntity<BigDecimal> getTotalEstimatedCost(@PathVariable String farmId) {
        BigDecimal totalCost = resourceRecommendationService.getTotalEstimatedCost(farmId);
        return ResponseEntity.ok(totalCost);
    }

    @GetMapping("/farm/{farmId}/stats/total-actual-cost")
    public ResponseEntity<BigDecimal> getTotalActualCost(@PathVariable String farmId) {
        BigDecimal totalCost = resourceRecommendationService.getTotalActualCost(farmId);
        return ResponseEntity.ok(totalCost);
    }

    @GetMapping("/farm/{farmId}/stats/cost-variance")
    public ResponseEntity<BigDecimal> getCostVariance(@PathVariable String farmId) {
        BigDecimal variance = resourceRecommendationService.getCostVariance(farmId);
        return ResponseEntity.ok(variance);
    }

    @GetMapping("/farm/{farmId}/summary")
    public ResponseEntity<Map<String, Object>> getFarmRecommendationSummary(
            @PathVariable String farmId) {
        Map<String, Object> summary = resourceRecommendationService.getFarmRecommendationSummary(farmId);
        return ResponseEntity.ok(summary);
    }

    // ===== MAINTENANCE OPERATIONS =====

    @PostMapping("/maintenance/expire-overdue")
    public ResponseEntity<Map<String, Integer>> expireOverdueRecommendations() {
        int count = resourceRecommendationService.expireOverdueRecommendations();
        return ResponseEntity.ok(Map.of("expiredCount", count));
    }

    @DeleteMapping("/maintenance/cleanup-expired")
    public ResponseEntity<Map<String, Integer>> cleanupExpiredRecommendations(
            @RequestParam(defaultValue = "30") int daysOld) {
        int count = resourceRecommendationService.cleanupExpiredRecommendations(daysOld);
        return ResponseEntity.ok(Map.of("deletedCount", count));
    }

    @DeleteMapping("/maintenance/cleanup-rejected")
    public ResponseEntity<Map<String, Integer>> cleanupOldRejectedRecommendations(
            @RequestParam(defaultValue = "90") int daysOld) {
        int count = resourceRecommendationService.cleanupOldRejectedRecommendations(daysOld);
        return ResponseEntity.ok(Map.of("deletedCount", count));
    }

    // ===== BULK OPERATIONS =====

    @PostMapping("/bulk")
    public ResponseEntity<List<ResourceRecommendation>> createRecommendations(
            @Valid @RequestBody List<ResourceRecommendation> recommendations) {
        try {
            List<ResourceRecommendation> created =
                    resourceRecommendationService.createRecommendations(recommendations);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (ValidationException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/bulk")
    public ResponseEntity<List<ResourceRecommendation>> updateRecommendations(
            @Valid @RequestBody List<ResourceRecommendation> recommendations) {
        try {
            List<ResourceRecommendation> updated =
                    resourceRecommendationService.updateRecommendations(recommendations);
            return ResponseEntity.ok(updated);
        } catch (ValidationException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteRecommendations(
            @RequestBody List<String> ids) {
        try {
            resourceRecommendationService.deleteRecommendations(ids);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ===== AI MODEL OPERATIONS =====

    @GetMapping("/ai-model/{aiModelVersion}")
    public ResponseEntity<List<ResourceRecommendation>> getRecommendationsByAiModel(
            @PathVariable String aiModelVersion) {
        List<ResourceRecommendation> recommendations =
                resourceRecommendationService.findByAiModelVersion(aiModelVersion);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/ai-model/versions")
    public ResponseEntity<List<String>> getAllAiModelVersions() {
        List<String> versions = resourceRecommendationService.getAllAiModelVersions();
        return ResponseEntity.ok(versions);
    }

    // ===== UTILITY ENDPOINTS =====

    @GetMapping("/exists/{id}")
    public ResponseEntity<Map<String, Boolean>> checkRecommendationExists(
            @PathVariable String id) {
        boolean exists = resourceRecommendationService.existsById(id);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/exists/code/{recommendationCode}")
    public ResponseEntity<Map<String, Boolean>> checkRecommendationCodeExists(
            @PathVariable String recommendationCode) {
        boolean exists = resourceRecommendationService.existsByRecommendationCode(recommendationCode);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalRecommendationCount() {
        long count = resourceRecommendationService.count();
        return ResponseEntity.ok(count);
    }

    // ===== EXCEPTION HANDLERS =====

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(
            ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Resource not found", "message", e.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            ValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Validation error", "message", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error", "message", "An unexpected error occurred"));
    }
}