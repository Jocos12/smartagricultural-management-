package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.Buyer;
import SmartAgricultural.Management.dto.BuyerDTO;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.Repository.BuyerRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BuyerService {

    private final BuyerRepository buyerRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public BuyerService(BuyerRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
        this.objectMapper = new ObjectMapper();
    }

    // Create
    public BuyerDTO createBuyer(BuyerDTO buyerDTO) {
        Buyer buyer = convertToEntity(buyerDTO);
        buyer = buyerRepository.save(buyer);
        return convertToDTO(buyer);
    }

    // Read
    public List<BuyerDTO> getAllBuyers() {
        return buyerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BuyerDTO getBuyerById(String id) {
        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with ID: " + id));
        return convertToDTO(buyer);
    }

    public BuyerDTO getBuyerByUserId(String userId) {
        Buyer buyer = buyerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found for user ID: " + userId));
        return convertToDTO(buyer);
    }

    public BuyerDTO getBuyerByBuyerCode(String buyerCode) {
        Buyer buyer = buyerRepository.findByBuyerCode(buyerCode)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with buyer code: " + buyerCode));
        return convertToDTO(buyer);
    }

    // Update
    public BuyerDTO updateBuyer(String id, BuyerDTO buyerDTO) {
        Buyer existingBuyer = buyerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with ID: " + id));

        // Update fields
        existingBuyer.setCompanyName(buyerDTO.getCompanyName());
        existingBuyer.setBuyerType(buyerDTO.getBuyerType());
        existingBuyer.setBusinessLicense(buyerDTO.getBusinessLicense());
        existingBuyer.setTaxRegistration(buyerDTO.getTaxRegistration());
        existingBuyer.setLocation(buyerDTO.getLocation());
        existingBuyer.setContactPerson(buyerDTO.getContactPerson());
        existingBuyer.setPrimaryProductsList(buyerDTO.getPrimaryProductsList());
        existingBuyer.setCreditLimit(buyerDTO.getCreditLimit());
        existingBuyer.setCreditRating(buyerDTO.getCreditRating());
        existingBuyer.setPaymentTerms(buyerDTO.getPaymentTerms());
        existingBuyer.setPreferredPaymentMethodsList(buyerDTO.getPreferredPaymentMethodsList());
        existingBuyer.setStorageCapacity(buyerDTO.getStorageCapacity());
        existingBuyer.setTransportCapacity(buyerDTO.getTransportCapacity());
        existingBuyer.setQualityStandards(buyerDTO.getQualityStandards());
        existingBuyer.setCertificationsRequired(buyerDTO.getCertificationsRequired());
        existingBuyer.setSeasonalDemand(buyerDTO.getSeasonalDemand());
        existingBuyer.setGeographicalCoverage(buyerDTO.getGeographicalCoverage());
        existingBuyer.setEstablishedYear(buyerDTO.getEstablishedYear());
        existingBuyer.setAnnualVolume(buyerDTO.getAnnualVolume());

        if (buyerDTO.getVerified() != null) {
            existingBuyer.setVerified(buyerDTO.getVerified());
        }

        existingBuyer = buyerRepository.save(existingBuyer);
        return convertToDTO(existingBuyer);
    }

    // Delete
    public void deleteBuyer(String id) {
        if (!buyerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Buyer not found with ID: " + id);
        }
        buyerRepository.deleteById(id);
    }

    // Verification
    public void verifyBuyer(String id, boolean verified) {
        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with ID: " + id));
        buyer.setVerified(verified);
        buyerRepository.save(buyer);
    }

    // Rating
    public void updateBuyerRating(String id, BigDecimal rating) {
        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with ID: " + id));
        buyer.setRating(rating);
        buyerRepository.save(buyer);
    }

    // Specialized queries
    public List<BuyerDTO> getBuyersByType(Buyer.BuyerType buyerType) {
        return buyerRepository.findByBuyerType(buyerType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BuyerDTO> getVerifiedBuyers() {
        return buyerRepository.findByVerified(true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BuyerDTO> getUnverifiedBuyers() {
        return buyerRepository.findByVerified(false).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BuyerDTO> getBuyersByLocation(String location) {
        return buyerRepository.findByLocationContainingIgnoreCase(location).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BuyerDTO> getHighVolumeBuyers() {
        return buyerRepository.findHighVolumeBuyers(new BigDecimal("1000")).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BuyerDTO> getBuyersWithGoodCredit() {
        List<String> goodRatings = Arrays.asList("A+", "A", "B+");
        return buyerRepository.findByGoodCreditRatings(goodRatings).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BuyerDTO> getPremiumBuyers() {
        return buyerRepository.findPremiumBuyers(new BigDecimal("4.0")).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BuyerDTO> getExperiencedBuyers() {
        int maxYear = Year.now().getValue() - 5; // 5+ years experience
        return buyerRepository.findExperiencedBuyers(maxYear).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BuyerDTO> getLargeCapacityBuyers() {
        return buyerRepository.findLargeCapacityBuyers(new BigDecimal("500")).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BuyerDTO> getBuyersWithHighCreditLimit() {
        return buyerRepository.findByHighCreditLimit(new BigDecimal("50000")).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Search
    public List<BuyerDTO> searchBuyers(String query, String buyerType, Boolean verified, String location) {
        Buyer.BuyerType type = null;
        if (buyerType != null && !buyerType.trim().isEmpty()) {
            try {
                type = Buyer.BuyerType.valueOf(buyerType.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid buyer type, ignore
            }
        }

        return buyerRepository.searchBuyers(query, type, verified, location).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BuyerDTO> getBuyersWithFilters(String buyerType, Boolean verified, String location, String minRating, String sortBy, String sortDirection) {
        // For simplicity, using the search method. You can implement more sophisticated filtering
        Buyer.BuyerType type = null;
        if (buyerType != null && !buyerType.trim().isEmpty()) {
            try {
                type = Buyer.BuyerType.valueOf(buyerType.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid buyer type, ignore
            }
        }

        List<BuyerDTO> buyers = buyerRepository.searchBuyers(null, type, verified, location).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // Apply rating filter if specified
        if (minRating != null && !minRating.trim().isEmpty()) {
            try {
                BigDecimal minRatingValue = new BigDecimal(minRating);
                buyers = buyers.stream()
                        .filter(b -> b.getRating() != null && b.getRating().compareTo(minRatingValue) >= 0)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // Invalid rating format, ignore filter
            }
        }

        return buyers;
    }

    // Statistics
    public long getTotalBuyerCount() {
        return buyerRepository.count();
    }

    public long getVerifiedBuyerCount() {
        return buyerRepository.countByVerified(true);
    }

    public long getUnverifiedBuyerCount() {
        return buyerRepository.countByVerified(false);
    }

    public long getBuyerCountByType(Buyer.BuyerType buyerType) {
        return buyerRepository.countByBuyerType(buyerType);
    }

    public Double getAverageRating() {
        Double avg = buyerRepository.findAverageRating();
        return avg != null ? avg : 0.0;
    }

    public long getHighRatedBuyerCount() {
        return buyerRepository.countByRatingGreaterThanEqual(new BigDecimal("4.0"));
    }

    // Credit operations
    public void updateCreditRating(String id, String creditRating) {
        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with ID: " + id));
        buyer.setCreditRating(creditRating);
        buyerRepository.save(buyer);
    }

    public void updateCreditLimit(String id, BigDecimal creditLimit) {
        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with ID: " + id));
        buyer.setCreditLimit(creditLimit);
        buyerRepository.save(buyer);
    }

    // Conversion methods
    private BuyerDTO convertToDTO(Buyer buyer) {
        BuyerDTO dto = new BuyerDTO();
        dto.setId(buyer.getId());
        dto.setUserId(buyer.getUserId());
        dto.setBuyerCode(buyer.getBuyerCode());
        dto.setCompanyName(buyer.getCompanyName());
        dto.setBuyerType(buyer.getBuyerType());
        dto.setBusinessLicense(buyer.getBusinessLicense());
        dto.setTaxRegistration(buyer.getTaxRegistration());
        dto.setLocation(buyer.getLocation());
        dto.setContactPerson(buyer.getContactPerson());
        dto.setPrimaryProductsList(buyer.getPrimaryProductsList());
        dto.setCreditLimit(buyer.getCreditLimit());
        dto.setCreditRating(buyer.getCreditRating());
        dto.setPaymentTerms(buyer.getPaymentTerms());
        dto.setPreferredPaymentMethodsList(buyer.getPreferredPaymentMethodsList());
        dto.setStorageCapacity(buyer.getStorageCapacity());
        dto.setTransportCapacity(buyer.getTransportCapacity());
        dto.setQualityStandards(buyer.getQualityStandards());
        dto.setCertificationsRequired(buyer.getCertificationsRequired());
        dto.setSeasonalDemand(buyer.getSeasonalDemand());
        dto.setGeographicalCoverage(buyer.getGeographicalCoverage());
        dto.setEstablishedYear(buyer.getEstablishedYear());
        dto.setAnnualVolume(buyer.getAnnualVolume());
        dto.setRating(buyer.getRating());
        dto.setVerified(buyer.getVerified());
        dto.setCreatedAt(buyer.getCreatedAt());
        dto.setUpdatedAt(buyer.getUpdatedAt());
        return dto;
    }

    private Buyer convertToEntity(BuyerDTO dto) {
        Buyer buyer = new Buyer();
        buyer.setUserId(dto.getUserId());
        buyer.setCompanyName(dto.getCompanyName());
        buyer.setBuyerType(dto.getBuyerType());
        buyer.setBusinessLicense(dto.getBusinessLicense());
        buyer.setTaxRegistration(dto.getTaxRegistration());
        buyer.setLocation(dto.getLocation());
        buyer.setContactPerson(dto.getContactPerson());
        buyer.setPrimaryProductsList(dto.getPrimaryProductsList());
        buyer.setCreditLimit(dto.getCreditLimit());
        buyer.setCreditRating(dto.getCreditRating());
        buyer.setPaymentTerms(dto.getPaymentTerms());
        buyer.setPreferredPaymentMethodsList(dto.getPreferredPaymentMethodsList());
        buyer.setStorageCapacity(dto.getStorageCapacity());
        buyer.setTransportCapacity(dto.getTransportCapacity());
        buyer.setQualityStandards(dto.getQualityStandards());
        buyer.setCertificationsRequired(dto.getCertificationsRequired());
        buyer.setSeasonalDemand(dto.getSeasonalDemand());
        buyer.setGeographicalCoverage(dto.getGeographicalCoverage());
        buyer.setEstablishedYear(dto.getEstablishedYear());
        buyer.setAnnualVolume(dto.getAnnualVolume());
        buyer.setRating(dto.getRating());
        buyer.setVerified(dto.getVerified());
        return buyer;
    }
}