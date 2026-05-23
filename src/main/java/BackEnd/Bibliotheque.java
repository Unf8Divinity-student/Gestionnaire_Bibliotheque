package BackEnd;

import BackEnd.Livres.EtatPhisique;
import BackEnd.Livres.Statut;
import BackEnd.Usager.*;
import BackEnd.Livres.Livre;

import java.io.Serializable;
import java.util.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.groupingBy;

public class Bibliotheque implements Serializable {

    AtomicInteger totalEmprunt;

    private ArrayList<Livre> listeLivres;
    private ArrayList<Usager> listeUsager;
    private ArrayList<Emprunt> listeEmprunt;
    private ArrayList<Livre> listeBrise;

    public Bibliotheque() throws IOException {
        this.listeLivres = new ArrayList<>();
        this.listeUsager = new ArrayList<>();
        this.listeEmprunt = new ArrayList<>();
        this.listeBrise = new ArrayList<>();
        this.totalEmprunt = new AtomicInteger(0);
    }

    public AtomicInteger getTotalEmprunt() {
        return totalEmprunt;
    }

    // pas sync car SaveLoad.load() est déja sync et unique appelleur de cette méthode
    public void setTotalEmprunt(AtomicInteger totalEmprunt) {
        this.totalEmprunt = totalEmprunt;
    }

    // ######### methode listeUsager #########
    public ArrayList<Usager> getListeUsager() {
        return this.listeUsager;
    }

    // pas sync car SaveLoad.load() est déja sync et unique appelleur de cette méthode
    public void setListeUsager(ArrayList<Usager> listeUsager) {
        this.listeUsager = listeUsager;
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
        listeLivres = parser.chargerFichierMarc(Objects.requireNonNull(getClass().getResource("/Cegep.iso2709")).getPath());
    }

    public synchronized void ajouterFichierMarc(String chemin) throws IOException {
        MarcParser parser = new MarcParser();
        listeLivres.addAll(parser.chargerFichierMarc(chemin));
    }

    public ArrayList<Livre> getListeLivres() {
        return this.listeLivres;
    }

    // pas sync car SaveLoad.load() est déja sync et unique appelleur de cette méthode
    public void setListeLivres(ArrayList<Livre> listeLivres) {
        this.listeLivres = listeLivres;
    }


    // ######### gestion d'emprunt #########

    public ArrayList<Emprunt> getListeEmprunt() {
        return this.listeEmprunt;
    }

    // pas sync car SaveLoad.load() est déja sync et unique appelleur de cette méthode
    public void setListeEmprunt(ArrayList<Emprunt> listeEmprunt) {
        this.listeEmprunt = listeEmprunt;
    }

    // fonciton principale emprunt
    public synchronized void emprunt(Livre livre, Usager user) {
        if (peuxEmprunter(livre, user)) {
            LocalDate today = LocalDate.now();

            listeLivres.get(listeLivres.indexOf(livre)).setStatut(Statut.EMPRUNTE);
            listeEmprunt.add(new Emprunt(livre, user, today, dateDeRetour(user), 0));
            user.addNombreEmprunt();
            totalEmprunt.getAndIncrement();
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
        List<Emprunt> listeEmpruntUser = listeEmprunt.stream().filter(e -> e.getUser() == user).toList(); // donne la liste d'emprunt de l'usager

        return (listeEmpruntUser.size() < user.getLimiteEmprunt() && pasDeRetard(user) && pasDejaEmprunt(livre, user) && livre.getStatut() == Statut.DISPONIBLE);
    }

    // détermine si user à un retard de retour
    private boolean pasDeRetard(Usager user) {
        int enRetard = listeEmprunt.stream()
                .filter(e -> e.getUser() == user)
                .mapToInt(Emprunt::getRetard)
                .sum();

        return enRetard == 0;
    }

    // determine si user à deja le livre dans ses emprunt
    private boolean pasDejaEmprunt (Livre livre, Usager user) {
        List<String> listeEmpruntUser = listeEmprunt.stream()
                .filter(e -> e.getUser() == user)
                .map(e -> e.getLivre().getISBN())
                .toList();

        return listeEmpruntUser.contains(livre.getISBN());
    }

    // ######### gestion des retours #########
    public void retourDeLivre(Emprunt emprunt) {
        if (!estBrise()) {
            listeLivres.get(listeLivres.indexOf(emprunt.getLivre())).setStatut(Statut.DISPONIBLE);
            if (emprunt.getLivre().getAEteRepare()) {
                listeLivres.get(listeLivres.indexOf(emprunt.getLivre())).setEtatPhisique(EtatPhisique.USE);
            } else {
                listeLivres.get(listeLivres.indexOf(emprunt.getLivre())).setEtatPhisique(EtatPhisique.BON);
            }
        } else {
            listeLivres.get(listeLivres.indexOf(emprunt.getLivre())).setStatut(Statut.A_REPARER);
            listeLivres.get(listeLivres.indexOf(emprunt.getLivre())).setEtatPhisique(EtatPhisique.A_REPARER);
            listeBrise.add(emprunt.getLivre());
        }
        emprunt.getUser().reduceNombreEmprunt();
        listeEmprunt.remove(emprunt);
    }

    // ######### méthode de bris #########
        // générateur de bris
    private boolean estBrise() {
        Random random = new Random();
        int nb = random.nextInt(1, 11);

        return nb == 1; // 10% de chance de tomber sur 1
    }

    public ArrayList<Livre> getListeBrise() {
        return this.listeBrise;
    }

    // pas sync car SaveLoad.load() est déja sync et unique appelleur de cette méthode
    public void setListeBrise(ArrayList<Livre> listeBrise) {
        this.listeBrise = listeBrise;
    }

    public synchronized void livreRepare(Livre livre) {
        this.listeBrise.remove(livre);
        this.listeLivres.get(listeLivres.indexOf(livre)).setStatut(Statut.DISPONIBLE);
        this.listeLivres.get(listeLivres.indexOf(livre)).setEtatPhisique(EtatPhisique.USE);
        this.listeLivres.get(listeLivres.indexOf(livre)).setAEteRepare(true);
    }

    // ################## FILTRES ##################
        // listelivres

        // par titre
    public final List<Livre> PAR_TITRE(String titre) {
        return this.listeLivres.stream()
                .parallel()
                .filter(e -> Objects.equals(e.getTitre().toLowerCase(), titre.toLowerCase()))
                .sorted()
                .toList();
    }

        // par auteur
    public final List<Livre> PAR_AUTEUR(String auteur) {
        return this.listeLivres.stream()
                .parallel()
                .filter(e -> Objects.equals(e.getAuteur().toLowerCase(), auteur.toLowerCase()))
                .sorted()
                .toList();
    }

        // par disponibilité
    public final List<Livre> DISPONIBLE() {
        return this.listeLivres.stream()
                .parallel()
                .filter(e -> e.getStatut() == Statut.DISPONIBLE)
                .sorted()
                .toList();
    }

        // stats emprunt par userType
    public Map<Class<? extends Usager>, Long> userTypeStat() {
        return this.listeEmprunt.parallelStream()
                .map(e -> e.getUser().getClass())
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
    }

    // ################## Emprunt en retard ##################
    public List<Emprunt> getRetard() {
        return listeEmprunt.parallelStream()
                .filter(e-> e.getDateRetour().isBefore(LocalDate.now()))
                .sorted((e1, e2) -> e1.getDateRetour().compareTo(e2.getDateRetour()))
                .toList();
    }

    // ################## affichage titre seulement #################
    public List<String> afficherTitre(List<Livre> list) {
        return list.parallelStream()
                .map(Livre::getTitre)
                .toList();
    }
}