package SmartAgricultural.Management.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Entity
@Table(name = "crop_productions")
public class CropProduction {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    // ⭐ MODIFIÉ : Maintenant farm_id référence la table users (farmers)
    @Column(name = "farm_id", length = 20, nullable = false)
    @NotBlank(message = "Farmer is required")
    private String farmId; // C'est maintenant un User ID avec role FARMER

    @Column(name = "crop_id", length = 20, nullable = false)
    @NotBlank(message = "Crop ID is required")
    private String cropId;

    @Column(name = "production_code", length = 30, unique = true, nullable = false)
    @NotBlank(message = "Production code is required")
    @Size(max = 30, message = "Production code must not exceed 30 characters")
    private String productionCode;

    @Column(name = "planting_date", nullable = false)
    @NotNull(message = "Planting date is required")
    private LocalDate plantingDate;

    @Column(name = "expected_harvest_date", nullable = false)
    @NotNull(message = "Expected harvest date is required")
    private LocalDate expectedHarvestDate;

    @Column(name = "actual_harvest_date")
    private LocalDate actualHarvestDate;

    @Column(name = "area_planted", precision = 8, scale = 2, nullable = false)
    @NotNull(message = "Area planted is required")
    @DecimalMin(value = "0.01", message = "Area planted must be greater than 0")
    @Digits(integer = 6, fraction = 2, message = "Area planted format is invalid")
    private BigDecimal areaPlanted;

    @Column(name = "expected_yield", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Expected yield must be positive")
    @Digits(integer = 6, fraction = 2, message = "Expected yield format is invalid")
    private BigDecimal expectedYield;

    @Column(name = "actual_yield", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Actual yield must be positive")
    @Digits(integer = 6, fraction = 2, message = "Actual yield format is invalid")
    private BigDecimal actualYield;

    @Column(name = "total_production", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Total production must be positive")
    @Digits(integer = 8, fraction = 2, message = "Total production format is invalid")
    private BigDecimal totalProduction;


    @Column(name = "estimated_price", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Estimated price must be positive")
    private BigDecimal estimatedPrice;

    @Column(name = "price_per_kg", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Price per kg must be positive")
    private BigDecimal pricePerKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "production_status")
    private ProductionStatus productionStatus = ProductionStatus.PLANNED;

    @Enumerated(EnumType.STRING)
    @Column(name = "season", nullable = false)
    @NotNull(message = "Season is required")
    private Season season;

    @Column(name = "year", nullable = false)
    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be 2000 or later")
    @Max(value = 2100, message = "Year must not exceed 2100")
    private Integer year;

    @Column(name = "seed_variety", length = 100)
    @Size(max = 100, message = "Seed variety must not exceed 100 characters")
    private String seedVariety;

    @Column(name = "seed_source", length = 100)
    @Size(max = 100, message = "Seed source must not exceed 100 characters")
    private String seedSource;

    @Enumerated(EnumType.STRING)
    @Column(name = "production_method")
    private ProductionMethod productionMethod = ProductionMethod.CONVENTIONAL;

    @Column(name = "certification", length = 50)
    @Size(max = 50, message = "Certification must not exceed 50 characters")
    private String certification;

    @Column(name = "notes", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ⭐ MODIFIÉ : Relationships - Maintenant farmer référence User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", insertable = false, updatable = false)
    @JsonIgnore
    private User farmer; // Changé de Farm à User

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", insertable = false, updatable = false)
    @JsonIgnore
    private Crop crop;

    // Enums (inchangés)
    public enum ProductionStatus {
        PLANNED("Planned"),
        PLANTED("Planted"),
        GROWING("Growing"),
        HARVESTED("Harvested"),
        SOLD("Sold");

        private final String displayName;

        ProductionStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Season {
        SEASON_A("Season A"),
        SEASON_B("Season B"),
        SEASON_C("Season C"),
        OFF_SEASON("Off Season");

        private final String displayName;

        Season(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ProductionMethod {
        ORGANIC("Organic"),
        CONVENTIONAL("Conventional"),
        INTEGRATED("Integrated");

        private final String displayName;

        ProductionMethod(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public CropProduction() {
        this.id = generateAlphanumericId();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public CropProduction(String farmId, String cropId, String productionCode,
                          LocalDate plantingDate, LocalDate expectedHarvestDate,
                          BigDecimal areaPlanted, Season season, Integer year) {
        this();
        this.farmId = farmId;
        this.cropId = cropId;
        this.productionCode = productionCode;
        this.plantingDate = plantingDate;
        this.expectedHarvestDate = expectedHarvestDate;
        this.areaPlanted = areaPlanted;
        this.season = season;
        this.year = year;
    }

    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append("CP");
        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 6);
        sb.append(shortTimestamp);
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = generateAlphanumericId();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        calculateTotalProduction();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculateTotalProduction();
    }

    private void calculateTotalProduction() {
        if (actualYield != null && areaPlanted != null) {
            this.totalProduction = actualYield.multiply(areaPlanted);
        } else if (expectedYield != null && areaPlanted != null && totalProduction == null) {
            this.totalProduction = expectedYield.multiply(areaPlanted);
        }
    }

    // ⭐ GETTER/SETTER MODIFIÉS pour Farmer
    public User getFarmer() {
        return farmer;
    }

    public void setFarmer(User farmer) {
        this.farmer = farmer;
    }

    // Tous les autres getters/setters (inchangés)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFarmId() { return farmId; }
    public void setFarmId(String farmId) { this.farmId = farmId; }

    public String getCropId() { return cropId; }
    public void setCropId(String cropId) { this.cropId = cropId; }

    public String getProductionCode() { return productionCode; }
    public void setProductionCode(String productionCode) { this.productionCode = productionCode; }

    public LocalDate getPlantingDate() { return plantingDate; }
    public void setPlantingDate(LocalDate plantingDate) { this.plantingDate = plantingDate; }

    public LocalDate getExpectedHarvestDate() { return expectedHarvestDate; }
    public void setExpectedHarvestDate(LocalDate expectedHarvestDate) { this.expectedHarvestDate = expectedHarvestDate; }

    public LocalDate getActualHarvestDate() { return actualHarvestDate; }
    public void setActualHarvestDate(LocalDate actualHarvestDate) { this.actualHarvestDate = actualHarvestDate; }

    public BigDecimal getAreaPlanted() { return areaPlanted; }
    public void setAreaPlanted(BigDecimal areaPlanted) {
        this.areaPlanted = areaPlanted;
        calculateTotalProduction();
    }

    public BigDecimal getExpectedYield() { return expectedYield; }
    public void setExpectedYield(BigDecimal expectedYield) {
        this.expectedYield = expectedYield;
        calculateTotalProduction();
    }

    public BigDecimal getActualYield() { return actualYield; }
    public void setActualYield(BigDecimal actualYield) {
        this.actualYield = actualYield;
        calculateTotalProduction();
    }

    public BigDecimal getTotalProduction() { return totalProduction; }
    public void setTotalProduction(BigDecimal totalProduction) { this.totalProduction = totalProduction; }

    public ProductionStatus getProductionStatus() { return productionStatus; }
    public void setProductionStatus(ProductionStatus productionStatus) { this.productionStatus = productionStatus; }

    public Season getSeason() { return season; }
    public void setSeason(Season season) { this.season = season; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getSeedVariety() { return seedVariety; }
    public void setSeedVariety(String seedVariety) { this.seedVariety = seedVariety; }

    public String getSeedSource() { return seedSource; }
    public void setSeedSource(String seedSource) { this.seedSource = seedSource; }

    public ProductionMethod getProductionMethod() { return productionMethod; }
    public void setProductionMethod(ProductionMethod productionMethod) { this.productionMethod = productionMethod; }

    public String getCertification() { return certification; }
    public void setCertification(String certification) { this.certification = certification; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Crop getCrop() { return crop; }
    public void setCrop(Crop crop) { this.crop = crop; }

    // Utility methods (inchangés)
    public boolean isHarvested() {
        return productionStatus == ProductionStatus.HARVESTED || productionStatus == ProductionStatus.SOLD;
    }

    public boolean isSold() {
        return productionStatus == ProductionStatus.SOLD;
    }

    public boolean isActive() {
        return productionStatus == ProductionStatus.PLANTED || productionStatus == ProductionStatus.GROWING;
    }

    public boolean isOverdue() {
        return expectedHarvestDate != null && LocalDate.now().isAfter(expectedHarvestDate) && !isHarvested();
    }

    public long getDaysToHarvest() {
        if (expectedHarvestDate == null || isHarvested()) return 0;
        return LocalDate.now().until(expectedHarvestDate).getDays();
    }

    public long getDaysSincePlanting() {
        if (plantingDate == null) return 0;
        return plantingDate.until(LocalDate.now()).getDays();
    }

    public String getProductionCycle() {
        return season.getDisplayName() + " " + year;
    }

    public BigDecimal getYieldEfficiency() {
        if (expectedYield == null || actualYield == null || expectedYield.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return actualYield.divide(expectedYield, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }


    public BigDecimal getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(BigDecimal estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }
    public BigDecimal getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(BigDecimal pricePerKg) {
        this.pricePerKg = pricePerKg;
    }
    public String getPlantingDateFormatted() {
        return plantingDate != null ? plantingDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Not set";
    }

    public String getExpectedHarvestDateFormatted() {
        return expectedHarvestDate != null ? expectedHarvestDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Not set";
    }

    public String getActualHarvestDateFormatted() {
        return actualHarvestDate != null ? actualHarvestDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Not harvested";
    }

    public boolean isOrganic() {
        return productionMethod == ProductionMethod.ORGANIC;
    }

    public boolean hasCertification() {
        return certification != null && !certification.trim().isEmpty();
    }

    public String getAreaPlantedDisplay() {
        return areaPlanted != null ? areaPlanted + " hectares" : "Not specified";
    }

    public String getExpectedYieldDisplay() {
        return expectedYield != null ? expectedYield + " t/ha" : "Not specified";
    }

    public String getActualYieldDisplay() {
        return actualYield != null ? actualYield + " t/ha" : "Not measured";
    }

    public String getTotalProductionDisplay() {
        return totalProduction != null ? totalProduction + " tonnes" : "Not calculated";
    }

    @Override
    public String toString() {
        return "CropProduction{" +
                "id='" + id + '\'' +
                ", farmId='" + farmId + '\'' +
                ", cropId='" + cropId + '\'' +
                ", productionCode='" + productionCode + '\'' +
                ", plantingDate=" + plantingDate +
                ", expectedHarvestDate=" + expectedHarvestDate +
                ", areaPlanted=" + areaPlanted +
                ", productionStatus=" + productionStatus +
                ", season=" + season +
                ", year=" + year +
                ", productionMethod=" + productionMethod +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CropProduction that = (CropProduction) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}