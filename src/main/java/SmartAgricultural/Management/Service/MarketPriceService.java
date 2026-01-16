package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.MarketPrice;
import SmartAgricultural.Management.Model.MarketPrice.MarketType;
import SmartAgricultural.Management.Model.MarketPrice.DemandLevel;
import SmartAgricultural.Management.Model.MarketPrice.SupplyLevel;
import SmartAgricultural.Management.Model.MarketPrice.PriceTrend;
import SmartAgricultural.Management.Repository.MarketPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MarketPriceService {

    private final MarketPriceRepository marketPriceRepository;

    @Autowired
    public MarketPriceService(MarketPriceRepository marketPriceRepository) {
        this.marketPriceRepository = marketPriceRepository;
    }

    // Basic CRUD operations
    public MarketPrice save(MarketPrice marketPrice) {
        validateMarketPrice(marketPrice);
        return marketPriceRepository.save(marketPrice);
    }

    public Optional<MarketPrice> findById(String id) {
        return marketPriceRepository.findById(id);
    }

    public List<MarketPrice> findAll() {
        return marketPriceRepository.findAll();
    }

    public Page<MarketPrice> findAll(Pageable pageable) {
        return marketPriceRepository.findAll(pageable);
    }

    public Page<MarketPrice> findAllSorted(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return marketPriceRepository.findAll(pageable);
    }

    public MarketPrice update(String id, MarketPrice marketPrice) {
        return marketPriceRepository.findById(id)
                .map(existingPrice -> {
                    updateExistingPrice(existingPrice, marketPrice);
                    validateMarketPrice(existingPrice);
                    return marketPriceRepository.save(existingPrice);
                })
                .orElseThrow(() -> new RuntimeException("Market price not found with id: " + id));
    }

    public void deleteById(String id) {
        if (!marketPriceRepository.existsById(id)) {
            throw new RuntimeException("Market price not found with id: " + id);
        }
        marketPriceRepository.deleteById(id);
    }

    public boolean existsById(String id) {
        return marketPriceRepository.existsById(id);
    }

    // Crop-related queries
    public List<MarketPrice> findByCropId(String cropId) {
        return marketPriceRepository.findByCropId(cropId);
    }

    public Page<MarketPrice> findByCropId(String cropId, Pageable pageable) {
        return marketPriceRepository.findByCropId(cropId, pageable);
    }

    public List<MarketPrice> findLatestPricesByCrop(String cropId) {
        return marketPriceRepository.findLatestPricesByCrop(cropId);
    }

    public Optional<MarketPrice> findLatestPriceByCrop(String cropId) {
        return marketPriceRepository.findLatestPriceByCrop(cropId);
    }

    // Market-related queries
    public List<MarketPrice> findByMarketName(String marketName) {
        return marketPriceRepository.findByMarketNameContainingIgnoreCase(marketName);
    }

    public List<MarketPrice> findByMarketType(MarketType marketType) {
        return marketPriceRepository.findByMarketType(marketType);
    }

    public List<MarketPrice> findByLocation(String location) {
        return marketPriceRepository.findByLocationContainingIgnoreCase(location);
    }

    public List<MarketPrice> findByCropAndMarketType(String cropId, MarketType marketType) {
        return marketPriceRepository.findByCropAndMarketType(cropId, marketType);
    }

    public List<MarketPrice> findByCropAndLocation(String cropId, String location) {
        return marketPriceRepository.findByCropAndLocation(cropId, location);
    }

    // Date-based queries
    public List<MarketPrice> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return marketPriceRepository.findByPriceDateBetween(startDate, endDate);
    }

    public Page<MarketPrice> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return marketPriceRepository.findByPriceDateBetween(startDate, endDate, pageable);
    }

    public List<MarketPrice> findByCropAndDateRange(String cropId, LocalDate startDate, LocalDate endDate) {
        return marketPriceRepository.findByCropIdAndPriceDateBetween(cropId, startDate, endDate);
    }

    public List<MarketPrice> findRecentPrices(int days) {
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        return marketPriceRepository.findByPriceDateAfter(cutoffDate);
    }

    public List<MarketPrice> findRecentPricesByCrop(String cropId, int days) {
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        return marketPriceRepository.findReliableRecentPricesByCrop(cropId, cutoffDate);
    }

    // Price-based queries
    public List<MarketPrice> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return marketPriceRepository.findByPricePerKgBetween(minPrice, maxPrice);
    }

    public List<MarketPrice> findHighPrices(BigDecimal threshold) {
        return marketPriceRepository.findByPricePerKgGreaterThan(threshold);
    }

    public List<MarketPrice> findLowPrices(BigDecimal threshold) {
        return marketPriceRepository.findByPricePerKgLessThan(threshold);
    }

    // Market condition queries
    public List<MarketPrice> findByDemandLevel(DemandLevel demandLevel) {
        return marketPriceRepository.findByDemandLevel(demandLevel);
    }

    public List<MarketPrice> findBySupplyLevel(SupplyLevel supplyLevel) {
        return marketPriceRepository.findBySupplyLevel(supplyLevel);
    }

    public List<MarketPrice> findByPriceTrend(PriceTrend priceTrend) {
        return marketPriceRepository.findByPriceTrend(priceTrend);
    }

    public List<MarketPrice> findByDemandAndSupplyLevels(DemandLevel demandLevel, SupplyLevel supplyLevel) {
        return marketPriceRepository.findByDemandAndSupplyLevels(demandLevel, supplyLevel);
    }

    public List<MarketPrice> findMarketOpportunities() {
        return marketPriceRepository.findMarketOpportunities();
    }

    // Reliability and data quality queries
    public List<MarketPrice> findReliablePrices(Integer minScore) {
        return marketPriceRepository.findByReliabilityScoreGreaterThanEqual(minScore);
    }

    public List<MarketPrice> findByDataSource(String dataSource) {
        return marketPriceRepository.findByDataSourceContainingIgnoreCase(dataSource);
    }

    public List<MarketPrice> findReliableRecentPrices(Integer minScore, int days) {
        LocalDate minDate = LocalDate.now().minusDays(days);
        return marketPriceRepository.findReliableRecentPrices(minScore, minDate);
    }

    // Statistics and analytics
    public BigDecimal getAveragePrice(String cropId, LocalDate startDate, LocalDate endDate) {
        BigDecimal average = marketPriceRepository.getAveragePriceByCropAndPeriod(cropId, startDate, endDate);
        return average != null ? average : BigDecimal.ZERO;
    }

    public BigDecimal getMinPrice(String cropId, LocalDate startDate, LocalDate endDate) {
        BigDecimal min = marketPriceRepository.getMinPriceByCropAndPeriod(cropId, startDate, endDate);
        return min != null ? min : BigDecimal.ZERO;
    }

    public BigDecimal getMaxPrice(String cropId, LocalDate startDate, LocalDate endDate) {
        BigDecimal max = marketPriceRepository.getMaxPriceByCropAndPeriod(cropId, startDate, endDate);
        return max != null ? max : BigDecimal.ZERO;
    }

    public Long getPriceCount(String cropId, LocalDate startDate, LocalDate endDate) {
        return marketPriceRepository.countPricesByCropAndPeriod(cropId, startDate, endDate);
    }

    public Double getPriceStandardDeviation(String cropId, LocalDate startDate, LocalDate endDate) {
        return marketPriceRepository.getPriceStandardDeviation(cropId, startDate, endDate);
    }

    public Double getPriceVolatility(String cropId, LocalDate startDate, LocalDate endDate) {
        return marketPriceRepository.getPriceVolatility(cropId, startDate, endDate);
    }

    // Market analysis
    public List<Object[]> getAveragePriceByMarketType(String cropId) {
        return marketPriceRepository.getAveragePriceByMarketType(cropId);
    }

    public List<Object[]> getAveragePriceByLocation(String cropId) {
        return marketPriceRepository.getAveragePriceByLocation(cropId);
    }

    public List<Object[]> getMonthlyAveragePrices(String cropId, int year) {
        return marketPriceRepository.getMonthlyAveragePrices(cropId, year);
    }

    public List<Object[]> getDailyPriceStats(String cropId, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        return marketPriceRepository.getDailyPriceStats(cropId, startDate);
    }

    public List<Object[]> getMarketAnalysis(String cropId) {
        return marketPriceRepository.getMarketAnalysis(cropId);
    }

    public List<Object[]> getDemandSupplyPriceAnalysis(String cropId) {
        return marketPriceRepository.getDemandSupplyPriceAnalysis(cropId);
    }

    public List<Object[]> getPriceTrendAnalysis(String cropId, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        return marketPriceRepository.getPriceTrendAnalysis(cropId, startDate);
    }

    // Reference data
    public List<String> getMarketNamesByCrop(String cropId) {
        return marketPriceRepository.findMarketNamesByCrop(cropId);
    }

    public List<String> getLocationsByCrop(String cropId) {
        return marketPriceRepository.findLocationsByCrop(cropId);
    }

    // Price comparison and forecasting
    public List<Object[]> comparePricesByDate(String cropId, LocalDate date) {
        return marketPriceRepository.comparePricesByDate(cropId, date);
    }

    public List<MarketPrice> findPricesNearDate(String cropId, LocalDate targetDate) {
        return marketPriceRepository.findPricesNearDate(cropId, targetDate);
    }

    public List<MarketPrice> findSimilarSeasonalPrices(String cropId) {
        return marketPriceRepository.findSimilarSeasonalPrices(cropId);
    }

    // Reporting methods
    public Map<String, Object> generateCropPriceReport(String cropId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();

        List<MarketPrice> prices = findByCropAndDateRange(cropId, startDate, endDate);

        report.put("cropId", cropId);
        report.put("reportPeriod", startDate + " to " + endDate);
        report.put("totalPriceEntries", prices.size());
        report.put("averagePrice", getAveragePrice(cropId, startDate, endDate));
        report.put("minPrice", getMinPrice(cropId, startDate, endDate));
        report.put("maxPrice", getMaxPrice(cropId, startDate, endDate));
        report.put("priceVolatility", getPriceVolatility(cropId, startDate, endDate));
        report.put("marketTypeAnalysis", getMarketTypeStatistics(prices));
        report.put("locationAnalysis", getLocationStatistics(prices));
        report.put("trendAnalysis", getPriceTrendStatistics(prices));
        report.put("qualityAnalysis", getDataQualityStatistics(prices));

        return report;
    }

    public Map<MarketType, Map<String, Object>> getMarketTypeStatistics(List<MarketPrice> prices) {
        return prices.stream()
                .filter(p -> p.getMarketType() != null)
                .collect(Collectors.groupingBy(
                        MarketPrice::getMarketType,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                priceList -> {
                                    Map<String, Object> stats = new HashMap<>();
                                    stats.put("count", priceList.size());
                                    stats.put("averagePrice", calculateAveragePrice(priceList));
                                    stats.put("minPrice", calculateMinPrice(priceList));
                                    stats.put("maxPrice", calculateMaxPrice(priceList));
                                    return stats;
                                }
                        )
                ));
    }

    public Map<String, Map<String, Object>> getLocationStatistics(List<MarketPrice> prices) {
        return prices.stream()
                .filter(p -> p.getLocation() != null)
                .collect(Collectors.groupingBy(
                        MarketPrice::getLocation,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                priceList -> {
                                    Map<String, Object> stats = new HashMap<>();
                                    stats.put("count", priceList.size());
                                    stats.put("averagePrice", calculateAveragePrice(priceList));
                                    return stats;
                                }
                        )
                ));
    }

    public Map<PriceTrend, Long> getPriceTrendStatistics(List<MarketPrice> prices) {
        return prices.stream()
                .filter(p -> p.getPriceTrend() != null)
                .collect(Collectors.groupingBy(
                        MarketPrice::getPriceTrend,
                        Collectors.counting()
                ));
    }

    public Map<String, Object> getDataQualityStatistics(List<MarketPrice> prices) {
        Map<String, Object> stats = new HashMap<>();

        long totalPrices = prices.size();
        long reliablePrices = prices.stream()
                .mapToLong(p -> p.isReliableData() ? 1L : 0L)
                .sum();
        long recentPrices = prices.stream()
                .mapToLong(p -> p.isRecentPrice() ? 1L : 0L)
                .sum();
        long outdatedPrices = prices.stream()
                .mapToLong(p -> p.isOutdatedPrice() ? 1L : 0L)
                .sum();

        stats.put("totalPrices", totalPrices);
        stats.put("reliablePrices", reliablePrices);
        stats.put("recentPrices", recentPrices);
        stats.put("outdatedPrices", outdatedPrices);
        stats.put("reliabilityPercentage",
                totalPrices > 0 ? BigDecimal.valueOf(reliablePrices * 100.0 / totalPrices)
                        .setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        stats.put("recencyPercentage",
                totalPrices > 0 ? BigDecimal.valueOf(recentPrices * 100.0 / totalPrices)
                        .setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        return stats;
    }

    // Price prediction and recommendations
    public List<String> generatePriceRecommendations(String cropId) {
        List<String> recommendations = new ArrayList<>();
        List<MarketPrice> recentPrices = findRecentPricesByCrop(cropId, 30);

        if (recentPrices.isEmpty()) {
            recommendations.add("No recent price data available for analysis.");
            return recommendations;
        }

        // Market opportunity analysis
        List<MarketPrice> opportunities = recentPrices.stream()
                .filter(MarketPrice::isMarketOpportunity)
                .collect(Collectors.toList());

        if (!opportunities.isEmpty()) {
            recommendations.add("Market opportunities identified: High demand with low supply and increasing price trend in " +
                    opportunities.size() + " markets.");
        }

        // Price trend analysis
        Map<PriceTrend, Long> trendStats = getPriceTrendStatistics(recentPrices);
        long increasingTrend = trendStats.getOrDefault(PriceTrend.INCREASING, 0L);
        long decreasingTrend = trendStats.getOrDefault(PriceTrend.DECREASING, 0L);

        if (increasingTrend > decreasingTrend) {
            recommendations.add("Price trend is predominantly increasing. Consider holding stock if possible.");
        } else if (decreasingTrend > increasingTrend) {
            recommendations.add("Price trend is predominantly decreasing. Consider selling soon.");
        }

        // Market type analysis
        BigDecimal wholesaleAvg = calculateAveragePrice(recentPrices.stream()
                .filter(p -> p.getMarketType() == MarketType.WHOLESALE)
                .collect(Collectors.toList()));
        BigDecimal retailAvg = calculateAveragePrice(recentPrices.stream()
                .filter(p -> p.getMarketType() == MarketType.RETAIL)
                .collect(Collectors.toList()));

        if (wholesaleAvg.compareTo(BigDecimal.ZERO) > 0 && retailAvg.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margin = retailAvg.subtract(wholesaleAvg);
            BigDecimal marginPercentage = margin.divide(wholesaleAvg, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

            if (marginPercentage.compareTo(BigDecimal.valueOf(20)) > 0) {
                recommendations.add("High retail margin (" + marginPercentage + "%). Consider direct retail sales.");
            }
        }

        // Data quality warnings
        long outdatedCount = recentPrices.stream()
                .mapToLong(p -> p.isOutdatedPrice() ? 1L : 0L)
                .sum();

        if (outdatedCount > recentPrices.size() * 0.3) {
            recommendations.add("More than 30% of price data is outdated. Seek more recent market information.");
        }

        return recommendations;
    }

    public BigDecimal predictNextMonthPrice(String cropId) {
        List<MarketPrice> historicalPrices = findRecentPricesByCrop(cropId, 90);

        if (historicalPrices.size() < 3) {
            return null; // Not enough data for prediction
        }

        // Simple trend-based prediction
        BigDecimal totalPrice = BigDecimal.ZERO;
        int count = 0;

        for (MarketPrice price : historicalPrices) {
            if (price.getPricePerKg() != null) {
                totalPrice = totalPrice.add(price.getPricePerKg());
                count++;
            }
        }

        if (count == 0) return null;

        BigDecimal averagePrice = totalPrice.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);

        // Apply trend factor
        long increasingCount = historicalPrices.stream()
                .mapToLong(p -> p.getPriceTrend() == PriceTrend.INCREASING ? 1L : 0L)
                .sum();
        long decreasingCount = historicalPrices.stream()
                .mapToLong(p -> p.getPriceTrend() == PriceTrend.DECREASING ? 1L : 0L)
                .sum();

        BigDecimal trendFactor = BigDecimal.ONE;
        if (increasingCount > decreasingCount) {
            trendFactor = BigDecimal.valueOf(1.05); // 5% increase
        } else if (decreasingCount > increasingCount) {
            trendFactor = BigDecimal.valueOf(0.95); // 5% decrease
        }

        return averagePrice.multiply(trendFactor).setScale(2, RoundingMode.HALF_UP);
    }

    // Utility methods
    private void validateMarketPrice(MarketPrice price) {
        if (price.getCropId() == null || price.getCropId().trim().isEmpty()) {
            throw new IllegalArgumentException("Crop ID is required");
        }

        if (price.getMarketName() == null || price.getMarketName().trim().isEmpty()) {
            throw new IllegalArgumentException("Market name is required");
        }

        if (price.getMarketType() == null) {
            throw new IllegalArgumentException("Market type is required");
        }

        if (price.getLocation() == null || price.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Location is required");
        }

        if (price.getPricePerKg() == null || price.getPricePerKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price per kg must be positive");
        }

        if (price.getDataSource() == null || price.getDataSource().trim().isEmpty()) {
            throw new IllegalArgumentException("Data source is required");
        }

        if (price.getPriceDate() == null) {
            throw new IllegalArgumentException("Price date is required");
        }

        if (price.getPriceDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Price date cannot be in the future");
        }
    }

    private void updateExistingPrice(MarketPrice existing, MarketPrice updated) {
        if (updated.getCropId() != null) existing.setCropId(updated.getCropId());
        if (updated.getMarketName() != null) existing.setMarketName(updated.getMarketName());
        if (updated.getMarketType() != null) existing.setMarketType(updated.getMarketType());
        if (updated.getLocation() != null) existing.setLocation(updated.getLocation());
        if (updated.getPriceDate() != null) existing.setPriceDate(updated.getPriceDate());
        if (updated.getPricePerKg() != null) existing.setPricePerKg(updated.getPricePerKg());
        if (updated.getCurrency() != null) existing.setCurrency(updated.getCurrency());
        if (updated.getQualityGrade() != null) existing.setQualityGrade(updated.getQualityGrade());
        if (updated.getDemandLevel() != null) existing.setDemandLevel(updated.getDemandLevel());
        if (updated.getSupplyLevel() != null) existing.setSupplyLevel(updated.getSupplyLevel());
        if (updated.getPriceTrend() != null) existing.setPriceTrend(updated.getPriceTrend());
        if (updated.getSeasonalFactor() != null) existing.setSeasonalFactor(updated.getSeasonalFactor());
        if (updated.getTransportCost() != null) existing.setTransportCost(updated.getTransportCost());
        if (updated.getStorageCost() != null) existing.setStorageCost(updated.getStorageCost());
        if (updated.getProcessingCost() != null) existing.setProcessingCost(updated.getProcessingCost());
        if (updated.getDataSource() != null) existing.setDataSource(updated.getDataSource());
        if (updated.getDataCollector() != null) existing.setDataCollector(updated.getDataCollector());
        if (updated.getReliabilityScore() != null) existing.setReliabilityScore(updated.getReliabilityScore());
        if (updated.getNotes() != null) existing.setNotes(updated.getNotes());
    }

    private BigDecimal calculateAveragePrice(List<MarketPrice> prices) {
        if (prices.isEmpty()) return BigDecimal.ZERO;

        BigDecimal total = prices.stream()
                .filter(p -> p.getPricePerKg() != null)
                .map(MarketPrice::getPricePerKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(BigDecimal.valueOf(prices.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateMinPrice(List<MarketPrice> prices) {
        return prices.stream()
                .filter(p -> p.getPricePerKg() != null)
                .map(MarketPrice::getPricePerKg)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateMaxPrice(List<MarketPrice> prices) {
        return prices.stream()
                .filter(p -> p.getPricePerKg() != null)
                .map(MarketPrice::getPricePerKg)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    // Bulk operations
    public List<MarketPrice> saveAll(List<MarketPrice> marketPrices) {
        marketPrices.forEach(this::validateMarketPrice);
        return marketPriceRepository.saveAll(marketPrices);
    }

    public void deleteAll(List<String> ids) {
        ids.forEach(id -> {
            if (!marketPriceRepository.existsById(id)) {
                throw new RuntimeException("Market price not found with id: " + id);
            }
        });
        marketPriceRepository.deleteAllById(ids);
    }

    // Search functionality
    public Page<MarketPrice> searchMarketPrices(String cropId, MarketType marketType,
                                                String location, LocalDate startDate,
                                                LocalDate endDate, BigDecimal minPrice,
                                                BigDecimal maxPrice, Pageable pageable) {
        // Complex search logic - in a real application, you'd use Specifications
        if (cropId != null && startDate != null && endDate != null) {
            return marketPriceRepository.findByCropIdAndPriceDateBetween(cropId, startDate, endDate, pageable);
        } else if (cropId != null) {
            return marketPriceRepository.findByCropId(cropId, pageable);
        } else if (marketType != null) {
            return marketPriceRepository.findByMarketType(marketType, pageable);
        } else if (location != null) {
            return marketPriceRepository.findByLocationContainingIgnoreCase(location, pageable);
        } else if (minPrice != null && maxPrice != null) {
            return marketPriceRepository.findByPricePerKgBetween(minPrice, maxPrice, pageable);
        } else {
            return marketPriceRepository.findAll(pageable);
        }
    }

    // Alert and notification methods
    public List<MarketPrice> findPriceAlerts(String cropId, BigDecimal targetPrice, boolean isAbove) {
        List<MarketPrice> recentPrices = findRecentPricesByCrop(cropId, 7);

        return recentPrices.stream()
                .filter(p -> isAbove ?
                        p.getPricePerKg().compareTo(targetPrice) > 0 :
                        p.getPricePerKg().compareTo(targetPrice) < 0)
                .collect(Collectors.toList());
    }

    public List<MarketPrice> findVolatilePrices(String cropId, double volatilityThreshold) {
        List<MarketPrice> prices = findRecentPricesByCrop(cropId, 30);

        if (prices.size() < 3) return Collections.emptyList();

        BigDecimal avgPrice = calculateAveragePrice(prices);

        return prices.stream()
                .filter(p -> {
                    BigDecimal deviation = p.getPricePerKg().subtract(avgPrice).abs();
                    double volatility = deviation.divide(avgPrice, 4, RoundingMode.HALF_UP).doubleValue();
                    return volatility > volatilityThreshold;
                })
                .collect(Collectors.toList());
    }
}