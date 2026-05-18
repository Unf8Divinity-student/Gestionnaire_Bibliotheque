package BackEnd;

import BackEnd.Usager.*;
import BackEnd.Livres.Livre;

import java.io.IOException;
import java.util.ArrayList;

public class Bibliotheque {

    private ArrayList<Livre> listeLivres;
    private ArrayList<Usager> listeUsager;

    public Bibliotheque() throws IOException {
        this.listeLivres = new ArrayList<>();
        this.listeUsager = new ArrayList<>();
    }

    // methode listeUsager

    public ArrayList<Usager> getListeUsager() {
        return this.listeUsager;
    }

    public synchronized void addUser(userType type, String nom) {
        switch (type) {
            case ETUDIANT: listeUsager.add(new Etudiant(nom));
            case PROFESSEUR: listeUsager.add(new Professeur(nom));
            case VISITEUR: listeUsager.add(new Visiteur(nom));
        }
    }

    public void deleteUser(Usager user) {
        listeUsager.remove(user);
    }

    // methode listeLivre
    public synchronized void nouveauFichierMarc() throws IOException {
        MarcParser parser = new MarcParser();
        listeLivres = parser.chargerFichierMarc(getClass().getResource("/Cegep.iso2709").getPath());
    }

    public synchronized void ajouterFichierMarc(String chemin) throws IOException {
        MarcParser parser = new MarcParser();
        listeLivres.addAll(parser.chargerFichierMarc(chemin));
    }

    public ArrayList<Livre> getListeLivres() {
        return this.listeLivres;
    }
}