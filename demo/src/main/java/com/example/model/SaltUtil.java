package com.example.model;
import java.security.SecureRandom;
import java.util.Base64;
// Utility class for generating a random salt for password hashing.
public class SaltUtil {
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

}
