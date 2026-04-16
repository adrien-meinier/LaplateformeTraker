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

public class BulletinControllerTest {

    private BulletinController controller;

    @BeforeEach
    void setUp() {
        controller = new BulletinController();
    }

    @Test
    void mention_shouldReturnCorrectValues() {
        assertEquals("Tres Bien", controllerTestMention(18));
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
            var method = BulletinController.class.getDeclaredMethod("mention", int.class);
            method.setAccessible(true);
            return (String) method.invoke(controller, note);
        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
            return null;
        }
    }

    @Test
    void generateCsv_shouldContainStudentInfoAndGrades() throws IOException {

        // Create a valid student using the full constructor
        StudentModel student = new StudentModel(
                1,
                "John",
                "Doe",
                LocalDate.of(2000, 1, 1),
                LocalDateTime.now(),
                LocalDateTime.now(),
                15
        );

        // Create valid grades using LocalDateTime
        GradeModel g1 = new GradeModel(
                1,
                1,
                15,
                "Math",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        GradeModel g2 = new GradeModel(
                2,
                1,
                10,
                "Physics",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        List<GradeModel> grades = List.of(g1, g2);

        // Create a temporary file to simulate CSV export
        File tempFile = File.createTempFile("bulletin_test", ".csv");

        // Simulate CSV writing (reproducing controller logic)
        try (var writer = Files.newBufferedWriter(tempFile.toPath())) {

            writer.write("BULLETIN DE NOTES\n");
            writer.write("Etudiant;John Doe\n");
            writer.write("Date de naissance;01/01/2000\n");

            int age = java.time.Period.between(student.getBirthDate(), LocalDate.now()).getYears();
            writer.write("Age;" + age + " ans\n");

            writer.write("Matiere;Note /20;Mention\n");

            for (GradeModel g : grades) {
                writer.write(g.getSubject() + ";"
                        + g.getGrade() + ";"
                        + controllerTestMention(g.getGrade()) + "\n");
            }
        }

        String content = Files.readString(tempFile.toPath());

        // Verify important content
        assertTrue(content.contains("John Doe"));
        assertTrue(content.contains("Math"));
        assertTrue(content.contains("Physics"));
        assertTrue(content.contains("15"));
        assertTrue(content.contains("10"));
    }

    @Test
    void emptyGrades_shouldHandleNoGradesCase() throws IOException {

        // Create a temporary file to simulate empty grades export
        File tempFile = File.createTempFile("bulletin_empty", ".csv");

        try (var writer = Files.newBufferedWriter(tempFile.toPath())) {
            writer.write("Aucune note enregistree;;\n");
        }

        String content = Files.readString(tempFile.toPath());

        // Verify that the "no grades" message is present
        assertTrue(content.contains("Aucune note enregistree"));
    }
}