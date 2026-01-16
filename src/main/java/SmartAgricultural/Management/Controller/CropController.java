package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.Crop;
import SmartAgricultural.Management.Service.CropService;
import SmartAgricultural.Management.dto.CropStatsDTO;
import SmartAgricultural.Management.dto.CropDTO;
import SmartAgricultural.Management.dto.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour la gestion des cultures
 *
 * @author SmartAgricultural Management System
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/v1/crops")
@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
public class CropController {

    @Autowired
    private CropService cropService;

    // ==================== CRUD OPERATIONS ====================

    /**
     * Créer une nouvelle culture
     * POST /api/v1/crops
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CropDTO>> createCrop(@Valid @RequestBody CropDTO cropDTO) {
        Crop crop = cropDTO.toEntity();
        Crop createdCrop = cropService.createCrop(crop);
        CropDTO responseDTO = CropDTO.fromEntity(createdCrop);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Culture créée avec succès", responseDTO));
    }

    /**
     * Obtenir toutes les cultures avec pagination
     * GET /api/v1/crops
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CropDTO>>> getAllCrops(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "cropName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Crop> crops = cropService.getAllCrops(pageable);
        Page<CropDTO> cropDTOs = crops.map(CropDTO::fromEntity);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cultures récupérées avec succès", cropDTOs));
    }

    /**
     * Obtenir toutes les cultures (sans pagination)
     * GET /api/v1/crops/all
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CropDTO>>> getAllCropsNoPaging() {
        List<Crop> crops = cropService.getAllCrops();
        List<CropDTO> cropDTOs = convertToCropDTOList(crops);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Toutes les cultures récupérées", cropDTOs));
    }

    /**
     * Obtenir une culture par ID
     * GET /api/v1/crops/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CropDTO>> getCropById(@PathVariable String id) {
        Crop crop = cropService.getCropById(id);
        CropDTO cropDTO = CropDTO.fromEntity(crop);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Culture trouvée", cropDTO));
    }

    /**
     * Obtenir une culture par nom
     * GET /api/v1/crops/name/{cropName}
     */
    @GetMapping("/name/{cropName}")
    public ResponseEntity<ApiResponse<CropDTO>> getCropByName(@PathVariable String cropName) {
        Optional<Crop> crop = cropService.getCropByName(cropName);
        if (crop.isPresent()) {
            CropDTO cropDTO = CropDTO.fromEntity(crop.get());
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Culture trouvée", cropDTO));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Mettre à jour une culture
     * PUT /api/v1/crops/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CropDTO>> updateCrop(
            @PathVariable String id,
            @Valid @RequestBody CropDTO cropDTO) {

        Crop crop = cropDTO.toEntity();
        Crop updatedCrop = cropService.updateCrop(id, crop);
        CropDTO responseDTO = CropDTO.fromEntity(updatedCrop);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Culture mise à jour avec succès", responseDTO));
    }

    /**
     * Supprimer une culture
     * DELETE /api/v1/crops/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCrop(@PathVariable String id) {
        cropService.deleteCrop(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Culture supprimée avec succès", null));
    }

    // ==================== SEARCH OPERATIONS ====================

    /**
     * Rechercher des cultures par nom/nom scientifique/variété
     * GET /api/v1/crops/search?query=...
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CropDTO>>> searchCrops(
            @RequestParam @NotBlank String query) {
        List<Crop> crops = cropService.searchCrops(query);
        List<CropDTO> cropDTOs = convertToCropDTOList(crops);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Résultats de recherche pour '" + query + "'", cropDTOs));
    }

    /**
     * Recherche avancée avec filtres multiples
     * GET /api/v1/crops/filter
     */
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<CropDTO>>> findWithFilters(
            @RequestParam(required = false) Crop.CropType cropType,
            @RequestParam(required = false) Crop.MarketDemandLevel marketDemand,
            @RequestParam(required = false) String plantingSeason,
            @RequestParam(required = false) Integer maxGrowingPeriod,
            @RequestParam(required = false) Integer minStorageLife,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "cropName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Crop> crops = cropService.findWithFilters(cropType, marketDemand,
                plantingSeason, maxGrowingPeriod, minStorageLife, pageable);
        Page<CropDTO> cropDTOs = crops.map(CropDTO::fromEntity);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cultures filtrées récupérées", cropDTOs));
    }

    // ==================== CATEGORY OPERATIONS ====================

    /**
     * Obtenir les cultures par type
     * GET /api/v1/crops/type/{cropType}
     */
    @GetMapping("/type/{cropType}")
    public ResponseEntity<ApiResponse<List<CropDTO>>> getCropsByType(
            @PathVariable Crop.CropType cropType) {
        List<Crop> crops = cropService.getCropsByType(cropType);
        List<CropDTO> cropDTOs = convertToCropDTOList(crops);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Cultures de type " + cropType.getDisplayName(), cropDTOs));
    }

    /**
     * Obtenir les cultures par demande du marché
     * GET /api/v1/crops/market-demand/{marketDemand}
     */
    @GetMapping("/market-demand/{marketDemand}")
    public ResponseEntity<ApiResponse<List<CropDTO>>> getCropsByMarketDemand(
            @PathVariable Crop.MarketDemandLevel marketDemand) {
        List<Crop> crops = cropService.getCropsByMarketDemand(marketDemand);
        List<CropDTO> cropDTOs = convertToCropDTOList(crops);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Cultures avec demande " + marketDemand.getDisplayName(), cropDTOs));
    }

    /**
     * Obtenir les cultures par saison de plantation
     * GET /api/v1/crops/planting-season/{season}
     */
    @GetMapping("/planting-season/{season}")
    public ResponseEntity<ApiResponse<List<CropDTO>>> getCropsByPlantingSeason(
            @PathVariable String season) {
        List<Crop> crops = cropService.getCropsByPlantingSeason(season);
        List<CropDTO> cropDTOs = convertToCropDTOList(crops);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Cultures à planter en " + season, cropDTOs));
    }

    /**
     * Obtenir les cultures par saison de récolte
     * GET /api/v1/crops/harvest-season/{season}
     */
    @GetMapping("/harvest-season/{season}")
    public ResponseEntity<ApiResponse<List<CropDTO>>> getCropsByHarvestSeason(
            @PathVariable String season) {
        List<Crop> crops = cropService.getCropsByHarvestSeason(season);
        List<CropDTO> cropDTOs = convertToCropDTOList(crops);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Cultures à récolter en " + season, cropDTOs));
    }

    // ==================== SPECIAL CATEGORIES ====================

    /**
     * Obtenir les cultures à forte demande
     * GET /api/v1/crops/high-demand
     */
    @GetMapping("/high-demand")
    public ResponseEntity<ApiResponse<List<CropDTO>>> getHighDemandCrops() {
        List<Crop> crops = cropService.getHighDemandCrops();
        List<CropDTO> cropDTOs = convertToCropDTOList(crops);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cultures à forte demande", cropDTOs));
    }

    /**
     * Obtenir les cultures à croissance rapide (≤ 90 jours)
     * GET /api/v1/crops/short-season
     */
    @GetMapping("/short-season")
    public ResponseEntity<ApiResponse<List<CropDTO>>> getShortSeasonCrops() {
        List<Crop> crops = cropService.getShortSeasonCrops();
        List<CropDTO> cropDTOs = convertToCropDTOList(crops);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cultures à croissance rapide", cropDTOs));
    }

    /**
     * Obtenir les cultures à longue conservation (> 1 an)
     * GET /api/v1/crops/long-storage
     */
    @GetMapping("/long-storage")
    public ResponseEntity<ApiResponse<List<CropDTO>>> getLongStorageCrops() {
        List<Crop> crops = cropService.getLongStorageCrops();
        List<CropDTO> cropDTOs = convertToCropDTOList(crops);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cultures à longue conservation", cropDTOs));
    }

    /**
     * Obtenir les cultures résistantes à la sécheresse
     * GET /api/v1/crops/drought-resistant?maxWaterRequirement=...
     */
    @GetMapping("/drought-resistant")
    public ResponseEntity<ApiResponse<List<CropDTO>>> getDroughtResistantCrops(
            @RequestParam BigDecimal maxWaterRequirement) {
        List<Crop> crops = cropService.getDroughtResistantCrops(maxWaterRequirement);
        List<CropDTO> cropDTOs = convertToCropDTOList(crops);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cultures résistantes à la sécheresse", cropDTOs));
    }

    // ==================== CONDITIONS-BASED OPERATIONS ====================

    /**
     * Trouver des cultures adaptées aux conditions spécifiques
     * GET /api/v1/crops/suitable-conditions
     */
    @GetMapping("/suitable-conditions")
    public ResponseEntity<ApiResponse<List<CropDTO>>> findSuitableForConditions(
            @RequestParam(required = false) BigDecimal temperature,
            @RequestParam(required = false) BigDecimal ph,
            @RequestParam(required = false) BigDecimal rainfall) {

        List<Crop> crops = cropService.findSuitableForConditions(temperature, ph, rainfall);
        List<CropDTO> cropDTOs = convertToCropDTOList(crops);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cultures adaptées aux conditions", cropDTOs));
    }

    /**
     * Trouver des cultures par température
     * GET /api/v1/crops/temperature/{temperature}
     */
    @GetMapping("/temperature/{temperature}")
    public ResponseEntity<ApiResponse<List<CropDTO>>> findCropsByTemperature(
            @PathVariable BigDecimal temperature) {
        List<Crop> crops = cropService.findCropsBySuitableTemperature(temperature);
        List<CropDTO> cropDTOs = convertToCropDTOList(crops);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Cultures adaptées à " + temperature + "°C", cropDTOs));
    }

    /**
     * Trouver des cultures par pH
     * GET /api/v1/crops/ph/{ph}
     */
    @GetMapping("/ph/{ph}")
    public ResponseEntity<ApiResponse<List<CropDTO>>> findCropsByPh(
            @PathVariable BigDecimal ph) {
        List<Crop> crops = cropService.findCropsBySuitablePh(ph);
        List<CropDTO> cropDTOs = convertToCropDTOList(crops);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Cultures adaptées au pH " + ph, cropDTOs));
    }

    /**
     * Trouver des cultures par exigence climatique
     * GET /api/v1/crops/climate/{climate}
     */
    @GetMapping("/climate/{climate}")
    public ResponseEntity<ApiResponse<List<CropDTO>>> findByClimateRequirement(
            @PathVariable String climate) {
        List<Crop> crops = cropService.findByClimateRequirement(climate);
        List<CropDTO> cropDTOs = convertToCropDTOList(crops);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Cultures adaptées au climat " + climate, cropDTOs));
    }

    // ==================== RANGE-BASED OPERATIONS ====================

    /**
     * Trouver des cultures par période de croissance
     * GET /api/v1/crops/growing-period?minDays=...&maxDays=...
     */
    @GetMapping("/growing-period")
    public ResponseEntity<ApiResponse<List<CropDTO>>> findByGrowingPeriodRange(
            @RequestParam @Positive Integer minDays,
            @RequestParam @Positive Integer maxDays) {
        List<Crop> crops = cropService.findByGrowingPeriodRange(minDays, maxDays);
        List<CropDTO> cropDTOs = convertToCropDTOList(crops);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Cultures avec période de croissance " + minDays + "-" + maxDays + " jours",
                        cropDTOs));
    }

    /**
     * Trouver des cultures par pluviométrie
     * GET /api/v1/crops/rainfall-range?minRainfall=...&maxRainfall=...
     */
    @GetMapping("/rainfall-range")
    public ResponseEntity<ApiResponse<List<CropDTO>>> findByRainfallRange(
            @RequestParam BigDecimal minRainfall,
            @RequestParam BigDecimal maxRainfall) {
        List<Crop> crops = cropService.findByRainfallRange(minRainfall, maxRainfall);
        List<CropDTO> cropDTOs = convertToCropDTOList(crops);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Cultures avec pluviométrie " + minRainfall + "-" + maxRainfall + " mm/an",
                        cropDTOs));
    }

    // ==================== RECOMMENDATION SYSTEM ====================

    /**
     * Recommander des cultures basées sur les conditions
     * POST /api/v1/crops/recommend
     */
    @PostMapping("/recommend")
    public ResponseEntity<ApiResponse<List<CropDTO>>> recommendCrops(
            @RequestParam(required = false) BigDecimal temperature,
            @RequestParam(required = false) BigDecimal ph,
            @RequestParam(required = false) BigDecimal rainfall,
            @RequestParam(required = false) String season,
            @RequestParam(required = false) Crop.MarketDemandLevel preferredDemand) {

        List<Crop> recommendedCrops = cropService.recommendCrops(
                temperature, ph, rainfall, season, preferredDemand);
        List<CropDTO> cropDTOs = convertToCropDTOList(recommendedCrops);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cultures recommandées", cropDTOs));
    }

    // ==================== UTILITY OPERATIONS ====================

    /**
     * Obtenir les cultures les plus récentes
     * GET /api/v1/crops/recent?limit=...
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<CropDTO>>> getMostRecentCrops(
            @RequestParam(defaultValue = "10") @Positive int limit) {
        List<Crop> crops = cropService.getMostRecentCrops(limit);
        List<CropDTO> cropDTOs = convertToCropDTOList(crops);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cultures récemment ajoutées", cropDTOs));
    }

    /**
     * Trouver des cultures similaires
     * GET /api/v1/crops/{id}/similar?limit=...
     */
    @GetMapping("/{id}/similar")
    public ResponseEntity<ApiResponse<List<CropDTO>>> findSimilarCrops(
            @PathVariable String id,
            @RequestParam(defaultValue = "5") @Positive int limit) {
        List<Crop> similarCrops = cropService.findSimilarCrops(id, limit);
        List<CropDTO> cropDTOs = convertToCropDTOList(similarCrops);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cultures similaires", cropDTOs));
    }

    /**
     * Obtenir les cultures avec informations manquantes
     * GET /api/v1/crops/missing-info
     */
    @GetMapping("/missing-info")
    public ResponseEntity<ApiResponse<List<CropDTO>>> getCropsWithMissingInfo() {
        List<Crop> crops = cropService.getCropsWithMissingInfo();
        List<CropDTO> cropDTOs = convertToCropDTOList(crops);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cultures avec informations manquantes", cropDTOs));
    }

    // ==================== STATISTICS OPERATIONS ====================

    /**
     * Obtenir des statistiques complètes des cultures
     * GET /api/v1/crops/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<CropStatsDTO>> getCropStatistics() {
        CropStatsDTO stats = cropService.getCropStatistics();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Statistiques des cultures", stats));
    }

    /**
     * Compter les cultures par type
     * GET /api/v1/crops/count/type/{cropType}
     */
    @GetMapping("/count/type/{cropType}")
    public ResponseEntity<ApiResponse<Long>> countCropsByType(
            @PathVariable Crop.CropType cropType) {
        Long count = cropService.countCropsByType(cropType);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Nombre de cultures de type " + cropType.getDisplayName(), count));
    }

    /**
     * Obtenir les meilleures variétés par type
     * GET /api/v1/crops/varieties/top/{cropType}?limit=...
     */
    @GetMapping("/varieties/top/{cropType}")
    public ResponseEntity<ApiResponse<List<Object[]>>> getTopVarietiesByType(
            @PathVariable Crop.CropType cropType,
            @RequestParam(defaultValue = "5") @Positive int limit) {
        List<Object[]> varieties = cropService.getTopVarietiesByType(cropType, limit);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Top " + limit + " variétés pour " + cropType.getDisplayName(), varieties));
    }

    /**
     * Obtenir le nombre total de cultures
     * GET /api/v1/crops/count
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getTotalCropsCount() {
        List<Crop> allCrops = cropService.getAllCrops();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Nombre total de cultures", (long) allCrops.size()));
    }

    // ==================== METADATA OPERATIONS ====================

    /**
     * Obtenir les types de cultures disponibles
     * GET /api/v1/crops/types
     */
    @GetMapping("/types")
    public ResponseEntity<ApiResponse<Crop.CropType[]>> getCropTypes() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Types de cultures disponibles", Crop.CropType.values()));
    }

    /**
     * Obtenir les niveaux de demande du marché disponibles
     * GET /api/v1/crops/market-demand-levels
     */
    @GetMapping("/market-demand-levels")
    public ResponseEntity<ApiResponse<Crop.MarketDemandLevel[]>> getMarketDemandLevels() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Niveaux de demande du marché",
                        Crop.MarketDemandLevel.values()));
    }

    /**
     * Vérifier si une culture existe par nom
     * GET /api/v1/crops/exists/{cropName}
     */
    @GetMapping("/exists/{cropName}")
    public ResponseEntity<ApiResponse<Boolean>> checkCropExists(@PathVariable String cropName) {
        Optional<Crop> crop = cropService.getCropByName(cropName);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Culture '" + cropName + "' existe: " + crop.isPresent(), crop.isPresent()));
    }

    // ==================== BATCH OPERATIONS ====================

    /**
     * Créer plusieurs cultures en une fois
     * POST /api/v1/crops/batch
     */
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<CropDTO>>> createMultipleCrops(
            @Valid @RequestBody List<CropDTO> cropDTOs) {
        List<Crop> createdCrops = cropDTOs.stream()
                .map(dto -> {
                    Crop crop = dto.toEntity();
                    return cropService.createCrop(crop);
                })
                .collect(Collectors.toList());

        List<CropDTO> responseDTOs = convertToCropDTOList(createdCrops);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true,
                        "Cultures créées avec succès (" + responseDTOs.size() + ")",
                        responseDTOs));
    }

    /**
     * Supprimer plusieurs cultures par IDs
     * DELETE /api/v1/crops/batch
     */
    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> deleteMultipleCrops(
            @RequestBody List<String> cropIds) {
        cropIds.forEach(id -> cropService.deleteCrop(id));

        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Cultures supprimées avec succès (" + cropIds.size() + ")", null));
    }

    // ==================== EXPORT OPERATIONS ====================

    /**
     * Exporter les cultures avec filtres optionnels
     * GET /api/v1/crops/export
     */
    @GetMapping("/export")
    public ResponseEntity<ApiResponse<List<CropDTO>>> exportCrops(
            @RequestParam(required = false) Crop.CropType cropType,
            @RequestParam(required = false) Crop.MarketDemandLevel marketDemand) {

        List<Crop> crops;
        if (cropType != null && marketDemand != null) {
            crops = cropService.getAllCrops().stream()
                    .filter(crop -> crop.getCropType() == cropType &&
                            crop.getMarketDemandLevel() == marketDemand)
                    .collect(Collectors.toList());
        } else if (cropType != null) {
            crops = cropService.getCropsByType(cropType);
        } else if (marketDemand != null) {
            crops = cropService.getCropsByMarketDemand(marketDemand);
        } else {
            crops = cropService.getAllCrops();
        }

        List<CropDTO> cropDTOs = convertToCropDTOList(crops);

        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Données exportées (" + cropDTOs.size() + " cultures)", cropDTOs));
    }

    // ==================== HEALTH CHECK ====================

    /**
     * Vérifier l'état du service
     * GET /api/v1/crops/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        long count = cropService.getAllCrops().size();
        String healthInfo = String.format(
                "Service opérationnel - %d cultures en base de données", count);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Service en bonne santé", healthInfo));
    }

    // ==================== CONVERSION METHODS - FIXED ====================

    /**
     * Convertir Crop en CropDTO
     */
    private CropDTO convertToCropDTO(Crop crop) {
        return CropDTO.fromEntity(crop); // Use the static factory method
    }

    /**
     * Convertir liste de Crop en liste de CropDTO
     */
    private List<CropDTO> convertToCropDTOList(List<Crop> crops) {
        return crops.stream()
                .map(CropDTO::fromEntity) // Use the static factory method
                .collect(Collectors.toList());
    }
}