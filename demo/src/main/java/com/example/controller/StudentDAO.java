package com.example.controller;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.model.DatabaseInitializer;
import com.example.model.StudentModel;

/*
 Data Access Object for the student table.
 Provides CRUD operations using prepared statements to prevent SQL injection.
*/
public class StudentDAO {

    // Inserts a new student and returns the generated ID
    public int addStudent(String firstName, String lastName, LocalDate birthDate) throws SQLException {
        String sql = """
                INSERT INTO student (first_name, last_name, birth_date)
                VALUES (?, ?, ?)
                RETURNING id;
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setDate(3, Date.valueOf(birthDate));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        throw new SQLException("Failed to insert student.");
    }

    // Updates an existing student by ID
    public boolean updateStudent(int id, String firstName, String lastName, LocalDate birthDate) throws SQLException {
        String sql = """
                UPDATE student
                SET first_name = ?, last_name = ?, birth_date = ?, last_modified_date = NOW()
                WHERE id = ?;
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setDate(3, Date.valueOf(birthDate));
            stmt.setInt(4, id);

            return stmt.executeUpdate() > 0;
        }
    }

    // Deletes a student by ID (grades are deleted automatically via ON DELETE CASCADE)
    public boolean deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM student WHERE id = ?;";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Retrieves a single student by ID
    public StudentModel getStudentById(int id) throws SQLException {
        String sql = """
                SELECT id, first_name, last_name, birth_date, creation_date, last_modified_date, average_grade
                FROM student
                WHERE id = ?;
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new StudentModel(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getDate("birth_date").toLocalDate(),
                            rs.getTimestamp("creation_date").toLocalDateTime(),
                            rs.getTimestamp("last_modified_date").toLocalDateTime(),
                            rs.getDouble("average_grade")
                    );
                }
            }
        }
        return null;
    }

    // Retrieves all students
    public List<StudentModel> getAllStudents() throws SQLException {
        String sql = """
                SELECT id, first_name, last_name, birth_date, creation_date, last_modified_date, average_grade
                FROM student
                ORDER BY id;
                """;

        List<StudentModel> students = new ArrayList<>();

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                students.add(new StudentModel(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("birth_date").toLocalDate(),
                        rs.getTimestamp("creation_date").toLocalDateTime(),
                        rs.getTimestamp("last_modified_date").toLocalDateTime(),
                        rs.getDouble("average_grade")
                ));
            }
        }

        return students;
    }

    // AVERAGES
    // Returns the average grade of a specific student
    public double getStudentAverage(int studentId) throws SQLException {
        String sql = """
                SELECT COALESCE(AVG(grade), 0)
                FROM grades
                WHERE student_id = ?;
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }

    // Returns the global class average (all grades)
    public double getClassAverage() throws SQLException {
        String sql = """
                SELECT COALESCE(AVG(grade), 0)
                FROM grades;
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }
}
