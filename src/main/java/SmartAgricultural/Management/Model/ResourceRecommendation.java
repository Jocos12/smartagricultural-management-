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
@Table(name = "resource_recommendations")
public class ResourceRecommendation {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "recommendation_code", length = 30, unique = true, nullable = false)
    @NotBlank(message = "Recommendation code is required")
    @Size(max = 30, message = "Recommendation code must not exceed 30 characters")
    private String recommendationCode;

    @Column(name = "farm_id", length = 20, nullable = false)
    @NotBlank(message = "Farm ID is required")
    private String farmId;

    @Column(name = "crop_production_id", length = 20)
    private String cropProductionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    @NotNull(message = "Resource type is required")
    private ResourceType resourceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation_category", nullable = false)
    @NotNull(message = "Recommendation category is required")
    private RecommendationCategory recommendationCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level", nullable = false)
    @NotNull(message = "Priority level is required")
    private PriorityLevel priorityLevel;

    @Column(name = "title", length = 200, nullable = false)
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @Column(name = "recommended_action", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Recommended action is required")
    @Size(max = 5000, message = "Recommended action must not exceed 5000 characters")
    private String recommendedAction;

    @Column(name = "recommended_quantity", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Recommended quantity must be positive")
    @Digits(integer = 8, fraction = 2, message = "Recommended quantity format is invalid")
    private BigDecimal recommendedQuantity;

    @Column(name = "unit", length = 20)
    @Size(max = 20, message = "Unit must not exceed 20 characters")
    private String unit;

    @Column(name = "optimal_timing", length = 100)
    @Size(max = 100, message = "Optimal timing must not exceed 100 characters")
    private String optimalTiming;

    @Column(name = "timing_start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate timingStartDate;

    @Column(name = "timing_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate timingEndDate;

    @Column(name = "frequency", length = 50)
    @Size(max = 50, message = "Frequency must not exceed 50 characters")
    private String frequency;

    @Column(name = "estimated_cost", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Estimated cost must be positive")
    @Digits(integer = 8, fraction = 2, message = "Estimated cost format is invalid")
    private BigDecimal estimatedCost;

    @Column(name = "currency", length = 3)
    @Size(max = 3, message = "Currency code must not exceed 3 characters")
    private String currency = "RWF";

    @Column(name = "expected_benefit", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Expected benefit must not exceed 3000 characters")
    private String expectedBenefit;

    @Column(name = "expected_roi", precision = 6, scale = 2)
    @DecimalMin(value = "0.0", message = "Expected ROI must be positive")
    @Digits(integer = 4, fraction = 2, message = "Expected ROI format is invalid")
    private BigDecimal expectedRoi;

    @Column(name = "rationale", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Rationale is required")
    @Size(max = 5000, message = "Rationale must not exceed 5000 characters")
    private String rationale;

    @Column(name = "scientific_basis", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Scientific basis must not exceed 3000 characters")
    private String scientificBasis;

    @Column(name = "ai_model_version", length = 20, nullable = false)
    @NotBlank(message = "AI model version is required")
    @Size(max = 20, message = "AI model version must not exceed 20 characters")
    private String aiModelVersion;

    @Column(name = "confidence_score", precision = 5, scale = 2, nullable = false)
    @NotNull(message = "Confidence score is required")
    @DecimalMin(value = "0.0", message = "Confidence score must be positive")
    @DecimalMax(value = "100.0", message = "Confidence score must not exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Confidence score format is invalid")
    private BigDecimal confidenceScore;

    @Column(name = "data_sources", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Data sources must not exceed 3000 characters")
    private String dataSources; // JSON string

    @Column(name = "environmental_impact", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Environmental impact must not exceed 2000 characters")
    private String environmentalImpact;

    @Column(name = "sustainability_score")
    @Min(value = 1, message = "Sustainability score must be at least 1")
    @Max(value = 10, message = "Sustainability score must not exceed 10")
    private Integer sustainabilityScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "implementation_difficulty")
    private ImplementationDifficulty implementationDifficulty = ImplementationDifficulty.MODERATE;

    @Column(name = "prerequisites", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Prerequisites must not exceed 2000 characters")
    private String prerequisites;

    @Column(name = "alternative_options", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Alternative options must not exceed 3000 characters")
    private String alternativeOptions; // JSON string

    @Column(name = "success_indicators", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Success indicators must not exceed 2000 characters")
    private String successIndicators;

    @Column(name = "monitoring_parameters", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Monitoring parameters must not exceed 2000 characters")
    private String monitoringParameters;

    @Column(name = "generated_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime generatedDate;

    @Column(name = "valid_until")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate validUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RecommendationStatus status = RecommendationStatus.ACTIVE;

    @Column(name = "implementation_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate implementationDate;

    @Column(name = "implementation_notes", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Implementation notes must not exceed 3000 characters")
    private String implementationNotes;

    @Column(name = "effectiveness_rating")
    @Min(value = 1, message = "Effectiveness rating must be at least 1")
    @Max(value = 5, message = "Effectiveness rating must not exceed 5")
    private Integer effectivenessRating;

    @Column(name = "farmer_feedback", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Farmer feedback must not exceed 3000 characters")
    private String farmerFeedback;

    @Column(name = "actual_cost", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Actual cost must be positive")
    @Digits(integer = 8, fraction = 2, message = "Actual cost format is invalid")
    private BigDecimal actualCost;

    @Column(name = "actual_benefit", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Actual benefit must not exceed 3000 characters")
    private String actualBenefit;

    @Column(name = "follow_up_required")
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate followUpDate;

    @Column(name = "created_by", length = 50)
    @Size(max = 50, message = "Created by must not exceed 50 characters")
    private String createdBy = "AI_SYSTEM";

    @Column(name = "reviewed_by", length = 20)
    private String reviewedBy;

    @Column(name = "review_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", insertable = false, updatable = false)
    @JsonIgnore
    private Farm farm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_production_id", insertable = false, updatable = false)
    @JsonIgnore
    private CropProduction cropProduction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by", insertable = false, updatable = false)
    @JsonIgnore
    private User reviewer;

    // Enums
    public enum ResourceType {
        FERTILIZER("Fertilizer"),
        WATER("Water"),
        SEEDS("Seeds"),
        PESTICIDE("Pesticide"),
        EQUIPMENT("Equipment"),
        LABOR("Labor"),
        FINANCING("Financing");

        private final String displayName;

        ResourceType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isInputResource() {
            return this == FERTILIZER || this == WATER || this == SEEDS || this == PESTICIDE;
        }

        public boolean isCapitalResource() {
            return this == EQUIPMENT || this == FINANCING;
        }

        public boolean isHumanResource() {
            return this == LABOR;
        }
    }

    public enum RecommendationCategory {
        OPTIMIZATION("Optimization"),
        PROBLEM_SOLVING("Problem Solving"),
        PREVENTIVE("Preventive"),
        SEASONAL("Seasonal"),
        EMERGENCY("Emergency");

        private final String displayName;

        RecommendationCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isUrgent() {
            return this == EMERGENCY || this == PROBLEM_SOLVING;
        }

        public boolean isPlanned() {
            return this == SEASONAL || this == OPTIMIZATION || this == PREVENTIVE;
        }
    }

    public enum PriorityLevel {
        LOW("Low", 1),
        MEDIUM("Medium", 2),
        HIGH("High", 3),
        URGENT("Urgent", 4);

        private final String displayName;
        private final int numericValue;

        PriorityLevel(String displayName, int numericValue) {
            this.displayName = displayName;
            this.numericValue = numericValue;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getNumericValue() {
            return numericValue;
        }

        public boolean isHighPriority() {
            return this == HIGH || this == URGENT;
        }

        public boolean requiresImmediateAction() {
            return this == URGENT;
        }

        public static PriorityLevel fromNumericValue(int value) {
            for (PriorityLevel level : PriorityLevel.values()) {
                if (level.numericValue == value) {
                    return level;
                }
            }
            return MEDIUM; // Default
        }
    }

    public enum RecommendationStatus {
        ACTIVE("Active"),
        IMPLEMENTED("Implemented"),
        EXPIRED("Expired"),
        REJECTED("Rejected"),
        SUPERSEDED("Superseded");

        private final String displayName;

        RecommendationStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isActive() {
            return this == ACTIVE;
        }

        public boolean isCompleted() {
            return this == IMPLEMENTED;
        }

        public boolean isClosed() {
            return this == EXPIRED || this == REJECTED || this == SUPERSEDED;
        }

        public boolean canBeImplemented() {
            return this == ACTIVE;
        }

        public boolean canBeRejected() {
            return this == ACTIVE;
        }
    }

    public enum ImplementationDifficulty {
        EASY("Easy", 1),
        MODERATE("Moderate", 2),
        DIFFICULT("Difficult", 3),
        EXPERT_REQUIRED("Expert Required", 4);

        private final String displayName;
        private final int complexityLevel;

        ImplementationDifficulty(String displayName, int complexityLevel) {
            this.displayName = displayName;
            this.complexityLevel = complexityLevel;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getComplexityLevel() {
            return complexityLevel;
        }

        public boolean requiresExpertise() {
            return this == EXPERT_REQUIRED || this == DIFFICULT;
        }

        public boolean isFarmerFriendly() {
            return this == EASY || this == MODERATE;
        }
    }

    public enum EffectivenessRating {
        VERY_POOR("Very Poor", 1),
        POOR("Poor", 2),
        FAIR("Fair", 3),
        GOOD("Good", 4),
        EXCELLENT("Excellent", 5);

        private final String displayName;
        private final int rating;

        EffectivenessRating(String displayName, int rating) {
            this.displayName = displayName;
            this.rating = rating;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getRating() {
            return rating;
        }

        public boolean isPositive() {
            return rating >= 3;
        }

        public static EffectivenessRating fromRating(int rating) {
            for (EffectivenessRating level : EffectivenessRating.values()) {
                if (level.rating == rating) {
                    return level;
                }
            }
            return FAIR; // Default
        }
    }

    public enum SustainabilityLevel {
        VERY_LOW("Very Low", 1, 2),
        LOW("Low", 3, 4),
        MEDIUM("Medium", 5, 6),
        HIGH("High", 7, 8),
        VERY_HIGH("Very High", 9, 10);

        private final String displayName;
        private final int minScore;
        private final int maxScore;

        SustainabilityLevel(String displayName, int minScore, int maxScore) {
            this.displayName = displayName;
            this.minScore = minScore;
            this.maxScore = maxScore;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getMinScore() {
            return minScore;
        }

        public int getMaxScore() {
            return maxScore;
        }

        public static SustainabilityLevel fromScore(int score) {
            for (SustainabilityLevel level : SustainabilityLevel.values()) {
                if (score >= level.minScore && score <= level.maxScore) {
                    return level;
                }
            }
            return MEDIUM; // Default
        }
    }

    // Constructors
    public ResourceRecommendation() {
        this.id = generateAlphanumericId();
        this.recommendationCode = generateRecommendationCode();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.generatedDate = LocalDateTime.now();
    }

    public ResourceRecommendation(String farmId, ResourceType resourceType,
                                  RecommendationCategory category, PriorityLevel priorityLevel,
                                  String title, String description, String recommendedAction,
                                  String rationale, String aiModelVersion, BigDecimal confidenceScore) {
        this();
        this.farmId = farmId;
        this.resourceType = resourceType;
        this.recommendationCategory = category;
        this.priorityLevel = priorityLevel;
        this.title = title;
        this.description = description;
        this.recommendedAction = recommendedAction;
        this.rationale = rationale;
        this.aiModelVersion = aiModelVersion;
        this.confidenceScore = confidenceScore;
    }

    // Method to generate alphanumeric ID with mixed letters and numbers
    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Add "RR" prefix for Resource Recommendation
        sb.append("RR");

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

    // Method to generate recommendation code
    private String generateRecommendationCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Add "REC" prefix
        sb.append("REC");

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
        if (this.recommendationCode == null) {
            this.recommendationCode = generateRecommendationCode();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.generatedDate == null) {
            this.generatedDate = LocalDateTime.now();
        }

        // Set default valid until date (30 days from now for most categories)
        if (this.validUntil == null) {
            setDefaultValidUntil();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();

        // Set implementation date when status changes to IMPLEMENTED
        if (this.status == RecommendationStatus.IMPLEMENTED && this.implementationDate == null) {
            this.implementationDate = LocalDate.now();
        }

        // Set review date when reviewed_by is set
        if (this.reviewedBy != null && this.reviewDate == null) {
            this.reviewDate = LocalDateTime.now();
        }
    }

    // Utility methods
    private void setDefaultValidUntil() {
        int daysValid = 30; // Default

        if (recommendationCategory != null) {
            switch (recommendationCategory) {
                case EMERGENCY:
                    daysValid = 7;
                    break;
                case PROBLEM_SOLVING:
                    daysValid = 14;
                    break;
                case SEASONAL:
                    daysValid = 90;
                    break;
                case PREVENTIVE:
                    daysValid = 60;
                    break;
                case OPTIMIZATION:
                default:
                    daysValid = 30;
                    break;
            }
        }

        this.validUntil = LocalDate.now().plusDays(daysValid);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecommendationCode() {
        return recommendationCode;
    }

    public void setRecommendationCode(String recommendationCode) {
        this.recommendationCode = recommendationCode;
    }

    public String getFarmId() {
        return farmId;
    }

    public void setFarmId(String farmId) {
        this.farmId = farmId;
    }

    public String getCropProductionId() {
        return cropProductionId;
    }

    public void setCropProductionId(String cropProductionId) {
        this.cropProductionId = cropProductionId;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public RecommendationCategory getRecommendationCategory() {
        return recommendationCategory;
    }

    public void setRecommendationCategory(RecommendationCategory recommendationCategory) {
        this.recommendationCategory = recommendationCategory;
    }

    public PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(PriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public void setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction;
    }

    public BigDecimal getRecommendedQuantity() {
        return recommendedQuantity;
    }

    public void setRecommendedQuantity(BigDecimal recommendedQuantity) {
        this.recommendedQuantity = recommendedQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getOptimalTiming() {
        return optimalTiming;
    }

    public void setOptimalTiming(String optimalTiming) {
        this.optimalTiming = optimalTiming;
    }

    public LocalDate getTimingStartDate() {
        return timingStartDate;
    }

    public void setTimingStartDate(LocalDate timingStartDate) {
        this.timingStartDate = timingStartDate;
    }

    public LocalDate getTimingEndDate() {
        return timingEndDate;
    }

    public void setTimingEndDate(LocalDate timingEndDate) {
        this.timingEndDate = timingEndDate;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getExpectedBenefit() {
        return expectedBenefit;
    }

    public void setExpectedBenefit(String expectedBenefit) {
        this.expectedBenefit = expectedBenefit;
    }

    public BigDecimal getExpectedRoi() {
        return expectedRoi;
    }

    public void setExpectedRoi(BigDecimal expectedRoi) {
        this.expectedRoi = expectedRoi;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }

    public String getScientificBasis() {
        return scientificBasis;
    }

    public void setScientificBasis(String scientificBasis) {
        this.scientificBasis = scientificBasis;
    }

    public String getAiModelVersion() {
        return aiModelVersion;
    }

    public void setAiModelVersion(String aiModelVersion) {
        this.aiModelVersion = aiModelVersion;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getDataSources() {
        return dataSources;
    }

    public void setDataSources(String dataSources) {
        this.dataSources = dataSources;
    }

    public String getEnvironmentalImpact() {
        return environmentalImpact;
    }

    public void setEnvironmentalImpact(String environmentalImpact) {
        this.environmentalImpact = environmentalImpact;
    }

    public Integer getSustainabilityScore() {
        return sustainabilityScore;
    }

    public void setSustainabilityScore(Integer sustainabilityScore) {
        this.sustainabilityScore = sustainabilityScore;
    }

    public ImplementationDifficulty getImplementationDifficulty() {
        return implementationDifficulty;
    }

    public void setImplementationDifficulty(ImplementationDifficulty implementationDifficulty) {
        this.implementationDifficulty = implementationDifficulty;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public String getAlternativeOptions() {
        return alternativeOptions;
    }

    public void setAlternativeOptions(String alternativeOptions) {
        this.alternativeOptions = alternativeOptions;
    }

    public String getSuccessIndicators() {
        return successIndicators;
    }

    public void setSuccessIndicators(String successIndicators) {
        this.successIndicators = successIndicators;
    }

    public String getMonitoringParameters() {
        return monitoringParameters;
    }

    public void setMonitoringParameters(String monitoringParameters) {
        this.monitoringParameters = monitoringParameters;
    }

    public LocalDateTime getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDateTime generatedDate) {
        this.generatedDate = generatedDate;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public RecommendationStatus getStatus() {
        return status;
    }

    public void setStatus(RecommendationStatus status) {
        this.status = status;
    }

    public LocalDate getImplementationDate() {
        return implementationDate;
    }

    public void setImplementationDate(LocalDate implementationDate) {
        this.implementationDate = implementationDate;
    }

    public String getImplementationNotes() {
        return implementationNotes;
    }

    public void setImplementationNotes(String implementationNotes) {
        this.implementationNotes = implementationNotes;
    }

    public Integer getEffectivenessRating() {
        return effectivenessRating;
    }

    public void setEffectivenessRating(Integer effectivenessRating) {
        this.effectivenessRating = effectivenessRating;
    }

    public String getFarmerFeedback() {
        return farmerFeedback;
    }

    public void setFarmerFeedback(String farmerFeedback) {
        this.farmerFeedback = farmerFeedback;
    }

    public BigDecimal getActualCost() {
        return actualCost;
    }

    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }

    public String getActualBenefit() {
        return actualBenefit;
    }

    public void setActualBenefit(String actualBenefit) {
        this.actualBenefit = actualBenefit;
    }

    public Boolean getFollowUpRequired() {
        return followUpRequired;
    }

    public void setFollowUpRequired(Boolean followUpRequired) {
        this.followUpRequired = followUpRequired;
    }

    public LocalDate getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(LocalDate followUpDate) {
        this.followUpDate = followUpDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
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

    public Farm getFarm() {
        return farm;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    public CropProduction getCropProduction() {
        return cropProduction;
    }

    public void setCropProduction(CropProduction cropProduction) {
        this.cropProduction = cropProduction;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    // Utility methods
    public String getResourceTypeDescription() {
        return resourceType != null ? resourceType.getDisplayName() : "Unknown";
    }

    public String getRecommendationCategoryDescription() {
        return recommendationCategory != null ? recommendationCategory.getDisplayName() : "Unknown";
    }

    public String getPriorityLevelDescription() {
        return priorityLevel != null ? priorityLevel.getDisplayName() : "Unknown";
    }

    public String getStatusDescription() {
        return status != null ? status.getDisplayName() : "Unknown";
    }

    public String getImplementationDifficultyDescription() {
        return implementationDifficulty != null ? implementationDifficulty.getDisplayName() : "Unknown";
    }

    public EffectivenessRating getEffectivenessRatingLevel() {
        if (effectivenessRating == null) return null;
        return EffectivenessRating.fromRating(effectivenessRating);
    }

    public String getEffectivenessDescription() {
        EffectivenessRating level = getEffectivenessRatingLevel();
        return level != null ? level.getDisplayName() : "Not rated";
    }

    public SustainabilityLevel getSustainabilityLevel() {
        if (sustainabilityScore == null) return null;
        return SustainabilityLevel.fromScore(sustainabilityScore);
    }

    public String getSustainabilityDescription() {
        SustainabilityLevel level = getSustainabilityLevel();
        return level != null ? level.getDisplayName() : "Not assessed";
    }

    // Status check methods
    public boolean isActive() {
        return status == RecommendationStatus.ACTIVE;
    }

    public boolean isImplemented() {
        return status == RecommendationStatus.IMPLEMENTED;
    }

    public boolean isExpired() {
        return status == RecommendationStatus.EXPIRED;
    }

    public boolean isRejected() {
        return status == RecommendationStatus.REJECTED;
    }

    public boolean isSuperseded() {
        return status == RecommendationStatus.SUPERSEDED;
    }

    public boolean isClosed() {
        return status != null && status.isClosed();
    }

    public boolean canBeImplemented() {
        return status != null && status.canBeImplemented();
    }

    public boolean canBeRejected() {
        return status != null && status.canBeRejected();
    }

    public boolean isPriorityLevel(PriorityLevel level) {
        return priorityLevel == level;
    }

    public boolean isHighPriority() {
        return priorityLevel != null && priorityLevel.isHighPriority();
    }

    public boolean isUrgent() {
        return priorityLevel == PriorityLevel.URGENT;
    }

    public boolean requiresImmediateAction() {
        return priorityLevel != null && priorityLevel.requiresImmediateAction();
    }

    public boolean isTimeSensitive() {
        return recommendationCategory != null && recommendationCategory.isUrgent();
    }

    public boolean isPlannedRecommendation() {
        return recommendationCategory != null && recommendationCategory.isPlanned();
    }

    public boolean hasTimingWindow() {
        return timingStartDate != null && timingEndDate != null;
    }

    public boolean isWithinTimingWindow() {
        if (!hasTimingWindow()) return true;
        LocalDate now = LocalDate.now();
        return !now.isBefore(timingStartDate) && !now.isAfter(timingEndDate);
    }

    public boolean isOverdue() {
        if (validUntil == null || status != RecommendationStatus.ACTIVE) return false;
        return LocalDate.now().isAfter(validUntil);
    }

    public boolean isExpiringSoon() {
        if (validUntil == null || status != RecommendationStatus.ACTIVE) return false;
        return LocalDate.now().plusDays(7).isAfter(validUntil);
    }

    public boolean hasBeenReviewed() {
        return reviewedBy != null && reviewDate != null;
    }

    public boolean hasEffectivenessRating() {
        return effectivenessRating != null;
    }

    public boolean hasActualCost() {
        return actualCost != null;
    }

    public boolean hasFarmerFeedback() {
        return farmerFeedback != null && !farmerFeedback.trim().isEmpty();
    }

    public boolean requiresFollowUp() {
        return followUpRequired != null && followUpRequired;
    }

    public boolean isFollowUpDue() {
        if (!requiresFollowUp() || followUpDate == null) return false;
        return LocalDate.now().isAfter(followUpDate) || LocalDate.now().isEqual(followUpDate);
    }

    public boolean isHighConfidence() {
        return confidenceScore != null && confidenceScore.compareTo(new BigDecimal("80")) >= 0;
    }

    public boolean isLowConfidence() {
        return confidenceScore != null && confidenceScore.compareTo(new BigDecimal("60")) < 0;
    }

    public boolean isEasyToImplement() {
        return implementationDifficulty != null && implementationDifficulty.isFarmerFriendly();
    }

    public boolean requiresExpertise() {
        return implementationDifficulty != null && implementationDifficulty.requiresExpertise();
    }

    public boolean isHighlyRated() {
        EffectivenessRating rating = getEffectivenessRatingLevel();
        return rating != null && rating.isPositive();
    }

    public boolean isSustainable() {
        SustainabilityLevel level = getSustainabilityLevel();
        return level == SustainabilityLevel.HIGH || level == SustainabilityLevel.VERY_HIGH;
    }

    public boolean hasPositiveROI() {
        return expectedRoi != null && expectedRoi.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isLowCost() {
        return estimatedCost != null && estimatedCost.compareTo(new BigDecimal("10000")) <= 0; // Adjust threshold as needed
    }

    public boolean isHighValue() {
        return hasPositiveROI() && expectedRoi.compareTo(new BigDecimal("20")) >= 0; // 20% ROI threshold
    }

    // Calculation methods
    public long getDaysUntilExpiry() {
        if (validUntil == null) return -1;
        return LocalDate.now().until(validUntil).getDays();
    }

    public long getDaysSinceGenerated() {
        if (generatedDate == null) return 0;
        return java.time.Duration.between(generatedDate, LocalDateTime.now()).toDays();
    }

    public long getDaysSinceImplemented() {
        if (implementationDate == null) return -1;
        return LocalDate.now().until(implementationDate).getDays() * -1; // Negative for past dates
    }

    public long getDaysUntilFollowUp() {
        if (followUpDate == null) return -1;
        return LocalDate.now().until(followUpDate).getDays();
    }

    public BigDecimal getCostVariance() {
        if (estimatedCost == null || actualCost == null) return null;
        return actualCost.subtract(estimatedCost);
    }

    public BigDecimal getCostVariancePercentage() {
        BigDecimal variance = getCostVariance();
        if (variance == null || estimatedCost == null || estimatedCost.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return variance.divide(estimatedCost, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
    }

    public int getTimingWindowDuration() {
        if (!hasTimingWindow()) return 0;
        return (int) timingStartDate.until(timingEndDate).getDays();
    }

    // Formatted getters
    public String getRecommendedQuantityFormatted() {
        if (recommendedQuantity == null) return "Not specified";
        return recommendedQuantity + (unit != null ? " " + unit : "");
    }

    public String getEstimatedCostFormatted() {
        if (estimatedCost == null) return "Not specified";
        return (currency != null ? currency : "RWF") + " " + estimatedCost;
    }

    public String getActualCostFormatted() {
        if (actualCost == null) return "Not available";
        return (currency != null ? currency : "RWF") + " " + actualCost;
    }

    public String getExpectedRoiFormatted() {
        if (expectedRoi == null) return "Not calculated";
        return expectedRoi + "%";
    }

    public String getConfidenceScoreFormatted() {
        if (confidenceScore == null) return "Not specified";
        return confidenceScore + "%";
    }

    public String getCostVarianceFormatted() {
        BigDecimal variance = getCostVariance();
        if (variance == null) return "Not calculated";
        String prefix = variance.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
        return prefix + (currency != null ? currency : "RWF") + " " + variance;
    }

    public String getCostVariancePercentageFormatted() {
        BigDecimal variancePercent = getCostVariancePercentage();
        if (variancePercent == null) return "Not calculated";
        String prefix = variancePercent.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
        return prefix + variancePercent + "%";
    }

    public String getGeneratedDateFormatted() {
        return generatedDate != null ?
                generatedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) :
                "Not set";
    }

    public String getValidUntilFormatted() {
        return validUntil != null ?
                validUntil.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) :
                "Not set";
    }

    public String getImplementationDateFormatted() {
        return implementationDate != null ?
                implementationDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) :
                "Not implemented";
    }

    public String getFollowUpDateFormatted() {
        return followUpDate != null ?
                followUpDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) :
                "No follow-up";
    }

    public String getReviewDateFormatted() {
        return reviewDate != null ?
                reviewDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) :
                "Not reviewed";
    }

    public String getTimingWindowFormatted() {
        if (!hasTimingWindow()) return "Not specified";
        return timingStartDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " to " + timingEndDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    // Summary methods
    public String getRecommendationSummary() {
        StringBuilder summary = new StringBuilder();

        if (recommendationCode != null) {
            summary.append(recommendationCode);
        }

        if (title != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append(title);
        }

        if (priorityLevel != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Priority: ").append(priorityLevel.getDisplayName());
        }

        if (confidenceScore != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Confidence: ").append(getConfidenceScoreFormatted());
        }

        return summary.length() > 0 ? summary.toString() : "No recommendation data";
    }

    public String getResourceSummary() {
        StringBuilder summary = new StringBuilder();

        if (resourceType != null) {
            summary.append("Type: ").append(resourceType.getDisplayName());
        }

        if (recommendedQuantity != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Quantity: ").append(getRecommendedQuantityFormatted());
        }

        if (estimatedCost != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Cost: ").append(getEstimatedCostFormatted());
        }

        if (expectedRoi != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("ROI: ").append(getExpectedRoiFormatted());
        }

        return summary.length() > 0 ? summary.toString() : "No resource data";
    }

    public String getTimingSummary() {
        StringBuilder summary = new StringBuilder();

        if (optimalTiming != null) {
            summary.append("Timing: ").append(optimalTiming);
        }

        if (hasTimingWindow()) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Window: ").append(getTimingWindowFormatted());
        }

        if (frequency != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Frequency: ").append(frequency);
        }

        if (validUntil != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Valid until: ").append(getValidUntilFormatted());
        }

        return summary.length() > 0 ? summary.toString() : "No timing data";
    }

    public String getStatusSummary() {
        StringBuilder summary = new StringBuilder();

        if (status != null) {
            summary.append("Status: ").append(status.getDisplayName());
        }

        if (implementationDate != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Implemented: ").append(getImplementationDateFormatted());
        }

        if (effectivenessRating != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Rating: ").append(effectivenessRating).append("/5");
        }

        if (isOverdue()) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("OVERDUE");
        } else if (isExpiringSoon()) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("EXPIRING SOON");
        }

        return summary.length() > 0 ? summary.toString() : "No status data";
    }

    public String getPerformanceSummary() {
        StringBuilder summary = new StringBuilder();

        if (hasEffectivenessRating()) {
            summary.append("Effectiveness: ").append(getEffectivenessDescription());
        }

        if (hasActualCost()) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Actual Cost: ").append(getActualCostFormatted());

            BigDecimal variance = getCostVariancePercentage();
            if (variance != null) {
                summary.append(" (").append(getCostVariancePercentageFormatted()).append(")");
            }
        }

        if (sustainabilityScore != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Sustainability: ").append(getSustainabilityDescription());
        }

        return summary.length() > 0 ? summary.toString() : "No performance data";
    }

    // State transition methods
    public boolean canImplement() {
        return status == RecommendationStatus.ACTIVE && !isOverdue();
    }

    public boolean canReject() {
        return status == RecommendationStatus.ACTIVE;
    }

    public boolean canSupersede() {
        return status == RecommendationStatus.ACTIVE;
    }

    public void implement(String implementationNotes) {
        if (canImplement()) {
            this.status = RecommendationStatus.IMPLEMENTED;
            this.implementationDate = LocalDate.now();
            this.implementationNotes = implementationNotes;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void reject() {
        if (canReject()) {
            this.status = RecommendationStatus.REJECTED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void supersede() {
        if (canSupersede()) {
            this.status = RecommendationStatus.SUPERSEDED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void markAsExpired() {
        if (isOverdue()) {
            this.status = RecommendationStatus.EXPIRED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void addEffectivenessRating(int rating, String feedback) {
        this.effectivenessRating = rating;
        if (feedback != null && !feedback.trim().isEmpty()) {
            this.farmerFeedback = feedback;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void updateActualCost(BigDecimal cost) {
        this.actualCost = cost;
        this.updatedAt = LocalDateTime.now();
    }

    public void scheduleFollowUp(LocalDate date) {
        this.followUpRequired = true;
        this.followUpDate = date;
        this.updatedAt = LocalDateTime.now();
    }

    public void completeFollowUp() {
        this.followUpRequired = false;
        this.followUpDate = null;
        this.updatedAt = LocalDateTime.now();
    }

    // toString, equals and hashCode
    @Override
    public String toString() {
        return "ResourceRecommendation{" +
                "id='" + id + '\'' +
                ", recommendationCode='" + recommendationCode + '\'' +
                ", farmId='" + farmId + '\'' +
                ", resourceType=" + resourceType +
                ", recommendationCategory=" + recommendationCategory +
                ", priorityLevel=" + priorityLevel +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", confidenceScore=" + confidenceScore +
                ", generatedDate=" + generatedDate +
                ", validUntil=" + validUntil +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceRecommendation that = (ResourceRecommendation) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}