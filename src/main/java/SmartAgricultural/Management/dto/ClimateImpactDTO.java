package SmartAgricultural.Management.dto;

import SmartAgricultural.Management.Model.ClimateImpact;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Climate Impact Data Transfer Object")
public class ClimateImpactDTO {

    @Schema(description = "Unique identifier", example = "CI123456A1B2C3")
    private String id;

    @Schema(description = "Impact code", example = "IMP2501015678XYZW", required = true)
    @NotBlank(message = "Impact code is required")
    @Size(max = 30, message = "Impact code must not exceed 30 characters")
    private String impactCode;

    @Schema(description = "Related crop ID", example = "CR001")
    private String cropId;

    @Schema(description = "Region name", example = "Northern Province", required = true)
    @NotBlank(message = "Region is required")
    @Size(max = 100, message = "Region must not exceed 100 characters")
    private String region;

    @Schema(description = "District name", example = "Musanze")
    @Size(max = 50, message = "District must not exceed 50 characters")
    private String district;

    @Schema(description = "Year of the climate impact", example = "2024", required = true)
    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be at least 2000")
    @Max(value = 2100, message = "Year must not exceed 2100")
    private Integer year;

    @Schema(description = "Agricultural season", required = true)
    @NotNull(message = "Season is required")
    private ClimateImpact.Season season;

    @Schema(description = "Type of climate event", required = true)
    @NotNull(message = "Climate event is required")
    private ClimateImpact.ClimateEvent climateEvent;

    @Schema(description = "Intensity level of the event", required = true)
    @NotNull(message = "Event intensity is required")
    private ClimateImpact.EventIntensity eventIntensity;

    @Schema(description = "Start date of the climate event", example = "2024-01-15", required = true)
    @NotNull(message = "Event start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventStartDate;

    @Schema(description = "End date of the climate event", example = "2024-01-20")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventEndDate;

    @Schema(description = "Duration of the event in days", example = "5")
    @Min(value = 1, message = "Event duration must be at least 1 day")
    private Integer eventDurationDays;

    @Schema(description = "Frequency of the event occurrence", example = "1")
    @Min(value = 1, message = "Event frequency must be at least 1")
    private Integer eventFrequency;

    @Schema(description = "Total affected area in hectares", example = "1500.50")
    @DecimalMin(value = "0.0", message = "Affected area must be positive")
    @Digits(integer = 8, fraction = 2, message = "Affected area format is invalid")
    private BigDecimal affectedArea;

    @Schema(description = "Number of affected people", example = "1200")
    @Min(value = 0, message = "Affected population must be positive")
    private Integer affectedPopulation;

    @Schema(description = "Number of affected households", example = "300")
    @Min(value = 0, message = "Affected households must be positive")
    private Integer affectedHouseholds;

    @Schema(description = "Crop area affected in hectares", example = "800.25")
    @DecimalMin(value = "0.0", message = "Crop area affected must be positive")
    @Digits(integer = 8, fraction = 2, message = "Crop area affected format is invalid")
    private BigDecimal cropAreaAffected;

    @Schema(description = "Number of livestock affected", example = "150")
    @Min(value = 0, message = "Livestock affected must be positive")
    private Integer livestockAffected;

    @Schema(description = "Description of affected infrastructure")
    @Size(max = 3000, message = "Infrastructure affected must not exceed 3000 characters")
    private String infrastructureAffected;

    @Schema(description = "Percentage of yield impact", example = "35.50")
    @DecimalMin(value = "0.0", message = "Yield impact must be positive")
    @DecimalMax(value = "100.0", message = "Yield impact must not exceed 100%")
    @Digits(integer = 4, fraction = 2, message = "Yield impact format is invalid")
    private BigDecimal yieldImpact;

    @Schema(description = "Production loss in tonnes", example = "450.75")
    @DecimalMin(value = "0.0", message = "Production loss must be positive")
    @Digits(integer = 8, fraction = 2, message = "Production loss format is invalid")
    private BigDecimal productionLoss;

    @Schema(description = "Impact on crop quality", example = "Reduced quality due to drought stress")
    @Size(max = 100, message = "Quality impact must not exceed 100 characters")
    private String qualityImpact;

    @Schema(description = "Economic loss in currency units", example = "250000.00")
    @DecimalMin(value = "0.0", message = "Economic loss must be positive")
    @Digits(integer = 13, fraction = 2, message = "Economic loss format is invalid")
    private BigDecimal economicLoss;

    @Schema(description = "Description of social impact")
    @Size(max = 3000, message = "Social impact must not exceed 3000 characters")
    private String socialImpact;

    @Schema(description = "Description of health impact")
    @Size(max = 3000, message = "Health impact must not exceed 3000 characters")
    private String healthImpact;

    @Schema(description = "Description of environmental impact")
    @Size(max = 3000, message = "Environmental impact must not exceed 3000 characters")
    private String environmentalImpact;

    @Schema(description = "Immediate response measures taken")
    @Size(max = 3000, message = "Immediate response must not exceed 3000 characters")
    private String immediateResponse;

    @Schema(description = "Emergency measures implemented")
    @Size(max = 3000, message = "Emergency measures must not exceed 3000 characters")
    private String emergencyMeasures;

    @Schema(description = "Adaptation strategy implemented")
    @Size(max = 3000, message = "Adaptation strategy must not exceed 3000 characters")
    private String adaptationStrategy;

    @Schema(description = "Mitigation measures taken")
    @Size(max = 3000, message = "Mitigation measures must not exceed 3000 characters")
    private String mitigationMeasures;

    @Schema(description = "Recovery time in days", example = "90")
    @Min(value = 1, message = "Recovery time must be at least 1 day")
    private Integer recoveryTimeDays;

    @Schema(description = "Cost of recovery in currency units", example = "50000.00")
    @DecimalMin(value = "0.0", message = "Recovery cost must be positive")
    @Digits(integer = 10, fraction = 2, message = "Recovery cost format is invalid")
    private BigDecimal recoveryCost;

    @Schema(description = "Insurance payout received", example = "75000.00")
    @DecimalMin(value = "0.0", message = "Insurance payout must be positive")
    @Digits(integer = 10, fraction = 2, message = "Insurance payout format is invalid")
    private BigDecimal insurancePayout;

    @Schema(description = "Government assistance received", example = "100000.00")
    @DecimalMin(value = "0.0", message = "Government assistance must be positive")
    @Digits(integer = 10, fraction = 2, message = "Government assistance format is invalid")
    private BigDecimal governmentAssistance;

    @Schema(description = "NGO support description")
    @Size(max = 2000, message = "NGO support must not exceed 2000 characters")
    private String ngoSupport;

    @Schema(description = "International aid received", example = "25000.00")
    @DecimalMin(value = "0.0", message = "International aid must be positive")
    @Digits(integer = 10, fraction = 2, message = "International aid format is invalid")
    private BigDecimal internationalAid;

    @Schema(description = "Lessons learned from the impact")
    @Size(max = 5000, message = "Lessons learned must not exceed 5000 characters")
    private String lessonsLearned;

    @Schema(description = "Prevention measures for future events")
    @Size(max = 3000, message = "Prevention measures must not exceed 3000 characters")
    private String preventionMeasures;

    @Schema(description = "Effectiveness of early warning systems")
    private ClimateImpact.WarningEffectiveness earlyWarningEffectiveness;

    @Schema(description = "Effectiveness of response measures")
    private ClimateImpact.ResponseEffectiveness responseEffectiveness;

    @Schema(description = "Vulnerability factors (JSON format)")
    @Size(max = 3000, message = "Vulnerability factors must not exceed 3000 characters")
    private String vulnerabilityFactors;

    @Schema(description = "Resilience factors (JSON format)")
    @Size(max = 3000, message = "Resilience factors must not exceed 3000 characters")
    private String resilienceFactors;

    @Schema(description = "Climate scenario reference", example = "RCP4.5")
    @Size(max = 50, message = "Climate scenario must not exceed 50 characters")
    private String climateScenario;

    @Schema(description = "Probability of recurrence as percentage", example = "15.5")
    @DecimalMin(value = "0.0", message = "Probability recurrence must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Probability recurrence must be between 0 and 100")
    @Digits(integer = 3, fraction = 2, message = "Probability recurrence format is invalid")
    private BigDecimal probabilityRecurrence;

    @Schema(description = "Trend analysis of the climate impact")
    @Size(max = 3000, message = "Trend analysis must not exceed 3000 characters")
    private String trendAnalysis;

    @Schema(description = "Future projections related to the impact")
    @Size(max = 3000, message = "Future projections must not exceed 3000 characters")
    private String futureProjections;

    @Schema(description = "Adaptation recommendations")
    @Size(max = 3000, message = "Adaptation recommendations must not exceed 3000 characters")
    private String adaptationRecommendations;

    @Schema(description = "Stakeholders involved in response")
    @Size(max = 2000, message = "Stakeholders involved must not exceed 2000 characters")
    private String stakeholdersInvolved;

    @Schema(description = "Whether the impact received media coverage", example = "true")
    private Boolean mediaCoverage;

    @Schema(description = "Whether research studies were conducted", example = "false")
    private Boolean researchStudiesConducted;

    @Schema(description = "Data sources (JSON format)")
    @Size(max = 2000, message = "Data sources must not exceed 2000 characters")
    private String dataSources;

    @Schema(description = "Date when the impact was reported", example = "2024-01-25T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportDate;

    @Schema(description = "ID of the user who reported the impact", example = "USER001")
    private String reportedBy;

    @Schema(description = "Whether the impact has been verified", example = "true")
    private Boolean verified;

    @Schema(description = "Date when the impact was verified", example = "2024-01-26T14:20:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime verificationDate;

    @Schema(description = "Creation timestamp", example = "2024-01-25T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-26T14:20:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // Additional computed fields
    @Schema(description = "Calculated severity level of the impact")
    private ClimateImpact.ImpactSeverity impactSeverity;

    @Schema(description = "Whether the event is currently ongoing", example = "false")
    private Boolean isOngoing;

    @Schema(description = "Whether the event occurred recently (within 30 days)", example = "true")
    private Boolean isRecent;

    @Schema(description = "Whether the impact requires emergency response", example = "true")
    private Boolean requiresEmergencyResponse;

    // Constructors
    public ClimateImpactDTO() {
        this.verified = false;
        this.mediaCoverage = false;
        this.researchStudiesConducted = false;
        this.earlyWarningEffectiveness = ClimateImpact.WarningEffectiveness.NONE;
        this.responseEffectiveness = ClimateImpact.ResponseEffectiveness.FAIR;
    }

    // Utility methods
    public String getFormattedEconomicLoss() {
        if (economicLoss == null) return "N/A";
        return String.format("%.2f", economicLoss);
    }

    public String getFormattedEventDuration() {
        if (eventDurationDays == null) return "N/A";
        if (eventDurationDays == 1) return "1 day";
        return eventDurationDays + " days";
    }

    public String getEventSummary() {
        if (climateEvent == null || season == null || region == null || year == null || eventIntensity == null) {
            return "Incomplete event information";
        }
        return String.format("%s %s in %s (%d) - %s intensity",
                climateEvent.getDisplayName(),
                season.getDisplayName(),
                region,
                year,
                eventIntensity.getDisplayName());
    }

    public boolean hasSignificantEconomicImpact() {
        return economicLoss != null && economicLoss.compareTo(new BigDecimal("50000")) >= 0;
    }

    public boolean isHighIntensityEvent() {
        return eventIntensity != null && eventIntensity.isHighIntensity();
    }

    public boolean isEffectiveWarningSystem() {
        return earlyWarningEffectiveness != null && earlyWarningEffectiveness.isEffective();
    }

    public boolean isEffectiveResponse() {
        return responseEffectiveness != null && responseEffectiveness.isEffective();
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

    public ClimateImpact.Season getSeason() {
        return season;
    }

    public void setSeason(ClimateImpact.Season season) {
        this.season = season;
    }

    public ClimateImpact.ClimateEvent getClimateEvent() {
        return climateEvent;
    }

    public void setClimateEvent(ClimateImpact.ClimateEvent climateEvent) {
        this.climateEvent = climateEvent;
    }

    public ClimateImpact.EventIntensity getEventIntensity() {
        return eventIntensity;
    }

    public void setEventIntensity(ClimateImpact.EventIntensity eventIntensity) {
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

    public ClimateImpact.WarningEffectiveness getEarlyWarningEffectiveness() {
        return earlyWarningEffectiveness;
    }

    public void setEarlyWarningEffectiveness(ClimateImpact.WarningEffectiveness earlyWarningEffectiveness) {
        this.earlyWarningEffectiveness = earlyWarningEffectiveness;
    }

    public ClimateImpact.ResponseEffectiveness getResponseEffectiveness() {
        return responseEffectiveness;
    }

    public void setResponseEffectiveness(ClimateImpact.ResponseEffectiveness responseEffectiveness) {
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

    public ClimateImpact.ImpactSeverity getImpactSeverity() {
        return impactSeverity;
    }

    public void setImpactSeverity(ClimateImpact.ImpactSeverity impactSeverity) {
        this.impactSeverity = impactSeverity;
    }

    public Boolean getIsOngoing() {
        return isOngoing;
    }

    public void setIsOngoing(Boolean isOngoing) {
        this.isOngoing = isOngoing;
    }

    public Boolean getIsRecent() {
        return isRecent;
    }

    public void setIsRecent(Boolean isRecent) {
        this.isRecent = isRecent;
    }

    public Boolean getRequiresEmergencyResponse() {
        return requiresEmergencyResponse;
    }

    public void setRequiresEmergencyResponse(Boolean requiresEmergencyResponse) {
        this.requiresEmergencyResponse = requiresEmergencyResponse;
    }

    // toString method
    @Override
    public String toString() {
        return "ClimateImpactDTO{" +
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

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClimateImpactDTO that = (ClimateImpactDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(impactCode, that.impactCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, impactCode);
    }
}