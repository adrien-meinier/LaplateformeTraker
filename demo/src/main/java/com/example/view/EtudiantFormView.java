package com.example.view;

import java.sql.SQLException;
import java.time.LocalDate;

import com.example.controller.GradeDAO;
import com.example.controller.StudentDAO;
import com.example.model.StudentModel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class EtudiantFormView {

    private final StudentDAO studentDao;
    private final GradeDAO gradeDao = new GradeDAO();
    private final StudentModel existing;
    private final Runnable onSaved;

    private TextField tfPrenom, tfNom;
    private DatePicker dpBirthDate;
    private Label lblError;

    public EtudiantFormView(StudentDAO dao, StudentModel existing, Runnable onSaved) {
        this.studentDao = dao;
        this.existing = existing;
        this.onSaved = onSaved;
    }

    public Node build() {
        boolean isEdit = (existing != null);

        VBox card = new VBox(20);
        card.setPadding(new Insets(30, 35, 30, 35));
        card.setStyle(StyleFactory.cardBg());
        card.setMaxWidth(480);
        card.setAlignment(Pos.TOP_LEFT);

        Label title = new Label(isEdit ? "✏️ Modifier l'étudiant" : "➕ Ajouter un étudiant");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: " + StyleFactory.C_PRIMARY + ";");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col, col);

        // Champs sécurisés
        tfPrenom = createStyledTextField(isEdit && existing.getFirstName() != null ? existing.getFirstName() : "");
        tfNom    = createStyledTextField(isEdit && existing.getLastName()  != null ? existing.getLastName()  : "");

        LocalDate birthInit = LocalDate.of(2000, 1, 1);
        if (isEdit && existing.getBirthDate() != null) {
            birthInit = existing.getBirthDate();
        }
        dpBirthDate = new DatePicker(birthInit);
        dpBirthDate.setPrefHeight(42);

        // Moyenne sécurisée
        double avg = (isEdit ? existing.getAverageGrade() : 0.0);
        Label lblAverage = new Label(String.format("%.2f", avg));
        lblAverage.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        grid.add(fieldGroup("Prénom *", tfPrenom), 0, 0);
        grid.add(fieldGroup("Nom *", tfNom), 1, 0);
        grid.add(fieldGroup("Date de naissance *", dpBirthDate), 0, 1, 2, 1);
        grid.add(fieldGroup("Moyenne", lblAverage), 0, 2, 2, 1);

        lblError = new Label();
        lblError.setStyle("-fx-text-fill: " + StyleFactory.C_DANGER + "; -fx-font-size: 13px; -fx-font-weight: bold;");
        lblError.setWrapText(true);
        lblError.setVisible(false);

        // button to add grade only visible in edit mode (student must exist to add grades)
        Button btnAddGrade = null;
        if (isEdit) {
            btnAddGrade = StyleFactory.primaryBtn("➕ Ajouter une note");
            btnAddGrade.setPrefHeight(40);
            btnAddGrade.setOnAction(e -> openAddGradeDialog(existing));
        }

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

        if (btnAddGrade != null)
            card.getChildren().addAll(title, grid, btnAddGrade, lblError, actions);
        else
            card.getChildren().addAll(title, grid, lblError, actions);

        StackPane wrapper = new StackPane(card);
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle(StyleFactory.rootBg());
        wrapper.setAlignment(Pos.CENTER);

        return wrapper;
    }
// Method to save the student data, either by adding a new student or updating an existing one.
    private void doSave() {
        String prenom = tfPrenom.getText().trim();
        String nom = tfNom.getText().trim();
        LocalDate birth = dpBirthDate.getValue();

        if (prenom.isEmpty() || nom.isEmpty() || birth == null) {
            showError("Veuillez remplir tous les champs obligatoires (*).");
            return;
        }

        try {
            if (existing == null) {
                studentDao.addStudent(prenom, nom, birth);
            } else {
                studentDao.updateStudent(existing.getId(), prenom, nom, birth);
            }
            onSaved.run();
        } catch (SQLException ex) {
            showError("Erreur SQL : " + ex.getMessage());
        }
    }
// Opens a dialog to add a grade for the specified student. Validates the input and saves the grade to the database.
    private void openAddGradeDialog(StudentModel student) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une note");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField tfGrade = new TextField();
        tfGrade.setPromptText("Note (0 à 20)");

        TextField tfSubject = new TextField();
        tfSubject.setPromptText("Matière");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        grid.addRow(0, new Label("Note :"), tfGrade);
        grid.addRow(1, new Label("Matière :"), tfSubject);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    int grade = Integer.parseInt(tfGrade.getText().trim());
                    String subject = tfSubject.getText().trim();

                    if (subject.isEmpty()) {
                        showError("La matière ne peut pas être vide.");
                        return null;
                    }
                    if (grade < 0 || grade > 20) {
                        showError("La note doit être comprise entre 0 et 20.");
                        return null;
                    }

                    gradeDao.addGrade(student.getId(), grade, subject);
                    onSaved.run();
                } catch (Exception ex) {
                    showError("Erreur : " + ex.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
// Utility method to create a styled TextField with focus effects.
    private TextField createStyledTextField(String value) {
        TextField tf = new TextField(value);
        tf.setStyle(StyleFactory.textFieldStyle());
        tf.setPrefHeight(42);
        tf.focusedProperty().addListener((obs, old, focused) ->
                tf.setStyle(focused ? StyleFactory.textFieldFocusStyle() : StyleFactory.textFieldStyle())
        );
        return tf;
    }

    private VBox fieldGroup(String labelText, Node control) {
        Label lbl = new Label(labelText);
        lbl.setStyle(StyleFactory.labelStyle());
        return new VBox(6, lbl, control);
    }

    private void showError(String msg) {
        lblError.setText("Error : " + msg);
        lblError.setVisible(true);
    }
}
