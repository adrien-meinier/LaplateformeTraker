package com.example.view;

import com.example.controller.GradeDAO;
import com.example.controller.StudentDAO;
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
 * ExportController — exporte tous les étudiants avec leurs notes en CSV.
 */
public class ExportController {

    private final StudentDAO studentDAO = new StudentDAO();
    private final GradeDAO   gradeDAO   = new GradeDAO();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void exporterTousLesEtudiants() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les données étudiants");
        fileChooser.setInitialFileName("etudiants_export_"
                + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier CSV (*.csv)", "*.csv"));

        File file = fileChooser.showSaveDialog(null);
        if (file == null) return;

        try {
            List<StudentModel> etudiants = studentDAO.getAllStudents();

            try (FileWriter writer = new FileWriter(file)) {

                // ── En-tête du fichier ────────────────────────────────────
                writer.write("EXPORT COMPLET DES ÉTUDIANTS\n");
                writer.write("Date d'export;%s\n".formatted(LocalDate.now().format(FMT)));
                writer.write("Nombre d'étudiants;%d\n".formatted(etudiants.size()));
                writer.write("\n");

                // ── En-tête colonnes 
                writer.write("ID;Prénom;Nom;Date de naissance;Âge;Inscrit le;" +
                             "Matière;Note /20;Mention;Moyenne générale\n");

                for (StudentModel s : etudiants) {
                    List<GradeModel> notes = gradeDAO.getGradesByStudentId(s.getId());

                    String prenom     = s.getFirstName();
                    String nom        = s.getLastName();
                    String naissance  = s.getBirthDate() != null ? s.getBirthDate().format(FMT) : "—";
                    String age        = s.getBirthDate() != null
                            ? java.time.Period.between(s.getBirthDate(), LocalDate.now()).getYears() + " ans"
                            : "—";
                    String inscritLe  = s.getCreationDate() != null
                            ? s.getCreationDate().toLocalDate().format(FMT) : "—";

                    if (notes.isEmpty()) {
                        // Ligne sans note
                        writer.write("%d;%s;%s;%s;%s;%s;Aucune note;-;-;-\n".formatted(
                                s.getId(), prenom, nom, naissance, age, inscritLe));
                    } else {
                        double total = 0;
                        for (GradeModel note : notes) total += note.getGrade();
                        double moyenne = total / notes.size();
                        String moyenneStr = "%.2f (%s)".formatted(moyenne, mention((int) Math.round(moyenne)));

                        // Première note sur la ligne étudiant
                        GradeModel first = notes.get(0);
                        writer.write("%d;%s;%s;%s;%s;%s;%s;%d;%s;%s\n".formatted(
                                s.getId(), prenom, nom, naissance, age, inscritLe,
                                first.getSubject(), first.getGrade(),
                                mention(first.getGrade()), moyenneStr));

                        // Notes suivantes : colonnes étudiant vides pour éviter la répétition
                        for (int i = 1; i < notes.size(); i++) {
                            GradeModel note = notes.get(i);
                            writer.write(";;;;;;;%s;%d;%s;\n".formatted(
                                    note.getSubject(), note.getGrade(), mention(note.getGrade())));
                        }
                    }
                }
            }

            Alert ok = new Alert(Alert.AlertType.INFORMATION,
                    "Export réussi !\n%d étudiant(s) exporté(s).\n\n%s"
                            .formatted(etudiants.size(), file.getAbsolutePath()),
                    ButtonType.OK);
            ok.setTitle("Export réussi");
            ok.setHeaderText(null);
            ok.showAndWait();

        } catch (Exception e) {
            Alert err = new Alert(Alert.AlertType.ERROR,
                    "Erreur lors de l'export : " + e.getMessage(), ButtonType.OK);
            err.setTitle("Erreur export");
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