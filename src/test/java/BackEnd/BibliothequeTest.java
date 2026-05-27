package BackEnd;

import BackEnd.Livres.EtatPhisique;
import BackEnd.Livres.Livre;
import BackEnd.Livres.Statut;
import BackEnd.Usager.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class BibliothequeTest {

    Bibliotheque bib = new Bibliotheque();

    @BeforeEach
    void setUp() {
        bib.setListeLivres(new ArrayList<>(List.of(
                new Livre("Le Petit Prince", "Antoine de Saint-Exupéry", "978-2-07-040850-4", 1, Statut.DISPONIBLE, EtatPhisique.BON),
                new Livre("1984", "George Orwell", "978-0-452-28423-4", 1, Statut.DISPONIBLE, EtatPhisique.NEUF),
                new Livre("L'Étranger", "Albert Camus", "978-2-07-036024-7", 1, Statut.EMPRUNTE, EtatPhisique.USE),
                new Livre("Harry Potter et la Pierre Philosophale", "J.K. Rowling", "978-2-07-054090-1", 1, Statut.DISPONIBLE, EtatPhisique.BON),
                new Livre("Dune", "Frank Herbert", "978-2-221-25174-5", 1, Statut.A_REPARER, EtatPhisique.A_REPARER)
        )));

        bib.setListeUsager(new ArrayList<>(List.of(
                new Etudiant("Alice Tremblay"),
                new Etudiant("Bob Gagnon"),
                new Professeur("Marie Côté"),
                new Professeur("Jean Lapointe"),
                new Visiteur("Pierre Dubois")
        )));
    }

// listeLivre

    @Test
    void setListeLivres() {
        ArrayList<Livre> listeLivresTest = new ArrayList<>();
        Livre livre1 = new Livre("coucou les amis", "Dany", "1234-354-2134-1", 1, Statut.DISPONIBLE, EtatPhisique.NEUF);
        Livre livre2 = new Livre("wasaa", "Moi", "1234-354-7654-2", 1, Statut.DISPONIBLE, EtatPhisique.NEUF);

        listeLivresTest.add(livre1);
        listeLivresTest.add(livre2);
        bib.setListeLivres(listeLivresTest);

        // test le getListeLivre en même temps
        assertEquals(listeLivresTest, bib.getListeLivres());
    }

    @Test
    void addLivre() {
        int tailleAvant = bib.getListeLivres().size();
        bib.addLivre("wasaa", "Moi", "1234-354-7654-2", 1);

        assertEquals(tailleAvant + 1,  bib.getListeLivres().size());
    }

    @Test
    void modifierLivre() {
        bib.modifierLivre(bib.getListeLivres().get(0), Statut.A_REPARER, EtatPhisique.A_REPARER);

        assertEquals(Statut.A_REPARER, bib.getListeLivres().get(0).getStatut());
        assertEquals(EtatPhisique.A_REPARER, bib.getListeLivres().get(0).getEtatPhisique());
    }

    @Test
    void deleteLivre() {
        int tailleAvant = bib.getListeLivres().size();
        bib.deleteLivre(bib.getListeLivres().get(0));

        assertEquals(tailleAvant - 1, bib.getListeLivres().size());
    }

    // pour save-load
    @Test
    void setTotalEmprunt() {
        bib.setTotalEmprunt(new AtomicInteger(3));

        assertEquals(new AtomicInteger(3).get(), bib.getTotalEmprunt().get());
    }

    @Test
    void setListeUsager() {
        bib.setListeUsager(new ArrayList<>(List.of(
                new Etudiant("Dany"),
                new Professeur("Michel")
        )));

        assertEquals(2, bib.getListeUsager().size());
    }

    @Test
    void addUser() {
        int tailleAvant = 5;
        bib.addUser(userType.VISITEUR, "thomas");

        assertEquals(6, bib.getListeUsager().size());
    }

    @Test
    void modifierUser() {
        bib.modifierUser(bib.getListeUsager().get(0), "thomas");

        assertEquals("thomas", bib.getListeUsager().get(0).getNom());
    }

    @Test
    void deleteUser() {
        int tailleAvant = 5;
        bib.deleteUser(bib.getListeUsager().get(0));
        assertEquals(tailleAvant - 1, bib.getListeUsager().size());

    }

    /*
    test séparer avec fichier iso
    @Test
    void nouveauFichierMarc() {
    }

    @Test
    void ajouterFichierMarc() {
    }
    */

    @Test
    void emprunt() {
        bib.emprunt(bib.getListeLivres().get(0), bib.getListeUsager().get(0));

        assertEquals(1,bib.getListeEmprunt().size());
        assertEquals(Statut.EMPRUNTE, bib.getListeLivres().get(0).getStatut());
        assertEquals(LocalDate.class, bib.getListeLivres().get(0).getDateDisponibilite().getClass());
        assertEquals(1, bib.getListeUsager().get(0).getNombreEmprunt());
        assertEquals(1, bib.getTotalEmprunt().get()); // ne marche pas si je test en AtomicInteger?????
    }

    @Test
    void obtenirDateRetour() {
        Livre livreEnDouble = new Livre("Le Petit Prince", "Antoine de Saint-Exupéry", "978-2-07-040850-4", 2, Statut.DISPONIBLE, EtatPhisique.BON);
        Livre livre1 = bib.getListeLivres().get(0);
        bib.getListeLivres().add(livreEnDouble);
        bib.emprunt(livre1, bib.getListeUsager().get(0)); // étudiant, duré emprunt 14 jours
        bib.emprunt(livreEnDouble, bib.getListeUsager().get(2)); // professeur, dur. d'emprunt 28 jours

        assertEquals(LocalDate.now().plusDays(14),bib.obtenirDateRetour(livre1)); // pas sur sur du test
    }

    @Test
    void setListeEmprunt() {
        Livre petitPrince = bib.getListeLivres().get(0);
        Livre orwell = bib.getListeLivres().get(1);
        Livre etranger = bib.getListeLivres().get(2);
        Livre harryPotter = bib.getListeLivres().get(3);
        Livre dune = bib.getListeLivres().get(4);

        Usager alice = bib.getListeUsager().get(0);
        Usager bob = bib.getListeUsager().get(1);
        Usager marie = bib.getListeUsager().get(2);
        Usager jean = bib.getListeUsager().get(3);
        Usager pierre = bib.getListeUsager().get(4);

        int tailleInitiale = bib.getListeEmprunt().size();

        bib.setListeEmprunt(new ArrayList<>(List.of(
            new Emprunt(petitPrince, alice, LocalDate.now(), LocalDate.now().plusDays(14), 0),
            new Emprunt(orwell, bob, LocalDate.now(), LocalDate.now().plusDays(14), 0),
            new Emprunt(etranger, marie, LocalDate.now(), LocalDate.now().plusDays(30), 0),
            new Emprunt(harryPotter, jean, LocalDate.now(), LocalDate.now().plusDays(30), 0),
            new Emprunt(dune, pierre, LocalDate.now(), LocalDate.now().plusDays(7), 0)
        )));

        assertEquals(tailleInitiale + 5, bib.getListeLivres().size());
    }



    @Test
    void dateDeRetour() {
        bib.emprunt(bib.getListeLivres().get(0), bib.getListeUsager().get(0)); // 14 jours

        assertEquals(LocalDate.now().plusDays(bib.getListeUsager().get(0).getDureeEmprunt()), bib.getListeEmprunt().get(0).getDateRetour());
    }
// condition d'emprunt
    @Test
    void peuxEmprunter() {
        Livre livreEnDouble = new Livre("Le Petit Prince", "Antoine de Saint-Exupéry", "978-2-07-040850-4", 2, Statut.DISPONIBLE, EtatPhisique.BON);
        Livre livreperso = new Livre("wasaa", "Dany", "123-1-45-655434-4", 1, Statut.DISPONIBLE, EtatPhisique.BON);

        bib.getListeLivres().add(livreEnDouble);
        bib.getListeLivres().add(livreperso);

        bib.emprunt(bib.getListeLivres().get(0), bib.getListeUsager().get(0));
        bib.emprunt(bib.getListeLivres().get(1), bib.getListeUsager().get(0));
        assertTrue(bib.peuxEmprunter(bib.getListeLivres().get(3), bib.getListeUsager().get(0))); // sttus = dispo
        assertFalse(bib.peuxEmprunter(bib.getListeLivres().get(2), bib.getListeUsager().get(0))); // status != dispo

        // test pasDejaEmprunt() -> private
        assertFalse(bib.peuxEmprunter(livreEnDouble, bib.getListeUsager().get(0))); // livre déja emprunté

        bib.emprunt(bib.getListeLivres().get(3), bib.getListeUsager().get(0));
        assertFalse(bib.peuxEmprunter(bib.getListeLivres().get(3), bib.getListeUsager().get(0))); // limite emprunt atteinte

        // test pasDeRetard() -> private
        bib.getListeEmprunt().get(0).addRetard();
        assertFalse(bib.peuxEmprunter(livreperso, bib.getListeUsager().get(0))); // retard sur un livre
    }

    @Test
    void retourDeLivre() {
        bib.emprunt(bib.getListeLivres().get(0), bib.getListeUsager().get(0));
        bib.retourDeLivre(bib.getListeEmprunt().get(0));

        assertEquals(0, bib.getListeEmprunt().size());
    }

    @Test
    void getListeBrise() {
        while (bib.getListeLivres().get(0).getEtatPhisique() != EtatPhisique.A_REPARER) {
            bib.emprunt(bib.getListeLivres().get(0), bib.getListeUsager().get(0));
            bib.retourDeLivre(bib.getListeEmprunt().get(0));
        }
        assertEquals(1, bib.getListeBrise().size());
    }

    @Test
    void setListeBrise() {
        bib.setListeBrise(new ArrayList<Livre>(List.of(bib.getListeLivres().get(0))));

        assertEquals(1, bib.getListeBrise().size());
    }

    @Test
    void livreRepare() {
        while (bib.getListeLivres().get(0).getEtatPhisique() != EtatPhisique.A_REPARER) {
            bib.emprunt(bib.getListeLivres().get(0), bib.getListeUsager().get(0));
            bib.retourDeLivre(bib.getListeEmprunt().get(0));
        }

        bib.livreRepare(bib.getListeBrise().get(0));
        assertEquals(0, bib.getListeBrise().size());
        assertEquals(EtatPhisique.USE, bib.getListeLivres().get(0).getEtatPhisique());
        assertTrue(bib.getListeLivres().get(0).getAEteRepare());
    }
// filtres
    @Test
    void PAR_TITRE() {
        assertEquals(1, bib.PAR_TITRE("Dun").size());
        assertEquals(3, bib.PAR_TITRE("n").size());
        assertEquals(3, bib.PAR_TITRE("N").size());
        assertEquals(0, bib.PAR_TITRE("Java is frying my brain!!!").size());
    }

    @Test
    void PAR_AUTEUR() {
        assertEquals(1, bib.PAR_AUTEUR("antoine").size());
        assertEquals(4, bib.PAR_AUTEUR("E").size());
        assertEquals(0, bib.PAR_AUTEUR("Dreamer of java stream").size());
    }

    @Test
    void DISPONIBLE() {
        assertEquals(3, bib.DISPONIBLE().size());
    }
// stats
    @Test
    void userTypeStat() {
        Livre petitPrince = bib.getListeLivres().get(0);
        Livre orwell = bib.getListeLivres().get(1);
        Livre etranger = bib.getListeLivres().get(2);
        etranger.setStatut(Statut.DISPONIBLE);
        etranger.setEtatPhisique(EtatPhisique.BON);

        Livre harryPotter = bib.getListeLivres().get(3);
        Livre dune = bib.getListeLivres().get(4);
        dune.setStatut(Statut.DISPONIBLE);
        dune.setEtatPhisique(EtatPhisique.BON);

        Usager etu1 = bib.getListeUsager().get(0);
        Usager etu2 = bib.getListeUsager().get(1);
        Usager prof1 = bib.getListeUsager().get(2);
        Usager prof2 = bib.getListeUsager().get(3);
        Usager vis1 = bib.getListeUsager().get(4);

        bib.emprunt(petitPrince, etu1);
        bib.emprunt(orwell, prof1);
        bib.emprunt(etranger, prof1);
        bib.emprunt(harryPotter, prof2);
        bib.emprunt(dune, vis1);

        System.out.println(bib.getListeEmprunt().size());

        Map<Class<? extends Usager>, Long> resultat = new HashMap<>(Map.of(
                Etudiant.class, 1L,
                Professeur.class, 3L,
                Visiteur.class, 1L
        ));
        assertEquals(resultat, bib.userTypeStat());
    }


// affichage
    @Test
    void afficherTitre() {

        List<String> listeTitre = List.of(
                "1984",
                "Dune",
                "Harry Potter et la Pierre Philosophale",
                "L'Étranger",
                "Le Petit Prince"
        );

        assertEquals(listeTitre, bib.afficherTitre(bib.PAR_TITRE("")));
    }
}