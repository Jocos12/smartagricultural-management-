package SmartAgricultural.Management.dto;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * DTO for crop statistics
 */
public class CropStatsDTO {

    private Long totalCrops;
    private Long highDemandCropsCount;
    private Map<String, Long> cropTypeStats;
    private Map<String, Long> marketDemandStats;
    private Map<String, Long> seasonalDistribution;
    private Map<String, Double> averageGrowingPeriodByType;

    // Constructor with 6 parameters matching your service call
    public CropStatsDTO(Long totalCrops,
                        Long highDemandCropsCount,
                        List<Object[]> typeStats,
                        List<Object[]> demandStats,
                        List<Object[]> seasonalStats,
                        List<Object[]> avgGrowingPeriod) {
        this.totalCrops = totalCrops;
        this.highDemandCropsCount = highDemandCropsCount;

        // Convert Object[] lists to Maps for easier usage
        this.cropTypeStats = convertToMap(typeStats);
        this.marketDemandStats = convertToMap(demandStats);
        this.seasonalDistribution = convertToMap(seasonalStats);
        this.averageGrowingPeriodByType = convertToDoubleMap(avgGrowingPeriod);
    }

    // Default constructor
    public CropStatsDTO() {
        this.cropTypeStats = new HashMap<>();
        this.marketDemandStats = new HashMap<>();
        this.seasonalDistribution = new HashMap<>();
        this.averageGrowingPeriodByType = new HashMap<>();
    }

    // Helper method to convert Object[] to Map<String, Long>
    private Map<String, Long> convertToMap(List<Object[]> data) {
        Map<String, Long> result = new HashMap<>();
        if (data != null) {
            for (Object[] row : data) {
                if (row.length >= 2) {
                    String key = row[0] != null ? row[0].toString() : "Unknown";
                    Long value = row[1] instanceof Number ? ((Number) row[1]).longValue() : 0L;
                    result.put(key, value);
                }
            }
        }
        return result;
    }

    // Helper method to convert Object[] to Map<String, Double>
    private Map<String, Double> convertToDoubleMap(List<Object[]> data) {
        Map<String, Double> result = new HashMap<>();
        if (data != null) {
            for (Object[] row : data) {
                if (row.length >= 2) {
                    String key = row[0] != null ? row[0].toString() : "Unknown";
                    Double value = row[1] instanceof Number ? ((Number) row[1]).doubleValue() : 0.0;
                    result.put(key, value);
                }
            }
        }
        return result;
    }

    // Getters and Setters
    public Long getTotalCrops() {
        return totalCrops;
    }

    public void setTotalCrops(Long totalCrops) {
        this.totalCrops = totalCrops;
    }

    public Long getHighDemandCropsCount() {
        return highDemandCropsCount;
    }

    public void setHighDemandCropsCount(Long highDemandCropsCount) {
        this.highDemandCropsCount = highDemandCropsCount;
    }

    public Map<String, Long> getCropTypeStats() {
        return cropTypeStats;
    }

    public void setCropTypeStats(Map<String, Long> cropTypeStats) {
        this.cropTypeStats = cropTypeStats;
    }

    public Map<String, Long> getMarketDemandStats() {
        return marketDemandStats;
    }

    public void setMarketDemandStats(Map<String, Long> marketDemandStats) {
        this.marketDemandStats = marketDemandStats;
    }

    public Map<String, Long> getSeasonalDistribution() {
        return seasonalDistribution;
    }

    public void setSeasonalDistribution(Map<String, Long> seasonalDistribution) {
        this.seasonalDistribution = seasonalDistribution;
    }

    public Map<String, Double> getAverageGrowingPeriodByType() {
        return averageGrowingPeriodByType;
    }

    public void setAverageGrowingPeriodByType(Map<String, Double> averageGrowingPeriodByType) {
        this.averageGrowingPeriodByType = averageGrowingPeriodByType;
    }

    // Utility methods
    public double getHighDemandPercentage() {
        if (totalCrops == null || totalCrops == 0) {
            return 0.0;
        }
        return (highDemandCropsCount != null ? highDemandCropsCount : 0) * 100.0 / totalCrops;
    }

    public String getMostPopularCropType() {
        return cropTypeStats.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");
    }

    public String getMostPopularSeason() {
        return seasonalDistribution.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");
    }

    public double getOverallAverageGrowingPeriod() {
        return averageGrowingPeriodByType.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    @Override
    public String toString() {
        return "CropStatsDTO{" +
                "totalCrops=" + totalCrops +
                ", highDemandCropsCount=" + highDemandCropsCount +
                ", cropTypeStats=" + cropTypeStats +
                ", marketDemandStats=" + marketDemandStats +
                ", seasonalDistribution=" + seasonalDistribution +
                ", averageGrowingPeriodByType=" + averageGrowingPeriodByType +
                '}';
    }
}