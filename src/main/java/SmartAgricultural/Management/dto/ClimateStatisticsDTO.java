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
@Schema(description = "Climate Statistics Data Transfer Object")
public class ClimateStatisticsDTO {

    @Schema(description = "Total number of climate impacts recorded", example = "1250")
    private Long totalImpacts;

    @Schema(description = "Number of verified climate impacts", example = "1180")
    private Long verifiedImpacts;

    @Schema(description = "Number of unverified climate impacts", example = "70")
    private Long unverifiedImpacts;

    @Schema(description = "Number of climate impacts in current year", example = "145")
    private Long currentYearImpacts;

    @Schema(description = "Number of climate impacts in previous year", example = "132")
    private Long previousYearImpacts;

    @Schema(description = "Total economic loss from all impacts", example = "15750000.50")
    private BigDecimal totalEconomicLoss;

    @Schema(description = "Economic loss for current year", example = "2250000.75")
    private BigDecimal currentYearEconomicLoss;

    @Schema(description = "Economic loss for previous year", example = "1980000.25")
    private BigDecimal previousYearEconomicLoss;

    @Schema(description = "Average economic loss per impact", example = "12600.25")
    private BigDecimal averageEconomicLoss;

    @Schema(description = "Total affected area in hectares", example = "125750.50")
    private BigDecimal totalAffectedArea;

    @Schema(description = "Average affected area per impact", example = "100.60")
    private BigDecimal averageAffectedArea;

    @Schema(description = "Total affected population", example = "98500")
    private Long totalAffectedPopulation;

    @Schema(description = "Total affected households", example = "24625")
    private Long totalAffectedHouseholds;

    @Schema(description = "Total livestock affected", example = "15400")
    private Long totalLivestockAffected;

    @Schema(description = "Total production loss in tonnes", example = "35420.75")
    private BigDecimal totalProductionLoss;

    @Schema(description = "Number of ongoing climate events", example = "8")
    private Long ongoingEvents;

    @Schema(description = "Number of recent events (last 30 days)", example = "25")
    private Long recentEvents;

    @Schema(description = "Number of high-risk impacts", example = "285")
    private Long highRiskImpacts;

    @Schema(description = "Number of impacts requiring emergency response", example = "156")
    private Long emergencyResponseRequired;

    @Schema(description = "Total recovery cost", example = "8500000.00")
    private BigDecimal totalRecoveryCost;

    @Schema(description = "Total insurance payouts", example = "5200000.00")
    private BigDecimal totalInsurancePayouts;

    @Schema(description = "Total government assistance", example = "7800000.00")
    private BigDecimal totalGovernmentAssistance;

    @Schema(description = "Total international aid", example = "1250000.00")
    private BigDecimal totalInternationalAid;

    @Schema(description = "Count of impacts by climate event type")
    private Map<ClimateImpact.ClimateEvent, Long> eventTypeDistribution;

    @Schema(description = "Count of impacts by season")
    private Map<ClimateImpact.Season, Long> seasonalDistribution;

    @Schema(description = "Count of impacts by intensity level")
    private Map<ClimateImpact.EventIntensity, Long> intensityDistribution;

    @Schema(description = "Count of impacts by severity level")
    private Map<ClimateImpact.ImpactSeverity, Long> severityDistribution;

    @Schema(description = "Count of impacts by region")
    private Map<String, Long> regionalDistribution;

    @Schema(description = "Economic loss by climate event type")
    private Map<ClimateImpact.ClimateEvent, BigDecimal> economicLossByEventType;

    @Schema(description = "Economic loss by season")
    private Map<ClimateImpact.Season, BigDecimal> economicLossBySeason;

    @Schema(description = "Economic loss by region")
    private Map<String, BigDecimal> economicLossByRegion;

    @Schema(description = "Yearly trend data (year -> count)")
    private Map<Integer, Long> yearlyTrends;

    @Schema(description = "Yearly economic loss trends (year -> loss)")
    private Map<Integer, BigDecimal> yearlyEconomicTrends;

    @Schema(description = "Monthly distribution of impacts (month -> count)")
    private Map<Integer, Long> monthlyDistribution;

    @Schema(description = "Warning effectiveness statistics")
    private Map<ClimateImpact.WarningEffectiveness, Long> warningEffectivenessStats;

    @Schema(description = "Response effectiveness statistics")
    private Map<ClimateImpact.ResponseEffectiveness, Long> responseEffectivenessStats;

    @Schema(description = "Top affected crops (cropId -> count)")
    private Map<String, Long> topAffectedCrops;

    @Schema(description = "Top affected districts (district -> count)")
    private Map<String, Long> topAffectedDistricts;

    @Schema(description = "Verification rate percentage", example = "94.4")
    private Double verificationRate;

    @Schema(description = "Year-over-year growth rate percentage", example = "9.8")
    private Double yearOverYearGrowthRate;

    @Schema(description = "Economic loss growth rate percentage", example = "13.6")
    private Double economicLossGrowthRate;

    @Schema(description = "High-risk impact percentage", example = "22.8")
    private Double highRiskPercentage;

    @Schema(description = "Emergency response rate percentage", example = "12.5")
    private Double emergencyResponseRate;

    @Schema(description = "Average recovery time in days", example = "72.5")
    private Double averageRecoveryTime;

    @Schema(description = "Financial support coverage rate percentage", example = "68.3")
    private Double financialSupportCoverage;

    @Schema(description = "Media coverage rate percentage", example = "35.2")
    private Double mediaCoverageRate;

    @Schema(description = "Research studies rate percentage", example = "18.7")
    private Double researchStudiesRate;

    @Schema(description = "Weather-related impacts count", example = "1085")
    private Long weatherRelatedImpacts;

    @Schema(description = "Biological-related impacts count", example = "165")
    private Long biologicalRelatedImpacts;

    @Schema(description = "Most frequent climate event")
    private ClimateImpact.ClimateEvent mostFrequentEvent;

    @Schema(description = "Most affected season")
    private ClimateImpact.Season mostAffectedSeason;

    @Schema(description = "Most affected region")
    private String mostAffectedRegion;

    @Schema(description = "Dominant event intensity")
    private ClimateImpact.EventIntensity dominantIntensity;

    @Schema(description = "Current year reference", example = "2024")
    private Integer currentYear;

    @Schema(description = "Previous year reference", example = "2023")
    private Integer previousYear;

    @Schema(description = "Statistics generation timestamp")
    private LocalDateTime generatedAt;

    @Schema(description = "Data quality indicators")
    private Map<String, Object> dataQualityMetrics;

    @Schema(description = "Additional metadata and notes")
    private Map<String, Object> metadata;

    // Constructors
    public ClimateStatisticsDTO() {
        this.eventTypeDistribution = new HashMap<>();
        this.seasonalDistribution = new HashMap<>();
        this.intensityDistribution = new HashMap<>();
        this.severityDistribution = new HashMap<>();
        this.regionalDistribution = new HashMap<>();
        this.economicLossByEventType = new HashMap<>();
        this.economicLossBySeason = new HashMap<>();
        this.economicLossByRegion = new HashMap<>();
        this.yearlyTrends = new HashMap<>();
        this.yearlyEconomicTrends = new HashMap<>();
        this.monthlyDistribution = new HashMap<>();
        this.warningEffectivenessStats = new HashMap<>();
        this.responseEffectivenessStats = new HashMap<>();
        this.topAffectedCrops = new HashMap<>();
        this.topAffectedDistricts = new HashMap<>();
        this.dataQualityMetrics = new HashMap<>();
        this.metadata = new HashMap<>();
        this.generatedAt = LocalDateTime.now();
        this.currentYear = LocalDateTime.now().getYear();
        this.previousYear = this.currentYear - 1;
    }

    // Utility methods for calculations
    public void calculateDerivedStatistics() {
        // Calculate verification rate
        if (totalImpacts != null && totalImpacts > 0 && verifiedImpacts != null) {
            this.verificationRate = (verifiedImpacts.doubleValue() / totalImpacts.doubleValue()) * 100;
        }

        // Calculate year-over-year growth rate
        if (currentYearImpacts != null && previousYearImpacts != null && previousYearImpacts > 0) {
            this.yearOverYearGrowthRate =
                    ((currentYearImpacts.doubleValue() - previousYearImpacts.doubleValue()) /
                            previousYearImpacts.doubleValue()) * 100;
        }

        // Calculate economic loss growth rate
        if (currentYearEconomicLoss != null && previousYearEconomicLoss != null &&
                previousYearEconomicLoss.compareTo(BigDecimal.ZERO) > 0) {
            this.economicLossGrowthRate =
                    currentYearEconomicLoss.subtract(previousYearEconomicLoss)
                            .divide(previousYearEconomicLoss, 4, BigDecimal.ROUND_HALF_UP)
                            .multiply(new BigDecimal("100")).doubleValue();
        }

        // Calculate high-risk percentage
        if (totalImpacts != null && totalImpacts > 0 && highRiskImpacts != null) {
            this.highRiskPercentage = (highRiskImpacts.doubleValue() / totalImpacts.doubleValue()) * 100;
        }

        // Calculate emergency response rate
        if (totalImpacts != null && totalImpacts > 0 && emergencyResponseRequired != null) {
            this.emergencyResponseRate = (emergencyResponseRequired.doubleValue() / totalImpacts.doubleValue()) * 100;
        }

        // Calculate average economic loss
        if (totalImpacts != null && totalImpacts > 0 && totalEconomicLoss != null) {
            this.averageEconomicLoss = totalEconomicLoss.divide(
                    new BigDecimal(totalImpacts), 2, BigDecimal.ROUND_HALF_UP);
        }

        // Calculate average affected area
        if (totalImpacts != null && totalImpacts > 0 && totalAffectedArea != null) {
            this.averageAffectedArea = totalAffectedArea.divide(
                    new BigDecimal(totalImpacts), 2, BigDecimal.ROUND_HALF_UP);
        }

        // Calculate financial support coverage
        if (totalEconomicLoss != null && totalEconomicLoss.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal totalSupport = BigDecimal.ZERO;
            if (totalInsurancePayouts != null) totalSupport = totalSupport.add(totalInsurancePayouts);
            if (totalGovernmentAssistance != null) totalSupport = totalSupport.add(totalGovernmentAssistance);
            if (totalInternationalAid != null) totalSupport = totalSupport.add(totalInternationalAid);

            this.financialSupportCoverage = totalSupport.divide(totalEconomicLoss, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100")).doubleValue();
        }

        // Determine most frequent categories
        if (!eventTypeDistribution.isEmpty()) {
            this.mostFrequentEvent = eventTypeDistribution.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);
        }

        if (!seasonalDistribution.isEmpty()) {
            this.mostAffectedSeason = seasonalDistribution.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);
        }

        if (!regionalDistribution.isEmpty()) {
            this.mostAffectedRegion = regionalDistribution.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);
        }

        if (!intensityDistribution.isEmpty()) {
            this.dominantIntensity = intensityDistribution.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);
        }
    }

    public void addDataQualityMetric(String metric, Object value) {
        if (dataQualityMetrics == null) {
            dataQualityMetrics = new HashMap<>();
        }
        dataQualityMetrics.put(metric, value);
    }

    public void addMetadata(String key, Object value) {
        if (metadata == null) {
            metadata = new HashMap<>();
        }
        metadata.put(key, value);
    }

    public String getSummaryDescription() {
        StringBuilder summary = new StringBuilder();
        summary.append("Climate Impact Statistics Summary: ");
        summary.append(totalImpacts != null ? totalImpacts : 0).append(" total impacts recorded");

        if (verificationRate != null) {
            summary.append(String.format(" (%.1f%% verified)", verificationRate));
        }

        if (currentYearImpacts != null) {
            summary.append(", ").append(currentYearImpacts).append(" in current year");
        }

        if (yearOverYearGrowthRate != null) {
            summary.append(String.format(" (%.1f%% YoY growth)", yearOverYearGrowthRate));
        }

        if (totalEconomicLoss != null) {
            summary.append(String.format(", Total Economic Loss: %.2f", totalEconomicLoss));
        }

        return summary.toString();
    }

    public boolean hasSignificantGrowth() {
        return yearOverYearGrowthRate != null && yearOverYearGrowthRate > 10.0;
    }

    public boolean hasHighVerificationRate() {
        return verificationRate != null && verificationRate > 90.0;
    }

    public boolean hasAdequateFinancialSupport() {
        return financialSupportCoverage != null && financialSupportCoverage > 50.0;
    }

    public String getRiskAssessment() {
        if (highRiskPercentage == null) return "Unknown";
        if (highRiskPercentage > 30.0) return "High Risk";
        if (highRiskPercentage > 15.0) return "Moderate Risk";
        return "Low Risk";
    }

    // Getters and Setters
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

    public Long getCurrentYearImpacts() {
        return currentYearImpacts;
    }

    public void setCurrentYearImpacts(Long currentYearImpacts) {
        this.currentYearImpacts = currentYearImpacts;
    }

    public Long getPreviousYearImpacts() {
        return previousYearImpacts;
    }

    public void setPreviousYearImpacts(Long previousYearImpacts) {
        this.previousYearImpacts = previousYearImpacts;
    }

    public BigDecimal getTotalEconomicLoss() {
        return totalEconomicLoss;
    }

    public void setTotalEconomicLoss(BigDecimal totalEconomicLoss) {
        this.totalEconomicLoss = totalEconomicLoss;
    }

    public BigDecimal getCurrentYearEconomicLoss() {
        return currentYearEconomicLoss;
    }

    public void setCurrentYearEconomicLoss(BigDecimal currentYearEconomicLoss) {
        this.currentYearEconomicLoss = currentYearEconomicLoss;
    }

    public BigDecimal getPreviousYearEconomicLoss() {
        return previousYearEconomicLoss;
    }

    public void setPreviousYearEconomicLoss(BigDecimal previousYearEconomicLoss) {
        this.previousYearEconomicLoss = previousYearEconomicLoss;
    }

    public BigDecimal getAverageEconomicLoss() {
        return averageEconomicLoss;
    }

    public void setAverageEconomicLoss(BigDecimal averageEconomicLoss) {
        this.averageEconomicLoss = averageEconomicLoss;
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

    public Long getTotalAffectedHouseholds() {
        return totalAffectedHouseholds;
    }

    public void setTotalAffectedHouseholds(Long totalAffectedHouseholds) {
        this.totalAffectedHouseholds = totalAffectedHouseholds;
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

    public Long getHighRiskImpacts() {
        return highRiskImpacts;
    }

    public void setHighRiskImpacts(Long highRiskImpacts) {
        this.highRiskImpacts = highRiskImpacts;
    }

    public Long getEmergencyResponseRequired() {
        return emergencyResponseRequired;
    }

    public void setEmergencyResponseRequired(Long emergencyResponseRequired) {
        this.emergencyResponseRequired = emergencyResponseRequired;
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

    public Map<ClimateImpact.ClimateEvent, Long> getEventTypeDistribution() {
        return eventTypeDistribution;
    }

    public void setEventTypeDistribution(Map<ClimateImpact.ClimateEvent, Long> eventTypeDistribution) {
        this.eventTypeDistribution = eventTypeDistribution;
    }

    public Map<ClimateImpact.Season, Long> getSeasonalDistribution() {
        return seasonalDistribution;
    }

    public void setSeasonalDistribution(Map<ClimateImpact.Season, Long> seasonalDistribution) {
        this.seasonalDistribution = seasonalDistribution;
    }

    public Map<ClimateImpact.EventIntensity, Long> getIntensityDistribution() {
        return intensityDistribution;
    }

    public void setIntensityDistribution(Map<ClimateImpact.EventIntensity, Long> intensityDistribution) {
        this.intensityDistribution = intensityDistribution;
    }

    public Map<ClimateImpact.ImpactSeverity, Long> getSeverityDistribution() {
        return severityDistribution;
    }

    public void setSeverityDistribution(Map<ClimateImpact.ImpactSeverity, Long> severityDistribution) {
        this.severityDistribution = severityDistribution;
    }

    public Map<String, Long> getRegionalDistribution() {
        return regionalDistribution;
    }

    public void setRegionalDistribution(Map<String, Long> regionalDistribution) {
        this.regionalDistribution = regionalDistribution;
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

    public Map<String, BigDecimal> getEconomicLossByRegion() {
        return economicLossByRegion;
    }

    public void setEconomicLossByRegion(Map<String, BigDecimal> economicLossByRegion) {
        this.economicLossByRegion = economicLossByRegion;
    }

    public Map<Integer, Long> getYearlyTrends() {
        return yearlyTrends;
    }

    public void setYearlyTrends(Map<Integer, Long> yearlyTrends) {
        this.yearlyTrends = yearlyTrends;
    }

    public Map<Integer, BigDecimal> getYearlyEconomicTrends() {
        return yearlyEconomicTrends;
    }

    public void setYearlyEconomicTrends(Map<Integer, BigDecimal> yearlyEconomicTrends) {
        this.yearlyEconomicTrends = yearlyEconomicTrends;
    }

    public Map<Integer, Long> getMonthlyDistribution() {
        return monthlyDistribution;
    }

    public void setMonthlyDistribution(Map<Integer, Long> monthlyDistribution) {
        this.monthlyDistribution = monthlyDistribution;
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

    public Map<String, Long> getTopAffectedCrops() {
        return topAffectedCrops;
    }

    public void setTopAffectedCrops(Map<String, Long> topAffectedCrops) {
        this.topAffectedCrops = topAffectedCrops;
    }

    public Map<String, Long> getTopAffectedDistricts() {
        return topAffectedDistricts;
    }

    public void setTopAffectedDistricts(Map<String, Long> topAffectedDistricts) {
        this.topAffectedDistricts = topAffectedDistricts;
    }

    public Double getVerificationRate() {
        return verificationRate;
    }

    public void setVerificationRate(Double verificationRate) {
        this.verificationRate = verificationRate;
    }

    public Double getYearOverYearGrowthRate() {
        return yearOverYearGrowthRate;
    }

    public void setYearOverYearGrowthRate(Double yearOverYearGrowthRate) {
        this.yearOverYearGrowthRate = yearOverYearGrowthRate;
    }

    public Double getEconomicLossGrowthRate() {
        return economicLossGrowthRate;
    }

    public void setEconomicLossGrowthRate(Double economicLossGrowthRate) {
        this.economicLossGrowthRate = economicLossGrowthRate;
    }

    public Double getHighRiskPercentage() {
        return highRiskPercentage;
    }

    public void setHighRiskPercentage(Double highRiskPercentage) {
        this.highRiskPercentage = highRiskPercentage;
    }

    public Double getEmergencyResponseRate() {
        return emergencyResponseRate;
    }

    public void setEmergencyResponseRate(Double emergencyResponseRate) {
        this.emergencyResponseRate = emergencyResponseRate;
    }

    public Double getAverageRecoveryTime() {
        return averageRecoveryTime;
    }

    public void setAverageRecoveryTime(Double averageRecoveryTime) {
        this.averageRecoveryTime = averageRecoveryTime;
    }

    public Double getFinancialSupportCoverage() {
        return financialSupportCoverage;
    }

    public void setFinancialSupportCoverage(Double financialSupportCoverage) {
        this.financialSupportCoverage = financialSupportCoverage;
    }

    public Double getMediaCoverageRate() {
        return mediaCoverageRate;
    }

    public void setMediaCoverageRate(Double mediaCoverageRate) {
        this.mediaCoverageRate = mediaCoverageRate;
    }

    public Double getResearchStudiesRate() {
        return researchStudiesRate;
    }

    public void setResearchStudiesRate(Double researchStudiesRate) {
        this.researchStudiesRate = researchStudiesRate;
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

    public ClimateImpact.ClimateEvent getMostFrequentEvent() {
        return mostFrequentEvent;
    }

    public void setMostFrequentEvent(ClimateImpact.ClimateEvent mostFrequentEvent) {
        this.mostFrequentEvent = mostFrequentEvent;
    }

    public ClimateImpact.Season getMostAffectedSeason() {
        return mostAffectedSeason;
    }

    public void setMostAffectedSeason(ClimateImpact.Season mostAffectedSeason) {
        this.mostAffectedSeason = mostAffectedSeason;
    }

    public String getMostAffectedRegion() {
        return mostAffectedRegion;
    }

    public void setMostAffectedRegion(String mostAffectedRegion) {
        this.mostAffectedRegion = mostAffectedRegion;
    }

    public ClimateImpact.EventIntensity getDominantIntensity() {
        return dominantIntensity;
    }

    public void setDominantIntensity(ClimateImpact.EventIntensity dominantIntensity) {
        this.dominantIntensity = dominantIntensity;
    }

    public Integer getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(Integer currentYear) {
        this.currentYear = currentYear;
    }

    public Integer getPreviousYear() {
        return previousYear;
    }

    public void setPreviousYear(Integer previousYear) {
        this.previousYear = previousYear;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public Map<String, Object> getDataQualityMetrics() {
        return dataQualityMetrics;
    }

    public void setDataQualityMetrics(Map<String, Object> dataQualityMetrics) {
        this.dataQualityMetrics = dataQualityMetrics;
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
        return "ClimateStatisticsDTO{" +
                "totalImpacts=" + totalImpacts +
                ", verifiedImpacts=" + verifiedImpacts +
                ", currentYearImpacts=" + currentYearImpacts +
                ", totalEconomicLoss=" + totalEconomicLoss +
                ", mostFrequentEvent=" + mostFrequentEvent +
                ", mostAffectedSeason=" + mostAffectedSeason +
                ", mostAffectedRegion='" + mostAffectedRegion + '\'' +
                ", verificationRate=" + verificationRate +
                ", yearOverYearGrowthRate=" + yearOverYearGrowthRate +
                ", highRiskPercentage=" + highRiskPercentage +
                ", generatedAt=" + generatedAt +
                '}';
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClimateStatisticsDTO that = (ClimateStatisticsDTO) o;
        return Objects.equals(totalImpacts, that.totalImpacts) &&
                Objects.equals(currentYear, that.currentYear) &&
                Objects.equals(generatedAt, that.generatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalImpacts, currentYear, generatedAt);
    }
}