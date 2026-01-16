package SmartAgricultural.Management.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Entity
@Table(name = "irrigation_data")
public class IrrigationData {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "farm_id", length = 20, nullable = false)
    @NotBlank(message = "Farm ID is required")
    private String farmId;

    @Column(name = "crop_production_id", length = 20)
    private String cropProductionId;

    @Column(name = "irrigation_date", nullable = false)
    @NotNull(message = "Irrigation date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime irrigationDate;

    @Column(name = "water_amount", precision = 8, scale = 2, nullable = false)
    @NotNull(message = "Water amount is required")
    @DecimalMin(value = "0.01", message = "Water amount must be positive")
    @Digits(integer = 6, fraction = 2, message = "Water amount format is invalid")
    private BigDecimal waterAmount; // litres ou m³

    @Enumerated(EnumType.STRING)
    @Column(name = "irrigation_method", nullable = false)
    @NotNull(message = "Irrigation method is required")
    private IrrigationMethod irrigationMethod;

    @Column(name = "duration")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 1440, message = "Duration must not exceed 1440 minutes (24 hours)")
    private Integer duration; // minutes

    @Enumerated(EnumType.STRING)
    @Column(name = "water_source", nullable = false)
    @NotNull(message = "Water source is required")
    private WaterSource waterSource;

    @Column(name = "water_cost", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Water cost must be positive")
    @Digits(integer = 6, fraction = 2, message = "Water cost format is invalid")
    private BigDecimal waterCost; // coût par unité

    @Column(name = "total_cost", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Total cost must be positive")
    @Digits(integer = 8, fraction = 2, message = "Total cost format is invalid")
    private BigDecimal totalCost;

    @Column(name = "soil_moisture_before", precision = 4, scale = 2)
    @DecimalMin(value = "0.0", message = "Soil moisture before must be positive")
    @DecimalMax(value = "100.0", message = "Soil moisture before cannot exceed 100%")
    @Digits(integer = 2, fraction = 2, message = "Soil moisture before format is invalid")
    private BigDecimal soilMoistureBefore; // %

    @Column(name = "soil_moisture_after", precision = 4, scale = 2)
    @DecimalMin(value = "0.0", message = "Soil moisture after must be positive")
    @DecimalMax(value = "100.0", message = "Soil moisture after cannot exceed 100%")
    @Digits(integer = 2, fraction = 2, message = "Soil moisture after format is invalid")
    private BigDecimal soilMoistureAfter; // %

    @Column(name = "weather_condition", length = 50)
    @Size(max = 50, message = "Weather condition must not exceed 50 characters")
    private String weatherCondition;

    @Column(name = "operator_name", length = 100)
    @Size(max = 100, message = "Operator name must not exceed 100 characters")
    private String operatorName;

    @Column(name = "equipment_used", length = 100)
    @Size(max = 100, message = "Equipment used must not exceed 100 characters")
    private String equipmentUsed;

    @Column(name = "water_quality", length = 50)
    @Size(max = 50, message = "Water quality must not exceed 50 characters")
    private String waterQuality;

    @Column(name = "fertilizer_applied")
    private Boolean fertilizerApplied = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Notes must not exceed 3000 characters")
    private String notes;

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

    // Enums
    public enum IrrigationMethod {
        SPRINKLER("Sprinkler"),
        DRIP("Drip"),
        FLOOD("Flood"),
        FURROW("Furrow"),
        MANUAL("Manual");

        private final String displayName;

        IrrigationMethod(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum WaterSource {
        WELL("Well"),
        RIVER("River"),
        LAKE("Lake"),
        RAINWATER("Rainwater"),
        MUNICIPAL("Municipal");

        private final String displayName;

        WaterSource(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum WaterQualityLevel {
        EXCELLENT("Excellent"),
        GOOD("Good"),
        FAIR("Fair"),
        POOR("Poor"),
        CONTAMINATED("Contaminated");

        private final String displayName;

        WaterQualityLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum EfficiencyLevel {
        VERY_LOW("Very Low", 0.0, 40.0),
        LOW("Low", 40.0, 60.0),
        MEDIUM("Medium", 60.0, 75.0),
        HIGH("High", 75.0, 85.0),
        VERY_HIGH("Very High", 85.0, 100.0);

        private final String displayName;
        private final double minEfficiency;
        private final double maxEfficiency;

        EfficiencyLevel(String displayName, double minEfficiency, double maxEfficiency) {
            this.displayName = displayName;
            this.minEfficiency = minEfficiency;
            this.maxEfficiency = maxEfficiency;
        }

        public String getDisplayName() {
            return displayName;
        }

        public double getMinEfficiency() {
            return minEfficiency;
        }

        public double getMaxEfficiency() {
            return maxEfficiency;
        }

        public static EfficiencyLevel fromValue(double efficiency) {
            for (EfficiencyLevel level : EfficiencyLevel.values()) {
                if (efficiency >= level.minEfficiency && efficiency < level.maxEfficiency) {
                    return level;
                }
            }
            return VERY_HIGH; // Default for values >= 85.0
        }
    }

    // Constructors
    public IrrigationData() {
        this.id = generateAlphanumericId();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.irrigationDate = LocalDateTime.now();
        this.fertilizerApplied = false;
    }

    public IrrigationData(String farmId, BigDecimal waterAmount, IrrigationMethod irrigationMethod, WaterSource waterSource) {
        this();
        this.farmId = farmId;
        this.waterAmount = waterAmount;
        this.irrigationMethod = irrigationMethod;
        this.waterSource = waterSource;
    }

    // Method to generate alphanumeric ID with mixed letters and numbers
    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Add "IR" prefix for Irrigation Data
        sb.append("IR");

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

        // Calculate total cost if water cost and amount are provided
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
        if (waterCost != null && waterAmount != null) {
            this.totalCost = waterCost.multiply(waterAmount);
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public LocalDateTime getIrrigationDate() {
        return irrigationDate;
    }

    public void setIrrigationDate(LocalDateTime irrigationDate) {
        this.irrigationDate = irrigationDate;
    }

    public BigDecimal getWaterAmount() {
        return waterAmount;
    }

    public void setWaterAmount(BigDecimal waterAmount) {
        this.waterAmount = waterAmount;
    }

    public IrrigationMethod getIrrigationMethod() {
        return irrigationMethod;
    }

    public void setIrrigationMethod(IrrigationMethod irrigationMethod) {
        this.irrigationMethod = irrigationMethod;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public WaterSource getWaterSource() {
        return waterSource;
    }

    public void setWaterSource(WaterSource waterSource) {
        this.waterSource = waterSource;
    }

    public BigDecimal getWaterCost() {
        return waterCost;
    }

    public void setWaterCost(BigDecimal waterCost) {
        this.waterCost = waterCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getSoilMoistureBefore() {
        return soilMoistureBefore;
    }

    public void setSoilMoistureBefore(BigDecimal soilMoistureBefore) {
        this.soilMoistureBefore = soilMoistureBefore;
    }

    public BigDecimal getSoilMoistureAfter() {
        return soilMoistureAfter;
    }

    public void setSoilMoistureAfter(BigDecimal soilMoistureAfter) {
        this.soilMoistureAfter = soilMoistureAfter;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getEquipmentUsed() {
        return equipmentUsed;
    }

    public void setEquipmentUsed(String equipmentUsed) {
        this.equipmentUsed = equipmentUsed;
    }

    public String getWaterQuality() {
        return waterQuality;
    }

    public void setWaterQuality(String waterQuality) {
        this.waterQuality = waterQuality;
    }

    public Boolean getFertilizerApplied() {
        return fertilizerApplied;
    }

    public void setFertilizerApplied(Boolean fertilizerApplied) {
        this.fertilizerApplied = fertilizerApplied;
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

    // Utility methods
    public BigDecimal getMoistureIncrease() {
        if (soilMoistureBefore != null && soilMoistureAfter != null) {
            return soilMoistureAfter.subtract(soilMoistureBefore);
        }
        return null;
    }

    public BigDecimal getWaterEfficiency() {
        BigDecimal moistureIncrease = getMoistureIncrease();
        if (moistureIncrease != null && waterAmount != null && waterAmount.compareTo(BigDecimal.ZERO) > 0) {
            // Simple efficiency calculation: moisture increase per liter of water
            return moistureIncrease.divide(waterAmount, 4, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }

    public EfficiencyLevel getEfficiencyLevel() {
        BigDecimal efficiency = getWaterEfficiency();
        if (efficiency == null) return null;

        // Convert to percentage for classification
        double efficiencyPercent = efficiency.doubleValue() * 100;
        return EfficiencyLevel.fromValue(efficiencyPercent);
    }

    public BigDecimal getCostPerLiter() {
        if (totalCost != null && waterAmount != null && waterAmount.compareTo(BigDecimal.ZERO) > 0) {
            return totalCost.divide(waterAmount, 4, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }

    public String getIrrigationMethodDescription() {
        return irrigationMethod != null ? irrigationMethod.getDisplayName() : "Unknown";
    }

    public String getWaterSourceDescription() {
        return waterSource != null ? waterSource.getDisplayName() : "Unknown";
    }

    public String getDurationFormatted() {
        if (duration == null) return "Not specified";

        int hours = duration / 60;
        int minutes = duration % 60;

        if (hours > 0) {
            return String.format("%dh %02dm", hours, minutes);
        } else {
            return String.format("%d minutes", minutes);
        }
    }

    public String getWaterAmountFormatted() {
        return waterAmount != null ? waterAmount + " L" : "Not specified";
    }

    public boolean isExpensiveIrrigation() {
        if (totalCost != null) {
            return totalCost.compareTo(new BigDecimal("1000.0")) > 0;
        }
        return false;
    }

    public boolean isLowEfficiency() {
        EfficiencyLevel level = getEfficiencyLevel();
        return level == EfficiencyLevel.VERY_LOW || level == EfficiencyLevel.LOW;
    }

    public boolean isHighWaterUsage() {
        return waterAmount != null && waterAmount.compareTo(new BigDecimal("10000.0")) > 0;
    }

    public boolean needsOptimization() {
        return isLowEfficiency() || isExpensiveIrrigation() || isHighWaterUsage();
    }

    public String getIrrigationDateFormatted() {
        return irrigationDate != null ?
                irrigationDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) :
                "Not set";
    }

    public String getIrrigationSummary() {
        StringBuilder summary = new StringBuilder();

        if (waterAmount != null) {
            summary.append(waterAmount).append("L");
        }

        if (irrigationMethod != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append(irrigationMethod.getDisplayName());
        }

        if (duration != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append(getDurationFormatted());
        }

        if (waterSource != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Source: ").append(waterSource.getDisplayName());
        }

        return summary.length() > 0 ? summary.toString() : "No data";
    }

    public String getMoistureSummary() {
        if (soilMoistureBefore != null && soilMoistureAfter != null) {
            BigDecimal increase = getMoistureIncrease();
            return String.format("%.1f%% → %.1f%% (+%.1f%%)",
                    soilMoistureBefore.doubleValue(),
                    soilMoistureAfter.doubleValue(),
                    increase.doubleValue());
        }
        return "Not measured";
    }

    // toString, equals and hashCode
    @Override
    public String toString() {
        return "IrrigationData{" +
                "id='" + id + '\'' +
                ", farmId='" + farmId + '\'' +
                ", cropProductionId='" + cropProductionId + '\'' +
                ", irrigationDate=" + irrigationDate +
                ", waterAmount=" + waterAmount +
                ", irrigationMethod=" + irrigationMethod +
                ", duration=" + duration +
                ", waterSource=" + waterSource +
                ", totalCost=" + totalCost +
                ", operatorName='" + operatorName + '\'' +
                ", fertilizerApplied=" + fertilizerApplied +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IrrigationData that = (IrrigationData) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}