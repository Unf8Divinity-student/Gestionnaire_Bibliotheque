package BackEnd;

public enum EtatPhisique {
    NEUF("Neuf"),
    BON("Bon"),
    USE("Usé"),
    A_REPARER("À réparer");

    private String etatPhisique;

    EtatPhisique(String etatPhisique) {
        this.etatPhisique = etatPhisique;
    }

    public String getEtatPhisique() {
        return etatPhisique;
    }
}
