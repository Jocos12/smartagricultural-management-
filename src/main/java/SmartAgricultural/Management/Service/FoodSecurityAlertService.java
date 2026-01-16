package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.FoodSecurityAlert;
import SmartAgricultural.Management.Repository.FoodSecurityAlertRepository;
import SmartAgricultural.Management.dto.FoodSecurityAlertDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FoodSecurityAlertService {

    @Autowired
    private FoodSecurityAlertRepository alertRepository;

    // Create new alert
    public FoodSecurityAlert createAlert(FoodSecurityAlertDTO dto) {
        FoodSecurityAlert alert = new FoodSecurityAlert();
        mapDtoToEntity(dto, alert);
        return alertRepository.save(alert);
    }

    // Get all alerts
    public Page<FoodSecurityAlert> getAllAlerts(Pageable pageable) {
        return alertRepository.findAll(pageable);
    }

    // Get alert by ID
    public FoodSecurityAlert getAlertById(String id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found with id: " + id));
    }

    // Get alert by code
    public FoodSecurityAlert getAlertByCode(String alertCode) {
        return alertRepository.findByAlertCode(alertCode)
                .orElseThrow(() -> new RuntimeException("Alert not found with code: " + alertCode));
    }

    // Update alert
    public FoodSecurityAlert updateAlert(String id, FoodSecurityAlertDTO dto) {
        FoodSecurityAlert alert = getAlertById(id);
        mapDtoToEntity(dto, alert);
        return alertRepository.save(alert);
    }

    // Delete alert
    public void deleteAlert(String id) {
        FoodSecurityAlert alert = getAlertById(id);
        alertRepository.delete(alert);
    }

    // Get active alerts
    public Page<FoodSecurityAlert> getActiveAlerts(Pageable pageable) {
        return alertRepository.findByIsActiveTrueAndExpiryDateAfter(
                LocalDateTime.now(), pageable);
    }

    // Get alerts by category
    public Page<FoodSecurityAlert> getAlertsByCategory(
            FoodSecurityAlert.AlertCategory category, Pageable pageable) {
        return alertRepository.findByAlertCategory(category, pageable);
    }

    // Get alerts by level
    public Page<FoodSecurityAlert> getAlertsByLevel(
            FoodSecurityAlert.AlertLevel level, Pageable pageable) {
        return alertRepository.findByAlertLevel(level, pageable);
    }

    // Get critical alerts
    public List<FoodSecurityAlert> getCriticalAlerts() {
        return alertRepository.findByAlertLevelAndIsActiveTrue(
                FoodSecurityAlert.AlertLevel.CRITICAL);
    }

    // Get alerts by region
    public Page<FoodSecurityAlert> getAlertsByRegion(String region, Pageable pageable) {
        return alertRepository.findByAffectedRegionContainingIgnoreCase(region, pageable);
    }

    // Get alerts by date range
    public Page<FoodSecurityAlert> getAlertsByDateRange(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return alertRepository.findByAlertDateBetween(startDate, endDate, pageable);
    }

    // Get unresolved alerts
    public Page<FoodSecurityAlert> getUnresolvedAlerts(Pageable pageable) {
        return alertRepository.findByResolutionStatusNot(
                FoodSecurityAlert.ResolutionStatus.RESOLVED, pageable);
    }

    // Get overdue alerts
    public List<FoodSecurityAlert> getOverdueAlerts() {
        return alertRepository.findByResponseRequiredTrueAndResponseDeadlineBefore(
                LocalDateTime.now());
    }

    // Escalate alert
    public FoodSecurityAlert escalateAlert(String id) {
        FoodSecurityAlert alert = getAlertById(id);
        if (!alert.canEscalate()) {
            throw new RuntimeException("Alert cannot be escalated");
        }
        alert.escalate();
        return alertRepository.save(alert);
    }

    // Mark as in progress
    public FoodSecurityAlert markAsInProgress(String id) {
        FoodSecurityAlert alert = getAlertById(id);
        alert.markInProgress();
        return alertRepository.save(alert);
    }

    // Resolve alert
    public FoodSecurityAlert resolveAlert(String id, String lessonsLearned) {
        FoodSecurityAlert alert = getAlertById(id);
        if (!alert.canResolve()) {
            throw new RuntimeException("Alert cannot be resolved");
        }
        alert.markResolved();
        if (lessonsLearned != null && !lessonsLearned.trim().isEmpty()) {
            alert.setLessonsLearned(lessonsLearned);
        }
        return alertRepository.save(alert);
    }

    // Deactivate alert
    public FoodSecurityAlert deactivateAlert(String id) {
        FoodSecurityAlert alert = getAlertById(id);
        if (!alert.canDeactivate()) {
            throw new RuntimeException("Alert cannot be deactivated");
        }
        alert.deactivate();
        return alertRepository.save(alert);
    }

    // Reactivate alert
    public FoodSecurityAlert reactivateAlert(String id) {
        FoodSecurityAlert alert = getAlertById(id);
        alert.reactivate();
        return alertRepository.save(alert);
    }

    // Extend expiry
    public FoodSecurityAlert extendExpiry(String id, int days) {
        FoodSecurityAlert alert = getAlertById(id);
        if (!alert.canExtend()) {
            throw new RuntimeException("Alert expiry cannot be extended");
        }
        alert.extendExpiry(days);
        return alertRepository.save(alert);
    }

    // Update response deadline
    public FoodSecurityAlert updateResponseDeadline(String id, LocalDateTime deadline) {
        FoodSecurityAlert alert = getAlertById(id);
        alert.updateResponseDeadline(deadline);
        return alertRepository.save(alert);
    }

    // Add stakeholder
    public FoodSecurityAlert addStakeholder(String id, String stakeholder) {
        FoodSecurityAlert alert = getAlertById(id);
        alert.addStakeholder(stakeholder);
        return alertRepository.save(alert);
    }

    // Add follow-up alert
    public FoodSecurityAlert addFollowUpAlert(String id, String followUpAlertId) {
        FoodSecurityAlert alert = getAlertById(id);

        // Verify follow-up alert exists
        if (!alertRepository.existsById(followUpAlertId)) {
            throw new RuntimeException("Follow-up alert not found: " + followUpAlertId);
        }

        alert.addFollowUpAlert(followUpAlertId);
        return alertRepository.save(alert);
    }

    // Get alert statistics
    public Map<String, Object> getAlertStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long totalAlerts = alertRepository.count();
        long activeAlerts = alertRepository.countByIsActiveTrue();
        long criticalAlerts = alertRepository.countByAlertLevelAndIsActiveTrue(
                FoodSecurityAlert.AlertLevel.CRITICAL);
        long unresolvedAlerts = alertRepository.countByResolutionStatusNot(
                FoodSecurityAlert.ResolutionStatus.RESOLVED);
        long overdueAlerts = alertRepository.countByResponseRequiredTrueAndResponseDeadlineBefore(
                LocalDateTime.now());

        stats.put("totalAlerts", totalAlerts);
        stats.put("activeAlerts", activeAlerts);
        stats.put("criticalAlerts", criticalAlerts);
        stats.put("unresolvedAlerts", unresolvedAlerts);
        stats.put("overdueAlerts", overdueAlerts);

        // Alerts by category
        Map<String, Long> byCategory = new HashMap<>();
        for (FoodSecurityAlert.AlertCategory category : FoodSecurityAlert.AlertCategory.values()) {
            long count = alertRepository.countByAlertCategory(category);
            byCategory.put(category.name(), count);
        }
        stats.put("alertsByCategory", byCategory);

        // Alerts by level
        Map<String, Long> byLevel = new HashMap<>();
        for (FoodSecurityAlert.AlertLevel level : FoodSecurityAlert.AlertLevel.values()) {
            long count = alertRepository.countByAlertLevel(level);
            byLevel.put(level.name(), count);
        }
        stats.put("alertsByLevel", byLevel);

        // Alerts by resolution status
        Map<String, Long> byStatus = new HashMap<>();
        for (FoodSecurityAlert.ResolutionStatus status : FoodSecurityAlert.ResolutionStatus.values()) {
            long count = alertRepository.countByResolutionStatus(status);
            byStatus.put(status.name(), count);
        }
        stats.put("alertsByStatus", byStatus);

        return stats;
    }

    // Get alerts by severity range
    public Page<FoodSecurityAlert> getAlertsBySeverityRange(
            int minScore, int maxScore, Pageable pageable) {
        return alertRepository.findBySeverityScoreBetween(minScore, maxScore, pageable);
    }

    // Search alerts
    public Page<FoodSecurityAlert> searchAlerts(String keyword, Pageable pageable) {
        return alertRepository.findByAlertTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                keyword, keyword, pageable);
    }

    // Get alerts by creator
    public Page<FoodSecurityAlert> getAlertsByCreator(String userId, Pageable pageable) {
        return alertRepository.findByCreatedBy(userId, pageable);
    }

    // Get alerts with high population impact
    public Page<FoodSecurityAlert> getAlertsWithHighPopulationImpact(
            int threshold, Pageable pageable) {
        return alertRepository.findByAffectedPopulationGreaterThan(threshold, pageable);
    }

    // Get alerts with media attention
    public Page<FoodSecurityAlert> getAlertsWithMediaAttention(Pageable pageable) {
        return alertRepository.findByMediaCoverageTrueOrInternationalAttentionTrue(pageable);
    }

    // Get recent alerts
    public List<FoodSecurityAlert> getRecentAlerts(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return alertRepository.findByAlertDateAfterOrderByAlertDateDesc(startDate);
    }

    // Helper method to map DTO to entity
    private void mapDtoToEntity(FoodSecurityAlertDTO dto, FoodSecurityAlert alert) {
        alert.setAlertTitle(dto.getAlertTitle());
        alert.setAlertCategory(dto.getAlertCategory());
        alert.setDescription(dto.getDescription());
        alert.setAlertLevel(dto.getAlertLevel());
        alert.setSource(dto.getSource());

        if (dto.getSeverityScore() != null) {
            alert.setSeverityScore(dto.getSeverityScore());
        }

        if (dto.getAffectedRegion() != null) {
            alert.setAffectedRegion(dto.getAffectedRegion());
        }

        if (dto.getAffectedDistricts() != null && !dto.getAffectedDistricts().isEmpty()) {
            alert.setAffectedDistrictsList(dto.getAffectedDistricts());
        }

        if (dto.getAffectedCrops() != null && !dto.getAffectedCrops().isEmpty()) {
            alert.setAffectedCropsList(dto.getAffectedCrops());
        }

        if (dto.getAffectedPopulation() != null) {
            alert.setAffectedPopulation(dto.getAffectedPopulation());
        }

        if (dto.getEventStartDate() != null) {
            alert.setEventStartDate(dto.getEventStartDate());
        }

        if (dto.getEventEndDate() != null) {
            alert.setEventEndDate(dto.getEventEndDate());
        }

        if (dto.getExpiryDate() != null) {
            alert.setExpiryDate(dto.getExpiryDate());
        }

        if (dto.getSourceReliability() != null) {
            alert.setSourceReliability(dto.getSourceReliability());
        }

        if (dto.getEscalationLevel() != null) {
            alert.setEscalationLevel(dto.getEscalationLevel());
        }

        if (dto.getResponseRequired() != null) {
            alert.setResponseRequired(dto.getResponseRequired());
        }

        if (dto.getResponseDeadline() != null) {
            alert.setResponseDeadline(dto.getResponseDeadline());
        }

        if (dto.getRecommendedActions() != null) {
            alert.setRecommendedActions(dto.getRecommendedActions());
        }

        if (dto.getStakeholdersNotified() != null && !dto.getStakeholdersNotified().isEmpty()) {
            alert.setStakeholdersNotifiedList(dto.getStakeholdersNotified());
        }

        if (dto.getEconomicImpact() != null) {
            alert.setEconomicImpact(dto.getEconomicImpact());
        }

        if (dto.getSocialImpact() != null) {
            alert.setSocialImpact(dto.getSocialImpact());
        }

        if (dto.getEnvironmentalImpact() != null) {
            alert.setEnvironmentalImpact(dto.getEnvironmentalImpact());
        }

        if (dto.getMitigationMeasures() != null) {
            alert.setMitigationMeasures(dto.getMitigationMeasures());
        }

        if (dto.getMediaCoverage() != null) {
            alert.setMediaCoverage(dto.getMediaCoverage());
        }

        if (dto.getInternationalAttention() != null) {
            alert.setInternationalAttention(dto.getInternationalAttention());
        }

        if (dto.getCreatedBy() != null) {
            alert.setCreatedBy(dto.getCreatedBy());
        }
    }
}