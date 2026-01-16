package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Controller.InventoryController;
import SmartAgricultural.Management.Model.Inventory;
import SmartAgricultural.Management.Model.Inventory.*;
import SmartAgricultural.Management.Repository.InventoryRepository;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.exception.BusinessLogicException;
import SmartAgricultural.Management.dto.InventoryDTO;
import SmartAgricultural.Management.dto.InventorySearchCriteria;
import SmartAgricultural.Management.dto.InventoryStatistics;
import SmartAgricultural.Management.dto.InventoryAlert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);







// InventoryService.java - REMPLACER la méthode findByFarmerUserId

    /**
     * Find inventories by farmer user ID with COMPLETE DEBUG
     *
     * @param farmerId Farmer user ID
     * @return List of inventories (never null)
     */
    @Transactional(readOnly = true)
    public List<Inventory> findByFarmerUserId(String farmerId) {
        // ============ PHASE 1: VALIDATION ============
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("🔍 INVENTORY SEARCH START");
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("📥 Input Farmer ID: {}", farmerId);

        try {
            // Validation stricte
            if (farmerId == null) {
                logger.error("❌ Farmer ID is NULL");
                return Collections.emptyList();
            }

            if (farmerId.trim().isEmpty()) {
                logger.error("❌ Farmer ID is EMPTY after trim");
                return Collections.emptyList();
            }

            logger.info("✅ Farmer ID validation passed");
            logger.info("📊 Farmer ID length: {}", farmerId.length());
            logger.info("📊 Farmer ID trimmed: '{}'", farmerId.trim());

            // ============ PHASE 2: DATABASE CALL ============
            logger.info("───────────────────────────────────────────────────────");
            logger.info("🔄 Calling Repository Method");
            logger.info("───────────────────────────────────────────────────────");

            List<Inventory> inventories = null;

            try {
                logger.debug("⏳ Executing: inventoryRepository.findByFarmerUserId('{}')...", farmerId);
                long startTime = System.currentTimeMillis();

                inventories = inventoryRepository.findByFarmerUserId(farmerId);

                long executionTime = System.currentTimeMillis() - startTime;
                logger.info("⚡ Query executed in {} ms", executionTime);

            } catch (org.springframework.dao.DataAccessException dae) {
                logger.error("═══════════════════════════════════════════════════════");
                logger.error("💥 DATABASE ACCESS ERROR");
                logger.error("═══════════════════════════════════════════════════════");
                logger.error("Error Type: DataAccessException");
                logger.error("Error Message: {}", dae.getMessage());
                logger.error("Root Cause: {}", dae.getRootCause() != null ? dae.getRootCause().getMessage() : "None");
                logger.error("Stack Trace:", dae);
                return Collections.emptyList();

            } catch (org.hibernate.exception.SQLGrammarException sqlEx) {
                logger.error("═══════════════════════════════════════════════════════");
                logger.error("💥 SQL GRAMMAR ERROR");
                logger.error("═══════════════════════════════════════════════════════");
                logger.error("SQL Error Message: {}", sqlEx.getMessage());
                logger.error("SQL State: {}", sqlEx.getSQLState());
                logger.error("Error Code: {}", sqlEx.getErrorCode());
                if (sqlEx.getSQLException() != null) {
                    logger.error("SQL Exception Detail: {}", sqlEx.getSQLException().getMessage());
                }
                logger.error("SQL Query might be malformed or table/column doesn't exist");
                logger.error("Stack Trace:", sqlEx);
                return Collections.emptyList();

            } catch (jakarta.persistence.PersistenceException pe) {
                logger.error("═══════════════════════════════════════════════════════");
                logger.error("💥 JPA PERSISTENCE ERROR");
                logger.error("═══════════════════════════════════════════════════════");
                logger.error("Persistence Error: {}", pe.getMessage());
                logger.error("Cause: {}", pe.getCause() != null ? pe.getCause().getMessage() : "None");
                logger.error("Stack Trace:", pe);
                return Collections.emptyList();

            } catch (Exception e) {
                logger.error("═══════════════════════════════════════════════════════");
                logger.error("💥 UNEXPECTED REPOSITORY ERROR");
                logger.error("═══════════════════════════════════════════════════════");
                logger.error("Error Type: {}", e.getClass().getName());
                logger.error("Error Message: {}", e.getMessage());
                logger.error("Stack Trace:", e);
                return Collections.emptyList();
            }

            // ============ PHASE 3: RESULT VALIDATION ============
            logger.info("───────────────────────────────────────────────────────");
            logger.info("📦 Processing Results");
            logger.info("───────────────────────────────────────────────────────");

            if (inventories == null) {
                logger.warn("⚠️ Repository returned NULL (should not happen)");
                logger.warn("Returning empty list to prevent NullPointerException");
                return Collections.emptyList();
            }

            // ============ PHASE 4: SUCCESS SUMMARY ============
            logger.info("═══════════════════════════════════════════════════════");
            logger.info("✅ INVENTORY SEARCH COMPLETED SUCCESSFULLY");
            logger.info("═══════════════════════════════════════════════════════");
            logger.info("📊 Results Summary:");
            logger.info("   - Farmer ID: {}", farmerId);
            logger.info("   - Items Found: {}", inventories.size());

            if (inventories.isEmpty()) {
                logger.info("   - Status: No inventories found (this is OK)");
            } else {
                logger.info("   - Status: Inventories retrieved successfully");
                logger.info("   - Sample IDs: {}",
                        inventories.stream()
                                .limit(3)
                                .map(Inventory::getId)
                                .collect(java.util.stream.Collectors.joining(", ")));
            }
            logger.info("═══════════════════════════════════════════════════════");

            return inventories;

        } catch (Exception e) {
            // ============ PHASE 5: CATASTROPHIC ERROR ============
            logger.error("═══════════════════════════════════════════════════════");
            logger.error("💀 CATASTROPHIC ERROR IN findByFarmerUserId");
            logger.error("═══════════════════════════════════════════════════════");
            logger.error("This should never happen!");
            logger.error("Farmer ID: {}", farmerId);
            logger.error("Error Type: {}", e.getClass().getName());
            logger.error("Error Message: {}", e.getMessage());
            logger.error("Full Stack Trace:", e);
            logger.error("═══════════════════════════════════════════════════════");

            return Collections.emptyList();
        }
    }




    // CRUD Operations
    public Inventory createInventory(Inventory inventory) {
        validateInventoryForCreate(inventory);

        // Ensure unique inventory code
        if (inventoryRepository.findByInventoryCode(inventory.getInventoryCode()).isPresent()) {
            throw new BusinessLogicException("Inventory code already exists: " + inventory.getInventoryCode());
        }

        // Set default values if not provided
        setDefaultValues(inventory);

        return inventoryRepository.save(inventory);
    }

    public Inventory updateInventory(String id, Inventory updatedInventory) {
        Inventory existingInventory = findById(id);
        validateInventoryForUpdate(updatedInventory, existingInventory);

        // Update fields
        updateInventoryFields(existingInventory, updatedInventory);

        return inventoryRepository.save(existingInventory);
    }

    public Inventory partialUpdateInventory(String id, Map<String, Object> updates) {
        Inventory inventory = findById(id);

        updates.forEach((field, value) -> {
            switch (field) {
                case "currentQuantity":
                    if (value instanceof Number) {
                        inventory.setCurrentQuantity(new BigDecimal(value.toString()));
                    }
                    break;
                case "reservedQuantity":
                    if (value instanceof Number) {
                        inventory.setReservedQuantity(new BigDecimal(value.toString()));
                    }
                    break;
                case "status":
                    if (value instanceof String) {
                        inventory.setStatus(InventoryStatus.valueOf((String) value));
                    }
                    break;
                case "qualityGrade":
                    if (value instanceof String) {
                        inventory.setQualityGrade((String) value);
                    }
                    break;
                case "marketValuePerUnit":
                    if (value instanceof Number) {
                        inventory.setMarketValuePerUnit(new BigDecimal(value.toString()));
                    }
                    break;
                case "pestStatus":
                    if (value instanceof String) {
                        inventory.setPestStatus(PestStatus.valueOf((String) value));
                    }
                    break;
                case "moistureContent":
                    if (value instanceof Number) {
                        inventory.setMoistureContent(new BigDecimal(value.toString()));
                    }
                    break;
                case "lossPercentage":
                    if (value instanceof Number) {
                        inventory.setLossPercentage(new BigDecimal(value.toString()));
                        calculateLossValue(inventory);
                    }
                    break;
                // Add more fields as needed
            }
        });

        return inventoryRepository.save(inventory);
    }

    @Transactional(readOnly = true)
    public Inventory findById(String id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Inventory> findByInventoryCode(String inventoryCode) {
        return inventoryRepository.findByInventoryCode(inventoryCode);
    }

    @Transactional(readOnly = true)
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Inventory> findAll(Pageable pageable) {
        return inventoryRepository.findAll(pageable);
    }

    public void deleteInventory(String id) {
        Inventory inventory = findById(id);

        // Business rule: Can't delete inventory that's reserved or in transit
        if (inventory.getStatus() == InventoryStatus.RESERVED ||
                inventory.getStatus() == InventoryStatus.IN_TRANSIT) {
            throw new BusinessLogicException("Cannot delete inventory that is reserved or in transit");
        }

        inventoryRepository.deleteById(id);
    }





    /**
     * Search inventories with advanced criteria
     * FIXED: Properly handles both String and Enum types for facilityType and status
     *
     * @param criteria Search criteria
     * @param pageable Pagination parameters
     * @return Page of matching inventories
     */
    @Transactional(readOnly = true)
    public Page<Inventory> searchInventory(InventorySearchCriteria criteria, Pageable pageable) {
        logger.info("🔍 Starting inventory search with criteria: {}", criteria);

        try {
            // Validate criteria
            if (criteria == null) {
                logger.warn("Search criteria is null, returning all inventories");
                return inventoryRepository.findAll(pageable);
            }

            // Validate date ranges
            if (!criteria.isDateRangeValid()) {
                throw new BusinessLogicException("Invalid date range in search criteria");
            }

            if (!criteria.isQuantityRangeValid()) {
                throw new BusinessLogicException("Invalid quantity range in search criteria");
            }

            if (!criteria.isValueRangeValid()) {
                throw new BusinessLogicException("Invalid value range in search criteria");
            }

            // ============ FIXED: Use smart getters that convert String to Enum ============
            FacilityType facilityType = criteria.getFacilityType(); // Auto-converts from String if needed
            InventoryStatus status = criteria.getStatus();           // Auto-converts from String if needed

            logger.debug("📊 Search parameters:");
            logger.debug("   - Crop ID: {}", criteria.getCropId());
            logger.debug("   - Farmer ID: {}", criteria.getFarmerId());
            logger.debug("   - Facility Type: {} (converted from String if needed)", facilityType);
            logger.debug("   - Status: {} (converted from String if needed)", status);
            logger.debug("   - Quality Grade: {}", criteria.getQualityGrade());

            // Call repository method with ENUM parameters (not Strings)
            Page<Inventory> results = inventoryRepository.findInventoryWithFilters(
                    criteria.getCropId(),
                    criteria.getFarmerId(),
                    facilityType,              // ENUM (can be null)
                    status,                    // ENUM (can be null)
                    criteria.getQualityGrade(),
                    criteria.getMinQuantity(),
                    criteria.getMaxQuantity(),
                    pageable
            );

            logger.info("✅ Search completed. Found {} results", results.getTotalElements());
            return results;

        } catch (BusinessLogicException e) {
            logger.error("❌ Business logic error in search: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("❌ Unexpected error in search: {}", e.getMessage(), e);
            throw new BusinessLogicException("Error performing inventory search: " + e.getMessage());
        }
    }





    @Transactional(readOnly = true)
    public List<Inventory> searchByKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptyList();
        }
        return inventoryRepository.searchInventory(keyword);
    }

    @Transactional(readOnly = true)
    public List<Inventory> findByCropId(String cropId) {
        return inventoryRepository.findByCropId(cropId);
    }

    @Transactional(readOnly = true)
    public List<Inventory> findByFarmerId(String farmerId) {
        return inventoryRepository.findByFarmerUserId(farmerId); // CHANGÉ
    }

    @Transactional(readOnly = true)
    public List<Inventory> findByStatus(InventoryStatus status) {
        return inventoryRepository.findByStatus(status);
    }

    // Inventory Management Operations


    public Inventory reserveInventory(String id, BigDecimal quantity, String buyerId) {
        Inventory inventory = findById(id);

        if (inventory.getStatus() != InventoryStatus.AVAILABLE) {
            throw new BusinessLogicException("Inventory is not available for reservation");
        }

        if (inventory.getAvailableQuantity().compareTo(quantity) < 0) {
            throw new BusinessLogicException("Insufficient available quantity. Available: " +
                    inventory.getAvailableQuantity() + ", Requested: " + quantity);
        }

        BigDecimal currentReserved = inventory.getReservedQuantity() != null ?
                inventory.getReservedQuantity() : BigDecimal.ZERO;
        inventory.setReservedQuantity(currentReserved.add(quantity));
        inventory.setBuyerUserId(buyerId); // CHANGÉ: setBuyerId -> setBuyerUserId

        if (inventory.getAvailableQuantity().equals(inventory.getReservedQuantity())) {
            inventory.setStatus(InventoryStatus.RESERVED);
        }

        return inventoryRepository.save(inventory);
    }



    public Inventory releaseReservation(String id, BigDecimal quantity) {
        Inventory inventory = findById(id);

        BigDecimal currentReserved = inventory.getReservedQuantity() != null ?
                inventory.getReservedQuantity() : BigDecimal.ZERO;

        if (currentReserved.compareTo(quantity) < 0) {
            throw new BusinessLogicException("Cannot release more than reserved quantity");
        }

        inventory.setReservedQuantity(currentReserved.subtract(quantity));

        if (inventory.getReservedQuantity().equals(BigDecimal.ZERO)) {
            inventory.setStatus(InventoryStatus.AVAILABLE);
            inventory.setBuyerUserId(null); // CHANGÉ: setBuyerId -> setBuyerUserId
        }

        return inventoryRepository.save(inventory);
    }

    public Inventory adjustQuantity(String id, BigDecimal adjustment, String reason) {
        Inventory inventory = findById(id);

        BigDecimal newQuantity = inventory.getCurrentQuantity().add(adjustment);
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessLogicException("Adjustment would result in negative quantity");
        }

        inventory.setCurrentQuantity(newQuantity);

        // Log the adjustment in movement history if needed
        if (StringUtils.hasText(reason)) {
            updateMovementHistory(inventory, "Quantity adjustment: " + adjustment + " - " + reason);
        }

        return inventoryRepository.save(inventory);
    }

    public Inventory transferInventory(String id, String newStorageLocation,
                                       FacilityType newFacilityType, String reason) {
        Inventory inventory = findById(id);

        String oldLocation = inventory.getStorageLocation();
        FacilityType oldFacilityType = inventory.getFacilityType();

        inventory.setStorageLocation(newStorageLocation);
        inventory.setFacilityType(newFacilityType);
        inventory.setStatus(InventoryStatus.IN_TRANSIT);
        inventory.setLastMovementDate(LocalDateTime.now());

        updateMovementHistory(inventory,
                String.format("Transfer from %s (%s) to %s (%s) - %s",
                        oldLocation, oldFacilityType, newStorageLocation, newFacilityType, reason));

        return inventoryRepository.save(inventory);
    }

    public Inventory completeTransfer(String id) {
        Inventory inventory = findById(id);

        if (inventory.getStatus() != InventoryStatus.IN_TRANSIT) {
            throw new BusinessLogicException("Inventory is not in transit");
        }

        inventory.setStatus(InventoryStatus.AVAILABLE);
        inventory.setLastMovementDate(LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    public Inventory markAsSold(String id, BigDecimal soldQuantity, BigDecimal soldPrice) {
        Inventory inventory = findById(id);

        if (inventory.getCurrentQuantity().compareTo(soldQuantity) < 0) {
            throw new BusinessLogicException("Cannot sell more than current quantity");
        }

        if (inventory.getCurrentQuantity().equals(soldQuantity)) {
            inventory.setStatus(InventoryStatus.SOLD);
            inventory.setCurrentQuantity(BigDecimal.ZERO);
            inventory.setAvailableQuantity(BigDecimal.ZERO);
            inventory.setReservedQuantity(BigDecimal.ZERO);
        } else {
            inventory.setCurrentQuantity(inventory.getCurrentQuantity().subtract(soldQuantity));
        }

        inventory.setMarketValuePerUnit(soldPrice);
        inventory.setLastMovementDate(LocalDateTime.now());

        updateMovementHistory(inventory, "Sold " + soldQuantity + " units at " + soldPrice + " per unit");

        return inventoryRepository.save(inventory);
    }

    // Quality Management Operations
    public Inventory updateQualityAssessment(String id, String qualityGrade,
                                             BigDecimal moistureContent, String qualityTests) {
        Inventory inventory = findById(id);

        inventory.setQualityGrade(qualityGrade);
        inventory.setMoistureContent(moistureContent);
        inventory.setQualityTests(qualityTests);
        inventory.setConditionAssessment(LocalDate.now());

        // Set next inspection date
        inventory.setNextInspectionDate(LocalDate.now().plusMonths(1));

        return inventoryRepository.save(inventory);
    }

    public Inventory updatePestInspection(String id, PestStatus pestStatus, String treatmentApplied) {
        Inventory inventory = findById(id);

        inventory.setPestStatus(pestStatus);
        inventory.setPestInspectionDate(LocalDate.now());

        if (StringUtils.hasText(treatmentApplied)) {
            inventory.setTreatmentApplied(treatmentApplied);
        }

        // Update status if pest issues detected
        if (pestStatus == PestStatus.MAJOR_INFESTATION &&
                inventory.getStatus() == InventoryStatus.AVAILABLE) {
            inventory.setStatus(InventoryStatus.DAMAGED);
        }

        return inventoryRepository.save(inventory);
    }

    public Inventory recordLoss(String id, BigDecimal lossQuantity, String lossReason) {
        Inventory inventory = findById(id);

        if (inventory.getCurrentQuantity().compareTo(lossQuantity) < 0) {
            throw new BusinessLogicException("Loss quantity cannot exceed current quantity");
        }

        inventory.setCurrentQuantity(inventory.getCurrentQuantity().subtract(lossQuantity));

        // Calculate loss percentage
        if (inventory.getCurrentQuantity().add(lossQuantity).compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal lossPercentage = lossQuantity
                    .divide(inventory.getCurrentQuantity().add(lossQuantity), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            inventory.setLossPercentage(lossPercentage);
        }

        inventory.setLossReasons(lossReason);
        calculateLossValue(inventory);

        return inventoryRepository.save(inventory);
    }

    // Analytics and Reporting
    @Transactional(readOnly = true)
    public InventoryStatistics getInventoryStatistics() {
        InventoryStatistics stats = new InventoryStatistics();

        List<Inventory> allInventory = inventoryRepository.findAll();

        stats.setTotalItems(allInventory.size());
        stats.setAvailableItems((int) allInventory.stream()
                .filter(i -> i.getStatus() == InventoryStatus.AVAILABLE).count());
        stats.setReservedItems((int) allInventory.stream()
                .filter(i -> i.getStatus() == InventoryStatus.RESERVED).count());

        BigDecimal totalValue = inventoryRepository.getTotalInventoryValue();
        stats.setTotalInventoryValue(totalValue != null ? totalValue : BigDecimal.ZERO);

        BigDecimal totalQuantity = inventoryRepository.getTotalAvailableQuantity();
        stats.setTotalQuantity(totalQuantity != null ? totalQuantity : BigDecimal.ZERO);

        Double avgStorageDays = inventoryRepository.getAverageStorageDays();
        stats.setAverageStorageDays(avgStorageDays != null ? avgStorageDays : 0.0);

        // Calculate additional statistics
        calculateAdvancedStatistics(stats, allInventory);

        return stats;
    }

    @Transactional(readOnly = true)
    public List<InventoryAlert> getInventoryAlerts() {
        List<InventoryAlert> alerts = new ArrayList<>();

        // Expiring soon
        LocalDate alertDate = LocalDate.now().plusDays(7);
        List<Inventory> expiringSoon = inventoryRepository.findExpiringSoon(alertDate);
        expiringSoon.forEach(inventory -> {
            InventoryAlert alert = new InventoryAlert(
                    inventory.getId(),
                    inventory.getInventoryCode(),
                    Inventory.InventoryAlert.EXPIRING_SOON.getDisplayName(),
                    "Expires on " + inventory.getExpiryDate(),
                    InventoryAlert.AlertSeverity.HIGH,
                    InventoryAlert.AlertCategory.EXPIRY
            );
            alert.setRecommendedAction("Consider immediate sale or processing");
            alert.setDetails("Item expires in " + inventory.getRemainingShelfLife());
            alert.generateAlertId();
            alerts.add(alert);
        });

        // Low stock items
        List<Inventory> lowStockItems = inventoryRepository.findLowStockItems();
        lowStockItems.forEach(inventory -> {
            InventoryAlert alert = new InventoryAlert(
                    inventory.getId(),
                    inventory.getInventoryCode(),
                    Inventory.InventoryAlert.LOW_STOCK.getDisplayName(),
                    "Current: " + inventory.getAvailableQuantity() + ", Min: " + inventory.getMinimumStockLevel(),
                    InventoryAlert.AlertSeverity.MEDIUM,
                    InventoryAlert.AlertCategory.QUANTITY
            );
            alert.setRecommendedAction("Reorder stock or adjust minimum levels");
            alert.generateAlertId();
            alerts.add(alert);
        });

        // High loss items
        List<Inventory> highLossItems = inventoryRepository.findHighLossInventory(new BigDecimal("5"));
        highLossItems.forEach(inventory -> {
            InventoryAlert alert = new InventoryAlert(
                    inventory.getId(),
                    inventory.getInventoryCode(),
                    Inventory.InventoryAlert.HIGH_LOSS.getDisplayName(),
                    "Loss: " + inventory.getLossPercentage() + "%",
                    InventoryAlert.AlertSeverity.HIGH,
                    InventoryAlert.AlertCategory.VALUE
            );
            alert.setRecommendedAction("Investigate loss causes and implement preventive measures");
            alert.setDetails("Loss value: " + inventory.getLossValue() + " RWF");
            alert.generateAlertId();
            alerts.add(alert);
        });

        // Pest issues
        List<Inventory> pestIssues = inventoryRepository.findInventoryWithPestIssues();
        pestIssues.forEach(inventory -> {
            InventoryAlert.AlertSeverity severity = inventory.getPestStatus() == PestStatus.MAJOR_INFESTATION ?
                    InventoryAlert.AlertSeverity.CRITICAL : InventoryAlert.AlertSeverity.HIGH;

            InventoryAlert alert = new InventoryAlert(
                    inventory.getId(),
                    inventory.getInventoryCode(),
                    Inventory.InventoryAlert.PEST_DETECTED.getDisplayName(),
                    "Pest status: " + inventory.getPestStatus().getDisplayName(),
                    severity,
                    InventoryAlert.AlertCategory.PEST
            );
            alert.setRecommendedAction("Apply appropriate pest treatment immediately");
            alert.setDetails("Last inspection: " + inventory.getPestInspectionDate());
            alert.generateAlertId();
            alerts.add(alert);
        });

        // Items requiring inspection
        List<Inventory> needsInspection = inventoryRepository.findItemsRequiringInspection();
        needsInspection.forEach(inventory -> {
            InventoryAlert alert = new InventoryAlert(
                    inventory.getId(),
                    inventory.getInventoryCode(),
                    Inventory.InventoryAlert.QUALITY_DEGRADING.getDisplayName(),
                    "Inspection due: " + inventory.getNextInspectionDate(),
                    InventoryAlert.AlertSeverity.MEDIUM,
                    InventoryAlert.AlertCategory.QUALITY
            );
            alert.setRecommendedAction("Schedule quality inspection");
            alert.setDetails("Days overdue: " + ChronoUnit.DAYS.between(inventory.getNextInspectionDate(), LocalDate.now()));
            alert.generateAlertId();
            alerts.add(alert);
        });

        return alerts;
    }

    @Transactional(readOnly = true)
    public List<Object[]> getInventoryTrendsByDate(LocalDate startDate) {
        return inventoryRepository.getInventoryTrendsByDate(startDate);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getInventoryCountByFacilityType() {
        return inventoryRepository.getInventoryCountByFacilityType();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getInventoryCountByQualityGrade() {
        return inventoryRepository.getInventoryCountByQualityGrade();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getInventorySummaryByCrop() {
        return inventoryRepository.getInventorySummaryByCrop();
    }

    // Bulk Operations
    public List<Inventory> bulkUpdateStatus(List<String> inventoryIds, InventoryStatus newStatus) {
        List<Inventory> inventories = inventoryRepository.findAllById(inventoryIds);

        inventories.forEach(inventory -> {
            validateStatusTransition(inventory.getStatus(), newStatus);
            inventory.setStatus(newStatus);
        });

        return inventoryRepository.saveAll(inventories);
    }

    public void bulkDelete(List<String> inventoryIds) {
        List<Inventory> inventories = inventoryRepository.findAllById(inventoryIds);

        // Validate each inventory can be deleted
        inventories.forEach(inventory -> {
            if (inventory.getStatus() == InventoryStatus.RESERVED ||
                    inventory.getStatus() == InventoryStatus.IN_TRANSIT) {
                throw new BusinessLogicException("Cannot delete inventory with ID " + inventory.getId() +
                        " - Status: " + inventory.getStatus());
            }
        });

        inventoryRepository.deleteAllById(inventoryIds);
    }

    // Automated Operations
    @Transactional
    public void processExpiredInventory() {
        List<Inventory> expiredItems = inventoryRepository.findExpiredInventory();

        expiredItems.forEach(inventory -> {
            inventory.setStatus(InventoryStatus.EXPIRED);
            updateMovementHistory(inventory, "Automatically marked as expired");
        });

        inventoryRepository.saveAll(expiredItems);
    }

    @Transactional
    public void updateInventoryValues() {
        // This would typically integrate with market price APIs
        // For now, we'll update based on internal logic

        List<Inventory> inventories = inventoryRepository.findByStatus(InventoryStatus.AVAILABLE);

        inventories.forEach(inventory -> {
            // Recalculate total market value
            if (inventory.getMarketValuePerUnit() != null && inventory.getCurrentQuantity() != null) {
                BigDecimal totalValue = inventory.getMarketValuePerUnit()
                        .multiply(inventory.getCurrentQuantity())
                        .setScale(2, RoundingMode.HALF_UP);
                inventory.setTotalMarketValue(totalValue);
            }

            // Update profit margin
            if (inventory.getMarketValuePerUnit() != null &&
                    inventory.getPurchasePricePerUnit() != null &&
                    inventory.getPurchasePricePerUnit().compareTo(BigDecimal.ZERO) > 0) {

                BigDecimal profit = inventory.getMarketValuePerUnit()
                        .subtract(inventory.getPurchasePricePerUnit());
                BigDecimal margin = profit.divide(inventory.getPurchasePricePerUnit(), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .setScale(2, RoundingMode.HALF_UP);
                inventory.setProfitMargin(margin);
            }
        });

        inventoryRepository.saveAll(inventories);
    }

    // Private helper methods
    private void validateInventoryForCreate(Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory cannot be null");
        }

        if (!StringUtils.hasText(inventory.getCropId())) {
            throw new IllegalArgumentException("Crop ID is required");
        }

        if (inventory.getFacilityType() == null) {
            throw new IllegalArgumentException("Facility type is required");
        }

        if (!StringUtils.hasText(inventory.getStorageLocation())) {
            throw new IllegalArgumentException("Storage location is required");
        }

        if (inventory.getCurrentQuantity() == null ||
                inventory.getCurrentQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Current quantity must be positive");
        }

        if (!StringUtils.hasText(inventory.getQualityGrade())) {
            throw new IllegalArgumentException("Quality grade is required");
        }
    }

    private void validateInventoryForUpdate(Inventory updated, Inventory existing) {
        if (updated == null) {
            throw new IllegalArgumentException("Updated inventory cannot be null");
        }

        // Business rules for updates
        if (existing.getStatus() == InventoryStatus.SOLD &&
                updated.getCurrentQuantity() != null &&
                updated.getCurrentQuantity().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessLogicException("Cannot update quantity of sold inventory");
        }

        if (updated.getCurrentQuantity() != null &&
                updated.getCurrentQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Current quantity cannot be negative");
        }
    }

    private void validateStatusTransition(InventoryStatus currentStatus, InventoryStatus newStatus) {
        // Define valid status transitions
        Set<InventoryStatus> validTransitions = new HashSet<>();

        switch (currentStatus) {
            case AVAILABLE:
                validTransitions.addAll(Arrays.asList(
                        InventoryStatus.RESERVED, InventoryStatus.IN_TRANSIT,
                        InventoryStatus.DAMAGED, InventoryStatus.EXPIRED));
                break;
            case RESERVED:
                validTransitions.addAll(Arrays.asList(
                        InventoryStatus.AVAILABLE, InventoryStatus.SOLD,
                        InventoryStatus.IN_TRANSIT));
                break;
            case IN_TRANSIT:
                validTransitions.addAll(Arrays.asList(
                        InventoryStatus.AVAILABLE, InventoryStatus.DAMAGED));
                break;
            case DAMAGED:
                validTransitions.addAll(Arrays.asList(
                        InventoryStatus.DISPOSED, InventoryStatus.AVAILABLE));
                break;
            case EXPIRED:
                validTransitions.add(InventoryStatus.DISPOSED);
                break;
            case SOLD:
            case DISPOSED:
                // Terminal states - no transitions allowed
                break;
        }

        if (!validTransitions.contains(newStatus)) {
            throw new BusinessLogicException(
                    String.format("Invalid status transition from %s to %s", currentStatus, newStatus));
        }
    }

    private void setDefaultValues(Inventory inventory) {
        if (inventory.getStorageDate() == null) {
            inventory.setStorageDate(LocalDate.now());
        }

        if (inventory.getStatus() == null) {
            inventory.setStatus(InventoryStatus.AVAILABLE);
        }

        if (inventory.getPackagingCondition() == null) {
            inventory.setPackagingCondition(PackagingCondition.GOOD);
        }

        if (inventory.getPestStatus() == null) {
            inventory.setPestStatus(PestStatus.PEST_FREE);
        }

        if (inventory.getUnit() == null) {
            inventory.setUnit("KG");
        }

        if (inventory.getReservedQuantity() == null) {
            inventory.setReservedQuantity(BigDecimal.ZERO);
        }

        if (inventory.getLossPercentage() == null) {
            inventory.setLossPercentage(BigDecimal.ZERO);
        }

        if (inventory.getLossValue() == null) {
            inventory.setLossValue(BigDecimal.ZERO);
        }
    }

    private void updateInventoryFields(Inventory existing, Inventory updated) {
        if (updated.getCurrentQuantity() != null) {
            existing.setCurrentQuantity(updated.getCurrentQuantity());
        }

        if (updated.getReservedQuantity() != null) {
            existing.setReservedQuantity(updated.getReservedQuantity());
        }

        if (updated.getQualityGrade() != null) {
            existing.setQualityGrade(updated.getQualityGrade());
        }

        if (updated.getMarketValuePerUnit() != null) {
            existing.setMarketValuePerUnit(updated.getMarketValuePerUnit());
        }

        if (updated.getStorageLocation() != null) {
            existing.setStorageLocation(updated.getStorageLocation());
        }

        if (updated.getFacilityType() != null) {
            existing.setFacilityType(updated.getFacilityType());
        }

        if (updated.getStatus() != null) {
            validateStatusTransition(existing.getStatus(), updated.getStatus());
            existing.setStatus(updated.getStatus());
        }

        if (updated.getPestStatus() != null) {
            existing.setPestStatus(updated.getPestStatus());
        }

        if (updated.getMoistureContent() != null) {
            existing.setMoistureContent(updated.getMoistureContent());
        }

        // Update other fields as needed...
    }

    private void updateMovementHistory(Inventory inventory, String description) {
        String currentHistory = inventory.getMovementHistory();
        String timestamp = LocalDateTime.now().toString();
        String newEntry = timestamp + ": " + description;

        if (StringUtils.hasText(currentHistory)) {
            inventory.setMovementHistory(currentHistory + "\n" + newEntry);
        } else {
            inventory.setMovementHistory(newEntry);
        }
    }

    private void calculateLossValue(Inventory inventory) {
        if (inventory.getLossPercentage() != null &&
                inventory.getTotalMarketValue() != null) {
            BigDecimal lossValue = inventory.getTotalMarketValue()
                    .multiply(inventory.getLossPercentage())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            inventory.setLossValue(lossValue);
        }
    }

    private void calculateAdvancedStatistics(InventoryStatistics stats, List<Inventory> allInventory) {
        // Calculate high-value items count
        long highValueCount = allInventory.stream()
                .filter(i -> i.getTotalMarketValue() != null &&
                        i.getTotalMarketValue().compareTo(new BigDecimal("100000")) >= 0)
                .count();
        stats.setHighValueItems((int) highValueCount);

        // Calculate expired items count
        long expiredCount = allInventory.stream()
                .filter(i -> i.getStatus() == InventoryStatus.EXPIRED)
                .count();
        stats.setExpiredItems((int) expiredCount);

        // Calculate damaged items count
        long damagedCount = allInventory.stream()
                .filter(i -> i.getStatus() == InventoryStatus.DAMAGED)
                .count();
        stats.setDamagedItems((int) damagedCount);

        // Calculate sustainable items count
        long sustainableCount = allInventory.stream()
                .filter(Inventory::isSustainable)
                .count();
        stats.setSustainableItems((int) sustainableCount);
    }
}