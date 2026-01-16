package SmartAgricultural.Management.Repository;





import SmartAgricultural.Management.Model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByOrderByTimestampDesc();

    @Query("SELECT n FROM Notification n WHERE n.contentHash = :contentHash AND n.timestamp >= :cutoffTime AND n.duplicate = false")
    List<Notification> findSimilarNotificationsAfterTime(@Param("contentHash") String contentHash, @Param("cutoffTime") LocalDateTime cutoffTime);
}
