package com.example.view;

import com.example.controller.GradeDAO;
import com.example.model.GradeModel;
import com.example.model.StudentModel;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * BulletinController — génère et télécharge le bulletin CSV
 * d'un étudiant en récupérant ses notes via GradeDAO.
 */
public class BulletinController {

    private final GradeDAO gradeDAO = new GradeDAO();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void telechargerBulletin(StudentModel etudiant) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le bulletin");
        fileChooser.setInitialFileName(
                "bulletin_" + etudiant.getLastName().toLowerCase()
                + "_" + etudiant.getFirstName().toLowerCase() + ".csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier CSV (*.csv)", "*.csv"));

        File file = fileChooser.showSaveDialog(null);
        if (file == null) return;

        try {
            List<GradeModel> notes = gradeDAO.getGradesByStudentId(etudiant.getId());

            try (FileWriter writer = new FileWriter(file)) {

                // ── En-tête ──────────────────────────────────────────────
                writer.write("BULLETIN DE NOTES\n");
                writer.write("Étudiant;%s %s\n".formatted(
                        etudiant.getFirstName(), etudiant.getLastName()));
                if (etudiant.getBirthDate() != null) {
                    writer.write("Date de naissance;%s\n".formatted(
                            etudiant.getBirthDate().format(FMT)));
                }
                writer.write("Âge;%d ans\n".formatted(calculerAge(etudiant.getBirthDate())));
                writer.write("Date d'édition;%s\n".formatted(LocalDate.now().format(FMT)));
                writer.write("\n");

                // ── Notes ────────────────────────────────────────────────
                writer.write("Matière;Note /20;Mention\n");

                if (notes.isEmpty()) {
                    writer.write("Aucune note enregistrée;;\n");
                } else {
                    double total = 0;
                    for (GradeModel note : notes) {
                        writer.write("%s;%d;%s\n".formatted(
                                note.getSubject(),
                                note.getGrade(),
                                mention(note.getGrade())));
                        total += note.getGrade();
                    }
                    double moyenne = total / notes.size();
                    writer.write("\n");
                    writer.write("MOYENNE GÉNÉRALE;%.2f;%s\n".formatted(
                            moyenne, mention((int) Math.round(moyenne))));
                }
            }

            Alert ok = new Alert(Alert.AlertType.INFORMATION,
                    "Bulletin enregistré :\n" + file.getAbsolutePath(), ButtonType.OK);
            ok.setTitle("Téléchargement réussi");
            ok.setHeaderText(null);
            ok.showAndWait();

        } catch (Exception e) {
            Alert err = new Alert(Alert.AlertType.ERROR,
                    "Erreur lors de la génération : " + e.getMessage(), ButtonType.OK);
            err.setTitle("Erreur");
            err.setHeaderText(null);
            err.showAndWait();
        }
    }

    private int calculerAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return java.time.Period.between(birthDate, LocalDate.now()).getYears();
    }

    private String mention(int note) {
        if (note >= 18) return "Très Bien";
        if (note >= 16) return "Bien";
        if (note >= 14) return "Assez Bien";
        if (note >= 10) return "Passable";
        return "Insuffisant";
    }
}