module com.example.gestionnairebibliotheque {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens com.example.gestionnairebibliotheque to javafx.fxml;
    exports com.example.gestionnairebibliotheque;

    opens BackEnd.Livres to com.google.gson;
    opens BackEnd.Usager to com.google.gson;
    opens BackEnd to com.google.gson;
    opens BackEnd.logic to com.google.gson;
    opens BackEnd.logic.BackgroundTask to com.google.gson;
}