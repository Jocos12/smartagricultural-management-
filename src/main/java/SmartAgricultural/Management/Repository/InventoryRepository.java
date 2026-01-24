package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.Inventory;
import SmartAgricultural.Management.Model.Inventory.FacilityType;
import SmartAgricultural.Management.Model.Inventory.InventoryStatus;
import SmartAgricultural.Management.Model.Inventory.PestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {

    // ✅ CORRECTION CRITIQUE - Utiliser nativeQuery pour être 100% sûr

    Optional<Inventory> findByInventoryCode(String inventoryCode);

    @Query("SELECT i FROM Inventory i WHERE i.cropId = :cropId")
    List<Inventory> findByCropId(@Param("cropId") String cropId);





    /**
     * ✅ CORRECTION CRITIQUE - Query native SQL directe
     */
    /**
     * ✅ QUERY NATIVE CORRIGÉE - Utiliser le bon nom de colonne
     */
    @Query(value = "SELECT * FROM inventories WHERE farmer_user_id = :farmerUserId",
            nativeQuery = true)
    List<Inventory> findByFarmerUserId(@Param("farmerUserId") String farmerUserId);

    /**
     * ✅ CORRECTION - Query native pour buyer
     */
    @Query(value = "SELECT * FROM inventories WHERE buyer_user_id = :buyerUserId",
            nativeQuery = true)
    List<Inventory> findByBuyerUserId(@Param("buyerUserId") String buyerUserId);

    @Query("SELECT i FROM Inventory i WHERE i.facilityType = :facilityType")
    List<Inventory> findByFacilityType(@Param("facilityType") FacilityType facilityType);

    @Query("SELECT i FROM Inventory i WHERE i.status = :status")
    List<Inventory> findByStatus(@Param("status") InventoryStatus status);

    @Query("SELECT i FROM Inventory i WHERE i.qualityGrade = :qualityGrade")
    List<Inventory> findByQualityGrade(@Param("qualityGrade") String qualityGrade);

    // Status-based queries
    @Query("SELECT i FROM Inventory i WHERE i.status IN :statuses")
    List<Inventory> findByStatusIn(@Param("statuses") List<InventoryStatus> statuses);

    @Query("SELECT i FROM Inventory i WHERE i.status = 'AVAILABLE' AND i.availableQuantity > 0")
    List<Inventory> findAvailableInventory();

    @Query("SELECT i FROM Inventory i WHERE i.status = 'AVAILABLE' AND i.availableQuantity >= :minQuantity")
    List<Inventory> findAvailableInventoryWithMinQuantity(@Param("minQuantity") BigDecimal minQuantity);

    // Date-based queries
    @Query("SELECT i FROM Inventory i WHERE i.storageDate BETWEEN :startDate AND :endDate")
    List<Inventory> findByStorageDateBetween(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    @Query("SELECT i FROM Inventory i WHERE i.harvestDate BETWEEN :startDate AND :endDate")
    List<Inventory> findByHarvestDateBetween(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    @Query("SELECT i FROM Inventory i WHERE i.expiryDate BETWEEN :startDate AND :endDate")
    List<Inventory> findByExpiryDateBetween(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    @Query("SELECT i FROM Inventory i WHERE i.expiryDate <= :date AND i.status = 'AVAILABLE'")
    List<Inventory> findExpiringSoon(@Param("date") LocalDate date);

    @Query("SELECT i FROM Inventory i WHERE i.expiryDate < CURRENT_DATE AND i.status != 'EXPIRED'")
    List<Inventory> findExpiredInventory();

    // Quantity-based queries
    @Query("SELECT i FROM Inventory i WHERE i.availableQuantity <= i.minimumStockLevel AND i.minimumStockLevel IS NOT NULL")
    List<Inventory> findLowStockItems();

    @Query("SELECT i FROM Inventory i WHERE i.currentQuantity >= i.maximumStockLevel AND i.maximumStockLevel IS NOT NULL")
    List<Inventory> findOverstockItems();

    @Query("SELECT i FROM Inventory i WHERE i.currentQuantity BETWEEN :minQuantity AND :maxQuantity")
    List<Inventory> findByQuantityRange(@Param("minQuantity") BigDecimal minQuantity,
                                        @Param("maxQuantity") BigDecimal maxQuantity);

    // Value-based queries
    @Query("SELECT i FROM Inventory i WHERE i.totalMarketValue >= :minValue")
    List<Inventory> findHighValueInventory(@Param("minValue") BigDecimal minValue);

    @Query("SELECT i FROM Inventory i WHERE i.totalMarketValue BETWEEN :minValue AND :maxValue")
    List<Inventory> findByValueRange(@Param("minValue") BigDecimal minValue,
                                     @Param("maxValue") BigDecimal maxValue);

    @Query("SELECT i FROM Inventory i WHERE i.profitMargin >= :minMargin AND i.profitMargin IS NOT NULL")
    List<Inventory> findProfitableInventory(@Param("minMargin") BigDecimal minMargin);

    // Location-based queries
    @Query("SELECT i FROM Inventory i WHERE i.storageLocation = :storageLocation")
    List<Inventory> findByStorageLocation(@Param("storageLocation") String storageLocation);

    @Query("SELECT i FROM Inventory i WHERE LOWER(i.storageLocation) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Inventory> findByStorageLocationContainingIgnoreCase(@Param("location") String location);

    @Query("SELECT i FROM Inventory i WHERE i.facilityName = :facilityName")
    List<Inventory> findByFacilityName(@Param("facilityName") String facilityName);

    @Query("SELECT i FROM Inventory i WHERE i.facilityOwner = :facilityOwner")
    List<Inventory> findByFacilityOwner(@Param("facilityOwner") String facilityOwner);

    // Quality and condition queries
    @Query("SELECT i FROM Inventory i WHERE i.pestStatus = :pestStatus")
    List<Inventory> findByPestStatus(@Param("pestStatus") PestStatus pestStatus);

    @Query("SELECT i FROM Inventory i WHERE i.pestStatus != 'PEST_FREE'")
    List<Inventory> findInventoryWithPestIssues();

    @Query("SELECT i FROM Inventory i WHERE i.moistureContent > :maxMoisture")
    List<Inventory> findHighMoistureInventory(@Param("maxMoisture") BigDecimal maxMoisture);

    @Query("SELECT i FROM Inventory i WHERE i.lossPercentage >= :threshold")
    List<Inventory> findHighLossInventory(@Param("threshold") BigDecimal threshold);

    // Inspection and maintenance queries
    @Query("SELECT i FROM Inventory i WHERE i.nextInspectionDate <= CURRENT_DATE")
    List<Inventory> findItemsRequiringInspection();

    @Query("SELECT i FROM Inventory i WHERE i.conditionAssessment < :date OR i.conditionAssessment IS NULL")
    List<Inventory> findItemsNeedingConditionAssessment(@Param("date") LocalDate date);

    // Sustainability queries
    @Query("SELECT i FROM Inventory i WHERE i.organicCertified = true")
    List<Inventory> findOrganicInventory();

    @Query("SELECT i FROM Inventory i WHERE i.fairTradeCertified = true")
    List<Inventory> findFairTradeInventory();

    @Query("SELECT i FROM Inventory i WHERE i.localSourcing = true")
    List<Inventory> findLocallySourcedInventory();

    @Query("SELECT i FROM Inventory i WHERE i.organicCertified = true OR i.fairTradeCertified = true OR i.localSourcing = true")
    List<Inventory> findSustainableInventory();

    // Statistical queries
    @Query("SELECT SUM(i.currentQuantity) FROM Inventory i WHERE i.status = 'AVAILABLE'")
    BigDecimal getTotalAvailableQuantity();

    @Query("SELECT SUM(i.totalMarketValue) FROM Inventory i WHERE i.status = 'AVAILABLE'")
    BigDecimal getTotalInventoryValue();

    @Query("SELECT AVG(i.daysInStorage) FROM Inventory i WHERE i.daysInStorage IS NOT NULL")
    Double getAverageStorageDays();

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.status = :status")
    Long countByStatus(@Param("status") InventoryStatus status);

    @Query("SELECT i.facilityType, COUNT(i) FROM Inventory i GROUP BY i.facilityType")
    List<Object[]> getInventoryCountByFacilityType();

    @Query("SELECT i.qualityGrade, COUNT(i) FROM Inventory i GROUP BY i.qualityGrade")
    List<Object[]> getInventoryCountByQualityGrade();

    // Performance queries
    @Query("SELECT i FROM Inventory i WHERE i.inventoryTurnRate >= :minRate AND i.inventoryTurnRate IS NOT NULL")
    List<Inventory> findFastMovingInventory(@Param("minRate") BigDecimal minRate);

    @Query("SELECT i FROM Inventory i WHERE i.inventoryTurnRate < :maxRate AND i.inventoryTurnRate IS NOT NULL")
    List<Inventory> findSlowMovingInventory(@Param("maxRate") BigDecimal maxRate);

    @Query("SELECT i FROM Inventory i WHERE " +
            "(:cropId IS NULL OR i.cropId = :cropId) AND " +
            "(:farmerId IS NULL OR i.farmerUserId = :farmerId) AND " +
            "(:facilityType IS NULL OR i.facilityType = :facilityType) AND " +
            "(:status IS NULL OR i.status = :status) AND " +
            "(:qualityGrade IS NULL OR i.qualityGrade = :qualityGrade) AND " +
            "(:minQuantity IS NULL OR i.currentQuantity >= :minQuantity) AND " +
            "(:maxQuantity IS NULL OR i.currentQuantity <= :maxQuantity)")
    Page<Inventory> findInventoryWithFilters(
            @Param("cropId") String cropId,
            @Param("farmerId") String farmerId,
            @Param("facilityType") FacilityType facilityType,    // ✅ ENUM (pas String)
            @Param("status") InventoryStatus status,              // ✅ ENUM (pas String)
            @Param("qualityGrade") String qualityGrade,
            @Param("minQuantity") BigDecimal minQuantity,
            @Param("maxQuantity") BigDecimal maxQuantity,
            Pageable pageable
    );

    // Full-text search
    @Query("SELECT i FROM Inventory i WHERE " +
            "LOWER(i.inventoryCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.storageLocation) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.facilityName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.qualityGrade) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Inventory> searchInventory(@Param("searchTerm") String searchTerm);

    // Recent activity queries
    @Query("SELECT i FROM Inventory i WHERE i.lastMovementDate >= :date")
    List<Inventory> findRecentlyMovedInventory(@Param("date") LocalDateTime date);

    @Query("SELECT i FROM Inventory i WHERE i.createdDate >= :date")
    List<Inventory> findRecentlyCreatedInventory(@Param("date") LocalDateTime date);

    @Query("SELECT i FROM Inventory i WHERE i.lastUpdated >= :date")
    List<Inventory> findRecentlyUpdatedInventory(@Param("date") LocalDateTime date);

    // Batch operations
    @Query("SELECT i FROM Inventory i WHERE i.batchTraceabilityCode = :batchCode")
    List<Inventory> findByBatchCode(@Param("batchCode") String batchCode);

    @Query("SELECT i FROM Inventory i WHERE i.sourceFarmCodes LIKE CONCAT('%', :farmCode, '%')")
    List<Inventory> findBySourceFarmCode(@Param("farmCode") String farmCode);

    // Insurance and coverage
    @Query("SELECT i FROM Inventory i WHERE i.insuranceCoverage = true AND i.totalMarketValue >= :minValue")
    List<Inventory> findInsuredHighValueInventory(@Param("minValue") BigDecimal minValue);

    @Query("SELECT i FROM Inventory i WHERE i.insuranceCoverage = false AND i.totalMarketValue >= :minValue")
    List<Inventory> findUninsuredHighValueInventory(@Param("minValue") BigDecimal minValue);

    // Supplier and reliability
    @Query("SELECT i FROM Inventory i WHERE i.supplierReliability >= :minRating AND i.supplierReliability IS NOT NULL")
    List<Inventory> findInventoryFromReliableSuppliers(@Param("minRating") BigDecimal minRating);

    // ✅ CORRECTION - Custom aggregation queries
    @Query("SELECT i.cropId, SUM(i.currentQuantity), AVG(i.marketValuePerUnit) FROM Inventory i " +
            "WHERE i.status = 'AVAILABLE' GROUP BY i.cropId")
    List<Object[]> getInventorySummaryByCrop();

    @Query(value = "SELECT farmer_user_id, COUNT(*), SUM(total_market_value) FROM inventories " +
            "GROUP BY farmer_user_id HAVING COUNT(*) > 0",
            nativeQuery = true)
    List<Object[]> getInventoryStatsByFarmer();

    @Query(value = "SELECT DATE(storage_date), COUNT(*), SUM(current_quantity) FROM inventories " +
            "WHERE storage_date >= :startDate GROUP BY DATE(storage_date) ORDER BY DATE(storage_date)",
            nativeQuery = true)
    List<Object[]> getInventoryTrendsByDate(@Param("startDate") LocalDate startDate);

    // Alert-based queries
    @Query("SELECT i FROM Inventory i WHERE " +
            "(i.expiryDate <= :alertDate AND i.status = 'AVAILABLE') OR " +
            "(i.availableQuantity <= i.minimumStockLevel AND i.minimumStockLevel IS NOT NULL) OR " +
            "(i.lossPercentage >= 5.0) OR " +
            "(i.pestStatus != 'PEST_FREE') OR " +
            "(i.nextInspectionDate <= CURRENT_DATE)")
    List<Inventory> findInventoryRequiringAttention(@Param("alertDate") LocalDate alertDate);

    // Top performers
    @Query("SELECT i FROM Inventory i WHERE i.profitMargin IS NOT NULL ORDER BY i.profitMargin DESC")
    List<Inventory> findTopProfitableInventory(Pageable pageable);

    @Query("SELECT i FROM Inventory i WHERE i.inventoryTurnRate IS NOT NULL ORDER BY i.inventoryTurnRate DESC")
    List<Inventory> findFastestMovingInventory(Pageable pageable);

    // Delete operations
    @Query("DELETE FROM Inventory i WHERE i.status = :status")
    void deleteByStatus(@Param("status") InventoryStatus status);

    @Query("DELETE FROM Inventory i WHERE i.expiryDate < :date AND i.status = 'EXPIRED'")
    void deleteExpiredInventory(@Param("date") LocalDate date);
}