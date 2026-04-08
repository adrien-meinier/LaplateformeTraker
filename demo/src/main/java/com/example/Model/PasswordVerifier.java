package com.example.model;
public class PasswordVerifier {

    public static boolean verify(String inputPassword, String salt, String storedHash) throws Exception {
        String newHash = PasswordHasher.hashPassword(inputPassword, salt);
        return newHash.equals(storedHash);
    }
}