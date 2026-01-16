package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.User;
import SmartAgricultural.Management.dto.UserDTO;
import SmartAgricultural.Management.exception.ResourceNotFoundException;
import SmartAgricultural.Management.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a new user
     */
    public UserDTO createUser(UserDTO userDTO) {
        try {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + userDTO.getEmail());
            }

            if (userRepository.existsByUsername(userDTO.getUsername())) {
                throw new IllegalArgumentException("Username already exists: " + userDTO.getUsername());
            }

            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            user.setFullName(userDTO.getFullName());
            user.setPhoneNumber(userDTO.getPhoneNumber());
            user.setRole(userDTO.getRole());
            user.setIsActive(userDTO.getIsActive() != null ? userDTO.getIsActive() : true);
            user.setProfileImageUrl(userDTO.getProfileImageUrl());

            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }

            User savedUser = userRepository.save(user);
            logger.info("User created successfully with ID: {}", savedUser.getId());

            return convertToDTO(savedUser);
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return users.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting all users: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving users", e);
        }
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(String id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> ResourceNotFoundException.userNotFoundById(id));
            return convertToDTO(user);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting user by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error retrieving user", e);
        }
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> ResourceNotFoundException.userNotFoundByEmail(email));
            return convertToDTO(user);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting user by email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Error retrieving user", e);
        }
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> ResourceNotFoundException.userNotFoundByUsername(username));
            return convertToDTO(user);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting user by username {}: {}", username, e.getMessage(), e);
            throw new RuntimeException("Error retrieving user", e);
        }
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(User.Role role) {
        try {
            List<User> users = userRepository.findByRole(role);
            return users.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting users by role {}: {}", role, e.getMessage(), e);
            throw new RuntimeException("Error retrieving users by role", e);
        }
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getActiveUsers() {
        try {
            List<User> users = userRepository.findByIsActiveTrue();
            return users.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting active users: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving active users", e);
        }
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getActiveUsersByRole(User.Role role) {
        try {
            List<User> users = userRepository.findByRoleAndIsActiveTrue(role);
            return users.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting active users by role {}: {}", role, e.getMessage(), e);
            throw new RuntimeException("Error retrieving active users by role", e);
        }
    }

    public UserDTO updateUser(String id, UserDTO userDTO) {
        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> ResourceNotFoundException.userNotFoundById(id));

            if (!existingUser.getEmail().equals(userDTO.getEmail()) &&
                    userRepository.existsByEmail(userDTO.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + userDTO.getEmail());
            }

            if (!existingUser.getUsername().equals(userDTO.getUsername()) &&
                    userRepository.existsByUsername(userDTO.getUsername())) {
                throw new IllegalArgumentException("Username already exists: " + userDTO.getUsername());
            }

            existingUser.setUsername(userDTO.getUsername());
            existingUser.setEmail(userDTO.getEmail());
            existingUser.setFullName(userDTO.getFullName());
            existingUser.setPhoneNumber(userDTO.getPhoneNumber());
            existingUser.setRole(userDTO.getRole());

            if (userDTO.getIsActive() != null) {
                existingUser.setIsActive(userDTO.getIsActive());
            }

            if (userDTO.getProfileImageUrl() != null) {
                existingUser.setProfileImageUrl(userDTO.getProfileImageUrl());
            }

            User updatedUser = userRepository.save(existingUser);
            logger.info("User updated successfully with ID: {}", updatedUser.getId());

            return convertToDTO(updatedUser);
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating user {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error updating user", e);
        }
    }

    public void updateUserPassword(String id, String newPasswordHash) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> ResourceNotFoundException.userNotFoundById(id));

            user.setPassword(newPasswordHash);
            userRepository.save(user);

            logger.info("Password updated successfully for user ID: {}", id);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating password for user {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error updating password", e);
        }
    }

    public void updateUserStatus(String id, boolean isActive) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> ResourceNotFoundException.userNotFoundById(id));

            user.setIsActive(isActive);
            userRepository.save(user);

            logger.info("User status updated to {} for user ID: {}", isActive ? "active" : "inactive", id);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating status for user {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error updating user status", e);
        }
    }

    public void updateLastLogin(String id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> ResourceNotFoundException.userNotFoundById(id));

            user.updateLastLogin();
            userRepository.save(user);

            logger.debug("Last login updated for user ID: {}", id);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating last login for user {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error updating last login", e);
        }
    }

    public void deleteUser(String id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> ResourceNotFoundException.userNotFoundById(id));

            userRepository.delete(user);
            logger.info("User deleted successfully with ID: {}", id);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting user {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting user", e);
        }
    }

    @Transactional(readOnly = true)
    public String getPasswordHashByEmail(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> ResourceNotFoundException.userNotFoundByEmail(email));
            return user.getPassword();
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting password hash for email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Error retrieving password", e);
        }
    }

    /**
     * FIXED: Update user reset token - NOW ACTUALLY SAVES THE TOKEN!
     */
    public void updateUserResetToken(String id, String resetToken, LocalDateTime expiration) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> ResourceNotFoundException.userNotFoundById(id));

            // SET THE RESET TOKEN AND EXPIRATION
            user.setResetToken(resetToken);
            user.setResetTokenExpiration(expiration);

            // SAVE TO DATABASE
            userRepository.save(user);

            logger.info("Reset token updated for user ID: {} - Token: {}, Expiration: {}",
                    id, resetToken, expiration);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating reset token for user {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error updating reset token", e);
        }
    }

    /**
     * FIXED: Reset password with token - NOW VALIDATES AND CLEARS TOKEN PROPERLY
     */
    public void resetPasswordWithToken(String email, String resetToken, String newPasswordHash) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> ResourceNotFoundException.userNotFoundByEmail(email));

            // Validate token exists
            if (user.getResetToken() == null || user.getResetTokenExpiration() == null) {
                logger.warn("No reset token found for user: {}", email);
                throw new IllegalStateException("No active reset request found. Please request a new password reset.");
            }

            // Validate token matches
            if (!user.getResetToken().equals(resetToken)) {
                logger.warn("Invalid reset token for user: {}", email);
                throw new IllegalArgumentException("Invalid reset token.");
            }

            // Validate token not expired
            if (user.getResetTokenExpiration().isBefore(LocalDateTime.now())) {
                logger.warn("Expired reset token for user: {}", email);
                throw new IllegalStateException("Reset token has expired. Please request a new one.");
            }

            // Update password
            user.setPassword(newPasswordHash);

            // CLEAR RESET TOKEN AFTER SUCCESSFUL PASSWORD RESET
            user.clearResetToken();

            userRepository.save(user);

            logger.info("Password reset successfully for email: {}", email);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error resetting password for email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Error resetting password", e);
        }
    }

    @Transactional(readOnly = true)
    public List<UserDTO> searchUsers(String query, String role, Boolean active) {
        try {
            List<User> users;

            if (query != null && !query.trim().isEmpty()) {
                users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
                        query, query, query);
            } else {
                users = userRepository.findAll();
            }

            return users.stream()
                    .filter(user -> role == null || user.getRole().name().equals(role.toUpperCase()))
                    .filter(user -> active == null || user.getIsActive().equals(active))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error searching users: {}", e.getMessage(), e);
            throw new RuntimeException("Error searching users", e);
        }
    }

    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }

    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        return userRepository.countByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public long getInactiveUserCount() {
        return userRepository.countByIsActiveFalse();
    }

    @Transactional(readOnly = true)
    public long getUserCountByRole(User.Role role) {
        return userRepository.countByRole(role);
    }

    /**
     * UPDATED: Convert User entity to UserDTO - NOW INCLUDES RESET TOKEN FIELDS
     */
    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setFullName(user.getFullName());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setRole(user.getRole());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setIsActive(user.getIsActive());
        userDTO.setLastLogin(user.getLastLogin());
        userDTO.setProfileImageUrl(user.getProfileImageUrl());

        // Include reset token fields for internal use
        userDTO.setResetToken(user.getResetToken());
        userDTO.setResetTokenExpiration(user.getResetTokenExpiration());

        // Note: Password is intentionally not set for security
        return userDTO;
    }

    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setRole(userDTO.getRole());
        user.setIsActive(userDTO.getIsActive());
        user.setProfileImageUrl(userDTO.getProfileImageUrl());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        return user;
    }
}