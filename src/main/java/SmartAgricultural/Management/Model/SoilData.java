package SmartAgricultural.Management.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Entity
@Table(name = "soil_data")
public class SoilData {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "farm_id", length = 20, nullable = false)
    @NotBlank(message = "Farm ID is required")
    private String farmId;

    @Column(name = "sample_code", length = 30, unique = true)
    @Size(max = 30, message = "Sample code must not exceed 30 characters")
    private String sampleCode;

    @Column(name = "ph_level", precision = 3, scale = 1, nullable = false)
    @NotNull(message = "pH level is required")
    @DecimalMin(value = "0.0", message = "pH level must be positive")
    @DecimalMax(value = "14.0", message = "pH level must not exceed 14.0")
    private BigDecimal phLevel;

    @Column(name = "nitrogen", precision = 6, scale = 2)
    @DecimalMin(value = "0.0", message = "Nitrogen level must be positive")
    @Digits(integer = 4, fraction = 2, message = "Nitrogen format is invalid")
    private BigDecimal nitrogen; // ppm

    @Column(name = "phosphorus", precision = 6, scale = 2)
    @DecimalMin(value = "0.0", message = "Phosphorus level must be positive")
    @Digits(integer = 4, fraction = 2, message = "Phosphorus format is invalid")
    private BigDecimal phosphorus; // ppm

    @Column(name = "potassium", precision = 6, scale = 2)
    @DecimalMin(value = "0.0", message = "Potassium level must be positive")
    @Digits(integer = 4, fraction = 2, message = "Potassium format is invalid")
    private BigDecimal potassium; // ppm

    @Column(name = "organic_matter", precision = 4, scale = 2)
    @DecimalMin(value = "0.0", message = "Organic matter must be positive")
    @DecimalMax(value = "100.0", message = "Organic matter cannot exceed 100%")
    @Digits(integer = 2, fraction = 2, message = "Organic matter format is invalid")
    private BigDecimal organicMatter; // %

    @Column(name = "moisture", precision = 4, scale = 2)
    @DecimalMin(value = "0.0", message = "Moisture must be positive")
    @DecimalMax(value = "100.0", message = "Moisture cannot exceed 100%")
    @Digits(integer = 2, fraction = 2, message = "Moisture format is invalid")
    private BigDecimal moisture; // %

    @Column(name = "soil_texture", length = 50)
    @Size(max = 50, message = "Soil texture must not exceed 50 characters")
    private String soilTexture; // CLAY, LOAM, SAND, etc.

    @Column(name = "bulk_density", precision = 4, scale = 2)
    @DecimalMin(value = "0.0", message = "Bulk density must be positive")
    @DecimalMax(value = "5.0", message = "Bulk density must be realistic")
    @Digits(integer = 2, fraction = 2, message = "Bulk density format is invalid")
    private BigDecimal bulkDensity; // g/cm³

    @Column(name = "porosity", precision = 4, scale = 2)
    @DecimalMin(value = "0.0", message = "Porosity must be positive")
    @DecimalMax(value = "100.0", message = "Porosity cannot exceed 100%")
    @Digits(integer = 2, fraction = 2, message = "Porosity format is invalid")
    private BigDecimal porosity; // %

    @Column(name = "electrical_conductivity", precision = 6, scale = 2)
    @DecimalMin(value = "0.0", message = "Electrical conductivity must be positive")
    @Digits(integer = 4, fraction = 2, message = "Electrical conductivity format is invalid")
    private BigDecimal electricalConductivity; // dS/m

    @Column(name = "cation_exchange_capacity", precision = 6, scale = 2)
    @DecimalMin(value = "0.0", message = "Cation exchange capacity must be positive")
    @Digits(integer = 4, fraction = 2, message = "Cation exchange capacity format is invalid")
    private BigDecimal cationExchangeCapacity; // cmol/kg

    @Column(name = "measurement_date", nullable = false)
    @NotNull(message = "Measurement date is required")
    private LocalDateTime measurementDate;

    @Column(name = "testing_method", length = 100)
    @Size(max = 100, message = "Testing method must not exceed 100 characters")
    private String testingMethod;

    @Column(name = "laboratory_name", length = 100)
    @Size(max = 100, message = "Laboratory name must not exceed 100 characters")
    private String laboratoryName;

    @Column(name = "depth_cm")
    @Min(value = 1, message = "Depth must be at least 1 cm")
    @Max(value = 500, message = "Depth must not exceed 500 cm")
    private Integer depthCm = 30; // Default 30 cm

    @Column(name = "sample_location_gps", length = 50)
    @Size(max = 50, message = "GPS location must not exceed 50 characters")
    private String sampleLocationGps;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Recommendations must not exceed 3000 characters")
    private String recommendations;

    @Column(name = "next_test_due")
    private LocalDate nextTestDue;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationship with Farm
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", insertable = false, updatable = false)
    @JsonIgnore
    private Farm farm;

    // ==================== ENUMERATIONS ====================

    public enum SoilTexture {
        CLAY("Clay"),
        LOAM("Loam"),
        SAND("Sand"),
        SILT("Silt"),
        CLAY_LOAM("Clay Loam"),
        SANDY_LOAM("Sandy Loam"),
        SILTY_LOAM("Silty Loam"),
        SANDY_CLAY("Sandy Clay"),
        SILTY_CLAY("Silty Clay"),
        SANDY_CLAY_LOAM("Sandy Clay Loam"),
        SILTY_CLAY_LOAM("Silty Clay Loam");

        private final String displayName;

        SoilTexture(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum PhLevel {
        VERY_ACIDIC("Very Acidic", 0.0, 4.5),
        ACIDIC("Acidic", 4.5, 6.0),
        SLIGHTLY_ACIDIC("Slightly Acidic", 6.0, 6.8),
        NEUTRAL("Neutral", 6.8, 7.2),
        SLIGHTLY_ALKALINE("Slightly Alkaline", 7.2, 8.0),
        ALKALINE("Alkaline", 8.0, 9.0),
        VERY_ALKALINE("Very Alkaline", 9.0, 14.0);

        private final String displayName;
        private final double minValue;
        private final double maxValue;

        PhLevel(String displayName, double minValue, double maxValue) {
            this.displayName = displayName;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public String getDisplayName() {
            return displayName;
        }

        public double getMinValue() {
            return minValue;
        }

        public double getMaxValue() {
            return maxValue;
        }

        public static PhLevel fromValue(double pH) {
            for (PhLevel level : PhLevel.values()) {
                if (pH >= level.minValue && pH < level.maxValue) {
                    return level;
                }
            }
            return VERY_ALKALINE; // Default for values >= 9.0
        }
    }

    public enum NutrientLevel {
        VERY_LOW("Very Low"),
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High"),
        VERY_HIGH("Very High");

        private final String displayName;

        NutrientLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum SoilCondition {
        GOOD("Good", "Soil conditions are favorable for optimal crop growth"),
        MODERATE("Moderate", "Soil has some issues that require attention"),
        BAD("Bad", "Soil has significant problems that need immediate action");

        private final String displayName;
        private final String description;

        SoilCondition(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    // ==================== CONSTRUCTORS ====================

    public SoilData() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.measurementDate = LocalDateTime.now();
    }

    public SoilData(String farmId, BigDecimal phLevel, LocalDateTime measurementDate) {
        this();
        this.farmId = farmId;
        this.phLevel = phLevel;
        this.measurementDate = measurementDate;
    }

    // ==================== ID GENERATION ====================

    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Add "SD" prefix for Soil Data
        sb.append("SD");

        // Timestamp-based prefix to ensure uniqueness
        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 6);
        sb.append(shortTimestamp);

        // Add random characters
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    public String generateSampleCode(String farmCode) {
        if (farmCode == null || measurementDate == null) {
            return null;
        }

        String datePart = measurementDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.valueOf(new Random().nextInt(1000)).formatted("%03d");

        return String.format("SOIL_%s_%s_%s",
                farmCode.substring(0, Math.min(5, farmCode.length())),
                datePart,
                randomPart
        );
    }

    // ==================== JPA LIFECYCLE METHODS ====================

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = generateAlphanumericId();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        // Set default next test due date (1 year from measurement)
        if (nextTestDue == null && measurementDate != null) {
            this.nextTestDue = measurementDate.toLocalDate().plusYears(1);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== GETTERS AND SETTERS ====================

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

    public String getSampleCode() {
        return sampleCode;
    }

    public void setSampleCode(String sampleCode) {
        this.sampleCode = sampleCode;
    }

    public BigDecimal getPhLevel() {
        return phLevel;
    }

    public void setPhLevel(BigDecimal phLevel) {
        this.phLevel = phLevel;
    }

    public BigDecimal getNitrogen() {
        return nitrogen;
    }

    public void setNitrogen(BigDecimal nitrogen) {
        this.nitrogen = nitrogen;
    }

    public BigDecimal getPhosphorus() {
        return phosphorus;
    }

    public void setPhosphorus(BigDecimal phosphorus) {
        this.phosphorus = phosphorus;
    }

    public BigDecimal getPotassium() {
        return potassium;
    }

    public void setPotassium(BigDecimal potassium) {
        this.potassium = potassium;
    }

    public BigDecimal getOrganicMatter() {
        return organicMatter;
    }

    public void setOrganicMatter(BigDecimal organicMatter) {
        this.organicMatter = organicMatter;
    }

    public BigDecimal getMoisture() {
        return moisture;
    }

    public void setMoisture(BigDecimal moisture) {
        this.moisture = moisture;
    }

    public String getSoilTexture() {
        return soilTexture;
    }

    public void setSoilTexture(String soilTexture) {
        this.soilTexture = soilTexture;
    }

    public BigDecimal getBulkDensity() {
        return bulkDensity;
    }

    public void setBulkDensity(BigDecimal bulkDensity) {
        this.bulkDensity = bulkDensity;
    }

    public BigDecimal getPorosity() {
        return porosity;
    }

    public void setPorosity(BigDecimal porosity) {
        this.porosity = porosity;
    }

    public BigDecimal getElectricalConductivity() {
        return electricalConductivity;
    }

    public void setElectricalConductivity(BigDecimal electricalConductivity) {
        this.electricalConductivity = electricalConductivity;
    }

    public BigDecimal getCationExchangeCapacity() {
        return cationExchangeCapacity;
    }

    public void setCationExchangeCapacity(BigDecimal cationExchangeCapacity) {
        this.cationExchangeCapacity = cationExchangeCapacity;
    }

    public LocalDateTime getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(LocalDateTime measurementDate) {
        this.measurementDate = measurementDate;
    }

    public String getTestingMethod() {
        return testingMethod;
    }

    public void setTestingMethod(String testingMethod) {
        this.testingMethod = testingMethod;
    }

    public String getLaboratoryName() {
        return laboratoryName;
    }

    public void setLaboratoryName(String laboratoryName) {
        this.laboratoryName = laboratoryName;
    }

    public Integer getDepthCm() {
        return depthCm;
    }

    public void setDepthCm(Integer depthCm) {
        this.depthCm = depthCm;
    }

    public String getSampleLocationGps() {
        return sampleLocationGps;
    }

    public void setSampleLocationGps(String sampleLocationGps) {
        this.sampleLocationGps = sampleLocationGps;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public LocalDate getNextTestDue() {
        return nextTestDue;
    }

    public void setNextTestDue(LocalDate nextTestDue) {
        this.nextTestDue = nextTestDue;
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

    // ==================== UTILITY METHODS ====================

    public PhLevel getPhLevelCategory() {
        if (phLevel == null) return null;
        return PhLevel.fromValue(phLevel.doubleValue());
    }

    public String getPhLevelDescription() {
        PhLevel level = getPhLevelCategory();
        return level != null ? level.getDisplayName() : "Unknown";
    }

    public NutrientLevel getNitrogenLevel() {
        if (nitrogen == null) return null;

        double n = nitrogen.doubleValue();
        if (n < 20) return NutrientLevel.VERY_LOW;
        if (n < 40) return NutrientLevel.LOW;
        if (n < 80) return NutrientLevel.MEDIUM;
        if (n < 150) return NutrientLevel.HIGH;
        return NutrientLevel.VERY_HIGH;
    }

    public NutrientLevel getPhosphorusLevel() {
        if (phosphorus == null) return null;

        double p = phosphorus.doubleValue();
        if (p < 10) return NutrientLevel.VERY_LOW;
        if (p < 25) return NutrientLevel.LOW;
        if (p < 50) return NutrientLevel.MEDIUM;
        if (p < 100) return NutrientLevel.HIGH;
        return NutrientLevel.VERY_HIGH;
    }

    public NutrientLevel getPotassiumLevel() {
        if (potassium == null) return null;

        double k = potassium.doubleValue();
        if (k < 80) return NutrientLevel.VERY_LOW;
        if (k < 150) return NutrientLevel.LOW;
        if (k < 300) return NutrientLevel.MEDIUM;
        if (k < 500) return NutrientLevel.HIGH;
        return NutrientLevel.VERY_HIGH;
    }

    public boolean isTestDue() {
        if (nextTestDue == null) return false;
        return LocalDate.now().isAfter(nextTestDue) || LocalDate.now().equals(nextTestDue);
    }

    public boolean isTestOverdue() {
        if (nextTestDue == null) return false;
        return LocalDate.now().isAfter(nextTestDue);
    }

    public long getDaysUntilNextTest() {
        if (nextTestDue == null) return -1;
        return LocalDate.now().until(nextTestDue).getDays();
    }

    public boolean hasSalinityIssues() {
        return electricalConductivity != null &&
                electricalConductivity.compareTo(new BigDecimal("2.0")) > 0;
    }

    public boolean hasLowOrganicMatter() {
        return organicMatter != null &&
                organicMatter.compareTo(new BigDecimal("2.0")) < 0;
    }

    public boolean isWellDrained() {
        return porosity != null &&
                porosity.compareTo(new BigDecimal("40.0")) > 0;
    }

    public String getSoilQualityScore() {
        if (phLevel == null) return "Incomplete Data";

        int score = 0;
        int maxScore = 0;

        // pH score (30%)
        maxScore += 30;
        PhLevel phCategory = getPhLevelCategory();
        if (phCategory == PhLevel.NEUTRAL || phCategory == PhLevel.SLIGHTLY_ACIDIC) {
            score += 30;
        } else if (phCategory == PhLevel.SLIGHTLY_ALKALINE || phCategory == PhLevel.ACIDIC) {
            score += 20;
        } else {
            score += 10;
        }

        // Organic matter score (25%)
        if (organicMatter != null) {
            maxScore += 25;
            double om = organicMatter.doubleValue();
            if (om >= 5.0) score += 25;
            else if (om >= 3.0) score += 20;
            else if (om >= 2.0) score += 15;
            else if (om >= 1.0) score += 10;
            else score += 5;
        }

        // NPK score (25%)
        if (nitrogen != null && phosphorus != null && potassium != null) {
            maxScore += 25;
            int nutrientScore = 0;

            NutrientLevel nLevel = getNitrogenLevel();
            NutrientLevel pLevel = getPhosphorusLevel();
            NutrientLevel kLevel = getPotassiumLevel();

            if (nLevel == NutrientLevel.MEDIUM || nLevel == NutrientLevel.HIGH) nutrientScore += 8;
            if (pLevel == NutrientLevel.MEDIUM || pLevel == NutrientLevel.HIGH) nutrientScore += 8;
            if (kLevel == NutrientLevel.MEDIUM || kLevel == NutrientLevel.HIGH) nutrientScore += 9;

            score += nutrientScore;
        }

        // Physical properties score (20%)
        if (porosity != null && bulkDensity != null) {
            maxScore += 20;
            double p = porosity.doubleValue();
            double bd = bulkDensity.doubleValue();

            if (p > 40 && bd < 1.4) score += 20;
            else if (p > 30 && bd < 1.6) score += 15;
            else if (p > 20 && bd < 1.8) score += 10;
            else score += 5;
        }

        if (maxScore == 0) return "No Data";

        int percentage = (score * 100) / maxScore;

        if (percentage >= 80) return "Excellent";
        if (percentage >= 65) return "Good";
        if (percentage >= 50) return "Fair";
        if (percentage >= 35) return "Poor";
        return "Very Poor";
    }

    public String getMeasurementDateFormatted() {
        return measurementDate != null ?
                measurementDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) :
                "Not set";
    }

    public String getNextTestDueFormatted() {
        return nextTestDue != null ?
                nextTestDue.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) :
                "Not scheduled";
    }

    public String getDepthDisplay() {
        return depthCm != null ? depthCm + " cm" : "Not specified";
    }

    public String getNutrientSummary() {
        StringBuilder summary = new StringBuilder();

        if (nitrogen != null) {
            summary.append("N: ").append(nitrogen).append(" ppm");
        }
        if (phosphorus != null) {
            if (summary.length() > 0) summary.append(", ");
            summary.append("P: ").append(phosphorus).append(" ppm");
        }
        if (potassium != null) {
            if (summary.length() > 0) summary.append(", ");
            summary.append("K: ").append(potassium).append(" ppm");
        }

        return summary.length() > 0 ? summary.toString() : "Not measured";
    }

    // ==================== SOIL CONDITION ASSESSMENT ====================

    public SoilCondition evaluateSoilCondition() {
        if (phLevel == null) {
            return SoilCondition.BAD;
        }

        int score = 0;
        int maxScore = 100;

        // pH Score (25 points)
        double ph = phLevel.doubleValue();
        if (ph >= 6.0 && ph <= 7.5) {
            score += 25; // Optimal range
        } else if (ph >= 5.5 && ph <= 8.0) {
            score += 15; // Acceptable range
        } else if (ph >= 4.5 && ph <= 8.5) {
            score += 5; // Marginal range
        } else {
            score += 0; // Critical
        }

        // Nutrient Score (30 points)
        if (nitrogen != null && phosphorus != null && potassium != null) {
            double n = nitrogen.doubleValue();
            double p = phosphorus.doubleValue();
            double k = potassium.doubleValue();

            if (n >= 40 && n <= 150 && p >= 25 && p <= 100 && k >= 150 && k <= 500) {
                score += 30; // All nutrients optimal
            } else if (n >= 20 && n <= 200 && p >= 15 && p <= 120 && k >= 100 && k <= 600) {
                score += 20; // Acceptable levels
            } else if (n >= 10 && p >= 10 && k >= 50) {
                score += 10; // Minimal levels
            } else {
                score += 0; // Deficient
            }
        } else {
            maxScore -= 30;
        }

        // Organic Matter Score (20 points)
        if (organicMatter != null) {
            double om = organicMatter.doubleValue();
            if (om >= 4.0) {
                score += 20; // Excellent
            } else if (om >= 3.0) {
                score += 15; // Good
            } else if (om >= 2.0) {
                score += 10; // Fair
            } else if (om >= 1.0) {
                score += 5; // Low
            } else {
                score += 0; // Very low
            }
        } else {
            maxScore -= 20;
        }

        // Moisture Score (15 points)
        if (moisture != null) {
            double m = moisture.doubleValue();
            if (m >= 25 && m <= 45) {
                score += 15; // Optimal moisture
            } else if (m >= 20 && m <= 60) {
                score += 10; // Acceptable
            } else if (m >= 10 && m <= 70) {
                score += 5; // Marginal
            } else {
                score += 0; // Extreme
            }
        } else {
            maxScore -= 15;
        }

        // EC Score (10 points - Electrical Conductivity for salinity)
        if (electricalConductivity != null) {
            double ec = electricalConductivity.doubleValue();
            if (ec < 0.8) {
                score += 10; // Very low salinity
            } else if (ec < 2.0) {
                score += 8; // Low salinity
            } else if (ec < 4.0) {
                score += 5; // Moderate salinity
            } else if (ec < 8.0) {
                score += 2; // High salinity
            } else {
                score += 0; // Very high salinity
            }
        } else {
            maxScore -= 10;
        }

        // Calculate percentage score
        int percentage = (maxScore > 0) ? (score * 100) / maxScore : 0;

        // Determine soil condition
        if (percentage >= 75) {
            return SoilCondition.GOOD;
        } else if (percentage >= 50) {
            return SoilCondition.MODERATE;
        } else {
            return SoilCondition.BAD;
        }
    }

    public List<String> getSoilConditionRecommendations() {
        SoilCondition condition = evaluateSoilCondition();
        List<String> recommendations = new ArrayList<>();

        // pH recommendations
        if (phLevel != null) {
            double ph = phLevel.doubleValue();
            if (ph < 5.5) {
                recommendations.add("Apply agricultural lime to increase soil pH (target: 6.0-7.0)");
            } else if (ph > 8.0) {
                recommendations.add("Apply sulfur or organic matter to decrease soil pH");
            } else if (ph >= 6.0 && ph <= 7.5) {
                recommendations.add("pH is optimal, maintain current management");
            }
        }

        // Nutrient recommendations
        if (nitrogen != null && nitrogen.doubleValue() < 40) {
            recommendations.add("Apply nitrogen fertilizer (urea or ammonium nitrate)");
        }
        if (phosphorus != null && phosphorus.doubleValue() < 25) {
            recommendations.add("Apply phosphorus fertilizer (DAP or superphosphate)");
        }
        if (potassium != null && potassium.doubleValue() < 150) {
            recommendations.add("Apply potassium fertilizer (potassium chloride or potassium sulfate)");
        }

        // Organic matter recommendations
        if (organicMatter != null && organicMatter.doubleValue() < 2.0) {
            recommendations.add("Add compost, manure, or green manure to increase organic matter");
        }

        // Moisture recommendations
        if (moisture != null) {
            double m = moisture.doubleValue();
            if (m < 20) {
                recommendations.add("Improve irrigation or implement water conservation practices");
            } else if (m > 60) {
                recommendations.add("Improve drainage to prevent waterlogging");
            }
        }

        // EC recommendations
        if (electricalConductivity != null && electricalConductivity.doubleValue() > 2.0) {
            recommendations.add("Leach soil with good quality water to reduce salinity");
            recommendations.add("Use salt-tolerant crops");
            recommendations.add("Avoid fertilizers with high salt index");
        }

        // If no specific issues, add general recommendations
        if (recommendations.isEmpty() && condition == SoilCondition.GOOD) {
            recommendations.add("Continue current soil management practices");
            recommendations.add("Regular soil testing recommended (every 1-2 years)");
            recommendations.add("Maintain organic matter through cover crops");
        }

        return recommendations;
    }

    public Map<String, Object> getSoilConditionReport() {
        Map<String, Object> report = new HashMap<>();

        SoilCondition condition = evaluateSoilCondition();
        report.put("condition", condition.getDisplayName());
        report.put("conditionDescription", condition.getDescription());

        // Scores individuels
        Map<String, Integer> componentScores = new HashMap<>();

        // pH Score
        if (phLevel != null) {
            double ph = phLevel.doubleValue();
            componentScores.put("pH", (ph >= 6.0 && ph <= 7.5) ? 100 : (ph >= 5.5 && ph <= 8.0) ? 60 : 30);
        }

        // Nutrient Score
        if (nitrogen != null && phosphorus != null && potassium != null) {
            double avgNutrientScore = 0;
            avgNutrientScore += (nitrogen.doubleValue() >= 40 && nitrogen.doubleValue() <= 150) ? 100 : 60;
            avgNutrientScore += (phosphorus.doubleValue() >= 25 && phosphorus.doubleValue() <= 100) ? 100 : 60;
            avgNutrientScore += (potassium.doubleValue() >= 150 && potassium.doubleValue() <= 500) ? 100 : 60;
            componentScores.put("Nutrients", (int)(avgNutrientScore / 3));
        }

        // Organic Matter Score
        if (organicMatter != null) {
            double om = organicMatter.doubleValue();
            componentScores.put("OrganicMatter", om >= 4.0 ? 100 : om >= 3.0 ? 80 : om >= 2.0 ? 60 : 40);
        }

        report.put("componentScores", componentScores);
        report.put("recommendations", getSoilConditionRecommendations());

        // Timestamp
        report.put("assessmentDate", LocalDateTime.now());

        return report;
    }

    public Map<String, Object> getSoilAttributesForAI() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("id", this.id);
        attributes.put("farmId", this.farmId);
        attributes.put("sampleCode", this.sampleCode);

        // Propriétés chimiques
        attributes.put("pH", this.phLevel != null ? this.phLevel.doubleValue() : null);
        attributes.put("nitrogen", this.nitrogen != null ? this.nitrogen.doubleValue() : null);
        attributes.put("phosphorus", this.phosphorus != null ? this.phosphorus.doubleValue() : null);
        attributes.put("potassium", this.potassium != null ? this.potassium.doubleValue() : null);
        attributes.put("organicMatter", this.organicMatter != null ? this.organicMatter.doubleValue() : null);
        attributes.put("electricalConductivity", this.electricalConductivity != null ? this.electricalConductivity.doubleValue() : null);

        // Propriétés physiques
        attributes.put("soilTexture", this.soilTexture);
        attributes.put("moisture", this.moisture != null ? this.moisture.doubleValue() : null);
        attributes.put("bulkDensity", this.bulkDensity != null ? this.bulkDensity.doubleValue() : null);
        attributes.put("porosity", this.porosity != null ? this.porosity.doubleValue() : null);
        attributes.put("cationExchangeCapacity", this.cationExchangeCapacity != null ? this.cationExchangeCapacity.doubleValue() : null);
        attributes.put("depth", this.depthCm);

        // Données temporelles
        attributes.put("measurementDate", this.measurementDate != null ? this.measurementDate.toString() : null);

        // Évaluation
        attributes.put("soilCondition", evaluateSoilCondition().getDisplayName());
        attributes.put("soilQualityScore", getSoilQualityScore());

        return attributes;
    }

    // ==================== toString, equals and hashCode ====================

    @Override
    public String toString() {
        return "SoilData{" +
                "id='" + id + '\'' +
                ", farmId='" + farmId + '\'' +
                ", sampleCode='" + sampleCode + '\'' +
                ", phLevel=" + phLevel +
                ", nitrogen=" + nitrogen +
                ", phosphorus=" + phosphorus +
                ", potassium=" + potassium +
                ", organicMatter=" + organicMatter +
                ", measurementDate=" + measurementDate +
                ", depthCm=" + depthCm +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SoilData soilData = (SoilData) o;
        return id != null ? id.equals(soilData.id) : soilData.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}