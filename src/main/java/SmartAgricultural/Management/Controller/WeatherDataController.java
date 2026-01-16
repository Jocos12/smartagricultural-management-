package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.WeatherData;
import SmartAgricultural.Management.Model.WeatherData.DataQuality;
import SmartAgricultural.Management.Service.WeatherDataService;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.exception.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/weather-data")
@Validated
@CrossOrigin(origins = "*")
public class WeatherDataController {

    @Autowired
    private WeatherDataService weatherDataService;

    // =================================================================================
    // BASIC CRUD OPERATIONS
    // =================================================================================

    @PostMapping
    public ResponseEntity<?> createWeatherData(@Valid @RequestBody WeatherData weatherData) {
        try {
            System.out.println("=== Received Weather Data ===");
            System.out.println("Raw Data: " + weatherData);

            // Validate that required fields are not null
            if (weatherData.getLatitude() == null) {
                System.err.println("ERROR: Latitude is null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Validation Error", "Latitude is required and cannot be null"));
            }
            if (weatherData.getLongitude() == null) {
                System.err.println("ERROR: Longitude is null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Validation Error", "Longitude is required and cannot be null"));
            }
            if (weatherData.getTemperature() == null) {
                System.err.println("ERROR: Temperature is null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Validation Error", "Temperature is required and cannot be null"));
            }
            if (weatherData.getDataSource() == null || weatherData.getDataSource().trim().isEmpty()) {
                System.err.println("ERROR: Data source is null or empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Validation Error", "Data source is required and cannot be empty"));
            }

            // Set default record date if not provided
            if (weatherData.getRecordDate() == null) {
                weatherData.setRecordDate(LocalDateTime.now());
                System.out.println("Set default record date: " + weatherData.getRecordDate());
            }

            // Set default data quality if not provided
            if (weatherData.getDataQuality() == null) {
                weatherData.setDataQuality(WeatherData.DataQuality.GOOD);
                System.out.println("Set default data quality: GOOD");
            }

            System.out.println("Creating weather data with values:");
            System.out.println("  Latitude: " + weatherData.getLatitude());
            System.out.println("  Longitude: " + weatherData.getLongitude());
            System.out.println("  Temperature: " + weatherData.getTemperature());
            System.out.println("  Data Source: " + weatherData.getDataSource());
            System.out.println("  Data Quality: " + weatherData.getDataQuality());

            WeatherData created = weatherDataService.create(weatherData);

            System.out.println("Weather data created successfully with ID: " + created.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (ValidationException e) {
            System.err.println("Validation Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            System.err.println("IllegalArgument Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Invalid Data Format", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Unexpected Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error",
                            "Failed to create weather data: " + e.getMessage()));
        }
    }




    @GetMapping
    public ResponseEntity<?> getAllWeatherData(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "recordDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<WeatherData> weatherDataPage = weatherDataService.findAll(pageable);

            // Convert to a custom response to avoid PageImpl serialization warning
            Map<String, Object> response = new HashMap<>();
            response.put("content", weatherDataPage.getContent());
            response.put("totalElements", weatherDataPage.getTotalElements());
            response.put("totalPages", weatherDataPage.getTotalPages());
            response.put("currentPage", weatherDataPage.getNumber());
            response.put("pageSize", weatherDataPage.getSize());
            response.put("hasNext", weatherDataPage.hasNext());
            response.put("hasPrevious", weatherDataPage.hasPrevious());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve weather data"));
        }
    }




    @GetMapping("/{id}")
    public ResponseEntity<?> getWeatherDataById(@PathVariable @NotBlank String id) {
        try {
            WeatherData weatherData = weatherDataService.findById(id);
            return ResponseEntity.ok(weatherData);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve weather data"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWeatherData(
            @PathVariable @NotBlank String id,
            @Valid @RequestBody WeatherData weatherData) {
        try {
            WeatherData updated = weatherDataService.update(id, weatherData);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to update weather data"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWeatherData(@PathVariable @NotBlank String id) {
        try {
            weatherDataService.deleteById(id);
            return ResponseEntity.ok(createSuccessResponse("Weather data deleted successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to delete weather data"));
        }
    }


    @GetMapping("/count")
    public ResponseEntity<?> getWeatherDataCount() {
        try {
            long count = weatherDataService.count();
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get weather data count"));
        }
    }

    // =================================================================================
    // LOCATION-BASED OPERATIONS
    // =================================================================================

    @GetMapping("/location/radius")
    public ResponseEntity<?> getWeatherDataByLocationRadius(
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
            @RequestParam @DecimalMin("0.0") @DecimalMax("10.0") BigDecimal radius) {
        try {
            List<WeatherData> weatherData = weatherDataService.findByLocationRadius(latitude, longitude, radius);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve weather data by location"));
        }
    }

    @GetMapping("/location/bounds")
    public ResponseEntity<?> getWeatherDataByLocationBounds(
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal minLatitude,
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal maxLatitude,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal minLongitude,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal maxLongitude) {
        try {
            List<WeatherData> weatherData = weatherDataService.findByLocationBounds(
                    minLatitude, maxLatitude, minLongitude, maxLongitude);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve weather data by bounds"));
        }
    }

    @GetMapping("/location/latest")
    public ResponseEntity<?> getLatestWeatherDataByLocation(
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {
        try {
            List<WeatherData> weatherData = weatherDataService.findLatestByLocation(latitude, longitude, limit);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve latest weather data"));
        }
    }

    // =================================================================================
    // DATE-BASED OPERATIONS
    // =================================================================================

    @GetMapping("/date-range")
    public ResponseEntity<?> getWeatherDataByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<WeatherData> weatherData = weatherDataService.findByDateRange(start, end);
            return ResponseEntity.ok(weatherData);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Invalid Date", "Date format should be yyyy-MM-ddTHH:mm:ss"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve weather data by date range"));
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<?> getWeatherDataByDate(@PathVariable String date) {
        try {
            LocalDateTime recordDate = LocalDateTime.parse(date);
            List<WeatherData> weatherData = weatherDataService.findByRecordDate(recordDate);
            return ResponseEntity.ok(weatherData);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Invalid Date", "Date format should be yyyy-MM-ddTHH:mm:ss"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve weather data by date"));
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentWeatherData(
            @RequestParam(defaultValue = "7") @Min(1) @Max(365) int days) {
        try {
            List<WeatherData> weatherData = weatherDataService.findRecentWeatherData(days);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve recent weather data"));
        }
    }

    // =================================================================================
    // TEMPERATURE-BASED OPERATIONS
    // =================================================================================

    @GetMapping("/temperature/range")
    public ResponseEntity<?> getWeatherDataByTemperatureRange(
            @RequestParam @DecimalMin("-50.0") @DecimalMax("60.0") BigDecimal minTemp,
            @RequestParam @DecimalMin("-50.0") @DecimalMax("60.0") BigDecimal maxTemp) {
        try {
            List<WeatherData> weatherData = weatherDataService.findByTemperatureRange(minTemp, maxTemp);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve weather data by temperature"));
        }
    }

    @GetMapping("/temperature/hot")
    public ResponseEntity<?> getHotWeatherData(
            @RequestParam(defaultValue = "30.0") @DecimalMin("0.0") @DecimalMax("60.0") BigDecimal threshold) {
        try {
            List<WeatherData> weatherData = weatherDataService.findHotWeatherData(threshold);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve hot weather data"));
        }
    }

    @GetMapping("/temperature/cold")
    public ResponseEntity<?> getColdWeatherData(
            @RequestParam(defaultValue = "10.0") @DecimalMin("-50.0") @DecimalMax("30.0") BigDecimal threshold) {
        try {
            List<WeatherData> weatherData = weatherDataService.findColdWeatherData(threshold);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve cold weather data"));
        }
    }

    // =================================================================================
    // WEATHER CONDITION OPERATIONS
    // =================================================================================

    @GetMapping("/condition/{condition}")
    public ResponseEntity<?> getWeatherDataByCondition(@PathVariable @NotBlank String condition) {
        try {
            List<WeatherData> weatherData = weatherDataService.findByWeatherCondition(condition);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve weather data by condition"));
        }
    }

    @GetMapping("/condition/rainy")
    public ResponseEntity<?> getRainyWeatherData() {
        try {
            List<WeatherData> weatherData = weatherDataService.findRainyWeatherData();
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve rainy weather data"));
        }
    }

    @GetMapping("/condition/heavy-rain")
    public ResponseEntity<?> getHeavyRainData(
            @RequestParam(defaultValue = "25.0") @DecimalMin("0.0") BigDecimal threshold) {
        try {
            List<WeatherData> weatherData = weatherDataService.findHeavyRainData(threshold);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve heavy rain data"));
        }
    }

    @GetMapping("/condition/windy")
    public ResponseEntity<?> getWindyWeatherData(
            @RequestParam(defaultValue = "20.0") @DecimalMin("0.0") BigDecimal threshold) {
        try {
            List<WeatherData> weatherData = weatherDataService.findWindyWeatherData(threshold);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve windy weather data"));
        }
    }

    // =================================================================================
    // DATA SOURCE AND QUALITY OPERATIONS
    // =================================================================================

    @GetMapping("/source/{dataSource}")
    public ResponseEntity<?> getWeatherDataBySource(@PathVariable @NotBlank String dataSource) {
        try {
            List<WeatherData> weatherData = weatherDataService.findByDataSource(dataSource);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve weather data by source"));
        }
    }

    @GetMapping("/quality/{dataQuality}")
    public ResponseEntity<?> getWeatherDataByQuality(@PathVariable DataQuality dataQuality) {
        try {
            List<WeatherData> weatherData = weatherDataService.findByDataQuality(dataQuality);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve weather data by quality"));
        }
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<?> getWeatherDataByStationId(@PathVariable @NotBlank String stationId) {
        try {
            Optional<WeatherData> weatherData = weatherDataService.findByStationId(stationId);
            if (weatherData.isPresent()) {
                return ResponseEntity.ok(weatherData.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Not Found", "No weather data found for station: " + stationId));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve weather data by station"));
        }
    }

    @GetMapping("/station/{stationId}/latest")
    public ResponseEntity<?> getLatestWeatherDataByStation(
            @PathVariable @NotBlank String stationId,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {
        try {
            List<WeatherData> weatherData = weatherDataService.findLatestByStationId(stationId, limit);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve latest station data"));
        }
    }

    // =================================================================================
    // SEARCH AND FILTERING OPERATIONS
    // =================================================================================

    @GetMapping("/search")
    public ResponseEntity<?> searchWeatherData(@RequestParam @NotBlank String searchTerm) {
        try {
            List<WeatherData> weatherData = weatherDataService.searchWeatherData(searchTerm);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to search weather data"));
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getWeatherDataWithFilters(
            @RequestParam(required = false) String dataSource,
            @RequestParam(required = false) DataQuality dataQuality,
            @RequestParam(required = false) @DecimalMin("-50.0") @DecimalMax("60.0") BigDecimal minTemperature,
            @RequestParam(required = false) @DecimalMin("-50.0") @DecimalMax("60.0") BigDecimal maxTemperature,
            @RequestParam(required = false) @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal minHumidity,
            @RequestParam(required = false) @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal maxHumidity,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
            @RequestParam(required = false) @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
            @RequestParam(required = false) @DecimalMin("0.0") @DecimalMax("10.0") BigDecimal radius,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "recordDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
            LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;

            Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<WeatherData> weatherData = weatherDataService.findWithFilters(
                    dataSource, dataQuality, minTemperature, maxTemperature,
                    minHumidity, maxHumidity, start, end,
                    latitude, longitude, radius, pageable);
            return ResponseEntity.ok(weatherData);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Invalid Date", "Date format should be yyyy-MM-ddTHH:mm:ss"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to filter weather data"));
        }
    }

    // =================================================================================
    // ANALYTICS AND STATISTICS
    // =================================================================================

    @GetMapping("/analytics/statistics")
    public ResponseEntity<?> getWeatherStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            Map<String, Object> statistics = weatherDataService.getWeatherStatistics(start, end);
            return ResponseEntity.ok(statistics);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Invalid Date", "Date format should be yyyy-MM-ddTHH:mm:ss"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get weather statistics"));
        }
    }

    @GetMapping("/analytics/data-source-stats")
    public ResponseEntity<?> getDataSourceStatistics() {
        try {
            Map<String, Long> stats = weatherDataService.getDataSourceStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get data source statistics"));
        }
    }

    @GetMapping("/analytics/data-quality-stats")
    public ResponseEntity<?> getDataQualityStatistics() {
        try {
            Map<String, Long> stats = weatherDataService.getDataQualityStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get data quality statistics"));
        }
    }

    @GetMapping("/analytics/weather-condition-stats")
    public ResponseEntity<?> getWeatherConditionStatistics() {
        try {
            Map<String, Long> stats = weatherDataService.getWeatherConditionStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get weather condition statistics"));
        }
    }

    // =================================================================================
    // TREND ANALYSIS
    // =================================================================================

    @GetMapping("/analytics/trends")
    public ResponseEntity<?> getWeatherTrends(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<Map<String, Object>> trends = weatherDataService.getWeatherTrends(start, end);
            return ResponseEntity.ok(trends);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Invalid Date", "Date format should be yyyy-MM-ddTHH:mm:ss"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get weather trends"));
        }
    }

    @GetMapping("/analytics/monthly-stats")
    public ResponseEntity<?> getMonthlyWeatherStats() {
        try {
            List<Map<String, Object>> stats = weatherDataService.getMonthlyWeatherStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get monthly weather statistics"));
        }
    }

    @GetMapping("/analytics/daily-stats")
    public ResponseEntity<?> getDailyWeatherStats(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<Map<String, Object>> stats = weatherDataService.getDailyWeatherStats(start, end);
            return ResponseEntity.ok(stats);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Invalid Date", "Date format should be yyyy-MM-ddTHH:mm:ss"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get daily weather statistics"));
        }
    }

    // =================================================================================
    // EXTREME WEATHER AND ALERTS
    // =================================================================================

    @GetMapping("/extreme-weather")
    public ResponseEntity<?> getExtremeWeatherConditions(
            @RequestParam(defaultValue = "35.0") @DecimalMin("0.0") BigDecimal hotThreshold,
            @RequestParam(defaultValue = "0.0") @DecimalMax("30.0") BigDecimal coldThreshold,
            @RequestParam(defaultValue = "25.0") @DecimalMin("0.0") BigDecimal windThreshold,
            @RequestParam(defaultValue = "50.0") @DecimalMin("0.0") BigDecimal rainThreshold) {
        try {
            List<WeatherData> extremeWeather = weatherDataService.findExtremeWeatherConditions(
                    hotThreshold, coldThreshold, windThreshold, rainThreshold);
            return ResponseEntity.ok(extremeWeather);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve extreme weather conditions"));
        }
    }

    @GetMapping("/weather-alerts")
    public ResponseEntity<?> getWeatherAlerts(@RequestParam String since) {
        try {
            LocalDateTime sinceDateTime = LocalDateTime.parse(since);
            List<WeatherData> alerts = weatherDataService.findWeatherAlerts(sinceDateTime);
            return ResponseEntity.ok(alerts);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Invalid Date", "Date format should be yyyy-MM-ddTHH:mm:ss"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve weather alerts"));
        }
    }

    @GetMapping("/weather-alerts/recent")
    public ResponseEntity<?> getRecentWeatherAlerts(
            @RequestParam(defaultValue = "24") @Min(1) @Max(168) int hours) {
        try {
            List<WeatherData> alerts = weatherDataService.findRecentWeatherAlerts(hours);
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve recent weather alerts"));
        }
    }

    // =================================================================================
    // WEATHER INSIGHTS AND RECOMMENDATIONS
    // =================================================================================

    @GetMapping("/insights")
    public ResponseEntity<?> getWeatherInsights(
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
            @RequestParam(defaultValue = "7") @Min(1) @Max(30) int days) {
        try {
            Map<String, Object> insights = weatherDataService.getWeatherInsights(latitude, longitude, days);
            return ResponseEntity.ok(insights);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to generate weather insights"));
        }
    }

    @GetMapping("/location-averages")
    public ResponseEntity<?> getLocationBasedAverages() {
        try {
            List<Map<String, Object>> averages = weatherDataService.getLocationBasedAverages();
            return ResponseEntity.ok(averages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get location-based averages"));
        }
    }

    @GetMapping("/data-quality-by-source")
    public ResponseEntity<?> getDataQualityBySource() {
        try {
            List<Map<String, Object>> qualityData = weatherDataService.getDataQualityBySource();
            return ResponseEntity.ok(qualityData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get data quality by source"));
        }
    }

    // =================================================================================
    // BULK OPERATIONS
    // =================================================================================

    @PostMapping("/bulk")
    public ResponseEntity<?> createBulkWeatherData(@Valid @RequestBody List<WeatherData> weatherDataList) {
        try {
            List<WeatherData> created = weatherDataService.createWeatherDataBulk(weatherDataList);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bulk weather data created successfully");
            response.put("count", created.size());
            response.put("weatherData", created);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to create bulk weather data"));
        }
    }

    @PutMapping("/bulk")
    public ResponseEntity<?> updateBulkWeatherData(@Valid @RequestBody List<WeatherData> weatherDataList) {
        try {
            List<WeatherData> updated = weatherDataService.updateWeatherDataBulk(weatherDataList);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bulk weather data updated successfully");
            response.put("count", updated.size());
            response.put("weatherData", updated);
            return ResponseEntity.ok(response);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to update bulk weather data"));
        }
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<?> deleteBulkWeatherData(@RequestBody List<String> ids) {
        try {
            weatherDataService.deleteWeatherDataBulk(ids);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bulk weather data deleted successfully");
            response.put("count", ids.size());
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to delete bulk weather data"));
        }
    }

    // =================================================================================
    // MAINTENANCE OPERATIONS
    // =================================================================================

    @DeleteMapping("/maintenance/cleanup-poor-quality")
    public ResponseEntity<?> cleanupPoorQualityOldData(
            @RequestParam(defaultValue = "30") @Min(1) @Max(365) int daysOld) {
        try {
            int deletedCount = weatherDataService.cleanupPoorQualityOldData(daysOld);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Poor quality old weather data cleaned up successfully");
            response.put("deletedCount", deletedCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to cleanup poor quality data"));
        }
    }

    @DeleteMapping("/maintenance/cleanup-old-data")
    public ResponseEntity<?> cleanupOldWeatherData(
            @RequestParam(defaultValue = "365") @Min(30) @Max(1095) int daysOld) {
        try {
            int deletedCount = weatherDataService.cleanupOldWeatherData(daysOld);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Old weather data cleaned up successfully");
            response.put("deletedCount", deletedCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to cleanup old weather data"));
        }
    }

    // =================================================================================
    // UTILITY METHODS
    // =================================================================================

    private Map<String, Object> createErrorResponse(String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    // =================================================================================
    // EXCEPTION HANDLERS
    // =================================================================================

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("Validation Error", e.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("Resource Not Found", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Internal Server Error", "An unexpected error occurred"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("Invalid Argument", e.getMessage()));
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<?> handleDateTimeParseException(DateTimeParseException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("Invalid Date Format", "Date format should be yyyy-MM-ddTHH:mm:ss"));
    }

    // =================================================================================
    // HEALTH CHECK
    // =================================================================================

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            long totalRecords = weatherDataService.count();
            Map<String, Object> health = new HashMap<>();
            health.put("status", "UP");
            health.put("service", "WeatherDataService");
            health.put("totalRecords", totalRecords);
            health.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            Map<String, Object> health = new HashMap<>();
            health.put("status", "DOWN");
            health.put("service", "WeatherDataService");
            health.put("error", e.getMessage());
            health.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(health);
        }
    }



    // =================================================================================
// AI PREDICTION INTEGRATION
// =================================================================================

    @GetMapping("/analytics/prediction-data")
    public ResponseEntity<?> getPredictionTrainingData(
            @RequestParam(defaultValue = "90") @Min(1) @Max(365) int days) {
        try {
            List<WeatherData> trainingData = weatherDataService.getTrainingDataForPrediction(days);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", trainingData);
            response.put("count", trainingData.size());
            response.put("daysIncluded", days);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get prediction training data"));
        }
    }

    @GetMapping("/analytics/temperature-history")
    public ResponseEntity<?> getTemperatureHistory(
            @RequestParam(defaultValue = "30") @Min(1) @Max(180) int days) {
        try {
            List<Map<String, Object>> history = weatherDataService.getTemperatureHistory(days);
            return ResponseEntity.ok(createSuccessResponse("Temperature history retrieved", history));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get temperature history"));
        }
    }

    @PostMapping("/analytics/generate-predictions")
    public ResponseEntity<?> generateWeatherPredictions(
            @RequestParam(defaultValue = "7") @Min(1) @Max(30) int days,
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude) {
        try {
            Map<String, Object> predictions = weatherDataService.generatePredictions(days, latitude, longitude);
            return ResponseEntity.ok(createSuccessResponse("Predictions generated", predictions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to generate predictions: " + e.getMessage()));
        }
    }

// =================================================================================
// FOOD SECURITY INTEGRATION
// =================================================================================

    @GetMapping("/analytics/food-security-risk")
    public ResponseEntity<?> analyzeFoodSecurityRisk(
            @RequestParam(defaultValue = "30") @Min(1) @Max(90) int days) {
        try {
            Map<String, Object> riskAnalysis = weatherDataService.analyzeFoodSecurityRisk(days);
            return ResponseEntity.ok(createSuccessResponse("Risk analysis completed", riskAnalysis));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to analyze food security risk"));
        }
    }

    @GetMapping("/analytics/drought-risk")
    public ResponseEntity<?> assessDroughtRisk(
            @RequestParam(defaultValue = "30") @Min(1) @Max(90) int days,
            @RequestParam(required = false) BigDecimal latitude,
            @RequestParam(required = false) BigDecimal longitude) {
        try {
            Map<String, Object> droughtRisk = weatherDataService.assessDroughtRisk(days, latitude, longitude);
            return ResponseEntity.ok(createSuccessResponse("Drought risk assessed", droughtRisk));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to assess drought risk"));
        }
    }

    @GetMapping("/analytics/flood-risk")
    public ResponseEntity<?> assessFloodRisk(
            @RequestParam(defaultValue = "7") @Min(1) @Max(30) int days,
            @RequestParam(required = false) BigDecimal latitude,
            @RequestParam(required = false) BigDecimal longitude) {
        try {
            Map<String, Object> floodRisk = weatherDataService.assessFloodRisk(days, latitude, longitude);
            return ResponseEntity.ok(createSuccessResponse("Flood risk assessed", floodRisk));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to assess flood risk"));
        }
    }

    @GetMapping("/analytics/crop-stress-indicators")
    public ResponseEntity<?> getCropStressIndicators(
            @RequestParam(defaultValue = "14") @Min(1) @Max(60) int days) {
        try {
            Map<String, Object> indicators = weatherDataService.getCropStressIndicators(days);
            return ResponseEntity.ok(createSuccessResponse("Crop stress indicators retrieved", indicators));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get crop stress indicators"));
        }
    }

    @PostMapping("/analytics/check-alert-triggers")
    public ResponseEntity<?> checkAlertTriggers() {
        try {
            List<Map<String, Object>> triggers = weatherDataService.checkWeatherAlertTriggers();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("triggeredAlerts", triggers);
            response.put("count", triggers.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to check alert triggers"));
        }
    }
}