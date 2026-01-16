package SmartAgricultural.Management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO for inventory alert notifications
 * Used to communicate important inventory issues that require attention
 *
 * @author Smart Agricultural Management System
 * @version 1.0
 */
@Schema(description = "Inventory alert notification")
public class InventoryAlert {

    @Schema(description = "Unique alert identifier", example = "ALERT123456")
    private String alertId;

    @Schema(description = "ID of the inventory item", example = "INV123456789ABC")
    private String inventoryId;

    @Schema(description = "Inventory code for easy identification", example = "STOCK2401151234ABC")
    private String inventoryCode;

    @Schema(description = "Type of alert", example = "EXPIRING_SOON")
    private String alertType;

    @Schema(description = "Alert message describing the issue", example = "Inventory expires in 3 days")
    private String message;

    @Schema(description = "Alert severity level", example = "HIGH")
    private AlertSeverity severity;

    @Schema(description = "When the alert was created")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @Schema(description = "Whether the alert has been acknowledged", example = "false")
    private boolean acknowledged;

    @Schema(description = "Who acknowledged the alert", example = "USER001")
    private String acknowledgedBy;

    @Schema(description = "When the alert was acknowledged")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime acknowledgedDate;

    @Schema(description = "Priority level for processing", example = "1")
    private Integer priority;

    @Schema(description = "Category of the alert", example = "QUALITY")
    private AlertCategory category;

    @Schema(description = "Recommended action", example = "Schedule quality inspection")
    private String recommendedAction;

    @Schema(description = "Additional context or details")
    private String details;

    /**
     * Alert severity enumeration
     */
    public enum AlertSeverity {
        LOW("Low", "#28a745", 1),
        MEDIUM("Medium", "#ffc107", 2),
        HIGH("High", "#fd7e14", 3),
        CRITICAL("Critical", "#dc3545", 4);

        private final String displayName;
        private final String colorCode;
        private final int level;

        AlertSeverity(String displayName, String colorCode, int level) {
            this.displayName = displayName;
            this.colorCode = colorCode;
            this.level = level;
        }

        public String getDisplayName() { return displayName; }
        public String getColorCode() { return colorCode; }
        public int getLevel() { return level; }
    }

    /**
     * Alert category enumeration
     */
    public enum AlertCategory {
        EXPIRY("Expiry Management"),
        QUANTITY("Quantity Management"),
        QUALITY("Quality Control"),
        PEST("Pest Control"),
        VALUE("Value Management"),
        STORAGE("Storage Conditions"),
        SYSTEM("System Generated");

        private final String displayName;

        AlertCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    // ==================== CONSTRUCTORS ====================

    /**
     * Default constructor
     */
    public InventoryAlert() {
        this.createdDate = LocalDateTime.now();
        this.acknowledged = false;
    }

    /**
     * Constructor with basic alert information
     */
    public InventoryAlert(String inventoryId, String inventoryCode, String alertType,
                          String message, AlertSeverity severity) {
        this();
        this.inventoryId = inventoryId;
        this.inventoryCode = inventoryCode;
        this.alertType = alertType;
        this.message = message;
        this.severity = severity;
        this.priority = severity.getLevel();
    }

    /**
     * Constructor with full alert information
     */
    public InventoryAlert(String inventoryId, String inventoryCode, String alertType,
                          String message, AlertSeverity severity, AlertCategory category) {
        this(inventoryId, inventoryCode, alertType, message, severity);
        this.category = category;
    }

    // ==================== GETTERS AND SETTERS ====================

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
        if (severity != null && this.priority == null) {
            this.priority = severity.getLevel();
        }
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public String getAcknowledgedBy() {
        return acknowledgedBy;
    }

    public void setAcknowledgedBy(String acknowledgedBy) {
        this.acknowledgedBy = acknowledgedBy;
    }

    public LocalDateTime getAcknowledgedDate() {
        return acknowledgedDate;
    }

    public void setAcknowledgedDate(LocalDateTime acknowledgedDate) {
        this.acknowledgedDate = acknowledgedDate;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public AlertCategory getCategory() {
        return category;
    }

    public void setCategory(AlertCategory category) {
        this.category = category;
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public void setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Acknowledge the alert
     */
    public void acknowledge(String userId) {
        this.acknowledged = true;
        this.acknowledgedBy = userId;
        this.acknowledgedDate = LocalDateTime.now();
    }

    /**
     * Check if alert is overdue (created more than specified hours ago)
     */
    public boolean isOverdue(int hours) {
        return createdDate.isBefore(LocalDateTime.now().minusHours(hours));
    }

    /**
     * Check if alert is urgent (HIGH or CRITICAL severity)
     */
    public boolean isUrgent() {
        return severity == AlertSeverity.HIGH || severity == AlertSeverity.CRITICAL;
    }

    /**
     * Get age of alert in hours
     */
    public long getAgeInHours() {
        return java.time.Duration.between(createdDate, LocalDateTime.now()).toHours();
    }

    /**
     * Get formatted alert summary
     */
    public String getSummary() {
        return String.format("[%s] %s - %s",
                severity.getDisplayName(),
                inventoryCode,
                message);
    }

    /**
     * Generate alert ID if not provided
     */
    public void generateAlertId() {
        if (this.alertId == null || this.alertId.trim().isEmpty()) {
            this.alertId = "ALERT" + System.currentTimeMillis() +
                    (inventoryId != null ? inventoryId.substring(Math.max(0, inventoryId.length() - 3)) : "");
        }
    }

    @Override
    public String toString() {
        return "InventoryAlert{" +
                "alertId='" + alertId + '\'' +
                ", inventoryId='" + inventoryId + '\'' +
                ", inventoryCode='" + inventoryCode + '\'' +
                ", alertType='" + alertType + '\'' +
                ", message='" + message + '\'' +
                ", severity=" + severity +
                ", acknowledged=" + acknowledged +
                ", priority=" + priority +
                ", category=" + category +
                ", createdDate=" + createdDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InventoryAlert that = (InventoryAlert) o;

        if (alertId != null) {
            return alertId.equals(that.alertId);
        }

        return inventoryId != null ? inventoryId.equals(that.inventoryId) : that.inventoryId == null &&
                alertType != null ? alertType.equals(that.alertType) : that.alertType == null;
    }

    @Override
    public int hashCode() {
        if (alertId != null) {
            return alertId.hashCode();
        }
        int result = inventoryId != null ? inventoryId.hashCode() : 0;
        result = 31 * result + (alertType != null ? alertType.hashCode() : 0);
        return result;
    }
}