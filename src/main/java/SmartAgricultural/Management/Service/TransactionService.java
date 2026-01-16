package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.Transaction;
import SmartAgricultural.Management.Model.Transaction.TransactionStatus;
import SmartAgricultural.Management.Model.Transaction.PaymentMethod;
import SmartAgricultural.Management.Model.Transaction.TransportResponsibility;
import SmartAgricultural.Management.Repository.TransactionRepository;
import SmartAgricultural.Management.dto.UserDTO;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.exception.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TransactionRepository transactionRepository;

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    // Basic CRUD operations
    public Transaction save(Transaction transaction) {
        validateTransaction(transaction);
        return transactionRepository.save(transaction);
    }

    public Transaction create(Transaction transaction) {
        if (transaction.getId() != null) {
            throw new ValidationException("Cannot create transaction with existing ID");
        }

        if (StringUtils.hasText(transaction.getTransactionCode()) &&
                existsByTransactionCode(transaction.getTransactionCode())) {
            throw new ValidationException("Transaction code already exists: " + transaction.getTransactionCode());
        }

        Transaction savedTransaction = save(transaction);

        // ✨ NOUVEAU: Envoyer les emails de notification
        try {
            UserDTO farmer = userService.getUserById(transaction.getFarmerId());
            UserDTO buyer = userService.getUserById(transaction.getBuyerId());
            emailService.sendTransactionCreatedNotification(farmer, buyer, savedTransaction);
        } catch (Exception e) {
            logger.error("Failed to send transaction creation emails", e);
            // Ne pas bloquer la transaction si l'email échoue
        }

        return savedTransaction;
    }


    public Transaction update(String id, Transaction transaction) {
        Transaction existing = findById(id);
        updateTransactionFields(existing, transaction);
        return save(existing);
    }

    @Transactional(readOnly = true)
    public Transaction findById(String id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Transaction> findByIdOptional(String id) {
        return transactionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Transaction findByTransactionCode(String transactionCode) {
        return transactionRepository.findByTransactionCode(transactionCode)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with code: " + transactionCode));
    }

    @Transactional(readOnly = true)
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findAll(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    public void deleteById(String id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(String id) {
        return transactionRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByTransactionCode(String transactionCode) {
        return transactionRepository.existsByTransactionCode(transactionCode);
    }

    @Transactional(readOnly = true)
    public long count() {
        return transactionRepository.count();
    }

    // Farmer-specific operations
    @Transactional(readOnly = true)
    public List<Transaction> findByFarmerId(String farmerId) {
        return transactionRepository.findByFarmerId(farmerId);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findByFarmerId(String farmerId, Pageable pageable) {
        return transactionRepository.findByFarmerId(farmerId, pageable);
    }

    @Transactional(readOnly = true)
    public long countByFarmerId(String farmerId) {
        return transactionRepository.countByFarmerId(farmerId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findByFarmerIdAndStatus(String farmerId, TransactionStatus status) {
        return transactionRepository.findByFarmerIdAndStatus(farmerId, status);
    }

    // Buyer-specific operations
    @Transactional(readOnly = true)
    public List<Transaction> findByBuyerId(String buyerId) {
        return transactionRepository.findByBuyerId(buyerId);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findByBuyerId(String buyerId, Pageable pageable) {
        return transactionRepository.findByBuyerId(buyerId, pageable);
    }

    @Transactional(readOnly = true)
    public long countByBuyerId(String buyerId) {
        return transactionRepository.countByBuyerId(buyerId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findByBuyerIdAndStatus(String buyerId, TransactionStatus status) {
        return transactionRepository.findByBuyerIdAndStatus(buyerId, status);
    }

    // Crop-specific operations
    @Transactional(readOnly = true)
    public List<Transaction> findByCropId(String cropId) {
        return transactionRepository.findByCropId(cropId);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findByCropId(String cropId, Pageable pageable) {
        return transactionRepository.findByCropId(cropId, pageable);
    }

    @Transactional(readOnly = true)
    public long countByCropId(String cropId) {
        return transactionRepository.countByCropId(cropId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findByCropProductionId(String cropProductionId) {
        return transactionRepository.findByCropProductionId(cropProductionId);
    }

    // Status-based operations
    @Transactional(readOnly = true)
    public List<Transaction> findByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findByStatus(TransactionStatus status, Pageable pageable) {
        return transactionRepository.findByStatus(status, pageable);
    }

    @Transactional(readOnly = true)
    public long countByStatus(TransactionStatus status) {
        return transactionRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findActiveTransactions() {
        return transactionRepository.findActiveTransactions();
    }

    @Transactional(readOnly = true)
    public List<Transaction> findCompletedTransactions() {
        return transactionRepository.findCompletedTransactions();
    }

    @Transactional(readOnly = true)
    public List<Transaction> findCancelledTransactions() {
        return transactionRepository.findCancelledTransactions();
    }

    @Transactional(readOnly = true)
    public List<Transaction> findDisputedTransactions() {
        return transactionRepository.findDisputedTransactions();
    }

    // Transaction state management
    public Transaction confirmTransaction(String id) {
        Transaction transaction = findById(id);

        if (!transaction.canConfirm()) {
            throw new ValidationException("Transaction cannot be confirmed in current state: " + transaction.getStatus());
        }

        transaction.confirm();
        Transaction savedTransaction = save(transaction);

        // ✨ NOUVEAU: Envoyer les emails de confirmation
        try {
            UserDTO farmer = userService.getUserById(transaction.getFarmerId());
            UserDTO buyer = userService.getUserById(transaction.getBuyerId());
            emailService.sendTransactionConfirmedNotification(farmer, buyer, savedTransaction);
        } catch (Exception e) {
            logger.error("Failed to send transaction confirmed emails", e);
        }

        return savedTransaction;
    }



    public Transaction markAsDelivered(String id, LocalDateTime deliveryTime) {
        Transaction transaction = findById(id);

        if (!transaction.canDeliver()) {
            throw new ValidationException("Transaction cannot be marked as delivered in current state: " + transaction.getStatus());
        }

        transaction.markAsDelivered();
        if (deliveryTime != null) {
            transaction.setUpdatedAt(deliveryTime);
        }
        Transaction savedTransaction = save(transaction);

        // ✨ NOUVEAU: Envoyer les emails de livraison
        try {
            UserDTO farmer = userService.getUserById(transaction.getFarmerId());
            UserDTO buyer = userService.getUserById(transaction.getBuyerId());
            emailService.sendTransactionDeliveredNotification(farmer, buyer, savedTransaction);
        } catch (Exception e) {
            logger.error("Failed to send transaction delivered emails", e);
        }

        return savedTransaction;
    }



    public Transaction markAsPaid(String id, LocalDateTime paymentTime, String paymentNotes) {
        Transaction transaction = findById(id);

        if (!transaction.canMarkAsPaid()) {
            throw new ValidationException("Transaction cannot be marked as paid in current state: " + transaction.getStatus());
        }

        transaction.markAsPaid();
        if (paymentTime != null) {
            transaction.setPaymentDate(paymentTime);
        }
        if (StringUtils.hasText(paymentNotes)) {
            transaction.setNotes(transaction.getNotes() != null ?
                    transaction.getNotes() + "\nPayment: " + paymentNotes :
                    "Payment: " + paymentNotes);
        }
        Transaction savedTransaction = save(transaction);

        // ✨ NOUVEAU: Envoyer les emails de paiement
        try {
            UserDTO farmer = userService.getUserById(transaction.getFarmerId());
            UserDTO buyer = userService.getUserById(transaction.getBuyerId());
            emailService.sendTransactionPaidNotification(farmer, buyer, savedTransaction);
        } catch (Exception e) {
            logger.error("Failed to send transaction paid emails", e);
        }

        return savedTransaction;
    }


    public Transaction cancelTransaction(String id, String reason) {
        Transaction transaction = findById(id);

        if (!transaction.canCancel()) {
            throw new ValidationException("Transaction cannot be cancelled in current state: " + transaction.getStatus());
        }

        transaction.cancel();
        if (StringUtils.hasText(reason)) {
            transaction.setNotes(transaction.getNotes() != null ?
                    transaction.getNotes() + "\nCancellation: " + reason :
                    "Cancellation: " + reason);
        }
        Transaction savedTransaction = save(transaction);

        // ✨ NOUVEAU: Envoyer les emails d'annulation
        try {
            UserDTO farmer = userService.getUserById(transaction.getFarmerId());
            UserDTO buyer = userService.getUserById(transaction.getBuyerId());
            emailService.sendTransactionCancelledNotification(farmer, buyer, savedTransaction, reason);
        } catch (Exception e) {
            logger.error("Failed to send transaction cancelled emails", e);
        }

        return savedTransaction;
    }



    public Transaction markAsDisputed(String id, String disputeReason) {
        Transaction transaction = findById(id);

        if (!transaction.canDispute()) {
            throw new ValidationException("Transaction cannot be disputed in current state: " + transaction.getStatus());
        }

        transaction.markAsDisputed();
        if (StringUtils.hasText(disputeReason)) {
            transaction.setNotes(transaction.getNotes() != null ?
                    transaction.getNotes() + "\nDispute: " + disputeReason :
                    "Dispute: " + disputeReason);
        }
        Transaction savedTransaction = save(transaction);

        // ✨ NOUVEAU: Envoyer les emails de litige
        try {
            UserDTO farmer = userService.getUserById(transaction.getFarmerId());
            UserDTO buyer = userService.getUserById(transaction.getBuyerId());
            emailService.sendTransactionDisputedNotification(farmer, buyer, savedTransaction, disputeReason);
        } catch (Exception e) {
            logger.error("Failed to send transaction disputed emails", e);
        }

        return savedTransaction;
    }

    // Payment operations
    @Transactional(readOnly = true)
    public List<Transaction> findByPaymentMethod(PaymentMethod paymentMethod) {
        return transactionRepository.findByPaymentMethod(paymentMethod);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findUnpaidTransactions() {
        return transactionRepository.findUnpaidTransactions();
    }

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsWithAdvancePayment() {
        return transactionRepository.findTransactionsWithAdvancePayment();
    }

    public Transaction updateAdvancePayment(String id, BigDecimal advanceAmount) {
        Transaction transaction = findById(id);

        if (advanceAmount != null && advanceAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Advance payment cannot be negative");
        }

        if (advanceAmount != null && transaction.getTotalAmount() != null &&
                advanceAmount.compareTo(transaction.getTotalAmount()) > 0) {
            throw new ValidationException("Advance payment cannot exceed total amount");
        }

        transaction.setAdvancePayment(advanceAmount);
        return save(transaction);
    }

    // Date-based operations
    @Transactional(readOnly = true)
    public List<Transaction> findOverdueTransactions() {
        return transactionRepository.findOverdueTransactions(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Transaction> findUpcomingDeliveries(int daysAhead) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(daysAhead);
        return transactionRepository.findUpcomingDeliveries(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsForDeliveryDate(LocalDate date) {
        return transactionRepository.findTransactionsForDeliveryDate(date);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByTransactionDateBetween(startDate, endDate);
    }

    // Amount-based operations
    @Transactional(readOnly = true)
    public List<Transaction> findHighValueTransactions(BigDecimal threshold) {
        return transactionRepository.findHighValueTransactions(threshold);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findByTotalAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return transactionRepository.findByTotalAmountBetween(minAmount, maxAmount);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return transactionRepository.findByPricePerUnitBetween(minPrice, maxPrice);
    }

    // Location operations
    @Transactional(readOnly = true)
    public List<Transaction> findByDeliveryLocation(String location) {
        return transactionRepository.findByDeliveryLocation(location);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findByDeliveryLocationContaining(String location) {
        return transactionRepository.findByDeliveryLocationContaining(location);
    }

    // Quality operations
    @Transactional(readOnly = true)
    public List<Transaction> findByQualityGrade(String qualityGrade) {
        return transactionRepository.findByQualityGrade(qualityGrade);
    }

    public Transaction updateQualitySpecifications(String id, String qualityGrade, String specifications, String packaging) {
        Transaction transaction = findById(id);

        if (StringUtils.hasText(qualityGrade)) {
            transaction.setQualityGrade(qualityGrade);
        }

        if (StringUtils.hasText(specifications)) {
            transaction.setQualitySpecifications(specifications);
        }

        if (StringUtils.hasText(packaging)) {
            transaction.setPackagingRequirements(packaging);
        }

        return save(transaction);
    }

    // Rating operations
    public Transaction addFarmerRating(String id, int rating, String feedback) {
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Farmer rating must be between 1 and 5");
        }

        Transaction transaction = findById(id);

        if (!transaction.isCompleted()) {
            throw new ValidationException("Transaction must be completed before rating");
        }

        transaction.setRatingFarmer(rating);

        if (StringUtils.hasText(feedback)) {
            transaction.setNotes(transaction.getNotes() != null ?
                    transaction.getNotes() + "\nFarmer Rating (" + rating + "/5): " + feedback :
                    "Farmer Rating (" + rating + "/5): " + feedback);
        }

        return save(transaction);
    }

    public Transaction addBuyerRating(String id, int rating, String feedback) {
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Buyer rating must be between 1 and 5");
        }

        Transaction transaction = findById(id);

        if (!transaction.isCompleted()) {
            throw new ValidationException("Transaction must be completed before rating");
        }

        transaction.setRatingBuyer(rating);

        if (StringUtils.hasText(feedback)) {
            transaction.setNotes(transaction.getNotes() != null ?
                    transaction.getNotes() + "\nBuyer Rating (" + rating + "/5): " + feedback :
                    "Buyer Rating (" + rating + "/5): " + feedback);
        }

        return save(transaction);
    }

    // Search and filtering operations
    @Transactional(readOnly = true)
    public List<Transaction> searchTransactions(String searchTerm) {
        if (!StringUtils.hasText(searchTerm)) {
            return findAll();
        }
        return transactionRepository.searchByTerm(searchTerm.trim());
    }

    @Transactional(readOnly = true)
    public List<Transaction> searchTransactionsByFarmer(String farmerId, String searchTerm) {
        if (!StringUtils.hasText(searchTerm)) {
            return findByFarmerId(farmerId);
        }
        return transactionRepository.searchByTermAndFarmerId(searchTerm.trim(), farmerId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> searchTransactionsByBuyer(String buyerId, String searchTerm) {
        if (!StringUtils.hasText(searchTerm)) {
            return findByBuyerId(buyerId);
        }
        return transactionRepository.searchByTermAndBuyerId(searchTerm.trim(), buyerId);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findWithFilters(
            TransactionStatus status,
            PaymentMethod paymentMethod,
            String farmerId,
            String buyerId,
            String cropId,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String deliveryLocation,
            Pageable pageable) {

        return transactionRepository.findWithFilters(
                status, paymentMethod, farmerId, buyerId, cropId,
                minAmount, maxAmount, deliveryLocation, pageable);
    }

    // Analytics and statistics
    @Transactional(readOnly = true)
    public Map<String, Long> getStatusStatistics() {
        List<Object[]> results = transactionRepository.countByStatus();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> ((TransactionStatus) result[0]).getDisplayName(),
                        result -> (Long) result[1]
                ));
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getPaymentMethodStatistics() {
        List<Object[]> results = transactionRepository.countByPaymentMethod();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> ((PaymentMethod) result[0]).getDisplayName(),
                        result -> (Long) result[1]
                ));
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getCropStatistics() {
        List<Object[]> results = transactionRepository.countByCrop();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]
                ));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getTransactionSummary() {
        Map<String, Object> summary = new HashMap<>();

        summary.put("totalTransactions", count());
        summary.put("activeTransactions", findActiveTransactions().size());
        summary.put("completedTransactions", findCompletedTransactions().size());
        summary.put("cancelledTransactions", findCancelledTransactions().size());
        summary.put("disputedTransactions", findDisputedTransactions().size());
        summary.put("overdueTransactions", findOverdueTransactions().size());

        BigDecimal averageAmount = transactionRepository.getAverageTotalAmount();
        summary.put("averageTotalAmount", averageAmount != null ? averageAmount : BigDecimal.ZERO);

        BigDecimal averagePrice = transactionRepository.getAveragePricePerUnit();
        summary.put("averagePricePerUnit", averagePrice != null ? averagePrice : BigDecimal.ZERO);

        summary.put("statusDistribution", getStatusStatistics());
        summary.put("paymentMethodDistribution", getPaymentMethodStatistics());

        return summary;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getFarmerAnalytics(String farmerId) {
        Map<String, Object> analytics = new HashMap<>();

        List<Transaction> farmerTransactions = findByFarmerId(farmerId);
        analytics.put("totalTransactions", farmerTransactions.size());

        long completedCount = transactionRepository.getCompletedTransactionCountByFarmer(farmerId);
        analytics.put("completedTransactions", completedCount);

        long cancelledCount = transactionRepository.getCancelledTransactionCountByFarmer(farmerId);
        analytics.put("cancelledTransactions", cancelledCount);

        long disputedCount = transactionRepository.getDisputedTransactionCountByFarmer(farmerId);
        analytics.put("disputedTransactions", disputedCount);

        BigDecimal totalEarnings = transactionRepository.getTotalNetAmountByFarmer(farmerId);
        analytics.put("totalEarnings", totalEarnings != null ? totalEarnings : BigDecimal.ZERO);

        Double averageRating = transactionRepository.getAverageFarmerRating(farmerId);
        analytics.put("averageRating", averageRating != null ? averageRating : 0.0);

        // Calculate success rate
        if (farmerTransactions.size() > 0) {
            double successRate = (double) completedCount / farmerTransactions.size() * 100;
            analytics.put("successRate", BigDecimal.valueOf(successRate).setScale(2, RoundingMode.HALF_UP));
        } else {
            analytics.put("successRate", BigDecimal.ZERO);
        }

        return analytics;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getBuyerAnalytics(String buyerId) {
        Map<String, Object> analytics = new HashMap<>();

        List<Transaction> buyerTransactions = findByBuyerId(buyerId);
        analytics.put("totalTransactions", buyerTransactions.size());

        long completedCount = transactionRepository.getCompletedTransactionCountByBuyer(buyerId);
        analytics.put("completedTransactions", completedCount);

        long cancelledCount = transactionRepository.getCancelledTransactionCountByBuyer(buyerId);
        analytics.put("cancelledTransactions", cancelledCount);

        long disputedCount = transactionRepository.getDisputedTransactionCountByBuyer(buyerId);
        analytics.put("disputedTransactions", disputedCount);

        BigDecimal totalSpent = transactionRepository.getTotalAmountByBuyer(buyerId);
        analytics.put("totalSpent", totalSpent != null ? totalSpent : BigDecimal.ZERO);

        Double averageRating = transactionRepository.getAverageBuyerRating(buyerId);
        analytics.put("averageRating", averageRating != null ? averageRating : 0.0);

        // Calculate success rate
        if (buyerTransactions.size() > 0) {
            double successRate = (double) completedCount / buyerTransactions.size() * 100;
            analytics.put("successRate", BigDecimal.valueOf(successRate).setScale(2, RoundingMode.HALF_UP));
        } else {
            analytics.put("successRate", BigDecimal.ZERO);
        }

        return analytics;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCropAnalytics(String cropId) {
        Map<String, Object> analytics = new HashMap<>();

        List<Transaction> cropTransactions = findByCropId(cropId);
        analytics.put("totalTransactions", cropTransactions.size());

        BigDecimal totalQuantity = transactionRepository.getTotalQuantityByCrop(cropId);
        analytics.put("totalQuantityTraded", totalQuantity != null ? totalQuantity : BigDecimal.ZERO);

        List<Object[]> avgPrices = transactionRepository.findAveragePricesByCrop();
        Optional<Object[]> cropPrice = avgPrices.stream()
                .filter(result -> cropId.equals(result[0]))
                .findFirst();

        if (cropPrice.isPresent()) {
            analytics.put("averagePrice", (BigDecimal) cropPrice.get()[1]);
        } else {
            analytics.put("averagePrice", BigDecimal.ZERO);
        }

        // Calculate market performance metrics
        long completedTransactions = cropTransactions.stream()
                .mapToLong(t -> t.isCompleted() ? 1 : 0)
                .sum();

        analytics.put("completedTransactions", completedTransactions);

        if (cropTransactions.size() > 0) {
            double completionRate = (double) completedTransactions / cropTransactions.size() * 100;
            analytics.put("completionRate", BigDecimal.valueOf(completionRate).setScale(2, RoundingMode.HALF_UP));
        } else {
            analytics.put("completionRate", BigDecimal.ZERO);
        }

        return analytics;
    }

    // Time-based analytics
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMonthlyTransactionStats() {
        List<Object[]> counts = transactionRepository.getTransactionCountByMonth();
        List<Object[]> values = transactionRepository.getTransactionValueByMonth();

        Map<String, Object[]> valueMap = values.stream()
                .collect(Collectors.toMap(
                        result -> result[0] + "-" + result[1],
                        result -> result
                ));

        return counts.stream()
                .map(count -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("year", count[0]);
                    stat.put("month", count[1]);
                    stat.put("transactionCount", count[2]);

                    String key = count[0] + "-" + count[1];
                    Object[] valueData = valueMap.get(key);
                    stat.put("totalValue", valueData != null ? valueData[2] : BigDecimal.ZERO);

                    return stat;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDailyTransactionStats(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = transactionRepository.getDailyTransactionCount(startDate, endDate);

        return results.stream()
                .map(result -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("date", result[0]);
                    stat.put("transactionCount", result[1]);
                    return stat;
                })
                .collect(Collectors.toList());
    }

    // Performance operations
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopPerformingFarmers(int limit) {
        List<Object[]> results = transactionRepository.findTopPerformingFarmers();

        return results.stream()
                .limit(limit)
                .map(result -> {
                    Map<String, Object> farmer = new HashMap<>();
                    farmer.put("farmerId", result[0]);
                    farmer.put("transactionCount", result[1]);
                    return farmer;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopPerformingBuyers(int limit) {
        List<Object[]> results = transactionRepository.findTopPerformingBuyers();

        return results.stream()
                .limit(limit)
                .map(result -> {
                    Map<String, Object> buyer = new HashMap<>();
                    buyer.put("buyerId", result[0]);
                    buyer.put("transactionCount", result[1]);
                    return buyer;
                })
                .collect(Collectors.toList());
    }

    // Bulk operations
    public List<Transaction> createTransactions(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            validateTransaction(transaction);

            if (transaction.getId() != null) {
                throw new ValidationException("Cannot create transaction with existing ID");
            }
        }
        return transactionRepository.saveAll(transactions);
    }

    public List<Transaction> updateTransactions(List<Transaction> transactions) {
        List<Transaction> updatedTransactions = transactions.stream()
                .map(transaction -> {
                    if (transaction.getId() == null) {
                        throw new ValidationException("Cannot update transaction without ID");
                    }
                    validateTransaction(transaction);
                    return transaction;
                })
                .collect(Collectors.toList());

        return transactionRepository.saveAll(updatedTransactions);
    }

    public void deleteTransactions(List<String> ids) {
        List<Transaction> transactions = ids.stream()
                .map(this::findById)
                .collect(Collectors.toList());

        transactionRepository.deleteAll(transactions);
    }

    // Maintenance operations
    public int cleanupOldCancelledTransactions(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        return transactionRepository.deleteOldCancelledTransactions(cutoffDate);
    }

    public int cancelOldPendingTransactions(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        return transactionRepository.cancelOldPendingTransactions(cutoffDate);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findRecentTransactions(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return transactionRepository.findRecentTransactions(since);
    }

    // Validation methods
    private void validateTransaction(Transaction transaction) {
        if (!StringUtils.hasText(transaction.getFarmerId())) {
            throw new ValidationException("Farmer ID is required");
        }

        if (!StringUtils.hasText(transaction.getBuyerId())) {
            throw new ValidationException("Buyer ID is required");
        }

        if (!StringUtils.hasText(transaction.getCropId())) {
            throw new ValidationException("Crop ID is required");
        }

        if (transaction.getPaymentMethod() == null) {
            throw new ValidationException("Payment method is required");
        }

        if (transaction.getQuantity() == null || transaction.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Quantity must be positive");
        }

        if (transaction.getPricePerUnit() == null || transaction.getPricePerUnit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Price per unit must be positive");
        }

        if (transaction.getTotalAmount() == null || transaction.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Total amount must be positive");
        }

        if (transaction.getAdvancePayment() != null &&
                transaction.getAdvancePayment().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Advance payment cannot be negative");
        }

        if (transaction.getAdvancePayment() != null && transaction.getTotalAmount() != null &&
                transaction.getAdvancePayment().compareTo(transaction.getTotalAmount()) > 0) {
            throw new ValidationException("Advance payment cannot exceed total amount");
        }

        if (transaction.getTransportCost() != null &&
                transaction.getTransportCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Transport cost cannot be negative");
        }

        if (transaction.getInsuranceCost() != null &&
                transaction.getInsuranceCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Insurance cost cannot be negative");
        }

        if (transaction.getBrokerCommission() != null &&
                transaction.getBrokerCommission().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Broker commission cannot be negative");
        }

        if (transaction.getGovernmentTax() != null &&
                transaction.getGovernmentTax().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Government tax cannot be negative");
        }

        if (transaction.getRatingFarmer() != null &&
                (transaction.getRatingFarmer() < 1 || transaction.getRatingFarmer() > 5)) {
            throw new ValidationException("Farmer rating must be between 1 and 5");
        }

        if (transaction.getRatingBuyer() != null &&
                (transaction.getRatingBuyer() < 1 || transaction.getRatingBuyer() > 5)) {
            throw new ValidationException("Buyer rating must be between 1 and 5");
        }

        if (transaction.getDeliveryDate() != null &&
                transaction.getTransactionDate() != null &&
                transaction.getDeliveryDate().isBefore(transaction.getTransactionDate().toLocalDate())) {
            throw new ValidationException("Delivery date cannot be before transaction date");
        }
    }

    private void updateTransactionFields(Transaction existing, Transaction updated) {
        // Update mutable fields only (non-core business fields)
        if (updated.getDeliveryDate() != null) {
            existing.setDeliveryDate(updated.getDeliveryDate());
        }

        if (StringUtils.hasText(updated.getDeliveryLocation())) {
            existing.setDeliveryLocation(updated.getDeliveryLocation());
        }

        if (StringUtils.hasText(updated.getQualityGrade())) {
            existing.setQualityGrade(updated.getQualityGrade());
        }

        if (StringUtils.hasText(updated.getQualitySpecifications())) {
            existing.setQualitySpecifications(updated.getQualitySpecifications());
        }

        if (StringUtils.hasText(updated.getPackagingRequirements())) {
            existing.setPackagingRequirements(updated.getPackagingRequirements());
        }

        if (updated.getTransportResponsibility() != null) {
            existing.setTransportResponsibility(updated.getTransportResponsibility());
        }

        if (updated.getTransportCost() != null) {
            existing.setTransportCost(updated.getTransportCost());
        }

        if (updated.getInsuranceCost() != null) {
            existing.setInsuranceCost(updated.getInsuranceCost());
        }

        if (StringUtils.hasText(updated.getContractTerms())) {
            existing.setContractTerms(updated.getContractTerms());
        }

        if (StringUtils.hasText(updated.getPenaltyClause())) {
            existing.setPenaltyClause(updated.getPenaltyClause());
        }

        if (StringUtils.hasText(updated.getDisputeResolution())) {
            existing.setDisputeResolution(updated.getDisputeResolution());
        }

        if (updated.getBrokerInvolved() != null) {
            existing.setBrokerInvolved(updated.getBrokerInvolved());
        }

        if (updated.getBrokerCommission() != null) {
            existing.setBrokerCommission(updated.getBrokerCommission());
        }

        if (updated.getGovernmentTax() != null) {
            existing.setGovernmentTax(updated.getGovernmentTax());
        }

        if (updated.getAdvancePayment() != null) {
            existing.setAdvancePayment(updated.getAdvancePayment());
        }

        if (StringUtils.hasText(updated.getPaymentTerms())) {
            existing.setPaymentTerms(updated.getPaymentTerms());
        }

        if (StringUtils.hasText(updated.getNotes())) {
            existing.setNotes(updated.getNotes());
        }
    }
}