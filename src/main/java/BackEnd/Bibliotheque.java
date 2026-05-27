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

    private AtomicInteger totalEmprunt;

    private ArrayList<Livre> listeLivres;
    private ArrayList<Usager> listeUsager;
    private ArrayList<Emprunt> listeEmprunt;
    private ArrayList<Livre> listeBrise;

    public Bibliotheque() {
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
    public synchronized void setListeUsager(ArrayList<Usager> listeUsager) {
        this.listeUsager = listeUsager;
    }

    public synchronized void addUser(userType type, String nom) {
        switch (type) {
            case ETUDIANT -> listeUsager.add(new Etudiant(nom));
            case PROFESSEUR -> listeUsager.add(new Professeur(nom));
            case VISITEUR -> listeUsager.add(new Visiteur(nom));
        }
    }

    public synchronized void modifierUser(Usager user, String nom) {
        user.setNom(nom);
    }

    public synchronized void deleteUser(Usager user) {
        if (user.getNombreEmprunt() == 0) {
            listeUsager.remove(user);
        }
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

    public synchronized void addLivre(String titre, String auteur, String ISBN, int id) {
        this.listeLivres.add(new Livre(titre, auteur, ISBN, id, Statut.DISPONIBLE, EtatPhisique.NEUF));
    }

    public synchronized void modifierLivre(Livre livre, Statut statut, EtatPhisique etatPhisique) {
        livre.setStatut(statut);
        livre.setEtatPhisique(etatPhisique);
    }

    public synchronized void deleteLivre(Livre livre) {
        boolean nonEmprunte = listeEmprunt.stream()
                .noneMatch(l -> l.getLivre().equals(livre));
        if (nonEmprunte) {
            this.listeLivres.remove(livre);
        }
    }

    // tout livre = emprunt -> date retour
    public LocalDate obtenirDateRetour(Livre livre) {
        String ISBN =  livre.getISBN();
        Optional<LocalDate> dateDispo;
        List<Livre> toutLesExemplaire = this.listeLivres.stream()
                .filter(l -> l.getISBN().equals(ISBN))
                .toList();

        if (toutLesExemplaire.stream().allMatch(l -> l.getStatut() != Statut.DISPONIBLE)) {
            dateDispo = toutLesExemplaire.stream().map(Livre::getDateDisponibilite).min(Comparator.naturalOrder());
        } else { return LocalDate.now(); }
        return dateDispo.orElseGet(LocalDate::now);
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
            listeLivres.get(listeLivres.indexOf(livre)).setDateDisponibilite(dateDeRetour(user));
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
            case SATURDAY -> dateRetour = dateRetour.plusDays(2);
            case SUNDAY -> dateRetour = dateRetour.plusDays(1);
        }

        return dateRetour;
    }

    // calcul de condition d'emprunt
    public boolean peuxEmprunter(Livre livre, Usager user) {
        return (user.getNombreEmprunt() < user.getLimiteEmprunt() && pasDeRetard(user) && pasDejaEmprunt(livre, user) && livre.getStatut() == Statut.DISPONIBLE);
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

        return listeEmprunt.stream()
                .filter(e -> e.getUser() == user)
                .map(e -> e.getLivre().getISBN())
                .noneMatch(e -> Objects.equals(e, livre.getISBN()));
    }

    // ######### gestion des retours #########
    public synchronized void retourDeLivre(Emprunt emprunt) {
        if (!estBrise()) {
            listeLivres.get(listeLivres.indexOf(emprunt.getLivre())).setStatut(Statut.DISPONIBLE);
            listeLivres.get(listeLivres.indexOf(emprunt.getLivre())).setDateDisponibilite(null);
            if (emprunt.getLivre().getAEteRepare()) {
                listeLivres.get(listeLivres.indexOf(emprunt.getLivre())).setEtatPhisique(EtatPhisique.USE);
            } else {
                listeLivres.get(listeLivres.indexOf(emprunt.getLivre())).setEtatPhisique(EtatPhisique.BON);
            }
        } else {
            listeLivres.get(listeLivres.indexOf(emprunt.getLivre())).setStatut(Statut.A_REPARER);
            listeLivres.get(listeLivres.indexOf(emprunt.getLivre())).setEtatPhisique(EtatPhisique.A_REPARER);
            listeLivres.get(listeLivres.indexOf(emprunt.getLivre())).setDateDisponibilite(calculDateDisponibilite());
            listeBrise.add(listeLivres.get(listeLivres.indexOf(emprunt.getLivre())));
        }
        emprunt.getUser().reduceNombreEmprunt();
        listeEmprunt.remove(emprunt);
    }

    // ######### méthode de bris #########
        // générateur de bris
    private boolean estBrise() {
        Random random = new Random();
        int nb = random.nextInt(10);

        return nb == 0; // 10% de chance de tomber sur 1
    }

        // calcul dateDisponibilite apres bris
    private LocalDate calculDateDisponibilite() {
        int dureReparation = 3;
        LocalDate dateDisponibilite = LocalDate.now().plusDays(dureReparation);

        for (int i = 1; i <= dureReparation; i++) {
            switch (LocalDate.now().plusDays(i).getDayOfWeek()) {
                case SATURDAY -> dateDisponibilite = dateDisponibilite.plusDays(1);
                case SUNDAY -> dateDisponibilite = dateDisponibilite.plusDays(1);
            }
        }
        switch(dateDisponibilite.getDayOfWeek()) {
            case SATURDAY ->  dateDisponibilite = dateDisponibilite.plusDays(2);
            case SUNDAY ->  dateDisponibilite = dateDisponibilite.plusDays(1);
        }
        return dateDisponibilite;
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
                .filter(e -> e.getTitre().toLowerCase().contains(titre.toLowerCase()))
                .sorted()
                .toList();
    }

        // par auteur
    public final List<Livre> PAR_AUTEUR(String auteur) {
        return this.listeLivres.stream()
                .parallel()
                .filter(e -> e.getAuteur().toLowerCase().contains(auteur.toLowerCase()))
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