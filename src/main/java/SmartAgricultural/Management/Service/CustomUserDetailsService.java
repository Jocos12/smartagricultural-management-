package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.User;
import SmartAgricultural.Management.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Essayer de trouver l'utilisateur par nom d'utilisateur d'abord
        User user = userRepository.findByUsername(username)
                .orElse(null);

        // Si pas trouvé par nom d'utilisateur, essayer par email
        if (user == null) {
            user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "Utilisateur non trouvé avec le nom d'utilisateur ou l'email : " + username));
        }

        // Vérifier si l'utilisateur est actif
        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("Compte utilisateur désactivé : " + username);
        }

        return user;
    }

    /**
     * Charger l'utilisateur par email uniquement
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec l'email : " + email));

        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("Compte utilisateur désactivé : " + email);
        }

        return user;
    }

    /**
     * Charger l'utilisateur par ID
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(String id) throws UsernameNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec l'ID : " + id));

        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("Compte utilisateur désactivé : " + id);
        }

        return user;
    }
}