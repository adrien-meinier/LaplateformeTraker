package com.example.model;

import java.time.LocalDateTime;

/**
 * Représente un enregistrement de la table app_user.
**/

public class UserModel {

    private final String        email;
    private final String        passwordHash;   // lecture seule, jamais modifié directement
    private final String        salt;           // lecture seule
    private final LocalDateTime creationDate;
    private final boolean       isAdmin;

    public UserModel(String email,
                     String passwordHash,
                     String salt,
                     LocalDateTime creationDate,
                     boolean isAdmin) {
        this.email        = email;
        this.passwordHash = passwordHash;
        this.salt         = salt;
        this.creationDate = creationDate;
        this.isAdmin      = isAdmin;
    }

    public String        getEmail()        { return email; }
    public String        getPasswordHash() { return passwordHash; }
    public String        getSalt()         { return salt; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public boolean       isAdmin()         { return isAdmin; }

    @Override
    public String toString() {
        return "UserModel{email='" + email + "', isAdmin=" + isAdmin
                + ", creationDate=" + creationDate + "}";
    }
}