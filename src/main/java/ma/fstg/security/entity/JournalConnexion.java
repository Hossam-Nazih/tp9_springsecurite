package ma.fstg.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/**
 * Trace d'une tentative de connexion, réussie ou non.
 * L'administrateur peut ainsi repérer des échecs répétés sur un compte.
 */
@Entity
public class JournalConnexion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idJournal;

    /** Identifiant saisi, qui peut ne correspondre à aucun compte en cas d'échec. */
    @Column(nullable = false, length = 100)
    private String identifiantSaisi;

    @Column(nullable = false)
    private LocalDateTime dateTentative;

    @Column(nullable = false)
    private boolean reussie;

    public int getIdJournal() {
        return idJournal;
    }

    public void setIdJournal(int idJournal) {
        this.idJournal = idJournal;
    }

    public String getIdentifiantSaisi() {
        return identifiantSaisi;
    }

    public void setIdentifiantSaisi(String identifiantSaisi) {
        this.identifiantSaisi = identifiantSaisi;
    }

    public LocalDateTime getDateTentative() {
        return dateTentative;
    }

    public void setDateTentative(LocalDateTime dateTentative) {
        this.dateTentative = dateTentative;
    }

    public boolean isReussie() {
        return reussie;
    }

    public void setReussie(boolean reussie) {
        this.reussie = reussie;
    }
}
