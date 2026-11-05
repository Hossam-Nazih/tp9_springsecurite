package ma.fstg.security.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Champs réellement modifiables par l'administrateur.
 * Passer par cette classe plutôt que par l'entité empêche d'envoyer un auteur ou un identifiant.
 */
public record AnnonceForm(
        @NotBlank(message = "Le titre est obligatoire")
        @Size(max = 100, message = "Le titre ne dépasse pas 100 caractères")
        String titre,

        @NotBlank(message = "Le contenu est obligatoire")
        @Size(max = 1000, message = "Le contenu ne dépasse pas 1000 caractères")
        String contenu) {
}
