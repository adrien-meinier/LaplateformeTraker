package com.example.model;

import org.junit.jupiter.api.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

/* Tests for DatabaseInitializer.
Ensures tables are created, seeded correctly, and migrations behave as expected. */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseInitializerTest {

    // Utility method to obtain a connection
    private Connection getConn() throws SQLException {
        return DatabaseInitializer.getConnection();
    }

    // Clean database before each test to ensure deterministic behavior.
     
    @BeforeEach
    void cleanDatabase() throws SQLException {
        try (Connection conn = getConn();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM grades;");
            stmt.execute("DELETE FROM student;");
            stmt.execute("DELETE FROM app_user;");
        }
    }

    // Test database creation method
    @Test
    @Order(0)
    void testCreateDatabaseIfNotExists() throws SQLException {
        DatabaseInitializer.createDatabaseIfNotExists();
    }

    // Test that tables are created
    @Test
    @Order(1)
    void testTablesAreCreated() throws SQLException {
        DatabaseInitializer.initialize();

        try (Connection conn = getConn();
             Statement stmt = conn.createStatement()) {

            stmt.executeQuery("SELECT * FROM app_user LIMIT 1;");
            stmt.executeQuery("SELECT * FROM student LIMIT 1;");
            stmt.executeQuery("SELECT * FROM grades LIMIT 1;");
        }
    }

    // Test seeding when student table is empty
    @Test
    @Order(2)
    void testStudentTableIsSeededWhenEmpty() throws SQLException {
        DatabaseInitializer.initialize();

        try (Connection conn = getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM student")) {

            assertTrue(rs.next());
            int count = rs.getInt(1);

            assertEquals(15, count);
        }
    }

    // Test grades seeding (75 = 15 students × 5 grades)
    @Test
    @Order(3)
    void testGradesTableIsSeeded() throws SQLException {
        DatabaseInitializer.initialize();

        try (Connection conn = getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM grades")) {

            assertTrue(rs.next());
            int count = rs.getInt(1);

            assertEquals(75, count);
        }
    }

    // Test initialize() when student table is NOT empty
    @Test
    @Order(4)
    void testInitializeWhenStudentTableNotEmpty() throws SQLException {
        // Insert a single student so the table is not empty
        try (Connection conn = getConn();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                INSERT INTO student (first_name, last_name, birth_date)
                VALUES ('John', 'Doe', '2000-01-01');
            """);
        }

        // initialize() should NOT seed 15 students
        DatabaseInitializer.initialize();

        try (Connection conn = getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM student")) {

            assertTrue(rs.next());
            int count = rs.getInt(1);

            // Only the manually inserted student should remain
            assertEquals(1, count);
        }
    }

}