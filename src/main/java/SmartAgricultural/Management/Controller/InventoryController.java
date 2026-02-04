package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.Inventory;
import SmartAgricultural.Management.Model.Inventory.*;
import SmartAgricultural.Management.Service.InventoryPredictionService;
import SmartAgricultural.Management.Service.InventoryService;
import SmartAgricultural.Management.dto.*;
import SmartAgricultural.Management.dto.InventoryAlert;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.exception.BusinessLogicException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST Controller for Inventory Management
 * Provides comprehensive CRUD operations and advanced inventory management features
 *
 * @author Smart Agricultural Management System
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/inventories")
@Validated
@CrossOrigin(origins = "*")
public class InventoryController {
    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryPredictionService predictionService;

    // ==================== CRUD OPERATIONS ====================

    /**
     * Create a new inventory item
     *
     * @param request CreateInventoryRequest containing inventory details
     * @return ApiResponse containing the created inventory
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_MANAGER') or hasRole('INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<InventoryDTO>> createInventory(@Valid @RequestBody CreateInventoryRequest request) {
        try {
            Inventory inventory = mapToInventory(request);
            Inventory createdInventory = inventoryService.createInventory(inventory);
            InventoryDTO inventoryDTO = mapToDTO(createdInventory);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Inventory created successfully", inventoryDTO));
        } catch (BusinessLogicException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to create inventory", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error", e.getMessage()));
        }
    }

    /**
     * Get inventory by ID
     *
     * @param id Inventory ID
     * @return ApiResponse containing the inventory details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('FARMER') or hasRole('BUYER')")
    public ResponseEntity<ApiResponse<InventoryDTO>> getInventoryById(@PathVariable String id) {
        try {
            Inventory inventory = inventoryService.findById(id);
            InventoryDTO inventoryDTO = mapToDTO(inventory);

            return ResponseEntity.ok(ApiResponse.success("Inventory retrieved successfully", inventoryDTO));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Inventory not found", e.getMessage()));
        }
    }

    /**
     * Get inventory by inventory code
     *
     * @param inventoryCode Inventory code
     * @return ApiResponse containing the inventory details
     */
    @GetMapping("/code/{inventoryCode}")
    @PreAuthorize("hasRole('USER') or hasRole('FARMER') or hasRole('BUYER')")
    public ResponseEntity<ApiResponse<InventoryDTO>> getInventoryByCode(@PathVariable String inventoryCode) {
        Optional<Inventory> inventory = inventoryService.findByInventoryCode(inventoryCode);

        if (inventory.isPresent()) {
            InventoryDTO inventoryDTO = mapToDTO(inventory.get());
            return ResponseEntity.ok(ApiResponse.success("Inventory retrieved successfully", inventoryDTO));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Inventory not found", "No inventory found with code: " + inventoryCode));
        }
    }







// InventoryController.java - Corrections

// InventoryController.java - REMPLACER la méthode getInventoriesByFarmer



// InventoryController.java - REMPLACER la méthode getInventoriesByFarmer

    /**
     * Get inventories by farmer ID with complete error handling
     *
     * @param farmerId Farmer ID
     * @return ApiResponse containing list of inventory summaries
     */
    @GetMapping("/farmer/{farmerId}")
    @PreAuthorize("hasRole('USER') or hasRole('FARMER') or hasRole('BUYER')")
    public ResponseEntity<ApiResponse<List<InventorySummaryDTO>>> getInventoriesByFarmer(
            @PathVariable String farmerId) {

        logger.info("╔═══════════════════════════════════════════════════════╗");
        logger.info("║     INVENTORY CONTROLLER - GET BY FARMER REQUEST      ║");
        logger.info("╚═══════════════════════════════════════════════════════╝");

        try {
            // VALIDATION RENFORCÉE AVEC DEBUG
            logger.info("📥 Request Details:");
            logger.info("   - Endpoint: GET /api/v1/inventories/farmer/{}", farmerId);
            logger.info("   - Farmer ID: {}", farmerId);
            logger.info("   - Farmer ID Type: {}", farmerId != null ? farmerId.getClass().getName() : "null");
            logger.info("   - Farmer ID Length: {}", farmerId != null ? farmerId.length() : 0);

            if (farmerId == null || farmerId.trim().isEmpty()) {
                logger.warn("❌ VALIDATION FAILED: Invalid farmer ID");
                return ResponseEntity.ok()
                        .body(ApiResponse.success("No inventories found", new ArrayList<>()));
            }

            logger.info("✅ Validation passed, proceeding to service call");
            logger.info("🔄 Calling inventoryService.findByFarmerUserId('{}')", farmerId);

            // RÉCUPÉRATION DES DONNÉES AVEC GESTION D'ERREUR COMPLÈTE
            List<Inventory> inventories;
            try {
                inventories = inventoryService.findByFarmerUserId(farmerId);

                // Triple vérification de sécurité
                if (inventories == null) {
                    logger.warn("Service returned null for farmer: {}", farmerId);
                    inventories = new ArrayList<>();
                }

            } catch (Exception e) {
                logger.error("💥 Service error for farmer {}: {}", farmerId, e.getMessage());
                // En cas d'erreur service, retourner liste vide au lieu de 500
                inventories = new ArrayList<>();
            }

            logger.info("📦 Found {} inventories for farmer: {}", inventories.size(), farmerId);

            // MAPPING ULTRA-SÉCURISÉ
            List<InventorySummaryDTO> inventoryDTOs = inventories.stream()
                    .filter(Objects::nonNull) // Filtrer les nulls
                    .map(inventory -> {
                        try {
                            return mapToSummaryDTO(inventory);
                        } catch (Exception e) {
                            logger.error("Error mapping inventory {}: {}",
                                    inventory != null ? inventory.getId() : "null", e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull) // Filtrer les DTOs qui ont échoué
                    .collect(Collectors.toList());

            String message = inventoryDTOs.isEmpty()
                    ? "No inventories found for this farmer"
                    : "Inventories retrieved successfully";

            // TOUJOURS RETOURNER 200 OK avec une liste (vide ou remplie)
            return ResponseEntity.ok(ApiResponse.success(message, inventoryDTOs));

        } catch (Exception e) {
            // DERNIER FILET DE SÉCURITÉ - ne jamais retourner 500
            logger.error("💥 Unexpected error for farmer {}: {}", farmerId, e.getMessage(), e);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("No inventories available", new ArrayList<>()));
        }
    }

    /**
     * Get global inventory predictions
     */
    @GetMapping("/predictions/global")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_MANAGER') or hasRole('ANALYTICS_VIEWER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGlobalPredictions() {
        try {
            Map<String, Object> predictions = predictionService.getGlobalInventoryPredictions();
            return ResponseEntity.ok(ApiResponse.success("Global predictions retrieved successfully", predictions));
        } catch (Exception e) {
            logger.error("Error getting global predictions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get predictions", e.getMessage()));
        }
    }

    /**
     * Get crop-specific predictions
     */
    @GetMapping("/predictions/crop/{cropId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_MANAGER') or hasRole('ANALYTICS_VIEWER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCropPredictions(@PathVariable String cropId) {
        try {
            Map<String, Object> predictions = predictionService.getCropSpecificPredictions(cropId);
            return ResponseEntity.ok(ApiResponse.success("Crop predictions retrieved successfully", predictions));
        } catch (Exception e) {
            logger.error("Error getting crop predictions for {}: {}", cropId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get crop predictions", e.getMessage()));
        }
    }







    /**
     * Get all inventories with pagination
     *
     * @param pageable Pagination parameters
     * @return ApiResponse containing paginated inventory summaries
     */
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('FARMER') or hasRole('BUYER')")
    public ResponseEntity<ApiResponse<Page<InventorySummaryDTO>>> getAllInventories(
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Inventory> inventories = inventoryService.findAll(pageable);
        Page<InventorySummaryDTO> inventoryDTOs = inventories.map(this::mapToSummaryDTO);

        return ResponseEntity.ok(ApiResponse.success("Inventories retrieved successfully", inventoryDTOs));
    }

    /**
     * Update an existing inventory
     *
     * @param id Inventory ID
     * @param request UpdateInventoryRequest containing updated details
     * @return ApiResponse containing the updated inventory
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_MANAGER') or hasRole('INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<InventoryDTO>> updateInventory(
            @PathVariable String id, @Valid @RequestBody UpdateInventoryRequest request) {
        try {
            Inventory inventory = mapToInventory(request);
            Inventory updatedInventory = inventoryService.updateInventory(id, inventory);
            InventoryDTO inventoryDTO = mapToDTO(updatedInventory);

            return ResponseEntity.ok(ApiResponse.success("Inventory updated successfully", inventoryDTO));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Inventory not found", e.getMessage()));
        } catch (BusinessLogicException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update inventory", e.getMessage()));
        }
    }

    /**
     * Partially update an inventory
     *
     * @param id Inventory ID
     * @param updates Map of fields to update
     * @return ApiResponse containing the updated inventory
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_MANAGER') or hasRole('INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<InventoryDTO>> partialUpdateInventory(
            @PathVariable String id, @RequestBody Map<String, Object> updates) {
        try {
            Inventory updatedInventory = inventoryService.partialUpdateInventory(id, updates);
            InventoryDTO inventoryDTO = mapToDTO(updatedInventory);

            return ResponseEntity.ok(ApiResponse.success("Inventory updated successfully", inventoryDTO));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Inventory not found", e.getMessage()));
        } catch (BusinessLogicException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update inventory", e.getMessage()));
        }
    }

    /**
     * Delete an inventory
     *
     * @param id Inventory ID
     * @return ApiResponse confirming deletion
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteInventory(@PathVariable String id) {
        try {
            inventoryService.deleteInventory(id);
            return ResponseEntity.ok(ApiResponse.success("Inventory deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Inventory not found", e.getMessage()));
        } catch (BusinessLogicException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to delete inventory", e.getMessage()));
        }
    }

    // ==================== SEARCH AND FILTER OPERATIONS ====================

    /**
     * Search inventories with advanced criteria
     *
     * @param criteria InventorySearchCriteria
     * @param pageable Pagination parameters
     * @return ApiResponse containing search results
     */
    @PostMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('FARMER') or hasRole('BUYER')")
    public ResponseEntity<ApiResponse<Page<InventorySummaryDTO>>> searchInventory(
            @RequestBody InventorySearchCriteria criteria,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<Inventory> results = inventoryService.searchInventory(criteria, pageable);
        Page<InventorySummaryDTO> resultDTOs = results.map(this::mapToSummaryDTO);

        return ResponseEntity.ok(ApiResponse.success("Search completed successfully", resultDTOs));
    }

    /**
     * Search inventories by keyword
     *
     * @param keyword Search keyword
     * @return ApiResponse containing search results
     */
    @GetMapping("/search/keyword")
    @PreAuthorize("hasRole('USER') or hasRole('FARMER') or hasRole('BUYER')")
    public ResponseEntity<ApiResponse<List<InventorySummaryDTO>>> searchByKeyword(@RequestParam String keyword) {
        List<Inventory> results = inventoryService.searchByKeyword(keyword);
        List<InventorySummaryDTO> resultDTOs = results.stream()
                .map(this::mapToSummaryDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Search completed successfully", resultDTOs));
    }

    // ==================== INVENTORY MANAGEMENT OPERATIONS ====================

    /**
     * Reserve inventory for a buyer
     *
     * @param id Inventory ID
     * @param request ReserveInventoryRequest
     * @return ApiResponse containing updated inventory
     */
    @PostMapping("/{id}/reserve")
    @PreAuthorize("hasRole('BUYER') or hasRole('ADMIN') or hasRole('INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<InventoryDTO>> reserveInventory(
            @PathVariable String id, @RequestBody @Valid ReserveInventoryRequest request) {
        try {
            Inventory reservedInventory = inventoryService.reserveInventory(
                    id, request.getQuantity(), request.getBuyerId());
            InventoryDTO inventoryDTO = mapToDTO(reservedInventory);

            return ResponseEntity.ok(ApiResponse.success("Inventory reserved successfully", inventoryDTO));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Inventory not found", e.getMessage()));
        } catch (BusinessLogicException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to reserve inventory", e.getMessage()));
        }
    }

    /**
     * Mark inventory as sold
     *
     * @param id Inventory ID
     * @param request MarkAsSoldRequest
     * @return ApiResponse containing updated inventory
     */
    @PostMapping("/{id}/mark-sold")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_MANAGER') or hasRole('SALES_MANAGER')")
    public ResponseEntity<ApiResponse<InventoryDTO>> markAsSold(
            @PathVariable String id, @RequestBody @Valid MarkAsSoldRequest request) {
        try {
            Inventory inventory = inventoryService.markAsSold(
                    id, request.getSoldQuantity(), request.getSoldPrice());
            InventoryDTO inventoryDTO = mapToDTO(inventory);

            return ResponseEntity.ok(ApiResponse.success("Inventory marked as sold", inventoryDTO));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Inventory not found", e.getMessage()));
        } catch (BusinessLogicException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to mark as sold", e.getMessage()));
        }
    }

    // ==================== ANALYTICS AND REPORTING ====================

    /**
     * Get inventory statistics
     *
     * @return ApiResponse containing statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_MANAGER') or hasRole('ANALYTICS_VIEWER')")
    public ResponseEntity<ApiResponse<InventoryStatistics>> getInventoryStatistics() {
        InventoryStatistics stats = inventoryService.getInventoryStatistics();
        return ResponseEntity.ok(ApiResponse.success("Statistics retrieved successfully", stats));
    }

    /**
     * Get inventory alerts
     *
     * @return ApiResponse containing alerts
     */
    @GetMapping("/alerts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARM_MANAGER') or hasRole('INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<List<InventoryAlert>>> getInventoryAlerts() {
        List<InventoryAlert> alerts = inventoryService.getInventoryAlerts();
        return ResponseEntity.ok(ApiResponse.success("Alerts retrieved successfully", alerts));
    }

    // ==================== HEALTH CHECK ====================

    /**
     * Health check endpoint
     *
     * @return ApiResponse with service health status
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> health = new java.util.HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("version", "1.0");

        return ResponseEntity.ok(ApiResponse.success("Service is healthy", health));
    }

    // ==================== EXCEPTION HANDLERS ====================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Resource not found", e.getMessage()));
    }

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessLogicException(BusinessLogicException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Business logic error", e.getMessage()));
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            jakarta.validation.ConstraintViolationException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Validation error", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error", e.getMessage()));
    }

    // ==================== PRIVATE MAPPING METHODS ====================


// ==================== DANS InventoryController.java ====================
// REMPLACER la méthode mapToDTO par celle-ci:

    private InventoryDTO mapToDTO(Inventory inventory) {
        InventoryDTO dto = new InventoryDTO();
        dto.setId(inventory.getId());
        dto.setInventoryCode(inventory.getInventoryCode());
        dto.setCropId(inventory.getCropId());
        dto.setFarmerId(inventory.getFarmerUserId());
        dto.setFacilityType(inventory.getFacilityType());
        dto.setStorageLocation(inventory.getStorageLocation());
        dto.setCurrentQuantity(inventory.getCurrentQuantity());
        dto.setAvailableQuantity(inventory.getAvailableQuantity());
        dto.setQualityGrade(inventory.getQualityGrade());
        dto.setStatus(inventory.getStatus());
        dto.setTotalMarketValue(inventory.getTotalMarketValue());

        // ⭐ AJOUTER CES 4 LIGNES:
        dto.setStorageDate(inventory.getStorageDate());
        dto.setExpiryDate(inventory.getExpiryDate());
        dto.setOrganicCertified(inventory.getOrganicCertified());
        dto.setFairTradeCertified(inventory.getFairTradeCertified());

        dto.setCreatedDate(inventory.getCreatedDate());
        dto.setLastUpdated(inventory.getLastUpdated());
        return dto;
    }

    private InventorySummaryDTO mapToSummaryDTO(Inventory inventory) {
        InventorySummaryDTO dto = new InventorySummaryDTO();
        dto.setId(inventory.getId());
        dto.setInventoryCode(inventory.getInventoryCode());
        dto.setCropId(inventory.getCropId());
        dto.setFacilityType(inventory.getFacilityType());
        dto.setStorageLocation(inventory.getStorageLocation());
        dto.setCurrentQuantity(inventory.getCurrentQuantity());
        dto.setAvailableQuantity(inventory.getAvailableQuantity());
        dto.setUnit(inventory.getUnit());
        dto.setQualityGrade(inventory.getQualityGrade());
        dto.setStatus(inventory.getStatus());
        dto.setTotalMarketValue(inventory.getTotalMarketValue());
        dto.setFormattedQuantity(inventory.getFormattedQuantity());
        dto.setFormattedMarketValue(inventory.getFormattedMarketValue());
        return dto;
    }



    private Inventory mapToInventory(CreateInventoryRequest request) {
        Inventory inventory = new Inventory();
        inventory.setCropId(request.getCropId());
        inventory.setFarmerUserId(request.getFarmerId()); // CHANGÉ
        inventory.setFacilityType(request.getFacilityType());
        inventory.setStorageLocation(request.getStorageLocation());
        inventory.setCurrentQuantity(request.getCurrentQuantity());
        inventory.setQualityGrade(request.getQualityGrade());
        inventory.setMarketValuePerUnit(request.getMarketValuePerUnit());
        inventory.setOrganicCertified(request.getOrganicCertified());
        inventory.setFairTradeCertified(request.getFairTradeCertified());
        return inventory;
    }

    private Inventory mapToInventory(UpdateInventoryRequest request) {
        Inventory inventory = new Inventory();
        inventory.setCurrentQuantity(request.getCurrentQuantity());
        inventory.setQualityGrade(request.getQualityGrade());
        inventory.setMarketValuePerUnit(request.getMarketValuePerUnit());
        inventory.setStorageLocation(request.getStorageLocation());
        inventory.setStatus(request.getStatus());
        return inventory;
    }
}