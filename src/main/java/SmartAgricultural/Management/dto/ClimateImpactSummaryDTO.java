package SmartAgricultural.Management.dto;

import SmartAgricultural.Management.Model.ClimateImpact;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Climate Impact Summary Data Transfer Object")
public class ClimateImpactSummaryDTO {

    @Schema(description = "Region for which summary is generated", example = "Northern Province")
    private String region;

    @Schema(description = "Year for which summary is generated", example = "2024")
    private Integer year;

    @Schema(description = "District for which summary is generated", example = "Musanze")
    private String district;

    @Schema(description = "Season for which summary is generated")
    private ClimateImpact.Season season;

    @Schema(description = "Total number of climate impacts recorded", example = "45")
    private Long totalImpacts;

    @Schema(description = "Number of verified impacts", example = "42")
    private Long verifiedImpacts;

    @Schema(description = "Number of unverified impacts", example = "3")
    private Long unverifiedImpacts;

    @Schema(description = "Number of ongoing events", example = "2")
    private Long ongoingEvents;

    @Schema(description = "Number of recent events (last 30 days)", example = "8")
    private Long recentEvents;

    @Schema(description = "Total economic loss", example = "1250000.50")
    private BigDecimal totalEconomicLoss;

    @Schema(description = "Average economic loss per impact", example = "27777.78")
    private BigDecimal averageEconomicLoss;

    @Schema(description = "Maximum economic loss from single impact", example = "350000.00")
    private BigDecimal maxEconomicLoss;

    @Schema(description = "Minimum economic loss from single impact", example = "5000.00")
    private BigDecimal minEconomicLoss;

    @Schema(description = "Total affected area in hectares", example = "15750.25")
    private BigDecimal totalAffectedArea;

    @Schema(description = "Average affected area per impact", example = "350.01")
    private BigDecimal averageAffectedArea;

    @Schema(description = "Total affected population", example = "12500")
    private Long totalAffectedPopulation;

    @Schema(description = "Average affected population per impact", example = "277")
    private Long averageAffectedPopulation;

    @Schema(description = "Total affected households", example = "3200")
    private Long totalAffectedHouseholds;

    @Schema(description = "Total crop area affected in hectares", example = "8500.75")
    private BigDecimal totalCropAreaAffected;

    @Schema(description = "Total livestock affected", example = "850")
    private Long totalLivestockAffected;

    @Schema(description = "Total production loss in tonnes", example = "2350.50")
    private BigDecimal totalProductionLoss;

    @Schema(description = "Average production loss per impact", example = "52.23")
    private BigDecimal averageProductionLoss;

    @Schema(description = "Total recovery cost", example = "750000.00")
    private BigDecimal totalRecoveryCost;

    @Schema(description = "Total insurance payouts", example = "450000.00")
    private BigDecimal totalInsurancePayouts;

    @Schema(description = "Total government assistance", example = "680000.00")
    private BigDecimal totalGovernmentAssistance;

    @Schema(description = "Total international aid", example = "125000.00")
    private BigDecimal totalInternationalAid;

    @Schema(description = "Count of impacts by event type")
    private Map<ClimateImpact.ClimateEvent, Long> eventTypeCounts;

    @Schema(description = "Count of impacts by season")
    private Map<ClimateImpact.Season, Long> seasonalCounts;

    @Schema(description = "Count of impacts by intensity level")
    private Map<ClimateImpact.EventIntensity, Long> intensityCounts;

    @Schema(description = "Count of impacts by severity level")
    private Map<ClimateImpact.ImpactSeverity, Long> severityCounts;

    @Schema(description = "Economic loss by event type")
    private Map<ClimateImpact.ClimateEvent, BigDecimal> economicLossByEventType;

    @Schema(description = "Economic loss by season")
    private Map<ClimateImpact.Season, BigDecimal> economicLossBySeason;

    @Schema(description = "Economic loss by intensity")
    private Map<ClimateImpact.EventIntensity, BigDecimal> economicLossByIntensity;

    @Schema(description = "Count of impacts by warning effectiveness")
    private Map<ClimateImpact.WarningEffectiveness, Long> warningEffectivenessStats;

    @Schema(description = "Count of impacts by response effectiveness")
    private Map<ClimateImpact.ResponseEffectiveness, Long> responseEffectivenessStats;

    @Schema(description = "Most affected districts with impact counts")
    private Map<String, Long> topAffectedDistricts;

    @Schema(description = "Top crops affected by climate impacts")
    private Map<String, Long> topAffectedCrops;

    @Schema(description = "Monthly distribution of impacts")
    private Map<Integer, Long> monthlyDistribution;

    @Schema(description = "Average recovery time in days", example = "65")
    private Double averageRecoveryTime;

    @Schema(description = "Percentage of impacts with media coverage", example = "35.5")
    private Double mediaCoveragePercentage;

    @Schema(description = "Percentage of impacts with research studies", example = "22.2")
    private Double researchStudiesPercentage;

    @Schema(description = "Number of high-risk impacts (severe/extreme)", example = "18")
    private Long highRiskImpacts;

    @Schema(description = "Number of low-risk impacts (mild/moderate)", example = "27")
    private Long lowRiskImpacts;

    @Schema(description = "Impacts requiring emergency response", example = "15")
    private Long emergencyResponseRequired;

    @Schema(description = "Weather-related impacts count", example = "38")
    private Long weatherRelatedImpacts;

    @Schema(description = "Biological-related impacts count (pests/diseases)", example = "7")
    private Long biologicalRelatedImpacts;

    @Schema(description = "Summary generation timestamp")
    private LocalDateTime generatedAt;

    @Schema(description = "Additional metadata")
    private Map<String, Object> metadata;

    // Constructors
    public ClimateImpactSummaryDTO() {
        this.eventTypeCounts = new HashMap<>();
        this.seasonalCounts = new HashMap<>();
        this.intensityCounts = new HashMap<>();
        this.severityCounts = new HashMap<>();
        this.economicLossByEventType = new HashMap<>();
        this.economicLossBySeason = new HashMap<>();
        this.economicLossByIntensity = new HashMap<>();
        this.warningEffectivenessStats = new HashMap<>();
        this.responseEffectivenessStats = new HashMap<>();
        this.topAffectedDistricts = new HashMap<>();
        this.topAffectedCrops = new HashMap<>();
        this.monthlyDistribution = new HashMap<>();
        this.metadata = new HashMap<>();
        this.generatedAt = LocalDateTime.now();
    }

    public ClimateImpactSummaryDTO(String region, Integer year) {
        this();
        this.region = region;
        this.year = year;
    }

    // Utility methods
    public Double getVerificationRate() {
        if (totalImpacts == null || totalImpacts == 0) return 0.0;
        if (verifiedImpacts == null) return 0.0;
        return (verifiedImpacts.doubleValue() / totalImpacts.doubleValue()) * 100;
    }

    public Double getHighRiskPercentage() {
        if (totalImpacts == null || totalImpacts == 0) return 0.0;
        if (highRiskImpacts == null) return 0.0;
        return (highRiskImpacts.doubleValue() / totalImpacts.doubleValue()) * 100;
    }

    public Double getEmergencyResponsePercentage() {
        if (totalImpacts == null || totalImpacts == 0) return 0.0;
        if (emergencyResponseRequired == null) return 0.0;
        return (emergencyResponseRequired.doubleValue() / totalImpacts.doubleValue()) * 100;
    }

    public BigDecimal getFinancialSupportCoverage() {
        BigDecimal totalSupport = BigDecimal.ZERO;
        if (totalInsurancePayouts != null) totalSupport = totalSupport.add(totalInsurancePayouts);
        if (totalGovernmentAssistance != null) totalSupport = totalSupport.add(totalGovernmentAssistance);
        if (totalInternationalAid != null) totalSupport = totalSupport.add(totalInternationalAid);

        if (totalEconomicLoss == null || totalEconomicLoss.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return totalSupport.divide(totalEconomicLoss, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    public String getMostFrequentEventType() {
        if (eventTypeCounts.isEmpty()) return "N/A";
        return eventTypeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey().getDisplayName())
                .orElse("N/A");
    }

    public String getMostAffectedSeason() {
        if (seasonalCounts.isEmpty()) return "N/A";
        return seasonalCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey().getDisplayName())
                .orElse("N/A");
    }

    public String getDominantIntensity() {
        if (intensityCounts.isEmpty()) return "N/A";
        return intensityCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey().getDisplayName())
                .orElse("N/A");
    }

    public String getSummaryDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append("Climate Impact Summary for ");
        if (region != null) desc.append(region);
        if (district != null) desc.append(", ").append(district);
        if (year != null) desc.append(" (").append(year).append(")");
        if (season != null) desc.append(" - ").append(season.getDisplayName());

        desc.append(": ").append(totalImpacts != null ? totalImpacts : 0).append(" total impacts");

        if (totalEconomicLoss != null) {
            desc.append(", Economic Loss: ").append(String.format("%.2f", totalEconomicLoss));
        }

        return desc.toString();
    }

    // Calculate derived statistics
    public void calculateDerivedStats() {
        // Calculate averages
        if (totalImpacts != null && totalImpacts > 0) {
            if (totalEconomicLoss != null) {
                this.averageEconomicLoss = totalEconomicLoss.divide(
                        new BigDecimal(totalImpacts), 2, BigDecimal.ROUND_HALF_UP);
            }

            if (totalAffectedArea != null) {
                this.averageAffectedArea = totalAffectedArea.divide(
                        new BigDecimal(totalImpacts), 2, BigDecimal.ROUND_HALF_UP);
            }

            if (totalAffectedPopulation != null) {
                this.averageAffectedPopulation = totalAffectedPopulation / totalImpacts;
            }

            if (totalProductionLoss != null) {
                this.averageProductionLoss = totalProductionLoss.divide(
                        new BigDecimal(totalImpacts), 2, BigDecimal.ROUND_HALF_UP);
            }
        }

        // Calculate risk distribution
        if (highRiskImpacts != null && lowRiskImpacts != null) {
            Long calculatedTotal = highRiskImpacts + lowRiskImpacts;
            if (totalImpacts == null) {
                this.totalImpacts = calculatedTotal;
            }
        }
    }

    // Add metadata entry
    public void addMetadata(String key, Object value) {
        if (metadata == null) {
            metadata = new HashMap<>();
        }
        metadata.put(key, value);
    }

    // Getters and Setters
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public ClimateImpact.Season getSeason() {
        return season;
    }

    public void setSeason(ClimateImpact.Season season) {
        this.season = season;
    }

    public Long getTotalImpacts() {
        return totalImpacts;
    }

    public void setTotalImpacts(Long totalImpacts) {
        this.totalImpacts = totalImpacts;
    }

    public Long getVerifiedImpacts() {
        return verifiedImpacts;
    }

    public void setVerifiedImpacts(Long verifiedImpacts) {
        this.verifiedImpacts = verifiedImpacts;
    }

    public Long getUnverifiedImpacts() {
        return unverifiedImpacts;
    }

    public void setUnverifiedImpacts(Long unverifiedImpacts) {
        this.unverifiedImpacts = unverifiedImpacts;
    }

    public Long getOngoingEvents() {
        return ongoingEvents;
    }

    public void setOngoingEvents(Long ongoingEvents) {
        this.ongoingEvents = ongoingEvents;
    }

    public Long getRecentEvents() {
        return recentEvents;
    }

    public void setRecentEvents(Long recentEvents) {
        this.recentEvents = recentEvents;
    }

    public BigDecimal getTotalEconomicLoss() {
        return totalEconomicLoss;
    }

    public void setTotalEconomicLoss(BigDecimal totalEconomicLoss) {
        this.totalEconomicLoss = totalEconomicLoss;
    }

    public BigDecimal getAverageEconomicLoss() {
        return averageEconomicLoss;
    }

    public void setAverageEconomicLoss(BigDecimal averageEconomicLoss) {
        this.averageEconomicLoss = averageEconomicLoss;
    }

    public BigDecimal getMaxEconomicLoss() {
        return maxEconomicLoss;
    }

    public void setMaxEconomicLoss(BigDecimal maxEconomicLoss) {
        this.maxEconomicLoss = maxEconomicLoss;
    }

    public BigDecimal getMinEconomicLoss() {
        return minEconomicLoss;
    }

    public void setMinEconomicLoss(BigDecimal minEconomicLoss) {
        this.minEconomicLoss = minEconomicLoss;
    }

    public BigDecimal getTotalAffectedArea() {
        return totalAffectedArea;
    }

    public void setTotalAffectedArea(BigDecimal totalAffectedArea) {
        this.totalAffectedArea = totalAffectedArea;
    }

    public BigDecimal getAverageAffectedArea() {
        return averageAffectedArea;
    }

    public void setAverageAffectedArea(BigDecimal averageAffectedArea) {
        this.averageAffectedArea = averageAffectedArea;
    }

    public Long getTotalAffectedPopulation() {
        return totalAffectedPopulation;
    }

    public void setTotalAffectedPopulation(Long totalAffectedPopulation) {
        this.totalAffectedPopulation = totalAffectedPopulation;
    }

    public Long getAverageAffectedPopulation() {
        return averageAffectedPopulation;
    }

    public void setAverageAffectedPopulation(Long averageAffectedPopulation) {
        this.averageAffectedPopulation = averageAffectedPopulation;
    }

    public Long getTotalAffectedHouseholds() {
        return totalAffectedHouseholds;
    }

    public void setTotalAffectedHouseholds(Long totalAffectedHouseholds) {
        this.totalAffectedHouseholds = totalAffectedHouseholds;
    }

    public BigDecimal getTotalCropAreaAffected() {
        return totalCropAreaAffected;
    }

    public void setTotalCropAreaAffected(BigDecimal totalCropAreaAffected) {
        this.totalCropAreaAffected = totalCropAreaAffected;
    }

    public Long getTotalLivestockAffected() {
        return totalLivestockAffected;
    }

    public void setTotalLivestockAffected(Long totalLivestockAffected) {
        this.totalLivestockAffected = totalLivestockAffected;
    }

    public BigDecimal getTotalProductionLoss() {
        return totalProductionLoss;
    }

    public void setTotalProductionLoss(BigDecimal totalProductionLoss) {
        this.totalProductionLoss = totalProductionLoss;
    }

    public BigDecimal getAverageProductionLoss() {
        return averageProductionLoss;
    }

    public void setAverageProductionLoss(BigDecimal averageProductionLoss) {
        this.averageProductionLoss = averageProductionLoss;
    }

    public BigDecimal getTotalRecoveryCost() {
        return totalRecoveryCost;
    }

    public void setTotalRecoveryCost(BigDecimal totalRecoveryCost) {
        this.totalRecoveryCost = totalRecoveryCost;
    }

    public BigDecimal getTotalInsurancePayouts() {
        return totalInsurancePayouts;
    }

    public void setTotalInsurancePayouts(BigDecimal totalInsurancePayouts) {
        this.totalInsurancePayouts = totalInsurancePayouts;
    }

    public BigDecimal getTotalGovernmentAssistance() {
        return totalGovernmentAssistance;
    }

    public void setTotalGovernmentAssistance(BigDecimal totalGovernmentAssistance) {
        this.totalGovernmentAssistance = totalGovernmentAssistance;
    }

    public BigDecimal getTotalInternationalAid() {
        return totalInternationalAid;
    }

    public void setTotalInternationalAid(BigDecimal totalInternationalAid) {
        this.totalInternationalAid = totalInternationalAid;
    }

    public Map<ClimateImpact.ClimateEvent, Long> getEventTypeCounts() {
        return eventTypeCounts;
    }

    public void setEventTypeCounts(Map<ClimateImpact.ClimateEvent, Long> eventTypeCounts) {
        this.eventTypeCounts = eventTypeCounts;
    }

    public Map<ClimateImpact.Season, Long> getSeasonalCounts() {
        return seasonalCounts;
    }

    public void setSeasonalCounts(Map<ClimateImpact.Season, Long> seasonalCounts) {
        this.seasonalCounts = seasonalCounts;
    }

    public Map<ClimateImpact.EventIntensity, Long> getIntensityCounts() {
        return intensityCounts;
    }

    public void setIntensityCounts(Map<ClimateImpact.EventIntensity, Long> intensityCounts) {
        this.intensityCounts = intensityCounts;
    }

    public Map<ClimateImpact.ImpactSeverity, Long> getSeverityCounts() {
        return severityCounts;
    }

    public void setSeverityCounts(Map<ClimateImpact.ImpactSeverity, Long> severityCounts) {
        this.severityCounts = severityCounts;
    }

    public Map<ClimateImpact.ClimateEvent, BigDecimal> getEconomicLossByEventType() {
        return economicLossByEventType;
    }

    public void setEconomicLossByEventType(Map<ClimateImpact.ClimateEvent, BigDecimal> economicLossByEventType) {
        this.economicLossByEventType = economicLossByEventType;
    }

    public Map<ClimateImpact.Season, BigDecimal> getEconomicLossBySeason() {
        return economicLossBySeason;
    }

    public void setEconomicLossBySeason(Map<ClimateImpact.Season, BigDecimal> economicLossBySeason) {
        this.economicLossBySeason = economicLossBySeason;
    }

    public Map<ClimateImpact.EventIntensity, BigDecimal> getEconomicLossByIntensity() {
        return economicLossByIntensity;
    }

    public void setEconomicLossByIntensity(Map<ClimateImpact.EventIntensity, BigDecimal> economicLossByIntensity) {
        this.economicLossByIntensity = economicLossByIntensity;
    }

    public Map<ClimateImpact.WarningEffectiveness, Long> getWarningEffectivenessStats() {
        return warningEffectivenessStats;
    }

    public void setWarningEffectivenessStats(Map<ClimateImpact.WarningEffectiveness, Long> warningEffectivenessStats) {
        this.warningEffectivenessStats = warningEffectivenessStats;
    }

    public Map<ClimateImpact.ResponseEffectiveness, Long> getResponseEffectivenessStats() {
        return responseEffectivenessStats;
    }

    public void setResponseEffectivenessStats(Map<ClimateImpact.ResponseEffectiveness, Long> responseEffectivenessStats) {
        this.responseEffectivenessStats = responseEffectivenessStats;
    }

    public Map<String, Long> getTopAffectedDistricts() {
        return topAffectedDistricts;
    }

    public void setTopAffectedDistricts(Map<String, Long> topAffectedDistricts) {
        this.topAffectedDistricts = topAffectedDistricts;
    }

    public Map<String, Long> getTopAffectedCrops() {
        return topAffectedCrops;
    }

    public void setTopAffectedCrops(Map<String, Long> topAffectedCrops) {
        this.topAffectedCrops = topAffectedCrops;
    }

    public Map<Integer, Long> getMonthlyDistribution() {
        return monthlyDistribution;
    }

    public void setMonthlyDistribution(Map<Integer, Long> monthlyDistribution) {
        this.monthlyDistribution = monthlyDistribution;
    }

    public Double getAverageRecoveryTime() {
        return averageRecoveryTime;
    }

    public void setAverageRecoveryTime(Double averageRecoveryTime) {
        this.averageRecoveryTime = averageRecoveryTime;
    }

    public Double getMediaCoveragePercentage() {
        return mediaCoveragePercentage;
    }

    public void setMediaCoveragePercentage(Double mediaCoveragePercentage) {
        this.mediaCoveragePercentage = mediaCoveragePercentage;
    }

    public Double getResearchStudiesPercentage() {
        return researchStudiesPercentage;
    }

    public void setResearchStudiesPercentage(Double researchStudiesPercentage) {
        this.researchStudiesPercentage = researchStudiesPercentage;
    }

    public Long getHighRiskImpacts() {
        return highRiskImpacts;
    }

    public void setHighRiskImpacts(Long highRiskImpacts) {
        this.highRiskImpacts = highRiskImpacts;
    }

    public Long getLowRiskImpacts() {
        return lowRiskImpacts;
    }

    public void setLowRiskImpacts(Long lowRiskImpacts) {
        this.lowRiskImpacts = lowRiskImpacts;
    }

    public Long getEmergencyResponseRequired() {
        return emergencyResponseRequired;
    }

    public void setEmergencyResponseRequired(Long emergencyResponseRequired) {
        this.emergencyResponseRequired = emergencyResponseRequired;
    }

    public Long getWeatherRelatedImpacts() {
        return weatherRelatedImpacts;
    }

    public void setWeatherRelatedImpacts(Long weatherRelatedImpacts) {
        this.weatherRelatedImpacts = weatherRelatedImpacts;
    }

    public Long getBiologicalRelatedImpacts() {
        return biologicalRelatedImpacts;
    }

    public void setBiologicalRelatedImpacts(Long biologicalRelatedImpacts) {
        this.biologicalRelatedImpacts = biologicalRelatedImpacts;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    // toString method
    @Override
    public String toString() {
        return "ClimateImpactSummaryDTO{" +
                "region='" + region + '\'' +
                ", year=" + year +
                ", district='" + district + '\'' +
                ", season=" + season +
                ", totalImpacts=" + totalImpacts +
                ", totalEconomicLoss=" + totalEconomicLoss +
                ", totalAffectedArea=" + totalAffectedArea +
                ", generatedAt=" + generatedAt +
                '}';
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClimateImpactSummaryDTO that = (ClimateImpactSummaryDTO) o;
        return Objects.equals(region, that.region) &&
                Objects.equals(year, that.year) &&
                Objects.equals(district, that.district) &&
                season == that.season &&
                Objects.equals(generatedAt, that.generatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(region, year, district, season, generatedAt);
    }
}