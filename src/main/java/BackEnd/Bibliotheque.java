package BackEnd;

import BackEnd.Livres.Statut;
import BackEnd.Usager.*;
import BackEnd.Livres.Livre;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Bibliotheque {

    private ArrayList<Livre> listeLivres;
    private ArrayList<Usager> listeUsager;
    private ArrayList<Emprunt> listeEmprunt;

    public Bibliotheque() throws IOException {
        this.listeLivres = new ArrayList<>();
        this.listeUsager = new ArrayList<>();
        this.listeEmprunt = new ArrayList<>();
    }

    // ######### methode listeUsager #########
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

    public synchronized void deleteUser(Usager user) {
        listeUsager.remove(user);
    }

    // ######### methode listeLivre #########
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


    // ######### gestion d'emprunt #########

    // fonciton principale emprunt
    public synchronized void emprunt(Livre livre, Usager user) {
        if (peuxEmprunter(livre, user)) {
            LocalDate today = LocalDate.now();

            listeLivres.get(listeLivres.indexOf(livre)).setStatut(Statut.EMPRUNTE);
            listeEmprunt.add(new Emprunt(livre, user, today, dateDeRetour(user), 0));
        }
    }

    // calcul date de retour prévu
    public LocalDate dateDeRetour(Usager user) {
        int dureeEmprunt = user.getDureeEmprunt();
        LocalDate dateRetour = LocalDate.now().plusDays(dureeEmprunt);

        // si fin de semaine (FERMER)
        switch(dateRetour.getDayOfWeek()) {
            case SATURDAY -> dateRetour.plusDays(2);
            case SUNDAY -> dateRetour.plusDays(1);
        }

        return dateRetour;
    }

    // calcul de condition d'emprunt
    public boolean peuxEmprunter(Livre livre, Usager user) {
        List<Emprunt> listeEmpruntUser = listeEmprunt.stream().filter(e -> e.getUser == user).toList(); // donne la liste d'emprunt de l'usager

        return (listeEmpruntUser.size() < user.getLimiteEmprunt() && pasDeRetard(user) && pasDejaEmprunt(livre, user) && livre.getStatut() == Statut.DISPONIBLE);
    }

    // détermine si user à un retard de retour
    private boolean pasDeRetard(Usager user) {
        int enRetard = listeEmprunt.stream()
                .filter(e -> e.getUser == user)
                .mapToInt(e -> e.getRetard)
                .sum();

        return enRetard == 0;
    }

    // determine si user à deja le livre dans ses emprunt
    private boolean pasDejaEmprunt (Livre livre, Usager user) {
        List<String> listeEmpruntUser = listeEmprunt.stream()
                .filter(e -> e.getUser == user)
                .map(e -> e.getISBN)
                .toList();

        return listeEmpruntUser.contains(livre.getISBN());
    }
}