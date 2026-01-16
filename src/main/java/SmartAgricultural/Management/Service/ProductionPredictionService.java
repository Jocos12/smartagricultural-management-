package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.ProductionPrediction;
import SmartAgricultural.Management.Model.ProductionPrediction.Season;
import SmartAgricultural.Management.Model.ProductionPrediction.PredictionType;
import SmartAgricultural.Management.Model.ProductionPrediction.ValidationStatus;
import SmartAgricultural.Management.Repository.ProductionPredictionRepository;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.exception.ValidationException;
import SmartAgricultural.Management.exception.DuplicateResourceException;

// ✅ AJOUTER ces imports tout en haut après les imports existants
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
@Transactional
public class ProductionPredictionService {


    // ✅ AJOUTER cette ligne juste après la déclaration de la classe
    private static final Logger log = LoggerFactory.getLogger(ProductionPredictionService.class);

    @Autowired
    private ProductionPredictionRepository predictionRepository;


    // Basic CRUD Operations

    /**
     * Create a new production prediction
     */
    public ProductionPrediction createPrediction(ProductionPrediction prediction) {
        validatePrediction(prediction);

        // Check for duplicates
        if (predictionRepository.existsByCropIdAndYearAndSeasonAndPredictionType(
                prediction.getCropId(), prediction.getYear(),
                prediction.getSeason(), prediction.getPredictionType())) {
            throw new DuplicateResourceException(
                    "Prediction already exists for this crop, year, season, and type combination");
        }

        return predictionRepository.save(prediction);
    }

    /**
     * Get prediction by ID
     */
    @Transactional(readOnly = true)
    public ProductionPrediction getPredictionById(String id) {
        return predictionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prediction not found with id: " + id));
    }

    /**
     * Get prediction by prediction code
     */
    @Transactional(readOnly = true)
    public ProductionPrediction getPredictionByCode(String predictionCode) {
        return predictionRepository.findByPredictionCode(predictionCode)
                .orElseThrow(() -> new ResourceNotFoundException("Prediction not found with code: " + predictionCode));
    }

    /**
     * Update existing prediction
     */
    public ProductionPrediction updatePrediction(String id, ProductionPrediction updatedPrediction) {
        ProductionPrediction existingPrediction = getPredictionById(id);

        // Update fields
        if (updatedPrediction.getPredictedValue() != null) {
            existingPrediction.setPredictedValue(updatedPrediction.getPredictedValue());
        }
        if (updatedPrediction.getConfidenceLevel() != null) {
            existingPrediction.setConfidenceLevel(updatedPrediction.getConfidenceLevel());
        }
        if (updatedPrediction.getConfidenceIntervalMin() != null) {
            existingPrediction.setConfidenceIntervalMin(updatedPrediction.getConfidenceIntervalMin());
        }
        if (updatedPrediction.getConfidenceIntervalMax() != null) {
            existingPrediction.setConfidenceIntervalMax(updatedPrediction.getConfidenceIntervalMax());
        }
        if (updatedPrediction.getModelVersion() != null) {
            existingPrediction.setModelVersion(updatedPrediction.getModelVersion());
        }
        if (updatedPrediction.getInputFeatures() != null) {
            existingPrediction.setInputFeatures(updatedPrediction.getInputFeatures());
        }
        if (updatedPrediction.getFactorsConsidered() != null) {
            existingPrediction.setFactorsConsidered(updatedPrediction.getFactorsConsidered());
        }
        if (updatedPrediction.getRiskFactors() != null) {
            existingPrediction.setRiskFactors(updatedPrediction.getRiskFactors());
        }
        if (updatedPrediction.getAssumptions() != null) {
            existingPrediction.setAssumptions(updatedPrediction.getAssumptions());
        }
        if (updatedPrediction.getLimitations() != null) {
            existingPrediction.setLimitations(updatedPrediction.getLimitations());
        }

        validatePrediction(existingPrediction);
        return predictionRepository.save(existingPrediction);
    }

    /**
     * Delete prediction
     */
    public void deletePrediction(String id) {
        ProductionPrediction prediction = getPredictionById(id);

        // Check if prediction can be deleted (business logic)
        if (prediction.isPublished()) {
            throw new ValidationException("Cannot delete published prediction");
        }

        predictionRepository.delete(prediction);
    }

    /**
     * Get all predictions with pagination
     */
    @Transactional(readOnly = true)
    public Page<ProductionPrediction> getAllPredictions(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return predictionRepository.findAll(pageable);
    }

    // Validation and Status Management

    /**
     * Validate a prediction
     */
    public ProductionPrediction validatePrediction(String id, String validatorId) {
        ProductionPrediction prediction = getPredictionById(id);

        if (!prediction.canValidate()) {
            throw new ValidationException("Prediction cannot be validated in current status");
        }

        prediction.validate(validatorId);
        return predictionRepository.save(prediction);
    }

    /**
     * Reject a prediction
     */
    public ProductionPrediction rejectPrediction(String id, String validatorId) {
        ProductionPrediction prediction = getPredictionById(id);

        if (!prediction.canReject()) {
            throw new ValidationException("Prediction cannot be rejected in current status");
        }

        prediction.reject(validatorId);
        return predictionRepository.save(prediction);
    }

    /**
     * Publish a prediction
     */
    public ProductionPrediction publishPrediction(String id) {
        ProductionPrediction prediction = getPredictionById(id);

        if (!prediction.canPublish()) {
            throw new ValidationException("Prediction cannot be published. Must be validated first.");
        }

        prediction.publish();
        return predictionRepository.save(prediction);
    }

    /**
     * Unpublish a prediction
     */
    public ProductionPrediction unpublishPrediction(String id) {
        ProductionPrediction prediction = getPredictionById(id);

        if (!prediction.canUnpublish()) {
            throw new ValidationException("Prediction is not currently published");
        }

        prediction.unpublish();
        return predictionRepository.save(prediction);
    }

    /**
     * Update actual value and calculate accuracy
     */
    public ProductionPrediction updateActualValue(String id, BigDecimal actualValue) {
        ProductionPrediction prediction = getPredictionById(id);
        prediction.updateActualValue(actualValue);
        return predictionRepository.save(prediction);
    }



    // ✅ REMPLACER la méthode existante par cette version corrigée
    public Page<ProductionPrediction> getPredictionsByCropProductionIds(
            List<String> cropProductionIds, int page, int size) {
        try {
            if (cropProductionIds == null || cropProductionIds.isEmpty()) {
                log.warn("Empty crop production IDs list provided");
                return Page.empty();
            }

            Pageable pageable = PageRequest.of(page, size,
                    Sort.by(Sort.Direction.DESC, "predictionDate"));

            return predictionRepository.findByCropProductionIdIn(cropProductionIds, pageable);
        } catch (Exception e) {
            log.error("Error getting predictions by crop production IDs", e);
            return Page.empty();
        }
    }


    // Query Methods

    /**
     * Get predictions by crop
     */
    @Transactional(readOnly = true)
    public Page<ProductionPrediction> getPredictionsByCrop(String cropId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("predictionDate").descending());
        return predictionRepository.findByCropId(cropId, pageable);
    }

    /**
     * Get predictions by region
     */
    @Transactional(readOnly = true)
    public Page<ProductionPrediction> getPredictionsByRegion(String region, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("predictionDate").descending());
        return predictionRepository.findByRegion(region, pageable);
    }

    /**
     * Get predictions by validation status
     */
    @Transactional(readOnly = true)
    public Page<ProductionPrediction> getPredictionsByStatus(ValidationStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("predictionDate").descending());
        return predictionRepository.findByValidationStatus(status, pageable);
    }

    /**
     * Get published predictions
     */
    @Transactional(readOnly = true)
    public Page<ProductionPrediction> getPublishedPredictions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("predictionDate").descending());
        return predictionRepository.findByPublished(true, pageable);
    }

    /**
     * Search predictions
     */
    @Transactional(readOnly = true)
    public Page<ProductionPrediction> searchPredictions(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("predictionDate").descending());
        return predictionRepository.searchPredictions(searchTerm, pageable);
    }

    /**
     * Get predictions by year and season
     */
    @Transactional(readOnly = true)
    public List<ProductionPrediction> getPredictionsByYearAndSeason(Integer year, Season season) {
        if (season != null) {
            return predictionRepository.findByRegionAndYearAndSeason(null, year, season);
        }
        return predictionRepository.findByYear(year);
    }

    /**
     * Get validated predictions for specific crop, year and season
     */
    @Transactional(readOnly = true)
    public List<ProductionPrediction> getValidatedPredictions(String cropId, Integer year, Season season) {
        return predictionRepository.findValidatedPredictionsForCropYearSeason(cropId, year, season);
    }

    /**
     * Get overdue predictions
     */
    @Transactional(readOnly = true)
    public List<ProductionPrediction> getOverduePredictions() {
        return predictionRepository.findOverduePredictions(LocalDate.now());
    }

    /**
     * Get high confidence predictions
     */
    @Transactional(readOnly = true)
    public List<ProductionPrediction> getHighConfidencePredictions() {
        return predictionRepository.findHighConfidencePredictions();
    }

    /**
     * Get accurate predictions
     */
    @Transactional(readOnly = true)
    public List<ProductionPrediction> getAccuratePredictions() {
        return predictionRepository.findAccuratePredictions();
    }

    /**
     * Get recent predictions
     */
    @Transactional(readOnly = true)
    public List<ProductionPrediction> getRecentPredictions(int days) {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(days);
        return predictionRepository.findRecentPredictions(sinceDate);
    }

    /**
     * Get predictions needing update
     */
    @Transactional(readOnly = true)
    public List<ProductionPrediction> getPredictionsNeedingUpdate() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(1); // Default to 1 month
        return predictionRepository.findPredictionsNeedingUpdate(cutoffDate);
    }

    // Analytics and Statistics

    /**
     * Get dashboard statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStatistics() {
        Object[] stats = predictionRepository.getDashboardStatistics();

        Map<String, Object> dashboard = new HashMap<>();
        if (stats != null && stats.length >= 6) {
            dashboard.put("totalPredictions", stats[0]);
            dashboard.put("validatedCount", stats[1]);
            dashboard.put("publishedCount", stats[2]);
            dashboard.put("completedCount", stats[3]);
            dashboard.put("averageAccuracy", stats[4]);
            dashboard.put("averageConfidence", stats[5]);
        }

        // Additional statistics
        dashboard.put("overdueCount", predictionRepository.countOverduePredictions(LocalDate.now()));
        dashboard.put("pendingCount", predictionRepository.countByValidationStatus(ValidationStatus.PENDING));

        return dashboard;
    }

    /**
     * Get model performance analysis
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getModelPerformanceAnalysis() {
        List<Object[]> results = predictionRepository.findTopPerformingModels();
        return results.stream()
                .map(result -> {
                    Map<String, Object> model = new HashMap<>();
                    model.put("modelName", result[0]);
                    model.put("averageAccuracy", result[1]);
                    return model;
                }).toList();
    }

    /**
     * Get seasonal analysis
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSeasonalAnalysis(Integer year) {
        List<Object[]> results = predictionRepository.findSeasonalAnalysis(year);
        return results.stream()
                .map(result -> {
                    Map<String, Object> season = new HashMap<>();
                    season.put("season", result[0]);
                    season.put("predictionCount", result[1]);
                    season.put("averageAccuracy", result[2]);
                    return season;
                }).toList();
    }

    /**
     * Get regional performance
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRegionalPerformance(Integer year) {
        List<Object[]> results = predictionRepository.findRegionalPerformance(year);
        return results.stream()
                .map(result -> {
                    Map<String, Object> region = new HashMap<>();
                    region.put("region", result[0]);
                    region.put("predictionCount", result[1]);
                    region.put("averageAccuracy", result[2]);
                    region.put("averageConfidence", result[3]);
                    return region;
                }).toList();
    }

    /**
     * Get crop-specific statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCropStatistics(String cropId) {
        Map<String, Object> stats = new HashMap<>();

        List<ProductionPrediction> predictions = predictionRepository.findByCropId(cropId);
        stats.put("totalPredictions", predictions.size());

        long validatedCount = predictions.stream()
                .mapToLong(p -> p.isValidated() ? 1 : 0)
                .sum();
        stats.put("validatedCount", validatedCount);

        BigDecimal averageAccuracy = predictionRepository.findAverageAccuracyByCrop(cropId);
        stats.put("averageAccuracy", averageAccuracy);

        return stats;
    }

    // Batch Operations

    /**
     * Bulk validate predictions
     */
    public List<ProductionPrediction> bulkValidatePredictions(List<String> predictionIds, String validatorId) {
        return predictionIds.stream()
                .map(id -> {
                    try {
                        return validatePrediction(id, validatorId);
                    } catch (Exception e) {
                        // Log error and continue with others
                        return null;
                    }
                })
                .filter(p -> p != null)
                .toList();
    }

    /**
     * Bulk update actual values
     */
    public List<ProductionPrediction> bulkUpdateActualValues(Map<String, BigDecimal> actualValues) {
        return actualValues.entrySet().stream()
                .map(entry -> {
                    try {
                        return updateActualValue(entry.getKey(), entry.getValue());
                    } catch (Exception e) {
                        // Log error and continue with others
                        return null;
                    }
                })
                .filter(p -> p != null)
                .toList();
    }

    /**
     * Clean up old predictions
     */
    public void cleanupOldPredictions(int daysOld) {
        LocalDate cutoffDate = LocalDate.now().minusDays(daysOld);
        predictionRepository.deleteOldUncompletedPredictions(cutoffDate);
    }

    // Advanced Analytics

    /**
     * Get confidence vs accuracy correlation data
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getConfidenceAccuracyCorrelation() {
        List<Object[]> results = predictionRepository.findConfidenceAccuracyCorrelation();
        return results.stream()
                .map(result -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("confidenceLevel", result[0]);
                    data.put("accuracyAchieved", result[1]);
                    return data;
                }).toList();
    }

    /**
     * Get model performance over time
     */
    @Transactional(readOnly = true)
    public List<ProductionPrediction> getModelPerformanceOverTime(String modelUsed,
                                                                  LocalDateTime startDate, LocalDateTime endDate) {
        return predictionRepository.findModelPredictionsInPeriod(modelUsed, startDate, endDate);
    }

    /**
     * Get regional trend analysis
     */
    @Transactional(readOnly = true)
    public List<ProductionPrediction> getRegionalTrendAnalysis(String region,
                                                               Integer startYear, Integer endYear, PredictionType predictionType) {
        return predictionRepository.findRegionalTrendPredictions(region, startYear, endYear, predictionType);
    }

    // Utility Methods

    /**
     * Check if prediction code exists
     */
    @Transactional(readOnly = true)
    public boolean predictionCodeExists(String predictionCode) {
        return predictionRepository.existsByPredictionCode(predictionCode);
    }

    /**
     * Get latest predictions for crop
     */
    @Transactional(readOnly = true)
    public List<ProductionPrediction> getLatestPredictionsForCrop(String cropId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return predictionRepository.findLatestPredictionsForCrop(cropId, pageable);
    }

    /**
     * Get predictions by date range
     */
    @Transactional(readOnly = true)
    public List<ProductionPrediction> getPredictionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return predictionRepository.findByTargetDateBetween(startDate, endDate);
    }

    /**
     * Get predictions by multiple criteria
     */
    @Transactional(readOnly = true)
    public List<ProductionPrediction> getPredictionsByCriteria(String cropId, String region,
                                                               Integer year, Season season, PredictionType predictionType, ValidationStatus status) {

        // Build query based on provided criteria
        if (cropId != null && year != null && season != null) {
            return predictionRepository.findByCropIdAndYearAndSeason(cropId, year, season);
        } else if (region != null && year != null && season != null) {
            return predictionRepository.findByRegionAndYearAndSeason(region, year, season);
        } else if (predictionType != null && status != null) {
            return predictionRepository.findByPredictionTypeAndValidationStatus(predictionType, status);
        } else if (cropId != null) {
            return predictionRepository.findByCropId(cropId);
        } else if (region != null) {
            return predictionRepository.findByRegion(region);
        } else if (year != null) {
            return predictionRepository.findByYear(year);
        } else if (season != null) {
            return predictionRepository.findBySeason(season);
        } else if (predictionType != null) {
            return predictionRepository.findByPredictionType(predictionType);
        } else if (status != null) {
            return predictionRepository.findByValidationStatus(status);
        }

        return predictionRepository.findAll();
    }

    // Report Generation Support

    /**
     * Generate prediction accuracy report
     */
    @Transactional(readOnly = true)
    public Map<String, Object> generateAccuracyReport() {
        Map<String, Object> report = new HashMap<>();

        // Overall statistics
        BigDecimal avgAccuracy = predictionRepository.findAverageAccuracy();
        BigDecimal avgConfidence = predictionRepository.findAverageConfidenceLevel();

        report.put("averageAccuracy", avgAccuracy);
        report.put("averageConfidence", avgConfidence);

        // Accuracy distribution
        List<ProductionPrediction> accurate = predictionRepository.findAccuratePredictions();
        List<ProductionPrediction> all = predictionRepository.findPredictionsWithActualValues();

        report.put("accurateCount", accurate.size());
        report.put("totalMeasured", all.size());
        report.put("accuracyRate", all.size() > 0 ? (double) accurate.size() / all.size() * 100 : 0);

        // Top performing models
        report.put("topModels", getModelPerformanceAnalysis());

        return report;
    }

    /**
     * Generate seasonal performance report
     */
    @Transactional(readOnly = true)
    public Map<String, Object> generateSeasonalReport(Integer year) {
        Map<String, Object> report = new HashMap<>();

        report.put("year", year);
        report.put("seasonalAnalysis", getSeasonalAnalysis(year));
        report.put("regionalPerformance", getRegionalPerformance(year));

        return report;
    }

    // Notification and Alert Support

    /**
     * Get predictions requiring attention
     */
    @Transactional(readOnly = true)
    public Map<String, List<ProductionPrediction>> getPredictionsRequiringAttention() {
        Map<String, List<ProductionPrediction>> alerts = new HashMap<>();

        alerts.put("overdue", getOverduePredictions());
        alerts.put("lowConfidence", predictionRepository.findLowConfidencePredictions());
        alerts.put("needingUpdate", getPredictionsNeedingUpdate());
        alerts.put("pending", predictionRepository.findByValidationStatus(ValidationStatus.PENDING));

        return alerts;
    }

    // Private Validation Methods

    private void validatePrediction(ProductionPrediction prediction) {
        if (prediction == null) {
            throw new ValidationException("Prediction cannot be null");
        }

        if (prediction.getCropId() == null || prediction.getCropId().trim().isEmpty()) {
            throw new ValidationException("Crop ID is required");
        }

        if (prediction.getRegion() == null || prediction.getRegion().trim().isEmpty()) {
            throw new ValidationException("Region is required");
        }

        if (prediction.getYear() == null || prediction.getYear() < 2000 || prediction.getYear() > 2100) {
            throw new ValidationException("Valid year is required (2000-2100)");
        }

        if (prediction.getSeason() == null) {
            throw new ValidationException("Season is required");
        }

        if (prediction.getPredictionType() == null) {
            throw new ValidationException("Prediction type is required");
        }

        if (prediction.getPredictedValue() == null || prediction.getPredictedValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Predicted value must be positive");
        }

        if (prediction.getConfidenceLevel() == null ||
                prediction.getConfidenceLevel().compareTo(BigDecimal.ZERO) < 0 ||
                prediction.getConfidenceLevel().compareTo(new BigDecimal("100")) > 0) {
            throw new ValidationException("Confidence level must be between 0 and 100");
        }

        if (prediction.getModelUsed() == null || prediction.getModelUsed().trim().isEmpty()) {
            throw new ValidationException("Model used is required");
        }

        if (prediction.getTargetDate() == null) {
            throw new ValidationException("Target date is required");
        }

        // Validate confidence interval if provided
        if (prediction.getConfidenceIntervalMin() != null && prediction.getConfidenceIntervalMax() != null) {
            if (prediction.getConfidenceIntervalMin().compareTo(prediction.getConfidenceIntervalMax()) > 0) {
                throw new ValidationException("Confidence interval min cannot be greater than max");
            }

            if (prediction.getPredictedValue().compareTo(prediction.getConfidenceIntervalMin()) < 0 ||
                    prediction.getPredictedValue().compareTo(prediction.getConfidenceIntervalMax()) > 0) {
                throw new ValidationException("Predicted value should be within confidence interval");
            }
        }

        // Validate actual value if provided
        if (prediction.getActualValue() != null && prediction.getActualValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Actual value must be positive");
        }
    }
}