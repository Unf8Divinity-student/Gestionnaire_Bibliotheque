package BackEnd.Usager;

import java.io.Serializable;

public class Etudiant extends Usager implements Serializable {

    public Etudiant(String nom) {
        super(nom, 3, 14);
    }
}
