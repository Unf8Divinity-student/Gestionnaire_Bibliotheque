package BackEnd;

import BackEnd.Usager.Professeur;
import BackEnd.Usager.Usager;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmpruntTest {
    LocalDate now = LocalDate.now();
    Livre livre1 = new Livre("coucou les copinaux", "Dany", "978-2-1234-5680-3", 1, Statut.DISPONIBLE, EtatPhisique.BON);
    Professeur michel = new Professeur("Michel");
    Emprunt empruntTest = new Emprunt(livre1, michel, now, now.plusDays(michel.getDureeEmprunt()), 0);

    @Test
    void testInfoEmprunt() {
        assertEquals(livre1, empruntTest.getLivre());
        assertEquals(michel, empruntTest.getUser());
        assertEquals(now, empruntTest.getDateEmprunt());
        assertEquals(now.plusDays(michel.getDureeEmprunt()), empruntTest.getDateRetour());
        assertEquals(0, empruntTest.getRetard());
        empruntTest.addRetard();
        assertEquals(1, empruntTest.getRetard());
    }
}