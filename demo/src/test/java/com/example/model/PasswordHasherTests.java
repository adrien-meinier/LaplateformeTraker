package com.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;

public class PasswordHasherTests {

    // Utility: generate a deterministic Base64 salt for tests
    private String generateTestSalt() {
        byte[] salt = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
        return Base64.getEncoder().encodeToString(salt);
    }

    // Verify that the hash is not null
    @Test
    void hashPassword_shouldReturnNonNullHash() throws Exception {
        String salt = generateTestSalt();
        String hash = PasswordHasher.hashPassword("password123", salt);

        assertNotNull(hash, "Hash should never be null");
        assertFalse(hash.isEmpty(), "Hash should not be empty");
    }

    // Verify that two hashes of the same password produce the same result
    @Test
    void hashPassword_shouldBeDeterministic() throws Exception {
        String salt = generateTestSalt();

        String h1 = PasswordHasher.hashPassword("password123", salt);
        String h2 = PasswordHasher.hashPassword("password123", salt);

        assertEquals(h1, h2, "Same input must produce same hash");
    }


    // Verify that two hashes of different passwords are different
    @Test
    void hashPassword_shouldChangeWhenPasswordChanges() throws Exception {
        String salt = generateTestSalt();

        String h1 = PasswordHasher.hashPassword("password123", salt);
        String h2 = PasswordHasher.hashPassword("differentPass", salt);

        assertNotEquals(h1, h2, "Different passwords must produce different hashes");
    }

    // Verify that two hashes with different salts are different
    @Test
    void hashPassword_shouldChangeWhenSaltChanges() throws Exception {
        String salt1 = generateTestSalt();
        String salt2 = Base64.getEncoder().encodeToString(new byte[]{9, 9, 9, 9, 9, 9, 9, 9});

        String h1 = PasswordHasher.hashPassword("password123", salt1);
        String h2 = PasswordHasher.hashPassword("password123", salt2);

        assertNotEquals(h1, h2, "Different salts must produce different hashes");
    }
}