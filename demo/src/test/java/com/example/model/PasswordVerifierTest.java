package com.example.model;

import org.junit.jupiter.api.Test;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordVerifierTest {

    // Fixed salt for deterministic tests
    private String testSalt() {
        return Base64.getEncoder().encodeToString(new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
    }

    @Test
    void verifyShouldReturnTrueForCorrectPassword() throws Exception {
        String salt = testSalt();
        String hash = PasswordHasher.hashPassword("secret123", salt);
        assertTrue(PasswordVerifier.verify("secret123", salt, hash));
    }

    @Test
    void verifyShouldReturnFalseForWrongPassword() throws Exception {
        String salt = testSalt();
        String hash = PasswordHasher.hashPassword("secret123", salt);
        assertFalse(PasswordVerifier.verify("wrongPassword", salt, hash));
    }

    @Test
    void verifyShouldReturnFalseForEmptyPassword() throws Exception {
        String salt = testSalt();
        String hash = PasswordHasher.hashPassword("secret123", salt);
        assertFalse(PasswordVerifier.verify("", salt, hash));
    }

    @Test
    void verifyShouldReturnFalseWhenHashIsForDifferentPassword() throws Exception {
        String salt = testSalt();
        String hashA = PasswordHasher.hashPassword("passwordA", salt);
        String hashB = PasswordHasher.hashPassword("passwordB", salt);
        // Cross-check: hashA must not verify against passwordB
        assertFalse(PasswordVerifier.verify("passwordA", salt, hashB));
        assertFalse(PasswordVerifier.verify("passwordB", salt, hashA));
    }
}