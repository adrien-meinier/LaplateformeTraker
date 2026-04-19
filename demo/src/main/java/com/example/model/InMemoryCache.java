package com.example.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Thread-safe in-memory snapshot of the database.
// Used as a read-only fallback by DAOs when the database is unreachable.
// Populated and refreshed periodically by AutoBackupService.
public final class InMemoryCache {

    private static final InMemoryCache INSTANCE = new InMemoryCache();

    private volatile List<StudentModel> students  = Collections.emptyList();
    private volatile List<GradeModel>   grades    = Collections.emptyList();
    private volatile List<UserModel>    users     = Collections.emptyList();
    private volatile boolean            populated = false;
    private volatile LocalDateTime      lastRefreshed;

    private InMemoryCache() {}

    public static InMemoryCache getInstance() {
        return INSTANCE;
    }

    // Replaces the entire snapshot atomically. Called by AutoBackupService.
    public synchronized void refresh(List<StudentModel> students,
                                     List<GradeModel>   grades,
                                     List<UserModel>    users) {
        this.students      = List.copyOf(students);
        this.grades        = List.copyOf(grades);
        this.users         = List.copyOf(users);
        this.populated     = true;
        this.lastRefreshed = LocalDateTime.now();
    }

    // Returns true if the cache has been populated at least once.
    public boolean isPopulated() {
        return populated;
    }

    public LocalDateTime getLastRefreshed() {
        return lastRefreshed;
    }

    // --- Student lookups ---

    public List<StudentModel> getStudents() {
        return students;
    }

    public StudentModel getStudentById(int id) {
        return students.stream()
                .filter(s -> s.getId() == id)
                .findFirst().orElse(null);
    }

    // --- Grade lookups ---

    public List<GradeModel> getGrades() {
        return grades;
    }

    public GradeModel getGradeById(int id) {
        return grades.stream()
                .filter(g -> g.getId() == id)
                .findFirst().orElse(null);
    }

    // Returns grades for a student, sorted by subject to match DB query order.
    public List<GradeModel> getGradesByStudentId(int studentId) {
        return grades.stream()
                .filter(g -> g.getStudentId() == studentId)
                .sorted(Comparator.comparing(GradeModel::getSubject))
                .toList();
    }

    // --- User lookups ---

    public List<UserModel> getUsers() {
        return users;
    }

    public UserModel getUserByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst().orElse(null);
    }

    public UserModel getUserByEmail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst().orElse(null);
    }

    public boolean emailExists(String email) {
        return users.stream().anyMatch(u -> u.getEmail().equals(email));
    }

    public boolean usernameExists(String username) {
        return users.stream().anyMatch(u -> u.getUsername().equals(username));
    }
}