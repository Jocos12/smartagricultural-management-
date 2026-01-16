package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.ProductionPrediction;
import SmartAgricultural.Management.Model.ProductionPrediction.Season;
import SmartAgricultural.Management.Model.ProductionPrediction.PredictionType;
import SmartAgricultural.Management.Model.ProductionPrediction.ValidationStatus;
import SmartAgricultural.Management.Service.CropProductionService;
import SmartAgricultural.Management.Service.FarmService;
import SmartAgricultural.Management.Service.ProductionPredictionService;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.exception.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/predictions")
@CrossOrigin(origins = "*")
public class ProductionPredictionController {



    @Autowired
    private FarmService farmService;

    @Autowired
    private CropProductionService cropProductionService;
    @Autowired
    private ProductionPredictionService predictionService;

    // Basic CRUD Operations

    /**
     * Create a new production prediction
     */
    @PostMapping
    public ResponseEntity<?> createPrediction(@Valid @RequestBody ProductionPrediction prediction) {
        try {
            ProductionPrediction createdPrediction = predictionService.createPrediction(prediction);
            return new ResponseEntity<>(createSuccessResponse("Prediction created successfully", createdPrediction),
                    HttpStatus.CREATED);
        } catch (ValidationException e) {
            return new ResponseEntity<>(createErrorResponse("Validation Error", e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to create prediction"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get prediction by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPredictionById(@PathVariable String id) {
        try {
            ProductionPrediction prediction = predictionService.getPredictionById(id);
            return new ResponseEntity<>(createSuccessResponse("Prediction retrieved successfully", prediction),
                    HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(createErrorResponse("Not Found", e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve prediction"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get prediction by code
     */
    @GetMapping("/code/{predictionCode}")
    public ResponseEntity<?> getPredictionByCode(@PathVariable String predictionCode) {
        try {
            ProductionPrediction prediction = predictionService.getPredictionByCode(predictionCode);
            return new ResponseEntity<>(createSuccessResponse("Prediction retrieved successfully", prediction),
                    HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(createErrorResponse("Not Found", e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve prediction"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update existing prediction
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePrediction(@PathVariable String id,
                                              @Valid @RequestBody ProductionPrediction updatedPrediction) {
        try {
            ProductionPrediction prediction = predictionService.updatePrediction(id, updatedPrediction);
            return new ResponseEntity<>(createSuccessResponse("Prediction updated successfully", prediction),
                    HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(createErrorResponse("Not Found", e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity<>(createErrorResponse("Validation Error", e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to update prediction"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete prediction
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePrediction(@PathVariable String id) {
        try {
            predictionService.deletePrediction(id);
            return new ResponseEntity<>(createSuccessResponse("Prediction deleted successfully", null),
                    HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(createErrorResponse("Not Found", e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity<>(createErrorResponse("Validation Error", e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to delete prediction"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all predictions with pagination
     */
    @GetMapping
    public ResponseEntity<?> getAllPredictions(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "predictionDate") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        try {
            Page<ProductionPrediction> predictions = predictionService.getAllPredictions(page, size, sortBy, sortDir);
            return new ResponseEntity<>(createPagedResponse("Predictions retrieved successfully", predictions),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve predictions"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Status Management Endpoints

    /**
     * Validate a prediction
     */
    @PostMapping("/{id}/validate")
    public ResponseEntity<?> validatePrediction(@PathVariable String id,
                                                @RequestParam String validatorId) {
        try {
            ProductionPrediction prediction = predictionService.validatePrediction(id, validatorId);
            return new ResponseEntity<>(createSuccessResponse("Prediction validated successfully", prediction),
                    HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(createErrorResponse("Not Found", e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity<>(createErrorResponse("Validation Error", e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to validate prediction"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Reject a prediction
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectPrediction(@PathVariable String id,
                                              @RequestParam String validatorId) {
        try {
            ProductionPrediction prediction = predictionService.rejectPrediction(id, validatorId);
            return new ResponseEntity<>(createSuccessResponse("Prediction rejected successfully", prediction),
                    HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(createErrorResponse("Not Found", e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity<>(createErrorResponse("Validation Error", e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to reject prediction"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Publish a prediction
     */
    @PostMapping("/{id}/publish")
    public ResponseEntity<?> publishPrediction(@PathVariable String id) {
        try {
            ProductionPrediction prediction = predictionService.publishPrediction(id);
            return new ResponseEntity<>(createSuccessResponse("Prediction published successfully", prediction),
                    HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(createErrorResponse("Not Found", e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity<>(createErrorResponse("Validation Error", e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to publish prediction"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Unpublish a prediction
     */
    @PostMapping("/{id}/unpublish")
    public ResponseEntity<?> unpublishPrediction(@PathVariable String id) {
        try {
            ProductionPrediction prediction = predictionService.unpublishPrediction(id);
            return new ResponseEntity<>(createSuccessResponse("Prediction unpublished successfully", prediction),
                    HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(createErrorResponse("Not Found", e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity<>(createErrorResponse("Validation Error", e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to unpublish prediction"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update actual value
     */
    @PostMapping("/{id}/actual-value")
    public ResponseEntity<?> updateActualValue(@PathVariable String id,
                                               @RequestParam BigDecimal actualValue) {
        try {
            ProductionPrediction prediction = predictionService.updateActualValue(id, actualValue);
            return new ResponseEntity<>(createSuccessResponse("Actual value updated successfully", prediction),
                    HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(createErrorResponse("Not Found", e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity<>(createErrorResponse("Validation Error", e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to update actual value"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Query Endpoints

    /**
     * Get predictions by crop
     */
    @GetMapping("/crop/{cropId}")
    public ResponseEntity<?> getPredictionsByCrop(@PathVariable String cropId,
                                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            Page<ProductionPrediction> predictions = predictionService.getPredictionsByCrop(cropId, page, size);
            return new ResponseEntity<>(createPagedResponse("Predictions retrieved successfully", predictions),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve predictions"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get predictions by region
     */
    @GetMapping("/region/{region}")
    public ResponseEntity<?> getPredictionsByRegion(@PathVariable String region,
                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            Page<ProductionPrediction> predictions = predictionService.getPredictionsByRegion(region, page, size);
            return new ResponseEntity<>(createPagedResponse("Predictions retrieved successfully", predictions),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve predictions"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get predictions by validation status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getPredictionsByStatus(@PathVariable ValidationStatus status,
                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            Page<ProductionPrediction> predictions = predictionService.getPredictionsByStatus(status, page, size);
            return new ResponseEntity<>(createPagedResponse("Predictions retrieved successfully", predictions),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve predictions"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get published predictions
     */
    @GetMapping("/published")
    public ResponseEntity<?> getPublishedPredictions(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            Page<ProductionPrediction> predictions = predictionService.getPublishedPredictions(page, size);
            return new ResponseEntity<>(createPagedResponse("Published predictions retrieved successfully", predictions),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve published predictions"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search predictions
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchPredictions(@RequestParam String query,
                                               @RequestParam(value = "page", defaultValue = "0") int page,
                                               @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            Page<ProductionPrediction> predictions = predictionService.searchPredictions(query, page, size);
            return new ResponseEntity<>(createPagedResponse("Search results retrieved successfully", predictions),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to search predictions"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get validated predictions for specific criteria
     */
    @GetMapping("/validated")
    public ResponseEntity<?> getValidatedPredictions(
            @RequestParam(required = false) String cropId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Season season) {
        try {
            List<ProductionPrediction> predictions = predictionService.getValidatedPredictions(cropId, year, season);
            return new ResponseEntity<>(createSuccessResponse("Validated predictions retrieved successfully", predictions),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve validated predictions"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get overdue predictions
     */
    @GetMapping("/overdue")
    public ResponseEntity<?> getOverduePredictions() {
        try {
            List<ProductionPrediction> predictions = predictionService.getOverduePredictions();
            return new ResponseEntity<>(createSuccessResponse("Overdue predictions retrieved successfully", predictions),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve overdue predictions"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get high confidence predictions
     */
    @GetMapping("/high-confidence")
    public ResponseEntity<?> getHighConfidencePredictions() {
        try {
            List<ProductionPrediction> predictions = predictionService.getHighConfidencePredictions();
            return new ResponseEntity<>(createSuccessResponse("High confidence predictions retrieved successfully", predictions),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve high confidence predictions"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get accurate predictions
     */
    @GetMapping("/accurate")
    public ResponseEntity<?> getAccuratePredictions() {
        try {
            List<ProductionPrediction> predictions = predictionService.getAccuratePredictions();
            return new ResponseEntity<>(createSuccessResponse("Accurate predictions retrieved successfully", predictions),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve accurate predictions"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get recent predictions
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentPredictions(@RequestParam(defaultValue = "30") int days) {
        try {
            List<ProductionPrediction> predictions = predictionService.getRecentPredictions(days);
            return new ResponseEntity<>(createSuccessResponse("Recent predictions retrieved successfully", predictions),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve recent predictions"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get predictions by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<?> getPredictionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<ProductionPrediction> predictions = predictionService.getPredictionsByDateRange(startDate, endDate);
            return new ResponseEntity<>(createSuccessResponse("Predictions retrieved successfully", predictions),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve predictions"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get predictions by multiple criteria
     */
    @GetMapping("/criteria")
    public ResponseEntity<?> getPredictionsByCriteria(
            @RequestParam(required = false) String cropId,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Season season,
            @RequestParam(required = false) PredictionType predictionType,
            @RequestParam(required = false) ValidationStatus status) {
        try {
            List<ProductionPrediction> predictions = predictionService.getPredictionsByCriteria(
                    cropId, region, year, season, predictionType, status);
            return new ResponseEntity<>(createSuccessResponse("Predictions retrieved successfully", predictions),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve predictions"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // ✅ REMPLACER la méthode existante (ligne ~371) par cette version corrigée
    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<?> getPredictionsByFarmer(
            @PathVariable String farmerId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            // Récupérer les IDs des fermes du farmer
            List<String> farmIds = farmService.getFarmIdsByFarmerId(farmerId);

            if (farmIds.isEmpty()) {
                return new ResponseEntity<>(
                        createSuccessResponse("No farms found for farmer", List.of()),
                        HttpStatus.OK
                );
            }

            // Récupérer les IDs des productions pour ces fermes
            List<String> cropProductionIds = cropProductionService
                    .getCropProductionIdsByFarmIds(farmIds);

            if (cropProductionIds.isEmpty()) {
                return new ResponseEntity<>(
                        createSuccessResponse("No predictions available", List.of()),
                        HttpStatus.OK
                );
            }

            // Récupérer les prédictions pour ces productions
            Page<ProductionPrediction> predictions = predictionService
                    .getPredictionsByCropProductionIds(cropProductionIds, page, size);

            return new ResponseEntity<>(
                    createPagedResponse("Predictions retrieved successfully", predictions),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    createErrorResponse("Server Error", "Failed to retrieve farmer predictions: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }





    // Analytics Endpoints

    /**
     * Get dashboard statistics
     */
    @GetMapping("/dashboard/statistics")
    public ResponseEntity<?> getDashboardStatistics() {
        try {
            Map<String, Object> statistics = predictionService.getDashboardStatistics();
            return new ResponseEntity<>(createSuccessResponse("Dashboard statistics retrieved successfully", statistics),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve dashboard statistics"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get model performance analysis
     */
    @GetMapping("/analytics/model-performance")
    public ResponseEntity<?> getModelPerformanceAnalysis() {
        try {
            List<Map<String, Object>> analysis = predictionService.getModelPerformanceAnalysis();
            return new ResponseEntity<>(createSuccessResponse("Model performance analysis retrieved successfully", analysis),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve model performance analysis"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get seasonal analysis
     */
    @GetMapping("/analytics/seasonal")
    public ResponseEntity<?> getSeasonalAnalysis(@RequestParam Integer year) {
        try {
            List<Map<String, Object>> analysis = predictionService.getSeasonalAnalysis(year);
            return new ResponseEntity<>(createSuccessResponse("Seasonal analysis retrieved successfully", analysis),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve seasonal analysis"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get regional performance
     */
    @GetMapping("/analytics/regional")
    public ResponseEntity<?> getRegionalPerformance(@RequestParam Integer year) {
        try {
            List<Map<String, Object>> performance = predictionService.getRegionalPerformance(year);
            return new ResponseEntity<>(createSuccessResponse("Regional performance retrieved successfully", performance),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve regional performance"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get crop statistics
     */
    @GetMapping("/analytics/crop/{cropId}")
    public ResponseEntity<?> getCropStatistics(@PathVariable String cropId) {
        try {
            Map<String, Object> statistics = predictionService.getCropStatistics(cropId);
            return new ResponseEntity<>(createSuccessResponse("Crop statistics retrieved successfully", statistics),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve crop statistics"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get confidence vs accuracy correlation
     */
    @GetMapping("/analytics/confidence-accuracy-correlation")
    public ResponseEntity<?> getConfidenceAccuracyCorrelation() {
        try {
            List<Map<String, Object>> correlation = predictionService.getConfidenceAccuracyCorrelation();
            return new ResponseEntity<>(createSuccessResponse("Correlation data retrieved successfully", correlation),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve correlation data"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get model performance over time
     */
    @GetMapping("/analytics/model-performance-timeline")
    public ResponseEntity<?> getModelPerformanceOverTime(
            @RequestParam String modelUsed,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<ProductionPrediction> predictions = predictionService.getModelPerformanceOverTime(modelUsed, startDate, endDate);
            return new ResponseEntity<>(createSuccessResponse("Model performance timeline retrieved successfully", predictions),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve model performance timeline"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get regional trend analysis
     */
    @GetMapping("/analytics/regional-trends")
    public ResponseEntity<?> getRegionalTrendAnalysis(
            @RequestParam String region,
            @RequestParam Integer startYear,
            @RequestParam Integer endYear,
            @RequestParam PredictionType predictionType) {
        try {
            List<ProductionPrediction> trends = predictionService.getRegionalTrendAnalysis(region, startYear, endYear, predictionType);
            return new ResponseEntity<>(createSuccessResponse("Regional trend analysis retrieved successfully", trends),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve regional trend analysis"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Report Endpoints

    /**
     * Generate accuracy report
     */
    @GetMapping("/reports/accuracy")
    public ResponseEntity<?> generateAccuracyReport() {
        try {
            Map<String, Object> report = predictionService.generateAccuracyReport();
            return new ResponseEntity<>(createSuccessResponse("Accuracy report generated successfully", report),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to generate accuracy report"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Generate seasonal report
     */
    @GetMapping("/reports/seasonal")
    public ResponseEntity<?> generateSeasonalReport(@RequestParam Integer year) {
        try {
            Map<String, Object> report = predictionService.generateSeasonalReport(year);
            return new ResponseEntity<>(createSuccessResponse("Seasonal report generated successfully", report),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to generate seasonal report"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Alert and Notification Endpoints

    /**
     * Get predictions requiring attention
     */
    @GetMapping("/alerts/attention-required")
    public ResponseEntity<?> getPredictionsRequiringAttention() {
        try {
            Map<String, List<ProductionPrediction>> alerts = predictionService.getPredictionsRequiringAttention();
            return new ResponseEntity<>(createSuccessResponse("Attention alerts retrieved successfully", alerts),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve attention alerts"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get predictions needing update
     */
    @GetMapping("/alerts/needs-update")
    public ResponseEntity<?> getPredictionsNeedingUpdate() {
        try {
            List<ProductionPrediction> predictions = predictionService.getPredictionsNeedingUpdate();
            return new ResponseEntity<>(createSuccessResponse("Predictions needing update retrieved successfully", predictions),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve predictions needing update"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Batch Operations

    /**
     * Bulk validate predictions
     */
    @PostMapping("/bulk/validate")
    public ResponseEntity<?> bulkValidatePredictions(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> predictionIds = (List<String>) request.get("predictionIds");
            String validatorId = (String) request.get("validatorId");

            List<ProductionPrediction> validatedPredictions = predictionService.bulkValidatePredictions(predictionIds, validatorId);
            return new ResponseEntity<>(createSuccessResponse("Bulk validation completed", validatedPredictions),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to bulk validate predictions"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Bulk update actual values
     */
    @PostMapping("/bulk/actual-values")
    public ResponseEntity<?> bulkUpdateActualValues(@RequestBody Map<String, BigDecimal> actualValues) {
        try {
            List<ProductionPrediction> updatedPredictions = predictionService.bulkUpdateActualValues(actualValues);
            return new ResponseEntity<>(createSuccessResponse("Bulk actual values update completed", updatedPredictions),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to bulk update actual values"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Utility Endpoints

    /**
     * Check if prediction code exists
     */
    @GetMapping("/exists/code/{predictionCode}")
    public ResponseEntity<?> checkPredictionCodeExists(@PathVariable String predictionCode) {
        try {
            boolean exists = predictionService.predictionCodeExists(predictionCode);
            Map<String, Object> result = new HashMap<>();
            result.put("exists", exists);
            return new ResponseEntity<>(createSuccessResponse("Code existence check completed", result),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to check code existence"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get latest predictions for crop
     */
    @GetMapping("/latest/crop/{cropId}")
    public ResponseEntity<?> getLatestPredictionsForCrop(@PathVariable String cropId,
                                                         @RequestParam(defaultValue = "5") int limit) {
        try {
            List<ProductionPrediction> predictions = predictionService.getLatestPredictionsForCrop(cropId, limit);
            return new ResponseEntity<>(createSuccessResponse("Latest predictions retrieved successfully", predictions),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve latest predictions"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Maintenance Endpoints

    /**
     * Cleanup old predictions
     */
    @DeleteMapping("/maintenance/cleanup")
    public ResponseEntity<?> cleanupOldPredictions(@RequestParam(defaultValue = "365") int daysOld) {
        try {
            predictionService.cleanupOldPredictions(daysOld);
            return new ResponseEntity<>(createSuccessResponse("Old predictions cleaned up successfully", null),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to cleanup old predictions"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Enum Helper Endpoints

    /**
     * Get all seasons
     */
    @GetMapping("/enums/seasons")
    public ResponseEntity<?> getAllSeasons() {
        try {
            Season[] seasons = Season.values();
            return new ResponseEntity<>(createSuccessResponse("Seasons retrieved successfully", seasons),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve seasons"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all prediction types
     */
    @GetMapping("/enums/prediction-types")
    public ResponseEntity<?> getAllPredictionTypes() {
        try {
            PredictionType[] types = PredictionType.values();
            return new ResponseEntity<>(createSuccessResponse("Prediction types retrieved successfully", types),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve prediction types"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all validation statuses
     */
    @GetMapping("/enums/validation-statuses")
    public ResponseEntity<?> getAllValidationStatuses() {
        try {
            ValidationStatus[] statuses = ValidationStatus.values();
            return new ResponseEntity<>(createSuccessResponse("Validation statuses retrieved successfully", statuses),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(createErrorResponse("Server Error", "Failed to retrieve validation statuses"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper Methods

    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        if (data != null) {
            response.put("data", data);
        }
        return response;
    }

    private Map<String, Object> createPagedResponse(String message, Page<?> page) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());

        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("content", page.getContent());
        pageInfo.put("totalElements", page.getTotalElements());
        pageInfo.put("totalPages", page.getTotalPages());
        pageInfo.put("currentPage", page.getNumber());
        pageInfo.put("pageSize", page.getSize());
        pageInfo.put("hasNext", page.hasNext());
        pageInfo.put("hasPrevious", page.hasPrevious());
        pageInfo.put("isFirst", page.isFirst());
        pageInfo.put("isLast", page.isLast());

        response.put("data", pageInfo);
        return response;
    }

    private Map<String, Object> createErrorResponse(String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    // Exception Handlers

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity<>(createErrorResponse("Resource Not Found", e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException e) {
        return new ResponseEntity<>(createErrorResponse("Validation Error", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(createErrorResponse("Invalid Argument", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        return new ResponseEntity<>(createErrorResponse("Internal Server Error", "An unexpected error occurred"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}