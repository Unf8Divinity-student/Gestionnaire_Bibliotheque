package BackEnd;

import BackEnd.Livres.EtatPhisique;
import BackEnd.Livres.Livre;
import BackEnd.Livres.Statut;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Parseur du format ISO 2709 (MARC) vers des objets Livre.
 *
 * Structure d'une notice ISO 2709 :
 *  - Leader      : 24 caractères (dont positions 0-4 = longueur totale de la notice)
 *  - Répertoire  : n entrées de 12 caractères chacune (tag 3c + longueur 4c + offset 4c... +1c séparateur)
 *  - Champ fixe  : tag 001–009 sans indicateurs ni sous-champs
 *  - Champs var. : indicateurs 2c + sous-champs (délimiteur 0x1F + code 1c + valeur)
 *  - Fin notice  : 0x1D
 *
 * Champs utilisés :
 *  020 $a → ISBN
 *  100 $a → auteur principal
 *  245 $a $b → titre
 *  700 $a → co-auteurs
 */

public class MarcParser {

    // Séparateurs ISO 2709
    private static final char FIELD_TERMINATOR  = 0x1D; // fin de notice
    private static final char RECORD_TERMINATOR = 0x1E; // fin de champ
    private static final char SUBFIELD_DELIMITER = 0x1F; // début de sous-champ

    // Probabilités du nombre d'exemplaires (selon l'énoncé)
    private static final double PROB_1 = 0.50;
    private static final double PROB_2 = 0.80; // 0.50 + 0.30
    private static final double PROB_3 = 0.95; // 0.80 + 0.15
    // 4 exemplaires : les 5% restants

    private final Random random = new Random();

    /**
     * Lit un fichier ISO 2709 et retourne une ArrayList de Livre
     * avec duplication selon les probabilités de l'énoncé.
     *
     * @param cheminFichier chemin vers le fichier .iso2709
     * @return liste de tous les exemplaires (Livre)
     */
    public ArrayList<Livre> chargerFichierMarc(String cheminFichier) throws IOException {
        ArrayList<Livre> tousLesExemplaires = new ArrayList<>();

        // Le fichier MARC est souvent en ISO-8859-1 ou UTF-8 selon la source
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(cheminFichier), StandardCharsets.ISO_8859_1))) {

            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
                // Une notice se termine par le FIELD_TERMINATOR (0x1D)
                if ((char) c == FIELD_TERMINATOR) {
                    String notice = sb.toString();
                    sb.setLength(0);
                    Livre livreBase = parserNotice(notice);
                    if (livreBase != null) {
                        // Générer les exemplaires selon les probabilités
                        List<Livre> exemplaires = genererExemplaires(livreBase);
                        tousLesExemplaires.addAll(exemplaires);
                    }
                }
            }
        }

        return tousLesExemplaires;
    }

    /**
     * Parse une notice MARC et retourne un objet Livre de base (1 exemplaire).
     * Retourne null si les données essentielles sont absentes.
     */
    private Livre parserNotice(String notice) {
        if (notice.length() < 24) return null;

        try {
            // --- Leader (24 premiers caractères) ---
            int longueurNotice = Integer.parseInt(notice.substring(0, 5).trim());
            if (notice.length() < longueurNotice) return null;

            // Position du début des données (Base address of data), positions 12-16
            int baseAdresse = Integer.parseInt(notice.substring(12, 17).trim());

            // --- Répertoire : commence à position 24, finit au premier 0x1E ---
            int finRepertoire = notice.indexOf(RECORD_TERMINATOR);
            if (finRepertoire < 0) return null;

            String repertoire = notice.substring(24, finRepertoire);
            // Chaque entrée du répertoire = 12 caractères : 3 (tag) + 4 (longueur) + 5 (offset)
            int nbEntrees = repertoire.length() / 12;

            // --- Extraire les champs ---
            Map<String, List<String>> champs = new LinkedHashMap<>();

            for (int i = 0; i < nbEntrees; i++) {
                String entree = repertoire.substring(i * 12, i * 12 + 12);
                String tag        = entree.substring(0, 3);
                int    longueur   = Integer.parseInt(entree.substring(3, 7).trim());
                int    offset     = Integer.parseInt(entree.substring(7, 12).trim());

                int debut = baseAdresse + offset;
                int fin   = debut + longueur;
                if (fin > notice.length()) fin = notice.length();

                String valeurChamp = notice.substring(debut, fin);
                // Supprimer le terminateur de champ final
                valeurChamp = valeurChamp.replace(String.valueOf(RECORD_TERMINATOR), "")
                        .replace(String.valueOf(FIELD_TERMINATOR), "");

                champs.computeIfAbsent(tag, k -> new ArrayList<>()).add(valeurChamp);
            }

            // --- Extraire les données utiles ---
            String isbn   = extraireISBN(champs);
            String titre  = extraireTitre(champs);
            String auteur = extraireAuteur(champs);

            // On ignore les notices sans titre
            if (titre == null || titre.isBlank()) return null;

            // Valeurs par défaut si absentes
            if (isbn   == null || isbn.isBlank())   isbn   = genererISBNFictif();
            if (auteur == null || auteur.isBlank()) auteur = "Auteur inconnu";

            return new Livre(titre, auteur, isbn,
                    1,
                    Statut.DISPONIBLE,
                    EtatPhisique.NEUF);

        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            // Notice malformée : on l'ignore silencieusement
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Extraction des champs MARC
    // -------------------------------------------------------------------------

    /** 020 $a → ISBN (on nettoie les caractères parasites après l'ISBN) */
    private String extraireISBN(Map<String, List<String>> champs) {
        List<String> champ020 = champs.get("020");
        if (champ020 == null || champ020.isEmpty()) return null;

        for (String valeur : champ020) {
            String sousChamp = extraireSousChamp(valeur, 'a');
            if (sousChamp != null) {
                // L'ISBN peut être suivi de " (" ou de qualificatifs : on garde seulement les chiffres/X/-
                return sousChamp.replaceAll("[^0-9Xx\\-].*", "").trim();
            }
        }
        return null;
    }

    /** 245 $a + $b → titre complet */
    private String extraireTitre(Map<String, List<String>> champs) {
        List<String> champ245 = champs.get("245");
        if (champ245 == null || champ245.isEmpty()) return null;

        String valeur = champ245.get(0);
        // Sauter les 2 indicateurs (2 premiers caractères du champ variable)
        if (valeur.length() > 2) valeur = valeur.substring(2);

        String titreA = extraireSousChamp(valeur, 'a');
        String titreB = extraireSousChamp(valeur, 'b');

        StringBuilder titre = new StringBuilder();
        if (titreA != null) titre.append(titreA.trim().replaceAll("[/:]+$", "").trim());
        if (titreB != null) titre.append(" ").append(titreB.trim().replaceAll("[/:]+$", "").trim());

        return titre.toString().trim();
    }

    /** 100 $a → auteur principal, sinon premier 700 $a */
    private String extraireAuteur(Map<String, List<String>> champs) {
        // Auteur principal (100)
        List<String> champ100 = champs.get("100");
        if (champ100 != null && !champ100.isEmpty()) {
            String valeur = champ100.get(0);
            if (valeur.length() > 2) valeur = valeur.substring(2);
            String auteur = extraireSousChamp(valeur, 'a');
            if (auteur != null) return nettoyer(auteur);
        }

        // Co-auteur (700) si pas de 100
        List<String> champ700 = champs.get("700");
        if (champ700 != null && !champ700.isEmpty()) {
            String valeur = champ700.get(0);
            if (valeur.length() > 2) valeur = valeur.substring(2);
            String auteur = extraireSousChamp(valeur, 'a');
            if (auteur != null) return nettoyer(auteur);
        }

        return null;
    }

    /**
     * Extrait la valeur d'un sous-champ identifié par son code (ex: 'a').
     * Format : ...0x1F + code + valeur + 0x1F...
     */
    private String extraireSousChamp(String champBrut, char code) {
        int idx = champBrut.indexOf(SUBFIELD_DELIMITER + String.valueOf(code));
        if (idx < 0) return null;
        int debut = idx + 2; // sauter délimiteur + code
        int fin   = champBrut.indexOf(SUBFIELD_DELIMITER, debut);
        if (fin < 0) fin = champBrut.length();
        return champBrut.substring(debut, fin).trim();
    }

    /** Supprime la ponctuation en fin de chaîne et trim */
    private String nettoyer(String s) {
        return s.replaceAll("[,.:;/]+$", "").trim();
    }

    // -------------------------------------------------------------------------
    // Génération des exemplaires
    // -------------------------------------------------------------------------

    /**
     * Détermine le nombre d'exemplaires selon les probabilités et crée les copies.
     * Le premier exemplaire reçoit l'idExemplaire déjà assigné ;
     * les suivants obtiennent de nouveaux IDs.
     */
    private List<Livre> genererExemplaires(Livre livreBase) {
        int nbExemplaires = tirerNombreExemplaires();
        List<Livre> exemplaires = new ArrayList<>();

        exemplaires.add(livreBase);

        for (int i = 2; i <= nbExemplaires; i++) {
            Livre copie = new Livre(
                    livreBase.getTitre(),
                    livreBase.getAuteur(),
                    livreBase.getISBN(),
                    i,
                    Statut.DISPONIBLE,
                    EtatPhisique.NEUF
            );
            exemplaires.add(copie);
        }

        return exemplaires;
    }

    /**
     * Tire aléatoirement un nombre d'exemplaires selon :
     *  50% → 1,  30% → 2,  15% → 3,  5% → 4
     */
    private int tirerNombreExemplaires() {
        double p = random.nextDouble();
        if (p < PROB_1) return 1;
        if (p < PROB_2) return 2;
        if (p < PROB_3) return 3;
        return 4;
    }

    /** Génère un ISBN fictif si absent de la notice */
    private String genererISBNFictif() {
        return "ISBN-" + (100000 + random.nextInt(900000));
    }
}

