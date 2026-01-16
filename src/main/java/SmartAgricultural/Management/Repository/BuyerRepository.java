package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, String> {

    // Basic queries
    Optional<Buyer> findByUserId(String userId);
    Optional<Buyer> findByBuyerCode(String buyerCode);
    List<Buyer> findByBuyerType(Buyer.BuyerType buyerType);
    List<Buyer> findByVerified(Boolean verified);
    List<Buyer> findByLocationContainingIgnoreCase(String location);

    // Rating queries
    List<Buyer> findByRatingGreaterThanEqual(BigDecimal minRating);

    @Query("SELECT AVG(b.rating) FROM Buyer b WHERE b.rating IS NOT NULL")
    Double findAverageRating();

    @Query("SELECT COUNT(b) FROM Buyer b WHERE b.rating >= :minRating")
    Long countByRatingGreaterThanEqual(@Param("minRating") BigDecimal minRating);

    // Volume queries
    @Query("SELECT b FROM Buyer b WHERE b.annualVolume > :volume")
    List<Buyer> findHighVolumeBuyers(@Param("volume") BigDecimal volume);

    // Storage capacity queries
    @Query("SELECT b FROM Buyer b WHERE b.storageCapacity > :capacity")
    List<Buyer> findLargeCapacityBuyers(@Param("capacity") BigDecimal capacity);

    // Credit queries
    @Query("SELECT b FROM Buyer b WHERE b.creditRating IN :ratings")
    List<Buyer> findByGoodCreditRatings(@Param("ratings") List<String> ratings);

    @Query("SELECT b FROM Buyer b WHERE b.creditLimit > :limit")
    List<Buyer> findByHighCreditLimit(@Param("limit") BigDecimal limit);

    // Premium buyers (high rating and verified)
    @Query("SELECT b FROM Buyer b WHERE b.rating >= :minRating AND b.verified = true")
    List<Buyer> findPremiumBuyers(@Param("minRating") BigDecimal minRating);

    // Experienced buyers
    @Query("SELECT b FROM Buyer b WHERE b.establishedYear <= :maxYear")
    List<Buyer> findExperiencedBuyers(@Param("maxYear") Integer maxYear);

    // Search queries
    @Query("SELECT b FROM Buyer b WHERE " +
            "(:query IS NULL OR " +
            "LOWER(b.companyName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.location) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.contactPerson) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "(:buyerType IS NULL OR b.buyerType = :buyerType) AND " +
            "(:verified IS NULL OR b.verified = :verified) AND " +
            "(:location IS NULL OR LOWER(b.location) LIKE LOWER(CONCAT('%', :location, '%')))")
    List<Buyer> searchBuyers(@Param("query") String query,
                             @Param("buyerType") Buyer.BuyerType buyerType,
                             @Param("verified") Boolean verified,
                             @Param("location") String location);

    // Count queries
    Long countByVerified(Boolean verified);
    Long countByBuyerType(Buyer.BuyerType buyerType);

    // Exists queries
    boolean existsByUserId(String userId);
    boolean existsByBuyerCode(String buyerCode);
}