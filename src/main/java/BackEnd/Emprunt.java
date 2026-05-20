package BackEnd;

import BackEnd.Usager.Usager;

import java.time.LocalDate;

public class Emprunt {
    private Livre livre;
    private Usager user;
    private LocalDate dateEmprunt;
    private LocalDate dateRetour;
    private int retard;

    public Emprunt(Livre livre, Usager user, LocalDate dateEmprunt, LocalDate dateRetour, int retard) {
        this.livre = livre;
        this. user = user;
        this. dateEmprunt = dateEmprunt;
        this. dateRetour = dateRetour;
        this.retard = retard;
    }

    public Livre getLivre() {
        return this.livre;
    }

    public Usager getUser() {
        return this.user;
    }

    public LocalDate getDateEmprunt() {
        return this.dateEmprunt;
    }

    public LocalDate getDateRetour() {
        return this.dateRetour;
    }

    public void setDateRetour(LocalDate dateRetour) {
        this.dateRetour = dateRetour;
    }

    public int getRetard() {
        return this.retard;
    }

    public void addRetard() {
        this.retard++;
    }
}