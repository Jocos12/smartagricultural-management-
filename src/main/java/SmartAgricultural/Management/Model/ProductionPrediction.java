package SmartAgricultural.Management.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Entity
@Table(name = "production_predictions")
public class ProductionPrediction {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "prediction_code", length = 30, unique = true, nullable = false)
    @NotBlank(message = "Prediction code is required")
    @Size(max = 30, message = "Prediction code must not exceed 30 characters")
    private String predictionCode;

    // ✅ NEW FIELD - Links prediction to specific crop production
    @Column(name = "crop_production_id", length = 20)
    private String cropProductionId;

    @Column(name = "crop_id", length = 20, nullable = false)
    @NotBlank(message = "Crop ID is required")
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
    @Column(name = "prediction_type", nullable = false)
    @NotNull(message = "Prediction type is required")
    private PredictionType predictionType;

    @Column(name = "predicted_value", precision = 12, scale = 2, nullable = false)
    @NotNull(message = "Predicted value is required")
    @DecimalMin(value = "0.0", message = "Predicted value must be positive")
    @Digits(integer = 10, fraction = 2, message = "Predicted value format is invalid")
    private BigDecimal predictedValue;

    @Column(name = "unit", length = 20, nullable = false)
    @NotBlank(message = "Unit is required")
    @Size(max = 20, message = "Unit must not exceed 20 characters")
    private String unit;

    @Column(name = "confidence_level", precision = 5, scale = 2, nullable = false)
    @NotNull(message = "Confidence level is required")
    @DecimalMin(value = "0.0", message = "Confidence level must be positive")
    @DecimalMax(value = "100.0", message = "Confidence level must not exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Confidence level format is invalid")
    private BigDecimal confidenceLevel;

    @Column(name = "confidence_interval_min", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "Confidence interval min must be positive")
    @Digits(integer = 10, fraction = 2, message = "Confidence interval min format is invalid")
    private BigDecimal confidenceIntervalMin;

    @Column(name = "confidence_interval_max", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "Confidence interval max must be positive")
    @Digits(integer = 10, fraction = 2, message = "Confidence interval max format is invalid")
    private BigDecimal confidenceIntervalMax;

    @Column(name = "model_used", length = 100, nullable = false)
    @NotBlank(message = "Model used is required")
    @Size(max = 100, message = "Model used must not exceed 100 characters")
    private String modelUsed;

    @Column(name = "model_version", length = 20)
    @Size(max = 20, message = "Model version must not exceed 20 characters")
    private String modelVersion;

    @Column(name = "algorithm", length = 50)
    @Size(max = 50, message = "Algorithm must not exceed 50 characters")
    private String algorithm;

    @Column(name = "prediction_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime predictionDate;

    @Column(name = "target_date", nullable = false)
    @NotNull(message = "Target date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate targetDate;

    @Column(name = "input_features", columnDefinition = "TEXT")
    @Size(max = 5000, message = "Input features must not exceed 5000 characters")
    private String inputFeatures;

    @Column(name = "factors_considered", columnDefinition = "TEXT")
    @Size(max = 5000, message = "Factors considered must not exceed 5000 characters")
    private String factorsConsidered;

    @Column(name = "historical_accuracy", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Historical accuracy must be positive")
    @DecimalMax(value = "100.0", message = "Historical accuracy must not exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Historical accuracy format is invalid")
    private BigDecimal historicalAccuracy;

    @Column(name = "risk_factors", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Risk factors must not exceed 3000 characters")
    private String riskFactors;

    @Column(name = "assumptions", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Assumptions must not exceed 3000 characters")
    private String assumptions;

    @Column(name = "limitations", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Limitations must not exceed 3000 characters")
    private String limitations;

    @Column(name = "update_frequency", length = 50)
    @Size(max = 50, message = "Update frequency must not exceed 50 characters")
    private String updateFrequency = "MONTHLY";

    @Column(name = "last_updated")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;

    @Column(name = "actual_value", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "Actual value must be positive")
    @Digits(integer = 10, fraction = 2, message = "Actual value format is invalid")
    private BigDecimal actualValue;

    @Column(name = "accuracy_achieved", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Accuracy achieved must be positive")
    @DecimalMax(value = "100.0", message = "Accuracy achieved must not exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Accuracy achieved format is invalid")
    private BigDecimal accuracyAchieved;

    @Column(name = "variance_analysis", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Variance analysis must not exceed 3000 characters")
    private String varianceAnalysis;

    @Column(name = "policy_implications", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Policy implications must not exceed 3000 characters")
    private String policyImplications;

    @Column(name = "market_implications", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Market implications must not exceed 3000 characters")
    private String marketImplications;

    @Enumerated(EnumType.STRING)
    @Column(name = "validation_status")
    private ValidationStatus validationStatus = ValidationStatus.PENDING;

    @Column(name = "validated_by", length = 20)
    private String validatedBy;

    @Column(name = "validation_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validationDate;

    @Column(name = "published")
    private Boolean published = false;

    @Column(name = "stakeholders_shared", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Stakeholders shared must not exceed 2000 characters")
    private String stakeholdersShared;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ✅ NEW RELATIONSHIP - Links to CropProduction
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_production_id", insertable = false, updatable = false)
    @JsonIgnore
    private CropProduction cropProduction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", insertable = false, updatable = false)
    @JsonIgnore
    private Crop crop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by", insertable = false, updatable = false)
    @JsonIgnore
    private User validator;

    // Enums (unchanged)
    public enum Season {
        SEASON_A("Season A"),
        SEASON_B("Season B"),
        SEASON_C("Season C"),
        ANNUAL("Annual");

        private final String displayName;
        Season(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum PredictionType {
        YIELD("Yield Prediction"),
        TOTAL_PRODUCTION("Total Production"),
        PLANTED_AREA("Planted Area"),
        HARVEST_PERIOD("Harvest Period");

        private final String displayName;
        PredictionType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum ValidationStatus {
        PENDING("Pending"),
        VALIDATED("Validated"),
        REJECTED("Rejected");

        private final String displayName;
        ValidationStatus(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
        public boolean isValidated() { return this == VALIDATED; }
        public boolean isPending() { return this == PENDING; }
        public boolean isRejected() { return this == REJECTED; }
    }

    public enum UpdateFrequency {
        DAILY("Daily"),
        WEEKLY("Weekly"),
        MONTHLY("Monthly"),
        SEASONAL("Seasonal"),
        ANNUAL("Annual");

        private final String displayName;
        UpdateFrequency(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum AccuracyLevel {
        VERY_LOW("Very Low", 0.0, 40.0),
        LOW("Low", 40.0, 60.0),
        MEDIUM("Medium", 60.0, 75.0),
        HIGH("High", 75.0, 85.0),
        VERY_HIGH("Very High", 85.0, 100.0);

        private final String displayName;
        private final double minAccuracy;
        private final double maxAccuracy;

        AccuracyLevel(String displayName, double minAccuracy, double maxAccuracy) {
            this.displayName = displayName;
            this.minAccuracy = minAccuracy;
            this.maxAccuracy = maxAccuracy;
        }

        public String getDisplayName() { return displayName; }
        public double getMinAccuracy() { return minAccuracy; }
        public double getMaxAccuracy() { return maxAccuracy; }

        public static AccuracyLevel fromAccuracy(double accuracy) {
            for (AccuracyLevel level : AccuracyLevel.values()) {
                if (accuracy >= level.minAccuracy && accuracy <= level.maxAccuracy) {
                    return level;
                }
            }
            return MEDIUM;
        }
    }

    // Constructors
    public ProductionPrediction() {
        this.id = generateAlphanumericId();
        this.predictionCode = generatePredictionCode();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.predictionDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    public ProductionPrediction(String cropId, String region, Integer year, Season season,
                                PredictionType predictionType, BigDecimal predictedValue, String unit,
                                BigDecimal confidenceLevel, String modelUsed, LocalDate targetDate) {
        this();
        this.cropId = cropId;
        this.region = region;
        this.year = year;
        this.season = season;
        this.predictionType = predictionType;
        this.predictedValue = predictedValue;
        this.unit = unit;
        this.confidenceLevel = confidenceLevel;
        this.modelUsed = modelUsed;
        this.targetDate = targetDate;
    }

    // ID Generation Methods
    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append("PP");
        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 6);
        sb.append(shortTimestamp);
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String generatePredictionCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append("PRED");
        LocalDateTime now = LocalDateTime.now();
        sb.append(String.format("%02d%02d%02d",
                now.getYear() % 100,
                now.getMonthValue(),
                now.getDayOfMonth()));
        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 4);
        sb.append(shortTimestamp);
        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // JPA Lifecycle Methods
    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = generateAlphanumericId();
        }
        if (this.predictionCode == null) {
            this.predictionCode = generatePredictionCode();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.predictionDate == null) {
            this.predictionDate = LocalDateTime.now();
        }
        if (this.lastUpdated == null) {
            this.lastUpdated = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        if (actualValue != null && predictedValue != null && predictedValue.compareTo(BigDecimal.ZERO) > 0) {
            calculateAccuracy();
        }
    }

    private void calculateAccuracy() {
        if (actualValue == null || predictedValue == null || predictedValue.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        BigDecimal difference = actualValue.subtract(predictedValue).abs();
        BigDecimal accuracyPercent = BigDecimal.ONE.subtract(difference.divide(predictedValue, 4, BigDecimal.ROUND_HALF_UP))
                .multiply(new BigDecimal("100"));
        if (accuracyPercent.compareTo(BigDecimal.ZERO) < 0) {
            accuracyPercent = BigDecimal.ZERO;
        } else if (accuracyPercent.compareTo(new BigDecimal("100")) > 0) {
            accuracyPercent = new BigDecimal("100");
        }
        this.accuracyAchieved = accuracyPercent;
    }

    // ✅ NEW GETTER/SETTER for cropProductionId
    public String getCropProductionId() {
        return cropProductionId;
    }

    public void setCropProductionId(String cropProductionId) {
        this.cropProductionId = cropProductionId;
    }

    public CropProduction getCropProduction() {
        return cropProduction;
    }

    public void setCropProduction(CropProduction cropProduction) {
        this.cropProduction = cropProduction;
    }

    // All other getters/setters (keep existing ones)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPredictionCode() { return predictionCode; }
    public void setPredictionCode(String predictionCode) { this.predictionCode = predictionCode; }

    public String getCropId() { return cropId; }
    public void setCropId(String cropId) { this.cropId = cropId; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Season getSeason() { return season; }
    public void setSeason(Season season) { this.season = season; }

    public PredictionType getPredictionType() { return predictionType; }
    public void setPredictionType(PredictionType predictionType) { this.predictionType = predictionType; }

    public BigDecimal getPredictedValue() { return predictedValue; }
    public void setPredictedValue(BigDecimal predictedValue) { this.predictedValue = predictedValue; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getConfidenceLevel() { return confidenceLevel; }
    public void setConfidenceLevel(BigDecimal confidenceLevel) { this.confidenceLevel = confidenceLevel; }

    public BigDecimal getConfidenceIntervalMin() { return confidenceIntervalMin; }
    public void setConfidenceIntervalMin(BigDecimal confidenceIntervalMin) { this.confidenceIntervalMin = confidenceIntervalMin; }

    public BigDecimal getConfidenceIntervalMax() { return confidenceIntervalMax; }
    public void setConfidenceIntervalMax(BigDecimal confidenceIntervalMax) { this.confidenceIntervalMax = confidenceIntervalMax; }

    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public LocalDateTime getPredictionDate() { return predictionDate; }
    public void setPredictionDate(LocalDateTime predictionDate) { this.predictionDate = predictionDate; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public String getInputFeatures() { return inputFeatures; }
    public void setInputFeatures(String inputFeatures) { this.inputFeatures = inputFeatures; }

    public String getFactorsConsidered() { return factorsConsidered; }
    public void setFactorsConsidered(String factorsConsidered) { this.factorsConsidered = factorsConsidered; }

    public BigDecimal getHistoricalAccuracy() { return historicalAccuracy; }
    public void setHistoricalAccuracy(BigDecimal historicalAccuracy) { this.historicalAccuracy = historicalAccuracy; }

    public String getRiskFactors() { return riskFactors; }
    public void setRiskFactors(String riskFactors) { this.riskFactors = riskFactors; }

    public String getAssumptions() { return assumptions; }
    public void setAssumptions(String assumptions) { this.assumptions = assumptions; }

    public String getLimitations() { return limitations; }
    public void setLimitations(String limitations) { this.limitations = limitations; }

    public String getUpdateFrequency() { return updateFrequency; }
    public void setUpdateFrequency(String updateFrequency) { this.updateFrequency = updateFrequency; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public BigDecimal getActualValue() { return actualValue; }
    public void setActualValue(BigDecimal actualValue) { this.actualValue = actualValue; }

    public BigDecimal getAccuracyAchieved() { return accuracyAchieved; }
    public void setAccuracyAchieved(BigDecimal accuracyAchieved) { this.accuracyAchieved = accuracyAchieved; }

    public String getVarianceAnalysis() { return varianceAnalysis; }
    public void setVarianceAnalysis(String varianceAnalysis) { this.varianceAnalysis = varianceAnalysis; }

    public String getPolicyImplications() { return policyImplications; }
    public void setPolicyImplications(String policyImplications) { this.policyImplications = policyImplications; }

    public String getMarketImplications() { return marketImplications; }
    public void setMarketImplications(String marketImplications) { this.marketImplications = marketImplications; }

    public ValidationStatus getValidationStatus() { return validationStatus; }
    public void setValidationStatus(ValidationStatus validationStatus) { this.validationStatus = validationStatus; }

    public String getValidatedBy() { return validatedBy; }
    public void setValidatedBy(String validatedBy) { this.validatedBy = validatedBy; }

    public LocalDateTime getValidationDate() { return validationDate; }
    public void setValidationDate(LocalDateTime validationDate) { this.validationDate = validationDate; }

    public Boolean getPublished() { return published; }
    public void setPublished(Boolean published) { this.published = published; }

    public String getStakeholdersShared() { return stakeholdersShared; }
    public void setStakeholdersShared(String stakeholdersShared) { this.stakeholdersShared = stakeholdersShared; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Crop getCrop() { return crop; }
    public void setCrop(Crop crop) { this.crop = crop; }

    public User getValidator() { return validator; }
    public void setValidator(User validator) { this.validator = validator; }

    // Utility methods
    public boolean canValidate() {
        return validationStatus == ValidationStatus.PENDING;
    }

    public boolean canReject() {
        return validationStatus == ValidationStatus.PENDING;
    }

    public boolean canPublish() {
        return validationStatus == ValidationStatus.VALIDATED && (published == null || !published);
    }

    public boolean canUnpublish() {
        return published != null && published;
    }

    public void validate(String validatorId) {
        if (canValidate()) {
            this.validationStatus = ValidationStatus.VALIDATED;
            this.validatedBy = validatorId;
            this.validationDate = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void reject(String validatorId) {
        if (canReject()) {
            this.validationStatus = ValidationStatus.REJECTED;
            this.validatedBy = validatorId;
            this.validationDate = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void publish() {
        if (canPublish()) {
            this.published = true;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void unpublish() {
        if (canUnpublish()) {
            this.published = false;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void updateActualValue(BigDecimal actualValue) {
        this.actualValue = actualValue;
        calculateAccuracy();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isValidated() {
        return validationStatus == ValidationStatus.VALIDATED;
    }

    public boolean isPending() {
        return validationStatus == ValidationStatus.PENDING;
    }

    public boolean isRejected() {
        return validationStatus == ValidationStatus.REJECTED;
    }

    public boolean isPublished() {
        return published != null && published;
    }

    @Override
    public String toString() {
        return "ProductionPrediction{" +
                "id='" + id + '\'' +
                ", predictionCode='" + predictionCode + '\'' +
                ", cropProductionId='" + cropProductionId + '\'' +
                ", cropId='" + cropId + '\'' +
                ", region='" + region + '\'' +
                ", year=" + year +
                ", season=" + season +
                ", predictionType=" + predictionType +
                ", predictedValue=" + predictedValue +
                ", validationStatus=" + validationStatus +
                ", published=" + published +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductionPrediction that = (ProductionPrediction) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}