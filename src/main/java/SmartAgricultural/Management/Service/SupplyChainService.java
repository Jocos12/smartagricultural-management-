package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.SupplyChain;
import SmartAgricultural.Management.Model.SupplyChain.Stage;
import SmartAgricultural.Management.Model.SupplyChain.QualityStatus;
import SmartAgricultural.Management.Repository.CropProductionRepository;
import SmartAgricultural.Management.Repository.SupplyChainRepository;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.exception.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import SmartAgricultural.Management.Model.CropProduction;
import SmartAgricultural.Management.Repository.CropProductionRepository;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupplyChainService {

    @Autowired
    private SupplyChainRepository supplyChainRepository;

    @Autowired
    private CropProductionRepository cropProductionRepository;

    // Basic CRUD operations
    public SupplyChain save(SupplyChain supplyChain) {
        validateSupplyChain(supplyChain);
        return supplyChainRepository.save(supplyChain);
    }

    public SupplyChain create(SupplyChain supplyChain) {
        if (supplyChain.getId() != null) {
            throw new ValidationException("Cannot create supply chain stage with existing ID");
        }

        if (StringUtils.hasText(supplyChain.getTrackingCode()) &&
                existsByTrackingCode(supplyChain.getTrackingCode())) {
            throw new ValidationException("Tracking code already exists: " + supplyChain.getTrackingCode());
        }

        if (StringUtils.hasText(supplyChain.getTransactionId()) &&
                existsByTransactionId(supplyChain.getTransactionId())) {
            throw new ValidationException("Transaction ID already exists: " + supplyChain.getTransactionId());
        }

        // Validate stage order within crop production
        if (existsByCropProductionIdAndStageOrder(supplyChain.getCropProductionId(), supplyChain.getStageOrder())) {
            throw new ValidationException("Stage order " + supplyChain.getStageOrder() +
                    " already exists for crop production: " + supplyChain.getCropProductionId());
        }

        return save(supplyChain);
    }

    public SupplyChain update(String id, SupplyChain supplyChain) {
        SupplyChain existing = findById(id);
        updateSupplyChainFields(existing, supplyChain);
        return save(existing);
    }

    @Transactional(readOnly = true)
    public SupplyChain findById(String id) {
        return supplyChainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supply chain stage not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<SupplyChain> findByIdOptional(String id) {
        return supplyChainRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public SupplyChain findByTrackingCode(String trackingCode) {
        return supplyChainRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Supply chain stage not found with tracking code: " + trackingCode));
    }

    @Transactional(readOnly = true)
    public SupplyChain findByTransactionId(String transactionId) {
        return supplyChainRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Supply chain stage not found with transaction ID: " + transactionId));
    }

    @Transactional(readOnly = true)
    public List<SupplyChain> findAll() {
        return supplyChainRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<SupplyChain> findAll(Pageable pageable) {
        return supplyChainRepository.findAll(pageable);
    }

    public void deleteById(String id) {
        if (!supplyChainRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supply chain stage not found with id: " + id);
        }
        supplyChainRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(String id) {
        return supplyChainRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByTrackingCode(String trackingCode) {
        return supplyChainRepository.existsByTrackingCode(trackingCode);
    }

    @Transactional(readOnly = true)
    public boolean existsByTransactionId(String transactionId) {
        return supplyChainRepository.existsByTransactionId(transactionId);
    }

    @Transactional(readOnly = true)
    public boolean existsByCropProductionIdAndStageOrder(String cropProductionId, Integer stageOrder) {
        return supplyChainRepository.existsByCropProductionIdAndStageOrder(cropProductionId, stageOrder);
    }

    @Transactional(readOnly = true)
    public long count() {
        return supplyChainRepository.count();
    }

    // Crop production specific operations
    @Transactional(readOnly = true)
    public List<SupplyChain> findByCropProductionId(String cropProductionId) {
        return supplyChainRepository.findByCropProductionIdOrderByStageOrder(cropProductionId);
    }

    @Transactional(readOnly = true)
    public Page<SupplyChain> findByCropProductionId(String cropProductionId, Pageable pageable) {
        return supplyChainRepository.findByCropProductionId(cropProductionId, pageable);
    }

    @Transactional(readOnly = true)
    public long countByCropProductionId(String cropProductionId) {
        return supplyChainRepository.countByCropProductionId(cropProductionId);
    }

    // Stage-based operations
    @Transactional(readOnly = true)
    public List<SupplyChain> findByStage(Stage stage) {
        return supplyChainRepository.findByStage(stage);
    }

    @Transactional(readOnly = true)
    public Page<SupplyChain> findByStage(Stage stage, Pageable pageable) {
        return supplyChainRepository.findByStage(stage, pageable);
    }

    @Transactional(readOnly = true)
    public List<SupplyChain> findByCropProductionIdAndStage(String cropProductionId, Stage stage) {
        return supplyChainRepository.findByCropProductionIdAndStage(cropProductionId, stage);
    }

    @Transactional(readOnly = true)
    public Optional<SupplyChain> findByCropProductionIdAndStageOrder(String cropProductionId, Integer stageOrder) {
        return supplyChainRepository.findByCropProductionIdAndStageOrder(cropProductionId, stageOrder);
    }


    public List<SupplyChain> findByFarmId(String farmId) {
        try {
            // ✅ CORRECTION: Utiliser la méthode avec OrderBy qui retourne explicitement une List
            List<CropProduction> productions = cropProductionRepository.findByFarmIdOrderByCreatedAtDesc(farmId);

            if (productions.isEmpty()) {
                return new ArrayList<>();
            }

            // Récupérer toutes les supply chains pour ces productions
            return productions.stream()
                    .flatMap(production -> {
                        try {
                            return supplyChainRepository
                                    .findByCropProductionIdOrderByStageOrder(production.getId())
                                    .stream();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new ArrayList<SupplyChain>().stream();
                        }
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<SupplyChain> findByFarmerId(String farmerId) {
        try {
            return supplyChainRepository.findAll()
                    .stream()
                    .filter(sc -> {
                        if (sc.getCropProduction() != null) {
                            return farmerId.equals(sc.getCropProduction().getFarmId()); // ✅ Correct
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            // Log l'erreur si vous avez un logger
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public long countByStage(Stage stage) {
        return supplyChainRepository.countByStage(stage);
    }

    // Stage management operations
    public SupplyChain completeStage(String id, BigDecimal quantityOut, String handlingNotes, QualityStatus qualityStatus) {
        SupplyChain stage = findById(id);

        if (stage.getStageEndDate() != null) {
            throw new ValidationException("Stage is already completed");
        }

        stage.setStageEndDate(LocalDateTime.now());

        if (quantityOut != null) {
            stage.setQuantityOut(quantityOut);
        }

        if (StringUtils.hasText(handlingNotes)) {
            stage.setHandlingNotes(handlingNotes);
        }

        if (qualityStatus != null) {
            stage.setQualityStatus(qualityStatus);
        }

        return save(stage);
    }

    public SupplyChain createNextStage(String cropProductionId, String location, String responsibleParty, String facilityName) {
        // Find the current last stage
        Optional<SupplyChain> lastStageOpt = supplyChainRepository.findLastStage(cropProductionId);

        Stage nextStage;
        Integer nextStageOrder;
        BigDecimal quantityIn = null;

        if (lastStageOpt.isPresent()) {
            SupplyChain lastStage = lastStageOpt.get();

            if (lastStage.getStageEndDate() == null) {
                throw new ValidationException("Cannot create next stage: current stage is not completed");
            }

            nextStage = lastStage.getStage().getNextStage();
            if (nextStage == null) {
                throw new ValidationException("No next stage available after: " + lastStage.getStage().getDisplayName());
            }

            nextStageOrder = lastStage.getStageOrder() + 1;
            quantityIn = lastStage.getQuantityOut();
        } else {
            // First stage (HARVEST)
            nextStage = Stage.HARVEST;
            nextStageOrder = 1;
        }

        SupplyChain newStage = new SupplyChain();
        newStage.setCropProductionId(cropProductionId);
        newStage.setStage(nextStage);
        newStage.setStageOrder(nextStageOrder);
        newStage.setLocation(location);
        newStage.setResponsibleParty(responsibleParty);
        newStage.setFacilityName(facilityName);
        newStage.setQuantityIn(quantityIn);

        return save(newStage);
    }

    // Quality management
    @Transactional(readOnly = true)
    public List<SupplyChain> findByQualityStatus(QualityStatus qualityStatus) {
        return supplyChainRepository.findByQualityStatus(qualityStatus);
    }

    @Transactional(readOnly = true)
    public Page<SupplyChain> findByQualityStatus(QualityStatus qualityStatus, Pageable pageable) {
        return supplyChainRepository.findByQualityStatus(qualityStatus, pageable);
    }

    @Transactional(readOnly = true)
    public List<SupplyChain> findQualityIssues() {
        return supplyChainRepository.findQualityIssues();
    }

    @Transactional(readOnly = true)
    public List<SupplyChain> findQualityIssuesByCropProduction(String cropProductionId) {
        return supplyChainRepository.findQualityIssuesByCropProduction(cropProductionId);
    }


    private void validateQuantities(SupplyChain supplyChain) {
        if (supplyChain.getQuantityIn() == null) {
            throw new ValidationException("Quantity In is required");
        }

        BigDecimal qtyIn = supplyChain.getQuantityIn();
        BigDecimal qtyOut = supplyChain.getQuantityOut() != null ? supplyChain.getQuantityOut() : BigDecimal.ZERO;
        BigDecimal loss = supplyChain.getLossQuantity() != null ? supplyChain.getLossQuantity() : BigDecimal.ZERO;

        if (qtyOut.compareTo(qtyIn) > 0) {
            throw new ValidationException(
                    String.format("Quantity Out (%.2f) cannot exceed Quantity In (%.2f)", qtyOut, qtyIn)
            );
        }

        if (loss.compareTo(qtyIn) > 0) {
            throw new ValidationException(
                    String.format("Loss Quantity (%.2f) cannot exceed Quantity In (%.2f)", loss, qtyIn)
            );
        }

        if (qtyOut.add(loss).compareTo(qtyIn) > 0) {
            throw new ValidationException(
                    String.format("Quantity Out (%.2f) + Loss (%.2f) = %.2f exceeds Quantity In (%.2f)",
                            qtyOut, loss, qtyOut.add(loss), qtyIn)
            );
        }
    }

    @Transactional(readOnly = true)
    public long countQualityIssues() {
        return supplyChainRepository.countQualityIssues();
    }

    public SupplyChain updateQualityStatus(String id, QualityStatus qualityStatus, String qualityTests) {
        SupplyChain stage = findById(id);
        stage.setQualityStatus(qualityStatus);

        if (StringUtils.hasText(qualityTests)) {
            stage.setQualityTests(qualityTests);
        }

        return save(stage);
    }

    // Loss management
    @Transactional(readOnly = true)
    public List<SupplyChain> findStagesWithLosses() {
        return supplyChainRepository.findStagesWithLosses();
    }

    @Transactional(readOnly = true)
    public List<SupplyChain> findStagesWithHighLosses(BigDecimal threshold) {
        return supplyChainRepository.findStagesWithHighLosses(threshold);
    }

    @Transactional(readOnly = true)
    public List<SupplyChain> findStagesWithLossesByCropProduction(String cropProductionId) {
        return supplyChainRepository.findStagesWithLossesByCropProduction(cropProductionId);
    }

    @Transactional(readOnly = true)
    public long countStagesWithLosses() {
        return supplyChainRepository.countStagesWithLosses();
    }

    public SupplyChain updateLossInformation(String id, BigDecimal lossQuantity, String lossReason) {
        SupplyChain stage = findById(id);

        if (lossQuantity != null) {
            stage.setLossQuantity(lossQuantity);
        }

        if (StringUtils.hasText(lossReason)) {
            stage.setLossReason(lossReason);
        }

        return save(stage);
    }

    // Status queries
    @Transactional(readOnly = true)
    public List<SupplyChain> findIncompleteStages() {
        return supplyChainRepository.findIncompleteStages();
    }

    @Transactional(readOnly = true)
    public List<SupplyChain> findIncompleteStageByCropProduction(String cropProductionId) {
        return supplyChainRepository.findIncompleteStageByCropProduction(cropProductionId);
    }

    @Transactional(readOnly = true)
    public List<SupplyChain> findCompletedStages() {
        return supplyChainRepository.findCompletedStages();
    }

    @Transactional(readOnly = true)
    public List<SupplyChain> findActiveStagesAtDate(LocalDateTime date) {
        return supplyChainRepository.findActiveStagesAtDate(date);
    }

    @Transactional(readOnly = true)
    public long countIncompleteStages() {
        return supplyChainRepository.countIncompleteStages();
    }

    // Search and filtering
    @Transactional(readOnly = true)
    public List<SupplyChain> searchSupplyChain(String searchTerm) {
        if (!StringUtils.hasText(searchTerm)) {
            return findAll();
        }
        return supplyChainRepository.searchByTerm(searchTerm.trim());
    }

    @Transactional(readOnly = true)
    public Page<SupplyChain> searchSupplyChain(String cropProductionId, String searchTerm, Pageable pageable) {
        if (!StringUtils.hasText(searchTerm)) {
            if (StringUtils.hasText(cropProductionId)) {
                return findByCropProductionId(cropProductionId, pageable);
            } else {
                return findAll(pageable);
            }
        }

        if (StringUtils.hasText(cropProductionId)) {
            return supplyChainRepository.searchByTermAndCropProduction(cropProductionId, searchTerm.trim(), pageable);
        } else {
            List<SupplyChain> searchResults = supplyChainRepository.searchByTerm(searchTerm.trim());
            int start = Math.min((int) pageable.getOffset(), searchResults.size());
            int end = Math.min(start + pageable.getPageSize(), searchResults.size());
            List<SupplyChain> pageContent = searchResults.subList(start, end);

            return new PageImpl<>(pageContent, pageable, searchResults.size());
        }
    }

    @Transactional(readOnly = true)
    public Page<SupplyChain> findWithFilters(
            String cropProductionId,
            Stage stage,
            QualityStatus qualityStatus,
            String location,
            String responsibleParty,
            BigDecimal minCost,
            BigDecimal maxCost,
            Pageable pageable) {

        if (StringUtils.hasText(cropProductionId)) {
            return supplyChainRepository.findWithFiltersByCropProduction(
                    cropProductionId, stage, qualityStatus, location,
                    responsibleParty, minCost, maxCost, pageable);
        } else {
            return supplyChainRepository.findWithFilters(
                    stage, qualityStatus, location, responsibleParty,
                    minCost, maxCost, pageable);
        }
    }

    // Analytics and statistics
    @Transactional(readOnly = true)
    public Map<String, Long> getStageStatistics() {
        List<Object[]> results = supplyChainRepository.countByStage();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> ((Stage) result[0]).getDisplayName(),
                        result -> (Long) result[1]
                ));
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getQualityStatistics() {
        List<Object[]> results = supplyChainRepository.countByQualityStatus();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> ((QualityStatus) result[0]).getDisplayName(),
                        result -> (Long) result[1]
                ));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSupplyChainSummary(String cropProductionId) {
        Map<String, Object> summary = new HashMap<>();

        List<SupplyChain> stages = findByCropProductionId(cropProductionId);
        summary.put("totalStages", stages.size());
        summary.put("completedStages", stages.stream().filter(SupplyChain::isStageCompleted).count());
        summary.put("incompleteStages", stages.stream().filter(SupplyChain::isStageInProgress).count());

        BigDecimal totalQuantityIn = supplyChainRepository.getTotalQuantityInByCropProduction(cropProductionId);
        BigDecimal totalQuantityOut = supplyChainRepository.getTotalQuantityOutByCropProduction(cropProductionId);
        BigDecimal totalLoss = supplyChainRepository.getTotalLossQuantityByCropProduction(cropProductionId);
        BigDecimal totalCost = supplyChainRepository.getTotalCostByCropProduction(cropProductionId);

        summary.put("totalQuantityIn", totalQuantityIn != null ? totalQuantityIn : BigDecimal.ZERO);
        summary.put("totalQuantityOut", totalQuantityOut != null ? totalQuantityOut : BigDecimal.ZERO);
        summary.put("totalLoss", totalLoss != null ? totalLoss : BigDecimal.ZERO);
        summary.put("totalCost", totalCost != null ? totalCost : BigDecimal.ZERO);

        BigDecimal averageLoss = supplyChainRepository.getAverageLossPercentageByCropProduction(cropProductionId);
        summary.put("averageLossPercentage", averageLoss != null ? averageLoss : BigDecimal.ZERO);

        long qualityIssueCount = supplyChainRepository.countQualityIssuesByCropProduction(cropProductionId);
        summary.put("qualityIssueCount", qualityIssueCount);

        List<Object[]> stageStats = supplyChainRepository.countByStageAndCropProduction(cropProductionId);
        Map<String, Long> stageDistribution = stageStats.stream()
                .collect(Collectors.toMap(
                        result -> ((Stage) result[0]).getDisplayName(),
                        result -> (Long) result[1]
                ));
        summary.put("stageDistribution", stageDistribution);

        return summary;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCropProductionMetrics(String cropProductionId) {
        Map<String, Object> metrics = new HashMap<>();

        List<SupplyChain> stages = findByCropProductionId(cropProductionId);

        if (stages.isEmpty()) {
            metrics.put("message", "No supply chain data found");
            return metrics;
        }

        // Basic metrics
        metrics.put("totalStages", stages.size());
        metrics.put("completedStages", stages.stream().mapToLong(s -> s.isStageCompleted() ? 1 : 0).sum());
        metrics.put("stagesWithLosses", stages.stream().mapToLong(s -> s.hasLosses() ? 1 : 0).sum());
        metrics.put("stagesWithQualityIssues", stages.stream().mapToLong(s -> s.hasQualityIssues() ? 1 : 0).sum());

        // Quantity metrics
        BigDecimal totalInput = stages.stream()
                .map(SupplyChain::getQuantityIn)
                .filter(q -> q != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOutput = stages.stream()
                .map(SupplyChain::getQuantityOut)
                .filter(q -> q != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLoss = stages.stream()
                .map(SupplyChain::getLossQuantity)
                .filter(q -> q != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        metrics.put("totalQuantityIn", totalInput);
        metrics.put("totalQuantityOut", totalOutput);
        metrics.put("totalLoss", totalLoss);

        if (totalInput.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal overallLossPercentage = totalLoss.divide(totalInput, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            metrics.put("overallLossPercentage", overallLossPercentage);
        }

        // Cost metrics
        BigDecimal totalCost = stages.stream()
                .map(SupplyChain::getCostIncurred)
                .filter(c -> c != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        metrics.put("totalCost", totalCost);

        if (totalInput.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal costPerUnit = totalCost.divide(totalInput, 4, RoundingMode.HALF_UP);
            metrics.put("costPerUnit", costPerUnit);
        }

        // Timeline metrics
        Optional<SupplyChain> firstStage = stages.stream()
                .filter(s -> s.getStageStartDate() != null)
                .min((s1, s2) -> s1.getStageStartDate().compareTo(s2.getStageStartDate()));

        Optional<SupplyChain> lastCompletedStage = stages.stream()
                .filter(s -> s.getStageEndDate() != null)
                .max((s1, s2) -> s1.getStageEndDate().compareTo(s2.getStageEndDate()));

        if (firstStage.isPresent() && lastCompletedStage.isPresent()) {
            long totalDurationHours = java.time.Duration.between(
                    firstStage.get().getStageStartDate(),
                    lastCompletedStage.get().getStageEndDate()
            ).toHours();

            metrics.put("totalDurationHours", totalDurationHours);
            metrics.put("totalDurationDays", totalDurationHours / 24);
        }

        return metrics;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPerformanceAnalysis(String cropProductionId) {
        Map<String, Object> analysis = new HashMap<>();

        List<SupplyChain> stages = findByCropProductionId(cropProductionId);

        if (stages.isEmpty()) {
            analysis.put("message", "No supply chain data found");
            return analysis;
        }

        // Efficiency analysis
        List<BigDecimal> efficiencyRates = stages.stream()
                .map(SupplyChain::getEfficiencyRate)
                .filter(rate -> rate != null)
                .collect(Collectors.toList());

        if (!efficiencyRates.isEmpty()) {
            BigDecimal avgEfficiency = efficiencyRates.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(new BigDecimal(efficiencyRates.size()), 4, RoundingMode.HALF_UP);

            analysis.put("averageEfficiency", avgEfficiency);
            analysis.put("minEfficiency", efficiencyRates.stream().min(BigDecimal::compareTo).orElse(null));
            analysis.put("maxEfficiency", efficiencyRates.stream().max(BigDecimal::compareTo).orElse(null));
        }

        // Quality analysis
        Map<QualityStatus, Long> qualityDistribution = stages.stream()
                .collect(Collectors.groupingBy(
                        SupplyChain::getQualityStatus,
                        Collectors.counting()
                ));

        analysis.put("qualityDistribution", qualityDistribution);

        // Loss analysis by stage
        Map<Stage, BigDecimal> lossByStage = stages.stream()
                .filter(s -> s.getLossQuantity() != null && s.getLossQuantity().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.groupingBy(
                        SupplyChain::getStage,
                        Collectors.mapping(
                                SupplyChain::getLossQuantity,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        analysis.put("lossByStage", lossByStage);

        // Cost analysis by stage
        Map<Stage, BigDecimal> costByStage = stages.stream()
                .filter(s -> s.getCostIncurred() != null)
                .collect(Collectors.groupingBy(
                        SupplyChain::getStage,
                        Collectors.mapping(
                                SupplyChain::getCostIncurred,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        analysis.put("costByStage", costByStage);

        // Duration analysis
        Map<Stage, Long> durationByStage = stages.stream()
                .filter(SupplyChain::isStageCompleted)
                .collect(Collectors.groupingBy(
                        SupplyChain::getStage,
                        Collectors.mapping(
                                SupplyChain::getStageDurationHours,
                                Collectors.summingLong(Long::longValue)
                        )
                ));

        analysis.put("durationByStage", durationByStage);

        return analysis;
    }

    // Bulk operations
    public List<SupplyChain> createSupplyChainStages(List<SupplyChain> supplyChains) {
        for (SupplyChain supplyChain : supplyChains) {
            validateSupplyChain(supplyChain);

            if (supplyChain.getId() != null) {
                throw new ValidationException("Cannot create supply chain stage with existing ID");
            }
        }
        return supplyChainRepository.saveAll(supplyChains);
    }

    public List<SupplyChain> updateSupplyChainStages(List<SupplyChain> supplyChains) {
        List<SupplyChain> updatedSupplyChains = supplyChains.stream()
                .map(supplyChain -> {
                    if (supplyChain.getId() == null) {
                        throw new ValidationException("Cannot update supply chain stage without ID");
                    }
                    validateSupplyChain(supplyChain);
                    return supplyChain;
                })
                .collect(Collectors.toList());

        return supplyChainRepository.saveAll(updatedSupplyChains);
    }

    public void deleteSupplyChainStages(List<String> ids) {
        List<SupplyChain> supplyChains = ids.stream()
                .map(this::findById)
                .collect(Collectors.toList());

        supplyChainRepository.deleteAll(supplyChains);
    }

    // Maintenance operations
    public int cleanupOldCompletedEntries(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        return supplyChainRepository.deleteOldCompletedEntries(cutoffDate);
    }

    // Validation methods
    private void validateSupplyChain(SupplyChain supplyChain) {
        if (!StringUtils.hasText(supplyChain.getCropProductionId())) {
            throw new ValidationException("Crop production ID is required");
        }

        if (supplyChain.getStage() == null) {
            throw new ValidationException("Stage is required");
        }

        if (supplyChain.getStageOrder() == null || supplyChain.getStageOrder() < 1) {
            throw new ValidationException("Valid stage order is required (minimum 1)");
        }

        if (!StringUtils.hasText(supplyChain.getLocation())) {
            throw new ValidationException("Location is required");
        }

        if (!StringUtils.hasText(supplyChain.getResponsibleParty())) {
            throw new ValidationException("Responsible party is required");
        }

        if (supplyChain.getQuantityIn() == null || supplyChain.getQuantityIn().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Quantity in must be positive");
        }

        if (supplyChain.getQuantityOut() != null &&
                supplyChain.getQuantityOut().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Quantity out cannot be negative");
        }

        if (supplyChain.getLossQuantity() != null &&
                supplyChain.getLossQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Loss quantity cannot be negative");
        }

        if (supplyChain.getLossPercentage() != null &&
                (supplyChain.getLossPercentage().compareTo(BigDecimal.ZERO) < 0 ||
                        supplyChain.getLossPercentage().compareTo(new BigDecimal("100")) > 0)) {
            throw new ValidationException("Loss percentage must be between 0 and 100");
        }

        if (supplyChain.getCostIncurred() != null &&
                supplyChain.getCostIncurred().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Cost incurred cannot be negative");
        }

        if (supplyChain.getStageStartDate() != null &&
                supplyChain.getStageEndDate() != null &&
                supplyChain.getStageStartDate().isAfter(supplyChain.getStageEndDate())) {
            throw new ValidationException("Stage start date cannot be after end date");
        }

        // Validate stage order consistency
        if (supplyChain.getStage().getOrder() != supplyChain.getStageOrder()) {
            // Allow flexibility but warn about inconsistency
            // This validation can be relaxed based on business requirements
        }
    }

    private void updateSupplyChainFields(SupplyChain existing, SupplyChain updated) {
        // Update mutable fields only
        if (updated.getStageEndDate() != null) {
            existing.setStageEndDate(updated.getStageEndDate());
        }

        if (StringUtils.hasText(updated.getLocation())) {
            existing.setLocation(updated.getLocation());
        }

        if (StringUtils.hasText(updated.getFacilityName())) {
            existing.setFacilityName(updated.getFacilityName());
        }

        if (updated.getQuantityIn() != null) {
            existing.setQuantityIn(updated.getQuantityIn());
        }

        if (updated.getQuantityOut() != null) {
            existing.setQuantityOut(updated.getQuantityOut());
        }

        if (StringUtils.hasText(updated.getUnit())) {
            existing.setUnit(updated.getUnit());
        }

        if (updated.getLossQuantity() != null) {
            existing.setLossQuantity(updated.getLossQuantity());
        }

        if (updated.getLossPercentage() != null) {
            existing.setLossPercentage(updated.getLossPercentage());
        }

        if (StringUtils.hasText(updated.getLossReason())) {
            existing.setLossReason(updated.getLossReason());
        }

        if (updated.getQualityStatus() != null) {
            existing.setQualityStatus(updated.getQualityStatus());
        }

        if (StringUtils.hasText(updated.getQualityTests())) {
            existing.setQualityTests(updated.getQualityTests());
        }

        if (StringUtils.hasText(updated.getStorageConditions())) {
            existing.setStorageConditions(updated.getStorageConditions());
        }

        if (StringUtils.hasText(updated.getTransportMethod())) {
            existing.setTransportMethod(updated.getTransportMethod());
        }

        if (StringUtils.hasText(updated.getResponsibleParty())) {
            existing.setResponsibleParty(updated.getResponsibleParty());
        }

        if (updated.getCostIncurred() != null) {
            existing.setCostIncurred(updated.getCostIncurred());
        }

        if (StringUtils.hasText(updated.getTemperatureLog())) {
            existing.setTemperatureLog(updated.getTemperatureLog());
        }

        if (StringUtils.hasText(updated.getHumidityLog())) {
            existing.setHumidityLog(updated.getHumidityLog());
        }

        if (StringUtils.hasText(updated.getHandlingNotes())) {
            existing.setHandlingNotes(updated.getHandlingNotes());
        }

        if (StringUtils.hasText(updated.getComplianceCertificates())) {
            existing.setComplianceCertificates(updated.getComplianceCertificates());
        }

        if (updated.getInsuranceCoverage() != null) {
            existing.setInsuranceCoverage(updated.getInsuranceCoverage());
        }

        if (StringUtils.hasText(updated.getNextStageLocation())) {
            existing.setNextStageLocation(updated.getNextStageLocation());
        }
    }
}