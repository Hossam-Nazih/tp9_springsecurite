package ma.fstg.security.repository;

import ma.fstg.security.entity.JournalConnexion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JournalConnexionRepository extends JpaRepository<JournalConnexion, Integer> {

    // Limite l'affichage aux 20 dernières tentatives
    List<JournalConnexion> findTop20ByOrderByDateTentativeDesc();
}
