package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.FoodSecurityAlert;
import SmartAgricultural.Management.Service.FoodSecurityAlertService;
import SmartAgricultural.Management.dto.FoodSecurityAlertDTO;
import SmartAgricultural.Management.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/food-security-alerts")
@CrossOrigin(origins = "*")
public class FoodSecurityAlertController {

    @Autowired
    private FoodSecurityAlertService alertService;

    // Create new alert
    @PostMapping
    public ResponseEntity<ApiResponse<FoodSecurityAlert>> createAlert(
            @Valid @RequestBody FoodSecurityAlertDTO alertDTO) {
        try {
            FoodSecurityAlert alert = alertService.createAlert(alertDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Alert created successfully", alert));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Failed to create alert: " + e.getMessage(), null));
        }
    }

    // Get all alerts with pagination
    @GetMapping
    public ResponseEntity<ApiResponse<Page<FoodSecurityAlert>>> getAllAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "alertDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("ASC") ?
                    Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<FoodSecurityAlert> alerts = alertService.getAllAlerts(pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alerts retrieved successfully", alerts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve alerts: " + e.getMessage(), null));
        }
    }

    // Get alert by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodSecurityAlert>> getAlertById(@PathVariable String id) {
        try {
            FoodSecurityAlert alert = alertService.getAlertById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alert retrieved successfully", alert));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Alert not found: " + e.getMessage(), null));
        }
    }

    // Get alert by alert code
    @GetMapping("/code/{alertCode}")
    public ResponseEntity<ApiResponse<FoodSecurityAlert>> getAlertByCode(@PathVariable String alertCode) {
        try {
            FoodSecurityAlert alert = alertService.getAlertByCode(alertCode);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alert retrieved successfully", alert));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Alert not found: " + e.getMessage(), null));
        }
    }

    // Update alert
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodSecurityAlert>> updateAlert(
            @PathVariable String id,
            @Valid @RequestBody FoodSecurityAlertDTO alertDTO) {
        try {
            FoodSecurityAlert alert = alertService.updateAlert(id, alertDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alert updated successfully", alert));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Failed to update alert: " + e.getMessage(), null));
        }
    }

    // Delete alert
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAlert(@PathVariable String id) {
        try {
            alertService.deleteAlert(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alert deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Failed to delete alert: " + e.getMessage(), null));
        }
    }

    // Get active alerts
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<Page<FoodSecurityAlert>>> getActiveAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("alertDate").descending());
            Page<FoodSecurityAlert> alerts = alertService.getActiveAlerts(pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Active alerts retrieved successfully", alerts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve active alerts: " + e.getMessage(), null));
        }
    }

    // Get alerts by category
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<Page<FoodSecurityAlert>>> getAlertsByCategory(
            @PathVariable FoodSecurityAlert.AlertCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("alertDate").descending());
            Page<FoodSecurityAlert> alerts = alertService.getAlertsByCategory(category, pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alerts retrieved successfully", alerts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve alerts: " + e.getMessage(), null));
        }
    }

    // Get alerts by level
    @GetMapping("/level/{level}")
    public ResponseEntity<ApiResponse<Page<FoodSecurityAlert>>> getAlertsByLevel(
            @PathVariable FoodSecurityAlert.AlertLevel level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("alertDate").descending());
            Page<FoodSecurityAlert> alerts = alertService.getAlertsByLevel(level, pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alerts retrieved successfully", alerts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve alerts: " + e.getMessage(), null));
        }
    }

    // Get critical alerts
    @GetMapping("/critical")
    public ResponseEntity<ApiResponse<List<FoodSecurityAlert>>> getCriticalAlerts() {
        try {
            List<FoodSecurityAlert> alerts = alertService.getCriticalAlerts();
            return ResponseEntity.ok(new ApiResponse<>(true, "Critical alerts retrieved successfully", alerts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve critical alerts: " + e.getMessage(), null));
        }
    }

    // Get alerts by region
    @GetMapping("/region/{region}")
    public ResponseEntity<ApiResponse<Page<FoodSecurityAlert>>> getAlertsByRegion(
            @PathVariable String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("alertDate").descending());
            Page<FoodSecurityAlert> alerts = alertService.getAlertsByRegion(region, pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alerts retrieved successfully", alerts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve alerts: " + e.getMessage(), null));
        }
    }

    // Get alerts by date range
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<Page<FoodSecurityAlert>>> getAlertsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("alertDate").descending());
            Page<FoodSecurityAlert> alerts = alertService.getAlertsByDateRange(startDate, endDate, pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alerts retrieved successfully", alerts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve alerts: " + e.getMessage(), null));
        }
    }

    // Get unresolved alerts
    @GetMapping("/unresolved")
    public ResponseEntity<ApiResponse<Page<FoodSecurityAlert>>> getUnresolvedAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("alertDate").descending());
            Page<FoodSecurityAlert> alerts = alertService.getUnresolvedAlerts(pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Unresolved alerts retrieved successfully", alerts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve unresolved alerts: " + e.getMessage(), null));
        }
    }

    // Get overdue alerts
    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<FoodSecurityAlert>>> getOverdueAlerts() {
        try {
            List<FoodSecurityAlert> alerts = alertService.getOverdueAlerts();
            return ResponseEntity.ok(new ApiResponse<>(true, "Overdue alerts retrieved successfully", alerts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve overdue alerts: " + e.getMessage(), null));
        }
    }

    // Escalate alert
    @PutMapping("/{id}/escalate")
    public ResponseEntity<ApiResponse<FoodSecurityAlert>> escalateAlert(@PathVariable String id) {
        try {
            FoodSecurityAlert alert = alertService.escalateAlert(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alert escalated successfully", alert));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Failed to escalate alert: " + e.getMessage(), null));
        }
    }

    // Mark alert as in progress
    @PutMapping("/{id}/in-progress")
    public ResponseEntity<ApiResponse<FoodSecurityAlert>> markAsInProgress(@PathVariable String id) {
        try {
            FoodSecurityAlert alert = alertService.markAsInProgress(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alert marked as in progress", alert));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Failed to update alert: " + e.getMessage(), null));
        }
    }

    // Mark alert as resolved
    @PutMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<FoodSecurityAlert>> resolveAlert(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, String> resolutionDetails) {
        try {
            String lessonsLearned = resolutionDetails != null ?
                    resolutionDetails.get("lessonsLearned") : null;
            FoodSecurityAlert alert = alertService.resolveAlert(id, lessonsLearned);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alert resolved successfully", alert));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Failed to resolve alert: " + e.getMessage(), null));
        }
    }

    // Deactivate alert
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<FoodSecurityAlert>> deactivateAlert(@PathVariable String id) {
        try {
            FoodSecurityAlert alert = alertService.deactivateAlert(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alert deactivated successfully", alert));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Failed to deactivate alert: " + e.getMessage(), null));
        }
    }

    // Reactivate alert
    @PutMapping("/{id}/reactivate")
    public ResponseEntity<ApiResponse<FoodSecurityAlert>> reactivateAlert(@PathVariable String id) {
        try {
            FoodSecurityAlert alert = alertService.reactivateAlert(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alert reactivated successfully", alert));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Failed to reactivate alert: " + e.getMessage(), null));
        }
    }

    // Extend expiry date
    @PutMapping("/{id}/extend-expiry")
    public ResponseEntity<ApiResponse<FoodSecurityAlert>> extendExpiry(
            @PathVariable String id,
            @RequestParam int days) {
        try {
            FoodSecurityAlert alert = alertService.extendExpiry(id, days);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alert expiry extended successfully", alert));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Failed to extend expiry: " + e.getMessage(), null));
        }
    }

    // Update response deadline
    @PutMapping("/{id}/response-deadline")
    public ResponseEntity<ApiResponse<FoodSecurityAlert>> updateResponseDeadline(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deadline) {
        try {
            FoodSecurityAlert alert = alertService.updateResponseDeadline(id, deadline);
            return ResponseEntity.ok(new ApiResponse<>(true, "Response deadline updated successfully", alert));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Failed to update deadline: " + e.getMessage(), null));
        }
    }

    // Add stakeholder
    @PostMapping("/{id}/stakeholders")
    public ResponseEntity<ApiResponse<FoodSecurityAlert>> addStakeholder(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        try {
            String stakeholder = request.get("stakeholder");
            FoodSecurityAlert alert = alertService.addStakeholder(id, stakeholder);
            return ResponseEntity.ok(new ApiResponse<>(true, "Stakeholder added successfully", alert));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Failed to add stakeholder: " + e.getMessage(), null));
        }
    }

    // Add follow-up alert
    @PostMapping("/{id}/follow-up")
    public ResponseEntity<ApiResponse<FoodSecurityAlert>> addFollowUpAlert(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        try {
            String followUpAlertId = request.get("alertId");
            FoodSecurityAlert alert = alertService.addFollowUpAlert(id, followUpAlertId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Follow-up alert added successfully", alert));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Failed to add follow-up alert: " + e.getMessage(), null));
        }
    }

    // Get alert statistics
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAlertStatistics() {
        try {
            Map<String, Object> statistics = alertService.getAlertStatistics();
            return ResponseEntity.ok(new ApiResponse<>(true, "Statistics retrieved successfully", statistics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve statistics: " + e.getMessage(), null));
        }
    }

    // Get alerts by severity score range
    @GetMapping("/severity-range")
    public ResponseEntity<ApiResponse<Page<FoodSecurityAlert>>> getAlertsBySeverityRange(
            @RequestParam int minScore,
            @RequestParam int maxScore,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("severityScore").descending());
            Page<FoodSecurityAlert> alerts = alertService.getAlertsBySeverityRange(minScore, maxScore, pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alerts retrieved successfully", alerts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve alerts: " + e.getMessage(), null));
        }
    }

    // Search alerts
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<FoodSecurityAlert>>> searchAlerts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("alertDate").descending());
            Page<FoodSecurityAlert> alerts = alertService.searchAlerts(keyword, pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Search results retrieved successfully", alerts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to search alerts: " + e.getMessage(), null));
        }
    }

    // Get alerts created by user
    @GetMapping("/created-by/{userId}")
    public ResponseEntity<ApiResponse<Page<FoodSecurityAlert>>> getAlertsByCreator(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<FoodSecurityAlert> alerts = alertService.getAlertsByCreator(userId, pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alerts retrieved successfully", alerts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve alerts: " + e.getMessage(), null));
        }
    }

    // Get alerts with high population impact
    @GetMapping("/high-population-impact")
    public ResponseEntity<ApiResponse<Page<FoodSecurityAlert>>> getAlertsWithHighPopulationImpact(
            @RequestParam(defaultValue = "10000") int threshold,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("affectedPopulation").descending());
            Page<FoodSecurityAlert> alerts = alertService.getAlertsWithHighPopulationImpact(threshold, pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alerts retrieved successfully", alerts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve alerts: " + e.getMessage(), null));
        }
    }

    // Get alerts with media attention
    @GetMapping("/media-attention")
    public ResponseEntity<ApiResponse<Page<FoodSecurityAlert>>> getAlertsWithMediaAttention(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("alertDate").descending());
            Page<FoodSecurityAlert> alerts = alertService.getAlertsWithMediaAttention(pageable);
            return ResponseEntity.ok(new ApiResponse<>(true, "Alerts retrieved successfully", alerts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve alerts: " + e.getMessage(), null));
        }
    }

    // Get recent alerts
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<FoodSecurityAlert>>> getRecentAlerts(
            @RequestParam(defaultValue = "7") int days) {
        try {
            List<FoodSecurityAlert> alerts = alertService.getRecentAlerts(days);
            return ResponseEntity.ok(new ApiResponse<>(true, "Recent alerts retrieved successfully", alerts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve recent alerts: " + e.getMessage(), null));
        }
    }
}