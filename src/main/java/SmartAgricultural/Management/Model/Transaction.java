package SmartAgricultural.Management.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "transaction_code", length = 30, unique = true, nullable = false)
    @Size(max = 30, message = "Transaction code must not exceed 30 characters")
    private String transactionCode;

    // FIXED: Reference users table instead of farmers/buyers tables
    @Column(name = "farmer_id", length = 20, nullable = false)
    @NotBlank(message = "Farmer ID is required")
    private String farmerId;

    @Column(name = "buyer_id", length = 20, nullable = false)
    @NotBlank(message = "Buyer ID is required")
    private String buyerId;

    @Column(name = "crop_id", length = 20, nullable = false)
    @NotBlank(message = "Crop ID is required")
    private String cropId;

    @Column(name = "crop_production_id", length = 20)
    private String cropProductionId;

    @Column(name = "transaction_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transactionDate;

    @Column(name = "quantity", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be positive")
    @Digits(integer = 8, fraction = 2, message = "Quantity format is invalid")
    private BigDecimal quantity;

    @Column(name = "unit", length = 20)
    @Size(max = 20, message = "Unit must not exceed 20 characters")
    private String unit = "KG";

    @Column(name = "price_per_unit", precision = 8, scale = 2, nullable = false)
    @NotNull(message = "Price per unit is required")
    @DecimalMin(value = "0.01", message = "Price per unit must be positive")
    @Digits(integer = 6, fraction = 2, message = "Price per unit format is invalid")
    private BigDecimal pricePerUnit;

    @Column(name = "total_amount", precision = 12, scale = 2, nullable = false)
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.01", message = "Total amount must be positive")
    @Digits(integer = 10, fraction = 2, message = "Total amount format is invalid")
    private BigDecimal totalAmount;

    @Column(name = "currency", length = 3)
    @Size(max = 3, message = "Currency code must not exceed 3 characters")
    private String currency = "XAF";

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransactionStatus status = TransactionStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Column(name = "payment_terms", length = 100)
    @Size(max = 100, message = "Payment terms must not exceed 100 characters")
    private String paymentTerms;

    @Column(name = "advance_payment", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "Advance payment must be positive")
    @Digits(integer = 10, fraction = 2, message = "Advance payment format is invalid")
    private BigDecimal advancePayment = BigDecimal.ZERO;

    @Column(name = "delivery_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;

    @Column(name = "delivery_location", length = 255)
    @Size(max = 255, message = "Delivery location must not exceed 255 characters")
    private String deliveryLocation;

    @Column(name = "quality_grade", length = 50)
    @Size(max = 50, message = "Quality grade must not exceed 50 characters")
    private String qualityGrade;

    @Column(name = "quality_specifications", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Quality specifications must not exceed 3000 characters")
    private String qualitySpecifications;

    @Column(name = "packaging_requirements", length = 100)
    @Size(max = 100, message = "Packaging requirements must not exceed 100 characters")
    private String packagingRequirements;

    @Enumerated(EnumType.STRING)
    @Column(name = "transport_responsibility")
    private TransportResponsibility transportResponsibility = TransportResponsibility.BUYER;

    @Column(name = "transport_cost", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Transport cost must be positive")
    @Digits(integer = 6, fraction = 2, message = "Transport cost format is invalid")
    private BigDecimal transportCost;

    @Column(name = "insurance_cost", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Insurance cost must be positive")
    @Digits(integer = 6, fraction = 2, message = "Insurance cost format is invalid")
    private BigDecimal insuranceCost;

    @Column(name = "contract_terms", columnDefinition = "TEXT")
    @Size(max = 5000, message = "Contract terms must not exceed 5000 characters")
    private String contractTerms;

    @Column(name = "penalty_clause", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Penalty clause must not exceed 2000 characters")
    private String penaltyClause;

    @Column(name = "dispute_resolution", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Dispute resolution must not exceed 2000 characters")
    private String disputeResolution;

    @Column(name = "broker_involved")
    private Boolean brokerInvolved = false;

    @Column(name = "broker_commission", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Broker commission must be positive")
    @Digits(integer = 6, fraction = 2, message = "Broker commission format is invalid")
    private BigDecimal brokerCommission;

    @Column(name = "government_tax", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Government tax must be positive")
    @Digits(integer = 6, fraction = 2, message = "Government tax format is invalid")
    private BigDecimal governmentTax;

    @Column(name = "net_amount_farmer", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "Net amount farmer must be positive")
    @Digits(integer = 10, fraction = 2, message = "Net amount farmer format is invalid")
    private BigDecimal netAmountFarmer;

    @Column(name = "payment_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentDate;

    @Column(name = "completion_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completionDate;

    @Column(name = "rating_farmer")
    @Min(value = 1, message = "Farmer rating must be at least 1")
    @Max(value = 5, message = "Farmer rating must not exceed 5")
    private Integer ratingFarmer;

    @Column(name = "rating_buyer")
    @Min(value = 1, message = "Buyer rating must be at least 1")
    @Max(value = 5, message = "Buyer rating must not exceed 5")
    private Integer ratingBuyer;

    @Column(name = "notes", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Notes must not exceed 3000 characters")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // FIXED: Relationships now reference User table instead of separate Farmer/Buyer tables
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", insertable = false, updatable = false)
    @JsonIgnore
    private User farmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", insertable = false, updatable = false)
    @JsonIgnore
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", insertable = false, updatable = false)
    @JsonIgnore
    private Crop crop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_production_id", insertable = false, updatable = false)
    @JsonIgnore
    private CropProduction cropProduction;

    // Enums
    public enum TransactionStatus {
        PENDING("Pending"),
        CONFIRMED("Confirmed"),
        DELIVERED("Delivered"),
        PAID("Paid"),
        CANCELLED("Cancelled"),
        DISPUTED("Disputed");

        private final String displayName;

        TransactionStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isActive() {
            return this != CANCELLED;
        }

        public boolean isCompleted() {
            return this == PAID;
        }

        public boolean canBeCancelled() {
            return this == PENDING || this == CONFIRMED;
        }

        public boolean canBeDelivered() {
            return this == CONFIRMED;
        }

        public boolean canBePaid() {
            return this == DELIVERED;
        }
    }

    public enum PaymentMethod {
        CASH("Cash"),
        BANK_TRANSFER("Bank Transfer"),
        MOBILE_MONEY("Mobile Money"),
        CHECK("Check"),
        CREDIT("Credit");

        private final String displayName;

        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isImmediate() {
            return this == CASH || this == MOBILE_MONEY;
        }

        public boolean requiresVerification() {
            return this == BANK_TRANSFER || this == CHECK;
        }
    }

    public enum TransportResponsibility {
        FARMER("Farmer"),
        BUYER("Buyer"),
        SHARED("Shared"),
        THIRD_PARTY("Third Party");

        private final String displayName;

        TransportResponsibility(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum TransactionType {
        SPOT("Spot Transaction"),
        CONTRACT("Contract Transaction"),
        AUCTION("Auction Transaction"),
        COOPERATIVE("Cooperative Transaction");

        private final String displayName;

        TransactionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum RatingLevel {
        VERY_POOR("Very Poor", 1, 1),
        POOR("Poor", 2, 2),
        FAIR("Fair", 3, 3),
        GOOD("Good", 4, 4),
        EXCELLENT("Excellent", 5, 5);

        private final String displayName;
        private final int minRating;
        private final int maxRating;

        RatingLevel(String displayName, int minRating, int maxRating) {
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

        public static RatingLevel fromRating(int rating) {
            for (RatingLevel level : RatingLevel.values()) {
                if (rating >= level.minRating && rating <= level.maxRating) {
                    return level;
                }
            }
            return FAIR;
        }
    }

    // Constructors
    public Transaction() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.transactionDate = LocalDateTime.now();
    }

    public Transaction(String farmerId, String buyerId, String cropId, BigDecimal quantity,
                       BigDecimal pricePerUnit, PaymentMethod paymentMethod) {
        this();
        this.farmerId = farmerId;
        this.buyerId = buyerId;
        this.cropId = cropId;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.paymentMethod = paymentMethod;
        calculateTotalAmount();
    }

    // ID generation method with timestamp and random chars
    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        sb.append("TX");

        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 6);
        sb.append(shortTimestamp);

        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    // Transaction code generation with date and timestamp
    private String generateTransactionCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        sb.append("TXN");

        LocalDateTime now = LocalDateTime.now();
        sb.append(String.format("%02d%02d%02d",
                now.getYear() % 100,
                now.getMonthValue(),
                now.getDayOfMonth()));

        sb.append(String.format("%02d%02d",
                now.getHour(),
                now.getMinute()));

        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 3);
        sb.append(shortTimestamp);

        for (int i = 0; i < 3; i++) {
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

        if (this.transactionCode == null) {
            this.transactionCode = generateTransactionCode();
        }

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.transactionDate == null) {
            this.transactionDate = LocalDateTime.now();
        }

        calculateTotalAmount();
        calculateNetAmountFarmer();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculateTotalAmount();
        calculateNetAmountFarmer();
    }

    // Utility methods for calculations
    private void calculateTotalAmount() {
        if (quantity != null && pricePerUnit != null) {
            this.totalAmount = quantity.multiply(pricePerUnit);
        }
    }

    private void calculateNetAmountFarmer() {
        if (totalAmount == null) return;

        BigDecimal netAmount = totalAmount;

        if (brokerCommission != null) {
            netAmount = netAmount.subtract(brokerCommission);
        }

        if (governmentTax != null) {
            netAmount = netAmount.subtract(governmentTax);
        }

        if (transportCost != null &&
                (transportResponsibility == TransportResponsibility.FARMER ||
                        transportResponsibility == TransportResponsibility.SHARED)) {
            BigDecimal farmerTransportCost = transportResponsibility == TransportResponsibility.SHARED ?
                    transportCost.divide(new BigDecimal("2"), 2, BigDecimal.ROUND_HALF_UP) :
                    transportCost;
            netAmount = netAmount.subtract(farmerTransportCost);
        }

        this.netAmountFarmer = netAmount;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTransactionCode() { return transactionCode; }
    public void setTransactionCode(String transactionCode) { this.transactionCode = transactionCode; }

    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }

    public String getCropId() { return cropId; }
    public void setCropId(String cropId) { this.cropId = cropId; }

    public String getCropProductionId() { return cropProductionId; }
    public void setCropProductionId(String cropProductionId) { this.cropProductionId = cropProductionId; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(BigDecimal pricePerUnit) { this.pricePerUnit = pricePerUnit; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public BigDecimal getAdvancePayment() { return advancePayment; }
    public void setAdvancePayment(BigDecimal advancePayment) { this.advancePayment = advancePayment; }

    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getDeliveryLocation() { return deliveryLocation; }
    public void setDeliveryLocation(String deliveryLocation) { this.deliveryLocation = deliveryLocation; }

    public String getQualityGrade() { return qualityGrade; }
    public void setQualityGrade(String qualityGrade) { this.qualityGrade = qualityGrade; }

    public String getQualitySpecifications() { return qualitySpecifications; }
    public void setQualitySpecifications(String qualitySpecifications) { this.qualitySpecifications = qualitySpecifications; }

    public String getPackagingRequirements() { return packagingRequirements; }
    public void setPackagingRequirements(String packagingRequirements) { this.packagingRequirements = packagingRequirements; }

    public TransportResponsibility getTransportResponsibility() { return transportResponsibility; }
    public void setTransportResponsibility(TransportResponsibility transportResponsibility) { this.transportResponsibility = transportResponsibility; }

    public BigDecimal getTransportCost() { return transportCost; }
    public void setTransportCost(BigDecimal transportCost) { this.transportCost = transportCost; }

    public BigDecimal getInsuranceCost() { return insuranceCost; }
    public void setInsuranceCost(BigDecimal insuranceCost) { this.insuranceCost = insuranceCost; }

    public String getContractTerms() { return contractTerms; }
    public void setContractTerms(String contractTerms) { this.contractTerms = contractTerms; }

    public String getPenaltyClause() { return penaltyClause; }
    public void setPenaltyClause(String penaltyClause) { this.penaltyClause = penaltyClause; }

    public String getDisputeResolution() { return disputeResolution; }
    public void setDisputeResolution(String disputeResolution) { this.disputeResolution = disputeResolution; }

    public Boolean getBrokerInvolved() { return brokerInvolved; }
    public void setBrokerInvolved(Boolean brokerInvolved) { this.brokerInvolved = brokerInvolved; }

    public BigDecimal getBrokerCommission() { return brokerCommission; }
    public void setBrokerCommission(BigDecimal brokerCommission) { this.brokerCommission = brokerCommission; }

    public BigDecimal getGovernmentTax() { return governmentTax; }
    public void setGovernmentTax(BigDecimal governmentTax) { this.governmentTax = governmentTax; }

    public BigDecimal getNetAmountFarmer() { return netAmountFarmer; }
    public void setNetAmountFarmer(BigDecimal netAmountFarmer) { this.netAmountFarmer = netAmountFarmer; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public LocalDateTime getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDateTime completionDate) { this.completionDate = completionDate; }

    public Integer getRatingFarmer() { return ratingFarmer; }
    public void setRatingFarmer(Integer ratingFarmer) { this.ratingFarmer = ratingFarmer; }

    public Integer getRatingBuyer() { return ratingBuyer; }
    public void setRatingBuyer(Integer ratingBuyer) { this.ratingBuyer = ratingBuyer; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // FIXED: Now returns User objects instead of Farmer/Buyer
    public User getFarmer() { return farmer; }
    public void setFarmer(User farmer) { this.farmer = farmer; }

    public User getBuyer() { return buyer; }
    public void setBuyer(User buyer) { this.buyer = buyer; }

    public Crop getCrop() { return crop; }
    public void setCrop(Crop crop) { this.crop = crop; }

    public CropProduction getCropProduction() { return cropProduction; }
    public void setCropProduction(CropProduction cropProduction) { this.cropProduction = cropProduction; }

    // Business logic methods
    public boolean canConfirm() {
        return status == TransactionStatus.PENDING;
    }

    public boolean canDeliver() {
        return status == TransactionStatus.CONFIRMED;
    }

    public boolean canMarkAsPaid() {
        return status == TransactionStatus.DELIVERED;
    }

    public boolean canCancel() {
        return status != null && status.canBeCancelled();
    }

    public boolean canDispute() {
        return status != TransactionStatus.CANCELLED && status != TransactionStatus.DISPUTED;
    }

    public void confirm() {
        if (canConfirm()) {
            this.status = TransactionStatus.CONFIRMED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void markAsDelivered() {
        if (canDeliver()) {
            this.status = TransactionStatus.DELIVERED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void markAsPaid() {
        if (canMarkAsPaid()) {
            this.status = TransactionStatus.PAID;
            this.paymentDate = LocalDateTime.now();
            this.completionDate = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void cancel() {
        if (canCancel()) {
            this.status = TransactionStatus.CANCELLED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void markAsDisputed() {
        if (canDispute()) {
            this.status = TransactionStatus.DISPUTED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean isCompleted() {
        return status != null && status.isCompleted();
    }

    public boolean isActive() {
        return status != null && status.isActive();
    }

    // Validation helper methods
    public boolean isValidFarmer(User user) {
        return user != null && user.getRole() == User.Role.FARMER;
    }

    public boolean isValidBuyer(User user) {
        return user != null && user.getRole() == User.Role.BUYER;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", transactionCode='" + transactionCode + '\'' +
                ", farmerId='" + farmerId + '\'' +
                ", buyerId='" + buyerId + '\'' +
                ", cropId='" + cropId + '\'' +
                ", quantity=" + quantity +
                ", pricePerUnit=" + pricePerUnit +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", paymentMethod=" + paymentMethod +
                ", deliveryDate=" + deliveryDate +
                ", transactionDate=" + transactionDate +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}