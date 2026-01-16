package SmartAgricultural.Management.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Entity
@Table(name = "climate_impacts")
public class ClimateImpact {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "impact_code", length = 30, unique = true, nullable = false)
    @NotBlank(message = "Impact code is required")
    @Size(max = 30, message = "Impact code must not exceed 30 characters")
    private String impactCode;

    @Column(name = "crop_id", length = 20)
    private String cropId;

    @Column(name = "region", length = 100, nullable = false)
    @NotBlank(message = "Region is required")
    @Size(max = 100, message = "Region must not exceed 100 characters")
    private String region;

    @Column(name = "district", length = 50)
    @Size(max = 50, message = "District must not exceed 50 characters")
    private String district;

    @Column(name = "year", nullable = false)
    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be at least 2000")
    @Max(value = 2100, message = "Year must not exceed 2100")
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(name = "season", nullable = false)
    @NotNull(message = "Season is required")
    private Season season;

    @Enumerated(EnumType.STRING)
    @Column(name = "climate_event", nullable = false)
    @NotNull(message = "Climate event is required")
    private ClimateEvent climateEvent;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_intensity", nullable = false)
    @NotNull(message = "Event intensity is required")
    private EventIntensity eventIntensity;

    @Column(name = "event_start_date", nullable = false)
    @NotNull(message = "Event start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventStartDate;

    @Column(name = "event_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventEndDate;

    @Column(name = "event_duration_days")
    @Min(value = 1, message = "Event duration must be at least 1 day")
    private Integer eventDurationDays;

    @Column(name = "event_frequency")
    @Min(value = 1, message = "Event frequency must be at least 1")
    private Integer eventFrequency;

    @Column(name = "affected_area", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Affected area must be positive")
    @Digits(integer = 8, fraction = 2, message = "Affected area format is invalid")
    private BigDecimal affectedArea; // hectares

    @Column(name = "affected_population")
    @Min(value = 0, message = "Affected population must be positive")
    private Integer affectedPopulation;

    @Column(name = "affected_households")
    @Min(value = 0, message = "Affected households must be positive")
    private Integer affectedHouseholds;

    @Column(name = "crop_area_affected", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Crop area affected must be positive")
    @Digits(integer = 8, fraction = 2, message = "Crop area affected format is invalid")
    private BigDecimal cropAreaAffected; // hectares

    @Column(name = "livestock_affected")
    @Min(value = 0, message = "Livestock affected must be positive")
    private Integer livestockAffected;

    @Column(name = "infrastructure_affected", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Infrastructure affected must not exceed 3000 characters")
    private String infrastructureAffected;

    @Column(name = "yield_impact", precision = 6, scale = 2)
    @DecimalMin(value = "0.0", message = "Yield impact must be positive")
    @DecimalMax(value = "100.0", message = "Yield impact must not exceed 100%")
    @Digits(integer = 4, fraction = 2, message = "Yield impact format is invalid")
    private BigDecimal yieldImpact; // % loss

    @Column(name = "production_loss", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Production loss must be positive")
    @Digits(integer = 8, fraction = 2, message = "Production loss format is invalid")
    private BigDecimal productionLoss; // tonnes

    @Column(name = "quality_impact", length = 100)
    @Size(max = 100, message = "Quality impact must not exceed 100 characters")
    private String qualityImpact;

    @Column(name = "economic_loss", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", message = "Economic loss must be positive")
    @Digits(integer = 13, fraction = 2, message = "Economic loss format is invalid")
    private BigDecimal economicLoss;

    @Column(name = "social_impact", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Social impact must not exceed 3000 characters")
    private String socialImpact;

    @Column(name = "health_impact", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Health impact must not exceed 3000 characters")
    private String healthImpact;

    @Column(name = "environmental_impact", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Environmental impact must not exceed 3000 characters")
    private String environmentalImpact;

    @Column(name = "immediate_response", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Immediate response must not exceed 3000 characters")
    private String immediateResponse;

    @Column(name = "emergency_measures", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Emergency measures must not exceed 3000 characters")
    private String emergencyMeasures;

    @Column(name = "adaptation_strategy", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Adaptation strategy must not exceed 3000 characters")
    private String adaptationStrategy;

    @Column(name = "mitigation_measures", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Mitigation measures must not exceed 3000 characters")
    private String mitigationMeasures;

    @Column(name = "recovery_time_days")
    @Min(value = 1, message = "Recovery time must be at least 1 day")
    private Integer recoveryTimeDays;

    @Column(name = "recovery_cost", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "Recovery cost must be positive")
    @Digits(integer = 10, fraction = 2, message = "Recovery cost format is invalid")
    private BigDecimal recoveryCost;

    @Column(name = "insurance_payout", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "Insurance payout must be positive")
    @Digits(integer = 10, fraction = 2, message = "Insurance payout format is invalid")
    private BigDecimal insurancePayout;

    @Column(name = "government_assistance", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "Government assistance must be positive")
    @Digits(integer = 10, fraction = 2, message = "Government assistance format is invalid")
    private BigDecimal governmentAssistance;

    @Column(name = "ngo_support", columnDefinition = "TEXT")
    @Size(max = 2000, message = "NGO support must not exceed 2000 characters")
    private String ngoSupport;

    @Column(name = "international_aid", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "International aid must be positive")
    @Digits(integer = 10, fraction = 2, message = "International aid format is invalid")
    private BigDecimal internationalAid;

    @Column(name = "lessons_learned", columnDefinition = "TEXT")
    @Size(max = 5000, message = "Lessons learned must not exceed 5000 characters")
    private String lessonsLearned;

    @Column(name = "prevention_measures", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Prevention measures must not exceed 3000 characters")
    private String preventionMeasures;

    @Enumerated(EnumType.STRING)
    @Column(name = "early_warning_effectiveness")
    private WarningEffectiveness earlyWarningEffectiveness = WarningEffectiveness.NONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "response_effectiveness")
    private ResponseEffectiveness responseEffectiveness = ResponseEffectiveness.FAIR;

    @Column(name = "vulnerability_factors", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Vulnerability factors must not exceed 3000 characters")
    private String vulnerabilityFactors; // JSON

    @Column(name = "resilience_factors", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Resilience factors must not exceed 3000 characters")
    private String resilienceFactors; // JSON

    @Column(name = "climate_scenario", length = 50)
    @Size(max = 50, message = "Climate scenario must not exceed 50 characters")
    private String climateScenario; // RCP2.6, RCP4.5, etc.

    @Column(name = "probability_recurrence", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Probability recurrence must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Probability recurrence must be between 0 and 100")
    @Digits(integer = 3, fraction = 2, message = "Probability recurrence format is invalid")
    private BigDecimal probabilityRecurrence; // %

    @Column(name = "trend_analysis", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Trend analysis must not exceed 3000 characters")
    private String trendAnalysis;

    @Column(name = "future_projections", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Future projections must not exceed 3000 characters")
    private String futureProjections;

    @Column(name = "adaptation_recommendations", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Adaptation recommendations must not exceed 3000 characters")
    private String adaptationRecommendations;

    @Column(name = "stakeholders_involved", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Stakeholders involved must not exceed 2000 characters")
    private String stakeholdersInvolved;

    @Column(name = "media_coverage")
    private Boolean mediaCoverage = false;

    @Column(name = "research_studies_conducted")
    private Boolean researchStudiesConducted = false;

    @Column(name = "data_sources", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Data sources must not exceed 2000 characters")
    private String dataSources; // JSON

    @Column(name = "report_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportDate;

    @Column(name = "reported_by", length = 20)
    private String reportedBy;

    @Column(name = "verified")
    private Boolean verified = false;

    @Column(name = "verification_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime verificationDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", insertable = false, updatable = false)
    @JsonIgnore
    private Crop crop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by", insertable = false, updatable = false)
    @JsonIgnore
    private User reporter;

    // Enums
    public enum Season {
        SEASON_A("Season A"),
        SEASON_B("Season B"),
        SEASON_C("Season C"),
        ANNUAL("Annual");

        private final String displayName;

        Season(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ClimateEvent {
        DROUGHT("Drought"),
        FLOOD("Flood"),
        EXTREME_HEAT("Extreme Heat"),
        COLD_WAVE("Cold Wave"),
        HAIL("Hail"),
        STRONG_WINDS("Strong Winds"),
        PEST_OUTBREAK("Pest Outbreak"),
        DISEASE_OUTBREAK("Disease Outbreak");

        private final String displayName;

        ClimateEvent(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isWeatherEvent() {
            return this == DROUGHT || this == FLOOD || this == EXTREME_HEAT ||
                    this == COLD_WAVE || this == HAIL || this == STRONG_WINDS;
        }

        public boolean isBiologicalEvent() {
            return this == PEST_OUTBREAK || this == DISEASE_OUTBREAK;
        }

        public boolean isWaterRelated() {
            return this == DROUGHT || this == FLOOD;
        }

        public boolean isTemperatureRelated() {
            return this == EXTREME_HEAT || this == COLD_WAVE;
        }
    }

    public enum EventIntensity {
        MILD("Mild", 1),
        MODERATE("Moderate", 2),
        SEVERE("Severe", 3),
        EXTREME("Extreme", 4);

        private final String displayName;
        private final int severityLevel;

        EventIntensity(String displayName, int severityLevel) {
            this.displayName = displayName;
            this.severityLevel = severityLevel;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getSeverityLevel() {
            return severityLevel;
        }

        public boolean isHighIntensity() {
            return this == SEVERE || this == EXTREME;
        }

        public boolean isLowIntensity() {
            return this == MILD || this == MODERATE;
        }
    }

    public enum WarningEffectiveness {
        EXCELLENT("Excellent", 5),
        GOOD("Good", 4),
        FAIR("Fair", 3),
        POOR("Poor", 2),
        NONE("None", 1);

        private final String displayName;
        private final int effectivenessScore;

        WarningEffectiveness(String displayName, int effectivenessScore) {
            this.displayName = displayName;
            this.effectivenessScore = effectivenessScore;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getEffectivenessScore() {
            return effectivenessScore;
        }

        public boolean isEffective() {
            return this == EXCELLENT || this == GOOD;
        }

        public boolean needsImprovement() {
            return this == FAIR || this == POOR || this == NONE;
        }
    }

    public enum ResponseEffectiveness {
        EXCELLENT("Excellent", 4),
        GOOD("Good", 3),
        FAIR("Fair", 2),
        POOR("Poor", 1);

        private final String displayName;
        private final int effectivenessScore;

        ResponseEffectiveness(String displayName, int effectivenessScore) {
            this.displayName = displayName;
            this.effectivenessScore = effectivenessScore;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getEffectivenessScore() {
            return effectivenessScore;
        }

        public boolean isEffective() {
            return this == EXCELLENT || this == GOOD;
        }

        public boolean needsImprovement() {
            return this == FAIR || this == POOR;
        }
    }

    public enum ImpactSeverity {
        LOW("Low Impact", 1),
        MODERATE("Moderate Impact", 2),
        HIGH("High Impact", 3),
        CATASTROPHIC("Catastrophic Impact", 4);

        private final String displayName;
        private final int severityLevel;

        ImpactSeverity(String displayName, int severityLevel) {
            this.displayName = displayName;
            this.severityLevel = severityLevel;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getSeverityLevel() {
            return severityLevel;
        }

        public static ImpactSeverity fromEconomicLoss(BigDecimal economicLoss) {
            if (economicLoss == null) return LOW;

            if (economicLoss.compareTo(new BigDecimal("1000000")) >= 0) return CATASTROPHIC;
            else if (economicLoss.compareTo(new BigDecimal("100000")) >= 0) return HIGH;
            else if (economicLoss.compareTo(new BigDecimal("10000")) >= 0) return MODERATE;
            else return LOW;
        }
    }

    // Constructors
    public ClimateImpact() {
        this.id = generateAlphanumericId();
        this.impactCode = generateImpactCode();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.reportDate = LocalDateTime.now();
    }

    public ClimateImpact(String region, Integer year, Season season, ClimateEvent climateEvent,
                         EventIntensity eventIntensity, LocalDate eventStartDate) {
        this();
        this.region = region;
        this.year = year;
        this.season = season;
        this.climateEvent = climateEvent;
        this.eventIntensity = eventIntensity;
        this.eventStartDate = eventStartDate;
    }

    // Method to generate alphanumeric ID with mixed letters and numbers
    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Add "CI" prefix for Climate Impact
        sb.append("CI");

        // Timestamp-based part to ensure uniqueness (6 characters from timestamp)
        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 6);
        sb.append(shortTimestamp);

        // Add random mixed characters (letters and numbers)
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    // Method to generate impact code
    private String generateImpactCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Add "IMP" prefix
        sb.append("IMP");

        // Add date part (YYMMDD)
        LocalDateTime now = LocalDateTime.now();
        sb.append(String.format("%02d%02d%02d",
                now.getYear() % 100,
                now.getMonthValue(),
                now.getDayOfMonth()));

        // Add timestamp (4 characters)
        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 4);
        sb.append(shortTimestamp);

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
        if (this.impactCode == null) {
            this.impactCode = generateImpactCode();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.reportDate == null) {
            this.reportDate = LocalDateTime.now();
        }

        // Calculate event duration if not provided
        if (eventDurationDays == null && eventStartDate != null && eventEndDate != null) {
            calculateEventDuration();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();

        // Recalculate event duration if dates changed
        if (eventStartDate != null && eventEndDate != null) {
            calculateEventDuration();
        }

        // Set verification date when verified
        if (verified != null && verified && verificationDate == null) {
            this.verificationDate = LocalDateTime.now();
        }
    }

    // Utility methods
    private void calculateEventDuration() {
        if (eventStartDate != null && eventEndDate != null) {
            this.eventDurationDays = (int) ChronoUnit.DAYS.between(eventStartDate, eventEndDate) + 1;
        }
    }

    public ImpactSeverity calculateImpactSeverity() {
        return ImpactSeverity.fromEconomicLoss(this.economicLoss);
    }

    public String getFormattedEventDuration() {
        if (eventDurationDays == null) return "N/A";
        if (eventDurationDays == 1) return "1 day";
        return eventDurationDays + " days";
    }

    public String getFormattedEconomicLoss() {
        if (economicLoss == null) return "N/A";
        return String.format("%.2f", economicLoss);
    }

    public boolean isOngoingEvent() {
        if (eventStartDate == null) return false;
        if (eventEndDate == null) return true; // No end date means ongoing
        return LocalDate.now().isBefore(eventEndDate) || LocalDate.now().isEqual(eventEndDate);
    }

    public boolean isRecentEvent() {
        if (eventStartDate == null) return false;
        return ChronoUnit.DAYS.between(eventStartDate, LocalDate.now()) <= 30;
    }

    public boolean hasSignificantEconomicImpact() {
        return economicLoss != null && economicLoss.compareTo(new BigDecimal("50000")) >= 0;
    }

    public boolean requiresEmergencyResponse() {
        return eventIntensity == EventIntensity.SEVERE || eventIntensity == EventIntensity.EXTREME ||
                hasSignificantEconomicImpact();
    }

    public String getEventSummary() {
        return String.format("%s %s in %s (%d) - %s intensity",
                climateEvent.getDisplayName(),
                season.getDisplayName(),
                region,
                year,
                eventIntensity.getDisplayName());
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImpactCode() {
        return impactCode;
    }

    public void setImpactCode(String impactCode) {
        this.impactCode = impactCode;
    }

    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public ClimateEvent getClimateEvent() {
        return climateEvent;
    }

    public void setClimateEvent(ClimateEvent climateEvent) {
        this.climateEvent = climateEvent;
    }

    public EventIntensity getEventIntensity() {
        return eventIntensity;
    }

    public void setEventIntensity(EventIntensity eventIntensity) {
        this.eventIntensity = eventIntensity;
    }

    public LocalDate getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(LocalDate eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public LocalDate getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(LocalDate eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public Integer getEventDurationDays() {
        return eventDurationDays;
    }

    public void setEventDurationDays(Integer eventDurationDays) {
        this.eventDurationDays = eventDurationDays;
    }

    public Integer getEventFrequency() {
        return eventFrequency;
    }

    public void setEventFrequency(Integer eventFrequency) {
        this.eventFrequency = eventFrequency;
    }

    public BigDecimal getAffectedArea() {
        return affectedArea;
    }

    public void setAffectedArea(BigDecimal affectedArea) {
        this.affectedArea = affectedArea;
    }

    public Integer getAffectedPopulation() {
        return affectedPopulation;
    }

    public void setAffectedPopulation(Integer affectedPopulation) {
        this.affectedPopulation = affectedPopulation;
    }

    public Integer getAffectedHouseholds() {
        return affectedHouseholds;
    }

    public void setAffectedHouseholds(Integer affectedHouseholds) {
        this.affectedHouseholds = affectedHouseholds;
    }

    public BigDecimal getCropAreaAffected() {
        return cropAreaAffected;
    }

    public void setCropAreaAffected(BigDecimal cropAreaAffected) {
        this.cropAreaAffected = cropAreaAffected;
    }

    public Integer getLivestockAffected() {
        return livestockAffected;
    }

    public void setLivestockAffected(Integer livestockAffected) {
        this.livestockAffected = livestockAffected;
    }

    public String getInfrastructureAffected() {
        return infrastructureAffected;
    }

    public void setInfrastructureAffected(String infrastructureAffected) {
        this.infrastructureAffected = infrastructureAffected;
    }

    public BigDecimal getYieldImpact() {
        return yieldImpact;
    }

    public void setYieldImpact(BigDecimal yieldImpact) {
        this.yieldImpact = yieldImpact;
    }

    public BigDecimal getProductionLoss() {
        return productionLoss;
    }

    public void setProductionLoss(BigDecimal productionLoss) {
        this.productionLoss = productionLoss;
    }

    public String getQualityImpact() {
        return qualityImpact;
    }

    public void setQualityImpact(String qualityImpact) {
        this.qualityImpact = qualityImpact;
    }

    public BigDecimal getEconomicLoss() {
        return economicLoss;
    }

    public void setEconomicLoss(BigDecimal economicLoss) {
        this.economicLoss = economicLoss;
    }

    public String getSocialImpact() {
        return socialImpact;
    }

    public void setSocialImpact(String socialImpact) {
        this.socialImpact = socialImpact;
    }

    public String getHealthImpact() {
        return healthImpact;
    }

    public void setHealthImpact(String healthImpact) {
        this.healthImpact = healthImpact;
    }

    public String getEnvironmentalImpact() {
        return environmentalImpact;
    }

    public void setEnvironmentalImpact(String environmentalImpact) {
        this.environmentalImpact = environmentalImpact;
    }

    public String getImmediateResponse() {
        return immediateResponse;
    }

    public void setImmediateResponse(String immediateResponse) {
        this.immediateResponse = immediateResponse;
    }

    public String getEmergencyMeasures() {
        return emergencyMeasures;
    }

    public void setEmergencyMeasures(String emergencyMeasures) {
        this.emergencyMeasures = emergencyMeasures;
    }

    public String getAdaptationStrategy() {
        return adaptationStrategy;
    }

    public void setAdaptationStrategy(String adaptationStrategy) {
        this.adaptationStrategy = adaptationStrategy;
    }

    public String getMitigationMeasures() {
        return mitigationMeasures;
    }

    public void setMitigationMeasures(String mitigationMeasures) {
        this.mitigationMeasures = mitigationMeasures;
    }

    public Integer getRecoveryTimeDays() {
        return recoveryTimeDays;
    }

    public void setRecoveryTimeDays(Integer recoveryTimeDays) {
        this.recoveryTimeDays = recoveryTimeDays;
    }

    public BigDecimal getRecoveryCost() {
        return recoveryCost;
    }

    public void setRecoveryCost(BigDecimal recoveryCost) {
        this.recoveryCost = recoveryCost;
    }

    public BigDecimal getInsurancePayout() {
        return insurancePayout;
    }

    public void setInsurancePayout(BigDecimal insurancePayout) {
        this.insurancePayout = insurancePayout;
    }

    public BigDecimal getGovernmentAssistance() {
        return governmentAssistance;
    }

    public void setGovernmentAssistance(BigDecimal governmentAssistance) {
        this.governmentAssistance = governmentAssistance;
    }

    public String getNgoSupport() {
        return ngoSupport;
    }

    public void setNgoSupport(String ngoSupport) {
        this.ngoSupport = ngoSupport;
    }

    public BigDecimal getInternationalAid() {
        return internationalAid;
    }

    public void setInternationalAid(BigDecimal internationalAid) {
        this.internationalAid = internationalAid;
    }

    public String getLessonsLearned() {
        return lessonsLearned;
    }

    public void setLessonsLearned(String lessonsLearned) {
        this.lessonsLearned = lessonsLearned;
    }

    public String getPreventionMeasures() {
        return preventionMeasures;
    }

    public void setPreventionMeasures(String preventionMeasures) {
        this.preventionMeasures = preventionMeasures;
    }

    public WarningEffectiveness getEarlyWarningEffectiveness() {
        return earlyWarningEffectiveness;
    }

    public void setEarlyWarningEffectiveness(WarningEffectiveness earlyWarningEffectiveness) {
        this.earlyWarningEffectiveness = earlyWarningEffectiveness;
    }

    public ResponseEffectiveness getResponseEffectiveness() {
        return responseEffectiveness;
    }

    public void setResponseEffectiveness(ResponseEffectiveness responseEffectiveness) {
        this.responseEffectiveness = responseEffectiveness;
    }

    public String getVulnerabilityFactors() {
        return vulnerabilityFactors;
    }

    public void setVulnerabilityFactors(String vulnerabilityFactors) {
        this.vulnerabilityFactors = vulnerabilityFactors;
    }

    public String getResilienceFactors() {
        return resilienceFactors;
    }

    public void setResilienceFactors(String resilienceFactors) {
        this.resilienceFactors = resilienceFactors;
    }

    public String getClimateScenario() {
        return climateScenario;
    }

    public void setClimateScenario(String climateScenario) {
        this.climateScenario = climateScenario;
    }

    public BigDecimal getProbabilityRecurrence() {
        return probabilityRecurrence;
    }

    public void setProbabilityRecurrence(BigDecimal probabilityRecurrence) {
        this.probabilityRecurrence = probabilityRecurrence;
    }

    public String getTrendAnalysis() {
        return trendAnalysis;
    }

    public void setTrendAnalysis(String trendAnalysis) {
        this.trendAnalysis = trendAnalysis;
    }

    public String getFutureProjections() {
        return futureProjections;
    }

    public void setFutureProjections(String futureProjections) {
        this.futureProjections = futureProjections;
    }

    public String getAdaptationRecommendations() {
        return adaptationRecommendations;
    }

    public void setAdaptationRecommendations(String adaptationRecommendations) {
        this.adaptationRecommendations = adaptationRecommendations;
    }

    public String getStakeholdersInvolved() {
        return stakeholdersInvolved;
    }

    public void setStakeholdersInvolved(String stakeholdersInvolved) {
        this.stakeholdersInvolved = stakeholdersInvolved;
    }

    public Boolean getMediaCoverage() {
        return mediaCoverage;
    }

    public void setMediaCoverage(Boolean mediaCoverage) {
        this.mediaCoverage = mediaCoverage;
    }

    public Boolean getResearchStudiesConducted() {
        return researchStudiesConducted;
    }

    public void setResearchStudiesConducted(Boolean researchStudiesConducted) {
        this.researchStudiesConducted = researchStudiesConducted;
    }

    public String getDataSources() {
        return dataSources;
    }

    public void setDataSources(String dataSources) {
        this.dataSources = dataSources;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public LocalDateTime getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(LocalDateTime verificationDate) {
        this.verificationDate = verificationDate;
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

    public Crop getCrop() {
        return crop;
    }

    public void setCrop(Crop crop) {
        this.crop = crop;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    // ToString method for debugging
    @Override
    public String toString() {
        return "ClimateImpact{" +
                "id='" + id + '\'' +
                ", impactCode='" + impactCode + '\'' +
                ", region='" + region + '\'' +
                ", district='" + district + '\'' +
                ", year=" + year +
                ", season=" + season +
                ", climateEvent=" + climateEvent +
                ", eventIntensity=" + eventIntensity +
                ", eventStartDate=" + eventStartDate +
                ", eventEndDate=" + eventEndDate +
                ", economicLoss=" + economicLoss +
                ", verified=" + verified +
                '}';
    }

    // Equals and HashCode based on ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClimateImpact that = (ClimateImpact) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}