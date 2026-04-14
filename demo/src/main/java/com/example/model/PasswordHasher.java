package com.example.model;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;

public class PasswordHasher {

    //  Pepper is a secret value added to the password before hashing, stored in an environment variable for security
    private static final String PEPPER = System.getenv("APP_PEPPER");

    public static String hashPassword(String password, String salt) throws Exception {

        String combined = password + PEPPER;

        KeySpec spec = new PBEKeySpec(
                combined.toCharArray(),
                Base64.getDecoder().decode(salt),
                65536, // iterations
                256    // key length
        );

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(hash);
    }
}