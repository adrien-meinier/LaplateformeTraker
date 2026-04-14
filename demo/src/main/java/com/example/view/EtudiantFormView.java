package com.example.view;

import com.example.controller.StudentDAO;
import com.example.model.StudentModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * EtudiantFormView — Vue de création et modification d'un étudiant.
 * Rectifiée pour une intégration propre avec le StyleFactory.
 */
public class EtudiantFormView {

    private final StudentDAO dao;
    private final StudentModel existing;
    private final Runnable onSaved;

    private TextField tfPrenom, tfNom;
    private DatePicker dpBirthDate;
    private Label lblError;

    public EtudiantFormView(StudentDAO dao, StudentModel existing, Runnable onSaved) {
        this.dao = dao;
        this.existing = existing;
        this.onSaved = onSaved;
    }

    public Node build() {
        boolean isEdit = (existing != null);

        // principal card
        VBox card = new VBox(20);
        card.setPadding(new Insets(30, 35, 30, 35));
        card.setStyle(StyleFactory.cardBg());
        card.setMaxWidth(480);
        card.setAlignment(Pos.TOP_LEFT);

        // title with dynamic text and color
        Label title = new Label(isEdit ? "✏️ Modifier l'étudiant" : "➕ Ajouter un étudiant");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: " + StyleFactory.C_PRIMARY + ";");

        // form grid
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col, col);

        //  Form fields with pre-filled values in edit mode
        tfPrenom = createStyledTextField(isEdit ? existing.getFirstName() : "");
        tfPrenom.setPromptText("Ex: Jean");
        
        tfNom = createStyledTextField(isEdit ? existing.getLastName() : "");
        tfNom.setPromptText("Ex: Dupont");

        dpBirthDate = new DatePicker(isEdit && existing.getBirthDate() != null
                ? existing.getBirthDate()
                : LocalDate.of(2000, 1, 1));
        dpBirthDate.setPrefHeight(42);
        dpBirthDate.setMaxWidth(Double.MAX_VALUE);
        dpBirthDate.getEditor().setStyle("-fx-font-size: 14px;");

        // grouping fields with labels
        grid.add(fieldGroup("Prénom *", tfPrenom), 0, 0);
        grid.add(fieldGroup("Nom *", tfNom), 1, 0);
        grid.add(fieldGroup("Date de naissance *", dpBirthDate), 0, 1, 2, 1);

        //  Error label (hidden by default)
        lblError = new Label();
        lblError.setStyle("-fx-text-fill: " + StyleFactory.C_DANGER + "; -fx-font-size: 13px; -fx-font-weight: bold;");
        lblError.setWrapText(true);
        lblError.setVisible(false);

        //  Action buttons with dynamic text and styles
        Button btnSave = isEdit ? StyleFactory.primaryBtn("💾 Enregistrer")
                                : StyleFactory.successBtn("➕ Ajouter l'étudiant");
        btnSave.setPrefHeight(45);
        btnSave.setPrefWidth(180);
        btnSave.setOnAction(e -> doSave());

        Button btnCancel = StyleFactory.secondaryBtn("Annuler");
        btnCancel.setPrefHeight(45);
        btnCancel.setPrefWidth(100);
        btnCancel.setOnAction(e -> onSaved.run());

        HBox actions = new HBox(12, btnCancel, btnSave);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(10, 0, 0, 0));

        //  Assembling the card
        card.getChildren().addAll(title, grid, lblError, actions);

        //  Wrapper with padding and background
        StackPane wrapper = new StackPane(card);
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle(StyleFactory.rootBg());
        wrapper.setAlignment(Pos.CENTER);

        return wrapper;
    }

    private void doSave() {
        String prenom = tfPrenom.getText().trim();
        String nom = tfNom.getText().trim();
        LocalDate birth = dpBirthDate.getValue();

        //Simple validation of required fields
        if (prenom.isEmpty() || nom.isEmpty() || birth == null) {
            showError("Veuillez remplir tous les champs obligatoires (*).");
            return;
        }

        try {
            if (existing == null) {
                // creation mode
                dao.addStudent(prenom, nom, birth);
            } else {
                // edit mode
                dao.updateStudent(existing.getId(), prenom, nom, birth);
            }
            // refresh the list and close the form
            onSaved.run();
        } catch (SQLException ex) {
            showError("Erreur SQL : " + ex.getMessage());
        }
    }

    private TextField createStyledTextField(String value) {
        TextField tf = new TextField(value);
        tf.setStyle(StyleFactory.textFieldStyle());
        tf.setPrefHeight(42);
        
        // Effet de focus dynamique
        tf.focusedProperty().addListener((obs, old, focused) -> 
            tf.setStyle(focused ? StyleFactory.textFieldFocusStyle() : StyleFactory.textFieldStyle())
        );
        return tf;
    }

    private VBox fieldGroup(String labelText, Node control) {
        Label lbl = new Label(labelText);
        lbl.setStyle(StyleFactory.labelStyle());
        VBox group = new VBox(6, lbl, control);
        group.setFillWidth(true);
        return group;
    }

    private void showError(String msg) {
        lblError.setText("⚠️ " + msg);
        lblError.setVisible(true);
    }
}