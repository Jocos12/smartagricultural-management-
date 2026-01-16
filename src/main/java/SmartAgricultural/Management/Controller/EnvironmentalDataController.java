package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.EnvironmentalData;
import SmartAgricultural.Management.Model.EnvironmentalData.EnvironmentalRiskLevel;
import SmartAgricultural.Management.Model.EnvironmentalData.DataQuality;
import SmartAgricultural.Management.Model.EnvironmentalData.ValidationStatus;
import SmartAgricultural.Management.Service.EnvironmentalDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/environmental-data")
@Validated
@CrossOrigin(origins = "*")
public class EnvironmentalDataController {

    @Autowired
    private EnvironmentalDataService environmentalDataService;

    // CRUD Operations
    @PostMapping
    public ResponseEntity<EnvironmentalData> createEnvironmentalData(@Valid @RequestBody EnvironmentalData environmentalData) {
        try {
            EnvironmentalData savedData = environmentalDataService.save(environmentalData);
            return new ResponseEntity<>(savedData, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<List<EnvironmentalData>> createBatchEnvironmentalData(@Valid @RequestBody List<EnvironmentalData> environmentalDataList) {
        try {
            List<EnvironmentalData> savedDataList = environmentalDataService.saveAll(environmentalDataList);
            return new ResponseEntity<>(savedDataList, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<Page<EnvironmentalData>> getAllEnvironmentalData(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "recordDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<EnvironmentalData> environmentalDataPage = environmentalDataService.findAll(pageable);
            return new ResponseEntity<>(environmentalDataPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnvironmentalData> getEnvironmentalDataById(@PathVariable String id) {
        Optional<EnvironmentalData> environmentalData = environmentalDataService.findById(id);
        if (environmentalData.isPresent()) {
            return new ResponseEntity<>(environmentalData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/monitoring-code/{monitoringCode}")
    public ResponseEntity<EnvironmentalData> getEnvironmentalDataByMonitoringCode(@PathVariable String monitoringCode) {
        Optional<EnvironmentalData> environmentalData = environmentalDataService.findByMonitoringCode(monitoringCode);
        if (environmentalData.isPresent()) {
            return new ResponseEntity<>(environmentalData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnvironmentalData> updateEnvironmentalData(@PathVariable String id,
                                                                     @Valid @RequestBody EnvironmentalData environmentalData) {
        Optional<EnvironmentalData> existingData = environmentalDataService.findById(id);
        if (existingData.isPresent()) {
            environmentalData.setId(id);
            EnvironmentalData updatedData = environmentalDataService.save(environmentalData);
            return new ResponseEntity<>(updatedData, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteEnvironmentalData(@PathVariable String id) {
        try {
            if (environmentalDataService.existsById(id)) {
                environmentalDataService.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Geographic Queries
    @GetMapping("/region/{region}")
    public ResponseEntity<Page<EnvironmentalData>> getEnvironmentalDataByRegion(
            @PathVariable String region,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "recordDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<EnvironmentalData> environmentalDataPage = environmentalDataService.findByRegion(region, pageable);
            return new ResponseEntity<>(environmentalDataPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/region/{region}/district/{district}")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalDataByRegionAndDistrict(
            @PathVariable String region, @PathVariable String district) {
        try {
            List<EnvironmentalData> environmentalDataList = environmentalDataService.findByRegionAndDistrict(region, district);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/region/{region}/district/{district}/sector/{sector}")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalDataByRegionDistrictAndSector(
            @PathVariable String region, @PathVariable String district, @PathVariable String sector) {
        try {
            List<EnvironmentalData> environmentalDataList =
                    environmentalDataService.findByRegionAndDistrictAndSector(region, district, sector);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/location-bounds")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalDataByLocationBounds(
            @RequestParam BigDecimal minLatitude,
            @RequestParam BigDecimal maxLatitude,
            @RequestParam BigDecimal minLongitude,
            @RequestParam BigDecimal maxLongitude) {
        try {
            List<EnvironmentalData> environmentalDataList =
                    environmentalDataService.findByLocationBounds(minLatitude, maxLatitude, minLongitude, maxLongitude);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Risk Assessment Endpoints
    @GetMapping("/risk-level/{riskLevel}")
    public ResponseEntity<Page<EnvironmentalData>> getEnvironmentalDataByRiskLevel(
            @PathVariable EnvironmentalRiskLevel riskLevel,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "recordDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<EnvironmentalData> environmentalDataPage = environmentalDataService.findByRiskLevel(riskLevel, pageable);
            return new ResponseEntity<>(environmentalDataPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/high-risk-areas")
    public ResponseEntity<List<EnvironmentalData>> getHighRiskAreas() {
        try {
            List<EnvironmentalData> highRiskAreas = environmentalDataService.findHighRiskAreas();
            return new ResponseEntity<>(highRiskAreas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/critical-risk-areas")
    public ResponseEntity<List<EnvironmentalData>> getCriticalRiskAreas() {
        try {
            List<EnvironmentalData> criticalRiskAreas = environmentalDataService.findCriticalRiskAreas();
            return new ResponseEntity<>(criticalRiskAreas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/validated-high-risk-areas")
    public ResponseEntity<List<EnvironmentalData>> getValidatedHighRiskAreas() {
        try {
            List<EnvironmentalData> validatedHighRiskAreas = environmentalDataService.findValidatedHighRiskAreas();
            return new ResponseEntity<>(validatedHighRiskAreas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Data Quality Management
    @GetMapping("/data-quality/{dataQuality}")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalDataByDataQuality(@PathVariable DataQuality dataQuality) {
        try {
            List<EnvironmentalData> environmentalDataList = environmentalDataService.findByDataQuality(dataQuality);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/reliable-data")
    public ResponseEntity<List<EnvironmentalData>> getReliableData() {
        try {
            List<EnvironmentalData> reliableData = environmentalDataService.findReliableData();
            return new ResponseEntity<>(reliableData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/incomplete-records")
    public ResponseEntity<List<EnvironmentalData>> getIncompleteRecords() {
        try {
            List<EnvironmentalData> incompleteRecords = environmentalDataService.findIncompleteRecords();
            return new ResponseEntity<>(incompleteRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/data-quality")
    public ResponseEntity<EnvironmentalData> updateDataQuality(@PathVariable String id, @RequestParam DataQuality dataQuality) {
        try {
            EnvironmentalData updatedData = environmentalDataService.updateDataQuality(id, dataQuality);
            return new ResponseEntity<>(updatedData, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Validation Management
    @GetMapping("/validation-status/{status}")
    public ResponseEntity<Page<EnvironmentalData>> getEnvironmentalDataByValidationStatus(
            @PathVariable ValidationStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "recordDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<EnvironmentalData> environmentalDataPage = environmentalDataService.findByValidationStatus(status, pageable);
            return new ResponseEntity<>(environmentalDataPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pending-validation")
    public ResponseEntity<List<EnvironmentalData>> getPendingValidation() {
        try {
            List<EnvironmentalData> pendingValidation = environmentalDataService.findPendingValidation();
            return new ResponseEntity<>(pendingValidation, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/validate")
    public ResponseEntity<EnvironmentalData> validateData(@PathVariable String id,
                                                          @RequestParam String validatorId,
                                                          @RequestParam ValidationStatus status) {
        try {
            EnvironmentalData validatedData = environmentalDataService.validateData(id, validatorId, status);
            return new ResponseEntity<>(validatedData, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Date Range Queries
    @GetMapping("/date-range")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalDataByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<EnvironmentalData> environmentalDataList = environmentalDataService.findByDateRange(startDate, endDate);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/after-date")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalDataAfterDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        try {
            List<EnvironmentalData> environmentalDataList = environmentalDataService.findAfterDate(date);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/before-date")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalDataBeforeDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        try {
            List<EnvironmentalData> environmentalDataList = environmentalDataService.findBeforeDate(date);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/region/{region}/date-range")
    public ResponseEntity<List<EnvironmentalData>> getValidatedDataByRegionAndDateRange(
            @PathVariable String region,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<EnvironmentalData> environmentalDataList =
                    environmentalDataService.findValidatedDataByRegionAndDateRange(region, startDate, endDate);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Environmental Quality Queries
    @GetMapping("/air-quality/above-threshold")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalDataByAirQualityThreshold(@RequestParam Integer threshold) {
        try {
            List<EnvironmentalData> environmentalDataList = environmentalDataService.findByAirQualityThreshold(threshold);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/air-quality/range")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalDataByAirQualityRange(
            @RequestParam Integer min, @RequestParam Integer max) {
        try {
            List<EnvironmentalData> environmentalDataList = environmentalDataService.findByAirQualityRange(min, max);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/water-quality/below-threshold")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalDataByWaterQualityThreshold(@RequestParam Integer threshold) {
        try {
            List<EnvironmentalData> environmentalDataList = environmentalDataService.findByWaterQualityThreshold(threshold);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/water-ph/range")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalDataByWaterPhRange(
            @RequestParam BigDecimal minPh, @RequestParam BigDecimal maxPh) {
        try {
            List<EnvironmentalData> environmentalDataList = environmentalDataService.findByWaterPhRange(minPh, maxPh);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Forest and Biodiversity
    @GetMapping("/forest/low-coverage")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalDataByLowForestCoverage(@RequestParam BigDecimal threshold) {
        try {
            List<EnvironmentalData> environmentalDataList = environmentalDataService.findByLowForestCoverage(threshold);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/deforestation/high-rate")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalDataByHighDeforestationRate(@RequestParam BigDecimal threshold) {
        try {
            List<EnvironmentalData> environmentalDataList = environmentalDataService.findByHighDeforestationRate(threshold);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/endangered-species/high-count")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalDataByHighEndangeredSpeciesCount(@RequestParam Integer threshold) {
        try {
            List<EnvironmentalData> environmentalDataList = environmentalDataService.findByHighEndangeredSpeciesCount(threshold);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Climate and Carbon
    @GetMapping("/carbon-emission/high")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalDataByHighCarbonEmission(@RequestParam BigDecimal threshold) {
        try {
            List<EnvironmentalData> environmentalDataList = environmentalDataService.findByHighCarbonEmission(threshold);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/climate-resilience/low")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalDataByLowClimateResilience(@RequestParam Integer threshold) {
        try {
            List<EnvironmentalData> environmentalDataList = environmentalDataService.findByLowClimateResilience(threshold);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Agricultural Impact
    @GetMapping("/soil-erosion/high")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalDataByHighSoilErosion(@RequestParam BigDecimal threshold) {
        try {
            List<EnvironmentalData> environmentalDataList = environmentalDataService.findByHighSoilErosion(threshold);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pesticide-residue/high")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalDataByHighPesticideResidue(@RequestParam BigDecimal threshold) {
        try {
            List<EnvironmentalData> environmentalDataList = environmentalDataService.findByHighPesticideResidue(threshold);
            return new ResponseEntity<>(environmentalDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Monitoring Management
    @GetMapping("/due-for-monitoring")
    public ResponseEntity<List<EnvironmentalData>> getDueForMonitoring() {
        try {
            List<EnvironmentalData> dueForMonitoring = environmentalDataService.findDueForMonitoring();
            return new ResponseEntity<>(dueForMonitoring, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/due-for-monitoring/{date}")
    public ResponseEntity<List<EnvironmentalData>> getDueForMonitoring(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<EnvironmentalData> dueForMonitoring = environmentalDataService.findDueForMonitoring(date);
            return new ResponseEntity<>(dueForMonitoring, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/next-monitoring-date")
    public ResponseEntity<String> updateNextMonitoringDate(@PathVariable String id,
                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nextMonitoringDate) {
        try {
            boolean updated = environmentalDataService.updateNextMonitoringDate(id, nextMonitoringDate);
            if (updated) {
                return new ResponseEntity<>("Next monitoring date updated successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Environmental data not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating next monitoring date", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Statistical Endpoints
    @GetMapping("/statistics/air-quality/region/{region}")
    public ResponseEntity<Double> getAverageAirQualityByRegion(@PathVariable String region) {
        try {
            Double averageAirQuality = environmentalDataService.getAverageAirQualityByRegion(region);
            return new ResponseEntity<>(averageAirQuality, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/statistics/water-quality/region/{region}")
    public ResponseEntity<Double> getAverageWaterQualityByRegion(@PathVariable String region) {
        try {
            Double averageWaterQuality = environmentalDataService.getAverageWaterQualityByRegion(region);
            return new ResponseEntity<>(averageWaterQuality, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/statistics/forest-coverage/region/{region}")
    public ResponseEntity<Double> getAverageForestCoverageByRegion(@PathVariable String region) {
        try {
            Double averageForestCoverage = environmentalDataService.getAverageForestCoverageByRegion(region);
            return new ResponseEntity<>(averageForestCoverage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/statistics/risk-level/{riskLevel}/count")
    public ResponseEntity<Long> countByRiskLevel(@PathVariable EnvironmentalRiskLevel riskLevel) {
        try {
            Long count = environmentalDataService.countByRiskLevel(riskLevel);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/statistics/risk-levels")
    public ResponseEntity<Map<EnvironmentalRiskLevel, Long>> getRiskLevelStatistics() {
        try {
            Map<EnvironmentalRiskLevel, Long> statistics = environmentalDataService.getRiskLevelStatistics();
            return new ResponseEntity<>(statistics, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/statistics/regional-air-quality-averages")
    public ResponseEntity<Map<String, Double>> getRegionalAirQualityAverages() {
        try {
            Map<String, Double> averages = environmentalDataService.getRegionalAirQualityAverages();
            return new ResponseEntity<>(averages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/statistics/regional-water-quality-averages")
    public ResponseEntity<Map<String, Double>> getRegionalWaterQualityAverages() {
        try {
            Map<String, Double> averages = environmentalDataService.getRegionalWaterQualityAverages();
            return new ResponseEntity<>(averages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Geographic Reference Endpoints
    @GetMapping("/regions")
    public ResponseEntity<List<String>> getAllRegions() {
        try {
            List<String> regions = environmentalDataService.getAllRegions();
            return new ResponseEntity<>(regions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/regions/{region}/districts")
    public ResponseEntity<List<String>> getDistrictsByRegion(@PathVariable String region) {
        try {
            List<String> districts = environmentalDataService.getDistrictsByRegion(region);
            return new ResponseEntity<>(districts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/regions/{region}/districts/{district}/sectors")
    public ResponseEntity<List<String>> getSectorsByRegionAndDistrict(@PathVariable String region, @PathVariable String district) {
        try {
            List<String> sectors = environmentalDataService.getSectorsByRegionAndDistrict(region, district);
            return new ResponseEntity<>(sectors, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Trend Analysis
    @GetMapping("/trends/region/{region}")
    public ResponseEntity<List<EnvironmentalData>> getTrendDataByRegion(@PathVariable String region,
                                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate) {
        try {
            List<EnvironmentalData> trendData = environmentalDataService.getTrendDataByRegion(region, fromDate);
            return new ResponseEntity<>(trendData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Business Intelligence Endpoints
    @GetMapping("/alerts")
    public ResponseEntity<List<EnvironmentalData>> getEnvironmentalAlerts() {
        try {
            List<EnvironmentalData> alerts = environmentalDataService.getEnvironmentalAlerts();
            return new ResponseEntity<>(alerts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/summary/region/{region}")
    public ResponseEntity<Map<String, Object>> getEnvironmentalSummaryByRegion(@PathVariable String region) {
        try {
            Map<String, Object> summary = environmentalDataService.getEnvironmentalSummaryByRegion(region);
            return new ResponseEntity<>(summary, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<EnvironmentalData>> getRecommendationsForImprovement() {
        try {
            List<EnvironmentalData> recommendations = environmentalDataService.getRecommendationsForImprovement();
            return new ResponseEntity<>(recommendations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Health Check
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            Map<String, Object> health = Map.of(
                    "status", "UP",
                    "totalRecords", environmentalDataService.count(),
                    "timestamp", LocalDateTime.now()
            );
            return new ResponseEntity<>(health, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> health = Map.of(
                    "status", "DOWN",
                    "error", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            );
            return new ResponseEntity<>(health, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}