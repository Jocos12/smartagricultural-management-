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
@Table(name = "fertilizer_usage")
public class FertilizerUsage {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "crop_production_id", length = 20, nullable = false)
    @NotBlank(message = "Crop production ID is required")
    private String cropProductionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "fertilizer_type", nullable = false)
    @NotNull(message = "Fertilizer type is required")
    private FertilizerType fertilizerType;

    @Column(name = "fertilizer_name", length = 100, nullable = false)
    @NotBlank(message = "Fertilizer name is required")
    @Size(max = 100, message = "Fertilizer name must not exceed 100 characters")
    private String fertilizerName;

    @Column(name = "brand", length = 50)
    @Size(max = 50, message = "Brand must not exceed 50 characters")
    private String brand;

    @Column(name = "composition", length = 100)
    @Size(max = 100, message = "Composition must not exceed 100 characters")
    private String composition; // NPK ratios

    @Column(name = "quantity", precision = 8, scale = 2, nullable = false)
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be positive")
    @Digits(integer = 6, fraction = 2, message = "Quantity format is invalid")
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    @NotNull(message = "Unit is required")
    private Unit unit;

    @Column(name = "application_date", nullable = false)
    @NotNull(message = "Application date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate applicationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_method", nullable = false)
    @NotNull(message = "Application method is required")
    private ApplicationMethod applicationMethod;

    @Column(name = "application_stage", length = 50)
    @Size(max = 50, message = "Application stage must not exceed 50 characters")
    private String applicationStage; // PLANTING, VEGETATIVE, FLOWERING, etc.

    @Column(name = "cost_per_unit", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Cost per unit must be positive")
    @Digits(integer = 6, fraction = 2, message = "Cost per unit format is invalid")
    private BigDecimal costPerUnit;

    @Column(name = "total_cost", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Total cost must be positive")
    @Digits(integer = 8, fraction = 2, message = "Total cost format is invalid")
    private BigDecimal totalCost;

    @Column(name = "supplier", length = 100)
    @Size(max = 100, message = "Supplier must not exceed 100 characters")
    private String supplier;

    @Column(name = "batch_number", length = 50)
    @Size(max = 50, message = "Batch number must not exceed 50 characters")
    private String batchNumber;

    @Column(name = "expiry_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    @Column(name = "weather_conditions", length = 100)
    @Size(max = 100, message = "Weather conditions must not exceed 100 characters")
    private String weatherConditions;

    @Column(name = "soil_conditions", length = 100)
    @Size(max = 100, message = "Soil conditions must not exceed 100 characters")
    private String soilConditions;

    @Column(name = "operator_name", length = 100)
    @Size(max = 100, message = "Operator name must not exceed 100 characters")
    private String operatorName;

    @Column(name = "effectiveness_rating")
    @Min(value = 1, message = "Effectiveness rating must be at least 1")
    @Max(value = 5, message = "Effectiveness rating must not exceed 5")
    private Integer effectivenessRating; // 1-5

    @Column(name = "notes", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Notes must not exceed 3000 characters")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_production_id", insertable = false, updatable = false)
    @JsonIgnore
    private CropProduction cropProduction;

    // Enums
    public enum FertilizerType {
        ORGANIC("Organic"),
        NPK("NPK"),
        NITROGEN("Nitrogen"),
        PHOSPHATE("Phosphate"),
        POTASH("Potash"),
        MICRONUTRIENTS("Micronutrients");

        private final String displayName;

        FertilizerType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Unit {
        KG("Kilograms"),
        TONNES("Tonnes"),
        LITERS("Liters"),
        BAGS("Bags");

        private final String displayName;

        Unit(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ApplicationMethod {
        BROADCAST("Broadcast"),
        BAND("Band"),
        FOLIAR("Foliar"),
        FERTIGATION("Fertigation"),
        SPOT("Spot");

        private final String displayName;

        ApplicationMethod(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ApplicationStage {
        PLANTING("Planting"),
        VEGETATIVE("Vegetative"),
        FLOWERING("Flowering"),
        FRUITING("Fruiting"),
        MATURITY("Maturity"),
        PRE_PLANTING("Pre-Planting"),
        POST_HARVEST("Post-Harvest");

        private final String displayName;

        ApplicationStage(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum EffectivenessLevel {
        VERY_POOR("Very Poor", 1, 1),
        POOR("Poor", 2, 2),
        FAIR("Fair", 3, 3),
        GOOD("Good", 4, 4),
        EXCELLENT("Excellent", 5, 5);

        private final String displayName;
        private final int minRating;
        private final int maxRating;

        EffectivenessLevel(String displayName, int minRating, int maxRating) {
            this.displayName = displayName;
            this.minRating = minRating;
            this.maxRating = maxRating;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getMinRating() {
            return minRating;
        }

        public int getMaxRating() {
            return maxRating;
        }

        public static EffectivenessLevel fromRating(int rating) {
            for (EffectivenessLevel level : EffectivenessLevel.values()) {
                if (rating >= level.minRating && rating <= level.maxRating) {
                    return level;
                }
            }
            return FAIR; // Default
        }
    }

    // Constructors
    public FertilizerUsage() {
        this.id = generateAlphanumericId();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.applicationDate = LocalDate.now();
    }

    public FertilizerUsage(String cropProductionId, FertilizerType fertilizerType, String fertilizerName,
                           BigDecimal quantity, Unit unit, ApplicationMethod applicationMethod) {
        this();
        this.cropProductionId = cropProductionId;
        this.fertilizerType = fertilizerType;
        this.fertilizerName = fertilizerName;
        this.quantity = quantity;
        this.unit = unit;
        this.applicationMethod = applicationMethod;
    }

    // Method to generate alphanumeric ID with mixed letters and numbers
    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Add "FU" prefix for Fertilizer Usage
        sb.append("FU");

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

    // JPA lifecycle methods
    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = generateAlphanumericId();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        // Calculate total cost if cost per unit and quantity are provided
        calculateTotalCost();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        // Recalculate total cost on update
        calculateTotalCost();
    }

    // Utility method to calculate total cost
    private void calculateTotalCost() {
        if (costPerUnit != null && quantity != null) {
            this.totalCost = costPerUnit.multiply(quantity);
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCropProductionId() {
        return cropProductionId;
    }

    public void setCropProductionId(String cropProductionId) {
        this.cropProductionId = cropProductionId;
    }

    public FertilizerType getFertilizerType() {
        return fertilizerType;
    }

    public void setFertilizerType(FertilizerType fertilizerType) {
        this.fertilizerType = fertilizerType;
    }

    public String getFertilizerName() {
        return fertilizerName;
    }

    public void setFertilizerName(String fertilizerName) {
        this.fertilizerName = fertilizerName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getComposition() {
        return composition;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public ApplicationMethod getApplicationMethod() {
        return applicationMethod;
    }

    public void setApplicationMethod(ApplicationMethod applicationMethod) {
        this.applicationMethod = applicationMethod;
    }

    public String getApplicationStage() {
        return applicationStage;
    }

    public void setApplicationStage(String applicationStage) {
        this.applicationStage = applicationStage;
    }

    public BigDecimal getCostPerUnit() {
        return costPerUnit;
    }

    public void setCostPerUnit(BigDecimal costPerUnit) {
        this.costPerUnit = costPerUnit;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getWeatherConditions() {
        return weatherConditions;
    }

    public void setWeatherConditions(String weatherConditions) {
        this.weatherConditions = weatherConditions;
    }

    public String getSoilConditions() {
        return soilConditions;
    }

    public void setSoilConditions(String soilConditions) {
        this.soilConditions = soilConditions;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getEffectivenessRating() {
        return effectivenessRating;
    }

    public void setEffectivenessRating(Integer effectivenessRating) {
        this.effectivenessRating = effectivenessRating;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public CropProduction getCropProduction() {
        return cropProduction;
    }

    public void setCropProduction(CropProduction cropProduction) {
        this.cropProduction = cropProduction;
    }

    // Utility methods
    public String getFertilizerTypeDescription() {
        return fertilizerType != null ? fertilizerType.getDisplayName() : "Unknown";
    }

    public String getUnitDescription() {
        return unit != null ? unit.getDisplayName() : "Unknown";
    }

    public String getApplicationMethodDescription() {
        return applicationMethod != null ? applicationMethod.getDisplayName() : "Unknown";
    }

    public EffectivenessLevel getEffectivenessLevel() {
        if (effectivenessRating == null) return null;
        return EffectivenessLevel.fromRating(effectivenessRating);
    }

    public String getEffectivenessDescription() {
        EffectivenessLevel level = getEffectivenessLevel();
        return level != null ? level.getDisplayName() : "Not rated";
    }

    public BigDecimal getCostPerKg() {
        if (totalCost == null || quantity == null || quantity.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        BigDecimal quantityInKg = convertToKg(quantity, unit);
        if (quantityInKg != null && quantityInKg.compareTo(BigDecimal.ZERO) > 0) {
            return totalCost.divide(quantityInKg, 4, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }

    private BigDecimal convertToKg(BigDecimal quantity, Unit unit) {
        if (quantity == null || unit == null) return null;

        switch (unit) {
            case KG:
                return quantity;
            case TONNES:
                return quantity.multiply(new BigDecimal("1000"));
            case BAGS:
                // Assuming average bag weight of 50kg
                return quantity.multiply(new BigDecimal("50"));
            case LITERS:
                // Assuming liquid fertilizer density of 1.2 kg/L
                return quantity.multiply(new BigDecimal("1.2"));
            default:
                return null;
        }
    }

    public boolean isExpired() {
        if (expiryDate == null) return false;
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean isExpiringSoon() {
        if (expiryDate == null) return false;
        LocalDate warningDate = LocalDate.now().plusMonths(3);
        return expiryDate.isBefore(warningDate) && !isExpired();
    }

    public long getDaysUntilExpiry() {
        if (expiryDate == null) return -1;
        return LocalDate.now().until(expiryDate).getDays();
    }

    public boolean isHighCost() {
        return totalCost != null && totalCost.compareTo(new BigDecimal("5000.0")) > 0;
    }

    public boolean isLowEffectiveness() {
        return effectivenessRating != null && effectivenessRating <= 2;
    }

    public boolean isHighEffectiveness() {
        return effectivenessRating != null && effectivenessRating >= 4;
    }

    public boolean isOrganicFertilizer() {
        return fertilizerType == FertilizerType.ORGANIC;
    }

    public boolean needsOptimization() {
        return isHighCost() || isLowEffectiveness() || isExpired();
    }

    public String getApplicationDateFormatted() {
        return applicationDate != null ?
                applicationDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) :
                "Not set";
    }

    public String getExpiryDateFormatted() {
        return expiryDate != null ?
                expiryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) :
                "Not specified";
    }

    public String getQuantityFormatted() {
        if (quantity == null || unit == null) return "Not specified";
        return quantity + " " + unit.getDisplayName();
    }

    public String getFertilizerSummary() {
        StringBuilder summary = new StringBuilder();

        if (fertilizerName != null) {
            summary.append(fertilizerName);
        }

        if (brand != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append(brand);
        }

        if (quantity != null && unit != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append(getQuantityFormatted());
        }

        if (applicationMethod != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append(applicationMethod.getDisplayName());
        }

        return summary.length() > 0 ? summary.toString() : "No data";
    }

    public String getCostSummary() {
        if (totalCost == null) return "Cost not specified";

        StringBuilder summary = new StringBuilder();
        summary.append("Total: $").append(totalCost);

        if (costPerUnit != null) {
            summary.append(" ($").append(costPerUnit).append(" per ").append(unit.getDisplayName()).append(")");
        }

        BigDecimal costPerKg = getCostPerKg();
        if (costPerKg != null) {
            summary.append(" - $").append(costPerKg).append("/kg");
        }

        return summary.toString();
    }

    public String getCompositionSummary() {
        if (composition != null && !composition.trim().isEmpty()) {
            return composition;
        }

        // Generate basic composition summary based on fertilizer type
        switch (fertilizerType) {
            case NPK:
                return "NPK compound fertilizer";
            case NITROGEN:
                return "Nitrogen-based fertilizer";
            case PHOSPHATE:
                return "Phosphorus-based fertilizer";
            case POTASH:
                return "Potassium-based fertilizer";
            case ORGANIC:
                return "Organic fertilizer";
            case MICRONUTRIENTS:
                return "Micronutrient fertilizer";
            default:
                return "Composition not specified";
        }
    }

    // toString, equals and hashCode
    @Override
    public String toString() {
        return "FertilizerUsage{" +
                "id='" + id + '\'' +
                ", cropProductionId='" + cropProductionId + '\'' +
                ", fertilizerType=" + fertilizerType +
                ", fertilizerName='" + fertilizerName + '\'' +
                ", brand='" + brand + '\'' +
                ", quantity=" + quantity +
                ", unit=" + unit +
                ", applicationDate=" + applicationDate +
                ", applicationMethod=" + applicationMethod +
                ", totalCost=" + totalCost +
                ", effectivenessRating=" + effectivenessRating +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FertilizerUsage that = (FertilizerUsage) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}