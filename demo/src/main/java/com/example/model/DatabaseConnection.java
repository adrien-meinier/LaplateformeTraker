package com.example.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Class util for managing database connections.
 * Centralise the lifecycle of a connection: opening, execution, closing.
 * Intended to be used by DAOs to perform their queries.
 */
public class DatabaseConnection implements AutoCloseable {

    private Connection connection;

    // opens a connection to the database using the DatabaseInitializer utility.

    public void openConnection() throws SQLException {
        this.connection = DatabaseInitializer.getConnection();
    }

    // prepares and returns a {@link PreparedStatement} on the open connection.

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("La connexion n'est pas ouverte. Appelez openConnection() d'abord.");
        }
        return connection.prepareStatement(sql);
    }


    // closes the connection if it is open. Should be called after all operations are done to free resources.

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
