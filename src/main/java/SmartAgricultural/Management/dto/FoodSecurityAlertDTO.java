package SmartAgricultural.Management.dto;

import SmartAgricultural.Management.Model.FoodSecurityAlert;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class FoodSecurityAlertDTO {

    private String id;

    private String alertCode;

    @NotBlank(message = "Alert title is required")
    @Size(max = 200, message = "Alert title must not exceed 200 characters")
    private String alertTitle;

    @NotNull(message = "Alert category is required")
    private FoodSecurityAlert.AlertCategory alertCategory;

    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @NotNull(message = "Alert level is required")
    private FoodSecurityAlert.AlertLevel alertLevel;

    @Min(value = 1, message = "Severity score must be at least 1")
    @Max(value = 10, message = "Severity score must not exceed 10")
    private Integer severityScore;

    @Size(max = 100, message = "Affected region must not exceed 100 characters")
    private String affectedRegion;

    private List<String> affectedDistricts;

    private List<String> affectedCrops;

    @Min(value = 0, message = "Affected population must be positive")
    private Integer affectedPopulation;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alertDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventEndDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryDate;

    @NotBlank(message = "Source is required")
    @Size(max = 100, message = "Source must not exceed 100 characters")
    private String source;

    private FoodSecurityAlert.SourceReliability sourceReliability;

    private Boolean isActive;

    @Min(value = 1, message = "Escalation level must be at least 1")
    @Max(value = 5, message = "Escalation level must not exceed 5")
    private Integer escalationLevel;

    private Boolean responseRequired;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime responseDeadline;

    @Size(max = 3000, message = "Recommended actions must not exceed 3000 characters")
    private String recommendedActions;

    private List<String> stakeholdersNotified;

    @DecimalMin(value = "0.0", message = "Economic impact must be positive")
    @Digits(integer = 13, fraction = 2, message = "Economic impact format is invalid")
    private BigDecimal economicImpact;

    @Size(max = 3000, message = "Social impact must not exceed 3000 characters")
    private String socialImpact;

    @Size(max = 3000, message = "Environmental impact must not exceed 3000 characters")
    private String environmentalImpact;

    @Size(max = 3000, message = "Mitigation measures must not exceed 3000 characters")
    private String mitigationMeasures;

    private List<String> followUpAlerts;

    private FoodSecurityAlert.ResolutionStatus resolutionStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resolutionDate;

    @Size(max = 3000, message = "Lessons learned must not exceed 3000 characters")
    private String lessonsLearned;

    private Boolean mediaCoverage;

    private Boolean internationalAttention;

    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // Constructors
    public FoodSecurityAlertDTO() {
    }

    public FoodSecurityAlertDTO(String alertTitle, FoodSecurityAlert.AlertCategory alertCategory,
                                String description, FoodSecurityAlert.AlertLevel alertLevel, String source) {
        this.alertTitle = alertTitle;
        this.alertCategory = alertCategory;
        this.description = description;
        this.alertLevel = alertLevel;
        this.source = source;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlertCode() {
        return alertCode;
    }

    public void setAlertCode(String alertCode) {
        this.alertCode = alertCode;
    }

    public String getAlertTitle() {
        return alertTitle;
    }

    public void setAlertTitle(String alertTitle) {
        this.alertTitle = alertTitle;
    }

    public FoodSecurityAlert.AlertCategory getAlertCategory() {
        return alertCategory;
    }

    public void setAlertCategory(FoodSecurityAlert.AlertCategory alertCategory) {
        this.alertCategory = alertCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FoodSecurityAlert.AlertLevel getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(FoodSecurityAlert.AlertLevel alertLevel) {
        this.alertLevel = alertLevel;
    }

    public Integer getSeverityScore() {
        return severityScore;
    }

    public void setSeverityScore(Integer severityScore) {
        this.severityScore = severityScore;
    }

    public String getAffectedRegion() {
        return affectedRegion;
    }

    public void setAffectedRegion(String affectedRegion) {
        this.affectedRegion = affectedRegion;
    }

    public List<String> getAffectedDistricts() {
        return affectedDistricts;
    }

    public void setAffectedDistricts(List<String> affectedDistricts) {
        this.affectedDistricts = affectedDistricts;
    }

    public List<String> getAffectedCrops() {
        return affectedCrops;
    }

    public void setAffectedCrops(List<String> affectedCrops) {
        this.affectedCrops = affectedCrops;
    }

    public Integer getAffectedPopulation() {
        return affectedPopulation;
    }

    public void setAffectedPopulation(Integer affectedPopulation) {
        this.affectedPopulation = affectedPopulation;
    }

    public LocalDateTime getAlertDate() {
        return alertDate;
    }

    public void setAlertDate(LocalDateTime alertDate) {
        this.alertDate = alertDate;
    }

    public LocalDateTime getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(LocalDateTime eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public LocalDateTime getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(LocalDateTime eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public FoodSecurityAlert.SourceReliability getSourceReliability() {
        return sourceReliability;
    }

    public void setSourceReliability(FoodSecurityAlert.SourceReliability sourceReliability) {
        this.sourceReliability = sourceReliability;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getEscalationLevel() {
        return escalationLevel;
    }

    public void setEscalationLevel(Integer escalationLevel) {
        this.escalationLevel = escalationLevel;
    }

    public Boolean getResponseRequired() {
        return responseRequired;
    }

    public void setResponseRequired(Boolean responseRequired) {
        this.responseRequired = responseRequired;
    }

    public LocalDateTime getResponseDeadline() {
        return responseDeadline;
    }

    public void setResponseDeadline(LocalDateTime responseDeadline) {
        this.responseDeadline = responseDeadline;
    }

    public String getRecommendedActions() {
        return recommendedActions;
    }

    public void setRecommendedActions(String recommendedActions) {
        this.recommendedActions = recommendedActions;
    }

    public List<String> getStakeholdersNotified() {
        return stakeholdersNotified;
    }

    public void setStakeholdersNotified(List<String> stakeholdersNotified) {
        this.stakeholdersNotified = stakeholdersNotified;
    }

    public BigDecimal getEconomicImpact() {
        return economicImpact;
    }

    public void setEconomicImpact(BigDecimal economicImpact) {
        this.economicImpact = economicImpact;
    }

    public String getSocialImpact() {
        return socialImpact;
    }

    public void setSocialImpact(String socialImpact) {
        this.socialImpact = socialImpact;
    }

    public String getEnvironmentalImpact() {
        return environmentalImpact;
    }

    public void setEnvironmentalImpact(String environmentalImpact) {
        this.environmentalImpact = environmentalImpact;
    }

    public String getMitigationMeasures() {
        return mitigationMeasures;
    }

    public void setMitigationMeasures(String mitigationMeasures) {
        this.mitigationMeasures = mitigationMeasures;
    }

    public List<String> getFollowUpAlerts() {
        return followUpAlerts;
    }

    public void setFollowUpAlerts(List<String> followUpAlerts) {
        this.followUpAlerts = followUpAlerts;
    }

    public FoodSecurityAlert.ResolutionStatus getResolutionStatus() {
        return resolutionStatus;
    }

    public void setResolutionStatus(FoodSecurityAlert.ResolutionStatus resolutionStatus) {
        this.resolutionStatus = resolutionStatus;
    }

    public LocalDateTime getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(LocalDateTime resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public String getLessonsLearned() {
        return lessonsLearned;
    }

    public void setLessonsLearned(String lessonsLearned) {
        this.lessonsLearned = lessonsLearned;
    }

    public Boolean getMediaCoverage() {
        return mediaCoverage;
    }

    public void setMediaCoverage(Boolean mediaCoverage) {
        this.mediaCoverage = mediaCoverage;
    }

    public Boolean getInternationalAttention() {
        return internationalAttention;
    }

    public void setInternationalAttention(Boolean internationalAttention) {
        this.internationalAttention = internationalAttention;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility methods to convert to/from Entity
    public static FoodSecurityAlertDTO fromEntity(FoodSecurityAlert alert) {
        if (alert == null) {
            return null;
        }

        FoodSecurityAlertDTO dto = new FoodSecurityAlertDTO();
        dto.setId(alert.getId());
        dto.setAlertCode(alert.getAlertCode());
        dto.setAlertTitle(alert.getAlertTitle());
        dto.setAlertCategory(alert.getAlertCategory());
        dto.setDescription(alert.getDescription());
        dto.setAlertLevel(alert.getAlertLevel());
        dto.setSeverityScore(alert.getSeverityScore());
        dto.setAffectedRegion(alert.getAffectedRegion());
        dto.setAffectedDistricts(alert.getAffectedDistrictsList());
        dto.setAffectedCrops(alert.getAffectedCropsList());
        dto.setAffectedPopulation(alert.getAffectedPopulation());
        dto.setAlertDate(alert.getAlertDate());
        dto.setEventStartDate(alert.getEventStartDate());
        dto.setEventEndDate(alert.getEventEndDate());
        dto.setExpiryDate(alert.getExpiryDate());
        dto.setSource(alert.getSource());
        dto.setSourceReliability(alert.getSourceReliability());
        dto.setIsActive(alert.getIsActive());
        dto.setEscalationLevel(alert.getEscalationLevel());
        dto.setResponseRequired(alert.getResponseRequired());
        dto.setResponseDeadline(alert.getResponseDeadline());
        dto.setRecommendedActions(alert.getRecommendedActions());
        dto.setStakeholdersNotified(alert.getStakeholdersNotifiedList());
        dto.setEconomicImpact(alert.getEconomicImpact());
        dto.setSocialImpact(alert.getSocialImpact());
        dto.setEnvironmentalImpact(alert.getEnvironmentalImpact());
        dto.setMitigationMeasures(alert.getMitigationMeasures());
        dto.setFollowUpAlerts(alert.getFollowUpAlertsList());
        dto.setResolutionStatus(alert.getResolutionStatus());
        dto.setResolutionDate(alert.getResolutionDate());
        dto.setLessonsLearned(alert.getLessonsLearned());
        dto.setMediaCoverage(alert.getMediaCoverage());
        dto.setInternationalAttention(alert.getInternationalAttention());
        dto.setCreatedBy(alert.getCreatedBy());
        dto.setCreatedAt(alert.getCreatedAt());
        dto.setUpdatedAt(alert.getUpdatedAt());

        return dto;
    }

    public FoodSecurityAlert toEntity() {
        FoodSecurityAlert alert = new FoodSecurityAlert();

        if (this.id != null) {
            alert.setId(this.id);
        }
        if (this.alertCode != null) {
            alert.setAlertCode(this.alertCode);
        }

        alert.setAlertTitle(this.alertTitle);
        alert.setAlertCategory(this.alertCategory);
        alert.setDescription(this.description);
        alert.setAlertLevel(this.alertLevel);
        alert.setSeverityScore(this.severityScore);
        alert.setAffectedRegion(this.affectedRegion);

        if (this.affectedDistricts != null) {
            alert.setAffectedDistrictsList(this.affectedDistricts);
        }
        if (this.affectedCrops != null) {
            alert.setAffectedCropsList(this.affectedCrops);
        }

        alert.setAffectedPopulation(this.affectedPopulation);
        alert.setAlertDate(this.alertDate);
        alert.setEventStartDate(this.eventStartDate);
        alert.setEventEndDate(this.eventEndDate);
        alert.setExpiryDate(this.expiryDate);
        alert.setSource(this.source);
        alert.setSourceReliability(this.sourceReliability);
        alert.setIsActive(this.isActive);
        alert.setEscalationLevel(this.escalationLevel);
        alert.setResponseRequired(this.responseRequired);
        alert.setResponseDeadline(this.responseDeadline);
        alert.setRecommendedActions(this.recommendedActions);

        if (this.stakeholdersNotified != null) {
            alert.setStakeholdersNotifiedList(this.stakeholdersNotified);
        }

        alert.setEconomicImpact(this.economicImpact);
        alert.setSocialImpact(this.socialImpact);
        alert.setEnvironmentalImpact(this.environmentalImpact);
        alert.setMitigationMeasures(this.mitigationMeasures);

        if (this.followUpAlerts != null) {
            alert.setFollowUpAlertsList(this.followUpAlerts);
        }

        alert.setResolutionStatus(this.resolutionStatus);
        alert.setResolutionDate(this.resolutionDate);
        alert.setLessonsLearned(this.lessonsLearned);
        alert.setMediaCoverage(this.mediaCoverage);
        alert.setInternationalAttention(this.internationalAttention);
        alert.setCreatedBy(this.createdBy);

        if (this.createdAt != null) {
            alert.setCreatedAt(this.createdAt);
        }
        if (this.updatedAt != null) {
            alert.setUpdatedAt(this.updatedAt);
        }

        return alert;
    }

    public void updateEntity(FoodSecurityAlert alert) {
        if (alert == null) {
            return;
        }

        if (this.alertTitle != null) {
            alert.setAlertTitle(this.alertTitle);
        }
        if (this.alertCategory != null) {
            alert.setAlertCategory(this.alertCategory);
        }
        if (this.description != null) {
            alert.setDescription(this.description);
        }
        if (this.alertLevel != null) {
            alert.setAlertLevel(this.alertLevel);
        }
        if (this.severityScore != null) {
            alert.setSeverityScore(this.severityScore);
        }
        if (this.affectedRegion != null) {
            alert.setAffectedRegion(this.affectedRegion);
        }
        if (this.affectedDistricts != null) {
            alert.setAffectedDistrictsList(this.affectedDistricts);
        }
        if (this.affectedCrops != null) {
            alert.setAffectedCropsList(this.affectedCrops);
        }
        if (this.affectedPopulation != null) {
            alert.setAffectedPopulation(this.affectedPopulation);
        }
        if (this.alertDate != null) {
            alert.setAlertDate(this.alertDate);
        }
        if (this.eventStartDate != null) {
            alert.setEventStartDate(this.eventStartDate);
        }
        if (this.eventEndDate != null) {
            alert.setEventEndDate(this.eventEndDate);
        }
        if (this.expiryDate != null) {
            alert.setExpiryDate(this.expiryDate);
        }
        if (this.source != null) {
            alert.setSource(this.source);
        }
        if (this.sourceReliability != null) {
            alert.setSourceReliability(this.sourceReliability);
        }
        if (this.isActive != null) {
            alert.setIsActive(this.isActive);
        }
        if (this.escalationLevel != null) {
            alert.setEscalationLevel(this.escalationLevel);
        }
        if (this.responseRequired != null) {
            alert.setResponseRequired(this.responseRequired);
        }
        if (this.responseDeadline != null) {
            alert.setResponseDeadline(this.responseDeadline);
        }
        if (this.recommendedActions != null) {
            alert.setRecommendedActions(this.recommendedActions);
        }
        if (this.stakeholdersNotified != null) {
            alert.setStakeholdersNotifiedList(this.stakeholdersNotified);
        }
        if (this.economicImpact != null) {
            alert.setEconomicImpact(this.economicImpact);
        }
        if (this.socialImpact != null) {
            alert.setSocialImpact(this.socialImpact);
        }
        if (this.environmentalImpact != null) {
            alert.setEnvironmentalImpact(this.environmentalImpact);
        }
        if (this.mitigationMeasures != null) {
            alert.setMitigationMeasures(this.mitigationMeasures);
        }
        if (this.followUpAlerts != null) {
            alert.setFollowUpAlertsList(this.followUpAlerts);
        }
        if (this.resolutionStatus != null) {
            alert.setResolutionStatus(this.resolutionStatus);
        }
        if (this.resolutionDate != null) {
            alert.setResolutionDate(this.resolutionDate);
        }
        if (this.lessonsLearned != null) {
            alert.setLessonsLearned(this.lessonsLearned);
        }
        if (this.mediaCoverage != null) {
            alert.setMediaCoverage(this.mediaCoverage);
        }
        if (this.internationalAttention != null) {
            alert.setInternationalAttention(this.internationalAttention);
        }
        if (this.createdBy != null) {
            alert.setCreatedBy(this.createdBy);
        }
    }

    @Override
    public String toString() {
        return "FoodSecurityAlertDTO{" +
                "id='" + id + '\'' +
                ", alertCode='" + alertCode + '\'' +
                ", alertTitle='" + alertTitle + '\'' +
                ", alertCategory=" + alertCategory +
                ", alertLevel=" + alertLevel +
                ", affectedRegion='" + affectedRegion + '\'' +
                ", source='" + source + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}