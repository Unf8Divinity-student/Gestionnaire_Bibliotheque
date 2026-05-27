package BackEnd.Usager;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class UsagerTest {

    @Test
    void setCount() {
        Usager.setCount(new AtomicInteger(2));
        Usager ginette = new Visiteur("ginette");
        assertEquals(3,Usager.getCount().get() );
    }

    @Test
    void setNom() {
        Usager michel = new Professeur("Michel");
        michel.setNom("le meilleur prof");
        assertEquals("le meilleur prof",michel.getNom());
    }

    @Test
    void addNombreEmprunt() {
        Usager phill = new Professeur("Phill");
        phill.addNombreEmprunt();
        assertEquals(1,phill.getNombreEmprunt());
    }

    @Test
    void reduceNombreEmprunt() {
        Usager phill = new Professeur("Phill");
        phill.addNombreEmprunt();
        phill.addNombreEmprunt();
        phill.addNombreEmprunt();
        assertEquals(3,phill.getNombreEmprunt());
        phill.reduceNombreEmprunt();
        assertEquals(2,phill.getNombreEmprunt());
    }
}