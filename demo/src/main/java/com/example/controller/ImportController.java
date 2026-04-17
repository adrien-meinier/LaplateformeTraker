package com.example.controller;

import com.example.DAO.StudentDAO;
import com.example.model.StudentModel;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ImportController {

    private final StudentDAO studentDAO = new StudentDAO();

    private static final DateTimeFormatter FMT_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_FR  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void importerEtudiantsDepuisBulletin() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importer un bulletin étudiant");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV file (*.csv)", "*.csv"));

        File file = fileChooser.showOpenDialog(null);
        if (file == null) return;

        String prenom = null;
        String nom = null;
        LocalDate birthDate = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(";", -1);
                if (cols.length < 2) continue;

                // Format : Etudiant;John Doe
                if (cols[0].equalsIgnoreCase("Etudiant")) {
                    String fullName = cols[1].trim();
                    String[] parts = fullName.split(" ", 2);

                    if (parts.length == 2) {
                        prenom = parts[0].trim();
                        nom    = parts[1].trim();
                    }
                }

                // Format : Date de naissance;01/01/2000
                if (cols[0].equalsIgnoreCase("Date de naissance")) {
                    birthDate = parseDate(cols[1].trim());
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lecture fichier", e.getMessage());
            return;
        }

        // Validation
        if (prenom == null || nom == null || birthDate == null) {
            showAlert(Alert.AlertType.ERROR, "Format invalide",
                    "Impossible d'extraire les informations de l'étudiant.\n" +
                    "Vérifiez que le bulletin contient bien :\n" +
                    "  Etudiant;Prenom;Nom\n" +
                    "  Date;de;naissance;JJ/MM/AAAA");
            return;
        }

        try {
            List<StudentModel> existants = studentDAO.getAllStudents();

            // ───────────────────────────────────────────────
            // 🔥 LIGNE EXACTE où tu dois mettre les variables finales
            // ───────────────────────────────────────────────
            final String fPrenom = prenom;
            final String fNom    = nom;

            StudentModel existing = existants.stream()
                    .filter(s -> s.getFirstName().equalsIgnoreCase(fPrenom)
                              && s.getLastName().equalsIgnoreCase(fNom))
                    .findFirst()
                    .orElse(null);

            if (existing != null) {
                studentDAO.updateStudent(existing.getId(), prenom, nom, birthDate);
                showAlert(Alert.AlertType.INFORMATION, "Import bulletin",
                        "Étudiant mis à jour : " + prenom + " " + nom);
            } else {
                studentDAO.addStudent(prenom, nom, birthDate);
                showAlert(Alert.AlertType.INFORMATION, "Import bulletin",
                        "Nouvel étudiant ajouté : " + prenom + " " + nom);
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur base de données", e.getMessage());
        }
    }

    // ───────────────────────────────────────────────
    // Helpers
    // ───────────────────────────────────────────────

    private LocalDate parseDate(String s) {
        for (DateTimeFormatter fmt : new DateTimeFormatter[]{FMT_ISO, FMT_FR}) {
            try { return LocalDate.parse(s, fmt); }
            catch (DateTimeParseException ignored) {}
        }
        return null;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(null);
        a.getDialogPane().setPrefWidth(480);
        a.showAndWait();
    }
}
