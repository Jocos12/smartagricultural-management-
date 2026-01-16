package SmartAgricultural.Management.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Entity
@Table(name = "buyers")
public class Buyer {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "user_id", length = 20, unique = true, nullable = false)
    @NotBlank(message = "User ID is required")
    private String userId;

    @Column(name = "buyer_code", length = 20, unique = true, nullable = false)
    @NotBlank(message = "Buyer code is required")
    @Size(max = 20, message = "Buyer code must not exceed 20 characters")
    private String buyerCode;

    @Column(name = "company_name", length = 150, nullable = false)
    @NotBlank(message = "Company name is required")
    @Size(max = 150, message = "Company name must not exceed 150 characters")
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "buyer_type", nullable = false)
    @NotNull(message = "Buyer type is required")
    private BuyerType buyerType;

    @Column(name = "business_license", length = 50)
    @Size(max = 50, message = "Business license must not exceed 50 characters")
    private String businessLicense;

    @Column(name = "tax_registration", length = 30)
    @Size(max = 30, message = "Tax registration must not exceed 30 characters")
    private String taxRegistration;

    @Column(name = "location", length = 255, nullable = false)
    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    @Column(name = "contact_person", length = 100)
    @Size(max = 100, message = "Contact person must not exceed 100 characters")
    private String contactPerson;

    @Column(name = "primary_products", columnDefinition = "TEXT")
    private String primaryProducts; // JSON array

    @Column(name = "credit_limit", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "Credit limit must be positive")
    @Digits(integer = 10, fraction = 2, message = "Credit limit format is invalid")
    private BigDecimal creditLimit = BigDecimal.ZERO;

    @Column(name = "credit_rating", length = 10)
    @Size(max = 10, message = "Credit rating must not exceed 10 characters")
    private String creditRating;

    @Column(name = "payment_terms", length = 100)
    @Size(max = 100, message = "Payment terms must not exceed 100 characters")
    private String paymentTerms;

    @Column(name = "preferred_payment_methods", columnDefinition = "TEXT")
    private String preferredPaymentMethods; // JSON array

    @Column(name = "storage_capacity", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Storage capacity must be positive")
    @Digits(integer = 8, fraction = 2, message = "Storage capacity format is invalid")
    private BigDecimal storageCapacity; // tonnes

    @Column(name = "transport_capacity", length = 100)
    @Size(max = 100, message = "Transport capacity must not exceed 100 characters")
    private String transportCapacity;

    @Column(name = "quality_standards", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Quality standards must not exceed 3000 characters")
    private String qualityStandards;

    @Column(name = "certifications_required", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Certifications required must not exceed 3000 characters")
    private String certificationsRequired;

    @Column(name = "seasonal_demand", columnDefinition = "TEXT")
    private String seasonalDemand; // JSON

    @Column(name = "geographical_coverage", length = 255)
    @Size(max = 255, message = "Geographical coverage must not exceed 255 characters")
    private String geographicalCoverage;

    @Column(name = "established_year")
    @Min(value = 1900, message = "Established year must be after 1900")
    @Max(value = 2050, message = "Established year must be before 2050")
    private Integer establishedYear;

    @Column(name = "annual_volume", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "Annual volume must be positive")
    @Digits(integer = 10, fraction = 2, message = "Annual volume format is invalid")
    private BigDecimal annualVolume; // tonnes/an

    @Column(name = "rating", precision = 3, scale = 2)
    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Rating must not exceed 5.0")
    @Digits(integer = 1, fraction = 2, message = "Rating format is invalid")
    private BigDecimal rating = new BigDecimal("5.0");

    @Column(name = "verified")
    private Boolean verified = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationship
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    // Enums
    public enum BuyerType {
        WHOLESALER("Wholesaler"),
        RETAILER("Retailer"),
        PROCESSOR("Processor"),
        EXPORTER("Exporter"),
        COOPERATIVE("Cooperative"),
        GOVERNMENT("Government");

        private final String displayName;

        BuyerType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum CreditRating {
        A_PLUS("A+", 5),
        A("A", 4),
        B_PLUS("B+", 3),
        B("B", 2),
        C("C", 1);

        private final String displayName;
        private final int numericValue;

        CreditRating(String displayName, int numericValue) {
            this.displayName = displayName;
            this.numericValue = numericValue;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getNumericValue() {
            return numericValue;
        }

        public static CreditRating fromString(String rating) {
            if (rating == null) return null;
            for (CreditRating cr : CreditRating.values()) {
                if (cr.displayName.equals(rating)) {
                    return cr;
                }
            }
            return null;
        }
    }

    public enum VerificationStatus {
        NOT_VERIFIED("Not Verified"),
        PENDING("Pending"),
        VERIFIED("Verified"),
        REJECTED("Rejected");

        private final String displayName;

        VerificationStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Buyer() {
        this.id = generateAlphanumericId();
        this.buyerCode = generateBuyerCode();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Buyer(String userId, String companyName, BuyerType buyerType, String location) {
        this();
        this.userId = userId;
        this.companyName = companyName;
        this.buyerType = buyerType;
        this.location = location;
    }

    // Method to generate alphanumeric ID with mixed letters and numbers
    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Add "BY" prefix for Buyer
        sb.append("BY");

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

    // Method to generate buyer code
    private String generateBuyerCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Add "BUY" prefix
        sb.append("BUY");

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
        if (this.buyerCode == null) {
            this.buyerCode = generateBuyerCode();
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBuyerCode() {
        return buyerCode;
    }

    public void setBuyerCode(String buyerCode) {
        this.buyerCode = buyerCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public BuyerType getBuyerType() {
        return buyerType;
    }

    public void setBuyerType(BuyerType buyerType) {
        this.buyerType = buyerType;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getTaxRegistration() {
        return taxRegistration;
    }

    public void setTaxRegistration(String taxRegistration) {
        this.taxRegistration = taxRegistration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPrimaryProducts() {
        return primaryProducts;
    }

    public void setPrimaryProducts(String primaryProducts) {
        this.primaryProducts = primaryProducts;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getCreditRating() {
        return creditRating;
    }

    public void setCreditRating(String creditRating) {
        this.creditRating = creditRating;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public String getPreferredPaymentMethods() {
        return preferredPaymentMethods;
    }

    public void setPreferredPaymentMethods(String preferredPaymentMethods) {
        this.preferredPaymentMethods = preferredPaymentMethods;
    }

    public BigDecimal getStorageCapacity() {
        return storageCapacity;
    }

    public void setStorageCapacity(BigDecimal storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    public String getTransportCapacity() {
        return transportCapacity;
    }

    public void setTransportCapacity(String transportCapacity) {
        this.transportCapacity = transportCapacity;
    }

    public String getQualityStandards() {
        return qualityStandards;
    }

    public void setQualityStandards(String qualityStandards) {
        this.qualityStandards = qualityStandards;
    }

    public String getCertificationsRequired() {
        return certificationsRequired;
    }

    public void setCertificationsRequired(String certificationsRequired) {
        this.certificationsRequired = certificationsRequired;
    }

    public String getSeasonalDemand() {
        return seasonalDemand;
    }

    public void setSeasonalDemand(String seasonalDemand) {
        this.seasonalDemand = seasonalDemand;
    }

    public String getGeographicalCoverage() {
        return geographicalCoverage;
    }

    public void setGeographicalCoverage(String geographicalCoverage) {
        this.geographicalCoverage = geographicalCoverage;
    }

    public Integer getEstablishedYear() {
        return establishedYear;
    }

    public void setEstablishedYear(Integer establishedYear) {
        this.establishedYear = establishedYear;
    }

    public BigDecimal getAnnualVolume() {
        return annualVolume;
    }

    public void setAnnualVolume(BigDecimal annualVolume) {
        this.annualVolume = annualVolume;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Utility methods
    public String getBuyerTypeDescription() {
        return buyerType != null ? buyerType.getDisplayName() : "Unknown";
    }

    public CreditRating getCreditRatingEnum() {
        return CreditRating.fromString(creditRating);
    }

    public void setCreditRatingEnum(CreditRating creditRating) {
        this.creditRating = creditRating != null ? creditRating.getDisplayName() : null;
    }

    public VerificationStatus getVerificationStatus() {
        if (verified == null) return VerificationStatus.NOT_VERIFIED;
        return verified ? VerificationStatus.VERIFIED : VerificationStatus.NOT_VERIFIED;
    }

    public List<String> getPrimaryProductsList() {
        if (primaryProducts == null || primaryProducts.trim().isEmpty()) {
            return List.of();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(primaryProducts, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    public void setPrimaryProductsList(List<String> products) {
        if (products == null || products.isEmpty()) {
            this.primaryProducts = null;
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.primaryProducts = mapper.writeValueAsString(products);
        } catch (Exception e) {
            this.primaryProducts = null;
        }
    }

    public List<String> getPreferredPaymentMethodsList() {
        if (preferredPaymentMethods == null || preferredPaymentMethods.trim().isEmpty()) {
            return List.of();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(preferredPaymentMethods, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    public void setPreferredPaymentMethodsList(List<String> methods) {
        if (methods == null || methods.isEmpty()) {
            this.preferredPaymentMethods = null;
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.preferredPaymentMethods = mapper.writeValueAsString(methods);
        } catch (Exception e) {
            this.preferredPaymentMethods = null;
        }
    }

    public boolean isHighVolumeBuyer() {
        return annualVolume != null && annualVolume.compareTo(new BigDecimal("1000")) > 0;
    }

    public boolean isLargeCapacityBuyer() {
        return storageCapacity != null && storageCapacity.compareTo(new BigDecimal("500")) > 0;
    }

    public boolean isPremiumBuyer() {
        return rating != null && rating.compareTo(new BigDecimal("4.0")) >= 0 && verified != null && verified;
    }

    public boolean isExperiencedBuyer() {
        if (establishedYear == null) return false;
        return (java.time.Year.now().getValue() - establishedYear) >= 5;
    }

    public boolean hasGoodCreditRating() {
        CreditRating cr = getCreditRatingEnum();
        return cr != null && (cr == CreditRating.A_PLUS || cr == CreditRating.A || cr == CreditRating.B_PLUS);
    }

    public boolean hasHighCreditLimit() {
        return creditLimit != null && creditLimit.compareTo(new BigDecimal("50000")) > 0;
    }

    public boolean isGovernmentBuyer() {
        return buyerType == BuyerType.GOVERNMENT;
    }

    public boolean isExporter() {
        return buyerType == BuyerType.EXPORTER;
    }

    public boolean isProcessor() {
        return buyerType == BuyerType.PROCESSOR;
    }

    public boolean needsVerification() {
        return verified == null || !verified;
    }

    public int getBusinessAge() {
        if (establishedYear == null) return 0;
        return java.time.Year.now().getValue() - establishedYear;
    }

    public String getRatingFormatted() {
        if (rating == null) return "Not rated";
        return rating + "/5.0";
    }

    public String getStorageCapacityFormatted() {
        if (storageCapacity == null) return "Not specified";
        return storageCapacity + " tonnes";
    }

    public String getAnnualVolumeFormatted() {
        if (annualVolume == null) return "Not specified";
        return annualVolume + " tonnes/year";
    }

    public String getCreditLimitFormatted() {
        if (creditLimit == null) return "Not specified";
        return "RWF " + creditLimit;
    }

    public String getCompanySummary() {
        StringBuilder summary = new StringBuilder();

        if (companyName != null) {
            summary.append(companyName);
        }

        if (buyerType != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append(buyerType.getDisplayName());
        }

        if (location != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append(location);
        }

        return summary.length() > 0 ? summary.toString() : "No data";
    }

    public String getBusinessProfileSummary() {
        StringBuilder summary = new StringBuilder();

        if (establishedYear != null) {
            summary.append("Est. ").append(establishedYear);
        }

        if (annualVolume != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append(getAnnualVolumeFormatted());
        }

        if (rating != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Rating: ").append(getRatingFormatted());
        }

        if (verified != null && verified) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Verified");
        }

        return summary.length() > 0 ? summary.toString() : "No profile data";
    }

    public String getFinancialSummary() {
        StringBuilder summary = new StringBuilder();

        if (creditRating != null) {
            summary.append("Credit: ").append(creditRating);
        }

        if (creditLimit != null && creditLimit.compareTo(BigDecimal.ZERO) > 0) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Limit: ").append(getCreditLimitFormatted());
        }

        if (paymentTerms != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append(paymentTerms);
        }

        return summary.length() > 0 ? summary.toString() : "No financial data";
    }

    public String getCapacitySummary() {
        StringBuilder summary = new StringBuilder();

        if (storageCapacity != null && storageCapacity.compareTo(BigDecimal.ZERO) > 0) {
            summary.append("Storage: ").append(getStorageCapacityFormatted());
        }

        if (transportCapacity != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Transport: ").append(transportCapacity);
        }

        if (geographicalCoverage != null) {
            if (summary.length() > 0) summary.append(" - ");
            summary.append("Coverage: ").append(geographicalCoverage);
        }

        return summary.length() > 0 ? summary.toString() : "No capacity data";
    }

    // toString, equals and hashCode
    @Override
    public String toString() {
        return "Buyer{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", buyerCode='" + buyerCode + '\'' +
                ", companyName='" + companyName + '\'' +
                ", buyerType=" + buyerType +
                ", location='" + location + '\'' +
                ", creditRating='" + creditRating + '\'' +
                ", annualVolume=" + annualVolume +
                ", rating=" + rating +
                ", verified=" + verified +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Buyer buyer = (Buyer) o;
        return id != null ? id.equals(buyer.id) : buyer.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}