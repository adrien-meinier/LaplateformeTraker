package com.example.controller;

import java.sql.*;

import com.example.model.DatabaseInitializer;
import com.example.model.PasswordHasher;
import com.example.model.PasswordVerifier;
import com.example.model.SaltUtil;
import com.example.model.UserModel;

public class UserDAO {

    /** Inscription d'un nouvel utilisateur */
    public boolean register(String username, String email, String password, boolean isAdmin)
            throws Exception {

        String salt         = SaltUtil.generateSalt();
        String passwordHash = PasswordHasher.hashPassword(password, salt);

        String sql = """
                INSERT INTO app_user (username, email, password_hash, salt, is_admin)
                VALUES (?, ?, ?, ?, ?);
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, passwordHash);
            stmt.setString(4, salt);
            stmt.setBoolean(5, isAdmin);

            return stmt.executeUpdate() > 0;
        }
    }

    /** Connexion par username + mot de passe */
    public UserModel login(String username, String password) throws Exception {

        UserModel user = getUserByUsername(username);

        if (user == null) return null;

        boolean valid = PasswordVerifier.verify(password, user.getSalt(), user.getPasswordHash());
        return valid ? user : null;
    }

    /** Récupère un utilisateur par son username */
    public UserModel getUserByUsername(String username) throws SQLException {

        String sql = """
                SELECT username, email, password_hash, salt, creation_date, is_admin
                FROM app_user
                WHERE username = ?;
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /** Récupère un utilisateur par son email */
    public UserModel getUserByEmail(String email) throws SQLException {

        String sql = """
                SELECT username, email, password_hash, salt, creation_date, is_admin
                FROM app_user
                WHERE email = ?;
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /** Vérifie si un email existe déjà */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM app_user WHERE email = ?;";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Vérifie si un username existe déjà */
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM app_user WHERE username = ?;";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Mise à jour du mot de passe */
    public boolean updatePassword(String email, String newPassword) throws Exception {

        String newSalt = SaltUtil.generateSalt();
        String newHash = PasswordHasher.hashPassword(newPassword, newSalt);

        String sql = """
                UPDATE app_user
                SET password_hash = ?, salt = ?
                WHERE email = ?;
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newHash);
            stmt.setString(2, newSalt);
            stmt.setString(3, email);

            return stmt.executeUpdate() > 0;
        }
    }

    /** Suppression */
    public boolean deleteUser(String email) throws SQLException {

        String sql = "DELETE FROM app_user WHERE email = ?;";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            return stmt.executeUpdate() > 0;
        }
    }

    /** Mapping ResultSet → UserModel */
    private UserModel mapRow(ResultSet rs) throws SQLException {
        return new UserModel(
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("salt"),
                rs.getTimestamp("creation_date").toLocalDateTime(),
                rs.getBoolean("is_admin")
        );
    }
}
