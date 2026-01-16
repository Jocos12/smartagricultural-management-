package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.Inventory;
import SmartAgricultural.Management.Model.CropProduction;
import SmartAgricultural.Management.Repository.InventoryRepository;
import SmartAgricultural.Management.Repository.CropProductionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryPredictionService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CropProductionRepository cropProductionRepository;

    /**
     * Calcule les prédictions globales d'inventaire
     */
    public Map<String, Object> getGlobalInventoryPredictions() {
        Map<String, Object> predictions = new HashMap<>();

        List<Inventory> allInventory = inventoryRepository.findAll();

        // 1. Stock Coverage Analysis
        predictions.put("stockCoverage", calculateStockCoverage(allInventory));

        // 2. Consumption Predictions
        predictions.put("consumptionForecasts", calculateConsumptionForecasts(allInventory));

        // 3. Deficit Predictions
        predictions.put("deficitAnalysis", calculateDeficitPredictions(allInventory));

        // 4. Capacity Utilization
        predictions.put("capacityUtilization", calculateCapacityUtilization(allInventory));

        // 5. Inventory Trends
        predictions.put("inventoryTrends", calculateInventoryTrends(allInventory));

        // 6. Food Security Score
        predictions.put("foodSecurityScore", calculateFoodSecurityScore(allInventory));

        // 7. Restock Recommendations
        predictions.put("restockRecommendations", generateRestockRecommendations(allInventory));

        return predictions;
    }

    /**
     * ✅ CORRECTION - Prédictions par culture
     */
    public Map<String, Object> getCropSpecificPredictions(String cropId) {
        Map<String, Object> predictions = new HashMap<>();

        // ✅ FIX: Utiliser les méthodes qui retournent List directement
        List<Inventory> cropInventory = inventoryRepository.findByCropId(cropId);
        List<CropProduction> productions = cropProductionRepository.findByCropIdOrderByCreatedAtDesc(cropId);

        predictions.put("currentStock", calculateCurrentStock(cropInventory));
        predictions.put("averageDailyConsumption", calculateAverageDailyConsumption(cropInventory));
        predictions.put("stockoutRisk", calculateStockoutRisk(cropInventory, productions));
        predictions.put("optimalRestockDate", calculateOptimalRestockDate(cropInventory, productions));
        predictions.put("expectedYield", predictExpectedYield(productions));
        predictions.put("seasonalPattern", analyzeSeasonalPattern(cropInventory));

        return predictions;
    }

    // ============= MÉTHODES DE CALCUL DÉTAILLÉES =============

    private Map<String, Object> calculateStockCoverage(List<Inventory> inventories) {
        Map<String, Object> coverage = new HashMap<>();

        BigDecimal totalStock = inventories.stream()
                .map(Inventory::getAvailableQuantity)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcul de la consommation quotidienne moyenne basée sur l'historique
        BigDecimal avgDailyConsumption = calculateHistoricalDailyConsumption(inventories);

        // Jours de couverture
        int coverageDays = avgDailyConsumption.compareTo(BigDecimal.ZERO) > 0
                ? totalStock.divide(avgDailyConsumption, 0, RoundingMode.DOWN).intValue()
                : 0;

        coverage.put("totalAvailableStock", totalStock);
        coverage.put("averageDailyConsumption", avgDailyConsumption);
        coverage.put("coverageDays", coverageDays);
        coverage.put("coverageWeeks", coverageDays / 7);
        coverage.put("coverageMonths", coverageDays / 30);
        coverage.put("adequacyLevel", getAdequacyLevel(coverageDays));

        return coverage;
    }

    private BigDecimal calculateHistoricalDailyConsumption(List<Inventory> inventories) {
        // Analyse des mouvements d'inventaire sur les 30 derniers jours
        Map<String, BigDecimal> dailyChanges = new HashMap<>();

        for (Inventory inv : inventories) {
            if (inv.getStorageDate() != null && inv.getLastMovementDate() != null) {
                long daysBetween = ChronoUnit.DAYS.between(
                        inv.getStorageDate(),
                        LocalDate.now()
                );

                if (daysBetween > 0) {
                    BigDecimal initialQty = inv.getCurrentQuantity()
                            .add(inv.getReservedQuantity() != null ? inv.getReservedQuantity() : BigDecimal.ZERO);
                    BigDecimal consumed = initialQty.subtract(inv.getAvailableQuantity() != null ?
                            inv.getAvailableQuantity() : BigDecimal.ZERO);
                    BigDecimal dailyRate = consumed.divide(
                            BigDecimal.valueOf(daysBetween),
                            2,
                            RoundingMode.HALF_UP
                    );

                    String dateKey = inv.getStorageDate().toString();
                    dailyChanges.put(dateKey, dailyRate);
                }
            }
        }

        return dailyChanges.isEmpty() ? BigDecimal.ZERO
                : dailyChanges.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(dailyChanges.size()), 2, RoundingMode.HALF_UP);
    }

    private Map<String, Object> calculateConsumptionForecasts(List<Inventory> inventories) {
        Map<String, Object> forecasts = new HashMap<>();

        BigDecimal dailyConsumption = calculateHistoricalDailyConsumption(inventories);

        forecasts.put("next7Days", dailyConsumption.multiply(BigDecimal.valueOf(7)));
        forecasts.put("next30Days", dailyConsumption.multiply(BigDecimal.valueOf(30)));
        forecasts.put("next90Days", dailyConsumption.multiply(BigDecimal.valueOf(90)));
        forecasts.put("nextQuarter", dailyConsumption.multiply(BigDecimal.valueOf(90)));
        forecasts.put("nextYear", dailyConsumption.multiply(BigDecimal.valueOf(365)));

        // Ajustements saisonniers
        double seasonalFactor = getSeasonalAdjustmentFactor();
        forecasts.put("seasonallyAdjustedDaily",
                dailyConsumption.multiply(BigDecimal.valueOf(seasonalFactor)));

        return forecasts;
    }

    private Map<String, Object> calculateDeficitPredictions(List<Inventory> inventories) {
        Map<String, Object> deficit = new HashMap<>();

        BigDecimal totalStock = inventories.stream()
                .map(Inventory::getAvailableQuantity)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal dailyConsumption = calculateHistoricalDailyConsumption(inventories);
        BigDecimal monthlyNeed = dailyConsumption.multiply(BigDecimal.valueOf(30));

        BigDecimal currentDeficit = monthlyNeed.subtract(totalStock);

        deficit.put("currentDeficit", currentDeficit.max(BigDecimal.ZERO));
        deficit.put("deficitPercentage", calculateDeficitPercentage(totalStock, monthlyNeed));
        deficit.put("estimatedStockoutDate", calculateStockoutDate(totalStock, dailyConsumption));
        deficit.put("criticalLevel", currentDeficit.compareTo(BigDecimal.ZERO) > 0);

        // Prédiction déficit futur
        List<Map<String, Object>> futureDeficits = new ArrayList<>();
        for (int month = 1; month <= 6; month++) {
            Map<String, Object> monthDeficit = new HashMap<>();
            BigDecimal projectedStock = totalStock.subtract(
                    dailyConsumption.multiply(BigDecimal.valueOf(30 * month))
            );
            monthDeficit.put("month", month);
            monthDeficit.put("projectedStock", projectedStock);
            monthDeficit.put("deficit", projectedStock.compareTo(BigDecimal.ZERO) < 0
                    ? projectedStock.abs() : BigDecimal.ZERO);
            futureDeficits.add(monthDeficit);
        }
        deficit.put("futureProjections", futureDeficits);

        return deficit;
    }

    private Map<String, Object> calculateCapacityUtilization(List<Inventory> inventories) {
        Map<String, Object> utilization = new HashMap<>();

        BigDecimal totalCapacity = inventories.stream()
                .map(Inventory::getStorageCapacity)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalUsed = inventories.stream()
                .map(Inventory::getCurrentQuantity)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal utilizationRate = totalCapacity.compareTo(BigDecimal.ZERO) > 0
                ? totalUsed.divide(totalCapacity, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        utilization.put("totalCapacity", totalCapacity);
        utilization.put("currentlyUsed", totalUsed);
        utilization.put("availableCapacity", totalCapacity.subtract(totalUsed));
        utilization.put("utilizationPercentage", utilizationRate);
        utilization.put("status", getCapacityStatus(utilizationRate));

        // Par type de facilité
        Map<String, Map<String, BigDecimal>> byFacility = inventories.stream()
                .collect(Collectors.groupingBy(
                        inv -> inv.getFacilityType().toString(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    Map<String, BigDecimal> metrics = new HashMap<>();
                                    BigDecimal capacity = list.stream()
                                            .map(Inventory::getStorageCapacity)
                                            .filter(Objects::nonNull)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    BigDecimal used = list.stream()
                                            .map(Inventory::getCurrentQuantity)
                                            .filter(Objects::nonNull)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    metrics.put("capacity", capacity);
                                    metrics.put("used", used);
                                    metrics.put("utilization", capacity.compareTo(BigDecimal.ZERO) > 0
                                            ? used.divide(capacity, 4, RoundingMode.HALF_UP)
                                            .multiply(BigDecimal.valueOf(100))
                                            : BigDecimal.ZERO);
                                    return metrics;
                                }
                        )
                ));

        utilization.put("byFacilityType", byFacility);

        return utilization;
    }

    private Map<String, Object> calculateInventoryTrends(List<Inventory> inventories) {
        Map<String, Object> trends = new HashMap<>();

        // Tri par date
        List<Inventory> sorted = inventories.stream()
                .filter(inv -> inv.getStorageDate() != null)
                .sorted(Comparator.comparing(Inventory::getStorageDate))
                .collect(Collectors.toList());

        // Calcul de tendance mensuelle
        Map<String, BigDecimal> monthlyTrends = new TreeMap<>();
        for (Inventory inv : sorted) {
            String monthKey = inv.getStorageDate().getYear() + "-" +
                    String.format("%02d", inv.getStorageDate().getMonthValue());
            monthlyTrends.merge(monthKey, inv.getCurrentQuantity(), BigDecimal::add);
        }

        trends.put("monthlyTrends", monthlyTrends);
        trends.put("trendDirection", calculateTrendDirection(monthlyTrends));
        trends.put("growthRate", calculateGrowthRate(monthlyTrends));

        return trends;
    }

    private Map<String, Object> calculateFoodSecurityScore(List<Inventory> inventories) {
        Map<String, Object> score = new HashMap<>();

        // Facteurs de sécurité alimentaire
        int diversityScore = calculateDiversityScore(inventories);
        int adequacyScore = calculateAdequacyScore(inventories);
        int qualityScore = calculateQualityScore(inventories);
        int accessibilityScore = calculateAccessibilityScore(inventories);

        int totalScore = (diversityScore + adequacyScore + qualityScore + accessibilityScore) / 4;

        score.put("overallScore", totalScore);
        score.put("diversity", diversityScore);
        score.put("adequacy", adequacyScore);
        score.put("quality", qualityScore);
        score.put("accessibility", accessibilityScore);
        score.put("rating", getFoodSecurityRating(totalScore));
        score.put("recommendations", generateSecurityRecommendations(totalScore, inventories));

        return score;
    }

    private List<Map<String, Object>> generateRestockRecommendations(List<Inventory> inventories) {
        List<Map<String, Object>> recommendations = new ArrayList<>();

        // Grouper par culture
        Map<String, List<Inventory>> byCrop = inventories.stream()
                .collect(Collectors.groupingBy(Inventory::getCropId));

        for (Map.Entry<String, List<Inventory>> entry : byCrop.entrySet()) {
            String cropId = entry.getKey();
            List<Inventory> cropInventories = entry.getValue();

            BigDecimal totalStock = cropInventories.stream()
                    .map(Inventory::getAvailableQuantity)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal minStock = cropInventories.stream()
                    .map(Inventory::getMinimumStockLevel)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::max);

            if (totalStock.compareTo(minStock) < 0) {
                Map<String, Object> recommendation = new HashMap<>();
                recommendation.put("cropId", cropId);
                recommendation.put("currentStock", totalStock);
                recommendation.put("minimumRequired", minStock);
                recommendation.put("recommendedRestock", minStock.subtract(totalStock));
                recommendation.put("priority", calculateRestockPriority(totalStock, minStock));
                recommendation.put("urgency", calculateUrgency(totalStock, minStock));
                recommendations.add(recommendation);
            }
        }

        // Trier par priorité
        recommendations.sort((a, b) ->
                ((String) b.get("priority")).compareTo((String) a.get("priority"))
        );

        return recommendations;
    }

    // ============= MÉTHODES UTILITAIRES =============

    private String getAdequacyLevel(int days) {
        if (days < 7) return "CRITICAL";
        if (days < 30) return "LOW";
        if (days < 90) return "MODERATE";
        return "ADEQUATE";
    }

    private BigDecimal calculateDeficitPercentage(BigDecimal stock, BigDecimal need) {
        if (need.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return stock.divide(need, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private LocalDate calculateStockoutDate(BigDecimal stock, BigDecimal dailyConsumption) {
        if (dailyConsumption.compareTo(BigDecimal.ZERO) == 0) return null;
        int daysUntilStockout = stock.divide(dailyConsumption, 0, RoundingMode.DOWN).intValue();
        return LocalDate.now().plusDays(daysUntilStockout);
    }

    private double getSeasonalAdjustmentFactor() {
        int month = LocalDate.now().getMonthValue();
        // Ajustement basé sur la saisonnalité agricole au Rwanda
        if (month >= 3 && month <= 5) return 0.8; // Saison de récolte
        if (month >= 9 && month <= 11) return 0.9; // Deuxième saison
        return 1.2; // Période de soudure
    }

    private String getCapacityStatus(BigDecimal utilizationRate) {
        if (utilizationRate.compareTo(BigDecimal.valueOf(90)) > 0) return "CRITICAL";
        if (utilizationRate.compareTo(BigDecimal.valueOf(75)) > 0) return "HIGH";
        if (utilizationRate.compareTo(BigDecimal.valueOf(50)) > 0) return "OPTIMAL";
        return "LOW";
    }

    private String calculateTrendDirection(Map<String, BigDecimal> trends) {
        if (trends.size() < 2) return "STABLE";

        List<BigDecimal> values = new ArrayList<>(trends.values());
        BigDecimal first = values.get(0);
        BigDecimal last = values.get(values.size() - 1);

        if (last.compareTo(first.multiply(BigDecimal.valueOf(1.1))) > 0) return "INCREASING";
        if (last.compareTo(first.multiply(BigDecimal.valueOf(0.9))) < 0) return "DECREASING";
        return "STABLE";
    }

    private BigDecimal calculateGrowthRate(Map<String, BigDecimal> trends) {
        if (trends.size() < 2) return BigDecimal.ZERO;

        List<BigDecimal> values = new ArrayList<>(trends.values());
        BigDecimal first = values.get(0);
        BigDecimal last = values.get(values.size() - 1);

        if (first.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        return last.subtract(first)
                .divide(first, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private int calculateDiversityScore(List<Inventory> inventories) {
        Set<String> uniqueCrops = inventories.stream()
                .map(Inventory::getCropId)
                .collect(Collectors.toSet());

        int cropCount = uniqueCrops.size();
        if (cropCount >= 10) return 100;
        if (cropCount >= 7) return 80;
        if (cropCount >= 5) return 60;
        if (cropCount >= 3) return 40;
        return 20;
    }

    private int calculateAdequacyScore(List<Inventory> inventories) {
        Map<String, Object> coverage = calculateStockCoverage(inventories);
        int days = (int) coverage.get("coverageDays");

        if (days >= 180) return 100;
        if (days >= 90) return 80;
        if (days >= 60) return 60;
        if (days >= 30) return 40;
        return 20;
    }

    private int calculateQualityScore(List<Inventory> inventories) {
        long goodQuality = inventories.stream()
                .filter(inv -> "Grade A".equalsIgnoreCase(inv.getQualityGrade()) ||
                        "Grade B".equalsIgnoreCase(inv.getQualityGrade()))
                .count();

        double ratio = inventories.isEmpty() ? 0 :
                (double) goodQuality / inventories.size();

        return (int) (ratio * 100);
    }

    private int calculateAccessibilityScore(List<Inventory> inventories) {
        long available = inventories.stream()
                .filter(inv -> inv.getStatus() == Inventory.InventoryStatus.AVAILABLE)
                .count();

        double ratio = inventories.isEmpty() ? 0 :
                (double) available / inventories.size();

        return (int) (ratio * 100);
    }

    private String getFoodSecurityRating(int score) {
        if (score >= 80) return "EXCELLENT";
        if (score >= 60) return "GOOD";
        if (score >= 40) return "MODERATE";
        if (score >= 20) return "POOR";
        return "CRITICAL";
    }

    private List<String> generateSecurityRecommendations(int score, List<Inventory> inventories) {
        List<String> recommendations = new ArrayList<>();

        if (score < 40) {
            recommendations.add("Urgent: Increase crop diversity to improve food security");
            recommendations.add("Critical: Immediate restocking required for essential crops");
        }

        if (score < 60) {
            recommendations.add("Improve storage capacity utilization");
            recommendations.add("Enhance quality control measures");
        }

        if (score < 80) {
            recommendations.add("Consider seasonal adjustments in inventory planning");
            recommendations.add("Optimize stock rotation practices");
        }

        return recommendations;
    }

    private String calculateRestockPriority(BigDecimal current, BigDecimal minimum) {
        if (minimum.compareTo(BigDecimal.ZERO) == 0) return "LOW";

        BigDecimal ratio = current.divide(minimum, 4, RoundingMode.HALF_UP);

        if (ratio.compareTo(BigDecimal.valueOf(0.25)) < 0) return "CRITICAL";
        if (ratio.compareTo(BigDecimal.valueOf(0.5)) < 0) return "HIGH";
        if (ratio.compareTo(BigDecimal.valueOf(0.75)) < 0) return "MEDIUM";
        return "LOW";
    }

    private String calculateUrgency(BigDecimal current, BigDecimal minimum) {
        if (minimum.compareTo(BigDecimal.ZERO) == 0) return "PLANNED";

        BigDecimal deficit = minimum.subtract(current);
        BigDecimal deficitPercentage = deficit.divide(minimum, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        if (deficitPercentage.compareTo(BigDecimal.valueOf(75)) > 0) return "IMMEDIATE";
        if (deficitPercentage.compareTo(BigDecimal.valueOf(50)) > 0) return "URGENT";
        if (deficitPercentage.compareTo(BigDecimal.valueOf(25)) > 0) return "SOON";
        return "PLANNED";
    }

    private BigDecimal calculateCurrentStock(List<Inventory> inventories) {
        return inventories.stream()
                .map(Inventory::getAvailableQuantity)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateAverageDailyConsumption(List<Inventory> inventories) {
        return calculateHistoricalDailyConsumption(inventories);
    }

    private Map<String, Object> calculateStockoutRisk(List<Inventory> inventories,
                                                      List<CropProduction> productions) {
        Map<String, Object> risk = new HashMap<>();

        BigDecimal currentStock = calculateCurrentStock(inventories);
        BigDecimal dailyConsumption = calculateAverageDailyConsumption(inventories);

        int daysUntilStockout = dailyConsumption.compareTo(BigDecimal.ZERO) > 0
                ? currentStock.divide(dailyConsumption, 0, RoundingMode.DOWN).intValue()
                : Integer.MAX_VALUE;

        risk.put("daysUntilStockout", daysUntilStockout);
        risk.put("riskLevel", daysUntilStockout < 30 ? "HIGH" :
                daysUntilStockout < 60 ? "MEDIUM" : "LOW");
        risk.put("stockoutDate", LocalDate.now().plusDays(daysUntilStockout));

        return risk;
    }

    private LocalDate calculateOptimalRestockDate(List<Inventory> inventories,
                                                  List<CropProduction> productions) {
        Map<String, Object> stockoutRisk = calculateStockoutRisk(inventories, productions);
        int daysUntil = (int) stockoutRisk.get("daysUntilStockout");

        // Recommander de réapprovisionner 15 jours avant la rupture
        return LocalDate.now().plusDays(Math.max(0, daysUntil - 15));
    }

    private BigDecimal predictExpectedYield(List<CropProduction> productions) {
        if (productions.isEmpty()) return BigDecimal.ZERO;

        return productions.stream()
                .filter(p -> p.getExpectedYield() != null)
                .map(CropProduction::getExpectedYield)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(productions.size()), 2, RoundingMode.HALF_UP);
    }

    private Map<String, Object> analyzeSeasonalPattern(List<Inventory> inventories) {
        Map<String, Object> pattern = new HashMap<>();

        Map<Integer, BigDecimal> monthlyAvg = inventories.stream()
                .filter(inv -> inv.getStorageDate() != null)
                .collect(Collectors.groupingBy(
                        inv -> inv.getStorageDate().getMonthValue(),
                        Collectors.collectingAndThen(
                                Collectors.mapping(Inventory::getCurrentQuantity, Collectors.toList()),
                                list -> list.stream()
                                        .filter(Objects::nonNull)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                                        .divide(BigDecimal.valueOf(Math.max(1, list.size())), 2, RoundingMode.HALF_UP)
                        )
                ));

        pattern.put("monthlyAverages", monthlyAvg);
        pattern.put("peakMonth", monthlyAvg.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(1));
        pattern.put("lowMonth", monthlyAvg.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(1));

        return pattern;
    }
}