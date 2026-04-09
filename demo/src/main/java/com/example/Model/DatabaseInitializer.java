package com.example.model;
import java.sql.Connection;
import java.sql.DriverManager;
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
                salt              VARCHAR(64)  NOT NULL,
                creation_date     TIMESTAMP    NOT NULL DEFAULT NOW(),
                is_admin          BOOLEAN      NOT NULL DEFAULT FALSE
            );
            """;

    private static final String MIGRATE_ADD_SALT = """
        ALTER TABLE app_user
            ADD COLUMN IF NOT EXISTS salt VARCHAR(64) NOT NULL DEFAULT '';
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
    Seeds 15 fictitious students if the student table is empty.
    throws SQLException if any DDL statement fails
    */
    public static void initialize() throws SQLException {
    try {
        Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
        throw new SQLException("PostgreSQL JDBC driver not found on classpath.", e);
    }

    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement()) {

        stmt.execute(CREATE_TABLE_APP_USER);
        stmt.execute(MIGRATE_ADD_SALT);
        stmt.execute(CREATE_TABLE_STUDENT);
        stmt.execute(CREATE_TABLE_GRADES);
        stmt.execute(CREATE_INDEX_GRADES_STUDENT);

        // Always reseed cleanly
        StudentSeeder.seed(conn);

        System.out.println("Database initialization complete.");
    }
}

}
