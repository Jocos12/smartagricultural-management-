package SmartAgricultural.Management.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "farmers")
public class Farmer {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "user_id", length = 20, unique = true, nullable = false)
    @NotBlank(message = "User ID is required")
    private String userId;

    @Column(name = "farmer_code", length = 20, unique = true, nullable = false)
    @NotBlank(message = "Farmer code is required")
    @Size(max = 20, message = "Farmer code must not exceed 20 characters")
    private String farmerCode;

    @Column(name = "cooperative_name", length = 100)
    @Size(max = 100, message = "Cooperative name must not exceed 100 characters")
    private String cooperativeName;

    @Column(name = "total_land_size", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", inclusive = false, message = "Total land size must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Total land size format is invalid")
    private BigDecimal totalLandSize;

    @Column(name = "location", nullable = false)
    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    @Column(name = "latitude", precision = 10, scale = 8, nullable = false)
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8, nullable = false)
    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private BigDecimal longitude;

    @Column(name = "province", length = 50, nullable = false)
    @NotBlank(message = "Province is required")
    @Size(max = 50, message = "Province must not exceed 50 characters")
    private String province;

    @Column(name = "district", length = 50, nullable = false)
    @NotBlank(message = "District is required")
    @Size(max = 50, message = "District must not exceed 50 characters")
    private String district;

    @Column(name = "sector", length = 50, nullable = false)
    @NotBlank(message = "Sector is required")
    @Size(max = 50, message = "Sector must not exceed 50 characters")
    private String sector;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level")
    private ExperienceLevel experienceLevel = ExperienceLevel.BEGINNER;

    @Column(name = "certification_level", length = 50)
    @Size(max = 50, message = "Certification level must not exceed 50 characters")
    private String certificationLevel;

    @Column(name = "contact_person", length = 100)
    @Size(max = 100, message = "Contact person must not exceed 100 characters")
    private String contactPerson;

    @Column(name = "bank_account", length = 50)
    @Size(max = 50, message = "Bank account must not exceed 50 characters")
    private String bankAccount;

    @Column(name = "tax_number", length = 30)
    @Size(max = 30, message = "Tax number must not exceed 30 characters")
    private String taxNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationship with User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    // Enum for experience levels
    public enum ExperienceLevel {
        BEGINNER("Beginner"),
        INTERMEDIATE("Intermediate"),
        EXPERT("Expert");

        private final String displayName;

        ExperienceLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Farmer() {
        this.id = generateAlphanumericId();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Farmer(String userId, String farmerCode, String location,
                  BigDecimal latitude, BigDecimal longitude,
                  String province, String district, String sector) {
        this();
        this.userId = userId;
        this.farmerCode = farmerCode;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.province = province;
        this.district = district;
        this.sector = sector;
    }

    // Method to generate alphanumeric ID
    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Add "F" prefix for Farmer
        sb.append("F");

        // Timestamp-based prefix to ensure uniqueness
        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 6);
        sb.append(shortTimestamp);

        // Add random characters
        for (int i = 0; i < 7; i++) {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFarmerCode() {
        return farmerCode;
    }

    public void setFarmerCode(String farmerCode) {
        this.farmerCode = farmerCode;
    }

    public String getCooperativeName() {
        return cooperativeName;
    }

    public void setCooperativeName(String cooperativeName) {
        this.cooperativeName = cooperativeName;
    }

    public BigDecimal getTotalLandSize() {
        return totalLandSize;
    }

    public void setTotalLandSize(BigDecimal totalLandSize) {
        this.totalLandSize = totalLandSize;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(ExperienceLevel experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getCertificationLevel() {
        return certificationLevel;
    }

    public void setCertificationLevel(String certificationLevel) {
        this.certificationLevel = certificationLevel;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
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
    public boolean isBeginner() {
        return this.experienceLevel == ExperienceLevel.BEGINNER;
    }

    public boolean isIntermediate() {
        return this.experienceLevel == ExperienceLevel.INTERMEDIATE;
    }

    public boolean isExpert() {
        return this.experienceLevel == ExperienceLevel.EXPERT;
    }

    public boolean hasCertification() {
        return this.certificationLevel != null && !this.certificationLevel.trim().isEmpty();
    }

    public String getFullAddress() {
        return String.format("%s, %s, %s, %s", location, sector, district, province);
    }

    public String getCoordinates() {
        return String.format("%.6f, %.6f", latitude, longitude);
    }

    // toString, equals and hashCode
    @Override
    public String toString() {
        return "Farmer{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", farmerCode='" + farmerCode + '\'' +
                ", cooperativeName='" + cooperativeName + '\'' +
                ", totalLandSize=" + totalLandSize +
                ", location='" + location + '\'' +
                ", province='" + province + '\'' +
                ", district='" + district + '\'' +
                ", sector='" + sector + '\'' +
                ", experienceLevel=" + experienceLevel +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Farmer farmer = (Farmer) o;
        return id != null ? id.equals(farmer.id) : farmer.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}