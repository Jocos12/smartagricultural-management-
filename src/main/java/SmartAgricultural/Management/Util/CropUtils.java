// ===== CLASSE UTILITAIRE POUR LES CULTURES =====

package SmartAgricultural.Management.Util;

import SmartAgricultural.Management.Model.Crop;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Classe utilitaire pour les opérations sur les cultures
 */
public class CropUtils {

    /**
     * Calculer la durée de croissance moyenne pour une liste de cultures
     */
    public static double calculateAverageGrowingPeriod(List<Crop> crops) {
        return crops.stream()
                .filter(crop -> crop.getGrowingPeriodDays() != null)
                .mapToInt(Crop::getGrowingPeriodDays)
                .average()
                .orElse(0.0);
    }

    /**
     * Grouper les cultures par type
     */
    public static Map<Crop.CropType, List<Crop>> groupByType(List<Crop> crops) {
        return crops.stream()
                .collect(Collectors.groupingBy(Crop::getCropType));
    }

    /**
     * Grouper les cultures par saison de plantation
     */
    public static Map<String, List<Crop>> groupByPlantingSeason(List<Crop> crops) {
        return crops.stream()
                .collect(Collectors.groupingBy(Crop::getPlantingSeason));
    }

    /**
     * Filtrer les cultures par température optimale
     */
    public static List<Crop> filterByOptimalTemperature(List<Crop> crops, BigDecimal temperature) {
        return crops.stream()
                .filter(crop -> isTemperatureSuitable(crop, temperature))
                .collect(Collectors.toList());
    }

    /**
     * Vérifier si une température convient à une culture
     */
    public static boolean isTemperatureSuitable(Crop crop, BigDecimal temperature) {
        if (temperature == null) return false;

        BigDecimal minTemp = crop.getTemperatureMin();
        BigDecimal maxTemp = crop.getTemperatureMax();

        if (minTemp == null && maxTemp == null) return true;
        if (minTemp == null) return temperature.compareTo(maxTemp) <= 0;
        if (maxTemp == null) return temperature.compareTo(minTemp) >= 0;

        return temperature.compareTo(minTemp) >= 0 && temperature.compareTo(maxTemp) <= 0;
    }

    /**
     * Vérifier si un pH convient à une culture
     */
    public static boolean isPhSuitable(Crop crop, BigDecimal ph) {
        if (ph == null) return false;

        BigDecimal minPh = crop.getSoilPhMin();
        BigDecimal maxPh = crop.getSoilPhMax();

        if (minPh == null && maxPh == null) return true;
        if (minPh == null) return ph.compareTo(maxPh) <= 0;
        if (maxPh == null) return ph.compareTo(minPh) >= 0;

        return ph.compareTo(minPh) >= 0 && ph.compareTo(maxPh) <= 0;
    }

    /**
     * Calculer le score de compatibilité d'une culture avec des conditions données
     */
    public static double calculateCompatibilityScore(Crop crop, BigDecimal temperature,
                                                     BigDecimal ph, BigDecimal rainfall) {
        double score = 0.0;
        int factors = 0;

        // Score température
        if (temperature != null) {
            factors++;
            if (isTemperatureSuitable(crop, temperature)) {
                score += 1.0;
            }
        }

        // Score pH
        if (ph != null) {
            factors++;
            if (isPhSuitable(crop, ph)) {
                score += 1.0;
            }
        }

        // Score pluviométrie
        if (rainfall != null && crop.getRainfallRequirement() != null) {
            factors++;
            BigDecimal required = crop.getRainfallRequirement();
            // Tolérance de ±20%
            BigDecimal tolerance = required.multiply(new BigDecimal("0.2"));
            if (rainfall.compareTo(required.subtract(tolerance)) >= 0 &&
                    rainfall.compareTo(required.add(tolerance)) <= 0) {
                score += 1.0;
            }
        }

        return factors > 0 ? score / factors : 0.0;
    }

    /**
     * Obtenir les cultures recommandées triées par score de compatibilité
     */
    public static List<Crop> getRecommendedCrops(List<Crop> crops, BigDecimal temperature,
                                                 BigDecimal ph, BigDecimal rainfall) {
        return crops.stream()
                .filter(crop -> calculateCompatibilityScore(crop, temperature, ph, rainfall) > 0.5)
                .sorted((c1, c2) -> Double.compare(
                        calculateCompatibilityScore(c2, temperature, ph, rainfall),
                        calculateCompatibilityScore(c1, temperature, ph, rainfall)))
                .collect(Collectors.toList());
    }
}
