package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.AIRecommendation;
import SmartAgricultural.Management.Repository.AIRecommendationRepository;
import SmartAgricultural.Management.dto.AIRecommendationDTO;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AIRecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(AIRecommendationService.class);

    @Autowired
    private AIRecommendationRepository recommendationRepository;

    /**
     * Create a new recommendation
     */
    public AIRecommendationDTO createRecommendation(AIRecommendationDTO dto) {
        try {
            AIRecommendation recommendation = new AIRecommendation();
            recommendation.setFarmerId(dto.getFarmerId());
            recommendation.setFarmId(dto.getFarmId());
            recommendation.setCropProductionId(dto.getCropProductionId());
            recommendation.setRecommendationType(dto.getRecommendationType());
            recommendation.setTitle(dto.getTitle());
            recommendation.setDescription(dto.getDescription());
            recommendation.setActionItems(dto.getActionItems());
            recommendation.setPriority(dto.getPriority());
            recommendation.setConfidenceScore(dto.getConfidenceScore());
            recommendation.setGeneratedBy(dto.getGeneratedBy() != null ? dto.getGeneratedBy() : "AI System v1.0");
            recommendation.setIsRead(false);
            recommendation.setIsImplemented(false);
            recommendation.setValidFrom(dto.getValidFrom() != null ? dto.getValidFrom() : LocalDateTime.now());
            recommendation.setValidUntil(dto.getValidUntil());
            recommendation.setIsActive(true);

            AIRecommendation saved = recommendationRepository.save(recommendation);
            logger.info("Created AI recommendation: {}", saved.getId());

            return convertToDTO(saved);
        } catch (Exception e) {
            logger.error("Error creating AI recommendation", e);
            throw new RuntimeException("Failed to create recommendation: " + e.getMessage());
        }
    }

    /**
     * Get recommendation by ID
     */
    public AIRecommendationDTO getRecommendationById(String id) {
        AIRecommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found with id: " + id));
        return convertToDTO(recommendation);
    }

    /**
     * Get all recommendations for a farmer
     */
    public List<AIRecommendationDTO> getRecommendationsByFarmer(String farmerId) {
        List<AIRecommendation> recommendations = recommendationRepository
                .findByFarmerIdOrderByCreatedAtDesc(farmerId);
        return recommendations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get active recommendations for a farmer
     */
    public List<AIRecommendationDTO> getActiveRecommendations(String farmerId) {
        List<AIRecommendation> recommendations = recommendationRepository
                .findByFarmerIdAndIsActiveOrderByCreatedAtDesc(farmerId, true);
        return recommendations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get unread recommendations
     */
    public List<AIRecommendationDTO> getUnreadRecommendations(String farmerId) {
        List<AIRecommendation> recommendations = recommendationRepository
                .findByFarmerIdAndIsReadFalseOrderByCreatedAtDesc(farmerId);
        return recommendations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get urgent recommendations
     */
    public List<AIRecommendationDTO> getUrgentRecommendations(String farmerId) {
        List<AIRecommendation> recommendations = recommendationRepository
                .findUrgentRecommendations(farmerId);
        return recommendations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get valid recommendations (within validity period)
     */
    public List<AIRecommendationDTO> getValidRecommendations(String farmerId) {
        List<AIRecommendation> recommendations = recommendationRepository
                .findValidRecommendations(farmerId, LocalDateTime.now());
        return recommendations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get recommendations by type
     */
    public List<AIRecommendationDTO> getRecommendationsByType(
            String farmerId,
            AIRecommendation.RecommendationType type
    ) {
        List<AIRecommendation> recommendations = recommendationRepository
                .findByFarmerIdAndRecommendationTypeOrderByCreatedAtDesc(farmerId, type);
        return recommendations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get recommendations by priority
     */
    public List<AIRecommendationDTO> getRecommendationsByPriority(
            String farmerId,
            AIRecommendation.Priority priority
    ) {
        List<AIRecommendation> recommendations = recommendationRepository
                .findByFarmerIdAndPriorityOrderByCreatedAtDesc(farmerId, priority);
        return recommendations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mark recommendation as read
     */
    public AIRecommendationDTO markAsRead(String id) {
        AIRecommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found with id: " + id));

        if (!recommendation.getIsRead()) {
            recommendation.setIsRead(true);
            recommendation.setReadAt(LocalDateTime.now());
            AIRecommendation updated = recommendationRepository.save(recommendation);
            logger.info("Marked recommendation as read: {}", id);
            return convertToDTO(updated);
        }

        return convertToDTO(recommendation);
    }

    /**
     * Mark recommendation as implemented
     */
    public AIRecommendationDTO markAsImplemented(String id, String implementationNotes) {
        AIRecommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found with id: " + id));

        recommendation.setIsImplemented(true);
        recommendation.setImplementationDate(LocalDateTime.now());
        recommendation.setImplementationNotes(implementationNotes);

        AIRecommendation updated = recommendationRepository.save(recommendation);
        logger.info("Marked recommendation as implemented: {}", id);

        return convertToDTO(updated);
    }

    /**
     * Rate recommendation effectiveness
     */
    public AIRecommendationDTO rateEffectiveness(String id, Integer rating, String notes) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        AIRecommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found with id: " + id));

        recommendation.setEffectivenessRating(rating);
        recommendation.setEffectivenessNotes(notes);

        AIRecommendation updated = recommendationRepository.save(recommendation);
        logger.info("Rated recommendation effectiveness: {} - Rating: {}", id, rating);

        return convertToDTO(updated);
    }

    /**
     * Update recommendation
     */
    public AIRecommendationDTO updateRecommendation(String id, AIRecommendationDTO dto) {
        AIRecommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found with id: " + id));

        if (dto.getTitle() != null) {
            recommendation.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            recommendation.setDescription(dto.getDescription());
        }
        if (dto.getActionItems() != null) {
            recommendation.setActionItems(dto.getActionItems());
        }
        if (dto.getPriority() != null) {
            recommendation.setPriority(dto.getPriority());
        }
        if (dto.getConfidenceScore() != null) {
            recommendation.setConfidenceScore(dto.getConfidenceScore());
        }
        if (dto.getValidUntil() != null) {
            recommendation.setValidUntil(dto.getValidUntil());
        }
        if (dto.getIsActive() != null) {
            recommendation.setIsActive(dto.getIsActive());
        }

        AIRecommendation updated = recommendationRepository.save(recommendation);
        logger.info("Updated AI recommendation: {}", id);

        return convertToDTO(updated);
    }

    /**
     * Deactivate recommendation
     */
    public void deactivateRecommendation(String id) {
        AIRecommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found with id: " + id));

        recommendation.setIsActive(false);
        recommendationRepository.save(recommendation);
        logger.info("Deactivated recommendation: {}", id);
    }

    /**
     * Delete recommendation
     */
    public void deleteRecommendation(String id) {
        if (!recommendationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recommendation not found with id: " + id);
        }
        recommendationRepository.deleteById(id);
        logger.info("Deleted recommendation: {}", id);
    }

    /**
     * Get recommendation statistics for farmer
     */
    public RecommendationStatistics getStatistics(String farmerId) {
        List<AIRecommendation> allRecommendations = recommendationRepository
                .findByFarmerIdOrderByCreatedAtDesc(farmerId);

        long totalCount = allRecommendations.size();
        long unreadCount = recommendationRepository.countByFarmerIdAndIsReadFalse(farmerId);
        long urgentCount = recommendationRepository.countUrgentRecommendations(farmerId);
        long implementedCount = allRecommendations.stream()
                .filter(AIRecommendation::getIsImplemented)
                .count();

        Double avgEffectiveness = recommendationRepository.getAverageEffectivenessRating(farmerId);
        Double implementationRate = recommendationRepository.getImplementationRate(farmerId);

        return new RecommendationStatistics(
                totalCount,
                unreadCount,
                urgentCount,
                implementedCount,
                avgEffectiveness != null ? avgEffectiveness : 0.0,
                implementationRate != null ? implementationRate : 0.0
        );
    }

    /**
     * Convert entity to DTO
     */
    private AIRecommendationDTO convertToDTO(AIRecommendation recommendation) {
        AIRecommendationDTO dto = new AIRecommendationDTO();
        dto.setId(recommendation.getId());
        dto.setFarmerId(recommendation.getFarmerId());
        dto.setFarmId(recommendation.getFarmId());
        dto.setCropProductionId(recommendation.getCropProductionId());
        dto.setRecommendationType(recommendation.getRecommendationType());
        dto.setTitle(recommendation.getTitle());
        dto.setDescription(recommendation.getDescription());
        dto.setActionItems(recommendation.getActionItems());
        dto.setPriority(recommendation.getPriority());
        dto.setConfidenceScore(recommendation.getConfidenceScore());
        dto.setGeneratedBy(recommendation.getGeneratedBy());
        dto.setIsRead(recommendation.getIsRead());
        dto.setIsImplemented(recommendation.getIsImplemented());
        dto.setImplementationDate(recommendation.getImplementationDate());
        dto.setImplementationNotes(recommendation.getImplementationNotes());
        dto.setEffectivenessRating(recommendation.getEffectivenessRating());
        dto.setEffectivenessNotes(recommendation.getEffectivenessNotes());
        dto.setValidFrom(recommendation.getValidFrom());
        dto.setValidUntil(recommendation.getValidUntil());
        dto.setIsActive(recommendation.getIsActive());
        dto.setCreatedAt(recommendation.getCreatedAt());
        dto.setUpdatedAt(recommendation.getUpdatedAt());
        dto.setReadAt(recommendation.getReadAt());

        // Calculate if expired
        if (recommendation.getValidUntil() != null) {
            LocalDateTime now = LocalDateTime.now();
            dto.setIsExpired(now.isAfter(recommendation.getValidUntil()));
            if (!dto.getIsExpired()) {
                long daysUntil = ChronoUnit.DAYS.between(now, recommendation.getValidUntil());
                dto.setDaysUntilExpiry((int) daysUntil);
            }
        }

        return dto;
    }

    /**
     * Statistics class
     */
    public static class RecommendationStatistics {
        private Long totalRecommendations;
        private Long unreadRecommendations;
        private Long urgentRecommendations;
        private Long implementedRecommendations;
        private Double averageEffectivenessRating;
        private Double implementationRate;

        public RecommendationStatistics() {
        }

        public RecommendationStatistics(Long totalRecommendations, Long unreadRecommendations,
                                        Long urgentRecommendations, Long implementedRecommendations,
                                        Double averageEffectivenessRating, Double implementationRate) {
            this.totalRecommendations = totalRecommendations;
            this.unreadRecommendations = unreadRecommendations;
            this.urgentRecommendations = urgentRecommendations;
            this.implementedRecommendations = implementedRecommendations;
            this.averageEffectivenessRating = averageEffectivenessRating;
            this.implementationRate = implementationRate;
        }

        public Long getTotalRecommendations() {
            return totalRecommendations;
        }

        public void setTotalRecommendations(Long totalRecommendations) {
            this.totalRecommendations = totalRecommendations;
        }

        public Long getUnreadRecommendations() {
            return unreadRecommendations;
        }

        public void setUnreadRecommendations(Long unreadRecommendations) {
            this.unreadRecommendations = unreadRecommendations;
        }

        public Long getUrgentRecommendations() {
            return urgentRecommendations;
        }

        public void setUrgentRecommendations(Long urgentRecommendations) {
            this.urgentRecommendations = urgentRecommendations;
        }

        public Long getImplementedRecommendations() {
            return implementedRecommendations;
        }

        public void setImplementedRecommendations(Long implementedRecommendations) {
            this.implementedRecommendations = implementedRecommendations;
        }

        public Double getAverageEffectivenessRating() {
            return averageEffectivenessRating;
        }

        public void setAverageEffectivenessRating(Double averageEffectivenessRating) {
            this.averageEffectivenessRating = averageEffectivenessRating;
        }

        public Double getImplementationRate() {
            return implementationRate;
        }

        public void setImplementationRate(Double implementationRate) {
            this.implementationRate = implementationRate;
        }
    }
}