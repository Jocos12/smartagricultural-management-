package SmartAgricultural.Management.dto;

import SmartAgricultural.Management.Model.PolicyData.PolicyType;
import SmartAgricultural.Management.Model.PolicyData.PolicyCategory;
import SmartAgricultural.Management.Model.PolicyData.PolicyStatus;

import java.math.BigDecimal;
import java.util.Map;

public class PolicyStatisticsDTO {
    private Long totalPolicies;
    private Long activePolicies;
    private Long draftPolicies;
    private Long expiredPolicies;
    private Long suspendedPolicies;
    private Long cancelledPolicies;
    private Long underReviewPolicies;

    private BigDecimal totalBudget;
    private BigDecimal totalBudgetAllocated;
    private BigDecimal totalBudgetUtilized;
    private BigDecimal averageUtilizationRate;
    private BigDecimal highestUtilizationRate;
    private BigDecimal lowestUtilizationRate;

    private Integer totalBeneficiaries;
    private Integer totalFarmersBenefited;
    private Integer totalCooperativesBenefited;

    private Long climateSmartPolicies;
    private Long youthFocusedPolicies;
    private Long environmentalPolicies;
    private Long policiesWithParliamentaryApproval;
    private Long policiesWithPublicConsultation;

    private Map<PolicyStatus, Long> policyCountByStatus;
    private Map<PolicyType, Long> policyCountByType;
    private Map<PolicyCategory, Long> policyCountByCategory;

    private Integer policiesExpiringSoon;
    private Integer policiesRequiringReview;
    private Integer recentPolicies;

    // Constructors
    public PolicyStatisticsDTO() {}

    // Getters and Setters
    public Long getTotalPolicies() { return totalPolicies; }
    public void setTotalPolicies(Long totalPolicies) { this.totalPolicies = totalPolicies; }

    public Long getActivePolicies() { return activePolicies; }
    public void setActivePolicies(Long activePolicies) { this.activePolicies = activePolicies; }

    public Long getDraftPolicies() { return draftPolicies; }
    public void setDraftPolicies(Long draftPolicies) { this.draftPolicies = draftPolicies; }

    public Long getExpiredPolicies() { return expiredPolicies; }
    public void setExpiredPolicies(Long expiredPolicies) { this.expiredPolicies = expiredPolicies; }

    public Long getSuspendedPolicies() { return suspendedPolicies; }
    public void setSuspendedPolicies(Long suspendedPolicies) { this.suspendedPolicies = suspendedPolicies; }

    public Long getCancelledPolicies() { return cancelledPolicies; }
    public void setCancelledPolicies(Long cancelledPolicies) { this.cancelledPolicies = cancelledPolicies; }

    public Long getUnderReviewPolicies() { return underReviewPolicies; }
    public void setUnderReviewPolicies(Long underReviewPolicies) { this.underReviewPolicies = underReviewPolicies; }

    public BigDecimal getTotalBudget() { return totalBudget; }
    public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }

    public BigDecimal getTotalBudgetAllocated() { return totalBudgetAllocated; }
    public void setTotalBudgetAllocated(BigDecimal totalBudgetAllocated) { this.totalBudgetAllocated = totalBudgetAllocated; }

    public BigDecimal getTotalBudgetUtilized() { return totalBudgetUtilized; }
    public void setTotalBudgetUtilized(BigDecimal totalBudgetUtilized) { this.totalBudgetUtilized = totalBudgetUtilized; }

    public BigDecimal getAverageUtilizationRate() { return averageUtilizationRate; }
    public void setAverageUtilizationRate(BigDecimal averageUtilizationRate) { this.averageUtilizationRate = averageUtilizationRate; }

    public BigDecimal getHighestUtilizationRate() { return highestUtilizationRate; }
    public void setHighestUtilizationRate(BigDecimal highestUtilizationRate) { this.highestUtilizationRate = highestUtilizationRate; }

    public BigDecimal getLowestUtilizationRate() { return lowestUtilizationRate; }
    public void setLowestUtilizationRate(BigDecimal lowestUtilizationRate) { this.lowestUtilizationRate = lowestUtilizationRate; }

    public Integer getTotalBeneficiaries() { return totalBeneficiaries; }
    public void setTotalBeneficiaries(Integer totalBeneficiaries) { this.totalBeneficiaries = totalBeneficiaries; }

    public Integer getTotalFarmersBenefited() { return totalFarmersBenefited; }
    public void setTotalFarmersBenefited(Integer totalFarmersBenefited) { this.totalFarmersBenefited = totalFarmersBenefited; }

    public Integer getTotalCooperativesBenefited() { return totalCooperativesBenefited; }
    public void setTotalCooperativesBenefited(Integer totalCooperativesBenefited) { this.totalCooperativesBenefited = totalCooperativesBenefited; }

    public Long getClimateSmartPolicies() { return climateSmartPolicies; }
    public void setClimateSmartPolicies(Long climateSmartPolicies) { this.climateSmartPolicies = climateSmartPolicies; }

    public Long getYouthFocusedPolicies() { return youthFocusedPolicies; }
    public void setYouthFocusedPolicies(Long youthFocusedPolicies) { this.youthFocusedPolicies = youthFocusedPolicies; }

    public Long getEnvironmentalPolicies() { return environmentalPolicies; }
    public void setEnvironmentalPolicies(Long environmentalPolicies) { this.environmentalPolicies = environmentalPolicies; }

    public Long getPoliciesWithParliamentaryApproval() { return policiesWithParliamentaryApproval; }
    public void setPoliciesWithParliamentaryApproval(Long policiesWithParliamentaryApproval) { this.policiesWithParliamentaryApproval = policiesWithParliamentaryApproval; }

    public Long getPoliciesWithPublicConsultation() { return policiesWithPublicConsultation; }
    public void setPoliciesWithPublicConsultation(Long policiesWithPublicConsultation) { this.policiesWithPublicConsultation = policiesWithPublicConsultation; }

    public Map<PolicyStatus, Long> getPolicyCountByStatus() { return policyCountByStatus; }
    public void setPolicyCountByStatus(Map<PolicyStatus, Long> policyCountByStatus) { this.policyCountByStatus = policyCountByStatus; }

    public Map<PolicyType, Long> getPolicyCountByType() { return policyCountByType; }
    public void setPolicyCountByType(Map<PolicyType, Long> policyCountByType) { this.policyCountByType = policyCountByType; }

    public Map<PolicyCategory, Long> getPolicyCountByCategory() { return policyCountByCategory; }
    public void setPolicyCountByCategory(Map<PolicyCategory, Long> policyCountByCategory) { this.policyCountByCategory = policyCountByCategory; }

    public Integer getPoliciesExpiringSoon() { return policiesExpiringSoon; }
    public void setPoliciesExpiringSoon(Integer policiesExpiringSoon) { this.policiesExpiringSoon = policiesExpiringSoon; }

    public Integer getPoliciesRequiringReview() { return policiesRequiringReview; }
    public void setPoliciesRequiringReview(Integer policiesRequiringReview) { this.policiesRequiringReview = policiesRequiringReview; }

    public Integer getRecentPolicies() { return recentPolicies; }
    public void setRecentPolicies(Integer recentPolicies) { this.recentPolicies = recentPolicies; }
}