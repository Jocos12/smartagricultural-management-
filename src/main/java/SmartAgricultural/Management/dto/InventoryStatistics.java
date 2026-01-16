package SmartAgricultural.Management.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO containing comprehensive inventory statistics and metrics
 * Used for dashboard analytics and reporting
 *
 * @author Smart Agricultural Management System
 * @version 1.0
 */
@Schema(description = "Comprehensive inventory statistics and metrics")
public class InventoryStatistics {

    @Schema(description = "Total number of inventory items", example = "1250")
    private int totalItems;

    @Schema(description = "Number of available items", example = "850")
    private int availableItems;

    @Schema(description = "Number of reserved items", example = "200")
    private int reservedItems;

    @Schema(description = "Number of expired items", example = "45")
    private int expiredItems;

    @Schema(description = "Number of damaged items", example = "15")
    private int damagedItems;

    @Schema(description = "Number of high value items", example = "125")
    private int highValueItems;

    @Schema(description = "Number of sustainable/certified items", example = "320")
    private int sustainableItems;

    @Schema(description = "Number of items sold", example = "140")
    private int soldItems;

    @Schema(description = "Total inventory value", example = "2450000.00")
    private BigDecimal totalInventoryValue;

    @Schema(description = "Total quantity across all items", example = "15750.50")
    private BigDecimal totalQuantity;

    @Schema(description = "Average storage days", example = "45.5")
    private Double averageStorageDays;

    @Schema(description = "Average market value per unit", example = "155.75")
    private BigDecimal averageMarketValue;

    @Schema(description = "Total loss value", example = "12500.00")
    private BigDecimal totalLossValue;

    @Schema(description = "Average profit margin percentage", example = "22.5")
    private BigDecimal averageProfitMargin;

    @Schema(description = "Total revenue from sales", example = "875000.00")
    private BigDecimal totalRevenue;

    @Schema(description = "Inventory turnover rate", example = "8.5")
    private BigDecimal inventoryTurnoverRate;

    @Schema(description = "Storage utilization percentage", example = "75.5")
    private BigDecimal storageUtilization;

    @Schema(description = "Items requiring attention count", example = "85")
    private int itemsRequiringAttention;

    @Schema(description = "Low stock items count", example = "32")
    private int lowStockItems;

    @Schema(description = "Overstock items count", example = "18")
    private int overstockItems;

    @Schema(description = "Items expiring within 7 days", example = "25")
    private int itemsExpiringSoon;

    @Schema(description = "Items with pest issues", example = "8")
    private int itemsWithPestIssues;

    @Schema(description = "Quality grade A items count", example = "450")
    private int gradeAItems;

    @Schema(description = "Quality grade B items count", example = "380")
    private int gradeBItems;

    @Schema(description = "Quality grade C items count", example = "240")
    private int gradeCItems;

    @Schema(description = "Organic certified items count", example = "180")
    private int organicItems;

    @Schema(description = "Fair trade certified items count", example = "95")
    private int fairTradeItems;

    @Schema(description = "When statistics were calculated")
    private LocalDateTime calculatedAt;

    @Schema(description = "Performance metrics summary")
    private String performanceSummary;

    // ==================== CONSTRUCTORS ====================

    /**
     * Default constructor
     */
    public InventoryStatistics() {
        this.calculatedAt = LocalDateTime.now();
    }

    // ==================== GETTERS AND SETTERS ====================

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getAvailableItems() {
        return availableItems;
    }

    public void setAvailableItems(int availableItems) {
        this.availableItems = availableItems;
    }

    public int getReservedItems() {
        return reservedItems;
    }

    public void setReservedItems(int reservedItems) {
        this.reservedItems = reservedItems;
    }

    public int getExpiredItems() {
        return expiredItems;
    }

    public void setExpiredItems(int expiredItems) {
        this.expiredItems = expiredItems;
    }

    public int getDamagedItems() {
        return damagedItems;
    }

    public void setDamagedItems(int damagedItems) {
        this.damagedItems = damagedItems;
    }

    public int getHighValueItems() {
        return highValueItems;
    }

    public void setHighValueItems(int highValueItems) {
        this.highValueItems = highValueItems;
    }

    public int getSustainableItems() {
        return sustainableItems;
    }

    public void setSustainableItems(int sustainableItems) {
        this.sustainableItems = sustainableItems;
    }

    public int getSoldItems() {
        return soldItems;
    }

    public void setSoldItems(int soldItems) {
        this.soldItems = soldItems;
    }

    public BigDecimal getTotalInventoryValue() {
        return totalInventoryValue;
    }

    public void setTotalInventoryValue(BigDecimal totalInventoryValue) {
        this.totalInventoryValue = totalInventoryValue;
    }

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(BigDecimal totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Double getAverageStorageDays() {
        return averageStorageDays;
    }

    public void setAverageStorageDays(Double averageStorageDays) {
        this.averageStorageDays = averageStorageDays;
    }

    public BigDecimal getAverageMarketValue() {
        return averageMarketValue;
    }

    public void setAverageMarketValue(BigDecimal averageMarketValue) {
        this.averageMarketValue = averageMarketValue;
    }

    public BigDecimal getTotalLossValue() {
        return totalLossValue;
    }

    public void setTotalLossValue(BigDecimal totalLossValue) {
        this.totalLossValue = totalLossValue;
    }

    public BigDecimal getAverageProfitMargin() {
        return averageProfitMargin;
    }

    public void setAverageProfitMargin(BigDecimal averageProfitMargin) {
        this.averageProfitMargin = averageProfitMargin;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getInventoryTurnoverRate() {
        return inventoryTurnoverRate;
    }

    public void setInventoryTurnoverRate(BigDecimal inventoryTurnoverRate) {
        this.inventoryTurnoverRate = inventoryTurnoverRate;
    }

    public BigDecimal getStorageUtilization() {
        return storageUtilization;
    }

    public void setStorageUtilization(BigDecimal storageUtilization) {
        this.storageUtilization = storageUtilization;
    }

    public int getItemsRequiringAttention() {
        return itemsRequiringAttention;
    }

    public void setItemsRequiringAttention(int itemsRequiringAttention) {
        this.itemsRequiringAttention = itemsRequiringAttention;
    }

    public int getLowStockItems() {
        return lowStockItems;
    }

    public void setLowStockItems(int lowStockItems) {
        this.lowStockItems = lowStockItems;
    }

    public int getOverstockItems() {
        return overstockItems;
    }

    public void setOverstockItems(int overstockItems) {
        this.overstockItems = overstockItems;
    }

    public int getItemsExpiringSoon() {
        return itemsExpiringSoon;
    }

    public void setItemsExpiringSoon(int itemsExpiringSoon) {
        this.itemsExpiringSoon = itemsExpiringSoon;
    }

    public int getItemsWithPestIssues() {
        return itemsWithPestIssues;
    }

    public void setItemsWithPestIssues(int itemsWithPestIssues) {
        this.itemsWithPestIssues = itemsWithPestIssues;
    }

    public int getGradeAItems() {
        return gradeAItems;
    }

    public void setGradeAItems(int gradeAItems) {
        this.gradeAItems = gradeAItems;
    }

    public int getGradeBItems() {
        return gradeBItems;
    }

    public void setGradeBItems(int gradeBItems) {
        this.gradeBItems = gradeBItems;
    }

    public int getGradeCItems() {
        return gradeCItems;
    }

    public void setGradeCItems(int gradeCItems) {
        this.gradeCItems = gradeCItems;
    }

    public int getOrganicItems() {
        return organicItems;
    }

    public void setOrganicItems(int organicItems) {
        this.organicItems = organicItems;
    }

    public int getFairTradeItems() {
        return fairTradeItems;
    }

    public void setFairTradeItems(int fairTradeItems) {
        this.fairTradeItems = fairTradeItems;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public String getPerformanceSummary() {
        return performanceSummary;
    }

    public void setPerformanceSummary(String performanceSummary) {
        this.performanceSummary = performanceSummary;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Calculate availability percentage
     */
    public BigDecimal getAvailabilityPercentage() {
        if (totalItems == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(availableItems * 100.0 / totalItems);
    }

    /**
     * Calculate loss percentage
     */
    public BigDecimal getLossPercentage() {
        if (totalInventoryValue == null || totalInventoryValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (totalLossValue == null) return BigDecimal.ZERO;
        return totalLossValue.multiply(BigDecimal.valueOf(100))
                .divide(totalInventoryValue, 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Calculate quality grade distribution percentage for Grade A
     */
    public BigDecimal getGradeAPercentage() {
        if (totalItems == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(gradeAItems * 100.0 / totalItems);
    }

    /**
     * Calculate sustainability percentage
     */
    public BigDecimal getSustainabilityPercentage() {
        if (totalItems == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(sustainableItems * 100.0 / totalItems);
    }

    /**
     * Generate performance summary
     */
    public void generatePerformanceSummary() {
        StringBuilder summary = new StringBuilder();

        summary.append("Total Items: ").append(totalItems);
        summary.append(", Available: ").append(getAvailabilityPercentage()).append("%");

        if (averageProfitMargin != null) {
            summary.append(", Avg Profit: ").append(averageProfitMargin).append("%");
        }

        if (inventoryTurnoverRate != null) {
            summary.append(", Turnover: ").append(inventoryTurnoverRate);
        }

        if (itemsRequiringAttention > 0) {
            summary.append(", Attention Needed: ").append(itemsRequiringAttention);
        }

        this.performanceSummary = summary.toString();
    }

    @Override
    public String toString() {
        return "InventoryStatistics{" +
                "totalItems=" + totalItems +
                ", availableItems=" + availableItems +
                ", reservedItems=" + reservedItems +
                ", totalInventoryValue=" + totalInventoryValue +
                ", averageProfitMargin=" + averageProfitMargin +
                ", itemsRequiringAttention=" + itemsRequiringAttention +
                ", calculatedAt=" + calculatedAt +
                '}';
    }
}