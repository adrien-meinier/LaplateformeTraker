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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * ImportController — imports students from a CSV file.
 *
 * Expected CSV format (header required):
 *   firstName;lastName;birthDate
 *   Alice;Martin;2004-03-12
 *   Bob;Dupont;12/03/2003
 *
 * Rules:
 *  - If a student (same firstName + lastName) already exists → update birth date
 *  - Otherwise → insert
 *  - Separator: ; (semicolon)
 *  - Accepted date formats: yyyy-MM-dd  or  dd/MM/yyyy
 */
public class ImportController {

    private final StudentDAO studentDAO = new StudentDAO();

    private static final DateTimeFormatter FMT_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_FR  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void importerEtudiants() {

        // ── File selection ────────────────────────────────────────────────
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import students (CSV)");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV file (*.csv)", "*.csv"));

        File file = fileChooser.showOpenDialog(null);
        if (file == null) return;

        // ── Load all existing students into memory ────────────────────────
        List<StudentModel> existants;
        try {
            existants = studentDAO.getAllStudents();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database error", e.getMessage());
            return;
        }

        // ── Read and process lines ────────────────────────────────────────
        List<String> errors = new ArrayList<>();
        int inserted = 0;
        int updated  = 0;
        int skipped  = 0;
        int lineNum  = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();

                // Skip empty lines and header
                if (line.isEmpty()) continue;
                if (lineNum == 1 && looksLikeHeader(line)) continue;

                String[] cols = line.split(";", -1);
                if (cols.length < 3) {
                    errors.add("Line %d skipped: not enough columns (%s)".formatted(lineNum, line));
                    skipped++;
                    continue;
                }

                String prenom = cols[0].trim();
                String nom    = cols[1].trim();
                String datStr = cols[2].trim();

                if (prenom.isEmpty() || nom.isEmpty()) {
                    errors.add("Line %d skipped: first name or last name is empty.".formatted(lineNum));
                    skipped++;
                    continue;
                }

                LocalDate birthDate = parseDate(datStr);
                if (birthDate == null) {
                    errors.add("Line %d skipped: invalid date '%s' (expected: yyyy-MM-dd or dd/MM/yyyy)."
                            .formatted(lineNum, datStr));
                    skipped++;
                    continue;
                }

                // ── Look for a duplicate in the in-memory list ────────────
                StudentModel existing = existants.stream()
                        .filter(s -> s.getFirstName().equalsIgnoreCase(prenom)
                                  && s.getLastName().equalsIgnoreCase(nom))
                        .findFirst()
                        .orElse(null);

                try {
                    if (existing != null) {
                        // Student exists → update
                        studentDAO.updateStudent(existing.getId(), prenom, nom, birthDate);
                        updated++;
                    } else {
                        // New student → insert
                        studentDAO.addStudent(prenom, nom, birthDate);
                        inserted++;
                    }
                } catch (Exception e) {
                    errors.add("Line %d database error: %s".formatted(lineNum, e.getMessage()));
                    skipped++;
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "File read error", e.getMessage());
            return;
        }

        // ── Summary report ────────────────────────────────────────────────
        StringBuilder report = new StringBuilder();
        report.append("Import complete!\n\n");
        report.append("✅ Inserted  : %d\n".formatted(inserted));
        report.append("🔄 Updated   : %d\n".formatted(updated));
        report.append("⏭ Skipped   : %d\n".formatted(skipped));

        if (!errors.isEmpty()) {
            report.append("\nSkipped lines detail:\n");
            errors.stream().limit(10).forEach(e -> report.append("  • ").append(e).append("\n"));
            if (errors.size() > 10)
                report.append("  … and %d more.\n".formatted(errors.size() - 10));
        }

        showAlert(Alert.AlertType.INFORMATION, "CSV Import", report.toString());
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /** Tries to parse the date using both supported formats. */
    private LocalDate parseDate(String s) {
        for (DateTimeFormatter fmt : new DateTimeFormatter[]{ FMT_ISO, FMT_FR }) {
            try { return LocalDate.parse(s, fmt); }
            catch (DateTimeParseException ignored) {}
        }
        return null;
    }

    /** Detects whether the line looks like a header row. */
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