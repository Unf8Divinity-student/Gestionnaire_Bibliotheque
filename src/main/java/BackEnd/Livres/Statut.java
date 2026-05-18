package BackEnd.Livres;

public enum Statut {
    DISPONIBLE("Disponible"),
    EMPRUNTE("Emprunté"),
    A_REPARER("En réparation");

    private String status;

    Statut(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
