package SmartAgricultural.Management.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Entity
@Table(name = "supply_chains")
public class SupplyChain {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "crop_production_id", length = 20, nullable = false)
    @NotBlank(message = "Crop production ID is required")
    private String cropProductionId;

    @Column(name = "transaction_id", length = 20)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false)
    @NotNull(message = "Stage is required")
    private Stage stage;

    @Column(name = "stage_order", nullable = false)
    @NotNull(message = "Stage order is required")
    @Min(value = 1, message = "Stage order must be at least 1")
    private Integer stageOrder;

    @Column(name = "stage_start_date", nullable = false)
    @NotNull(message = "Stage start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime stageStartDate;

    @Column(name = "stage_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime stageEndDate;

    @Column(name = "location", length = 255, nullable = false)
    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    @Column(name = "facility_name", length = 100)
    @Size(max = 100, message = "Facility name must not exceed 100 characters")
    private String facilityName;

    @Column(name = "quantity_in", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "Quantity in is required")
    @DecimalMin(value = "0.0", message = "Quantity in must be positive")
    @Digits(integer = 8, fraction = 2, message = "Quantity in format is invalid")
    private BigDecimal quantityIn;

    @Column(name = "quantity_out", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Quantity out must be positive")
    @Digits(integer = 8, fraction = 2, message = "Quantity out format is invalid")
    private BigDecimal quantityOut;

    @Column(name = "unit", length = 20)
    @Size(max = 20, message = "Unit must not exceed 20 characters")
    private String unit = "KG";

    @Column(name = "loss_quantity", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Loss quantity must be positive")
    @Digits(integer = 6, fraction = 2, message = "Loss quantity format is invalid")
    private BigDecimal lossQuantity = BigDecimal.ZERO;

    @Column(name = "loss_percentage", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Loss percentage must be positive")
    @DecimalMax(value = "100.0", message = "Loss percentage cannot exceed 100")
    @Digits(integer = 3, fraction = 2, message = "Loss percentage format is invalid")
    private BigDecimal lossPercentage = BigDecimal.ZERO;

    @Column(name = "loss_reason", length = 100)
    @Size(max = 100, message = "Loss reason must not exceed 100 characters")
    private String lossReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "quality_status")
    private QualityStatus qualityStatus = QualityStatus.GOOD;

    @Column(name = "quality_tests", columnDefinition = "TEXT")
    private String qualityTests;

    @Column(name = "storage_conditions", length = 100)
    @Size(max = 100, message = "Storage conditions must not exceed 100 characters")
    private String storageConditions;

    @Column(name = "transport_method", length = 100)
    @Size(max = 100, message = "Transport method must not exceed 100 characters")
    private String transportMethod;

    @Column(name = "responsible_party", length = 100, nullable = false)
    @NotBlank(message = "Responsible party is required")
    @Size(max = 100, message = "Responsible party must not exceed 100 characters")
    private String responsibleParty;

    @Column(name = "cost_incurred", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Cost incurred must be positive")
    @Digits(integer = 8, fraction = 2, message = "Cost incurred format is invalid")
    private BigDecimal costIncurred;

    @Column(name = "temperature_log", columnDefinition = "TEXT")
    private String temperatureLog;

    @Column(name = "humidity_log", columnDefinition = "TEXT")
    private String humidityLog;

    @Column(name = "handling_notes", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Handling notes must not exceed 3000 characters")
    private String handlingNotes;

    @Column(name = "compliance_certificates", length = 255)
    @Size(max = 255, message = "Compliance certificates must not exceed 255 characters")
    private String complianceCertificates;

    @Column(name = "insurance_coverage")
    private Boolean insuranceCoverage = false;

    @Column(name = "tracking_code", length = 50)
    @Size(max = 50, message = "Tracking code must not exceed 50 characters")
    private String trackingCode;

    @Column(name = "next_stage_location", length = 255)
    @Size(max = 255, message = "Next stage location must not exceed 255 characters")
    private String nextStageLocation;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_production_id", insertable = false, updatable = false)
    @JsonIgnore
    private CropProduction cropProduction;

    // Enums
    public enum Stage {
        HARVEST("Harvest"),
        COLLECTION("Collection"),
        STORAGE("Storage"),
        PROCESSING("Processing"),
        PACKAGING("Packaging"),
        TRANSPORT("Transport"),
        DISTRIBUTION("Distribution"),
        RETAIL("Retail");

        private final String displayName;

        Stage(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getOrder() {
            return this.ordinal() + 1;
        }

        public Stage getNextStage() {
            Stage[] stages = Stage.values();
            int currentIndex = this.ordinal();
            if (currentIndex < stages.length - 1) {
                return stages[currentIndex + 1];
            }
            return null;
        }

        public Stage getPreviousStage() {
            Stage[] stages = Stage.values();
            int currentIndex = this.ordinal();
            if (currentIndex > 0) {
                return stages[currentIndex - 1];
            }
            return null;
        }
    }

    public enum QualityStatus {
        EXCELLENT("Excellent", 5),
        GOOD("Good", 4),
        FAIR("Fair", 3),
        POOR("Poor", 2),
        REJECTED("Rejected", 1);

        private final String displayName;
        private final int numericValue;

        QualityStatus(String displayName, int numericValue) {
            this.displayName = displayName;
            this.numericValue = numericValue;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getNumericValue() {
            return numericValue;
        }

        public boolean isAcceptable() {
            return this.numericValue >= 3;
        }
    }

    public enum StageCategory {
        PRE_HARVEST("Pre-Harvest", List.of()),
        POST_HARVEST("Post-Harvest", List.of(Stage.HARVEST, Stage.COLLECTION)),
        PROCESSING_PHASE("Processing Phase", List.of(Stage.STORAGE, Stage.PROCESSING, Stage.PACKAGING)),
        DISTRIBUTION_PHASE("Distribution Phase", List.of(Stage.TRANSPORT, Stage.DISTRIBUTION, Stage.RETAIL));

        private final String displayName;
        private final List<Stage> stages;

        StageCategory(String displayName, List<Stage> stages) {
            this.displayName = displayName;
            this.stages = stages;
        }

        public String getDisplayName() {
            return displayName;
        }

        public List<Stage> getStages() {
            return stages;
        }

        public static StageCategory getCategoryForStage(Stage stage) {
            for (StageCategory category : StageCategory.values()) {
                if (category.stages.contains(stage)) {
                    return category;
                }
            }
            return POST_HARVEST;
        }
    }

    // Constructors
    public SupplyChain() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.stageStartDate = LocalDateTime.now();
    }

    public SupplyChain(String cropProductionId, Stage stage, Integer stageOrder,
                       String location, BigDecimal quantityIn, String responsibleParty) {
        this();
        this.cropProductionId = cropProductionId;
        this.stage = stage;
        this.stageOrder = stageOrder;
        this.location = location;
        this.quantityIn = quantityIn;
        this.responsibleParty = responsibleParty;
    }

    // ID and tracking code generation
    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        sb.append("SC");

        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 6);
        sb.append(shortTimestamp);

        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    private String generateTrackingCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        sb.append("TRK");

        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 6);
        sb.append(shortTimestamp);

        for (int i = 0; i < 5; i++) {
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
        if (this.trackingCode == null) {
            this.trackingCode = generateTrackingCode();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        calculateLossMetrics();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculateLossMetrics();
    }

    private void calculateLossMetrics() {
        // Only validate when both quantityIn and quantityOut are set
        if (quantityIn != null && quantityOut != null &&
                quantityOut.compareTo(BigDecimal.ZERO) > 0) {

            // Validate: quantityOut cannot exceed quantityIn
            if (quantityOut.compareTo(quantityIn) > 0) {
                throw new IllegalArgumentException(
                        String.format("Quantity Out (%.2f) cannot exceed Quantity In (%.2f)",
                                quantityOut, quantityIn)
                );
            }
        }

        // Only validate loss when both quantityIn and lossQuantity are set
        if (quantityIn != null && lossQuantity != null &&
                lossQuantity.compareTo(BigDecimal.ZERO) > 0) {

            // Validate: lossQuantity cannot exceed quantityIn
            if (lossQuantity.compareTo(quantityIn) > 0) {
                throw new IllegalArgumentException(
                        String.format("Loss Quantity (%.2f) cannot exceed Quantity In (%.2f)",
                                lossQuantity, quantityIn)
                );
            }
        }

        // Only validate combined total when all three values are set
        if (quantityIn != null && quantityOut != null && lossQuantity != null &&
                (quantityOut.compareTo(BigDecimal.ZERO) > 0 || lossQuantity.compareTo(BigDecimal.ZERO) > 0)) {

            BigDecimal total = quantityOut.add(lossQuantity);
            if (total.compareTo(quantityIn) > 0) {
                throw new IllegalArgumentException(
                        String.format("Quantity Out (%.2f) + Loss Quantity (%.2f) = %.2f exceeds Quantity In (%.2f)",
                                quantityOut, lossQuantity, total, quantityIn)
                );
            }
        }

        // Calculate loss metrics only when appropriate values exist
        if (quantityIn != null && quantityIn.compareTo(BigDecimal.ZERO) > 0) {

            // Calculate from quantityOut if available
            if (quantityOut != null && quantityOut.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal actualLoss = quantityIn.subtract(quantityOut);

                if (actualLoss.compareTo(BigDecimal.ZERO) > 0) {
                    // Only auto-calculate loss if not manually set
                    if (this.lossQuantity == null || this.lossQuantity.compareTo(BigDecimal.ZERO) == 0) {
                        this.lossQuantity = actualLoss.setScale(2, RoundingMode.HALF_UP);
                    }

                    // Calculate loss percentage with proper rounding to 2 decimal places
                    this.lossPercentage = actualLoss
                            .divide(quantityIn, 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"))
                            .setScale(2, RoundingMode.HALF_UP);
                }
            }
            // Calculate from lossQuantity if quantityOut is not set
            else if (lossQuantity != null && lossQuantity.compareTo(BigDecimal.ZERO) > 0) {
                // Calculate loss percentage with proper rounding to 2 decimal places
                this.lossPercentage = lossQuantity
                        .divide(quantityIn, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .setScale(2, RoundingMode.HALF_UP);
            }
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Integer getStageOrder() {
        return stageOrder;
    }

    public void setStageOrder(Integer stageOrder) {
        this.stageOrder = stageOrder;
    }

    public LocalDateTime getStageStartDate() {
        return stageStartDate;
    }

    public void setStageStartDate(LocalDateTime stageStartDate) {
        this.stageStartDate = stageStartDate;
    }

    public LocalDateTime getStageEndDate() {
        return stageEndDate;
    }

    public void setStageEndDate(LocalDateTime stageEndDate) {
        this.stageEndDate = stageEndDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public BigDecimal getQuantityIn() {
        return quantityIn;
    }

    public void setQuantityIn(BigDecimal quantityIn) {
        this.quantityIn = quantityIn;
    }

    public BigDecimal getQuantityOut() {
        return quantityOut;
    }

    public void setQuantityOut(BigDecimal quantityOut) {
        this.quantityOut = quantityOut;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getLossQuantity() {
        return lossQuantity;
    }

    public void setLossQuantity(BigDecimal lossQuantity) {
        this.lossQuantity = lossQuantity;
    }

    public BigDecimal getLossPercentage() {
        return lossPercentage;
    }

    public void setLossPercentage(BigDecimal lossPercentage) {
        this.lossPercentage = lossPercentage;
    }

    public String getLossReason() {
        return lossReason;
    }

    public void setLossReason(String lossReason) {
        this.lossReason = lossReason;
    }

    public QualityStatus getQualityStatus() {
        return qualityStatus;
    }

    public void setQualityStatus(QualityStatus qualityStatus) {
        this.qualityStatus = qualityStatus;
    }

    public String getQualityTests() {
        return qualityTests;
    }

    public void setQualityTests(String qualityTests) {
        this.qualityTests = qualityTests;
    }

    public String getStorageConditions() {
        return storageConditions;
    }

    public void setStorageConditions(String storageConditions) {
        this.storageConditions = storageConditions;
    }

    public String getTransportMethod() {
        return transportMethod;
    }

    public void setTransportMethod(String transportMethod) {
        this.transportMethod = transportMethod;
    }

    public String getResponsibleParty() {
        return responsibleParty;
    }

    public void setResponsibleParty(String responsibleParty) {
        this.responsibleParty = responsibleParty;
    }

    public BigDecimal getCostIncurred() {
        return costIncurred;
    }

    public void setCostIncurred(BigDecimal costIncurred) {
        this.costIncurred = costIncurred;
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

    public String getHandlingNotes() {
        return handlingNotes;
    }

    public void setHandlingNotes(String handlingNotes) {
        this.handlingNotes = handlingNotes;
    }

    public String getComplianceCertificates() {
        return complianceCertificates;
    }

    public void setComplianceCertificates(String complianceCertificates) {
        this.complianceCertificates = complianceCertificates;
    }

    public Boolean getInsuranceCoverage() {
        return insuranceCoverage;
    }

    public void setInsuranceCoverage(Boolean insuranceCoverage) {
        this.insuranceCoverage = insuranceCoverage;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public String getNextStageLocation() {
        return nextStageLocation;
    }

    public void setNextStageLocation(String nextStageLocation) {
        this.nextStageLocation = nextStageLocation;
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
    public String getStageDescription() {
        return stage != null ? stage.getDisplayName() : "Unknown";
    }

    public String getQualityStatusDescription() {
        return qualityStatus != null ? qualityStatus.getDisplayName() : "Unknown";
    }

    public StageCategory getStageCategory() {
        return StageCategory.getCategoryForStage(stage);
    }

    public List<Map<String, Object>> getQualityTestsList() {
        if (qualityTests == null || qualityTests.trim().isEmpty()) {
            return List.of();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(qualityTests, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    public void setQualityTestsList(List<Map<String, Object>> tests) {
        if (tests == null || tests.isEmpty()) {
            this.qualityTests = null;
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.qualityTests = mapper.writeValueAsString(tests);
        } catch (Exception e) {
            this.qualityTests = null;
        }
    }

    public List<Map<String, Object>> getTemperatureLogList() {
        if (temperatureLog == null || temperatureLog.trim().isEmpty()) {
            return List.of();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(temperatureLog, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    public void setTemperatureLogList(List<Map<String, Object>> tempLog) {
        if (tempLog == null || tempLog.isEmpty()) {
            this.temperatureLog = null;
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.temperatureLog = mapper.writeValueAsString(tempLog);
        } catch (Exception e) {
            this.temperatureLog = null;
        }
    }

    public List<Map<String, Object>> getHumidityLogList() {
        if (humidityLog == null || humidityLog.trim().isEmpty()) {
            return List.of();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(humidityLog, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    public void setHumidityLogList(List<Map<String, Object>> humLog) {
        if (humLog == null || humLog.isEmpty()) {
            this.humidityLog = null;
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.humidityLog = mapper.writeValueAsString(humLog);
        } catch (Exception e) {
            this.humidityLog = null;
        }
    }

    public boolean isStageCompleted() {
        return stageEndDate != null;
    }

    public boolean isStageInProgress() {
        return stageStartDate != null && stageEndDate == null;
    }

    public boolean hasQualityIssues() {
        return qualityStatus != null && !qualityStatus.isAcceptable();
    }

    public boolean hasLosses() {
        return lossQuantity != null && lossQuantity.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasHighLosses() {
        return lossPercentage != null && lossPercentage.compareTo(new BigDecimal("5.0")) > 0;
    }

    public boolean hasInsurance() {
        return insuranceCoverage != null && insuranceCoverage;
    }

    public boolean hasCertificates() {
        return complianceCertificates != null && !complianceCertificates.trim().isEmpty();
    }

    public boolean isFirstStage() {
        return stage == Stage.HARVEST;
    }

    public boolean isLastStage() {
        return stage == Stage.RETAIL;
    }

    public boolean isProcessingStage() {
        return stage == Stage.PROCESSING || stage == Stage.PACKAGING;
    }

    public boolean isLogisticsStage() {
        return stage == Stage.TRANSPORT || stage == Stage.DISTRIBUTION;
    }

    public long getStageDurationHours() {
        if (stageStartDate == null || stageEndDate == null) return 0;
        return java.time.Duration.between(stageStartDate, stageEndDate).toHours();
    }

    public long getStageDurationDays() {
        return getStageDurationHours() / 24;
    }

    public BigDecimal getEfficiencyRate() {
        if (quantityIn == null || quantityOut == null || quantityIn.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return quantityOut.divide(quantityIn, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }

    public BigDecimal getCostPerUnit() {
        if (costIncurred == null || quantityIn == null || quantityIn.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return costIncurred.divide(quantityIn, 4, RoundingMode.HALF_UP);
    }

    public String getStageStartDateFormatted() {
        return stageStartDate != null ?
                stageStartDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) :
                "Not set";
    }

    public String getStageEndDateFormatted() {
        return stageEndDate != null ?
                stageEndDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) :
                "Not completed";
    }

    public String getQuantityFormatted() {
        if (quantityIn == null) return "Not specified";
        String result = quantityIn + " " + (unit != null ? unit : "KG");
        if (quantityOut != null) {
            result += " → " + quantityOut + " " + (unit != null ? unit : "KG");
        }
        return result;
    }

    public String getLossFormatted() {
        if (lossQuantity == null || lossQuantity.compareTo(BigDecimal.ZERO) == 0) {
            return "No losses";
        }
        String result = lossQuantity + " " + (unit != null ? unit : "KG");
        if (lossPercentage != null) {
            result += " (" + lossPercentage + "%)";
        }
        return result;
    }

    public String getStageSummary() {
        StringBuilder summary = new StringBuilder();

        if (stage != null) {
            summary.append(stage.getDisplayName());
        }

        if (location != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append(location);
        }

        if (facilityName != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append(facilityName);
        }

        return summary.length() > 0 ? summary.toString() : "No data";
    }

    public String getPerformanceSummary() {
        StringBuilder summary = new StringBuilder();

        if (qualityStatus != null) {
            summary.append("Quality: ").append(qualityStatus.getDisplayName());
        }

        BigDecimal efficiency = getEfficiencyRate();
        if (efficiency != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Efficiency: ").append(efficiency).append("%");
        }

        if (hasLosses()) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Losses: ").append(getLossFormatted());
        }

        return summary.length() > 0 ? summary.toString() : "No performance data";
    }

    public String getTimelineSummary() {
        StringBuilder summary = new StringBuilder();

        summary.append("Started: ").append(getStageStartDateFormatted());

        if (isStageCompleted()) {
            summary.append(" - Completed: ").append(getStageEndDateFormatted());
            long duration = getStageDurationDays();
            if (duration > 0) {
                summary.append(" (").append(duration).append(" days)");
            }
        } else {
            summary.append(" - In Progress");
        }

        return summary.toString();
    }

    @Override
    public String toString() {
        return "SupplyChain{" +
                "id='" + id + '\'' +
                ", cropProductionId='" + cropProductionId + '\'' +
                ", stage=" + stage +
                ", stageOrder=" + stageOrder +
                ", location='" + location + '\'' +
                ", quantityIn=" + quantityIn +
                ", quantityOut=" + quantityOut +
                ", qualityStatus=" + qualityStatus +
                ", responsibleParty='" + responsibleParty + '\'' +
                ", trackingCode='" + trackingCode + '\'' +
                ", stageStartDate=" + stageStartDate +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupplyChain that = (SupplyChain) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}