package SmartAgricultural.Management.Controller;

import SmartAgricultural.Management.Model.User;
import SmartAgricultural.Management.Service.*;
import SmartAgricultural.Management.dto.UserDTO;
import SmartAgricultural.Management.exception.ResourceNotFoundException;

import java.util.HashMap;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final boolean DEBUG_MODE = true;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final OtpService otpService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder,
                          JwtService jwtService, EmailService emailService, OtpService otpService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.otpService = otpService;
    }

    // Structure pour la réponse de login
    private static class LoginResponse {
        public boolean success;
        public String message;
        public UserDTO user;
        public String token;
        public boolean requiresOtp;

        public LoginResponse(boolean success, String message, UserDTO user, String token, boolean requiresOtp) {
            this.success = success;
            this.message = message;
            this.user = user;
            this.token = token;
            this.requiresOtp = requiresOtp;
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registerRequest) {
        try {
            String username = registerRequest.get("username");
            String fullName = registerRequest.get("fullName");
            String email = registerRequest.get("email");
            String phoneNumber = registerRequest.get("phoneNumber");
            String password = registerRequest.get("password");
            String role = registerRequest.get("role");

            // Validation
            if (username == null || username.trim().isEmpty() ||
                    fullName == null || fullName.trim().isEmpty() ||
                    email == null || email.trim().isEmpty() ||
                    phoneNumber == null || phoneNumber.trim().isEmpty() ||
                    password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "All fields are required"));
            }

            if (password.length() < 8) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Password must be at least 8 characters long"));
            }

            // Check if email already exists
            try {
                userService.getUserByEmail(email);
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Email already exists"));
            } catch (ResourceNotFoundException e) {
                // Email doesn't exist, continue with registration
            }

            // Check if username already exists
            try {
                userService.getUserByUsername(username);
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Username already exists"));
            } catch (ResourceNotFoundException e) {
                // Username doesn't exist, continue with registration
            }

            // Utiliser le rôle envoyé depuis le frontend
            User.Role userRole;
            if (role != null && !role.trim().isEmpty()) {
                try {
                    userRole = User.Role.valueOf(role.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("success", false, "message", "Invalid role specified"));
                }
            } else {
                userRole = User.Role.FARMER;
            }

            // Create new user with the selected role
            UserDTO newUser = new UserDTO();
            newUser.setUsername(username);
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setPhoneNumber(phoneNumber);
            newUser.setPassword(password);
            newUser.setRole(userRole);
            newUser.setIsActive(true);

            UserDTO createdUser = userService.createUser(newUser);

            // ✨ AJOUT: Envoi de l'email de bienvenue
            try {
                emailService.sendBilingualWelcomeEmail(createdUser);
                logger.info("Welcome email sent successfully to: {}", createdUser.getEmail());
            } catch (Exception emailError) {
                // Log l'erreur mais ne pas bloquer l'inscription
                logger.error("Failed to send welcome email to: {}, Error: {}",
                        createdUser.getEmail(), emailError.getMessage());
            }

            // Create safe response without password
            UserDTO safeUser = createSafeUserDTO(createdUser);

            if (DEBUG_MODE) {
                logger.info("User registered successfully with role: {}", userRole);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Registration successful. Welcome email sent!",
                    "user", safeUser
            ));

        } catch (Exception e) {
            logger.error("Error during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "An error occurred processing your request"));
        }
    }




// Ajoutez cette méthode dans votre UserController.java

    @PostMapping("/generate-password-hash")
    public ResponseEntity<?> generatePasswordHash(@RequestBody Map<String, String> request) {
        try {
            String plainPassword = request.get("password");

            if (plainPassword == null || plainPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Password is required"));
            }

            String hashedPassword = passwordEncoder.encode(plainPassword);

            if (DEBUG_MODE) {
                logger.info("Generated hash for password '{}': {}", plainPassword, hashedPassword);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "plainPassword", plainPassword,
                    "hashedPassword", hashedPassword,
                    "sqlCommand", "UPDATE users SET password = '" + hashedPassword + "' WHERE email = 'your_email@domain.com';"
            ));

        } catch (Exception e) {
            logger.error("Error generating password hash", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error generating password hash"));
        }
    }




    @PostMapping(value = "/upload-profile-image/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadProfileImage(
            @PathVariable String userId,
            @RequestParam("file") MultipartFile file) {

        logger.info("=== UPLOAD PROFILE IMAGE ===");
        logger.info("User ID: {}", userId);
        logger.info("File name: {}", file.getOriginalFilename());
        logger.info("File size: {}", file.getSize());
        logger.info("File type: {}", file.getContentType());

        try {
            // Validate user exists
            UserDTO user = userService.getUserById(userId);
            logger.info("User found: {}", user.getEmail());

            // Delete old profile image file if exists
            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                if (user.getProfileImageUrl().startsWith("uploads/")) {
                    logger.info("Deleting old profile image: {}", user.getProfileImageUrl());
                    fileStorageService.deleteFile(user.getProfileImageUrl());
                }
            }

            // Store new file
            logger.info("Storing new file...");
            String filePath = fileStorageService.storeFile(file, userId);
            logger.info("File stored at: {}", filePath);

            // Update user profile image URL + data (store in DB)
            byte[] imageBytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
            }
            // FIX: Use userId in the path instead of complex naming
            String dbImageUrl = "api/users/" + userId + "/profile-image" + ext;
            userService.updateUserProfileImage(userId, dbImageUrl, imageBytes);
            logger.info("User profile image URL and data updated in database");

            // FIX: Return the correct image URL
            String imageUrl = "http://localhost:1010/" + dbImageUrl;
            logger.info("✅ Upload successful, image URL: {}", imageUrl);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Profile image uploaded successfully",
                    "imageUrl", imageUrl,
                    "filePath", filePath
            ));
        } catch (ResourceNotFoundException e) {
            logger.error("❌ User not found: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "User not found"));
        } catch (IOException e) {
            logger.error("❌ Error uploading file for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error uploading file: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ Unexpected error uploading file for user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error uploading file"));
        }
    }



    @DeleteMapping("/{userId}/profile-image")
    public ResponseEntity<Map<String, Object>> deleteProfileImage(@PathVariable String userId) {
        try {
            UserDTO user = userService.getUserById(userId);

            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                if (user.getProfileImageUrl().startsWith("uploads/")) {
                    fileStorageService.deleteFile(user.getProfileImageUrl());
                }
            }

            userService.updateUserProfileImage(userId, null, null);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Profile image deleted successfully"
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "User not found"));
        } catch (Exception e) {
            logger.error("Error deleting profile image for user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error deleting profile image"));
        }
    }

    @GetMapping(value = {"/{userId}/profile-image", "/{userId}/profile-image.{ext}"})
    public ResponseEntity<byte[]> getProfileImage(@PathVariable String userId,
                                                   @PathVariable(name = "ext", required = false) String ext) {
        try {
            UserDTO user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            byte[] imageData = userService.getProfileImageData(userId);
            if (imageData == null || imageData.length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String url = user.getProfileImageUrl();
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
            String lower = null;
            if (ext != null && !ext.isEmpty()) {
                lower = "." + ext.toLowerCase();
            } else if (url != null) {
                lower = url.toLowerCase();
            }
            if (lower != null) {
                if (lower.endsWith(".png")) mediaType = MediaType.IMAGE_PNG;
                else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) mediaType = MediaType.IMAGE_JPEG;
                else if (lower.endsWith(".gif")) mediaType = MediaType.IMAGE_GIF;
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(imageData);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error serving profile image for user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @PostMapping("/fix-user-password")
    public ResponseEntity<?> fixUserPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String newPlainPassword = request.get("newPassword");

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Email is required"));
            }

            if (newPlainPassword == null || newPlainPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "New password is required"));
            }

            // Vérifier que l'utilisateur existe
            UserDTO user;
            try {
                user = userService.getUserByEmail(email);
            } catch (ResourceNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "User not found"));
            }

            // Générer le nouveau hash
            String newHashedPassword = passwordEncoder.encode(newPlainPassword);

            // Mettre à jour le mot de passe
            userService.updateUserPassword(user.getId(), newHashedPassword);

            if (DEBUG_MODE) {
                logger.info("Password updated for user: {} with new hash: {}", email, newHashedPassword);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Password updated successfully for " + email,
                    "email", email,
                    "newHashedPassword", newHashedPassword
            ));

        } catch (Exception e) {
            logger.error("Error fixing user password", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error fixing password"));
        }
    }

    // Méthode pour corriger le login avec une vérification plus robuste
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            if (DEBUG_MODE) {
                logger.info("Login attempt for email: {}", email);
            }

            // Validation
            if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Email and password are required"));
            }

            // Get user
            UserDTO userDTO;
            try {
                userDTO = userService.getUserByEmail(email);
                if (DEBUG_MODE) {
                    logger.info("User found: {} with role: {}", email, userDTO.getRole());
                }
            } catch (ResourceNotFoundException e) {
                if (DEBUG_MODE) {
                    logger.info("User not found: {}", email);
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Invalid credentials"));
            }

            // Check if account is active
            if (!userDTO.getIsActive()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Account is inactive"));
            }

            // Verify password avec gestion d'erreur améliorée
            boolean passwordMatches = false;
            try {
                String storedPasswordHash = userService.getPasswordHashByEmail(email);
                if (DEBUG_MODE) {
                    logger.info("Stored password hash exists: {}", storedPasswordHash != null && !storedPasswordHash.isEmpty());
                    logger.info("Stored hash starts with: {}", storedPasswordHash != null ? storedPasswordHash.substring(0, Math.min(7, storedPasswordHash.length())) : "null");
                    logger.info("Input password: '{}'", password);
                }

                if (storedPasswordHash != null && !storedPasswordHash.trim().isEmpty()) {
                    try {
                        // Essayer BCrypt
                        passwordMatches = passwordEncoder.matches(password, storedPasswordHash);
                        if (DEBUG_MODE) {
                            logger.info("BCrypt comparison result: {}", passwordMatches);
                        }
                    } catch (IllegalArgumentException e) {
                        if (DEBUG_MODE) {
                            logger.warn("Invalid hash format, might be corrupted: {}", e.getMessage());
                        }
                        // Si le hash est invalide, ne pas permettre la connexion
                        passwordMatches = false;
                    }
                } else {
                    if (DEBUG_MODE) {
                        logger.warn("No password hash found for user: {}", email);
                    }
                }
            } catch (Exception e) {
                logger.error("Error checking password for user {}: {}", email, e.getMessage(), e);
                passwordMatches = false;
            }

            if (!passwordMatches) {
                if (DEBUG_MODE) {
                    logger.info("Password verification failed for user: {}", email);
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Invalid credentials"));
            }

            // Check if OTP is required for this role
            boolean requiresOtp = requiresOtpForRole(userDTO.getRole());

            if (requiresOtp) {
                // Generate and send OTP
                String otp = otpService.generateOtp(email);
                emailService.sendOtpEmail(userDTO, otp);

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "OTP sent to your email",
                        "requiresOtp", true,
                        "email", email,
                        "user", Map.of("role", userDTO.getRole().toString())
                ));
            } else {
                // Direct login for FARMER and BUYER
                return generateLoginResponse(userDTO);
            }

        } catch (Exception e) {
            logger.error("Error during login for email: {}", loginRequest.get("email"), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Server error occurred"));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> otpRequest) {
        try {
            String email = otpRequest.get("email");
            String otp = otpRequest.get("otp");

            if (email == null || email.trim().isEmpty() || otp == null || otp.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Email and OTP are required"));
            }

            // Verify OTP
            if (!otpService.validateOtp(email, otp)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid or expired OTP"));
            }

            // Get user and generate token
            UserDTO userDTO = userService.getUserByEmail(email);

            // Clear OTP after successful verification
            otpService.clearOtp(email);

            return generateLoginResponse(userDTO);

        } catch (Exception e) {
            logger.error("Error during OTP verification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Server error occurred"));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Email is required"));
            }

            // Get user
            UserDTO userDTO = userService.getUserByEmail(email);

            // Check if OTP is required for this role
            if (!requiresOtpForRole(userDTO.getRole())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "OTP not required for this role"));
            }

            // Generate and send new OTP
            String otp = otpService.generateOtp(email);
            emailService.sendOtpEmail(userDTO, otp);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "New OTP sent to your email"
            ));

        } catch (Exception e) {
            logger.error("Error during OTP resend", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Server error occurred"));
        }
    }

    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Email is required")
                );
            }

            try {
                UserDTO user = userService.getUserByEmail(email);
                Map<String, Object> response = new HashMap<>();
                response.put("exists", true);
                response.put("role", user.getRole().toString());
                response.put("active", user.getIsActive());
                response.put("passwordSet", user.getPassword() != null && !user.getPassword().isEmpty());
                return ResponseEntity.ok(response);
            } catch (ResourceNotFoundException e) {
                Map<String, Object> response = new HashMap<>();
                response.put("exists", false);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            logger.error("Error checking email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Server error: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestParam(value = "role", required = false) String role) {
        try {
            List<UserDTO> userList;

            if (role != null && !role.trim().isEmpty()) {
                // Filter by role
                try {
                    User.Role userRole = User.Role.valueOf(role.toUpperCase());
                    userList = userService.getUsersByRole(userRole);
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid role specified: {}", role);
                    return ResponseEntity.badRequest().build();
                }
            } else {
                // Get all users if no role specified
                userList = userService.getAllUsers();
            }

            return ResponseEntity.ok(userList);
        } catch (Exception e) {
            logger.error("Error getting users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String role = request.get("role");

            if (DEBUG_MODE) {
                logger.info("=== FORGOT PASSWORD DEBUG ===");
                logger.info("Email: {}", email);
                logger.info("Role: {}", role);
            }

            // Validate inputs
            if (email == null || email.trim().isEmpty()) {
                logger.warn("Email is missing");
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Email is required."));
            }

            if (role == null || role.trim().isEmpty()) {
                logger.warn("Role is missing");
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Role is required."));
            }

            // Get user
            UserDTO user;
            try {
                user = userService.getUserByEmail(email);
                if (DEBUG_MODE) {
                    logger.info("User found: {} with role: {}", user.getEmail(), user.getRole());
                    logger.info("User fullName: {}", user.getFullName());
                    logger.info("User role null? {}", user.getRole() == null);
                }
            } catch (ResourceNotFoundException e) {
                logger.warn("No account found with email: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "No account found with this email."));
            }

            // Check for null fields that could cause issues
            if (user.getRole() == null) {
                logger.error("User role is null for email: {}", email);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("success", false, "message", "User account data is incomplete. Please contact support."));
            }

            if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
                logger.error("User fullName is null or empty for email: {}", email);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("success", false, "message", "User account data is incomplete. Please contact support."));
            }

            // Verify role matches
            if (!user.getRole().toString().equals(role)) {
                logger.warn("Role mismatch. Expected: {}, Got: {}", user.getRole(), role);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Invalid role selected for this email."));
            }

            // Generate reset token
            String resetToken = generateResetToken();
            if (DEBUG_MODE) {
                logger.info("Generated reset token: {}", resetToken);
            }

            // Update user with reset token
            try {
                userService.updateUserResetToken(user.getId(), resetToken, LocalDateTime.now().plusHours(1));
                if (DEBUG_MODE) {
                    logger.info("Reset token saved to database");
                }
            } catch (Exception e) {
                logger.error("Failed to save reset token", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("success", false, "message", "Failed to generate reset code. Please try again."));
            }

            // Send email with enhanced error handling
            try {
                String subject = "Password Reset Request - Smart Agriculture";
                String body = String.format(
                        "Dear %s,\n\n" +
                                "You have requested to reset your password for your %s account. " +
                                "Please use the following code to reset your password:\n\n" +
                                "%s\n\n" +
                                "This code will expire in 1 hour.\n\n" +
                                "If you did not request a password reset, please ignore this email.\n\n" +
                                "Best regards,\nThe Smart Agriculture Management Team",
                        user.getFullName(),
                        user.getRole().name(),
                        resetToken
                );

                emailService.sendEmail(user.getEmail(), subject, body);
                logger.info("Reset token email sent successfully to: {}", email);

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Reset code sent to your email."
                ));

            } catch (Exception emailException) {
                logger.error("Failed to send reset email to: {}", email, emailException);
                // Even if email fails, we still saved the token, so user could potentially use it
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                                "success", false,
                                "message", "Failed to send reset code email. Please check your email address or try again later."
                        ));
            }

        } catch (Exception e) {
            logger.error("Unexpected error in forgot password process", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "An error occurred while processing your request. Please try again."
                    ));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String resetToken = request.get("resetToken");
            String newPassword = request.get("newPassword");
            String role = request.get("role");

            if (DEBUG_MODE) {
                logger.info("=== RESET PASSWORD DEBUG ===");
                logger.info("Email: {}", email);
                logger.info("Reset Token: {}", resetToken);
                logger.info("Role: {}", role);
            }

            // Validate inputs
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required.");
            }

            if (resetToken == null || resetToken.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Reset token is required.");
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("New password is required.");
            }

            if (!isPasswordValid(newPassword)) {
                return ResponseEntity.badRequest()
                        .body("New password must be at least 8 characters long");
            }

            // Get user
            UserDTO user;
            try {
                user = userService.getUserByEmail(email);
                if (DEBUG_MODE) {
                    logger.info("User found: {} with role: {}", user.getEmail(), user.getRole());
                }
            } catch (ResourceNotFoundException e) {
                logger.warn("No account found with email: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No account found with this email.");
            }

            // Verify role matches
            if (!user.getRole().toString().equals(role)) {
                logger.warn("Role mismatch. Expected: {}, Got: {}", user.getRole(), role);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid role selected for this email.");
            }

            // Check reset token exists and is not expired
            if (user.getResetToken() == null || user.getResetTokenExpiration() == null) {
                logger.warn("No active reset request found for email: {}", email);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No active reset request found. Please request a new password reset.");
            }

            // Verify token matches and is not expired
            if (!user.getResetToken().equals(resetToken)) {
                logger.warn("Invalid reset token for email: {}", email);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid reset token.");
            }

            if (user.getResetTokenExpiration().isBefore(LocalDateTime.now())) {
                logger.warn("Expired reset token for email: {}", email);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Reset token has expired. Please request a new one.");
            }

            // Reset password
            String encodedPassword = passwordEncoder.encode(newPassword);
            userService.resetPasswordWithToken(email, resetToken, encodedPassword);

            if (DEBUG_MODE) {
                logger.info("Password reset successful for email: {}", email);
            }

            return ResponseEntity.ok("Password reset successful.");

        } catch (Exception e) {
            logger.error("Unexpected error in password reset process", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request.");
        }
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@PathVariable String id, @RequestBody Map<String, Object> updateRequest) {
        try {
            String username = (String) updateRequest.get("username");
            String fullName = (String) updateRequest.get("fullName");
            String email = (String) updateRequest.get("email");
            String phoneNumber = (String) updateRequest.get("phoneNumber");
            String role = (String) updateRequest.get("role");
            Boolean active = (Boolean) updateRequest.get("active");
            String profileImageUrl = (String) updateRequest.get("profileImageUrl");

            // Validation
            if (username == null || username.trim().isEmpty() ||
                    fullName == null || fullName.trim().isEmpty() ||
                    email == null || email.trim().isEmpty() ||
                    phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "All required fields must be filled"));
            }

            // Get existing user
            UserDTO existingUser = userService.getUserById(id);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }

            // Create updated user DTO
            UserDTO updatedUser = new UserDTO();
            updatedUser.setUsername(username.trim());
            updatedUser.setFullName(fullName.trim());
            updatedUser.setEmail(email.trim());
            updatedUser.setPhoneNumber(phoneNumber.trim());
            updatedUser.setRole(User.Role.valueOf(role));
            updatedUser.setIsActive(active != null ? active : existingUser.getIsActive());
            updatedUser.setProfileImageUrl(profileImageUrl);

            if (DEBUG_MODE) {
                logger.info("Profile image URL updated for user ID: {}", id);
            }

            // Update the user
            UserDTO result = userService.updateUser(id, updatedUser);

            // Create safe response without sensitive data
            UserDTO safeUser = createSafeUserDTO(result);

            if (DEBUG_MODE) {
                logger.info("Profile updated successfully for user ID: {}", id);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Profile updated successfully",
                    "data", safeUser
            ));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "User not found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Invalid role specified"));
        } catch (Exception e) {
            logger.error("Error updating profile for user ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error updating profile"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUserDirect(@PathVariable String id, @RequestBody Map<String, Object> updateRequest) {
        try {
            if (DEBUG_MODE) {
                logger.info("=== DIRECT UPDATE DEBUG ===");
                logger.info("Received direct update request for user ID: {}", id);
                logger.info("Request body: {}", updateRequest);
                logger.info("============================");
            }

            String username = (String) updateRequest.get("username");
            String fullName = (String) updateRequest.get("fullName");
            String email = (String) updateRequest.get("email");
            String phoneNumber = (String) updateRequest.get("phoneNumber");
            String role = (String) updateRequest.get("role");
            Boolean active = (Boolean) updateRequest.get("active");
            String profileImageUrl = (String) updateRequest.get("profileImageUrl");

            if (DEBUG_MODE) {
                logger.info("Parsed fields:");
                logger.info("- username: {}", username);
                logger.info("- fullName: {}", fullName);
                logger.info("- email: {}", email);
                logger.info("- phoneNumber: {}", phoneNumber);
                logger.info("- role: {}", role);
                logger.info("- active: {}", active);
                logger.info("- profileImageUrl present: {}", profileImageUrl != null && !profileImageUrl.trim().isEmpty());
            }

            // Validation
            if (username == null || username.trim().isEmpty() ||
                    fullName == null || fullName.trim().isEmpty() ||
                    email == null || email.trim().isEmpty() ||
                    phoneNumber == null || phoneNumber.trim().isEmpty()) {

                logger.warn("Validation failed: missing required fields");
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "All required fields must be filled"));
            }

            // Get existing user
            UserDTO existingUser = userService.getUserById(id);
            if (existingUser == null) {
                logger.warn("User not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }

            if (DEBUG_MODE) {
                logger.info("Existing user found:");
                logger.info("- Current username: {}", existingUser.getUsername());
                logger.info("- Current fullName: {}", existingUser.getFullName());
                logger.info("- Current email: {}", existingUser.getEmail());
                logger.info("- Current phoneNumber: {}", existingUser.getPhoneNumber());
            }

            // Create updated user DTO
            UserDTO updatedUser = new UserDTO();
            updatedUser.setUsername(username.trim());
            updatedUser.setFullName(fullName.trim());
            updatedUser.setEmail(email.trim());
            updatedUser.setPhoneNumber(phoneNumber.trim());
            updatedUser.setRole(User.Role.valueOf(role));
            updatedUser.setIsActive(active != null ? active : existingUser.getIsActive());
            updatedUser.setProfileImageUrl(profileImageUrl);

            if (DEBUG_MODE) {
                logger.info("About to update user with new data:");
                logger.info("- New username: {}", updatedUser.getUsername());
                logger.info("- New fullName: {}", updatedUser.getFullName());
                logger.info("- New email: {}", updatedUser.getEmail());
                logger.info("- New phoneNumber: {}", updatedUser.getPhoneNumber());
            }

            // Update the user
            UserDTO result = userService.updateUser(id, updatedUser);

            if (DEBUG_MODE) {
                logger.info("User updated successfully:");
                logger.info("- Result username: {}", result.getUsername());
                logger.info("- Result fullName: {}", result.getFullName());
                logger.info("- Result email: {}", result.getEmail());
                logger.info("- Result phoneNumber: {}", result.getPhoneNumber());
            }

            // Create safe response without sensitive data
            UserDTO safeUser = createSafeUserDTO(result);

            if (DEBUG_MODE) {
                logger.info("Profile updated successfully for user ID: {}", id);
                logger.info("Sending safe response: {}", safeUser);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Profile updated successfully",
                    "data", safeUser
            ));

        } catch (ResourceNotFoundException e) {
            logger.error("User not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "User not found"));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid role specified for user ID: {}", id, e);
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Invalid role specified"));
        } catch (Exception e) {
            logger.error("Error updating profile for user ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error updating profile: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@PathVariable String id, @RequestBody Map<String, String> passwordRequest) {
        try {
            String currentPassword = passwordRequest.get("currentPassword");
            String newPassword = passwordRequest.get("newPassword");

            // Validation
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Current password is required"));
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "New password is required"));
            }

            if (!isPasswordValid(newPassword)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "New password must be at least 8 characters long"));
            }

            // Get existing user
            UserDTO existingUser = userService.getUserById(id);
            if (existingUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "User not found"));
            }

            // Verify current password
            String storedPasswordHash = userService.getPasswordHashByEmail(existingUser.getEmail());
            if (storedPasswordHash == null || !passwordEncoder.matches(currentPassword, storedPasswordHash)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Current password is incorrect"));
            }

            // Update password using the service method
            String newPasswordHash = passwordEncoder.encode(newPassword);
            userService.updateUserPassword(id, newPasswordHash);

            if (DEBUG_MODE) {
                logger.info("Password changed successfully for user ID: {}", id);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Password changed successfully"
            ));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "User not found"));
        } catch (Exception e) {
            logger.error("Error changing password for user ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error changing password"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable String id) {
        try {
            UserDTO user = userService.getUserById(id);

            // Create safe response without password
            UserDTO safeUser = createSafeUserDTO(user);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", safeUser
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "User not found"));
        } catch (Exception e) {
            logger.error("Error getting user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error retrieving user information"));
        }
    }





    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody Map<String, String> adminRequest) {
        try {
            String username = adminRequest.get("username");
            String fullName = adminRequest.get("fullName");
            String email = adminRequest.get("email");
            String phoneNumber = adminRequest.get("phoneNumber");
            String password = adminRequest.get("password");

            // Validation
            if (username == null || username.trim().isEmpty() ||
                    fullName == null || fullName.trim().isEmpty() ||
                    email == null || email.trim().isEmpty() ||
                    phoneNumber == null || phoneNumber.trim().isEmpty() ||
                    password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "All fields are required"));
            }

            if (password.length() < 8) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Password must be at least 8 characters long"));
            }

            // Check if email already exists
            try {
                userService.getUserByEmail(email);
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Email already exists"));
            } catch (ResourceNotFoundException e) {
                // Email doesn't exist, continue with registration
            }

            // Check if username already exists
            try {
                userService.getUserByUsername(username);
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Username already exists"));
            } catch (ResourceNotFoundException e) {
                // Username doesn't exist, continue with registration
            }

            // Create new user with ADMIN role
            UserDTO newAdmin = new UserDTO();
            newAdmin.setUsername(username);
            newAdmin.setFullName(fullName);
            newAdmin.setEmail(email);
            newAdmin.setPhoneNumber(phoneNumber);
            newAdmin.setPassword(password);
            newAdmin.setRole(User.Role.ADMIN);
            newAdmin.setIsActive(true);

            UserDTO createdAdmin = userService.createUser(newAdmin);

            // ✨ AJOUT: Envoi de l'email de bienvenue
            try {
                emailService.sendBilingualWelcomeEmail(createdAdmin);
                logger.info("Welcome email sent successfully to new admin: {}", createdAdmin.getEmail());
            } catch (Exception emailError) {
                // Log l'erreur mais ne pas bloquer la création
                logger.error("Failed to send welcome email to admin: {}, Error: {}",
                        createdAdmin.getEmail(), emailError.getMessage());
            }

            // Create safe response without password
            UserDTO safeAdmin = createSafeUserDTO(createdAdmin);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Admin created successfully. Welcome email sent!",
                    "user", safeAdmin
            ));

        } catch (Exception e) {
            logger.error("Error during admin creation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "An error occurred processing your request"));
        }
    }





    @GetMapping("/admins")
    public ResponseEntity<List<UserDTO>> getAllAdmins() {
        try {
            List<UserDTO> admins = userService.getUsersByRole(User.Role.ADMIN);
            return ResponseEntity.ok(admins);
        } catch (Exception e) {
            logger.error("Error getting admins", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/farmers")
    public ResponseEntity<List<UserDTO>> getAllFarmers() {
        try {
            List<UserDTO> farmers = userService.getUsersByRole(User.Role.FARMER);
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            logger.error("Error getting farmers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buyers")
    public ResponseEntity<List<UserDTO>> getAllBuyers() {
        try {
            List<UserDTO> buyers = userService.getUsersByRole(User.Role.BUYER);
            return ResponseEntity.ok(buyers);
        } catch (Exception e) {
            logger.error("Error getting buyers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/analysts")
    public ResponseEntity<List<UserDTO>> getAllAnalysts() {
        try {
            List<UserDTO> analysts = userService.getUsersByRole(User.Role.ANALYST);
            return ResponseEntity.ok(analysts);
        } catch (Exception e) {
            logger.error("Error getting analysts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/government")
    public ResponseEntity<List<UserDTO>> getAllGovernmentUsers() {
        try {
            List<UserDTO> governmentUsers = userService.getUsersByRole(User.Role.GOVERNMENT);
            return ResponseEntity.ok(governmentUsers);
        } catch (Exception e) {
            logger.error("Error getting government users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserDTO>> getActiveUsers(@RequestParam(value = "role", required = false) String role) {
        try {
            List<UserDTO> activeUsers;

            if (role != null && !role.trim().isEmpty()) {
                try {
                    User.Role userRole = User.Role.valueOf(role.toUpperCase());
                    activeUsers = userService.getActiveUsersByRole(userRole);
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid role specified: {}", role);
                    return ResponseEntity.badRequest().build();
                }
            } else {
                activeUsers = userService.getActiveUsers();
            }

            return ResponseEntity.ok(activeUsers);
        } catch (Exception e) {
            logger.error("Error getting active users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper methods
    private boolean requiresOtpForRole(User.Role role) {
        // Require OTP for sensitive roles
        return role == User.Role.ADMIN ||
                role == User.Role.ANALYST ||
                role == User.Role.GOVERNMENT;
    }

    private ResponseEntity<?> generateLoginResponse(UserDTO userDTO) {
        try {
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userDTO.getRole().name()));

            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(userDTO.getEmail())
                    .password("")
                    .authorities(authorities)
                    .build();

            String token = jwtService.generateToken(userDetails);
            UserDTO safeUserDTO = createSafeUserDTO(userDTO);

            // Update last login
            userService.updateLastLogin(userDTO.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("user", safeUserDTO);
            response.put("token", token);
            response.put("requiresOtp", false);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error generating login response", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Server error occurred"));
        }
    }

    private UserDTO createSafeUserDTO(UserDTO user) {
        UserDTO safeUser = new UserDTO();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setFullName(user.getFullName());
        safeUser.setEmail(user.getEmail());
        safeUser.setPhoneNumber(user.getPhoneNumber());
        safeUser.setRole(user.getRole());
        safeUser.setIsActive(user.getIsActive());
        safeUser.setCreatedAt(user.getCreatedAt());
        safeUser.setUpdatedAt(user.getUpdatedAt());
        safeUser.setLastLogin(user.getLastLogin());
        safeUser.setProfileImageUrl(user.getProfileImageUrl());
        return safeUser;
    }

    private String generateResetToken() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000);
        return String.valueOf(number);
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.length() >= 8;
    }




    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createUser(
            @RequestParam("username") String username,
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("role") String role,
            @RequestParam("password") String password,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

        try {
            // Validate required fields
            if (username == null || username.trim().isEmpty() ||
                    fullName == null || fullName.trim().isEmpty() ||
                    email == null || email.trim().isEmpty() ||
                    phoneNumber == null || phoneNumber.trim().isEmpty() ||
                    password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "All required fields must be filled"));
            }

            // Validate password strength
            if (password.length() < 8) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Password must be at least 8 characters long"));
            }

            // Check if email already exists
            try {
                userService.getUserByEmail(email);
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Email already exists"));
            } catch (ResourceNotFoundException e) {
                // Email doesn't exist, continue
            }

            // Check if username already exists
            try {
                userService.getUserByUsername(username);
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Username already exists"));
            } catch (ResourceNotFoundException e) {
                // Username doesn't exist, continue
            }

            // Handle profile image upload if present
            String profileImageUrl = null;
            if (profileImage != null && !profileImage.isEmpty()) {
                // Validate file size (max 2MB)
                if (profileImage.getSize() > 2 * 1024 * 1024) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("success", false, "message", "Profile image size must be less than 2MB"));
                }

                // Validate file type
                String contentType = profileImage.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("success", false, "message", "Only image files are allowed"));
                }

                profileImageUrl = "uploads/profiles/" + System.currentTimeMillis() + "_" + profileImage.getOriginalFilename();
            }

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setFullName(fullName);
            userDTO.setEmail(email);
            userDTO.setPhoneNumber(phoneNumber);
            userDTO.setRole(User.Role.valueOf(role));
            userDTO.setPassword(password);
            userDTO.setProfileImageUrl(profileImageUrl);
            userDTO.setIsActive(true);

            UserDTO createdUser = userService.createUser(userDTO);

            // ✨ AJOUT: Envoi de l'email de bienvenue
            try {
                emailService.sendBilingualWelcomeEmail(createdUser);
                logger.info("Welcome email sent successfully to newly created user: {}", createdUser.getEmail());
            } catch (Exception emailError) {
                // Log l'erreur mais ne pas bloquer la création
                logger.error("Failed to send welcome email to: {}, Error: {}",
                        createdUser.getEmail(), emailError.getMessage());
            }

            UserDTO safeUser = createSafeUserDTO(createdUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "User created successfully. Welcome email sent!",
                    "user", safeUser
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Invalid role specified"));
        } catch (Exception e) {
            logger.error("Error creating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error creating user"));
        }
    }






    // Replace your existing updateUser method with this fixed version:
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable String id,
            @RequestParam("username") String username,
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("role") String role,
            @RequestParam("active") String active,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

        try {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setFullName(fullName);
            userDTO.setEmail(email);
            userDTO.setPhoneNumber(phoneNumber);
            userDTO.setRole(User.Role.valueOf(role));
            userDTO.setIsActive(Boolean.parseBoolean(active));

            // Handle file upload if present
            if (profileImage != null && !profileImage.isEmpty()) {
                // Validate file size (max 2MB)
                if (profileImage.getSize() > 2 * 1024 * 1024) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("success", false, "message", "Profile image size must be less than 2MB"));
                }

                // Validate file type
                String contentType = profileImage.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("success", false, "message", "Only image files are allowed"));
                }

                // Here you would typically upload to a file storage service
                String profileImageUrl = "uploads/profiles/" + System.currentTimeMillis() + "_" + profileImage.getOriginalFilename();
                userDTO.setProfileImageUrl(profileImageUrl);
            }

            UserDTO updatedUser = userService.updateUser(id, userDTO);
            UserDTO safeUser = createSafeUserDTO(updatedUser);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User updated successfully",
                    "data", safeUser
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "User not found"));
        } catch (Exception e) {
            logger.error("Error updating user with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error updating user"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User deleted successfully"
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "User not found"));
        } catch (Exception e) {
            logger.error("Error deleting user with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error deleting user"));
        }
    }




    @PutMapping("/{id}/activate")
    public ResponseEntity<Map<String, Object>> activateUser(@PathVariable String id) {
        try {
            UserDTO user = userService.getUserById(id);
            userService.updateUserStatus(id, true);

            // ✨ AJOUT: Envoi de l'email de notification d'activation
            try {
                emailService.sendAccountActivationEmail(user);
                logger.info("Activation email sent to: {}", user.getEmail());
            } catch (Exception emailError) {
                logger.error("Failed to send activation email to: {}", user.getEmail(), emailError);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User activated successfully"
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "User not found"));
        } catch (Exception e) {
            logger.error("Error activating user with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error activating user"));
        }
    }



    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateUser(@PathVariable String id) {
        try {
            UserDTO user = userService.getUserById(id);
            userService.updateUserStatus(id, false);

            // ✨ AJOUT: Envoi de l'email de notification de désactivation
            try {
                emailService.sendAccountDeactivationEmail(user);
                logger.info("Deactivation email sent to: {}", user.getEmail());
            } catch (Exception emailError) {
                logger.error("Failed to send deactivation email to: {}", user.getEmail(), emailError);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User deactivated successfully"
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "User not found"));
        } catch (Exception e) {
            logger.error("Error deactivating user with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error deactivating user"));
        }
    }




    @PostMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestBody Map<String, Object> searchCriteria) {
        try {
            String query = (String) searchCriteria.get("query");
            String role = (String) searchCriteria.get("role");
            Boolean active = (Boolean) searchCriteria.get("active");

            List<UserDTO> users = userService.searchUsers(query, role, active);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error searching users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            statistics.put("totalUsers", userService.getTotalUserCount());
            statistics.put("activeUsers", userService.getActiveUserCount());
            statistics.put("inactiveUsers", userService.getInactiveUserCount());

            // Role-based statistics
            Map<String, Long> roleStats = new HashMap<>();
            for (User.Role role : User.Role.values()) {
                roleStats.put(role.name(), userService.getUserCountByRole(role));
            }
            statistics.put("roleStatistics", roleStats);

            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Error getting user statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}