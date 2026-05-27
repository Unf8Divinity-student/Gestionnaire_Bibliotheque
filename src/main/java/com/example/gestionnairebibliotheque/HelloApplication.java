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
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException{

        GridPane MainLayer = new GridPane();
        Scene scene = new Scene(MainLayer, 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}