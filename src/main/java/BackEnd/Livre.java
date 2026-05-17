package BackEnd;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

public class Livre {
    private static int nombreUnique = 0;

    private String titre;
    private String auteur;
    private String ISBN;
    private int id;
    private Statut statut;
    private EtatPhisique etatPhisique;

    public  Livre(String titre, String auteur, String ISBN, Statut statut, EtatPhisique etatPhisique) {
        this.titre = titre;
        this.auteur = auteur;
        this.ISBN = ISBN;
        this.id = nombreUnique++;
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

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public EtatPhisique getEtatPhisique() {
        return etatPhisique;
    }

    public void setEtatPhisique(EtatPhisique etatPhisique) {
        this.etatPhisique = etatPhisique;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s écrit par %s | Status: %s - État: %s", this.ISBN, this.titre, this.auteur, this.statut, this.etatPhisique);
    }
}
