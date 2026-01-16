package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String jwtSecret; // Ensure this is set securely in application.properties

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration; // Default 24 hours in milliseconds

    @Value("${jwt.refresh.expiration:604800000}")
    private long refreshExpiration; // Default 7 days in milliseconds

    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret key must be at least 32 bytes for HS256");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token is expired: {}", e.getMessage());
            throw new RuntimeException("Token expired");
        } catch (MalformedJwtException e) {
            logger.warn("JWT token is malformed: {}", e.getMessage());
            throw new RuntimeException("Malformed token");
        } catch (SignatureException e) {
            logger.warn("JWT signature validation failed: {}", e.getMessage());
            throw new RuntimeException("Invalid token signature");
        } catch (Exception e) {
            logger.error("Unexpected error extracting username from token: {}", e.getMessage());
            throw new RuntimeException("Invalid token");
        }
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    public String extractUserRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public String extractFullName(String token) {
        return extractClaim(token, claims -> claims.get("fullName", String.class));
    }

    public String getEmailFromToken(String token) {
        return extractEmail(token);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claims != null ? claimsResolver.apply(claims) : null;
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            logger.warn("JWT parsing failed: {}", e.getMessage());
            return null;
        }
    }

    private Boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration != null && expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userDetails.getUsername());
        claims.put("authorities", userDetails.getAuthorities());
        return createToken(claims, userDetails.getUsername());
    }

    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        return createToken(claims, email);
    }

    /**
     * Generate token with User entity - ADAPTED FOR USER MODEL
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("username", user.getUsername());
        claims.put("fullName", user.getFullName());
        claims.put("role", user.getRole().name());
        claims.put("authorities", user.getRole().getAuthority());
        claims.put("isActive", user.getIsActive());

        logger.debug("Generating token for user: {} with role: {}", user.getEmail(), user.getRole());
        return createToken(claims, user.getEmail());
    }

    /**
     * Generate token with enhanced claims for User
     */
    public String generateTokenWithClaims(User user, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("username", user.getUsername());
        claims.put("fullName", user.getFullName());
        claims.put("role", user.getRole().name());
        claims.put("authorities", user.getRole().getAuthority());
        claims.put("isActive", user.getIsActive());

        // Add extra claims if provided
        if (extraClaims != null && !extraClaims.isEmpty()) {
            claims.putAll(extraClaims);
        }

        logger.debug("Generating token with extra claims for user: {}", user.getEmail());
        return createToken(claims, user.getEmail());
    }

    /**
     * Generate refresh token
     */
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("type", "refresh");

        logger.debug("Generating refresh token for user: {}", user.getEmail());
        return createRefreshToken(claims, user.getEmail());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        logger.debug("Creating token for subject: {}", subject);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String createRefreshToken(Map<String, Object> claims, String subject) {
        logger.debug("Creating refresh token for subject: {}", subject);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username != null &&
                    username.equals(userDetails.getUsername()) &&
                    !isTokenExpired(token);
        } catch (RuntimeException e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate token against User entity
     */
    public Boolean validateToken(String token, User user) {
        try {
            final String tokenEmail = extractEmail(token);
            final String tokenUserId = extractUserId(token);

            return tokenEmail != null && tokenUserId != null &&
                    tokenEmail.equals(user.getEmail()) &&
                    tokenUserId.equals(user.getId()) &&
                    !isTokenExpired(token) &&
                    user.getIsActive(); // Check if user is still active
        } catch (RuntimeException e) {
            logger.warn("Token validation against user failed: {}", e.getMessage());
            return false;
        }
    }

    public Boolean isTokenValid(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return false;
            }
            Claims claims = extractAllClaims(token);
            return claims != null && !isTokenExpired(token);
        } catch (Exception e) {
            logger.warn("Token validity check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if token is a refresh token
     */
    public Boolean isRefreshToken(String token) {
        try {
            String tokenType = extractClaim(token, claims -> claims.get("type", String.class));
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            logger.warn("Error checking token type: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate refresh token
     */
    public Boolean validateRefreshToken(String token, User user) {
        try {
            if (!isRefreshToken(token)) {
                logger.warn("Token is not a refresh token");
                return false;
            }

            final String tokenEmail = extractEmail(token);
            final String tokenUserId = extractUserId(token);

            return tokenEmail != null && tokenUserId != null &&
                    tokenEmail.equals(user.getEmail()) &&
                    tokenUserId.equals(user.getId()) &&
                    !isTokenExpired(token) &&
                    user.getIsActive();
        } catch (RuntimeException e) {
            logger.warn("Refresh token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Long getTokenExpirationTime(String token) {
        Date expiration = extractExpiration(token);
        return expiration != null ? expiration.getTime() - System.currentTimeMillis() : null;
    }

    /**
     * Get remaining time until token expires in minutes
     */
    public Long getTokenExpirationInMinutes(String token) {
        Long remainingTimeMs = getTokenExpirationTime(token);
        return remainingTimeMs != null ? remainingTimeMs / (1000 * 60) : null;
    }

    /**
     * Check if token will expire within specified minutes
     */
    public Boolean isTokenExpiringSoon(String token, long minutes) {
        Long remainingMinutes = getTokenExpirationInMinutes(token);
        return remainingMinutes != null && remainingMinutes <= minutes;
    }

    /**
     * Get user information from token
     */
    public Map<String, Object> getUserInfoFromToken(String token) {
        try {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", extractUserId(token));
            userInfo.put("email", extractEmail(token));
            userInfo.put("username", extractUsername(token));
            userInfo.put("fullName", extractFullName(token));
            userInfo.put("role", extractUserRole(token));
            userInfo.put("issuedAt", extractIssuedAt(token));
            userInfo.put("expiration", extractExpiration(token));
            userInfo.put("isExpired", isTokenExpired(token));

            return userInfo;
        } catch (Exception e) {
            logger.error("Error extracting user info from token: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Check if user has required role based on token
     */
    public Boolean hasRole(String token, User.Role requiredRole) {
        try {
            String tokenRole = extractUserRole(token);
            return tokenRole != null && tokenRole.equals(requiredRole.name());
        } catch (Exception e) {
            logger.warn("Error checking role from token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if user has any of the required roles
     */
    public Boolean hasAnyRole(String token, User.Role... requiredRoles) {
        try {
            String tokenRole = extractUserRole(token);
            if (tokenRole == null) return false;

            for (User.Role role : requiredRoles) {
                if (tokenRole.equals(role.name())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.warn("Error checking roles from token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if token belongs to an admin user
     */
    public Boolean isAdminToken(String token) {
        return hasRole(token, User.Role.ADMIN);
    }

    /**
     * Check if token belongs to a farmer
     */
    public Boolean isFarmerToken(String token) {
        return hasRole(token, User.Role.FARMER);
    }

    /**
     * Check if token belongs to a buyer
     */
    public Boolean isBuyerToken(String token) {
        return hasRole(token, User.Role.BUYER);
    }

    /**
     * Check if token belongs to an analyst
     */
    public Boolean isAnalystToken(String token) {
        return hasRole(token, User.Role.ANALYST);
    }

    /**
     * Check if token belongs to government user
     */
    public Boolean isGovernmentToken(String token) {
        return hasRole(token, User.Role.GOVERNMENT);
    }

    /**
     * Refresh an access token using refresh token
     */
    public String refreshAccessToken(String refreshToken, User user) {
        if (!validateRefreshToken(refreshToken, user)) {
            throw new RuntimeException("Invalid refresh token");
        }

        logger.debug("Refreshing access token for user: {}", user.getEmail());
        return generateToken(user);
    }

    /**
     * Invalidate token (for logout - this would typically be used with a token blacklist)
     */
    public Boolean invalidateToken(String token) {
        // In a real implementation, you would add this token to a blacklist
        // For now, we just check if it's valid
        try {
            return isTokenValid(token);
        } catch (Exception e) {
            logger.warn("Error invalidating token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract all claims as a map for debugging
     */
    public Map<String, Object> extractAllClaimsAsMap(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (claims == null) return new HashMap<>();

            Map<String, Object> claimsMap = new HashMap<>();
            claims.forEach(claimsMap::put);
            return claimsMap;
        } catch (Exception e) {
            logger.error("Error extracting claims: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Get token type (access or refresh)
     */
    public String getTokenType(String token) {
        String type = extractClaim(token, claims -> claims.get("type", String.class));
        return type != null ? type : "access";
    }

    /**
     * Check token health - comprehensive validation
     */
    public Map<String, Object> checkTokenHealth(String token) {
        Map<String, Object> health = new HashMap<>();

        try {
            health.put("isValid", isTokenValid(token));
            health.put("isExpired", isTokenExpired(token));
            health.put("expirationTime", extractExpiration(token));
            health.put("remainingMinutes", getTokenExpirationInMinutes(token));
            health.put("tokenType", getTokenType(token));
            health.put("userRole", extractUserRole(token));
            health.put("userId", extractUserId(token));
            health.put("email", extractEmail(token));
        } catch (Exception e) {
            health.put("error", e.getMessage());
            health.put("isValid", false);
        }

        return health;
    }
}