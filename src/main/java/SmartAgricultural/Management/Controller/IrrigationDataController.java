package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.IrrigationData;
import SmartAgricultural.Management.Model.IrrigationData.IrrigationMethod;
import SmartAgricultural.Management.Model.IrrigationData.WaterSource;
import SmartAgricultural.Management.Service.IrrigationDataService;
import SmartAgricultural.Management.Service.FarmService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/irrigation")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@Validated
public class IrrigationDataController {

    private static final Logger logger = LoggerFactory.getLogger(IrrigationDataController.class);
    private static final boolean DEBUG_MODE = true;

    private final IrrigationDataService irrigationDataService;
    private final FarmService farmService;

    @Autowired
    public IrrigationDataController(IrrigationDataService irrigationDataService, FarmService farmService) {
        this.irrigationDataService = irrigationDataService;
        this.farmService = farmService;
        logger.info("=== IrrigationDataController initialized ===");
    }

    // Helper methods
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }

    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }

    // Basic CRUD Operations
    @PostMapping
    public ResponseEntity<?> createIrrigation(@Valid @RequestBody IrrigationData irrigationData) {
        try {
            if (DEBUG_MODE) {
                logger.info("=== CREATE IRRIGATION REQUEST ===");
                logger.info("Received data: {}", irrigationData);
                logger.info("Farm ID: {}", irrigationData.getFarmId());
                logger.info("Water Amount: {}", irrigationData.getWaterAmount());
                logger.info("Method: {}", irrigationData.getIrrigationMethod());
                logger.info("Source: {}", irrigationData.getWaterSource());
                logger.info("Date: {}", irrigationData.getIrrigationDate());
            }

            // Validation supplémentaire
            if (irrigationData.getFarmId() == null || irrigationData.getFarmId().trim().isEmpty()) {
                logger.warn("Validation failed: Farm ID is missing");
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Farm ID is required"));
            }

            if (irrigationData.getIrrigationDate() == null) {
                logger.warn("Validation failed: Irrigation date is missing");
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Irrigation date is required"));
            }

            if (irrigationData.getWaterAmount() == null || irrigationData.getWaterAmount().compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Validation failed: Invalid water amount");
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Valid water amount is required"));
            }

            if (irrigationData.getIrrigationMethod() == null) {
                logger.warn("Validation failed: Irrigation method is missing");
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Irrigation method is required"));
            }

            if (irrigationData.getWaterSource() == null) {
                logger.warn("Validation failed: Water source is missing");
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Water source is required"));
            }

            IrrigationData savedData = irrigationDataService.save(irrigationData);

            if (DEBUG_MODE) {
                logger.info("Irrigation created successfully with ID: {}", savedData.getId());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(savedData);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Validation error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating irrigation data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error creating irrigation data: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIrrigationById(@PathVariable String id) {
        try {
            if (DEBUG_MODE) {
                logger.info("Getting irrigation by ID: {}", id);
            }

            Optional<IrrigationData> irrigationData = irrigationDataService.findById(id);
            if (irrigationData.isPresent()) {
                return ResponseEntity.ok(irrigationData.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Irrigation not found with id: " + id));
            }
        } catch (Exception e) {
            logger.error("Error retrieving irrigation data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving irrigation data: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllIrrigations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "irrigationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            if (DEBUG_MODE) {
                logger.info("=== GET ALL IRRIGATIONS ===");
                logger.info("Page: {}, Size: {}, SortBy: {}, Direction: {}", page, size, sortBy, sortDirection);
            }

            Page<IrrigationData> irrigations = irrigationDataService.findAllSorted(page, size, sortBy, sortDirection);

            if (DEBUG_MODE) {
                logger.info("Found {} total irrigations", irrigations.getTotalElements());
            }

            return ResponseEntity.ok(irrigations);
        } catch (Exception e) {
            logger.error("Error retrieving irrigation data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving irrigation data: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateIrrigation(
            @PathVariable String id,
            @Valid @RequestBody IrrigationData irrigationData) {
        try {
            if (DEBUG_MODE) {
                logger.info("=== UPDATE IRRIGATION ===");
                logger.info("Updating irrigation ID: {}", id);
                logger.info("New data: {}", irrigationData);
            }

            IrrigationData updatedData = irrigationDataService.update(id, irrigationData);

            if (DEBUG_MODE) {
                logger.info("Irrigation updated successfully");
            }

            return ResponseEntity.ok(updatedData);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Validation error: " + e.getMessage()));
        } catch (RuntimeException e) {
            logger.error("Irrigation not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Irrigation not found with id: " + id));
        } catch (Exception e) {
            logger.error("Error updating irrigation data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error updating irrigation data: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIrrigation(@PathVariable String id) {
        try {
            if (DEBUG_MODE) {
                logger.info("Deleting irrigation with ID: {}", id);
            }

            irrigationDataService.deleteById(id);

            if (DEBUG_MODE) {
                logger.info("Irrigation deleted successfully");
            }

            return ResponseEntity.ok(createSuccessResponse("Irrigation data deleted successfully"));

        } catch (RuntimeException e) {
            logger.error("Irrigation not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Irrigation not found with id: " + id));
        } catch (Exception e) {
            logger.error("Error deleting irrigation data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error deleting irrigation data: " + e.getMessage()));
        }
    }

    // Farm-specific endpoints
    @GetMapping("/farm/{farmId}")
    public ResponseEntity<?> getIrrigationsByFarm(
            @PathVariable String farmId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<IrrigationData> irrigations = irrigationDataService.findByFarmId(farmId, pageable);
            return ResponseEntity.ok(irrigations);
        } catch (Exception e) {
            logger.error("Error retrieving farm irrigation data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving farm irrigation data: " + e.getMessage()));
        }
    }

    @GetMapping("/farm/{farmId}/recent")
    public ResponseEntity<?> getRecentIrrigationsByFarm(
            @PathVariable String farmId,
            @RequestParam(defaultValue = "30") int days) {
        try {
            List<IrrigationData> irrigations = irrigationDataService.findRecentIrrigationsByFarm(farmId, days);
            return ResponseEntity.ok(irrigations);
        } catch (Exception e) {
            logger.error("Error retrieving recent irrigation data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving recent irrigation data: " + e.getMessage()));
        }
    }

    @GetMapping("/crop/{cropProductionId}")
    public ResponseEntity<?> getIrrigationsByCrop(@PathVariable String cropProductionId) {
        try {
            List<IrrigationData> irrigations = irrigationDataService.findByCropProductionId(cropProductionId);
            return ResponseEntity.ok(irrigations);
        } catch (Exception e) {
            logger.error("Error retrieving crop irrigation data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving crop irrigation data: " + e.getMessage()));
        }
    }

    // Date-based endpoints
    @GetMapping("/date-range")
    public ResponseEntity<?> getIrrigationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<IrrigationData> irrigations = irrigationDataService.findByDateRange(startDate, endDate, pageable);
            return ResponseEntity.ok(irrigations);
        } catch (Exception e) {
            logger.error("Error retrieving irrigation data by date range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving irrigation data by date range: " + e.getMessage()));
        }
    }

    @GetMapping("/farm/{farmId}/date-range")
    public ResponseEntity<?> getIrrigationsByFarmAndDateRange(
            @PathVariable String farmId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<IrrigationData> irrigations = irrigationDataService.findByFarmAndDateRange(farmId, startDate, endDate);
            return ResponseEntity.ok(irrigations);
        } catch (Exception e) {
            logger.error("Error retrieving farm irrigation data by date range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving farm irrigation data by date range: " + e.getMessage()));
        }
    }

    // Method and source-based endpoints
    @GetMapping("/method/{method}")
    public ResponseEntity<?> getIrrigationsByMethod(@PathVariable IrrigationMethod method) {
        try {
            List<IrrigationData> irrigations = irrigationDataService.findByIrrigationMethod(method);
            return ResponseEntity.ok(irrigations);
        } catch (Exception e) {
            logger.error("Error retrieving irrigation data by method", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving irrigation data by method: " + e.getMessage()));
        }
    }

    @GetMapping("/source/{source}")
    public ResponseEntity<?> getIrrigationsBySource(@PathVariable WaterSource source) {
        try {
            List<IrrigationData> irrigations = irrigationDataService.findByWaterSource(source);
            return ResponseEntity.ok(irrigations);
        } catch (Exception e) {
            logger.error("Error retrieving irrigation data by source", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving irrigation data by source: " + e.getMessage()));
        }
    }



    @GetMapping("/farmer/{userId}")
    public ResponseEntity<?> getIrrigationsByFarmer(@PathVariable String userId) {
        try {
            if (DEBUG_MODE) {
                logger.info("Getting irrigations for farmer/user: {}", userId);
            }

            // Get all farm IDs for this user
            List<String> farmIds = getUserFarmIds(userId);

            if (farmIds.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "No farms found for this farmer",
                        "data", Collections.emptyList()
                ));
            }

            // Get all irrigations for these farms using the service
            List<IrrigationData> irrigations;
            try {
                irrigations = irrigationDataService.findByFarmIds(farmIds);
            } catch (Exception e) {
                logger.error("Error retrieving irrigation data from service for farmer: {}", userId, e);
                // Return empty list instead of 500 error
                irrigations = Collections.emptyList();
            }

            if (irrigations.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "No irrigation data found for this farmer",
                        "data", Collections.emptyList()
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Irrigation data retrieved successfully",
                    "data", irrigations
            ));

        } catch (Exception e) {
            logger.error("Error retrieving irrigation data for farmer: {}", userId, e);
            // ✅ Return empty data instead of 500 error to prevent frontend issues
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "No irrigation data available",
                    "data", Collections.emptyList()
            ));
        }
    }

    // Helper method to get farm IDs for a user
    private List<String> getUserFarmIds(String userId) {
        try {
            if (DEBUG_MODE) {
                logger.info("Getting farm IDs for farmer/user: {}", userId);
            }
            
            // ✅ Use FarmService to get actual farm IDs for the farmer
            List<String> farmIds = farmService.getFarmIdsByFarmerId(userId);
            
            if (DEBUG_MODE) {
                logger.info("Found {} farms for farmer: {}", farmIds.size(), userId);
            }
            
            return farmIds;
        } catch (Exception e) {
            logger.error("Error getting farm IDs for farmer: {}", userId, e);
            // Return empty list instead of throwing exception to prevent 500 error
            return Collections.emptyList();
        }
    }

    @GetMapping("/farm/{farmId}/method/{method}")
    public ResponseEntity<?> getIrrigationsByFarmAndMethod(
            @PathVariable String farmId,
            @PathVariable IrrigationMethod method) {
        try {
            List<IrrigationData> irrigations = irrigationDataService.findByFarmAndMethod(farmId, method);
            return ResponseEntity.ok(irrigations);
        } catch (Exception e) {
            logger.error("Error retrieving irrigation data by farm and method", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving irrigation data by farm and method: " + e.getMessage()));
        }
    }

    // Water usage and cost endpoints
    @GetMapping("/high-usage")
    public ResponseEntity<?> getHighWaterUsageIrrigations(
            @RequestParam(defaultValue = "5000") BigDecimal threshold) {
        try {
            List<IrrigationData> irrigations = irrigationDataService.findHighWaterUsage(threshold);
            return ResponseEntity.ok(irrigations);
        } catch (Exception e) {
            logger.error("Error retrieving high water usage irrigation data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving high water usage irrigation data: " + e.getMessage()));
        }
    }

    @GetMapping("/expensive")
    public ResponseEntity<?> getExpensiveIrrigations(
            @RequestParam(defaultValue = "1000") BigDecimal threshold) {
        try {
            List<IrrigationData> irrigations = irrigationDataService.findExpensiveIrrigations(threshold);
            return ResponseEntity.ok(irrigations);
        } catch (Exception e) {
            logger.error("Error retrieving expensive irrigation data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving expensive irrigation data: " + e.getMessage()));
        }
    }

    @GetMapping("/water-range")
    public ResponseEntity<?> getIrrigationsByWaterRange(
            @RequestParam BigDecimal minAmount,
            @RequestParam BigDecimal maxAmount) {
        try {
            List<IrrigationData> irrigations = irrigationDataService.findByWaterAmountRange(minAmount, maxAmount);
            return ResponseEntity.ok(irrigations);
        } catch (Exception e) {
            logger.error("Error retrieving irrigation data by water range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving irrigation data by water range: " + e.getMessage()));
        }
    }

    // Operator and equipment endpoints
    @GetMapping("/operator/{operatorName}")
    public ResponseEntity<?> getIrrigationsByOperator(@PathVariable String operatorName) {
        try {
            List<IrrigationData> irrigations = irrigationDataService.findByOperator(operatorName);
            return ResponseEntity.ok(irrigations);
        } catch (Exception e) {
            logger.error("Error retrieving irrigation data by operator", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving irrigation data by operator: " + e.getMessage()));
        }
    }

    @GetMapping("/farm/{farmId}/operators")
    public ResponseEntity<?> getOperatorsByFarm(@PathVariable String farmId) {
        try {
            List<String> operators = irrigationDataService.findOperatorsByFarm(farmId);
            return ResponseEntity.ok(operators);
        } catch (Exception e) {
            logger.error("Error retrieving operators by farm", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving operators by farm: " + e.getMessage()));
        }
    }

    @GetMapping("/with-fertilizer")
    public ResponseEntity<?> getIrrigationsWithFertilizer() {
        try {
            List<IrrigationData> irrigations = irrigationDataService.findWithFertilizer();
            return ResponseEntity.ok(irrigations);
        } catch (Exception e) {
            logger.error("Error retrieving irrigation data with fertilizer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving irrigation data with fertilizer: " + e.getMessage()));
        }
    }

    // Statistics and analytics endpoints
    @GetMapping("/farm/{farmId}/stats")
    public ResponseEntity<?> getFarmStatistics(
            @PathVariable String farmId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            Map<String, Object> stats = Map.of(
                    "totalWaterUsage", irrigationDataService.getTotalWaterUsage(farmId, startDate, endDate),
                    "totalCost", irrigationDataService.getTotalCost(farmId, startDate, endDate),
                    "averageWaterUsage", irrigationDataService.getAverageWaterUsage(farmId),
                    "irrigationCount", irrigationDataService.getIrrigationCount(farmId, startDate, endDate)
            );
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error retrieving farm statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving farm statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/farm/{farmId}/report")
    public ResponseEntity<?> generateFarmReport(
            @PathVariable String farmId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            Map<String, Object> report = irrigationDataService.generateFarmReport(farmId, startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            logger.error("Error generating farm report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error generating farm report: " + e.getMessage()));
        }
    }

    @GetMapping("/farm/{farmId}/daily-stats")
    public ResponseEntity<?> getDailyStatistics(
            @PathVariable String farmId,
            @RequestParam(defaultValue = "30") int days) {
        try {
            List<Object[]> stats = irrigationDataService.getDailyStats(farmId, days);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error retrieving daily statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving daily statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/farm/{farmId}/method-analysis")
    public ResponseEntity<?> getMethodAnalysis(@PathVariable String farmId) {
        try {
            List<Object[]> analysis = irrigationDataService.getMethodAnalysis(farmId);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            logger.error("Error retrieving method analysis", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving method analysis: " + e.getMessage()));
        }
    }

    @GetMapping("/farm/{farmId}/monthly-usage")
    public ResponseEntity<?> getMonthlyWaterUsage(
            @PathVariable String farmId,
            @RequestParam(defaultValue = "2024") int year) {
        try {
            List<Object[]> usage = irrigationDataService.getMonthlyWaterUsage(farmId, year);
            return ResponseEntity.ok(usage);
        } catch (Exception e) {
            logger.error("Error retrieving monthly water usage", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving monthly water usage: " + e.getMessage()));
        }
    }

    // Efficiency and optimization endpoints
    @GetMapping("/low-efficiency")
    public ResponseEntity<?> getLowEfficiencyIrrigations(
            @RequestParam(defaultValue = "0.01") BigDecimal threshold) {
        try {
            List<IrrigationData> irrigations = irrigationDataService.findLowEfficiencyIrrigations(threshold);
            return ResponseEntity.ok(irrigations);
        } catch (Exception e) {
            logger.error("Error retrieving low efficiency irrigation data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving low efficiency irrigation data: " + e.getMessage()));
        }
    }

    @GetMapping("/needs-optimization")
    public ResponseEntity<?> getIrrigationsNeedingOptimization() {
        try {
            List<IrrigationData> irrigations = irrigationDataService.getIrrigationsNeedingOptimization();
            return ResponseEntity.ok(irrigations);
        } catch (Exception e) {
            logger.error("Error retrieving irrigation data needing optimization", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving irrigation data needing optimization: " + e.getMessage()));
        }
    }

    @GetMapping("/farm/{farmId}/recommendations")
    public ResponseEntity<?> getFarmRecommendations(@PathVariable String farmId) {
        try {
            List<String> recommendations = irrigationDataService.generateRecommendations(farmId);
            return ResponseEntity.ok(Map.of("recommendations", recommendations));
        } catch (Exception e) {
            logger.error("Error generating recommendations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error generating recommendations: " + e.getMessage()));
        }
    }

    // Search endpoint
    @GetMapping("/search")
    public ResponseEntity<?> searchIrrigations(
            @RequestParam(required = false) String farmId,
            @RequestParam(required = false) IrrigationMethod method,
            @RequestParam(required = false) WaterSource source,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<IrrigationData> irrigations = irrigationDataService.searchIrrigations(
                    farmId, method, source, startDate, endDate, pageable);
            return ResponseEntity.ok(irrigations);
        } catch (Exception e) {
            logger.error("Error searching irrigation data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error searching irrigation data: " + e.getMessage()));
        }
    }

    // Bulk operations endpoints
    @PostMapping("/bulk")
    public ResponseEntity<?> createBulkIrrigations(@Valid @RequestBody List<IrrigationData> irrigationDataList) {
        try {
            List<IrrigationData> savedData = irrigationDataService.saveAll(irrigationDataList);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedData);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error in bulk creation", e);
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Validation error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating bulk irrigation data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error creating bulk irrigation data: " + e.getMessage()));
        }
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<?> deleteBulkIrrigations(@RequestBody List<String> ids) {
        try {
            irrigationDataService.deleteAll(ids);
            return ResponseEntity.ok(createSuccessResponse("Bulk irrigation data deleted successfully"));
        } catch (RuntimeException e) {
            logger.error("Error in bulk delete", e);
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting bulk irrigation data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error deleting bulk irrigation data: " + e.getMessage()));
        }
    }

    // Utility endpoints
    @GetMapping("/methods")
    public ResponseEntity<IrrigationMethod[]> getAllIrrigationMethods() {
        logger.info("Fetching all irrigation methods");
        return ResponseEntity.ok(IrrigationMethod.values());
    }

    @GetMapping("/sources")
    public ResponseEntity<WaterSource[]> getAllWaterSources() {
        logger.info("Fetching all water sources");
        return ResponseEntity.ok(WaterSource.values());
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Map<String, Boolean>> checkIfExists(@PathVariable String id) {
        boolean exists = irrigationDataService.existsById(id);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // Exception handlers
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleValidationException(IllegalArgumentException e) {
        logger.error("Validation exception", e);
        return ResponseEntity.badRequest()
                .body(createErrorResponse("Validation error: " + e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        logger.error("Runtime exception", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("Resource not found: " + e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception e) {
        logger.error("General exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Internal server error: " + e.getMessage()));
    }
}