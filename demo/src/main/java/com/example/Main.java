package com.example;
 
/**
 * Main — lanceur de secours pour les environnements sans support JavaFX natif.
 *
 * Certains IDEs (IntelliJ, Eclipse) ne peuvent pas lancer directement une classe
 * qui étend {@link javafx.application.Application} sans configuration spéciale.
 * Ce lanceur intermédiaire contourne ce problème.
 *
 * Usage : exécutez cette classe, pas App.java.
 */
public class Main {
    public static void main(String[] args) {
        App.main(args);
    }
}