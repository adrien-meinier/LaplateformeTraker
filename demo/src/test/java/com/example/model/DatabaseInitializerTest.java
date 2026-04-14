package com.example.model;

import org.junit.jupiter.api.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

// A class to test the database initialization and seeding

// TestMethodOrder allows to execute tests in a certain order
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseInitializerTest {

    // Connect to the database
    private Connection getConn() throws SQLException {
        return DatabaseInitializer.getConnection();
    }

    // BeforeEach : this method will be executed before each test
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
    // Order() : the order in which tests will be executed
    @Order(1)
    void testTablesAreCreated() throws SQLException {
        DatabaseInitializer.initialize();

        try (Connection conn = getConn();
             Statement stmt = conn.createStatement()) {

            // Test that the tables exist
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

            // Check if 15 users have been seeded correctly
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

            // Check if 5 grades x 15 users have been seeded
            assertEquals(75, count);
        }
    }
}