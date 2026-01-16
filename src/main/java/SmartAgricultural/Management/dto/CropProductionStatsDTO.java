package SmartAgricultural.Management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

/**
 * DTO pour les statistiques de production de cultures
 *
 * @author SmartAgricultural Management System
 * @version 1.0
 * @since 2024-01-01
 */
public class CropProductionStatsDTO {

    // Statistiques générales
    private Long totalProductions;
    private Long activeProductions;
    private Long harvestedProductions;
    private Long plannedProductions;
    private Long soldProductions;
    private Long overdueProductions;
    private Long organicProductions;
    private Long certifiedProductions;

    // Statistiques de superficie
    private BigDecimal totalAreaPlanted;
    private BigDecimal averageAreaPerProduction;
    private BigDecimal largestProduction;
    private BigDecimal smallestProduction;

    // Statistiques de rendement
    private BigDecimal averageYield;
    private BigDecimal highestYield;
    private BigDecimal lowestYield;
    private BigDecimal totalProduction;

    // Statistiques par méthode de production
    private Long conventionalProductions;
    private Long integratedProductions;

    // Statistiques par saison
    private Long seasonAProductions;
    private Long seasonBProductions;
    private Long seasonCProductions;
    private Long offSeasonProductions;

    // Statistiques par statut (détaillées)
    @JsonProperty("statusDistribution")
    private Map<String, Long> productionStatusCounts;

    // Statistiques par méthode (détaillées)
    @JsonProperty("methodDistribution")
    private Map<String, Long> productionMethodCounts;

    // Statistiques par saison (détaillées)
    @JsonProperty("seasonDistribution")
    private Map<String, Long> seasonCounts;

    // Statistiques par année
    @JsonProperty("yearlyStats")
    private Map<Integer, YearlyStats> yearlyStatistics;

    // Top productions
    @JsonProperty("topFarms")
    private List<FarmProductionSummary> topProductiveFarms;

    @JsonProperty("topCrops")
    private List<CropProductionSummary> topProductiveCrops;

    // Tendances
    private BigDecimal yieldTrend; // Pourcentage d'amélioration du rendement
    private BigDecimal productionTrend; // Pourcentage d'amélioration de la production
    private BigDecimal areaTrend; // Pourcentage d'augmentation de la superficie

    // Efficacité
    private BigDecimal averageEfficiency; // Rendement réel / rendement attendu
    private BigDecimal bestEfficiency;
    private BigDecimal worstEfficiency;

    // Constructeurs
    public CropProductionStatsDTO() {
        // Initialiser avec des valeurs par défaut
        this.totalProductions = 0L;
        this.activeProductions = 0L;
        this.harvestedProductions = 0L;
        this.plannedProductions = 0L;
        this.soldProductions = 0L;
        this.overdueProductions = 0L;
        this.organicProductions = 0L;
        this.certifiedProductions = 0L;
        this.conventionalProductions = 0L;
        this.integratedProductions = 0L;
        this.seasonAProductions = 0L;
        this.seasonBProductions = 0L;
        this.seasonCProductions = 0L;
        this.offSeasonProductions = 0L;

        this.totalAreaPlanted = BigDecimal.ZERO;
        this.averageAreaPerProduction = BigDecimal.ZERO;
        this.largestProduction = BigDecimal.ZERO;
        this.smallestProduction = BigDecimal.ZERO;
        this.averageYield = BigDecimal.ZERO;
        this.highestYield = BigDecimal.ZERO;
        this.lowestYield = BigDecimal.ZERO;
        this.totalProduction = BigDecimal.ZERO;
        this.yieldTrend = BigDecimal.ZERO;
        this.productionTrend = BigDecimal.ZERO;
        this.areaTrend = BigDecimal.ZERO;
        this.averageEfficiency = BigDecimal.ZERO;
        this.bestEfficiency = BigDecimal.ZERO;
        this.worstEfficiency = BigDecimal.ZERO;
    }

    // Classes internes pour les statistiques détaillées
    public static class YearlyStats {
        private Long productionCount;
        private BigDecimal totalArea;
        private BigDecimal totalProduction;
        private BigDecimal averageYield;
        private Long harvestedCount;
        private Long organicCount;

        public YearlyStats() {
        }

        public YearlyStats(Long productionCount, BigDecimal totalArea, BigDecimal totalProduction,
                           BigDecimal averageYield, Long harvestedCount, Long organicCount) {
            this.productionCount = productionCount;
            this.totalArea = totalArea;
            this.totalProduction = totalProduction;
            this.averageYield = averageYield;
            this.harvestedCount = harvestedCount;
            this.organicCount = organicCount;
        }

        // Getters and Setters
        public Long getProductionCount() { return productionCount; }
        public void setProductionCount(Long productionCount) { this.productionCount = productionCount; }
        public BigDecimal getTotalArea() { return totalArea; }
        public void setTotalArea(BigDecimal totalArea) { this.totalArea = totalArea; }
        public BigDecimal getTotalProduction() { return totalProduction; }
        public void setTotalProduction(BigDecimal totalProduction) { this.totalProduction = totalProduction; }
        public BigDecimal getAverageYield() { return averageYield; }
        public void setAverageYield(BigDecimal averageYield) { this.averageYield = averageYield; }
        public Long getHarvestedCount() { return harvestedCount; }
        public void setHarvestedCount(Long harvestedCount) { this.harvestedCount = harvestedCount; }
        public Long getOrganicCount() { return organicCount; }
        public void setOrganicCount(Long organicCount) { this.organicCount = organicCount; }
    }

    public static class FarmProductionSummary {
        private String farmId;
        private String farmName;
        private Long productionCount;
        private BigDecimal totalArea;
        private BigDecimal totalProduction;
        private BigDecimal averageYield;

        public FarmProductionSummary() {
        }

        public FarmProductionSummary(String farmId, String farmName, Long productionCount,
                                     BigDecimal totalArea, BigDecimal totalProduction, BigDecimal averageYield) {
            this.farmId = farmId;
            this.farmName = farmName;
            this.productionCount = productionCount;
            this.totalArea = totalArea;
            this.totalProduction = totalProduction;
            this.averageYield = averageYield;
        }

        // Getters and Setters
        public String getFarmId() { return farmId; }
        public void setFarmId(String farmId) { this.farmId = farmId; }
        public String getFarmName() { return farmName; }
        public void setFarmName(String farmName) { this.farmName = farmName; }
        public Long getProductionCount() { return productionCount; }
        public void setProductionCount(Long productionCount) { this.productionCount = productionCount; }
        public BigDecimal getTotalArea() { return totalArea; }
        public void setTotalArea(BigDecimal totalArea) { this.totalArea = totalArea; }
        public BigDecimal getTotalProduction() { return totalProduction; }
        public void setTotalProduction(BigDecimal totalProduction) { this.totalProduction = totalProduction; }
        public BigDecimal getAverageYield() { return averageYield; }
        public void setAverageYield(BigDecimal averageYield) { this.averageYield = averageYield; }
    }

    public static class CropProductionSummary {
        private String cropId;
        private String cropName;
        private Long productionCount;
        private BigDecimal totalArea;
        private BigDecimal totalProduction;
        private BigDecimal averageYield;

        public CropProductionSummary() {
        }

        public CropProductionSummary(String cropId, String cropName, Long productionCount,
                                     BigDecimal totalArea, BigDecimal totalProduction, BigDecimal averageYield) {
            this.cropId = cropId;
            this.cropName = cropName;
            this.productionCount = productionCount;
            this.totalArea = totalArea;
            this.totalProduction = totalProduction;
            this.averageYield = averageYield;
        }

        // Getters and Setters
        public String getCropId() { return cropId; }
        public void setCropId(String cropId) { this.cropId = cropId; }
        public String getCropName() { return cropName; }
        public void setCropName(String cropName) { this.cropName = cropName; }
        public Long getProductionCount() { return productionCount; }
        public void setProductionCount(Long productionCount) { this.productionCount = productionCount; }
        public BigDecimal getTotalArea() { return totalArea; }
        public void setTotalArea(BigDecimal totalArea) { this.totalArea = totalArea; }
        public BigDecimal getTotalProduction() { return totalProduction; }
        public void setTotalProduction(BigDecimal totalProduction) { this.totalProduction = totalProduction; }
        public BigDecimal getAverageYield() { return averageYield; }
        public void setAverageYield(BigDecimal averageYield) { this.averageYield = averageYield; }
    }

    // ==================== GETTERS AND SETTERS ====================

    public Long getTotalProductions() {
        return totalProductions;
    }

    public void setTotalProductions(Long totalProductions) {
        this.totalProductions = totalProductions;
    }

    public Long getActiveProductions() {
        return activeProductions;
    }

    public void setActiveProductions(Long activeProductions) {
        this.activeProductions = activeProductions;
    }

    public Long getHarvestedProductions() {
        return harvestedProductions;
    }

    public void setHarvestedProductions(Long harvestedProductions) {
        this.harvestedProductions = harvestedProductions;
    }

    public Long getPlannedProductions() {
        return plannedProductions;
    }

    public void setPlannedProductions(Long plannedProductions) {
        this.plannedProductions = plannedProductions;
    }

    public Long getSoldProductions() {
        return soldProductions;
    }

    public void setSoldProductions(Long soldProductions) {
        this.soldProductions = soldProductions;
    }

    public Long getOverdueProductions() {
        return overdueProductions;
    }

    public void setOverdueProductions(Long overdueProductions) {
        this.overdueProductions = overdueProductions;
    }

    public Long getOrganicProductions() {
        return organicProductions;
    }

    public void setOrganicProductions(Long organicProductions) {
        this.organicProductions = organicProductions;
    }

    public Long getCertifiedProductions() {
        return certifiedProductions;
    }

    public void setCertifiedProductions(Long certifiedProductions) {
        this.certifiedProductions = certifiedProductions;
    }

    public BigDecimal getTotalAreaPlanted() {
        return totalAreaPlanted;
    }

    public void setTotalAreaPlanted(BigDecimal totalAreaPlanted) {
        this.totalAreaPlanted = totalAreaPlanted;
    }

    public BigDecimal getAverageAreaPerProduction() {
        return averageAreaPerProduction;
    }

    public void setAverageAreaPerProduction(BigDecimal averageAreaPerProduction) {
        this.averageAreaPerProduction = averageAreaPerProduction;
    }

    public BigDecimal getLargestProduction() {
        return largestProduction;
    }

    public void setLargestProduction(BigDecimal largestProduction) {
        this.largestProduction = largestProduction;
    }

    public BigDecimal getSmallestProduction() {
        return smallestProduction;
    }

    public void setSmallestProduction(BigDecimal smallestProduction) {
        this.smallestProduction = smallestProduction;
    }

    public BigDecimal getAverageYield() {
        return averageYield;
    }

    public void setAverageYield(BigDecimal averageYield) {
        this.averageYield = averageYield;
    }

    public BigDecimal getHighestYield() {
        return highestYield;
    }

    public void setHighestYield(BigDecimal highestYield) {
        this.highestYield = highestYield;
    }

    public BigDecimal getLowestYield() {
        return lowestYield;
    }

    public void setLowestYield(BigDecimal lowestYield) {
        this.lowestYield = lowestYield;
    }

    public BigDecimal getTotalProduction() {
        return totalProduction;
    }

    public void setTotalProduction(BigDecimal totalProduction) {
        this.totalProduction = totalProduction;
    }

    public Long getConventionalProductions() {
        return conventionalProductions;
    }

    public void setConventionalProductions(Long conventionalProductions) {
        this.conventionalProductions = conventionalProductions;
    }

    public Long getIntegratedProductions() {
        return integratedProductions;
    }

    public void setIntegratedProductions(Long integratedProductions) {
        this.integratedProductions = integratedProductions;
    }

    public Long getSeasonAProductions() {
        return seasonAProductions;
    }

    public void setSeasonAProductions(Long seasonAProductions) {
        this.seasonAProductions = seasonAProductions;
    }

    public Long getSeasonBProductions() {
        return seasonBProductions;
    }

    public void setSeasonBProductions(Long seasonBProductions) {
        this.seasonBProductions = seasonBProductions;
    }

    public Long getSeasonCProductions() {
        return seasonCProductions;
    }

    public void setSeasonCProductions(Long seasonCProductions) {
        this.seasonCProductions = seasonCProductions;
    }

    public Long getOffSeasonProductions() {
        return offSeasonProductions;
    }

    public void setOffSeasonProductions(Long offSeasonProductions) {
        this.offSeasonProductions = offSeasonProductions;
    }

    public Map<String, Long> getProductionStatusCounts() {
        return productionStatusCounts;
    }

    public void setProductionStatusCounts(Map<String, Long> productionStatusCounts) {
        this.productionStatusCounts = productionStatusCounts;
    }

    public Map<String, Long> getProductionMethodCounts() {
        return productionMethodCounts;
    }

    public void setProductionMethodCounts(Map<String, Long> productionMethodCounts) {
        this.productionMethodCounts = productionMethodCounts;
    }

    public Map<String, Long> getSeasonCounts() {
        return seasonCounts;
    }

    public void setSeasonCounts(Map<String, Long> seasonCounts) {
        this.seasonCounts = seasonCounts;
    }

    public Map<Integer, YearlyStats> getYearlyStatistics() {
        return yearlyStatistics;
    }

    public void setYearlyStatistics(Map<Integer, YearlyStats> yearlyStatistics) {
        this.yearlyStatistics = yearlyStatistics;
    }

    public List<FarmProductionSummary> getTopProductiveFarms() {
        return topProductiveFarms;
    }

    public void setTopProductiveFarms(List<FarmProductionSummary> topProductiveFarms) {
        this.topProductiveFarms = topProductiveFarms;
    }

    public List<CropProductionSummary> getTopProductiveCrops() {
        return topProductiveCrops;
    }

    public void setTopProductiveCrops(List<CropProductionSummary> topProductiveCrops) {
        this.topProductiveCrops = topProductiveCrops;
    }

    public BigDecimal getYieldTrend() {
        return yieldTrend;
    }

    public void setYieldTrend(BigDecimal yieldTrend) {
        this.yieldTrend = yieldTrend;
    }

    public BigDecimal getProductionTrend() {
        return productionTrend;
    }

    public void setProductionTrend(BigDecimal productionTrend) {
        this.productionTrend = productionTrend;
    }

    public BigDecimal getAreaTrend() {
        return areaTrend;
    }

    public void setAreaTrend(BigDecimal areaTrend) {
        this.areaTrend = areaTrend;
    }

    public BigDecimal getAverageEfficiency() {
        return averageEfficiency;
    }

    public void setAverageEfficiency(BigDecimal averageEfficiency) {
        this.averageEfficiency = averageEfficiency;
    }

    public BigDecimal getBestEfficiency() {
        return bestEfficiency;
    }

    public void setBestEfficiency(BigDecimal bestEfficiency) {
        this.bestEfficiency = bestEfficiency;
    }

    public BigDecimal getWorstEfficiency() {
        return worstEfficiency;
    }

    public void setWorstEfficiency(BigDecimal worstEfficiency) {
        this.worstEfficiency = worstEfficiency;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Calculer le pourcentage de productions récoltées
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Double getHarvestSuccessRate() {
        if (totalProductions == 0) return 0.0;
        return (harvestedProductions + soldProductions) * 100.0 / totalProductions;
    }

    /**
     * Calculer le pourcentage de productions biologiques
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Double getOrganicPercentage() {
        if (totalProductions == 0) return 0.0;
        return organicProductions * 100.0 / totalProductions;
    }

    /**
     * Calculer le pourcentage de productions certifiées
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Double getCertificationRate() {
        if (totalProductions == 0) return 0.0;
        return certifiedProductions * 100.0 / totalProductions;
    }

    /**
     * Calculer le pourcentage de productions en retard
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Double getOverdueRate() {
        if (totalProductions == 0) return 0.0;
        return overdueProductions * 100.0 / totalProductions;
    }

    @Override
    public String toString() {
        return "CropProductionStatsDTO{" +
                "totalProductions=" + totalProductions +
                ", activeProductions=" + activeProductions +
                ", harvestedProductions=" + harvestedProductions +
                ", totalAreaPlanted=" + totalAreaPlanted +
                ", averageYield=" + averageYield +
                ", totalProduction=" + totalProduction +
                ", organicProductions=" + organicProductions +
                ", harvestSuccessRate=" + getHarvestSuccessRate() + "%" +
                '}';
    }
}