package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.FertilizerUsage;
import SmartAgricultural.Management.Model.FertilizerUsage.FertilizerType;
import SmartAgricultural.Management.Model.FertilizerUsage.Unit;
import SmartAgricultural.Management.Model.FertilizerUsage.ApplicationMethod;
import SmartAgricultural.Management.Repository.FertilizerUsageRepository;
import SmartAgricultural.Management.Service.FertilizerUsageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/fertilizer-usages")
@CrossOrigin(origins = "*")
public class FertilizerUsageController {

    @Autowired
    private FertilizerUsageService fertilizerUsageService;


    @Autowired
    private FertilizerUsageRepository fertilizerUsageRepository;

    // Basic CRUD operations

    @PostMapping
    public ResponseEntity<?> createFertilizerUsage(@Valid @RequestBody FertilizerUsage fertilizerUsage) {
        try {
            FertilizerUsage created = fertilizerUsageService.createFertilizerUsage(fertilizerUsage);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create fertilizer usage: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFertilizerUsageById(@PathVariable String id) {
        try {
            Optional<FertilizerUsage> fertilizerUsage = fertilizerUsageService.findFertilizerUsageById(id);
            if (fertilizerUsage.isPresent()) {
                return ResponseEntity.ok(fertilizerUsage.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usage: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllFertilizerUsages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "applicationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            Page<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findAllFertilizerUsages(page, size, sortBy, sortDirection);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFertilizerUsage(@PathVariable String id,
                                                   @Valid @RequestBody FertilizerUsage fertilizerUsage) {
        try {
            FertilizerUsage updated = fertilizerUsageService.updateFertilizerUsage(id, fertilizerUsage);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update fertilizer usage: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFertilizerUsage(@PathVariable String id) {
        try {
            fertilizerUsageService.deleteFertilizerUsage(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete fertilizer usage: " + e.getMessage()));
        }
    }

    // Crop production related endpoints

    @GetMapping("/crop-production/{cropProductionId}")
    public ResponseEntity<?> getFertilizerUsagesByCropProduction(
            @PathVariable String cropProductionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesByCropProductionId(cropProductionId, page, size);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/crop-production/{cropProductionId}/{id}")
    public ResponseEntity<?> getFertilizerUsageByIdAndCropProduction(
            @PathVariable String cropProductionId,
            @PathVariable String id) {
        try {
            Optional<FertilizerUsage> fertilizerUsage = fertilizerUsageService
                    .findFertilizerUsageByIdAndCropProductionId(id, cropProductionId);
            if (fertilizerUsage.isPresent()) {
                return ResponseEntity.ok(fertilizerUsage.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usage: " + e.getMessage()));
        }
    }

    @DeleteMapping("/crop-production/{cropProductionId}")
    public ResponseEntity<?> deleteFertilizerUsagesByCropProduction(@PathVariable String cropProductionId) {
        try {
            fertilizerUsageService.deleteFertilizerUsagesByCropProductionId(cropProductionId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete fertilizer usages: " + e.getMessage()));
        }
    }

    // Search by fertilizer type

    @GetMapping("/type/{fertilizerType}")
    public ResponseEntity<?> getFertilizerUsagesByType(
            @PathVariable FertilizerType fertilizerType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesByType(fertilizerType, page, size);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/type/{fertilizerType}/crop-production/{cropProductionId}")
    public ResponseEntity<?> getFertilizerUsagesByTypeAndCropProduction(
            @PathVariable FertilizerType fertilizerType,
            @PathVariable String cropProductionId) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesByTypeAndCropProduction(fertilizerType, cropProductionId);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    // Search by fertilizer name and brand

    @GetMapping("/search/name")
    public ResponseEntity<?> searchFertilizerUsagesByName(@RequestParam String fertilizerName) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .searchFertilizerUsagesByName(fertilizerName);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to search fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/search/brand")
    public ResponseEntity<?> searchFertilizerUsagesByBrand(@RequestParam String brand) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .searchFertilizerUsagesByBrand(brand);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to search fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/search/name-brand")
    public ResponseEntity<?> getFertilizerUsagesByNameAndBrand(
            @RequestParam String fertilizerName,
            @RequestParam String brand) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesByNameAndBrand(fertilizerName, brand);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    // Search by application method

    @GetMapping("/application-method/{applicationMethod}")
    public ResponseEntity<?> getFertilizerUsagesByApplicationMethod(
            @PathVariable ApplicationMethod applicationMethod,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesByApplicationMethod(applicationMethod, page, size);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    // Search by application stage

    @GetMapping("/search/application-stage")
    public ResponseEntity<?> searchFertilizerUsagesByApplicationStage(@RequestParam String applicationStage) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .searchFertilizerUsagesByApplicationStage(applicationStage);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to search fertilizer usages: " + e.getMessage()));
        }
    }

    // Date-based searches

    @GetMapping("/date/{applicationDate}")
    public ResponseEntity<?> getFertilizerUsagesByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate applicationDate) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesByApplicationDate(applicationDate);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<?> getFertilizerUsagesByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesByApplicationDateBetween(startDate, endDate);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/recent/{days}")
    public ResponseEntity<?> getRecentFertilizerUsages(@PathVariable int days) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findRecentFertilizerUsages(days);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve recent fertilizer usages: " + e.getMessage()));
        }
    }

    // Quantity and cost searches

    @GetMapping("/quantity/greater-than/{quantity}")
    public ResponseEntity<?> getFertilizerUsagesByQuantityGreaterThan(@PathVariable BigDecimal quantity) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesByQuantityGreaterThan(quantity);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/quantity/range")
    public ResponseEntity<?> getFertilizerUsagesByQuantityRange(
            @RequestParam BigDecimal minQuantity,
            @RequestParam BigDecimal maxQuantity) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesByQuantityRange(minQuantity, maxQuantity);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/unit/{unit}")
    public ResponseEntity<?> getFertilizerUsagesByUnit(@PathVariable Unit unit) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesByUnit(unit);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/cost/greater-than/{cost}")
    public ResponseEntity<?> getFertilizerUsagesByCostGreaterThan(@PathVariable BigDecimal cost) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesByTotalCostGreaterThan(cost);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/cost/range")
    public ResponseEntity<?> getFertilizerUsagesByCostRange(
            @RequestParam BigDecimal minCost,
            @RequestParam BigDecimal maxCost) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesByCostRange(minCost, maxCost);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/cost/high/{threshold}")
    public ResponseEntity<?> getHighCostFertilizerUsages(@PathVariable BigDecimal threshold) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findHighCostFertilizerUsages(threshold);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve high cost fertilizer usages: " + e.getMessage()));
        }
    }

    // Effectiveness searches

    @GetMapping("/effectiveness/{rating}")
    public ResponseEntity<?> getFertilizerUsagesByEffectivenessRating(@PathVariable Integer rating) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesByEffectivenessRating(rating);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/effectiveness/range")
    public ResponseEntity<?> getFertilizerUsagesByEffectivenessRange(
            @RequestParam Integer minRating,
            @RequestParam Integer maxRating) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesByEffectivenessRatingRange(minRating, maxRating);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/effectiveness/high/{minRating}")
    public ResponseEntity<?> getHighEffectivenessFertilizerUsages(@PathVariable Integer minRating) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findHighEffectivenessFertilizerUsages(minRating);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve high effectiveness fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/effectiveness/low/{maxRating}")
    public ResponseEntity<?> getLowEffectivenessFertilizerUsages(@PathVariable Integer maxRating) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findLowEffectivenessFertilizerUsages(maxRating);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve low effectiveness fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/effectiveness/unrated")
    public ResponseEntity<?> getUnratedFertilizerUsages() {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findUnratedFertilizerUsages();
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve unrated fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/effectiveness/rated")
    public ResponseEntity<?> getRatedFertilizerUsages() {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findRatedFertilizerUsages();
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve rated fertilizer usages: " + e.getMessage()));
        }
    }

    // Supplier searches

    @GetMapping("/supplier/{supplier}")
    public ResponseEntity<?> getFertilizerUsagesBySupplier(
            @PathVariable String supplier,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesBySupplier(supplier, page, size);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/search/supplier")
    public ResponseEntity<?> searchFertilizerUsagesBySupplier(@RequestParam String supplier) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .searchFertilizerUsagesBySupplier(supplier);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to search fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/supplier/{supplier}/date-range")
    public ResponseEntity<?> getFertilizerUsagesBySupplierAndDateRange(
            @PathVariable String supplier,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesBySupplierAndDateRange(supplier, startDate, endDate);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    // Expiry date searches

    @GetMapping("/expiry/{expiryDate}")
    public ResponseEntity<?> getFertilizerUsagesByExpiryDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate expiryDate) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesByExpiryDate(expiryDate);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/expired")
    public ResponseEntity<?> getExpiredFertilizerUsages() {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findExpiredFertilizerUsages();
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve expired fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/expiring-soon/{daysAhead}")
    public ResponseEntity<?> getExpiringSoonFertilizerUsages(@PathVariable int daysAhead) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findExpiringSoonFertilizerUsages(daysAhead);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve expiring fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/expiry/without")
    public ResponseEntity<?> getFertilizerUsagesWithoutExpiry() {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesWithoutExpiry();
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages without expiry: " + e.getMessage()));
        }
    }

    @GetMapping("/expiry/with")
    public ResponseEntity<?> getFertilizerUsagesWithExpiry() {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesWithExpiry();
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages with expiry: " + e.getMessage()));
        }
    }

    // Batch number searches

    @GetMapping("/batch/{batchNumber}")
    public ResponseEntity<?> getFertilizerUsagesByBatchNumber(@PathVariable String batchNumber) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesByBatchNumber(batchNumber);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/search/batch")
    public ResponseEntity<?> searchFertilizerUsagesByBatchNumber(@RequestParam String batchNumber) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .searchFertilizerUsagesByBatchNumber(batchNumber);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to search fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/batch/{batchNumber}/supplier/{supplier}")
    public ResponseEntity<?> getFertilizerUsageByBatchAndSupplier(
            @PathVariable String batchNumber,
            @PathVariable String supplier) {
        try {
            Optional<FertilizerUsage> fertilizerUsage = fertilizerUsageService
                    .findFertilizerUsageByBatchNumberAndSupplier(batchNumber, supplier);
            if (fertilizerUsage.isPresent()) {
                return ResponseEntity.ok(fertilizerUsage.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usage: " + e.getMessage()));
        }
    }

    // Weather and soil conditions searches

    @GetMapping("/search/weather")
    public ResponseEntity<?> searchFertilizerUsagesByWeatherConditions(@RequestParam String weatherConditions) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .searchFertilizerUsagesByWeatherConditions(weatherConditions);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to search fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/search/soil")
    public ResponseEntity<?> searchFertilizerUsagesBySoilConditions(@RequestParam String soilConditions) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .searchFertilizerUsagesBySoilConditions(soilConditions);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to search fertilizer usages: " + e.getMessage()));
        }
    }

    // Operator searches

    @GetMapping("/operator/{operatorName}")
    public ResponseEntity<?> getFertilizerUsagesByOperator(@PathVariable String operatorName) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesByOperator(operatorName);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/search/operator")
    public ResponseEntity<?> searchFertilizerUsagesByOperator(@RequestParam String operatorName) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .searchFertilizerUsagesByOperator(operatorName);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to search fertilizer usages: " + e.getMessage()));
        }
    }

    // Statistical endpoints

    @GetMapping("/statistics/count/type/{fertilizerType}")
    public ResponseEntity<?> getCountByType(@PathVariable FertilizerType fertilizerType) {
        try {
            Long count = fertilizerUsageService.countFertilizerUsagesByType(fertilizerType);
            return ResponseEntity.ok(Map.of("count", count, "type", fertilizerType));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get count: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics/count/crop-production/{cropProductionId}")
    public ResponseEntity<?> getCountByCropProduction(@PathVariable String cropProductionId) {
        try {
            Long count = fertilizerUsageService.countFertilizerUsagesByCropProduction(cropProductionId);
            return ResponseEntity.ok(Map.of("count", count, "cropProductionId", cropProductionId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get count: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics/count/application-method/{applicationMethod}")
    public ResponseEntity<?> getCountByApplicationMethod(@PathVariable ApplicationMethod applicationMethod) {
        try {
            Long count = fertilizerUsageService.countFertilizerUsagesByApplicationMethod(applicationMethod);
            return ResponseEntity.ok(Map.of("count", count, "applicationMethod", applicationMethod));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get count: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics/count/high-effectiveness/{minRating}")
    public ResponseEntity<?> getCountHighEffectiveness(@PathVariable Integer minRating) {
        try {
            Long count = fertilizerUsageService.countHighEffectivenessFertilizerUsages(minRating);
            return ResponseEntity.ok(Map.of("count", count, "minRating", minRating));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get count: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics/count/supplier/{supplier}")
    public ResponseEntity<?> getCountBySupplier(@PathVariable String supplier) {
        try {
            Long count = fertilizerUsageService.countFertilizerUsagesBySupplier(supplier);
            return ResponseEntity.ok(Map.of("count", count, "supplier", supplier));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get count: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics/count/total")
    public ResponseEntity<?> getTotalCount() {
        try {
            Long count = fertilizerUsageService.countTotalFertilizerUsages();
            return ResponseEntity.ok(Map.of("totalCount", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get total count: " + e.getMessage()));
        }
    }

    // Sum and average endpoints

    @GetMapping("/statistics/total-quantity/crop-production/{cropProductionId}/unit/{unit}")
    public ResponseEntity<?> getTotalQuantityByCropProductionAndUnit(
            @PathVariable String cropProductionId,
            @PathVariable Unit unit) {
        try {
            BigDecimal totalQuantity = fertilizerUsageService
                    .getTotalQuantityByCropProductionIdAndUnit(cropProductionId, unit);
            return ResponseEntity.ok(Map.of(
                    "totalQuantity", totalQuantity,
                    "cropProductionId", cropProductionId,
                    "unit", unit
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get total quantity: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics/total-cost/crop-production/{cropProductionId}")
    public ResponseEntity<?> getTotalCostByCropProduction(@PathVariable String cropProductionId) {
        try {
            BigDecimal totalCost = fertilizerUsageService.getTotalCostByCropProductionId(cropProductionId);
            return ResponseEntity.ok(Map.of("totalCost", totalCost, "cropProductionId", cropProductionId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get total cost: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics/total-quantity/type/{fertilizerType}/unit/{unit}")
    public ResponseEntity<?> getTotalQuantityByTypeAndUnit(
            @PathVariable FertilizerType fertilizerType,
            @PathVariable Unit unit) {
        try {
            BigDecimal totalQuantity = fertilizerUsageService
                    .getTotalQuantityByFertilizerTypeAndUnit(fertilizerType, unit);
            return ResponseEntity.ok(Map.of(
                    "totalQuantity", totalQuantity,
                    "fertilizerType", fertilizerType,
                    "unit", unit
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get total quantity: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics/total-cost/type/{fertilizerType}")
    public ResponseEntity<?> getTotalCostByType(@PathVariable FertilizerType fertilizerType) {
        try {
            BigDecimal totalCost = fertilizerUsageService.getTotalCostByFertilizerType(fertilizerType);
            return ResponseEntity.ok(Map.of("totalCost", totalCost, "fertilizerType", fertilizerType));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get total cost: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics/average-effectiveness/type/{fertilizerType}")
    public ResponseEntity<?> getAverageEffectivenessByType(@PathVariable FertilizerType fertilizerType) {
        try {
            Double avgEffectiveness = fertilizerUsageService
                    .getAverageEffectivenessRatingByFertilizerType(fertilizerType);
            return ResponseEntity.ok(Map.of(
                    "averageEffectiveness", avgEffectiveness,
                    "fertilizerType", fertilizerType
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get average effectiveness: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics/average-cost-per-unit/type/{fertilizerType}")
    public ResponseEntity<?> getAverageCostPerUnitByType(@PathVariable FertilizerType fertilizerType) {
        try {
            BigDecimal avgCost = fertilizerUsageService.getAverageCostPerUnitByFertilizerType(fertilizerType);
            return ResponseEntity.ok(Map.of("averageCostPerUnit", avgCost, "fertilizerType", fertilizerType));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get average cost per unit: " + e.getMessage()));
        }
    }



    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<?> getFertilizerUsagesByFarmer(
            @PathVariable String farmerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // Récupérer directement toutes les productions du fermier
            Page<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findFertilizerUsagesByFarmer(farmerId, page, size);

            return ResponseEntity.ok(Map.of(
                    "content", fertilizerUsages.getContent(),
                    "totalElements", fertilizerUsages.getTotalElements(),
                    "totalPages", fertilizerUsages.getTotalPages(),
                    "currentPage", fertilizerUsages.getNumber(),
                    "pageSize", fertilizerUsages.getSize(),
                    "farmerId", farmerId
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Failed to retrieve fertilizer usages: " + e.getMessage(),
                            "content", List.of(),
                            "totalElements", 0
                    ));
        }
    }




    // Test endpoint - Ajoutez ceci dans FertilizerUsageController.java
    @GetMapping("/test/farmer/{farmerId}")
    public ResponseEntity<?> testGetByFarmer(@PathVariable String farmerId) {
        try {
            // Test 1: Récupérer TOUTES les fertilizers
            List<FertilizerUsage> allFerts = fertilizerUsageRepository.findAll();
            System.out.println("Total fertilizers in DB: " + allFerts.size());

            // Test 2: Récupérer par production ID directement
            List<FertilizerUsage> byProd = fertilizerUsageRepository
                    .findByCropProductionId("CP737050DVJQW5");
            System.out.println("By production CP737050DVJQW5: " + byProd.size());

            // Test 3: Query native
            Page<FertilizerUsage> result = fertilizerUsageRepository
                    .findByFarmerId(farmerId, PageRequest.of(0, 100));

            return ResponseEntity.ok(Map.of(
                    "totalInDB", allFerts.size(),
                    "byProductionId", byProd.size(),
                    "byFarmerId", result.getContent().size(),
                    "allFertilizers", allFerts,
                    "farmerFertilizers", result.getContent()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/statistics/average-total-cost")
    public ResponseEntity<?> getAverageTotalCost() {
        try {
            BigDecimal avgTotalCost = fertilizerUsageService.getAverageTotalCost();
            return ResponseEntity.ok(Map.of("averageTotalCost", avgTotalCost));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get average total cost: " + e.getMessage()));
        }
    }

    // Complex search endpoints

    @GetMapping("/search/effective-affordable")
    public ResponseEntity<?> getEffectiveAndAffordableFertilizerUsages(
            @RequestParam Integer minRating,
            @RequestParam BigDecimal maxCost) {
        try {
            List<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findEffectiveAndAffordableFertilizerUsages(minRating, maxCost);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve effective and affordable fertilizer usages: " + e.getMessage()));
        }
    }

    @GetMapping("/search/criteria")
    public ResponseEntity<?> searchFertilizerUsagesByCriteria(
            @RequestParam(required = false) String cropProductionId,
            @RequestParam(required = false) FertilizerType fertilizerType,
            @RequestParam(required = false) ApplicationMethod applicationMethod,
            @RequestParam(required = false) String supplier,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) BigDecimal minCost,
            @RequestParam(required = false) BigDecimal maxCost,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .searchFertilizerUsagesByCriteria(cropProductionId, fertilizerType, applicationMethod,
                            supplier, startDate, endDate, minCost, maxCost, minRating, page, size);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to search fertilizer usages: " + e.getMessage()));
        }
    }

    // Performance analysis endpoints

    @GetMapping("/analysis/top-performing")
    public ResponseEntity<?> getTopPerformingFertilizerUsages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findTopPerformingFertilizerUsages(page, size);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get top performing fertilizers: " + e.getMessage()));
        }
    }

    @GetMapping("/analysis/cost-effective")
    public ResponseEntity<?> getMostCostEffectiveFertilizerUsages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<FertilizerUsage> fertilizerUsages = fertilizerUsageService
                    .findMostCostEffectiveFertilizerUsages(page, size);
            return ResponseEntity.ok(fertilizerUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get most cost effective fertilizers: " + e.getMessage()));
        }
    }

    @GetMapping("/analysis/optimization-candidates")
    public ResponseEntity<?> getOptimizationCandidates() {
        try {
            List<FertilizerUsage> candidates = fertilizerUsageService.findOptimizationCandidates();
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get optimization candidates: " + e.getMessage()));
        }
    }

    @GetMapping("/organic")
    public ResponseEntity<?> getOrganicFertilizerUsages() {
        try {
            List<FertilizerUsage> organicUsages = fertilizerUsageService.findOrganicFertilizerUsages();
            return ResponseEntity.ok(organicUsages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get organic fertilizer usages: " + e.getMessage()));
        }
    }

    // Lookup endpoints

    @GetMapping("/lookup/fertilizer-names")
    public ResponseEntity<?> getAllDistinctFertilizerNames() {
        try {
            List<String> names = fertilizerUsageService.getAllDistinctFertilizerNames();
            return ResponseEntity.ok(Map.of("fertilizerNames", names));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get fertilizer names: " + e.getMessage()));
        }
    }

    @GetMapping("/lookup/brands")
    public ResponseEntity<?> getAllDistinctBrands() {
        try {
            List<String> brands = fertilizerUsageService.getAllDistinctBrands();
            return ResponseEntity.ok(Map.of("brands", brands));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get brands: " + e.getMessage()));
        }
    }

    @GetMapping("/lookup/suppliers")
    public ResponseEntity<?> getAllDistinctSuppliers() {
        try {
            List<String> suppliers = fertilizerUsageService.getAllDistinctSuppliers();
            return ResponseEntity.ok(Map.of("suppliers", suppliers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get suppliers: " + e.getMessage()));
        }
    }

    @GetMapping("/lookup/application-stages")
    public ResponseEntity<?> getAllDistinctApplicationStages() {
        try {
            List<String> stages = fertilizerUsageService.getAllDistinctApplicationStages();
            return ResponseEntity.ok(Map.of("applicationStages", stages));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get application stages: " + e.getMessage()));
        }
    }

    @GetMapping("/lookup/operators")
    public ResponseEntity<?> getAllDistinctOperatorNames() {
        try {
            List<String> operators = fertilizerUsageService.getAllDistinctOperatorNames();
            return ResponseEntity.ok(Map.of("operators", operators));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get operator names: " + e.getMessage()));
        }
    }

    // Reporting endpoints

    @GetMapping("/reports/monthly-statistics")
    public ResponseEntity<?> getMonthlyUsageStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            List<Object[]> statistics = fertilizerUsageService.getMonthlyUsageStatistics(startDate, endDate);
            return ResponseEntity.ok(Map.of("monthlyStatistics", statistics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get monthly statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/reports/performance-by-type")
    public ResponseEntity<?> getFertilizerPerformanceByType() {
        try {
            List<Object[]> performance = fertilizerUsageService.getFertilizerPerformanceByType();
            return ResponseEntity.ok(Map.of("performanceByType", performance));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get performance by type: " + e.getMessage()));
        }
    }

    @GetMapping("/reports/usage-summary/crop-production/{cropProductionId}")
    public ResponseEntity<?> getFertilizerUsageSummary(@PathVariable String cropProductionId) {
        try {
            Map<String, Object> summary = fertilizerUsageService.getFertilizerUsageSummary(cropProductionId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get usage summary: " + e.getMessage()));
        }
    }

    @GetMapping("/reports/effectiveness-analysis")
    public ResponseEntity<?> getFertilizerEffectivenessAnalysis() {
        try {
            Map<String, Object> analysis = fertilizerUsageService.getFertilizerEffectivenessAnalysis();
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get effectiveness analysis: " + e.getMessage()));
        }
    }

    // Maintenance endpoints

    @DeleteMapping("/maintenance/expired")
    public ResponseEntity<?> deleteExpiredFertilizerUsages() {
        try {
            fertilizerUsageService.deleteExpiredFertilizerUsages();
            return ResponseEntity.ok(Map.of("message", "Expired fertilizer usages deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete expired fertilizer usages: " + e.getMessage()));
        }
    }

    @DeleteMapping("/maintenance/before-date/{date}")
    public ResponseEntity<?> deleteFertilizerUsagesBeforeDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            fertilizerUsageService.deleteFertilizerUsagesBeforeDate(date);
            return ResponseEntity.ok(Map.of("message", "Fertilizer usages before " + date + " deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete fertilizer usages: " + e.getMessage()));
        }
    }

    @DeleteMapping("/maintenance/old-records")
    public ResponseEntity<?> deleteOldFertilizerUsageRecords(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime beforeDate) {
        try {
            fertilizerUsageService.deleteOldFertilizerUsageRecords(beforeDate);
            return ResponseEntity.ok(Map.of("message", "Old fertilizer usage records deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete old records: " + e.getMessage()));
        }
    }

    // Utility endpoints

    @GetMapping("/exists/{id}")
    public ResponseEntity<?> checkIfFertilizerUsageExists(@PathVariable String id) {
        try {
            boolean exists = fertilizerUsageService.existsById(id);
            return ResponseEntity.ok(Map.of("exists", exists, "id", id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to check existence: " + e.getMessage()));
        }
    }

    @PostMapping("/calculate-cost")
    public ResponseEntity<?> calculateTotalCost(@RequestBody FertilizerUsage fertilizerUsage) {
        try {
            FertilizerUsage calculated = fertilizerUsageService.calculateTotalCostForFertilizerUsage(fertilizerUsage);
            return ResponseEntity.ok(calculated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to calculate total cost: " + e.getMessage()));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateFertilizerUsageForCrop(@RequestBody FertilizerUsage fertilizerUsage) {
        try {
            fertilizerUsageService.validateFertilizerUsageForCrop(fertilizerUsage.getCropProductionId(), fertilizerUsage);
            return ResponseEntity.ok(Map.of("valid", true, "message", "Fertilizer usage is valid"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to validate fertilizer usage: " + e.getMessage()));
        }
    }

    // Enum endpoints for frontend

    @GetMapping("/enums/fertilizer-types")
    public ResponseEntity<?> getFertilizerTypes() {
        try {
            return ResponseEntity.ok(Map.of("fertilizerTypes", FertilizerType.values()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get fertilizer types: " + e.getMessage()));
        }
    }

    @GetMapping("/enums/units")
    public ResponseEntity<?> getUnits() {
        try {
            return ResponseEntity.ok(Map.of("units", Unit.values()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get units: " + e.getMessage()));
        }
    }

    @GetMapping("/enums/application-methods")
    public ResponseEntity<?> getApplicationMethods() {
        try {
            return ResponseEntity.ok(Map.of("applicationMethods", ApplicationMethod.values()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get application methods: " + e.getMessage()));
        }
    }

    // Health check endpoint

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            Long totalCount = fertilizerUsageService.countTotalFertilizerUsages();
            return ResponseEntity.ok(Map.of(
                    "status", "healthy",
                    "service", "FertilizerUsageService",
                    "totalRecords", totalCount,
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "status", "unhealthy",
                            "error", e.getMessage(),
                            "timestamp", LocalDateTime.now()
                    ));
        }
    }
}