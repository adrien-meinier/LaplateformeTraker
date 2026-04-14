package com.example.model;

import org.junit.jupiter.api.Test;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class SaltUtilTest {

    @Test
    void generateSaltShouldReturnNonNullAndNonEmpty() {
        String salt = SaltUtil.generateSalt();
        assertNotNull(salt);
        assertFalse(salt.isEmpty());
    }

    @Test
    void generateSaltShouldReturnValidBase64() {
        String salt = SaltUtil.generateSalt();
        // Should not throw
        assertDoesNotThrow(() -> Base64.getDecoder().decode(salt));
    }

    @Test
    void generateSaltShouldDecodeToSixteenBytes() {
        String salt = SaltUtil.generateSalt();
        byte[] decoded = Base64.getDecoder().decode(salt);
        assertEquals(16, decoded.length);
    }

    @Test
    void generateSaltShouldProduceDifferentValuesEachCall() {
        String salt1 = SaltUtil.generateSalt();
        String salt2 = SaltUtil.generateSalt();
        // Two consecutive salts must not be identical
        assertNotEquals(salt1, salt2);
    }
}