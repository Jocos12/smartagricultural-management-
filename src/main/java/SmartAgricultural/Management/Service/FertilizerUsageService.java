package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.FertilizerUsage;
import SmartAgricultural.Management.Model.FertilizerUsage.FertilizerType;
import SmartAgricultural.Management.Model.FertilizerUsage.Unit;
import SmartAgricultural.Management.Model.FertilizerUsage.ApplicationMethod;
import SmartAgricultural.Management.Model.FertilizerUsage.EffectivenessLevel;
import SmartAgricultural.Management.Repository.FertilizerUsageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class FertilizerUsageService {

    @Autowired
    private FertilizerUsageRepository fertilizerUsageRepository;

    // Basic CRUD operations
    public FertilizerUsage createFertilizerUsage(FertilizerUsage fertilizerUsage) {
        validateFertilizerUsage(fertilizerUsage);
        return fertilizerUsageRepository.save(fertilizerUsage);
    }

    public Optional<FertilizerUsage> findFertilizerUsageById(String id) {
        return fertilizerUsageRepository.findById(id);
    }

    public FertilizerUsage getFertilizerUsageById(String id) {
        return fertilizerUsageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fertilizer usage not found with id: " + id));
    }


    /**
     * Trouve tous les IDs de crop production pour un fermier donné
     */
    public List<String> findCropProductionIdsByFarmerId(String farmerId) {
        // Cette méthode doit appeler le service CropProduction
        // ou la base de données pour récupérer les IDs

        // OPTION 1 : Si vous avez un CropProductionService
        // return cropProductionService.findAllIdsByFarmerId(farmerId);

        // OPTION 2 : Si vous devez le faire via repository
        // Pour l'instant, on retourne une liste vide
        return new ArrayList<>();
    }






    /**
     * Trouve tous les fertilizer usages pour un fermier donné avec pagination
     */
    public Page<FertilizerUsage> findFertilizerUsagesByFarmer(String farmerId, int page, int size) {
        try {
            // Récupérer les IDs des productions du fermier
            List<String> cropProductionIds = findCropProductionIdsByFarmerId(farmerId);

            if (cropProductionIds.isEmpty()) {
                return Page.empty(PageRequest.of(page, size));
            }

            // Récupérer tous les fertilizer usages pour ces productions
            // On suppose qu'il y a une méthode dans le repository
            Pageable pageable = PageRequest.of(page, size, Sort.by("applicationDate").descending());
            return fertilizerUsageRepository.findByCropProductionIdIn(cropProductionIds, pageable);

        } catch (Exception e) {
            e.printStackTrace();
            return Page.empty(PageRequest.of(page, size));
        }
    }

    public List<FertilizerUsage> findAllFertilizerUsages() {
        return fertilizerUsageRepository.findAll();
    }

    public Page<FertilizerUsage> findAllFertilizerUsages(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return fertilizerUsageRepository.findAll(pageable);
    }

    public FertilizerUsage updateFertilizerUsage(String id, FertilizerUsage updatedFertilizerUsage) {
        FertilizerUsage existingFertilizerUsage = getFertilizerUsageById(id);
        updateFertilizerUsageFields(existingFertilizerUsage, updatedFertilizerUsage);
        validateFertilizerUsage(existingFertilizerUsage);
        return fertilizerUsageRepository.save(existingFertilizerUsage);
    }

    public void deleteFertilizerUsage(String id) {
        if (!fertilizerUsageRepository.existsById(id)) {
            throw new RuntimeException("Fertilizer usage not found with id: " + id);
        }
        fertilizerUsageRepository.deleteById(id);
    }

    // Crop production related services
    public List<FertilizerUsage> findFertilizerUsagesByCropProductionId(String cropProductionId) {
        return fertilizerUsageRepository.findByCropProductionId(cropProductionId);
    }

    public Page<FertilizerUsage> findFertilizerUsagesByCropProductionId(String cropProductionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("applicationDate").descending());
        return fertilizerUsageRepository.findByCropProductionId(cropProductionId, pageable);
    }

    public Optional<FertilizerUsage> findFertilizerUsageByIdAndCropProductionId(String id, String cropProductionId) {
        return fertilizerUsageRepository.findByIdAndCropProductionId(id, cropProductionId);
    }

    public void deleteFertilizerUsagesByCropProductionId(String cropProductionId) {
        fertilizerUsageRepository.deleteByCropProductionId(cropProductionId);
    }

    // Fertilizer type services
    public List<FertilizerUsage> findFertilizerUsagesByType(FertilizerType fertilizerType) {
        return fertilizerUsageRepository.findByFertilizerType(fertilizerType);
    }

    public Page<FertilizerUsage> findFertilizerUsagesByType(FertilizerType fertilizerType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return fertilizerUsageRepository.findByFertilizerType(fertilizerType, pageable);
    }

    public List<FertilizerUsage> findFertilizerUsagesByTypeAndCropProduction(FertilizerType fertilizerType, String cropProductionId) {
        return fertilizerUsageRepository.findByFertilizerTypeAndCropProductionId(fertilizerType, cropProductionId);
    }

    // Fertilizer name and brand services
    public List<FertilizerUsage> findFertilizerUsagesByName(String fertilizerName) {
        return fertilizerUsageRepository.findByFertilizerName(fertilizerName);
    }

    public List<FertilizerUsage> searchFertilizerUsagesByName(String fertilizerName) {
        return fertilizerUsageRepository.findByFertilizerNameContaining(fertilizerName);
    }

    public List<FertilizerUsage> findFertilizerUsagesByBrand(String brand) {
        return fertilizerUsageRepository.findByBrand(brand);
    }

    public List<FertilizerUsage> searchFertilizerUsagesByBrand(String brand) {
        return fertilizerUsageRepository.findByBrandContaining(brand);
    }

    public List<FertilizerUsage> findFertilizerUsagesByNameAndBrand(String fertilizerName, String brand) {
        return fertilizerUsageRepository.findByFertilizerNameAndBrand(fertilizerName, brand);
    }

    // Application method services
    public List<FertilizerUsage> findFertilizerUsagesByApplicationMethod(ApplicationMethod applicationMethod) {
        return fertilizerUsageRepository.findByApplicationMethod(applicationMethod);
    }

    public Page<FertilizerUsage> findFertilizerUsagesByApplicationMethod(ApplicationMethod applicationMethod, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return fertilizerUsageRepository.findByApplicationMethod(applicationMethod, pageable);
    }

    public List<FertilizerUsage> findFertilizerUsagesByApplicationMethodAndCropProduction(ApplicationMethod applicationMethod, String cropProductionId) {
        return fertilizerUsageRepository.findByApplicationMethodAndCropProductionId(applicationMethod, cropProductionId);
    }

    // Application stage services
    public List<FertilizerUsage> findFertilizerUsagesByApplicationStage(String applicationStage) {
        return fertilizerUsageRepository.findByApplicationStage(applicationStage);
    }

    public List<FertilizerUsage> searchFertilizerUsagesByApplicationStage(String applicationStage) {
        return fertilizerUsageRepository.findByApplicationStageContaining(applicationStage);
    }

    // Date-based services
    public List<FertilizerUsage> findFertilizerUsagesByApplicationDate(LocalDate applicationDate) {
        return fertilizerUsageRepository.findByApplicationDate(applicationDate);
    }

    public List<FertilizerUsage> findFertilizerUsagesByApplicationDateBetween(LocalDate startDate, LocalDate endDate) {
        return fertilizerUsageRepository.findByApplicationDateBetween(startDate, endDate);
    }

    public List<FertilizerUsage> findFertilizerUsagesAfterDate(LocalDate date) {
        return fertilizerUsageRepository.findByApplicationDateAfter(date);
    }

    public List<FertilizerUsage> findFertilizerUsagesBeforeDate(LocalDate date) {
        return fertilizerUsageRepository.findByApplicationDateBefore(date);
    }

    public List<FertilizerUsage> findFertilizerUsagesCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return fertilizerUsageRepository.findByCreatedAtBetween(startDate, endDate);
    }

    public List<FertilizerUsage> findRecentFertilizerUsages(int days) {
        LocalDate sinceDate = LocalDate.now().minusDays(days);
        return fertilizerUsageRepository.findRecentApplications(sinceDate);
    }

    // Quantity and cost services
    public List<FertilizerUsage> findFertilizerUsagesByQuantityGreaterThan(BigDecimal quantity) {
        return fertilizerUsageRepository.findByQuantityGreaterThan(quantity);
    }

    public List<FertilizerUsage> findFertilizerUsagesByQuantityRange(BigDecimal minQuantity, BigDecimal maxQuantity) {
        return fertilizerUsageRepository.findByQuantityBetween(minQuantity, maxQuantity);
    }

    public List<FertilizerUsage> findFertilizerUsagesByUnit(Unit unit) {
        return fertilizerUsageRepository.findByUnit(unit);
    }

    public List<FertilizerUsage> findFertilizerUsagesByTotalCostGreaterThan(BigDecimal cost) {
        return fertilizerUsageRepository.findByTotalCostGreaterThan(cost);
    }

    public List<FertilizerUsage> findFertilizerUsagesByCostRange(BigDecimal minCost, BigDecimal maxCost) {
        return fertilizerUsageRepository.findByTotalCostBetween(minCost, maxCost);
    }

    public List<FertilizerUsage> findHighCostFertilizerUsages(BigDecimal threshold) {
        return fertilizerUsageRepository.findHighCostFertilizers(threshold);
    }

    // Effectiveness services
    public List<FertilizerUsage> findFertilizerUsagesByEffectivenessRating(Integer rating) {
        return fertilizerUsageRepository.findByEffectivenessRating(rating);
    }

    public List<FertilizerUsage> findFertilizerUsagesByEffectivenessRatingRange(Integer minRating, Integer maxRating) {
        return fertilizerUsageRepository.findByEffectivenessRatingBetween(minRating, maxRating);
    }

    public List<FertilizerUsage> findHighEffectivenessFertilizerUsages(Integer minRating) {
        return fertilizerUsageRepository.findByEffectivenessRatingGreaterThan(minRating);
    }

    public List<FertilizerUsage> findLowEffectivenessFertilizerUsages(Integer maxRating) {
        return fertilizerUsageRepository.findLowEffectivenessFertilizers(maxRating);
    }

    public List<FertilizerUsage> findUnratedFertilizerUsages() {
        return fertilizerUsageRepository.findByEffectivenessRatingIsNull();
    }

    public List<FertilizerUsage> findRatedFertilizerUsages() {
        return fertilizerUsageRepository.findByEffectivenessRatingIsNotNull();
    }

    // Supplier services
    public List<FertilizerUsage> findFertilizerUsagesBySupplier(String supplier) {
        return fertilizerUsageRepository.findBySupplier(supplier);
    }

    public Page<FertilizerUsage> findFertilizerUsagesBySupplier(String supplier, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return fertilizerUsageRepository.findBySupplier(supplier, pageable);
    }

    public List<FertilizerUsage> searchFertilizerUsagesBySupplier(String supplier) {
        return fertilizerUsageRepository.findBySupplierContaining(supplier);
    }

    public List<FertilizerUsage> findFertilizerUsagesBySupplierAndDateRange(String supplier, LocalDate startDate, LocalDate endDate) {
        return fertilizerUsageRepository.findBySupplierAndApplicationDateBetween(supplier, startDate, endDate);
    }

    // Expiry date services
    public List<FertilizerUsage> findFertilizerUsagesByExpiryDate(LocalDate expiryDate) {
        return fertilizerUsageRepository.findByExpiryDate(expiryDate);
    }

    public List<FertilizerUsage> findExpiredFertilizerUsages() {
        return fertilizerUsageRepository.findExpiredFertilizers();
    }

    public List<FertilizerUsage> findExpiringSoonFertilizerUsages(int daysAhead) {
        LocalDate futureDate = LocalDate.now().plusDays(daysAhead);
        return fertilizerUsageRepository.findExpiringSoonFertilizers(futureDate);
    }

    public List<FertilizerUsage> findFertilizerUsagesWithoutExpiry() {
        return fertilizerUsageRepository.findByExpiryDateIsNull();
    }

    public List<FertilizerUsage> findFertilizerUsagesWithExpiry() {
        return fertilizerUsageRepository.findByExpiryDateIsNotNull();
    }

    // Batch number services
    public List<FertilizerUsage> findFertilizerUsagesByBatchNumber(String batchNumber) {
        return fertilizerUsageRepository.findByBatchNumber(batchNumber);
    }

    public List<FertilizerUsage> searchFertilizerUsagesByBatchNumber(String batchNumber) {
        return fertilizerUsageRepository.findByBatchNumberContaining(batchNumber);
    }

    public Optional<FertilizerUsage> findFertilizerUsageByBatchNumberAndSupplier(String batchNumber, String supplier) {
        return fertilizerUsageRepository.findByBatchNumberAndSupplier(batchNumber, supplier);
    }

    // Weather and soil conditions services
    public List<FertilizerUsage> searchFertilizerUsagesByWeatherConditions(String weatherConditions) {
        return fertilizerUsageRepository.findByWeatherConditionsContaining(weatherConditions);
    }

    public List<FertilizerUsage> searchFertilizerUsagesBySoilConditions(String soilConditions) {
        return fertilizerUsageRepository.findBySoilConditionsContaining(soilConditions);
    }

    // Operator services
    public List<FertilizerUsage> findFertilizerUsagesByOperator(String operatorName) {
        return fertilizerUsageRepository.findByOperatorName(operatorName);
    }

    public List<FertilizerUsage> searchFertilizerUsagesByOperator(String operatorName) {
        return fertilizerUsageRepository.findByOperatorNameContaining(operatorName);
    }

    // Statistical services
    public Long countFertilizerUsagesByType(FertilizerType fertilizerType) {
        return fertilizerUsageRepository.countByFertilizerType(fertilizerType);
    }

    public Long countFertilizerUsagesByCropProduction(String cropProductionId) {
        return fertilizerUsageRepository.countByCropProductionId(cropProductionId);
    }

    public Long countFertilizerUsagesByApplicationMethod(ApplicationMethod applicationMethod) {
        return fertilizerUsageRepository.countByApplicationMethod(applicationMethod);
    }

    public Long countHighEffectivenessFertilizerUsages(Integer minRating) {
        return fertilizerUsageRepository.countByEffectivenessRatingGreaterThanEqual(minRating);
    }

    public Long countFertilizerUsagesBySupplier(String supplier) {
        return fertilizerUsageRepository.countBySupplier(supplier);
    }

    public Long countTotalFertilizerUsages() {
        return fertilizerUsageRepository.count();
    }

    // Sum and average services
    public BigDecimal getTotalQuantityByCropProductionIdAndUnit(String cropProductionId, Unit unit) {
        return fertilizerUsageRepository.getTotalQuantityByCropProductionIdAndUnit(cropProductionId, unit);
    }

    public BigDecimal getTotalCostByCropProductionId(String cropProductionId) {
        return fertilizerUsageRepository.getTotalCostByCropProductionId(cropProductionId);
    }

    public BigDecimal getTotalQuantityByFertilizerTypeAndUnit(FertilizerType fertilizerType, Unit unit) {
        return fertilizerUsageRepository.getTotalQuantityByFertilizerTypeAndUnit(fertilizerType, unit);
    }

    public BigDecimal getTotalCostByFertilizerType(FertilizerType fertilizerType) {
        return fertilizerUsageRepository.getTotalCostByFertilizerType(fertilizerType);
    }

    public Double getAverageEffectivenessRatingByFertilizerType(FertilizerType fertilizerType) {
        return fertilizerUsageRepository.getAverageEffectivenessRatingByFertilizerType(fertilizerType);
    }

    public BigDecimal getAverageCostPerUnitByFertilizerType(FertilizerType fertilizerType) {
        return fertilizerUsageRepository.getAverageCostPerUnitByFertilizerType(fertilizerType);
    }

    public BigDecimal getAverageTotalCost() {
        return fertilizerUsageRepository.getAverageTotalCost();
    }

    // Complex query services
    public List<FertilizerUsage> findFertilizerUsagesByCropProductionIdAndType(String cropProductionId, FertilizerType fertilizerType) {
        return fertilizerUsageRepository.findByCropProductionIdAndFertilizerType(cropProductionId, fertilizerType);
    }

    public List<FertilizerUsage> findFertilizerUsagesByTypeAndDateRange(FertilizerType fertilizerType, LocalDate startDate, LocalDate endDate) {
        return fertilizerUsageRepository.findByFertilizerTypeAndApplicationDateBetween(fertilizerType, startDate, endDate);
    }

    public List<FertilizerUsage> findEffectiveAndAffordableFertilizerUsages(Integer minRating, BigDecimal maxCost) {
        return fertilizerUsageRepository.findEffectiveAndAffordableFertilizers(minRating, maxCost);
    }

    // Advanced search services
    public Page<FertilizerUsage> searchFertilizerUsagesByCriteria(String cropProductionId, FertilizerType fertilizerType,
                                                                  ApplicationMethod applicationMethod, String supplier,
                                                                  LocalDate startDate, LocalDate endDate,
                                                                  BigDecimal minCost, BigDecimal maxCost,
                                                                  Integer minRating, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("applicationDate").descending());
        return fertilizerUsageRepository.findByCriteria(cropProductionId, fertilizerType, applicationMethod,
                supplier, startDate, endDate, minCost, maxCost, minRating, pageable);
    }

    // Performance analysis services
    public Page<FertilizerUsage> findTopPerformingFertilizerUsages(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return fertilizerUsageRepository.findTopPerformingFertilizers(pageable);
    }

    public Page<FertilizerUsage> findMostCostEffectiveFertilizerUsages(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return fertilizerUsageRepository.findMostCostEffectiveFertilizers(pageable);
    }

    // Lookup services
    public List<String> getAllDistinctFertilizerNames() {
        return fertilizerUsageRepository.findDistinctFertilizerNames();
    }

    public List<String> getAllDistinctBrands() {
        return fertilizerUsageRepository.findDistinctBrands();
    }

    public List<String> getAllDistinctSuppliers() {
        return fertilizerUsageRepository.findDistinctSuppliers();
    }

    public List<String> getAllDistinctApplicationStages() {
        return fertilizerUsageRepository.findDistinctApplicationStages();
    }

    public List<String> getAllDistinctOperatorNames() {
        return fertilizerUsageRepository.findDistinctOperatorNames();
    }

    // Reporting services
    public List<Object[]> getMonthlyUsageStatistics(LocalDate startDate, LocalDate endDate) {
        return fertilizerUsageRepository.getMonthlyUsageStatistics(startDate, endDate);
    }

    public List<Object[]> getFertilizerPerformanceByType() {
        return fertilizerUsageRepository.getFertilizerPerformanceByType();
    }

    public Map<String, Object> getFertilizerUsageSummary(String cropProductionId) {
        Map<String, Object> summary = new HashMap<>();

        Long totalUsages = countFertilizerUsagesByCropProduction(cropProductionId);
        BigDecimal totalCost = getTotalCostByCropProductionId(cropProductionId);

        summary.put("totalUsages", totalUsages);
        summary.put("totalCost", totalCost);

        // Get usage breakdown by type
        for (FertilizerType type : FertilizerType.values()) {
            Long count = fertilizerUsageRepository.countByFertilizerType(type);
            BigDecimal cost = fertilizerUsageRepository.getTotalCostByFertilizerType(type);

            Map<String, Object> typeData = new HashMap<>();
            typeData.put("count", count);
            typeData.put("totalCost", cost);

            summary.put(type.name().toLowerCase(), typeData);
        }

        return summary;
    }

    public Map<String, Object> getFertilizerEffectivenessAnalysis() {
        Map<String, Object> analysis = new HashMap<>();

        // Fixed: Convert list size to Long instead of int
        Long totalRated = (long) fertilizerUsageRepository.findByEffectivenessRatingIsNotNull().size();
        Long totalUnrated = (long) fertilizerUsageRepository.findByEffectivenessRatingIsNull().size();

        analysis.put("totalRated", totalRated);
        analysis.put("totalUnrated", totalUnrated);

        // Count by effectiveness levels - Fixed: Convert list size to Long
        for (int rating = 1; rating <= 5; rating++) {
            Long count = (long) fertilizerUsageRepository.findByEffectivenessRating(rating).size();
            analysis.put("rating" + rating, count);
        }

        // High and low effectiveness counts - Fixed: Convert list size to Long
        Long highEffectiveness = countHighEffectivenessFertilizerUsages(4);
        Long lowEffectiveness = (long) fertilizerUsageRepository.findLowEffectivenessFertilizers(2).size();

        analysis.put("highEffectiveness", highEffectiveness);
        analysis.put("lowEffectiveness", lowEffectiveness);

        return analysis;
    }

    // Maintenance services
    public void deleteExpiredFertilizerUsages() {
        fertilizerUsageRepository.deleteExpiredFertilizers();
    }

    public void deleteFertilizerUsagesBeforeDate(LocalDate date) {
        fertilizerUsageRepository.deleteByApplicationDateBefore(date);
    }

    public void deleteOldFertilizerUsageRecords(LocalDateTime beforeDate) {
        fertilizerUsageRepository.deleteOldRecords(beforeDate);
    }

    // Utility services
    public boolean existsById(String id) {
        return fertilizerUsageRepository.existsById(id);
    }

    public FertilizerUsage calculateTotalCostForFertilizerUsage(FertilizerUsage fertilizerUsage) {
        if (fertilizerUsage.getCostPerUnit() != null && fertilizerUsage.getQuantity() != null) {
            BigDecimal totalCost = fertilizerUsage.getCostPerUnit().multiply(fertilizerUsage.getQuantity());
            fertilizerUsage.setTotalCost(totalCost);
        }
        return fertilizerUsage;
    }

    public List<FertilizerUsage> findOptimizationCandidates() {
        List<FertilizerUsage> candidates = fertilizerUsageRepository.findAll();
        return candidates.stream()
                .filter(FertilizerUsage::needsOptimization)
                .toList();
    }

    public List<FertilizerUsage> findOrganicFertilizerUsages() {
        return findFertilizerUsagesByType(FertilizerType.ORGANIC);
    }

    // Validation services
    public void validateFertilizerUsageForCrop(String cropProductionId, FertilizerUsage fertilizerUsage) {
        // Check if there are conflicting fertilizer applications on the same date
        List<FertilizerUsage> sameDay = fertilizerUsageRepository.findByApplicationDate(fertilizerUsage.getApplicationDate());
        sameDay = sameDay.stream()
                .filter(fu -> fu.getCropProductionId().equals(cropProductionId))
                .filter(fu -> !fu.getId().equals(fertilizerUsage.getId()))
                .toList();

        if (!sameDay.isEmpty()) {
            // Log warning about multiple applications on same day
            System.out.println("Warning: Multiple fertilizer applications on the same date for crop production: " + cropProductionId);
        }

        // Validate expiry date
        if (fertilizerUsage.isExpired()) {
            throw new IllegalArgumentException("Cannot apply expired fertilizer");
        }
    }

    // Private helper methods
    private void validateFertilizerUsage(FertilizerUsage fertilizerUsage) {
        if (fertilizerUsage == null) {
            throw new IllegalArgumentException("Fertilizer usage cannot be null");
        }
        if (fertilizerUsage.getCropProductionId() == null || fertilizerUsage.getCropProductionId().trim().isEmpty()) {
            throw new IllegalArgumentException("Crop production ID is required");
        }
        if (fertilizerUsage.getFertilizerType() == null) {
            throw new IllegalArgumentException("Fertilizer type is required");
        }
        if (fertilizerUsage.getFertilizerName() == null || fertilizerUsage.getFertilizerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Fertilizer name is required");
        }
        if (fertilizerUsage.getQuantity() == null || fertilizerUsage.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (fertilizerUsage.getUnit() == null) {
            throw new IllegalArgumentException("Unit is required");
        }
        if (fertilizerUsage.getApplicationDate() == null) {
            throw new IllegalArgumentException("Application date is required");
        }
        if (fertilizerUsage.getApplicationMethod() == null) {
            throw new IllegalArgumentException("Application method is required");
        }
        if (fertilizerUsage.getEffectivenessRating() != null &&
                (fertilizerUsage.getEffectivenessRating() < 1 || fertilizerUsage.getEffectivenessRating() > 5)) {
            throw new IllegalArgumentException("Effectiveness rating must be between 1 and 5");
        }
        if (fertilizerUsage.isExpired()) {
            throw new IllegalArgumentException("Cannot apply expired fertilizer");
        }
    }

    private void updateFertilizerUsageFields(FertilizerUsage existingFertilizerUsage, FertilizerUsage updatedFertilizerUsage) {
        if (updatedFertilizerUsage.getCropProductionId() != null) {
            existingFertilizerUsage.setCropProductionId(updatedFertilizerUsage.getCropProductionId());
        }
        if (updatedFertilizerUsage.getFertilizerType() != null) {
            existingFertilizerUsage.setFertilizerType(updatedFertilizerUsage.getFertilizerType());
        }
        if (updatedFertilizerUsage.getFertilizerName() != null) {
            existingFertilizerUsage.setFertilizerName(updatedFertilizerUsage.getFertilizerName());
        }
        if (updatedFertilizerUsage.getBrand() != null) {
            existingFertilizerUsage.setBrand(updatedFertilizerUsage.getBrand());
        }
        if (updatedFertilizerUsage.getComposition() != null) {
            existingFertilizerUsage.setComposition(updatedFertilizerUsage.getComposition());
        }
        if (updatedFertilizerUsage.getQuantity() != null) {
            existingFertilizerUsage.setQuantity(updatedFertilizerUsage.getQuantity());
        }
        if (updatedFertilizerUsage.getUnit() != null) {
            existingFertilizerUsage.setUnit(updatedFertilizerUsage.getUnit());
        }
        if (updatedFertilizerUsage.getApplicationDate() != null) {
            existingFertilizerUsage.setApplicationDate(updatedFertilizerUsage.getApplicationDate());
        }
        if (updatedFertilizerUsage.getApplicationMethod() != null) {
            existingFertilizerUsage.setApplicationMethod(updatedFertilizerUsage.getApplicationMethod());
        }
        if (updatedFertilizerUsage.getApplicationStage() != null) {
            existingFertilizerUsage.setApplicationStage(updatedFertilizerUsage.getApplicationStage());
        }
        if (updatedFertilizerUsage.getCostPerUnit() != null) {
            existingFertilizerUsage.setCostPerUnit(updatedFertilizerUsage.getCostPerUnit());
        }
        if (updatedFertilizerUsage.getTotalCost() != null) {
            existingFertilizerUsage.setTotalCost(updatedFertilizerUsage.getTotalCost());
        }
        if (updatedFertilizerUsage.getSupplier() != null) {
            existingFertilizerUsage.setSupplier(updatedFertilizerUsage.getSupplier());
        }
        if (updatedFertilizerUsage.getBatchNumber() != null) {
            existingFertilizerUsage.setBatchNumber(updatedFertilizerUsage.getBatchNumber());
        }
        if (updatedFertilizerUsage.getExpiryDate() != null) {
            existingFertilizerUsage.setExpiryDate(updatedFertilizerUsage.getExpiryDate());
        }
        if (updatedFertilizerUsage.getWeatherConditions() != null) {
            existingFertilizerUsage.setWeatherConditions(updatedFertilizerUsage.getWeatherConditions());
        }
        if (updatedFertilizerUsage.getSoilConditions() != null) {
            existingFertilizerUsage.setSoilConditions(updatedFertilizerUsage.getSoilConditions());
        }
        if (updatedFertilizerUsage.getOperatorName() != null) {
            existingFertilizerUsage.setOperatorName(updatedFertilizerUsage.getOperatorName());
        }
        if (updatedFertilizerUsage.getEffectivenessRating() != null) {
            existingFertilizerUsage.setEffectivenessRating(updatedFertilizerUsage.getEffectivenessRating());
        }
        if (updatedFertilizerUsage.getNotes() != null) {
            existingFertilizerUsage.setNotes(updatedFertilizerUsage.getNotes());
        }
    }
}