module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // Autorise le FXML à trouver ton contrôleur
    opens com.example.controller to javafx.fxml;
    
    // Autorise la TableView à lire les données de ton modèle
    opens com.example.model to javafx.base;

    exports com.example;
}