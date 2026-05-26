package BackEnd;

import BackEnd.Livres.EtatPhisique;
import BackEnd.Livres.Livre;
import BackEnd.Livres.Statut;
import BackEnd.Usager.Etudiant;
import BackEnd.Usager.Professeur;
import BackEnd.Usager.Visiteur;
import BackEnd.Usager.userType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
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
        assertEquals(EtatPhisique.A_REPARER, bib.getListeLivres().get(1).getEtatPhisique());
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

        assertEquals(new AtomicInteger(3), bib.getTotalEmprunt());
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
    void obtenirDateRetour() {
    }

    @Test
    void getListeEmprunt() {
    }

    @Test
    void setListeEmprunt() {
    }

    @Test
    void emprunt() {
    }

    @Test
    void dateDeRetour() {
    }
// condition d'emprunt
    @Test
    void peuxEmprunter() {
    }

    @Test
    void pasDeRetard() {

    }

    @Test
    void pasDejaEmprunt() {

    }

    @Test
    void retourDeLivre() {
    }

    @Test
    void getListeBrise() {
    }

    @Test
    void setListeBrise() {
    }

    @Test
    void livreRepare() {
    }
// filtres
    @Test
    void PAR_TITRE() {
    }

    @Test
    void PAR_AUTEUR() {
    }

    @Test
    void DISPONIBLE() {
    }
// stats
    @Test
    void userTypeStat() {
    }


// affichage
    @Test
    void afficherTitre() {
    }
}