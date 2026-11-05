package ma.fstg.security;

import ma.fstg.security.entity.Annonce;
import ma.fstg.security.repository.AnnonceRepository;
import ma.fstg.security.service.AnnonceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/** Vérifie la publication d'une annonce sans base de données. */
@ExtendWith(MockitoExtension.class)
class AnnonceServiceTest {

    @Mock
    private AnnonceRepository annonceRepository;

    @InjectMocks
    private AnnonceService annonceService;

    @Test
    void publierRenseigneLAuteurEtLaDate() {
        when(annonceRepository.save(any(Annonce.class))).then(returnsFirstArg());

        Annonce annonce = annonceService.publier("Examen", "Lundi à 9h", "admin");

        assertEquals("admin", annonce.getAuteur());
        assertNotNull(annonce.getDatePublication());
    }
}
