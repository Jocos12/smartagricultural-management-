package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.AIRecommendation;
import SmartAgricultural.Management.Service.AIRecommendationService;
import SmartAgricultural.Management.dto.AIRecommendationDTO;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai-recommendations")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AIRecommendationController {

    private static final Logger logger = LoggerFactory.getLogger(AIRecommendationController.class);

    @Autowired
    private AIRecommendationService recommendationService;

    /**
     * Create a new recommendation
     */
    @PostMapping
    public ResponseEntity<?> createRecommendation(@RequestBody AIRecommendationDTO dto) {
        try {
            AIRecommendationDTO created = recommendationService.createRecommendation(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            logger.error("Error creating recommendation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error creating recommendation: " + e.getMessage()));
        }
    }

    /**
     * Get recommendation by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRecommendationById(@PathVariable String id) {
        try {
            AIRecommendationDTO recommendation = recommendationService.getRecommendationById(id);
            return ResponseEntity.ok(recommendation);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting recommendation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error retrieving recommendation"));
        }
    }

    /**
     * Get all recommendations for a farmer
     */
    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<?> getRecommendationsByFarmer(@PathVariable String farmerId) {
        try {
            List<AIRecommendationDTO> recommendations = recommendationService.getRecommendationsByFarmer(farmerId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            logger.error("Error getting farmer recommendations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error retrieving recommendations"));
        }
    }

    /**
     * Get active recommendations for a farmer
     */
    @GetMapping("/farmer/{farmerId}/active")
    public ResponseEntity<?> getActiveRecommendations(@PathVariable String farmerId) {
        try {
            List<AIRecommendationDTO> recommendations = recommendationService.getActiveRecommendations(farmerId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            logger.error("Error getting active recommendations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error retrieving active recommendations"));
        }
    }

    /**
     * Get unread recommendations
     */
    @GetMapping("/farmer/{farmerId}/unread")
    public ResponseEntity<?> getUnreadRecommendations(@PathVariable String farmerId) {
        try {
            List<AIRecommendationDTO> recommendations = recommendationService.getUnreadRecommendations(farmerId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            logger.error("Error getting unread recommendations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error retrieving unread recommendations"));
        }
    }

    /**
     * Get urgent recommendations
     */
    @GetMapping("/farmer/{farmerId}/urgent")
    public ResponseEntity<?> getUrgentRecommendations(@PathVariable String farmerId) {
        try {
            List<AIRecommendationDTO> recommendations = recommendationService.getUrgentRecommendations(farmerId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            logger.error("Error getting urgent recommendations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error retrieving urgent recommendations"));
        }
    }

    /**
     * Get valid recommendations (within validity period)
     */
    @GetMapping("/farmer/{farmerId}/valid")
    public ResponseEntity<?> getValidRecommendations(@PathVariable String farmerId) {
        try {
            List<AIRecommendationDTO> recommendations = recommendationService.getValidRecommendations(farmerId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            logger.error("Error getting valid recommendations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error retrieving valid recommendations"));
        }
    }

    /**
     * Get recommendations by type
     */
    @GetMapping("/farmer/{farmerId}/type/{type}")
    public ResponseEntity<?> getRecommendationsByType(
            @PathVariable String farmerId,
            @PathVariable String type
    ) {
        try {
            AIRecommendation.RecommendationType recommendationType =
                    AIRecommendation.RecommendationType.valueOf(type.toUpperCase());
            List<AIRecommendationDTO> recommendations =
                    recommendationService.getRecommendationsByType(farmerId, recommendationType);
            return ResponseEntity.ok(recommendations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Invalid recommendation type"));
        } catch (Exception e) {
            logger.error("Error getting recommendations by type", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error retrieving recommendations by type"));
        }
    }

    /**
     * Get recommendations by priority
     */
    @GetMapping("/farmer/{farmerId}/priority/{priority}")
    public ResponseEntity<?> getRecommendationsByPriority(
            @PathVariable String farmerId,
            @PathVariable String priority
    ) {
        try {
            AIRecommendation.Priority priorityLevel =
                    AIRecommendation.Priority.valueOf(priority.toUpperCase());
            List<AIRecommendationDTO> recommendations =
                    recommendationService.getRecommendationsByPriority(farmerId, priorityLevel);
            return ResponseEntity.ok(recommendations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Invalid priority level"));
        } catch (Exception e) {
            logger.error("Error getting recommendations by priority", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error retrieving recommendations by priority"));
        }
    }

    /**
     * Mark recommendation as read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String id) {
        try {
            AIRecommendationDTO updated = recommendationService.markAsRead(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Recommendation marked as read",
                    "data", updated
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error marking recommendation as read", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error updating recommendation"));
        }
    }

    /**
     * Mark recommendation as implemented
     */
    @PutMapping("/{id}/implement")
    public ResponseEntity<?> markAsImplemented(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, String> request
    ) {
        try {
            String notes = request != null ? request.get("notes") : null;
            AIRecommendationDTO updated = recommendationService.markAsImplemented(id, notes);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Recommendation marked as implemented",
                    "data", updated
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error marking recommendation as implemented", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error updating recommendation"));
        }
    }

    /**
     * Rate recommendation effectiveness
     */
    @PutMapping("/{id}/rate")
    public ResponseEntity<?> rateEffectiveness(
            @PathVariable String id,
            @RequestBody Map<String, Object> request
    ) {
        try {
            Integer rating = (Integer) request.get("rating");
            String notes = (String) request.get("notes");

            if (rating == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Rating is required"));
            }

            AIRecommendationDTO updated = recommendationService.rateEffectiveness(id, rating, notes);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Recommendation rated successfully",
                    "data", updated
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error rating recommendation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error rating recommendation"));
        }
    }

    /**
     * Update recommendation
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecommendation(
            @PathVariable String id,
            @RequestBody AIRecommendationDTO dto
    ) {
        try {
            AIRecommendationDTO updated = recommendationService.updateRecommendation(id, dto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Recommendation updated successfully",
                    "data", updated
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating recommendation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error updating recommendation"));
        }
    }

    /**
     * Deactivate recommendation
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateRecommendation(@PathVariable String id) {
        try {
            recommendationService.deactivateRecommendation(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Recommendation deactivated successfully"
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deactivating recommendation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error deactivating recommendation"));
        }
    }

    /**
     * Delete recommendation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecommendation(@PathVariable String id) {
        try {
            recommendationService.deleteRecommendation(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Recommendation deleted successfully"
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting recommendation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error deleting recommendation"));
        }
    }

    /**
     * Get recommendation statistics
     */
    @GetMapping("/farmer/{farmerId}/statistics")
    public ResponseEntity<?> getStatistics(@PathVariable String farmerId) {
        try {
            AIRecommendationService.RecommendationStatistics stats =
                    recommendationService.getStatistics(farmerId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error getting recommendation statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error retrieving statistics"));
        }
    }

    /**
     * Bulk mark as read
     */
    @PutMapping("/farmer/{farmerId}/mark-all-read")
    public ResponseEntity<?> markAllAsRead(@PathVariable String farmerId) {
        try {
            List<AIRecommendationDTO> unread = recommendationService.getUnreadRecommendations(farmerId);
            int count = 0;
            for (AIRecommendationDTO dto : unread) {
                try {
                    recommendationService.markAsRead(dto.getId());
                    count++;
                } catch (Exception e) {
                    logger.warn("Failed to mark recommendation {} as read", dto.getId());
                }
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", count + " recommendations marked as read",
                    "count", count
            ));
        } catch (Exception e) {
            logger.error("Error marking all as read", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error marking recommendations as read"));
        }
    }
}