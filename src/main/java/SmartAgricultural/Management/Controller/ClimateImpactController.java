package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.ClimateImpact;
import SmartAgricultural.Management.Service.ClimateImpactService;
import SmartAgricultural.Management.dto.ClimateImpactDTO;
import SmartAgricultural.Management.dto.ClimateImpactSummaryDTO;
import SmartAgricultural.Management.dto.ClimateStatisticsDTO;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/climate-impacts")
@Tag(name = "Climate Impact Management", description = "API for managing climate impact records")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClimateImpactController {

    private static final Logger logger = LoggerFactory.getLogger(ClimateImpactController.class);

    private final ClimateImpactService climateImpactService;

    @Autowired
    public ClimateImpactController(ClimateImpactService climateImpactService) {
        this.climateImpactService = climateImpactService;
    }

    /**
     * Create a new climate impact record
     */
    @PostMapping
    @Operation(summary = "Create climate impact", description = "Create a new climate impact record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Climate impact created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Impact code already exists")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ClimateImpactDTO> createClimateImpact(
            @Valid @RequestBody ClimateImpactDTO climateImpactDTO) {
        try {
            ClimateImpactDTO createdImpact = climateImpactService.createClimateImpact(climateImpactDTO);
            return new ResponseEntity<>(createdImpact, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Error creating climate impact: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error("Unexpected error creating climate impact: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all climate impacts
     */
    @GetMapping
    @Operation(summary = "Get all climate impacts", description = "Retrieve all climate impact records")
    @ApiResponse(responseCode = "200", description = "Climate impacts retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClimateImpactDTO>> getAllClimateImpacts() {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getAllClimateImpacts();
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving climate impacts: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get climate impacts with pagination
     */
    @GetMapping("/paginated")
    @Operation(summary = "Get paginated climate impacts", description = "Retrieve climate impacts with pagination")
    @ApiResponse(responseCode = "200", description = "Paginated climate impacts retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Page<ClimateImpactDTO>> getAllClimateImpactsPaginated(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction")
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ClimateImpactDTO> impacts = climateImpactService.getAllClimateImpacts(pageable);
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving paginated climate impacts: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get climate impact by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get climate impact by ID", description = "Retrieve a climate impact record by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Climate impact found"),
            @ApiResponse(responseCode = "404", description = "Climate impact not found")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ClimateImpactDTO> getClimateImpactById(
            @Parameter(description = "Climate impact ID")
            @PathVariable String id) {
        try {
            ClimateImpactDTO impact = climateImpactService.getClimateImpactById(id);
            return ResponseEntity.ok(impact);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error retrieving climate impact: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get climate impact by code
     */
    @GetMapping("/code/{impactCode}")
    @Operation(summary = "Get climate impact by code", description = "Retrieve a climate impact record by its code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Climate impact found"),
            @ApiResponse(responseCode = "404", description = "Climate impact not found")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ClimateImpactDTO> getClimateImpactByCode(
            @Parameter(description = "Climate impact code")
            @PathVariable String impactCode) {
        try {
            ClimateImpactDTO impact = climateImpactService.getClimateImpactByCode(impactCode);
            return ResponseEntity.ok(impact);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error retrieving climate impact by code: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update climate impact
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update climate impact", description = "Update an existing climate impact record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Climate impact updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Climate impact not found"),
            @ApiResponse(responseCode = "409", description = "Impact code already exists")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ClimateImpactDTO> updateClimateImpact(
            @Parameter(description = "Climate impact ID")
            @PathVariable String id,
            @Valid @RequestBody ClimateImpactDTO climateImpactDTO) {
        try {
            ClimateImpactDTO updatedImpact = climateImpactService.updateClimateImpact(id, climateImpactDTO);
            return ResponseEntity.ok(updatedImpact);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error("Error updating climate impact: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete climate impact
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete climate impact", description = "Delete a climate impact record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Climate impact deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Climate impact not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClimateImpact(
            @Parameter(description = "Climate impact ID")
            @PathVariable String id) {
        try {
            climateImpactService.deleteClimateImpact(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error deleting climate impact: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Verify climate impact
     */
    @PutMapping("/{id}/verify")
    @Operation(summary = "Verify climate impact", description = "Mark a climate impact as verified")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Climate impact verified successfully"),
            @ApiResponse(responseCode = "404", description = "Climate impact not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClimateImpactDTO> verifyClimateImpact(
            @Parameter(description = "Climate impact ID")
            @PathVariable String id,
            @Parameter(description = "Verified by user")
            @RequestParam String verifiedBy) {
        try {
            ClimateImpactDTO verifiedImpact = climateImpactService.verifyClimateImpact(id, verifiedBy);
            return ResponseEntity.ok(verifiedImpact);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error verifying climate impact: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get climate impacts by region
     */
    @GetMapping("/region/{region}")
    @Operation(summary = "Get climate impacts by region", description = "Retrieve climate impacts for a specific region")
    @ApiResponse(responseCode = "200", description = "Climate impacts retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClimateImpactDTO>> getClimateImpactsByRegion(
            @Parameter(description = "Region name")
            @PathVariable String region) {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getClimateImpactsByRegion(region);
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving climate impacts by region: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get climate impacts by year
     */
    @GetMapping("/year/{year}")
    @Operation(summary = "Get climate impacts by year", description = "Retrieve climate impacts for a specific year")
    @ApiResponse(responseCode = "200", description = "Climate impacts retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClimateImpactDTO>> getClimateImpactsByYear(
            @Parameter(description = "Year")
            @PathVariable @Min(2000) @Max(2100) Integer year) {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getClimateImpactsByYear(year);
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving climate impacts by year: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get climate impacts by event type
     */
    @GetMapping("/event/{event}")
    @Operation(summary = "Get climate impacts by event", description = "Retrieve climate impacts by event type")
    @ApiResponse(responseCode = "200", description = "Climate impacts retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClimateImpactDTO>> getClimateImpactsByEvent(
            @Parameter(description = "Climate event type")
            @PathVariable ClimateImpact.ClimateEvent event) {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getClimateImpactsByEvent(event);
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving climate impacts by event: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get climate impacts by season
     */
    @GetMapping("/season/{season}")
    @Operation(summary = "Get climate impacts by season", description = "Retrieve climate impacts by season")
    @ApiResponse(responseCode = "200", description = "Climate impacts retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClimateImpactDTO>> getClimateImpactsBySeason(
            @Parameter(description = "Season")
            @PathVariable ClimateImpact.Season season) {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getClimateImpactsBySeason(season);
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving climate impacts by season: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get verified climate impacts
     */
    @GetMapping("/verified")
    @Operation(summary = "Get verified climate impacts", description = "Retrieve all verified climate impacts")
    @ApiResponse(responseCode = "200", description = "Verified climate impacts retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClimateImpactDTO>> getVerifiedClimateImpacts() {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getVerifiedClimateImpacts();
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving verified climate impacts: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get unverified climate impacts
     */
    @GetMapping("/unverified")
    @Operation(summary = "Get unverified climate impacts", description = "Retrieve all unverified climate impacts")
    @ApiResponse(responseCode = "200", description = "Unverified climate impacts retrieved successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClimateImpactDTO>> getUnverifiedClimateImpacts() {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getUnverifiedClimateImpacts();
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving unverified climate impacts: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get ongoing climate events
     */
    @GetMapping("/ongoing")
    @Operation(summary = "Get ongoing events", description = "Retrieve ongoing climate events")
    @ApiResponse(responseCode = "200", description = "Ongoing events retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClimateImpactDTO>> getOngoingEvents() {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getOngoingEvents();
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving ongoing events: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get recent climate events (last 30 days)
     */
    @GetMapping("/recent")
    @Operation(summary = "Get recent events", description = "Retrieve recent climate events (last 30 days)")
    @ApiResponse(responseCode = "200", description = "Recent events retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClimateImpactDTO>> getRecentEvents() {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getRecentEvents();
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving recent events: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get climate impacts by economic loss threshold
     */
    @GetMapping("/economic-loss")
    @Operation(summary = "Get impacts by economic loss", description = "Retrieve climate impacts above economic loss threshold")
    @ApiResponse(responseCode = "200", description = "Climate impacts retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClimateImpactDTO>> getClimateImpactsByEconomicLoss(
            @Parameter(description = "Minimum economic loss threshold")
            @RequestParam BigDecimal minLoss) {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getClimateImpactsByEconomicLoss(minLoss);
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving climate impacts by economic loss: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get high-risk climate impacts
     */
    @GetMapping("/high-risk")
    @Operation(summary = "Get high-risk impacts", description = "Retrieve high-risk climate impacts")
    @ApiResponse(responseCode = "200", description = "High-risk impacts retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClimateImpactDTO>> getHighRiskImpacts() {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getHighRiskImpacts();
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving high-risk impacts: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search climate impacts
     */
    @GetMapping("/search")
    @Operation(summary = "Search climate impacts", description = "Search climate impacts by keyword")
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClimateImpactDTO>> searchClimateImpacts(
            @Parameter(description = "Search query")
            @RequestParam String query) {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.searchClimateImpacts(query);
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error searching climate impacts: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get climate impacts with filters
     */
    @GetMapping("/filter")
    @Operation(summary = "Get filtered climate impacts", description = "Retrieve climate impacts with multiple filters")
    @ApiResponse(responseCode = "200", description = "Filtered climate impacts retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Page<ClimateImpactDTO>> getClimateImpactsWithFilters(
            @Parameter(description = "Region filter") @RequestParam(required = false) String region,
            @Parameter(description = "District filter") @RequestParam(required = false) String district,
            @Parameter(description = "Year filter") @RequestParam(required = false) Integer year,
            @Parameter(description = "Season filter") @RequestParam(required = false) ClimateImpact.Season season,
            @Parameter(description = "Event filter") @RequestParam(required = false) ClimateImpact.ClimateEvent event,
            @Parameter(description = "Intensity filter") @RequestParam(required = false) ClimateImpact.EventIntensity intensity,
            @Parameter(description = "Verified filter") @RequestParam(required = false) Boolean verified,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ClimateImpactDTO> impacts = climateImpactService.getClimateImpactsWithFilters(
                    region, district, year, season, event, intensity, verified, pageable);
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving filtered climate impacts: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get climate impacts by crop
     */
    @GetMapping("/crop/{cropId}")
    @Operation(summary = "Get impacts by crop", description = "Retrieve climate impacts for a specific crop")
    @ApiResponse(responseCode = "200", description = "Climate impacts retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClimateImpactDTO>> getClimateImpactsByCrop(
            @Parameter(description = "Crop ID")
            @PathVariable String cropId) {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getClimateImpactsByCrop(cropId);
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving climate impacts by crop: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get climate impacts by reporter
     */
    @GetMapping("/reporter/{reporterId}")
    @Operation(summary = "Get impacts by reporter", description = "Retrieve climate impacts by reporter")
    @ApiResponse(responseCode = "200", description = "Climate impacts retrieved successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClimateImpactDTO>> getClimateImpactsByReporter(
            @Parameter(description = "Reporter ID")
            @PathVariable String reporterId) {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getClimateImpactsByReporter(reporterId);
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving climate impacts by reporter: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get climate impacts by date range
     */
    @GetMapping("/date-range")
    @Operation(summary = "Get impacts by date range", description = "Retrieve climate impacts within date range")
    @ApiResponse(responseCode = "200", description = "Climate impacts retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClimateImpactDTO>> getClimateImpactsByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getClimateImpactsByDateRange(startDate, endDate);
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving climate impacts by date range: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get top impacts by economic loss
     */
    @GetMapping("/top/economic-loss")
    @Operation(summary = "Get top impacts by economic loss", description = "Retrieve top climate impacts by economic loss")
    @ApiResponse(responseCode = "200", description = "Top impacts retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClimateImpactDTO>> getTopImpactsByEconomicLoss(
            @Parameter(description = "Number of top impacts to retrieve")
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getTopImpactsByEconomicLoss(limit);
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving top impacts by economic loss: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get top impacts by affected area
     */
    @GetMapping("/top/affected-area")
    @Operation(summary = "Get top impacts by affected area", description = "Retrieve top climate impacts by affected area")
    @ApiResponse(responseCode = "200", description = "Top impacts retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClimateImpactDTO>> getTopImpactsByAffectedArea(
            @Parameter(description = "Number of top impacts to retrieve")
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getTopImpactsByAffectedArea(limit);
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving top impacts by affected area: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get climate statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get climate statistics", description = "Retrieve climate impact statistics")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ClimateStatisticsDTO> getClimateStatistics() {
        try {
            ClimateStatisticsDTO statistics = climateImpactService.getClimateStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Error retrieving climate statistics: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get yearly trends by event
     */
    @GetMapping("/trends/yearly/{event}")
    @Operation(summary = "Get yearly trends", description = "Retrieve yearly trends for specific climate event")
    @ApiResponse(responseCode = "200", description = "Yearly trends retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Map<String, Object>>> getYearlyTrendsByEvent(
            @Parameter(description = "Climate event type")
            @PathVariable ClimateImpact.ClimateEvent event) {
        try {
            List<Map<String, Object>> trends = climateImpactService.getYearlyTrendsByEvent(event);
            return ResponseEntity.ok(trends);
        } catch (Exception e) {
            logger.error("Error retrieving yearly trends: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get regional statistics by year
     */
    @GetMapping("/statistics/regional/{year}")
    @Operation(summary = "Get regional statistics", description = "Retrieve regional statistics for specific year")
    @ApiResponse(responseCode = "200", description = "Regional statistics retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Map<String, Object>>> getRegionalStatsByYear(
            @Parameter(description = "Year")
            @PathVariable @Min(2000) @Max(2100) Integer year) {
        try {
            List<Map<String, Object>> stats = climateImpactService.getRegionalStatsByYear(year);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error retrieving regional statistics: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get seasonal statistics by year
     */
    @GetMapping("/statistics/seasonal/{year}")
    @Operation(summary = "Get seasonal statistics", description = "Retrieve seasonal statistics for specific year")
    @ApiResponse(responseCode = "200", description = "Seasonal statistics retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Map<String, Object>>> getSeasonalStatsByYear(
            @Parameter(description = "Year")
            @PathVariable @Min(2000) @Max(2100) Integer year) {
        try {
            List<Map<String, Object>> stats = climateImpactService.getSeasonalStatsByYear(year);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error retrieving seasonal statistics: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get impacts with financial support
     */
    @GetMapping("/financial-support")
    @Operation(summary = "Get impacts with financial support", description = "Retrieve impacts that received financial support")
    @ApiResponse(responseCode = "200", description = "Impacts with financial support retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClimateImpactDTO>> getImpactsWithFinancialSupport() {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getImpactsWithFinancialSupport();
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving impacts with financial support: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get warning effectiveness statistics
     */
    @GetMapping("/statistics/warning-effectiveness")
    @Operation(summary = "Get warning effectiveness stats", description = "Retrieve warning effectiveness statistics")
    @ApiResponse(responseCode = "200", description = "Warning effectiveness statistics retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Map<String, Object>>> getWarningEffectivenessStats() {
        try {
            List<Map<String, Object>> stats = climateImpactService.getWarningEffectivenessStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error retrieving warning effectiveness stats: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get response effectiveness statistics
     */
    @GetMapping("/statistics/response-effectiveness")
    @Operation(summary = "Get response effectiveness stats", description = "Retrieve response effectiveness statistics")
    @ApiResponse(responseCode = "200", description = "Response effectiveness statistics retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Map<String, Object>>> getResponseEffectivenessStats() {
        try {
            List<Map<String, Object>> stats = climateImpactService.getResponseEffectivenessStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error retrieving response effectiveness stats: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get latest climate impacts
     */
    @GetMapping("/latest")
    @Operation(summary = "Get latest climate impacts", description = "Retrieve latest climate impacts")
    @ApiResponse(responseCode = "200", description = "Latest climate impacts retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClimateImpactDTO>> getLatestClimateImpacts() {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getLatestClimateImpacts();
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving latest climate impacts: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get impacts requiring emergency response
     */
    @GetMapping("/emergency-response")
    @Operation(summary = "Get emergency response impacts", description = "Retrieve impacts requiring emergency response")
    @ApiResponse(responseCode = "200", description = "Emergency response impacts retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClimateImpactDTO>> getEmergencyResponseRequired() {
        try {
            List<ClimateImpactDTO> impacts = climateImpactService.getEmergencyResponseRequired();
            return ResponseEntity.ok(impacts);
        } catch (Exception e) {
            logger.error("Error retrieving emergency response impacts: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Calculate impact severity
     */
    @GetMapping("/{id}/severity")
    @Operation(summary = "Calculate impact severity", description = "Calculate severity level for a climate impact")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Impact severity calculated successfully"),
            @ApiResponse(responseCode = "404", description = "Climate impact not found")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> calculateImpactSeverity(
            @Parameter(description = "Climate impact ID")
            @PathVariable String id) {
        try {
            ClimateImpact.ImpactSeverity severity = climateImpactService.calculateImpactSeverity(id);
            Map<String, Object> response = Map.of(
                    "impactId", id,
                    "severity", severity.name(),
                    "severityLevel", severity.getSeverityLevel(),
                    "displayName", severity.getDisplayName()
            );
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error calculating impact severity: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Generate climate impact summary
     */
    @GetMapping("/summary")
    @Operation(summary = "Generate impact summary", description = "Generate summary for climate impacts")
    @ApiResponse(responseCode = "200", description = "Summary generated successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ClimateImpactSummaryDTO> generateSummary(
            @Parameter(description = "Region filter") @RequestParam String region,
            @Parameter(description = "Year filter") @RequestParam @Min(2000) @Max(2100) Integer year) {
        try {
            ClimateImpactSummaryDTO summary = climateImpactService.generateSummary(region, year);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            logger.error("Error generating summary: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Bulk verify climate impacts
     */
    @PutMapping("/bulk-verify")
    @Operation(summary = "Bulk verify impacts", description = "Verify multiple climate impacts at once")
    @ApiResponse(responseCode = "200", description = "Climate impacts verified successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClimateImpactDTO>> bulkVerifyImpacts(
            @Parameter(description = "List of impact IDs to verify") @RequestBody List<String> impactIds,
            @Parameter(description = "Verified by user") @RequestParam String verifiedBy) {
        try {
            List<ClimateImpactDTO> verifiedImpacts = climateImpactService.bulkVerifyImpacts(impactIds, verifiedBy);
            return ResponseEntity.ok(verifiedImpacts);
        } catch (IllegalArgumentException e) {
            logger.error("Error bulk verifying impacts: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error bulk verifying impacts: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete unverified old records
     */
    @DeleteMapping("/cleanup")
    @Operation(summary = "Cleanup old records", description = "Delete unverified climate impacts older than specified days")
    @ApiResponse(responseCode = "204", description = "Cleanup completed successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUnverifiedOldRecords(
            @Parameter(description = "Number of days old") @RequestParam @Min(1) @Max(365) int daysOld) {
        try {
            climateImpactService.deleteUnverifiedOldRecords(daysOld);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error deleting old records: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get climate impact enums for dropdown options
     */
    @GetMapping("/enums")
    @Operation(summary = "Get enum values", description = "Retrieve all enum values for dropdown options")
    @ApiResponse(responseCode = "200", description = "Enum values retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getEnumValues() {
        try {
            Map<String, Object> enums = Map.of(
                    "seasons", ClimateImpact.Season.values(),
                    "climateEvents", ClimateImpact.ClimateEvent.values(),
                    "eventIntensities", ClimateImpact.EventIntensity.values(),
                    "warningEffectiveness", ClimateImpact.WarningEffectiveness.values(),
                    "responseEffectiveness", ClimateImpact.ResponseEffectiveness.values(),
                    "impactSeverity", ClimateImpact.ImpactSeverity.values()
            );
            return ResponseEntity.ok(enums);
        } catch (Exception e) {
            logger.error("Error retrieving enum values: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Export climate impacts to CSV
     */
    @GetMapping("/export/csv")
    @Operation(summary = "Export to CSV", description = "Export climate impacts to CSV format")
    @ApiResponse(responseCode = "200", description = "CSV export successful")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<String> exportToCSV(
            @Parameter(description = "Region filter") @RequestParam(required = false) String region,
            @Parameter(description = "Year filter") @RequestParam(required = false) Integer year,
            @Parameter(description = "Verified filter") @RequestParam(required = false) Boolean verified) {
        try {
            // This would typically generate CSV content
            // For now, returning a simple response
            return ResponseEntity.ok()
                    .header("Content-Type", "text/csv")
                    .header("Content-Disposition", "attachment; filename=climate-impacts.csv")
                    .body("Export functionality would be implemented here");
        } catch (Exception e) {
            logger.error("Error exporting to CSV: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check service health")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = Map.of(
                "status", "UP",
                "service", "ClimateImpactService",
                "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(health);
    }

    /**
     * Get available endpoints
     */
    @GetMapping("/endpoints")
    @Operation(summary = "Get available endpoints", description = "List all available endpoints")
    @ApiResponse(responseCode = "200", description = "Endpoints retrieved successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAvailableEndpoints() {
        Map<String, Object> endpoints = Map.of(
                "message", "Climate Impact Management API",
                "version", "1.0",
                "endpoints", List.of(
                        "GET /api/climate-impacts - Get all climate impacts",
                        "POST /api/climate-impacts - Create climate impact",
                        "GET /api/climate-impacts/{id} - Get climate impact by ID",
                        "PUT /api/climate-impacts/{id} - Update climate impact",
                        "DELETE /api/climate-impacts/{id} - Delete climate impact",
                        "GET /api/climate-impacts/statistics - Get statistics",
                        "GET /api/climate-impacts/search - Search climate impacts"
                )
        );
        return ResponseEntity.ok(endpoints);
    }
}