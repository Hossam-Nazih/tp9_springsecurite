package ma.fstg.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Règles d'accès de toute l'application, réunies en un seul endroit.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Les règles sont lues dans l'ordre : la première qui correspond s'applique
            .authorizeHttpRequests(auth -> auth
                // Le CSS doit rester public, sinon la page de connexion s'affiche sans style
                .requestMatchers("/", "/css/**", "/error").permitAll()
                .requestMatchers("/user/**").hasRole("USER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Moindre privilège : toute route non listée exige une connexion
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                // Noms des champs du formulaire, à la place de username et password
                .usernameParameter("identifiant")
                .passwordParameter("motDePasse")
                .permitAll()
            )
            .logout(logout -> logout
                // Redirige vers login.html, qui affiche le message de déconnexion
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            // Nouvel identifiant de session après connexion, contre la fixation de session
            .sessionManagement(session -> session
                .sessionFixation(fixation -> fixation.changeSessionId())
            );
        return http.build();
    }
}
