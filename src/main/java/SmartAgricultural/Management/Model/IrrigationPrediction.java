package SmartAgricultural.Management.Model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "irrigation_predictions")
public class IrrigationPrediction {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "farm_id", length = 20, nullable = false)
    private String farmId;

    @Column(name = "crop_production_id", length = 20)
    private String cropProductionId;

    @Column(name = "prediction_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime predictionDate;

    @Column(name = "predicted_water_need", precision = 10, scale = 2)
    private BigDecimal predictedWaterNeed; // Litres

    @Column(name = "predicted_irrigation_frequency")
    private Integer predictedIrrigationFrequency; // times per week

    @Enumerated(EnumType.STRING)
    @Column(name = "recommended_method")
    private IrrigationData.IrrigationMethod recommendedMethod;

    @Column(name = "water_stress_risk", precision = 5, scale = 2)
    private BigDecimal waterStressRisk; // 0-100%

    @Column(name = "predicted_yield_impact", precision = 5, scale = 2)
    private BigDecimal predictedYieldImpact; // percentage impact on yield

    @Column(name = "optimal_duration")
    private Integer optimalDuration; // minutes

    @Column(name = "cost_estimation", precision = 10, scale = 2)
    private BigDecimal costEstimation;

    @Column(name = "confidence_level", precision = 5, scale = 2)
    private BigDecimal confidenceLevel; // 0-100%

    @Column(name = "weather_factor", precision = 5, scale = 2)
    private BigDecimal weatherFactor;

    @Column(name = "soil_moisture_target", precision = 4, scale = 2)
    private BigDecimal soilMoistureTarget;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_level")
    private AlertLevel alertLevel;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "based_on_historical_days")
    private Integer basedOnHistoricalDays;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum AlertLevel {
        LOW("Low - Normal irrigation needed"),
        MODERATE("Moderate - Increase monitoring"),
        HIGH("High - Immediate action required"),
        CRITICAL("Critical - Water stress imminent");

        private final String description;

        AlertLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public IrrigationPrediction() {
        this.id = generateAlphanumericId();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.predictionDate = LocalDateTime.now();
    }

    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append("IP");
        String timestamp = String.valueOf(System.currentTimeMillis());
        sb.append(timestamp.substring(timestamp.length() - 6));
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null) this.id = generateAlphanumericId();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFarmId() { return farmId; }
    public void setFarmId(String farmId) { this.farmId = farmId; }
    public String getCropProductionId() { return cropProductionId; }
    public void setCropProductionId(String cropProductionId) { this.cropProductionId = cropProductionId; }
    public LocalDateTime getPredictionDate() { return predictionDate; }
    public void setPredictionDate(LocalDateTime predictionDate) { this.predictionDate = predictionDate; }
    public BigDecimal getPredictedWaterNeed() { return predictedWaterNeed; }
    public void setPredictedWaterNeed(BigDecimal predictedWaterNeed) { this.predictedWaterNeed = predictedWaterNeed; }
    public Integer getPredictedIrrigationFrequency() { return predictedIrrigationFrequency; }
    public void setPredictedIrrigationFrequency(Integer predictedIrrigationFrequency) { this.predictedIrrigationFrequency = predictedIrrigationFrequency; }
    public IrrigationData.IrrigationMethod getRecommendedMethod() { return recommendedMethod; }
    public void setRecommendedMethod(IrrigationData.IrrigationMethod recommendedMethod) { this.recommendedMethod = recommendedMethod; }
    public BigDecimal getWaterStressRisk() { return waterStressRisk; }
    public void setWaterStressRisk(BigDecimal waterStressRisk) { this.waterStressRisk = waterStressRisk; }
    public BigDecimal getPredictedYieldImpact() { return predictedYieldImpact; }
    public void setPredictedYieldImpact(BigDecimal predictedYieldImpact) { this.predictedYieldImpact = predictedYieldImpact; }
    public Integer getOptimalDuration() { return optimalDuration; }
    public void setOptimalDuration(Integer optimalDuration) { this.optimalDuration = optimalDuration; }
    public BigDecimal getCostEstimation() { return costEstimation; }
    public void setCostEstimation(BigDecimal costEstimation) { this.costEstimation = costEstimation; }
    public BigDecimal getConfidenceLevel() { return confidenceLevel; }
    public void setConfidenceLevel(BigDecimal confidenceLevel) { this.confidenceLevel = confidenceLevel; }
    public BigDecimal getWeatherFactor() { return weatherFactor; }
    public void setWeatherFactor(BigDecimal weatherFactor) { this.weatherFactor = weatherFactor; }
    public BigDecimal getSoilMoistureTarget() { return soilMoistureTarget; }
    public void setSoilMoistureTarget(BigDecimal soilMoistureTarget) { this.soilMoistureTarget = soilMoistureTarget; }
    public AlertLevel getAlertLevel() { return alertLevel; }
    public void setAlertLevel(AlertLevel alertLevel) { this.alertLevel = alertLevel; }
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    public Integer getBasedOnHistoricalDays() { return basedOnHistoricalDays; }
    public void setBasedOnHistoricalDays(Integer basedOnHistoricalDays) { this.basedOnHistoricalDays = basedOnHistoricalDays; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}