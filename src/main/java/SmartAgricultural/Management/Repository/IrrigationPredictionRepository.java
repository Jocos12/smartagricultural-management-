package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.IrrigationPrediction;
import SmartAgricultural.Management.Model.IrrigationPrediction.AlertLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IrrigationPredictionRepository extends JpaRepository<IrrigationPrediction, String> {

    List<IrrigationPrediction> findByFarmId(String farmId);

    List<IrrigationPrediction> findByCropProductionId(String cropProductionId);

    List<IrrigationPrediction> findByAlertLevel(AlertLevel alertLevel);

    @Query("SELECT ip FROM IrrigationPrediction ip WHERE ip.farmId = :farmId " +
            "AND ip.predictionDate >= :startDate AND ip.predictionDate <= :endDate " +
            "ORDER BY ip.predictionDate DESC")
    List<IrrigationPrediction> findByFarmAndDateRange(
            @Param("farmId") String farmId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT ip FROM IrrigationPrediction ip WHERE ip.farmId = :farmId " +
            "ORDER BY ip.predictionDate DESC")
    List<IrrigationPrediction> findLatestByFarm(@Param("farmId") String farmId);

    @Query("SELECT ip FROM IrrigationPrediction ip WHERE ip.alertLevel IN ('HIGH', 'CRITICAL') " +
            "ORDER BY ip.predictionDate DESC")
    List<IrrigationPrediction> findCriticalAlerts();

    @Query("SELECT ip FROM IrrigationPrediction ip WHERE ip.farmId = :farmId " +
            "ORDER BY ip.predictionDate DESC LIMIT 1")
    Optional<IrrigationPrediction> findLatestPredictionByFarm(@Param("farmId") String farmId);
}