package SmartAgricultural.Management.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

    private final Map<String, OtpEntry> otpStorage = new ConcurrentHashMap<>();
    private final long EXPIRATION_TIME_MS = 5 * 60 * 1000; // 5 minutes
    private final long EXTENDED_EXPIRATION_TIME_MS = 10 * 60 * 1000; // 10 minutes for sensitive operations
    private final int OTP_LENGTH = 6;
    private final int ADMIN_OTP_LENGTH = 8; // Longer OTP for admin operations
    private final Random random = new Random();

    // Rate limiting: max attempts per email
    private final Map<String, AttemptEntry> attemptStorage = new ConcurrentHashMap<>();
    private final int MAX_ATTEMPTS = 3;
    private final long ATTEMPT_RESET_TIME_MS = 15 * 60 * 1000; // 15 minutes

    // Scheduler pour nettoyer automatiquement les OTP expirés
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public OtpService() {
        // Démarrer le nettoyage automatique des OTP expirés toutes les 2 minutes
        scheduler.scheduleAtFixedRate(this::cleanupExpiredOtps, 2, 2, TimeUnit.MINUTES);
        scheduler.scheduleAtFixedRate(this::cleanupExpiredAttempts, 5, 5, TimeUnit.MINUTES);
        logger.info("OtpService initialized with automatic cleanup every 2 minutes for Smart Agricultural Management System");
    }

    /**
     * Génère un OTP standard de 6 chiffres pour l'email donné
     * @param email L'email de l'utilisateur
     * @return Le code OTP généré
     */
    public String generateOtp(String email) {
        return generateOtp(email, OtpType.STANDARD);
    }

    /**
     * Génère un OTP avec type spécifique pour l'email donné
     * @param email L'email de l'utilisateur
     * @param type Le type d'OTP à générer
     * @return Le code OTP généré
     */
    public String generateOtp(String email, OtpType type) {
        if (email == null || email.trim().isEmpty()) {
            logger.error("Cannot generate OTP: email is null or empty");
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        String normalizedEmail = email.trim().toLowerCase();

        // Vérifier si l'utilisateur a atteint la limite de tentatives
        if (isAccountLocked(normalizedEmail)) {
            logger.warn("OTP generation blocked: Account locked for email: {}", normalizedEmail);
            throw new RuntimeException("Account temporarily locked due to multiple failed attempts. Please try again later.");
        }

        // Déterminer la longueur et l'expiration selon le type
        int otpLength = (type == OtpType.ADMIN_OPERATION) ? ADMIN_OTP_LENGTH : OTP_LENGTH;
        long expirationTime = (type == OtpType.SENSITIVE_OPERATION) ? EXTENDED_EXPIRATION_TIME_MS : EXPIRATION_TIME_MS;

        // Générer l'OTP
        String otp = generateOtpCode(otpLength);

        // Stocker l'OTP avec timestamp et type
        OtpEntry entry = new OtpEntry(otp, Instant.now(), type, expirationTime);
        otpStorage.put(normalizedEmail, entry);

        logger.info("OTP generated for email: {} (type: {}, expires at: {})",
                normalizedEmail,
                type,
                LocalDateTime.now().plusSeconds(expirationTime / 1000)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return otp;
    }

    /**
     * Génère un OTP pour une opération spécifique d'un fermier
     */
    public String generateFarmingOtp(String email) {
        logger.info("Generating farming-specific OTP for farmer: {}", email);
        return generateOtp(email, OtpType.FARMING_OPERATION);
    }

    /**
     * Génère un OTP pour une opération d'achat
     */
    public String generateBuyingOtp(String email) {
        logger.info("Generating buying-specific OTP for buyer: {}", email);
        return generateOtp(email, OtpType.BUYING_OPERATION);
    }

    /**
     * Génère un OTP pour une opération d'analyse
     */
    public String generateAnalysisOtp(String email) {
        logger.info("Generating analysis-specific OTP for analyst: {}", email);
        return generateOtp(email, OtpType.ANALYSIS_OPERATION);
    }

    /**
     * Génère un OTP pour une opération gouvernementale
     */
    public String generateGovernmentOtp(String email) {
        logger.info("Generating government-specific OTP for government user: {}", email);
        return generateOtp(email, OtpType.GOVERNMENT_OPERATION);
    }

    /**
     * Génère un OTP pour une opération administrative
     */
    public String generateAdminOtp(String email) {
        logger.info("Generating admin-specific OTP for admin: {}", email);
        return generateOtp(email, OtpType.ADMIN_OPERATION);
    }

    /**
     * Valide l'OTP pour l'email donné
     * @param email L'email de l'utilisateur
     * @param otp Le code OTP à valider
     * @return true si l'OTP est valide, false sinon
     */
    public boolean validateOtp(String email, String otp) {
        if (email == null || email.trim().isEmpty()) {
            logger.error("Cannot validate OTP: email is null or empty");
            return false;
        }

        if (otp == null || otp.trim().isEmpty()) {
            logger.error("Cannot validate OTP: OTP is null or empty");
            return false;
        }

        String normalizedEmail = email.trim().toLowerCase();
        String normalizedOtp = otp.trim();

        // Vérifier si l'account est verrouillé
        if (isAccountLocked(normalizedEmail)) {
            logger.warn("OTP validation blocked: Account locked for email: {}", normalizedEmail);
            return false;
        }

        // Vérifier si l'OTP existe
        if (!otpStorage.containsKey(normalizedEmail)) {
            logger.warn("OTP validation failed: No OTP found for email: {}", normalizedEmail);
            incrementFailedAttempts(normalizedEmail);
            return false;
        }

        OtpEntry entry = otpStorage.get(normalizedEmail);

        // Vérifier l'expiration
        if (Instant.now().isAfter(entry.timestamp.plusMillis(entry.expirationTime))) {
            logger.warn("OTP validation failed: OTP expired for email: {}", normalizedEmail);
            otpStorage.remove(normalizedEmail);
            incrementFailedAttempts(normalizedEmail);
            return false;
        }

        // Vérifier le code OTP
        boolean isValid = entry.otp.equals(normalizedOtp);

        if (isValid) {
            logger.info("OTP validation successful for email: {} (type: {})", normalizedEmail, entry.type);
            // Réinitialiser les tentatives échouées en cas de succès
            attemptStorage.remove(normalizedEmail);
        } else {
            logger.warn("OTP validation failed: Invalid OTP for email: {}", normalizedEmail);
            incrementFailedAttempts(normalizedEmail);
        }

        return isValid;
    }

    /**
     * Valide l'OTP avec vérification du type
     */
    public boolean validateOtp(String email, String otp, OtpType expectedType) {
        boolean isValid = validateOtp(email, otp);

        if (isValid) {
            OtpEntry entry = otpStorage.get(email.trim().toLowerCase());
            if (entry != null && entry.type != expectedType) {
                logger.warn("OTP type mismatch for email: {}. Expected: {}, Found: {}", email, expectedType, entry.type);
                return false;
            }
        }

        return isValid;
    }

    /**
     * Supprime l'OTP pour l'email donné
     * @param email L'email de l'utilisateur
     */
    public void clearOtp(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.error("Cannot clear OTP: email is null or empty");
            return;
        }

        String normalizedEmail = email.trim().toLowerCase();

        if (otpStorage.remove(normalizedEmail) != null) {
            logger.info("OTP cleared for email: {}", normalizedEmail);
        } else {
            logger.debug("No OTP found to clear for email: {}", normalizedEmail);
        }
    }

    /**
     * Vérifie si un OTP existe pour l'email donné
     * @param email L'email de l'utilisateur
     * @return true si un OTP existe, false sinon
     */
    public boolean hasOtp(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String normalizedEmail = email.trim().toLowerCase();
        return otpStorage.containsKey(normalizedEmail);
    }

    /**
     * Vérifie si un OTP d'un type spécifique existe
     */
    public boolean hasOtpOfType(String email, OtpType type) {
        if (!hasOtp(email)) return false;

        String normalizedEmail = email.trim().toLowerCase();
        OtpEntry entry = otpStorage.get(normalizedEmail);
        return entry != null && entry.type == type;
    }

    /**
     * Retourne le temps restant avant expiration de l'OTP en secondes
     * @param email L'email de l'utilisateur
     * @return Le temps restant en secondes, ou -1 si aucun OTP n'existe
     */
    public long getRemainingTimeSeconds(String email) {
        if (email == null || email.trim().isEmpty()) {
            return -1;
        }

        String normalizedEmail = email.trim().toLowerCase();

        if (!otpStorage.containsKey(normalizedEmail)) {
            return -1;
        }

        OtpEntry entry = otpStorage.get(normalizedEmail);
        long elapsedTime = Instant.now().toEpochMilli() - entry.timestamp.toEpochMilli();
        long remainingTime = (entry.expirationTime - elapsedTime) / 1000;

        return Math.max(0, remainingTime);
    }

    /**
     * Vérifie si le compte est temporairement verrouillé
     */
    public boolean isAccountLocked(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String normalizedEmail = email.trim().toLowerCase();
        AttemptEntry attempts = attemptStorage.get(normalizedEmail);

        if (attempts == null) return false;

        // Vérifier si la période de verrouillage est expirée
        if (Instant.now().isAfter(attempts.lastAttempt.plusMillis(ATTEMPT_RESET_TIME_MS))) {
            attemptStorage.remove(normalizedEmail);
            return false;
        }

        return attempts.count >= MAX_ATTEMPTS;
    }

    /**
     * Retourne le temps restant avant déblocage du compte en minutes
     */
    public long getLockoutRemainingMinutes(String email) {
        if (!isAccountLocked(email)) return 0;

        String normalizedEmail = email.trim().toLowerCase();
        AttemptEntry attempts = attemptStorage.get(normalizedEmail);

        if (attempts == null) return 0;

        long elapsedTime = Instant.now().toEpochMilli() - attempts.lastAttempt.toEpochMilli();
        long remainingTime = (ATTEMPT_RESET_TIME_MS - elapsedTime) / (1000 * 60);

        return Math.max(0, remainingTime);
    }

    /**
     * Génère un code OTP de la longueur spécifiée
     */
    private String generateOtpCode(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    /**
     * Incrémente le compteur de tentatives échouées
     */
    private void incrementFailedAttempts(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        AttemptEntry attempts = attemptStorage.get(normalizedEmail);

        if (attempts == null) {
            attempts = new AttemptEntry(1, Instant.now());
        } else {
            attempts = new AttemptEntry(attempts.count + 1, Instant.now());
        }

        attemptStorage.put(normalizedEmail, attempts);

        if (attempts.count >= MAX_ATTEMPTS) {
            logger.warn("Account locked for email: {} after {} failed attempts", normalizedEmail, attempts.count);
        }
    }

    /**
     * Nettoie automatiquement les OTP expirés
     * FIXED: Using AtomicInteger instead of primitive int to avoid lambda variable issue
     */
    private void cleanupExpiredOtps() {
        try {
            Instant now = Instant.now();
            AtomicInteger removedCount = new AtomicInteger(0);

            // Parcourir et supprimer les OTP expirés
            otpStorage.entrySet().removeIf(entry -> {
                if (now.isAfter(entry.getValue().timestamp.plusMillis(entry.getValue().expirationTime))) {
                    removedCount.incrementAndGet();
                    return true;
                }
                return false;
            });

            if (removedCount.get() > 0) {
                logger.debug("Cleaned up {} expired OTPs", removedCount.get());
            }

        } catch (Exception e) {
            logger.error("Error during OTP cleanup: {}", e.getMessage(), e);
        }
    }

    /**
     * Nettoie automatiquement les tentatives expirées
     * FIXED: Using AtomicInteger instead of primitive int to avoid lambda variable issue
     */
    private void cleanupExpiredAttempts() {
        try {
            Instant now = Instant.now();
            AtomicInteger removedCount = new AtomicInteger(0);

            attemptStorage.entrySet().removeIf(entry -> {
                if (now.isAfter(entry.getValue().lastAttempt.plusMillis(ATTEMPT_RESET_TIME_MS))) {
                    removedCount.incrementAndGet();
                    return true;
                }
                return false;
            });

            if (removedCount.get() > 0) {
                logger.debug("Cleaned up {} expired attempt records", removedCount.get());
            }

        } catch (Exception e) {
            logger.error("Error during attempt cleanup: {}", e.getMessage(), e);
        }
    }

    /**
     * Retourne le nombre d'OTP actuellement en mémoire
     * @return Le nombre d'OTP stockés
     */
    public int getOtpCount() {
        return otpStorage.size();
    }

    /**
     * Retourne le type d'OTP pour un email donné
     */
    public OtpType getOtpType(String email) {
        if (!hasOtp(email)) return null;

        String normalizedEmail = email.trim().toLowerCase();
        OtpEntry entry = otpStorage.get(normalizedEmail);
        return entry != null ? entry.type : null;
    }

    /**
     * Vide complètement le stockage des OTP (pour les tests ou la maintenance)
     */
    public void clearAllOtps() {
        int count = otpStorage.size();
        otpStorage.clear();
        attemptStorage.clear();
        logger.info("Cleared all {} OTPs and attempt records from storage", count);
    }

    /**
     * Réinitialise les tentatives échouées pour un email
     */
    public void resetFailedAttempts(String email) {
        if (email == null || email.trim().isEmpty()) return;

        String normalizedEmail = email.trim().toLowerCase();
        attemptStorage.remove(normalizedEmail);
        logger.info("Reset failed attempts for email: {}", normalizedEmail);
    }

    /**
     * Obtient les statistiques du service OTP
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("activeOtps", otpStorage.size());
        stats.put("lockedAccounts", attemptStorage.values().stream()
                .mapToInt(attempt -> attempt.count >= MAX_ATTEMPTS ? 1 : 0)
                .sum());
        stats.put("totalAttemptRecords", attemptStorage.size());

        // Statistiques par type d'OTP
        Map<String, Long> typeStats = new ConcurrentHashMap<>();
        otpStorage.values().forEach(entry -> {
            String type = entry.type.name();
            typeStats.put(type, typeStats.getOrDefault(type, 0L) + 1);
        });
        stats.put("otpsByType", typeStats);

        return stats;
    }

    /**
     * Arrête le service de nettoyage automatique
     */
    public void shutdown() {
        try {
            scheduler.shutdown();
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            logger.info("OtpService shutdown completed");
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
            logger.error("Error during OtpService shutdown: {}", e.getMessage());
        }
    }

    /**
     * Enum pour les types d'OTP
     */
    public enum OtpType {
        STANDARD("Standard Login"),
        FARMING_OPERATION("Farming Operation"),
        BUYING_OPERATION("Buying Operation"),
        ANALYSIS_OPERATION("Analysis Operation"),
        GOVERNMENT_OPERATION("Government Operation"),
        ADMIN_OPERATION("Administrative Operation"),
        SENSITIVE_OPERATION("Sensitive Operation");

        private final String displayName;

        OtpType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Classe interne pour stocker les entrées OTP
     */
    private static class OtpEntry {
        final String otp;
        final Instant timestamp;
        final OtpType type;
        final long expirationTime;

        OtpEntry(String otp, Instant timestamp, OtpType type, long expirationTime) {
            this.otp = otp;
            this.timestamp = timestamp;
            this.type = type;
            this.expirationTime = expirationTime;
        }

        @Override
        public String toString() {
            return "OtpEntry{" +
                    "type=" + type +
                    ", timestamp=" + timestamp +
                    ", expirationTime=" + expirationTime +
                    '}';
        }
    }

    /**
     * Classe interne pour stocker les tentatives échouées
     */
    private static class AttemptEntry {
        final int count;
        final Instant lastAttempt;

        AttemptEntry(int count, Instant lastAttempt) {
            this.count = count;
            this.lastAttempt = lastAttempt;
        }

        @Override
        public String toString() {
            return "AttemptEntry{" +
                    "count=" + count +
                    ", lastAttempt=" + lastAttempt +
                    '}';
        }
    }
}