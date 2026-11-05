package ma.fstg.security.web;

import jakarta.validation.Valid;
import ma.fstg.security.service.AnnonceService;
import ma.fstg.security.service.JournalConnexionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Pages de l'application. Ce contrôleur ne vérifie aucun rôle :
 * l'accès à chaque route est décidé en amont par SecurityConfig.
 */
@Controller
public class HomeController {

    @Autowired
    private AnnonceService annonceService;

    @Autowired
    private JournalConnexionService journalService;

    @GetMapping("/")
    public String accueil(Principal connecte, Model model) {
        // Principal vaut null pour un visiteur non connecté
        model.addAttribute("nomConnecte", connecte == null ? null : connecte.getName());
        return "index";
    }

    /** Sans cette route, loginPage("/login") mènerait à une erreur 404. */
    @GetMapping("/login")
    public String connexion() {
        return "login";
    }

    @GetMapping("/user/dashboard")
    public String espaceUtilisateur(Principal connecte, Model model) {
        model.addAttribute("nomConnecte", connecte.getName());
        model.addAttribute("annonces", annonceService.findAll());
        return "user_dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String espaceAdministrateur(Principal connecte, Model model) {
        model.addAttribute("nomConnecte", connecte.getName());
        model.addAttribute("annonces", annonceService.findAll());
        model.addAttribute("journal", journalService.findDernieres());
        return "admin_dashboard";
    }

    @PostMapping("/admin/annonces")
    public String publierAnnonce(@Valid AnnonceForm formulaire, BindingResult erreurs,
                                 Principal connecte, RedirectAttributes redirection) {
        if (erreurs.hasErrors()) {
            redirection.addFlashAttribute("erreurFormulaire", erreurs.getAllErrors().get(0).getDefaultMessage());
        } else {
            annonceService.publier(formulaire.titre(), formulaire.contenu(), connecte.getName());
            redirection.addFlashAttribute("succesFormulaire", "Annonce publiée");
        }
        // Redirection après POST : actualiser la page ne republie pas l'annonce
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/annonces/{idAnnonce}/supprimer")
    public String supprimerAnnonce(@PathVariable("idAnnonce") int idAnnonce) {
        annonceService.supprimer(idAnnonce);
        return "redirect:/admin/dashboard";
    }
}
