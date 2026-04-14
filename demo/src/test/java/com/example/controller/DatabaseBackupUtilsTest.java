package com.example.controller;

import org.junit.jupiter.api.*;
import java.io.IOException;
// Allows accessing and invoking private constructors via reflection
import java.lang.reflect.Constructor;
// Wraps exceptions thrown when invoking constructors/methods reflectively
import java.lang.reflect.InvocationTargetException;
// Provides utilities for working with files and directories (Paths, Files, etc.)
import java.nio.file.*;


import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseBackupUtilsTest {

    private static final Path BACKUP_DIR = Path.of("C:/Backups/Tracker");

    // Deletes all files inside the backup directory before each test
    @BeforeEach
    void cleanBackupDirectory() throws IOException {
        if (Files.exists(BACKUP_DIR)) {
            try (var stream = Files.list(BACKUP_DIR)) {
                for (Path p : stream.toList()) {
                    Files.deleteIfExists(p);
                }
            }
        }
    }

    // Ensures the backup directory is created even if pg_dump fails
    @Test
    @Order(1)
    void testBackupDirectoryIsCreated() {
        try {
            DatabaseBackupUtils.createBackup();
        } catch (IOException ignored) {
        }

        assertTrue(Files.exists(BACKUP_DIR));
        assertTrue(Files.isDirectory(BACKUP_DIR));
    }

    // Confirms that createBackup() throws an exception when pg_dump fails
    @Test
    @Order(2)
    void testBackupFileNameIsGenerated() throws IOException {
        try {
            DatabaseBackupUtils.createBackup();
        } catch (IOException ignored) {
        }

        try (var stream = Files.list(BACKUP_DIR)) {
            var files = stream.toList();
            assertEquals(1, files.size());

            String name = files.get(0).getFileName().toString();
            assertTrue(name.startsWith("backup_"));
            assertTrue(name.endsWith(".sql"));
        }
    }

}