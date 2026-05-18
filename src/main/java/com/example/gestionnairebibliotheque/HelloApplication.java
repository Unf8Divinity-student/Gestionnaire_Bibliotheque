package com.example.gestionnairebibliotheque;

import BackEnd.Bibliotheque;
import BackEnd.Livres.Livre;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;


import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Map;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Bibliotheque bib = new Bibliotheque();
        bib.nouveauFichierMarc();

        ArrayList<Livre> livres = bib.getListeLivres();

        // Total exemplaires
        System.out.println("Total exemplaires : " + livres.size());

        // Ouvrages uniques (groupés par ISBN)
        long ouvragesUniques = livres.stream()
                .map(Livre::getISBN)
                .distinct()
                .count();
        System.out.println("Ouvrages uniques : " + ouvragesUniques);

        // 5 premiers exemplaires
        System.out.println("\n--- 5 premiers exemplaires ---");
        livres.stream()
                .limit(5)
                .forEach(System.out::println);

        // Livres sans ISBN (ceux générés avec "ISBN-")
        long sansISBN = livres.stream()
                .filter(l -> l.getISBN().startsWith("ISBN-"))
                .count();
        System.out.println("\nLivres sans ISBN réel : " + sansISBN);

        // Nombre de livres par nombre d'exemplaires
        System.out.println("\n--- Distribution des exemplaires ---");
        livres.stream()
                .collect(Collectors.groupingBy(Livre::getId, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.println(e.getKey() + " exemplaire(s) : " + e.getValue() + " livres"));
    }

        /*
        GridPane MainLayer = new GridPane();
        Scene scene = new Scene(MainLayer, 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
         */

}
