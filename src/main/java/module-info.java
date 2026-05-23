module com.example.gestionnairebibliotheque {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens com.example.gestionnairebibliotheque to javafx.fxml;
    exports com.example.gestionnairebibliotheque;
}