package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.MarketPrice;
import SmartAgricultural.Management.Model.MarketPrice.MarketType;
import SmartAgricultural.Management.Model.MarketPrice.DemandLevel;
import SmartAgricultural.Management.Model.MarketPrice.SupplyLevel;
import SmartAgricultural.Management.Model.MarketPrice.PriceTrend;
import SmartAgricultural.Management.Service.MarketPriceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/market-prices")
@CrossOrigin(origins = "*")
public class MarketPriceController {

    private final MarketPriceService marketPriceService;

    @Autowired
    public MarketPriceController(MarketPriceService marketPriceService) {
        this.marketPriceService = marketPriceService;
    }

    // ========== BASIC CRUD OPERATIONS ==========

    @PostMapping
    public ResponseEntity<?> createMarketPrice(@Valid @RequestBody MarketPrice marketPrice) {
        try {
            MarketPrice savedPrice = marketPriceService.save(marketPrice);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPrice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating market price: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMarketPriceById(@PathVariable String id) {
        try {
            Optional<MarketPrice> marketPrice = marketPriceService.findById(id);
            if (marketPrice.isPresent()) {
                return ResponseEntity.ok(marketPrice.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving market price: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllMarketPrices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "priceDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            Page<MarketPrice> marketPrices = marketPriceService.findAllSorted(page, size, sortBy, sortDirection);
            return ResponseEntity.ok(marketPrices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving market prices: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMarketPrice(@PathVariable String id,
                                               @Valid @RequestBody MarketPrice marketPrice) {
        try {
            MarketPrice updatedPrice = marketPriceService.update(id, marketPrice);
            return ResponseEntity.ok(updatedPrice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating market price: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMarketPrice(@PathVariable String id) {
        try {
            marketPriceService.deleteById(id);
            return ResponseEntity.ok("Market price deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting market price: " + e.getMessage());
        }
    }

    // ========== CROP-SPECIFIC ENDPOINTS ==========

    @GetMapping("/crop/{cropId}")
    public ResponseEntity<?> getPricesByCrop(
            @PathVariable String cropId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<MarketPrice> prices = marketPriceService.findByCropId(cropId, pageable);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving crop prices: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/latest")
    public ResponseEntity<?> getLatestPriceByCrop(@PathVariable String cropId) {
        try {
            Optional<MarketPrice> latestPrice = marketPriceService.findLatestPriceByCrop(cropId);
            if (latestPrice.isPresent()) {
                return ResponseEntity.ok(latestPrice.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving latest crop price: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/recent")
    public ResponseEntity<?> getRecentPricesByCrop(
            @PathVariable String cropId,
            @RequestParam(defaultValue = "30") int days) {
        try {
            List<MarketPrice> prices = marketPriceService.findRecentPricesByCrop(cropId, days);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving recent crop prices: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/history")
    public ResponseEntity<?> getCropPriceHistory(
            @PathVariable String cropId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<MarketPrice> prices = marketPriceService.findByCropAndDateRange(cropId, startDate, endDate);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving crop price history: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/monthly-prices")
    public ResponseEntity<?> getMonthlyPrices(
            @PathVariable String cropId,
            @RequestParam(defaultValue = "2024") int year) {
        try {
            List<Object[]> monthlyPrices = marketPriceService.getMonthlyAveragePrices(cropId, year);
            return ResponseEntity.ok(monthlyPrices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving monthly prices: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/daily-stats")
    public ResponseEntity<?> getDailyStatistics(
            @PathVariable String cropId,
            @RequestParam(defaultValue = "30") int days) {
        try {
            List<Object[]> stats = marketPriceService.getDailyPriceStats(cropId, days);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving daily statistics: " + e.getMessage());
        }
    }

    // ========== MARKET-SPECIFIC ENDPOINTS ==========

    @GetMapping("/market/{marketName}")
    public ResponseEntity<?> getPricesByMarket(@PathVariable String marketName) {
        try {
            List<MarketPrice> prices = marketPriceService.findByMarketName(marketName);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving market prices: " + e.getMessage());
        }
    }

    @GetMapping("/market-type/{marketType}")
    public ResponseEntity<?> getPricesByMarketType(@PathVariable MarketType marketType) {
        try {
            List<MarketPrice> prices = marketPriceService.findByMarketType(marketType);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving prices by market type: " + e.getMessage());
        }
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<?> getPricesByLocation(@PathVariable String location) {
        try {
            List<MarketPrice> prices = marketPriceService.findByLocation(location);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving prices by location: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/market-type/{marketType}")
    public ResponseEntity<?> getPricesByCropAndMarketType(
            @PathVariable String cropId,
            @PathVariable MarketType marketType) {
        try {
            List<MarketPrice> prices = marketPriceService.findByCropAndMarketType(cropId, marketType);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving prices by crop and market type: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/location/{location}")
    public ResponseEntity<?> getPricesByCropAndLocation(
            @PathVariable String cropId,
            @PathVariable String location) {
        try {
            List<MarketPrice> prices = marketPriceService.findByCropAndLocation(cropId, location);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving prices by crop and location: " + e.getMessage());
        }
    }

    // ========== DATE-BASED ENDPOINTS ==========

    @GetMapping("/date-range")
    public ResponseEntity<?> getPricesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<MarketPrice> prices = marketPriceService.findByDateRange(startDate, endDate, pageable);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving prices by date range: " + e.getMessage());
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentPrices(@RequestParam(defaultValue = "7") int days) {
        try {
            List<MarketPrice> prices = marketPriceService.findRecentPrices(days);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving recent prices: " + e.getMessage());
        }
    }

    // ========== PRICE-BASED ENDPOINTS ==========

    @GetMapping("/price-range")
    public ResponseEntity<?> getPricesByRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        try {
            List<MarketPrice> prices = marketPriceService.findByPriceRange(minPrice, maxPrice);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving prices by range: " + e.getMessage());
        }
    }

    @GetMapping("/high-prices")
    public ResponseEntity<?> getHighPrices(
            @RequestParam(defaultValue = "1000") BigDecimal threshold) {
        try {
            List<MarketPrice> prices = marketPriceService.findHighPrices(threshold);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving high prices: " + e.getMessage());
        }
    }

    @GetMapping("/low-prices")
    public ResponseEntity<?> getLowPrices(
            @RequestParam(defaultValue = "100") BigDecimal threshold) {
        try {
            List<MarketPrice> prices = marketPriceService.findLowPrices(threshold);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving low prices: " + e.getMessage());
        }
    }

    // ========== MARKET CONDITION ENDPOINTS ==========

    @GetMapping("/demand/{demandLevel}")
    public ResponseEntity<?> getPricesByDemandLevel(@PathVariable DemandLevel demandLevel) {
        try {
            List<MarketPrice> prices = marketPriceService.findByDemandLevel(demandLevel);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving prices by demand level: " + e.getMessage());
        }
    }

    @GetMapping("/supply/{supplyLevel}")
    public ResponseEntity<?> getPricesBySupplyLevel(@PathVariable SupplyLevel supplyLevel) {
        try {
            List<MarketPrice> prices = marketPriceService.findBySupplyLevel(supplyLevel);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving prices by supply level: " + e.getMessage());
        }
    }

    @GetMapping("/trend/{priceTrend}")
    public ResponseEntity<?> getPricesByTrend(@PathVariable PriceTrend priceTrend) {
        try {
            List<MarketPrice> prices = marketPriceService.findByPriceTrend(priceTrend);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving prices by trend: " + e.getMessage());
        }
    }

    @GetMapping("/opportunities")
    public ResponseEntity<?> getMarketOpportunities() {
        try {
            List<MarketPrice> opportunities = marketPriceService.findMarketOpportunities();
            return ResponseEntity.ok(opportunities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving market opportunities: " + e.getMessage());
        }
    }

    // ========== STATISTICS AND ANALYTICS ENDPOINTS ==========

    @GetMapping("/crop/{cropId}/stats")
    public ResponseEntity<?> getCropPriceStatistics(
            @PathVariable String cropId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Map<String, Object> stats = Map.of(
                    "averagePrice", marketPriceService.getAveragePrice(cropId, startDate, endDate),
                    "minPrice", marketPriceService.getMinPrice(cropId, startDate, endDate),
                    "maxPrice", marketPriceService.getMaxPrice(cropId, startDate, endDate),
                    "priceCount", marketPriceService.getPriceCount(cropId, startDate, endDate),
                    "priceVolatility", marketPriceService.getPriceVolatility(cropId, startDate, endDate)
            );
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving crop statistics: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/report")
    public ResponseEntity<?> generateCropPriceReport(
            @PathVariable String cropId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Map<String, Object> report = marketPriceService.generateCropPriceReport(cropId, startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating crop price report: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/market-analysis")
    public ResponseEntity<?> getMarketAnalysis(@PathVariable String cropId) {
        try {
            List<Object[]> analysis = marketPriceService.getMarketAnalysis(cropId);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving market analysis: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/market-type-analysis")
    public ResponseEntity<?> getMarketTypeAnalysis(@PathVariable String cropId) {
        try {
            List<Object[]> analysis = marketPriceService.getAveragePriceByMarketType(cropId);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving market type analysis: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/location-analysis")
    public ResponseEntity<?> getLocationAnalysis(@PathVariable String cropId) {
        try {
            List<Object[]> analysis = marketPriceService.getAveragePriceByLocation(cropId);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving location analysis: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/demand-supply-analysis")
    public ResponseEntity<?> getDemandSupplyAnalysis(@PathVariable String cropId) {
        try {
            List<Object[]> analysis = marketPriceService.getDemandSupplyPriceAnalysis(cropId);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving demand-supply analysis: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/trend-analysis")
    public ResponseEntity<?> getPriceTrendAnalysis(
            @PathVariable String cropId,
            @RequestParam(defaultValue = "90") int days) {
        try {
            List<Object[]> analysis = marketPriceService.getPriceTrendAnalysis(cropId, days);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving price trend analysis: " + e.getMessage());
        }
    }

    // ========== PREDICTION AND RECOMMENDATION ENDPOINTS ==========

    @GetMapping("/crop/{cropId}/recommendations")
    public ResponseEntity<?> getPriceRecommendations(@PathVariable String cropId) {
        try {
            List<String> recommendations = marketPriceService.generatePriceRecommendations(cropId);
            return ResponseEntity.ok(Map.of("recommendations", recommendations));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating price recommendations: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/price-prediction")
    public ResponseEntity<?> getPricePrediction(@PathVariable String cropId) {
        try {
            BigDecimal predictedPrice = marketPriceService.predictNextMonthPrice(cropId);
            if (predictedPrice != null) {
                return ResponseEntity.ok(Map.of("predictedPrice", predictedPrice, "period", "Next Month"));
            } else {
                return ResponseEntity.ok(Map.of("message", "Insufficient data for price prediction"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error predicting price: " + e.getMessage());
        }
    }

    // ========== PRICE COMPARISON ENDPOINTS ==========

    @GetMapping("/crop/{cropId}/price-comparison")
    public ResponseEntity<?> comparePrices(
            @PathVariable String cropId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<Object[]> comparison = marketPriceService.comparePricesByDate(cropId, date);
            return ResponseEntity.ok(comparison);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error comparing prices: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/seasonal-prices")
    public ResponseEntity<?> getSeasonalPrices(@PathVariable String cropId) {
        try {
            List<MarketPrice> seasonalPrices = marketPriceService.findSimilarSeasonalPrices(cropId);
            return ResponseEntity.ok(seasonalPrices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving seasonal prices: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/prices-near-date")
    public ResponseEntity<?> getPricesNearDate(
            @PathVariable String cropId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate) {
        try {
            List<MarketPrice> prices = marketPriceService.findPricesNearDate(cropId, targetDate);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving prices near date: " + e.getMessage());
        }
    }

    // ========== DATA QUALITY AND RELIABILITY ENDPOINTS ==========

    @GetMapping("/reliable")
    public ResponseEntity<?> getReliablePrices(
            @RequestParam(defaultValue = "4") Integer minScore) {
        try {
            List<MarketPrice> prices = marketPriceService.findReliablePrices(minScore);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving reliable prices: " + e.getMessage());
        }
    }

    @GetMapping("/data-source/{dataSource}")
    public ResponseEntity<?> getPricesByDataSource(@PathVariable String dataSource) {
        try {
            List<MarketPrice> prices = marketPriceService.findByDataSource(dataSource);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving prices by data source: " + e.getMessage());
        }
    }

    @GetMapping("/reliable-recent")
    public ResponseEntity<?> getReliableRecentPrices(
            @RequestParam(defaultValue = "4") Integer minScore,
            @RequestParam(defaultValue = "7") int days) {
        try {
            List<MarketPrice> prices = marketPriceService.findReliableRecentPrices(minScore, days);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving reliable recent prices: " + e.getMessage());
        }
    }

    // ========== REFERENCE DATA ENDPOINTS ==========

    @GetMapping("/crop/{cropId}/markets")
    public ResponseEntity<?> getMarketsByCrop(@PathVariable String cropId) {
        try {
            List<String> markets = marketPriceService.getMarketNamesByCrop(cropId);
            return ResponseEntity.ok(markets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving markets by crop: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/locations")
    public ResponseEntity<?> getLocationsByCrop(@PathVariable String cropId) {
        try {
            List<String> locations = marketPriceService.getLocationsByCrop(cropId);
            return ResponseEntity.ok(locations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving locations by crop: " + e.getMessage());
        }
    }

    // ========== ALERT ENDPOINTS ==========

    @GetMapping("/crop/{cropId}/price-alerts")
    public ResponseEntity<?> getPriceAlerts(
            @PathVariable String cropId,
            @RequestParam BigDecimal targetPrice,
            @RequestParam(defaultValue = "true") boolean isAbove) {
        try {
            List<MarketPrice> alerts = marketPriceService.findPriceAlerts(cropId, targetPrice, isAbove);
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving price alerts: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/volatile-prices")
    public ResponseEntity<?> getVolatilePrices(
            @PathVariable String cropId,
            @RequestParam(defaultValue = "0.2") double volatilityThreshold) {
        try {
            List<MarketPrice> volatilePrices = marketPriceService.findVolatilePrices(cropId, volatilityThreshold);
            return ResponseEntity.ok(volatilePrices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving volatile prices: " + e.getMessage());
        }
    }

    // ========== SEARCH ENDPOINT ==========

    @GetMapping("/search")
    public ResponseEntity<?> searchMarketPrices(
            @RequestParam(required = false) String cropId,
            @RequestParam(required = false) MarketType marketType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<MarketPrice> prices = marketPriceService.searchMarketPrices(
                    cropId, marketType, location, startDate, endDate, minPrice, maxPrice, pageable);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching market prices: " + e.getMessage());
        }
    }

    // ========== BULK OPERATIONS ENDPOINTS ==========

    @PostMapping("/bulk")
    public ResponseEntity<?> createBulkMarketPrices(@Valid @RequestBody List<MarketPrice> marketPrices) {
        try {
            List<MarketPrice> savedPrices = marketPriceService.saveAll(marketPrices);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPrices);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating bulk market prices: " + e.getMessage());
        }
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<?> deleteBulkMarketPrices(@RequestBody List<String> ids) {
        try {
            marketPriceService.deleteAll(ids);
            return ResponseEntity.ok("Bulk market prices deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting bulk market prices: " + e.getMessage());
        }
    }

// ========== UTILITY ENDPOINTS ==========

    @GetMapping("/market-types")
    public ResponseEntity<MarketType[]> getAllMarketTypes() {
        return ResponseEntity.ok(MarketType.values());
    }

    @GetMapping("/demand-levels")
    public ResponseEntity<DemandLevel[]> getAllDemandLevels() {
        return ResponseEntity.ok(DemandLevel.values());
    }

    @GetMapping("/supply-levels")
    public ResponseEntity<SupplyLevel[]> getAllSupplyLevels() {
        return ResponseEntity.ok(SupplyLevel.values());
    }

    @GetMapping("/price-trends")
    public ResponseEntity<PriceTrend[]> getAllPriceTrends() {
        return ResponseEntity.ok(PriceTrend.values());
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Map<String, Boolean>> checkIfExists(@PathVariable String id) {
        boolean exists = marketPriceService.existsById(id);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/count")
    public ResponseEntity<?> getTotalCount() {
        try {
            long totalCount = marketPriceService.findAll().size();
            return ResponseEntity.ok(Map.of("totalCount", totalCount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving total count: " + e.getMessage());
        }
    }

    @GetMapping("/crop/{cropId}/count")
    public ResponseEntity<?> getCountByCrop(@PathVariable String cropId) {
        try {
            long cropCount = marketPriceService.findByCropId(cropId).size();
            return ResponseEntity.ok(Map.of("cropCount", cropCount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving crop count: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "MarketPriceController",
                "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }

    // ========== EXCEPTION HANDLING ==========

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(IllegalArgumentException e) {
        Map<String, String> errorResponse = Map.of(
                "error", "Validation Error",
                "message", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> errorResponse = Map.of(
                "error", "Resource Not Found",
                "message", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception e) {
        Map<String, String> errorResponse = Map.of(
                "error", "Internal Server Error",
                "message", "An unexpected error occurred: " + e.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(
            org.springframework.web.bind.MethodArgumentNotValidException e) {
        Map<String, String> fieldErrors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> errorResponse = Map.of(
                "error", "Validation Failed",
                "message", "Invalid input data",
                "fieldErrors", fieldErrors,
                "timestamp", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(
            org.springframework.dao.DataIntegrityViolationException e) {
        Map<String, String> errorResponse = Map.of(
                "error", "Data Integrity Violation",
                "message", "Database constraint violation occurred",
                "timestamp", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException e) {
        Map<String, String> errorResponse = Map.of(
                "error", "Invalid Parameter Type",
                "message", "Invalid value for parameter: " + e.getName(),
                "timestamp", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // ========== ADDITIONAL HELPER ENDPOINTS ==========

    @GetMapping("/summary")
    public ResponseEntity<?> getMarketSummary() {
        try {
            List<MarketPrice> allPrices = marketPriceService.findRecentPrices(30);

            Map<String, Object> summary = Map.of(
                    "totalRecentPrices", allPrices.size(),
                    "averagePrice", calculateOverallAverage(allPrices),
                    "highestPrice", calculateHighestPrice(allPrices),
                    "lowestPrice", calculateLowestPrice(allPrices),
                    "marketTypesCount", countMarketTypes(allPrices),
                    "locationsCount", countLocations(allPrices),
                    "reliabilityDistribution", calculateReliabilityDistribution(allPrices)
            );

            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving market summary: " + e.getMessage());
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateMarketPrice(@Valid @RequestBody MarketPrice marketPrice) {
        try {
            // This endpoint just validates without saving
            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "message", "Market price data is valid",
                    "priceFormatted", marketPrice.getPriceFormatted(),
                    "marketSummary", marketPrice.getMarketSummary()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "message", "Validation failed: " + e.getMessage()
            ));
        }
    }

    // ========== PRIVATE HELPER METHODS ==========

    private BigDecimal calculateOverallAverage(List<MarketPrice> prices) {
        if (prices.isEmpty()) return BigDecimal.ZERO;

        BigDecimal sum = prices.stream()
                .filter(p -> p.getPricePerKg() != null)
                .map(MarketPrice::getPricePerKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(prices.size()), 2, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal calculateHighestPrice(List<MarketPrice> prices) {
        return prices.stream()
                .filter(p -> p.getPricePerKg() != null)
                .map(MarketPrice::getPricePerKg)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateLowestPrice(List<MarketPrice> prices) {
        return prices.stream()
                .filter(p -> p.getPricePerKg() != null)
                .map(MarketPrice::getPricePerKg)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private long countMarketTypes(List<MarketPrice> prices) {
        return prices.stream()
                .filter(p -> p.getMarketType() != null)
                .map(MarketPrice::getMarketType)
                .distinct()
                .count();
    }

    private long countLocations(List<MarketPrice> prices) {
        return prices.stream()
                .filter(p -> p.getLocation() != null)
                .map(MarketPrice::getLocation)
                .distinct()
                .count();
    }

    private Map<Integer, Long> calculateReliabilityDistribution(List<MarketPrice> prices) {
        return prices.stream()
                .filter(p -> p.getReliabilityScore() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        MarketPrice::getReliabilityScore,
                        java.util.stream.Collectors.counting()
                ));
    }
}