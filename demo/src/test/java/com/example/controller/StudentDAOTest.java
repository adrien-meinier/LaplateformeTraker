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

import com.example.DAO.StudentDAO;
import com.example.model.DatabaseInitializer;
import com.example.model.StudentModel;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentDAOTest {

    private static final StudentDAO dao = new StudentDAO();

    // Clean student table before each test
    @BeforeEach
    void cleanStudents() throws SQLException {
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM grades;");
            stmt.execute("DELETE FROM student;");
        }
    }

    @Test
    @Order(1)
    void addStudent_shouldReturnGeneratedId() throws SQLException {
        int id = dao.addStudent("Alice", "Dupont", LocalDate.of(2000, 6, 15));
        assertTrue(id > 0, "Generated ID must be positive");
    }

    @Test
    @Order(2)
    void getStudentById_shouldReturnCorrectStudent() throws SQLException {
        int id = dao.addStudent("Bob", "Martin", LocalDate.of(1999, 3, 22));
        StudentModel student = dao.getStudentById(id);

        assertNotNull(student);
        assertEquals("Bob", student.getFirstName());
        assertEquals("Martin", student.getLastName());
        assertEquals(LocalDate.of(1999, 3, 22), student.getBirthDate());
    }

    @Test
    @Order(3)
    void getStudentById_shouldReturnNullForUnknownId() throws SQLException {
        StudentModel student = dao.getStudentById(999999);
        assertNull(student, "Unknown ID must return null");
    }

    @Test
    @Order(4)
    void updateStudent_shouldModifyFieldsInDatabase() throws SQLException {
        int id = dao.addStudent("Clara", "Bernard", LocalDate.of(2001, 1, 10));
        boolean updated = dao.updateStudent(id, "Clara", "Renard", LocalDate.of(2001, 1, 10));

        assertTrue(updated);
        StudentModel student = dao.getStudentById(id);
        assertEquals("Renard", student.getLastName());
    }

    @Test
    @Order(5)
    void updateStudent_shouldReturnFalseForUnknownId() throws SQLException {
        boolean updated = dao.updateStudent(999999, "Ghost", "User", LocalDate.now());
        assertFalse(updated, "Update on non-existent ID must return false");
    }

    @Test
    @Order(6)
    void deleteStudent_shouldRemoveFromDatabase() throws SQLException {
        int id = dao.addStudent("David", "Leroy", LocalDate.of(2003, 5, 5));
        boolean deleted = dao.deleteStudent(id);

        assertTrue(deleted);
        assertNull(dao.getStudentById(id), "Student must not exist after deletion");
    }

    @Test
    @Order(7)
    void deleteStudent_shouldReturnFalseForUnknownId() throws SQLException {
        boolean deleted = dao.deleteStudent(999999);
        assertFalse(deleted, "Delete on non-existent ID must return false");
    }

    @Test
    @Order(8)
    void getAllStudents_shouldReturnAllInsertedStudents() throws SQLException {
        dao.addStudent("Emma", "Moreau", LocalDate.of(2002, 7, 20));
        dao.addStudent("Florian", "Simon", LocalDate.of(2001, 9, 3));

        List<StudentModel> students = dao.getAllStudents();
        assertEquals(2, students.size());
    }

    @Test
    @Order(9)
    void getAllStudents_shouldReturnEmptyListWhenTableIsEmpty() throws SQLException {
        List<StudentModel> students = dao.getAllStudents();
        assertTrue(students.isEmpty(), "Empty table must return empty list");
    }
}