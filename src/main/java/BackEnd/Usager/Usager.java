package BackEnd.Usager;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Usager implements Serializable {
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

    // ### static get and set pour load ###
    public static AtomicInteger getCount() {
        return count;
    }

    public static void setCount(AtomicInteger valeur) {
        count = valeur;
    }

    public String getNom() {
        return nom;
    }

    public synchronized void setNom(String nom) {
        this.nom = nom;
    }

    public int getLimiteEmprunt() {
        return limiteEmprunt;
    }

    public int getNombreEmprunt() {
        return nombreEmprunt;
    }

    public synchronized void addNombreEmprunt(){
        this.nombreEmprunt++;
    }

    public synchronized void reduceNombreEmprunt(){
        this.nombreEmprunt--;
    }

    public int getDureeEmprunt() {
        return dureeEmprunt;
    }

    @Override
    public String toString() {
        return String.format("ID: %d - %s : %s", id, nom, this.getClass().getSimpleName());
    }
}
