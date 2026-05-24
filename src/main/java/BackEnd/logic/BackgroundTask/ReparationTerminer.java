package BackEnd.logic.BackgroundTask;

import BackEnd.Bibliotheque;
import BackEnd.Emprunt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReparationTerminer implements Runnable {

    private final Bibliotheque bibliotheque;

    private LocalDate dernierScan;

    // permet de passer bibliotheque update en parametre à la création de la tâche pour l'utiliser dans run()
    public ReparationTerminer(Bibliotheque bibliotheque) {
        this.bibliotheque = bibliotheque; // pointeur uniquement, pas un objet
    }

    public LocalDate getDernierScan() {
        return dernierScan;
    }

    public void setDernierScan(LocalDate dernierScan) {
        this.dernierScan = dernierScan;
    }

    @Override
    public void run() {
        // vérifie si ce n'est pas le premier scan + pas déja scanner today
        if (dernierScan != null && dernierScan.isEqual(LocalDate.now())) { return ; }

        List<Emprunt> copie; // évite la concurence sur l'arraylist originale en la copiant (pas besoin de vérifier les nouveaux bris de toute façon)

        synchronized (bibliotheque.getListeEmprunt()) { // évite que la liste originale soit modifier pendant la copie de l'array
            copie = new ArrayList<>(bibliotheque.getListeEmprunt());
        }

        copie.forEach(emprunt -> {
            // 3 jours minimum apres la date de remise
            if (!emprunt.getDateEmprunt().plusDays(emprunt.getRetard() + 3).isAfter(LocalDate.now())) {
                bibliotheque.livreRepare(emprunt);
            }
        });
        dernierScan = LocalDate.now();
    }
}
