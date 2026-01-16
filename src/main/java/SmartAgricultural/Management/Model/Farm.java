package SmartAgricultural.Management.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "farms")
public class Farm {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "farmer_id", length = 20, nullable = false)
    @NotBlank(message = "Farmer ID is required")
    private String farmerId;

    @Column(name = "farm_name", length = 100, nullable = false)
    @NotBlank(message = "Farm name is required")
    @Size(max = 100, message = "Farm name must not exceed 100 characters")
    private String farmName;

    @Column(name = "farm_code", length = 20, unique = true)
    @Size(max = 20, message = "Farm code must not exceed 20 characters")
    private String farmCode;

    @Column(name = "farm_size", precision = 8, scale = 2, nullable = false)
    @NotNull(message = "Farm size is required")
    @DecimalMin(value = "0.01", message = "Farm size must be greater than 0")
    @Digits(integer = 6, fraction = 2, message = "Farm size format is invalid")
    private BigDecimal farmSize;

    @Column(name = "soil_type", length = 50, nullable = false)
    @NotBlank(message = "Soil type is required")
    @Size(max = 50, message = "Soil type must not exceed 50 characters")
    private String soilType;

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

    @Column(name = "altitude", precision = 6, scale = 2)
    @DecimalMin(value = "0.0", message = "Altitude must be positive")
    @Digits(integer = 4, fraction = 2, message = "Altitude format is invalid")
    private BigDecimal altitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "irrigation_system")
    private IrrigationSystem irrigationSystem = IrrigationSystem.RAIN_FED;

    @Column(name = "topography", length = 50)
    @Size(max = 50, message = "Topography must not exceed 50 characters")
    private String topography;

    @Column(name = "water_source", length = 100)
    @Size(max = 100, message = "Water source must not exceed 100 characters")
    private String waterSource;

    @Column(name = "electricity_available")
    private Boolean electricityAvailable = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "road_access_quality")
    private RoadAccessQuality roadAccessQuality = RoadAccessQuality.MODERATE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // CORRECTION: Relation directe avec User au lieu de Farmer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private User farmer;

    // Enums
    public enum IrrigationSystem {
        RAIN_FED("Rain Fed"),
        SPRINKLER("Sprinkler"),
        DRIP("Drip Irrigation"),
        FLOOD("Flood Irrigation"),
        MANUAL("Manual Irrigation");

        private final String displayName;

        IrrigationSystem(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum RoadAccessQuality {
        GOOD("Good"),
        MODERATE("Moderate"),
        POOR("Poor");

        private final String displayName;

        RoadAccessQuality(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Topography {
        FLAT("Flat"),
        HILLY("Hilly"),
        MOUNTAINOUS("Mountainous");

        private final String displayName;

        Topography(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Farm() {
        this.id = generateAlphanumericId();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Farm(String farmerId, String farmName, BigDecimal farmSize, String soilType,
                BigDecimal latitude, BigDecimal longitude) {
        this();
        this.farmerId = farmerId;
        this.farmName = farmName;
        this.farmSize = farmSize;
        this.soilType = soilType;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Method to generate alphanumeric ID
    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        sb.append("FM");

        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 6);
        sb.append(shortTimestamp);

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

    public String getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getFarmCode() {
        return farmCode;
    }

    public void setFarmCode(String farmCode) {
        this.farmCode = farmCode;
    }

    public BigDecimal getFarmSize() {
        return farmSize;
    }

    public void setFarmSize(BigDecimal farmSize) {
        this.farmSize = farmSize;
    }

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
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

    public BigDecimal getAltitude() {
        return altitude;
    }

    public void setAltitude(BigDecimal altitude) {
        this.altitude = altitude;
    }

    public IrrigationSystem getIrrigationSystem() {
        return irrigationSystem;
    }

    public void setIrrigationSystem(IrrigationSystem irrigationSystem) {
        this.irrigationSystem = irrigationSystem;
    }

    public String getTopography() {
        return topography;
    }

    public void setTopography(String topography) {
        this.topography = topography;
    }

    public String getWaterSource() {
        return waterSource;
    }

    public void setWaterSource(String waterSource) {
        this.waterSource = waterSource;
    }

    public Boolean getElectricityAvailable() {
        return electricityAvailable;
    }

    public void setElectricityAvailable(Boolean electricityAvailable) {
        this.electricityAvailable = electricityAvailable;
    }

    public RoadAccessQuality getRoadAccessQuality() {
        return roadAccessQuality;
    }

    public void setRoadAccessQuality(RoadAccessQuality roadAccessQuality) {
        this.roadAccessQuality = roadAccessQuality;
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

    public User getFarmer() {
        return farmer;
    }

    public void setFarmer(User farmer) {
        this.farmer = farmer;
    }

    // Utility methods
    public String getCoordinates() {
        return String.format("%.6f, %.6f", latitude, longitude);
    }

    public String getAltitudeDisplay() {
        return altitude != null ? altitude + " m" : "N/A";
    }

    public boolean hasElectricity() {
        return electricityAvailable != null && electricityAvailable;
    }

    public boolean isLargeFarm() {
        return farmSize != null && farmSize.compareTo(new BigDecimal("10.0")) > 0;
    }

    public boolean isSmallFarm() {
        return farmSize != null && farmSize.compareTo(new BigDecimal("2.0")) <= 0;
    }

    public String getSizeCategory() {
        if (farmSize == null) return "Unknown";

        if (farmSize.compareTo(new BigDecimal("2.0")) <= 0) {
            return "Small Farm";
        } else if (farmSize.compareTo(new BigDecimal("10.0")) <= 0) {
            return "Medium Farm";
        } else {
            return "Large Farm";
        }
    }

    // Méthode utilitaire pour vérifier si le farmer est valide
    public boolean hasFarmerWithValidRole() {
        return farmer != null && farmer.getRole() == User.Role.FARMER;
    }

    @Override
    public String toString() {
        return "Farm{" +
                "id='" + id + '\'' +
                ", farmerId='" + farmerId + '\'' +
                ", farmName='" + farmName + '\'' +
                ", farmCode='" + farmCode + '\'' +
                ", farmSize=" + farmSize +
                ", soilType='" + soilType + '\'' +
                ", irrigationSystem=" + irrigationSystem +
                ", topography='" + topography + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Farm farm = (Farm) o;
        return id != null ? id.equals(farm.id) : farm.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}