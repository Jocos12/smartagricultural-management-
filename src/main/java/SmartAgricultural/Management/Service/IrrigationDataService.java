package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.IrrigationData;
import SmartAgricultural.Management.Model.IrrigationData.IrrigationMethod;
import SmartAgricultural.Management.Model.IrrigationData.WaterSource;
import SmartAgricultural.Management.Repository.IrrigationDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class IrrigationDataService {

    private final IrrigationDataRepository irrigationDataRepository;

    @Autowired
    public IrrigationDataService(IrrigationDataRepository irrigationDataRepository) {
        this.irrigationDataRepository = irrigationDataRepository;
    }

    // Basic CRUD operations
    public IrrigationData save(IrrigationData irrigationData) {
        validateIrrigationData(irrigationData);
        return irrigationDataRepository.save(irrigationData);
    }

    // Add this method to IrrigationDataService.java

    public List<IrrigationData> findByFarmIds(List<String> farmIds) {
        try {
            if (farmIds == null || farmIds.isEmpty()) {
                return Collections.emptyList();
            }
            
            // Filter out null or empty farm IDs
            List<String> validFarmIds = farmIds.stream()
                    .filter(id -> id != null && !id.trim().isEmpty())
                    .collect(java.util.stream.Collectors.toList());
            
            if (validFarmIds.isEmpty()) {
                return Collections.emptyList();
            }
            
            return irrigationDataRepository.findByFarmIdIn(validFarmIds);
        } catch (Exception e) {
            // Log error but return empty list to prevent 500 errors
            System.err.println("Error finding irrigation data by farm IDs: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Optional<IrrigationData> findById(String id) {
        return irrigationDataRepository.findById(id);
    }

    public List<IrrigationData> findAll() {
        return irrigationDataRepository.findAll();
    }

    public Page<IrrigationData> findAll(Pageable pageable) {
        return irrigationDataRepository.findAll(pageable);
    }

    public Page<IrrigationData> findAllSorted(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return irrigationDataRepository.findAll(pageable);
    }

    public IrrigationData update(String id, IrrigationData irrigationData) {
        return irrigationDataRepository.findById(id)
                .map(existingData -> {
                    updateExistingData(existingData, irrigationData);
                    validateIrrigationData(existingData);
                    return irrigationDataRepository.save(existingData);
                })
                .orElseThrow(() -> new RuntimeException("Irrigation data not found with id: " + id));
    }

    public void deleteById(String id) {
        if (!irrigationDataRepository.existsById(id)) {
            throw new RuntimeException("Irrigation data not found with id: " + id);
        }
        irrigationDataRepository.deleteById(id);
    }

    public boolean existsById(String id) {
        return irrigationDataRepository.existsById(id);
    }

    // Farm-related queries
    public List<IrrigationData> findByFarmId(String farmId) {
        return irrigationDataRepository.findByFarmId(farmId);
    }

    public Page<IrrigationData> findByFarmId(String farmId, Pageable pageable) {
        return irrigationDataRepository.findByFarmId(farmId, pageable);
    }

    public List<IrrigationData> findByCropProductionId(String cropProductionId) {
        return irrigationDataRepository.findByCropProductionId(cropProductionId);
    }

    // Date-based queries
    public List<IrrigationData> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return irrigationDataRepository.findByIrrigationDateBetween(startDate, endDate);
    }

    public Page<IrrigationData> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return irrigationDataRepository.findByIrrigationDateBetween(startDate, endDate, pageable);
    }

    public List<IrrigationData> findByFarmAndDateRange(String farmId, LocalDateTime startDate, LocalDateTime endDate) {
        return irrigationDataRepository.findByFarmIdAndIrrigationDateBetween(farmId, startDate, endDate);
    }

    public List<IrrigationData> findRecentIrrigations(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return irrigationDataRepository.findByIrrigationDateBetween(cutoffDate, LocalDateTime.now());
    }

    public List<IrrigationData> findRecentIrrigationsByFarm(String farmId, int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return irrigationDataRepository.findRecentIrrigationsByFarm(farmId, cutoffDate);
    }

    // Method and source-based queries
    public List<IrrigationData> findByIrrigationMethod(IrrigationMethod method) {
        return irrigationDataRepository.findByIrrigationMethod(method);
    }

    public List<IrrigationData> findByWaterSource(WaterSource source) {
        return irrigationDataRepository.findByWaterSource(source);
    }

    public List<IrrigationData> findByFarmAndMethod(String farmId, IrrigationMethod method) {
        return irrigationDataRepository.findByFarmAndIrrigationMethod(farmId, method);
    }

    // Water usage queries
    public List<IrrigationData> findByWaterAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return irrigationDataRepository.findByWaterAmountBetween(minAmount, maxAmount);
    }

    public List<IrrigationData> findHighWaterUsage(BigDecimal threshold) {
        return irrigationDataRepository.findByWaterAmountGreaterThan(threshold);
    }

    public List<IrrigationData> findExpensiveIrrigations(BigDecimal threshold) {
        return irrigationDataRepository.findByTotalCostGreaterThan(threshold);
    }

    // Operator and equipment queries
    public List<IrrigationData> findByOperator(String operatorName) {
        return irrigationDataRepository.findByOperatorNameContainingIgnoreCase(operatorName);
    }

    public List<IrrigationData> findWithFertilizer() {
        return irrigationDataRepository.findByFertilizerApplied(true);
    }

    public List<IrrigationData> findWithoutFertilizer() {
        return irrigationDataRepository.findByFertilizerApplied(false);
    }

    public List<String> findOperatorsByFarm(String farmId) {
        return irrigationDataRepository.findOperatorsByFarm(farmId);
    }

    // Statistics and analytics
    public BigDecimal getTotalWaterUsage(String farmId, LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal total = irrigationDataRepository.getTotalWaterUsageByFarmAndPeriod(farmId, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalCost(String farmId, LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal total = irrigationDataRepository.getTotalCostByFarmAndPeriod(farmId, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getAverageWaterUsage(String farmId) {
        BigDecimal average = irrigationDataRepository.getAverageWaterUsageByFarm(farmId);
        return average != null ? average : BigDecimal.ZERO;
    }

    public Long getIrrigationCount(String farmId, LocalDateTime startDate, LocalDateTime endDate) {
        return irrigationDataRepository.countIrrigationsByFarmAndPeriod(farmId, startDate, endDate);
    }

    // Efficiency analysis
    public List<IrrigationData> findLowEfficiencyIrrigations(BigDecimal threshold) {
        return irrigationDataRepository.findLowEfficiencyIrrigations(threshold);
    }

    public List<IrrigationData> getIrrigationsNeedingOptimization() {
        return findAll().stream()
                .filter(IrrigationData::needsOptimization)
                .collect(Collectors.toList());
    }

    // Reporting methods
    public Map<String, Object> generateFarmReport(String farmId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> report = new HashMap<>();

        List<IrrigationData> irrigations = findByFarmAndDateRange(farmId, startDate, endDate);

        report.put("farmId", farmId);
        report.put("reportPeriod", startDate + " to " + endDate);
        report.put("totalIrrigations", irrigations.size());
        report.put("totalWaterUsage", getTotalWaterUsage(farmId, startDate, endDate));
        report.put("totalCost", getTotalCost(farmId, startDate, endDate));
        report.put("averageWaterPerIrrigation", calculateAverageWaterPerIrrigation(irrigations));
        report.put("methodStats", getMethodStatistics(irrigations));
        report.put("sourceStats", getSourceStatistics(irrigations));
        report.put("efficiencyStats", getEfficiencyStatistics(irrigations));

        return report;
    }

    public Map<IrrigationMethod, Long> getMethodStatistics(List<IrrigationData> irrigations) {
        return irrigations.stream()
                .collect(Collectors.groupingBy(
                        IrrigationData::getIrrigationMethod,
                        Collectors.counting()
                ));
    }

    public Map<WaterSource, BigDecimal> getSourceStatistics(List<IrrigationData> irrigations) {
        return irrigations.stream()
                .filter(i -> i.getWaterAmount() != null)
                .collect(Collectors.groupingBy(
                        IrrigationData::getWaterSource,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                IrrigationData::getWaterAmount,
                                BigDecimal::add
                        )
                ));
    }

    public Map<String, Object> getEfficiencyStatistics(List<IrrigationData> irrigations) {
        Map<String, Object> stats = new HashMap<>();

        List<IrrigationData> withEfficiency = irrigations.stream()
                .filter(i -> i.getWaterEfficiency() != null)
                .collect(Collectors.toList());

        if (!withEfficiency.isEmpty()) {
            BigDecimal totalEfficiency = withEfficiency.stream()
                    .map(IrrigationData::getWaterEfficiency)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal averageEfficiency = totalEfficiency.divide(
                    BigDecimal.valueOf(withEfficiency.size()),
                    4,
                    RoundingMode.HALF_UP
            );

            stats.put("averageEfficiency", averageEfficiency);
            stats.put("totalWithEfficiencyData", withEfficiency.size());

            long lowEfficiencyCount = withEfficiency.stream()
                    .mapToLong(i -> i.isLowEfficiency() ? 1L : 0L)
                    .sum();

            stats.put("lowEfficiencyCount", lowEfficiencyCount);
            stats.put("lowEfficiencyPercentage",
                    BigDecimal.valueOf(lowEfficiencyCount * 100.0 / withEfficiency.size())
                            .setScale(2, RoundingMode.HALF_UP));
        }

        return stats;
    }

    public List<Object[]> getDailyStats(String farmId, int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return irrigationDataRepository.getDailyIrrigationStats(farmId, startDate);
    }

    public List<Object[]> getMethodAnalysis(String farmId) {
        return irrigationDataRepository.getIrrigationMethodAnalysis(farmId);
    }

    public List<Object[]> getMonthlyWaterUsage(String farmId, int year) {
        return irrigationDataRepository.getMonthlyWaterUsageByFarm(farmId, year);
    }

    // Recommendations and insights
    public List<String> generateRecommendations(String farmId) {
        List<String> recommendations = new ArrayList<>();
        List<IrrigationData> recentIrrigations = findRecentIrrigationsByFarm(farmId, 30);

        if (recentIrrigations.isEmpty()) {
            recommendations.add("No recent irrigation data available for analysis.");
            return recommendations;
        }

        // Water usage analysis
        BigDecimal avgWaterUsage = calculateAverageWaterPerIrrigation(recentIrrigations);
        BigDecimal highUsageThreshold = new BigDecimal("5000");

        if (avgWaterUsage.compareTo(highUsageThreshold) > 0) {
            recommendations.add("Consider more efficient irrigation methods. Current average water usage is high: " +
                    avgWaterUsage + "L per irrigation.");
        }

        // Cost analysis
        double avgCostPerLiter = recentIrrigations.stream()
                .filter(i -> i.getCostPerLiter() != null)
                .mapToDouble(i -> i.getCostPerLiter().doubleValue())
                .average().orElse(0.0);

        if (avgCostPerLiter > 0.5) {
            recommendations.add("Water costs are high. Consider alternative water sources or negotiate better rates.");
        }

        // Method efficiency analysis
        Map<IrrigationMethod, Long> methodStats = getMethodStatistics(recentIrrigations);
        if (methodStats.getOrDefault(IrrigationMethod.FLOOD, 0L) > methodStats.getOrDefault(IrrigationMethod.DRIP, 0L)) {
            recommendations.add("Consider switching to drip irrigation for better water efficiency.");
        }

        // Low efficiency warnings
        long lowEfficiencyCount = recentIrrigations.stream()
                .mapToLong(i -> i.isLowEfficiency() ? 1L : 0L)
                .sum();

        if (lowEfficiencyCount > recentIrrigations.size() * 0.3) {
            recommendations.add("More than 30% of recent irrigations show low efficiency. Review irrigation timing and soil conditions.");
        }

        return recommendations;
    }

    // Utility methods
    private void validateIrrigationData(IrrigationData data) {
        if (data.getFarmId() == null || data.getFarmId().trim().isEmpty()) {
            throw new IllegalArgumentException("Farm ID is required");
        }

        if (data.getWaterAmount() == null || data.getWaterAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Water amount must be positive");
        }

        if (data.getIrrigationMethod() == null) {
            throw new IllegalArgumentException("Irrigation method is required");
        }

        if (data.getWaterSource() == null) {
            throw new IllegalArgumentException("Water source is required");
        }

        if (data.getSoilMoistureBefore() != null && data.getSoilMoistureAfter() != null) {
            if (data.getSoilMoistureAfter().compareTo(data.getSoilMoistureBefore()) < 0) {
                throw new IllegalArgumentException("Soil moisture after irrigation cannot be less than before");
            }
        }
    }

    private void updateExistingData(IrrigationData existing, IrrigationData updated) {
        if (updated.getFarmId() != null) existing.setFarmId(updated.getFarmId());
        if (updated.getCropProductionId() != null) existing.setCropProductionId(updated.getCropProductionId());
        if (updated.getIrrigationDate() != null) existing.setIrrigationDate(updated.getIrrigationDate());
        if (updated.getWaterAmount() != null) existing.setWaterAmount(updated.getWaterAmount());
        if (updated.getIrrigationMethod() != null) existing.setIrrigationMethod(updated.getIrrigationMethod());
        if (updated.getDuration() != null) existing.setDuration(updated.getDuration());
        if (updated.getWaterSource() != null) existing.setWaterSource(updated.getWaterSource());
        if (updated.getWaterCost() != null) existing.setWaterCost(updated.getWaterCost());
        if (updated.getSoilMoistureBefore() != null) existing.setSoilMoistureBefore(updated.getSoilMoistureBefore());
        if (updated.getSoilMoistureAfter() != null) existing.setSoilMoistureAfter(updated.getSoilMoistureAfter());
        if (updated.getWeatherCondition() != null) existing.setWeatherCondition(updated.getWeatherCondition());
        if (updated.getOperatorName() != null) existing.setOperatorName(updated.getOperatorName());
        if (updated.getEquipmentUsed() != null) existing.setEquipmentUsed(updated.getEquipmentUsed());
        if (updated.getWaterQuality() != null) existing.setWaterQuality(updated.getWaterQuality());
        if (updated.getFertilizerApplied() != null) existing.setFertilizerApplied(updated.getFertilizerApplied());
        if (updated.getNotes() != null) existing.setNotes(updated.getNotes());
    }

    private BigDecimal calculateAverageWaterPerIrrigation(List<IrrigationData> irrigations) {
        if (irrigations.isEmpty()) return BigDecimal.ZERO;

        BigDecimal total = irrigations.stream()
                .filter(i -> i.getWaterAmount() != null)
                .map(IrrigationData::getWaterAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(BigDecimal.valueOf(irrigations.size()), 2, RoundingMode.HALF_UP);
    }

    // Bulk operations
    public List<IrrigationData> saveAll(List<IrrigationData> irrigationDataList) {
        irrigationDataList.forEach(this::validateIrrigationData);
        return irrigationDataRepository.saveAll(irrigationDataList);
    }

    public void deleteAll(List<String> ids) {
        ids.forEach(id -> {
            if (!irrigationDataRepository.existsById(id)) {
                throw new RuntimeException("Irrigation data not found with id: " + id);
            }
        });
        irrigationDataRepository.deleteAllById(ids);
    }

    // Search functionality
    public Page<IrrigationData> searchIrrigations(String farmId, IrrigationMethod method,
                                                  WaterSource source, LocalDateTime startDate,
                                                  LocalDateTime endDate, Pageable pageable) {
        // This would typically use Specifications for complex queries
        // For now, we'll use the existing repository methods
        if (farmId != null && startDate != null && endDate != null) {
            return irrigationDataRepository.findByFarmIdAndIrrigationDateBetween(farmId, startDate, endDate, pageable);
        } else if (farmId != null) {
            return irrigationDataRepository.findByFarmId(farmId, pageable);
        } else if (method != null) {
            return irrigationDataRepository.findByIrrigationMethod(method, pageable);
        } else if (source != null) {
            return irrigationDataRepository.findByWaterSource(source, pageable);
        } else {
            return irrigationDataRepository.findAll(pageable);
        }
    }
}