package com.example.Model;

import org.junit.jupiter.api.Test;

import com.example.model.DatabaseInitializer;

import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseInitializerTest {

    /**
     * Helper method that returns "<empty>" if the table contains zero rows.
     * Uses a simple SELECT COUNT(*) query.
     */
    private String getTableState(String tableName) throws SQLException {
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM " + tableName);
             ResultSet rs = ps.executeQuery()) {

            rs.next(); // Moves to the first (and only) row
            int count = rs.getInt(1);

            return count == 0 ? "<empty>" : "not empty";
        }
    }

    @Test
    void testAppUserTableIsEmpty() throws Exception {
        // Ensures tables exist before checking their content.
        DatabaseInitializer.initialize();

        // Verifies that the table contains zero rows.
        assertEquals("<empty>", getTableState("app_user"));
    }

    @Test
    void testStudentTableIsEmpty() throws Exception {
        DatabaseInitializer.initialize();
        assertEquals("<empty>", getTableState("student"));
    }

    @Test
    void testGradesTableIsEmpty() throws Exception {
        DatabaseInitializer.initialize();
        assertEquals("<empty>", getTableState("grades"));
    }
}