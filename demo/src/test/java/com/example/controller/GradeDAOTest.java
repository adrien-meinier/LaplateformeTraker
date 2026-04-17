package com.example.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.example.DAO.GradeDAO;
import com.example.DAO.StudentDAO;
import com.example.model.DatabaseInitializer;
import com.example.model.GradeModel;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GradeDAOTest {

    private static final GradeDAO dao = new GradeDAO();
    private static final StudentDAO studentDAO = new StudentDAO();

    // studentId shared across tests in this class
    private static int studentId;

    // Insert one student and clean grades before each test
    @BeforeEach
    void setUp() throws SQLException {
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM grades;");
            stmt.execute("DELETE FROM student;");
        }
        studentId = studentDAO.addStudent("Test", "Student", LocalDate.of(2000, 1, 1));
    }

    @Test
    @Order(1)
    void addGrade_shouldReturnPositiveId() throws SQLException {
        int id = dao.addGrade(studentId, 15, "Math");
        assertTrue(id > 0, "Generated grade ID must be positive");
    }

    @Test
    @Order(2)
    void getGradeById_shouldReturnCorrectGrade() throws SQLException {
        int id = dao.addGrade(studentId, 12, "Physics");
        GradeModel grade = dao.getGradeById(id);

        assertNotNull(grade);
        assertEquals(studentId, grade.getStudentId());
        assertEquals(12, grade.getGrade());
        assertEquals("Physics", grade.getSubject());
    }

    @Test
    @Order(3)
    void getGradeById_shouldReturnNullForUnknownId() throws SQLException {
        GradeModel grade = dao.getGradeById(999999);
        assertNull(grade, "Unknown grade ID must return null");
    }

    @Test
    @Order(4)
    void updateGrade_shouldModifyValueAndSubject() throws SQLException {
        int id = dao.addGrade(studentId, 10, "History");
        boolean updated = dao.updateGrade(id, 18, "English");

        assertTrue(updated);
        GradeModel grade = dao.getGradeById(id);
        assertEquals(18, grade.getGrade());
        assertEquals("English", grade.getSubject());
    }

    @Test
    @Order(5)
    void updateGrade_shouldReturnFalseForUnknownId() throws SQLException {
        boolean updated = dao.updateGrade(999999, 20, "Math");
        assertFalse(updated, "Update on non-existent grade must return false");
    }

    @Test
    @Order(6)
    void deleteGrade_shouldRemoveFromDatabase() throws SQLException {
        int id = dao.addGrade(studentId, 8, "Chemistry");
        boolean deleted = dao.deleteGrade(id);

        assertTrue(deleted);
        assertNull(dao.getGradeById(id), "Grade must not exist after deletion");
    }

    @Test
    @Order(7)
    void deleteGrade_shouldReturnFalseForUnknownId() throws SQLException {
        boolean deleted = dao.deleteGrade(999999);
        assertFalse(deleted, "Delete on non-existent grade must return false");
    }

    @Test
    @Order(8)
    void getGradesByStudentId_shouldReturnAllGradesForStudent() throws SQLException {
        dao.addGrade(studentId, 14, "Math");
        dao.addGrade(studentId, 16, "Physics");
        dao.addGrade(studentId, 9, "History");

        List<GradeModel> grades = dao.getGradesByStudentId(studentId);
        assertEquals(3, grades.size());
    }

    @Test
    @Order(9)
    void getGradesByStudentId_shouldReturnEmptyListForStudentWithNoGrades() throws SQLException {
        List<GradeModel> grades = dao.getGradesByStudentId(studentId);
        assertTrue(grades.isEmpty(), "Student with no grades must return empty list");
    }

    @Test
    @Order(10)
    void getAllGrades_shouldReturnEveryGradeInDatabase() throws SQLException {
        int studentId2 = studentDAO.addStudent("Other", "Student", LocalDate.of(2001, 5, 10));
        dao.addGrade(studentId, 11, "Math");
        dao.addGrade(studentId2, 17, "English");

        List<GradeModel> all = dao.getAllGrades();
        assertEquals(2, all.size());
    }

    @Test
    @Order(11)
    void getAllGrades_shouldReturnEmptyListWhenTableIsEmpty() throws SQLException {
        List<GradeModel> all = dao.getAllGrades();
        assertTrue(all.isEmpty(), "Empty grades table must return empty list");
    }

    @Test
    @Order(12)
    void getGradeById_shouldContainNonNullDates() throws SQLException {
        int id = dao.addGrade(studentId, 13, "Math");
        GradeModel grade = dao.getGradeById(id);

        assertNotNull(grade.getCreationDate(), "Creation date must not be null");
        assertNotNull(grade.getLastModifiedDate(), "Last modified date must not be null");
    }
}