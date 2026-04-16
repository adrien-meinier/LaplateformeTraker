package com.example.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// A test class to test the student model
class StudentModelTests {

    @Test
    void testConstructorAndGetters() {
        LocalDate birth = LocalDate.of(2000, 1, 1);
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime modified = LocalDateTime.of(2024, 1, 2, 12, 0);

        // Initialize a new Student Model to test the constructor
        StudentModel student = new StudentModel(
                42,
                "John",
                "Doe",
                birth,
                created,
                modified,
                15
        );

        // Test all the getters
        assertEquals(42, student.getId());
        assertEquals("John", student.getFirstName());
        assertEquals("Doe", student.getLastName());
        assertEquals(birth, student.getBirthDate());
        assertEquals(created, student.getCreationDate());
        assertEquals(modified, student.getLastModifiedDate());
    }
}