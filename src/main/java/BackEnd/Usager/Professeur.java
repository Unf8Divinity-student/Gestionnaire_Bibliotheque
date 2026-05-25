package BackEnd.Usager;

import java.io.Serializable;

public class Professeur extends Usager implements Serializable {

    public Professeur(String nom) {
        super(nom, 6, 28);
    }
}
