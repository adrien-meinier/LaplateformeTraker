package com.example.model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
Handles the PostgreSQL connection and creates all required tables
(student, grades, app_user) if they do not already exist.
*/
public class DatabaseInitializer {

    // Connection settings
    private static final String DB_HOST     = "localhost";
    private static final String DB_PORT     = "5432";
    private static final String DB_NAME     = "laplateformetracker";
    private static final String DB_USER     = "postgres";
    private static final String DB_PASSWORD = "root";

    public static void createDatabaseIfNotExists() throws SQLException {
        String url = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/postgres";

        try (Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            String checkDb = "SELECT 1 FROM pg_database WHERE datname = '" + DB_NAME + "';";
            var rs = stmt.executeQuery(checkDb);

            if (!rs.next()) {
                System.out.println("Database '" + DB_NAME + "' does not exist. Creating...");
                stmt.execute("CREATE DATABASE " + DB_NAME + ";");
                System.out.println("Database created successfully.");
            } else {
                System.out.println("Database '" + DB_NAME + "' already exists.");
            }
        }
    }

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

    private static final String SEED_STUDENTS = """
            INSERT INTO student (first_name, last_name, birth_date) VALUES
                ('Alice',     'Martin',   '2004-03-12'),
                ('Bob',       'Dupont',   '2003-07-24'),
                ('Clara',     'Bernard',  '2005-01-08'),
                ('David',     'Leroy',    '2002-11-30'),
                ('Emma',      'Moreau',   '2004-06-15'),
                ('Florian',   'Simon',    '2001-09-03'),
                ('Gabrielle', 'Laurent',  '2003-04-22'),
                ('Hugo',      'Michel',   '2002-08-19'),
                ('Inès',      'Garcia',   '2004-12-05'),
                ('Jules',     'David',    '2005-02-28'),
                ('Karine',    'Petit',    '2000-10-14'),
                ('Lucas',     'Robert',   '2003-05-07'),
                ('Marie',     'Richard',  '2004-09-18'),
                ('Nicolas',   'Thomas',   '2002-03-25'),
                ('Océane',    'Blanc',    '2005-07-11');
            """;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
    }

    /*
    Creates all required tables and indexes if they do not already exist.
    Seeds 15 fictitious students if the student table is empty.
    throws SQLException if any DDL statement fails
    */
    public static void initialize() throws SQLException {
        // Load the PostgreSQL JDBC driver
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC driver not found on classpath.", e);
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create tables in dependency order
            stmt.execute(CREATE_TABLE_APP_USER);
            System.out.println("Table 'app_user' is ready.");

            stmt.execute(CREATE_TABLE_STUDENT);
            System.out.println("Table 'student' is ready.");

            stmt.execute(CREATE_TABLE_GRADES);
            System.out.println("Table 'grades' is ready.");

            stmt.execute(CREATE_INDEX_GRADES_STUDENT);
            System.out.println("Index 'idx_grades_student_id' is ready.");

            // Seed students if table is empty
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM student;");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute(SEED_STUDENTS);
                System.out.println("15 fictitious students inserted.");
            }

            System.out.println("Database initialization complete.");
        }
    }
}
