package com.example.controller;

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
 * BulletinController — exports a student's report card to CSV format.
 * Cleaned of all hash/pepper logic.
 */

public class BulletinController{

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
                writer.write("Etudiant;" + etudiant.getFirstName() + " " + etudiant.getLastName() + "\n");

                if (etudiant.getBirthDate() != null) {
                    writer.write("Date de naissance;" + etudiant.getBirthDate().format(FMT) + "\n");
                    int age = Period.between(etudiant.getBirthDate(), LocalDate.now()).getYears();
                    writer.write("Age;" + age + " ans\n");
                }

                writer.write("Date edition;" + LocalDate.now().format(FMT) + "\n");
                writer.write("\n");
                writer.write("Matiere;Note /20;Mention\n");

                if (notes.isEmpty()) {
                    writer.write("Aucune note enregistree;;\n");
                } else {
                    double total = 0;
                    for (GradeModel note : notes) {
                        writer.write(note.getSubject() + ";"
                                + note.getGrade() + ";"
                                + mention(note.getGrade()) + "\n");
                        total += note.getGrade();
                    }
                    double moyenne = total / notes.size();
                    writer.write("\n");
                    writer.write("MOYENNE GENERALE;"
                            + String.format("%.2f", moyenne) + ";"
                            + mention((int) Math.round(moyenne)) + "\n");
                }
            }

            Alert ok = new Alert(Alert.AlertType.INFORMATION,
                    "Bulletin enregistre :\n" + file.getAbsolutePath(), ButtonType.OK);
            ok.setTitle("Telechargement reussi");
            ok.setHeaderText(null);
            ok.showAndWait();

        } catch (Exception e) {
            Alert err = new Alert(Alert.AlertType.ERROR,
                    "Erreur : " + e.getMessage(), ButtonType.OK);
            err.setTitle("Erreur");
            err.setHeaderText(null);
            err.showAndWait();
        }
    }

    private String mention(int note) {
        if (note >= 18) return "Tres Bien";
        if (note >= 16) return "Bien";
        if (note >= 14) return "Assez Bien";
        if (note >= 10) return "Passable";
        return "Insuffisant";
    }
}