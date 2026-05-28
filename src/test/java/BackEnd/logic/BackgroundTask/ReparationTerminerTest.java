package BackEnd.logic.BackgroundTask;

import BackEnd.Bibliotheque;
import BackEnd.Livres.EtatPhisique;
import BackEnd.Livres.Livre;
import BackEnd.Livres.Statut;
import BackEnd.Usager.Etudiant;
import BackEnd.Usager.Professeur;
import BackEnd.Usager.Visiteur;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReparationTerminerTest {
    Bibliotheque bib = new  Bibliotheque();

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

        Livre livreTest = bib.getListeLivres().get(0);
        Livre livreTest2 = bib.getListeLivres().get(1);

        bib.getListeBrise().add(livreTest);
        livreTest.setEtatPhisique(EtatPhisique.A_REPARER);
        livreTest.setStatut(Statut.A_REPARER);
        livreTest.setDateDisponibilite(LocalDate.now()); //simule aujourd'hui a des fin de test

        bib.getListeBrise().add(livreTest2);
        livreTest2.setEtatPhisique(EtatPhisique.A_REPARER);
        livreTest2.setStatut(Statut.A_REPARER);
        livreTest2.setDateDisponibilite(LocalDate.now().plusDays(3)); //simule aujourd'hui a des fin de test
    }

    @Test
    void run() throws InterruptedException {
        Livre livreTest = bib.getListeLivres().get(0);
        Livre livreTest2 = bib.getListeLivres().get(1);

        Thread task1 = new Thread(() -> new ReparationTerminer(bib).run());
        task1.start();
        try {
            task1.join(); // finir la tache avant le test
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        // livre vraiment réparé
        assertEquals(1, bib.getListeBrise().size());
        assertNull(livreTest.getDateDisponibilite());
        assertEquals(Statut.DISPONIBLE, livreTest.getStatut());
        assertEquals(EtatPhisique.USE, livreTest.getEtatPhisique());
        assertTrue(livreTest.getAEteRepare());

        // livre non réparé - date non atteinte
        assertEquals(LocalDate.now().plusDays(3), livreTest2.getDateDisponibilite());
        assertEquals(Statut.A_REPARER, livreTest2.getStatut());
        assertEquals(EtatPhisique.A_REPARER, livreTest2.getEtatPhisique());
        assertFalse(livreTest2.getAEteRepare());
    }
}