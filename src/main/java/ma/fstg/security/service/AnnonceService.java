package ma.fstg.security.service;

import ma.fstg.security.entity.Annonce;
import ma.fstg.security.repository.AnnonceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/** Logique métier des annonces. */
@Service
public class AnnonceService {

    @Autowired
    private AnnonceRepository annonceRepository;

    /** L'auteur et la date sont fixés ici pour que le client ne puisse pas les choisir. */
    public Annonce publier(String titre, String contenu, String auteur) {
        Annonce annonce = new Annonce();
        annonce.setTitre(titre);
        annonce.setContenu(contenu);
        annonce.setAuteur(auteur);
        annonce.setDatePublication(LocalDateTime.now());
        return annonceRepository.save(annonce);
    }

    public List<Annonce> findAll() {
        return annonceRepository.findAllByOrderByDatePublicationDesc();
    }

    public void supprimer(int idAnnonce) {
        annonceRepository.deleteById(idAnnonce);
    }
}
