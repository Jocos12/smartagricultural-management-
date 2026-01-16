package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.Buyer;
import SmartAgricultural.Management.Model.User;
import SmartAgricultural.Management.dto.BuyerDTO;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.Service.BuyerService;
import SmartAgricultural.Management.Service.UserService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;

@RestController
@RequestMapping("/api/buyers")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BuyerController {

    private static final Logger logger = LoggerFactory.getLogger(BuyerController.class);
    private static final boolean DEBUG_MODE = true;

    private final BuyerService buyerService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Autowired
    public BuyerController(BuyerService buyerService, UserService userService) {
        this.buyerService = buyerService;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    // Create a new buyer
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBuyer(@Valid @RequestBody BuyerDTO buyerDTO) {
        try {
            if (DEBUG_MODE) {
                logger.info("Creating buyer for user ID: {}", buyerDTO.getUserId());
            }

            // Validate that user exists and has BUYER role
            try {
                var user = userService.getUserById(buyerDTO.getUserId());
                if (user.getRole() != User.Role.BUYER) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("success", false, "message", "User must have BUYER role"));
                }
            } catch (ResourceNotFoundException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "User not found"));
            }

            // Check if buyer already exists for this user
            try {
                buyerService.getBuyerByUserId(buyerDTO.getUserId());
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Buyer profile already exists for this user"));
            } catch (ResourceNotFoundException e) {
                // Good, buyer doesn't exist yet
            }

            BuyerDTO createdBuyer = buyerService.createBuyer(buyerDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Buyer created successfully",
                    "data", createdBuyer
            ));

        } catch (Exception e) {
            logger.error("Error creating buyer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error creating buyer: " + e.getMessage()));
        }
    }

    // Create buyer with form data and file upload
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createBuyerWithFiles(
            @RequestParam("userId") String userId,
            @RequestParam("companyName") String companyName,
            @RequestParam("buyerType") String buyerType,
            @RequestParam("location") String location,
            @RequestParam(value = "businessLicense", required = false) String businessLicense,
            @RequestParam(value = "taxRegistration", required = false) String taxRegistration,
            @RequestParam(value = "contactPerson", required = false) String contactPerson,
            @RequestParam(value = "primaryProducts", required = false) String primaryProducts,
            @RequestParam(value = "creditLimit", required = false) String creditLimit,
            @RequestParam(value = "creditRating", required = false) String creditRating,
            @RequestParam(value = "paymentTerms", required = false) String paymentTerms,
            @RequestParam(value = "preferredPaymentMethods", required = false) String preferredPaymentMethods,
            @RequestParam(value = "storageCapacity", required = false) String storageCapacity,
            @RequestParam(value = "transportCapacity", required = false) String transportCapacity,
            @RequestParam(value = "qualityStandards", required = false) String qualityStandards,
            @RequestParam(value = "certificationsRequired", required = false) String certificationsRequired,
            @RequestParam(value = "seasonalDemand", required = false) String seasonalDemand,
            @RequestParam(value = "geographicalCoverage", required = false) String geographicalCoverage,
            @RequestParam(value = "establishedYear", required = false) String establishedYear,
            @RequestParam(value = "annualVolume", required = false) String annualVolume,
            @RequestParam(value = "documents", required = false) MultipartFile[] documents) {

        try {
            // Validate required fields
            if (userId == null || userId.trim().isEmpty() ||
                    companyName == null || companyName.trim().isEmpty() ||
                    buyerType == null || buyerType.trim().isEmpty() ||
                    location == null || location.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Required fields: userId, companyName, buyerType, location"));
            }

            // Create BuyerDTO
            BuyerDTO buyerDTO = new BuyerDTO();
            buyerDTO.setUserId(userId);
            buyerDTO.setCompanyName(companyName);
            buyerDTO.setBuyerType(Buyer.BuyerType.valueOf(buyerType.toUpperCase()));
            buyerDTO.setLocation(location);
            buyerDTO.setBusinessLicense(businessLicense);
            buyerDTO.setTaxRegistration(taxRegistration);
            buyerDTO.setContactPerson(contactPerson);

            // Parse JSON fields
            if (primaryProducts != null && !primaryProducts.trim().isEmpty()) {
                try {
                    List<String> productList = objectMapper.readValue(primaryProducts, new TypeReference<List<String>>() {});
                    buyerDTO.setPrimaryProductsList(productList);
                } catch (Exception e) {
                    logger.warn("Failed to parse primaryProducts JSON: {}", primaryProducts);
                }
            }

            if (preferredPaymentMethods != null && !preferredPaymentMethods.trim().isEmpty()) {
                try {
                    List<String> methodsList = objectMapper.readValue(preferredPaymentMethods, new TypeReference<List<String>>() {});
                    buyerDTO.setPreferredPaymentMethodsList(methodsList);
                } catch (Exception e) {
                    logger.warn("Failed to parse preferredPaymentMethods JSON: {}", preferredPaymentMethods);
                }
            }

            // Parse numeric fields
            if (creditLimit != null && !creditLimit.trim().isEmpty()) {
                try {
                    buyerDTO.setCreditLimit(new BigDecimal(creditLimit));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid creditLimit format: {}", creditLimit);
                }
            }

            if (storageCapacity != null && !storageCapacity.trim().isEmpty()) {
                try {
                    buyerDTO.setStorageCapacity(new BigDecimal(storageCapacity));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid storageCapacity format: {}", storageCapacity);
                }
            }

            if (establishedYear != null && !establishedYear.trim().isEmpty()) {
                try {
                    buyerDTO.setEstablishedYear(Integer.parseInt(establishedYear));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid establishedYear format: {}", establishedYear);
                }
            }

            if (annualVolume != null && !annualVolume.trim().isEmpty()) {
                try {
                    buyerDTO.setAnnualVolume(new BigDecimal(annualVolume));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid annualVolume format: {}", annualVolume);
                }
            }

            // Set other fields
            buyerDTO.setCreditRating(creditRating);
            buyerDTO.setPaymentTerms(paymentTerms);
            buyerDTO.setTransportCapacity(transportCapacity);
            buyerDTO.setQualityStandards(qualityStandards);
            buyerDTO.setCertificationsRequired(certificationsRequired);
            buyerDTO.setSeasonalDemand(seasonalDemand);
            buyerDTO.setGeographicalCoverage(geographicalCoverage);

            // Handle file uploads if present
            if (documents != null && documents.length > 0) {
                for (MultipartFile document : documents) {
                    if (document != null && !document.isEmpty()) {
                        // Validate file size (max 5MB per file)
                        if (document.getSize() > 5 * 1024 * 1024) {
                            return ResponseEntity.badRequest()
                                    .body(Map.of("success", false, "message", "Document size must be less than 5MB"));
                        }

                        // Here you would typically upload to a file storage service
                        // For now, we'll just log the file info
                        logger.info("Received document: {} ({}bytes)", document.getOriginalFilename(), document.getSize());
                    }
                }
            }

            BuyerDTO createdBuyer = buyerService.createBuyer(buyerDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Buyer created successfully",
                    "data", createdBuyer
            ));

        } catch (Exception e) {
            logger.error("Error creating buyer with files", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error creating buyer: " + e.getMessage()));
        }
    }

    // Get all buyers
    @GetMapping
    public ResponseEntity<List<BuyerDTO>> getAllBuyers(
            @RequestParam(value = "buyerType", required = false) String buyerType,
            @RequestParam(value = "verified", required = false) Boolean verified,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "minRating", required = false) String minRating,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection) {
        try {
            List<BuyerDTO> buyers;

            if (buyerType != null || verified != null || location != null || minRating != null) {
                // Apply filters
                buyers = buyerService.getBuyersWithFilters(buyerType, verified, location, minRating, sortBy, sortDirection);
            } else {
                buyers = buyerService.getAllBuyers();
            }

            return ResponseEntity.ok(buyers);
        } catch (Exception e) {
            logger.error("Error getting buyers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get buyer by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBuyerById(@PathVariable String id) {
        try {
            BuyerDTO buyer = buyerService.getBuyerById(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", buyer
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Buyer not found"));
        } catch (Exception e) {
            logger.error("Error getting buyer by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error retrieving buyer information"));
        }
    }

    // Get buyer by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getBuyerByUserId(@PathVariable String userId) {
        try {
            BuyerDTO buyer = buyerService.getBuyerByUserId(userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", buyer
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Buyer not found for this user"));
        } catch (Exception e) {
            logger.error("Error getting buyer by user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error retrieving buyer information"));
        }
    }

    // Get buyer by buyer code
    @GetMapping("/code/{buyerCode}")
    public ResponseEntity<Map<String, Object>> getBuyerByBuyerCode(@PathVariable String buyerCode) {
        try {
            BuyerDTO buyer = buyerService.getBuyerByBuyerCode(buyerCode);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", buyer
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Buyer not found with this buyer code"));
        } catch (Exception e) {
            logger.error("Error getting buyer by buyer code: {}", buyerCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error retrieving buyer information"));
        }
    }

    // Update buyer
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBuyer(@PathVariable String id, @Valid @RequestBody BuyerDTO buyerDTO) {
        try {
            BuyerDTO updatedBuyer = buyerService.updateBuyer(id, buyerDTO);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Buyer updated successfully",
                    "data", updatedBuyer
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Buyer not found"));
        } catch (Exception e) {
            logger.error("Error updating buyer: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error updating buyer: " + e.getMessage()));
        }
    }

    // Update buyer with multipart data
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateBuyerWithFiles(
            @PathVariable String id,
            @RequestParam("companyName") String companyName,
            @RequestParam("buyerType") String buyerType,
            @RequestParam("location") String location,
            @RequestParam(value = "businessLicense", required = false) String businessLicense,
            @RequestParam(value = "taxRegistration", required = false) String taxRegistration,
            @RequestParam(value = "contactPerson", required = false) String contactPerson,
            @RequestParam(value = "primaryProducts", required = false) String primaryProducts,
            @RequestParam(value = "creditLimit", required = false) String creditLimit,
            @RequestParam(value = "creditRating", required = false) String creditRating,
            @RequestParam(value = "paymentTerms", required = false) String paymentTerms,
            @RequestParam(value = "preferredPaymentMethods", required = false) String preferredPaymentMethods,
            @RequestParam(value = "storageCapacity", required = false) String storageCapacity,
            @RequestParam(value = "transportCapacity", required = false) String transportCapacity,
            @RequestParam(value = "qualityStandards", required = false) String qualityStandards,
            @RequestParam(value = "certificationsRequired", required = false) String certificationsRequired,
            @RequestParam(value = "seasonalDemand", required = false) String seasonalDemand,
            @RequestParam(value = "geographicalCoverage", required = false) String geographicalCoverage,
            @RequestParam(value = "establishedYear", required = false) String establishedYear,
            @RequestParam(value = "annualVolume", required = false) String annualVolume,
            @RequestParam(value = "documents", required = false) MultipartFile[] documents) {

        try {
            // Get existing buyer
            BuyerDTO existingBuyer = buyerService.getBuyerById(id);

            // Update fields
            existingBuyer.setCompanyName(companyName);
            existingBuyer.setBuyerType(Buyer.BuyerType.valueOf(buyerType.toUpperCase()));
            existingBuyer.setLocation(location);
            existingBuyer.setBusinessLicense(businessLicense);
            existingBuyer.setTaxRegistration(taxRegistration);
            existingBuyer.setContactPerson(contactPerson);

            // Parse JSON fields
            if (primaryProducts != null && !primaryProducts.trim().isEmpty()) {
                try {
                    List<String> productList = objectMapper.readValue(primaryProducts, new TypeReference<List<String>>() {});
                    existingBuyer.setPrimaryProductsList(productList);
                } catch (Exception e) {
                    logger.warn("Failed to parse primaryProducts JSON: {}", primaryProducts);
                }
            }

            // Parse numeric fields
            if (creditLimit != null && !creditLimit.trim().isEmpty()) {
                try {
                    existingBuyer.setCreditLimit(new BigDecimal(creditLimit));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid creditLimit format: {}", creditLimit);
                }
            }

            // Set other fields...
            existingBuyer.setCreditRating(creditRating);
            existingBuyer.setPaymentTerms(paymentTerms);
            existingBuyer.setTransportCapacity(transportCapacity);
            existingBuyer.setQualityStandards(qualityStandards);
            existingBuyer.setCertificationsRequired(certificationsRequired);
            existingBuyer.setSeasonalDemand(seasonalDemand);
            existingBuyer.setGeographicalCoverage(geographicalCoverage);

            if (establishedYear != null && !establishedYear.trim().isEmpty()) {
                try {
                    existingBuyer.setEstablishedYear(Integer.parseInt(establishedYear));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid establishedYear format: {}", establishedYear);
                }
            }

            if (annualVolume != null && !annualVolume.trim().isEmpty()) {
                try {
                    existingBuyer.setAnnualVolume(new BigDecimal(annualVolume));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid annualVolume format: {}", annualVolume);
                }
            }

            BuyerDTO updatedBuyer = buyerService.updateBuyer(id, existingBuyer);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Buyer updated successfully",
                    "data", updatedBuyer
            ));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Buyer not found"));
        } catch (Exception e) {
            logger.error("Error updating buyer with files: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error updating buyer: " + e.getMessage()));
        }
    }

    // Delete buyer
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBuyer(@PathVariable String id) {
        try {
            buyerService.deleteBuyer(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Buyer deleted successfully"
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Buyer not found"));
        } catch (Exception e) {
            logger.error("Error deleting buyer: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error deleting buyer"));
        }
    }

    // Verify buyer
    @PutMapping("/{id}/verify")
    public ResponseEntity<Map<String, Object>> verifyBuyer(@PathVariable String id) {
        try {
            buyerService.verifyBuyer(id, true);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Buyer verified successfully"
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Buyer not found"));
        } catch (Exception e) {
            logger.error("Error verifying buyer: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error verifying buyer"));
        }
    }

    // Unverify buyer
    @PutMapping("/{id}/unverify")
    public ResponseEntity<Map<String, Object>> unverifyBuyer(@PathVariable String id) {
        try {
            buyerService.verifyBuyer(id, false);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Buyer verification removed successfully"
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Buyer not found"));
        } catch (Exception e) {
            logger.error("Error unverifying buyer: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error removing buyer verification"));
        }
    }

    // Update buyer rating
    @PutMapping("/{id}/rating")
    public ResponseEntity<Map<String, Object>> updateBuyerRating(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            String ratingStr = request.get("rating");
            if (ratingStr == null || ratingStr.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Rating is required"));
            }

            BigDecimal rating;
            try {
                rating = new BigDecimal(ratingStr);
                if (rating.compareTo(BigDecimal.ONE) < 0 || rating.compareTo(new BigDecimal("5")) > 0) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("success", false, "message", "Rating must be between 1.0 and 5.0"));
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid rating format"));
            }

            buyerService.updateBuyerRating(id, rating);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Buyer rating updated successfully"
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Buyer not found"));
        } catch (Exception e) {
            logger.error("Error updating buyer rating: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error updating buyer rating"));
        }
    }

    // Get buyers by type
    @GetMapping("/type/{buyerType}")
    public ResponseEntity<List<BuyerDTO>> getBuyersByType(@PathVariable String buyerType) {
        try {
            Buyer.BuyerType type = Buyer.BuyerType.valueOf(buyerType.toUpperCase());
            List<BuyerDTO> buyers = buyerService.getBuyersByType(type);
            return ResponseEntity.ok(buyers);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid buyer type: {}", buyerType);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error getting buyers by type: {}", buyerType, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get verified buyers
    @GetMapping("/verified")
    public ResponseEntity<List<BuyerDTO>> getVerifiedBuyers() {
        try {
            List<BuyerDTO> buyers = buyerService.getVerifiedBuyers();
            return ResponseEntity.ok(buyers);
        } catch (Exception e) {
            logger.error("Error getting verified buyers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get unverified buyers
    @GetMapping("/unverified")
    public ResponseEntity<List<BuyerDTO>> getUnverifiedBuyers() {
        try {
            List<BuyerDTO> buyers = buyerService.getUnverifiedBuyers();
            return ResponseEntity.ok(buyers);
        } catch (Exception e) {
            logger.error("Error getting unverified buyers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get buyers by location
    @GetMapping("/location/{location}")
    public ResponseEntity<List<BuyerDTO>> getBuyersByLocation(@PathVariable String location) {
        try {
            List<BuyerDTO> buyers = buyerService.getBuyersByLocation(location);
            return ResponseEntity.ok(buyers);
        } catch (Exception e) {
            logger.error("Error getting buyers by location: {}", location, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get high-volume buyers
    @GetMapping("/high-volume")
    public ResponseEntity<List<BuyerDTO>> getHighVolumeBuyers() {
        try {
            List<BuyerDTO> buyers = buyerService.getHighVolumeBuyers();
            return ResponseEntity.ok(buyers);
        } catch (Exception e) {
            logger.error("Error getting high-volume buyers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get buyers with good credit rating
    @GetMapping("/good-credit")
    public ResponseEntity<List<BuyerDTO>> getBuyersWithGoodCredit() {
        try {
            List<BuyerDTO> buyers = buyerService.getBuyersWithGoodCredit();
            return ResponseEntity.ok(buyers);
        } catch (Exception e) {
            logger.error("Error getting buyers with good credit", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Search buyers
    @PostMapping("/search")
    public ResponseEntity<List<BuyerDTO>> searchBuyers(@RequestBody Map<String, Object> searchCriteria) {
        try {
            String query = (String) searchCriteria.get("query");
            String buyerType = (String) searchCriteria.get("buyerType");
            Boolean verified = (Boolean) searchCriteria.get("verified");
            String location = (String) searchCriteria.get("location");

            List<BuyerDTO> buyers = buyerService.searchBuyers(query, buyerType, verified, location);
            return ResponseEntity.ok(buyers);
        } catch (Exception e) {
            logger.error("Error searching buyers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get buyer statistics
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getBuyerStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            statistics.put("totalBuyers", buyerService.getTotalBuyerCount());
            statistics.put("verifiedBuyers", buyerService.getVerifiedBuyerCount());
            statistics.put("unverifiedBuyers", buyerService.getUnverifiedBuyerCount());

            // Type-based statistics
            Map<String, Long> typeStats = new HashMap<>();
            for (Buyer.BuyerType type : Buyer.BuyerType.values()) {
                typeStats.put(type.name(), buyerService.getBuyerCountByType(type));
            }
            statistics.put("typeStatistics", typeStats);

            // Rating statistics
            statistics.put("averageRating", buyerService.getAverageRating());
            statistics.put("highRatedBuyers", buyerService.getHighRatedBuyerCount());

            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Error getting buyer statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get buyer dashboard data
    @GetMapping("/dashboard/{buyerId}")
    public ResponseEntity<Map<String, Object>> getBuyerDashboard(@PathVariable String buyerId) {
        try {
            BuyerDTO buyer = buyerService.getBuyerById(buyerId);

            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("buyer", buyer);
            dashboard.put("businessAge", Year.now().getValue() - (buyer.getEstablishedYear() != null ? buyer.getEstablishedYear() : Year.now().getValue()));
            dashboard.put("isPremiumBuyer", buyer.getRating() != null && buyer.getRating().compareTo(new BigDecimal("4.0")) >= 0);
            dashboard.put("needsVerification", buyer.getVerified() == null || !buyer.getVerified());
            dashboard.put("isHighVolume", buyer.getAnnualVolume() != null && buyer.getAnnualVolume().compareTo(new BigDecimal("1000")) > 0);
            dashboard.put("hasGoodCredit", buyer.getCreditRating() != null &&
                    (buyer.getCreditRating().equals("A+") || buyer.getCreditRating().equals("A") || buyer.getCreditRating().equals("B+")));
            dashboard.put("storageCapacityFormatted", buyer.getStorageCapacity() != null ? buyer.getStorageCapacity() + " tonnes" : "Not specified");
            dashboard.put("creditLimitFormatted", buyer.getCreditLimit() != null ? "RWF " + buyer.getCreditLimit() : "Not specified");

            return ResponseEntity.ok(dashboard);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Buyer not found"));
        } catch (Exception e) {
            logger.error("Error getting buyer dashboard: {}", buyerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error retrieving buyer dashboard"));
        }
    }

    // Get buyer profile summary
    @GetMapping("/{id}/profile-summary")
    public ResponseEntity<Map<String, Object>> getBuyerProfileSummary(@PathVariable String id) {
        try {
            BuyerDTO buyer = buyerService.getBuyerById(id);

            Map<String, Object> summary = new HashMap<>();

            // Company summary
            StringBuilder companySummary = new StringBuilder();
            if (buyer.getCompanyName() != null) {
                companySummary.append(buyer.getCompanyName());
            }
            if (buyer.getBuyerType() != null) {
                if (companySummary.length() > 0) companySummary.append(" - ");
                companySummary.append(buyer.getBuyerType().getDisplayName());
            }
            if (buyer.getLocation() != null) {
                if (companySummary.length() > 0) companySummary.append(" - ");
                companySummary.append(buyer.getLocation());
            }
            summary.put("companySummary", companySummary.length() > 0 ? companySummary.toString() : "No data");

            // Business profile summary
            StringBuilder businessSummary = new StringBuilder();
            if (buyer.getEstablishedYear() != null) {
                businessSummary.append("Est. ").append(buyer.getEstablishedYear());
            }
            if (buyer.getAnnualVolume() != null) {
                if (businessSummary.length() > 0) businessSummary.append(" - ");
                businessSummary.append(buyer.getAnnualVolume()).append(" tonnes/year");
            }
            if (buyer.getRating() != null) {
                if (businessSummary.length() > 0) businessSummary.append(" - ");
                businessSummary.append("Rating: ").append(buyer.getRating()).append("/5.0");
            }
            if (buyer.getVerified() != null && buyer.getVerified()) {
                if (businessSummary.length() > 0) businessSummary.append(" - ");
                businessSummary.append("Verified");
            }
            summary.put("businessSummary", businessSummary.length() > 0 ? businessSummary.toString() : "No profile data");

            // Financial summary
            StringBuilder financialSummary = new StringBuilder();
            if (buyer.getCreditRating() != null) {
                financialSummary.append("Credit: ").append(buyer.getCreditRating());
            }
            if (buyer.getCreditLimit() != null && buyer.getCreditLimit().compareTo(BigDecimal.ZERO) > 0) {
                if (financialSummary.length() > 0) financialSummary.append(" - ");
                financialSummary.append("Limit: RWF ").append(buyer.getCreditLimit());
            }
            if (buyer.getPaymentTerms() != null) {
                if (financialSummary.length() > 0) financialSummary.append(" - ");
                financialSummary.append(buyer.getPaymentTerms());
            }
            summary.put("financialSummary", financialSummary.length() > 0 ? financialSummary.toString() : "No financial data");

            // Capacity summary
            StringBuilder capacitySummary = new StringBuilder();
            if (buyer.getStorageCapacity() != null && buyer.getStorageCapacity().compareTo(BigDecimal.ZERO) > 0) {
                capacitySummary.append("Storage: ").append(buyer.getStorageCapacity()).append(" tonnes");
            }
            if (buyer.getTransportCapacity() != null) {
                if (capacitySummary.length() > 0) capacitySummary.append(" - ");
                capacitySummary.append("Transport: ").append(buyer.getTransportCapacity());
            }
            if (buyer.getGeographicalCoverage() != null) {
                if (capacitySummary.length() > 0) capacitySummary.append(" - ");
                capacitySummary.append("Coverage: ").append(buyer.getGeographicalCoverage());
            }
            summary.put("capacitySummary", capacitySummary.length() > 0 ? capacitySummary.toString() : "No capacity data");

            return ResponseEntity.ok(summary);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Buyer not found"));
        } catch (Exception e) {
            logger.error("Error getting buyer profile summary: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error retrieving buyer profile summary"));
        }
    }

    // Get premium buyers (high rating and verified)
    @GetMapping("/premium")
    public ResponseEntity<List<BuyerDTO>> getPremiumBuyers() {
        try {
            List<BuyerDTO> buyers = buyerService.getPremiumBuyers();
            return ResponseEntity.ok(buyers);
        } catch (Exception e) {
            logger.error("Error getting premium buyers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get experienced buyers (established for 5+ years)
    @GetMapping("/experienced")
    public ResponseEntity<List<BuyerDTO>> getExperiencedBuyers() {
        try {
            List<BuyerDTO> buyers = buyerService.getExperiencedBuyers();
            return ResponseEntity.ok(buyers);
        } catch (Exception e) {
            logger.error("Error getting experienced buyers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get government buyers
    @GetMapping("/government")
    public ResponseEntity<List<BuyerDTO>> getGovernmentBuyers() {
        try {
            List<BuyerDTO> buyers = buyerService.getBuyersByType(Buyer.BuyerType.GOVERNMENT);
            return ResponseEntity.ok(buyers);
        } catch (Exception e) {
            logger.error("Error getting government buyers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get exporter buyers
    @GetMapping("/exporters")
    public ResponseEntity<List<BuyerDTO>> getExporterBuyers() {
        try {
            List<BuyerDTO> buyers = buyerService.getBuyersByType(Buyer.BuyerType.EXPORTER);
            return ResponseEntity.ok(buyers);
        } catch (Exception e) {
            logger.error("Error getting exporter buyers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get processor buyers
    @GetMapping("/processors")
    public ResponseEntity<List<BuyerDTO>> getProcessorBuyers() {
        try {
            List<BuyerDTO> buyers = buyerService.getBuyersByType(Buyer.BuyerType.PROCESSOR);
            return ResponseEntity.ok(buyers);
        } catch (Exception e) {
            logger.error("Error getting processor buyers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get large capacity buyers
    @GetMapping("/large-capacity")
    public ResponseEntity<List<BuyerDTO>> getLargeCapacityBuyers() {
        try {
            List<BuyerDTO> buyers = buyerService.getLargeCapacityBuyers();
            return ResponseEntity.ok(buyers);
        } catch (Exception e) {
            logger.error("Error getting large capacity buyers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get buyers with high credit limit
    @GetMapping("/high-credit-limit")
    public ResponseEntity<List<BuyerDTO>> getBuyersWithHighCreditLimit() {
        try {
            List<BuyerDTO> buyers = buyerService.getBuyersWithHighCreditLimit();
            return ResponseEntity.ok(buyers);
        } catch (Exception e) {
            logger.error("Error getting buyers with high credit limit", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update credit rating
    @PutMapping("/{id}/credit-rating")
    public ResponseEntity<Map<String, Object>> updateCreditRating(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            String creditRating = request.get("creditRating");
            if (creditRating == null || creditRating.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Credit rating is required"));
            }

            // Validate credit rating
            List<String> validRatings = Arrays.asList("A+", "A", "B+", "B", "C");
            if (!validRatings.contains(creditRating)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid credit rating. Valid ratings: A+, A, B+, B, C"));
            }

            buyerService.updateCreditRating(id, creditRating);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Credit rating updated successfully"
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Buyer not found"));
        } catch (Exception e) {
            logger.error("Error updating credit rating: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error updating credit rating"));
        }
    }

    // Update credit limit
    @PutMapping("/{id}/credit-limit")
    public ResponseEntity<Map<String, Object>> updateCreditLimit(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            String creditLimitStr = request.get("creditLimit");
            if (creditLimitStr == null || creditLimitStr.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Credit limit is required"));
            }

            BigDecimal creditLimit;
            try {
                creditLimit = new BigDecimal(creditLimitStr);
                if (creditLimit.compareTo(BigDecimal.ZERO) < 0) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("success", false, "message", "Credit limit must be positive"));
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid credit limit format"));
            }

            buyerService.updateCreditLimit(id, creditLimit);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Credit limit updated successfully"
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Buyer not found"));
        } catch (Exception e) {
            logger.error("Error updating credit limit: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error updating credit limit"));
        }
    }

    // Get buyer types enum
    @GetMapping("/buyer-types")
    public ResponseEntity<List<Map<String, String>>> getBuyerTypes() {
        try {
            List<Map<String, String>> types = new ArrayList<>();
            for (Buyer.BuyerType type : Buyer.BuyerType.values()) {
                Map<String, String> typeMap = new HashMap<>();
                typeMap.put("value", type.name());
                typeMap.put("label", type.getDisplayName());
                types.add(typeMap);
            }
            return ResponseEntity.ok(types);
        } catch (Exception e) {
            logger.error("Error getting buyer types", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get credit ratings enum
    @GetMapping("/credit-ratings")
    public ResponseEntity<List<Map<String, Object>>> getCreditRatings() {
        try {
            List<Map<String, Object>> ratings = new ArrayList<>();
            for (Buyer.CreditRating rating : Buyer.CreditRating.values()) {
                Map<String, Object> ratingMap = new HashMap<>();
                ratingMap.put("value", rating.getDisplayName());
                ratingMap.put("label", rating.getDisplayName());
                ratingMap.put("numericValue", rating.getNumericValue());
                ratings.add(ratingMap);
            }
            return ResponseEntity.ok(ratings);
        } catch (Exception e) {
            logger.error("Error getting credit ratings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}