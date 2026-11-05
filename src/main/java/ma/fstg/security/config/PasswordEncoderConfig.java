package ma.fstg.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Algorithme de hachage des mots de passe.
 * Déclaré à part pour éviter une dépendance circulaire avec la configuration des utilisateurs.
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt ajoute un sel aléatoire : deux hachages du même mot de passe sont différents
        return new BCryptPasswordEncoder();
    }
}
