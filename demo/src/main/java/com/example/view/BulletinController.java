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
import java.time.Period;
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

                writer.write("BULLETIN DE NOTES\n");
                writer.write("Étudiant;" + etudiant.getFirstName() + " " + etudiant.getLastName() + "\n");
                if (etudiant.getBirthDate() != null) {
                    writer.write("Date de naissance;" + etudiant.getBirthDate().format(FMT) + "\n");
                    int age = Period.between(etudiant.getBirthDate(), LocalDate.now()).getYears();
                    writer.write("Âge;" + age + " ans\n");
                }
                writer.write("Date d'édition;" + LocalDate.now().format(FMT) + "\n");
                writer.write("\n");
                writer.write("Matière;Note /20;Mention\n");

                if (notes.isEmpty()) {
                    writer.write("Aucune note enregistrée;;\n");
                } else {
                    double total = 0;
                    for (GradeModel note : notes) {
                        writer.write(note.getSubject() + ";" + note.getGrade()
                                + ";" + mention(note.getGrade()) + "\n");
                        total += note.getGrade();
                    }
                    double moyenne = total / notes.size();
                    writer.write("\n");
                    writer.write("MOYENNE GÉNÉRALE;" + String.format("%.2f", moyenne)
                            + ";" + mention((int) Math.round(moyenne)) + "\n");
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

    private String mention(int note) {
        if (note >= 18) return "Très Bien";
        if (note >= 16) return "Bien";
        if (note >= 14) return "Assez Bien";
        if (note >= 10) return "Passable";
        return "Insuffisant";
    }
}