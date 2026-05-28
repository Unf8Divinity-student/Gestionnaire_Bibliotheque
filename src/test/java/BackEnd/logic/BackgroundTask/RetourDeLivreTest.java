package BackEnd.logic.BackgroundTask;

import BackEnd.Bibliotheque;
import BackEnd.Livres.EtatPhisique;
import BackEnd.Livres.Livre;
import BackEnd.Livres.Statut;
import BackEnd.Usager.Etudiant;
import BackEnd.Usager.Professeur;
import BackEnd.Usager.Visiteur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RetourDeLivreTest {
    Bibliotheque bib = new Bibliotheque();

    @BeforeEach
        void setUp() {
        bib.setListeLivres(new ArrayList<>(List.of(
                new Livre("Le Petit Prince", "Antoine de Saint-Exupéry", "978-2-07-040850-4", 1, Statut.DISPONIBLE, EtatPhisique.BON),
                new Livre("1984", "George Orwell", "978-0-452-28423-4", 1, Statut.DISPONIBLE, EtatPhisique.NEUF),
                new Livre("L'Étranger", "Albert Camus", "978-2-07-036024-7", 1, Statut.DISPONIBLE, EtatPhisique.USE),
                new Livre("Harry Potter et la Pierre Philosophale", "J.K. Rowling", "978-2-07-054090-1", 1, Statut.DISPONIBLE, EtatPhisique.BON),
                new Livre("Dune", "Frank Herbert", "978-2-221-25174-5", 1, Statut.DISPONIBLE, EtatPhisique.BON)
        )));

        bib.setListeUsager(new ArrayList<>(List.of(
                new Etudiant("Alice Tremblay"),
                new Etudiant("Bob Gagnon"),
                new Professeur("Marie Côté"),
                new Professeur("Jean Lapointe"),
                new Visiteur("Pierre Dubois")
        )));

        bib.emprunt(bib.getListeLivres().get(0), bib.getListeUsager().get(0));
        bib.emprunt(bib.getListeLivres().get(1), bib.getListeUsager().get(1));
        bib.emprunt(bib.getListeLivres().get(2), bib.getListeUsager().get(2));
        bib.emprunt(bib.getListeLivres().get(3), bib.getListeUsager().get(3));
        bib.emprunt(bib.getListeLivres().get(4), bib.getListeUsager().get(4));

        // simulation date de retour pour test SAUF pour le dernier
        bib.getListeEmprunt().get(0).setDateRetour(LocalDate.now());
        bib.getListeEmprunt().get(1).setDateRetour(LocalDate.now());
        bib.getListeEmprunt().get(2).setDateRetour(LocalDate.now());
        bib.getListeEmprunt().get(3).setDateRetour(LocalDate.now());
    }

    @Test
    void run() {
        Livre test1 = bib.getListeLivres().get(0);

        RetourDeLivre retour = new RetourDeLivre(bib);

        Thread task1 = new Thread(retour);
        task1.start();

        try {
            task1.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        //détermine le nombre d'emprunt en retard (formule aléatoire -> 5%)
        Long nbRetard = bib.getListeEmprunt().stream()
                .filter(e -> e.getRetard() >= 1)
                .count();

        assertEquals(1 + nbRetard, bib.getListeEmprunt().size());

        if (!bib.getListeBrise().contains(test1)) {
            assertEquals(Statut.DISPONIBLE, test1.getStatut());
        } else {
            assertEquals(Statut.A_REPARER, test1.getStatut());
        }

        assertEquals(LocalDate.now().plusDays(7) ,bib.getListeLivres().get(4).getDateDisponibilite());
        assertEquals(LocalDate.now() ,retour.getDernierScan());

    }
}