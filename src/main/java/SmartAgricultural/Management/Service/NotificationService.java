package SmartAgricultural.Management.Service;







import SmartAgricultural.Management.Model.ChatNotification;
import SmartAgricultural.Management.Model.Notification;
import SmartAgricultural.Management.Repository.ChatNotificationRepository;
import SmartAgricultural.Management.Repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ChatNotificationRepository chatNotificationRepository;

    // Cache en mémoire pour réduire les requêtes à la base de données
    private final Map<String, LocalDateTime> recentHashesCache = new ConcurrentHashMap<>();

    // Nettoyer ce cache périodiquement
    @Scheduled(fixedRate = 900000) // 15 minutes
    public void cleanupCache() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(15);
        recentHashesCache.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoffTime));
    }

    // Générer un hash pour identifier les notifications similaires
    private String generateContentHash(String message, String type) {
        try {
            // Normaliser le message - supprimer les espaces supplémentaires, etc.
            String normalizedMessage = message.trim().replaceAll("\\s+", " ");
            String content = normalizedMessage + "_" + type;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            // Fallback à un hash plus simple
            return String.valueOf((message + "_" + type).hashCode());
        }
    }

    // Vérifier si une notification similaire a été envoyée récemment
    public boolean isSimilarNotificationRecentlySent(String message, String type) {
        String contentHash = generateContentHash(message, type);

        // Vérifier d'abord dans le cache
        if (recentHashesCache.containsKey(contentHash)) {
            return true;
        }

        // Si pas dans le cache, vérifier dans la base de données
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(15);
        List<Notification> recentSimilarNotifications =
                notificationRepository.findSimilarNotificationsAfterTime(contentHash, cutoffTime);

        boolean hasSimilar = !recentSimilarNotifications.isEmpty();
        if (hasSimilar) {
            // Ajouter au cache
            recentHashesCache.put(contentHash, LocalDateTime.now());
        }

        return hasSimilar;
    }

    // Créer et sauvegarder une notification avec vérification des doublons (version originale)
    public Notification createNotification(String message, String type) {
        // Pour les types d'alerte important (erreur/accident), assurez-vous qu'ils sont traités
        // même s'ils sont similaires à une notification récente
        boolean isHighPriority = "error".equals(type) || "accident".equals(type);

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setType(type);
        notification.setTimestamp(LocalDateTime.now());

        // Générer le hash de contenu
        String contentHash = generateContentHash(message, type);
        notification.setContentHash(contentHash);

        // Vérifier seulement les notifications non prioritaires pour les doublons
        if (!isHighPriority && isSimilarNotificationRecentlySent(message, type)) {
            // Marquer comme doublon mais toujours sauvegarder pour le suivi
            notification.setDuplicate(true);
            notification = notificationRepository.save(notification);
            return null; // Retourner null pour indiquer que ça ne devrait pas être affiché
        }

        // Ajouter au cache pour les vérifications futures
        recentHashesCache.put(contentHash, LocalDateTime.now());

        // C'est une nouvelle notification ou assez de temps a passé
        notification.setDuplicate(false);
        return notificationRepository.save(notification);
    }

    // Nouvelle méthode pour créer des notifications de chat
    public ChatNotification createNotification(String senderId, String recipientId, String content, ChatNotification.NotificationType type) {
        ChatNotification notification = new ChatNotification(senderId, recipientId, content, type);
        return chatNotificationRepository.save(notification);
    }

    // Méthode pour envoyer des notifications push (implémentation basique)
    public void sendPushNotification(String recipientId, String title, String message) {
        // Implémentation basique - vous pouvez l'étendre avec un service de push réel
        // comme Firebase Cloud Messaging (FCM) ou Apple Push Notification Service (APNs)

        // Pour l'instant, on crée une notification système
        ChatNotification pushNotification = new ChatNotification();
        pushNotification.setSenderId("SYSTEM");
        pushNotification.setRecipientId(recipientId);
        pushNotification.setContent(title + ": " + message);
        pushNotification.setType(ChatNotification.NotificationType.SYSTEM_ALERT);
        pushNotification.setTimestamp(LocalDateTime.now());

        chatNotificationRepository.save(pushNotification);

        // TODO: Intégrer avec un service de push réel
        // Exemple avec FCM:
        // fcmService.sendNotification(recipientId, title, message);
    }

    // Récupérer les notifications non lues pour un utilisateur
    public List<ChatNotification> getUnreadNotifications(String recipientId) {
        return chatNotificationRepository.findByRecipientIdAndReadFalseOrderByTimestampDesc(recipientId);
    }

    // Marquer une notification comme lue
    public void markNotificationAsRead(Long notificationId, String recipientId) {
        ChatNotification notification = chatNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (notification.getRecipientId().equals(recipientId)) {
            notification.setRead(true);
            chatNotificationRepository.save(notification);
        } else {
            throw new RuntimeException("Unauthorized access to notification");
        }
    }

    // Récupérer toutes les notifications non-dupliquées (version originale)
    public List<Notification> getAllNonDuplicateNotifications() {
        return notificationRepository.findAll().stream()
                .filter(n -> !n.isDuplicate())
                .toList();
    }

    // Vérifier si un utilisateur est en ligne (méthode helper)
    public boolean isUserOnline(String userId) {
        // Cette méthode devrait être déplacée vers ChatService ou UserService
        // Pour l'instant, retourne false par défaut
        return false;
    }
}

