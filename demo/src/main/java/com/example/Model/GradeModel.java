package com.example.Model;

import java.time.LocalDateTime;

// A class to modelise and store data of a grade
public class GradeModel {

    private final int id;
    private final int studentId;
    private final int grade;
    private final String subject;
    private final LocalDateTime creationDate;
    private final LocalDateTime lastModifiedDate;

    public GradeModel(int id, int studentId, int grade, String subject,
                      LocalDateTime creationDate, LocalDateTime lastModifiedDate) {

        this.id = id;
        this.studentId = studentId;
        this.grade = grade;
        this.subject = subject;
        this.creationDate = creationDate;
        this.lastModifiedDate = lastModifiedDate;
    }
    
    // Getters
    public int getId() { 
        return id; 
    }
    public int getStudentId() { 
        return studentId; 
    }
    public int getGrade() { 
        return grade; 
    }
    public String getSubject() { 
        return subject; 
    }
    public LocalDateTime getCreationDate() { 
        return creationDate; 
    }
    public LocalDateTime getLastModifiedDate() { 
        return lastModifiedDate; 
    }
}