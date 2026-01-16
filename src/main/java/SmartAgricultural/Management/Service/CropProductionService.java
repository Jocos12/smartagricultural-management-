package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.CropProduction;
import SmartAgricultural.Management.Repository.CropProductionRepository;
import SmartAgricultural.Management.dto.CropProductionStatsDTO;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.exception.DuplicateResourceException;
import SmartAgricultural.Management.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des productions de cultures
 *
 * @author SmartAgricultural Management System
 * @version 1.0
 * @since 2024-01-01
 */
@Service
@Transactional
public class CropProductionService {

    @Autowired
    private CropProductionRepository cropProductionRepository;


    // ==================== CRUD OPERATIONS ====================

    /**
     * Créer une nouvelle production
     */
    public CropProduction createCropProduction(CropProduction production) {
        validateCropProduction(production);

        // Vérifier l'unicité du code de production
        if (production.getProductionCode() != null &&
                cropProductionRepository.findByProductionCode(production.getProductionCode()).isPresent()) {
            throw new DuplicateResourceException(
                    "Une production avec le code '" + production.getProductionCode() + "' existe déjà");
        }

        return cropProductionRepository.save(production);
    }

    /**
     * Obtenir toutes les productions avec pagination
     */
    @Transactional(readOnly = true)
    public Page<CropProduction> getAllCropProductions(Pageable pageable) {
        return cropProductionRepository.findAll(pageable);
    }

    /**
     * Obtenir toutes les productions
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getAllCropProductions() {
        return cropProductionRepository.findAll();
    }

    /**
     * Obtenir une production par ID
     */
    @Transactional(readOnly = true)
    public CropProduction getCropProductionById(String id) {
        return cropProductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Production non trouvée avec l'ID: " + id));
    }

    /**
     * Obtenir une production par code
     */
    @Transactional(readOnly = true)
    public Optional<CropProduction> getCropProductionByCode(String productionCode) {
        return cropProductionRepository.findByProductionCode(productionCode);
    }

    /**
     * Mettre à jour une production
     */
    public CropProduction updateCropProduction(String id, CropProduction updatedProduction) {
        CropProduction existingProduction = getCropProductionById(id);

        validateCropProduction(updatedProduction);

        // Vérifier l'unicité du code de production (si changé)
        if (updatedProduction.getProductionCode() != null &&
                !updatedProduction.getProductionCode().equals(existingProduction.getProductionCode())) {
            Optional<CropProduction> existing = cropProductionRepository.findByProductionCode(updatedProduction.getProductionCode());
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new DuplicateResourceException(
                        "Une production avec le code '" + updatedProduction.getProductionCode() + "' existe déjà");
            }
        }

        // Mettre à jour les champs
        updateProductionFields(existingProduction, updatedProduction);

        return cropProductionRepository.save(existingProduction);
    }

    /**
     * Supprimer une production
     */
    public void deleteCropProduction(String id) {
        CropProduction production = getCropProductionById(id);
        cropProductionRepository.delete(production);
    }

    // ==================== SEARCH OPERATIONS ====================

    /**
     * Obtenir les productions par ferme
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getProductionsByFarm(String farmId) {
        return cropProductionRepository.findByFarmIdOrderByCreatedAtDesc(farmId);
    }


    /**
     * Obtenir les productions par ID de l'agriculteur (User ID)
     */
    public List<CropProduction> getProductionsByFarmerId(String userId) {
        return cropProductionRepository.findAll()
                .stream()
                .filter(p -> p.getFarmId() != null && p.getFarmId().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les productions par culture
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getProductionsByCrop(String cropId) {
        return cropProductionRepository.findByCropIdOrderByCreatedAtDesc(cropId);
    }

    /**
     * Obtenir les productions par statut
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getProductionsByStatus(CropProduction.ProductionStatus status) {
        return cropProductionRepository.findByProductionStatusOrderByCreatedAtDesc(status);
    }

    /**
     * Obtenir les productions par saison et année
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getProductionsBySeasonAndYear(CropProduction.Season season, Integer year) {
        return cropProductionRepository.findBySeasonAndYearOrderByCreatedAtDesc(season, year);
    }

    /**
     * Obtenir les productions par année
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getProductionsByYear(Integer year) {
        return cropProductionRepository.findByYearOrderByCreatedAtDesc(year);
    }

    // ==================== DATE RANGE OPERATIONS ====================

    /**
     * Obtenir les productions par période de plantation
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getProductionsByPlantingDateRange(LocalDate startDate, LocalDate endDate) {
        return cropProductionRepository.findByPlantingDateBetweenOrderByPlantingDateDesc(startDate, endDate);
    }

    /**
     * Obtenir les productions par période de récolte attendue
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getProductionsByExpectedHarvestDateRange(LocalDate startDate, LocalDate endDate) {
        return cropProductionRepository.findByExpectedHarvestDateBetweenOrderByExpectedHarvestDateDesc(startDate, endDate);
    }

    // ==================== STATUS-BASED OPERATIONS ====================

    /**
     * Obtenir les productions actives
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getActiveProductions() {
        return cropProductionRepository.findByProductionStatusInOrderByCreatedAtDesc(
                List.of(CropProduction.ProductionStatus.PLANTED, CropProduction.ProductionStatus.GROWING));
    }

    /**
     * Obtenir les productions récoltées
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getHarvestedProductions() {
        return cropProductionRepository.findByProductionStatusInOrderByCreatedAtDesc(
                List.of(CropProduction.ProductionStatus.HARVESTED, CropProduction.ProductionStatus.SOLD));
    }

    /**
     * Obtenir les productions en retard
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getOverdueProductions() {
        LocalDate today = LocalDate.now();
        return cropProductionRepository.findOverdueProductions(today);
    }

    /**
     * Obtenir les productions à récolter bientôt
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getProductionsToHarvestSoon(Integer days) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        return cropProductionRepository.findProductionsToHarvestSoon(today, futureDate);
    }

    // ==================== PRODUCTION METHOD OPERATIONS ====================

    /**
     * Obtenir les productions par méthode
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getProductionsByMethod(CropProduction.ProductionMethod method) {
        return cropProductionRepository.findByProductionMethodOrderByCreatedAtDesc(method);
    }

    /**
     * Obtenir les productions biologiques
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getOrganicProductions() {
        return getProductionsByMethod(CropProduction.ProductionMethod.ORGANIC);
    }

    /**
     * Obtenir les productions certifiées
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getCertifiedProductions() {
        return cropProductionRepository.findByCertificationIsNotNullOrderByCreatedAtDesc();
    }

    // ==================== YIELD OPERATIONS ====================

    /**
     * Obtenir les productions avec rendement élevé
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getProductionsWithHighYield(BigDecimal minYield) {
        return cropProductionRepository.findByActualYieldGreaterThanEqualOrderByActualYieldDesc(minYield);
    }

    /**
     * Obtenir les productions avec rendement faible
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getProductionsWithLowYield(BigDecimal maxYield) {
        return cropProductionRepository.findByActualYieldLessThanEqualOrderByActualYieldAsc(maxYield);
    }

    /**
     * Obtenir les productions par plage de rendement
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getProductionsByYieldRange(BigDecimal minYield, BigDecimal maxYield) {
        return cropProductionRepository.findByActualYieldBetweenOrderByActualYieldDesc(minYield, maxYield);
    }

    // ==================== AREA OPERATIONS ====================

    /**
     * Obtenir les productions par plage de superficie
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getProductionsByAreaRange(BigDecimal minArea, BigDecimal maxArea) {
        return cropProductionRepository.findByAreaPlantedBetweenOrderByAreaPlantedDesc(minArea, maxArea);
    }

    /**
     * Obtenir les productions à grande échelle
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getLargeScaleProductions(BigDecimal minArea) {
        return cropProductionRepository.findByAreaPlantedGreaterThanEqualOrderByAreaPlantedDesc(minArea);
    }

    // ==================== BATCH OPERATIONS ====================

    /**
     * Mettre à jour le statut de plusieurs productions
     */
    public List<CropProduction> updateProductionStatus(List<String> productionIds,
                                                       CropProduction.ProductionStatus newStatus) {
        return productionIds.stream()
                .map(this::getCropProductionById)
                .map(production -> {
                    production.setProductionStatus(newStatus);
                    return cropProductionRepository.save(production);
                })
                .collect(Collectors.toList());
    }

    /**
     * Marquer des productions comme récoltées
     */
    public List<CropProduction> markAsHarvested(List<String> productionIds, LocalDate harvestDate, BigDecimal actualYield) {
        return productionIds.stream()
                .map(this::getCropProductionById)
                .map(production -> {
                    production.setActualHarvestDate(harvestDate);
                    production.setProductionStatus(CropProduction.ProductionStatus.HARVESTED);
                    if (actualYield != null) {
                        production.setActualYield(actualYield);
                    }
                    return cropProductionRepository.save(production);
                })
                .collect(Collectors.toList());
    }

    // ==================== ANALYTICS OPERATIONS ====================

    /**
     * Obtenir les statistiques de production
     */
    @Transactional(readOnly = true)
    public CropProductionStatsDTO getProductionStatistics() {
        CropProductionStatsDTO stats = new CropProductionStatsDTO();

        List<CropProduction> allProductions = getAllCropProductions();

        stats.setTotalProductions((long) allProductions.size());
        stats.setActiveProductions((long) getActiveProductions().size());
        stats.setHarvestedProductions((long) getHarvestedProductions().size());
        stats.setOverdueProductions((long) getOverdueProductions().size());
        stats.setOrganicProductions((long) getOrganicProductions().size());

        // Calculer les moyennes
        BigDecimal avgYield = calculateAverageYield(allProductions);
        stats.setAverageYield(avgYield);

        BigDecimal totalArea = allProductions.stream()
                .map(CropProduction::getAreaPlanted)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalAreaPlanted(totalArea);

        BigDecimal totalProduction = allProductions.stream()
                .filter(p -> p.getTotalProduction() != null)
                .map(CropProduction::getTotalProduction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalProduction(totalProduction);

        return stats;
    }

    /**
     * Obtenir les statistiques par ferme
     */
    @Transactional(readOnly = true)
    public CropProductionStatsDTO getProductionStatisticsByFarm(String farmId) {
        List<CropProduction> farmProductions = getProductionsByFarm(farmId);
        return calculateStatsForProductions(farmProductions);
    }

    /**
     * Obtenir les statistiques par culture
     */
    @Transactional(readOnly = true)
    public CropProductionStatsDTO getProductionStatisticsByCrop(String cropId) {
        List<CropProduction> cropProductions = getProductionsByCrop(cropId);
        return calculateStatsForProductions(cropProductions);
    }

    /**
     * Obtenir le rendement moyen par culture
     */
    @Transactional(readOnly = true)
    public BigDecimal getAverageYieldByCrop(String cropId) {
        List<CropProduction> productions = getProductionsByCrop(cropId);
        return calculateAverageYield(productions);
    }

    /**
     * Obtenir la production totale par année
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalProductionByYear(Integer year) {
        List<CropProduction> yearProductions = getProductionsByYear(year);
        return yearProductions.stream()
                .filter(p -> p.getTotalProduction() != null)
                .map(CropProduction::getTotalProduction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ==================== SEARCH & FILTER OPERATIONS ====================

    /**
     * Recherche avec filtres multiples
     */
    @Transactional(readOnly = true)
    public Page<CropProduction> findWithFilters(String farmId, String cropId,
                                                CropProduction.ProductionStatus status,
                                                CropProduction.Season season, Integer year,
                                                CropProduction.ProductionMethod method,
                                                LocalDate plantingDateFrom, LocalDate plantingDateTo,
                                                BigDecimal minYield, BigDecimal maxYield,
                                                Pageable pageable) {
        return cropProductionRepository.findWithFilters(farmId, cropId, status, season, year, method,
                plantingDateFrom, plantingDateTo, minYield, maxYield, pageable);
    }

    /**
     * Rechercher des productions
     */
    @Transactional(readOnly = true)
    public List<CropProduction> searchProductions(String query) {
        return cropProductionRepository.searchProductions(query);
    }

    // ==================== UTILITY OPERATIONS ====================

    /**
     * Générer un code de production
     */
    public String generateProductionCode(String farmCode, String cropCode,
                                         CropProduction.Season season, Integer year) {
        if (farmCode == null || cropCode == null || season == null || year == null) {
            throw new ValidationException("Tous les paramètres sont requis pour générer un code de production");
        }

        return String.format("%s_%s_%s_%d",
                farmCode.substring(0, Math.min(5, farmCode.length())),
                cropCode.substring(0, Math.min(5, cropCode.length())),
                season.name().substring(season.name().length() - 1),
                year
        );
    }

    /**
     * Obtenir les productions récentes
     */
    @Transactional(readOnly = true)
    public List<CropProduction> getMostRecentProductions(int limit) {
        return cropProductionRepository.findTopByOrderByCreatedAtDesc(limit);
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Valider une production
     */
    private void validateCropProduction(CropProduction production) {
        if (production == null) {
            throw new ValidationException("La production ne peut pas être nulle");
        }

        if (production.getPlantingDate() != null && production.getExpectedHarvestDate() != null &&
                production.getPlantingDate().isAfter(production.getExpectedHarvestDate())) {
            throw new ValidationException("La date de plantation ne peut pas être postérieure à la date de récolte attendue");
        }

        if (production.getActualHarvestDate() != null && production.getPlantingDate() != null &&
                production.getActualHarvestDate().isBefore(production.getPlantingDate())) {
            throw new ValidationException("La date de récolte réelle ne peut pas être antérieure à la date de plantation");
        }

        if (production.getAreaPlanted() != null && production.getAreaPlanted().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("La superficie plantée doit être supérieure à zéro");
        }

        if (production.getExpectedYield() != null && production.getExpectedYield().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Le rendement attendu ne peut pas être négatif");
        }

        if (production.getActualYield() != null && production.getActualYield().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Le rendement réel ne peut pas être négatif");
        }
    }

    /**
     * Mettre à jour les champs d'une production
     */
    private void updateProductionFields(CropProduction existing, CropProduction updated) {
        if (updated.getFarmId() != null) {
            existing.setFarmId(updated.getFarmId());
        }
        if (updated.getCropId() != null) {
            existing.setCropId(updated.getCropId());
        }
        if (updated.getProductionCode() != null) {
            existing.setProductionCode(updated.getProductionCode());
        }
        if (updated.getPlantingDate() != null) {
            existing.setPlantingDate(updated.getPlantingDate());
        }
        if (updated.getExpectedHarvestDate() != null) {
            existing.setExpectedHarvestDate(updated.getExpectedHarvestDate());
        }
        if (updated.getActualHarvestDate() != null) {
            existing.setActualHarvestDate(updated.getActualHarvestDate());
        }
        if (updated.getAreaPlanted() != null) {
            existing.setAreaPlanted(updated.getAreaPlanted());
        }
        if (updated.getExpectedYield() != null) {
            existing.setExpectedYield(updated.getExpectedYield());
        }
        if (updated.getActualYield() != null) {
            existing.setActualYield(updated.getActualYield());
        }
        if (updated.getTotalProduction() != null) {
            existing.setTotalProduction(updated.getTotalProduction());
        }
        if (updated.getProductionStatus() != null) {
            existing.setProductionStatus(updated.getProductionStatus());
        }
        if (updated.getSeason() != null) {
            existing.setSeason(updated.getSeason());
        }
        if (updated.getYear() != null) {
            existing.setYear(updated.getYear());
        }
        if (updated.getSeedVariety() != null) {
            existing.setSeedVariety(updated.getSeedVariety());
        }
        if (updated.getSeedSource() != null) {
            existing.setSeedSource(updated.getSeedSource());
        }
        if (updated.getProductionMethod() != null) {
            existing.setProductionMethod(updated.getProductionMethod());
        }
        if (updated.getCertification() != null) {
            existing.setCertification(updated.getCertification());
        }
        if (updated.getNotes() != null) {
            existing.setNotes(updated.getNotes());
        }
    }

    /**
     * Calculer le rendement moyen
     */
    private BigDecimal calculateAverageYield(List<CropProduction> productions) {
        List<BigDecimal> yields = productions.stream()
                .filter(p -> p.getActualYield() != null)
                .map(CropProduction::getActualYield)
                .collect(Collectors.toList());

        if (yields.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = yields.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(yields.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculer les statistiques pour une liste de productions
     */
    private CropProductionStatsDTO calculateStatsForProductions(List<CropProduction> productions) {
        CropProductionStatsDTO stats = new CropProductionStatsDTO();

        stats.setTotalProductions((long) productions.size());

        long activeCount = productions.stream()
                .filter(p -> p.getProductionStatus() == CropProduction.ProductionStatus.PLANTED ||
                        p.getProductionStatus() == CropProduction.ProductionStatus.GROWING)
                .count();
        stats.setActiveProductions(activeCount);

        long harvestedCount = productions.stream()
                .filter(p -> p.getProductionStatus() == CropProduction.ProductionStatus.HARVESTED ||
                        p.getProductionStatus() == CropProduction.ProductionStatus.SOLD)
                .count();
        stats.setHarvestedProductions(harvestedCount);

        long organicCount = productions.stream()
                .filter(p -> p.getProductionMethod() == CropProduction.ProductionMethod.ORGANIC)
                .count();
        stats.setOrganicProductions(organicCount);

        BigDecimal avgYield = calculateAverageYield(productions);
        stats.setAverageYield(avgYield);

        BigDecimal totalArea = productions.stream()
                .map(CropProduction::getAreaPlanted)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalAreaPlanted(totalArea);

        BigDecimal totalProduction = productions.stream()
                .filter(p -> p.getTotalProduction() != null)
                .map(CropProduction::getTotalProduction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalProduction(totalProduction);

        return stats;
    }

    // ✅ AJOUTER cette méthode à la fin de la classe CropProductionService

    /**
     * Get all crop production IDs for specific farm IDs
     */
    @Transactional(readOnly = true)
    public List<String> getCropProductionIdsByFarmIds(List<String> farmIds) {
        if (farmIds == null || farmIds.isEmpty()) {
            return List.of();
        }

        return farmIds.stream()
                .flatMap(farmId -> cropProductionRepository.findByFarmIdOrderByCreatedAtDesc(farmId).stream())
                .map(CropProduction::getId)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
    }
}