package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.PolicyData;
import SmartAgricultural.Management.Model.PolicyData.PolicyType;
import SmartAgricultural.Management.Model.PolicyData.PolicyCategory;
import SmartAgricultural.Management.Model.PolicyData.PolicyStatus;
import SmartAgricultural.Management.Model.PolicyData.GeographicScope;
import SmartAgricultural.Management.Model.PolicyData.PolicyEffectiveness;
import SmartAgricultural.Management.Service.PolicyDataService;
import SmartAgricultural.Management.dto.PolicyDataDTO;
import SmartAgricultural.Management.dto.PolicySearchCriteriaDTO;
import SmartAgricultural.Management.dto.PolicyStatisticsDTO;
import SmartAgricultural.Management.dto.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/policies")
@CrossOrigin(origins = "*")
public class PolicyDataController {



    private static final Logger logger = LoggerFactory.getLogger(PolicyDataController.class);
    @Autowired
    private PolicyDataService policyDataService;

    // CRUD Endpoints

    @PostMapping
    public ResponseEntity<ApiResponse<PolicyData>> createPolicy(
            @Valid @RequestBody PolicyData policyData,
            @RequestParam(required = false) String createdBy) {
        try {
            policyData.setCreatedBy(createdBy);
            PolicyData createdPolicy = policyDataService.createPolicy(policyData);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Policy created successfully", createdPolicy));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PolicyData>> updatePolicy(
            @PathVariable String id,
            @Valid @RequestBody PolicyData policyData,
            @RequestParam(required = false) String updatedBy) {
        try {
            policyData.setUpdatedBy(updatedBy);
            PolicyData updatedPolicy = policyDataService.updatePolicy(id, policyData);
            return ResponseEntity.ok(new ApiResponse<>(true, "Policy updated successfully", updatedPolicy));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PolicyData>> getPolicyById(@PathVariable String id) {
        try {
            PolicyData policy = policyDataService.getPolicyById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Policy retrieved successfully", policy));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/code/{policyCode}")
    public ResponseEntity<ApiResponse<PolicyData>> getPolicyByCode(@PathVariable String policyCode) {
        try {
            PolicyData policy = policyDataService.getPolicyByCode(policyCode);
            return ResponseEntity.ok(new ApiResponse<>(true, "Policy retrieved successfully", policy));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PolicyData>>> getAllPolicies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "lastUpdated") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ?
                    Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<PolicyData> policies = policyDataService.getAllPolicies(pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Policies retrieved successfully", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePolicy(@PathVariable String id) {
        try {
            policyDataService.deletePolicy(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Policy deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Status Management Endpoints

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PolicyData>> updatePolicyStatus(
            @PathVariable String id,
            @RequestParam PolicyStatus status,
            @RequestParam(required = false) String updatedBy) {
        try {
            PolicyData policy = policyDataService.updatePolicyStatus(id, status, updatedBy);
            return ResponseEntity.ok(new ApiResponse<>(true, "Policy status updated successfully", policy));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<PolicyData>> activatePolicy(
            @PathVariable String id,
            @RequestParam(required = false) String updatedBy) {
        try {
            PolicyData policy = policyDataService.activatePolicy(id, updatedBy);
            return ResponseEntity.ok(new ApiResponse<>(true, "Policy activated successfully", policy));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/suspend")
    public ResponseEntity<ApiResponse<PolicyData>> suspendPolicy(
            @PathVariable String id,
            @RequestParam String reason,
            @RequestParam(required = false) String updatedBy) {
        try {
            PolicyData policy = policyDataService.suspendPolicy(id, reason, updatedBy);
            return ResponseEntity.ok(new ApiResponse<>(true, "Policy suspended successfully", policy));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/renew")
    public ResponseEntity<ApiResponse<PolicyData>> renewPolicy(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newExpiryDate,
            @RequestParam(required = false) String updatedBy) {
        try {
            PolicyData policy = policyDataService.renewPolicy(id, newExpiryDate, updatedBy);
            return ResponseEntity.ok(new ApiResponse<>(true, "Policy renewed successfully", policy));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/update-expired")
    public ResponseEntity<ApiResponse<Integer>> updateExpiredPolicies() {
        try {
            int updatedCount = policyDataService.updateExpiredPolicies();
            return ResponseEntity.ok(new ApiResponse<>(true,
                    updatedCount + " policies marked as expired", updatedCount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Search and Filter Endpoints

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PolicyData>>> searchPolicies(
            @RequestParam String keyword) {
        try {
            List<PolicyData> policies = policyDataService.searchPolicies(keyword);
            return ResponseEntity.ok(new ApiResponse<>(true, "Search completed successfully", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/filter/type/{policyType}")
    public ResponseEntity<ApiResponse<List<PolicyData>>> getPoliciesByType(
            @PathVariable PolicyType policyType) {
        try {
            List<PolicyData> policies = policyDataService.getPoliciesByType(policyType);
            return ResponseEntity.ok(new ApiResponse<>(true, "Policies filtered by type", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/filter/category/{policyCategory}")
    public ResponseEntity<ApiResponse<List<PolicyData>>> getPoliciesByCategory(
            @PathVariable PolicyCategory policyCategory) {
        try {
            List<PolicyData> policies = policyDataService.getPoliciesByCategory(policyCategory);
            return ResponseEntity.ok(new ApiResponse<>(true, "Policies filtered by category", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/filter/status/{status}")
    public ResponseEntity<ApiResponse<List<PolicyData>>> getPoliciesByStatus(
            @PathVariable PolicyStatus status) {
        try {
            List<PolicyData> policies = policyDataService.getPoliciesByStatus(status);
            return ResponseEntity.ok(new ApiResponse<>(true, "Policies filtered by status", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PolicyData>>> getActivePolicies() {
        try {
            List<PolicyData> policies = policyDataService.getActivePolicies();
            return ResponseEntity.ok(new ApiResponse<>(true, "Active policies retrieved", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/expired")
    public ResponseEntity<ApiResponse<List<PolicyData>>> getExpiredPolicies() {
        try {
            List<PolicyData> policies = policyDataService.getExpiredPolicies();
            return ResponseEntity.ok(new ApiResponse<>(true, "Expired policies retrieved", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/expiring-soon")
    public ResponseEntity<ApiResponse<List<PolicyData>>> getPoliciesExpiringSoon(
            @RequestParam(defaultValue = "90") int days) {
        try {
            List<PolicyData> policies = policyDataService.getPoliciesExpiringSoon(days);
            return ResponseEntity.ok(new ApiResponse<>(true, "Policies expiring soon retrieved", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/filter/agency")
    public ResponseEntity<ApiResponse<List<PolicyData>>> getPoliciesByImplementingAgency(
            @RequestParam String agency) {
        try {
            List<PolicyData> policies = policyDataService.getPoliciesByImplementingAgency(agency);
            return ResponseEntity.ok(new ApiResponse<>(true, "Policies filtered by agency", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/filter/scope/{scope}")
    public ResponseEntity<ApiResponse<List<PolicyData>>> getPoliciesByGeographicScope(
            @PathVariable GeographicScope scope) {
        try {
            List<PolicyData> policies = policyDataService.getPoliciesByGeographicScope(scope);
            return ResponseEntity.ok(new ApiResponse<>(true, "Policies filtered by geographic scope", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/filter/budget-range")
    public ResponseEntity<ApiResponse<List<PolicyData>>> getPoliciesByBudgetRange(
            @RequestParam BigDecimal minBudget,
            @RequestParam BigDecimal maxBudget) {
        try {
            List<PolicyData> policies = policyDataService.getPoliciesByBudgetRange(minBudget, maxBudget);
            return ResponseEntity.ok(new ApiResponse<>(true, "Policies filtered by budget range", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/requiring-review")
    public ResponseEntity<ApiResponse<List<PolicyData>>> getPoliciesRequiringReview() {
        try {
            List<PolicyData> policies = policyDataService.getPoliciesRequiringReview();
            return ResponseEntity.ok(new ApiResponse<>(true, "Policies requiring review retrieved", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/climate-smart")
    public ResponseEntity<ApiResponse<List<PolicyData>>> getClimateSmartPolicies() {
        try {
            List<PolicyData> policies = policyDataService.getClimateSmartPolicies();
            return ResponseEntity.ok(new ApiResponse<>(true, "Climate smart policies retrieved", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/youth-focused")
    public ResponseEntity<ApiResponse<List<PolicyData>>> getYouthFocusedPolicies() {
        try {
            List<PolicyData> policies = policyDataService.getYouthFocusedPolicies();
            return ResponseEntity.ok(new ApiResponse<>(true, "Youth focused policies retrieved", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<PolicyData>>> getRecentPolicies(
            @RequestParam(defaultValue = "30") int days) {
        try {
            List<PolicyData> policies = policyDataService.getRecentPolicies(days);
            return ResponseEntity.ok(new ApiResponse<>(true, "Recent policies retrieved", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Advanced Search Endpoint

    @PostMapping("/search/advanced")
    public ResponseEntity<ApiResponse<List<PolicyData>>> advancedSearch(
            @RequestBody PolicySearchCriteriaDTO searchCriteria) {
        try {
            List<PolicyData> policies = policyDataService.searchPoliciesWithCriteria(
                    searchCriteria.getPolicyType(),
                    searchCriteria.getPolicyCategory(),
                    searchCriteria.getStatus(),
                    searchCriteria.getGeographicScope(),
                    searchCriteria.getImplementingAgency(),
                    searchCriteria.getMinBudget(),
                    searchCriteria.getMaxBudget()
            );
            return ResponseEntity.ok(new ApiResponse<>(true, "Advanced search completed", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Performance Analysis Endpoints

    @GetMapping("/performance/high-performing")
    public ResponseEntity<ApiResponse<List<PolicyData>>> getHighPerformingPolicies(
            @RequestParam(defaultValue = "80") BigDecimal minUtilizationRate) {
        try {
            List<PolicyData> policies = policyDataService.getHighPerformingPolicies(minUtilizationRate);
            return ResponseEntity.ok(new ApiResponse<>(true, "High performing policies retrieved", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/performance/low-performing")
    public ResponseEntity<ApiResponse<List<PolicyData>>> getLowPerformingPolicies(
            @RequestParam(defaultValue = "40") BigDecimal threshold) {
        try {
            List<PolicyData> policies = policyDataService.getLowPerformingPolicies(threshold);
            return ResponseEntity.ok(new ApiResponse<>(true, "Low performing policies retrieved", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/performance/top")
    public ResponseEntity<ApiResponse<List<PolicyData>>> getTopPerformingPolicies() {
        try {
            List<PolicyData> policies = policyDataService.getTopPerformingPolicies();
            return ResponseEntity.ok(new ApiResponse<>(true, "Top performing policies retrieved", policies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}/effectiveness")
    public ResponseEntity<ApiResponse<PolicyEffectiveness>> assessPolicyEffectiveness(
            @PathVariable String id) {
        try {
            PolicyEffectiveness effectiveness = policyDataService.assessPolicyEffectiveness(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Policy effectiveness assessed", effectiveness));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }


    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPolicyStatistics() {
        try {
            logger.info("📊 Fetching policy statistics...");

            Map<String, Object> statistics = policyDataService.getPolicyStatistics();

            // Vérifier si une erreur est survenue
            if (statistics.containsKey("error")) {
                logger.warn("⚠️ Statistics returned with error: {}", statistics.get("error"));
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .body(new ApiResponse<>(true, "Statistics loaded with warnings", statistics));
            }

            logger.info("✅ Policy statistics fetched successfully");
            return ResponseEntity.ok(new ApiResponse<>(true, "Policy statistics retrieved successfully", statistics));

        } catch (Exception e) {
            logger.error("❌ Error in getPolicyStatistics endpoint: {}", e.getMessage(), e);

            // Retourner des statistiques vides plutôt qu'une erreur 500
            Map<String, Object> emptyStats = new HashMap<>();
            emptyStats.put("totalPolicies", 0L);
            emptyStats.put("activePolicies", 0L);
            emptyStats.put("error", "Service temporarily unavailable");

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ApiResponse<>(false, "Error retrieving statistics: " + e.getMessage(), emptyStats));
        }
    }

    @GetMapping("/statistics/by-status")
    public ResponseEntity<ApiResponse<Map<PolicyStatus, Long>>> getPolicyCountByStatus() {
        try {
            Map<PolicyStatus, Long> counts = policyDataService.getPolicyCountByStatus();
            return ResponseEntity.ok(new ApiResponse<>(true, "Policy count by status retrieved", counts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/statistics/by-type")
    public ResponseEntity<ApiResponse<Map<PolicyType, Long>>> getPolicyCountByType() {
        try {
            Map<PolicyType, Long> counts = policyDataService.getPolicyCountByType();
            return ResponseEntity.ok(new ApiResponse<>(true, "Policy count by type retrieved", counts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/statistics/by-category")
    public ResponseEntity<ApiResponse<Map<PolicyCategory, Long>>> getPolicyCountByCategory() {
        try {
            Map<PolicyCategory, Long> counts = policyDataService.getPolicyCountByCategory();
            return ResponseEntity.ok(new ApiResponse<>(true, "Policy count by category retrieved", counts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/statistics/total-budget")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalActiveBudget() {
        try {
            BigDecimal totalBudget = policyDataService.getTotalActiveBudget();
            return ResponseEntity.ok(new ApiResponse<>(true, "Total active budget retrieved", totalBudget));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/statistics/average-utilization")
    public ResponseEntity<ApiResponse<BigDecimal>> getAverageUtilizationRate() {
        try {
            BigDecimal averageRate = policyDataService.getAverageUtilizationRate();
            return ResponseEntity.ok(new ApiResponse<>(true, "Average utilization rate retrieved", averageRate));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Utility Endpoints

    @GetMapping("/enums/policy-types")
    public ResponseEntity<ApiResponse<PolicyType[]>> getPolicyTypes() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Policy types retrieved", PolicyType.values()));
    }

    @GetMapping("/enums/policy-categories")
    public ResponseEntity<ApiResponse<PolicyCategory[]>> getPolicyCategories() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Policy categories retrieved", PolicyCategory.values()));
    }

    @GetMapping("/enums/policy-statuses")
    public ResponseEntity<ApiResponse<PolicyStatus[]>> getPolicyStatuses() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Policy statuses retrieved", PolicyStatus.values()));
    }

    @GetMapping("/enums/geographic-scopes")
    public ResponseEntity<ApiResponse<GeographicScope[]>> getGeographicScopes() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Geographic scopes retrieved", GeographicScope.values()));
    }

    // Health Check Endpoint

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Policy service is running", "OK"));
    }
}