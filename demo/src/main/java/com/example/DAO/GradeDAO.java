package com.example.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.model.DatabaseConnection;
import com.example.model.GradeModel;
import com.example.model.InMemoryCache;

// Data Access Object for the grades table.
// Read operations fall back to InMemoryCache when the database is unreachable.
// Write operations always require a live connection and propagate any SQLException.
public class GradeDAO {

    // Inserts a new grade and returns the generated ID.
    public int addGrade(int studentId, int grade, String subject) throws SQLException {
        String sql = """
                INSERT INTO grades (student_id, grade, subject)
                VALUES (?, ?, ?)
                RETURNING id;
                """;

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            try (PreparedStatement stmt = db.prepareStatement(sql)) {
                stmt.setInt(1, studentId);
                stmt.setInt(2, grade);
                stmt.setString(3, subject);
                try (ResultSet rs = db.executeQuery(stmt)) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }
        }
        throw new SQLException("Failed to insert grade: no ID returned.");
    }

    // Updates grade value and subject by ID. Returns true if a row was affected.
    public boolean updateGrade(int id, int grade, String subject) throws SQLException {
        String sql = """
                UPDATE grades
                SET grade = ?, subject = ?, last_modified_date = NOW()
                WHERE id = ?;
                """;

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            try (PreparedStatement stmt = db.prepareStatement(sql)) {
                stmt.setInt(1, grade);
                stmt.setString(2, subject);
                stmt.setInt(3, id);
                return db.executeUpdate(stmt) > 0;
            }
        }
    }

    // Deletes a grade by ID. Returns true if a row was affected.
    public boolean deleteGrade(int id) throws SQLException {
        String sql = "DELETE FROM grades WHERE id = ?;";

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            try (PreparedStatement stmt = db.prepareStatement(sql)) {
                stmt.setInt(1, id);
                return db.executeUpdate(stmt) > 0;
            }
        }
    }

    // Retrieves a grade by ID. Falls back to cache if DB is unavailable.
    public GradeModel getGradeById(int id) throws SQLException {
        String sql = """
                SELECT id, student_id, grade, subject, creation_date, last_modified_date
                FROM grades
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
                System.err.println("DB unavailable, falling back to cache (getGradeById): " + e.getMessage());
                return cache.getGradeById(id);
            }
            throw e;
        }
    }

    // Retrieves all grades for a student, ordered by subject. Falls back to cache.
    public List<GradeModel> getGradesByStudentId(int studentId) throws SQLException {
        String sql = """
                SELECT id, student_id, grade, subject, creation_date, last_modified_date
                FROM grades
                WHERE student_id = ?
                ORDER BY subject;
                """;

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            List<GradeModel> grades = new ArrayList<>();
            try (PreparedStatement stmt = db.prepareStatement(sql)) {
                stmt.setInt(1, studentId);
                try (ResultSet rs = db.executeQuery(stmt)) {
                    while (rs.next()) {
                        grades.add(mapRow(rs));
                    }
                }
            }
            return grades;
        } catch (SQLException e) {
            InMemoryCache cache = InMemoryCache.getInstance();
            if (cache.isPopulated()) {
                System.err.println("DB unavailable, falling back to cache (getGradesByStudentId): " + e.getMessage());
                return cache.getGradesByStudentId(studentId);
            }
            throw e;
        }
    }

    // Retrieves every grade in the database, ordered by ID. Falls back to cache.
    public List<GradeModel> getAllGrades() throws SQLException {
        String sql = """
                SELECT id, student_id, grade, subject, creation_date, last_modified_date
                FROM grades
                ORDER BY id;
                """;

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            List<GradeModel> grades = new ArrayList<>();
            try (PreparedStatement stmt = db.prepareStatement(sql);
                 ResultSet rs = db.executeQuery(stmt)) {
                while (rs.next()) {
                    grades.add(mapRow(rs));
                }
            }
            return grades;
        } catch (SQLException e) {
            InMemoryCache cache = InMemoryCache.getInstance();
            if (cache.isPopulated()) {
                System.err.println("DB unavailable, falling back to cache (getAllGrades): " + e.getMessage());
                return cache.getGrades();
            }
            throw e;
        }
    }

    // Maps the current ResultSet row to a GradeModel. Handles nullable last_modified_date.
    private GradeModel mapRow(ResultSet rs) throws SQLException {
        Timestamp tsModified = rs.getTimestamp("last_modified_date");
        LocalDateTime lastModified = tsModified != null ? tsModified.toLocalDateTime() : null;
        return new GradeModel(
                rs.getInt("id"),
                rs.getInt("student_id"),
                rs.getInt("grade"),
                rs.getString("subject"),
                rs.getTimestamp("creation_date").toLocalDateTime(),
                lastModified
        );
    }
}