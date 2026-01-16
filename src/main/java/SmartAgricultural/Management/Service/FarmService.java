package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.Farm;
import SmartAgricultural.Management.Repository.FarmRepository;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.exception.DuplicateResourceException;
import SmartAgricultural.Management.exception.InvalidDataException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
@Transactional
public class FarmService {

    private final FarmRepository farmRepository;

    @Autowired
    public FarmService(FarmRepository farmRepository) {
        this.farmRepository = farmRepository;
    }

    // Create operations
    public Farm createFarm(Farm farm) {
        validateFarm(farm);

        // Check if farm code already exists
        if (StringUtils.hasText(farm.getFarmCode()) &&
                farmRepository.existsByFarmCode(farm.getFarmCode())) {
            throw new DuplicateResourceException("Farm with code " + farm.getFarmCode() + " already exists");
        }

        farm.setCreatedAt(LocalDateTime.now());
        farm.setUpdatedAt(LocalDateTime.now());

        return farmRepository.save(farm);
    }

    public List<Farm> createMultipleFarms(List<Farm> farms) {
        for (Farm farm : farms) {
            validateFarm(farm);
        }
        return farmRepository.saveAll(farms);
    }

    // Read operations
    @Transactional(readOnly = true)
    public List<Farm> getAllFarms() {
        return farmRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Farm> getAllFarms(Pageable pageable) {
        return farmRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Farm getFarmById(String id) {
        return farmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Farm> getFarmByIdOptional(String id) {
        return farmRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Farm> getFarmsByFarmerId(String farmerId) {
        return farmRepository.findByFarmerId(farmerId);
    }

    @Transactional(readOnly = true)
    public Page<Farm> getFarmsByFarmerId(String farmerId, Pageable pageable) {
        return farmRepository.findByFarmerId(farmerId, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Farm> getFarmByCode(String farmCode) {
        return farmRepository.findByFarmCode(farmCode);
    }

    @Transactional(readOnly = true)
    public List<Farm> getFarmsByName(String farmName) {
        return farmRepository.findByFarmNameContainingIgnoreCase(farmName);
    }

    @Transactional(readOnly = true)
    public List<Farm> getFarmsBySoilType(String soilType) {
        return farmRepository.findBySoilTypeIgnoreCase(soilType);
    }

    @Transactional(readOnly = true)
    public Page<Farm> getFarmsBySoilType(String soilType, Pageable pageable) {
        return farmRepository.findBySoilTypeIgnoreCase(soilType, pageable);
    }

    @Transactional(readOnly = true)
    public List<Farm> getFarmsByIrrigationSystem(Farm.IrrigationSystem irrigationSystem) {
        return farmRepository.findByIrrigationSystem(irrigationSystem);
    }

    @Transactional(readOnly = true)
    public List<Farm> getFarmsByElectricityAvailability(Boolean electricityAvailable) {
        return farmRepository.findByElectricityAvailable(electricityAvailable);
    }

    @Transactional(readOnly = true)
    public List<Farm> getFarmsByTopography(String topography) {
        return farmRepository.findByTopography(topography);
    }

    @Transactional(readOnly = true)
    public List<Farm> getFarmsBySizeRange(BigDecimal minSize, BigDecimal maxSize) {
        validateSizeRange(minSize, maxSize);
        return farmRepository.findByFarmSizeBetween(minSize, maxSize);
    }

    @Transactional(readOnly = true)
    public Page<Farm> getFarmsBySizeRange(BigDecimal minSize, BigDecimal maxSize, Pageable pageable) {
        validateSizeRange(minSize, maxSize);
        return farmRepository.findByFarmSizeBetween(minSize, maxSize, pageable);
    }

    @Transactional(readOnly = true)
    public List<Farm> getLargeFarms() {
        return farmRepository.findLargeFarms();
    }

    @Transactional(readOnly = true)
    public List<Farm> getSmallFarms() {
        return farmRepository.findSmallFarms();
    }

    @Transactional(readOnly = true)
    public List<Farm> getMediumFarms() {
        return farmRepository.findMediumFarms();
    }

    @Transactional(readOnly = true)
    public List<Farm> getFarmsWithinCoordinates(BigDecimal minLat, BigDecimal maxLat,
                                                BigDecimal minLon, BigDecimal maxLon) {
        validateCoordinateRange(minLat, maxLat, minLon, maxLon);
        return farmRepository.findFarmsWithinCoordinates(minLat, maxLat, minLon, maxLon);
    }

    @Transactional(readOnly = true)
    public List<Farm> getFarmsNearLocation(BigDecimal latitude, BigDecimal longitude, Double radiusKm) {
        if (radiusKm == null || radiusKm <= 0) {
            throw new InvalidDataException("Radius must be positive");
        }
        return farmRepository.findFarmsNearLocation(latitude, longitude, radiusKm);
    }

    @Transactional(readOnly = true)
    public Page<Farm> searchFarms(String farmerId, String soilType,
                                  Farm.IrrigationSystem irrigationSystem,
                                  Boolean electricityAvailable, BigDecimal minSize,
                                  BigDecimal maxSize, Pageable pageable) {
        return farmRepository.findFarmsByCriteria(farmerId, soilType, irrigationSystem,
                electricityAvailable, minSize, maxSize, pageable);
    }

    // Update operations
    public Farm updateFarm(String id, Farm updatedFarm) {
        Farm existingFarm = getFarmById(id);

        validateFarm(updatedFarm);

        // Check if farm code is being changed and if new code already exists
        if (StringUtils.hasText(updatedFarm.getFarmCode()) &&
                !updatedFarm.getFarmCode().equals(existingFarm.getFarmCode()) &&
                farmRepository.existsByFarmCode(updatedFarm.getFarmCode())) {
            throw new DuplicateResourceException("Farm with code " + updatedFarm.getFarmCode() + " already exists");
        }

        // Update fields
        existingFarm.setFarmerId(updatedFarm.getFarmerId());
        existingFarm.setFarmName(updatedFarm.getFarmName());
        existingFarm.setFarmCode(updatedFarm.getFarmCode());
        existingFarm.setFarmSize(updatedFarm.getFarmSize());
        existingFarm.setSoilType(updatedFarm.getSoilType());
        existingFarm.setLatitude(updatedFarm.getLatitude());
        existingFarm.setLongitude(updatedFarm.getLongitude());
        existingFarm.setAltitude(updatedFarm.getAltitude());
        existingFarm.setIrrigationSystem(updatedFarm.getIrrigationSystem());
        existingFarm.setTopography(updatedFarm.getTopography());
        existingFarm.setWaterSource(updatedFarm.getWaterSource());
        existingFarm.setElectricityAvailable(updatedFarm.getElectricityAvailable());
        existingFarm.setRoadAccessQuality(updatedFarm.getRoadAccessQuality());
        existingFarm.setUpdatedAt(LocalDateTime.now());

        return farmRepository.save(existingFarm);
    }

    public Farm partialUpdateFarm(String id, Farm partialUpdate) {
        Farm existingFarm = getFarmById(id);

        // Update only non-null fields
        if (StringUtils.hasText(partialUpdate.getFarmerId())) {
            existingFarm.setFarmerId(partialUpdate.getFarmerId());
        }
        if (StringUtils.hasText(partialUpdate.getFarmName())) {
            existingFarm.setFarmName(partialUpdate.getFarmName());
        }
        if (StringUtils.hasText(partialUpdate.getFarmCode())) {
            if (!partialUpdate.getFarmCode().equals(existingFarm.getFarmCode()) &&
                    farmRepository.existsByFarmCode(partialUpdate.getFarmCode())) {
                throw new DuplicateResourceException("Farm with code " + partialUpdate.getFarmCode() + " already exists");
            }
            existingFarm.setFarmCode(partialUpdate.getFarmCode());
        }
        if (partialUpdate.getFarmSize() != null) {
            existingFarm.setFarmSize(partialUpdate.getFarmSize());
        }
        if (StringUtils.hasText(partialUpdate.getSoilType())) {
            existingFarm.setSoilType(partialUpdate.getSoilType());
        }
        if (partialUpdate.getLatitude() != null) {
            existingFarm.setLatitude(partialUpdate.getLatitude());
        }
        if (partialUpdate.getLongitude() != null) {
            existingFarm.setLongitude(partialUpdate.getLongitude());
        }
        if (partialUpdate.getAltitude() != null) {
            existingFarm.setAltitude(partialUpdate.getAltitude());
        }
        if (partialUpdate.getIrrigationSystem() != null) {
            existingFarm.setIrrigationSystem(partialUpdate.getIrrigationSystem());
        }
        if (StringUtils.hasText(partialUpdate.getTopography())) {
            existingFarm.setTopography(partialUpdate.getTopography());
        }
        if (StringUtils.hasText(partialUpdate.getWaterSource())) {
            existingFarm.setWaterSource(partialUpdate.getWaterSource());
        }
        if (partialUpdate.getElectricityAvailable() != null) {
            existingFarm.setElectricityAvailable(partialUpdate.getElectricityAvailable());
        }
        if (partialUpdate.getRoadAccessQuality() != null) {
            existingFarm.setRoadAccessQuality(partialUpdate.getRoadAccessQuality());
        }

        existingFarm.setUpdatedAt(LocalDateTime.now());
        return farmRepository.save(existingFarm);
    }

    // Delete operations
    public void deleteFarm(String id) {
        Farm farm = getFarmById(id);
        farmRepository.delete(farm);
    }

    public void deleteFarmsByFarmerId(String farmerId) {
        List<Farm> farms = farmRepository.findByFarmerId(farmerId);
        if (!farms.isEmpty()) {
            farmRepository.deleteByFarmerId(farmerId);
        }
    }

    public void deleteAllFarms() {
        farmRepository.deleteAll();
    }

    // Statistical and utility methods
    @Transactional(readOnly = true)
    public Long getTotalFarmsCount() {
        return farmRepository.getTotalFarmsCount();
    }

    @Transactional(readOnly = true)
    public Long getFarmsCountByFarmerId(String farmerId) {
        return farmRepository.countFarmsByFarmerId(farmerId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalFarmsArea() {
        return farmRepository.getTotalFarmsArea();
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalAreaByFarmerId(String farmerId) {
        return farmRepository.getTotalFarmSizeByFarmerId(farmerId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getAverageFarmSize() {
        return farmRepository.getAverageFarmSize();
    }

    @Transactional(readOnly = true)
    public BigDecimal getAverageFarmSizeByFarmerId(String farmerId) {
        return farmRepository.getAverageFarmSizeByFarmerId(farmerId);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getFarmCountBySoilType() {
        List<Object[]> results = farmRepository.getFarmCountBySoilType();
        Map<String, Long> soilTypeMap = new HashMap<>();
        for (Object[] result : results) {
            soilTypeMap.put((String) result[0], (Long) result[1]);
        }
        return soilTypeMap;
    }

    @Transactional(readOnly = true)
    public Map<Farm.IrrigationSystem, Long> getFarmCountByIrrigationSystem() {
        List<Object[]> results = farmRepository.getFarmCountByIrrigationSystem();
        Map<Farm.IrrigationSystem, Long> irrigationMap = new HashMap<>();
        for (Object[] result : results) {
            irrigationMap.put((Farm.IrrigationSystem) result[0], (Long) result[1]);
        }
        return irrigationMap;
    }

    @Transactional(readOnly = true)
    public boolean farmExists(String id) {
        return farmRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean farmCodeExists(String farmCode) {
        return farmRepository.existsByFarmCode(farmCode);
    }

    @Transactional(readOnly = true)
    public boolean farmerHasFarms(String farmerId) {
        return farmRepository.existsByFarmerId(farmerId);
    }

    // Validation methods
    private void validateFarm(Farm farm) {
        if (farm == null) {
            throw new InvalidDataException("Farm cannot be null");
        }

        if (!StringUtils.hasText(farm.getFarmerId())) {
            throw new InvalidDataException("Farmer ID is required");
        }

        if (!StringUtils.hasText(farm.getFarmName())) {
            throw new InvalidDataException("Farm name is required");
        }

        if (farm.getFarmSize() == null || farm.getFarmSize().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDataException("Farm size must be greater than 0");
        }

        if (!StringUtils.hasText(farm.getSoilType())) {
            throw new InvalidDataException("Soil type is required");
        }

        if (farm.getLatitude() == null || farm.getLongitude() == null) {
            throw new InvalidDataException("Latitude and longitude are required");
        }

        validateCoordinates(farm.getLatitude(), farm.getLongitude());
    }

    private void validateCoordinates(BigDecimal latitude, BigDecimal longitude) {
        if (latitude.compareTo(new BigDecimal("-90")) < 0 || latitude.compareTo(new BigDecimal("90")) > 0) {
            throw new InvalidDataException("Latitude must be between -90 and 90");
        }

        if (longitude.compareTo(new BigDecimal("-180")) < 0 || longitude.compareTo(new BigDecimal("180")) > 0) {
            throw new InvalidDataException("Longitude must be between -180 and 180");
        }
    }

    private void validateSizeRange(BigDecimal minSize, BigDecimal maxSize) {
        if (minSize != null && maxSize != null && minSize.compareTo(maxSize) > 0) {
            throw new InvalidDataException("Minimum size cannot be greater than maximum size");
        }
    }

    private void validateCoordinateRange(BigDecimal minLat, BigDecimal maxLat,
                                         BigDecimal minLon, BigDecimal maxLon) {
        if (minLat.compareTo(maxLat) > 0) {
            throw new InvalidDataException("Minimum latitude cannot be greater than maximum latitude");
        }
        if (minLon.compareTo(maxLon) > 0) {
            throw new InvalidDataException("Minimum longitude cannot be greater than maximum longitude");
        }
    }


    // ✅ AJOUTER cette méthode à la fin de la classe FarmService

    /**
     * Get all farm IDs for a specific farmer
     */
    @Transactional(readOnly = true)
    public List<String> getFarmIdsByFarmerId(String farmerId) {
        return farmRepository.findByFarmerId(farmerId)
                .stream()
                .map(Farm::getId)
                .collect(java.util.stream.Collectors.toList());
    }
}