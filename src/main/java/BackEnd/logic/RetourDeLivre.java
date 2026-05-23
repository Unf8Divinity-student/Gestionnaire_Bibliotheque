package BackEnd.logic;

import BackEnd.Bibliotheque;
import BackEnd.Emprunt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RetourDeLivre implements  Runnable {

    private final Bibliotheque bibliotheque;

    private LocalDate dernierScan;

    // permet de passer bibliotheque update en parametre à la création de la tâche pour l'utiliser dans run()
    public RetourDeLivre(Bibliotheque bibliotheque) {
        this.bibliotheque = bibliotheque; // pointeur uniquement, pas un objet
    }

    public LocalDate getDernierScan() {
        return dernierScan;
    }

    public void setDernierScan(LocalDate dernierScan) {
        this.dernierScan = dernierScan;
    }

    private boolean retard() {
        Random rand = new Random();

        return rand.nextInt(100) < 5;
    }

    // vérifie tout les retard et les retourne automatiquement si due, sauf si retard
    @Override
    public void run() {
        // vérifie si ce n'est pas le premier scan + pas déja scanner today
        if ( dernierScan != null &&  dernierScan.isEqual(LocalDate.now())) { return; }

        List<Emprunt> copie; // évite la concurence sur l'arraylist originale en la copiant (pas besoin de vérifier les nouveaux emprunts de toute façon)

        synchronized (bibliotheque.getListeEmprunt()) { // évite que la liste originale soit modifier pendant la copie de l'array
            copie = new ArrayList<>(bibliotheque.getListeEmprunt());
        }

        copie.forEach(emprunt -> {
            if (!retard()) {
                bibliotheque.retourDeLivre(emprunt);
            } else {
                emprunt.addRetard();
            }
        });
        dernierScan = LocalDate.now();
    }
}
