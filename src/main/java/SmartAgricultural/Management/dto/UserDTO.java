package SmartAgricultural.Management.dto;

import SmartAgricultural.Management.Model.User;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class UserDTO {

    private String id;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String username;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "Le nom complet est obligatoire")
    private String fullName;

    @Pattern(regexp = "^[+]?[0-9\\-\\s()]*$", message = "Format de numéro de téléphone invalide")
    private String phoneNumber;

    @NotNull(message = "Le rôle est obligatoire")
    private User.Role role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean isActive;

    private LocalDateTime lastLogin;

    private String profileImageUrl;

    // Fields for password reset functionality
    private String resetToken;

    private LocalDateTime resetTokenExpiration;

    // Constructeurs
    public UserDTO() {
    }

    public UserDTO(String username, String email, String password, String fullName, User.Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.isActive = true;
    }

    // Constructor from User entity
    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.phoneNumber = user.getPhoneNumber();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.isActive = user.getIsActive();
        this.lastLogin = user.getLastLogin();
        this.profileImageUrl = user.getProfileImageUrl();
        // Note: password is intentionally not copied for security
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiration() {
        return resetTokenExpiration;
    }

    public void setResetTokenExpiration(LocalDateTime resetTokenExpiration) {
        this.resetTokenExpiration = resetTokenExpiration;
    }

    // Méthodes utilitaires
    public boolean isAdmin() {
        return this.role == User.Role.ADMIN;
    }

    public boolean isFarmer() {
        return this.role == User.Role.FARMER;
    }

    public boolean isBuyer() {
        return this.role == User.Role.BUYER;
    }

    public boolean isAnalyst() {
        return this.role == User.Role.ANALYST;
    }

    public boolean isGovernment() {
        return this.role == User.Role.GOVERNMENT;
    }

    public boolean isActive() {
        return this.isActive != null && this.isActive;
    }

    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    // Method to convert DTO to Entity
    public User toUser() {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setFullName(this.fullName);
        user.setPhoneNumber(this.phoneNumber);
        user.setRole(this.role);
        user.setCreatedAt(this.createdAt);
        user.setUpdatedAt(this.updatedAt);
        user.setIsActive(this.isActive);
        user.setLastLogin(this.lastLogin);
        user.setProfileImageUrl(this.profileImageUrl);
        return user;
    }

    // Method to create a safe DTO without sensitive information
    public UserDTO createSafeDTO() {
        UserDTO safeDTO = new UserDTO();
        safeDTO.setId(this.id);
        safeDTO.setUsername(this.username);
        safeDTO.setEmail(this.email);
        safeDTO.setFullName(this.fullName);
        safeDTO.setPhoneNumber(this.phoneNumber);
        safeDTO.setRole(this.role);
        safeDTO.setCreatedAt(this.createdAt);
        safeDTO.setUpdatedAt(this.updatedAt);
        safeDTO.setIsActive(this.isActive);
        safeDTO.setLastLogin(this.lastLogin);
        safeDTO.setProfileImageUrl(this.profileImageUrl);
        // Intentionally exclude password, resetToken, and resetTokenExpiration
        return safeDTO;
    }

    // Method to update fields from another UserDTO
    public void updateFrom(UserDTO other) {
        if (other.getUsername() != null) {
            this.username = other.getUsername();
        }
        if (other.getEmail() != null) {
            this.email = other.getEmail();
        }
        if (other.getFullName() != null) {
            this.fullName = other.getFullName();
        }
        if (other.getPhoneNumber() != null) {
            this.phoneNumber = other.getPhoneNumber();
        }
        if (other.getRole() != null) {
            this.role = other.getRole();
        }
        if (other.getIsActive() != null) {
            this.isActive = other.getIsActive();
        }
        if (other.getProfileImageUrl() != null) {
            this.profileImageUrl = other.getProfileImageUrl();
        }
        this.updatedAt = LocalDateTime.now();
    }

    // Validation methods
    public boolean isValidForCreation() {
        return username != null && !username.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() &&
                password != null && password.length() >= 8 &&
                fullName != null && !fullName.trim().isEmpty() &&
                role != null;
    }

    public boolean isValidForUpdate() {
        return username != null && !username.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() &&
                fullName != null && !fullName.trim().isEmpty() &&
                role != null;
    }

    // toString method (excluding sensitive information)
    @Override
    public String toString() {
        return "UserDTO{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", lastLogin=" + lastLogin +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                '}';
    }

    // equals and hashCode based on ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return id != null ? id.equals(userDTO.id) : userDTO.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}