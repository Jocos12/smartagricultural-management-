package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.IrrigationData;
import SmartAgricultural.Management.Model.IrrigationPrediction;
import SmartAgricultural.Management.Repository.IrrigationPredictionRepository;
import SmartAgricultural.Management.Repository.IrrigationDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class IrrigationPredictionService {

    private static final Logger logger = LoggerFactory.getLogger(IrrigationPredictionService.class);

    private final IrrigationPredictionRepository predictionRepository;
    private final IrrigationDataRepository irrigationDataRepository;

    @Autowired
    public IrrigationPredictionService(
            IrrigationPredictionRepository predictionRepository,
            IrrigationDataRepository irrigationDataRepository) {
        this.predictionRepository = predictionRepository;
        this.irrigationDataRepository = irrigationDataRepository;
    }

    // Generate AI prediction based on historical irrigation data
    public IrrigationPrediction generatePrediction(String farmId, String cropProductionId, int historicalDays) {
        logger.info("Generating AI prediction for farm: {}, crop: {}", farmId, cropProductionId);

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(historicalDays);

        List<IrrigationData> historicalData = irrigationDataRepository
                .findByFarmIdAndIrrigationDateBetween(farmId, startDate, endDate);

        if (historicalData.isEmpty()) {
            throw new RuntimeException("No historical data available for prediction");
        }

        IrrigationPrediction prediction = new IrrigationPrediction();
        prediction.setFarmId(farmId);
        prediction.setCropProductionId(cropProductionId);
        prediction.setBasedOnHistoricalDays(historicalDays);

        // Calculate predicted water need
        BigDecimal avgWaterNeed = calculateAverageWaterNeed(historicalData);
        prediction.setPredictedWaterNeed(avgWaterNeed);

        // Calculate irrigation frequency
        int frequency = calculateIrrigationFrequency(historicalData, historicalDays);
        prediction.setPredictedIrrigationFrequency(frequency);

        // Recommend irrigation method based on efficiency
        IrrigationData.IrrigationMethod recommendedMethod = recommendIrrigationMethod(historicalData);
        prediction.setRecommendedMethod(recommendedMethod);

        // Calculate water stress risk
        BigDecimal waterStressRisk = calculateWaterStressRisk(historicalData);
        prediction.setWaterStressRisk(waterStressRisk);

        // Predict yield impact
        BigDecimal yieldImpact = predictYieldImpact(historicalData, waterStressRisk);
        prediction.setPredictedYieldImpact(yieldImpact);

        // Calculate optimal duration
        int optimalDuration = calculateOptimalDuration(historicalData);
        prediction.setOptimalDuration(optimalDuration);

        // Estimate cost
        BigDecimal costEstimation = estimateIrrigationCost(historicalData, avgWaterNeed);
        prediction.setCostEstimation(costEstimation);

        // Calculate confidence level
        BigDecimal confidence = calculateConfidenceLevel(historicalData);
        prediction.setConfidenceLevel(confidence);

        // Set soil moisture target
        BigDecimal moistureTarget = calculateSoilMoistureTarget(historicalData);
        prediction.setSoilMoistureTarget(moistureTarget);

        // Determine alert level
        IrrigationPrediction.AlertLevel alertLevel = determineAlertLevel(waterStressRisk, yieldImpact);
        prediction.setAlertLevel(alertLevel);

        // Generate recommendations
        String recommendations = generateRecommendations(prediction, historicalData);
        prediction.setRecommendations(recommendations);

        // Weather factor (simplified - can be enhanced with real weather API)
        prediction.setWeatherFactor(BigDecimal.valueOf(0.85));

        return predictionRepository.save(prediction);
    }

    private BigDecimal calculateAverageWaterNeed(List<IrrigationData> data) {
        if (data.isEmpty()) return BigDecimal.ZERO;

        BigDecimal total = data.stream()
                .map(IrrigationData::getWaterAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(BigDecimal.valueOf(data.size()), 2, RoundingMode.HALF_UP);
    }

    private int calculateIrrigationFrequency(List<IrrigationData> data, int days) {
        if (data.isEmpty() || days == 0) return 0;

        int weeks = days / 7;
        if (weeks == 0) weeks = 1;

        return data.size() / weeks;
    }

    private IrrigationData.IrrigationMethod recommendIrrigationMethod(List<IrrigationData> data) {
        // Calculate efficiency for each method
        Map<IrrigationData.IrrigationMethod, Double> methodEfficiency = new HashMap<>();

        for (IrrigationData.IrrigationMethod method : IrrigationData.IrrigationMethod.values()) {
            List<IrrigationData> methodData = data.stream()
                    .filter(d -> d.getIrrigationMethod() == method)
                    .filter(d -> d.getWaterEfficiency() != null)
                    .collect(Collectors.toList());

            if (!methodData.isEmpty()) {
                double avgEfficiency = methodData.stream()
                        .mapToDouble(d -> d.getWaterEfficiency().doubleValue())
                        .average()
                        .orElse(0.0);
                methodEfficiency.put(method, avgEfficiency);
            }
        }

        // Return method with highest efficiency, or DRIP as default
        return methodEfficiency.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(IrrigationData.IrrigationMethod.DRIP);
    }

    private BigDecimal calculateWaterStressRisk(List<IrrigationData> data) {
        if (data.isEmpty()) return BigDecimal.valueOf(50);

        // Calculate based on irrigation intervals
        List<LocalDateTime> dates = data.stream()
                .map(IrrigationData::getIrrigationDate)
                .sorted()
                .collect(Collectors.toList());

        if (dates.size() < 2) return BigDecimal.valueOf(30);

        // Calculate average days between irrigations
        long totalDays = 0;
        for (int i = 1; i < dates.size(); i++) {
            totalDays += ChronoUnit.DAYS.between(dates.get(i-1), dates.get(i));
        }

        double avgDaysBetween = (double) totalDays / (dates.size() - 1);

        // Higher interval = higher stress risk
        double riskPercentage = Math.min(100, avgDaysBetween * 10);

        // Consider soil moisture
        double avgMoisture = data.stream()
                .filter(d -> d.getSoilMoistureAfter() != null)
                .mapToDouble(d -> d.getSoilMoistureAfter().doubleValue())
                .average()
                .orElse(50.0);

        // Low moisture increases risk
        if (avgMoisture < 40) {
            riskPercentage += 20;
        } else if (avgMoisture < 60) {
            riskPercentage += 10;
        }

        return BigDecimal.valueOf(Math.min(100, riskPercentage)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal predictYieldImpact(List<IrrigationData> data, BigDecimal waterStressRisk) {
        // Negative impact increases with water stress
        double stressRisk = waterStressRisk.doubleValue();

        double yieldImpact;
        if (stressRisk < 30) {
            yieldImpact = 5.0; // Optimal conditions
        } else if (stressRisk < 50) {
            yieldImpact = 0.0; // Normal conditions
        } else if (stressRisk < 70) {
            yieldImpact = -15.0; // Moderate stress
        } else if (stressRisk < 85) {
            yieldImpact = -30.0; // High stress
        } else {
            yieldImpact = -50.0; // Critical stress
        }

        // Consider irrigation efficiency
        double avgEfficiency = data.stream()
                .filter(d -> d.getWaterEfficiency() != null)
                .mapToDouble(d -> d.getWaterEfficiency().doubleValue() * 100)
                .average()
                .orElse(50.0);

        if (avgEfficiency > 70) {
            yieldImpact += 5.0;
        }

        return BigDecimal.valueOf(yieldImpact).setScale(2, RoundingMode.HALF_UP);
    }

    private int calculateOptimalDuration(List<IrrigationData> data) {
        return data.stream()
                .filter(d -> d.getDuration() != null)
                .filter(d -> d.getWaterEfficiency() != null)
                .max(Comparator.comparing(IrrigationData::getWaterEfficiency))
                .map(IrrigationData::getDuration)
                .orElse(60);
    }

    private BigDecimal estimateIrrigationCost(List<IrrigationData> data, BigDecimal predictedWaterNeed) {
        BigDecimal avgCostPerLiter = data.stream()
                .filter(d -> d.getCostPerLiter() != null)
                .map(IrrigationData::getCostPerLiter)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(data.size()), 4, RoundingMode.HALF_UP);

        return predictedWaterNeed.multiply(avgCostPerLiter);
    }

    private BigDecimal calculateConfidenceLevel(List<IrrigationData> data) {
        int dataPoints = data.size();

        double baseConfidence;
        if (dataPoints >= 30) {
            baseConfidence = 90.0;
        } else if (dataPoints >= 20) {
            baseConfidence = 80.0;
        } else if (dataPoints >= 10) {
            baseConfidence = 70.0;
        } else {
            baseConfidence = 50.0;
        }

        // Check data consistency
        boolean hasCompleteData = data.stream()
                .allMatch(d -> d.getSoilMoistureBefore() != null && d.getSoilMoistureAfter() != null);

        if (hasCompleteData) {
            baseConfidence += 10.0;
        }

        return BigDecimal.valueOf(Math.min(100, baseConfidence)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateSoilMoistureTarget(List<IrrigationData> data) {
        return data.stream()
                .filter(d -> d.getSoilMoistureAfter() != null)
                .map(IrrigationData::getSoilMoistureAfter)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(data.size()), 2, RoundingMode.HALF_UP);
    }

    private IrrigationPrediction.AlertLevel determineAlertLevel(BigDecimal waterStressRisk, BigDecimal yieldImpact) {
        double risk = waterStressRisk.doubleValue();
        double impact = yieldImpact.doubleValue();

        if (risk >= 80 || impact <= -40) {
            return IrrigationPrediction.AlertLevel.CRITICAL;
        } else if (risk >= 60 || impact <= -20) {
            return IrrigationPrediction.AlertLevel.HIGH;
        } else if (risk >= 40 || impact <= -10) {
            return IrrigationPrediction.AlertLevel.MODERATE;
        } else {
            return IrrigationPrediction.AlertLevel.LOW;
        }
    }

    private String generateRecommendations(IrrigationPrediction prediction, List<IrrigationData> historicalData) {
        StringBuilder recommendations = new StringBuilder();

        // Water stress recommendations
        if (prediction.getWaterStressRisk().doubleValue() > 60) {
            recommendations.append("⚠️ High water stress risk detected! ");
            recommendations.append("Increase irrigation frequency to ")
                    .append(prediction.getPredictedIrrigationFrequency() + 1)
                    .append(" times per week. ");
        }

        // Yield impact recommendations
        if (prediction.getPredictedYieldImpact().doubleValue() < -20) {
            recommendations.append("📉 Predicted yield reduction of ")
                    .append(Math.abs(prediction.getPredictedYieldImpact().doubleValue()))
                    .append("%. Immediate irrigation required. ");
        }

        // Method recommendations
        if (prediction.getRecommendedMethod() == IrrigationData.IrrigationMethod.DRIP) {
            recommendations.append("💧 Drip irrigation is recommended for optimal water efficiency. ");
        }

        // Cost optimization
        if (prediction.getCostEstimation().doubleValue() > 500) {
            recommendations.append("💰 High irrigation costs predicted. Consider water-saving techniques. ");
        }

        // Positive feedback
        if (prediction.getWaterStressRisk().doubleValue() < 30 &&
                prediction.getPredictedYieldImpact().doubleValue() > 0) {
            recommendations.append("✅ Irrigation practices are optimal. Continue current schedule. ");
        }

        // Moisture target
        recommendations.append(String.format("🎯 Target soil moisture: %.1f%%. ",
                prediction.getSoilMoistureTarget().doubleValue()));

        return recommendations.toString().trim();
    }

    // CRUD Operations
    public IrrigationPrediction save(IrrigationPrediction prediction) {
        return predictionRepository.save(prediction);
    }

    public Optional<IrrigationPrediction> findById(String id) {
        return predictionRepository.findById(id);
    }

    public List<IrrigationPrediction> findByFarmId(String farmId) {
        return predictionRepository.findByFarmId(farmId);
    }

    public List<IrrigationPrediction> findCriticalAlerts() {
        return predictionRepository.findCriticalAlerts();
    }

    public Optional<IrrigationPrediction> findLatestPredictionByFarm(String farmId) {
        return predictionRepository.findLatestPredictionByFarm(farmId);
    }

    public void deleteById(String id) {
        predictionRepository.deleteById(id);
    }
}