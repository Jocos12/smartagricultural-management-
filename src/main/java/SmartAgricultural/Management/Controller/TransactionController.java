package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.Transaction;
import SmartAgricultural.Management.Model.Transaction.TransactionStatus;
import SmartAgricultural.Management.Model.Transaction.PaymentMethod;
import SmartAgricultural.Management.Service.TransactionService;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.exception.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;

import java.util.ArrayList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@Validated
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // =================================================================================
    // BASIC CRUD OPERATIONS
    // =================================================================================


    @PostMapping
    public ResponseEntity<?> createTransaction(@Valid @RequestBody Transaction transaction) {
        try {
            System.out.println("=== CREATE TRANSACTION REQUEST ===");
            System.out.println("Farmer ID: " + transaction.getFarmerId());
            System.out.println("Buyer ID: " + transaction.getBuyerId());
            System.out.println("Crop ID: " + transaction.getCropId());

            // ⭐ FIX: Validate that required fields are present
            if (transaction.getFarmerId() == null || transaction.getFarmerId().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Farmer ID is required"));
            }

            if (transaction.getBuyerId() == null || transaction.getBuyerId().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Buyer ID is required"));
            }

            if (transaction.getCropId() == null || transaction.getCropId().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Crop ID is required"));
            }

            // ⭐ FIX: Set default values if missing
            if (transaction.getTransactionCode() == null) {
                // Generate transaction code if not provided
                transaction.setTransactionCode("TXN-" + System.currentTimeMillis());
            }

            if (transaction.getQuantity() == null) {
                transaction.setQuantity(new BigDecimal("1"));
            }

            if (transaction.getPricePerUnit() == null) {
                transaction.setPricePerUnit(new BigDecimal("1000"));
            }

            Transaction created = transactionService.create(transaction);

            System.out.println("=== TRANSACTION CREATED ===");
            System.out.println("ID: " + created.getId());
            System.out.println("Code: " + created.getTransactionCode());

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (Exception e) {
            System.err.println("Transaction creation error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create transaction: " + e.getMessage()));
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        System.err.println("GLOBAL EXCEPTION HANDLER TRIGGERED:");
        System.err.println("Exception type: " + e.getClass().getName());
        System.err.println("Exception message: " + e.getMessage());
        e.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Internal Server Error",
                        "Error: " + e.getMessage()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable @NotBlank String id) {
        try {
            Transaction transaction = transactionService.findById(id);
            return ResponseEntity.ok(transaction);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve transaction"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(
            @PathVariable @NotBlank String id,
            @Valid @RequestBody Transaction transaction) {
        try {
            Transaction updated = transactionService.update(id, transaction);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to update transaction"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable @NotBlank String id) {
        try {
            transactionService.deleteById(id);
            return ResponseEntity.ok(createSuccessResponse("Transaction deleted successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to delete transaction"));
        }
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getTransactionByCode(@PathVariable @NotBlank String code) {
        try {
            Transaction transaction = transactionService.findByTransactionCode(code);
            return ResponseEntity.ok(transaction);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve transaction"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTransactions(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<Transaction> transactions = transactionService.findAll(pageable);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve transactions"));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getTransactionCount() {
        try {
            long count = transactionService.count();
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get transaction count"));
        }
    }



    /**
     * Obtenir toutes les transactions sans pagination
     * GET /api/transactions/all
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllTransactionsNoPaging() {
        try {
            // Récupérer toutes les transactions avec une limite raisonnable
            Pageable pageable = PageRequest.of(0, 10000, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Transaction> transactionsPage = transactionService.findAll(pageable);
            List<Transaction> transactions = transactionsPage.getContent();

            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            // Log pour debugging
            System.err.println("Error in getAllTransactionsNoPaging: " + e.getMessage());
            e.printStackTrace();

            // Retourner liste vide au lieu d'erreur 500
            return ResponseEntity.ok(new ArrayList<>());
        }
    }




    // =================================================================================
    // FARMER-SPECIFIC OPERATIONS
    // =================================================================================

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<?> getTransactionsByFarmer(
            @PathVariable @NotBlank String farmerId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<Transaction> transactions = transactionService.findByFarmerId(farmerId, pageable);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve farmer transactions"));
        }
    }

    @GetMapping("/farmer/{farmerId}/status/{status}")
    public ResponseEntity<?> getTransactionsByFarmerAndStatus(
            @PathVariable @NotBlank String farmerId,
            @PathVariable TransactionStatus status) {
        try {
            List<Transaction> transactions = transactionService.findByFarmerIdAndStatus(farmerId, status);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve farmer transactions by status"));
        }
    }

    @GetMapping("/farmer/{farmerId}/count")
    public ResponseEntity<?> getTransactionCountByFarmer(@PathVariable @NotBlank String farmerId) {
        try {
            long count = transactionService.countByFarmerId(farmerId);
            Map<String, Object> response = new HashMap<>();
            response.put("farmerId", farmerId);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get farmer transaction count"));
        }
    }

    @GetMapping("/farmer/{farmerId}/analytics")
    public ResponseEntity<?> getFarmerAnalytics(@PathVariable @NotBlank String farmerId) {
        try {
            Map<String, Object> analytics = transactionService.getFarmerAnalytics(farmerId);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get farmer analytics"));
        }
    }

    // =================================================================================
    // BUYER-SPECIFIC OPERATIONS
    // =================================================================================

    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<?> getTransactionsByBuyer(
            @PathVariable @NotBlank String buyerId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<Transaction> transactions = transactionService.findByBuyerId(buyerId, pageable);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve buyer transactions"));
        }
    }

    @GetMapping("/buyer/{buyerId}/status/{status}")
    public ResponseEntity<?> getTransactionsByBuyerAndStatus(
            @PathVariable @NotBlank String buyerId,
            @PathVariable TransactionStatus status) {
        try {
            List<Transaction> transactions = transactionService.findByBuyerIdAndStatus(buyerId, status);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve buyer transactions by status"));
        }
    }

    @GetMapping("/buyer/{buyerId}/count")
    public ResponseEntity<?> getTransactionCountByBuyer(@PathVariable @NotBlank String buyerId) {
        try {
            long count = transactionService.countByBuyerId(buyerId);
            Map<String, Object> response = new HashMap<>();
            response.put("buyerId", buyerId);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get buyer transaction count"));
        }
    }

    @GetMapping("/buyer/{buyerId}/analytics")
    public ResponseEntity<?> getBuyerAnalytics(@PathVariable @NotBlank String buyerId) {
        try {
            Map<String, Object> analytics = transactionService.getBuyerAnalytics(buyerId);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get buyer analytics"));
        }
    }

    // =================================================================================
    // CROP-SPECIFIC OPERATIONS
    // =================================================================================

    @GetMapping("/crop/{cropId}")
    public ResponseEntity<?> getTransactionsByCrop(
            @PathVariable @NotBlank String cropId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Transaction> transactions = transactionService.findByCropId(cropId, pageable);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve crop transactions"));
        }
    }

    @GetMapping("/crop/{cropId}/analytics")
    public ResponseEntity<?> getCropAnalytics(@PathVariable @NotBlank String cropId) {
        try {
            Map<String, Object> analytics = transactionService.getCropAnalytics(cropId);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get crop analytics"));
        }
    }

    @GetMapping("/crop-production/{cropProductionId}")
    public ResponseEntity<?> getTransactionsByCropProduction(@PathVariable @NotBlank String cropProductionId) {
        try {
            List<Transaction> transactions = transactionService.findByCropProductionId(cropProductionId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve crop production transactions"));
        }
    }

    // =================================================================================
    // STATUS-BASED OPERATIONS
    // =================================================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getTransactionsByStatus(
            @PathVariable TransactionStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Transaction> transactions = transactionService.findByStatus(status, pageable);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve transactions by status"));
        }
    }

    @GetMapping("/status/active")
    public ResponseEntity<?> getActiveTransactions() {
        try {
            List<Transaction> transactions = transactionService.findActiveTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve active transactions"));
        }
    }

    @GetMapping("/status/completed")
    public ResponseEntity<?> getCompletedTransactions() {
        try {
            List<Transaction> transactions = transactionService.findCompletedTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve completed transactions"));
        }
    }

    @GetMapping("/status/cancelled")
    public ResponseEntity<?> getCancelledTransactions() {
        try {
            List<Transaction> transactions = transactionService.findCancelledTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve cancelled transactions"));
        }
    }

    @GetMapping("/status/disputed")
    public ResponseEntity<?> getDisputedTransactions() {
        try {
            List<Transaction> transactions = transactionService.findDisputedTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve disputed transactions"));
        }
    }

    @GetMapping("/overdue")
    public ResponseEntity<?> getOverdueTransactions() {
        try {
            List<Transaction> transactions = transactionService.findOverdueTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve overdue transactions"));
        }
    }

    // =================================================================================
    // TRANSACTION STATE MANAGEMENT
    // =================================================================================

    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmTransaction(@PathVariable @NotBlank String id) {
        try {
            Transaction transaction = transactionService.confirmTransaction(id);
            return ResponseEntity.ok(transaction);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to confirm transaction"));
        }
    }

    @PutMapping("/{id}/deliver")
    public ResponseEntity<?> markAsDelivered(
            @PathVariable @NotBlank String id,
            @RequestParam(required = false) String deliveryTime) {
        try {
            LocalDateTime deliveryDateTime = deliveryTime != null ? LocalDateTime.parse(deliveryTime) : null;
            Transaction transaction = transactionService.markAsDelivered(id, deliveryDateTime);
            return ResponseEntity.ok(transaction);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to mark transaction as delivered"));
        }
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<?> markAsPaid(
            @PathVariable @NotBlank String id,
            @RequestParam(required = false) String paymentTime,
            @RequestParam(required = false) String paymentNotes) {
        try {
            LocalDateTime paymentDateTime = paymentTime != null ? LocalDateTime.parse(paymentTime) : null;
            Transaction transaction = transactionService.markAsPaid(id, paymentDateTime, paymentNotes);
            return ResponseEntity.ok(transaction);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to mark transaction as paid"));
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelTransaction(
            @PathVariable @NotBlank String id,
            @RequestParam(required = false) String reason) {
        try {
            Transaction transaction = transactionService.cancelTransaction(id, reason);
            return ResponseEntity.ok(transaction);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to cancel transaction"));
        }
    }

    @PutMapping("/{id}/dispute")
    public ResponseEntity<?> markAsDisputed(
            @PathVariable @NotBlank String id,
            @RequestParam(required = false) String disputeReason) {
        try {
            Transaction transaction = transactionService.markAsDisputed(id, disputeReason);
            return ResponseEntity.ok(transaction);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to mark transaction as disputed"));
        }
    }

    // =================================================================================
    // PAYMENT OPERATIONS
    // =================================================================================

    @GetMapping("/payment-method/{paymentMethod}")
    public ResponseEntity<?> getTransactionsByPaymentMethod(@PathVariable PaymentMethod paymentMethod) {
        try {
            List<Transaction> transactions = transactionService.findByPaymentMethod(paymentMethod);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve transactions by payment method"));
        }
    }

    @GetMapping("/unpaid")
    public ResponseEntity<?> getUnpaidTransactions() {
        try {
            List<Transaction> transactions = transactionService.findUnpaidTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve unpaid transactions"));
        }
    }

    @GetMapping("/advance-payment")
    public ResponseEntity<?> getTransactionsWithAdvancePayment() {
        try {
            List<Transaction> transactions = transactionService.findTransactionsWithAdvancePayment();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve transactions with advance payment"));
        }
    }

    @PutMapping("/{id}/advance-payment")
    public ResponseEntity<?> updateAdvancePayment(
            @PathVariable @NotBlank String id,
            @RequestParam @Min(0) BigDecimal advanceAmount) {
        try {
            Transaction transaction = transactionService.updateAdvancePayment(id, advanceAmount);
            return ResponseEntity.ok(transaction);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to update advance payment"));
        }
    }

    // =================================================================================
    // DATE-BASED OPERATIONS
    // =================================================================================

    @GetMapping("/upcoming-deliveries")
    public ResponseEntity<?> getUpcomingDeliveries(
            @RequestParam(defaultValue = "7") @Min(1) @Max(365) int days) {
        try {
            List<Transaction> transactions = transactionService.findUpcomingDeliveries(days);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve upcoming deliveries"));
        }
    }

    @GetMapping("/delivery-date/{date}")
    public ResponseEntity<?> getTransactionsForDeliveryDate(@PathVariable String date) {
        try {
            LocalDate deliveryDate = LocalDate.parse(date);
            List<Transaction> transactions = transactionService.findTransactionsForDeliveryDate(deliveryDate);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve transactions for delivery date"));
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<?> getTransactionsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<Transaction> transactions = transactionService.findTransactionsByDateRange(start, end);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve transactions by date range"));
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentTransactions(
            @RequestParam(defaultValue = "30") @Min(1) @Max(365) int days) {
        try {
            List<Transaction> transactions = transactionService.findRecentTransactions(days);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve recent transactions"));
        }
    }

    // =================================================================================
    // AMOUNT-BASED OPERATIONS
    // =================================================================================

    @GetMapping("/high-value")
    public ResponseEntity<?> getHighValueTransactions(
            @RequestParam @Min(0) BigDecimal threshold) {
        try {
            List<Transaction> transactions = transactionService.findHighValueTransactions(threshold);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve high value transactions"));
        }
    }

    @GetMapping("/amount-range")
    public ResponseEntity<?> getTransactionsByAmountRange(
            @RequestParam @Min(0) BigDecimal minAmount,
            @RequestParam @Min(0) BigDecimal maxAmount) {
        try {
            List<Transaction> transactions = transactionService.findByTotalAmountRange(minAmount, maxAmount);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve transactions by amount range"));
        }
    }

    @GetMapping("/price-range")
    public ResponseEntity<?> getTransactionsByPriceRange(
            @RequestParam @Min(0) BigDecimal minPrice,
            @RequestParam @Min(0) BigDecimal maxPrice) {
        try {
            List<Transaction> transactions = transactionService.findByPriceRange(minPrice, maxPrice);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve transactions by price range"));
        }
    }

    // =================================================================================
    // LOCATION OPERATIONS
    // =================================================================================

    @GetMapping("/location/{location}")
    public ResponseEntity<?> getTransactionsByLocation(@PathVariable @NotBlank String location) {
        try {
            List<Transaction> transactions = transactionService.findByDeliveryLocation(location);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve transactions by location"));
        }
    }

    @GetMapping("/location/search/{location}")
    public ResponseEntity<?> getTransactionsByLocationContaining(@PathVariable @NotBlank String location) {
        try {
            List<Transaction> transactions = transactionService.findByDeliveryLocationContaining(location);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to search transactions by location"));
        }
    }

    // =================================================================================
    // QUALITY OPERATIONS
    // =================================================================================

    @GetMapping("/quality/{qualityGrade}")
    public ResponseEntity<?> getTransactionsByQuality(@PathVariable @NotBlank String qualityGrade) {
        try {
            List<Transaction> transactions = transactionService.findByQualityGrade(qualityGrade);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to retrieve transactions by quality grade"));
        }
    }

    @PutMapping("/{id}/quality")
    public ResponseEntity<?> updateQualitySpecifications(
            @PathVariable @NotBlank String id,
            @RequestParam(required = false) String qualityGrade,
            @RequestParam(required = false) String specifications,
            @RequestParam(required = false) String packaging) {
        try {
            Transaction transaction = transactionService.updateQualitySpecifications(id, qualityGrade, specifications, packaging);
            return ResponseEntity.ok(transaction);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to update quality specifications"));
        }
    }

    // =================================================================================
    // RATING OPERATIONS
    // =================================================================================

    @PutMapping("/{id}/rate-farmer")
    public ResponseEntity<?> addFarmerRating(
            @PathVariable @NotBlank String id,
            @RequestParam @Min(1) @Max(5) int rating,
            @RequestParam(required = false) String feedback) {
        try {
            Transaction transaction = transactionService.addFarmerRating(id, rating, feedback);
            return ResponseEntity.ok(transaction);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to add farmer rating"));
        }
    }

    @PutMapping("/{id}/rate-buyer")
    public ResponseEntity<?> addBuyerRating(
            @PathVariable @NotBlank String id,
            @RequestParam @Min(1) @Max(5) int rating,
            @RequestParam(required = false) String feedback) {
        try {
            Transaction transaction = transactionService.addBuyerRating(id, rating, feedback);
            return ResponseEntity.ok(transaction);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to add buyer rating"));
        }
    }

    // =================================================================================
    // SEARCH AND FILTERING OPERATIONS
    // =================================================================================

    @GetMapping("/search")
    public ResponseEntity<?> searchTransactions(@RequestParam @NotBlank String searchTerm) {
        try {
            List<Transaction> transactions = transactionService.searchTransactions(searchTerm);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to search transactions"));
        }
    }

    @GetMapping("/search/farmer/{farmerId}")
    public ResponseEntity<?> searchTransactionsByFarmer(
            @PathVariable @NotBlank String farmerId,
            @RequestParam @NotBlank String searchTerm) {
        try {
            List<Transaction> transactions = transactionService.searchTransactionsByFarmer(farmerId, searchTerm);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to search farmer transactions"));
        }
    }

    @GetMapping("/search/buyer/{buyerId}")
    public ResponseEntity<?> searchTransactionsByBuyer(
            @PathVariable @NotBlank String buyerId,
            @RequestParam @NotBlank String searchTerm) {
        try {
            List<Transaction> transactions = transactionService.searchTransactionsByBuyer(buyerId, searchTerm);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to search buyer transactions"));
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getTransactionsWithFilters(
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) String farmerId,
            @RequestParam(required = false) String buyerId,
            @RequestParam(required = false) String cropId,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String deliveryLocation,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<Transaction> transactions = transactionService.findWithFilters(
                    status, paymentMethod, farmerId, buyerId, cropId,
                    minAmount, maxAmount, deliveryLocation, pageable);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to filter transactions"));
        }
    }

    // =================================================================================
    // ANALYTICS AND STATISTICS
    // =================================================================================

    @GetMapping("/analytics/summary")
    public ResponseEntity<?> getTransactionSummary() {
        try {
            Map<String, Object> summary = transactionService.getTransactionSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get transaction summary"));
        }
    }

    @GetMapping("/analytics/status-stats")
    public ResponseEntity<?> getStatusStatistics() {
        try {
            Map<String, Long> stats = transactionService.getStatusStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get status statistics"));
        }
    }

    @GetMapping("/analytics/payment-method-stats")
    public ResponseEntity<?> getPaymentMethodStatistics() {
        try {
            Map<String, Long> stats = transactionService.getPaymentMethodStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get payment method statistics"));
        }
    }

    @GetMapping("/analytics/crop-stats")
    public ResponseEntity<?> getCropStatistics() {
        try {
            Map<String, Long> stats = transactionService.getCropStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get crop statistics"));
        }
    }

    @GetMapping("/analytics/monthly-stats")
    public ResponseEntity<?> getMonthlyTransactionStats() {
        try {
            List<Map<String, Object>> stats = transactionService.getMonthlyTransactionStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get monthly transaction statistics"));
        }
    }

    @GetMapping("/analytics/daily-stats")
    public ResponseEntity<?> getDailyTransactionStats(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<Map<String, Object>> stats = transactionService.getDailyTransactionStats(start, end);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get daily transaction statistics"));
        }
    }

    // =================================================================================
    // PERFORMANCE OPERATIONS
    // =================================================================================

    @GetMapping("/analytics/top-farmers")
    public ResponseEntity<?> getTopPerformingFarmers(
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {
        try {
            List<Map<String, Object>> topFarmers = transactionService.getTopPerformingFarmers(limit);
            return ResponseEntity.ok(topFarmers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get top performing farmers"));
        }
    }

    @GetMapping("/analytics/top-buyers")
    public ResponseEntity<?> getTopPerformingBuyers(
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {
        try {
            List<Map<String, Object>> topBuyers = transactionService.getTopPerformingBuyers(limit);
            return ResponseEntity.ok(topBuyers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to get top performing buyers"));
        }
    }

    // =================================================================================
    // BULK OPERATIONS
    // =================================================================================

    @PostMapping("/bulk")
    public ResponseEntity<?> createBulkTransactions(@Valid @RequestBody List<Transaction> transactions) {
        try {
            List<Transaction> created = transactionService.createTransactions(transactions);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bulk transactions created successfully");
            response.put("count", created.size());
            response.put("transactions", created);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to create bulk transactions"));
        }
    }

    @PutMapping("/bulk")
    public ResponseEntity<?> updateBulkTransactions(@Valid @RequestBody List<Transaction> transactions) {
        try {
            List<Transaction> updated = transactionService.updateTransactions(transactions);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bulk transactions updated successfully");
            response.put("count", updated.size());
            response.put("transactions", updated);
            return ResponseEntity.ok(response);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to update bulk transactions"));
        }
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<?> deleteBulkTransactions(@RequestBody List<String> ids) {
        try {
            transactionService.deleteTransactions(ids);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bulk transactions deleted successfully");
            response.put("count", ids.size());
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to delete bulk transactions"));
        }
    }

    // =================================================================================
    // MAINTENANCE OPERATIONS
    // =================================================================================

    @DeleteMapping("/maintenance/cleanup-cancelled")
    public ResponseEntity<?> cleanupOldCancelledTransactions(
            @RequestParam(defaultValue = "90") @Min(1) @Max(365) int daysOld) {
        try {
            int deletedCount = transactionService.cleanupOldCancelledTransactions(daysOld);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Old cancelled transactions cleaned up successfully");
            response.put("deletedCount", deletedCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to cleanup old cancelled transactions"));
        }
    }

    @PutMapping("/maintenance/cancel-old-pending")
    public ResponseEntity<?> cancelOldPendingTransactions(
            @RequestParam(defaultValue = "30") @Min(1) @Max(365) int daysOld) {
        try {
            int cancelledCount = transactionService.cancelOldPendingTransactions(daysOld);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Old pending transactions cancelled successfully");
            response.put("cancelledCount", cancelledCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal Error", "Failed to cancel old pending transactions"));
        }
    }

    // =================================================================================
    // UTILITY METHODS
    // =================================================================================

    private Map<String, Object> createErrorResponse(String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    // =================================================================================
    // EXCEPTION HANDLER
    // =================================================================================

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("Validation Error", e.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("Resource Not Found", e.getMessage()));
    }



    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("Invalid Argument", e.getMessage()));
    }

    // =================================================================================
    // HEALTH CHECK
    // =================================================================================

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            long totalTransactions = transactionService.count();
            Map<String, Object> health = new HashMap<>();
            health.put("status", "UP");
            health.put("service", "TransactionService");
            health.put("totalTransactions", totalTransactions);
            health.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            Map<String, Object> health = new HashMap<>();
            health.put("status", "DOWN");
            health.put("service", "TransactionService");
            health.put("error", e.getMessage());
            health.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(health);
        }
    }
}