package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.Farmer;
import SmartAgricultural.Management.Model.Farmer.ExperienceLevel;
import SmartAgricultural.Management.Repository.FarmerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FarmerService {

    @Autowired
    private FarmerRepository farmerRepository;

    // Basic CRUD operations
    public Farmer createFarmer(Farmer farmer) {
        validateFarmer(farmer);
        if (farmerRepository.existsByUserId(farmer.getUserId())) {
            throw new IllegalArgumentException("A farmer with this user ID already exists");
        }
        if (farmer.getFarmerCode() != null && farmerRepository.existsByFarmerCode(farmer.getFarmerCode())) {
            throw new IllegalArgumentException("A farmer with this farmer code already exists");
        }
        return farmerRepository.save(farmer);
    }

    public Optional<Farmer> findFarmerById(String id) {
        return farmerRepository.findById(id);
    }

    public Farmer getFarmerById(String id) {
        return farmerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer not found with id: " + id));
    }

    public Optional<Farmer> findFarmerByUserId(String userId) {
        return farmerRepository.findByUserId(userId);
    }

    public Farmer getFarmerByUserId(String userId) {
        return farmerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Farmer not found with user ID: " + userId));
    }

    public Optional<Farmer> findFarmerByFarmerCode(String farmerCode) {
        return farmerRepository.findByFarmerCode(farmerCode);
    }

    public Farmer getFarmerByFarmerCode(String farmerCode) {
        return farmerRepository.findByFarmerCode(farmerCode)
                .orElseThrow(() -> new RuntimeException("Farmer not found with farmer code: " + farmerCode));
    }

    public List<Farmer> findAllFarmers() {
        return farmerRepository.findAll();
    }

    public Page<Farmer> findAllFarmers(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return farmerRepository.findAll(pageable);
    }

    public Farmer updateFarmer(String id, Farmer updatedFarmer) {
        Farmer existingFarmer = getFarmerById(id);

        // Update fields (excluding ID and timestamps)
        if (updatedFarmer.getFarmerCode() != null &&
                !updatedFarmer.getFarmerCode().equals(existingFarmer.getFarmerCode()) &&
                farmerRepository.existsByFarmerCode(updatedFarmer.getFarmerCode())) {
            throw new IllegalArgumentException("A farmer with this farmer code already exists");
        }

        updateFarmerFields(existingFarmer, updatedFarmer);
        validateFarmer(existingFarmer);

        return farmerRepository.save(existingFarmer);
    }

    public void deleteFarmer(String id) {
        if (!farmerRepository.existsById(id)) {
            throw new RuntimeException("Farmer not found with id: " + id);
        }
        farmerRepository.deleteById(id);
    }

    public void deleteFarmerByUserId(String userId) {
        if (!farmerRepository.existsByUserId(userId)) {
            throw new RuntimeException("Farmer not found with user ID: " + userId);
        }
        farmerRepository.deleteByUserId(userId);
    }

    // Location-based services
    public List<Farmer> findFarmersByProvince(String province) {
        return farmerRepository.findByProvince(province);
    }

    public Page<Farmer> findFarmersByProvince(String province, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return farmerRepository.findByProvince(province, pageable);
    }

    public List<Farmer> findFarmersByDistrict(String district) {
        return farmerRepository.findByDistrict(district);
    }

    public List<Farmer> findFarmersBySector(String sector) {
        return farmerRepository.findBySector(sector);
    }

    public List<Farmer> findFarmersByLocation(String province, String district, String sector) {
        if (sector != null) {
            return farmerRepository.findByProvinceAndDistrictAndSector(province, district, sector);
        } else if (district != null) {
            return farmerRepository.findByProvinceAndDistrict(province, district);
        } else {
            return farmerRepository.findByProvince(province);
        }
    }

    public List<Farmer> findFarmersInArea(BigDecimal minLatitude, BigDecimal maxLatitude,
                                          BigDecimal minLongitude, BigDecimal maxLongitude) {
        return farmerRepository.findFarmersInArea(minLatitude, maxLatitude, minLongitude, maxLongitude);
    }

    // Experience and certification services
    public List<Farmer> findFarmersByExperienceLevel(ExperienceLevel experienceLevel) {
        return farmerRepository.findByExperienceLevel(experienceLevel);
    }

    public Page<Farmer> findFarmersByExperienceLevel(ExperienceLevel experienceLevel, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return farmerRepository.findByExperienceLevel(experienceLevel, pageable);
    }

    public List<Farmer> findCertifiedFarmers() {
        return farmerRepository.findByCertificationLevelIsNotNull();
    }

    public List<Farmer> findUnCertifiedFarmers() {
        return farmerRepository.findByCertificationLevelIsNull();
    }

    // Cooperative services
    public List<Farmer> findFarmersByCooperative(String cooperativeName) {
        return farmerRepository.findByCooperativeName(cooperativeName);
    }

    public Page<Farmer> findFarmersByCooperative(String cooperativeName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return farmerRepository.findByCooperativeName(cooperativeName, pageable);
    }

    public List<Farmer> findFarmersInCooperatives() {
        return farmerRepository.findByCooperativeNameIsNotNull();
    }

    public List<Farmer> findIndependentFarmers() {
        return farmerRepository.findByCooperativeNameIsNull();
    }

    // Land size services
    public List<Farmer> findFarmersWithLandSizeGreaterThan(BigDecimal landSize) {
        return farmerRepository.findByTotalLandSizeGreaterThan(landSize);
    }

    public List<Farmer> findFarmersWithLandSizeLessThan(BigDecimal landSize) {
        return farmerRepository.findByTotalLandSizeLessThan(landSize);
    }

    public List<Farmer> findFarmersWithLandSizeBetween(BigDecimal minSize, BigDecimal maxSize) {
        return farmerRepository.findByTotalLandSizeBetween(minSize, maxSize);
    }

    // Search services
    public List<Farmer> searchFarmersByLocation(String location) {
        return farmerRepository.findByLocationContaining(location);
    }

    public List<Farmer> searchFarmersByContactPerson(String contactPerson) {
        return farmerRepository.findByContactPersonContaining(contactPerson);
    }

    public Page<Farmer> searchFarmersByCriteria(String province, String district, String sector,
                                                ExperienceLevel experienceLevel, String cooperativeName,
                                                int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return farmerRepository.findByCriteria(province, district, sector, experienceLevel,
                cooperativeName, pageable);
    }

    // Date-based services
    public List<Farmer> findFarmersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return farmerRepository.findByCreatedAtBetween(startDate, endDate);
    }

    public List<Farmer> findFarmersCreatedAfter(LocalDateTime date) {
        return farmerRepository.findByCreatedAtAfter(date);
    }

    public List<Farmer> findFarmersCreatedBefore(LocalDateTime date) {
        return farmerRepository.findByCreatedAtBefore(date);
    }

    // Statistical services
    public Long countFarmersByExperienceLevel(ExperienceLevel experienceLevel) {
        return farmerRepository.countByExperienceLevel(experienceLevel);
    }

    public Long countFarmersByProvince(String province) {
        return farmerRepository.countByProvince(province);
    }

    public Long countFarmersWithLandSize() {
        return farmerRepository.countFarmersWithLandSize();
    }

    public Long countTotalFarmers() {
        return farmerRepository.count();
    }

    public BigDecimal getTotalLandSizeByProvince(String province) {
        return farmerRepository.getTotalLandSizeByProvince(province);
    }

    public BigDecimal getAverageLandSize() {
        return farmerRepository.getAverageLandSize();
    }

    // Advanced query services
    public List<Farmer> findFarmersByExperienceLevelAndProvince(ExperienceLevel experienceLevel, String province) {
        return farmerRepository.findByExperienceLevelAndProvince(experienceLevel, province);
    }

    public List<Farmer> findCooperativeFarmersByExperienceLevel(ExperienceLevel experienceLevel) {
        return farmerRepository.findCooperativeFarmersByExperienceLevel(experienceLevel);
    }

    // Lookup services
    public List<String> getAllProvinces() {
        return farmerRepository.findAllDistinctProvinces();
    }

    public List<String> getDistrictsByProvince(String province) {
        return farmerRepository.findDistinctDistrictsByProvince(province);
    }

    public List<String> getSectorsByProvinceAndDistrict(String province, String district) {
        return farmerRepository.findDistinctSectorsByProvinceAndDistrict(province, district);
    }

    public List<String> getAllCooperatives() {
        return farmerRepository.findAllDistinctCooperatives();
    }

    // Utility services
    public boolean existsByUserId(String userId) {
        return farmerRepository.existsByUserId(userId);
    }

    public boolean existsByFarmerCode(String farmerCode) {
        return farmerRepository.existsByFarmerCode(farmerCode);
    }

    public boolean existsById(String id) {
        return farmerRepository.existsById(id);
    }

    // Maintenance services
    public void deleteOldRecords(LocalDateTime beforeDate) {
        farmerRepository.deleteOldRecords(beforeDate);
    }

    // Private helper methods
    private void validateFarmer(Farmer farmer) {
        if (farmer == null) {
            throw new IllegalArgumentException("Farmer cannot be null");
        }
        if (farmer.getUserId() == null || farmer.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (farmer.getLocation() == null || farmer.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Location is required");
        }
        if (farmer.getLatitude() == null) {
            throw new IllegalArgumentException("Latitude is required");
        }
        if (farmer.getLongitude() == null) {
            throw new IllegalArgumentException("Longitude is required");
        }
        if (farmer.getProvince() == null || farmer.getProvince().trim().isEmpty()) {
            throw new IllegalArgumentException("Province is required");
        }
        if (farmer.getDistrict() == null || farmer.getDistrict().trim().isEmpty()) {
            throw new IllegalArgumentException("District is required");
        }
        if (farmer.getSector() == null || farmer.getSector().trim().isEmpty()) {
            throw new IllegalArgumentException("Sector is required");
        }
    }

    private void updateFarmerFields(Farmer existingFarmer, Farmer updatedFarmer) {
        if (updatedFarmer.getFarmerCode() != null) {
            existingFarmer.setFarmerCode(updatedFarmer.getFarmerCode());
        }
        if (updatedFarmer.getCooperativeName() != null) {
            existingFarmer.setCooperativeName(updatedFarmer.getCooperativeName());
        }
        if (updatedFarmer.getTotalLandSize() != null) {
            existingFarmer.setTotalLandSize(updatedFarmer.getTotalLandSize());
        }
        if (updatedFarmer.getLocation() != null) {
            existingFarmer.setLocation(updatedFarmer.getLocation());
        }
        if (updatedFarmer.getLatitude() != null) {
            existingFarmer.setLatitude(updatedFarmer.getLatitude());
        }
        if (updatedFarmer.getLongitude() != null) {
            existingFarmer.setLongitude(updatedFarmer.getLongitude());
        }
        if (updatedFarmer.getProvince() != null) {
            existingFarmer.setProvince(updatedFarmer.getProvince());
        }
        if (updatedFarmer.getDistrict() != null) {
            existingFarmer.setDistrict(updatedFarmer.getDistrict());
        }
        if (updatedFarmer.getSector() != null) {
            existingFarmer.setSector(updatedFarmer.getSector());
        }
        if (updatedFarmer.getExperienceLevel() != null) {
            existingFarmer.setExperienceLevel(updatedFarmer.getExperienceLevel());
        }
        if (updatedFarmer.getCertificationLevel() != null) {
            existingFarmer.setCertificationLevel(updatedFarmer.getCertificationLevel());
        }
        if (updatedFarmer.getContactPerson() != null) {
            existingFarmer.setContactPerson(updatedFarmer.getContactPerson());
        }
        if (updatedFarmer.getBankAccount() != null) {
            existingFarmer.setBankAccount(updatedFarmer.getBankAccount());
        }
        if (updatedFarmer.getTaxNumber() != null) {
            existingFarmer.setTaxNumber(updatedFarmer.getTaxNumber());
        }
    }
}