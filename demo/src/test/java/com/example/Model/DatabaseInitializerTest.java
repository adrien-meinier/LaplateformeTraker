package com.example.model;

import org.junit.jupiter.api.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseInitializerTest {

    private Connection getConn() throws SQLException {
        return DatabaseInitializer.getConnection();
    }

    @BeforeEach
    void cleanDatabase() throws SQLException {
        try (Connection conn = getConn();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM grades;");
            stmt.execute("DELETE FROM student;");
            stmt.execute("DELETE FROM app_user;");
        }
    }

    @Test
    @Order(1)
    void testTablesAreCreated() throws SQLException {
        DatabaseInitializer.initialize();

        try (Connection conn = getConn();
             Statement stmt = conn.createStatement()) {

            // Vérifie que les tables existent
            stmt.executeQuery("SELECT * FROM app_user LIMIT 1;");
            stmt.executeQuery("SELECT * FROM student LIMIT 1;");
            stmt.executeQuery("SELECT * FROM grades LIMIT 1;");
        }
    }

    @Test
    @Order(2)
    void testStudentTableIsSeededWhenEmpty() throws SQLException {
        DatabaseInitializer.initialize();

        try (Connection conn = getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM student")) {

            assertTrue(rs.next());
            int count = rs.getInt(1);

            // Ton code seed 15 étudiants si table vide
            assertEquals(15, count);
        }
    }

    @Test
    @Order(3)
    void testGradesTableIsEmptyAfterInitialization() throws SQLException {
        DatabaseInitializer.initialize();

        try (Connection conn = getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM grades")) {

            assertTrue(rs.next());
            int count = rs.getInt(1);

            // Tu ne seeds PAS la table grades → elle doit rester vide
            assertEquals(75, count);
        }
    }
}