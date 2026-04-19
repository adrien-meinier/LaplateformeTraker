package com.example.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.example.DAO.GradeDAO;
import com.example.DAO.StudentDAO;
import com.example.DAO.UserDAO;
import com.example.model.GradeModel;
import com.example.model.InMemoryCache;
import com.example.model.StudentModel;
import com.example.model.UserModel;

// Schedules periodic database backups (SQL dump via pg_dump) and in-memory cache refreshes.
// Both operations run on the same background daemon thread.
public final class AutoBackupService {

    private static final ScheduledExecutorService SCHEDULER =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "auto-backup");
                t.setDaemon(true); // won't block JVM shutdown
                return t;
            });

    private static ScheduledFuture<?> task;

    // Starts the backup cycle. First tick fires immediately, then every intervalMinutes.
    // Calling start() while already running is a no-op.
    public static synchronized void start(int intervalMinutes) {
        if (task != null && !task.isDone()) {
            return;
        }
        task = SCHEDULER.scheduleAtFixedRate(
                AutoBackupService::runCycle,
                0, intervalMinutes, TimeUnit.MINUTES
        );
        System.out.println("AutoBackupService started (interval: " + intervalMinutes + " min).");
    }

    // Stops the scheduled cycle. In-flight execution is not interrupted.
    public static synchronized void stop() {
        if (task != null) {
            task.cancel(false);
            task = null;
            System.out.println("AutoBackupService stopped.");
        }
    }

    // Loads all students, grades, and users from the DB and pushes them into InMemoryCache.
    // Called automatically by the scheduler.
    public static void refreshCache() throws SQLException {
        List<StudentModel> students = new StudentDAO().getAllStudents();
        List<GradeModel>   grades   = new GradeDAO().getAllGrades();
        List<UserModel>    users    = new UserDAO().getAllUsers();
        InMemoryCache.getInstance().refresh(students, grades, users);
        System.out.println("Cache refreshed: "
                + students.size() + " students, "
                + grades.size()   + " grades, "
                + users.size()    + " users.");
    }

    // Runs one full cycle: refresh the in-memory cache then write a SQL dump.
    // Each step is independent; a failure in one does not prevent the other.
    private static void runCycle() {
        try {
            refreshCache();
        } catch (SQLException e) {
            System.err.println("Cache refresh failed (DB unavailable): " + e.getMessage());
        }

        try {
            DatabaseBackupUtils.createBackup();
            System.out.println("SQL backup created successfully.");
        } catch (IOException e) {
            System.err.println("SQL backup failed: " + e.getMessage());
        }
    }
}