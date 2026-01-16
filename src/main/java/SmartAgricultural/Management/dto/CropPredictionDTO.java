package SmartAgricultural.Management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CropPredictionDTO {

    private String cropId;
    private String cropName;
    private BigDecimal predictedYield;
    private String yieldUnit;
    private Double confidenceScore; // 0-100%
    private Double riskScore; // 0-100%
    private Double qualityScore; // 0-100%
    private LocalDateTime predictionDate;
    private LocalDate optimalHarvestDate;
    private String[] recommendations;
    private String weatherImpact;
    private Double soilHealthScore;
    private String pestRiskLevel; // LOW, MEDIUM, HIGH
    private BigDecimal expectedProfit;
    private BigDecimal marketPrice;
    private String status; // EXCELLENT, GOOD, AVERAGE, POOR

    // Constructeurs
    public CropPredictionDTO() {}

    // Getters et Setters
    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public BigDecimal getPredictedYield() {
        return predictedYield;
    }

    public void setPredictedYield(BigDecimal predictedYield) {
        this.predictedYield = predictedYield;
    }

    public String getYieldUnit() {
        return yieldUnit;
    }

    public void setYieldUnit(String yieldUnit) {
        this.yieldUnit = yieldUnit;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }

    public Double getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(Double qualityScore) {
        this.qualityScore = qualityScore;
    }

    public LocalDateTime getPredictionDate() {
        return predictionDate;
    }

    public void setPredictionDate(LocalDateTime predictionDate) {
        this.predictionDate = predictionDate;
    }

    public LocalDate getOptimalHarvestDate() {
        return optimalHarvestDate;
    }

    public void setOptimalHarvestDate(LocalDate optimalHarvestDate) {
        this.optimalHarvestDate = optimalHarvestDate;
    }

    public String[] getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String[] recommendations) {
        this.recommendations = recommendations;
    }

    public String getWeatherImpact() {
        return weatherImpact;
    }

    public void setWeatherImpact(String weatherImpact) {
        this.weatherImpact = weatherImpact;
    }

    public Double getSoilHealthScore() {
        return soilHealthScore;
    }

    public void setSoilHealthScore(Double soilHealthScore) {
        this.soilHealthScore = soilHealthScore;
    }

    public String getPestRiskLevel() {
        return pestRiskLevel;
    }

    public void setPestRiskLevel(String pestRiskLevel) {
        this.pestRiskLevel = pestRiskLevel;
    }

    public BigDecimal getExpectedProfit() {
        return expectedProfit;
    }

    public void setExpectedProfit(BigDecimal expectedProfit) {
        this.expectedProfit = expectedProfit;
    }

    public BigDecimal getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(BigDecimal marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Méthode utilitaire pour calculer le status basé sur les scores
    public void calculateStatus() {
        if (confidenceScore == null || qualityScore == null) {
            this.status = "UNKNOWN";
            return;
        }

        double avgScore = (confidenceScore + qualityScore) / 2.0;

        if (avgScore >= 85) {
            this.status = "EXCELLENT";
        } else if (avgScore >= 70) {
            this.status = "GOOD";
        } else if (avgScore >= 50) {
            this.status = "AVERAGE";
        } else {
            this.status = "POOR";
        }
    }
}