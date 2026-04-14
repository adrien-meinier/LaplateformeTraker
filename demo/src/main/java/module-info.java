module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // allows reflection for JavaFX to access private members of the controller and model packages, which is necessary for features like FXML loading and TableView data binding.
    opens com.example.controller to javafx.fxml;
    
    // allows reflection for JavaFX to access private members of the view package, which is necessary for features like FXML loading and UI component manipulation.
    opens com.example.model to javafx.base;

    exports com.example;
}