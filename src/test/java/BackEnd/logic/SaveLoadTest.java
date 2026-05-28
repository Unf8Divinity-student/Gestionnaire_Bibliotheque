package BackEnd.logic;

import BackEnd.Bibliotheque;
import BackEnd.Emprunt;
import BackEnd.Livres.EtatPhisique;
import BackEnd.Livres.Livre;
import BackEnd.Livres.Statut;
import BackEnd.Usager.Etudiant;
import BackEnd.Usager.Professeur;
import BackEnd.Usager.Usager;
import BackEnd.Usager.Visiteur;
import BackEnd.logic.BackgroundTask.ReparationTerminer;
import BackEnd.logic.BackgroundTask.RetourDeLivre;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SaveLoadTest {
    Bibliotheque bib = new Bibliotheque();
    RetourDeLivre task1 = new RetourDeLivre(bib);
    ReparationTerminer task2 = new ReparationTerminer(bib);

    //setup tout se qui doit etre mis dans le json
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

        bib.emprunt(bib.getListeLivres().get(0), bib.getListeUsager().get(0));
        bib.getListeBrise().add(bib.getListeLivres().get(4));
    }

    //supprimer le fichier json apres les test pour ne pas poluer l'environnement

    @AfterEach
    void tearDown() {
        File fichier = new File("bibliotheque.json");
        if (fichier.exists()) {
            fichier.delete();
        }
    }

    @Test
    void save() {
        try {
            SaveLoad.save(bib, task1, task2);
        } catch (IOException ex) {
            System.out.println(ex + "Erreur lors de la sauvegarde.");
        }

        assertTrue(Files.exists(Paths.get("bibliotheque.json")));
    }

    @Test
    void load() {
        //préparation du load -> création du fichier json
        try {
            SaveLoad.save(bib, task1, task2);
        } catch (IOException ex) {
            System.out.println(ex + "Erreur lors de la sauvegarde.");
        }

        //création d'un conteneur vierge pour load et valeurs de comparaison
        Bibliotheque bib2 = new Bibliotheque();
        RetourDeLivre task1b = new RetourDeLivre(bib2);
        ReparationTerminer task2b = new ReparationTerminer(bib2);

        try {
            SaveLoad.load(bib2, task1b, task2b);
        } catch (IOException ex) {
            System.out.println(ex + "Erreur lors du chargement.");
        }

        // stream en toString pour byPass les adresse mémoire différente des objets (tests seulement)
        assertEquals(bib.getListeLivres().stream().map(Livre::toString).toList(), bib2.getListeLivres().stream().map(Livre::toString).toList());
        assertEquals(bib.getListeEmprunt().stream().map(Emprunt::toString).toList(), bib2.getListeEmprunt().stream().map(Emprunt::toString).toList());
        assertEquals(bib.getListeUsager().stream().map(Usager::toString).toList(), bib2.getListeUsager().stream().map(Usager::toString).toList());
        assertEquals(bib.getListeBrise().stream().map(Livre::toString).toList(), bib2.getListeBrise().stream().map(Livre::toString).toList());
        assertEquals(bib.getTotalEmprunt().get(), bib2.getTotalEmprunt().get());
        assertEquals(Usager.getCount().get(), bib2.getListeUsager().size());
        assertEquals(task1.getDernierScan(), task1b.getDernierScan());
        assertEquals(task2.getDernierScan(), task2b.getDernierScan());
    }
}