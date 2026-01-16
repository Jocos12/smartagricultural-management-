package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.Farm;
import SmartAgricultural.Management.Service.FarmService;

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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/farms")
@Validated
@CrossOrigin(origins = "*")
public class FarmController {

    private final FarmService farmService;

    @Autowired
    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    // CREATE operations
    @PostMapping
    public ResponseEntity<Farm> createFarm(@Valid @RequestBody Farm farm) {
        Farm createdFarm = farmService.createFarm(farm);
        return new ResponseEntity<>(createdFarm, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Farm>> createMultipleFarms(@Valid @RequestBody List<Farm> farms) {
        List<Farm> createdFarms = farmService.createMultipleFarms(farms);
        return new ResponseEntity<>(createdFarms, HttpStatus.CREATED);
    }

    // READ operations
    @GetMapping
    public ResponseEntity<Page<Farm>> getAllFarms(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Farm> farms = farmService.getAllFarms(pageable);
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Farm>> getAllFarmsNoPagination() {
        List<Farm> farms = farmService.getAllFarms();
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Farm> getFarmById(@PathVariable @NotBlank String id) {
        Farm farm = farmService.getFarmById(id);
        return ResponseEntity.ok(farm);
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<Page<Farm>> getFarmsByFarmerId(
            @PathVariable @NotBlank String farmerId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Farm> farms = farmService.getFarmsByFarmerId(farmerId, pageable);
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/farmer/{farmerId}/all")
    public ResponseEntity<List<Farm>> getAllFarmsByFarmerId(@PathVariable @NotBlank String farmerId) {
        List<Farm> farms = farmService.getFarmsByFarmerId(farmerId);
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/code/{farmCode}")
    public ResponseEntity<Farm> getFarmByCode(@PathVariable @NotBlank String farmCode) {
        Optional<Farm> farm = farmService.getFarmByCode(farmCode);
        return farm.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<Farm>> getFarmsByName(@RequestParam @NotBlank String farmName) {
        List<Farm> farms = farmService.getFarmsByName(farmName);
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/search/soil-type")
    public ResponseEntity<Page<Farm>> getFarmsBySoilType(
            @RequestParam @NotBlank String soilType,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Farm> farms = farmService.getFarmsBySoilType(soilType, pageable);
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/search/irrigation")
    public ResponseEntity<List<Farm>> getFarmsByIrrigationSystem(
            @RequestParam Farm.IrrigationSystem irrigationSystem) {
        List<Farm> farms = farmService.getFarmsByIrrigationSystem(irrigationSystem);
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/search/electricity")
    public ResponseEntity<List<Farm>> getFarmsByElectricityAvailability(
            @RequestParam Boolean electricityAvailable) {
        List<Farm> farms = farmService.getFarmsByElectricityAvailability(electricityAvailable);
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/search/topography")
    public ResponseEntity<List<Farm>> getFarmsByTopography(@RequestParam @NotBlank String topography) {
        List<Farm> farms = farmService.getFarmsByTopography(topography);
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/search/size-range")
    public ResponseEntity<Page<Farm>> getFarmsBySizeRange(
            @RequestParam BigDecimal minSize,
            @RequestParam BigDecimal maxSize,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Farm> farms = farmService.getFarmsBySizeRange(minSize, maxSize, pageable);
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/categories/large")
    public ResponseEntity<List<Farm>> getLargeFarms() {
        List<Farm> farms = farmService.getLargeFarms();
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/categories/small")
    public ResponseEntity<List<Farm>> getSmallFarms() {
        List<Farm> farms = farmService.getSmallFarms();
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/categories/medium")
    public ResponseEntity<List<Farm>> getMediumFarms() {
        List<Farm> farms = farmService.getMediumFarms();
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/search/coordinates")
    public ResponseEntity<List<Farm>> getFarmsWithinCoordinates(
            @RequestParam BigDecimal minLatitude,
            @RequestParam BigDecimal maxLatitude,
            @RequestParam BigDecimal minLongitude,
            @RequestParam BigDecimal maxLongitude) {

        List<Farm> farms = farmService.getFarmsWithinCoordinates(
                minLatitude, maxLatitude, minLongitude, maxLongitude);
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/search/near")
    public ResponseEntity<List<Farm>> getFarmsNearLocation(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam Double radiusKm) {

        List<Farm> farms = farmService.getFarmsNearLocation(latitude, longitude, radiusKm);
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/search/advanced")
    public ResponseEntity<Page<Farm>> searchFarms(
            @RequestParam(required = false) String farmerId,
            @RequestParam(required = false) String soilType,
            @RequestParam(required = false) Farm.IrrigationSystem irrigationSystem,
            @RequestParam(required = false) Boolean electricityAvailable,
            @RequestParam(required = false) BigDecimal minSize,
            @RequestParam(required = false) BigDecimal maxSize,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Farm> farms = farmService.searchFarms(farmerId, soilType, irrigationSystem,
                electricityAvailable, minSize, maxSize, pageable);
        return ResponseEntity.ok(farms);
    }

    // UPDATE operations
    @PutMapping("/{id}")
    public ResponseEntity<Farm> updateFarm(
            @PathVariable @NotBlank String id,
            @Valid @RequestBody Farm farm) {
        Farm updatedFarm = farmService.updateFarm(id, farm);
        return ResponseEntity.ok(updatedFarm);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Farm> partialUpdateFarm(
            @PathVariable @NotBlank String id,
            @RequestBody Farm farmUpdate) {
        Farm updatedFarm = farmService.partialUpdateFarm(id, farmUpdate);
        return ResponseEntity.ok(updatedFarm);
    }

    // DELETE operations
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFarm(@PathVariable @NotBlank String id) {
        farmService.deleteFarm(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/farmer/{farmerId}")
    public ResponseEntity<Void> deleteFarmsByFarmerId(@PathVariable @NotBlank String farmerId) {
        farmService.deleteFarmsByFarmerId(farmerId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllFarms() {
        farmService.deleteAllFarms();
        return ResponseEntity.noContent().build();
    }

    // STATISTICS and UTILITY endpoints
    @GetMapping("/statistics/total-count")
    public ResponseEntity<Long> getTotalFarmsCount() {
        Long count = farmService.getTotalFarmsCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/statistics/farmer/{farmerId}/count")
    public ResponseEntity<Long> getFarmsCountByFarmerId(@PathVariable @NotBlank String farmerId) {
        Long count = farmService.getFarmsCountByFarmerId(farmerId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/statistics/total-area")
    public ResponseEntity<BigDecimal> getTotalFarmsArea() {
        BigDecimal totalArea = farmService.getTotalFarmsArea();
        return ResponseEntity.ok(totalArea);
    }

    @GetMapping("/statistics/farmer/{farmerId}/total-area")
    public ResponseEntity<BigDecimal> getTotalAreaByFarmerId(@PathVariable @NotBlank String farmerId) {
        BigDecimal totalArea = farmService.getTotalAreaByFarmerId(farmerId);
        return ResponseEntity.ok(totalArea);
    }

    @GetMapping("/statistics/average-size")
    public ResponseEntity<BigDecimal> getAverageFarmSize() {
        BigDecimal averageSize = farmService.getAverageFarmSize();
        return ResponseEntity.ok(averageSize);
    }

    @GetMapping("/statistics/farmer/{farmerId}/average-size")
    public ResponseEntity<BigDecimal> getAverageFarmSizeByFarmerId(@PathVariable @NotBlank String farmerId) {
        BigDecimal averageSize = farmService.getAverageFarmSizeByFarmerId(farmerId);
        return ResponseEntity.ok(averageSize);
    }

    @GetMapping("/statistics/soil-type-distribution")
    public ResponseEntity<Map<String, Long>> getFarmCountBySoilType() {
        Map<String, Long> distribution = farmService.getFarmCountBySoilType();
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/statistics/irrigation-distribution")
    public ResponseEntity<Map<Farm.IrrigationSystem, Long>> getFarmCountByIrrigationSystem() {
        Map<Farm.IrrigationSystem, Long> distribution = farmService.getFarmCountByIrrigationSystem();
        return ResponseEntity.ok(distribution);
    }

    // VALIDATION endpoints
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> farmExists(@PathVariable @NotBlank String id) {
        boolean exists = farmService.farmExists(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/code/{farmCode}/exists")
    public ResponseEntity<Boolean> farmCodeExists(@PathVariable @NotBlank String farmCode) {
        boolean exists = farmService.farmCodeExists(farmCode);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/farmer/{farmerId}/has-farms")
    public ResponseEntity<Boolean> farmerHasFarms(@PathVariable @NotBlank String farmerId) {
        boolean hasFarms = farmService.farmerHasFarms(farmerId);
        return ResponseEntity.ok(hasFarms);
    }

    // BULK operations
    @PostMapping("/bulk-update")
    public ResponseEntity<List<Farm>> bulkUpdateFarms(@Valid @RequestBody List<Farm> farms) {
        List<Farm> updatedFarms = farms.stream()
                .map(farm -> farmService.updateFarm(farm.getId(), farm))
                .toList();
        return ResponseEntity.ok(updatedFarms);
    }

    @DeleteMapping("/bulk-delete")
    public ResponseEntity<Void> bulkDeleteFarms(@RequestBody List<String> farmIds) {
        farmIds.forEach(farmService::deleteFarm);
        return ResponseEntity.noContent().build();
    }

    // EXPORT endpoints
    @GetMapping("/export/farmer/{farmerId}")
    public ResponseEntity<List<Farm>> exportFarmerFarms(@PathVariable @NotBlank String farmerId) {
        List<Farm> farms = farmService.getFarmsByFarmerId(farmerId);
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/export/by-criteria")
    public ResponseEntity<List<Farm>> exportFarmsByCriteria(
            @RequestParam(required = false) String soilType,
            @RequestParam(required = false) Farm.IrrigationSystem irrigationSystem,
            @RequestParam(required = false) Boolean electricityAvailable,
            @RequestParam(required = false) BigDecimal minSize,
            @RequestParam(required = false) BigDecimal maxSize) {

        // Use a large page size for export
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<Farm> farmsPage = farmService.searchFarms(null, soilType, irrigationSystem,
                electricityAvailable, minSize, maxSize, pageable);
        return ResponseEntity.ok(farmsPage.getContent());
    }

    // DASHBOARD summary endpoint
    @GetMapping("/dashboard/summary")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        Map<String, Object> summary = Map.of(
                "totalFarms", farmService.getTotalFarmsCount(),
                "totalArea", farmService.getTotalFarmsArea(),
                "averageSize", farmService.getAverageFarmSize(),
                "largeFarmsCount", farmService.getLargeFarms().size(),
                "smallFarmsCount", farmService.getSmallFarms().size(),
                "mediumFarmsCount", farmService.getMediumFarms().size(),
                "soilTypeDistribution", farmService.getFarmCountBySoilType(),
                "irrigationDistribution", farmService.getFarmCountByIrrigationSystem()
        );
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/dashboard/farmer/{farmerId}/summary")
    public ResponseEntity<Map<String, Object>> getFarmerDashboardSummary(@PathVariable @NotBlank String farmerId) {
        List<Farm> farmerFarms = farmService.getFarmsByFarmerId(farmerId);

        Map<String, Object> summary = Map.of(
                "totalFarms", farmService.getFarmsCountByFarmerId(farmerId),
                "totalArea", farmService.getTotalAreaByFarmerId(farmerId),
                "averageSize", farmService.getAverageFarmSizeByFarmerId(farmerId),
                "farms", farmerFarms
        );
        return ResponseEntity.ok(summary);
    }
}