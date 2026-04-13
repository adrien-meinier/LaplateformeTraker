package com.example.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class UserModelTest {

    @Test
    void testConstructorAndGetters() {
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0);

        // Initialize a new User Model to test the constructor
        UserModel user = new UserModel(
                "a user name",
                "coucou@hihi.com",
                "qdfgsdhdfhf123547342",
                "a very salty salt",
                created,
                false
        );

        // Test all the getters
        assertEquals("coucou@hihi.com", user.getEmail());
        assertEquals("qdfgsdhdfhf123547342", user.getPasswordHash());
        assertEquals("a very salty salt", user.getSalt());
        assertEquals(created, user.getCreationDate());
        assertEquals(false, user.isAdmin());

    }

    @Test
    void testToString() { 
        
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0);

        // Initialize a new UserModel to test the toString
        UserModel user = new UserModel(
                "a user name",
                "coucou@hihi.com",
                "qdfgsdhdfhf123547342",
                "a very salty salt",
                created,
                false
        );

        String expected = "UserModel{email='coucou@hihi.com', isAdmin=false, creationDate=" + created + "}";
        assertEquals(expected, user.toString());

    }
}
