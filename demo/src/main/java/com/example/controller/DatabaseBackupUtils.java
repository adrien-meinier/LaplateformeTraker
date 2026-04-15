package com.example.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Minimal PostgreSQL backup utility using pg_dump.

public final class DatabaseBackupUtils {

    private static final String DB_HOST     = "localhost";
    private static final String DB_PORT     = "5432";
    private static final String DB_NAME     = "laplateformetracker";
    private static final String DB_USER     = "postgres";
    private static final String DB_PASSWORD = "root";

    // Full path to pg_dump.exe (Solution 1)
    private static final String PG_DUMP_PATH =
            "C:\\Program Files\\PostgreSQL\\18\\bin\\pg_dump.exe";

    // Directory where backups will be stored
    private static final Path BACKUP_DIR = Path.of("C:/Backups/Tracker");
    private static final Path BACKUP_PARENT_DIR = Path.of("C:/Backups");

    // Creates a timestamped backup using pg_dump.
    public static void createBackup() throws IOException {

        // Ensure backup directory exists
        if (!Files.exists(BACKUP_DIR)) {
            Files.createDirectories(BACKUP_DIR);
        }

        // Timestamp for filename
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        Path backupFile = BACKUP_DIR.resolve("backup_" + timestamp + ".sql");

        // Build pg_dump command using full path
        ProcessBuilder pb = new ProcessBuilder(
                PG_DUMP_PATH,
                "-h", DB_HOST,
                "-p", DB_PORT,
                "-U", DB_USER,
                "-d", DB_NAME,
                "-f", backupFile.toString()
        );

        // Pass password securely
        pb.environment().put("PGPASSWORD", DB_PASSWORD);

        // Optional: inherit IO to see pg_dump output in console
        pb.inheritIO();

        // Start backup
        Process process = pb.start();

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Backup failed with exit code: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Backup interrupted", e);
        }
    }

    public static void deleteAllBackups() throws IOException {

    if (!Files.exists(BACKUP_DIR)) {
        return; // Nothing to delete
    }

    // Deletes files in the directory
    try (var files = Files.list(BACKUP_DIR)) {
        files.forEach(path -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                System.err.println("Impossible de supprimer : " + path + " -> " + e.getMessage());
            }
        });
    }

    // Delete the directory itself
    try {
        Files.deleteIfExists(BACKUP_DIR);
        Files.deleteIfExists(BACKUP_PARENT_DIR);
    } catch (IOException e) {
        System.err.println("Impossible de supprimer le dossier : " + e.getMessage());
    }
}

}