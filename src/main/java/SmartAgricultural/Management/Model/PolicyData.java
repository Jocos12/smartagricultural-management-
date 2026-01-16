package SmartAgricultural.Management.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Entity
@Table(name = "policy_data")
public class PolicyData {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "policy_code", length = 30, unique = true, nullable = false)
    @NotBlank(message = "Policy code is required")
    @Size(max = 30, message = "Policy code must not exceed 30 characters")
    private String policyCode;

    @Column(name = "policy_name", length = 200, nullable = false)
    @NotBlank(message = "Policy name is required")
    @Size(max = 200, message = "Policy name must not exceed 200 characters")
    private String policyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "policy_type", nullable = false)
    @NotNull(message = "Policy type is required")
    private PolicyType policyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "policy_category", nullable = false)
    @NotNull(message = "Policy category is required")
    private PolicyCategory policyCategory;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @Column(name = "objectives", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Objectives must not exceed 3000 characters")
    private String objectives;

    @Column(name = "target_beneficiaries", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Target beneficiaries must not exceed 2000 characters")
    private String targetBeneficiaries; // JSON

    @Enumerated(EnumType.STRING)
    @Column(name = "geographic_scope", nullable = false)
    @NotNull(message = "Geographic scope is required")
    private GeographicScope geographicScope;

    @Column(name = "affected_regions", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Affected regions must not exceed 2000 characters")
    private String affectedRegions; // JSON

    @Column(name = "effective_date", nullable = false)
    @NotNull(message = "Effective date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveDate;

    @Column(name = "expiry_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    @Column(name = "implementation_period", length = 100)
    @Size(max = 100, message = "Implementation period must not exceed 100 characters")
    private String implementationPeriod;

    @Column(name = "total_budget", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", message = "Total budget must be positive")
    @Digits(integer = 13, fraction = 2, message = "Total budget format is invalid")
    private BigDecimal totalBudget;

    @Column(name = "currency", length = 3)
    @Size(max = 3, message = "Currency must be 3 characters")
    private String currency = "RWF";

    @Column(name = "funding_source", length = 100)
    @Size(max = 100, message = "Funding source must not exceed 100 characters")
    private String fundingSource;

    @Column(name = "implementing_agency", length = 150, nullable = false)
    @NotBlank(message = "Implementing agency is required")
    @Size(max = 150, message = "Implementing agency must not exceed 150 characters")
    private String implementingAgency;

    @Column(name = "ministry_responsible", length = 100)
    @Size(max = 100, message = "Ministry responsible must not exceed 100 characters")
    private String ministryResponsible;

    @Column(name = "key_stakeholders", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Key stakeholders must not exceed 2000 characters")
    private String keyStakeholders; // JSON

    @Column(name = "eligibility_criteria", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Eligibility criteria must not exceed 3000 characters")
    private String eligibilityCriteria;

    @Column(name = "application_process", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Application process must not exceed 3000 characters")
    private String applicationProcess;

    @Column(name = "documentation_required", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Documentation required must not exceed 2000 characters")
    private String documentationRequired;

    @Column(name = "approval_process", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Approval process must not exceed 2000 characters")
    private String approvalProcess;

    @Column(name = "beneficiaries_count")
    @Min(value = 0, message = "Beneficiaries count must be positive")
    private Integer beneficiariesCount;

    @Column(name = "farmers_benefited")
    @Min(value = 0, message = "Farmers benefited must be positive")
    private Integer farmersBenefited;

    @Column(name = "cooperatives_benefited")
    @Min(value = 0, message = "Cooperatives benefited must be positive")
    private Integer cooperativesBenefited;

    @Column(name = "budget_allocated", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", message = "Budget allocated must be positive")
    @Digits(integer = 13, fraction = 2, message = "Budget allocated format is invalid")
    private BigDecimal budgetAllocated;

    @Column(name = "budget_utilized", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", message = "Budget utilized must be positive")
    @Digits(integer = 13, fraction = 2, message = "Budget utilized format is invalid")
    private BigDecimal budgetUtilized;

    @Column(name = "utilization_rate", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Utilization rate must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Utilization rate must be between 0 and 100")
    @Digits(integer = 3, fraction = 2, message = "Utilization rate format is invalid")
    private BigDecimal utilizationRate; // %

    @Column(name = "expected_outcomes", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Expected outcomes must not exceed 3000 characters")
    private String expectedOutcomes;

    @Column(name = "performance_indicators", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Performance indicators must not exceed 3000 characters")
    private String performanceIndicators; // JSON

    @Column(name = "actual_outcomes", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Actual outcomes must not exceed 3000 characters")
    private String actualOutcomes;

    @Column(name = "success_metrics", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Success metrics must not exceed 2000 characters")
    private String successMetrics;

    @Column(name = "challenges_faced", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Challenges faced must not exceed 3000 characters")
    private String challengesFaced;

    @Column(name = "success_stories", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Success stories must not exceed 3000 characters")
    private String successStories;

    @Column(name = "impact_assessment", columnDefinition = "TEXT")
    @Size(max = 5000, message = "Impact assessment must not exceed 5000 characters")
    private String impactAssessment;

    @Column(name = "evaluation_reports", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Evaluation reports must not exceed 2000 characters")
    private String evaluationReports;

    @Column(name = "modification_history", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Modification history must not exceed 3000 characters")
    private String modificationHistory;

    @Column(name = "related_policies", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Related policies must not exceed 2000 characters")
    private String relatedPolicies; // JSON

    @Column(name = "international_alignment", length = 255)
    @Size(max = 255, message = "International alignment must not exceed 255 characters")
    private String internationalAlignment; // SDGs, etc.

    @Column(name = "compliance_requirements", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Compliance requirements must not exceed 3000 characters")
    private String complianceRequirements;

    @Column(name = "monitoring_mechanism", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Monitoring mechanism must not exceed 2000 characters")
    private String monitoringMechanism;

    @Column(name = "reporting_frequency", length = 50)
    @Size(max = 50, message = "Reporting frequency must not exceed 50 characters")
    private String reportingFrequency;

    @Column(name = "next_review_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextReviewDate;

    @Column(name = "renewal_process", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Renewal process must not exceed 2000 characters")
    private String renewalProcess;

    @Column(name = "discontinuation_criteria", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Discontinuation criteria must not exceed 2000 characters")
    private String discontinuationCriteria;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PolicyStatus status = PolicyStatus.DRAFT;

    @Column(name = "public_consultation")
    private Boolean publicConsultation = false;

    @Column(name = "consultation_feedback", columnDefinition = "TEXT")
    @Size(max = 5000, message = "Consultation feedback must not exceed 5000 characters")
    private String consultationFeedback;

    @Column(name = "parliamentary_approval")
    private Boolean parliamentaryApproval = false;

    @Column(name = "gazette_publication")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate gazettePublication;

    @Column(name = "communication_strategy", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Communication strategy must not exceed 3000 characters")
    private String communicationStrategy;

    @Column(name = "training_provided")
    private Boolean trainingProvided = false;

    @Column(name = "capacity_building", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Capacity building must not exceed 3000 characters")
    private String capacityBuilding;

    @Column(name = "technology_requirements", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Technology requirements must not exceed 2000 characters")
    private String technologyRequirements;

    @Column(name = "environmental_clearance")
    private Boolean environmentalClearance = false;

    @Column(name = "social_impact_assessment", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Social impact assessment must not exceed 3000 characters")
    private String socialImpactAssessment;

    @Column(name = "gender_considerations", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Gender considerations must not exceed 2000 characters")
    private String genderConsiderations;

    @Column(name = "youth_focus")
    private Boolean youthFocus = false;

    @Column(name = "climate_smart")
    private Boolean climateSmart = false;

    @Column(name = "innovation_component", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Innovation component must not exceed 2000 characters")
    private String innovationComponent;

    @Column(name = "research_component", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Research component must not exceed 2000 characters")
    private String researchComponent;

    @Column(name = "private_sector_involvement", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Private sector involvement must not exceed 2000 characters")
    private String privateSectorInvolvement;

    @Column(name = "international_cooperation", columnDefinition = "TEXT")
    @Size(max = 2000, message = "International cooperation must not exceed 2000 characters")
    private String internationalCooperation;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "created_by", length = 20)
    private String createdBy;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "updated_by", length = 20)
    private String updatedBy;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    @JsonIgnore
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", insertable = false, updatable = false)
    @JsonIgnore
    private User updater;

    // Enums
    public enum PolicyType {
        SUBSIDY("Subsidy", "Financial support to reduce costs"),
        TAX("Tax", "Tax-related policy"),
        REGULATION("Regulation", "Regulatory framework"),
        SUPPORT_PROGRAM("Support Program", "Direct support program"),
        TRADE_POLICY("Trade Policy", "International and domestic trade"),
        LAND_REFORM("Land Reform", "Land ownership and use"),
        CREDIT_PROGRAM("Credit Program", "Financial credit access"),
        INSURANCE("Insurance", "Agricultural insurance schemes"),
        RESEARCH_FUNDING("Research Funding", "Research and development funding");

        private final String displayName;
        private final String description;

        PolicyType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public boolean isFinancial() {
            return this == SUBSIDY || this == CREDIT_PROGRAM || this == INSURANCE || this == RESEARCH_FUNDING;
        }

        public boolean isRegulatory() {
            return this == REGULATION || this == TAX || this == TRADE_POLICY || this == LAND_REFORM;
        }
    }

    public enum PolicyCategory {
        PRODUCTION("Production", "Crop and livestock production"),
        MARKET("Market", "Market access and development"),
        ENVIRONMENT("Environment", "Environmental conservation"),
        SOCIAL("Social", "Social development and welfare"),
        TECHNOLOGY("Technology", "Technology adoption and innovation"),
        INFRASTRUCTURE("Infrastructure", "Infrastructure development");

        private final String displayName;
        private final String description;

        PolicyCategory(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum GeographicScope {
        NATIONAL("National", "Entire country"),
        PROVINCIAL("Provincial", "Provincial level"),
        DISTRICT("District", "District level"),
        SECTOR("Sector", "Sector level"),
        LOCAL("Local", "Local community level");

        private final String displayName;
        private final String description;

        GeographicScope(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public boolean isNationalLevel() {
            return this == NATIONAL;
        }

        public boolean isSubNational() {
            return this != NATIONAL;
        }
    }

    public enum PolicyStatus {
        DRAFT("Draft", "Policy in draft stage"),
        ACTIVE("Active", "Policy currently active"),
        SUSPENDED("Suspended", "Policy temporarily suspended"),
        EXPIRED("Expired", "Policy has expired"),
        CANCELLED("Cancelled", "Policy cancelled"),
        UNDER_REVIEW("Under Review", "Policy under review");

        private final String displayName;
        private final String description;

        PolicyStatus(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public boolean isActive() {
            return this == ACTIVE;
        }

        public boolean isInactive() {
            return this == SUSPENDED || this == EXPIRED || this == CANCELLED;
        }

        public boolean canBeModified() {
            return this == DRAFT || this == UNDER_REVIEW;
        }
    }

    public enum PolicyEffectiveness {
        HIGHLY_EFFECTIVE("Highly Effective", 5),
        EFFECTIVE("Effective", 4),
        MODERATELY_EFFECTIVE("Moderately Effective", 3),
        LESS_EFFECTIVE("Less Effective", 2),
        INEFFECTIVE("Ineffective", 1),
        NOT_ASSESSED("Not Assessed", 0);

        private final String displayName;
        private final int score;

        PolicyEffectiveness(String displayName, int score) {
            this.displayName = displayName;
            this.score = score;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getScore() {
            return score;
        }

        public static PolicyEffectiveness fromUtilizationRate(BigDecimal rate) {
            if (rate == null) return NOT_ASSESSED;

            if (rate.compareTo(new BigDecimal("90")) >= 0) return HIGHLY_EFFECTIVE;
            else if (rate.compareTo(new BigDecimal("75")) >= 0) return EFFECTIVE;
            else if (rate.compareTo(new BigDecimal("60")) >= 0) return MODERATELY_EFFECTIVE;
            else if (rate.compareTo(new BigDecimal("40")) >= 0) return LESS_EFFECTIVE;
            else return INEFFECTIVE;
        }
    }

    // Constructors
    public PolicyData() {
        this.id = generateAlphanumericId();
        this.policyCode = generatePolicyCode();
        this.createdDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    public PolicyData(String policyName, PolicyType policyType, PolicyCategory policyCategory,
                      String description, String implementingAgency, LocalDate effectiveDate) {
        this();
        this.policyName = policyName;
        this.policyType = policyType;
        this.policyCategory = policyCategory;
        this.description = description;
        this.implementingAgency = implementingAgency;
        this.effectiveDate = effectiveDate;
    }

    // Method to generate alphanumeric ID with mixed letters and numbers
    private String generateAlphanumericId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Add "PD" prefix for Policy Data
        sb.append("PD");

        // Timestamp-based part to ensure uniqueness (6 characters from timestamp)
        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 6);
        sb.append(shortTimestamp);

        // Add random mixed characters (letters and numbers)
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    // Method to generate policy code
    private String generatePolicyCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Add "POL" prefix
        sb.append("POL");

        // Add date part (YYMMDD)
        LocalDateTime now = LocalDateTime.now();
        sb.append(String.format("%02d%02d%02d",
                now.getYear() % 100,
                now.getMonthValue(),
                now.getDayOfMonth()));

        // Add timestamp (4 characters)
        String timestamp = String.valueOf(System.currentTimeMillis());
        String shortTimestamp = timestamp.substring(timestamp.length() - 4);
        sb.append(shortTimestamp);

        // Add random characters
        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    // JPA lifecycle methods
    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = generateAlphanumericId();
        }
        if (this.policyCode == null) {
            this.policyCode = generatePolicyCode();
        }
        this.createdDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();

        // Calculate utilization rate if not provided
        if (utilizationRate == null && budgetAllocated != null && budgetUtilized != null) {
            calculateUtilizationRate();
        }

        // Set default next review date if not provided
        if (nextReviewDate == null && effectiveDate != null) {
            this.nextReviewDate = effectiveDate.plusYears(1);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();

        // Recalculate utilization rate if budget values changed
        if (budgetAllocated != null && budgetUtilized != null) {
            calculateUtilizationRate();
        }

        // Update status based on dates
        updateStatusBasedOnDates();
    }

    // Utility methods
    private void calculateUtilizationRate() {
        if (budgetAllocated != null && budgetAllocated.compareTo(BigDecimal.ZERO) > 0 && budgetUtilized != null) {
            this.utilizationRate = budgetUtilized
                    .divide(budgetAllocated, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }

    private void updateStatusBasedOnDates() {
        if (status == PolicyStatus.ACTIVE && expiryDate != null && LocalDate.now().isAfter(expiryDate)) {
            this.status = PolicyStatus.EXPIRED;
        }
    }

    public PolicyEffectiveness calculateEffectiveness() {
        return PolicyEffectiveness.fromUtilizationRate(this.utilizationRate);
    }

    public String getFormattedBudget() {
        if (totalBudget == null) return "N/A";
        return String.format("%s %.2f", currency != null ? currency : "RWF", totalBudget);
    }

    public String getFormattedUtilizationRate() {
        if (utilizationRate == null) return "N/A";
        return utilizationRate.toString() + "%";
    }

    public boolean isCurrentlyActive() {
        if (status != PolicyStatus.ACTIVE) return false;
        LocalDate now = LocalDate.now();
        if (effectiveDate != null && now.isBefore(effectiveDate)) return false;
        if (expiryDate != null && now.isAfter(expiryDate)) return false;
        return true;
    }

    public boolean isExpiringSoon() {
        if (expiryDate == null || status != PolicyStatus.ACTIVE) return false;
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate) <= 90; // 3 months
    }

    public String getPolicyDuration() {
        if (effectiveDate == null) return "N/A";
        LocalDate endDate = expiryDate != null ? expiryDate : LocalDate.now();
        Period period = Period.between(effectiveDate, endDate);

        if (period.getYears() > 0) {
            return period.getYears() + " year(s), " + period.getMonths() + " month(s)";
        } else {
            return period.getMonths() + " month(s), " + period.getDays() + " day(s)";
        }
    }

    public boolean hasHighBudgetUtilization() {
        return utilizationRate != null && utilizationRate.compareTo(new BigDecimal("80")) >= 0;
    }

    public boolean requiresReview() {
        return nextReviewDate != null && LocalDate.now().isAfter(nextReviewDate);
    }

    public String getPolicySummary() {
        return String.format("%s - %s (%s) - %s",
                policyCode,
                policyName,
                policyType.getDisplayName(),
                status.getDisplayName());
    }

    public boolean isSociallyInclusive() {
        return youthFocus || (genderConsiderations != null && !genderConsiderations.isEmpty());
    }

    public boolean isEnvironmentallyFriendly() {
        return climateSmart || environmentalClearance || policyCategory == PolicyCategory.ENVIRONMENT;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPolicyCode() {
        return policyCode;
    }

    public void setPolicyCode(String policyCode) {
        this.policyCode = policyCode;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public PolicyType getPolicyType() {
        return policyType;
    }

    public void setPolicyType(PolicyType policyType) {
        this.policyType = policyType;
    }

    public PolicyCategory getPolicyCategory() {
        return policyCategory;
    }

    public void setPolicyCategory(PolicyCategory policyCategory) {
        this.policyCategory = policyCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    public String getTargetBeneficiaries() {
        return targetBeneficiaries;
    }

    public void setTargetBeneficiaries(String targetBeneficiaries) {
        this.targetBeneficiaries = targetBeneficiaries;
    }

    public GeographicScope getGeographicScope() {
        return geographicScope;
    }

    public void setGeographicScope(GeographicScope geographicScope) {
        this.geographicScope = geographicScope;
    }

    public String getAffectedRegions() {
        return affectedRegions;
    }

    public void setAffectedRegions(String affectedRegions) {
        this.affectedRegions = affectedRegions;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getImplementationPeriod() {
        return implementationPeriod;
    }

    public void setImplementationPeriod(String implementationPeriod) {
        this.implementationPeriod = implementationPeriod;
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(String fundingSource) {
        this.fundingSource = fundingSource;
    }

    public String getImplementingAgency() {
        return implementingAgency;
    }

    public void setImplementingAgency(String implementingAgency) {
        this.implementingAgency = implementingAgency;
    }

    public String getMinistryResponsible() {
        return ministryResponsible;
    }

    public void setMinistryResponsible(String ministryResponsible) {
        this.ministryResponsible = ministryResponsible;
    }

    public String getKeyStakeholders() {
        return keyStakeholders;
    }

    public void setKeyStakeholders(String keyStakeholders) {
        this.keyStakeholders = keyStakeholders;
    }

    public String getEligibilityCriteria() {
        return eligibilityCriteria;
    }

    public void setEligibilityCriteria(String eligibilityCriteria) {
        this.eligibilityCriteria = eligibilityCriteria;
    }

    public String getApplicationProcess() {
        return applicationProcess;
    }

    public void setApplicationProcess(String applicationProcess) {
        this.applicationProcess = applicationProcess;
    }

    public String getDocumentationRequired() {
        return documentationRequired;
    }

    public void setDocumentationRequired(String documentationRequired) {
        this.documentationRequired = documentationRequired;
    }

    public String getApprovalProcess() {
        return approvalProcess;
    }

    public void setApprovalProcess(String approvalProcess) {
        this.approvalProcess = approvalProcess;
    }

    public Integer getBeneficiariesCount() {
        return beneficiariesCount;
    }

    public void setBeneficiariesCount(Integer beneficiariesCount) {
        this.beneficiariesCount = beneficiariesCount;
    }

    public Integer getFarmersBenefited() {
        return farmersBenefited;
    }

    public void setFarmersBenefited(Integer farmersBenefited) {
        this.farmersBenefited = farmersBenefited;
    }

    public Integer getCooperativesBenefited() {
        return cooperativesBenefited;
    }

    public void setCooperativesBenefited(Integer cooperativesBenefited) {
        this.cooperativesBenefited = cooperativesBenefited;
    }

    public BigDecimal getBudgetAllocated() {
        return budgetAllocated;
    }

    public void setBudgetAllocated(BigDecimal budgetAllocated) {
        this.budgetAllocated = budgetAllocated;
    }

    public BigDecimal getBudgetUtilized() {
        return budgetUtilized;
    }

    public void setBudgetUtilized(BigDecimal budgetUtilized) {
        this.budgetUtilized = budgetUtilized;
    }

    public BigDecimal getUtilizationRate() {
        return utilizationRate;
    }

    public void setUtilizationRate(BigDecimal utilizationRate) {
        this.utilizationRate = utilizationRate;
    }

    public String getExpectedOutcomes() {
        return expectedOutcomes;
    }

    public void setExpectedOutcomes(String expectedOutcomes) {
        this.expectedOutcomes = expectedOutcomes;
    }

    public String getPerformanceIndicators() {
        return performanceIndicators;
    }

    public void setPerformanceIndicators(String performanceIndicators) {
        this.performanceIndicators = performanceIndicators;
    }

    public String getActualOutcomes() {
        return actualOutcomes;
    }

    public void setActualOutcomes(String actualOutcomes) {
        this.actualOutcomes = actualOutcomes;
    }

    public String getSuccessMetrics() {
        return successMetrics;
    }

    public void setSuccessMetrics(String successMetrics) {
        this.successMetrics = successMetrics;
    }

    public String getChallengesFaced() {
        return challengesFaced;
    }

    public void setChallengesFaced(String challengesFaced) {
        this.challengesFaced = challengesFaced;
    }

    public String getSuccessStories() {
        return successStories;
    }

    public void setSuccessStories(String successStories) {
        this.successStories = successStories;
    }

    public String getImpactAssessment() {
        return impactAssessment;
    }

    public void setImpactAssessment(String impactAssessment) {
        this.impactAssessment = impactAssessment;
    }

    public String getEvaluationReports() {
        return evaluationReports;
    }

    public void setEvaluationReports(String evaluationReports) {
        this.evaluationReports = evaluationReports;
    }

    public String getModificationHistory() {
        return modificationHistory;
    }

    public void setModificationHistory(String modificationHistory) {
        this.modificationHistory = modificationHistory;
    }

    public String getRelatedPolicies() {
        return relatedPolicies;
    }

    public void setRelatedPolicies(String relatedPolicies) {
        this.relatedPolicies = relatedPolicies;
    }

    public String getInternationalAlignment() {
        return internationalAlignment;
    }

    public void setInternationalAlignment(String internationalAlignment) {
        this.internationalAlignment = internationalAlignment;
    }

    public String getComplianceRequirements() {
        return complianceRequirements;
    }

    public void setComplianceRequirements(String complianceRequirements) {
        this.complianceRequirements = complianceRequirements;
    }

    public String getMonitoringMechanism() {
        return monitoringMechanism;
    }

    public void setMonitoringMechanism(String monitoringMechanism) {
        this.monitoringMechanism = monitoringMechanism;
    }

    public String getReportingFrequency() {
        return reportingFrequency;
    }

    public void setReportingFrequency(String reportingFrequency) {
        this.reportingFrequency = reportingFrequency;
    }

    public LocalDate getNextReviewDate() {
        return nextReviewDate;
    }

    public void setNextReviewDate(LocalDate nextReviewDate) {
        this.nextReviewDate = nextReviewDate;
    }

    public String getRenewalProcess() {
        return renewalProcess;
    }

    public void setRenewalProcess(String renewalProcess) {
        this.renewalProcess = renewalProcess;
    }

    public String getDiscontinuationCriteria() {
        return discontinuationCriteria;
    }

    public void setDiscontinuationCriteria(String discontinuationCriteria) {
        this.discontinuationCriteria = discontinuationCriteria;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public void setStatus(PolicyStatus status) {
        this.status = status;
    }

    public Boolean getPublicConsultation() {
        return publicConsultation;
    }

    public void setPublicConsultation(Boolean publicConsultation) {
        this.publicConsultation = publicConsultation;
    }

    public String getConsultationFeedback() {
        return consultationFeedback;
    }

    public void setConsultationFeedback(String consultationFeedback) {
        this.consultationFeedback = consultationFeedback;
    }

    public Boolean getParliamentaryApproval() {
        return parliamentaryApproval;
    }

    public void setParliamentaryApproval(Boolean parliamentaryApproval) {
        this.parliamentaryApproval = parliamentaryApproval;
    }

    public LocalDate getGazettePublication() {
        return gazettePublication;
    }

    public void setGazettePublication(LocalDate gazettePublication) {
        this.gazettePublication = gazettePublication;
    }

    public String getCommunicationStrategy() {
        return communicationStrategy;
    }

    public void setCommunicationStrategy(String communicationStrategy) {
        this.communicationStrategy = communicationStrategy;
    }

    public Boolean getTrainingProvided() {
        return trainingProvided;
    }

    public void setTrainingProvided(Boolean trainingProvided) {
        this.trainingProvided = trainingProvided;
    }

    public String getCapacityBuilding() {
        return capacityBuilding;
    }

    public void setCapacityBuilding(String capacityBuilding) {
        this.capacityBuilding = capacityBuilding;
    }

    public String getTechnologyRequirements() {
        return technologyRequirements;
    }

    public void setTechnologyRequirements(String technologyRequirements) {
        this.technologyRequirements = technologyRequirements;
    }

    public Boolean getEnvironmentalClearance() {
        return environmentalClearance;
    }

    public void setEnvironmentalClearance(Boolean environmentalClearance) {
        this.environmentalClearance = environmentalClearance;
    }

    public String getSocialImpactAssessment() {
        return socialImpactAssessment;
    }

    public void setSocialImpactAssessment(String socialImpactAssessment) {
        this.socialImpactAssessment = socialImpactAssessment;
    }

    public String getGenderConsiderations() {
        return genderConsiderations;
    }

    public void setGenderConsiderations(String genderConsiderations) {
        this.genderConsiderations = genderConsiderations;
    }

    public Boolean getYouthFocus() {
        return youthFocus;
    }

    public void setYouthFocus(Boolean youthFocus) {
        this.youthFocus = youthFocus;
    }

    public Boolean getClimateSmart() {
        return climateSmart;
    }

    public void setClimateSmart(Boolean climateSmart) {
        this.climateSmart = climateSmart;
    }

    public String getInnovationComponent() {
        return innovationComponent;
    }

    public void setInnovationComponent(String innovationComponent) {
        this.innovationComponent = innovationComponent;
    }

    public String getResearchComponent() {
        return researchComponent;
    }

    public void setResearchComponent(String researchComponent) {
        this.researchComponent = researchComponent;
    }

    public String getPrivateSectorInvolvement() {
        return privateSectorInvolvement;
    }

    public void setPrivateSectorInvolvement(String privateSectorInvolvement) {
        this.privateSectorInvolvement = privateSectorInvolvement;
    }

    public String getInternationalCooperation() {
        return internationalCooperation;
    }

    public void setInternationalCooperation(String internationalCooperation) {
        this.internationalCooperation = internationalCooperation;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public User getUpdater() {
        return updater;
    }

    public void setUpdater(User updater) {
        this.updater = updater;
    }

    // ToString method for debugging
    @Override
    public String toString() {
        return "PolicyData{" +
                "id='" + id + '\'' +
                ", policyCode='" + policyCode + '\'' +
                ", policyName='" + policyName + '\'' +
                ", policyType=" + policyType +
                ", policyCategory=" + policyCategory +
                ", implementingAgency='" + implementingAgency + '\'' +
                ", effectiveDate=" + effectiveDate +
                ", expiryDate=" + expiryDate +
                ", status=" + status +
                ", totalBudget=" + totalBudget +
                ", utilizationRate=" + utilizationRate +
                '}';
    }

    // Equals and HashCode based on ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolicyData that = (PolicyData) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}