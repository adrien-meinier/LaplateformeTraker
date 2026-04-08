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

/**
 * EtudiantFormView — formulaire d'ajout ou de modification d'un étudiant.
 *
 * Réutilisé dans deux contextes :
 * <ul>
 *   <li>Dans un dialog modal (depuis {@link EtudiantsView})</li>
 *   <li>En vue principale (depuis le bouton "Ajouter" de la sidebar)</li>
 * </ul>
 * Le callback {@code onSaved} est invoqué après chaque sauvegarde réussie.
 */
public class EtudiantFormView {

    private final StudentService service;
    private final Student        existing;   
    private final Runnable       onSaved;

    // Champs
    private TextField  tfPrenom, tfNom, tfEmail, tfPhone;
    private Spinner<Integer> spAge;
    private Slider     slGrade;
    private Label      lblGradeVal, lblError;

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

        // Titre
        Label title = new Label(isEdit ? "✏️ Modifier l'étudiant" : "➕ Ajouter un étudiant");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setStyle("-fx-text-fill: " + StyleFactory.C_PRIMARY + ";");

        // ── Champs 
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);
        grid.setFillWidth(true);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        // Prénom
        tfPrenom = field(isEdit ? existing.getFirstName() : "");
        // Nom
        tfNom = field(isEdit ? existing.getLastName() : "");
        // Email
        tfEmail = field(isEdit && existing.getEmail() != null ? existing.getEmail() : "");
        tfEmail.setPromptText("ex: alice@example.com");
        // Téléphone
        tfPhone = field(isEdit && existing.getPhone() != null ? existing.getPhone() : "");
        tfPhone.setPromptText("ex: 06 12 34 56 78");

        // Âge
        spAge = new Spinner<>(1, 120, isEdit ? existing.getAge() : 18);
        spAge.setEditable(true);
        spAge.setPrefHeight(42);
        spAge.setStyle("-fx-font-size: 13px;");
        spAge.setMaxWidth(Double.MAX_VALUE);

        // Note (slider)
        double initGrade = isEdit ? existing.getGrade() : 10.0;
        slGrade = new Slider(0, 20, initGrade);
        slGrade.setShowTickMarks(true);
        slGrade.setShowTickLabels(true);
        slGrade.setMajorTickUnit(5);
        slGrade.setBlockIncrement(0.5);
        slGrade.setSnapToTicks(false);

        lblGradeVal = new Label(String.format("%.2f / 20", initGrade));
        lblGradeVal.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblGradeVal.setStyle("-fx-text-fill: " + gradeColor(initGrade) + ";");

        slGrade.valueProperty().addListener((obs, old, val) -> {
            lblGradeVal.setText(String.format("%.2f / 20", val.doubleValue()));
            lblGradeVal.setStyle("-fx-text-fill: " + gradeColor(val.doubleValue()) + ";");
        });

        // Grille
        grid.add(fieldGroup("Prénom *",    tfPrenom), 0, 0);
        grid.add(fieldGroup("Nom *",       tfNom),    1, 0);
        grid.add(fieldGroup("Âge *",       spAge),    0, 1);
        grid.add(fieldGroup("Téléphone",   tfPhone),  1, 1);
        grid.add(fieldGroup("Email",       tfEmail),  0, 2, 2, 1);

        // Slider note
        VBox noteBox = new VBox(6);
        Label noteLabel = new Label("Note (sur 20) *");
        noteLabel.setStyle(StyleFactory.labelStyle());
        HBox sliderRow = new HBox(12, slGrade, lblGradeVal);
        sliderRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(slGrade, Priority.ALWAYS);
        noteBox.getChildren().addAll(noteLabel, sliderRow);

        // Message d'erreur
        lblError = new Label();
        lblError.setStyle("-fx-text-fill: " + StyleFactory.C_DANGER + "; -fx-font-size: 12px;");
        lblError.setVisible(false);
        lblError.setWrapText(true);

        // Boutons
        Button btnSave   = isEdit ? StyleFactory.primaryBtn("💾 Enregistrer")
                                  : StyleFactory.successBtn("➕ Ajouter");
        Button btnCancel = StyleFactory.secondaryBtn("Annuler");
        btnSave.setPrefWidth(160);
        btnCancel.setPrefWidth(110);

        btnSave.setOnAction(e -> doSave());
        btnCancel.setOnAction(e -> onSaved.run());  // ferme sans sauvegarder

        HBox buttons = new HBox(12, btnCancel, btnSave);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(title, grid, noteBox, lblError, buttons);

        // Centrage dans un wrapper
        StackPane wrapper = new StackPane(card);
        wrapper.setPadding(new Insets(16));
        wrapper.setStyle(StyleFactory.rootBg());
        return wrapper;
    }

    // ── Sauvegarde ────────────────────────────────────────────────────────

    private void doSave() {
        // Validation
        if (tfPrenom.getText().trim().isEmpty() || tfNom.getText().trim().isEmpty()) {
            showError("Le prénom et le nom sont obligatoires.");
            return;
        }

        try {
            Student s = existing != null ? existing : new Student();
            s.setFirstName(tfPrenom.getText().trim());
            s.setLastName(tfNom.getText().trim());
            s.setAge(spAge.getValue());
            s.setGrade(slGrade.getValue());
            s.setEmail(tfEmail.getText().trim().isEmpty() ? null : tfEmail.getText().trim());
            s.setPhone(tfPhone.getText().trim().isEmpty() ? null : tfPhone.getText().trim());

            if (existing != null) {
                service.updateStudent(s);
            } else {
                service.addStudent(s);
            }
            onSaved.run();

        } catch (Exception ex) {
            showError("Erreur lors de la sauvegarde : " + ex.getMessage());
        }
    }

    // ── Helpers 

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

    private String gradeColor(double grade) {
        return grade >= 10 ? StyleFactory.C_SUCCESS : StyleFactory.C_DANGER;
    }
}