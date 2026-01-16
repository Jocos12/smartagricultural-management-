package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.Farmer;
import SmartAgricultural.Management.Model.Farmer.ExperienceLevel;
import SmartAgricultural.Management.Service.FarmerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/api/farmers")
@CrossOrigin(origins = "*")
public class FarmerController {

    @Autowired
    private FarmerService farmerService;

    // Basic CRUD operations
    @PostMapping
    public ResponseEntity<?> createFarmer(@Valid @RequestBody Farmer farmer) {
        try {
            Farmer createdFarmer = farmerService.createFarmer(farmer);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFarmer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while creating the farmer"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFarmerById(@PathVariable String id) {
        try {
            Optional<Farmer> farmer = farmerService.findFarmerById(id);
            return farmer.map(f -> ResponseEntity.ok(f))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving the farmer"));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getFarmerByUserId(@PathVariable String userId) {
        try {
            Optional<Farmer> farmer = farmerService.findFarmerByUserId(userId);
            return farmer.map(f -> ResponseEntity.ok(f))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving the farmer"));
        }
    }

    @GetMapping("/code/{farmerCode}")
    public ResponseEntity<?> getFarmerByFarmerCode(@PathVariable String farmerCode) {
        try {
            Optional<Farmer> farmer = farmerService.findFarmerByFarmerCode(farmerCode);
            return farmer.map(f -> ResponseEntity.ok(f))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving the farmer"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllFarmers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            if (page < 0 || size <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Page must be >= 0 and size must be > 0"));
            }

            Page<Farmer> farmers = farmerService.findAllFarmers(page, size, sortBy, sortDirection);

            Map<String, Object> response = new HashMap<>();
            response.put("content", farmers.getContent());
            response.put("page", farmers.getNumber());
            response.put("size", farmers.getSize());
            response.put("totalElements", farmers.getTotalElements());
            response.put("totalPages", farmers.getTotalPages());
            response.put("first", farmers.isFirst());
            response.put("last", farmers.isLast());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving farmers"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFarmer(@PathVariable String id, @Valid @RequestBody Farmer farmer) {
        try {
            Farmer updatedFarmer = farmerService.updateFarmer(id, farmer);
            return ResponseEntity.ok(updatedFarmer);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while updating the farmer"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFarmer(@PathVariable String id) {
        try {
            farmerService.deleteFarmer(id);
            return ResponseEntity.ok(Map.of("message", "Farmer deleted successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while deleting the farmer"));
        }
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteFarmerByUserId(@PathVariable String userId) {
        try {
            farmerService.deleteFarmerByUserId(userId);
            return ResponseEntity.ok(Map.of("message", "Farmer deleted successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while deleting the farmer"));
        }
    }

    // Location-based endpoints
    @GetMapping("/province/{province}")
    public ResponseEntity<?> getFarmersByProvince(
            @PathVariable String province,
            @RequestParam(required = false, defaultValue = "false") boolean paginated,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (paginated) {
                Page<Farmer> farmers = farmerService.findFarmersByProvince(province, page, size);
                return ResponseEntity.ok(createPageResponse(farmers));
            } else {
                List<Farmer> farmers = farmerService.findFarmersByProvince(province);
                return ResponseEntity.ok(farmers);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving farmers by province"));
        }
    }

    @GetMapping("/district/{district}")
    public ResponseEntity<?> getFarmersByDistrict(@PathVariable String district) {
        try {
            List<Farmer> farmers = farmerService.findFarmersByDistrict(district);
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving farmers by district"));
        }
    }

    @GetMapping("/sector/{sector}")
    public ResponseEntity<?> getFarmersBySector(@PathVariable String sector) {
        try {
            List<Farmer> farmers = farmerService.findFarmersBySector(sector);
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving farmers by sector"));
        }
    }

    @GetMapping("/location")
    public ResponseEntity<?> getFarmersByLocation(
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String sector) {
        try {
            List<Farmer> farmers = farmerService.findFarmersByLocation(province, district, sector);
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving farmers by location"));
        }
    }

    @GetMapping("/area")
    public ResponseEntity<?> getFarmersInArea(
            @RequestParam BigDecimal minLatitude,
            @RequestParam BigDecimal maxLatitude,
            @RequestParam BigDecimal minLongitude,
            @RequestParam BigDecimal maxLongitude) {
        try {
            List<Farmer> farmers = farmerService.findFarmersInArea(minLatitude, maxLatitude, minLongitude, maxLongitude);
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving farmers in area"));
        }
    }

    // Experience and certification endpoints
    @GetMapping("/experience/{experienceLevel}")
    public ResponseEntity<?> getFarmersByExperienceLevel(
            @PathVariable ExperienceLevel experienceLevel,
            @RequestParam(required = false, defaultValue = "false") boolean paginated,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (paginated) {
                Page<Farmer> farmers = farmerService.findFarmersByExperienceLevel(experienceLevel, page, size);
                return ResponseEntity.ok(createPageResponse(farmers));
            } else {
                List<Farmer> farmers = farmerService.findFarmersByExperienceLevel(experienceLevel);
                return ResponseEntity.ok(farmers);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving farmers by experience level"));
        }
    }

    @GetMapping("/certified")
    public ResponseEntity<?> getCertifiedFarmers() {
        try {
            List<Farmer> farmers = farmerService.findCertifiedFarmers();
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving certified farmers"));
        }
    }

    @GetMapping("/uncertified")
    public ResponseEntity<?> getUncertifiedFarmers() {
        try {
            List<Farmer> farmers = farmerService.findUnCertifiedFarmers();
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving uncertified farmers"));
        }
    }

    // Cooperative endpoints
    @GetMapping("/cooperative/{cooperativeName}")
    public ResponseEntity<?> getFarmersByCooperative(
            @PathVariable String cooperativeName,
            @RequestParam(required = false, defaultValue = "false") boolean paginated,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (paginated) {
                Page<Farmer> farmers = farmerService.findFarmersByCooperative(cooperativeName, page, size);
                return ResponseEntity.ok(createPageResponse(farmers));
            } else {
                List<Farmer> farmers = farmerService.findFarmersByCooperative(cooperativeName);
                return ResponseEntity.ok(farmers);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving farmers by cooperative"));
        }
    }

    @GetMapping("/cooperative/members")
    public ResponseEntity<?> getFarmersInCooperatives() {
        try {
            List<Farmer> farmers = farmerService.findFarmersInCooperatives();
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving farmers in cooperatives"));
        }
    }

    @GetMapping("/independent")
    public ResponseEntity<?> getIndependentFarmers() {
        try {
            List<Farmer> farmers = farmerService.findIndependentFarmers();
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving independent farmers"));
        }
    }

    // Land size endpoints
    @GetMapping("/landsize/greater-than/{landSize}")
    public ResponseEntity<?> getFarmersWithLandSizeGreaterThan(@PathVariable BigDecimal landSize) {
        try {
            List<Farmer> farmers = farmerService.findFarmersWithLandSizeGreaterThan(landSize);
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving farmers by land size"));
        }
    }

    @GetMapping("/landsize/less-than/{landSize}")
    public ResponseEntity<?> getFarmersWithLandSizeLessThan(@PathVariable BigDecimal landSize) {
        try {
            List<Farmer> farmers = farmerService.findFarmersWithLandSizeLessThan(landSize);
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving farmers by land size"));
        }
    }

    @GetMapping("/landsize/between")
    public ResponseEntity<?> getFarmersWithLandSizeBetween(
            @RequestParam BigDecimal minSize,
            @RequestParam BigDecimal maxSize) {
        try {
            List<Farmer> farmers = farmerService.findFarmersWithLandSizeBetween(minSize, maxSize);
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving farmers by land size range"));
        }
    }

    // Search endpoints
    @GetMapping("/search/location")
    public ResponseEntity<?> searchFarmersByLocation(@RequestParam String location) {
        try {
            List<Farmer> farmers = farmerService.searchFarmersByLocation(location);
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while searching farmers by location"));
        }
    }

    @GetMapping("/search/contact")
    public ResponseEntity<?> searchFarmersByContactPerson(@RequestParam String contactPerson) {
        try {
            List<Farmer> farmers = farmerService.searchFarmersByContactPerson(contactPerson);
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while searching farmers by contact person"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchFarmersByCriteria(
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String sector,
            @RequestParam(required = false) ExperienceLevel experienceLevel,
            @RequestParam(required = false) String cooperativeName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Farmer> farmers = farmerService.searchFarmersByCriteria(
                    province, district, sector, experienceLevel, cooperativeName, page, size);
            return ResponseEntity.ok(createPageResponse(farmers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while searching farmers by criteria"));
        }
    }

    // Date-based endpoints
    @GetMapping("/created/between")
    public ResponseEntity<?> getFarmersCreatedBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<Farmer> farmers = farmerService.findFarmersCreatedBetween(startDate, endDate);
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving farmers by creation date"));
        }
    }

    @GetMapping("/created/after")
    public ResponseEntity<?> getFarmersCreatedAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        try {
            List<Farmer> farmers = farmerService.findFarmersCreatedAfter(date);
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving farmers created after date"));
        }
    }

    @GetMapping("/created/before")
    public ResponseEntity<?> getFarmersCreatedBefore(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        try {
            List<Farmer> farmers = farmerService.findFarmersCreatedBefore(date);
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving farmers created before date"));
        }
    }

    // Statistical endpoints
    @GetMapping("/stats/count/experience/{experienceLevel}")
    public ResponseEntity<?> countFarmersByExperienceLevel(@PathVariable ExperienceLevel experienceLevel) {
        try {
            Long count = farmerService.countFarmersByExperienceLevel(experienceLevel);
            return ResponseEntity.ok(Map.of("count", count, "experienceLevel", experienceLevel));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while counting farmers by experience level"));
        }
    }

    @GetMapping("/stats/count/province/{province}")
    public ResponseEntity<?> countFarmersByProvince(@PathVariable String province) {
        try {
            Long count = farmerService.countFarmersByProvince(province);
            return ResponseEntity.ok(Map.of("count", count, "province", province));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while counting farmers by province"));
        }
    }

    @GetMapping("/stats/count/total")
    public ResponseEntity<?> countTotalFarmers() {
        try {
            Long count = farmerService.countTotalFarmers();
            return ResponseEntity.ok(Map.of("totalCount", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while counting total farmers"));
        }
    }

    @GetMapping("/stats/count/with-landsize")
    public ResponseEntity<?> countFarmersWithLandSize() {
        try {
            Long count = farmerService.countFarmersWithLandSize();
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while counting farmers with land size"));
        }
    }

    @GetMapping("/stats/landsize/total/province/{province}")
    public ResponseEntity<?> getTotalLandSizeByProvince(@PathVariable String province) {
        try {
            BigDecimal totalLandSize = farmerService.getTotalLandSizeByProvince(province);
            return ResponseEntity.ok(Map.of("totalLandSize", totalLandSize, "province", province));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while calculating total land size by province"));
        }
    }

    @GetMapping("/stats/landsize/average")
    public ResponseEntity<?> getAverageLandSize() {
        try {
            BigDecimal averageLandSize = farmerService.getAverageLandSize();
            return ResponseEntity.ok(Map.of("averageLandSize", averageLandSize));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while calculating average land size"));
        }
    }

    // Advanced query endpoints
    @GetMapping("/experience-province")
    public ResponseEntity<?> getFarmersByExperienceLevelAndProvince(
            @RequestParam ExperienceLevel experienceLevel,
            @RequestParam String province) {
        try {
            List<Farmer> farmers = farmerService.findFarmersByExperienceLevelAndProvince(experienceLevel, province);
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving farmers by experience level and province"));
        }
    }

    @GetMapping("/cooperative/experience/{experienceLevel}")
    public ResponseEntity<?> getCooperativeFarmersByExperienceLevel(@PathVariable ExperienceLevel experienceLevel) {
        try {
            List<Farmer> farmers = farmerService.findCooperativeFarmersByExperienceLevel(experienceLevel);
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving cooperative farmers by experience level"));
        }
    }

    // Lookup endpoints
    @GetMapping("/lookup/provinces")
    public ResponseEntity<?> getAllProvinces() {
        try {
            List<String> provinces = farmerService.getAllProvinces();
            return ResponseEntity.ok(Map.of("provinces", provinces));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving provinces"));
        }
    }

    @GetMapping("/lookup/districts")
    public ResponseEntity<?> getDistrictsByProvince(@RequestParam String province) {
        try {
            List<String> districts = farmerService.getDistrictsByProvince(province);
            return ResponseEntity.ok(Map.of("districts", districts, "province", province));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving districts"));
        }
    }

    @GetMapping("/lookup/sectors")
    public ResponseEntity<?> getSectorsByProvinceAndDistrict(
            @RequestParam String province,
            @RequestParam String district) {
        try {
            List<String> sectors = farmerService.getSectorsByProvinceAndDistrict(province, district);
            return ResponseEntity.ok(Map.of("sectors", sectors, "province", province, "district", district));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving sectors"));
        }
    }

    @GetMapping("/lookup/cooperatives")
    public ResponseEntity<?> getAllCooperatives() {
        try {
            List<String> cooperatives = farmerService.getAllCooperatives();
            return ResponseEntity.ok(Map.of("cooperatives", cooperatives));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving cooperatives"));
        }
    }

    // Utility endpoints
    @GetMapping("/exists/user/{userId}")
    public ResponseEntity<?> checkExistsByUserId(@PathVariable String userId) {
        try {
            boolean exists = farmerService.existsByUserId(userId);
            return ResponseEntity.ok(Map.of("exists", exists, "userId", userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while checking farmer existence"));
        }
    }

    @GetMapping("/exists/code/{farmerCode}")
    public ResponseEntity<?> checkExistsByFarmerCode(@PathVariable String farmerCode) {
        try {
            boolean exists = farmerService.existsByFarmerCode(farmerCode);
            return ResponseEntity.ok(Map.of("exists", exists, "farmerCode", farmerCode));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while checking farmer code existence"));
        }
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<?> checkExistsById(@PathVariable String id) {
        try {
            boolean exists = farmerService.existsById(id);
            return ResponseEntity.ok(Map.of("exists", exists, "id", id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while checking farmer existence"));
        }
    }

    // Maintenance endpoints
    @DeleteMapping("/maintenance/old-records")
    public ResponseEntity<?> deleteOldRecords(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeDate) {
        try {
            farmerService.deleteOldRecords(beforeDate);
            return ResponseEntity.ok(Map.of("message", "Old records deleted successfully", "beforeDate", beforeDate));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while deleting old records"));
        }
    }

    // Helper method to create paginated response
    private Map<String, Object> createPageResponse(Page<Farmer> page) {
        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("page", page.getNumber());
        response.put("size", page.getSize());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("first", page.isFirst());
        response.put("last", page.isLast());
        return response;
    }
}