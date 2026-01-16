package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.Crop;
import SmartAgricultural.Management.Model.CropProduction;
import SmartAgricultural.Management.Service.CropProductionService;
import SmartAgricultural.Management.Service.CropService;
import SmartAgricultural.Management.Service.UserService;
import SmartAgricultural.Management.dto.CropProductionDTO;
import SmartAgricultural.Management.dto.CropProductionStatsDTO;
import SmartAgricultural.Management.dto.ApiResponse;
import SmartAgricultural.Management.dto.UserDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour la gestion des productions de cultures
 *
 * @author SmartAgricultural Management System
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/v1/crop-productions")
@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
public class CropProductionController {
    private static final Logger logger = LoggerFactory.getLogger(CropProductionController.class);
    @Autowired
    private UserService userService;


    @Autowired
    private CropService cropService;

    @Autowired
    private CropProductionService cropProductionService;

    // ==================== CRUD OPERATIONS ====================

    /**
     * Créer une nouvelle production de culture
     * POST /api/v1/crop-productions
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CropProductionDTO>> createCropProduction(
            @Valid @RequestBody CropProductionDTO cropProductionDTO) {
        CropProduction cropProduction = cropProductionDTO.toEntity();
        CropProduction createdCropProduction = cropProductionService.createCropProduction(cropProduction);
        CropProductionDTO responseDTO = CropProductionDTO.fromEntity(createdCropProduction);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Production de culture créée avec succès", responseDTO));
    }

    /**
     * Obtenir toutes les productions avec pagination
     * GET /api/v1/crop-productions
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CropProductionDTO>>> getAllCropProductions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CropProduction> productions = cropProductionService.getAllCropProductions(pageable);
        Page<CropProductionDTO> productionDTOs = productions.map(CropProductionDTO::fromEntity);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productions récupérées avec succès", productionDTOs));
    }

    /**
     * Obtenir toutes les productions (sans pagination)
     * GET /api/v1/crop-productions/all
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getAllCropProductionsNoPaging() {
        List<CropProduction> productions = cropProductionService.getAllCropProductions();
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Toutes les productions récupérées", productionDTOs));
    }










    /**
     * Obtenir une production par code de production
     * GET /api/v1/crop-productions/code/{productionCode}
     */
    @GetMapping("/code/{productionCode}")
    public ResponseEntity<ApiResponse<CropProductionDTO>> getCropProductionByCode(
            @PathVariable String productionCode) {
        Optional<CropProduction> production = cropProductionService.getCropProductionByCode(productionCode);
        if (production.isPresent()) {
            CropProductionDTO productionDTO = CropProductionDTO.fromEntity(production.get());
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Production trouvée", productionDTO));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Mettre à jour une production
     * PUT /api/v1/crop-productions/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CropProductionDTO>> updateCropProduction(
            @PathVariable String id,
            @Valid @RequestBody CropProductionDTO cropProductionDTO) {

        CropProduction production = cropProductionDTO.toEntity();
        CropProduction updatedProduction = cropProductionService.updateCropProduction(id, production);
        CropProductionDTO responseDTO = CropProductionDTO.fromEntity(updatedProduction);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Production mise à jour avec succès", responseDTO));
    }

    /**
     * Supprimer une production
     * DELETE /api/v1/crop-productions/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCropProduction(@PathVariable String id) {
        cropProductionService.deleteCropProduction(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Production supprimée avec succès", null));
    }

    // ==================== SEARCH OPERATIONS ====================

    /**
     * Rechercher des productions par ferme
     * GET /api/v1/crop-productions/farm/{farmId}
     */
    @GetMapping("/farm/{farmId}")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getProductionsByFarm(
            @PathVariable String farmId) {
        List<CropProduction> productions = cropProductionService.getProductionsByFarm(farmId);
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productions de la ferme " + farmId, productionDTOs));
    }

    /**
     * Rechercher des productions par culture
     * GET /api/v1/crop-productions/crop/{cropId}
     */
    @GetMapping("/crop/{cropId}")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getProductionsByCrop(
            @PathVariable String cropId) {
        List<CropProduction> productions = cropProductionService.getProductionsByCrop(cropId);
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productions de la culture " + cropId, productionDTOs));
    }

    /**
     * Rechercher des productions par statut
     * GET /api/v1/crop-productions/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getProductionsByStatus(
            @PathVariable CropProduction.ProductionStatus status) {
        List<CropProduction> productions = cropProductionService.getProductionsByStatus(status);
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productions avec statut " + status.getDisplayName(), productionDTOs));
    }

    /**
     * Rechercher des productions par saison et année
     * GET /api/v1/crop-productions/season/{season}/year/{year}
     */
    @GetMapping("/season/{season}/year/{year}")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getProductionsBySeasonAndYear(
            @PathVariable CropProduction.Season season,
            @PathVariable Integer year) {
        List<CropProduction> productions = cropProductionService.getProductionsBySeasonAndYear(season, year);
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Productions de " + season.getDisplayName() + " " + year, productionDTOs));
    }

    /**
     * Rechercher des productions par année
     * GET /api/v1/crop-productions/year/{year}
     */
    @GetMapping("/year/{year}")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getProductionsByYear(
            @PathVariable Integer year) {
        List<CropProduction> productions = cropProductionService.getProductionsByYear(year);
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productions de l'année " + year, productionDTOs));
    }

    // ==================== DATE RANGE OPERATIONS ====================

    /**
     * Rechercher des productions par période de plantation
     * GET /api/v1/crop-productions/planting-period?startDate=...&endDate=...
     */
    @GetMapping("/planting-period")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getProductionsByPlantingPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<CropProduction> productions = cropProductionService.getProductionsByPlantingDateRange(startDate, endDate);
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productions plantées entre " + startDate + " et " + endDate, productionDTOs));
    }


    /**
     * Obtenir les productions par agriculteur (farmer)
     * GET /api/v1/crop-productions/farmer/{farmerId}
     */
    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getProductionsByFarmer(
            @PathVariable String farmerId) {
        try {
            // Utiliser la même logique que /farm/{farmId} car farmerId = farmId dans votre système
            List<CropProduction> productions = cropProductionService.getProductionsByFarm(farmerId);
            List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Productions de l'agriculteur " + farmerId, productionDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des productions: " + e.getMessage(), null));
        }
    }


    /**
     * Obtenir les productions par ID de l'agriculteur (User ID)
     * GET /api/v1/crop-productions/by-farmer/{userId}
     */
    @GetMapping("/by-farmer/{userId}")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getProductionsByFarmerId(
            @PathVariable String userId) {
        try {
            // Récupérer toutes les productions où farmId = userId
            List<CropProduction> productions = cropProductionService.getAllCropProductions()
                    .stream()
                    .filter(p -> p.getFarmId() != null && p.getFarmId().equals(userId))
                    .collect(Collectors.toList());

            List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);

            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            "Productions de l'agriculteur " + userId,
                            productionDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des productions: " + e.getMessage(),
                            null));
        }
    }


    /**
     * Rechercher des productions par période de récolte attendue
     * GET /api/v1/crop-productions/harvest-period?startDate=...&endDate=...
     */
    @GetMapping("/harvest-period")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getProductionsByHarvestPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<CropProduction> productions = cropProductionService.getProductionsByExpectedHarvestDateRange(startDate, endDate);
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productions à récolter entre " + startDate + " et " + endDate, productionDTOs));
    }

    // ==================== STATUS-BASED OPERATIONS ====================
    @GetMapping("/active")
    public ResponseEntity<List<CropProductionDTO>> getActiveCropProductions() {
        try {
            List<CropProduction> activeProductions = cropProductionService.getActiveProductions();
            List<CropProductionDTO> enrichedDTOs = enrichProductionDTOs(activeProductions);
            return ResponseEntity.ok(enrichedDTOs);
        } catch (Exception e) {
            logger.error("Error getting active productions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir les productions récoltées
     * GET /api/v1/crop-productions/harvested
     */
    @GetMapping("/harvested")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getHarvestedProductions() {
        List<CropProduction> productions = cropProductionService.getHarvestedProductions();
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productions récoltées", productionDTOs));
    }

    /**
     * Obtenir les productions en retard
     * GET /api/v1/crop-productions/overdue
     */
    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getOverdueProductions() {
        List<CropProduction> productions = cropProductionService.getOverdueProductions();
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productions en retard", productionDTOs));
    }

    /**
     * Obtenir les productions à récolter bientôt
     * GET /api/v1/crop-productions/harvest-soon?days=...
     */
    @GetMapping("/harvest-soon")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getProductionsToHarvestSoon(
            @RequestParam(defaultValue = "7") @Positive Integer days) {
        List<CropProduction> productions = cropProductionService.getProductionsToHarvestSoon(days);
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productions à récolter dans " + days + " jours", productionDTOs));
    }

    // ==================== PRODUCTION METHOD OPERATIONS ====================

    /**
     * Obtenir les productions par méthode de production
     * GET /api/v1/crop-productions/method/{method}
     */
    @GetMapping("/method/{method}")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getProductionsByMethod(
            @PathVariable CropProduction.ProductionMethod method) {
        List<CropProduction> productions = cropProductionService.getProductionsByMethod(method);
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productions " + method.getDisplayName(), productionDTOs));
    }


    /**
     * Obtenir les productions actives avec détails enrichis pour le marketplace
     * GET /api/v1/crop-productions/marketplace
     */
    @GetMapping("/marketplace")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getMarketplaceProductions() {
        try {
            // Récupérer les productions actives (PLANTED, GROWING, HARVESTED)
            List<CropProduction> productions = cropProductionService.getAllCropProductions()
                    .stream()
                    .filter(p -> p.getProductionStatus() == CropProduction.ProductionStatus.PLANTED ||
                            p.getProductionStatus() == CropProduction.ProductionStatus.GROWING ||
                            p.getProductionStatus() == CropProduction.ProductionStatus.HARVESTED)
                    .filter(p -> {
                        // Vérifier qu'il y a une quantité disponible
                        BigDecimal quantity = p.getExpectedYield() != null ? p.getExpectedYield() : p.getActualYield();
                        return quantity != null && quantity.compareTo(BigDecimal.ZERO) > 0;
                    })
                    .collect(Collectors.toList());

            List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);

            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            "Marketplace productions récupérées",
                            productionDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des productions: " + e.getMessage(),
                            null));
        }
    }

    /**
     * Obtenir les productions biologiques
     * GET /api/v1/crop-productions/organic
     */
    @GetMapping("/organic")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getOrganicProductions() {
        List<CropProduction> productions = cropProductionService.getOrganicProductions();
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productions biologiques", productionDTOs));
    }

    /**
     * Obtenir les productions certifiées
     * GET /api/v1/crop-productions/certified
     */
    @GetMapping("/certified")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getCertifiedProductions() {
        List<CropProduction> productions = cropProductionService.getCertifiedProductions();
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productions certifiées", productionDTOs));
    }

    // ==================== YIELD OPERATIONS ====================

    /**
     * Obtenir les productions avec rendement élevé
     * GET /api/v1/crop-productions/high-yield?minYield=...
     */
    @GetMapping("/high-yield")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getHighYieldProductions(
            @RequestParam BigDecimal minYield) {
        List<CropProduction> productions = cropProductionService.getProductionsWithHighYield(minYield);
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productions avec rendement ≥ " + minYield + " t/ha", productionDTOs));
    }

    /**
     * Obtenir les productions avec rendement faible
     * GET /api/v1/crop-productions/low-yield?maxYield=...
     */
    @GetMapping("/low-yield")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getLowYieldProductions(
            @RequestParam BigDecimal maxYield) {
        List<CropProduction> productions = cropProductionService.getProductionsWithLowYield(maxYield);
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productions avec rendement ≤ " + maxYield + " t/ha", productionDTOs));
    }

    /**
     * Obtenir les productions par plage de rendement
     * GET /api/v1/crop-productions/yield-range?minYield=...&maxYield=...
     */
    @GetMapping("/yield-range")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getProductionsByYieldRange(
            @RequestParam BigDecimal minYield,
            @RequestParam BigDecimal maxYield) {
        List<CropProduction> productions = cropProductionService.getProductionsByYieldRange(minYield, maxYield);
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Productions avec rendement " + minYield + "-" + maxYield + " t/ha",
                        productionDTOs));
    }

    // ==================== AREA OPERATIONS ====================

    /**
     * Obtenir les productions par superficie
     * GET /api/v1/crop-productions/area-range?minArea=...&maxArea=...
     */
    @GetMapping("/area-range")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getProductionsByAreaRange(
            @RequestParam BigDecimal minArea,
            @RequestParam BigDecimal maxArea) {
        List<CropProduction> productions = cropProductionService.getProductionsByAreaRange(minArea, maxArea);
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Productions avec superficie " + minArea + "-" + maxArea + " ha",
                        productionDTOs));
    }

    /**
     * Obtenir les grandes productions
     * GET /api/v1/crop-productions/large-scale?minArea=...
     */
    @GetMapping("/large-scale")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getLargeScaleProductions(
            @RequestParam(defaultValue = "10") BigDecimal minArea) {
        List<CropProduction> productions = cropProductionService.getLargeScaleProductions(minArea);
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productions à grande échelle (≥ " + minArea + " ha)", productionDTOs));
    }

    // ==================== BATCH OPERATIONS ====================

    /**
     * Mettre à jour le statut de plusieurs productions
     * PUT /api/v1/crop-productions/batch/status
     */
    @PutMapping("/batch/status")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> updateProductionStatus(
            @RequestBody List<String> productionIds,
            @RequestParam CropProduction.ProductionStatus newStatus) {
        List<CropProduction> updatedProductions = cropProductionService.updateProductionStatus(productionIds, newStatus);
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(updatedProductions);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Statut mis à jour pour " + updatedProductions.size() + " productions",
                        productionDTOs));
    }

    /**
     * Marquer des productions comme récoltées
     * PUT /api/v1/crop-productions/batch/harvest
     */
    @PutMapping("/batch/harvest")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> markAsHarvested(
            @RequestBody List<String> productionIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate harvestDate,
            @RequestParam(required = false) BigDecimal actualYield) {
        List<CropProduction> harvestedProductions = cropProductionService.markAsHarvested(productionIds, harvestDate, actualYield);
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(harvestedProductions);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        harvestedProductions.size() + " productions marquées comme récoltées",
                        productionDTOs));
    }

    // ==================== ANALYTICS OPERATIONS ====================

    /**
     * Obtenir les statistiques de production
     * GET /api/v1/crop-productions/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<CropProductionStatsDTO>> getProductionStatistics() {
        CropProductionStatsDTO stats = cropProductionService.getProductionStatistics();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Statistiques de production", stats));
    }

    /**
     * Obtenir les statistiques par ferme
     * GET /api/v1/crop-productions/statistics/farm/{farmId}
     */
    @GetMapping("/statistics/farm/{farmId}")
    public ResponseEntity<ApiResponse<CropProductionStatsDTO>> getProductionStatisticsByFarm(
            @PathVariable String farmId) {
        CropProductionStatsDTO stats = cropProductionService.getProductionStatisticsByFarm(farmId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Statistiques de production pour la ferme " + farmId, stats));
    }

    /**
     * Obtenir les statistiques par culture
     * GET /api/v1/crop-productions/statistics/crop/{cropId}
     */
    @GetMapping("/statistics/crop/{cropId}")
    public ResponseEntity<ApiResponse<CropProductionStatsDTO>> getProductionStatisticsByCrop(
            @PathVariable String cropId) {
        CropProductionStatsDTO stats = cropProductionService.getProductionStatisticsByCrop(cropId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Statistiques de production pour la culture " + cropId, stats));
    }

    /**
     * Obtenir le rendement moyen par culture
     * GET /api/v1/crop-productions/average-yield/crop/{cropId}
     */
    @GetMapping("/average-yield/crop/{cropId}")
    public ResponseEntity<ApiResponse<BigDecimal>> getAverageYieldByCrop(@PathVariable String cropId) {
        BigDecimal avgYield = cropProductionService.getAverageYieldByCrop(cropId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Rendement moyen pour la culture " + cropId, avgYield));
    }

    /**
     * Obtenir la production totale par année
     * GET /api/v1/crop-productions/total-production/year/{year}
     */
    @GetMapping("/total-production/year/{year}")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalProductionByYear(@PathVariable Integer year) {
        BigDecimal totalProduction = cropProductionService.getTotalProductionByYear(year);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Production totale pour l'année " + year, totalProduction));
    }

    // ==================== SEARCH & FILTER OPERATIONS ====================

    /**
     * Recherche avancée avec filtres multiples
     * GET /api/v1/crop-productions/filter
     */
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<CropProductionDTO>>> findWithFilters(
            @RequestParam(required = false) String farmId,
            @RequestParam(required = false) String cropId,
            @RequestParam(required = false) CropProduction.ProductionStatus status,
            @RequestParam(required = false) CropProduction.Season season,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) CropProduction.ProductionMethod method,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate plantingDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate plantingDateTo,
            @RequestParam(required = false) BigDecimal minYield,
            @RequestParam(required = false) BigDecimal maxYield,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CropProduction> productions = cropProductionService.findWithFilters(
                farmId, cropId, status, season, year, method,
                plantingDateFrom, plantingDateTo, minYield, maxYield, pageable);
        Page<CropProductionDTO> productionDTOs = productions.map(CropProductionDTO::fromEntity);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productions filtrées récupérées", productionDTOs));
    }

    /**
     * Rechercher par code de production partiel
     * GET /api/v1/crop-productions/search?query=...
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> searchProductions(
            @RequestParam @NotBlank String query) {
        List<CropProduction> productions = cropProductionService.searchProductions(query);
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Résultats de recherche pour '" + query + "'", productionDTOs));
    }

    // ==================== UTILITY OPERATIONS ====================

    /**
     * Générer un code de production automatique
     * GET /api/v1/crop-productions/generate-code?farmCode=...&cropCode=...&season=...&year=...
     */
    @GetMapping("/generate-code")
    public ResponseEntity<ApiResponse<String>> generateProductionCode(
            @RequestParam String farmCode,
            @RequestParam String cropCode,
            @RequestParam CropProduction.Season season,
            @RequestParam Integer year) {
        String productionCode = cropProductionService.generateProductionCode(farmCode, cropCode, season, year);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Code de production généré", productionCode));
    }

    /**
     * Vérifier la disponibilité d'un code de production
     * GET /api/v1/crop-productions/check-code/{productionCode}
     */
    @GetMapping("/check-code/{productionCode}")
    public ResponseEntity<ApiResponse<Boolean>> checkProductionCodeAvailability(
            @PathVariable String productionCode) {
        boolean isAvailable = !cropProductionService.getCropProductionByCode(productionCode).isPresent();
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Code '" + productionCode + "' disponible: " + isAvailable,
                        isAvailable));
    }

    /**
     * Obtenir les productions récentes
     * GET /api/v1/crop-productions/recent?limit=...
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> getMostRecentProductions(
            @RequestParam(defaultValue = "10") @Positive int limit) {
        List<CropProduction> productions = cropProductionService.getMostRecentProductions(limit);
        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Productions récemment créées", productionDTOs));
    }

    // ==================== EXPORT OPERATIONS ====================

    /**
     * Exporter les productions avec filtres optionnels
     * GET /api/v1/crop-productions/export
     */
    @GetMapping("/export")
    public ResponseEntity<ApiResponse<List<CropProductionDTO>>> exportProductions(
            @RequestParam(required = false) String farmId,
            @RequestParam(required = false) CropProduction.ProductionStatus status,
            @RequestParam(required = false) Integer year) {

        List<CropProduction> productions;
        if (farmId != null && status != null && year != null) {
            productions = cropProductionService.getAllCropProductions().stream()
                    .filter(p -> p.getFarmId().equals(farmId) &&
                            p.getProductionStatus() == status &&
                            p.getYear().equals(year))
                    .collect(Collectors.toList());
        } else if (farmId != null) {
            productions = cropProductionService.getProductionsByFarm(farmId);
        } else if (status != null) {
            productions = cropProductionService.getProductionsByStatus(status);
        } else if (year != null) {
            productions = cropProductionService.getProductionsByYear(year);
        } else {
            productions = cropProductionService.getAllCropProductions();
        }

        List<CropProductionDTO> productionDTOs = convertToCropProductionDTOList(productions);

        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Données exportées (" + productionDTOs.size() + " productions)", productionDTOs));
    }

    // ==================== METADATA OPERATIONS ====================

    /**
     * Obtenir les statuts de production disponibles
     * GET /api/v1/crop-productions/statuses
     */
    @GetMapping("/statuses")
    public ResponseEntity<ApiResponse<CropProduction.ProductionStatus[]>> getProductionStatuses() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Statuts de production disponibles",
                        CropProduction.ProductionStatus.values()));
    }

    /**
     * Obtenir les saisons disponibles
     * GET /api/v1/crop-productions/seasons
     */
    @GetMapping("/seasons")
    public ResponseEntity<ApiResponse<CropProduction.Season[]>> getSeasons() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Saisons disponibles", CropProduction.Season.values()));
    }

    /**
     * Obtenir les méthodes de production disponibles
     * GET /api/v1/crop-productions/production-methods
     */
    @GetMapping("/production-methods")
    public ResponseEntity<ApiResponse<CropProduction.ProductionMethod[]>> getProductionMethods() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Méthodes de production disponibles",
                        CropProduction.ProductionMethod.values()));
    }

    // ==================== HEALTH CHECK ====================

    /**
     * Vérifier l'état du service
     * GET /api/v1/crop-productions/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        long count = cropProductionService.getAllCropProductions().size();
        String healthInfo = String.format(
                "Service opérationnel - %d productions en base de données", count);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Service en bonne santé", healthInfo));
    }

    // ==================== CONVERSION METHODS ====================

    /**
     * Convertir CropProduction en CropProductionDTO
     */
    private CropProductionDTO convertToCropProductionDTO(CropProduction production) {
        return CropProductionDTO.fromEntity(production);
    }

    /**
     * Convertir liste de CropProduction en liste de CropProductionDTO
     */
    private List<CropProductionDTO> convertToCropProductionDTOList(List<CropProduction> productions) {
        return productions.stream()
                .map(CropProductionDTO::fromEntity)
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CropProductionDTO>> getCropProductionById(@PathVariable String id) {
        CropProduction production = cropProductionService.getCropProductionById(id);
        CropProductionDTO productionDTO = CropProductionDTO.fromEntity(production);
        logger.info("=== ENRICHING PRODUCTION {} ===", id);

        // ⭐ ENRICH avec les informations du Crop
        try {
            Crop crop = cropService.getCropById(production.getCropId());
            if (crop != null) {
                productionDTO.setCropName(crop.getCropName());
                productionDTO.setCropType(crop.getCropType());
                productionDTO.setCropImageUrl(crop.getImageUrl());
                productionDTO.setCropVariety(crop.getVariety());
                productionDTO.setCropScientificName(crop.getScientificName());
                productionDTO.setCropGrowingPeriodDays(crop.getGrowingPeriodDays());
                productionDTO.setCropPlantingSeason(crop.getPlantingSeason());
                productionDTO.setCropHarvestSeason(crop.getHarvestSeason());
                logger.info("✓ Crop info loaded: {}", crop.getCropName());
            }
        } catch (Exception e) {
            logger.warn("Could not load crop info for production {} with cropId {}",
                    id, production.getCropId());
            productionDTO.setCropName("Unknown Crop");
        }

        // ⭐ ENRICH avec les informations du Farmer
        try {
            UserDTO farmer = userService.getUserById(production.getFarmId());
            if (farmer != null) {
                productionDTO.setFarmerName(farmer.getFullName());
                productionDTO.setFarmerUsername(farmer.getUsername());
                productionDTO.setFarmerEmail(farmer.getEmail());
                productionDTO.setFarmerPhone(farmer.getPhoneNumber());
                productionDTO.setFarmerProfileImageUrl(farmer.getProfileImageUrl());
                logger.info("✓ Farmer info loaded: {}", farmer.getFullName());
            }
        } catch (Exception e) {
            logger.warn("Could not load farmer info for production {} with farmerId {}",
                    id, production.getFarmId());
            productionDTO.setFarmerName("Farmer Info Unavailable");
        }

        // ⭐ ENRICH avec le prix par kg
        try {
            if (productionDTO.getPricePerKg() == null) {
                // Option 1: Si estimatedPrice existe et qu'on peut calculer le prix par kg
                if (productionDTO.getEstimatedPrice() != null &&
                        productionDTO.getEstimatedPrice().compareTo(BigDecimal.ZERO) > 0) {

                    BigDecimal quantity = productionDTO.getExpectedYield() != null ?
                            productionDTO.getExpectedYield() :
                            productionDTO.getActualYield();

                    if (quantity != null && quantity.compareTo(BigDecimal.ZERO) > 0) {
                        // Calculer le prix par kg à partir du prix total
                        BigDecimal pricePerKg = productionDTO.getEstimatedPrice()
                                .divide(quantity, 2, java.math.RoundingMode.HALF_UP);
                        productionDTO.setPricePerKg(pricePerKg);
                        logger.info("✓ Calculated pricePerKg from estimatedPrice: {} RWF/kg", pricePerKg);
                    } else {
                        // Si pas de quantité, utiliser estimatedPrice comme pricePerKg
                        productionDTO.setPricePerKg(productionDTO.getEstimatedPrice());
                        logger.info("✓ Using estimatedPrice as pricePerKg: {} RWF/kg",
                                productionDTO.getEstimatedPrice());
                    }
                } else {
                    // Option 2: Prix par défaut si aucun prix n'est disponible
                    productionDTO.setPricePerKg(new BigDecimal("1000"));
                    logger.warn("⚠️ No price found, using default: 1000 RWF/kg");
                }
            } else {
                logger.info("✓ Using existing pricePerKg: {} RWF/kg", productionDTO.getPricePerKg());
            }
        } catch (Exception e) {
            logger.error("❌ Could not set price for production {}", id, e);
            productionDTO.setPricePerKg(new BigDecimal("1000")); // Prix par défaut en cas d'erreur
        }

        logger.info("=== FINAL DTO ===");
        logger.info("Crop Name: {}", productionDTO.getCropName());
        logger.info("Farmer Name: {}", productionDTO.getFarmerName());
        logger.info("Price Per Kg: {} RWF/kg", productionDTO.getPricePerKg());
        logger.info("================");

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Production trouvée", productionDTO));
    }




    // ==================== AI PREDICTION ENDPOINTS ====================

    /**
     * Get AI-powered yield prediction for a production
     * GET /api/v1/crop-productions/{id}/predict-yield
     */
    @GetMapping("/{id}/predict-yield")
    public ResponseEntity<ApiResponse<Map<String, Object>>> predictYield(@PathVariable String id) {
        try {
            CropProduction production = cropProductionService.getCropProductionById(id);

            Map<String, Object> prediction = new HashMap<>();

            // Calculate predicted yield based on historical data
            BigDecimal predictedYield = calculatePredictedYield(production);
            BigDecimal confidence = calculatePredictionConfidence(production);
            String risk = assessProductionRisk(production);

            prediction.put("productionId", id);
            prediction.put("predictedYield", predictedYield);
            prediction.put("expectedYield", production.getExpectedYield());
            prediction.put("confidence", confidence);
            prediction.put("risk", risk);
            prediction.put("factors", analyzePredictionFactors(production));

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Yield prediction generated", prediction));
        } catch (Exception e) {
            logger.error("Error predicting yield for production {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to predict yield: " + e.getMessage(), null));
        }
    }

    /**
     * Get food security risk assessment for a production
     * GET /api/v1/crop-productions/{id}/security-risk
     */
    @GetMapping("/{id}/security-risk")
    public ResponseEntity<ApiResponse<Map<String, Object>>> assessSecurityRisk(@PathVariable String id) {
        try {
            CropProduction production = cropProductionService.getCropProductionById(id);

            Map<String, Object> assessment = new HashMap<>();

            String riskLevel = assessFoodSecurityRisk(production);
            List<String> indicators = identifySecurityIndicators(production);
            List<String> recommendations = generateRecommendations(production);

            assessment.put("productionId", id);
            assessment.put("riskLevel", riskLevel);
            assessment.put("indicators", indicators);
            assessment.put("recommendations", recommendations);
            assessment.put("assessmentDate", LocalDateTime.now());

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Security risk assessed", assessment));
        } catch (Exception e) {
            logger.error("Error assessing security risk for production {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to assess risk: " + e.getMessage(), null));
        }
    }

// ==================== PRIVATE HELPER METHODS ====================

    private BigDecimal calculatePredictedYield(CropProduction production) {
        BigDecimal baseYield = production.getExpectedYield();
        if (baseYield == null) return BigDecimal.ZERO;

        // Get historical average for this crop
        List<CropProduction> historicalProductions = cropProductionService.getProductionsByCrop(production.getCropId())
                .stream()
                .filter(p -> p.getActualYield() != null &&
                        p.getProductionStatus() == CropProduction.ProductionStatus.HARVESTED)
                .collect(Collectors.toList());

        if (historicalProductions.isEmpty()) {
            return baseYield;
        }

        BigDecimal historicalAvg = historicalProductions.stream()
                .map(CropProduction::getActualYield)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(historicalProductions.size()), 2, java.math.RoundingMode.HALF_UP);

        // Weighted average: 60% historical, 40% expected
        return historicalAvg.multiply(new BigDecimal("0.6"))
                .add(baseYield.multiply(new BigDecimal("0.4")));
    }

    private BigDecimal calculatePredictionConfidence(CropProduction production) {
        int confidenceScore = 50;

        if (production.getSeedVariety() != null) confidenceScore += 10;
        if (production.getSeedSource() != null) confidenceScore += 5;
        if (production.getProductionMethod() == CropProduction.ProductionMethod.ORGANIC) confidenceScore += 10;

        List<CropProduction> historical = cropProductionService.getProductionsByCrop(production.getCropId());
        if (historical.size() > 5) confidenceScore += 15;
        else if (historical.size() > 2) confidenceScore += 10;

        long daysSincePlanting = production.getDaysSincePlanting();
        if (daysSincePlanting > 60) confidenceScore += 10;
        else if (daysSincePlanting > 30) confidenceScore += 5;

        return BigDecimal.valueOf(Math.min(95, confidenceScore));
    }

    private String assessProductionRisk(CropProduction production) {
        int riskScore = 0;

        if (production.isOverdue()) riskScore += 30;

        long daysToHarvest = production.getDaysToHarvest();
        if (daysToHarvest < 0) riskScore += 25;
        else if (daysToHarvest < 7) riskScore += 15;
        else if (daysToHarvest < 30) riskScore += 5;

        if (production.getAreaPlanted() != null) {
            if (production.getAreaPlanted().compareTo(new BigDecimal("50")) > 0) riskScore += 15;
            else if (production.getAreaPlanted().compareTo(new BigDecimal("20")) > 0) riskScore += 10;
        }

        if (production.getProductionStatus() == CropProduction.ProductionStatus.PLANNED) riskScore += 10;

        if (riskScore >= 50) return "HIGH";
        if (riskScore >= 25) return "MEDIUM";
        return "LOW";
    }

    private List<String> analyzePredictionFactors(CropProduction production) {
        List<String> factors = new ArrayList<>();

        factors.add("Historical yield data from similar productions");
        factors.add("Current production method: " + production.getProductionMethod().getDisplayName());
        factors.add("Area planted: " + production.getAreaPlantedDisplay());
        factors.add("Days since planting: " + production.getDaysSincePlanting());

        if (production.getSeedVariety() != null) {
            factors.add("Seed variety: " + production.getSeedVariety());
        }

        return factors;
    }

    private String assessFoodSecurityRisk(CropProduction production) {
        int riskScore = 0;

        if (production.getProductionStatus() == CropProduction.ProductionStatus.PLANNED) riskScore += 20;
        if (production.isOverdue()) riskScore += 30;

        if (production.getExpectedYield() != null && production.getActualYield() != null) {
            BigDecimal efficiency = production.getYieldEfficiency();
            if (efficiency != null && efficiency.compareTo(new BigDecimal("70")) < 0) {
                riskScore += 25;
            }
        }

        if (production.getAreaPlanted() != null &&
                production.getAreaPlanted().compareTo(new BigDecimal("100")) > 0) {
            riskScore += 15;
        }

        if (riskScore >= 50) return "CRITICAL";
        if (riskScore >= 30) return "HIGH";
        if (riskScore >= 15) return "MEDIUM";
        return "LOW";
    }

    private List<String> identifySecurityIndicators(CropProduction production) {
        List<String> indicators = new ArrayList<>();

        if (production.isOverdue()) {
            indicators.add("⚠️ Harvest overdue - potential supply delay");
        }

        if (production.getDaysToHarvest() > 0 && production.getDaysToHarvest() < 14) {
            indicators.add("📅 Harvest approaching - prepare storage and distribution");
        }

        if (production.getProductionStatus() == CropProduction.ProductionStatus.GROWING) {
            indicators.add("🌱 Production in progress - monitor regularly");
        }

        BigDecimal totalProduction = production.getTotalProduction();
        if (totalProduction != null && totalProduction.compareTo(new BigDecimal("1000")) > 0) {
            indicators.add("📦 High volume production - significant food security impact");
        }

        if (production.isOrganic()) {
            indicators.add("🌿 Organic production - premium market opportunity");
        }

        return indicators;
    }

    private List<String> generateRecommendations(CropProduction production) {
        List<String> recommendations = new ArrayList<>();

        if (production.isOverdue()) {
            recommendations.add("Immediate harvest required to prevent losses");
            recommendations.add("Assess crop condition and adjust future planning");
        }

        if (production.getDaysToHarvest() > 0 && production.getDaysToHarvest() < 30) {
            recommendations.add("Prepare harvesting equipment and labor");
            recommendations.add("Coordinate with buyers and storage facilities");
        }

        if (production.getProductionStatus() == CropProduction.ProductionStatus.PLANNED) {
            recommendations.add("Finalize planting schedule");
            recommendations.add("Ensure seed and input availability");
        }

        if (production.getActualYield() == null && production.getExpectedYield() != null) {
            recommendations.add("Set up yield monitoring system");
            recommendations.add("Record actual yield data for future predictions");
        }

        return recommendations;
    }

    private List<CropProductionDTO> enrichProductionDTOs(List<CropProduction> productions) {
        return productions.stream()
                .map(production -> {
                    CropProductionDTO dto = CropProductionDTO.fromEntity(production);

                    // ⭐ ENRICH avec les informations du Crop
                    try {
                        Crop crop = cropService.getCropById(production.getCropId());
                        if (crop != null) {
                            dto.setCropName(crop.getCropName());
                            dto.setCropType(crop.getCropType());
                            dto.setCropImageUrl(crop.getImageUrl());
                            dto.setCropVariety(crop.getVariety());
                            dto.setCropScientificName(crop.getScientificName());
                            dto.setCropGrowingPeriodDays(crop.getGrowingPeriodDays());
                            dto.setCropPlantingSeason(crop.getPlantingSeason());
                            dto.setCropHarvestSeason(crop.getHarvestSeason());
                        }
                    } catch (Exception e) {
                        logger.warn("Could not load crop info for production {} with cropId {}",
                                dto.getId(), dto.getCropId());
                        dto.setCropName("Unknown Crop");
                    }

                    // ⭐ ENRICH avec les informations du Farmer
                    try {
                        UserDTO farmer = userService.getUserById(production.getFarmId());
                        if (farmer != null) {
                            dto.setFarmerName(farmer.getFullName());
                            dto.setFarmerUsername(farmer.getUsername());
                            dto.setFarmerEmail(farmer.getEmail());
                            dto.setFarmerPhone(farmer.getPhoneNumber());
                            dto.setFarmerProfileImageUrl(farmer.getProfileImageUrl());
                        }
                    } catch (Exception e) {
                        logger.warn("Could not load farmer info for production {} with farmerId {}",
                                dto.getId(), dto.getFarmId());
                        dto.setFarmerName("Farmer Info Unavailable");
                    }

                    // ⭐ ENRICH avec le prix par kg
                    try {
                        if (dto.getPricePerKg() == null) {
                            // Option 1: Si estimatedPrice existe
                            if (dto.getEstimatedPrice() != null &&
                                    dto.getEstimatedPrice().compareTo(BigDecimal.ZERO) > 0) {

                                BigDecimal quantity = dto.getExpectedYield() != null ?
                                        dto.getExpectedYield() :
                                        dto.getActualYield();

                                if (quantity != null && quantity.compareTo(BigDecimal.ZERO) > 0) {
                                    // Calculer prix par kg à partir du prix total
                                    BigDecimal pricePerKg = dto.getEstimatedPrice()
                                            .divide(quantity, 2, java.math.RoundingMode.HALF_UP);
                                    dto.setPricePerKg(pricePerKg);
                                } else {
                                    // Utiliser estimatedPrice comme pricePerKg
                                    dto.setPricePerKg(dto.getEstimatedPrice());
                                }
                            } else {
                                // Option 2: Prix par défaut
                                dto.setPricePerKg(new BigDecimal("1000"));
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("Could not calculate price for production {}", dto.getId());
                        dto.setPricePerKg(new BigDecimal("1000"));
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }
}