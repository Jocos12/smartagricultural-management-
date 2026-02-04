package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.Crop;
import SmartAgricultural.Management.Repository.CropRepository;
import SmartAgricultural.Management.exception.CropNotFoundException;
import SmartAgricultural.Management.exception.CropAlreadyExistsException;
import SmartAgricultural.Management.dto.CropDTO;
import SmartAgricultural.Management.dto.CropStatsDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
@Transactional
public class CropService {

    @Autowired
    private CropRepository cropRepository;

    /**
     * Créer une nouvelle culture
     */
    public Crop createCrop(Crop crop) {
        if (crop == null) {
            throw new IllegalArgumentException("La culture ne peut pas être null");
        }

        if (crop.getCropName() == null || crop.getCropName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la culture est obligatoire");
        }

        if (cropRepository.existsByCropNameIgnoreCase(crop.getCropName())) {
            throw new CropAlreadyExistsException("Une culture avec le nom '" + crop.getCropName() + "' existe déjà");
        }

        // Valider les données
        validateCrop(crop);

        return cropRepository.save(crop);
    }

    /**
     * Obtenir toutes les cultures avec pagination
     */
    @Transactional(readOnly = true)
    public Page<Crop> getAllCrops(Pageable pageable) {
        if (pageable == null) {
            pageable = PageRequest.of(0, 20);
        }
        return cropRepository.findAll(pageable);
    }

    /**
     * Obtenir toutes les cultures
     */
    @Transactional(readOnly = true)
    public List<Crop> getAllCrops() {
        return cropRepository.findAll();
    }

    /**
     * Obtenir une culture par ID
     */
    @Transactional(readOnly = true)
    public Crop getCropById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID de la culture ne peut pas être vide");
        }
        return cropRepository.findById(id)
                .orElseThrow(() -> new CropNotFoundException("Culture non trouvée avec l'ID: " + id));
    }

    /**
     * Obtenir une culture par nom (optionnel)
     */
    @Transactional(readOnly = true)
    public Optional<Crop> getCropByName(String cropName) {
        if (cropName == null || cropName.trim().isEmpty()) {
            return Optional.empty();
        }
        return cropRepository.findByCropNameIgnoreCase(cropName);
    }

    /**
     * Mettre à jour une culture
     */
    public Crop updateCrop(String id, Crop updatedCrop) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID de la culture ne peut pas être vide");
        }

        if (updatedCrop == null) {
            throw new IllegalArgumentException("Les données de mise à jour ne peuvent pas être null");
        }

        Crop existingCrop = getCropById(id);

        // Vérifier si le nouveau nom existe déjà (sauf si c'est la même culture)
        if (updatedCrop.getCropName() != null &&
                !existingCrop.getCropName().equalsIgnoreCase(updatedCrop.getCropName()) &&
                cropRepository.existsByCropNameIgnoreCase(updatedCrop.getCropName())) {
            throw new CropAlreadyExistsException("Une culture avec le nom '" + updatedCrop.getCropName() + "' existe déjà");
        }

        // Valider les nouvelles données
        validateCrop(updatedCrop);

        // Mettre à jour les champs seulement s'ils ne sont pas null
        if (updatedCrop.getCropName() != null) {
            existingCrop.setCropName(updatedCrop.getCropName());
        }
        if (updatedCrop.getCropType() != null) {
            existingCrop.setCropType(updatedCrop.getCropType());
        }
        if (updatedCrop.getScientificName() != null) {
            existingCrop.setScientificName(updatedCrop.getScientificName());
        }
        if (updatedCrop.getVariety() != null) {
            existingCrop.setVariety(updatedCrop.getVariety());
        }
        if (updatedCrop.getGrowingPeriodDays() != null) {
            existingCrop.setGrowingPeriodDays(updatedCrop.getGrowingPeriodDays());
        }
        if (updatedCrop.getPlantingSeason() != null) {
            existingCrop.setPlantingSeason(updatedCrop.getPlantingSeason());
        }
        if (updatedCrop.getHarvestSeason() != null) {
            existingCrop.setHarvestSeason(updatedCrop.getHarvestSeason());
        }
        if (updatedCrop.getWaterRequirement() != null) {
            existingCrop.setWaterRequirement(updatedCrop.getWaterRequirement());
        }
        if (updatedCrop.getClimaticRequirement() != null) {
            existingCrop.setClimaticRequirement(updatedCrop.getClimaticRequirement());
        }
        if (updatedCrop.getSoilPhMin() != null) {
            existingCrop.setSoilPhMin(updatedCrop.getSoilPhMin());
        }
        if (updatedCrop.getSoilPhMax() != null) {
            existingCrop.setSoilPhMax(updatedCrop.getSoilPhMax());
        }
        if (updatedCrop.getTemperatureMin() != null) {
            existingCrop.setTemperatureMin(updatedCrop.getTemperatureMin());
        }
        if (updatedCrop.getTemperatureMax() != null) {
            existingCrop.setTemperatureMax(updatedCrop.getTemperatureMax());
        }
        if (updatedCrop.getRainfallRequirement() != null) {
            existingCrop.setRainfallRequirement(updatedCrop.getRainfallRequirement());
        }
        if (updatedCrop.getMarketDemandLevel() != null) {
            existingCrop.setMarketDemandLevel(updatedCrop.getMarketDemandLevel());
        }
        if (updatedCrop.getNutritionalValue() != null) {
            existingCrop.setNutritionalValue(updatedCrop.getNutritionalValue());
        }
        if (updatedCrop.getImageUrl() != null) {
            existingCrop.setImageUrl(updatedCrop.getImageUrl());
        }

        return cropRepository.save(existingCrop);
    }

    /**
     * Supprimer une culture
     */
    public void deleteCrop(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID de la culture ne peut pas être vide");
        }
        if (!cropRepository.existsById(id)) {
            throw new CropNotFoundException("Culture non trouvée avec l'ID: " + id);
        }
        cropRepository.deleteById(id);
    }

    /**
     * Rechercher des cultures par nom ou nom scientifique
     */
    @Transactional(readOnly = true)
    public List<Crop> searchCrops(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCrops();
        }
        return cropRepository.searchByNameOrScientificName(query);
    }

    /**
     * Obtenir les cultures par type
     */
    @Transactional(readOnly = true)
    public List<Crop> getCropsByType(Crop.CropType cropType) {
        if (cropType == null) {
            return getAllCrops();
        }
        return cropRepository.findByCropType(cropType);
    }

    /**
     * Obtenir les cultures par niveau de demande du marché
     */
    @Transactional(readOnly = true)
    public List<Crop> getCropsByMarketDemand(Crop.MarketDemandLevel marketDemand) {
        if (marketDemand == null) {
            return getAllCrops();
        }
        return cropRepository.findByMarketDemandLevel(marketDemand);
    }

    /**
     * Obtenir les cultures par saison de plantation
     */
    @Transactional(readOnly = true)
    public List<Crop> getCropsByPlantingSeason(String season) {
        if (season == null || season.trim().isEmpty()) {
            return getAllCrops();
        }
        return cropRepository.findByPlantingSeasonIgnoreCase(season);
    }

    /**
     * Obtenir les cultures par saison de récolte
     */
    @Transactional(readOnly = true)
    public List<Crop> getCropsByHarvestSeason(String season) {
        if (season == null || season.trim().isEmpty()) {
            return getAllCrops();
        }
        return cropRepository.findByHarvestSeasonIgnoreCase(season);
    }

    /**
     * Obtenir les cultures à forte demande
     */
    @Transactional(readOnly = true)
    public List<Crop> getHighDemandCrops() {
        return cropRepository.findHighDemandCrops();
    }

    /**
     * Obtenir les cultures à croissance rapide
     */
    @Transactional(readOnly = true)
    public List<Crop> getShortSeasonCrops() {
        return cropRepository.findShortSeasonCrops();
    }

    /**
     * Obtenir les cultures à longue conservation
     */
    @Transactional(readOnly = true)
    public List<Crop> getLongStorageCrops() {
        return cropRepository.findLongStorageCrops();
    }

    /**
     * Trouver des cultures adaptées aux conditions spécifiques
     */
    @Transactional(readOnly = true)
    public List<Crop> findSuitableForConditions(BigDecimal temperature, BigDecimal ph, BigDecimal rainfall) {
        return cropRepository.findSuitableForConditions(temperature, ph, rainfall);
    }

    /**
     * Trouver des cultures adaptées à une température
     */
    @Transactional(readOnly = true)
    public List<Crop> findCropsBySuitableTemperature(BigDecimal temperature) {
        if (temperature == null) {
            return getAllCrops();
        }
        return cropRepository.findBySuitableTemperature(temperature);
    }

    /**
     * Trouver des cultures adaptées à un pH
     */
    @Transactional(readOnly = true)
    public List<Crop> findCropsBySuitablePh(BigDecimal ph) {
        if (ph == null) {
            return getAllCrops();
        }
        return cropRepository.findBySuitablePh(ph);
    }

    /**
     * Recherche avancée avec filtres multiples
     */
    @Transactional(readOnly = true)
    public Page<Crop> findWithFilters(Crop.CropType cropType,
                                      Crop.MarketDemandLevel marketDemand,
                                      String plantingSeason,
                                      Integer maxGrowingPeriod,
                                      Integer minStorageLife,
                                      Pageable pageable) {
        if (pageable == null) {
            pageable = PageRequest.of(0, 20);
        }
        return cropRepository.findWithFilters(cropType, marketDemand, plantingSeason,
                maxGrowingPeriod, minStorageLife, pageable);
    }

    /**
     * Obtenir les cultures les plus récentes
     */
    @Transactional(readOnly = true)
    public List<Crop> getMostRecentCrops(int limit) {
        if (limit <= 0) {
            limit = 10; // Valeur par défaut
        }
        Pageable pageable = PageRequest.of(0, limit);
        return cropRepository.findMostRecentCrops(pageable);
    }

    /**
     * Trouver des cultures similaires
     */
    @Transactional(readOnly = true)
    public List<Crop> findSimilarCrops(String cropId, int limit) {
        if (cropId == null || cropId.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID de la culture ne peut pas être vide");
        }

        if (limit <= 0) {
            limit = 5; // Valeur par défaut
        }

        Crop crop = getCropById(cropId);
        Pageable pageable = PageRequest.of(0, limit);

        return cropRepository.findSimilarCrops(
                cropId,
                crop.getCropType(),
                crop.getPlantingSeason(),
                crop.getHarvestSeason(),
                crop.getMarketDemandLevel(),
                pageable
        );
    }

    /**
     * Obtenir les cultures résistantes à la sécheresse
     */
    @Transactional(readOnly = true)
    public List<Crop> getDroughtResistantCrops(BigDecimal maxWaterRequirement) {
        if (maxWaterRequirement == null || maxWaterRequirement.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("L'exigence en eau maximale doit être positive");
        }
        return cropRepository.findDroughtResistantCrops(maxWaterRequirement);
    }

    /**
     * Trouver des cultures par exigence climatique
     */
    @Transactional(readOnly = true)
    public List<Crop> findByClimateRequirement(String climate) {
        if (climate == null || climate.trim().isEmpty()) {
            return getAllCrops();
        }
        return cropRepository.findByClimateRequirement(climate);
    }

    /**
     * Obtenir les cultures avec informations manquantes
     */
    @Transactional(readOnly = true)
    public List<Crop> getCropsWithMissingInfo() {
        return cropRepository.findCropsWithMissingInfo();
    }

    /**
     * Obtenir des statistiques des cultures - CORRECTED VERSION
     */
    @Transactional(readOnly = true)
    public CropStatsDTO getCropStatistics() {
        try {
            List<Object[]> typeStats = cropRepository.getCropStatisticsByType();
            List<Object[]> demandStats = cropRepository.getCropStatisticsByMarketDemand();
            List<Object[]> seasonalStats = cropRepository.getSeasonalDistribution();
            List<Object[]> avgGrowingPeriod = cropRepository.getAverageGrowingPeriodByType();

            Long totalCrops = cropRepository.count();
            Long highDemandCount = cropRepository.countHighDemandCrops();

            return new CropStatsDTO(totalCrops, highDemandCount, typeStats,
                    demandStats, seasonalStats, avgGrowingPeriod);
        } catch (Exception e) {
            // En cas d'erreur, retourner des statistiques vides
            return new CropStatsDTO();
        }
    }

    /**
     * Compter les cultures par type
     */
    @Transactional(readOnly = true)
    public Long countCropsByType(Crop.CropType cropType) {
        if (cropType == null) {
            return cropRepository.count();
        }
        return cropRepository.countByCropType(cropType);
    }

    /**
     * Obtenir les meilleures variétés par type
     */
    @Transactional(readOnly = true)
    public List<Object[]> getTopVarietiesByType(Crop.CropType cropType, int limit) {
        if (cropType == null) {
            throw new IllegalArgumentException("Le type de culture ne peut pas être null");
        }
        if (limit <= 0) {
            limit = 10; // Valeur par défaut
        }
        Pageable pageable = PageRequest.of(0, limit);
        return cropRepository.getTopVarietiesByType(cropType, pageable);
    }

    /**
     * Trouver des cultures par période de croissance
     */
    @Transactional(readOnly = true)
    public List<Crop> findByGrowingPeriodRange(Integer minDays, Integer maxDays) {
        if (minDays != null && minDays < 0) {
            throw new IllegalArgumentException("Le nombre minimum de jours doit être positif");
        }
        if (maxDays != null && maxDays < 0) {
            throw new IllegalArgumentException("Le nombre maximum de jours doit être positif");
        }
        if (minDays != null && maxDays != null && minDays > maxDays) {
            throw new IllegalArgumentException("Le minimum ne peut pas être supérieur au maximum");
        }

        return cropRepository.findByGrowingPeriodDaysBetween(minDays, maxDays);
    }

    /**
     * Trouver des cultures par pluviométrie
     */
    @Transactional(readOnly = true)
    public List<Crop> findByRainfallRange(BigDecimal minRainfall, BigDecimal maxRainfall) {
        if (minRainfall != null && minRainfall.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La pluviométrie minimale doit être positive");
        }
        if (maxRainfall != null && maxRainfall.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La pluviométrie maximale doit être positive");
        }
        if (minRainfall != null && maxRainfall != null && minRainfall.compareTo(maxRainfall) > 0) {
            throw new IllegalArgumentException("Le minimum ne peut pas être supérieur au maximum");
        }

        return cropRepository.findByRainfallRequirementBetween(minRainfall, maxRainfall);
    }

    /**
     * Valider les données d'une culture - IMPROVED VERSION
     */
    private void validateCrop(Crop crop) {
        if (crop == null) {
            throw new IllegalArgumentException("La culture ne peut pas être null");
        }

        // Validation des températures
        if (crop.getTemperatureMin() != null && crop.getTemperatureMax() != null &&
                crop.getTemperatureMin().compareTo(crop.getTemperatureMax()) > 0) {
            throw new IllegalArgumentException("La température minimale ne peut pas être supérieure à la température maximale");
        }

        // Validation du pH
        if (crop.getSoilPhMin() != null && crop.getSoilPhMax() != null &&
                crop.getSoilPhMin().compareTo(crop.getSoilPhMax()) > 0) {
            throw new IllegalArgumentException("Le pH minimal ne peut pas être supérieur au pH maximal");
        }

        // Validation des valeurs négatives
        if (crop.getGrowingPeriodDays() != null && crop.getGrowingPeriodDays() <= 0) {
            throw new IllegalArgumentException("La période de croissance doit être positive");
        }

        if (crop.getStorageLifeDays() != null && crop.getStorageLifeDays() <= 0) {
            throw new IllegalArgumentException("La durée de conservation doit être positive");
        }

        if (crop.getWaterRequirement() != null && crop.getWaterRequirement().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("L'exigence en eau doit être positive");
        }

        if (crop.getRainfallRequirement() != null && crop.getRainfallRequirement().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("L'exigence en pluviométrie doit être positive");
        }

        // Validation des plages de pH
        if (crop.getSoilPhMin() != null && (crop.getSoilPhMin().compareTo(BigDecimal.ZERO) < 0 ||
                crop.getSoilPhMin().compareTo(new BigDecimal("14")) > 0)) {
            throw new IllegalArgumentException("Le pH minimum doit être entre 0 et 14");
        }

        if (crop.getSoilPhMax() != null && (crop.getSoilPhMax().compareTo(BigDecimal.ZERO) < 0 ||
                crop.getSoilPhMax().compareTo(new BigDecimal("14")) > 0)) {
            throw new IllegalArgumentException("Le pH maximum doit être entre 0 et 14");
        }
    }

    /**
     * Recommander des cultures basées sur les conditions
     */
    @Transactional(readOnly = true)
    public List<Crop> recommendCrops(BigDecimal temperature, BigDecimal ph,
                                     BigDecimal rainfall, String season,
                                     Crop.MarketDemandLevel preferredDemand) {

        List<Crop> suitableCrops = findSuitableForConditions(temperature, ph, rainfall);

        return suitableCrops.stream()
                .filter(crop -> season == null || season.trim().isEmpty() ||
                        crop.getPlantingSeason().equalsIgnoreCase(season.trim()))
                .filter(crop -> preferredDemand == null ||
                        crop.getMarketDemandLevel() == preferredDemand)
                .sorted((c1, c2) -> {
                    // Prioriser les cultures à forte demande
                    if (c1.getMarketDemandLevel() != c2.getMarketDemandLevel()) {
                        return c2.getMarketDemandLevel().compareTo(c1.getMarketDemandLevel());
                    }
                    // Puis par période de croissance (plus courte en premier)
                    if (c1.getGrowingPeriodDays() != null && c2.getGrowingPeriodDays() != null) {
                        return c1.getGrowingPeriodDays().compareTo(c2.getGrowingPeriodDays());
                    }
                    // Si une des périodes est null, la mettre en dernier
                    if (c1.getGrowingPeriodDays() == null) return 1;
                    if (c2.getGrowingPeriodDays() == null) return -1;

                    return 0;
                })
                .collect(Collectors.toList());
    }
}