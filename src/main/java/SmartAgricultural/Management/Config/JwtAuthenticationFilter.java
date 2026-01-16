package SmartAgricultural.Management.Config;

import SmartAgricultural.Management.Model.User;
import SmartAgricultural.Management.Service.CustomUserDetailsService;
import SmartAgricultural.Management.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userIdentifier; // Peut être username ou email

        // Vérifier si l'en-tête Authorization est présent et commence par "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraire le token JWT
        jwt = authHeader.substring(7);

        try {
            // D'abord vérifier si le token est valide
            if (!jwtService.isTokenValid(jwt)) {
                logger.warn("Token JWT invalide ou expiré");
                filterChain.doFilter(request, response);
                return;
            }

            // Extraire l'identifiant utilisateur du token (peut être username ou email)
            userIdentifier = jwtService.extractUsername(jwt); // ou jwtService.extractEmail(jwt) selon votre implémentation

            // Vérifier si l'utilisateur n'est pas déjà authentifié
            if (userIdentifier != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    // Charger les détails de l'utilisateur
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userIdentifier);

                    // Validation supplémentaire du token contre l'utilisateur
                    if (userDetails instanceof User) {
                        User user = (User) userDetails;

                        // Utiliser la méthode de validation qui prend un User en paramètre
                        if (jwtService.validateToken(jwt, user)) {
                            // Créer l'objet d'authentification
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            // Définir l'authentification dans le contexte de sécurité
                            SecurityContextHolder.getContext().setAuthentication(authToken);

                            logger.debug("Utilisateur authentifié avec succès : {}", userIdentifier);
                        } else {
                            logger.warn("Token JWT invalide pour l'utilisateur : {}", userIdentifier);
                        }
                    } else {
                        // Fallback si UserDetails n'est pas une instance de User
                        // Utiliser une validation basique du token
                        if (jwtService.isTokenValid(jwt)) {
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);

                            logger.debug("Utilisateur authentifié avec succès (fallback) : {}", userIdentifier);
                        } else {
                            logger.warn("Token JWT invalide (fallback) pour l'utilisateur : {}", userIdentifier);
                        }
                    }

                } catch (Exception userLoadException) {
                    logger.warn("Erreur lors du chargement de l'utilisateur {}: {}", userIdentifier, userLoadException.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Erreur lors de l'authentification JWT : {}", e.getMessage());
            // Ne pas arrêter la chaîne de filtres, laisser Spring Security gérer l'erreur
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Ne pas filtrer les endpoints publics
        return path.startsWith("/api/auth/") ||
                path.startsWith("/api/public/") ||
                path.equals("/error") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/") ||
                path.startsWith("/actuator/") ||
                path.endsWith(".html") ||
                path.endsWith(".css") ||
                path.endsWith(".js") ||
                path.endsWith(".ico");
    }
}