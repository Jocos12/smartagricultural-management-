package SmartAgricultural.Management.dto;

import SmartAgricultural.Management.Model.PolicyData.PolicyType;
import SmartAgricultural.Management.Model.PolicyData.PolicyCategory;
import SmartAgricultural.Management.Model.PolicyData.PolicyStatus;
import SmartAgricultural.Management.Model.PolicyData.GeographicScope;

import java.math.BigDecimal;

public class PolicyDataDTO {
    private String id;
    private String policyCode;
    private String policyName;
    private PolicyType policyType;
    private PolicyCategory policyCategory;
    private String description;
    private String implementingAgency;
    private PolicyStatus status;
    private BigDecimal totalBudget;
    private BigDecimal utilizationRate;

    // Constructors
    public PolicyDataDTO() {}

    public PolicyDataDTO(String id, String policyCode, String policyName, PolicyType policyType,
                         PolicyCategory policyCategory, String description, String implementingAgency,
                         PolicyStatus status, BigDecimal totalBudget, BigDecimal utilizationRate) {
        this.id = id;
        this.policyCode = policyCode;
        this.policyName = policyName;
        this.policyType = policyType;
        this.policyCategory = policyCategory;
        this.description = description;
        this.implementingAgency = implementingAgency;
        this.status = status;
        this.totalBudget = totalBudget;
        this.utilizationRate = utilizationRate;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPolicyCode() { return policyCode; }
    public void setPolicyCode(String policyCode) { this.policyCode = policyCode; }

    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }

    public PolicyType getPolicyType() { return policyType; }
    public void setPolicyType(PolicyType policyType) { this.policyType = policyType; }

    public PolicyCategory getPolicyCategory() { return policyCategory; }
    public void setPolicyCategory(PolicyCategory policyCategory) { this.policyCategory = policyCategory; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImplementingAgency() { return implementingAgency; }
    public void setImplementingAgency(String implementingAgency) { this.implementingAgency = implementingAgency; }

    public PolicyStatus getStatus() { return status; }
    public void setStatus(PolicyStatus status) { this.status = status; }

    public BigDecimal getTotalBudget() { return totalBudget; }
    public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }

    public BigDecimal getUtilizationRate() { return utilizationRate; }
    public void setUtilizationRate(BigDecimal utilizationRate) { this.utilizationRate = utilizationRate; }
}