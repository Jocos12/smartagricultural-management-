package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.Transaction;
import SmartAgricultural.Management.Model.Transaction.TransactionStatus;
import SmartAgricultural.Management.Model.Transaction.PaymentMethod;
import SmartAgricultural.Management.Model.Transaction.TransportResponsibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    // Basic finders
    Optional<Transaction> findByTransactionCode(String transactionCode);

    List<Transaction> findByFarmerId(String farmerId);

    Page<Transaction> findByFarmerId(String farmerId, Pageable pageable);

    List<Transaction> findByBuyerId(String buyerId);

    Page<Transaction> findByBuyerId(String buyerId, Pageable pageable);

    List<Transaction> findByCropId(String cropId);

    Page<Transaction> findByCropId(String cropId, Pageable pageable);

    List<Transaction> findByCropProductionId(String cropProductionId);

    Page<Transaction> findByCropProductionId(String cropProductionId, Pageable pageable);

    // Status-based queries
    List<Transaction> findByStatus(TransactionStatus status);

    Page<Transaction> findByStatus(TransactionStatus status, Pageable pageable);

    List<Transaction> findByFarmerIdAndStatus(String farmerId, TransactionStatus status);

    List<Transaction> findByBuyerIdAndStatus(String buyerId, TransactionStatus status);

    @Query("SELECT t FROM Transaction t WHERE t.status IN ('PENDING', 'CONFIRMED')")
    List<Transaction> findActiveTransactions();

    @Query("SELECT t FROM Transaction t WHERE t.status = 'PAID'")
    List<Transaction> findCompletedTransactions();

    @Query("SELECT t FROM Transaction t WHERE t.status = 'CANCELLED'")
    List<Transaction> findCancelledTransactions();

    @Query("SELECT t FROM Transaction t WHERE t.status = 'DISPUTED'")
    List<Transaction> findDisputedTransactions();

    // Date-based queries
    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByDeliveryDateBetween(LocalDate startDate, LocalDate endDate);

    List<Transaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT t FROM Transaction t WHERE t.deliveryDate = :date")
    List<Transaction> findTransactionsForDeliveryDate(@Param("date") LocalDate date);

    @Query("SELECT t FROM Transaction t WHERE t.deliveryDate < :date AND t.status NOT IN ('DELIVERED', 'PAID', 'CANCELLED')")
    List<Transaction> findOverdueTransactions(@Param("date") LocalDate date);

    @Query("SELECT t FROM Transaction t WHERE t.deliveryDate BETWEEN :startDate AND :endDate AND t.status NOT IN ('DELIVERED', 'PAID', 'CANCELLED')")
    List<Transaction> findUpcomingDeliveries(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Payment-based queries
    List<Transaction> findByPaymentMethod(PaymentMethod paymentMethod);

    @Query("SELECT t FROM Transaction t WHERE t.advancePayment > 0")
    List<Transaction> findTransactionsWithAdvancePayment();

    @Query("SELECT t FROM Transaction t WHERE t.paymentDate IS NULL AND t.status != 'CANCELLED'")
    List<Transaction> findUnpaidTransactions();

    @Query("SELECT t FROM Transaction t WHERE t.paymentDate BETWEEN :startDate AND :endDate")
    List<Transaction> findTransactionsPaidBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Amount-based queries
    @Query("SELECT t FROM Transaction t WHERE t.totalAmount BETWEEN :minAmount AND :maxAmount")
    List<Transaction> findByTotalAmountBetween(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);

    @Query("SELECT t FROM Transaction t WHERE t.totalAmount > :threshold")
    List<Transaction> findHighValueTransactions(@Param("threshold") BigDecimal threshold);

    @Query("SELECT t FROM Transaction t WHERE t.pricePerUnit BETWEEN :minPrice AND :maxPrice")
    List<Transaction> findByPricePerUnitBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT t FROM Transaction t WHERE t.quantity BETWEEN :minQuantity AND :maxQuantity")
    List<Transaction> findByQuantityBetween(@Param("minQuantity") BigDecimal minQuantity, @Param("maxQuantity") BigDecimal maxQuantity);

    // Location and logistics queries
    List<Transaction> findByDeliveryLocation(String deliveryLocation);

    @Query("SELECT t FROM Transaction t WHERE LOWER(t.deliveryLocation) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Transaction> findByDeliveryLocationContaining(@Param("location") String location);

    List<Transaction> findByTransportResponsibility(TransportResponsibility transportResponsibility);

    @Query("SELECT t FROM Transaction t WHERE t.transportCost > 0")
    List<Transaction> findTransactionsWithTransportCost();

    @Query("SELECT t FROM Transaction t WHERE t.insuranceCost > 0")
    List<Transaction> findTransactionsWithInsurance();

    // Quality and specifications queries
    List<Transaction> findByQualityGrade(String qualityGrade);

    @Query("SELECT t FROM Transaction t WHERE t.qualitySpecifications IS NOT NULL AND t.qualitySpecifications != ''")
    List<Transaction> findTransactionsWithQualitySpecifications();

    @Query("SELECT t FROM Transaction t WHERE t.packagingRequirements IS NOT NULL AND t.packagingRequirements != ''")
    List<Transaction> findTransactionsWithPackagingRequirements();

    // Broker and commission queries
    @Query("SELECT t FROM Transaction t WHERE t.brokerInvolved = true")
    List<Transaction> findTransactionsWithBroker();

    @Query("SELECT t FROM Transaction t WHERE t.brokerCommission > 0")
    List<Transaction> findTransactionsWithBrokerCommission();

    @Query("SELECT t FROM Transaction t WHERE t.governmentTax > 0")
    List<Transaction> findTransactionsWithTax();

    // Rating queries
    @Query("SELECT t FROM Transaction t WHERE t.ratingFarmer IS NOT NULL")
    List<Transaction> findTransactionsWithFarmerRating();

    @Query("SELECT t FROM Transaction t WHERE t.ratingBuyer IS NOT NULL")
    List<Transaction> findTransactionsWithBuyerRating();

    @Query("SELECT t FROM Transaction t WHERE t.ratingFarmer >= :minRating")
    List<Transaction> findTransactionsWithHighFarmerRating(@Param("minRating") Integer minRating);

    @Query("SELECT t FROM Transaction t WHERE t.ratingBuyer >= :minRating")
    List<Transaction> findTransactionsWithHighBuyerRating(@Param("minRating") Integer minRating);

    // Search queries
    @Query("SELECT t FROM Transaction t WHERE " +
            "LOWER(t.transactionCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.deliveryLocation) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.qualityGrade) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Transaction> searchByTerm(@Param("searchTerm") String searchTerm);

    @Query("SELECT t FROM Transaction t WHERE t.farmerId = :farmerId AND (" +
            "LOWER(t.transactionCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.deliveryLocation) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.qualityGrade) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Transaction> searchByTermAndFarmerId(@Param("searchTerm") String searchTerm, @Param("farmerId") String farmerId);

    @Query("SELECT t FROM Transaction t WHERE t.buyerId = :buyerId AND (" +
            "LOWER(t.transactionCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.deliveryLocation) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.qualityGrade) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Transaction> searchByTermAndBuyerId(@Param("searchTerm") String searchTerm, @Param("buyerId") String buyerId);

    // Complex filtering
    @Query("SELECT t FROM Transaction t WHERE " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:paymentMethod IS NULL OR t.paymentMethod = :paymentMethod) AND " +
            "(:farmerId IS NULL OR t.farmerId = :farmerId) AND " +
            "(:buyerId IS NULL OR t.buyerId = :buyerId) AND " +
            "(:cropId IS NULL OR t.cropId = :cropId) AND " +
            "(:minAmount IS NULL OR t.totalAmount >= :minAmount) AND " +
            "(:maxAmount IS NULL OR t.totalAmount <= :maxAmount) AND " +
            "(:deliveryLocation IS NULL OR LOWER(t.deliveryLocation) LIKE LOWER(CONCAT('%', :deliveryLocation, '%')))")
    Page<Transaction> findWithFilters(
            @Param("status") TransactionStatus status,
            @Param("paymentMethod") PaymentMethod paymentMethod,
            @Param("farmerId") String farmerId,
            @Param("buyerId") String buyerId,
            @Param("cropId") String cropId,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("deliveryLocation") String deliveryLocation,
            Pageable pageable);

    // Statistics queries
    @Query("SELECT t.status, COUNT(t) FROM Transaction t GROUP BY t.status ORDER BY COUNT(t) DESC")
    List<Object[]> countByStatus();

    @Query("SELECT t.paymentMethod, COUNT(t) FROM Transaction t GROUP BY t.paymentMethod ORDER BY COUNT(t) DESC")
    List<Object[]> countByPaymentMethod();

    @Query("SELECT t.transportResponsibility, COUNT(t) FROM Transaction t GROUP BY t.transportResponsibility ORDER BY COUNT(t) DESC")
    List<Object[]> countByTransportResponsibility();

    @Query("SELECT t.qualityGrade, COUNT(t) FROM Transaction t WHERE t.qualityGrade IS NOT NULL GROUP BY t.qualityGrade ORDER BY COUNT(t) DESC")
    List<Object[]> countByQualityGrade();

    @Query("SELECT t.deliveryLocation, COUNT(t) FROM Transaction t WHERE t.deliveryLocation IS NOT NULL GROUP BY t.deliveryLocation ORDER BY COUNT(t) DESC")
    List<Object[]> countByDeliveryLocation();

    @Query("SELECT t.cropId, COUNT(t) FROM Transaction t GROUP BY t.cropId ORDER BY COUNT(t) DESC")
    List<Object[]> countByCrop();

    // Aggregation queries
    @Query("SELECT AVG(t.totalAmount) FROM Transaction t WHERE t.totalAmount IS NOT NULL")
    BigDecimal getAverageTotalAmount();

    @Query("SELECT AVG(t.pricePerUnit) FROM Transaction t WHERE t.pricePerUnit IS NOT NULL")
    BigDecimal getAveragePricePerUnit();

    @Query("SELECT AVG(t.quantity) FROM Transaction t WHERE t.quantity IS NOT NULL")
    BigDecimal getAverageQuantity();

    @Query("SELECT SUM(t.totalAmount) FROM Transaction t WHERE t.farmerId = :farmerId AND t.status = 'PAID'")
    BigDecimal getTotalAmountByFarmer(@Param("farmerId") String farmerId);

    @Query("SELECT SUM(t.totalAmount) FROM Transaction t WHERE t.buyerId = :buyerId AND t.status = 'PAID'")
    BigDecimal getTotalAmountByBuyer(@Param("buyerId") String buyerId);

    @Query("SELECT SUM(t.quantity) FROM Transaction t WHERE t.cropId = :cropId AND t.status IN ('DELIVERED', 'PAID')")
    BigDecimal getTotalQuantityByCrop(@Param("cropId") String cropId);

    @Query("SELECT SUM(t.netAmountFarmer) FROM Transaction t WHERE t.farmerId = :farmerId AND t.status = 'PAID'")
    BigDecimal getTotalNetAmountByFarmer(@Param("farmerId") String farmerId);

    @Query("SELECT SUM(t.brokerCommission) FROM Transaction t WHERE t.brokerCommission IS NOT NULL AND t.status = 'PAID'")
    BigDecimal getTotalBrokerCommissions();

    @Query("SELECT SUM(t.governmentTax) FROM Transaction t WHERE t.governmentTax IS NOT NULL AND t.status = 'PAID'")
    BigDecimal getTotalGovernmentTax();

    // Farmer-specific aggregations
    @Query("SELECT AVG(t.ratingFarmer) FROM Transaction t WHERE t.farmerId = :farmerId AND t.ratingFarmer IS NOT NULL")
    Double getAverageFarmerRating(@Param("farmerId") String farmerId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.farmerId = :farmerId AND t.status = 'PAID'")
    Long getCompletedTransactionCountByFarmer(@Param("farmerId") String farmerId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.farmerId = :farmerId AND t.status = 'CANCELLED'")
    Long getCancelledTransactionCountByFarmer(@Param("farmerId") String farmerId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.farmerId = :farmerId AND t.status = 'DISPUTED'")
    Long getDisputedTransactionCountByFarmer(@Param("farmerId") String farmerId);

    // Buyer-specific aggregations
    @Query("SELECT AVG(t.ratingBuyer) FROM Transaction t WHERE t.buyerId = :buyerId AND t.ratingBuyer IS NOT NULL")
    Double getAverageBuyerRating(@Param("buyerId") String buyerId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.buyerId = :buyerId AND t.status = 'PAID'")
    Long getCompletedTransactionCountByBuyer(@Param("buyerId") String buyerId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.buyerId = :buyerId AND t.status = 'CANCELLED'")
    Long getCancelledTransactionCountByBuyer(@Param("buyerId") String buyerId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.buyerId = :buyerId AND t.status = 'DISPUTED'")
    Long getDisputedTransactionCountByBuyer(@Param("buyerId") String buyerId);

    // Performance queries
    @Query("SELECT t.farmerId, COUNT(t) as transactionCount FROM Transaction t WHERE t.status = 'PAID' GROUP BY t.farmerId ORDER BY transactionCount DESC")
    List<Object[]> findTopPerformingFarmers();

    @Query("SELECT t.buyerId, COUNT(t) as transactionCount FROM Transaction t WHERE t.status = 'PAID' GROUP BY t.buyerId ORDER BY transactionCount DESC")
    List<Object[]> findTopPerformingBuyers();

    @Query("SELECT t.cropId, AVG(t.pricePerUnit) as avgPrice FROM Transaction t WHERE t.status = 'PAID' GROUP BY t.cropId ORDER BY avgPrice DESC")
    List<Object[]> findAveragePricesByCrop();

    @Query("SELECT t.deliveryLocation, COUNT(t) as transactionCount FROM Transaction t WHERE t.status IN ('DELIVERED', 'PAID') GROUP BY t.deliveryLocation ORDER BY transactionCount DESC")
    List<Object[]> findPopularDeliveryLocations();

    // Time-based analytics
    @Query("SELECT YEAR(t.transactionDate), MONTH(t.transactionDate), COUNT(t) " +
            "FROM Transaction t WHERE t.transactionDate IS NOT NULL " +
            "GROUP BY YEAR(t.transactionDate), MONTH(t.transactionDate) " +
            "ORDER BY YEAR(t.transactionDate) DESC, MONTH(t.transactionDate) DESC")
    List<Object[]> getTransactionCountByMonth();

    @Query("SELECT YEAR(t.transactionDate), MONTH(t.transactionDate), SUM(t.totalAmount) " +
            "FROM Transaction t WHERE t.transactionDate IS NOT NULL AND t.totalAmount IS NOT NULL " +
            "GROUP BY YEAR(t.transactionDate), MONTH(t.transactionDate) " +
            "ORDER BY YEAR(t.transactionDate) DESC, MONTH(t.transactionDate) DESC")
    List<Object[]> getTransactionValueByMonth();

    @Query("SELECT DATE(t.transactionDate), COUNT(t) " +
            "FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(t.transactionDate) " +
            "ORDER BY DATE(t.transactionDate)")
    List<Object[]> getDailyTransactionCount(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Count methods
    long countByStatus(TransactionStatus status);

    long countByFarmerId(String farmerId);

    long countByBuyerId(String buyerId);

    long countByCropId(String cropId);

    long countByPaymentMethod(PaymentMethod paymentMethod);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.deliveryDate < :date AND t.status NOT IN ('DELIVERED', 'PAID', 'CANCELLED')")
    long countOverdueTransactions(@Param("date") LocalDate date);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.brokerInvolved = true")
    long countTransactionsWithBroker();

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.advancePayment > 0")
    long countTransactionsWithAdvancePayment();

    // Existence checks
    boolean existsByTransactionCode(String transactionCode);

    boolean existsByFarmerIdAndBuyerIdAndCropIdAndStatus(String farmerId, String buyerId, String cropId, TransactionStatus status);

    // Recent data queries
    @Query("SELECT t FROM Transaction t WHERE t.createdAt >= :since ORDER BY t.createdAt DESC")
    List<Transaction> findRecentTransactions(@Param("since") LocalDateTime since);

    @Query("SELECT t FROM Transaction t WHERE t.farmerId = :farmerId AND t.createdAt >= :since ORDER BY t.createdAt DESC")
    List<Transaction> findRecentTransactionsByFarmer(@Param("farmerId") String farmerId, @Param("since") LocalDateTime since);

    @Query("SELECT t FROM Transaction t WHERE t.buyerId = :buyerId AND t.createdAt >= :since ORDER BY t.createdAt DESC")
    List<Transaction> findRecentTransactionsByBuyer(@Param("buyerId") String buyerId, @Param("since") LocalDateTime since);

    // Maintenance queries
    @Modifying
    @Query("DELETE FROM Transaction t WHERE t.status = 'CANCELLED' AND t.createdAt < :cutoffDate")
    int deleteOldCancelledTransactions(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Modifying
    @Query("UPDATE Transaction t SET t.status = 'CANCELLED' WHERE t.status = 'PENDING' AND t.createdAt < :cutoffDate")
    int cancelOldPendingTransactions(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Advanced queries for business intelligence
    @Query("SELECT t.farmerId, t.cropId, COUNT(t) as transactionCount, AVG(t.pricePerUnit) as avgPrice " +
            "FROM Transaction t WHERE t.status = 'PAID' " +
            "GROUP BY t.farmerId, t.cropId " +
            "ORDER BY transactionCount DESC")
    List<Object[]> getFarmerCropPerformance();

    @Query("SELECT t.buyerId, t.cropId, COUNT(t) as transactionCount, SUM(t.quantity) as totalQuantity " +
            "FROM Transaction t WHERE t.status = 'PAID' " +
            "GROUP BY t.buyerId, t.cropId " +
            "ORDER BY transactionCount DESC")
    List<Object[]> getBuyerCropDemand();

    @Query("SELECT AVG(TIMESTAMPDIFF(DAY, t.transactionDate, t.paymentDate)) " +
            "FROM Transaction t WHERE t.paymentDate IS NOT NULL AND t.transactionDate IS NOT NULL")
    Double getAveragePaymentDelayDays();

    @Query("SELECT AVG(TIMESTAMPDIFF(DAY, t.transactionDate, t.completionDate)) " +
            "FROM Transaction t WHERE t.completionDate IS NOT NULL AND t.transactionDate IS NOT NULL")
    Double getAverageCompletionTimeDays();
}