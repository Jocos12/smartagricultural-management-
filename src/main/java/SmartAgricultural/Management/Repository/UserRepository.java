package SmartAgricultural.Management.Repository;

import SmartAgricultural.Management.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Find users by role
     */
    List<User> findByRole(User.Role role);

    /**
     * Find active users
     */
    List<User> findByIsActiveTrue();

    /**
     * Find inactive users
     */
    List<User> findByIsActiveFalse();

    /**
     * Find active users by role
     */
    List<User> findByRoleAndIsActiveTrue(User.Role role);

    /**
     * Find inactive users by role
     */
    List<User> findByRoleAndIsActiveFalse(User.Role role);

    /**
     * Count active users
     */
    long countByIsActiveTrue();

    /**
     * Count inactive users
     */
    long countByIsActiveFalse();

    /**
     * Count users by role
     */
    long countByRole(User.Role role);

    /**
     * Count active users by role
     */
    long countByRoleAndIsActiveTrue(User.Role role);

    /**
     * Count inactive users by role
     */
    long countByRoleAndIsActiveFalse(User.Role role);

    /**
     * Search users by username, email, or full name containing the query (case insensitive)
     */
    List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            String username, String email, String fullName);

    /**
     * Find users by phone number
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * Find users created after a specific date
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :date")
    List<User> findUsersCreatedAfter(@Param("date") java.time.LocalDateTime date);

    /**
     * Find users with last login after a specific date
     */
    @Query("SELECT u FROM User u WHERE u.lastLogin >= :date")
    List<User> findUsersWithLastLoginAfter(@Param("date") java.time.LocalDateTime date);

    /**
     * Find users with no last login (never logged in)
     */
    @Query("SELECT u FROM User u WHERE u.lastLogin IS NULL")
    List<User> findUsersWhoNeverLoggedIn();

    /**
     * Find users by role and active status with custom query
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = :active")
    List<User> findByRoleAndActiveStatus(@Param("role") User.Role role, @Param("active") Boolean active);

    /**
     * Search users with advanced criteria
     */
    @Query("SELECT u FROM User u WHERE " +
            "(:query IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:active IS NULL OR u.isActive = :active)")
    List<User> findWithAdvancedSearch(@Param("query") String query,
                                      @Param("role") User.Role role,
                                      @Param("active") Boolean active);

    /**
     * Find users by role list
     */
    List<User> findByRoleIn(List<User.Role> roles);

    /**
     * Find users by email domain
     */
    @Query("SELECT u FROM User u WHERE u.email LIKE CONCAT('%@', :domain)")
    List<User> findByEmailDomain(@Param("domain") String domain);

    /**
     * Get user statistics by role
     */
    @Query("SELECT u.role, COUNT(u) FROM User u GROUP BY u.role")
    List<Object[]> getUserStatisticsByRole();

    /**
     * Get user statistics by active status
     */
    @Query("SELECT u.isActive, COUNT(u) FROM User u GROUP BY u.isActive")
    List<Object[]> getUserStatisticsByActiveStatus();

    /**
     * Find users with profile image
     */
    @Query("SELECT u FROM User u WHERE u.profileImageUrl IS NOT NULL AND u.profileImageUrl != ''")
    List<User> findUsersWithProfileImage();

    /**
     * Find users without profile image
     */
    @Query("SELECT u FROM User u WHERE u.profileImageUrl IS NULL OR u.profileImageUrl = ''")
    List<User> findUsersWithoutProfileImage();

    /**
     * Find users by partial phone number
     */
    @Query("SELECT u FROM User u WHERE u.phoneNumber LIKE CONCAT('%', :phoneNumber, '%')")
    List<User> findByPhoneNumberContaining(@Param("phoneNumber") String phoneNumber);

    /**
     * Find recently registered users (last 30 days)
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :thirtyDaysAgo ORDER BY u.createdAt DESC")
    List<User> findRecentlyRegisteredUsers(@Param("thirtyDaysAgo") java.time.LocalDateTime thirtyDaysAgo);

    /**
     * Find recently active users (logged in within last 30 days)
     */
    @Query("SELECT u FROM User u WHERE u.lastLogin >= :thirtyDaysAgo ORDER BY u.lastLogin DESC")
    List<User> findRecentlyActiveUsers(@Param("thirtyDaysAgo") java.time.LocalDateTime thirtyDaysAgo);

    /**
     * Find inactive users (not logged in for more than 90 days or never logged in)
     */
    @Query("SELECT u FROM User u WHERE u.lastLogin IS NULL OR u.lastLogin < :ninetyDaysAgo")
    List<User> findInactiveUsers(@Param("ninetyDaysAgo") java.time.LocalDateTime ninetyDaysAgo);

    /**
     * Find users for admin dashboard summary
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true ORDER BY u.createdAt DESC")
    List<User> findActiveUsersForDashboard();

    /**
     * Custom query to find users with specific criteria for reports
     */
    @Query("SELECT u FROM User u WHERE " +
            "u.createdAt BETWEEN :startDate AND :endDate AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "u.isActive = :active " +
            "ORDER BY u.createdAt DESC")
    List<User> findUsersForReport(@Param("startDate") java.time.LocalDateTime startDate,
                                  @Param("endDate") java.time.LocalDateTime endDate,
                                  @Param("role") User.Role role,
                                  @Param("active") Boolean active);
}