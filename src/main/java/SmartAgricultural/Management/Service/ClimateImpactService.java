package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.ClimateImpact;
import SmartAgricultural.Management.Repository.ClimateImpactRepository;
import SmartAgricultural.Management.dto.ClimateImpactDTO;
import SmartAgricultural.Management.dto.ClimateImpactSummaryDTO;
import SmartAgricultural.Management.dto.ClimateStatisticsDTO;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClimateImpactService {

    private static final Logger logger = LoggerFactory.getLogger(ClimateImpactService.class);

    private final ClimateImpactRepository climateImpactRepository;

    @Autowired
    public ClimateImpactService(ClimateImpactRepository climateImpactRepository) {
        this.climateImpactRepository = climateImpactRepository;
    }

    /**
     * Create a new climate impact record
     */
    public ClimateImpactDTO createClimateImpact(ClimateImpactDTO climateImpactDTO) {
        try {
            // Check if impact code already exists
            if (climateImpactDTO.getImpactCode() != null &&
                    climateImpactRepository.existsByImpactCode(climateImpactDTO.getImpactCode())) {
                throw new IllegalArgumentException("Impact code already exists: " + climateImpactDTO.getImpactCode());
            }

            ClimateImpact climateImpact = convertToEntity(climateImpactDTO);
            ClimateImpact savedImpact = climateImpactRepository.save(climateImpact);

            logger.info("Climate impact created successfully with ID: {}", savedImpact.getId());
            return convertToDTO(savedImpact);
        } catch (Exception e) {
            logger.error("Error creating climate impact: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get all climate impacts
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getAllClimateImpacts() {
        try {
            List<ClimateImpact> impacts = climateImpactRepository.findAll();
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting all climate impacts: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving climate impacts", e);
        }
    }

    /**
     * Get climate impacts with pagination
     */
    @Transactional(readOnly = true)
    public Page<ClimateImpactDTO> getAllClimateImpacts(Pageable pageable) {
        try {
            Page<ClimateImpact> impacts = climateImpactRepository.findAll(pageable);
            return impacts.map(this::convertToDTO);
        } catch (Exception e) {
            logger.error("Error getting paginated climate impacts: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving climate impacts", e);
        }
    }

    /**
     * Get climate impact by ID
     */
    @Transactional(readOnly = true)
    public ClimateImpactDTO getClimateImpactById(String id) {
        try {
            ClimateImpact impact = climateImpactRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Climate impact not found with id: " + id));
            return convertToDTO(impact);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting climate impact by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error retrieving climate impact", e);
        }
    }

    /**
     * Get climate impact by impact code
     */
    @Transactional(readOnly = true)
    public ClimateImpactDTO getClimateImpactByCode(String impactCode) {
        try {
            ClimateImpact impact = climateImpactRepository.findByImpactCode(impactCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Climate impact not found with code: " + impactCode));
            return convertToDTO(impact);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting climate impact by code {}: {}", impactCode, e.getMessage(), e);
            throw new RuntimeException("Error retrieving climate impact", e);
        }
    }

    /**
     * Update climate impact
     */
    public ClimateImpactDTO updateClimateImpact(String id, ClimateImpactDTO climateImpactDTO) {
        try {
            ClimateImpact existingImpact = climateImpactRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Climate impact not found with id: " + id));

            // Check if impact code is being changed and if it already exists
            if (climateImpactDTO.getImpactCode() != null &&
                    !existingImpact.getImpactCode().equals(climateImpactDTO.getImpactCode()) &&
                    climateImpactRepository.existsByImpactCode(climateImpactDTO.getImpactCode())) {
                throw new IllegalArgumentException("Impact code already exists: " + climateImpactDTO.getImpactCode());
            }

            updateEntityFromDTO(existingImpact, climateImpactDTO);
            ClimateImpact updatedImpact = climateImpactRepository.save(existingImpact);

            logger.info("Climate impact updated successfully with ID: {}", updatedImpact.getId());
            return convertToDTO(updatedImpact);
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating climate impact {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error updating climate impact", e);
        }
    }

    /**
     * Delete climate impact
     */
    public void deleteClimateImpact(String id) {
        try {
            ClimateImpact impact = climateImpactRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Climate impact not found with id: " + id));

            climateImpactRepository.delete(impact);
            logger.info("Climate impact deleted successfully with ID: {}", id);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting climate impact {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting climate impact", e);
        }
    }

    /**
     * Verify climate impact
     */
    public ClimateImpactDTO verifyClimateImpact(String id, String verifiedBy) {
        try {
            ClimateImpact impact = climateImpactRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Climate impact not found with id: " + id));

            impact.setVerified(true);
            impact.setVerificationDate(LocalDateTime.now());
            // Note: Add verifiedBy field to ClimateImpact entity if needed

            ClimateImpact savedImpact = climateImpactRepository.save(impact);
            logger.info("Climate impact verified successfully with ID: {}", id);

            return convertToDTO(savedImpact);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error verifying climate impact {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error verifying climate impact", e);
        }
    }

    /**
     * Get climate impacts by region
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getClimateImpactsByRegion(String region) {
        try {
            List<ClimateImpact> impacts = climateImpactRepository.findByRegion(region);
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting climate impacts by region {}: {}", region, e.getMessage(), e);
            throw new RuntimeException("Error retrieving climate impacts by region", e);
        }
    }

    /**
     * Get climate impacts by year
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getClimateImpactsByYear(Integer year) {
        try {
            List<ClimateImpact> impacts = climateImpactRepository.findByYear(year);
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting climate impacts by year {}: {}", year, e.getMessage(), e);
            throw new RuntimeException("Error retrieving climate impacts by year", e);
        }
    }

    /**
     * Get climate impacts by climate event
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getClimateImpactsByEvent(ClimateImpact.ClimateEvent event) {
        try {
            List<ClimateImpact> impacts = climateImpactRepository.findByClimateEvent(event);
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting climate impacts by event {}: {}", event, e.getMessage(), e);
            throw new RuntimeException("Error retrieving climate impacts by event", e);
        }
    }

    /**
     * Get climate impacts by season
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getClimateImpactsBySeason(ClimateImpact.Season season) {
        try {
            List<ClimateImpact> impacts = climateImpactRepository.findBySeason(season);
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting climate impacts by season {}: {}", season, e.getMessage(), e);
            throw new RuntimeException("Error retrieving climate impacts by season", e);
        }
    }

    /**
     * Get verified climate impacts
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getVerifiedClimateImpacts() {
        try {
            List<ClimateImpact> impacts = climateImpactRepository.findByVerified(true);
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting verified climate impacts: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving verified climate impacts", e);
        }
    }

    /**
     * Get unverified climate impacts
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getUnverifiedClimateImpacts() {
        try {
            List<ClimateImpact> impacts = climateImpactRepository.findByVerified(false);
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting unverified climate impacts: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving unverified climate impacts", e);
        }
    }

    /**
     * Get ongoing climate events
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getOngoingEvents() {
        try {
            List<ClimateImpact> impacts = climateImpactRepository.findOngoingEvents();
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting ongoing events: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving ongoing events", e);
        }
    }

    /**
     * Get recent climate events (last 30 days)
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getRecentEvents() {
        try {
            LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
            List<ClimateImpact> impacts = climateImpactRepository.findRecentEvents(thirtyDaysAgo);
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting recent events: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving recent events", e);
        }
    }

    /**
     * Get climate impacts by economic loss threshold
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getClimateImpactsByEconomicLoss(BigDecimal minLoss) {
        try {
            List<ClimateImpact> impacts = climateImpactRepository.findByEconomicLossGreaterThan(minLoss);
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting climate impacts by economic loss: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving climate impacts by economic loss", e);
        }
    }

    /**
     * Get high-risk climate impacts
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getHighRiskImpacts() {
        try {
            BigDecimal threshold = new BigDecimal("100000"); // 100,000 threshold
            List<ClimateImpact> impacts = climateImpactRepository.findHighRiskImpacts(threshold);
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting high-risk impacts: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving high-risk impacts", e);
        }
    }

    /**
     * Search climate impacts by keyword
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> searchClimateImpacts(String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return getAllClimateImpacts();
            }

            List<ClimateImpact> impacts = climateImpactRepository.searchByKeyword(query.trim());
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error searching climate impacts: {}", e.getMessage(), e);
            throw new RuntimeException("Error searching climate impacts", e);
        }
    }

    /**
     * Get climate impacts with filters
     */
    @Transactional(readOnly = true)
    public Page<ClimateImpactDTO> getClimateImpactsWithFilters(
            String region, String district, Integer year, ClimateImpact.Season season,
            ClimateImpact.ClimateEvent event, ClimateImpact.EventIntensity intensity,
            Boolean verified, Pageable pageable) {
        try {
            Page<ClimateImpact> impacts = climateImpactRepository.findWithFilters(
                    region, district, year, season, event, intensity, verified, pageable);
            return impacts.map(this::convertToDTO);
        } catch (Exception e) {
            logger.error("Error getting filtered climate impacts: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving filtered climate impacts", e);
        }
    }

    /**
     * Get climate impacts by crop
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getClimateImpactsByCrop(String cropId) {
        try {
            List<ClimateImpact> impacts = climateImpactRepository.findByCropId(cropId);
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting climate impacts by crop {}: {}", cropId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving climate impacts by crop", e);
        }
    }

    /**
     * Get climate impacts by reporter
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getClimateImpactsByReporter(String reporterId) {
        try {
            List<ClimateImpact> impacts = climateImpactRepository.findByReportedBy(reporterId);
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting climate impacts by reporter {}: {}", reporterId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving climate impacts by reporter", e);
        }
    }

    /**
     * Get climate impacts by date range
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getClimateImpactsByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            List<ClimateImpact> impacts = climateImpactRepository.findByEventStartDateBetween(startDate, endDate);
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting climate impacts by date range: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving climate impacts by date range", e);
        }
    }

    /**
     * Get top impacts by economic loss
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getTopImpactsByEconomicLoss(int limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit);
            List<ClimateImpact> impacts = climateImpactRepository.findTopByEconomicLoss(pageable);
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting top impacts by economic loss: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving top impacts", e);
        }
    }

    /**
     * Get top impacts by affected area
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getTopImpactsByAffectedArea(int limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit);
            List<ClimateImpact> impacts = climateImpactRepository.findTopByAffectedArea(pageable);
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting top impacts by affected area: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving top impacts", e);
        }
    }

    /**
     * Get climate statistics
     */
    @Transactional(readOnly = true)
    public ClimateStatisticsDTO getClimateStatistics() {
        try {
            ClimateStatisticsDTO stats = new ClimateStatisticsDTO();

            stats.setTotalImpacts(climateImpactRepository.count());
            stats.setVerifiedImpacts(climateImpactRepository.countVerified());
            stats.setUnverifiedImpacts(climateImpactRepository.countUnverified());

            // Get current year statistics
            int currentYear = LocalDate.now().getYear();
            stats.setCurrentYearImpacts(climateImpactRepository.countByYear(currentYear));
            stats.setCurrentYearEconomicLoss(climateImpactRepository.sumEconomicLossByYear(currentYear));

            return stats;
        } catch (Exception e) {
            logger.error("Error getting climate statistics: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving climate statistics", e);
        }
    }

    /**
     * Get yearly trends by event
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getYearlyTrendsByEvent(ClimateImpact.ClimateEvent event) {
        try {
            List<Object[]> results = climateImpactRepository.getYearlyTrendsByEvent(event);
            return results.stream()
                    .map(row -> Map.of(
                            "year", row[0],
                            "count", row[1],
                            "averageEconomicLoss", row[2]
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting yearly trends: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving yearly trends", e);
        }
    }

    /**
     * Get regional statistics by year
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRegionalStatsByYear(Integer year) {
        try {
            List<Object[]> results = climateImpactRepository.getRegionalStatsByYear(year);
            return results.stream()
                    .map(row -> Map.of(
                            "region", row[0],
                            "count", row[1],
                            "totalEconomicLoss", row[2]
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting regional statistics: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving regional statistics", e);
        }
    }

    /**
     * Get seasonal statistics by year
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSeasonalStatsByYear(Integer year) {
        try {
            List<Object[]> results = climateImpactRepository.getSeasonalStatsByYear(year);
            return results.stream()
                    .map(row -> Map.of(
                            "season", row[0],
                            "count", row[1],
                            "averageEconomicLoss", row[2]
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting seasonal statistics: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving seasonal statistics", e);
        }
    }

    /**
     * Get impacts with financial support
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getImpactsWithFinancialSupport() {
        try {
            List<ClimateImpact> impacts = climateImpactRepository.findImpactsWithFinancialSupport();
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting impacts with financial support: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving impacts with financial support", e);
        }
    }

    /**
     * Get warning effectiveness statistics
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getWarningEffectivenessStats() {
        try {
            List<Object[]> results = climateImpactRepository.getWarningEffectivenessStats();
            return results.stream()
                    .map(row -> Map.of(
                            "effectiveness", row[0],
                            "count", row[1],
                            "averageEconomicLoss", row[2]
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting warning effectiveness stats: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving warning effectiveness statistics", e);
        }
    }

    /**
     * Get response effectiveness statistics
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getResponseEffectivenessStats() {
        try {
            List<Object[]> results = climateImpactRepository.getResponseEffectivenessStats();
            return results.stream()
                    .map(row -> Map.of(
                            "effectiveness", row[0],
                            "count", row[1],
                            "averageEconomicLoss", row[2]
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting response effectiveness stats: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving response effectiveness statistics", e);
        }
    }

    /**
     * Get latest climate impacts
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getLatestClimateImpacts() {
        try {
            List<ClimateImpact> impacts = climateImpactRepository.findTop10ByOrderByCreatedAtDesc();
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting latest climate impacts: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving latest climate impacts", e);
        }
    }

    /**
     * Get climate impacts requiring emergency response
     */
    @Transactional(readOnly = true)
    public List<ClimateImpactDTO> getEmergencyResponseRequired() {
        try {
            BigDecimal emergencyThreshold = new BigDecimal("50000");
            List<ClimateImpact> impacts = climateImpactRepository.findEmergencyResponseRequired(emergencyThreshold);
            return impacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting emergency response required: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving emergency response impacts", e);
        }
    }

    /**
     * Calculate impact severity
     */
    public ClimateImpact.ImpactSeverity calculateImpactSeverity(String impactId) {
        try {
            ClimateImpact impact = climateImpactRepository.findById(impactId)
                    .orElseThrow(() -> new ResourceNotFoundException("Climate impact not found with id: " + impactId));
            return impact.calculateImpactSeverity();
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error calculating impact severity: {}", e.getMessage(), e);
            throw new RuntimeException("Error calculating impact severity", e);
        }
    }

    /**
     * Generate climate impact summary
     */
    @Transactional(readOnly = true)
    public ClimateImpactSummaryDTO generateSummary(String region, Integer year) {
        try {
            ClimateImpactSummaryDTO summary = new ClimateImpactSummaryDTO();
            summary.setRegion(region);
            summary.setYear(year);

            // Get all impacts for region and year
            List<ClimateImpact> impacts = climateImpactRepository.findWithFilters(
                    region, null, year, null, null, null, true, Pageable.unpaged()).getContent();

            if (impacts.isEmpty()) {
                summary.setTotalImpacts(0L);
                return summary;
            }

            summary.setTotalImpacts((long) impacts.size());
            summary.setTotalEconomicLoss(impacts.stream()
                    .map(ClimateImpact::getEconomicLoss)
                    .filter(loss -> loss != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));

            summary.setTotalAffectedArea(impacts.stream()
                    .map(ClimateImpact::getAffectedArea)
                    .filter(area -> area != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));

            summary.setTotalAffectedPopulation(impacts.stream()
                    .map(ClimateImpact::getAffectedPopulation)
                    .filter(pop -> pop != null)
                    .mapToLong(Integer::longValue)
                    .sum());

            // Group by event type
            Map<ClimateImpact.ClimateEvent, Long> eventCounts = impacts.stream()
                    .collect(Collectors.groupingBy(
                            ClimateImpact::getClimateEvent,
                            Collectors.counting()));
            summary.setEventTypeCounts(eventCounts);

            return summary;
        } catch (Exception e) {
            logger.error("Error generating summary: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating climate impact summary", e);
        }
    }

    /**
     * Bulk verify climate impacts
     */
    public List<ClimateImpactDTO> bulkVerifyImpacts(List<String> impactIds, String verifiedBy) {
        try {
            List<ClimateImpact> impacts = climateImpactRepository.findAllById(impactIds);

            if (impacts.size() != impactIds.size()) {
                throw new IllegalArgumentException("Some impact IDs were not found");
            }

            impacts.forEach(impact -> {
                impact.setVerified(true);
                impact.setVerificationDate(LocalDateTime.now());
            });

            List<ClimateImpact> savedImpacts = climateImpactRepository.saveAll(impacts);
            logger.info("Bulk verified {} climate impacts", savedImpacts.size());

            return savedImpacts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error bulk verifying impacts: {}", e.getMessage(), e);
            throw new RuntimeException("Error bulk verifying climate impacts", e);
        }
    }

    /**
     * Delete unverified old records
     */
    public void deleteUnverifiedOldRecords(int daysOld) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
            climateImpactRepository.deleteUnverifiedOlderThan(cutoffDate);
            logger.info("Deleted unverified climate impacts older than {} days", daysOld);
        } catch (Exception e) {
            logger.error("Error deleting old unverified records: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting old records", e);
        }
    }

    // ============== UTILITY METHODS ==============

    /**
     * Convert ClimateImpact entity to DTO
     */
    private ClimateImpactDTO convertToDTO(ClimateImpact impact) {
        ClimateImpactDTO dto = new ClimateImpactDTO();

        dto.setId(impact.getId());
        dto.setImpactCode(impact.getImpactCode());
        dto.setCropId(impact.getCropId());
        dto.setRegion(impact.getRegion());
        dto.setDistrict(impact.getDistrict());
        dto.setYear(impact.getYear());
        dto.setSeason(impact.getSeason());
        dto.setClimateEvent(impact.getClimateEvent());
        dto.setEventIntensity(impact.getEventIntensity());
        dto.setEventStartDate(impact.getEventStartDate());
        dto.setEventEndDate(impact.getEventEndDate());
        dto.setEventDurationDays(impact.getEventDurationDays());
        dto.setEventFrequency(impact.getEventFrequency());
        dto.setAffectedArea(impact.getAffectedArea());
        dto.setAffectedPopulation(impact.getAffectedPopulation());
        dto.setAffectedHouseholds(impact.getAffectedHouseholds());
        dto.setCropAreaAffected(impact.getCropAreaAffected());
        dto.setLivestockAffected(impact.getLivestockAffected());
        dto.setInfrastructureAffected(impact.getInfrastructureAffected());
        dto.setYieldImpact(impact.getYieldImpact());
        dto.setProductionLoss(impact.getProductionLoss());
        dto.setQualityImpact(impact.getQualityImpact());
        dto.setEconomicLoss(impact.getEconomicLoss());
        dto.setSocialImpact(impact.getSocialImpact());
        dto.setHealthImpact(impact.getHealthImpact());
        dto.setEnvironmentalImpact(impact.getEnvironmentalImpact());
        dto.setImmediateResponse(impact.getImmediateResponse());
        dto.setEmergencyMeasures(impact.getEmergencyMeasures());
        dto.setAdaptationStrategy(impact.getAdaptationStrategy());
        dto.setMitigationMeasures(impact.getMitigationMeasures());
        dto.setRecoveryTimeDays(impact.getRecoveryTimeDays());
        dto.setRecoveryCost(impact.getRecoveryCost());
        dto.setInsurancePayout(impact.getInsurancePayout());
        dto.setGovernmentAssistance(impact.getGovernmentAssistance());
        dto.setNgoSupport(impact.getNgoSupport());
        dto.setInternationalAid(impact.getInternationalAid());
        dto.setLessonsLearned(impact.getLessonsLearned());
        dto.setPreventionMeasures(impact.getPreventionMeasures());
        dto.setEarlyWarningEffectiveness(impact.getEarlyWarningEffectiveness());
        dto.setResponseEffectiveness(impact.getResponseEffectiveness());
        dto.setVulnerabilityFactors(impact.getVulnerabilityFactors());
        dto.setResilienceFactors(impact.getResilienceFactors());
        dto.setClimateScenario(impact.getClimateScenario());
        dto.setProbabilityRecurrence(impact.getProbabilityRecurrence());
        dto.setTrendAnalysis(impact.getTrendAnalysis());
        dto.setFutureProjections(impact.getFutureProjections());
        dto.setAdaptationRecommendations(impact.getAdaptationRecommendations());
        dto.setStakeholdersInvolved(impact.getStakeholdersInvolved());
        dto.setMediaCoverage(impact.getMediaCoverage());
        dto.setResearchStudiesConducted(impact.getResearchStudiesConducted());
        dto.setDataSources(impact.getDataSources());
        dto.setReportDate(impact.getReportDate());
        dto.setReportedBy(impact.getReportedBy());
        dto.setVerified(impact.getVerified());
        dto.setVerificationDate(impact.getVerificationDate());
        dto.setCreatedAt(impact.getCreatedAt());
        dto.setUpdatedAt(impact.getUpdatedAt());

        // Additional computed fields
        dto.setImpactSeverity(impact.calculateImpactSeverity());
        dto.setIsOngoing(impact.isOngoingEvent());
        dto.setIsRecent(impact.isRecentEvent());
        dto.setRequiresEmergencyResponse(impact.requiresEmergencyResponse());

        return dto;
    }

    /**
     * Convert DTO to ClimateImpact entity
     */
    private ClimateImpact convertToEntity(ClimateImpactDTO dto) {
        ClimateImpact impact = new ClimateImpact();

        if (dto.getId() != null) impact.setId(dto.getId());
        if (dto.getImpactCode() != null) impact.setImpactCode(dto.getImpactCode());
        impact.setCropId(dto.getCropId());
        impact.setRegion(dto.getRegion());
        impact.setDistrict(dto.getDistrict());
        impact.setYear(dto.getYear());
        impact.setSeason(dto.getSeason());
        impact.setClimateEvent(dto.getClimateEvent());
        impact.setEventIntensity(dto.getEventIntensity());
        impact.setEventStartDate(dto.getEventStartDate());
        impact.setEventEndDate(dto.getEventEndDate());
        impact.setEventDurationDays(dto.getEventDurationDays());
        impact.setEventFrequency(dto.getEventFrequency());
        impact.setAffectedArea(dto.getAffectedArea());
        impact.setAffectedPopulation(dto.getAffectedPopulation());
        impact.setAffectedHouseholds(dto.getAffectedHouseholds());
        impact.setCropAreaAffected(dto.getCropAreaAffected());
        impact.setLivestockAffected(dto.getLivestockAffected());
        impact.setInfrastructureAffected(dto.getInfrastructureAffected());
        impact.setYieldImpact(dto.getYieldImpact());
        impact.setProductionLoss(dto.getProductionLoss());
        impact.setQualityImpact(dto.getQualityImpact());
        impact.setEconomicLoss(dto.getEconomicLoss());
        impact.setSocialImpact(dto.getSocialImpact());
        impact.setHealthImpact(dto.getHealthImpact());
        impact.setEnvironmentalImpact(dto.getEnvironmentalImpact());
        impact.setImmediateResponse(dto.getImmediateResponse());
        impact.setEmergencyMeasures(dto.getEmergencyMeasures());
        impact.setAdaptationStrategy(dto.getAdaptationStrategy());
        impact.setMitigationMeasures(dto.getMitigationMeasures());
        impact.setRecoveryTimeDays(dto.getRecoveryTimeDays());
        impact.setRecoveryCost(dto.getRecoveryCost());
        impact.setInsurancePayout(dto.getInsurancePayout());
        impact.setGovernmentAssistance(dto.getGovernmentAssistance());
        impact.setNgoSupport(dto.getNgoSupport());
        impact.setInternationalAid(dto.getInternationalAid());
        impact.setLessonsLearned(dto.getLessonsLearned());
        impact.setPreventionMeasures(dto.getPreventionMeasures());
        impact.setEarlyWarningEffectiveness(dto.getEarlyWarningEffectiveness());
        impact.setResponseEffectiveness(dto.getResponseEffectiveness());
        impact.setVulnerabilityFactors(dto.getVulnerabilityFactors());
        impact.setResilienceFactors(dto.getResilienceFactors());
        impact.setClimateScenario(dto.getClimateScenario());
        impact.setProbabilityRecurrence(dto.getProbabilityRecurrence());
        impact.setTrendAnalysis(dto.getTrendAnalysis());
        impact.setFutureProjections(dto.getFutureProjections());
        impact.setAdaptationRecommendations(dto.getAdaptationRecommendations());
        impact.setStakeholdersInvolved(dto.getStakeholdersInvolved());
        impact.setMediaCoverage(dto.getMediaCoverage());
        impact.setResearchStudiesConducted(dto.getResearchStudiesConducted());
        impact.setDataSources(dto.getDataSources());
        impact.setReportDate(dto.getReportDate());
        impact.setReportedBy(dto.getReportedBy());
        impact.setVerified(dto.getVerified());
        impact.setVerificationDate(dto.getVerificationDate());

        return impact;
    }

    /**
     * Update entity from DTO
     */
    private void updateEntityFromDTO(ClimateImpact impact, ClimateImpactDTO dto) {
        if (dto.getImpactCode() != null) impact.setImpactCode(dto.getImpactCode());
        if (dto.getCropId() != null) impact.setCropId(dto.getCropId());
        if (dto.getRegion() != null) impact.setRegion(dto.getRegion());
        if (dto.getDistrict() != null) impact.setDistrict(dto.getDistrict());
        if (dto.getYear() != null) impact.setYear(dto.getYear());
        if (dto.getSeason() != null) impact.setSeason(dto.getSeason());
        if (dto.getClimateEvent() != null) impact.setClimateEvent(dto.getClimateEvent());
        if (dto.getEventIntensity() != null) impact.setEventIntensity(dto.getEventIntensity());
        if (dto.getEventStartDate() != null) impact.setEventStartDate(dto.getEventStartDate());
        if (dto.getEventEndDate() != null) impact.setEventEndDate(dto.getEventEndDate());
        if (dto.getEventDurationDays() != null) impact.setEventDurationDays(dto.getEventDurationDays());
        if (dto.getEventFrequency() != null) impact.setEventFrequency(dto.getEventFrequency());
        if (dto.getAffectedArea() != null) impact.setAffectedArea(dto.getAffectedArea());
        if (dto.getAffectedPopulation() != null) impact.setAffectedPopulation(dto.getAffectedPopulation());
        if (dto.getAffectedHouseholds() != null) impact.setAffectedHouseholds(dto.getAffectedHouseholds());
        if (dto.getCropAreaAffected() != null) impact.setCropAreaAffected(dto.getCropAreaAffected());
        if (dto.getLivestockAffected() != null) impact.setLivestockAffected(dto.getLivestockAffected());
        if (dto.getInfrastructureAffected() != null) impact.setInfrastructureAffected(dto.getInfrastructureAffected());
        if (dto.getYieldImpact() != null) impact.setYieldImpact(dto.getYieldImpact());
        if (dto.getProductionLoss() != null) impact.setProductionLoss(dto.getProductionLoss());
        if (dto.getQualityImpact() != null) impact.setQualityImpact(dto.getQualityImpact());
        if (dto.getEconomicLoss() != null) impact.setEconomicLoss(dto.getEconomicLoss());
        if (dto.getSocialImpact() != null) impact.setSocialImpact(dto.getSocialImpact());
        if (dto.getHealthImpact() != null) impact.setHealthImpact(dto.getHealthImpact());
        if (dto.getEnvironmentalImpact() != null) impact.setEnvironmentalImpact(dto.getEnvironmentalImpact());
        if (dto.getImmediateResponse() != null) impact.setImmediateResponse(dto.getImmediateResponse());
        if (dto.getEmergencyMeasures() != null) impact.setEmergencyMeasures(dto.getEmergencyMeasures());
        if (dto.getAdaptationStrategy() != null) impact.setAdaptationStrategy(dto.getAdaptationStrategy());
        if (dto.getMitigationMeasures() != null) impact.setMitigationMeasures(dto.getMitigationMeasures());
        if (dto.getRecoveryTimeDays() != null) impact.setRecoveryTimeDays(dto.getRecoveryTimeDays());
        if (dto.getRecoveryCost() != null) impact.setRecoveryCost(dto.getRecoveryCost());
        if (dto.getInsurancePayout() != null) impact.setInsurancePayout(dto.getInsurancePayout());
        if (dto.getGovernmentAssistance() != null) impact.setGovernmentAssistance(dto.getGovernmentAssistance());
        if (dto.getNgoSupport() != null) impact.setNgoSupport(dto.getNgoSupport());
        if (dto.getInternationalAid() != null) impact.setInternationalAid(dto.getInternationalAid());
        if (dto.getLessonsLearned() != null) impact.setLessonsLearned(dto.getLessonsLearned());
        if (dto.getPreventionMeasures() != null) impact.setPreventionMeasures(dto.getPreventionMeasures());
        if (dto.getEarlyWarningEffectiveness() != null) impact.setEarlyWarningEffectiveness(dto.getEarlyWarningEffectiveness());
        if (dto.getResponseEffectiveness() != null) impact.setResponseEffectiveness(dto.getResponseEffectiveness());
        if (dto.getVulnerabilityFactors() != null) impact.setVulnerabilityFactors(dto.getVulnerabilityFactors());
        if (dto.getResilienceFactors() != null) impact.setResilienceFactors(dto.getResilienceFactors());
        if (dto.getClimateScenario() != null) impact.setClimateScenario(dto.getClimateScenario());
        if (dto.getProbabilityRecurrence() != null) impact.setProbabilityRecurrence(dto.getProbabilityRecurrence());
        if (dto.getTrendAnalysis() != null) impact.setTrendAnalysis(dto.getTrendAnalysis());
        if (dto.getFutureProjections() != null) impact.setFutureProjections(dto.getFutureProjections());
        if (dto.getAdaptationRecommendations() != null) impact.setAdaptationRecommendations(dto.getAdaptationRecommendations());
        if (dto.getStakeholdersInvolved() != null) impact.setStakeholdersInvolved(dto.getStakeholdersInvolved());
        if (dto.getMediaCoverage() != null) impact.setMediaCoverage(dto.getMediaCoverage());
        if (dto.getResearchStudiesConducted() != null) impact.setResearchStudiesConducted(dto.getResearchStudiesConducted());
        if (dto.getDataSources() != null) impact.setDataSources(dto.getDataSources());
        if (dto.getReportDate() != null) impact.setReportDate(dto.getReportDate());
        if (dto.getReportedBy() != null) impact.setReportedBy(dto.getReportedBy());
        if (dto.getVerified() != null) impact.setVerified(dto.getVerified());
        if (dto.getVerificationDate() != null) impact.setVerificationDate(dto.getVerificationDate());
    }
}