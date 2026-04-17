package com.example.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.model.DatabaseConnection;
import com.example.model.GradeModel;

/*
 Data Access Object for the grades table.
 Provides CRUD operations using prepared statements.
 Uses DatabaseConnection to manage the connection lifecycle.
*/
public class GradeDAO {

    // Insert a new grade for a student
    public int addGrade(int studentId, int grade, String subject) throws SQLException {
        String sql = """
                INSERT INTO grades (student_id, grade, subject)
                VALUES (?, ?, ?)
                RETURNING id;
                """;
//  Use try-with-resources to ensure proper resource management
        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            PreparedStatement stmt = db.prepareStatement(sql);

            stmt.setInt(1, studentId);
            stmt.setInt(2, grade);
            stmt.setString(3, subject);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        throw new SQLException("Failed to insert grade.");
    }

    // Update an existing grade
    public boolean updateGrade(int id, int grade, String subject) throws SQLException {
        String sql = """
                UPDATE grades
                SET grade = ?, subject = ?, last_modified_date = NOW()
                WHERE id = ?;
                """;

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            PreparedStatement stmt = db.prepareStatement(sql);

            stmt.setInt(1, grade);
            stmt.setString(2, subject);
            stmt.setInt(3, id);

            return stmt.executeUpdate() > 0;
        }
    }

    // Delete a grade by ID
    public boolean deleteGrade(int id) throws SQLException {
        String sql = "DELETE FROM grades WHERE id = ?;";

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            PreparedStatement stmt = db.prepareStatement(sql);

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Retrieve a grade by ID
    public GradeModel getGradeById(int id) throws SQLException {
        String sql = """
                SELECT id, student_id, grade, subject, creation_date, last_modified_date
                FROM grades
                WHERE id = ?;
                """;
// Use try-with-resources to ensure proper resource management
        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            PreparedStatement stmt = db.prepareStatement(sql);

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new GradeModel(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("grade"),
                            rs.getString("subject"),
                            rs.getTimestamp("creation_date").toLocalDateTime(),
                            rs.getTimestamp("last_modified_date").toLocalDateTime()
                    );
                }
            }
        }
        return null;
    }

    // Retrieve all grades for a given student
    public List<GradeModel> getGradesByStudentId(int studentId) throws SQLException {
        String sql = """
                SELECT id, student_id, grade, subject, creation_date, last_modified_date
                FROM grades
                WHERE student_id = ?
                ORDER BY subject;
                """;

        List<GradeModel> grades = new ArrayList<>();

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            PreparedStatement stmt = db.prepareStatement(sql);

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(new GradeModel(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("grade"),
                            rs.getString("subject"),
                            rs.getTimestamp("creation_date").toLocalDateTime(),
                            rs.getTimestamp("last_modified_date").toLocalDateTime()
                    ));
                }
            }
        }

        return grades;
    }

    // Retrieve all grades in the database
    public List<GradeModel> getAllGrades() throws SQLException {
        String sql = """
                SELECT id, student_id, grade, subject, creation_date, last_modified_date
                FROM grades
                ORDER BY id;
                """;

        List<GradeModel> grades = new ArrayList<>();
// Use try-with-resources to ensure proper resource management
        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            PreparedStatement stmt = db.prepareStatement(sql);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(new GradeModel(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("grade"),
                            rs.getString("subject"),
                            rs.getTimestamp("creation_date").toLocalDateTime(),
                            rs.getTimestamp("last_modified_date").toLocalDateTime()
                    ));
                }
            }
        }

        return grades;
    }
}