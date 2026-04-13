package com.example.model;

import java.time.LocalDateTime;

// A class to represent an App User

public class UserModel {

    private final String        email;
    private final String        passwordHash;   // Read-only
    private final String        salt;           // Read-only
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

    // Accessors
    public String        getEmail()        { 
        return email; 
    }
    public String        getPasswordHash() { 
        return passwordHash; 
    }
    public String        getSalt()         { 
        return salt; 
    }
    public LocalDateTime getCreationDate() { 
        return creationDate; 
    }
    public boolean       isAdmin()         { 
        return isAdmin; 
    }

    // Print for debug
    @Override
    public String toString() {
        return "UserModel{email='" + email + "', isAdmin=" + isAdmin
                + ", creationDate=" + creationDate + "}";
    }
}