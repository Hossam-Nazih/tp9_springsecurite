package ma.fstg.security.service;

import ma.fstg.security.entity.JournalConnexion;
import ma.fstg.security.repository.JournalConnexionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Enregistre chaque tentative de connexion.
 * Spring Security publie un événement à chaque tentative : aucun code n'est à ajouter dans le filtre.
 */
@Service
public class JournalConnexionService {

    private static final int LONGUEUR_MAX_IDENTIFIANT = 100;

    @Autowired
    private JournalConnexionRepository journalRepository;

    @EventListener
    public void surConnexionReussie(AuthenticationSuccessEvent event) {
        enregistrer(event.getAuthentication().getName(), true);
    }

    @EventListener
    public void surConnexionEchouee(AbstractAuthenticationFailureEvent event) {
        enregistrer(event.getAuthentication().getName(), false);
    }

    public List<JournalConnexion> findDernieres() {
        return journalRepository.findTop20ByOrderByDateTentativeDesc();
    }

    private void enregistrer(String identifiant, boolean reussie) {
        JournalConnexion entree = new JournalConnexion();
        // Le champ est libre : on tronque pour qu'une saisie très longue ne provoque pas d'erreur SQL
        entree.setIdentifiantSaisi(identifiant.length() > LONGUEUR_MAX_IDENTIFIANT
                ? identifiant.substring(0, LONGUEUR_MAX_IDENTIFIANT)
                : identifiant);
        entree.setDateTentative(LocalDateTime.now());
        entree.setReussie(reussie);
        journalRepository.save(entree);
    }
}
