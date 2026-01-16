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
@Table(name = "market_prices")
public class MarketPrice {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "crop_id", length = 20, nullable = false)
    @NotBlank(message = "Crop ID is required")
    private String cropId;

    @Column(name = "market_name", length = 100, nullable = false)
    @NotBlank(message = "Market name is required")
    @Size(max = 100, message = "Market name must not exceed 100 characters")
    private String marketName;

    @Enumerated(EnumType.STRING)
    @Column(name = "market_type", nullable = false)
    @NotNull(message = "Market type is required")
    private MarketType marketType;

    @Column(name = "location", length = 100, nullable = false)
    @NotBlank(message = "Location is required")
    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;

    @Column(name = "price_date", nullable = false)
    @NotNull(message = "Price date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate priceDate;

    @Column(name = "price_per_kg", precision = 8, scale = 2, nullable = false)
    @NotNull(message = "Price per kg is required")
    @DecimalMin(value = "0.01", message = "Price per kg must be positive")
    @Digits(integer = 6, fraction = 2, message = "Price per kg format is invalid")
    private BigDecimal pricePerKg;

    @Column(name = "currency", length = 3)
    @Size(max = 3, message = "Currency code must not exceed 3 characters")
    private String currency = "RWF";

    @Column(name = "quality_grade", length = 50)
    @Size(max = 50, message = "Quality grade must not exceed 50 characters")
    private String qualityGrade;

    @Enumerated(EnumType.STRING)
    @Column(name = "demand_level")
    private DemandLevel demandLevel = DemandLevel.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(name = "supply_level")
    private SupplyLevel supplyLevel = SupplyLevel.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_trend")
    private PriceTrend priceTrend = PriceTrend.STABLE;

    @Column(name = "seasonal_factor", precision = 4, scale = 2)
    @DecimalMin(value = "0.1", message = "Seasonal factor must be positive")
    @DecimalMax(value = "5.0", message = "Seasonal factor must not exceed 5.0")
    @Digits(integer = 2, fraction = 2, message = "Seasonal factor format is invalid")
    private BigDecimal seasonalFactor;

    @Column(name = "transport_cost", precision = 6, scale = 2)
    @DecimalMin(value = "0.0", message = "Transport cost must be positive")
    @Digits(integer = 4, fraction = 2, message = "Transport cost format is invalid")
    private BigDecimal transportCost;

    @Column(name = "storage_cost", precision = 6, scale = 2)
    @DecimalMin(value = "0.0", message = "Storage cost must be positive")
    @Digits(integer = 4, fraction = 2, message = "Storage cost format is invalid")
    private BigDecimal storageCost;

    @Column(name = "processing_cost", precision = 6, scale = 2)
    @DecimalMin(value = "0.0", message = "Processing cost must be positive")
    @Digits(integer = 4, fraction = 2, message = "Processing cost format is invalid")
    private BigDecimal processingCost;

    @Column(name = "data_source", length = 50, nullable = false)
    @NotBlank(message = "Data source is required")
    @Size(max = 50, message = "Data source must not exceed 50 characters")
    private String dataSource;

    @Column(name = "data_collector", length = 100)
    @Size(max = 100, message = "Data collector must not exceed 100 characters")
    private String dataCollector;

    @Column(name = "reliability_score")
    @Min(value = 1, message = "Reliability score must be at least 1")
    @Max(value = 5, message = "Reliability score must not exceed 5")
    private Integer reliabilityScore = 5;

    @Column(name = "notes", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Notes must not exceed 3000 characters")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", insertable = false, updatable = false)
    @JsonIgnore
    private Crop crop;

    // Enums
    public enum MarketType {
        WHOLESALE("Wholesale"),
        RETAIL("Retail"),
        FARM_GATE("Farm Gate"),
        EXPORT("Export"),
        COMMODITY_EXCHANGE("Commodity Exchange");

        private final String displayName;

        MarketType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum DemandLevel {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High"),
        VERY_HIGH("Very High");

        private final String displayName;

        DemandLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum SupplyLevel {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High"),
        EXCESS("Excess");

        private final String displayName;

        SupplyLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum PriceTrend {
        INCREASING("Increasing"),
        STABLE("Stable"),
        DECREASING("Decreasing");

        private final String displayName;

        PriceTrend(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ReliabilityLevel {
        VERY_LOW("Very Low", 1, 1),
        LOW("Low", 2, 2),
        MEDIUM("Medium", 3, 3),
        HIGH("High", 4, 4),
        VERY_HIGH("Very High", 5, 5);

        private final String displayName;
        private final int minScore;
        private final int maxScore;

        ReliabilityLevel(String displayName, int minScore, int maxScore) {
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

        public static ReliabilityLevel fromScore(int score) {
            for (ReliabilityLevel level : ReliabilityLevel.values()) {
                if (score >= level.minScore && score <= level.maxScore) {
                    return level;
                }
            }
            return MEDIUM; // Default
        }
    }

    // Constructors
    public MarketPrice() {
        this.id = generateAlphanumericId();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.priceDate = LocalDate.now();
    }

    public MarketPrice(String cropId, String marketName, MarketType marketType,
                       String location, BigDecimal pricePerKg, String dataSource) {
        this();
        this.cropId = cropId;
        this.marketName = marketName;
        this.marketType = marketType;
        this.location = location;
        this.pricePerKg = pricePerKg;
        this.dataSource = dataSource;
    }

    // Method to generate alphanumeric ID with mixed letters and numbers
    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Add "MP" prefix for Market Price
        sb.append("MP");

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
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public MarketType getMarketType() {
        return marketType;
    }

    public void setMarketType(MarketType marketType) {
        this.marketType = marketType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(LocalDate priceDate) {
        this.priceDate = priceDate;
    }

    public BigDecimal getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(BigDecimal pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getQualityGrade() {
        return qualityGrade;
    }

    public void setQualityGrade(String qualityGrade) {
        this.qualityGrade = qualityGrade;
    }

    public DemandLevel getDemandLevel() {
        return demandLevel;
    }

    public void setDemandLevel(DemandLevel demandLevel) {
        this.demandLevel = demandLevel;
    }

    public SupplyLevel getSupplyLevel() {
        return supplyLevel;
    }

    public void setSupplyLevel(SupplyLevel supplyLevel) {
        this.supplyLevel = supplyLevel;
    }

    public PriceTrend getPriceTrend() {
        return priceTrend;
    }

    public void setPriceTrend(PriceTrend priceTrend) {
        this.priceTrend = priceTrend;
    }

    public BigDecimal getSeasonalFactor() {
        return seasonalFactor;
    }

    public void setSeasonalFactor(BigDecimal seasonalFactor) {
        this.seasonalFactor = seasonalFactor;
    }

    public BigDecimal getTransportCost() {
        return transportCost;
    }

    public void setTransportCost(BigDecimal transportCost) {
        this.transportCost = transportCost;
    }

    public BigDecimal getStorageCost() {
        return storageCost;
    }

    public void setStorageCost(BigDecimal storageCost) {
        this.storageCost = storageCost;
    }

    public BigDecimal getProcessingCost() {
        return processingCost;
    }

    public void setProcessingCost(BigDecimal processingCost) {
        this.processingCost = processingCost;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getDataCollector() {
        return dataCollector;
    }

    public void setDataCollector(String dataCollector) {
        this.dataCollector = dataCollector;
    }

    public Integer getReliabilityScore() {
        return reliabilityScore;
    }

    public void setReliabilityScore(Integer reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
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

    public Crop getCrop() {
        return crop;
    }

    public void setCrop(Crop crop) {
        this.crop = crop;
    }

    // Utility methods
    public String getMarketTypeDescription() {
        return marketType != null ? marketType.getDisplayName() : "Unknown";
    }

    public String getDemandLevelDescription() {
        return demandLevel != null ? demandLevel.getDisplayName() : "Unknown";
    }

    public String getSupplyLevelDescription() {
        return supplyLevel != null ? supplyLevel.getDisplayName() : "Unknown";
    }

    public String getPriceTrendDescription() {
        return priceTrend != null ? priceTrend.getDisplayName() : "Unknown";
    }

    public ReliabilityLevel getReliabilityLevel() {
        if (reliabilityScore == null) return null;
        return ReliabilityLevel.fromScore(reliabilityScore);
    }

    public String getReliabilityDescription() {
        ReliabilityLevel level = getReliabilityLevel();
        return level != null ? level.getDisplayName() : "Not rated";
    }

    public BigDecimal getTotalCostPerKg() {
        BigDecimal totalCost = pricePerKg;

        if (transportCost != null) {
            totalCost = totalCost.add(transportCost);
        }
        if (storageCost != null) {
            totalCost = totalCost.add(storageCost);
        }
        if (processingCost != null) {
            totalCost = totalCost.add(processingCost);
        }

        return totalCost;
    }

    public BigDecimal getAdjustedPrice() {
        if (seasonalFactor != null && seasonalFactor.compareTo(BigDecimal.ZERO) > 0) {
            return pricePerKg.multiply(seasonalFactor);
        }
        return pricePerKg;
    }

    public boolean isHighPrice() {
        return pricePerKg != null && pricePerKg.compareTo(new BigDecimal("1000.0")) > 0;
    }

    public boolean isLowPrice() {
        return pricePerKg != null && pricePerKg.compareTo(new BigDecimal("100.0")) < 0;
    }

    public boolean isHighDemand() {
        return demandLevel == DemandLevel.HIGH || demandLevel == DemandLevel.VERY_HIGH;
    }

    public boolean isLowSupply() {
        return supplyLevel == SupplyLevel.LOW;
    }

    public boolean isMarketOpportunity() {
        return isHighDemand() && isLowSupply() && priceTrend == PriceTrend.INCREASING;
    }

    public boolean isReliableData() {
        return reliabilityScore != null && reliabilityScore >= 4;
    }

    public boolean isRecentPrice() {
        if (priceDate == null) return false;
        return LocalDate.now().minusDays(7).isBefore(priceDate);
    }

    public boolean isOutdatedPrice() {
        if (priceDate == null) return true;
        return LocalDate.now().minusDays(30).isAfter(priceDate);
    }

    public long getDaysOld() {
        if (priceDate == null) return -1;
        return priceDate.until(LocalDate.now()).getDays();
    }

    public String getPriceDateFormatted() {
        return priceDate != null ?
                priceDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) :
                "Not set";
    }

    public String getPriceFormatted() {
        if (pricePerKg == null) return "Not specified";
        return currency + " " + pricePerKg + "/kg";
    }

    public String getMarketSummary() {
        StringBuilder summary = new StringBuilder();

        if (marketName != null) {
            summary.append(marketName);
        }

        if (location != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append(location);
        }

        if (marketType != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append(marketType.getDisplayName());
        }

        return summary.length() > 0 ? summary.toString() : "No data";
    }

    public String getPriceTrendSummary() {
        StringBuilder summary = new StringBuilder();

        if (pricePerKg != null) {
            summary.append(getPriceFormatted());
        }

        if (priceTrend != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append(priceTrend.getDisplayName());
        }

        if (demandLevel != null && supplyLevel != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Demand: ").append(demandLevel.getDisplayName())
                    .append(", Supply: ").append(supplyLevel.getDisplayName());
        }

        return summary.length() > 0 ? summary.toString() : "No data";
    }

    public String getDataQualitySummary() {
        StringBuilder summary = new StringBuilder();

        if (dataSource != null) {
            summary.append("Source: ").append(dataSource);
        }

        if (reliabilityScore != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Reliability: ").append(getReliabilityDescription());
        }

        if (priceDate != null) {
            if (summary.length() > 0) summary.append(" - ");
            long daysOld = getDaysOld();
            if (daysOld == 0) {
                summary.append("Today");
            } else if (daysOld == 1) {
                summary.append("1 day ago");
            } else {
                summary.append(daysOld).append(" days ago");
            }
        }

        return summary.length() > 0 ? summary.toString() : "No data quality info";
    }

    // toString, equals and hashCode
    @Override
    public String toString() {
        return "MarketPrice{" +
                "id='" + id + '\'' +
                ", cropId='" + cropId + '\'' +
                ", marketName='" + marketName + '\'' +
                ", marketType=" + marketType +
                ", location='" + location + '\'' +
                ", priceDate=" + priceDate +
                ", pricePerKg=" + pricePerKg +
                ", currency='" + currency + '\'' +
                ", demandLevel=" + demandLevel +
                ", supplyLevel=" + supplyLevel +
                ", priceTrend=" + priceTrend +
                ", dataSource='" + dataSource + '\'' +
                ", reliabilityScore=" + reliabilityScore +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketPrice that = (MarketPrice) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}