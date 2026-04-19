package com.example.controller;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// Centralised validation rules for student creation and update.
// All methods are pure (no DB calls) and return human-readable French error messages.
public final class StudentValidator {

    // Names: letters, spaces, hyphens and apostrophes only (supports accented chars)
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}\\s\\-']+$");

    private static final int MIN_AGE  = 5;
    private static final int MAX_AGE  = 122;
    private static final int MAX_NAME = 50;

    // Validates all fields at once and returns the list of errors.
    // Returns an empty list if everything is valid.
    public static List<String> validate(String firstName, String lastName, LocalDate birthDate) {
        List<String> errors = new ArrayList<>();

        String fnErr = validateName(firstName, "Le prénom");
        if (fnErr != null) errors.add(fnErr);

        String lnErr = validateName(lastName, "Le nom");
        if (lnErr != null) errors.add(lnErr);

        String bdErr = validateBirthDate(birthDate);
        if (bdErr != null) errors.add(bdErr);

        return errors;
    }

    // Returns an error message for the name field, or null if valid.
    private static String validateName(String name, String label) {
        if (name == null || name.isBlank()) {
            return label + " est obligatoire.";
        }
        String trimmed = name.trim();
        if (trimmed.length() < 2) {
            return label + " doit contenir au moins 2 caractères.";
        }
        if (trimmed.length() > MAX_NAME) {
            return label + " ne peut pas dépasser " + MAX_NAME + " caractères.";
        }
        if (!NAME_PATTERN.matcher(trimmed).matches()) {
            return label + " ne doit contenir que des lettres, espaces, tirets ou apostrophes.";
        }
        return null;
    }

    // Returns an error message for the birth date, or null if valid.
    private static String validateBirthDate(LocalDate date) {
        if (date == null) {
            return "La date de naissance est obligatoire.";
        }
        if (date.isAfter(LocalDate.now())) {
            return "La date de naissance ne peut pas être dans le futur.";
        }
        int age = Period.between(date, LocalDate.now()).getYears();
        if (age < MIN_AGE) {
            return "L'étudiant doit avoir au moins " + MIN_AGE + " ans.";
        }
        if (age > MAX_AGE) {
            return "La date de naissance est improbable (plus de " + MAX_AGE + " ans).";
        }
        return null;
    }
}