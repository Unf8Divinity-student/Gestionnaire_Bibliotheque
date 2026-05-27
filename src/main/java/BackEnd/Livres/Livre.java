package BackEnd.Livres;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Comparator;

public class Livre implements Comparable<Livre>, Serializable {


    private String titre;
    private String auteur;
    private String ISBN;
    private int id; // # d'exemplaire (1-2-3-4)
    private Statut statut;
    private EtatPhisique etatPhisique;
    private boolean AEteRepare = false;
    private LocalDate dateDisponibilite;

    public  Livre(String titre, String auteur, String ISBN, int id, Statut statut, EtatPhisique etatPhisique) {
        this.titre = titre;
        this.auteur = auteur;
        this.ISBN = ISBN;
        this.id = id;
        this.statut = statut;
        this.etatPhisique = etatPhisique;
    }

    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getISBN() {
        return ISBN;
    }

    public int getId() {
        return id;
    }

    public Statut getStatut() {
        return statut;
    }

    public synchronized void setStatut(Statut statut) {
        this.statut = statut;
    }

    public EtatPhisique getEtatPhisique() {
        return etatPhisique;
    }

    public synchronized void setEtatPhisique(EtatPhisique etatPhisique) {
        this.etatPhisique = etatPhisique;
    }

    public boolean getAEteRepare() {
        return this.AEteRepare;
    }

    public synchronized void setAEteRepare(boolean aEteRepare) {
        this.AEteRepare = aEteRepare;
    }

    public LocalDate getDateDisponibilite() {
        return this.dateDisponibilite;
    }

    public synchronized void setDateDisponibilite(LocalDate dateDisponibilite) {
        this.dateDisponibilite = dateDisponibilite;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s écrit par %s - exemplaire #%d | Status: %s - État: %s", this.ISBN, this.titre, this.auteur, this.id, this.statut, this.etatPhisique);
    }

    @Override
    public int compareTo(Livre o) {
        return this.getTitre().toLowerCase().compareTo(o.getTitre().toLowerCase());
    }
}