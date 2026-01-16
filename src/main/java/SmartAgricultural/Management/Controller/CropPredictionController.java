package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.dto.ApiResponse;
import SmartAgricultural.Management.dto.CropPredictionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/crops")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CropPredictionController {

    private final Random random = new Random();

    /**
     * Prédiction pour une culture spécifique
     * GET /api/v1/crops/{id}/prediction
     */
    @GetMapping("/{id}/prediction")
    public ResponseEntity<ApiResponse<CropPredictionDTO>> getCropPrediction(@PathVariable String id) {
        CropPredictionDTO prediction = generatePrediction(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Prédiction générée avec succès", prediction)
        );
    }

    /**
     * Prédictions pour toutes les cultures
     * GET /api/v1/crops/predictions/all
     */
    @GetMapping("/predictions/all")
    public ResponseEntity<ApiResponse<Map<String, CropPredictionDTO>>> getAllPredictions() {
        Map<String, CropPredictionDTO> predictions = new HashMap<>();

        // Simuler des prédictions pour différentes cultures
        for (int i = 0; i < 10; i++) {
            String cropId = "CR" + i;
            predictions.put(cropId, generatePrediction(cropId));
        }

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Prédictions générées avec succès", predictions)
        );
    }

    /**
     * Prédiction avec paramètres personnalisés
     * POST /api/v1/crops/predict
     */
    @PostMapping("/predict")
    public ResponseEntity<ApiResponse<CropPredictionDTO>> predictWithParams(
            @RequestBody Map<String, Object> params) {

        String cropId = (String) params.getOrDefault("cropId", "UNKNOWN");
        Double temperature = ((Number) params.getOrDefault("temperature", 25.0)).doubleValue();
        Double rainfall = ((Number) params.getOrDefault("rainfall", 800.0)).doubleValue();
        Double soilPh = ((Number) params.getOrDefault("soilPh", 6.5)).doubleValue();

        CropPredictionDTO prediction = generateAdvancedPrediction(cropId, temperature, rainfall, soilPh);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Prédiction personnalisée générée", prediction)
        );
    }

    // Méthodes privées pour générer les prédictions

    private CropPredictionDTO generatePrediction(String cropId) {
        CropPredictionDTO prediction = new CropPredictionDTO();

        prediction.setCropId(cropId);
        prediction.setPredictedYield(generateYield());
        prediction.setYieldUnit("tons/hectare");
        prediction.setConfidenceScore(generateConfidence());
        prediction.setRiskScore(generateRiskScore());
        prediction.setQualityScore(generateQualityScore());
        prediction.setPredictionDate(java.time.LocalDateTime.now());
        prediction.setOptimalHarvestDate(java.time.LocalDate.now().plusDays(random.nextInt(90) + 30));
        prediction.setRecommendations(generateRecommendations());
        prediction.setWeatherImpact(generateWeatherImpact());
        prediction.setSoilHealthScore(generateSoilHealth());
        prediction.setPestRiskLevel(generatePestRisk());
        prediction.setExpectedProfit(generateProfit());
        prediction.setMarketPrice(generateMarketPrice());

        return prediction;
    }

    private CropPredictionDTO generateAdvancedPrediction(String cropId, Double temperature,
                                                         Double rainfall, Double soilPh) {
        CropPredictionDTO prediction = generatePrediction(cropId);

        // Ajuster les prédictions basées sur les paramètres
        double tempFactor = calculateTemperatureFactor(temperature);
        double rainFactor = calculateRainfallFactor(rainfall);
        double phFactor = calculatePhFactor(soilPh);

        double combinedFactor = (tempFactor + rainFactor + phFactor) / 3.0;

        // Ajuster le rendement
        BigDecimal baseYield = prediction.getPredictedYield();
        BigDecimal adjustedYield = baseYield.multiply(BigDecimal.valueOf(combinedFactor))
                .setScale(2, RoundingMode.HALF_UP);
        prediction.setPredictedYield(adjustedYield);

        // Ajuster le score de confiance
        prediction.setConfidenceScore(Math.min(95.0, prediction.getConfidenceScore() * combinedFactor));

        // Ajuster le risque
        prediction.setRiskScore(Math.max(5.0, prediction.getRiskScore() / combinedFactor));

        return prediction;
    }

    // Facteurs de calcul

    private double calculateTemperatureFactor(Double temperature) {
        // Optimal: 20-30°C
        if (temperature >= 20 && temperature <= 30) {
            return 1.0;
        } else if (temperature >= 15 && temperature < 20) {
            return 0.8 + (temperature - 15) * 0.04;
        } else if (temperature > 30 && temperature <= 35) {
            return 1.0 - (temperature - 30) * 0.04;
        } else {
            return 0.6;
        }
    }

    private double calculateRainfallFactor(Double rainfall) {
        // Optimal: 600-1200mm
        if (rainfall >= 600 && rainfall <= 1200) {
            return 1.0;
        } else if (rainfall >= 400 && rainfall < 600) {
            return 0.7 + (rainfall - 400) * 0.0015;
        } else if (rainfall > 1200 && rainfall <= 1500) {
            return 1.0 - (rainfall - 1200) * 0.001;
        } else {
            return 0.5;
        }
    }

    private double calculatePhFactor(Double soilPh) {
        // Optimal: 6.0-7.5
        if (soilPh >= 6.0 && soilPh <= 7.5) {
            return 1.0;
        } else if (soilPh >= 5.5 && soilPh < 6.0) {
            return 0.8 + (soilPh - 5.5) * 0.4;
        } else if (soilPh > 7.5 && soilPh <= 8.0) {
            return 1.0 - (soilPh - 7.5) * 0.4;
        } else {
            return 0.6;
        }
    }

    // Générateurs de valeurs

    private BigDecimal generateYield() {
        double yield = 2.0 + (random.nextDouble() * 8.0); // 2-10 tons/hectare
        return BigDecimal.valueOf(yield).setScale(2, RoundingMode.HALF_UP);
    }

    private Double generateConfidence() {
        return 70.0 + (random.nextDouble() * 25.0); // 70-95%
    }

    private Double generateRiskScore() {
        return 5.0 + (random.nextDouble() * 35.0); // 5-40%
    }

    private Double generateQualityScore() {
        return 70.0 + (random.nextDouble() * 30.0); // 70-100%
    }

    private Double generateSoilHealth() {
        return 60.0 + (random.nextDouble() * 40.0); // 60-100%
    }

    private String generatePestRisk() {
        String[] risks = {"LOW", "MEDIUM", "HIGH"};
        return risks[random.nextInt(risks.length)];
    }

    private BigDecimal generateProfit() {
        double profit = 500.0 + (random.nextDouble() * 2000.0); // $500-$2500 per hectare
        return BigDecimal.valueOf(profit).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal generateMarketPrice() {
        double price = 200.0 + (random.nextDouble() * 800.0); // $200-$1000 per ton
        return BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
    }

    private String[] generateRecommendations() {
        String[][] possibleRecommendations = {
                {"Increase irrigation by 20%", "Apply nitrogen fertilizer", "Monitor pest activity"},
                {"Maintain current practices", "Prepare for harvest", "Check soil moisture weekly"},
                {"Reduce water usage", "Apply organic compost", "Monitor disease symptoms"},
                {"Improve drainage", "Add phosphorus fertilizer", "Control weed growth"}
        };
        return possibleRecommendations[random.nextInt(possibleRecommendations.length)];
    }

    private String generateWeatherImpact() {
        String[] impacts = {
                "Favorable conditions expected",
                "Moderate rainfall predicted - beneficial",
                "High temperatures forecasted - monitor irrigation",
                "Optimal growing conditions",
                "Potential dry spell - increase watering"
        };
        return impacts[random.nextInt(impacts.length)];
    }
}