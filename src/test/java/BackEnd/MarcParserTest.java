package BackEnd;

import BackEnd.Livres.EtatPhisique;
import BackEnd.Livres.Livre;
import BackEnd.Livres.Statut;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class MarcParserTest {
    Bibliotheque bib = new Bibliotheque();
    @BeforeEach
    void setUp() {
        Livre livre1 = new Livre("test1", "Dany", "1234-354-2134-1", 1, Statut.DISPONIBLE, EtatPhisique.NEUF);
        Livre livre2 = new Livre("test2", "Moi", "1234-354-7654-2", 1, Statut.DISPONIBLE, EtatPhisique.NEUF);

        bib.getListeLivres().add(livre1);
        bib.getListeLivres().add(livre2);
    }

    @Test
    void nouveauFichierMarc() throws IOException {
        try {
            bib.nouveauFichierMarc();
        }  catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        assertTrue(bib.getListeLivres().size() > 10_000);
    }

    @Test
    void ajouterFichierMarc() throws IOException {
        String pathToIso = getClass().getResource("/Cegep.iso2709").getPath();

        for (int i = 3;i < 10_000; i++ ) {
            bib.addLivre(String.format("titre%d", i), String.format("auteur%d", i), String.format("ISBN%d", i), 1);
        }
        try {
            bib.ajouterFichierMarc(pathToIso);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        assertTrue(bib.getListeLivres().size() > 20_000); // 10_000 manuel + fichier marc (10000 + duplication aléatoire (~ 17000))
    }

    @Test
    void donneCreeValide() throws IOException {
        bib.nouveauFichierMarc();
        int tailleInitiale = bib.getListeLivres().size();

        //tout les ISBN sont non-null ou contiennent qlqc (pas de "")
        assertEquals(tailleInitiale, bib.getListeLivres().parallelStream()
                .filter(e -> !Objects.equals(e.getISBN(), ""))
                .count());

        //tout les titres sont non-null et contiennent qlqc (pas de "")
        assertEquals(tailleInitiale, bib.getListeLivres().parallelStream()
            .filter(e -> !Objects.equals(e.getTitre(), ""))
            .count());

        //tout les auteurs sont non-null et contiennent qlqc (pas de "")
        assertEquals(tailleInitiale, bib.getListeLivres().parallelStream()
            .filter(e -> !Objects.equals(e.getAuteur(), ""))
            .count());
    }
}