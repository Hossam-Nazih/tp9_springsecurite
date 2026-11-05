package ma.fstg.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Comptes de démonstration, conservés en mémoire : ils sont recréés à chaque démarrage.
 * Seuls les hachages BCrypt sont stockés. Les mots de passe écrits ici sont ceux du sujet :
 * une vraie application ne les placerait pas dans le code source.
 */
@Configuration
public class UserDetailsConfig {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails utilisateur = User.withUsername("user")
                .password(passwordEncoder.encode("1111"))
                .roles("USER")
                .build();

        // L'administrateur possède les deux rôles : il accède aussi à l'espace utilisateur
        UserDetails administrateur = User.withUsername("admin")
                .password(passwordEncoder.encode("1234"))
                .roles("ADMIN", "USER")
                .build();

        return new InMemoryUserDetailsManager(utilisateur, administrateur);
    }
}
