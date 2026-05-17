package BackEnd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LivreTest {

    Livre livre1 = new Livre("coucou les copinaux", "Dany", "978-2-1234-5680-3", Statut.DISPONIBLE, EtatPhisique.BON);
    Livre livre2 = new Livre("Chop Rice", "Guy", "694-4-7541-2699-7",  Statut.EMPRUNTE, EtatPhisique.USE);
    Livre livre3 = new Livre("Stream master", "Jakob", "978-2-1257-8764-3", Statut.DISPONIBLE, EtatPhisique.A_REPARER);
    @Test
    void getId() {
        assertEquals(0, livre1.getId());
        assertEquals(1, livre2.getId());
        assertEquals(2, livre3.getId());
    }
}