package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.SoilData;
import SmartAgricultural.Management.Model.SoilData.NutrientLevel;
import SmartAgricultural.Management.Service.SoilDataService;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.exception.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/soil-data")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SoilDataController {

    @Autowired
    private SoilDataService soilDataService;

    // ===== BASIC CRUD OPERATIONS =====


    @PostMapping
    public ResponseEntity<?> createSoilData(@Valid @RequestBody SoilData soilData) {
        try {
            // Add detailed logging
            System.out.println("Received soil data: " + soilData);
            System.out.println("Farm ID: " + soilData.getFarmId());
            System.out.println("pH Level: " + soilData.getPhLevel());
            System.out.println("Measurement Date: " + soilData.getMeasurementDate());

            SoilData created = soilDataService.create(soilData);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (ValidationException e) {
            System.err.println("Validation error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Validation error", "message", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error creating soil data: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error", "message", e.getMessage()));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("error", "Validation failed");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        errors.put("fieldErrors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SoilData> getSoilDataById(@PathVariable String id) {
        try {
            SoilData soilData = soilDataService.findById(id);
            return ResponseEntity.ok(soilData);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/sample-code/{sampleCode}")
    public ResponseEntity<SoilData> getSoilDataBySampleCode(@PathVariable String sampleCode) {
        try {
            SoilData soilData = soilDataService.findBySampleCode(sampleCode);
            return ResponseEntity.ok(soilData);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SoilData> updateSoilData(
            @PathVariable String id,
            @Valid @RequestBody SoilData soilData) {
        try {
            SoilData updated = soilDataService.update(id, soilData);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSoilData(@PathVariable String id) {
        try {
            soilDataService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<Page<SoilData>> getAllSoilData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "measurementDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SoilData> soilData = soilDataService.findAll(pageable);
        return ResponseEntity.ok(soilData);
    }


    // ===== SOIL CONDITION ASSESSMENT ENDPOINTS =====





    @GetMapping("/farm/{farmId}/soil-conditions")
    public ResponseEntity<List<Map<String, Object>>> getAllSoilConditionsByFarm(@PathVariable String farmId) {
        List<SoilData> soilDataList = soilDataService.findByFarmId(farmId);
        List<Map<String, Object>> conditions = soilDataList.stream()
                .map(soilData -> {
                    Map<String, Object> condition = new HashMap<>();
                    condition.put("id", soilData.getId());
                    condition.put("sampleCode", soilData.getSampleCode());
                    condition.put("soilCondition", soilData.evaluateSoilCondition().getDisplayName());
                    condition.put("phLevel", soilData.getPhLevel());
                    condition.put("assessmentDate", LocalDateTime.now());
                    return condition;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(conditions);
    }

    @GetMapping("/farm/{farmId}/soil-condition-summary")
    public ResponseEntity<Map<String, Object>> getSoilConditionSummary(@PathVariable String farmId) {
        List<SoilData> soilDataList = soilDataService.findByFarmId(farmId);

        Map<String, Long> conditionCounts = soilDataList.stream()
                .collect(Collectors.groupingBy(
                        soilData -> soilData.evaluateSoilCondition().getDisplayName(),
                        Collectors.counting()
                ));

        long totalSamples = soilDataList.size();
        long goodCount = conditionCounts.getOrDefault("Good", 0L);
        long moderateCount = conditionCounts.getOrDefault("Moderate", 0L);
        long badCount = conditionCounts.getOrDefault("Bad", 0L);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalSamples", totalSamples);
        summary.put("good", goodCount);
        summary.put("moderate", moderateCount);
        summary.put("bad", badCount);
        summary.put("goodPercentage", totalSamples > 0 ? (goodCount * 100.0) / totalSamples : 0);
        summary.put("moderatePercentage", totalSamples > 0 ? (moderateCount * 100.0) / totalSamples : 0);
        summary.put("badPercentage", totalSamples > 0 ? (badCount * 100.0) / totalSamples : 0);
        summary.put("overallCondition", getOverallCondition(goodCount, moderateCount, badCount, totalSamples));

        return ResponseEntity.ok(summary);
    }

    private String getOverallCondition(long good, long moderate, long bad, long total) {
        if (total == 0) return "No Data";

        double goodPercentage = (good * 100.0) / total;
        double badPercentage = (bad * 100.0) / total;

        if (goodPercentage >= 70) {
            return "GOOD";
        } else if (badPercentage >= 40) {
            return "BAD";
        } else {
            return "MODERATE";
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalSoilDataCount() {
        long count = soilDataService.count();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Map<String, Boolean>> checkSoilDataExists(@PathVariable String id) {
        boolean exists = soilDataService.existsById(id);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/exists/sample-code/{sampleCode}")
    public ResponseEntity<Map<String, Boolean>> checkSampleCodeExists(@PathVariable String sampleCode) {
        boolean exists = soilDataService.existsBySampleCode(sampleCode);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // ===== FARM-SPECIFIC OPERATIONS =====

    @GetMapping("/farm/{farmId}")
    public ResponseEntity<Page<SoilData>> getSoilDataByFarm(
            @PathVariable String farmId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "measurementDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SoilData> soilData = soilDataService.findByFarmId(farmId, pageable);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/all")
    public ResponseEntity<List<SoilData>> getAllSoilDataByFarm(@PathVariable String farmId) {
        List<SoilData> soilData = soilDataService.findByFarmId(farmId);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/count")
    public ResponseEntity<Long> getSoilDataCountByFarm(@PathVariable String farmId) {
        long count = soilDataService.countByFarmId(farmId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/farm/{farmId}/latest")
    public ResponseEntity<SoilData> getLatestSoilDataByFarm(@PathVariable String farmId) {
        Optional<SoilData> soilData = soilDataService.findLatestByFarmId(farmId);
        return soilData.map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/farm/{farmId}/latest/depth/{depthCm}")
    public ResponseEntity<SoilData> getLatestSoilDataByFarmAndDepth(
            @PathVariable String farmId,
            @PathVariable Integer depthCm) {
        Optional<SoilData> soilData = soilDataService.findLatestByFarmIdAndDepth(farmId, depthCm);
        return soilData.map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/farm/{farmId}/ordered")
    public ResponseEntity<List<SoilData>> getSoilDataByFarmOrdered(@PathVariable String farmId) {
        List<SoilData> soilData = soilDataService.findByFarmIdOrderByMeasurementDateDesc(farmId);
        return ResponseEntity.ok(soilData);
    }

    // ===== DATE-BASED OPERATIONS =====

    @GetMapping("/date-range")
    public ResponseEntity<List<SoilData>> getSoilDataByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<SoilData> soilData = soilDataService.findByMeasurementDateBetween(startDate, endDate);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/date-range")
    public ResponseEntity<List<SoilData>> getSoilDataByFarmAndDateRange(
            @PathVariable String farmId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<SoilData> soilData = soilDataService.findByFarmIdAndMeasurementDateBetween(farmId, startDate, endDate);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/recent")
    public ResponseEntity<List<SoilData>> getRecentSoilDataByFarm(
            @PathVariable String farmId,
            @RequestParam(defaultValue = "30") int days) {
        List<SoilData> soilData = soilDataService.findRecentByFarmId(farmId, days);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/recent/paged")
    public ResponseEntity<Page<SoilData>> getRecentSoilDataByFarmPaged(
            @PathVariable String farmId,
            @RequestParam(defaultValue = "30") int daysBack,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SoilData> soilData = soilDataService.findRecentSoilDataPaged(farmId, page, size, daysBack);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/trend/depth/{depthCm}")
    public ResponseEntity<List<SoilData>> getTrendDataByFarmAndDepth(
            @PathVariable String farmId,
            @PathVariable Integer depthCm) {
        List<SoilData> soilData = soilDataService.findTrendDataByFarmIdAndDepth(farmId, depthCm);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/trend/date-range")
    public ResponseEntity<List<SoilData>> getTrendDataByFarmAndDateRange(
            @PathVariable String farmId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<SoilData> soilData = soilDataService.findTrendDataByFarmIdAndDateRange(farmId, startDate, endDate);
        return ResponseEntity.ok(soilData);
    }

    // ===== TEST DUE OPERATIONS =====

    @GetMapping("/tests-due")
    public ResponseEntity<List<SoilData>> getTestsDue() {
        List<SoilData> soilData = soilDataService.findTestsDue();
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/tests-due")
    public ResponseEntity<List<SoilData>> getTestsDueByFarm(@PathVariable String farmId) {
        List<SoilData> soilData = soilDataService.findTestsDueByFarmId(farmId);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/tests-overdue")
    public ResponseEntity<List<SoilData>> getOverdueTests() {
        List<SoilData> soilData = soilDataService.findOverdueTests();
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/tests-overdue")
    public ResponseEntity<List<SoilData>> getOverdueTestsByFarm(@PathVariable String farmId) {
        List<SoilData> soilData = soilDataService.findOverdueTestsByFarmId(farmId);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/tests-upcoming")
    public ResponseEntity<List<SoilData>> getUpcomingTests(
            @RequestParam(defaultValue = "30") int daysAhead) {
        List<SoilData> soilData = soilDataService.findUpcomingTests(daysAhead);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/tests-due/count")
    public ResponseEntity<Long> getTestsDueCountByFarm(@PathVariable String farmId) {
        long count = soilDataService.countTestsDueByFarmId(farmId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/farm/{farmId}/tests-overdue/count")
    public ResponseEntity<Long> getOverdueTestsCountByFarm(@PathVariable String farmId) {
        long count = soilDataService.countOverdueTestsByFarmId(farmId);
        return ResponseEntity.ok(count);
    }

    // ===== PH LEVEL OPERATIONS =====

    @GetMapping("/ph-range")
    public ResponseEntity<List<SoilData>> getSoilDataByPhRange(
            @RequestParam BigDecimal minPh,
            @RequestParam BigDecimal maxPh) {
        List<SoilData> soilData = soilDataService.findByPhLevelBetween(minPh, maxPh);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/acidic")
    public ResponseEntity<List<SoilData>> getAcidicSoils(
            @RequestParam(required = false) BigDecimal acidPh) {
        List<SoilData> soilData = soilDataService.findAcidicSoils(acidPh);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/alkaline")
    public ResponseEntity<List<SoilData>> getAlkalineSoils(
            @RequestParam(required = false) BigDecimal alkalinePh) {
        List<SoilData> soilData = soilDataService.findAlkalineSoils(alkalinePh);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/optimal-ph")
    public ResponseEntity<List<SoilData>> getOptimalPhSoilsByFarm(@PathVariable String farmId) {
        List<SoilData> soilData = soilDataService.findOptimalPhSoilsByFarmId(farmId);
        return ResponseEntity.ok(soilData);
    }

    // ===== NUTRIENT LEVEL OPERATIONS =====

    @GetMapping("/low-nutrients")
    public ResponseEntity<List<SoilData>> getLowNutrientSoils() {
        List<SoilData> soilData = soilDataService.findLowNutrientSoils();
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/low-nutrients")
    public ResponseEntity<List<SoilData>> getLowNutrientSoilsByFarm(@PathVariable String farmId) {
        List<SoilData> soilData = soilDataService.findLowNutrientSoilsByFarmId(farmId);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/low-nutrients/count")
    public ResponseEntity<Long> getLowNutrientSoilsCountByFarm(@PathVariable String farmId) {
        long count = soilDataService.countLowNutrientSoilsByFarmId(farmId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/farm/{farmId}/nitrogen-level/{level}")
    public ResponseEntity<List<SoilData>> getSoilDataByNitrogenLevel(
            @PathVariable String farmId,
            @PathVariable NutrientLevel level) {
        List<SoilData> soilData = soilDataService.findByNitrogenLevel(level, farmId);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/phosphorus-level/{level}")
    public ResponseEntity<List<SoilData>> getSoilDataByPhosphorusLevel(
            @PathVariable String farmId,
            @PathVariable NutrientLevel level) {
        List<SoilData> soilData = soilDataService.findByPhosphorusLevel(level, farmId);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/potassium-level/{level}")
    public ResponseEntity<List<SoilData>> getSoilDataByPotassiumLevel(
            @PathVariable String farmId,
            @PathVariable NutrientLevel level) {
        List<SoilData> soilData = soilDataService.findByPotassiumLevel(level, farmId);
        return ResponseEntity.ok(soilData);
    }

    // ===== ORGANIC MATTER OPERATIONS =====

    @GetMapping("/low-organic-matter")
    public ResponseEntity<List<SoilData>> getLowOrganicMatterSoils() {
        List<SoilData> soilData = soilDataService.findLowOrganicMatterSoils();
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/low-organic-matter")
    public ResponseEntity<List<SoilData>> getLowOrganicMatterSoilsByFarm(@PathVariable String farmId) {
        List<SoilData> soilData = soilDataService.findLowOrganicMatterSoilsByFarmId(farmId);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/low-organic-matter/count")
    public ResponseEntity<Long> getLowOrganicMatterSoilsCountByFarm(@PathVariable String farmId) {
        long count = soilDataService.countLowOrganicMatterSoilsByFarmId(farmId);
        return ResponseEntity.ok(count);
    }

    // ===== MOISTURE OPERATIONS =====

    @GetMapping("/moisture-range")
    public ResponseEntity<List<SoilData>> getSoilDataByMoistureRange(
            @RequestParam BigDecimal minMoisture,
            @RequestParam BigDecimal maxMoisture) {
        List<SoilData> soilData = soilDataService.findByMoistureBetween(minMoisture, maxMoisture);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/dry-soils")
    public ResponseEntity<List<SoilData>> getDrySoils(
            @RequestParam(required = false) BigDecimal maxMoisture) {
        List<SoilData> soilData = soilDataService.findDrySoils(maxMoisture);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/wet-soils")
    public ResponseEntity<List<SoilData>> getWetSoils(
            @RequestParam(required = false) BigDecimal minMoisture) {
        List<SoilData> soilData = soilDataService.findWetSoils(minMoisture);
        return ResponseEntity.ok(soilData);
    }

    // ===== SOIL TEXTURE OPERATIONS =====

    @GetMapping("/texture/{soilTexture}")
    public ResponseEntity<List<SoilData>> getSoilDataByTexture(@PathVariable String soilTexture) {
        List<SoilData> soilData = soilDataService.findBySoilTexture(soilTexture);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/texture/{soilTexture}")
    public ResponseEntity<List<SoilData>> getSoilDataByFarmAndTexture(
            @PathVariable String farmId,
            @PathVariable String soilTexture) {
        List<SoilData> soilData = soilDataService.findByFarmIdAndSoilTexture(farmId, soilTexture);
        return ResponseEntity.ok(soilData);
    }

    @PostMapping("/texture-types")
    public ResponseEntity<List<SoilData>> getSoilDataByTextureTypes(@RequestBody List<String> textureTypes) {
        List<SoilData> soilData = soilDataService.findBySoilTextureTypes(textureTypes);
        return ResponseEntity.ok(soilData);
    }

    // ===== PHYSICAL PROPERTIES OPERATIONS =====

    @GetMapping("/poor-drainage")
    public ResponseEntity<List<SoilData>> getPoorDrainageSoils() {
        List<SoilData> soilData = soilDataService.findPoorDrainageSoils();
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/compacted")
    public ResponseEntity<List<SoilData>> getCompactedSoils(
            @RequestParam(required = false) BigDecimal maxDensity) {
        List<SoilData> soilData = soilDataService.findCompactedSoils(maxDensity);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/low-porosity")
    public ResponseEntity<List<SoilData>> getLowPorositySoils(
            @RequestParam(required = false) BigDecimal minPorosity) {
        List<SoilData> soilData = soilDataService.findLowPorositySoils(minPorosity);
        return ResponseEntity.ok(soilData);
    }

    // ===== SALINITY OPERATIONS =====

    @GetMapping("/saline")
    public ResponseEntity<List<SoilData>> getSalineSoils() {
        List<SoilData> soilData = soilDataService.findSalineSoils();
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/saline")
    public ResponseEntity<List<SoilData>> getSalineSoilsByFarm(@PathVariable String farmId) {
        List<SoilData> soilData = soilDataService.findSalineSoilsByFarmId(farmId);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/saline/count")
    public ResponseEntity<Long> getSalineSoilsCountByFarm(@PathVariable String farmId) {
        long count = soilDataService.countSalineSoilsByFarmId(farmId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/salinity-level")
    public ResponseEntity<List<SoilData>> getSoilDataBySalinityLevel(
            @RequestParam(required = false) BigDecimal ecThreshold) {
        List<SoilData> soilData = soilDataService.findBySalinityLevel(ecThreshold);
        return ResponseEntity.ok(soilData);
    }

    // ===== DEPTH OPERATIONS =====

    @GetMapping("/depth/{depthCm}")
    public ResponseEntity<List<SoilData>> getSoilDataByDepth(@PathVariable Integer depthCm) {
        List<SoilData> soilData = soilDataService.findByDepthCm(depthCm);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/depth/{depthCm}")
    public ResponseEntity<List<SoilData>> getSoilDataByFarmAndDepth(
            @PathVariable String farmId,
            @PathVariable Integer depthCm) {
        List<SoilData> soilData = soilDataService.findByFarmIdAndDepthCm(farmId, depthCm);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/depth-range")
    public ResponseEntity<List<SoilData>> getSoilDataByDepthRange(
            @RequestParam Integer minDepth,
            @RequestParam Integer maxDepth) {
        List<SoilData> soilData = soilDataService.findByDepthRange(minDepth, maxDepth);
        return ResponseEntity.ok(soilData);
    }

    // ===== LABORATORY OPERATIONS =====

    @GetMapping("/laboratory/{laboratoryName}")
    public ResponseEntity<List<SoilData>> getSoilDataByLaboratory(@PathVariable String laboratoryName) {
        List<SoilData> soilData = soilDataService.findByLaboratoryName(laboratoryName);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/testing-method/{testingMethod}")
    public ResponseEntity<List<SoilData>> getSoilDataByTestingMethod(@PathVariable String testingMethod) {
        List<SoilData> soilData = soilDataService.findByTestingMethod(testingMethod);
        return ResponseEntity.ok(soilData);
    }

    // ===== QUALITY ASSESSMENT OPERATIONS =====

    @GetMapping("/farm/{farmId}/healthy-soils")
    public ResponseEntity<List<SoilData>> getHealthySoilsByFarm(@PathVariable String farmId) {
        List<SoilData> soilData = soilDataService.findHealthySoilsByFarmId(farmId);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/problematic-soils")
    public ResponseEntity<List<SoilData>> getProblematicSoilsByFarm(@PathVariable String farmId) {
        List<SoilData> soilData = soilDataService.findProblematicSoilsByFarmId(farmId);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/excellent-quality")
    public ResponseEntity<List<SoilData>> getExcellentQualitySoilsByFarm(@PathVariable String farmId) {
        List<SoilData> soilData = soilDataService.findExcellentQualitySoilsByFarmId(farmId);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/poor-quality")
    public ResponseEntity<List<SoilData>> getPoorQualitySoilsByFarm(@PathVariable String farmId) {
        List<SoilData> soilData = soilDataService.findPoorQualitySoilsByFarmId(farmId);
        return ResponseEntity.ok(soilData);
    }

    // ===== STATISTICS OPERATIONS =====

    @GetMapping("/farm/{farmId}/stats/average-ph")
    public ResponseEntity<BigDecimal> getAveragePhLevelByFarm(@PathVariable String farmId) {
        BigDecimal avgPh = soilDataService.getAveragePhLevelByFarmId(farmId);
        return ResponseEntity.ok(avgPh);
    }

    @GetMapping("/farm/{farmId}/stats/average-nitrogen")
    public ResponseEntity<BigDecimal> getAverageNitrogenByFarm(@PathVariable String farmId) {
        BigDecimal avgNitrogen = soilDataService.getAverageNitrogenByFarmId(farmId);
        return ResponseEntity.ok(avgNitrogen);
    }

    @GetMapping("/farm/{farmId}/stats/average-phosphorus")
    public ResponseEntity<BigDecimal> getAveragePhosphorusByFarm(@PathVariable String farmId) {
        BigDecimal avgPhosphorus = soilDataService.getAveragePhosphorusByFarmId(farmId);
        return ResponseEntity.ok(avgPhosphorus);
    }

    @GetMapping("/farm/{farmId}/stats/average-potassium")
    public ResponseEntity<BigDecimal> getAveragePotassiumByFarm(@PathVariable String farmId) {
        BigDecimal avgPotassium = soilDataService.getAveragePotassiumByFarmId(farmId);
        return ResponseEntity.ok(avgPotassium);
    }

    @GetMapping("/farm/{farmId}/stats/average-organic-matter")
    public ResponseEntity<BigDecimal> getAverageOrganicMatterByFarm(@PathVariable String farmId) {
        BigDecimal avgOrganic = soilDataService.getAverageOrganicMatterByFarmId(farmId);
        return ResponseEntity.ok(avgOrganic);
    }

    @GetMapping("/farm/{farmId}/stats/ph-range")
    public ResponseEntity<Map<String, Object>> getPhLevelRangeByFarm(@PathVariable String farmId) {
        Map<String, Object> range = soilDataService.getPhLevelRangeByFarmId(farmId);
        return ResponseEntity.ok(range);
    }

    @GetMapping("/farm/{farmId}/stats/soil-texture")
    public ResponseEntity<Map<String, Long>> getSoilTextureStatistics(@PathVariable String farmId) {
        Map<String, Long> stats = soilDataService.getSoilTextureStatistics(farmId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/farm/{farmId}/stats/yearly-tests")
    public ResponseEntity<Map<Integer, Long>> getYearlyTestStatistics(@PathVariable String farmId) {
        Map<Integer, Long> stats = soilDataService.getYearlyTestStatistics(farmId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/farm/{farmId}/stats/laboratory")
    public ResponseEntity<Map<String, Long>> getLaboratoryStatistics(@PathVariable String farmId) {
        Map<String, Long> stats = soilDataService.getLaboratoryStatistics(farmId);
        return ResponseEntity.ok(stats);
    }

    // ===== SEARCH OPERATIONS =====

    @GetMapping("/farm/{farmId}/search")
    public ResponseEntity<Page<SoilData>> searchSoilData(
            @PathVariable String farmId,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "measurementDate"));
        Page<SoilData> soilData = soilDataService.searchByFarmIdAndTerm(farmId, searchTerm, pageable);
        return ResponseEntity.ok(soilData);
    }

    @GetMapping("/farm/{farmId}/search/all")
    public ResponseEntity<List<SoilData>> searchAllSoilData(
            @PathVariable String farmId,
            @RequestParam(required = false) String searchTerm) {
        List<SoilData> soilData = soilDataService.searchByFarmIdAndTerm(farmId, searchTerm);
        return ResponseEntity.ok(soilData);
    }

    // ===== COMPREHENSIVE ANALYSIS =====

    @GetMapping("/farm/{farmId}/analysis")
    public ResponseEntity<Map<String, Object>> getFarmSoilAnalysis(@PathVariable String farmId) {
        Map<String, Object> analysis = soilDataService.getFarmSoilAnalysis(farmId);
        return ResponseEntity.ok(analysis);
    }

    @GetMapping("/farm/{farmId}/health-score")
    public ResponseEntity<Map<String, Object>> getSoilHealthScore(@PathVariable String farmId) {
        Map<String, Object> healthScore = soilDataService.getSoilHealthScore(farmId);
        return ResponseEntity.ok(healthScore);
    }

    // ===== BULK OPERATIONS =====

    @PostMapping("/bulk")
    public ResponseEntity<List<SoilData>> createSoilDataBatch(
            @Valid @RequestBody List<SoilData> soilDataList) {
        try {
            List<SoilData> created = soilDataService.createSoilDataBatch(soilDataList);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (ValidationException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/bulk")
    public ResponseEntity<List<SoilData>> updateSoilDataBatch(
            @Valid @RequestBody List<SoilData> soilDataList) {
        try {
            List<SoilData> updated = soilDataService.updateSoilDataBatch(soilDataList);
            return ResponseEntity.ok(updated);
        } catch (ValidationException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteSoilDataBatch(@RequestBody List<String> ids) {
        try {
            soilDataService.deleteSoilDataBatch(ids);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ===== MAINTENANCE OPERATIONS =====

    @DeleteMapping("/maintenance/cleanup")
    public ResponseEntity<Map<String, Integer>> deleteOldRecords(
            @RequestParam(defaultValue = "365") int daysOld) {
        int count = soilDataService.deleteOldRecords(daysOld);
        return ResponseEntity.ok(Map.of("deletedCount", count));
    }

    @PostMapping("/maintenance/update-test-dates")
    public ResponseEntity<Map<String, Object>> updateNextTestDueDates() {
        List<SoilData> updated = soilDataService.updateNextTestDueDates();
        return ResponseEntity.ok(Map.of("updatedCount", updated.size(), "updatedRecords", updated));
    }

    // ===== UTILITY ENDPOINTS =====

    @GetMapping("/farm/{farmId}/paged")
    public ResponseEntity<Page<SoilData>> getSoilDataByFarmPaged(
            @PathVariable String farmId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SoilData> soilData = soilDataService.findByFarmIdPaged(farmId, page, size);
        return ResponseEntity.ok(soilData);
    }

    // ===== DASHBOARD ENDPOINTS =====

    @GetMapping("/farm/{farmId}/dashboard")
    public ResponseEntity<Map<String, Object>> getFarmSoilDashboard(@PathVariable String farmId) {
        Map<String, Object> dashboard = new HashMap<>();

        // Basic counts
        dashboard.put("totalSamples", soilDataService.countByFarmId(farmId));
        dashboard.put("testsDue", soilDataService.countTestsDueByFarmId(farmId));
        dashboard.put("overdueTests", soilDataService.countOverdueTestsByFarmId(farmId));

        // Latest sample
        Optional<SoilData> latest = soilDataService.findLatestByFarmId(farmId);
        dashboard.put("latestSample", latest.orElse(null));

        // Health indicators
        dashboard.put("lowNutrientCount", soilDataService.countLowNutrientSoilsByFarmId(farmId));
        dashboard.put("lowOrganicMatterCount", soilDataService.countLowOrganicMatterSoilsByFarmId(farmId));
        dashboard.put("salineCount", soilDataService.countSalineSoilsByFarmId(farmId));

        // Recent tests
        dashboard.put("recentTests", soilDataService.findRecentByFarmId(farmId, 30));

        // Health score
        dashboard.put("healthScore", soilDataService.getSoilHealthScore(farmId));

        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/farm/{farmId}/summary")
    public ResponseEntity<Map<String, Object>> getFarmSoilSummary(@PathVariable String farmId) {
        Map<String, Object> summary = new HashMap<>();

        // Basic info
        summary.put("totalSamples", soilDataService.countByFarmId(farmId));

        // Averages
        summary.put("averagePh", soilDataService.getAveragePhLevelByFarmId(farmId));
        summary.put("averageNitrogen", soilDataService.getAverageNitrogenByFarmId(farmId));
        summary.put("averagePhosphorus", soilDataService.getAveragePhosphorusByFarmId(farmId));
        summary.put("averagePotassium", soilDataService.getAveragePotassiumByFarmId(farmId));
        summary.put("averageOrganicMatter", soilDataService.getAverageOrganicMatterByFarmId(farmId));

        // Quality counts
        summary.put("excellentQuality", soilDataService.findExcellentQualitySoilsByFarmId(farmId).size());
        summary.put("poorQuality", soilDataService.findPoorQualitySoilsByFarmId(farmId).size());
        summary.put("problematicSoils", soilDataService.findProblematicSoilsByFarmId(farmId).size());

        // Test scheduling
        summary.put("testsDue", soilDataService.countTestsDueByFarmId(farmId));
        summary.put("overdueTests", soilDataService.countOverdueTestsByFarmId(farmId));

        // Statistics
        summary.put("soilTextureDistribution", soilDataService.getSoilTextureStatistics(farmId));
        summary.put("testHistory", soilDataService.getYearlyTestStatistics(farmId));

        return ResponseEntity.ok(summary);
    }

    // ===== REPORTS ENDPOINTS =====

    @GetMapping("/farm/{farmId}/report/nutrient-status")
    public ResponseEntity<Map<String, Object>> getNutrientStatusReport(@PathVariable String farmId) {
        Map<String, Object> report = new HashMap<>();

        List<SoilData> allSoils = soilDataService.findByFarmId(farmId);

        // Nutrient level counts
        Map<String, Integer> nitrogenLevels = new HashMap<>();
        Map<String, Integer> phosphorusLevels = new HashMap<>();
        Map<String, Integer> potassiumLevels = new HashMap<>();

        for (SoilData soil : allSoils) {
            if (soil.getNitrogenLevel() != null) {
                nitrogenLevels.merge(soil.getNitrogenLevel().getDisplayName(), 1, Integer::sum);
            }
            if (soil.getPhosphorusLevel() != null) {
                phosphorusLevels.merge(soil.getPhosphorusLevel().getDisplayName(), 1, Integer::sum);
            }
            if (soil.getPotassiumLevel() != null) {
                potassiumLevels.merge(soil.getPotassiumLevel().getDisplayName(), 1, Integer::sum);
            }
        }

        report.put("nitrogenDistribution", nitrogenLevels);
        report.put("phosphorusDistribution", phosphorusLevels);
        report.put("potassiumDistribution", potassiumLevels);
        report.put("lowNutrientSoils", soilDataService.findLowNutrientSoilsByFarmId(farmId));
        report.put("averages", Map.of(
                "nitrogen", soilDataService.getAverageNitrogenByFarmId(farmId),
                "phosphorus", soilDataService.getAveragePhosphorusByFarmId(farmId),
                "potassium", soilDataService.getAveragePotassiumByFarmId(farmId)
        ));

        return ResponseEntity.ok(report);
    }

    @GetMapping("/farm/{farmId}/report/ph-analysis")
    public ResponseEntity<Map<String, Object>> getPhAnalysisReport(@PathVariable String farmId) {
        Map<String, Object> report = new HashMap<>();

        List<SoilData> allSoils = soilDataService.findByFarmId(farmId);

        // pH level distribution
        Map<String, Integer> phDistribution = new HashMap<>();
        for (SoilData soil : allSoils) {
            if (soil.getPhLevelCategory() != null) {
                phDistribution.merge(soil.getPhLevelCategory().getDisplayName(), 1, Integer::sum);
            }
        }

        report.put("phDistribution", phDistribution);
        report.put("averagePh", soilDataService.getAveragePhLevelByFarmId(farmId));
        report.put("phRange", soilDataService.getPhLevelRangeByFarmId(farmId));
        report.put("optimalPhSoils", soilDataService.findOptimalPhSoilsByFarmId(farmId));
        report.put("acidicSoils", soilDataService.findAcidicSoils(new BigDecimal("6.0")).stream()
                .filter(s -> s.getFarmId().equals(farmId)).toList());
        report.put("alkalineSoils", soilDataService.findAlkalineSoils(new BigDecimal("7.5")).stream()
                .filter(s -> s.getFarmId().equals(farmId)).toList());

        return ResponseEntity.ok(report);
    }

    @GetMapping("/farm/{farmId}/report/quality-assessment")
    public ResponseEntity<Map<String, Object>> getQualityAssessmentReport(@PathVariable String farmId) {
        Map<String, Object> report = new HashMap<>();

        List<SoilData> allSoils = soilDataService.findByFarmId(farmId);

        // Quality distribution
        Map<String, Integer> qualityDistribution = new HashMap<>();
        for (SoilData soil : allSoils) {
            String quality = soil.getSoilQualityScore();
            qualityDistribution.merge(quality, 1, Integer::sum);
        }

        report.put("qualityDistribution", qualityDistribution);
        report.put("healthScore", soilDataService.getSoilHealthScore(farmId));
        report.put("excellentSoils", soilDataService.findExcellentQualitySoilsByFarmId(farmId));
        report.put("poorQualitySoils", soilDataService.findPoorQualitySoilsByFarmId(farmId));
        report.put("problematicSoils", soilDataService.findProblematicSoilsByFarmId(farmId));
        report.put("healthySoils", soilDataService.findHealthySoilsByFarmId(farmId));

        // Issues summary
        report.put("issues", Map.of(
                "lowNutrients", soilDataService.countLowNutrientSoilsByFarmId(farmId),
                "lowOrganicMatter", soilDataService.countLowOrganicMatterSoilsByFarmId(farmId),
                "salineSoils", soilDataService.countSalineSoilsByFarmId(farmId)
        ));

        return ResponseEntity.ok(report);
    }

    // ===== EXCEPTION HANDLERS =====

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(
            ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Resource not found", "message", e.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            ValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Validation error", "message", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error", "message", "An unexpected error occurred"));
    }


// ==================== SOIL CONDITION ASSESSMENT ENDPOINTS ====================

    @GetMapping("/{id}/soil-condition")
    public ResponseEntity<?> getSoilConditionAssessment(@PathVariable String id) {
        try {
            SoilData soilData = soilDataService.findById(id);  // ✅ CORRECT
            Map<String, Object> report = soilData.getSoilConditionReport();
            return ResponseEntity.ok(report);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Soil data not found"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/ai-attributes")
    public ResponseEntity<?> getSoilAttributesForAI(@PathVariable String id) {
        try {
            SoilData soilData = soilDataService.findById(id);  // ✅ CORRECT
            Map<String, Object> attributes = soilData.getSoilAttributesForAI();
            return ResponseEntity.ok(attributes);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Soil data not found"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}