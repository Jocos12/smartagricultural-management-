package SmartAgricultural.Management.Model;

import SmartAgricultural.Management.Config.BigDecimalDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Entity
@Table(name = "weather_data")
public class WeatherData {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "latitude", precision = 10, scale = 8, nullable = false)
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8, nullable = false)
    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal longitude;

    @Column(name = "record_date", nullable = false)
    @NotNull(message = "Record date is required")
    // FIXED: Accept both ISO 8601 (with T) and space-separated formats
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime recordDate;

    @Column(name = "temperature", precision = 4, scale = 1, nullable = false)
    @NotNull(message = "Temperature is required")
    @DecimalMin(value = "-50.0")
    @DecimalMax(value = "60.0")
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal temperature;

    @Column(name = "temperature_min", precision = 4, scale = 1)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal temperatureMin;

    @Column(name = "temperature_max", precision = 4, scale = 1)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal temperatureMax;

    @Column(name = "humidity", precision = 4, scale = 1)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal humidity;

    @Column(name = "rainfall", precision = 6, scale = 2)
    @DecimalMin(value = "0.0")
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal rainfall;

    @Column(name = "wind_speed", precision = 4, scale = 1)
    @DecimalMin(value = "0.0")
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal windSpeed;

    @Column(name = "wind_direction")
    @Min(value = 0)
    @Max(value = 360)
    private Integer windDirection;

    @Column(name = "weather_condition", length = 50)
    @Size(max = 50)
    private String weatherCondition;

    @Column(name = "solar_radiation", precision = 6, scale = 2)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal solarRadiation;

    @Column(name = "evapotranspiration", precision = 6, scale = 2)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal evapotranspiration;

    @Column(name = "atmospheric_pressure", precision = 7, scale = 2)
    @DecimalMin(value = "800.0")
    @DecimalMax(value = "1200.0")
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal atmosphericPressure;

    @Column(name = "uv_index")
    @Min(value = 0)
    @Max(value = 15)
    private Integer uvIndex;

    @Column(name = "data_source", length = 50, nullable = false)
    @NotBlank(message = "Data source is required")
    @Size(max = 50)
    private String dataSource;

    @Column(name = "station_id", length = 20)
    @Size(max = 20)
    private String stationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_quality", length = 20)
    private DataQuality dataQuality = DataQuality.GOOD;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== ENUMS =====
    public enum DataQuality {
        EXCELLENT("Excellent"),
        GOOD("Good"),
        FAIR("Fair"),
        POOR("Poor");

        private final String displayName;

        DataQuality(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum WeatherCondition {
        SUNNY("Sunny"),
        PARTLY_CLOUDY("Partly Cloudy"),
        CLOUDY("Cloudy"),
        OVERCAST("Overcast"),
        LIGHT_RAIN("Light Rain"),
        RAIN("Rain"),
        HEAVY_RAIN("Heavy Rain"),
        THUNDERSTORM("Thunderstorm"),
        SNOW("Snow"),
        FOG("Fog"),
        WINDY("Windy"),
        HAIL("Hail"),
        DRIZZLE("Drizzle");

        private final String displayName;

        WeatherCondition(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum TemperatureRange {
        FREEZING("Freezing", -50.0, 0.0),
        COLD("Cold", 0.0, 10.0),
        COOL("Cool", 10.0, 20.0),
        MILD("Mild", 20.0, 25.0),
        WARM("Warm", 25.0, 30.0),
        HOT("Hot", 30.0, 35.0),
        VERY_HOT("Very Hot", 35.0, 60.0);

        private final String displayName;
        private final double minValue;
        private final double maxValue;

        TemperatureRange(String displayName, double minValue, double maxValue) {
            this.displayName = displayName;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static TemperatureRange fromValue(double temperature) {
            for (TemperatureRange range : TemperatureRange.values()) {
                if (temperature >= range.minValue && temperature < range.maxValue) {
                    return range;
                }
            }
            return VERY_HOT;
        }
    }

    // ===== CONSTRUCTORS =====
    public WeatherData() {
        // Don't generate ID here - let @PrePersist handle it
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.recordDate = LocalDateTime.now();
        this.dataQuality = DataQuality.GOOD;
    }

    public WeatherData(BigDecimal latitude, BigDecimal longitude, LocalDateTime recordDate) {
        this();
        this.latitude = latitude;
        this.longitude = longitude;
        this.recordDate = recordDate;
    }

    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append("WD");
        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 6);
        sb.append(shortTimestamp);
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // ===== JPA LIFECYCLE =====
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

    // ===== GETTERS AND SETTERS =====
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public LocalDateTime getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDateTime recordDate) { this.recordDate = recordDate; }

    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }

    public BigDecimal getTemperatureMin() { return temperatureMin; }
    public void setTemperatureMin(BigDecimal temperatureMin) { this.temperatureMin = temperatureMin; }

    public BigDecimal getTemperatureMax() { return temperatureMax; }
    public void setTemperatureMax(BigDecimal temperatureMax) { this.temperatureMax = temperatureMax; }

    public BigDecimal getHumidity() { return humidity; }
    public void setHumidity(BigDecimal humidity) { this.humidity = humidity; }

    public BigDecimal getRainfall() { return rainfall; }
    public void setRainfall(BigDecimal rainfall) { this.rainfall = rainfall; }

    public BigDecimal getWindSpeed() { return windSpeed; }
    public void setWindSpeed(BigDecimal windSpeed) { this.windSpeed = windSpeed; }

    public Integer getWindDirection() { return windDirection; }
    public void setWindDirection(Integer windDirection) { this.windDirection = windDirection; }

    public String getWeatherCondition() { return weatherCondition; }
    public void setWeatherCondition(String weatherCondition) { this.weatherCondition = weatherCondition; }

    public BigDecimal getSolarRadiation() { return solarRadiation; }
    public void setSolarRadiation(BigDecimal solarRadiation) { this.solarRadiation = solarRadiation; }

    public BigDecimal getEvapotranspiration() { return evapotranspiration; }
    public void setEvapotranspiration(BigDecimal evapotranspiration) { this.evapotranspiration = evapotranspiration; }

    public BigDecimal getAtmosphericPressure() { return atmosphericPressure; }
    public void setAtmosphericPressure(BigDecimal atmosphericPressure) { this.atmosphericPressure = atmosphericPressure; }

    public Integer getUvIndex() { return uvIndex; }
    public void setUvIndex(Integer uvIndex) { this.uvIndex = uvIndex; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public String getStationId() { return stationId; }
    public void setStationId(String stationId) { this.stationId = stationId; }

    public DataQuality getDataQuality() { return dataQuality; }
    public void setDataQuality(DataQuality dataQuality) { this.dataQuality = dataQuality; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ===== UTILITY METHODS =====
    public boolean isRainy() {
        return rainfall != null && rainfall.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isWindy() {
        return windSpeed != null && windSpeed.compareTo(new BigDecimal("20.0")) > 0;
    }

    public boolean isHot() {
        return temperature != null && temperature.compareTo(new BigDecimal("30.0")) > 0;
    }

    public boolean isCold() {
        return temperature != null && temperature.compareTo(new BigDecimal("10.0")) < 0;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "id='" + id + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", temperature=" + temperature +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherData that = (WeatherData) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}