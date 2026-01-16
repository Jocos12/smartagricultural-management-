package SmartAgricultural.Management.dto;

import SmartAgricultural.Management.Model.Buyer;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BuyerDTO {

    private String id;

    @NotBlank(message = "User ID is required")
    private String userId;

    private String buyerCode;

    @NotBlank(message = "Company name is required")
    @Size(max = 150, message = "Company name must not exceed 150 characters")
    private String companyName;

    @NotNull(message = "Buyer type is required")
    private Buyer.BuyerType buyerType;

    @Size(max = 50, message = "Business license must not exceed 50 characters")
    private String businessLicense;

    @Size(max = 30, message = "Tax registration must not exceed 30 characters")
    private String taxRegistration;

    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    @Size(max = 100, message = "Contact person must not exceed 100 characters")
    private String contactPerson;

    private List<String> primaryProductsList;

    @DecimalMin(value = "0.0", message = "Credit limit must be positive")
    @Digits(integer = 10, fraction = 2, message = "Credit limit format is invalid")
    private BigDecimal creditLimit;

    @Size(max = 10, message = "Credit rating must not exceed 10 characters")
    private String creditRating;

    @Size(max = 100, message = "Payment terms must not exceed 100 characters")
    private String paymentTerms;

    private List<String> preferredPaymentMethodsList;

    @DecimalMin(value = "0.0", message = "Storage capacity must be positive")
    @Digits(integer = 8, fraction = 2, message = "Storage capacity format is invalid")
    private BigDecimal storageCapacity;

    @Size(max = 100, message = "Transport capacity must not exceed 100 characters")
    private String transportCapacity;

    @Size(max = 3000, message = "Quality standards must not exceed 3000 characters")
    private String qualityStandards;

    @Size(max = 3000, message = "Certifications required must not exceed 3000 characters")
    private String certificationsRequired;

    private String seasonalDemand;

    @Size(max = 255, message = "Geographical coverage must not exceed 255 characters")
    private String geographicalCoverage;

    @Min(value = 1900, message = "Established year must be after 1900")
    @Max(value = 2050, message = "Established year must be before 2050")
    private Integer establishedYear;

    @DecimalMin(value = "0.0", message = "Annual volume must be positive")
    @Digits(integer = 10, fraction = 2, message = "Annual volume format is invalid")
    private BigDecimal annualVolume;

    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Rating must not exceed 5.0")
    @Digits(integer = 1, fraction = 2, message = "Rating format is invalid")
    private BigDecimal rating;

    private Boolean verified;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // Constructors
    public BuyerDTO() {}

    public BuyerDTO(String userId, String companyName, Buyer.BuyerType buyerType, String location) {
        this.userId = userId;
        this.companyName = companyName;
        this.buyerType = buyerType;
        this.location = location;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBuyerCode() { return buyerCode; }
    public void setBuyerCode(String buyerCode) { this.buyerCode = buyerCode; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Buyer.BuyerType getBuyerType() { return buyerType; }
    public void setBuyerType(Buyer.BuyerType buyerType) { this.buyerType = buyerType; }

    public String getBusinessLicense() { return businessLicense; }
    public void setBusinessLicense(String businessLicense) { this.businessLicense = businessLicense; }

    public String getTaxRegistration() { return taxRegistration; }
    public void setTaxRegistration(String taxRegistration) { this.taxRegistration = taxRegistration; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public List<String> getPrimaryProductsList() { return primaryProductsList; }
    public void setPrimaryProductsList(List<String> primaryProductsList) { this.primaryProductsList = primaryProductsList; }

    public BigDecimal getCreditLimit() { return creditLimit; }
    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }

    public String getCreditRating() { return creditRating; }
    public void setCreditRating(String creditRating) { this.creditRating = creditRating; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public List<String> getPreferredPaymentMethodsList() { return preferredPaymentMethodsList; }
    public void setPreferredPaymentMethodsList(List<String> preferredPaymentMethodsList) {
        this.preferredPaymentMethodsList = preferredPaymentMethodsList;
    }

    public BigDecimal getStorageCapacity() { return storageCapacity; }
    public void setStorageCapacity(BigDecimal storageCapacity) { this.storageCapacity = storageCapacity; }

    public String getTransportCapacity() { return transportCapacity; }
    public void setTransportCapacity(String transportCapacity) { this.transportCapacity = transportCapacity; }

    public String getQualityStandards() { return qualityStandards; }
    public void setQualityStandards(String qualityStandards) { this.qualityStandards = qualityStandards; }

    public String getCertificationsRequired() { return certificationsRequired; }
    public void setCertificationsRequired(String certificationsRequired) { this.certificationsRequired = certificationsRequired; }

    public String getSeasonalDemand() { return seasonalDemand; }
    public void setSeasonalDemand(String seasonalDemand) { this.seasonalDemand = seasonalDemand; }

    public String getGeographicalCoverage() { return geographicalCoverage; }
    public void setGeographicalCoverage(String geographicalCoverage) { this.geographicalCoverage = geographicalCoverage; }

    public Integer getEstablishedYear() { return establishedYear; }
    public void setEstablishedYear(Integer establishedYear) { this.establishedYear = establishedYear; }

    public BigDecimal getAnnualVolume() { return annualVolume; }
    public void setAnnualVolume(BigDecimal annualVolume) { this.annualVolume = annualVolume; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}