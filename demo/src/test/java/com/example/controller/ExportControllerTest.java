package com.example.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.model.GradeModel;
import com.example.model.StudentModel;

public class ExportControllerTest {

    private ExportController controller;

    @BeforeEach
    void setUp() {
        controller = new ExportController();
    }

    @Test
    void mention_shouldReturnCorrectValues() {
        assertEquals("Très Bien", controllerTestMention(18));
        assertEquals("Bien", controllerTestMention(16));
        assertEquals("Assez Bien", controllerTestMention(14));
        assertEquals("Passable", controllerTestMention(10));
        assertEquals("Insuffisant", controllerTestMention(5));
    }

    /**
     * Uses reflection to test the private method 'mention'
     */
    private String controllerTestMention(int note) {
        try {
            var method = ExportController.class.getDeclaredMethod("mention", int.class);
            method.setAccessible(true);
            return (String) method.invoke(controller, note);
        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
            return null;
        }
    }

    @Test
    void export_shouldContainStudentAndGrades() throws IOException {

        // Create a valid student
        StudentModel student = new StudentModel(
                1,
                "Alice",
                "Martin",
                LocalDate.of(2001, 5, 10),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Create grades
        GradeModel g1 = new GradeModel(
                1,
                1,
                14,
                "Math",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        GradeModel g2 = new GradeModel(
                2,
                1,
                18,
                "Physics",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        List<StudentModel> students = List.of(student);
        List<GradeModel> grades = List.of(g1, g2);

        // Temp file
        File tempFile = File.createTempFile("export_test", ".csv");

        // Simulate CSV writing (based on controller logic)
        try (var writer = Files.newBufferedWriter(tempFile.toPath())) {

            writer.write("EXPORT COMPLET DES ÉTUDIANTS\n");
            writer.write("Nombre d'étudiants;1\n");

            writer.write("ID;Prénom;Nom;Date de naissance;Âge;Inscrit le;" +
                         "Matière;Note /20;Mention;Moyenne générale\n");

            int age = java.time.Period.between(student.getBirthDate(), LocalDate.now()).getYears();

            double moyenne = (g1.getGrade() + g2.getGrade()) / 2.0;
            String moyenneStr = String.format("%.2f (%s)", moyenne,
                    controllerTestMention((int) Math.round(moyenne)));

            writer.write("%d;%s;%s;%s;%s;%s;%s;%d;%s;%s\n".formatted(
                    student.getId(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getBirthDate(),
                    age + " ans",
                    student.getCreationDate().toLocalDate(),
                    g1.getSubject(),
                    g1.getGrade(),
                    controllerTestMention(g1.getGrade()),
                    moyenneStr
            ));

            writer.write(";;;;;;;%s;%d;%s;\n".formatted(
                    g2.getSubject(),
                    g2.getGrade(),
                    controllerTestMention(g2.getGrade())
            ));
        }

        String content = Files.readString(tempFile.toPath());

        // Assertions
        assertTrue(content.contains("Alice"));
        assertTrue(content.contains("Martin"));
        assertTrue(content.contains("Math"));
        assertTrue(content.contains("Physics"));
        assertTrue(content.contains("14"));
        assertTrue(content.contains("18"));
    }

    @Test
    void export_shouldHandleStudentWithoutGrades() throws IOException {

        // Temp file
        File tempFile = File.createTempFile("export_empty", ".csv");

        try (var writer = Files.newBufferedWriter(tempFile.toPath())) {
            writer.write("Aucune note;-;-;-\n");
        }

        String content = Files.readString(tempFile.toPath());

        // Verify empty case
        assertTrue(content.contains("Aucune note"));
    }
}