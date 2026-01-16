package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.SupplyChain;
import SmartAgricultural.Management.Model.SupplyChain.Stage;
import SmartAgricultural.Management.Model.SupplyChain.QualityStatus;
import SmartAgricultural.Management.Service.SupplyChainService;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.exception.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/supply-chain")
@Validated
@CrossOrigin(origins = "*")
public class SupplyChainController {

    @Autowired
    private SupplyChainService supplyChainService;

    // Basic CRUD Operations
    @PostMapping
    public ResponseEntity<?> createSupplyChainStage(@Valid @RequestBody SupplyChain supplyChain) {
        try {
            // Additional validation before creation
            if (supplyChain.getQuantityIn() != null) {
                BigDecimal qtyIn = supplyChain.getQuantityIn();
                BigDecimal qtyOut = supplyChain.getQuantityOut() != null ? supplyChain.getQuantityOut() : BigDecimal.ZERO;
                BigDecimal loss = supplyChain.getLossQuantity() != null ? supplyChain.getLossQuantity() : BigDecimal.ZERO;

                // Validate quantityOut
                if (qtyOut.compareTo(qtyIn) > 0) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Validation Error");
                    error.put("message", String.format("Quantity Out (%.2f) cannot exceed Quantity In (%.2f)", qtyOut, qtyIn));
                    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
                }

                // Validate lossQuantity
                if (loss.compareTo(qtyIn) > 0) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Validation Error");
                    error.put("message", String.format("Loss Quantity (%.2f) cannot exceed Quantity In (%.2f)", loss, qtyIn));
                    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
                }

                // Validate combined total
                if (qtyOut.add(loss).compareTo(qtyIn) > 0) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Validation Error");
                    error.put("message", String.format("Quantity Out (%.2f) + Loss Quantity (%.2f) = %.2f exceeds Quantity In (%.2f)",
                            qtyOut, loss, qtyOut.add(loss), qtyIn));
                    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
                }
            }

            SupplyChain created = supplyChainService.create(supplyChain);
            return new ResponseEntity<>(created, HttpStatus.CREATED);

        } catch (ValidationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Validation Error");
            error.put("message", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Validation Error");
            error.put("message", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal Server Error");
            error.put("message", "An error occurred: " + e.getMessage());
            e.printStackTrace(); // Pour le debugging
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<SupplyChain>> getSupplyChainByFarmer(
            @PathVariable @NotBlank String farmerId) {
        try {
            List<SupplyChain> supplyChains = supplyChainService.findByFarmerId(farmerId);
            return new ResponseEntity<>(supplyChains, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplyChain> getSupplyChainStageById(@PathVariable String id) {
        try {
            SupplyChain supplyChain = supplyChainService.findById(id);
            return new ResponseEntity<>(supplyChain, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/farm/{farmId}")
    public ResponseEntity<List<SupplyChain>> getSupplyChainByFarm(
            @PathVariable @NotBlank String farmId) {
        try {
            // Récupérer toutes les productions de la ferme
            List<SupplyChain> supplyChains = supplyChainService.findByFarmId(farmId);
            return new ResponseEntity<>(supplyChains, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateSupplyChainStage(
            @PathVariable String id,
            @Valid @RequestBody SupplyChain supplyChain) {
        try {
            // Additional validation before update
            if (supplyChain.getQuantityIn() != null) {
                BigDecimal qtyIn = supplyChain.getQuantityIn();
                BigDecimal qtyOut = supplyChain.getQuantityOut() != null ? supplyChain.getQuantityOut() : BigDecimal.ZERO;
                BigDecimal loss = supplyChain.getLossQuantity() != null ? supplyChain.getLossQuantity() : BigDecimal.ZERO;

                // Validate quantityOut
                if (qtyOut.compareTo(qtyIn) > 0) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Validation Error");
                    error.put("message", String.format("Quantity Out (%.2f) cannot exceed Quantity In (%.2f)", qtyOut, qtyIn));
                    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
                }

                // Validate lossQuantity
                if (loss.compareTo(qtyIn) > 0) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Validation Error");
                    error.put("message", String.format("Loss Quantity (%.2f) cannot exceed Quantity In (%.2f)", loss, qtyIn));
                    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
                }

                // Validate combined total
                if (qtyOut.add(loss).compareTo(qtyIn) > 0) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Validation Error");
                    error.put("message", String.format("Quantity Out (%.2f) + Loss Quantity (%.2f) = %.2f exceeds Quantity In (%.2f)",
                            qtyOut, loss, qtyOut.add(loss), qtyIn));
                    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
                }
            }

            SupplyChain updated = supplyChainService.update(id, supplyChain);
            return new ResponseEntity<>(updated, HttpStatus.OK);

        } catch (ResourceNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Resource Not Found");
            error.put("message", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);

        } catch (ValidationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Validation Error");
            error.put("message", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Validation Error");
            error.put("message", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal Server Error");
            error.put("message", "An error occurred: " + e.getMessage());
            e.printStackTrace(); // Pour le debugging
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteSupplyChainStage(@PathVariable String id) {
        try {
            supplyChainService.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Supply chain stage deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<Page<SupplyChain>> getAllSupplyChainStages(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "stageOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                    Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<SupplyChain> stages = supplyChainService.findAll(pageable);
            return new ResponseEntity<>(stages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Crop Production specific endpoints
    @GetMapping("/crop-production/{cropProductionId}")
    public ResponseEntity<List<SupplyChain>> getSupplyChainByCropProduction(
            @PathVariable @NotBlank String cropProductionId) {
        try {
            List<SupplyChain> stages = supplyChainService.findByCropProductionId(cropProductionId);
            return new ResponseEntity<>(stages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/crop-production/{cropProductionId}/paged")
    public ResponseEntity<Page<SupplyChain>> getSupplyChainByCropProductionPaged(
            @PathVariable @NotBlank String cropProductionId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        try {
            Pageable pageable = PageRequest.of(page, size,
                    Sort.by(Sort.Direction.ASC, "stageOrder"));
            Page<SupplyChain> stages = supplyChainService.findByCropProductionId(cropProductionId, pageable);
            return new ResponseEntity<>(stages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/crop-production/{cropProductionId}/chain-summary")
    public ResponseEntity<Map<String, Object>> getSupplyChainSummary(
            @PathVariable @NotBlank String cropProductionId) {
        try {
            Map<String, Object> summary = supplyChainService.getSupplyChainSummary(cropProductionId);
            return new ResponseEntity<>(summary, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Stage-based operations
    @GetMapping("/stage/{stage}")
    public ResponseEntity<Page<SupplyChain>> getSupplyChainByStage(
            @PathVariable Stage stage,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        try {
            Pageable pageable = PageRequest.of(page, size,
                    Sort.by(Sort.Direction.DESC, "stageStartDate"));
            Page<SupplyChain> stages = supplyChainService.findByStage(stage, pageable);
            return new ResponseEntity<>(stages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/crop-production/{cropProductionId}/stage/{stage}")
    public ResponseEntity<List<SupplyChain>> getSupplyChainByCropProductionAndStage(
            @PathVariable @NotBlank String cropProductionId,
            @PathVariable Stage stage) {
        try {
            List<SupplyChain> stages = supplyChainService.findByCropProductionIdAndStage(cropProductionId, stage);
            return new ResponseEntity<>(stages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<SupplyChain> completeStage(
            @PathVariable String id,
            @RequestBody Map<String, Object> completionData) {
        try {
            BigDecimal quantityOut = null;
            String handlingNotes = null;
            QualityStatus qualityStatus = null;

            if (completionData.containsKey("quantityOut")) {
                quantityOut = new BigDecimal(completionData.get("quantityOut").toString());
            }
            if (completionData.containsKey("handlingNotes")) {
                handlingNotes = completionData.get("handlingNotes").toString();
            }
            if (completionData.containsKey("qualityStatus")) {
                qualityStatus = QualityStatus.valueOf(completionData.get("qualityStatus").toString());
            }

            SupplyChain completed = supplyChainService.completeStage(id, quantityOut, handlingNotes, qualityStatus);
            return new ResponseEntity<>(completed, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/crop-production/{cropProductionId}/next-stage")
    public ResponseEntity<SupplyChain> createNextStage(
            @PathVariable @NotBlank String cropProductionId,
            @RequestBody Map<String, Object> stageData) {
        try {
            String location = stageData.get("location").toString();
            String responsibleParty = stageData.get("responsibleParty").toString();
            String facilityName = stageData.containsKey("facilityName") ?
                    stageData.get("facilityName").toString() : null;

            SupplyChain nextStage = supplyChainService.createNextStage(
                    cropProductionId, location, responsibleParty, facilityName);
            return new ResponseEntity<>(nextStage, HttpStatus.CREATED);
        } catch (ValidationException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<SupplyChain>> getAllSupplyChainStagesList() {
        try {
            List<SupplyChain> stages = supplyChainService.findAll();
            return new ResponseEntity<>(stages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Quality management
    @GetMapping("/quality/{qualityStatus}")
    public ResponseEntity<Page<SupplyChain>> getSupplyChainByQuality(
            @PathVariable QualityStatus qualityStatus,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        try {
            Pageable pageable = PageRequest.of(page, size,
                    Sort.by(Sort.Direction.DESC, "stageStartDate"));
            Page<SupplyChain> stages = supplyChainService.findByQualityStatus(qualityStatus, pageable);
            return new ResponseEntity<>(stages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/quality-issues")
    public ResponseEntity<List<SupplyChain>> getQualityIssues() {
        try {
            List<SupplyChain> issues = supplyChainService.findQualityIssues();
            return new ResponseEntity<>(issues, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/quality-update")
    public ResponseEntity<SupplyChain> updateQualityStatus(
            @PathVariable String id,
            @RequestBody Map<String, Object> qualityData) {
        try {
            QualityStatus qualityStatus = QualityStatus.valueOf(qualityData.get("qualityStatus").toString());
            String qualityTests = qualityData.containsKey("qualityTests") ?
                    qualityData.get("qualityTests").toString() : null;

            SupplyChain updated = supplyChainService.updateQualityStatus(id, qualityStatus, qualityTests);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Loss management
    @GetMapping("/losses")
    public ResponseEntity<List<SupplyChain>> getStagesWithLosses() {
        try {
            List<SupplyChain> stages = supplyChainService.findStagesWithLosses();
            return new ResponseEntity<>(stages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/high-losses")
    public ResponseEntity<List<SupplyChain>> getStagesWithHighLosses(
            @RequestParam(defaultValue = "5.0") BigDecimal threshold) {
        try {
            List<SupplyChain> stages = supplyChainService.findStagesWithHighLosses(threshold);
            return new ResponseEntity<>(stages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/loss-update")
    public ResponseEntity<SupplyChain> updateLossInformation(
            @PathVariable String id,
            @RequestBody Map<String, Object> lossData) {
        try {
            BigDecimal lossQuantity = lossData.containsKey("lossQuantity") ?
                    new BigDecimal(lossData.get("lossQuantity").toString()) : null;
            String lossReason = lossData.containsKey("lossReason") ?
                    lossData.get("lossReason").toString() : null;

            SupplyChain updated = supplyChainService.updateLossInformation(id, lossQuantity, lossReason);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Tracking operations
    @GetMapping("/track/{trackingCode}")
    public ResponseEntity<SupplyChain> trackByCode(@PathVariable @NotBlank String trackingCode) {
        try {
            SupplyChain stage = supplyChainService.findByTrackingCode(trackingCode);
            return new ResponseEntity<>(stage, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<SupplyChain> getByTransactionId(@PathVariable @NotBlank String transactionId) {
        try {
            SupplyChain stage = supplyChainService.findByTransactionId(transactionId);
            return new ResponseEntity<>(stage, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Status queries
    @GetMapping("/incomplete")
    public ResponseEntity<List<SupplyChain>> getIncompleteStages() {
        try {
            List<SupplyChain> stages = supplyChainService.findIncompleteStages();
            return new ResponseEntity<>(stages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/completed")
    public ResponseEntity<List<SupplyChain>> getCompletedStages() {
        try {
            List<SupplyChain> stages = supplyChainService.findCompletedStages();
            return new ResponseEntity<>(stages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/active-at")
    public ResponseEntity<List<SupplyChain>> getActiveStagesAtDate(
            @RequestParam String date) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date);
            List<SupplyChain> stages = supplyChainService.findActiveStagesAtDate(dateTime);
            return new ResponseEntity<>(stages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Search and filtering
    @GetMapping("/search")
    public ResponseEntity<Page<SupplyChain>> searchSupplyChain(
            @RequestParam String searchTerm,
            @RequestParam(required = false) String cropProductionId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        try {
            Pageable pageable = PageRequest.of(page, size,
                    Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<SupplyChain> results = supplyChainService.searchSupplyChain(
                    cropProductionId, searchTerm, pageable);
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<SupplyChain>> filterSupplyChain(
            @RequestParam(required = false) Stage stage,
            @RequestParam(required = false) QualityStatus qualityStatus,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String responsibleParty,
            @RequestParam(required = false) BigDecimal minCost,
            @RequestParam(required = false) BigDecimal maxCost,
            @RequestParam(required = false) String cropProductionId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        try {
            Pageable pageable = PageRequest.of(page, size,
                    Sort.by(Sort.Direction.DESC, "stageStartDate"));
            Page<SupplyChain> results = supplyChainService.findWithFilters(
                    cropProductionId, stage, qualityStatus, location,
                    responsibleParty, minCost, maxCost, pageable);
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Analytics endpoints
    @GetMapping("/analytics/stage-statistics")
    public ResponseEntity<Map<String, Long>> getStageStatistics() {
        try {
            Map<String, Long> stats = supplyChainService.getStageStatistics();
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/analytics/quality-statistics")
    public ResponseEntity<Map<String, Long>> getQualityStatistics() {
        try {
            Map<String, Long> stats = supplyChainService.getQualityStatistics();
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/analytics/crop-production/{cropProductionId}/metrics")
    public ResponseEntity<Map<String, Object>> getCropProductionMetrics(
            @PathVariable @NotBlank String cropProductionId) {
        try {
            Map<String, Object> metrics = supplyChainService.getCropProductionMetrics(cropProductionId);
            return new ResponseEntity<>(metrics, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/analytics/performance/{cropProductionId}")
    public ResponseEntity<Map<String, Object>> getPerformanceAnalysis(
            @PathVariable @NotBlank String cropProductionId) {
        try {
            Map<String, Object> analysis = supplyChainService.getPerformanceAnalysis(cropProductionId);
            return new ResponseEntity<>(analysis, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Bulk operations
    @PostMapping("/bulk")
    public ResponseEntity<List<SupplyChain>> createBulkSupplyChainStages(
            @Valid @RequestBody List<SupplyChain> supplyChains) {
        try {
            List<SupplyChain> created = supplyChainService.createSupplyChainStages(supplyChains);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (ValidationException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/bulk")
    public ResponseEntity<List<SupplyChain>> updateBulkSupplyChainStages(
            @Valid @RequestBody List<SupplyChain> supplyChains) {
        try {
            List<SupplyChain> updated = supplyChainService.updateSupplyChainStages(supplyChains);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Map<String, String>> deleteBulkSupplyChainStages(
            @RequestBody List<String> ids) {
        try {
            supplyChainService.deleteSupplyChainStages(ids);
            Map<String, String> response = new HashMap<>();
            response.put("message", ids.size() + " supply chain stages deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Maintenance endpoints
    @DeleteMapping("/cleanup/old-completed")
    public ResponseEntity<Map<String, Object>> cleanupOldCompletedEntries(
            @RequestParam(defaultValue = "365") int daysOld) {
        try {
            int deletedCount = supplyChainService.cleanupOldCompletedEntries(daysOld);
            Map<String, Object> response = new HashMap<>();
            response.put("deletedCount", deletedCount);
            response.put("message", "Cleanup completed successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Exception handlers
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Validation Error");
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Resource Not Found");
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid Argument");
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}