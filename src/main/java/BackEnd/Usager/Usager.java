package BackEnd.Usager;

import java.util.concurrent.atomic.AtomicInteger;

public class Usager {
    static private AtomicInteger count = new AtomicInteger(0);

    private int id;
    private String nom;

    private int limiteEmprunt;
    private int nombreEmprunt = 0; //valeur par défaut à la création
    private int dureeEmprunt;

    public Usager(String nom, int limiteEmprunt, int dureeEmprunt) {
        this.id = count.getAndIncrement();
        this.nom = nom;
        this.limiteEmprunt = limiteEmprunt;
        this.dureeEmprunt = dureeEmprunt;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public int getLimiteEmprunt() {
        return limiteEmprunt;
    }

    public int getNombreEmprunt() {
        return nombreEmprunt;
    }

    public int getDureeEmprunt() {
        return dureeEmprunt;
    }
}
