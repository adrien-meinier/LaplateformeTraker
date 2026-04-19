package com.example.DAO;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.model.DatabaseConnection;
import com.example.model.GradeModel;
import com.example.model.InMemoryCache;
import com.example.model.StudentModel;

// Data Access Object for the student table.
// Read operations fall back to InMemoryCache when the database is unreachable.
// Write operations always require a live connection and propagate any SQLException.
public class StudentDAO {

    // Inserts a new student and returns the generated ID.
    public int addStudent(String firstName, String lastName, LocalDate birthDate) throws SQLException {
        String sql = """
                INSERT INTO student (first_name, last_name, birth_date)
                VALUES (?, ?, ?)
                RETURNING id;
                """;

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            try (PreparedStatement stmt = db.prepareStatement(sql)) {
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setDate(3, Date.valueOf(birthDate));
                try (ResultSet rs = db.executeQuery(stmt)) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }
        }
        throw new SQLException("Failed to insert student: no ID returned.");
    }

    // Updates an existing student by ID. Returns true if a row was affected.
    public boolean updateStudent(int id, String firstName, String lastName, LocalDate birthDate)
            throws SQLException {
        String sql = """
                UPDATE student
                SET first_name = ?, last_name = ?, birth_date = ?, last_modified_date = NOW()
                WHERE id = ?;
                """;

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            try (PreparedStatement stmt = db.prepareStatement(sql)) {
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setDate(3, Date.valueOf(birthDate));
                stmt.setInt(4, id);
                return db.executeUpdate(stmt) > 0;
            }
        }
    }

    // Deletes a student by ID. Grades are removed automatically via ON DELETE CASCADE.
    public boolean deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM student WHERE id = ?;";

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            try (PreparedStatement stmt = db.prepareStatement(sql)) {
                stmt.setInt(1, id);
                return db.executeUpdate(stmt) > 0;
            }
        }
    }

    // Retrieves a single student by ID. Falls back to cache if DB is unavailable.
    public StudentModel getStudentById(int id) throws SQLException {
        String sql = """
                SELECT id, first_name, last_name, birth_date,
                       creation_date, last_modified_date, average_grade
                FROM student
                WHERE id = ?;
                """;

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            try (PreparedStatement stmt = db.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = db.executeQuery(stmt)) {
                    if (rs.next()) {
                        return mapRow(rs);
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            InMemoryCache cache = InMemoryCache.getInstance();
            if (cache.isPopulated()) {
                System.err.println("DB unavailable, falling back to cache (getStudentById): " + e.getMessage());
                return cache.getStudentById(id);
            }
            throw e;
        }
    }

    // Retrieves all students ordered by ID. Falls back to cache if DB is unavailable.
    public List<StudentModel> getAllStudents() throws SQLException {
        String sql = """
                SELECT id, first_name, last_name, birth_date,
                       creation_date, last_modified_date, average_grade
                FROM student
                ORDER BY id;
                """;

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            List<StudentModel> students = new ArrayList<>();
            try (PreparedStatement stmt = db.prepareStatement(sql);
                 ResultSet rs = db.executeQuery(stmt)) {
                while (rs.next()) {
                    students.add(mapRow(rs));
                }
            }
            return students;
        } catch (SQLException e) {
            InMemoryCache cache = InMemoryCache.getInstance();
            if (cache.isPopulated()) {
                System.err.println("DB unavailable, falling back to cache (getAllStudents): " + e.getMessage());
                return cache.getStudents();
            }
            throw e;
        }
    }

    // Returns the average grade of a student. Falls back to computing from cached grades.
    public double getStudentAverage(int studentId) throws SQLException {
        String sql = """
                SELECT COALESCE(AVG(grade), 0)
                FROM grades
                WHERE student_id = ?;
                """;

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            try (PreparedStatement stmt = db.prepareStatement(sql)) {
                stmt.setInt(1, studentId);
                try (ResultSet rs = db.executeQuery(stmt)) {
                    if (rs.next()) {
                        return rs.getDouble(1);
                    }
                }
            }
            return 0.0;
        } catch (SQLException e) {
            InMemoryCache cache = InMemoryCache.getInstance();
            if (cache.isPopulated()) {
                System.err.println("DB unavailable, computing average from cache: " + e.getMessage());
                return cache.getGradesByStudentId(studentId).stream()
                        .mapToInt(GradeModel::getGrade)
                        .average()
                        .orElse(0.0);
            }
            throw e;
        }
    }

    // Returns the overall average across all grades. Falls back to computing from cache.
    public double getClassAverage() throws SQLException {
        String sql = """
                SELECT COALESCE(AVG(grade), 0)
                FROM grades;
                """;

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            try (PreparedStatement stmt = db.prepareStatement(sql);
                 ResultSet rs = db.executeQuery(stmt)) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
            return 0.0;
        } catch (SQLException e) {
            InMemoryCache cache = InMemoryCache.getInstance();
            if (cache.isPopulated()) {
                System.err.println("DB unavailable, computing class average from cache: " + e.getMessage());
                return cache.getGrades().stream()
                        .mapToInt(GradeModel::getGrade)
                        .average()
                        .orElse(0.0);
            }
            throw e;
        }
    }

    // Maps the current ResultSet row to a StudentModel. Handles nullable last_modified_date.
    private StudentModel mapRow(ResultSet rs) throws SQLException {
        var tsModified = rs.getTimestamp("last_modified_date");
        return new StudentModel(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getDate("birth_date").toLocalDate(),
                rs.getTimestamp("creation_date").toLocalDateTime(),
                tsModified != null ? tsModified.toLocalDateTime() : null,
                rs.getDouble("average_grade")
        );
    }
}