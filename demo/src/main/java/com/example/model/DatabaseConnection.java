package com.example.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Classe utilitaire de gestion des connexions SQL.
 * Centralise le cycle de vie d'une connexion : ouverture, exécution, fermeture.
 * Destinée à être utilisée par les DAO pour effectuer leurs requêtes.
 */
public class DatabaseConnection implements AutoCloseable {

    private Connection connection;

    // Ouvre une connexion à la base de données et la conserve en mémoire.

    public void openConnection() throws SQLException {
        this.connection = DatabaseInitializer.getConnection();
    }

    // Prépare et retourne un {@link PreparedStatement} sur la connexion ouverte.

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("La connexion n'est pas ouverte. Appelez openConnection() d'abord.");
        }
        return connection.prepareStatement(sql);
    }


    // Ferme la connexion à la base de données si elle est ouverte.

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
