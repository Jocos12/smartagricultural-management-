package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.WeatherData;
import SmartAgricultural.Management.Model.WeatherData.DataQuality;
import SmartAgricultural.Management.Repository.WeatherDataRepository;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.exception.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class WeatherDataService {

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    // =================================================================================
    // BASIC CRUD OPERATIONS
    // =================================================================================

    public WeatherData save(WeatherData weatherData) {
        validateWeatherData(weatherData);
        return weatherDataRepository.save(weatherData);
    }

    public WeatherData create(WeatherData weatherData) {
        if (weatherData.getId() != null) {
            throw new ValidationException("Cannot create weather data with existing ID");
        }
        return save(weatherData);
    }

    public WeatherData update(String id, WeatherData weatherData) {
        WeatherData existing = findById(id);
        updateWeatherDataFields(existing, weatherData);
        return save(existing);
    }

    @Transactional(readOnly = true)
    public WeatherData findById(String id) {
        return weatherDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Weather data not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<WeatherData> findByIdOptional(String id) {
        return weatherDataRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<WeatherData> findAll() {
        return weatherDataRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<WeatherData> findAll(Pageable pageable) {
        return weatherDataRepository.findAll(pageable);
    }

    public void deleteById(String id) {
        if (!weatherDataRepository.existsById(id)) {
            throw new ResourceNotFoundException("Weather data not found with id: " + id);
        }
        weatherDataRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(String id) {
        return weatherDataRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return weatherDataRepository.count();
    }

    // =================================================================================
    // LOCATION-BASED OPERATIONS
    // =================================================================================

    @Transactional(readOnly = true)
    public List<WeatherData> findByLocationRadius(BigDecimal latitude, BigDecimal longitude, BigDecimal radius) {
        return weatherDataRepository.findByLocationRadius(latitude, longitude, radius);
    }

    @Transactional(readOnly = true)
    public List<WeatherData> findByLocationBounds(BigDecimal minLatitude, BigDecimal maxLatitude,
                                                  BigDecimal minLongitude, BigDecimal maxLongitude) {
        return weatherDataRepository.findByLocationBounds(minLatitude, maxLatitude, minLongitude, maxLongitude);
    }

    @Transactional(readOnly = true)
    public List<WeatherData> findLatestByLocation(BigDecimal latitude, BigDecimal longitude, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return weatherDataRepository.findLatestByLocation(latitude, longitude, pageable);
    }

    // =================================================================================
    // DATE-BASED OPERATIONS
    // =================================================================================

    @Transactional(readOnly = true)
    public List<WeatherData> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return weatherDataRepository.findByRecordDateBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<WeatherData> findByRecordDate(LocalDateTime date) {
        return weatherDataRepository.findByRecordDate(date);
    }

    @Transactional(readOnly = true)
    public List<WeatherData> findRecentWeatherData(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return weatherDataRepository.findRecentWeatherData(since);
    }

    // =================================================================================
    // TEMPERATURE-BASED OPERATIONS
    // =================================================================================

    @Transactional(readOnly = true)
    public List<WeatherData> findByTemperatureRange(BigDecimal minTemp, BigDecimal maxTemp) {
        return weatherDataRepository.findByTemperatureBetween(minTemp, maxTemp);
    }

    @Transactional(readOnly = true)
    public List<WeatherData> findHotWeatherData(BigDecimal threshold) {
        return weatherDataRepository.findHotWeatherData(threshold);
    }

    @Transactional(readOnly = true)
    public List<WeatherData> findColdWeatherData(BigDecimal threshold) {
        return weatherDataRepository.findColdWeatherData(threshold);
    }

    // =================================================================================
    // WEATHER CONDITIONS OPERATIONS
    // =================================================================================

    @Transactional(readOnly = true)
    public List<WeatherData> findByWeatherCondition(String condition) {
        return weatherDataRepository.findByWeatherCondition(condition);
    }

    @Transactional(readOnly = true)
    public List<WeatherData> findRainyWeatherData() {
        return weatherDataRepository.findRainyWeatherData();
    }

    @Transactional(readOnly = true)
    public List<WeatherData> findHeavyRainData(BigDecimal threshold) {
        return weatherDataRepository.findHeavyRainData(threshold);
    }

    @Transactional(readOnly = true)
    public List<WeatherData> findWindyWeatherData(BigDecimal threshold) {
        return weatherDataRepository.findWindyWeatherData(threshold);
    }

    // =================================================================================
    // DATA SOURCE AND QUALITY OPERATIONS
    // =================================================================================

    @Transactional(readOnly = true)
    public List<WeatherData> findByDataSource(String dataSource) {
        return weatherDataRepository.findByDataSource(dataSource);
    }

    @Transactional(readOnly = true)
    public List<WeatherData> findByDataQuality(DataQuality dataQuality) {
        return weatherDataRepository.findByDataQuality(dataQuality);
    }

    @Transactional(readOnly = true)
    public Optional<WeatherData> findByStationId(String stationId) {
        return weatherDataRepository.findByStationId(stationId);
    }

    @Transactional(readOnly = true)
    public List<WeatherData> findLatestByStationId(String stationId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return weatherDataRepository.findLatestByStationId(stationId, pageable);
    }

    // =================================================================================
    // SEARCH AND FILTERING OPERATIONS
    // =================================================================================

    @Transactional(readOnly = true)
    public List<WeatherData> searchWeatherData(String searchTerm) {
        if (!StringUtils.hasText(searchTerm)) {
            return findAll();
        }
        return weatherDataRepository.searchByTerm(searchTerm.trim());
    }

    @Transactional(readOnly = true)
    public Page<WeatherData> findWithFilters(
            String dataSource,
            DataQuality dataQuality,
            BigDecimal minTemperature,
            BigDecimal maxTemperature,
            BigDecimal minHumidity,
            BigDecimal maxHumidity,
            LocalDateTime startDate,
            LocalDateTime endDate,
            BigDecimal latitude,
            BigDecimal longitude,
            BigDecimal radius,
            Pageable pageable) {

        return weatherDataRepository.findWithFilters(
                dataSource, dataQuality, minTemperature, maxTemperature,
                minHumidity, maxHumidity, startDate, endDate,
                latitude, longitude, radius, pageable);
    }

    // =================================================================================
    // STATISTICAL OPERATIONS
    // =================================================================================

    @Transactional(readOnly = true)
    public Map<String, Object> getWeatherStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> stats = new HashMap<>();

        BigDecimal avgTemp = weatherDataRepository.getAverageTemperature(startDate, endDate);
        BigDecimal maxTemp = weatherDataRepository.getMaxTemperature(startDate, endDate);
        BigDecimal minTemp = weatherDataRepository.getMinTemperature(startDate, endDate);
        BigDecimal avgHumidity = weatherDataRepository.getAverageHumidity(startDate, endDate);
        BigDecimal totalRainfall = weatherDataRepository.getTotalRainfall(startDate, endDate);
        BigDecimal avgWindSpeed = weatherDataRepository.getAverageWindSpeed(startDate, endDate);

        stats.put("averageTemperature", avgTemp != null ? avgTemp.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        stats.put("maxTemperature", maxTemp != null ? maxTemp : BigDecimal.ZERO);
        stats.put("minTemperature", minTemp != null ? minTemp : BigDecimal.ZERO);
        stats.put("averageHumidity", avgHumidity != null ? avgHumidity.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        stats.put("totalRainfall", totalRainfall != null ? totalRainfall.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        stats.put("averageWindSpeed", avgWindSpeed != null ? avgWindSpeed.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        long totalRecords = weatherDataRepository.countByRecordDateBetween(startDate, endDate);
        long rainyDays = weatherDataRepository.countRainyDays();
        long hotDays = weatherDataRepository.countHotDays(new BigDecimal("30.0"));
        long coldDays = weatherDataRepository.countColdDays(new BigDecimal("10.0"));

        stats.put("totalRecords", totalRecords);
        stats.put("rainyDays", rainyDays);
        stats.put("hotDays", hotDays);
        stats.put("coldDays", coldDays);

        return stats;
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getDataSourceStatistics() {
        List<Object[]> results = weatherDataRepository.countByDataSourceGrouped();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]
                ));
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getDataQualityStatistics() {
        List<Object[]> results = weatherDataRepository.countByDataQualityGrouped();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> ((DataQuality) result[0]).getDisplayName(),
                        result -> (Long) result[1]
                ));
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getWeatherConditionStatistics() {
        List<Object[]> results = weatherDataRepository.countByWeatherConditionGrouped();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]
                ));
    }

    // =================================================================================
    // TREND ANALYSIS OPERATIONS
    // =================================================================================

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getWeatherTrends(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = weatherDataRepository.getWeatherTrends(startDate, endDate);

        return results.stream()
                .map(result -> {
                    Map<String, Object> trend = new HashMap<>();
                    trend.put("date", result[0]);
                    trend.put("averageTemperature", result[1]);
                    trend.put("averageHumidity", result[2]);
                    trend.put("totalRainfall", result[3]);
                    return trend;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMonthlyWeatherStats() {
        List<Object[]> results = weatherDataRepository.getMonthlyWeatherDataCount();

        return results.stream()
                .map(result -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("year", result[0]);
                    stat.put("month", result[1]);
                    stat.put("recordCount", result[2]);
                    return stat;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDailyWeatherStats(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = weatherDataRepository.getDailyWeatherDataCount(startDate, endDate);

        return results.stream()
                .map(result -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("date", result[0]);
                    stat.put("recordCount", result[1]);
                    return stat;
                })
                .collect(Collectors.toList());
    }

    // =================================================================================
    // EXTREME WEATHER OPERATIONS
    // =================================================================================

    @Transactional(readOnly = true)
    public List<WeatherData> findExtremeWeatherConditions(
            BigDecimal hotThreshold, BigDecimal coldThreshold,
            BigDecimal windThreshold, BigDecimal rainThreshold) {
        return weatherDataRepository.findExtremeWeatherConditions(
                hotThreshold, coldThreshold, windThreshold, rainThreshold);
    }

    @Transactional(readOnly = true)
    public List<WeatherData> findWeatherAlerts(LocalDateTime since) {
        return weatherDataRepository.findWeatherAlerts(since);
    }

    @Transactional(readOnly = true)
    public List<WeatherData> findRecentWeatherAlerts(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return findWeatherAlerts(since);
    }

    // =================================================================================
    // BULK OPERATIONS
    // =================================================================================

    public List<WeatherData> createWeatherDataBulk(List<WeatherData> weatherDataList) {
        for (WeatherData weatherData : weatherDataList) {
            validateWeatherData(weatherData);
            if (weatherData.getId() != null) {
                throw new ValidationException("Cannot create weather data with existing ID");
            }
        }
        return weatherDataRepository.saveAll(weatherDataList);
    }

    public List<WeatherData> updateWeatherDataBulk(List<WeatherData> weatherDataList) {
        List<WeatherData> updatedWeatherData = weatherDataList.stream()
                .map(weatherData -> {
                    if (weatherData.getId() == null) {
                        throw new ValidationException("Cannot update weather data without ID");
                    }
                    validateWeatherData(weatherData);
                    return weatherData;
                })
                .collect(Collectors.toList());

        return weatherDataRepository.saveAll(updatedWeatherData);
    }

    public void deleteWeatherDataBulk(List<String> ids) {
        List<WeatherData> weatherDataList = ids.stream()
                .map(this::findById)
                .collect(Collectors.toList());

        weatherDataRepository.deleteAll(weatherDataList);
    }

    // =================================================================================
    // MAINTENANCE OPERATIONS
    // =================================================================================

    public int cleanupPoorQualityOldData(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        return weatherDataRepository.deletePoorQualityOldData(cutoffDate);
    }

    public int cleanupOldWeatherData(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        return weatherDataRepository.deleteOldWeatherData(cutoffDate);
    }

    // =================================================================================
    // LOCATION-BASED ANALYTICS
    // =================================================================================

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLocationBasedAverages() {
        List<Object[]> results = weatherDataRepository.getLocationBasedAverages();

        return results.stream()
                .map(result -> {
                    Map<String, Object> location = new HashMap<>();
                    location.put("latitude", result[0]);
                    location.put("longitude", result[1]);
                    location.put("averageTemperature", result[2]);
                    location.put("averageHumidity", result[3]);
                    return location;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDataQualityBySource() {
        List<Object[]> results = weatherDataRepository.getDataQualityBySource();

        return results.stream()
                .map(result -> {
                    Map<String, Object> quality = new HashMap<>();
                    quality.put("dataSource", result[0]);
                    quality.put("dataQuality", ((DataQuality) result[1]).getDisplayName());
                    quality.put("count", result[2]);
                    return quality;
                })
                .collect(Collectors.toList());
    }

    // =================================================================================
    // WEATHER ANALYSIS AND INSIGHTS
    // =================================================================================

    @Transactional(readOnly = true)
    public Map<String, Object> getWeatherInsights(BigDecimal latitude, BigDecimal longitude, int days) {
        Map<String, Object> insights = new HashMap<>();
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        LocalDateTime now = LocalDateTime.now();

        // Get weather data for the location and time period
        BigDecimal radius = new BigDecimal("0.1"); // 0.1 degree radius
        List<WeatherData> locationData = findByLocationRadius(latitude, longitude, radius);
        List<WeatherData> recentData = locationData.stream()
                .filter(w -> w.getRecordDate().isAfter(since))
                .collect(Collectors.toList());

        if (recentData.isEmpty()) {
            insights.put("message", "No weather data available for this location and time period");
            return insights;
        }

        // Temperature analysis
        BigDecimal avgTemp = recentData.stream()
                .filter(w -> w.getTemperature() != null)
                .map(WeatherData::getTemperature)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(recentData.size()), 2, RoundingMode.HALF_UP);

        BigDecimal maxTemp = recentData.stream()
                .filter(w -> w.getTemperature() != null)
                .map(WeatherData::getTemperature)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal minTemp = recentData.stream()
                .filter(w -> w.getTemperature() != null)
                .map(WeatherData::getTemperature)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        insights.put("averageTemperature", avgTemp);
        insights.put("maxTemperature", maxTemp);
        insights.put("minTemperature", minTemp);
        insights.put("temperatureRange", maxTemp.subtract(minTemp));

        // Rainfall analysis
        BigDecimal totalRainfall = recentData.stream()
                .filter(w -> w.getRainfall() != null)
                .map(WeatherData::getRainfall)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long rainyDays = recentData.stream()
                .mapToLong(w -> w.isRainy() ? 1 : 0)
                .sum();

        insights.put("totalRainfall", totalRainfall);
        insights.put("rainyDays", rainyDays);

        // Weather conditions analysis
        Map<String, Long> conditionCounts = recentData.stream()
                .filter(w -> w.getWeatherCondition() != null)
                .collect(Collectors.groupingBy(
                        WeatherData::getWeatherCondition,
                        Collectors.counting()
                ));

        insights.put("weatherConditions", conditionCounts);

        // Dominant weather condition
        String dominantCondition = conditionCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");

        insights.put("dominantWeatherCondition", dominantCondition);

        // Weather recommendations
        List<String> recommendations = generateWeatherRecommendations(avgTemp, totalRainfall, rainyDays, days);
        insights.put("recommendations", recommendations);

        return insights;
    }

    private List<String> generateWeatherRecommendations(BigDecimal avgTemp, BigDecimal totalRainfall, long rainyDays, int days) {
        List<String> recommendations = new java.util.ArrayList<>();

        if (avgTemp.compareTo(new BigDecimal("30")) > 0) {
            recommendations.add("High temperatures detected - ensure adequate irrigation and consider heat-resistant crops");
        }

        if (avgTemp.compareTo(new BigDecimal("10")) < 0) {
            recommendations.add("Low temperatures detected - protect sensitive crops from frost");
        }

        if (totalRainfall.compareTo(new BigDecimal("100")) > 0) {
            recommendations.add("High rainfall detected - monitor for waterlogging and fungal diseases");
        }

        if (totalRainfall.compareTo(new BigDecimal("10")) < 0 && days > 7) {
            recommendations.add("Low rainfall detected - consider supplemental irrigation");
        }

        double rainyDayPercentage = (double) rainyDays / days * 100;
        if (rainyDayPercentage > 60) {
            recommendations.add("Frequent rain detected - plan field activities accordingly");
        }

        if (recommendations.isEmpty()) {
            recommendations.add("Weather conditions appear favorable for agricultural activities");
        }

        return recommendations;
    }

    // =================================================================================
    // VALIDATION METHODS
    // =================================================================================



    private void validateWeatherData(WeatherData weatherData) {
        List<String> errors = new ArrayList<>();

        if (weatherData.getLatitude() == null) {
            errors.add("Latitude is required");
        } else {
            if (weatherData.getLatitude().compareTo(new BigDecimal("-90")) < 0 ||
                    weatherData.getLatitude().compareTo(new BigDecimal("90")) > 0) {
                errors.add("Latitude must be between -90 and 90");
            }
        }

        if (weatherData.getLongitude() == null) {
            errors.add("Longitude is required");
        } else {
            if (weatherData.getLongitude().compareTo(new BigDecimal("-180")) < 0 ||
                    weatherData.getLongitude().compareTo(new BigDecimal("180")) > 0) {
                errors.add("Longitude must be between -180 and 180");
            }
        }

        if (weatherData.getRecordDate() == null) {
            errors.add("Record date is required");
        } else {
            if (weatherData.getRecordDate().isAfter(LocalDateTime.now().plusHours(1))) {
                errors.add("Record date cannot be in the future");
            }
        }

        if (!StringUtils.hasText(weatherData.getDataSource())) {
            errors.add("Data source is required");
        }

        if (weatherData.getTemperature() == null) {
            errors.add("Temperature is required");
        } else {
            BigDecimal temp = weatherData.getTemperature();
            if (temp.compareTo(new BigDecimal("-50")) < 0 || temp.compareTo(new BigDecimal("60")) > 0) {
                errors.add("Temperature must be between -50°C and 60°C");
            }
        }

        if (weatherData.getHumidity() != null) {
            BigDecimal humidity = weatherData.getHumidity();
            if (humidity.compareTo(BigDecimal.ZERO) < 0 || humidity.compareTo(new BigDecimal("100")) > 0) {
                errors.add("Humidity must be between 0 and 100%");
            }
        }

        if (weatherData.getRainfall() != null && weatherData.getRainfall().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Rainfall cannot be negative");
        }

        if (weatherData.getWindSpeed() != null) {
            if (weatherData.getWindSpeed().compareTo(BigDecimal.ZERO) < 0) {
                errors.add("Wind speed cannot be negative");
            }
            if (weatherData.getWindSpeed().compareTo(new BigDecimal("200")) > 0) {
                errors.add("Wind speed seems unrealistic (max 200 km/h)");
            }
        }

        if (weatherData.getWindDirection() != null) {
            Integer direction = weatherData.getWindDirection();
            if (direction < 0 || direction > 360) {
                errors.add("Wind direction must be between 0 and 360 degrees");
            }
        }

        if (weatherData.getAtmosphericPressure() != null) {
            BigDecimal pressure = weatherData.getAtmosphericPressure();
            if (pressure.compareTo(new BigDecimal("800")) < 0 || pressure.compareTo(new BigDecimal("1200")) > 0) {
                errors.add("Atmospheric pressure must be between 800 and 1200 hPa");
            }
        }

        if (weatherData.getUvIndex() != null) {
            Integer uvIndex = weatherData.getUvIndex();
            if (uvIndex < 0 || uvIndex > 15) {
                errors.add("UV index must be between 0 and 15");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed: " + String.join("; ", errors));
        }
    }




    private void updateWeatherDataFields(WeatherData existing, WeatherData updated) {
        if (updated.getLatitude() != null) {
            existing.setLatitude(updated.getLatitude());
        }

        if (updated.getLongitude() != null) {
            existing.setLongitude(updated.getLongitude());
        }

        if (updated.getRecordDate() != null) {
            existing.setRecordDate(updated.getRecordDate());
        }

        if (updated.getTemperature() != null) {
            existing.setTemperature(updated.getTemperature());
        }

        if (updated.getTemperatureMin() != null) {
            existing.setTemperatureMin(updated.getTemperatureMin());
        }

        if (updated.getTemperatureMax() != null) {
            existing.setTemperatureMax(updated.getTemperatureMax());
        }

        if (updated.getHumidity() != null) {
            existing.setHumidity(updated.getHumidity());
        }

        if (updated.getRainfall() != null) {
            existing.setRainfall(updated.getRainfall());
        }

        if (updated.getWindSpeed() != null) {
            existing.setWindSpeed(updated.getWindSpeed());
        }

        if (updated.getWindDirection() != null) {
            existing.setWindDirection(updated.getWindDirection());
        }

        if (StringUtils.hasText(updated.getWeatherCondition())) {
            existing.setWeatherCondition(updated.getWeatherCondition());
        }

        if (updated.getSolarRadiation() != null) {
            existing.setSolarRadiation(updated.getSolarRadiation());
        }

        if (updated.getEvapotranspiration() != null) {
            existing.setEvapotranspiration(updated.getEvapotranspiration());
        }

        if (updated.getAtmosphericPressure() != null) {
            existing.setAtmosphericPressure(updated.getAtmosphericPressure());
        }

        if (updated.getUvIndex() != null) {
            existing.setUvIndex(updated.getUvIndex());
        }

        if (StringUtils.hasText(updated.getDataSource())) {
            existing.setDataSource(updated.getDataSource());
        }

        if (StringUtils.hasText(updated.getStationId())) {
            existing.setStationId(updated.getStationId());
        }

        if (updated.getDataQuality() != null) {
            existing.setDataQuality(updated.getDataQuality());
        }
    }





    // =================================================================================
// AI PREDICTION SUPPORT METHODS
// =================================================================================

    @Transactional(readOnly = true)
    public List<WeatherData> getTrainingDataForPrediction(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<WeatherData> data = weatherDataRepository.findRecentWeatherData(since);

        // Sort by date and filter valid temperature data
        return data.stream()
                .filter(w -> w.getTemperature() != null)
                .filter(w -> w.getRecordDate() != null)
                .sorted(Comparator.comparing(WeatherData::getRecordDate))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTemperatureHistory(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<WeatherData> data = weatherDataRepository.findRecentWeatherData(since);

        return data.stream()
                .filter(w -> w.getTemperature() != null)
                .sorted(Comparator.comparing(WeatherData::getRecordDate))
                .map(w -> {
                    Map<String, Object> point = new HashMap<>();
                    point.put("date", w.getRecordDate());
                    point.put("temperature", w.getTemperature());
                    point.put("humidity", w.getHumidity());
                    point.put("rainfall", w.getRainfall());
                    return point;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> generatePredictions(int days, BigDecimal latitude, BigDecimal longitude) {
        Map<String, Object> result = new HashMap<>();

        // Get historical data for the location
        BigDecimal radius = new BigDecimal("0.5");
        List<WeatherData> locationData = findByLocationRadius(latitude, longitude, radius);

        // Get last 60 days of data for pattern analysis
        LocalDateTime since = LocalDateTime.now().minusDays(60);
        List<WeatherData> recentData = locationData.stream()
                .filter(w -> w.getRecordDate().isAfter(since))
                .filter(w -> w.getTemperature() != null)
                .sorted(Comparator.comparing(WeatherData::getRecordDate))
                .collect(Collectors.toList());

        if (recentData.size() < 10) {
            result.put("error", "Insufficient data for predictions");
            result.put("dataPoints", recentData.size());
            return result;
        }

        // Calculate temperature trends
        List<BigDecimal> temperatures = recentData.stream()
                .map(WeatherData::getTemperature)
                .collect(Collectors.toList());

        BigDecimal avgTemp = temperatures.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(temperatures.size()), 2, RoundingMode.HALF_UP);

        // Calculate trend (simple linear regression slope)
        BigDecimal trend = calculateTrend(temperatures);

        // Generate predictions
        List<Map<String, Object>> predictions = new ArrayList<>();
        for (int i = 1; i <= days; i++) {
            Map<String, Object> prediction = new HashMap<>();

            // Simple prediction: avg + trend * day + seasonal variation
            BigDecimal predictedTemp = avgTemp
                    .add(trend.multiply(new BigDecimal(i)))
                    .add(getSeasonalAdjustment(i));

            prediction.put("day", i);
            prediction.put("date", LocalDateTime.now().plusDays(i));
            prediction.put("temperature", predictedTemp.setScale(1, RoundingMode.HALF_UP));
            prediction.put("confidence", calculateConfidence(i, recentData.size()));

            predictions.add(prediction);
        }

        result.put("predictions", predictions);
        result.put("baselineTemp", avgTemp);
        result.put("trend", trend);
        result.put("dataPoints", recentData.size());
        result.put("location", Map.of("latitude", latitude, "longitude", longitude));

        return result;
    }

    private BigDecimal calculateTrend(List<BigDecimal> values) {
        if (values.size() < 2) return BigDecimal.ZERO;

        int n = values.size();
        BigDecimal sumX = BigDecimal.ZERO;
        BigDecimal sumY = BigDecimal.ZERO;
        BigDecimal sumXY = BigDecimal.ZERO;
        BigDecimal sumX2 = BigDecimal.ZERO;

        for (int i = 0; i < n; i++) {
            BigDecimal x = new BigDecimal(i);
            BigDecimal y = values.get(i);

            sumX = sumX.add(x);
            sumY = sumY.add(y);
            sumXY = sumXY.add(x.multiply(y));
            sumX2 = sumX2.add(x.multiply(x));
        }

        BigDecimal nBig = new BigDecimal(n);
        BigDecimal numerator = nBig.multiply(sumXY).subtract(sumX.multiply(sumY));
        BigDecimal denominator = nBig.multiply(sumX2).subtract(sumX.multiply(sumX));

        if (denominator.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        return numerator.divide(denominator, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal getSeasonalAdjustment(int daysAhead) {
        // Simple seasonal adjustment based on day of year
        LocalDateTime futureDate = LocalDateTime.now().plusDays(daysAhead);
        int dayOfYear = futureDate.getDayOfYear();

        // Simplified seasonal pattern (adjust for your region)
        double seasonalFactor = Math.sin((dayOfYear / 365.0) * 2 * Math.PI) * 2.0;
        return new BigDecimal(seasonalFactor).setScale(2, RoundingMode.HALF_UP);
    }

    private double calculateConfidence(int daysAhead, int dataPoints) {
        // Confidence decreases with prediction distance and increases with data
        double baseConfidence = Math.min(0.95, dataPoints / 100.0);
        double distancePenalty = Math.exp(-daysAhead / 10.0);
        return baseConfidence * distancePenalty;
    }

// =================================================================================
// FOOD SECURITY RISK ANALYSIS
// =================================================================================

    @Transactional(readOnly = true)
    public Map<String, Object> analyzeFoodSecurityRisk(int days) {
        Map<String, Object> analysis = new HashMap<>();
        LocalDateTime since = LocalDateTime.now().minusDays(days);

        List<WeatherData> recentData = weatherDataRepository.findRecentWeatherData(since);

        if (recentData.isEmpty()) {
            analysis.put("error", "No data available");
            return analysis;
        }

        // Analyze different risk factors
        Map<String, Object> droughtRisk = analyzeDrought(recentData);
        Map<String, Object> floodRisk = analyzeFlood(recentData);
        Map<String, Object> heatStress = analyzeHeatStress(recentData);
        Map<String, Object> cropConditions = analyzeCropConditions(recentData);

        // Calculate overall risk score (0-100)
        int overallRisk = calculateOverallRisk(droughtRisk, floodRisk, heatStress, cropConditions);

        analysis.put("overallRiskScore", overallRisk);
        analysis.put("riskLevel", getRiskLevel(overallRisk));
        analysis.put("droughtRisk", droughtRisk);
        analysis.put("floodRisk", floodRisk);
        analysis.put("heatStressRisk", heatStress);
        analysis.put("cropConditions", cropConditions);
        analysis.put("analyzedPeriod", days);
        analysis.put("dataPoints", recentData.size());

        // Generate recommendations
        List<String> recommendations = generateRiskRecommendations(
                droughtRisk, floodRisk, heatStress, cropConditions);
        analysis.put("recommendations", recommendations);

        return analysis;
    }

    private Map<String, Object> analyzeDrought(List<WeatherData> data) {
        Map<String, Object> result = new HashMap<>();

        // Calculate rainfall deficit
        BigDecimal totalRainfall = data.stream()
                .map(w -> w.getRainfall() != null ? w.getRainfall() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Expected rainfall for the period (example: 5mm per day average)
        BigDecimal expectedRainfall = new BigDecimal(data.size() * 5);
        BigDecimal deficit = expectedRainfall.subtract(totalRainfall);

        // Count dry days (< 1mm rainfall)
        long dryDays = data.stream()
                .filter(w -> w.getRainfall() == null ||
                        w.getRainfall().compareTo(BigDecimal.ONE) < 0)
                .count();

        double dryDaysPercent = (dryDays * 100.0) / data.size();

        // Calculate risk score
        int riskScore = 0;
        if (deficit.compareTo(new BigDecimal("50")) > 0) riskScore += 40;
        else if (deficit.compareTo(BigDecimal.ZERO) > 0) riskScore += 20;

        if (dryDaysPercent > 80) riskScore += 30;
        else if (dryDaysPercent > 60) riskScore += 20;
        else if (dryDaysPercent > 40) riskScore += 10;

        result.put("riskScore", Math.min(riskScore, 100));
        result.put("totalRainfall", totalRainfall.setScale(2, RoundingMode.HALF_UP));
        result.put("rainfallDeficit", deficit.setScale(2, RoundingMode.HALF_UP));
        result.put("dryDays", dryDays);
        result.put("dryDaysPercent", Math.round(dryDaysPercent));
        result.put("status", getDroughtStatus(riskScore));

        return result;
    }

    private Map<String, Object> analyzeFlood(List<WeatherData> data) {
        Map<String, Object> result = new HashMap<>();

        // Find heavy rainfall events
        List<WeatherData> heavyRainDays = data.stream()
                .filter(w -> w.getRainfall() != null &&
                        w.getRainfall().compareTo(new BigDecimal("50")) > 0)
                .collect(Collectors.toList());

        // Calculate consecutive rainy days
        int maxConsecutiveRain = calculateMaxConsecutiveRainyDays(data);

        // Total extreme rainfall
        BigDecimal extremeRainfall = heavyRainDays.stream()
                .map(WeatherData::getRainfall)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate risk score
        int riskScore = 0;
        if (heavyRainDays.size() > 5) riskScore += 40;
        else if (heavyRainDays.size() > 2) riskScore += 25;
        else if (heavyRainDays.size() > 0) riskScore += 10;

        if (maxConsecutiveRain > 7) riskScore += 30;
        else if (maxConsecutiveRain > 5) riskScore += 20;
        else if (maxConsecutiveRain > 3) riskScore += 10;

        if (extremeRainfall.compareTo(new BigDecimal("200")) > 0) riskScore += 30;

        result.put("riskScore", Math.min(riskScore, 100));
        result.put("heavyRainEvents", heavyRainDays.size());
        result.put("maxConsecutiveRainyDays", maxConsecutiveRain);
        result.put("extremeRainfall", extremeRainfall.setScale(2, RoundingMode.HALF_UP));
        result.put("status", getFloodStatus(riskScore));

        return result;
    }

    private int calculateMaxConsecutiveRainyDays(List<WeatherData> data) {
        int maxConsecutive = 0;
        int currentConsecutive = 0;

        List<WeatherData> sorted = data.stream()
                .sorted(Comparator.comparing(WeatherData::getRecordDate))
                .collect(Collectors.toList());

        for (WeatherData w : sorted) {
            if (w.getRainfall() != null && w.getRainfall().compareTo(BigDecimal.ONE) > 0) {
                currentConsecutive++;
                maxConsecutive = Math.max(maxConsecutive, currentConsecutive);
            } else {
                currentConsecutive = 0;
            }
        }

        return maxConsecutive;
    }

    private Map<String, Object> analyzeHeatStress(List<WeatherData> data) {
        Map<String, Object> result = new HashMap<>();

        // Count hot days (> 35°C)
        long hotDays = data.stream()
                .filter(w -> w.getTemperature() != null &&
                        w.getTemperature().compareTo(new BigDecimal("35")) > 0)
                .count();

        // Count extreme heat days (> 40°C)
        long extremeHeatDays = data.stream()
                .filter(w -> w.getTemperature() != null &&
                        w.getTemperature().compareTo(new BigDecimal("40")) > 0)
                .count();

        // Calculate average temperature
        BigDecimal avgTemp = data.stream()
                .filter(w -> w.getTemperature() != null)
                .map(WeatherData::getTemperature)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(data.size()), 2, RoundingMode.HALF_UP);

        // Calculate risk score
        int riskScore = 0;
        if (extremeHeatDays > 5) riskScore += 50;
        else if (extremeHeatDays > 2) riskScore += 30;
        else if (extremeHeatDays > 0) riskScore += 15;

        if (hotDays > 15) riskScore += 30;
        else if (hotDays > 10) riskScore += 20;
        else if (hotDays > 5) riskScore += 10;

        if (avgTemp.compareTo(new BigDecimal("32")) > 0) riskScore += 20;

        result.put("riskScore", Math.min(riskScore, 100));
        result.put("hotDays", hotDays);
        result.put("extremeHeatDays", extremeHeatDays);
        result.put("averageTemperature", avgTemp);
        result.put("status", getHeatStressStatus(riskScore));

        return result;
    }

    private Map<String, Object> analyzeCropConditions(List<WeatherData> data) {
        Map<String, Object> result = new HashMap<>();

        // Ideal temperature range for most crops: 20-30°C
        long optimalTempDays = data.stream()
                .filter(w -> w.getTemperature() != null)
                .filter(w -> w.getTemperature().compareTo(new BigDecimal("20")) >= 0 &&
                        w.getTemperature().compareTo(new BigDecimal("30")) <= 0)
                .count();

        // Ideal humidity range: 40-70%
        long optimalHumidityDays = data.stream()
                .filter(w -> w.getHumidity() != null)
                .filter(w -> w.getHumidity().compareTo(new BigDecimal("40")) >= 0 &&
                        w.getHumidity().compareTo(new BigDecimal("70")) <= 0)
                .count();

        double optimalTempPercent = (optimalTempDays * 100.0) / data.size();
        double optimalHumidityPercent = (optimalHumidityDays * 100.0) / data.size();

        // Calculate favorability score (higher is better, inverse of risk)
        int favorabilityScore = (int) ((optimalTempPercent + optimalHumidityPercent) / 2);
        int riskScore = 100 - favorabilityScore;

        result.put("favorabilityScore", favorabilityScore);
        result.put("riskScore", riskScore);
        result.put("optimalTempDays", optimalTempDays);
        result.put("optimalHumidityDays", optimalHumidityDays);
        result.put("optimalTempPercent", Math.round(optimalTempPercent));
        result.put("optimalHumidityPercent", Math.round(optimalHumidityPercent));
        result.put("status", getCropConditionStatus(favorabilityScore));

        return result;
    }

    private int calculateOverallRisk(Map<String, Object> drought, Map<String, Object> flood,
                                     Map<String, Object> heat, Map<String, Object> crop) {
        int droughtRisk = (Integer) drought.get("riskScore");
        int floodRisk = (Integer) flood.get("riskScore");
        int heatRisk = (Integer) heat.get("riskScore");
        int cropRisk = (Integer) crop.get("riskScore");

        // Weighted average (drought and heat are more critical for food security)
        return (droughtRisk * 30 + floodRisk * 20 + heatRisk * 30 + cropRisk * 20) / 100;
    }

    private String getRiskLevel(int score) {
        if (score >= 75) return "CRITICAL";
        if (score >= 50) return "HIGH";
        if (score >= 25) return "MEDIUM";
        return "LOW";
    }

    private String getDroughtStatus(int score) {
        if (score >= 70) return "SEVERE_DROUGHT";
        if (score >= 50) return "MODERATE_DROUGHT";
        if (score >= 30) return "MILD_DROUGHT";
        return "NO_DROUGHT";
    }

    private String getFloodStatus(int score) {
        if (score >= 70) return "SEVERE_FLOOD_RISK";
        if (score >= 50) return "HIGH_FLOOD_RISK";
        if (score >= 30) return "MODERATE_FLOOD_RISK";
        return "LOW_FLOOD_RISK";
    }

    private String getHeatStressStatus(int score) {
        if (score >= 70) return "SEVERE_HEAT_STRESS";
        if (score >= 50) return "HIGH_HEAT_STRESS";
        if (score >= 30) return "MODERATE_HEAT_STRESS";
        return "LOW_HEAT_STRESS";
    }

    private String getCropConditionStatus(int score) {
        if (score >= 75) return "EXCELLENT";
        if (score >= 60) return "GOOD";
        if (score >= 40) return "FAIR";
        return "POOR";
    }

    private List<String> generateRiskRecommendations(Map<String, Object> drought,
                                                     Map<String, Object> flood,
                                                     Map<String, Object> heat,
                                                     Map<String, Object> crop) {
        List<String> recommendations = new ArrayList<>();

        int droughtRisk = (Integer) drought.get("riskScore");
        int floodRisk = (Integer) flood.get("riskScore");
        int heatRisk = (Integer) heat.get("riskScore");
        int cropRisk = (Integer) crop.get("riskScore");

        // Drought recommendations
        if (droughtRisk >= 50) {
            recommendations.add("URGENT: Implement water conservation measures");
            recommendations.add("Consider drought-resistant crop varieties");
            recommendations.add("Establish irrigation systems or improve existing ones");
        } else if (droughtRisk >= 30) {
            recommendations.add("Monitor soil moisture levels closely");
            recommendations.add("Prepare contingency plans for water shortage");
        }

        // Flood recommendations
        if (floodRisk >= 50) {
            recommendations.add("URGENT: Prepare drainage systems and flood barriers");
            recommendations.add("Relocate vulnerable crops to higher ground if possible");
            recommendations.add("Stock emergency supplies and equipment");
        } else if (floodRisk >= 30) {
            recommendations.add("Monitor weather forecasts closely");
            recommendations.add("Ensure drainage systems are functional");
        }

        // Heat stress recommendations
        if (heatRisk >= 50) {
            recommendations.add("URGENT: Provide shade for crops where possible");
            recommendations.add("Increase irrigation frequency during hot periods");
            recommendations.add("Apply mulch to conserve soil moisture");
        } else if (heatRisk >= 30) {
            recommendations.add("Monitor crop stress indicators");
            recommendations.add("Prepare for increased irrigation needs");
        }

        // Crop condition recommendations
        if (cropRisk >= 50) {
            recommendations.add("Assess crop health and consider early harvest if needed");
            recommendations.add("Apply appropriate fertilizers or soil amendments");
            recommendations.add("Increase pest and disease monitoring");
        }

        if (recommendations.isEmpty()) {
            recommendations.add("Current conditions are favorable - maintain standard farming practices");
            recommendations.add("Continue regular monitoring of weather and crop conditions");
        }

        return recommendations;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> assessDroughtRisk(int days, BigDecimal latitude, BigDecimal longitude) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);

        List<WeatherData> data;
        if (latitude != null && longitude != null) {
            BigDecimal radius = new BigDecimal("0.5");
            List<WeatherData> locationData = findByLocationRadius(latitude, longitude, radius);
            data = locationData.stream()
                    .filter(w -> w.getRecordDate().isAfter(since))
                    .collect(Collectors.toList());
        } else {
            data = weatherDataRepository.findRecentWeatherData(since);
        }

        return analyzeDrought(data);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> assessFloodRisk(int days, BigDecimal latitude, BigDecimal longitude) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);

        List<WeatherData> data;
        if (latitude != null && longitude != null) {
            BigDecimal radius = new BigDecimal("0.5");
            List<WeatherData> locationData = findByLocationRadius(latitude, longitude, radius);
            data = locationData.stream()
                    .filter(w -> w.getRecordDate().isAfter(since))
                    .collect(Collectors.toList());
        } else {
            data = weatherDataRepository.findRecentWeatherData(since);
        }

        return analyzeFlood(data);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCropStressIndicators(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<WeatherData> data = weatherDataRepository.findRecentWeatherData(since);

        Map<String, Object> indicators = new HashMap<>();

        // Temperature stress
        long highTempStress = data.stream()
                .filter(w -> w.getTemperature() != null &&
                        w.getTemperature().compareTo(new BigDecimal("35")) > 0)
                .count();

        long lowTempStress = data.stream()
                .filter(w -> w.getTemperature() != null &&
                        w.getTemperature().compareTo(new BigDecimal("10")) < 0)
                .count();

        // Water stress
        BigDecimal totalRainfall = data.stream()
                .map(w -> w.getRainfall() != null ? w.getRainfall() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgRainfallPerDay = totalRainfall.divide(
                new BigDecimal(days), 2, RoundingMode.HALF_UP);

        // Humidity stress
        long lowHumidityDays = data.stream()
                .filter(w -> w.getHumidity() != null &&
                        w.getHumidity().compareTo(new BigDecimal("30")) < 0)
                .count();

        long highHumidityDays = data.stream()
                .filter(w -> w.getHumidity() != null &&
                        w.getHumidity().compareTo(new BigDecimal("80")) > 0)
                .count();

        // Calculate overall stress level
        int stressScore = 0;

        // Temperature stress scoring
        if (highTempStress > days * 0.5) stressScore += 30;
        else if (highTempStress > days * 0.3) stressScore += 20;
        else if (highTempStress > days * 0.1) stressScore += 10;

        if (lowTempStress > days * 0.3) stressScore += 25;
        else if (lowTempStress > days * 0.1) stressScore += 15;

        // Water stress scoring
        if (avgRainfallPerDay.compareTo(new BigDecimal("2")) < 0) stressScore += 25;
        else if (avgRainfallPerDay.compareTo(new BigDecimal("3")) < 0) stressScore += 15;
        else if (avgRainfallPerDay.compareTo(new BigDecimal("5")) < 0) stressScore += 5;

        // Humidity stress scoring
        if (lowHumidityDays > days * 0.4) stressScore += 15;
        else if (lowHumidityDays > days * 0.2) stressScore += 10;

        if (highHumidityDays > days * 0.4) stressScore += 10;
        else if (highHumidityDays > days * 0.2) stressScore += 5;

        // Cap stress score at 100
        stressScore = Math.min(stressScore, 100);

        // Populate indicators map
        indicators.put("stressScore", stressScore);
        indicators.put("stressLevel", getStressLevel(stressScore));

        indicators.put("temperatureStress", Map.of(
                "highTempDays", highTempStress,
                "lowTempDays", lowTempStress,
                "highTempPercentage", Math.round((highTempStress * 100.0) / days),
                "lowTempPercentage", Math.round((lowTempStress * 100.0) / days)
        ));

        indicators.put("waterStress", Map.of(
                "totalRainfall", totalRainfall.setScale(2, RoundingMode.HALF_UP),
                "avgRainfallPerDay", avgRainfallPerDay,
                "status", getWaterStressStatus(avgRainfallPerDay)
        ));

        indicators.put("humidityStress", Map.of(
                "lowHumidityDays", lowHumidityDays,
                "highHumidityDays", highHumidityDays,
                "lowHumidityPercentage", Math.round((lowHumidityDays * 100.0) / days),
                "highHumidityPercentage", Math.round((highHumidityDays * 100.0) / days)
        ));

        indicators.put("analyzedDays", days);
        indicators.put("dataPoints", data.size());

        // Generate stress recommendations
        List<String> recommendations = generateStressRecommendations(
                highTempStress, lowTempStress, avgRainfallPerDay,
                lowHumidityDays, highHumidityDays, days);
        indicators.put("recommendations", recommendations);

        return indicators;
    }

    private String getStressLevel(int score) {
        if (score >= 75) return "SEVERE";
        if (score >= 50) return "HIGH";
        if (score >= 25) return "MODERATE";
        return "LOW";
    }

    private String getWaterStressStatus(BigDecimal avgRainfallPerDay) {
        if (avgRainfallPerDay.compareTo(new BigDecimal("2")) < 0) return "SEVERE_DEFICIT";
        if (avgRainfallPerDay.compareTo(new BigDecimal("3")) < 0) return "MODERATE_DEFICIT";
        if (avgRainfallPerDay.compareTo(new BigDecimal("5")) < 0) return "MILD_DEFICIT";
        if (avgRainfallPerDay.compareTo(new BigDecimal("10")) > 0) return "EXCESS";
        return "ADEQUATE";
    }

    private List<String> generateStressRecommendations(long highTempDays, long lowTempDays,
                                                       BigDecimal avgRainfall, long lowHumidity,
                                                       long highHumidity, int totalDays) {
        List<String> recommendations = new ArrayList<>();

        // Temperature-based recommendations
        if (highTempDays > totalDays * 0.3) {
            recommendations.add("High temperature stress detected - implement shade structures or shade nets");
            recommendations.add("Increase irrigation frequency during peak heat hours");
            recommendations.add("Apply organic mulch to reduce soil temperature");
        }

        if (lowTempDays > totalDays * 0.2) {
            recommendations.add("Cold stress detected - protect sensitive crops with row covers");
            recommendations.add("Consider delaying planting of warm-season crops");
        }

        // Water-based recommendations
        if (avgRainfall.compareTo(new BigDecimal("2")) < 0) {
            recommendations.add("CRITICAL: Severe water deficit - implement emergency irrigation");
            recommendations.add("Prioritize water for high-value crops");
            recommendations.add("Consider drought-tolerant crop varieties for next season");
        } else if (avgRainfall.compareTo(new BigDecimal("3")) < 0) {
            recommendations.add("Water deficit detected - increase irrigation scheduling");
            recommendations.add("Monitor soil moisture levels daily");
        }

        if (avgRainfall.compareTo(new BigDecimal("10")) > 0) {
            recommendations.add("Excessive rainfall detected - ensure adequate drainage");
            recommendations.add("Monitor for waterlogging and root diseases");
        }

        // Humidity-based recommendations
        if (lowHumidity > totalDays * 0.3) {
            recommendations.add("Low humidity stress - increase foliar spraying frequency");
            recommendations.add("Consider windbreaks to reduce moisture loss");
        }

        if (highHumidity > totalDays * 0.3) {
            recommendations.add("High humidity detected - increase air circulation");
            recommendations.add("Monitor closely for fungal diseases");
            recommendations.add("Reduce irrigation if soil moisture is adequate");
        }

        if (recommendations.isEmpty()) {
            recommendations.add("Crop stress levels are within acceptable range");
            recommendations.add("Continue current management practices");
        }

        return recommendations;
    }

// =================================================================================
// WEATHER ALERT TRIGGER CHECKING
// =================================================================================

    @Transactional(readOnly = true)
    public List<Map<String, Object>> checkWeatherAlertTriggers() {
        List<Map<String, Object>> triggers = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last24Hours = now.minusHours(24);

        List<WeatherData> recentData = weatherDataRepository.findRecentWeatherData(last24Hours);

        if (recentData.isEmpty()) {
            return triggers;
        }

        // Check for extreme temperature
        for (WeatherData data : recentData) {
            if (data.getTemperature() != null) {
                if (data.getTemperature().compareTo(new BigDecimal("40")) > 0) {
                    triggers.add(createAlertTrigger(
                            "EXTREME_HEAT",
                            "CRITICAL",
                            "Extreme heat detected: " + data.getTemperature() + "°C",
                            data.getRecordDate(),
                            data.getLatitude(),
                            data.getLongitude()
                    ));
                } else if (data.getTemperature().compareTo(new BigDecimal("0")) < 0) {
                    triggers.add(createAlertTrigger(
                            "FROST",
                            "HIGH",
                            "Frost conditions detected: " + data.getTemperature() + "°C",
                            data.getRecordDate(),
                            data.getLatitude(),
                            data.getLongitude()
                    ));
                }
            }

            // Check for heavy rainfall
            if (data.getRainfall() != null && data.getRainfall().compareTo(new BigDecimal("50")) > 0) {
                triggers.add(createAlertTrigger(
                        "HEAVY_RAIN",
                        "HIGH",
                        "Heavy rainfall detected: " + data.getRainfall() + "mm",
                        data.getRecordDate(),
                        data.getLatitude(),
                        data.getLongitude()
                ));
            }

            // Check for strong winds
            if (data.getWindSpeed() != null && data.getWindSpeed().compareTo(new BigDecimal("40")) > 0) {
                triggers.add(createAlertTrigger(
                        "STRONG_WIND",
                        "MEDIUM",
                        "Strong winds detected: " + data.getWindSpeed() + " km/h",
                        data.getRecordDate(),
                        data.getLatitude(),
                        data.getLongitude()
                ));
            }
        }

        // Check for drought conditions (no significant rain in last 7 days)
        LocalDateTime last7Days = now.minusDays(7);
        List<WeatherData> weekData = weatherDataRepository.findRecentWeatherData(last7Days);

        BigDecimal weekRainfall = weekData.stream()
                .map(w -> w.getRainfall() != null ? w.getRainfall() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (weekRainfall.compareTo(new BigDecimal("5")) < 0) {
            triggers.add(createAlertTrigger(
                    "DROUGHT_RISK",
                    "MEDIUM",
                    "Low rainfall in past 7 days: " + weekRainfall.setScale(2, RoundingMode.HALF_UP) + "mm",
                    now,
                    null,
                    null
            ));
        }

        return triggers;
    }

    private Map<String, Object> createAlertTrigger(String type, String severity,
                                                   String message, LocalDateTime timestamp,
                                                   BigDecimal latitude, BigDecimal longitude) {
        Map<String, Object> alert = new HashMap<>();
        alert.put("alertType", type);
        alert.put("severity", severity);
        alert.put("message", message);
        alert.put("timestamp", timestamp);
        alert.put("latitude", latitude);
        alert.put("longitude", longitude);
        return alert;
    }
}