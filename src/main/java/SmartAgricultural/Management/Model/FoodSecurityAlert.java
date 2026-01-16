package SmartAgricultural.Management.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Entity
@Table(name = "food_security_alerts")
public class FoodSecurityAlert {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "alert_code", length = 20, unique = true, nullable = false)
    @NotBlank(message = "Alert code is required")
    @Size(max = 20, message = "Alert code must not exceed 20 characters")
    private String alertCode;

    @Column(name = "alert_title", length = 200, nullable = false)
    @NotBlank(message = "Alert title is required")
    @Size(max = 200, message = "Alert title must not exceed 200 characters")
    private String alertTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_category", nullable = false)
    @NotNull(message = "Alert category is required")
    private AlertCategory alertCategory;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_level", nullable = false)
    @NotNull(message = "Alert level is required")
    private AlertLevel alertLevel;

    @Column(name = "severity_score")
    @Min(value = 1, message = "Severity score must be at least 1")
    @Max(value = 10, message = "Severity score must not exceed 10")
    private Integer severityScore;

    @Column(name = "affected_region", length = 100)
    @Size(max = 100, message = "Affected region must not exceed 100 characters")
    private String affectedRegion;

    @Column(name = "affected_districts", columnDefinition = "TEXT")
    private String affectedDistricts; // JSON array

    @Column(name = "affected_crops", columnDefinition = "TEXT")
    private String affectedCrops; // JSON array

    @Column(name = "affected_population")
    @Min(value = 0, message = "Affected population must be positive")
    private Integer affectedPopulation;

    @Column(name = "alert_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alertDate;

    @Column(name = "event_start_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventStartDate;

    @Column(name = "event_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventEndDate;

    @Column(name = "expiry_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryDate;

    @Column(name = "source", length = 100, nullable = false)
    @NotBlank(message = "Source is required")
    @Size(max = 100, message = "Source must not exceed 100 characters")
    private String source;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_reliability")
    private SourceReliability sourceReliability = SourceReliability.UNVERIFIED;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "escalation_level")
    @Min(value = 1, message = "Escalation level must be at least 1")
    @Max(value = 5, message = "Escalation level must not exceed 5")
    private Integer escalationLevel = 1;

    @Column(name = "response_required")
    private Boolean responseRequired = false;

    @Column(name = "response_deadline")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime responseDeadline;

    @Column(name = "recommended_actions", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Recommended actions must not exceed 3000 characters")
    private String recommendedActions;

    @Column(name = "stakeholders_notified", columnDefinition = "TEXT")
    private String stakeholdersNotified; // JSON array

    @Column(name = "economic_impact", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", message = "Economic impact must be positive")
    @Digits(integer = 13, fraction = 2, message = "Economic impact format is invalid")
    private BigDecimal economicImpact;

    @Column(name = "social_impact", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Social impact must not exceed 3000 characters")
    private String socialImpact;

    @Column(name = "environmental_impact", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Environmental impact must not exceed 3000 characters")
    private String environmentalImpact;

    @Column(name = "mitigation_measures", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Mitigation measures must not exceed 3000 characters")
    private String mitigationMeasures;

    @Column(name = "follow_up_alerts", columnDefinition = "TEXT")
    private String followUpAlerts; // JSON array

    @Enumerated(EnumType.STRING)
    @Column(name = "resolution_status")
    private ResolutionStatus resolutionStatus = ResolutionStatus.UNRESOLVED;

    @Column(name = "resolution_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resolutionDate;

    @Column(name = "lessons_learned", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Lessons learned must not exceed 3000 characters")
    private String lessonsLearned;

    @Column(name = "media_coverage")
    private Boolean mediaCoverage = false;

    @Column(name = "international_attention")
    private Boolean internationalAttention = false;

    @Column(name = "created_by", length = 20)
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    @JsonIgnore
    private User creator;

    // Enums
    public enum AlertCategory {
        PRODUCTION("Production"),
        WEATHER("Weather"),
        MARKET("Market"),
        DISEASE("Disease"),
        POLICY("Policy"),
        INFRASTRUCTURE("Infrastructure");

        private final String displayName;

        AlertCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isEnvironmentalCategory() {
            return this == WEATHER || this == DISEASE;
        }

        public boolean isEconomicCategory() {
            return this == MARKET || this == POLICY;
        }

        public boolean isOperationalCategory() {
            return this == PRODUCTION || this == INFRASTRUCTURE;
        }
    }

    public enum AlertLevel {
        INFO("Info", 1, "#17a2b8"),
        LOW("Low", 2, "#28a745"),
        MEDIUM("Medium", 3, "#ffc107"),
        HIGH("High", 4, "#fd7e14"),
        CRITICAL("Critical", 5, "#dc3545");

        private final String displayName;
        private final int priority;
        private final String colorCode;

        AlertLevel(String displayName, int priority, String colorCode) {
            this.displayName = displayName;
            this.priority = priority;
            this.colorCode = colorCode;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getPriority() {
            return priority;
        }

        public String getColorCode() {
            return colorCode;
        }

        public boolean requiresImmediateAction() {
            return this == HIGH || this == CRITICAL;
        }

        public boolean isUrgent() {
            return this == CRITICAL;
        }

        public static AlertLevel fromSeverityScore(int score) {
            if (score <= 2) return INFO;
            if (score <= 4) return LOW;
            if (score <= 6) return MEDIUM;
            if (score <= 8) return HIGH;
            return CRITICAL;
        }
    }

    public enum SourceReliability {
        VERIFIED("Verified", 3),
        UNVERIFIED("Unverified", 2),
        PRELIMINARY("Preliminary", 1);

        private final String displayName;
        private final int reliabilityScore;

        SourceReliability(String displayName, int reliabilityScore) {
            this.displayName = displayName;
            this.reliabilityScore = reliabilityScore;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getReliabilityScore() {
            return reliabilityScore;
        }

        public boolean isReliable() {
            return this == VERIFIED;
        }
    }

    public enum ResolutionStatus {
        UNRESOLVED("Unresolved"),
        IN_PROGRESS("In Progress"),
        RESOLVED("Resolved");

        private final String displayName;

        ResolutionStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isActive() {
            return this != RESOLVED;
        }
    }

    public enum UrgencyLevel {
        ROUTINE("Routine", 1),
        MODERATE("Moderate", 2),
        URGENT("Urgent", 3),
        EMERGENCY("Emergency", 4);

        private final String displayName;
        private final int level;

        UrgencyLevel(String displayName, int level) {
            this.displayName = displayName;
            this.level = level;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getLevel() {
            return level;
        }

        public static UrgencyLevel determineUrgency(AlertLevel alertLevel, int escalationLevel) {
            if (alertLevel == AlertLevel.CRITICAL || escalationLevel >= 4) {
                return EMERGENCY;
            } else if (alertLevel == AlertLevel.HIGH || escalationLevel >= 3) {
                return URGENT;
            } else if (alertLevel == AlertLevel.MEDIUM || escalationLevel >= 2) {
                return MODERATE;
            }
            return ROUTINE;
        }
    }

    // Constructors
    public FoodSecurityAlert() {
        this.id = generateAlphanumericId();
        this.alertCode = generateAlertCode();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.alertDate = LocalDateTime.now();
    }

    public FoodSecurityAlert(String alertTitle, AlertCategory alertCategory, String description,
                             AlertLevel alertLevel, String source) {
        this();
        this.alertTitle = alertTitle;
        this.alertCategory = alertCategory;
        this.description = description;
        this.alertLevel = alertLevel;
        this.source = source;
    }

    // Method to generate alphanumeric ID with mixed letters and numbers
    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Add "FSA" prefix for Food Security Alert
        sb.append("FSA");

        // Timestamp-based part to ensure uniqueness (6 characters from timestamp)
        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 6);
        sb.append(shortTimestamp);

        // Add random mixed characters (letters and numbers)
        for (int i = 0; i < 5; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    // Method to generate alert code
    private String generateAlertCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Add "ALT" prefix
        sb.append("ALT");

        // Add date part (YYMMDD)
        LocalDateTime now = LocalDateTime.now();
        sb.append(String.format("%02d%02d%02d",
                now.getYear() % 100,
                now.getMonthValue(),
                now.getDayOfMonth()));

        // Add random characters
        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    // JPA lifecycle methods
    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = generateAlphanumericId();
        }
        if (this.alertCode == null) {
            this.alertCode = generateAlertCode();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.alertDate == null) {
            this.alertDate = LocalDateTime.now();
        }

        // Auto-calculate severity score from alert level if not set
        if (severityScore == null && alertLevel != null) {
            this.severityScore = calculateSeverityScore();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();

        // Update resolution date when status changes to resolved
        if (resolutionStatus == ResolutionStatus.RESOLVED && resolutionDate == null) {
            this.resolutionDate = LocalDateTime.now();
        }
    }

    // Utility method to calculate severity score
    private int calculateSeverityScore() {
        int baseScore = alertLevel.getPriority() * 2;

        // Adjust based on escalation level
        if (escalationLevel != null) {
            baseScore += escalationLevel - 1;
        }

        // Adjust based on affected population
        if (affectedPopulation != null) {
            if (affectedPopulation > 100000) baseScore += 2;
            else if (affectedPopulation > 10000) baseScore += 1;
        }

        return Math.min(10, Math.max(1, baseScore));
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

    public AlertCategory getAlertCategory() {
        return alertCategory;
    }

    public void setAlertCategory(AlertCategory alertCategory) {
        this.alertCategory = alertCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AlertLevel getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(AlertLevel alertLevel) {
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

    public String getAffectedDistricts() {
        return affectedDistricts;
    }

    public void setAffectedDistricts(String affectedDistricts) {
        this.affectedDistricts = affectedDistricts;
    }

    public String getAffectedCrops() {
        return affectedCrops;
    }

    public void setAffectedCrops(String affectedCrops) {
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

    public SourceReliability getSourceReliability() {
        return sourceReliability;
    }

    public void setSourceReliability(SourceReliability sourceReliability) {
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

    public String getStakeholdersNotified() {
        return stakeholdersNotified;
    }

    public void setStakeholdersNotified(String stakeholdersNotified) {
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

    public String getFollowUpAlerts() {
        return followUpAlerts;
    }

    public void setFollowUpAlerts(String followUpAlerts) {
        this.followUpAlerts = followUpAlerts;
    }

    public ResolutionStatus getResolutionStatus() {
        return resolutionStatus;
    }

    public void setResolutionStatus(ResolutionStatus resolutionStatus) {
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

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    // Utility methods
    public String getAlertCategoryDescription() {
        return alertCategory != null ? alertCategory.getDisplayName() : "Unknown";
    }

    public String getAlertLevelDescription() {
        return alertLevel != null ? alertLevel.getDisplayName() : "Unknown";
    }

    public String getSourceReliabilityDescription() {
        return sourceReliability != null ? sourceReliability.getDisplayName() : "Unknown";
    }

    public String getResolutionStatusDescription() {
        return resolutionStatus != null ? resolutionStatus.getDisplayName() : "Unknown";
    }

    public UrgencyLevel getUrgencyLevel() {
        return UrgencyLevel.determineUrgency(alertLevel, escalationLevel != null ? escalationLevel : 1);
    }

    public String getUrgencyDescription() {
        return getUrgencyLevel().getDisplayName();
    }

    public List<String> getAffectedDistrictsList() {
        if (affectedDistricts == null || affectedDistricts.trim().isEmpty()) {
            return List.of();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(affectedDistricts, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    public void setAffectedDistrictsList(List<String> districts) {
        if (districts == null || districts.isEmpty()) {
            this.affectedDistricts = null;
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.affectedDistricts = mapper.writeValueAsString(districts);
        } catch (Exception e) {
            this.affectedDistricts = null;
        }
    }

    public List<String> getAffectedCropsList() {
        if (affectedCrops == null || affectedCrops.trim().isEmpty()) {
            return List.of();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(affectedCrops, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    public void setAffectedCropsList(List<String> crops) {
        if (crops == null || crops.isEmpty()) {
            this.affectedCrops = null;
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.affectedCrops = mapper.writeValueAsString(crops);
        } catch (Exception e) {
            this.affectedCrops = null;
        }
    }

    public List<String> getStakeholdersNotifiedList() {
        if (stakeholdersNotified == null || stakeholdersNotified.trim().isEmpty()) {
            return List.of();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(stakeholdersNotified, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    public void setStakeholdersNotifiedList(List<String> stakeholders) {
        if (stakeholders == null || stakeholders.isEmpty()) {
            this.stakeholdersNotified = null;
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.stakeholdersNotified = mapper.writeValueAsString(stakeholders);
        } catch (Exception e) {
            this.stakeholdersNotified = null;
        }
    }

    public List<String> getFollowUpAlertsList() {
        if (followUpAlerts == null || followUpAlerts.trim().isEmpty()) {
            return List.of();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(followUpAlerts, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    public void setFollowUpAlertsList(List<String> alerts) {
        if (alerts == null || alerts.isEmpty()) {
            this.followUpAlerts = null;
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.followUpAlerts = mapper.writeValueAsString(alerts);
        } catch (Exception e) {
            this.followUpAlerts = null;
        }
    }

    public boolean isActive() {
        return isActive != null && isActive && !isExpired();
    }

    public boolean isExpired() {
        return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
    }

    public boolean isOverdue() {
        return responseRequired != null && responseRequired &&
                responseDeadline != null && LocalDateTime.now().isAfter(responseDeadline);
    }

    public boolean isCritical() {
        return alertLevel == AlertLevel.CRITICAL;
    }

    public boolean requiresImmediateAction() {
        return alertLevel != null && alertLevel.requiresImmediateAction();
    }

    public boolean isUrgent() {
        return alertLevel != null && alertLevel.isUrgent();
    }

    public boolean isResolved() {
        return resolutionStatus == ResolutionStatus.RESOLVED;
    }

    public boolean isInProgress() {
        return resolutionStatus == ResolutionStatus.IN_PROGRESS;
    }

    public boolean hasMediaAttention() {
        return (mediaCoverage != null && mediaCoverage) || (internationalAttention != null && internationalAttention);
    }

    public boolean isReliableSource() {
        return sourceReliability != null && sourceReliability.isReliable();
    }

    public boolean hasEconomicImpact() {
        return economicImpact != null && economicImpact.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasHighPopulationImpact() {
        return affectedPopulation != null && affectedPopulation > 10000;
    }

    public boolean isEscalated() {
        return escalationLevel != null && escalationLevel > 2;
    }

    public boolean isEnvironmentalAlert() {
        return alertCategory != null && alertCategory.isEnvironmentalCategory();
    }

    public boolean isEconomicAlert() {
        return alertCategory != null && alertCategory.isEconomicCategory();
    }

    public boolean isOperationalAlert() {
        return alertCategory != null && alertCategory.isOperationalCategory();
    }

    public long getDaysSinceAlert() {
        if (alertDate == null) return 0;
        return java.time.Duration.between(alertDate, LocalDateTime.now()).toDays();
    }

    public long getDaysUntilExpiry() {
        if (expiryDate == null) return -1;
        return java.time.Duration.between(LocalDateTime.now(), expiryDate).toDays();
    }

    public long getDaysUntilResponseDeadline() {
        if (responseDeadline == null) return -1;
        return java.time.Duration.between(LocalDateTime.now(), responseDeadline).toDays();
    }

    public long getEventDurationDays() {
        if (eventStartDate == null || eventEndDate == null) return -1;
        return java.time.Duration.between(eventStartDate, eventEndDate).toDays();
    }

    public long getResolutionTimeDays() {
        if (alertDate == null || resolutionDate == null) return -1;
        return java.time.Duration.between(alertDate, resolutionDate).toDays();
    }

    public String getAlertDateFormatted() {
        return alertDate != null ?
                alertDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) :
                "Not set";
    }

    public String getEventStartDateFormatted() {
        return eventStartDate != null ?
                eventStartDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) :
                "Not specified";
    }

    public String getEventEndDateFormatted() {
        return eventEndDate != null ?
                eventEndDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) :
                "Ongoing";
    }

    public String getExpiryDateFormatted() {
        return expiryDate != null ?
                expiryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) :
                "No expiry";
    }

    public String getResponseDeadlineFormatted() {
        return responseDeadline != null ?
                responseDeadline.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) :
                "No deadline";
    }

    public String getResolutionDateFormatted() {
        return resolutionDate != null ?
                resolutionDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) :
                "Not resolved";
    }

    public String getAffectedPopulationFormatted() {
        if (affectedPopulation == null) return "Unknown";
        if (affectedPopulation >= 1000000) {
            return String.format("%.1fM people", affectedPopulation / 1000000.0);
        } else if (affectedPopulation >= 1000) {
            return String.format("%.1fK people", affectedPopulation / 1000.0);
        }
        return affectedPopulation + " people";
    }

    public String getEconomicImpactFormatted() {
        if (economicImpact == null) return "Not assessed";
        return "RWF " + economicImpact;
    }

    public String getSeverityScoreFormatted() {
        if (severityScore == null) return "Not scored";
        return severityScore + "/10";
    }

    public String getAlertSummary() {
        StringBuilder summary = new StringBuilder();

        if (alertCode != null) {
            summary.append(alertCode);
        }

        if (alertTitle != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append(alertTitle);
        }

        if (alertLevel != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append(alertLevel.getDisplayName().toUpperCase());
        }

        return summary.length() > 0 ? summary.toString() : "No alert data";
    }

    public String getImpactSummary() {
        StringBuilder summary = new StringBuilder();

        if (affectedRegion != null) {
            summary.append("Region: ").append(affectedRegion);
        }

        if (affectedPopulation != null && affectedPopulation > 0) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Population: ").append(getAffectedPopulationFormatted());
        }

        if (hasEconomicImpact()) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Economic: ").append(getEconomicImpactFormatted());
        }

        return summary.length() > 0 ? summary.toString() : "Impact not assessed";
    }

    public String getStatusSummary() {
        StringBuilder summary = new StringBuilder();

        if (resolutionStatus != null) {
            summary.append("Status: ").append(resolutionStatus.getDisplayName());
        }

        if (sourceReliability != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Source: ").append(sourceReliability.getDisplayName());
        }

        if (escalationLevel != null && escalationLevel > 1) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Escalation Level: ").append(escalationLevel);
        }

        if (isOverdue()) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("OVERDUE");
        }

        return summary.length() > 0 ? summary.toString() : "No status data";
    }

    public String getTimelineSummary() {
        StringBuilder summary = new StringBuilder();

        summary.append("Alert: ").append(getAlertDateFormatted());

        if (eventStartDate != null) {
            summary.append(" - Event Start: ").append(getEventStartDateFormatted());
        }

        if (eventEndDate != null) {
            summary.append(" - Event End: ").append(getEventEndDateFormatted());
        } else if (eventStartDate != null) {
            summary.append(" - Ongoing");
        }

        if (resolutionDate != null) {
            summary.append(" - Resolved: ").append(getResolutionDateFormatted());
        }

        return summary.toString();
    }

    public String getResponseSummary() {
        StringBuilder summary = new StringBuilder();

        if (responseRequired != null && responseRequired) {
            summary.append("Response Required");

            if (responseDeadline != null) {
                summary.append(" - Deadline: ").append(getResponseDeadlineFormatted());

                long daysLeft = getDaysUntilResponseDeadline();
                if (daysLeft >= 0) {
                    summary.append(" (").append(daysLeft).append(" days left)");
                } else {
                    summary.append(" (OVERDUE)");
                }
            }
        } else {
            summary.append("No response required");
        }

        List<String> notifiedStakeholders = getStakeholdersNotifiedList();
        if (!notifiedStakeholders.isEmpty()) {
            summary.append(" - Stakeholders: ").append(notifiedStakeholders.size()).append(" notified");
        }

        return summary.toString();
    }

    public String getAffectedAreasSummary() {
        StringBuilder summary = new StringBuilder();

        if (affectedRegion != null) {
            summary.append("Region: ").append(affectedRegion);
        }

        List<String> districts = getAffectedDistrictsList();
        if (!districts.isEmpty()) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Districts: ").append(String.join(", ", districts));
        }

        List<String> crops = getAffectedCropsList();
        if (!crops.isEmpty()) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Crops: ").append(String.join(", ", crops));
        }

        return summary.length() > 0 ? summary.toString() : "Areas not specified";
    }

    public String getMediaAttentionSummary() {
        StringBuilder summary = new StringBuilder();

        if (mediaCoverage != null && mediaCoverage) {
            summary.append("Media Coverage");
        }

        if (internationalAttention != null && internationalAttention) {
            if (summary.length() > 0) summary.append(" + ");
            summary.append("International Attention");
        }

        return summary.length() > 0 ? summary.toString() : "No media attention";
    }

    // State transition methods
    public boolean canEscalate() {
        return escalationLevel != null && escalationLevel < 5 && isActive();
    }

    public boolean canResolve() {
        return resolutionStatus != ResolutionStatus.RESOLVED && isActive();
    }

    public boolean canDeactivate() {
        return isActive() && (isExpired() || isResolved());
    }

    public boolean canExtend() {
        return isActive() && expiryDate != null;
    }

    public void escalate() {
        if (canEscalate()) {
            this.escalationLevel = escalationLevel + 1;
            this.updatedAt = LocalDateTime.now();

            // Auto-upgrade alert level if highly escalated
            if (escalationLevel >= 4 && alertLevel != AlertLevel.CRITICAL) {
                this.alertLevel = AlertLevel.CRITICAL;
                this.severityScore = calculateSeverityScore();
            }
        }
    }

    public void markInProgress() {
        if (resolutionStatus == ResolutionStatus.UNRESOLVED) {
            this.resolutionStatus = ResolutionStatus.IN_PROGRESS;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void markResolved() {
        if (canResolve()) {
            this.resolutionStatus = ResolutionStatus.RESOLVED;
            this.resolutionDate = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void deactivate() {
        if (canDeactivate()) {
            this.isActive = false;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void reactivate() {
        if (!isActive() && !isExpired()) {
            this.isActive = true;
            this.resolutionStatus = ResolutionStatus.UNRESOLVED;
            this.resolutionDate = null;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void extendExpiry(int days) {
        if (canExtend()) {
            if (expiryDate != null) {
                this.expiryDate = expiryDate.plusDays(days);
            } else {
                this.expiryDate = LocalDateTime.now().plusDays(days);
            }
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void updateResponseDeadline(LocalDateTime newDeadline) {
        this.responseDeadline = newDeadline;
        this.responseRequired = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void addStakeholder(String stakeholder) {
        List<String> currentStakeholders = getStakeholdersNotifiedList();
        if (!currentStakeholders.contains(stakeholder)) {
            currentStakeholders.add(stakeholder);
            setStakeholdersNotifiedList(currentStakeholders);
        }
    }

    public void addFollowUpAlert(String alertId) {
        List<String> currentAlerts = getFollowUpAlertsList();
        if (!currentAlerts.contains(alertId)) {
            currentAlerts.add(alertId);
            setFollowUpAlertsList(currentAlerts);
        }
    }

    // Validation methods
    public boolean isValidAlert() {
        return alertTitle != null && !alertTitle.trim().isEmpty() &&
                alertCategory != null &&
                alertLevel != null &&
                description != null && !description.trim().isEmpty() &&
                source != null && !source.trim().isEmpty();
    }

    public boolean hasCompleteImpactAssessment() {
        return (affectedRegion != null || !getAffectedDistrictsList().isEmpty()) &&
                (affectedPopulation != null || hasEconomicImpact() ||
                        (socialImpact != null && !socialImpact.trim().isEmpty()) ||
                        (environmentalImpact != null && !environmentalImpact.trim().isEmpty()));
    }

    public boolean hasResponsePlan() {
        return recommendedActions != null && !recommendedActions.trim().isEmpty() &&
                !getStakeholdersNotifiedList().isEmpty();
    }

    public boolean isReadyForResolution() {
        return resolutionStatus == ResolutionStatus.IN_PROGRESS &&
                (mitigationMeasures != null && !mitigationMeasures.trim().isEmpty());
    }

    // toString, equals and hashCode
    @Override
    public String toString() {
        return "FoodSecurityAlert{" +
                "id='" + id + '\'' +
                ", alertCode='" + alertCode + '\'' +
                ", alertTitle='" + alertTitle + '\'' +
                ", alertCategory=" + alertCategory +
                ", alertLevel=" + alertLevel +
                ", affectedRegion='" + affectedRegion + '\'' +
                ", affectedPopulation=" + affectedPopulation +
                ", source='" + source + '\'' +
                ", sourceReliability=" + sourceReliability +
                ", resolutionStatus=" + resolutionStatus +
                ", isActive=" + isActive +
                ", alertDate=" + alertDate +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodSecurityAlert that = (FoodSecurityAlert) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}