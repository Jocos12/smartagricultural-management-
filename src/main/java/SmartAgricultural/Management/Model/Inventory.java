package SmartAgricultural.Management.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Entity
@Table(name = "inventories")
public class Inventory {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "inventory_code", length = 30, unique = true, nullable = false)
    @NotBlank(message = "Inventory code is required")
    @Size(max = 30, message = "Inventory code must not exceed 30 characters")
    private String inventoryCode;

    @Column(name = "crop_id", length = 20, nullable = false)
    @NotBlank(message = "Crop ID is required")
    private String cropId;

    // Changed to use User references instead of farmer_id and buyer_id
    @Column(name = "farmer_user_id", length = 20)
    private String farmerUserId;

    @Column(name = "buyer_user_id", length = 20)
    private String buyerUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "facility_type", nullable = false)
    @NotNull(message = "Facility type is required")
    private FacilityType facilityType;

    @Column(name = "storage_location", length = 255, nullable = false)
    @NotBlank(message = "Storage location is required")
    @Size(max = 255, message = "Storage location must not exceed 255 characters")
    private String storageLocation;

    @Column(name = "facility_name", length = 150)
    @Size(max = 150, message = "Facility name must not exceed 150 characters")
    private String facilityName;

    @Column(name = "facility_owner", length = 100)
    @Size(max = 100, message = "Facility owner must not exceed 100 characters")
    private String facilityOwner;

    @Column(name = "storage_capacity", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Storage capacity must be positive")
    @Digits(integer = 8, fraction = 2, message = "Storage capacity format is invalid")
    private BigDecimal storageCapacity;

    @Column(name = "current_quantity", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "Current quantity is required")
    @DecimalMin(value = "0.0", message = "Current quantity must be positive")
    @Digits(integer = 8, fraction = 2, message = "Current quantity format is invalid")
    private BigDecimal currentQuantity;

    @Column(name = "reserved_quantity", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Reserved quantity must be positive")
    @Digits(integer = 8, fraction = 2, message = "Reserved quantity format is invalid")
    private BigDecimal reservedQuantity = BigDecimal.ZERO;

    @Column(name = "available_quantity", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "Available quantity is required")
    @DecimalMin(value = "0.0", message = "Available quantity must be positive")
    @Digits(integer = 8, fraction = 2, message = "Available quantity format is invalid")
    private BigDecimal availableQuantity;

    @Column(name = "unit", length = 20)
    @Size(max = 20, message = "Unit must not exceed 20 characters")
    private String unit = "KG";

    @Column(name = "quality_grade", length = 50, nullable = false)
    @NotBlank(message = "Quality grade is required")
    @Size(max = 50, message = "Quality grade must not exceed 50 characters")
    private String qualityGrade;

    @Column(name = "harvest_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate harvestDate;

    @Column(name = "storage_date", nullable = false)
    @NotNull(message = "Storage date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate storageDate;

    @Column(name = "expected_shelf_life_days")
    @Min(value = 1, message = "Expected shelf life must be at least 1 day")
    private Integer expectedShelfLifeDays;

    @Column(name = "expiry_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    @Column(name = "storage_conditions", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Storage conditions must not exceed 2000 characters")
    private String storageConditions;

    @Column(name = "packaging_type", length = 100)
    @Size(max = 100, message = "Packaging type must not exceed 100 characters")
    private String packagingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "packaging_condition")
    private PackagingCondition packagingCondition = PackagingCondition.GOOD;

    @Column(name = "treatment_applied", length = 255)
    @Size(max = 255, message = "Treatment applied must not exceed 255 characters")
    private String treatmentApplied;

    @Column(name = "certifications", length = 255)
    @Size(max = 255, message = "Certifications must not exceed 255 characters")
    private String certifications;

    @Column(name = "market_value_per_unit", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Market value per unit must be positive")
    @Digits(integer = 6, fraction = 2, message = "Market value per unit format is invalid")
    private BigDecimal marketValuePerUnit;

    @Column(name = "total_market_value", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "Total market value must be positive")
    @Digits(integer = 10, fraction = 2, message = "Total market value format is invalid")
    private BigDecimal totalMarketValue;

    @Column(name = "purchase_price_per_unit", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Purchase price per unit must be positive")
    @Digits(integer = 6, fraction = 2, message = "Purchase price per unit format is invalid")
    private BigDecimal purchasePricePerUnit;

    @Column(name = "storage_cost_per_unit", precision = 6, scale = 2)
    @DecimalMin(value = "0.0", message = "Storage cost per unit must be positive")
    @Digits(integer = 4, fraction = 2, message = "Storage cost per unit format is invalid")
    private BigDecimal storageCostPerUnit;

    @Column(name = "insurance_coverage")
    private Boolean insuranceCoverage = false;

    @Column(name = "insurance_value", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "Insurance value must be positive")
    @Digits(integer = 10, fraction = 2, message = "Insurance value format is invalid")
    private BigDecimal insuranceValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InventoryStatus status = InventoryStatus.AVAILABLE;

    @Column(name = "condition_assessment")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate conditionAssessment;

    @Column(name = "quality_tests", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Quality tests must not exceed 3000 characters")
    private String qualityTests;

    @Column(name = "moisture_content", precision = 4, scale = 2)
    @DecimalMin(value = "0.0", message = "Moisture content must be positive")
    @DecimalMax(value = "100.0", message = "Moisture content must not exceed 100%")
    @Digits(integer = 2, fraction = 2, message = "Moisture content format is invalid")
    private BigDecimal moistureContent;

    @Column(name = "pest_inspection_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pestInspectionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "pest_status")
    private PestStatus pestStatus = PestStatus.PEST_FREE;

    @Column(name = "contamination_level", length = 50)
    @Size(max = 50, message = "Contamination level must not exceed 50 characters")
    private String contaminationLevel;

    @Column(name = "handling_instructions", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Handling instructions must not exceed 2000 characters")
    private String handlingInstructions;

    @Column(name = "special_requirements", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Special requirements must not exceed 2000 characters")
    private String specialRequirements;

    @Column(name = "location_coordinates", length = 50)
    @Size(max = 50, message = "Location coordinates must not exceed 50 characters")
    private String locationCoordinates;

    @Column(name = "accessibility", length = 100)
    @Size(max = 100, message = "Accessibility must not exceed 100 characters")
    private String accessibility;

    @Column(name = "security_measures", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Security measures must not exceed 2000 characters")
    private String securityMeasures;

    @Column(name = "temperature_log", columnDefinition = "TEXT")
    @Size(max = 5000, message = "Temperature log must not exceed 5000 characters")
    private String temperatureLog;

    @Column(name = "humidity_log", columnDefinition = "TEXT")
    @Size(max = 5000, message = "Humidity log must not exceed 5000 characters")
    private String humidityLog;

    @Column(name = "movement_history", columnDefinition = "TEXT")
    @Size(max = 5000, message = "Movement history must not exceed 5000 characters")
    private String movementHistory;

    @Column(name = "quality_degradation_rate", precision = 5, scale = 3)
    @DecimalMin(value = "0.0", message = "Quality degradation rate must be positive")
    @Digits(integer = 2, fraction = 3, message = "Quality degradation rate format is invalid")
    private BigDecimal qualityDegradationRate;

    @Column(name = "optimal_sale_period", length = 100)
    @Size(max = 100, message = "Optimal sale period must not exceed 100 characters")
    private String optimalSalePeriod;

    @Column(name = "minimum_sale_price", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Minimum sale price must be positive")
    @Digits(integer = 6, fraction = 2, message = "Minimum sale price format is invalid")
    private BigDecimal minimumSalePrice;

    @Column(name = "maximum_sale_price", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Maximum sale price must be positive")
    @Digits(integer = 6, fraction = 2, message = "Maximum sale price format is invalid")
    private BigDecimal maximumSalePrice;

    @Column(name = "buyer_restrictions", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Buyer restrictions must not exceed 2000 characters")
    private String buyerRestrictions;

    @Column(name = "seasonal_demand_pattern", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Seasonal demand pattern must not exceed 2000 characters")
    private String seasonalDemandPattern;

    @Column(name = "transport_requirements", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Transport requirements must not exceed 2000 characters")
    private String transportRequirements;

    @Column(name = "loading_capacity", length = 50)
    @Size(max = 50, message = "Loading capacity must not exceed 50 characters")
    private String loadingCapacity;

    @Column(name = "batch_traceability_code", length = 50)
    @Size(max = 50, message = "Batch traceability code must not exceed 50 characters")
    private String batchTraceabilityCode;

    @Column(name = "source_farm_codes", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Source farm codes must not exceed 2000 characters")
    private String sourceFarmCodes;

    @Column(name = "processing_history", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Processing history must not exceed 3000 characters")
    private String processingHistory;

    @Column(name = "sustainability_certifications", length = 255)
    @Size(max = 255, message = "Sustainability certifications must not exceed 255 characters")
    private String sustainabilityCertifications;

    @Column(name = "carbon_footprint", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Carbon footprint must be positive")
    @Digits(integer = 6, fraction = 2, message = "Carbon footprint format is invalid")
    private BigDecimal carbonFootprint;

    @Column(name = "water_footprint", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Water footprint must be positive")
    @Digits(integer = 6, fraction = 2, message = "Water footprint format is invalid")
    private BigDecimal waterFootprint;

    @Column(name = "social_impact_score")
    @Min(value = 1, message = "Social impact score must be between 1 and 10")
    @Max(value = 10, message = "Social impact score must be between 1 and 10")
    private Integer socialImpactScore;

    @Column(name = "fair_trade_certified")
    private Boolean fairTradeCertified = false;

    @Column(name = "organic_certified")
    private Boolean organicCertified = false;

    @Column(name = "local_sourcing")
    private Boolean localSourcing = true;

    @Column(name = "inventory_turn_rate", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Inventory turn rate must be positive")
    @Digits(integer = 3, fraction = 2, message = "Inventory turn rate format is invalid")
    private BigDecimal inventoryTurnRate;

    @Column(name = "days_in_storage")
    @Min(value = 0, message = "Days in storage must be positive")
    private Integer daysInStorage;

    @Column(name = "average_storage_time")
    @Min(value = 0, message = "Average storage time must be positive")
    private Integer averageStorageTime;

    @Column(name = "loss_percentage", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Loss percentage must be positive")
    @DecimalMax(value = "100.0", message = "Loss percentage must not exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Loss percentage format is invalid")
    private BigDecimal lossPercentage = BigDecimal.ZERO;

    @Column(name = "loss_value", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Loss value must be positive")
    @Digits(integer = 8, fraction = 2, message = "Loss value format is invalid")
    private BigDecimal lossValue = BigDecimal.ZERO;

    @Column(name = "loss_reasons", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Loss reasons must not exceed 2000 characters")
    private String lossReasons;

    @Column(name = "profit_margin", precision = 5, scale = 2)
    @DecimalMin(value = "-100.0", message = "Profit margin must be greater than -100%")
    @DecimalMax(value = "1000.0", message = "Profit margin must not exceed 1000%")
    @Digits(integer = 3, fraction = 2, message = "Profit margin format is invalid")
    private BigDecimal profitMargin;

    @Column(name = "contribution_margin", precision = 8, scale = 2)
    @Digits(integer = 6, fraction = 2, message = "Contribution margin format is invalid")
    private BigDecimal contributionMargin;

    @Column(name = "last_movement_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMovementDate;

    @Column(name = "next_inspection_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextInspectionDate;

    @Column(name = "reorder_level", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Reorder level must be positive")
    @Digits(integer = 6, fraction = 2, message = "Reorder level format is invalid")
    private BigDecimal reorderLevel;

    @Column(name = "maximum_stock_level", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Maximum stock level must be positive")
    @Digits(integer = 8, fraction = 2, message = "Maximum stock level format is invalid")
    private BigDecimal maximumStockLevel;

    @Column(name = "minimum_stock_level", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Minimum stock level must be positive")
    @Digits(integer = 6, fraction = 2, message = "Minimum stock level format is invalid")
    private BigDecimal minimumStockLevel;

    @Column(name = "procurement_lead_time")
    @Min(value = 0, message = "Procurement lead time must be positive")
    private Integer procurementLeadTime;

    @Column(name = "supplier_reliability", precision = 3, scale = 2)
    @DecimalMin(value = "1.0", message = "Supplier reliability must be between 1 and 5")
    @DecimalMax(value = "5.0", message = "Supplier reliability must be between 1 and 5")
    @Digits(integer = 1, fraction = 2, message = "Supplier reliability format is invalid")
    private BigDecimal supplierReliability;

    @Column(name = "demand_forecast", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Demand forecast must not exceed 3000 characters")
    private String demandForecast;

    @Column(name = "price_forecast", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Price forecast must not exceed 3000 characters")
    private String priceForecast;

    @Column(name = "seasonality_pattern", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Seasonality pattern must not exceed 2000 characters")
    private String seasonalityPattern;

    @Enumerated(EnumType.STRING)
    @Column(name = "market_competition_level")
    private MarketCompetitionLevel marketCompetitionLevel = MarketCompetitionLevel.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(name = "strategic_importance")
    private StrategicImportance strategicImportance = StrategicImportance.MEDIUM;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "created_by", length = 20)
    private String createdBy;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "updated_by", length = 20)
    private String updatedBy;

    // FIXED Relationships - Now properly referencing User entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", insertable = false, updatable = false)
    @JsonIgnore
    private Crop crop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User farmerUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User buyerUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    @JsonIgnore
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", insertable = false, updatable = false)
    @JsonIgnore
    private User updater;

    // Enums (unchanged)
    public enum FacilityType {
        FARM_STORAGE("Farm Storage", "On-farm storage facility"),
        WAREHOUSE("Warehouse", "Commercial warehouse"),
        SILO("Silo", "Grain silo storage"),
        COLD_STORAGE("Cold Storage", "Temperature controlled storage"),
        PROCESSING_PLANT("Processing Plant", "Food processing facility"),
        RETAIL_STORE("Retail Store", "Retail outlet storage");

        private final String displayName;
        private final String description;

        FacilityType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public boolean isTemperatureControlled() {
            return this == COLD_STORAGE || this == PROCESSING_PLANT;
        }

        public boolean isCommercial() {
            return this == WAREHOUSE || this == PROCESSING_PLANT || this == RETAIL_STORE;
        }
    }

    public enum PackagingCondition {
        EXCELLENT("Excellent", 4), GOOD("Good", 3), FAIR("Fair", 2), POOR("Poor", 1);
        private final String displayName;
        private final int qualityScore;

        PackagingCondition(String displayName, int qualityScore) {
            this.displayName = displayName;
            this.qualityScore = qualityScore;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getQualityScore() {
            return qualityScore;
        }

        public boolean isAcceptable() {
            return this == EXCELLENT || this == GOOD || this == FAIR;
        }

        public boolean needsReplacement() {
            return this == POOR;
        }
    }

    public enum InventoryStatus {
        AVAILABLE("Available", "Ready for sale"), RESERVED("Reserved", "Reserved for specific buyer"),
        IN_TRANSIT("In Transit", "Being transported"), SOLD("Sold", "Already sold"),
        DAMAGED("Damaged", "Damaged goods"), EXPIRED("Expired", "Past expiry date"),
        DISPOSED("Disposed", "Properly disposed");
        private final String displayName;
        private final String description;

        InventoryStatus(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public boolean isSellable() {
            return this == AVAILABLE || this == RESERVED;
        }

        public boolean isActive() {
            return this != SOLD && this != DISPOSED;
        }

        public boolean requiresAction() {
            return this == DAMAGED || this == EXPIRED;
        }
    }

    public enum PestStatus {
        PEST_FREE("Pest Free", "No pest infestation detected"),
        MINOR_INFESTATION("Minor Infestation", "Minor pest presence"),
        MAJOR_INFESTATION("Major Infestation", "Significant pest infestation");
        private final String displayName;
        private final String description;

        PestStatus(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public boolean isSafe() {
            return this == PEST_FREE;
        }

        public boolean requiresTreatment() {
            return this == MINOR_INFESTATION || this == MAJOR_INFESTATION;
        }

        public boolean isHighRisk() {
            return this == MAJOR_INFESTATION;
        }
    }

    public enum MarketCompetitionLevel {
        LOW("Low", "Limited competition"), MEDIUM("Medium", "Moderate competition"), HIGH("High", "Intense competition");
        private final String displayName;
        private final String description;

        MarketCompetitionLevel(String displayName, String description) {
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

    public enum StrategicImportance {
        LOW("Low", "Low strategic value"), MEDIUM("Medium", "Moderate strategic value"),
        HIGH("High", "High strategic value"), CRITICAL("Critical", "Critical for operations");
        private final String displayName;
        private final String description;

        StrategicImportance(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public boolean isPriority() {
            return this == HIGH || this == CRITICAL;
        }
    }

    public enum InventoryAlert {
        EXPIRING_SOON("Expiring Soon", "Product will expire within 7 days"),
        LOW_STOCK("Low Stock", "Stock below minimum level"),
        HIGH_LOSS("High Loss", "Loss percentage above 5%"),
        PEST_DETECTED("Pest Detected", "Pest infestation detected"),
        QUALITY_DEGRADING("Quality Degrading", "Quality degradation detected"),
        OVERSTOCK("Overstock", "Stock above maximum level"),
        PRICE_DROP("Price Drop", "Market price has dropped significantly");
        private final String displayName;
        private final String description;

        InventoryAlert(String displayName, String description) {
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

    // Constructors
    public Inventory() {
        this.id = generateAlphanumericId();
        this.inventoryCode = generateInventoryCode();
        this.createdDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.storageDate = LocalDate.now();
    }

    public Inventory(String cropId, FacilityType facilityType, String storageLocation,
                     BigDecimal currentQuantity, String qualityGrade) {
        this();
        this.cropId = cropId;
        this.facilityType = facilityType;
        this.storageLocation = storageLocation;
        this.currentQuantity = currentQuantity;
        this.availableQuantity = currentQuantity;
        this.qualityGrade = qualityGrade;
    }

    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder("INV");
        String timestamp = String.valueOf(System.currentTimeMillis());
        sb.append(timestamp.substring(timestamp.length() - 6));
        for (int i = 0; i < 5; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String generateInventoryCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder("STOCK");
        LocalDateTime now = LocalDateTime.now();
        sb.append(String.format("%02d%02d%02d", now.getYear() % 100, now.getMonthValue(), now.getDayOfMonth()));
        String timestamp = String.valueOf(System.currentTimeMillis());
        sb.append(timestamp.substring(timestamp.length() - 4));
        for (int i = 0; i < 3; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null) this.id = generateAlphanumericId();
        if (this.inventoryCode == null) this.inventoryCode = generateInventoryCode();
        this.createdDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        if (this.storageDate == null) this.storageDate = LocalDate.now();
        calculateAvailableQuantity();
        if (daysInStorage == null && storageDate != null) calculateDaysInStorage();
        if (expiryDate == null && expectedShelfLifeDays != null && storageDate != null) calculateExpiryDate();
        if (totalMarketValue == null && marketValuePerUnit != null && currentQuantity != null)
            calculateTotalMarketValue();
        if (nextInspectionDate == null) this.nextInspectionDate = LocalDate.now().plusMonths(1);
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
        calculateAvailableQuantity();
        if (storageDate != null) calculateDaysInStorage();
        if (marketValuePerUnit != null && currentQuantity != null) calculateTotalMarketValue();
        updateStatusBasedOnConditions();
        if (profitMargin == null && marketValuePerUnit != null && purchasePricePerUnit != null) calculateProfitMargin();
    }

    // Utility methods (unchanged)
    private void calculateAvailableQuantity() {
        if (currentQuantity != null) {
            BigDecimal reserved = reservedQuantity != null ? reservedQuantity : BigDecimal.ZERO;
            this.availableQuantity = currentQuantity.subtract(reserved);
            if (this.availableQuantity.compareTo(BigDecimal.ZERO) < 0) {
                this.availableQuantity = BigDecimal.ZERO;
            }
        }
    }

    private void calculateDaysInStorage() {
        if (storageDate != null) {
            this.daysInStorage = (int) ChronoUnit.DAYS.between(storageDate, LocalDate.now());
        }
    }

    private void calculateExpiryDate() {
        if (storageDate != null && expectedShelfLifeDays != null) {
            this.expiryDate = storageDate.plusDays(expectedShelfLifeDays);
        }
    }

    private void calculateTotalMarketValue() {
        if (marketValuePerUnit != null && currentQuantity != null) {
            this.totalMarketValue = marketValuePerUnit.multiply(currentQuantity)
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }

    private void calculateProfitMargin() {
        if (marketValuePerUnit != null && purchasePricePerUnit != null &&
                purchasePricePerUnit.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal profit = marketValuePerUnit.subtract(purchasePricePerUnit);
            this.profitMargin = profit.divide(purchasePricePerUnit, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }

    private void updateStatusBasedOnConditions() {
        if (expiryDate != null && LocalDate.now().isAfter(expiryDate) && status == InventoryStatus.AVAILABLE) {
            this.status = InventoryStatus.EXPIRED;
        }
        if (pestStatus == PestStatus.MAJOR_INFESTATION && status == InventoryStatus.AVAILABLE) {
            this.status = InventoryStatus.DAMAGED;
        }
    }

    public BigDecimal calculateStorageCapacityUtilization() {
        if (storageCapacity == null || storageCapacity.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentQuantity.divide(storageCapacity, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public String getFormattedQuantity() {
        if (currentQuantity == null) return "N/A";
        return String.format("%.2f %s", currentQuantity, unit != null ? unit : "KG");
    }

    public String getFormattedMarketValue() {
        if (totalMarketValue == null) return "N/A";
        return String.format("RWF %.2f", totalMarketValue);
    }

    public boolean isExpiringSoon() {
        if (expiryDate == null) return false;
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate) <= 7;
    }

    public boolean isLowStock() {
        if (minimumStockLevel == null) return false;
        return availableQuantity.compareTo(minimumStockLevel) <= 0;
    }

    public boolean isOverstock() {
        if (maximumStockLevel == null) return false;
        return currentQuantity.compareTo(maximumStockLevel) >= 0;
    }

    public boolean hasHighLoss() {
        return lossPercentage != null && lossPercentage.compareTo(new BigDecimal("5")) >= 0;
    }

    public boolean requiresInspection() {
        return nextInspectionDate != null && LocalDate.now().isAfter(nextInspectionDate);
    }

    public boolean isHighValue() {
        return totalMarketValue != null && totalMarketValue.compareTo(new BigDecimal("100000")) >= 0;
    }

    public boolean isProfitable() {
        return profitMargin != null && profitMargin.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isSustainable() {
        return organicCertified || fairTradeCertified || localSourcing;
    }

    public String getStorageQualityScore() {
        int score = 0;
        int factors = 0;
        if (packagingCondition != null) {
            score += packagingCondition.getQualityScore();
            factors++;
        }
        if (pestStatus != null) {
            score += (pestStatus == PestStatus.PEST_FREE ? 4 :
                    pestStatus == PestStatus.MINOR_INFESTATION ? 2 : 1);
            factors++;
        }
        if (moistureContent != null) {
            if (moistureContent.compareTo(new BigDecimal("10")) >= 0 &&
                    moistureContent.compareTo(new BigDecimal("15")) <= 0) {
                score += 4;
            } else if (moistureContent.compareTo(new BigDecimal("20")) <= 0) {
                score += 2;
            } else {
                score += 1;
            }
            factors++;
        }
        if (factors == 0) return "N/A";
        double averageScore = (double) score / factors;
        if (averageScore >= 3.5) return "Excellent";
        else if (averageScore >= 2.5) return "Good";
        else if (averageScore >= 1.5) return "Fair";
        else return "Poor";
    }

    public String getInventorySummary() {
        return String.format("%s - %s %s (%s) - %s",
                inventoryCode, getFormattedQuantity(), qualityGrade,
                facilityType.getDisplayName(), status.getDisplayName());
    }

    public InventoryAlert[] getActiveAlerts() {
        java.util.List<InventoryAlert> alerts = new java.util.ArrayList<>();
        if (isExpiringSoon()) alerts.add(InventoryAlert.EXPIRING_SOON);
        if (isLowStock()) alerts.add(InventoryAlert.LOW_STOCK);
        if (hasHighLoss()) alerts.add(InventoryAlert.HIGH_LOSS);
        if (pestStatus != null && pestStatus.requiresTreatment()) alerts.add(InventoryAlert.PEST_DETECTED);
        if (isOverstock()) alerts.add(InventoryAlert.OVERSTOCK);
        if (qualityDegradationRate != null && daysInStorage != null) {
            BigDecimal degradation = qualityDegradationRate.multiply(new BigDecimal(daysInStorage));
            if (degradation.compareTo(new BigDecimal("10")) >= 0) {
                alerts.add(InventoryAlert.QUALITY_DEGRADING);
            }
        }
        return alerts.toArray(new InventoryAlert[0]);
    }

    public BigDecimal calculateCurrentValue() {
        if (currentQuantity == null || marketValuePerUnit == null) return BigDecimal.ZERO;
        BigDecimal baseValue = currentQuantity.multiply(marketValuePerUnit);
        if (lossPercentage != null && lossPercentage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal lossReduction = BigDecimal.ONE.subtract(lossPercentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            baseValue = baseValue.multiply(lossReduction);
        }
        return baseValue.setScale(2, RoundingMode.HALF_UP);
    }

    public String getRemainingShelfLife() {
        if (expiryDate == null) return "Unknown";
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
        if (daysRemaining <= 0) return "Expired";
        if (daysRemaining == 1) return "1 day";
        return daysRemaining + " days";
    }

    public boolean isOptimalSalePeriod() {
        if (optimalSalePeriod == null || optimalSalePeriod.isEmpty()) return true;
        String currentMonth = LocalDate.now().getMonth().toString();
        return optimalSalePeriod.toLowerCase().contains(currentMonth.toLowerCase());
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    // NEW GETTERS/SETTERS for User references
    public String getFarmerUserId() {
        return farmerUserId;
    }

    public void setFarmerUserId(String farmerUserId) {
        this.farmerUserId = farmerUserId;
    }

    public String getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public FacilityType getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(FacilityType facilityType) {
        this.facilityType = facilityType;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityOwner() {
        return facilityOwner;
    }

    public void setFacilityOwner(String facilityOwner) {
        this.facilityOwner = facilityOwner;
    }

    public BigDecimal getStorageCapacity() {
        return storageCapacity;
    }

    public void setStorageCapacity(BigDecimal storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    public BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(BigDecimal currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public BigDecimal getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(BigDecimal reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(BigDecimal availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getQualityGrade() {
        return qualityGrade;
    }

    public void setQualityGrade(String qualityGrade) {
        this.qualityGrade = qualityGrade;
    }

    public LocalDate getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(LocalDate harvestDate) {
        this.harvestDate = harvestDate;
    }

    public LocalDate getStorageDate() {
        return storageDate;
    }

    public void setStorageDate(LocalDate storageDate) {
        this.storageDate = storageDate;
    }

    public Integer getExpectedShelfLifeDays() {
        return expectedShelfLifeDays;
    }

    public void setExpectedShelfLifeDays(Integer expectedShelfLifeDays) {
        this.expectedShelfLifeDays = expectedShelfLifeDays;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStorageConditions() {
        return storageConditions;
    }

    public void setStorageConditions(String storageConditions) {
        this.storageConditions = storageConditions;
    }

    public String getPackagingType() {
        return packagingType;
    }

    public void setPackagingType(String packagingType) {
        this.packagingType = packagingType;
    }

    public PackagingCondition getPackagingCondition() {
        return packagingCondition;
    }

    public void setPackagingCondition(PackagingCondition packagingCondition) {
        this.packagingCondition = packagingCondition;
    }

    public String getTreatmentApplied() {
        return treatmentApplied;
    }

    public void setTreatmentApplied(String treatmentApplied) {
        this.treatmentApplied = treatmentApplied;
    }

    public String getCertifications() {
        return certifications;
    }

    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    public BigDecimal getMarketValuePerUnit() {
        return marketValuePerUnit;
    }

    public void setMarketValuePerUnit(BigDecimal marketValuePerUnit) {
        this.marketValuePerUnit = marketValuePerUnit;
    }

    public BigDecimal getTotalMarketValue() {
        return totalMarketValue;
    }

    public void setTotalMarketValue(BigDecimal totalMarketValue) {
        this.totalMarketValue = totalMarketValue;
    }

    public BigDecimal getPurchasePricePerUnit() {
        return purchasePricePerUnit;
    }

    public void setPurchasePricePerUnit(BigDecimal purchasePricePerUnit) {
        this.purchasePricePerUnit = purchasePricePerUnit;
    }

    public BigDecimal getStorageCostPerUnit() {
        return storageCostPerUnit;
    }

    public void setStorageCostPerUnit(BigDecimal storageCostPerUnit) {
        this.storageCostPerUnit = storageCostPerUnit;
    }

    public Boolean getInsuranceCoverage() {
        return insuranceCoverage;
    }

    public void setInsuranceCoverage(Boolean insuranceCoverage) {
        this.insuranceCoverage = insuranceCoverage;
    }

    public BigDecimal getInsuranceValue() {
        return insuranceValue;
    }

    public void setInsuranceValue(BigDecimal insuranceValue) {
        this.insuranceValue = insuranceValue;
    }

    public InventoryStatus getStatus() {
        return status;
    }

    public void setStatus(InventoryStatus status) {
        this.status = status;
    }

    public LocalDate getConditionAssessment() {
        return conditionAssessment;
    }

    public void setConditionAssessment(LocalDate conditionAssessment) {
        this.conditionAssessment = conditionAssessment;
    }

    public String getQualityTests() {
        return qualityTests;
    }

    public void setQualityTests(String qualityTests) {
        this.qualityTests = qualityTests;
    }

    public BigDecimal getMoistureContent() {
        return moistureContent;
    }

    public void setMoistureContent(BigDecimal moistureContent) {
        this.moistureContent = moistureContent;
    }

    public LocalDate getPestInspectionDate() {
        return pestInspectionDate;
    }

    public void setPestInspectionDate(LocalDate pestInspectionDate) {
        this.pestInspectionDate = pestInspectionDate;
    }

    public PestStatus getPestStatus() {
        return pestStatus;
    }

    public void setPestStatus(PestStatus pestStatus) {
        this.pestStatus = pestStatus;
    }

    public String getContaminationLevel() {
        return contaminationLevel;
    }

    public void setContaminationLevel(String contaminationLevel) {
        this.contaminationLevel = contaminationLevel;
    }

    public String getHandlingInstructions() {
        return handlingInstructions;
    }

    public void setHandlingInstructions(String handlingInstructions) {
        this.handlingInstructions = handlingInstructions;
    }

    public String getSpecialRequirements() {
        return specialRequirements;
    }

    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
    }

    public String getLocationCoordinates() {
        return locationCoordinates;
    }

    public void setLocationCoordinates(String locationCoordinates) {
        this.locationCoordinates = locationCoordinates;
    }

    public String getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }

    public String getSecurityMeasures() {
        return securityMeasures;
    }

    public void setSecurityMeasures(String securityMeasures) {
        this.securityMeasures = securityMeasures;
    }

    public String getTemperatureLog() {
        return temperatureLog;
    }

    public void setTemperatureLog(String temperatureLog) {
        this.temperatureLog = temperatureLog;
    }

    public String getHumidityLog() {
        return humidityLog;
    }

    public void setHumidityLog(String humidityLog) {
        this.humidityLog = humidityLog;
    }

    public String getMovementHistory() {
        return movementHistory;
    }

    public void setMovementHistory(String movementHistory) {
        this.movementHistory = movementHistory;
    }

    public BigDecimal getQualityDegradationRate() {
        return qualityDegradationRate;
    }

    public void setQualityDegradationRate(BigDecimal qualityDegradationRate) {
        this.qualityDegradationRate = qualityDegradationRate;
    }

    public String getOptimalSalePeriod() {
        return optimalSalePeriod;
    }

    public void setOptimalSalePeriod(String optimalSalePeriod) {
        this.optimalSalePeriod = optimalSalePeriod;
    }

    public BigDecimal getMinimumSalePrice() {
        return minimumSalePrice;
    }

    public void setMinimumSalePrice(BigDecimal minimumSalePrice) {
        this.minimumSalePrice = minimumSalePrice;
    }

    public BigDecimal getMaximumSalePrice() {
        return maximumSalePrice;
    }

    public void setMaximumSalePrice(BigDecimal maximumSalePrice) {
        this.maximumSalePrice = maximumSalePrice;
    }

    public String getBuyerRestrictions() {
        return buyerRestrictions;
    }

    public void setBuyerRestrictions(String buyerRestrictions) {
        this.buyerRestrictions = buyerRestrictions;
    }

    public String getSeasonalDemandPattern() {
        return seasonalDemandPattern;
    }

    public void setSeasonalDemandPattern(String seasonalDemandPattern) {
        this.seasonalDemandPattern = seasonalDemandPattern;
    }

    public String getTransportRequirements() {
        return transportRequirements;
    }

    public void setTransportRequirements(String transportRequirements) {
        this.transportRequirements = transportRequirements;
    }

    public String getLoadingCapacity() {
        return loadingCapacity;
    }

    public void setLoadingCapacity(String loadingCapacity) {
        this.loadingCapacity = loadingCapacity;
    }

    public String getBatchTraceabilityCode() {
        return batchTraceabilityCode;
    }

    public void setBatchTraceabilityCode(String batchTraceabilityCode) {
        this.batchTraceabilityCode = batchTraceabilityCode;
    }

    public String getSourceFarmCodes() {
        return sourceFarmCodes;
    }

    public void setSourceFarmCodes(String sourceFarmCodes) {
        this.sourceFarmCodes = sourceFarmCodes;
    }

    public String getProcessingHistory() {
        return processingHistory;
    }

    public void setProcessingHistory(String processingHistory) {
        this.processingHistory = processingHistory;
    }

    public String getSustainabilityCertifications() {
        return sustainabilityCertifications;
    }

    public void setSustainabilityCertifications(String sustainabilityCertifications) {
        this.sustainabilityCertifications = sustainabilityCertifications;
    }

    public BigDecimal getCarbonFootprint() {
        return carbonFootprint;
    }

    public void setCarbonFootprint(BigDecimal carbonFootprint) {
        this.carbonFootprint = carbonFootprint;
    }

    public BigDecimal getWaterFootprint() {
        return waterFootprint;
    }

    public void setWaterFootprint(BigDecimal waterFootprint) {
        this.waterFootprint = waterFootprint;
    }

    public Integer getSocialImpactScore() {
        return socialImpactScore;
    }

    public void setSocialImpactScore(Integer socialImpactScore) {
        this.socialImpactScore = socialImpactScore;
    }

    public Boolean getFairTradeCertified() {
        return fairTradeCertified;
    }

    public void setFairTradeCertified(Boolean fairTradeCertified) {
        this.fairTradeCertified = fairTradeCertified;
    }

    public Boolean getOrganicCertified() {
        return organicCertified;
    }

    public void setOrganicCertified(Boolean organicCertified) {
        this.organicCertified = organicCertified;
    }

    public Boolean getLocalSourcing() {
        return localSourcing;
    }

    public void setLocalSourcing(Boolean localSourcing) {
        this.localSourcing = localSourcing;
    }

    public BigDecimal getInventoryTurnRate() {
        return inventoryTurnRate;
    }

    public void setInventoryTurnRate(BigDecimal inventoryTurnRate) {
        this.inventoryTurnRate = inventoryTurnRate;
    }

    public Integer getDaysInStorage() {
        return daysInStorage;
    }

    public void setDaysInStorage(Integer daysInStorage) {
        this.daysInStorage = daysInStorage;
    }

    public Integer getAverageStorageTime() {
        return averageStorageTime;
    }

    public void setAverageStorageTime(Integer averageStorageTime) {
        this.averageStorageTime = averageStorageTime;
    }

    public BigDecimal getLossPercentage() {
        return lossPercentage;
    }

    public void setLossPercentage(BigDecimal lossPercentage) {
        this.lossPercentage = lossPercentage;
    }

    public BigDecimal getLossValue() {
        return lossValue;
    }

    public void setLossValue(BigDecimal lossValue) {
        this.lossValue = lossValue;
    }

    public String getLossReasons() {
        return lossReasons;
    }

    public void setLossReasons(String lossReasons) {
        this.lossReasons = lossReasons;
    }

    public BigDecimal getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(BigDecimal profitMargin) {
        this.profitMargin = profitMargin;
    }

    public BigDecimal getContributionMargin() {
        return contributionMargin;
    }

    public void setContributionMargin(BigDecimal contributionMargin) {
        this.contributionMargin = contributionMargin;
    }

    public LocalDateTime getLastMovementDate() {
        return lastMovementDate;
    }

    public void setLastMovementDate(LocalDateTime lastMovementDate) {
        this.lastMovementDate = lastMovementDate;
    }

    public LocalDate getNextInspectionDate() {
        return nextInspectionDate;
    }

    public void setNextInspectionDate(LocalDate nextInspectionDate) {
        this.nextInspectionDate = nextInspectionDate;
    }

    public BigDecimal getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(BigDecimal reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public BigDecimal getMaximumStockLevel() {
        return maximumStockLevel;
    }

    public void setMaximumStockLevel(BigDecimal maximumStockLevel) {
        this.maximumStockLevel = maximumStockLevel;
    }

    public BigDecimal getMinimumStockLevel() {
        return minimumStockLevel;
    }

    public void setMinimumStockLevel(BigDecimal minimumStockLevel) {
        this.minimumStockLevel = minimumStockLevel;
    }

    public Integer getProcurementLeadTime() {
        return procurementLeadTime;
    }

    public void setProcurementLeadTime(Integer procurementLeadTime) {
        this.procurementLeadTime = procurementLeadTime;
    }

    public BigDecimal getSupplierReliability() {
        return supplierReliability;
    }

    public void setSupplierReliability(BigDecimal supplierReliability) {
        this.supplierReliability = supplierReliability;
    }

    public String getDemandForecast() {
        return demandForecast;
    }

    public void setDemandForecast(String demandForecast) {
        this.demandForecast = demandForecast;
    }

    public String getPriceForecast() {
        return priceForecast;
    }

    public void setPriceForecast(String priceForecast) {
        this.priceForecast = priceForecast;
    }

    public String getSeasonalityPattern() {
        return seasonalityPattern;
    }

    public void setSeasonalityPattern(String seasonalityPattern) {
        this.seasonalityPattern = seasonalityPattern;
    }

    public MarketCompetitionLevel getMarketCompetitionLevel() {
        return marketCompetitionLevel;
    }

    public void setMarketCompetitionLevel(MarketCompetitionLevel marketCompetitionLevel) {
        this.marketCompetitionLevel = marketCompetitionLevel;
    }

    public StrategicImportance getStrategicImportance() {
        return strategicImportance;
    }

    public void setStrategicImportance(StrategicImportance strategicImportance) {
        this.strategicImportance = strategicImportance;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    // Relationship getters/setters
    public Crop getCrop() {
        return crop;
    }

    public void setCrop(Crop crop) {
        this.crop = crop;
    }

    public User getFarmerUser() {
        return farmerUser;
    }

    public void setFarmerUser(User farmerUser) {
        this.farmerUser = farmerUser;
    }

    public User getBuyerUser() {
        return buyerUser;
    }

    public void setBuyerUser(User buyerUser) {
        this.buyerUser = buyerUser;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public User getUpdater() {
        return updater;
    }

    public void setUpdater(User updater) {
        this.updater = updater;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id='" + id + '\'' +
                ", inventoryCode='" + inventoryCode + '\'' +
                ", cropId='" + cropId + '\'' +
                ", facilityType=" + facilityType +
                ", storageLocation='" + storageLocation + '\'' +
                ", currentQuantity=" + currentQuantity +
                ", availableQuantity=" + availableQuantity +
                ", qualityGrade='" + qualityGrade + '\'' +
                ", status=" + status +
                ", expiryDate=" + expiryDate +
                ", totalMarketValue=" + totalMarketValue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return id != null && id.equals(inventory.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}