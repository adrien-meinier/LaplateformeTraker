package com.example.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// A class to test the grade model
public class GradeModelTest {
    
    @Test
    void testConstructorAndGetters() {
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime modified = LocalDateTime.of(2024, 1, 2, 12, 0);

        // Initialize a new Grade Model to test the constructor
        GradeModel grade = new GradeModel(
                42,
                55,
                20,
                "Math",
                created,
                modified
        );

        // Test all the getters
        assertEquals(42, grade.getId());
        assertEquals(55, grade.getStudentId());
        assertEquals(20, grade.getGrade());
        assertEquals("Math", grade.getSubject());
        assertEquals(created, grade.getCreationDate());
        assertEquals(modified, grade.getLastModifiedDate());
    }
}
