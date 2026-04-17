package com.example.controller;

import com.example.controller.StudentDAO;
import com.example.model.StudentModel;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * ImportController — importe des étudiants depuis un fichier CSV.
 *
 * Format CSV attendu (avec en-tête obligatoire) :
 *   prenom;nom;date_naissance
 *   Alice;Martin;2004-03-12
 *   Bob;Dupont;12/03/2003
 *
 * Règles :
 *  - Si un étudiant (même prénom + même nom) existe déjà → mise à jour de la date de naissance
 *  - Sinon → insertion
 *  - Séparateur : ; (point-virgule)
 *  - Dates acceptées : yyyy-MM-dd  ou  dd/MM/yyyy
 */
public class ImportController {

    private final StudentDAO studentDAO = new StudentDAO();

    private static final DateTimeFormatter FMT_ISO   = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_FR    = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void importerEtudiants() {

        // ── Sélection du fichier ──────────────────────────────────────────
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importer des étudiants (CSV)");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier CSV (*.csv)", "*.csv"));

        File file = fileChooser.showOpenDialog(null);
        if (file == null) return;

        // ── Lecture + traitement ──────────────────────────────────────────
        List<String> errors   = new ArrayList<>();
        int inserted  = 0;
        int updated   = 0;
        int skipped   = 0;
        int lineNum   = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();

                // Ignore ligne vide ou en-tête
                if (line.isEmpty()) continue;
                if (lineNum == 1 && looksLikeHeader(line)) continue;

                String[] cols = line.split(";", -1);
                if (cols.length < 3) {
                    errors.add("Ligne %d ignorée : pas assez de colonnes (%s)"
                            .formatted(lineNum, line));
                    skipped++;
                    continue;
                }

                String prenom = cols[0].trim();
                String nom    = cols[1].trim();
                String datStr = cols[2].trim();

                if (prenom.isEmpty() || nom.isEmpty()) {
                    errors.add("Ligne %d ignorée : prénom ou nom vide.".formatted(lineNum));
                    skipped++;
                    continue;
                }

                LocalDate birthDate = parseDate(datStr);
                if (birthDate == null) {
                    errors.add("Ligne %d ignorée : date invalide '%s' (attendu : yyyy-MM-dd ou dd/MM/yyyy)."
                            .formatted(lineNum, datStr));
                    skipped++;
                    continue;
                }

                // ── Recherche d'un doublon par prénom + nom ───────────────
                StudentModel existing = studentDAO.findByName(prenom, nom);

                if (existing != null) {
                    // Mise à jour de la date de naissance si différente
                    if (!birthDate.equals(existing.getBirthDate())) {
                        studentDAO.updateStudent(
                                existing.getId(), prenom, nom, birthDate);
                        updated++;
                    } else {
                        skipped++; // rien à changer
                    }
                } else {
                    studentDAO.addStudent(prenom, nom, birthDate);
                    inserted++;
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lecture fichier", e.getMessage());
            return;
        }

        // ── Rapport ───────────────────────────────────────────────────────
        StringBuilder report = new StringBuilder();
        report.append("Import terminé !\n\n");
        report.append("✅ Ajoutés   : %d\n".formatted(inserted));
        report.append("🔄 Mis à jour : %d\n".formatted(updated));
        report.append("⏭ Ignorés   : %d\n".formatted(skipped));

        if (!errors.isEmpty()) {
            report.append("\nDétail des lignes ignorées :\n");
            errors.stream().limit(10).forEach(e -> report.append("  • ").append(e).append("\n"));
            if (errors.size() > 10)
                report.append("  … et %d autre(s).\n".formatted(errors.size() - 10));
        }

        showAlert(Alert.AlertType.INFORMATION, "Import CSV", report.toString());
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /** Tente de parser la date dans les deux formats supportés. */
    private LocalDate parseDate(String s) {
        for (DateTimeFormatter fmt : new DateTimeFormatter[]{ FMT_ISO, FMT_FR }) {
            try { return LocalDate.parse(s, fmt); }
            catch (DateTimeParseException ignored) {}
        }
        return null;
    }

    /** Détecte si la ligne ressemble à un en-tête (contient des lettres non-date). */
    private boolean looksLikeHeader(String line) {
        String low = line.toLowerCase();
        return low.contains("prenom") || low.contains("nom") || low.contains("date")
                || low.contains("first") || low.contains("last") || low.contains("birth");
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(null);
        a.getDialogPane().setPrefWidth(480);
        a.showAndWait();
    }
}