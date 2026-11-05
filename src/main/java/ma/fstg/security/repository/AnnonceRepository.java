package ma.fstg.security.repository;

import ma.fstg.security.entity.Annonce;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnonceRepository extends JpaRepository<Annonce, Integer> {

    // Les annonces les plus récentes s'affichent en premier
    List<Annonce> findAllByOrderByDatePublicationDesc();
}
