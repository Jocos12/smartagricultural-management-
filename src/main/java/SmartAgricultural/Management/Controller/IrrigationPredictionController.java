package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.IrrigationPrediction;
import SmartAgricultural.Management.Service.IrrigationPredictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/irrigation/predictions")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class IrrigationPredictionController {

    private static final Logger logger = LoggerFactory.getLogger(IrrigationPredictionController.class);

    private final IrrigationPredictionService predictionService;

    @Autowired
    public IrrigationPredictionController(IrrigationPredictionService predictionService) {
        this.predictionService = predictionService;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }

    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generatePrediction(
            @RequestParam String farmId,
            @RequestParam(required = false) String cropProductionId,
            @RequestParam(defaultValue = "30") int historicalDays) {
        try {
            logger.info("Generating prediction for farm: {}, crop: {}, days: {}",
                    farmId, cropProductionId, historicalDays);

            IrrigationPrediction prediction = predictionService.generatePrediction(
                    farmId, cropProductionId, historicalDays);

            return ResponseEntity.ok(createSuccessResponse(
                    "Prediction generated successfully", prediction));
        } catch (RuntimeException e) {
            logger.error("Error generating prediction: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error generating prediction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error generating prediction: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPredictionById(@PathVariable String id) {
        try {
            Optional<IrrigationPrediction> prediction = predictionService.findById(id);

            if (prediction.isPresent()) {
                return ResponseEntity.ok(prediction.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Prediction not found with id: " + id));
            }
        } catch (Exception e) {
            logger.error("Error retrieving prediction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving prediction: " + e.getMessage()));
        }
    }

    @GetMapping("/farm/{farmId}")
    public ResponseEntity<?> getPredictionsByFarm(@PathVariable String farmId) {
        try {
            List<IrrigationPrediction> predictions = predictionService.findByFarmId(farmId);
            return ResponseEntity.ok(predictions);
        } catch (Exception e) {
            logger.error("Error retrieving farm predictions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving farm predictions: " + e.getMessage()));
        }
    }

    @GetMapping("/farm/{farmId}/latest")
    public ResponseEntity<?> getLatestPrediction(@PathVariable String farmId) {
        try {
            Optional<IrrigationPrediction> prediction =
                    predictionService.findLatestPredictionByFarm(farmId);

            if (prediction.isPresent()) {
                return ResponseEntity.ok(prediction.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("No prediction found for farm: " + farmId));
            }
        } catch (Exception e) {
            logger.error("Error retrieving latest prediction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving latest prediction: " + e.getMessage()));
        }
    }

    @GetMapping("/alerts/critical")
    public ResponseEntity<?> getCriticalAlerts() {
        try {
            List<IrrigationPrediction> criticalAlerts = predictionService.findCriticalAlerts();
            return ResponseEntity.ok(criticalAlerts);
        } catch (Exception e) {
            logger.error("Error retrieving critical alerts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error retrieving critical alerts: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePrediction(@PathVariable String id) {
        try {
            predictionService.deleteById(id);
            return ResponseEntity.ok(createSuccessResponse("Prediction deleted successfully", null));
        } catch (Exception e) {
            logger.error("Error deleting prediction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error deleting prediction: " + e.getMessage()));
        }
    }
}