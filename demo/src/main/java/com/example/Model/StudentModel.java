package com.example.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

// A class to modelize and store data about a student, used in the SQL queries
public class StudentModel {

    private final int id;
    private final String firstName;
    private final String lastName;
    private final LocalDate birthDate;
    private final LocalDateTime creationDate;
    private final LocalDateTime lastModifiedDate;

    public StudentModel(int id, String firstName, String lastName,
                        LocalDate birthDate, LocalDateTime creationDate,
                        LocalDateTime lastModifiedDate) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.creationDate = creationDate;
        this.lastModifiedDate = lastModifiedDate;
    }


    // Getters
    public int getId() { 
        return id; 
    }

    public String getFirstName() { 
        return firstName; 
    }
    public String getLastName() { 
        return lastName; 
    }
    public LocalDate getBirthDate() { 
        return birthDate; 
    }
    public LocalDateTime getCreationDate() { 
        return creationDate; 
    }
    public LocalDateTime getLastModifiedDate() { 
        return lastModifiedDate; 
    }
}