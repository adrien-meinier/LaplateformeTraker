package com.example.Model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles the PostgreSQL connection and creates all required tables
 * (student, grades, app_user) if they do not already exist.
 */
public class DatabaseInitializer {


    // Connection settings
    private static final String DB_HOST     = "localhost";
    private static final String DB_PORT     = "5432";
    private static final String DB_NAME     = "student_db";
    private static final String DB_USER     = "postgres";
    private static final String DB_PASSWORD = "your_password_here";

    private static final String JDBC_URL =
            "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;


    private static final String CREATE_TABLE_APP_USER = """
            CREATE TABLE IF NOT EXISTS app_user (
                email             VARCHAR(255) PRIMARY KEY,
                password_hash     VARCHAR(512) NOT NULL,
                creation_date     TIMESTAMP    NOT NULL DEFAULT NOW(),
                is_admin          BOOLEAN      NOT NULL DEFAULT FALSE
            );
            """;

    private static final String CREATE_TABLE_STUDENT = """
            CREATE TABLE IF NOT EXISTS student (
                id                SERIAL       PRIMARY KEY,
                first_name        VARCHAR(100) NOT NULL,
                last_name         VARCHAR(100) NOT NULL,
                birth_date        DATE         NOT NULL,
                creation_date     TIMESTAMP    NOT NULL DEFAULT NOW(),
                last_modified_date TIMESTAMP   NOT NULL DEFAULT NOW()
            );
            """;

    private static final String CREATE_TABLE_GRADES = """
            CREATE TABLE IF NOT EXISTS grades (
                id                SERIAL       PRIMARY KEY,
                student_id        INT          NOT NULL
                                    REFERENCES student(id) ON DELETE CASCADE,
                grade             INT          NOT NULL
                                    CHECK (grade >= 0 AND grade <= 20),
                subject           VARCHAR(100) NOT NULL,
                creation_date     TIMESTAMP    NOT NULL DEFAULT NOW(),
                last_modified_date TIMESTAMP   NOT NULL DEFAULT NOW()
            );
            """;

    private static final String CREATE_INDEX_GRADES_STUDENT =
            "CREATE INDEX IF NOT EXISTS idx_grades_student_id ON grades(student_id);";


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
    }

    /*
    Creates all required tables and indexes if they do not already exist.
    throws SQLException if any DDL statement fails
    */
    public static void initialize() throws SQLException {
        // Load the PostgreSQL JDBC driver (auto-loaded in modern JVMs, but explicit is safer)
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC driver not found on classpath.", e);
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create tables in dependency order (app_user and student before grades)
            stmt.execute(CREATE_TABLE_APP_USER);
            System.out.println("[DB] Table 'app_user' is ready.");

            stmt.execute(CREATE_TABLE_STUDENT);
            System.out.println("[DB] Table 'student' is ready.");

            stmt.execute(CREATE_TABLE_GRADES);
            System.out.println("[DB] Table 'grades' is ready.");

            stmt.execute(CREATE_INDEX_GRADES_STUDENT);
            System.out.println("[DB] Index 'idx_grades_student_id' is ready.");

            System.out.println("[DB] Database initialization complete.");
        }
    }
}
