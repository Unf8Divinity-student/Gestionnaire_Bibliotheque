package BackEnd.logic;

import BackEnd.Bibliotheque;
import BackEnd.Emprunt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReparationTerminer implements Runnable {
    private final Bibliotheque bibliotheque;

    // permet de passer bibliotheque update en parametre à la création de la tâche pour l'utiliser dans run()
    public ReparationTerminer(Bibliotheque bibliotheque) {
        this.bibliotheque = bibliotheque; // pointeur uniquement, pas un objet
    }

    @Override
    public void run() {
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
    }
}
