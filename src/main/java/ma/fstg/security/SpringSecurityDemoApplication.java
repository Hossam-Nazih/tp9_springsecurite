package ma.fstg.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lance l'application. Les sous-packages config, web, entity, repository
 * et service sont détectés automatiquement à partir de ce package racine.
 */
@SpringBootApplication
public class SpringSecurityDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityDemoApplication.class, args);
    }
}
