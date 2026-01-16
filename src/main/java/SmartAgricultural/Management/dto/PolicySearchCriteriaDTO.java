package SmartAgricultural.Management.dto;

import SmartAgricultural.Management.Model.PolicyData.PolicyType;
import SmartAgricultural.Management.Model.PolicyData.PolicyCategory;
import SmartAgricultural.Management.Model.PolicyData.PolicyStatus;
import SmartAgricultural.Management.Model.PolicyData.GeographicScope;

import java.math.BigDecimal;

public class PolicySearchCriteriaDTO {
    private PolicyType policyType;
    private PolicyCategory policyCategory;
    private PolicyStatus status;
    private GeographicScope geographicScope;
    private String implementingAgency;
    private BigDecimal minBudget;
    private BigDecimal maxBudget;
    private String keyword;

    // Constructors
    public PolicySearchCriteriaDTO() {}

    // Getters and Setters
    public PolicyType getPolicyType() { return policyType; }
    public void setPolicyType(PolicyType policyType) { this.policyType = policyType; }

    public PolicyCategory getPolicyCategory() { return policyCategory; }
    public void setPolicyCategory(PolicyCategory policyCategory) { this.policyCategory = policyCategory; }

    public PolicyStatus getStatus() { return status; }
    public void setStatus(PolicyStatus status) { this.status = status; }

    public GeographicScope getGeographicScope() { return geographicScope; }
    public void setGeographicScope(GeographicScope geographicScope) { this.geographicScope = geographicScope; }

    public String getImplementingAgency() { return implementingAgency; }
    public void setImplementingAgency(String implementingAgency) { this.implementingAgency = implementingAgency; }

    public BigDecimal getMinBudget() { return minBudget; }
    public void setMinBudget(BigDecimal minBudget) { this.minBudget = minBudget; }

    public BigDecimal getMaxBudget() { return maxBudget; }
    public void setMaxBudget(BigDecimal maxBudget) { this.maxBudget = maxBudget; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
}