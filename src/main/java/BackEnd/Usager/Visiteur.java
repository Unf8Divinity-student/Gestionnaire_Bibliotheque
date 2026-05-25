package BackEnd.Usager;

import java.io.Serializable;

public class Visiteur extends Usager implements Serializable {

    public Visiteur(String nom) {
        super(nom, 1, 7);
    }
}