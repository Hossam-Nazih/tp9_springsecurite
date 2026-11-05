package ma.fstg.security;

import ma.fstg.security.repository.AnnonceRepository;
import ma.fstg.security.repository.JournalConnexionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.FormLoginRequestBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

/**
 * Rejoue les scénarios de l'étape 9 avec les vrais filtres de sécurité, sur une base H2.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private JournalConnexionRepository journalRepository;

    private FormLoginRequestBuilder connexion(String identifiant, String motDePasse) {
        return formLogin("/login")
                .userParameter("identifiant").user(identifiant)
                .passwordParam("motDePasse").password(motDePasse);
    }

    @Test
    void lesPagesPubliquesSontAccessiblesSansConnexion() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
        mockMvc.perform(get("/login")).andExpect(status().isOk());
        mockMvc.perform(get("/css/style.css")).andExpect(status().isOk());
    }

    @Test
    void leBoutonDeDeconnexionNApparaitQuApresConnexion() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(content().string(not(containsString("Se déconnecter"))));
        mockMvc.perform(get("/").with(user("user").roles("USER")))
                .andExpect(content().string(containsString("Se déconnecter")));
    }

    @Test
    void unVisiteurEstRedirigeVersLaConnexion() throws Exception {
        mockMvc.perform(get("/user/dashboard")).andExpect(redirectedUrlPattern("**/login"));
        mockMvc.perform(get("/admin/dashboard")).andExpect(redirectedUrlPattern("**/login"));
        mockMvc.perform(get("/route/inconnue")).andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void lesBonsIdentifiantsConnectentEtSontJournalises() throws Exception {
        long avant = journalRepository.count();

        mockMvc.perform(connexion("user", "1111"))
                .andExpect(authenticated().withUsername("user").withRoles("USER"))
                .andExpect(redirectedUrl("/"));

        assertEquals(avant + 1, journalRepository.count());
    }

    @Test
    void unMauvaisMotDePasseEstRefuseEtJournalise() throws Exception {
        long avant = journalRepository.count();

        mockMvc.perform(connexion("user", "mauvais"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"));

        assertEquals(avant + 1, journalRepository.count());
    }

    @Test
    void unIdentifiantTresLongNeProvoquePasDErreur() throws Exception {
        mockMvc.perform(connexion("x".repeat(500), "mauvais"))
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void leRoleUserNOuvrePasLEspaceAdministrateur() throws Exception {
        mockMvc.perform(get("/user/dashboard").with(user("user").roles("USER")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/admin/dashboard").with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void lAdministrateurOuvreLesDeuxEspaces() throws Exception {
        mockMvc.perform(get("/user/dashboard").with(user("admin").roles("ADMIN", "USER")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/admin/dashboard").with(user("admin").roles("ADMIN", "USER")))
                .andExpect(status().isOk());
    }

    @Test
    void lesMotsDePasseSontStockesHaches() {
        String hachage = userDetailsService.loadUserByUsername("admin").getPassword();

        assertTrue(hachage.startsWith("$2"), "Un hachage BCrypt commence par $2");
    }

    @Test
    void laDeconnexionRenvoieVersLeMessageDeConfirmation() throws Exception {
        mockMvc.perform(logout("/logout")).andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    void unPostSansJetonCsrfEstRefuse() throws Exception {
        mockMvc.perform(post("/admin/annonces").with(user("admin").roles("ADMIN"))
                        .param("titre", "T").param("contenu", "C"))
                .andExpect(status().isForbidden());
    }

    @Test
    void seulLAdministrateurPublieUneAnnonce() throws Exception {
        long avant = annonceRepository.count();

        mockMvc.perform(post("/admin/annonces").with(csrf()).with(user("user").roles("USER"))
                        .param("titre", "Refusée").param("contenu", "C"))
                .andExpect(status().isForbidden());
        assertEquals(avant, annonceRepository.count());

        mockMvc.perform(post("/admin/annonces").with(csrf()).with(user("admin").roles("ADMIN"))
                        .param("titre", "Examen").param("contenu", "Lundi à 9h"))
                .andExpect(redirectedUrl("/admin/dashboard"));
        assertEquals(avant + 1, annonceRepository.count());

        mockMvc.perform(get("/user/dashboard").with(user("user").roles("USER")))
                .andExpect(content().string(containsString("Lundi à 9h")));
    }

    @Test
    void uneAnnonceSansTitreNEstPasEnregistree() throws Exception {
        long avant = annonceRepository.count();

        mockMvc.perform(post("/admin/annonces").with(csrf()).with(user("admin").roles("ADMIN"))
                        .param("titre", " ").param("contenu", "C"))
                .andExpect(redirectedUrl("/admin/dashboard"));

        assertEquals(avant, annonceRepository.count());
    }

    @Test
    void leContenuDUneAnnonceEstEchappe() throws Exception {
        mockMvc.perform(post("/admin/annonces").with(csrf()).with(user("admin").roles("ADMIN"))
                        .param("titre", "XSS").param("contenu", "<script>alert(1)</script>"))
                .andExpect(redirectedUrl("/admin/dashboard"));

        mockMvc.perform(get("/user/dashboard").with(user("user").roles("USER")))
                .andExpect(content().string(containsString("&lt;script&gt;")));
    }
}
