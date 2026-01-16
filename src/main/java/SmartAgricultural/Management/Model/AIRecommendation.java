package SmartAgricultural.Management.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_recommendations")
@NoArgsConstructor
@AllArgsConstructor
public class AIRecommendation {

    @Id
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(
            name = "custom-id",
            type = SmartAgricultural.Management.Util.CustomIdGenerator.class,
            parameters = {
                    @Parameter(name = "prefix", value = "REC")
            }
    )
    @Column(name = "recommendation_id", nullable = false, unique = true, length = 14)
    private String id;

    @Column(name = "farmer_id", nullable = false, length = 14)
    private String farmerId;

    @Column(name = "farm_id", length = 14)
    private String farmId;

    @Column(name = "crop_production_id", length = 14)
    private String cropProductionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation_type", nullable = false, length = 50)
    private RecommendationType recommendationType;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "action_items", columnDefinition = "TEXT")
    private String actionItems;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private Priority priority;

    @Column(name = "confidence_score")
    private Double confidenceScore; // 0.00 to 1.00

    @Column(name = "generated_by", length = 50)
    private String generatedBy;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "is_implemented", nullable = false)
    private Boolean isImplemented = false;

    @Column(name = "implementation_date")
    private LocalDateTime implementationDate;

    @Column(name = "implementation_notes", columnDefinition = "TEXT")
    private String implementationNotes;

    @Column(name = "effectiveness_rating")
    private Integer effectivenessRating; // 1-5 stars

    @Column(name = "effectiveness_notes", columnDefinition = "TEXT")
    private String effectivenessNotes;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    public enum RecommendationType {
        FERTILIZER,
        WATER,
        SEEDS,
        PESTICIDE,
        HARVEST,
        PLANTING,
        SOIL_MANAGEMENT,
        PEST_CONTROL,
        DISEASE_PREVENTION,
        IRRIGATION,
        CROP_ROTATION,
        MARKET_TIMING,
        STORAGE,
        WEATHER_ADAPTATION,
        GENERAL
    }

    public enum Priority {
        URGENT,
        HIGH,
        MEDIUM,
        LOW
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (validFrom == null) {
            validFrom = LocalDateTime.now();
        }
        if (isRead == null) {
            isRead = false;
        }
        if (isImplemented == null) {
            isImplemented = false;
        }
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
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

    public RecommendationType getRecommendationType() {
        return recommendationType;
    }

    public void setRecommendationType(RecommendationType recommendationType) {
        this.recommendationType = recommendationType;
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

    public String getActionItems() {
        return actionItems;
    }

    public void setActionItems(String actionItems) {
        this.actionItems = actionItems;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Boolean getIsImplemented() {
        return isImplemented;
    }

    public void setIsImplemented(Boolean isImplemented) {
        this.isImplemented = isImplemented;
    }

    public LocalDateTime getImplementationDate() {
        return implementationDate;
    }

    public void setImplementationDate(LocalDateTime implementationDate) {
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

    public String getEffectivenessNotes() {
        return effectivenessNotes;
    }

    public void setEffectivenessNotes(String effectivenessNotes) {
        this.effectivenessNotes = effectivenessNotes;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}