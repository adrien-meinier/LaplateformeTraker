package com.example.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

class StudentValidatorTest {

    @Test
    void firstNameBlankShouldReturnError() {
        List<String> errors = StudentValidator.validate(" ", "Dupont", LocalDate.of(2000, 1, 1));
        assertTrue(errors.contains("Le prénom est obligatoire."));
    }

    @Test
    void lastNameBlankShouldReturnError() {
        List<String> errors = StudentValidator.validate("Jean", " ", LocalDate.of(2000, 1, 1));
        assertTrue(errors.contains("Le nom est obligatoire."));
    }

    @Test
    void namesTooShortShouldReturnErrors() {
        List<String> errors = StudentValidator.validate("J", "D", LocalDate.of(2000, 1, 1));
        assertTrue(errors.contains("Le prénom doit contenir au moins 2 caractères."));
        assertTrue(errors.contains("Le nom doit contenir au moins 2 caractères."));
    }

    @Test
    void namesTooLongShouldReturnErrors() {
        String longName = "A".repeat(51);
        List<String> errors = StudentValidator.validate(longName, longName, LocalDate.of(2000, 1, 1));
        assertEquals(2, errors.size());
        assertTrue(errors.get(0).contains("ne peut pas dépasser"));
    }

    @Test
    void invalidCharactersShouldReturnErrors() {
        List<String> errors = StudentValidator.validate("Jean123", "Du@pont", LocalDate.of(2000, 1, 1));
        assertEquals(2, errors.size());
        assertTrue(errors.get(0).contains("ne doit contenir que des lettres"));
    }

    @Test
    void validNamesShouldProduceNoError() {
        List<String> errors = StudentValidator.validate("Jean-Pierre", "D'Alembert", LocalDate.of(2000, 1, 1));
        assertTrue(errors.isEmpty());
    }

    @Test
    void nullBirthDateShouldReturnError() {
        List<String> errors = StudentValidator.validate("Jean", "Dupont", null);
        assertTrue(errors.contains("La date de naissance est obligatoire."));
    }

    @Test
    void futureBirthDateShouldReturnError() {
        LocalDate future = LocalDate.now().plusDays(1);
        List<String> errors = StudentValidator.validate("Jean", "Dupont", future);
        assertTrue(errors.contains("La date de naissance ne peut pas être dans le futur."));
    }

    @Test
    void tooYoungShouldReturnError() {
        LocalDate tooYoung = LocalDate.now().minusYears(3);
        List<String> errors = StudentValidator.validate("Jean", "Dupont", tooYoung);
        assertTrue(errors.contains("L'étudiant doit avoir au moins 5 ans."));
    }

    @Test
    void tooOldShouldReturnError() {
        LocalDate tooOld = LocalDate.now().minusYears(130);
        List<String> errors = StudentValidator.validate("Jean", "Dupont", tooOld);
        assertTrue(errors.contains("La date de naissance est improbable"));
    }

    @Test
    void validBirthDateShouldProduceNoError() {
        LocalDate valid = LocalDate.now().minusYears(20);
        List<String> errors = StudentValidator.validate("Jean", "Dupont", valid);
        assertTrue(errors.isEmpty());
    }

    @Test
    void allFieldsValidShouldReturnEmptyList() {
        List<String> errors = StudentValidator.validate(
                "Marie-Claire",
                "L'Écuyer",
                LocalDate.now().minusYears(25)
        );
        assertTrue(errors.isEmpty());
    }
}
