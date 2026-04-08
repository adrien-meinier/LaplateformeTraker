package com.example.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Random;

/*
Seeds the database with fictitious students and random grades.
Executed only if the student table is empty.
*/
public class StudentSeeder {

    private static final List<String[]> STUDENTS = List.of(
            new String[]{"Alice", "Martin", "2004-03-12"},
            new String[]{"Bob", "Dupont", "2003-07-24"},
            new String[]{"Clara", "Bernard", "2005-01-08"},
            new String[]{"David", "Leroy", "2002-11-30"},
            new String[]{"Emma", "Moreau", "2004-06-15"},
            new String[]{"Florian", "Simon", "2001-09-03"},
            new String[]{"Gabrielle", "Laurent", "2003-04-22"},
            new String[]{"Hugo", "Michel", "2002-08-19"},
            new String[]{"Inès", "Garcia", "2004-12-05"},
            new String[]{"Jules", "David", "2005-02-28"},
            new String[]{"Karine", "Petit", "2000-10-14"},
            new String[]{"Lucas", "Robert", "2003-05-07"},
            new String[]{"Marie", "Richard", "2004-09-18"},
            new String[]{"Nicolas", "Thomas", "2002-03-25"},
            new String[]{"Océane", "Blanc", "2005-07-11"}
    );

    private static final List<String> SUBJECTS = List.of(
            "Math", "Physics", "Chemistry", "History", "English"
    );

    public static void seed(Connection conn) throws SQLException {
    try (Statement insertStmt = conn.createStatement();
         Statement selectStmt = conn.createStatement()) {

        // Insert students
        for (String[] s : STUDENTS) {
            insertStmt.executeUpdate(
                    "INSERT INTO student (first_name, last_name, birth_date) VALUES (" +
                            "'" + s[0] + "', '" + s[1] + "', '" + s[2] + "');"
            );
        }

        // Select student IDs
        var rs = selectStmt.executeQuery("SELECT id FROM student;");

        Random random = new Random();

        while (rs.next()) {
            int studentId = rs.getInt("id");

            for (String subject : SUBJECTS) {
                int grade = random.nextInt(21);
                insertStmt.executeUpdate(
                        "INSERT INTO grades (student_id, grade, subject) VALUES (" +
                                studentId + ", " + grade + ", '" + subject + "');"
                );
            }
        }

        System.out.println("Students and grades seeded successfully.");
    }
}
}