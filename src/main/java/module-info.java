module com.example.gestionnairebibliotheque {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.gestionnairebibliotheque to javafx.fxml;
    exports com.example.gestionnairebibliotheque;
}