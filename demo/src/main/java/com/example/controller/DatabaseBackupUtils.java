package com.example.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Creates and manages PostgreSQL SQL dumps via pg_dump.
// Scheduled automatically by AutoBackupService; can also be called manually.
public final class DatabaseBackupUtils {

    private static final String DB_HOST     = "localhost";
    private static final String DB_PORT     = "5432";
    private static final String DB_NAME     = "laplateformetracker";
    private static final String DB_USER     = "postgres";
    private static final String DB_PASSWORD = "root";

    private static final String PG_DUMP_PATH =
            "C:\\Program Files\\PostgreSQL\\18\\bin\\pg_dump.exe";

    static final Path BACKUP_DIR        = Path.of("C:/Backups/Tracker");
    private static final Path BACKUP_PARENT_DIR = Path.of("C:/Backups");

    // Creates a timestamped .sql backup using pg_dump.
    // Throws IOException if pg_dump exits with a non-zero code or is interrupted.
    public static void createBackup() throws IOException {
        if (!Files.exists(BACKUP_DIR)) {
            Files.createDirectories(BACKUP_DIR);
        }

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path backupFile = BACKUP_DIR.resolve("backup_" + timestamp + ".sql");

        ProcessBuilder pb = new ProcessBuilder(
                PG_DUMP_PATH,
                "-h", DB_HOST,
                "-p", DB_PORT,
                "-U", DB_USER,
                "-d", DB_NAME,
                "-f", backupFile.toString()
        );
        pb.environment().put("PGPASSWORD", DB_PASSWORD);
        pb.inheritIO();

        Process process = pb.start();
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("pg_dump failed with exit code: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Backup interrupted", e);
        }
    }

    // Deletes all backup files and the backup directory.
    public static void deleteAllBackups() throws IOException {
        if (!Files.exists(BACKUP_DIR)) {
            return;
        }

        try (var files = Files.list(BACKUP_DIR)) {
            files.forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    System.err.println("Could not delete backup file: " + path + " — " + e.getMessage());
                }
            });
        }

        try {
            Files.deleteIfExists(BACKUP_DIR);
            Files.deleteIfExists(BACKUP_PARENT_DIR);
        } catch (IOException e) {
            System.err.println("Could not delete backup directory: " + e.getMessage());
        }
    }
}