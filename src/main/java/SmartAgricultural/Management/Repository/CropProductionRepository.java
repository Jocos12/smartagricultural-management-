package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.CropProduction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des productions de cultures
 *
 * @author SmartAgricultural Management System
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface CropProductionRepository extends JpaRepository<CropProduction, String> {

    // ==================== BASIC SEARCH METHODS ====================

    /**
     * Rechercher une production par code de production
     */
    Optional<CropProduction> findByProductionCode(String productionCode);

    /**
     * Vérifier si un code de production existe
     */
    boolean existsByProductionCode(String productionCode);

    // ==================== FARM RELATED METHODS ====================

    /**
     * Obtenir toutes les productions d'une ferme
     */
    List<CropProduction> findByFarmIdOrderByCreatedAtDesc(String farmId);

    /**
     * Obtenir les productions d'une ferme avec pagination
     */
    Page<CropProduction> findByFarmId(String farmId, Pageable pageable);

    /**
     * Compter les productions par ferme
     */
    Long countByFarmId(String farmId);

    // ==================== CROP RELATED METHODS ====================

    /**
     * Obtenir toutes les productions d'une culture
     */
    List<CropProduction> findByCropIdOrderByCreatedAtDesc(String cropId);

    /**
     * Obtenir les productions d'une culture avec pagination
     */
    Page<CropProduction> findByCropId(String cropId, Pageable pageable);

    /**
     * Compter les productions par culture
     */
    Long countByCropId(String cropId);

    // ==================== STATUS RELATED METHODS ====================

    /**
     * Obtenir les productions par statut
     */
    List<CropProduction> findByProductionStatusOrderByCreatedAtDesc(CropProduction.ProductionStatus status);

    /**
     * Obtenir les productions par statut avec pagination
     */
    Page<CropProduction> findByProductionStatus(CropProduction.ProductionStatus status, Pageable pageable);

    /**
     * Obtenir les productions par liste de statuts
     */
    List<CropProduction> findByProductionStatusInOrderByCreatedAtDesc(List<CropProduction.ProductionStatus> statuses);

    /**
     * Compter les productions par statut
     */
    Long countByProductionStatus(CropProduction.ProductionStatus status);

    // ==================== SEASON AND YEAR METHODS ====================

    /**
     * Obtenir les productions par saison
     */
    List<CropProduction> findBySeasonOrderByCreatedAtDesc(CropProduction.Season season);

    /**
     * Obtenir les productions par année
     */
    List<CropProduction> findByYearOrderByCreatedAtDesc(Integer year);

    /**
     * Obtenir les productions par saison et année
     */
    List<CropProduction> findBySeasonAndYearOrderByCreatedAtDesc(CropProduction.Season season, Integer year);

    /**
     * Obtenir les productions par ferme, saison et année
     */
    List<CropProduction> findByFarmIdAndSeasonAndYearOrderByCreatedAtDesc(String farmId, CropProduction.Season season, Integer year);

    /**
     * Compter les productions par année
     */
    Long countByYear(Integer year);

    /**
     * Compter les productions par saison et année
     */
    Long countBySeasonAndYear(CropProduction.Season season, Integer year);

    // ==================== DATE RANGE METHODS ====================

    /**
     * Obtenir les productions par période de plantation
     */
    List<CropProduction> findByPlantingDateBetweenOrderByPlantingDateDesc(LocalDate startDate, LocalDate endDate);

    /**
     * Obtenir les productions par période de récolte attendue
     */
    List<CropProduction> findByExpectedHarvestDateBetweenOrderByExpectedHarvestDateDesc(LocalDate startDate, LocalDate endDate);

    /**
     * Obtenir les productions par période de récolte réelle
     */
    List<CropProduction> findByActualHarvestDateBetweenOrderByActualHarvestDateDesc(LocalDate startDate, LocalDate endDate);

    /**
     * Obtenir les productions plantées après une date
     */
    List<CropProduction> findByPlantingDateAfterOrderByPlantingDateDesc(LocalDate date);

    /**
     * Obtenir les productions à récolter avant une date
     */
    List<CropProduction> findByExpectedHarvestDateBeforeOrderByExpectedHarvestDateAsc(LocalDate date);

    // ==================== PRODUCTION METHOD METHODS ====================

    /**
     * Obtenir les productions par méthode de production
     */
    List<CropProduction> findByProductionMethodOrderByCreatedAtDesc(CropProduction.ProductionMethod method);

    /**
     * Obtenir les productions biologiques
     */
    @Query("SELECT cp FROM CropProduction cp WHERE cp.productionMethod = 'ORGANIC' ORDER BY cp.createdAt DESC")
    List<CropProduction> findOrganicProductions();

    /**
     * Obtenir les productions certifiées (certification non nulle)
     */
    List<CropProduction> findByCertificationIsNotNullOrderByCreatedAtDesc();

    /**
     * Obtenir les productions par certification spécifique
     */
    List<CropProduction> findByCertificationOrderByCreatedAtDesc(String certification);

    /**
     * Compter les productions par méthode
     */
    Long countByProductionMethod(CropProduction.ProductionMethod method);

    // ==================== YIELD RELATED METHODS ====================

    /**
     * Obtenir les productions avec rendement supérieur ou égal à une valeur
     */
    List<CropProduction> findByActualYieldGreaterThanEqualOrderByActualYieldDesc(BigDecimal minYield);

    /**
     * Obtenir les productions avec rendement inférieur ou égal à une valeur
     */
    List<CropProduction> findByActualYieldLessThanEqualOrderByActualYieldAsc(BigDecimal maxYield);

    /**
     * Obtenir les productions par plage de rendement
     */
    List<CropProduction> findByActualYieldBetweenOrderByActualYieldDesc(BigDecimal minYield, BigDecimal maxYield);

    /**
     * Obtenir les productions avec rendement attendu supérieur à une valeur
     */
    List<CropProduction> findByExpectedYieldGreaterThanEqualOrderByExpectedYieldDesc(BigDecimal minYield);

    /**
     * Obtenir les productions sans rendement réel
     */
    List<CropProduction> findByActualYieldIsNullOrderByExpectedHarvestDateAsc();

    // ==================== AREA RELATED METHODS ====================

    /**
     * Obtenir les productions par plage de superficie
     */
    List<CropProduction> findByAreaPlantedBetweenOrderByAreaPlantedDesc(BigDecimal minArea, BigDecimal maxArea);

    /**
     * Obtenir les productions avec superficie supérieure ou égale à une valeur
     */
    List<CropProduction> findByAreaPlantedGreaterThanEqualOrderByAreaPlantedDesc(BigDecimal minArea);

    /**
     * Obtenir les productions avec superficie inférieure à une valeur
     */
    List<CropProduction> findByAreaPlantedLessThanOrderByAreaPlantedAsc(BigDecimal maxArea);

    // ==================== COMPLEX QUERY METHODS ====================

    /**
     * Obtenir les productions en retard (date de récolte attendue dépassée et pas encore récoltées)
     */
    @Query("SELECT cp FROM CropProduction cp WHERE cp.expectedHarvestDate < :currentDate " +
            "AND cp.productionStatus NOT IN ('HARVESTED', 'SOLD') ORDER BY cp.expectedHarvestDate ASC")
    List<CropProduction> findOverdueProductions(@Param("currentDate") LocalDate currentDate);

    /**
     * Obtenir les productions à récolter bientôt
     */
    @Query("SELECT cp FROM CropProduction cp WHERE cp.expectedHarvestDate BETWEEN :startDate AND :endDate " +
            "AND cp.productionStatus IN ('PLANTED', 'GROWING') ORDER BY cp.expectedHarvestDate ASC")
    List<CropProduction> findProductionsToHarvestSoon(@Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);

    /**
     * Obtenir les productions récemment plantées
     */
    @Query("SELECT cp FROM CropProduction cp WHERE cp.plantingDate >= :date ORDER BY cp.plantingDate DESC")
    List<CropProduction> findRecentlyPlantedProductions(@Param("date") LocalDate date);

    /**
     * Obtenir les productions avec efficacité de rendement supérieure à un seuil
     */
    @Query("SELECT cp FROM CropProduction cp WHERE cp.actualYield IS NOT NULL AND cp.expectedYield IS NOT NULL " +
            "AND (cp.actualYield / cp.expectedYield) >= :efficiency ORDER BY (cp.actualYield / cp.expectedYield) DESC")
    List<CropProduction> findHighEfficiencyProductions(@Param("efficiency") BigDecimal efficiency);

    /**
     * Obtenir les meilleures productions par rendement pour une culture
     */
    @Query("SELECT cp FROM CropProduction cp WHERE cp.cropId = :cropId AND cp.actualYield IS NOT NULL " +
            "ORDER BY cp.actualYield DESC LIMIT :limit")
    List<CropProduction> findTopProductionsByCropAndYield(@Param("cropId") String cropId, @Param("limit") int limit);

    /**
     * Obtenir les meilleures productions par superficie pour une ferme
     */
    @Query("SELECT cp FROM CropProduction cp WHERE cp.farmId = :farmId " +
            "ORDER BY cp.areaPlanted DESC LIMIT :limit")
    List<CropProduction> findTopProductionsByFarmAndArea(@Param("farmId") String farmId, @Param("limit") int limit);

    // ==================== SEARCH METHODS ====================

    /**
     * Recherche de productions par code de production partiel
     */
    @Query("SELECT cp FROM CropProduction cp WHERE LOWER(cp.productionCode) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "ORDER BY cp.createdAt DESC")
    List<CropProduction> searchByProductionCode(@Param("query") String query);

    /**
     * Recherche générale de productions
     */
    @Query("SELECT cp FROM CropProduction cp WHERE " +
            "LOWER(cp.productionCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(cp.seedVariety) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(cp.seedSource) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(cp.certification) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(cp.notes) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "ORDER BY cp.createdAt DESC")
    List<CropProduction> searchProductions(@Param("query") String query);

    // ==================== STATISTICAL METHODS ====================

    /**
     * Calculer le rendement moyen par culture
     */
    @Query("SELECT AVG(cp.actualYield) FROM CropProduction cp WHERE cp.cropId = :cropId AND cp.actualYield IS NOT NULL")
    BigDecimal findAverageYieldByCrop(@Param("cropId") String cropId);

    /**
     * Calculer la production totale par année
     */
    @Query("SELECT SUM(cp.totalProduction) FROM CropProduction cp WHERE cp.year = :year AND cp.totalProduction IS NOT NULL")
    BigDecimal findTotalProductionByYear(@Param("year") Integer year);

    /**
     * Calculer la superficie totale par ferme et année
     */
    @Query("SELECT SUM(cp.areaPlanted) FROM CropProduction cp WHERE cp.farmId = :farmId AND cp.year = :year")
    BigDecimal findTotalAreaByFarmAndYear(@Param("farmId") String farmId, @Param("year") Integer year);

    /**
     * Obtenir les statistiques de rendement par culture
     */
    @Query("SELECT cp.cropId, AVG(cp.actualYield), MIN(cp.actualYield), MAX(cp.actualYield), COUNT(cp) " +
            "FROM CropProduction cp WHERE cp.actualYield IS NOT NULL GROUP BY cp.cropId")
    List<Object[]> findYieldStatisticsByCrop();

    /**
     * Obtenir les statistiques de production par ferme
     */
    @Query("SELECT cp.farmId, COUNT(cp), SUM(cp.areaPlanted), SUM(cp.totalProduction), AVG(cp.actualYield) " +
            "FROM CropProduction cp GROUP BY cp.farmId")
    List<Object[]> findProductionStatisticsByFarm();

    /**
     * Obtenir les productions par méthode et année
     */
    @Query("SELECT cp.productionMethod, COUNT(cp) FROM CropProduction cp WHERE cp.year = :year GROUP BY cp.productionMethod")
    List<Object[]> findProductionCountByMethodAndYear(@Param("year") Integer year);

    // ==================== ADVANCED FILTER METHOD ====================

    /**
     * Recherche avec filtres multiples
     */
    @Query("SELECT cp FROM CropProduction cp WHERE " +
            "(:farmId IS NULL OR cp.farmId = :farmId) AND " +
            "(:cropId IS NULL OR cp.cropId = :cropId) AND " +
            "(:status IS NULL OR cp.productionStatus = :status) AND " +
            "(:season IS NULL OR cp.season = :season) AND " +
            "(:year IS NULL OR cp.year = :year) AND " +
            "(:method IS NULL OR cp.productionMethod = :method) AND " +
            "(:plantingDateFrom IS NULL OR cp.plantingDate >= :plantingDateFrom) AND " +
            "(:plantingDateTo IS NULL OR cp.plantingDate <= :plantingDateTo) AND " +
            "(:minYield IS NULL OR cp.actualYield >= :minYield) AND " +
            "(:maxYield IS NULL OR cp.actualYield <= :maxYield)")
    Page<CropProduction> findWithFilters(@Param("farmId") String farmId,
                                         @Param("cropId") String cropId,
                                         @Param("status") CropProduction.ProductionStatus status,
                                         @Param("season") CropProduction.Season season,
                                         @Param("year") Integer year,
                                         @Param("method") CropProduction.ProductionMethod method,
                                         @Param("plantingDateFrom") LocalDate plantingDateFrom,
                                         @Param("plantingDateTo") LocalDate plantingDateTo,
                                         @Param("minYield") BigDecimal minYield,
                                         @Param("maxYield") BigDecimal maxYield,
                                         Pageable pageable);

    // ==================== UTILITY METHODS ====================

    /**
     * Obtenir les productions les plus récentes
     */
    @Query("SELECT cp FROM CropProduction cp ORDER BY cp.createdAt DESC LIMIT :limit")
    List<CropProduction> findTopByOrderByCreatedAtDesc(@Param("limit") int limit);

    /**
     * Obtenir les productions par ferme et culture
     */
    List<CropProduction> findByFarmIdAndCropIdOrderByCreatedAtDesc(String farmId, String cropId);

    /**
     * Obtenir les productions actives (plantées ou en croissance)
     */
    @Query("SELECT cp FROM CropProduction cp WHERE cp.productionStatus IN ('PLANTED', 'GROWING') ORDER BY cp.createdAt DESC")
    List<CropProduction> findActiveProductions();

    /**
     * Obtenir les productions terminées (récoltées ou vendues)
     */
    @Query("SELECT cp FROM CropProduction cp WHERE cp.productionStatus IN ('HARVESTED', 'SOLD') ORDER BY cp.actualHarvestDate DESC")
    List<CropProduction> findCompletedProductions();

    /**
     * Vérifier l'existence d'une production pour une ferme, culture, saison et année
     */
    boolean existsByFarmIdAndCropIdAndSeasonAndYear(String farmId, String cropId, CropProduction.Season season, Integer year);

    /**
     * Obtenir la dernière production pour une ferme et une culture
     */
    @Query("SELECT cp FROM CropProduction cp WHERE cp.farmId = :farmId AND cp.cropId = :cropId ORDER BY cp.createdAt DESC LIMIT 1")
    Optional<CropProduction> findLatestProductionByFarmAndCrop(@Param("farmId") String farmId, @Param("cropId") String cropId);

    /**
     * Obtenir les productions avec des notes
     */
    @Query("SELECT cp FROM CropProduction cp WHERE cp.notes IS NOT NULL AND LENGTH(cp.notes) > 0 ORDER BY cp.createdAt DESC")
    List<CropProduction> findProductionsWithNotes();

    /**
     * Compter les productions par statut et année
     */
    Long countByProductionStatusAndYear(CropProduction.ProductionStatus status, Integer year);

    /**
     * Obtenir les années de production disponibles
     */
    @Query("SELECT DISTINCT cp.year FROM CropProduction cp ORDER BY cp.year DESC")
    List<Integer> findAvailableYears();

    /**
     * Obtenir les fermes ayant des productions
     */
    @Query("SELECT DISTINCT cp.farmId FROM CropProduction cp ORDER BY cp.farmId")
    List<String> findFarmsWithProductions();

    /**
     * Obtenir les cultures ayant des productions
     */
    @Query("SELECT DISTINCT cp.cropId FROM CropProduction cp ORDER BY cp.cropId")
    List<String> findCropsWithProductions();
}