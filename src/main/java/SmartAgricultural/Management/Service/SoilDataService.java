package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.SoilData;
import SmartAgricultural.Management.Model.SoilData.PhLevel;
import SmartAgricultural.Management.Model.SoilData.NutrientLevel;
import SmartAgricultural.Management.Repository.SoilDataRepository;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.exception.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class SoilDataService {

    @Autowired
    private SoilDataRepository soilDataRepository;

    // ===== BASIC CRUD OPERATIONS =====

    public SoilData save(SoilData soilData) {
        validateSoilData(soilData);
        return soilDataRepository.save(soilData);
    }

    public SoilData create(SoilData soilData) {
        if (soilData.getId() != null) {
            throw new ValidationException("Cannot create soil data with existing ID");
        }

        if (StringUtils.hasText(soilData.getSampleCode()) &&
                existsBySampleCode(soilData.getSampleCode())) {
            throw new ValidationException("Sample code already exists: " + soilData.getSampleCode());
        }

        return save(soilData);
    }

    public SoilData update(String id, SoilData soilData) {
        SoilData existing = findById(id);
        updateSoilDataFields(existing, soilData);
        return save(existing);
    }

    @Transactional(readOnly = true)
    public SoilData findById(String id) {
        return soilDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Soil data not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<SoilData> findByIdOptional(String id) {
        return soilDataRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public SoilData findBySampleCode(String sampleCode) {
        return soilDataRepository.findBySampleCode(sampleCode)
                .orElseThrow(() -> new ResourceNotFoundException("Soil data not found with sample code: " + sampleCode));
    }

    @Transactional(readOnly = true)
    public List<SoilData> findAll() {
        return soilDataRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<SoilData> findAll(Pageable pageable) {
        return soilDataRepository.findAll(pageable);
    }

    public void deleteById(String id) {
        if (!soilDataRepository.existsById(id)) {
            throw new ResourceNotFoundException("Soil data not found with id: " + id);
        }
        soilDataRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(String id) {
        return soilDataRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsBySampleCode(String sampleCode) {
        return soilDataRepository.existsBySampleCode(sampleCode);
    }

    @Transactional(readOnly = true)
    public long count() {
        return soilDataRepository.count();
    }

    // ===== FARM-SPECIFIC OPERATIONS =====

    @Transactional(readOnly = true)
    public List<SoilData> findByFarmId(String farmId) {
        return soilDataRepository.findByFarmId(farmId);
    }

    @Transactional(readOnly = true)
    public Page<SoilData> findByFarmId(String farmId, Pageable pageable) {
        return soilDataRepository.findByFarmId(farmId, pageable);
    }

    @Transactional(readOnly = true)
    public long countByFarmId(String farmId) {
        return soilDataRepository.countByFarmId(farmId);
    }

    @Transactional(readOnly = true)
    public Optional<SoilData> findLatestByFarmId(String farmId) {
        return soilDataRepository.findLatestByFarmId(farmId);
    }

    @Transactional(readOnly = true)
    public Optional<SoilData> findLatestByFarmIdAndDepth(String farmId, Integer depthCm) {
        return soilDataRepository.findLatestByFarmIdAndDepth(farmId, depthCm);
    }

    @Transactional(readOnly = true)
    public List<SoilData> findByFarmIdOrderByMeasurementDateDesc(String farmId) {
        return soilDataRepository.findByFarmIdOrderByMeasurementDateDesc(farmId);
    }

    // ===== DATE-BASED OPERATIONS =====

    @Transactional(readOnly = true)
    public List<SoilData> findByMeasurementDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return soilDataRepository.findByMeasurementDateBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<SoilData> findByFarmIdAndMeasurementDateBetween(
            String farmId, LocalDateTime startDate, LocalDateTime endDate) {
        return soilDataRepository.findByFarmIdAndMeasurementDateBetween(farmId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<SoilData> findRecentByFarmId(String farmId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return soilDataRepository.findRecentByFarmId(farmId, since);
    }

    @Transactional(readOnly = true)
    public List<SoilData> findTrendDataByFarmIdAndDepth(String farmId, Integer depthCm) {
        return soilDataRepository.findTrendDataByFarmIdAndDepth(farmId, depthCm);
    }

    @Transactional(readOnly = true)
    public List<SoilData> findTrendDataByFarmIdAndDateRange(
            String farmId, LocalDateTime startDate, LocalDateTime endDate) {
        return soilDataRepository.findTrendDataByFarmIdAndDateRange(farmId, startDate, endDate);
    }

    // ===== TEST DUE OPERATIONS =====

    @Transactional(readOnly = true)
    public List<SoilData> findTestsDue() {
        return soilDataRepository.findTestsDue();
    }

    @Transactional(readOnly = true)
    public List<SoilData> findTestsDueByFarmId(String farmId) {
        return soilDataRepository.findTestsDueByFarmId(farmId);
    }

    @Transactional(readOnly = true)
    public List<SoilData> findOverdueTests() {
        return soilDataRepository.findOverdueTests();
    }

    @Transactional(readOnly = true)
    public List<SoilData> findOverdueTestsByFarmId(String farmId) {
        return soilDataRepository.findOverdueTestsByFarmId(farmId);
    }

    @Transactional(readOnly = true)
    public List<SoilData> findUpcomingTests(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(daysAhead);
        return soilDataRepository.findUpcomingTests(today, futureDate);
    }

    @Transactional(readOnly = true)
    public long countTestsDueByFarmId(String farmId) {
        return soilDataRepository.countTestsDueByFarmId(farmId);
    }

    @Transactional(readOnly = true)
    public long countOverdueTestsByFarmId(String farmId) {
        return soilDataRepository.countOverdueTestsByFarmId(farmId);
    }

    // ===== PH LEVEL OPERATIONS =====

    @Transactional(readOnly = true)
    public List<SoilData> findByPhLevelBetween(BigDecimal minPh, BigDecimal maxPh) {
        return soilDataRepository.findByPhLevelBetween(minPh, maxPh);
    }

    @Transactional(readOnly = true)
    public List<SoilData> findAcidicSoils(BigDecimal acidPh) {
        return soilDataRepository.findAcidicSoils(acidPh != null ? acidPh : new BigDecimal("6.0"));
    }

    @Transactional(readOnly = true)
    public List<SoilData> findAlkalineSoils(BigDecimal alkalinePh) {
        return soilDataRepository.findAlkalineSoils(alkalinePh != null ? alkalinePh : new BigDecimal("7.5"));
    }

    @Transactional(readOnly = true)
    public List<SoilData> findOptimalPhSoilsByFarmId(String farmId) {
        return soilDataRepository.findOptimalPhSoilsByFarmId(farmId);
    }

    // ===== NUTRIENT LEVEL OPERATIONS =====

    @Transactional(readOnly = true)
    public List<SoilData> findLowNutrientSoils() {
        return soilDataRepository.findLowNutrientSoils(
                new BigDecimal("40"), new BigDecimal("25"), new BigDecimal("150"));
    }

    @Transactional(readOnly = true)
    public List<SoilData> findLowNutrientSoilsByFarmId(String farmId) {
        return soilDataRepository.findLowNutrientSoilsByFarmId(
                farmId, new BigDecimal("40"), new BigDecimal("25"), new BigDecimal("150"));
    }

    @Transactional(readOnly = true)
    public List<SoilData> findByNitrogenLevel(NutrientLevel level, String farmId) {
        return soilDataRepository.findByFarmId(farmId).stream()
                .filter(s -> s.getNitrogenLevel() == level)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SoilData> findByPhosphorusLevel(NutrientLevel level, String farmId) {
        return soilDataRepository.findByFarmId(farmId).stream()
                .filter(s -> s.getPhosphorusLevel() == level)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SoilData> findByPotassiumLevel(NutrientLevel level, String farmId) {
        return soilDataRepository.findByFarmId(farmId).stream()
                .filter(s -> s.getPotassiumLevel() == level)
                .toList();
    }

    @Transactional(readOnly = true)
    public long countLowNutrientSoilsByFarmId(String farmId) {
        return soilDataRepository.countLowNutrientSoilsByFarmId(
                farmId, new BigDecimal("40"), new BigDecimal("25"), new BigDecimal("150"));
    }

    // ===== ORGANIC MATTER OPERATIONS =====

    @Transactional(readOnly = true)
    public List<SoilData> findLowOrganicMatterSoils() {
        return soilDataRepository.findByOrganicMatterLessThan(new BigDecimal("2.0"));
    }

    @Transactional(readOnly = true)
    public List<SoilData> findLowOrganicMatterSoilsByFarmId(String farmId) {
        return soilDataRepository.findLowOrganicMatterSoilsByFarmId(farmId, new BigDecimal("2.0"));
    }

    @Transactional(readOnly = true)
    public long countLowOrganicMatterSoilsByFarmId(String farmId) {
        return soilDataRepository.countLowOrganicMatterSoilsByFarmId(farmId, new BigDecimal("2.0"));
    }

    // ===== MOISTURE OPERATIONS =====

    @Transactional(readOnly = true)
    public List<SoilData> findByMoistureBetween(BigDecimal minMoisture, BigDecimal maxMoisture) {
        return soilDataRepository.findByMoistureBetween(minMoisture, maxMoisture);
    }

    @Transactional(readOnly = true)
    public List<SoilData> findDrySoils(BigDecimal maxMoisture) {
        return soilDataRepository.findByMoistureLessThan(maxMoisture != null ? maxMoisture : new BigDecimal("20.0"));
    }

    @Transactional(readOnly = true)
    public List<SoilData> findWetSoils(BigDecimal minMoisture) {
        return soilDataRepository.findByMoistureGreaterThan(minMoisture != null ? minMoisture : new BigDecimal("60.0"));
    }

    // ===== SOIL TEXTURE OPERATIONS =====

    @Transactional(readOnly = true)
    public List<SoilData> findBySoilTexture(String soilTexture) {
        return soilDataRepository.findBySoilTexture(soilTexture);
    }

    @Transactional(readOnly = true)
    public List<SoilData> findByFarmIdAndSoilTexture(String farmId, String soilTexture) {
        return soilDataRepository.findByFarmIdAndSoilTexture(farmId, soilTexture);
    }

    @Transactional(readOnly = true)
    public List<SoilData> findBySoilTextureTypes(List<String> textureTypes) {
        return soilDataRepository.findBySoilTextureIn(textureTypes);
    }

    // ===== PHYSICAL PROPERTIES OPERATIONS =====

    @Transactional(readOnly = true)
    public List<SoilData> findPoorDrainageSoils() {
        return soilDataRepository.findPoorDrainageSoils(new BigDecimal("1.6"), new BigDecimal("40.0"));
    }

    @Transactional(readOnly = true)
    public List<SoilData> findCompactedSoils(BigDecimal maxDensity) {
        return soilDataRepository.findByBulkDensityGreaterThan(maxDensity != null ? maxDensity : new BigDecimal("1.4"));
    }

    @Transactional(readOnly = true)
    public List<SoilData> findLowPorositySoils(BigDecimal minPorosity) {
        return soilDataRepository.findByPorosityLessThan(minPorosity != null ? minPorosity : new BigDecimal("40.0"));
    }

    // ===== SALINITY OPERATIONS =====

    @Transactional(readOnly = true)
    public List<SoilData> findSalineSoils() {
        return soilDataRepository.findSalineSoils();
    }

    @Transactional(readOnly = true)
    public List<SoilData> findSalineSoilsByFarmId(String farmId) {
        return soilDataRepository.findSalineSoilsByFarmId(farmId);
    }

    @Transactional(readOnly = true)
    public long countSalineSoilsByFarmId(String farmId) {
        return soilDataRepository.countSalineSoilsByFarmId(farmId);
    }

    @Transactional(readOnly = true)
    public List<SoilData> findBySalinityLevel(BigDecimal ecThreshold) {
        return soilDataRepository.findByElectricalConductivityGreaterThan(
                ecThreshold != null ? ecThreshold : new BigDecimal("2.0"));
    }

    // ===== DEPTH OPERATIONS =====

    @Transactional(readOnly = true)
    public List<SoilData> findByDepthCm(Integer depthCm) {
        return soilDataRepository.findByDepthCm(depthCm);
    }

    @Transactional(readOnly = true)
    public List<SoilData> findByFarmIdAndDepthCm(String farmId, Integer depthCm) {
        return soilDataRepository.findByFarmIdAndDepthCm(farmId, depthCm);
    }

    @Transactional(readOnly = true)
    public List<SoilData> findByDepthRange(Integer minDepth, Integer maxDepth) {
        return soilDataRepository.findByDepthCmBetween(minDepth, maxDepth);
    }

    // ===== LABORATORY OPERATIONS =====

    @Transactional(readOnly = true)
    public List<SoilData> findByLaboratoryName(String laboratoryName) {
        return soilDataRepository.findByLaboratoryName(laboratoryName);
    }

    @Transactional(readOnly = true)
    public List<SoilData> findByTestingMethod(String testingMethod) {
        return soilDataRepository.findByTestingMethod(testingMethod);
    }

    // ===== QUALITY ASSESSMENT OPERATIONS =====

    @Transactional(readOnly = true)
    public List<SoilData> findHealthySoilsByFarmId(String farmId) {
        return soilDataRepository.findHealthySoilsByFarmId(
                farmId,
                new BigDecimal("6.0"), // minPh
                new BigDecimal("7.5"), // maxPh
                new BigDecimal("3.0"), // minOrganic
                new BigDecimal("40"),  // minN
                new BigDecimal("25"),  // minP
                new BigDecimal("150")  // minK
        );
    }

    @Transactional(readOnly = true)
    public List<SoilData> findProblematicSoilsByFarmId(String farmId) {
        return soilDataRepository.findProblematicSoilsByFarmId(
                farmId,
                new BigDecimal("20"), // lowN
                new BigDecimal("10"), // lowP
                new BigDecimal("80")  // lowK
        );
    }

    @Transactional(readOnly = true)
    public List<SoilData> findExcellentQualitySoilsByFarmId(String farmId) {
        return soilDataRepository.findExcellentQualitySoilsByFarmId(farmId);
    }

    @Transactional(readOnly = true)
    public List<SoilData> findPoorQualitySoilsByFarmId(String farmId) {
        return soilDataRepository.findPoorQualitySoilsByFarmId(farmId);
    }

    // ===== STATISTICS OPERATIONS =====

    @Transactional(readOnly = true)
    public BigDecimal getAveragePhLevelByFarmId(String farmId) {
        return soilDataRepository.getAveragePhLevelByFarmId(farmId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getAverageNitrogenByFarmId(String farmId) {
        return soilDataRepository.getAverageNitrogenByFarmId(farmId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getAveragePhosphorusByFarmId(String farmId) {
        return soilDataRepository.getAveragePhosphorusByFarmId(farmId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getAveragePotassiumByFarmId(String farmId) {
        return soilDataRepository.getAveragePotassiumByFarmId(farmId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getAverageOrganicMatterByFarmId(String farmId) {
        return soilDataRepository.getAverageOrganicMatterByFarmId(farmId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPhLevelRangeByFarmId(String farmId) {
        List<Object[]> result = soilDataRepository.getPhLevelRangeByFarmId(farmId);
        Map<String, Object> range = new HashMap<>();

        if (!result.isEmpty() && result.get(0).length >= 2) {
            Object[] row = result.get(0);
            range.put("minPh", row[0]);
            range.put("maxPh", row[1]);
        } else {
            range.put("minPh", null);
            range.put("maxPh", null);
        }

        return range;
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getSoilTextureStatistics(String farmId) {
        List<Object[]> results = soilDataRepository.countBySoilTextureAndFarmId(farmId);
        Map<String, Long> stats = new HashMap<>();

        for (Object[] result : results) {
            String texture = (String) result[0];
            Long count = (Long) result[1];
            stats.put(texture != null ? texture : "Unknown", count);
        }

        return stats;
    }

    @Transactional(readOnly = true)
    public Map<Integer, Long> getYearlyTestStatistics(String farmId) {
        List<Object[]> results = soilDataRepository.countByYearAndFarmId(farmId);
        Map<Integer, Long> stats = new HashMap<>();

        for (Object[] result : results) {
            Integer year = (Integer) result[0];
            Long count = (Long) result[1];
            stats.put(year, count);
        }

        return stats;
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getLaboratoryStatistics(String farmId) {
        List<Object[]> results = soilDataRepository.countByLaboratoryAndFarmId(farmId);
        Map<String, Long> stats = new HashMap<>();

        for (Object[] result : results) {
            String laboratory = (String) result[0];
            Long count = (Long) result[1];
            stats.put(laboratory != null ? laboratory : "Unknown", count);
        }

        return stats;
    }

    // ===== SEARCH OPERATIONS =====

    @Transactional(readOnly = true)
    public List<SoilData> searchByFarmIdAndTerm(String farmId, String searchTerm) {
        if (!StringUtils.hasText(searchTerm)) {
            return findByFarmId(farmId);
        }
        return soilDataRepository.searchByFarmIdAndTerm(farmId, searchTerm.trim());
    }

    @Transactional(readOnly = true)
    public Page<SoilData> searchByFarmIdAndTerm(String farmId, String searchTerm, Pageable pageable) {
        if (!StringUtils.hasText(searchTerm)) {
            return findByFarmId(farmId, pageable);
        }
        return soilDataRepository.searchByFarmIdAndTerm(farmId, searchTerm.trim(), pageable);
    }

    // ===== COMPREHENSIVE ANALYSIS =====

    @Transactional(readOnly = true)
    public Map<String, Object> getFarmSoilAnalysis(String farmId) {
        Map<String, Object> analysis = new HashMap<>();

        // Basic counts
        analysis.put("totalSamples", countByFarmId(farmId));
        analysis.put("testsDue", countTestsDueByFarmId(farmId));
        analysis.put("overdueTests", countOverdueTestsByFarmId(farmId));

        // Quality assessments
        analysis.put("excellentQualityCount", findExcellentQualitySoilsByFarmId(farmId).size());
        analysis.put("poorQualityCount", findPoorQualitySoilsByFarmId(farmId).size());
        analysis.put("problematicSoilsCount", findProblematicSoilsByFarmId(farmId).size());

        // Nutrient issues
        analysis.put("lowNutrientSoilsCount", countLowNutrientSoilsByFarmId(farmId));
        analysis.put("lowOrganicMatterCount", countLowOrganicMatterSoilsByFarmId(farmId));
        analysis.put("salineSoilsCount", countSalineSoilsByFarmId(farmId));

        // Averages
        analysis.put("averagePh", getAveragePhLevelByFarmId(farmId));
        analysis.put("averageNitrogen", getAverageNitrogenByFarmId(farmId));
        analysis.put("averagePhosphorus", getAveragePhosphorusByFarmId(farmId));
        analysis.put("averagePotassium", getAveragePotassiumByFarmId(farmId));
        analysis.put("averageOrganicMatter", getAverageOrganicMatterByFarmId(farmId));

        // pH range
        analysis.put("phRange", getPhLevelRangeByFarmId(farmId));

        // Statistics
        analysis.put("soilTextureStats", getSoilTextureStatistics(farmId));
        analysis.put("yearlyTestStats", getYearlyTestStatistics(farmId));
        analysis.put("laboratoryStats", getLaboratoryStatistics(farmId));

        return analysis;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSoilHealthScore(String farmId) {
        Map<String, Object> healthScore = new HashMap<>();

        List<SoilData> allSoils = findByFarmId(farmId);
        if (allSoils.isEmpty()) {
            healthScore.put("score", 0);
            healthScore.put("grade", "No Data");
            healthScore.put("recommendations", List.of("No soil data available"));
            return healthScore;
        }

        int totalScore = 0;
        int sampleCount = 0;

        for (SoilData soil : allSoils) {
            String qualityScore = soil.getSoilQualityScore();
            switch (qualityScore) {
                case "Excellent": totalScore += 100; break;
                case "Good": totalScore += 80; break;
                case "Fair": totalScore += 60; break;
                case "Poor": totalScore += 40; break;
                case "Very Poor": totalScore += 20; break;
                default: continue; // Skip incomplete data
            }
            sampleCount++;
        }

        if (sampleCount == 0) {
            healthScore.put("score", 0);
            healthScore.put("grade", "Insufficient Data");
            return healthScore;
        }

        int averageScore = totalScore / sampleCount;
        healthScore.put("score", averageScore);

        if (averageScore >= 85) healthScore.put("grade", "Excellent");
        else if (averageScore >= 70) healthScore.put("grade", "Good");
        else if (averageScore >= 55) healthScore.put("grade", "Fair");
        else if (averageScore >= 40) healthScore.put("grade", "Poor");
        else healthScore.put("grade", "Very Poor");

        return healthScore;
    }

    // ===== PAGINATION HELPERS =====

    @Transactional(readOnly = true)
    public Page<SoilData> findByFarmIdPaged(String farmId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "measurementDate"));
        return findByFarmId(farmId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<SoilData> findRecentSoilDataPaged(String farmId, int page, int size, int daysBack) {
        LocalDateTime since = LocalDateTime.now().minusDays(daysBack);
        List<SoilData> recentData = soilDataRepository.findRecentByFarmId(farmId, since);

        // Manual pagination for filtered results
        int start = page * size;
        int end = Math.min(start + size, recentData.size());
        List<SoilData> pageContent = recentData.subList(start, end);

        return new org.springframework.data.domain.PageImpl<>(
                pageContent,
                PageRequest.of(page, size),
                recentData.size()
        );
    }

    // ===== BULK OPERATIONS =====

    public List<SoilData> createSoilDataBatch(List<SoilData> soilDataList) {
        for (SoilData soilData : soilDataList) {
            validateSoilData(soilData);
        }
        return soilDataRepository.saveAll(soilDataList);
    }

    public List<SoilData> updateSoilDataBatch(List<SoilData> soilDataList) {
        List<SoilData> updatedSoilData = soilDataList.stream()
                .map(soilData -> {
                    if (soilData.getId() == null) {
                        throw new ValidationException("Cannot update soil data without ID");
                    }
                    validateSoilData(soilData);
                    return soilData;
                })
                .toList();

        return soilDataRepository.saveAll(updatedSoilData);
    }

    public void deleteSoilDataBatch(List<String> ids) {
        List<SoilData> soilDataList = ids.stream()
                .map(this::findById)
                .toList();

        soilDataRepository.deleteAll(soilDataList);
    }

    // ===== MAINTENANCE OPERATIONS =====

    public int deleteOldRecords(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        return soilDataRepository.deleteOldRecords(cutoffDate);
    }

    public List<SoilData> updateNextTestDueDates() {
        List<SoilData> allSoilData = soilDataRepository.findAll();
        List<SoilData> updated = new ArrayList<>();

        for (SoilData soilData : allSoilData) {
            if (soilData.getNextTestDue() == null && soilData.getMeasurementDate() != null) {
                soilData.setNextTestDue(soilData.getMeasurementDate().toLocalDate().plusYears(1));
                updated.add(soilData);
            }
        }

        return soilDataRepository.saveAll(updated);
    }

    // ===== VALIDATION METHODS =====

    private void validateSoilData(SoilData soilData) {
        if (!StringUtils.hasText(soilData.getFarmId())) {
            throw new ValidationException("Farm ID is required");
        }

        if (soilData.getPhLevel() == null) {
            throw new ValidationException("pH level is required");
        }

        if (soilData.getMeasurementDate() == null) {
            throw new ValidationException("Measurement date is required");
        }

        if (soilData.getPhLevel().compareTo(BigDecimal.ZERO) < 0 ||
                soilData.getPhLevel().compareTo(new BigDecimal("14")) > 0) {
            throw new ValidationException("pH level must be between 0 and 14");
        }

        if (soilData.getNitrogen() != null && soilData.getNitrogen().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Nitrogen level cannot be negative");
        }

        if (soilData.getPhosphorus() != null && soilData.getPhosphorus().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Phosphorus level cannot be negative");
        }

        if (soilData.getPotassium() != null && soilData.getPotassium().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Potassium level cannot be negative");
        }

        if (soilData.getOrganicMatter() != null &&
                (soilData.getOrganicMatter().compareTo(BigDecimal.ZERO) < 0 ||
                        soilData.getOrganicMatter().compareTo(new BigDecimal("100")) > 0)) {
            throw new ValidationException("Organic matter must be between 0 and 100%");
        }

        if (soilData.getMoisture() != null &&
                (soilData.getMoisture().compareTo(BigDecimal.ZERO) < 0 ||
                        soilData.getMoisture().compareTo(new BigDecimal("100")) > 0)) {
            throw new ValidationException("Moisture must be between 0 and 100%");
        }

        if (soilData.getDepthCm() != null &&
                (soilData.getDepthCm() < 1 || soilData.getDepthCm() > 500)) {
            throw new ValidationException("Depth must be between 1 and 500 cm");
        }

        if (soilData.getBulkDensity() != null &&
                (soilData.getBulkDensity().compareTo(BigDecimal.ZERO) <= 0 ||
                        soilData.getBulkDensity().compareTo(new BigDecimal("5")) > 0)) {
            throw new ValidationException("Bulk density must be between 0 and 5 g/cm³");
        }

        if (soilData.getPorosity() != null &&
                (soilData.getPorosity().compareTo(BigDecimal.ZERO) < 0 ||
                        soilData.getPorosity().compareTo(new BigDecimal("100")) > 0)) {
            throw new ValidationException("Porosity must be between 0 and 100%");
        }

        if (soilData.getElectricalConductivity() != null &&
                soilData.getElectricalConductivity().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Electrical conductivity cannot be negative");
        }

        if (soilData.getCationExchangeCapacity() != null &&
                soilData.getCationExchangeCapacity().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Cation exchange capacity cannot be negative");
        }
    }

    private void updateSoilDataFields(SoilData existing, SoilData updated) {
        if (updated.getFarmId() != null) {
            existing.setFarmId(updated.getFarmId());
        }

        if (StringUtils.hasText(updated.getSampleCode())) {
            existing.setSampleCode(updated.getSampleCode());
        }

        if (updated.getPhLevel() != null) {
            existing.setPhLevel(updated.getPhLevel());
        }

        if (updated.getNitrogen() != null) {
            existing.setNitrogen(updated.getNitrogen());
        }

        if (updated.getPhosphorus() != null) {
            existing.setPhosphorus(updated.getPhosphorus());
        }

        if (updated.getPotassium() != null) {
            existing.setPotassium(updated.getPotassium());
        }

        if (updated.getOrganicMatter() != null) {
            existing.setOrganicMatter(updated.getOrganicMatter());
        }

        if (updated.getMoisture() != null) {
            existing.setMoisture(updated.getMoisture());
        }

        if (StringUtils.hasText(updated.getSoilTexture())) {
            existing.setSoilTexture(updated.getSoilTexture());
        }

        if (updated.getBulkDensity() != null) {
            existing.setBulkDensity(updated.getBulkDensity());
        }

        if (updated.getPorosity() != null) {
            existing.setPorosity(updated.getPorosity());
        }

        if (updated.getElectricalConductivity() != null) {
            existing.setElectricalConductivity(updated.getElectricalConductivity());
        }

        if (updated.getCationExchangeCapacity() != null) {
            existing.setCationExchangeCapacity(updated.getCationExchangeCapacity());
        }

        if (updated.getMeasurementDate() != null) {
            existing.setMeasurementDate(updated.getMeasurementDate());
        }

        if (StringUtils.hasText(updated.getTestingMethod())) {
            existing.setTestingMethod(updated.getTestingMethod());
        }

        if (StringUtils.hasText(updated.getLaboratoryName())) {
            existing.setLaboratoryName(updated.getLaboratoryName());
        }

        if (updated.getDepthCm() != null) {
            existing.setDepthCm(updated.getDepthCm());
        }

        if (StringUtils.hasText(updated.getSampleLocationGps())) {
            existing.setSampleLocationGps(updated.getSampleLocationGps());
        }

        if (StringUtils.hasText(updated.getRecommendations())) {
            existing.setRecommendations(updated.getRecommendations());
        }

        if (updated.getNextTestDue() != null) {
            existing.setNextTestDue(updated.getNextTestDue());
        }
    }


    // ===== SOIL CONDITION ASSESSMENT METHODS =====

    @Transactional(readOnly = true)
    public List<SoilData> findSoilsByCondition(String farmId, String condition) {
        List<SoilData> allSoils = findByFarmId(farmId);
        return allSoils.stream()
                .filter(soilData -> soilData.evaluateSoilCondition().getDisplayName().equalsIgnoreCase(condition))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSoilConditionStatistics(String farmId) {
        List<SoilData> soilDataList = findByFarmId(farmId);

        Map<String, Object> statistics = new HashMap<>();

        // Count by condition
        Map<String, Long> conditionCounts = soilDataList.stream()
                .collect(Collectors.groupingBy(
                        soilData -> soilData.evaluateSoilCondition().getDisplayName(),
                        Collectors.counting()
                ));

        statistics.put("conditionCounts", conditionCounts);

        // Average values by condition
        Map<String, Map<String, BigDecimal>> averagesByCondition = new HashMap<>();

        for (SoilData soilData : soilDataList) {
            String condition = soilData.evaluateSoilCondition().getDisplayName();
            Map<String, BigDecimal> averages = averagesByCondition.computeIfAbsent(condition, k -> new HashMap<>());

            // pH average
            if (soilData.getPhLevel() != null) {
                averages.merge("phLevel", soilData.getPhLevel(), (a, b) -> a.add(b));
            }

            // Nutrient averages
            if (soilData.getNitrogen() != null) {
                averages.merge("nitrogen", soilData.getNitrogen(), (a, b) -> a.add(b));
            }
            if (soilData.getPhosphorus() != null) {
                averages.merge("phosphorus", soilData.getPhosphorus(), (a, b) -> a.add(b));
            }
            if (soilData.getPotassium() != null) {
                averages.merge("potassium", soilData.getPotassium(), (a, b) -> a.add(b));
            }

            // Organic matter average
            if (soilData.getOrganicMatter() != null) {
                averages.merge("organicMatter", soilData.getOrganicMatter(), (a, b) -> a.add(b));
            }
        }

        // Calculate actual averages
        for (Map.Entry<String, Map<String, BigDecimal>> entry : averagesByCondition.entrySet()) {
            String condition = entry.getKey();
            Map<String, BigDecimal> sums = entry.getValue();
            long count = conditionCounts.getOrDefault(condition, 1L); // Avoid division by zero

            for (Map.Entry<String, BigDecimal> sumEntry : sums.entrySet()) {
                BigDecimal average = sumEntry.getValue().divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
                sums.put(sumEntry.getKey(), average);
            }
        }

        statistics.put("averagesByCondition", averagesByCondition);

        // Most common issues
        List<String> commonIssues = new ArrayList<>();
        for (SoilData soilData : soilDataList) {
            List<String> recommendations = soilData.getSoilConditionRecommendations();
            if (!recommendations.isEmpty()) {
                commonIssues.addAll(recommendations);
            }
        }

        // Count frequency of each issue
        Map<String, Long> issueFrequency = commonIssues.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        statistics.put("commonIssues", issueFrequency);

        return statistics;
    }

    // Méthode pour générer des données d'entrée pour la prédiction AI
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSoilDataForAIPrediction(String farmId) {
        List<SoilData> soilDataList = findByFarmId(farmId);
        return soilDataList.stream()
                .map(SoilData::getSoilAttributesForAI)
                .collect(Collectors.toList());
    }

    // Méthode pour évaluer l'état du sol avec recommandations détaillées
    @Transactional(readOnly = true)
    public Map<String, Object> getDetailedSoilAssessment(String id) {
        SoilData soilData = findById(id);
        Map<String, Object> assessment = new HashMap<>();

        // Basic information
        assessment.put("id", soilData.getId());
        assessment.put("sampleCode", soilData.getSampleCode());
        assessment.put("measurementDate", soilData.getMeasurementDate());

        // Soil condition
        SoilData.SoilCondition condition = soilData.evaluateSoilCondition();
        assessment.put("condition", condition.getDisplayName());
        assessment.put("conditionDescription", condition.getDescription());

        // Component analysis
        Map<String, String> componentAnalysis = new HashMap<>();

        // pH analysis
        if (soilData.getPhLevel() != null) {
            double ph = soilData.getPhLevel().doubleValue();
            componentAnalysis.put("pH",
                    ph >= 6.0 && ph <= 7.5 ? "Optimal" :
                            ph >= 5.5 && ph <= 8.0 ? "Acceptable" :
                                    ph >= 4.5 && ph <= 8.5 ? "Marginal" : "Critical");
        }

        // Nutrient analysis
        if (soilData.getNitrogen() != null) {
            double n = soilData.getNitrogen().doubleValue();
            componentAnalysis.put("Nitrogen",
                    n >= 40 && n <= 150 ? "Optimal" :
                            n >= 20 && n <= 200 ? "Acceptable" :
                                    n >= 10 ? "Low" : "Very Low");
        }

        if (soilData.getPhosphorus() != null) {
            double p = soilData.getPhosphorus().doubleValue();
            componentAnalysis.put("Phosphorus",
                    p >= 25 && p <= 100 ? "Optimal" :
                            p >= 15 && p <= 120 ? "Acceptable" :
                                    p >= 10 ? "Low" : "Very Low");
        }

        if (soilData.getPotassium() != null) {
            double k = soilData.getPotassium().doubleValue();
            componentAnalysis.put("Potassium",
                    k >= 150 && k <= 500 ? "Optimal" :
                            k >= 100 && k <= 600 ? "Acceptable" :
                                    k >= 50 ? "Low" : "Very Low");
        }

        // Organic matter analysis
        if (soilData.getOrganicMatter() != null) {
            double om = soilData.getOrganicMatter().doubleValue();
            componentAnalysis.put("OrganicMatter",
                    om >= 4.0 ? "Excellent" :
                            om >= 3.0 ? "Good" :
                                    om >= 2.0 ? "Fair" :
                                            om >= 1.0 ? "Low" : "Very Low");
        }

        // Moisture analysis
        if (soilData.getMoisture() != null) {
            double m = soilData.getMoisture().doubleValue();
            componentAnalysis.put("Moisture",
                    m >= 25 && m <= 45 ? "Optimal" :
                            m >= 20 && m <= 60 ? "Acceptable" :
                                    m >= 10 && m <= 70 ? "Marginal" : "Extreme");
        }

        // EC analysis
        if (soilData.getElectricalConductivity() != null) {
            double ec = soilData.getElectricalConductivity().doubleValue();
            componentAnalysis.put("ElectricalConductivity",
                    ec < 0.8 ? "Very Low" :
                            ec < 2.0 ? "Low" :
                                    ec < 4.0 ? "Moderate" :
                                            ec < 8.0 ? "High" : "Very High");
        }

        assessment.put("componentAnalysis", componentAnalysis);

        // Recommendations
        assessment.put("recommendations", soilData.getSoilConditionRecommendations());

        // AI prediction input
        assessment.put("aiInputAttributes", soilData.getSoilAttributesForAI());

        return assessment;
    }
}