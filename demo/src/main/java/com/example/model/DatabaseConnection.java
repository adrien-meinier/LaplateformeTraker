package com.example.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Manages the full lifecycle of a JDBC connection: open, prepare, execute, close.
// Used by all DAOs as the single entry point for database access.
public class DatabaseConnection implements AutoCloseable {

    private Connection connection;

    // Opens a connection using DatabaseInitializer credentials.
    public void openConnection() throws SQLException {
        this.connection = DatabaseInitializer.getConnection();
    }

    // Returns a PreparedStatement for the given SQL on the open connection.
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Connection is not open. Call openConnection() first.");
        }
        return connection.prepareStatement(sql);
    }

    // Executes a SELECT statement and returns the ResultSet.
    public ResultSet executeQuery(PreparedStatement stmt) throws SQLException {
        return stmt.executeQuery();
    }

    // Executes an INSERT, UPDATE, or DELETE and returns the affected row count.
    public int executeUpdate(PreparedStatement stmt) throws SQLException {
        return stmt.executeUpdate();
    }

    // Closes the underlying connection and releases all JDBC resources.
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
        }
    }

    @Override
    public void close() throws SQLException {
        closeConnection();
    }
}