package com.example.view;

import com.example.model.Student;
import com.example.service.StudentService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;

/**
 * EtudiantFormView — formulaire ajout / modification, lié à la DB.
 */
public class EtudiantFormView {

    private final StudentService service;
    private final Student        existing;
    private final Runnable       onSaved;

    private TextField tfPrenom, tfNom;
    private DatePicker dpBirthDate;
    private Label      lblError;

    public EtudiantFormView(StudentService service, Student existing, Runnable onSaved) {
        this.service  = service;
        this.existing = existing;
        this.onSaved  = onSaved;
    }

    public Node build() {
        boolean isEdit = existing != null;

        VBox card = new VBox(18);
        card.setPadding(new Insets(28, 32, 28, 32));
        card.setStyle(StyleFactory.cardBg());
        card.setMaxWidth(460);

        Label title = new Label(isEdit ? "✏️ Modifier l'étudiant" : "➕ Ajouter un étudiant");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setStyle("-fx-text-fill: " + StyleFactory.C_PRIMARY + ";");

        // ── Grille 
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);
        grid.setFillWidth(true);

        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(50);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(c1, c2);

        tfPrenom    = field(isEdit ? existing.getFirstName() : "");
        tfNom       = field(isEdit ? existing.getLastName()  : "");
        dpBirthDate = new DatePicker(isEdit && existing.getBirthDate() != null
                        ? existing.getBirthDate() : LocalDate.of(2000, 1, 1));
        dpBirthDate.setPrefHeight(42);
        dpBirthDate.setMaxWidth(Double.MAX_VALUE);

        grid.add(fieldGroup("Prénom *",          tfPrenom),    0, 0);
        grid.add(fieldGroup("Nom *",             tfNom),       1, 0);
        grid.add(fieldGroup("Date de naissance *", dpBirthDate), 0, 1, 2, 1);

        // ── Erreur 
        lblError = new Label();
        lblError.setStyle("-fx-text-fill: " + StyleFactory.C_DANGER + "; -fx-font-size: 12px;");
        lblError.setVisible(false);
        lblError.setWrapText(true);

        // ── Boutons ──────────────────────────────────────────────────────
        Button btnSave   = isEdit ? StyleFactory.primaryBtn("💾 Enregistrer")
                                  : StyleFactory.successBtn("➕ Ajouter");
        Button btnCancel = StyleFactory.secondaryBtn("Annuler");
        btnSave.setPrefWidth(160);
        btnCancel.setPrefWidth(110);
        btnSave.setOnAction(e -> doSave());
        btnCancel.setOnAction(e -> onSaved.run());

        HBox buttons = new HBox(12, btnCancel, btnSave);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(title, grid, lblError, buttons);

        StackPane wrapper = new StackPane(card);
        wrapper.setPadding(new Insets(16));
        wrapper.setStyle(StyleFactory.rootBg());
        return wrapper;
    }

    private void doSave() {
        if (tfPrenom.getText().trim().isEmpty() || tfNom.getText().trim().isEmpty()) {
            showError("Le prénom et le nom sont obligatoires.");
            return;
        }
        if (dpBirthDate.getValue() == null) {
            showError("La date de naissance est obligatoire.");
            return;
        }
        try {
            Student s = existing != null ? existing : new Student();
            s.setFirstName(tfPrenom.getText().trim());
            s.setLastName (tfNom.getText().trim());
            s.setBirthDate(dpBirthDate.getValue());

            if (existing != null) service.updateStudent(s);
            else                  service.addStudent(s);

            onSaved.run();
        } catch (Exception ex) {
            showError("Erreur : " + ex.getMessage());
        }
    }

    private TextField field(String value) {
        TextField tf = new TextField(value);
        tf.setStyle(StyleFactory.textFieldStyle());
        tf.setPrefHeight(42);
        tf.focusedProperty().addListener((obs, old, focused) ->
                tf.setStyle(focused ? StyleFactory.textFieldFocusStyle()
                                    : StyleFactory.textFieldStyle()));
        return tf;
    }

    private VBox fieldGroup(String labelText, Node control) {
        Label lbl = new Label(labelText);
        lbl.setStyle(StyleFactory.labelStyle());
        VBox box = new VBox(4, lbl, control);
        box.setFillWidth(true);
        return box;
    }

    private void showError(String msg) {
        lblError.setText("⚠ " + msg);
        lblError.setVisible(true);
    }
}