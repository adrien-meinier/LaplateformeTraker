package com.example.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.model.DatabaseConnection;
import com.example.model.InMemoryCache;
import com.example.model.PasswordHasher;
import com.example.model.PasswordVerifier;
import com.example.model.SaltUtil;
import com.example.model.UserModel;

// Data Access Object for the app_user table.
// Read operations fall back to InMemoryCache when the database is unreachable.
// Write operations always require a live connection and propagate any exception.
public class UserDAO {

    // Registers a new user with a freshly generated salt and hashed password.
    public boolean register(String username, String email, String password, boolean isAdmin)
            throws Exception {
        String salt         = SaltUtil.generateSalt();
        String passwordHash = PasswordHasher.hashPassword(password, salt);

        String sql = """
                INSERT INTO app_user (username, email, password_hash, salt, is_admin)
                VALUES (?, ?, ?, ?, ?);
                """;

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            try (PreparedStatement stmt = db.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, passwordHash);
                stmt.setString(4, salt);
                stmt.setBoolean(5, isAdmin);
                return db.executeUpdate(stmt) > 0;
            }
        }
    }

    // Authenticates a user by username and password. Returns null if credentials are invalid.
    // Falls back to cached user data for lookup; password verification is always local.
    public UserModel login(String username, String password) throws Exception {
        UserModel user = getUserByUsername(username);
        if (user == null) return null;
        boolean valid = PasswordVerifier.verify(password, user.getSalt(), user.getPasswordHash());
        return valid ? user : null;
    }

    // Looks up a user by username. Falls back to cache if DB is unavailable.
    public UserModel getUserByUsername(String username) throws SQLException {
        String sql = """
                SELECT username, email, password_hash, salt, creation_date, is_admin
                FROM app_user
                WHERE username = ?;
                """;

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            try (PreparedStatement stmt = db.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = db.executeQuery(stmt)) {
                    if (rs.next()) {
                        return mapRow(rs);
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            InMemoryCache cache = InMemoryCache.getInstance();
            if (cache.isPopulated()) {
                System.err.println("DB unavailable, falling back to cache (getUserByUsername): " + e.getMessage());
                return cache.getUserByUsername(username);
            }
            throw e;
        }
    }

    // Looks up a user by email. Falls back to cache if DB is unavailable.
    public UserModel getUserByEmail(String email) throws SQLException {
        String sql = """
                SELECT username, email, password_hash, salt, creation_date, is_admin
                FROM app_user
                WHERE email = ?;
                """;

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            try (PreparedStatement stmt = db.prepareStatement(sql)) {
                stmt.setString(1, email);
                try (ResultSet rs = db.executeQuery(stmt)) {
                    if (rs.next()) {
                        return mapRow(rs);
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            InMemoryCache cache = InMemoryCache.getInstance();
            if (cache.isPopulated()) {
                System.err.println("DB unavailable, falling back to cache (getUserByEmail): " + e.getMessage());
                return cache.getUserByEmail(email);
            }
            throw e;
        }
    }

    // Returns all registered users. Used by AutoBackupService to populate InMemoryCache.
    // Falls back to cache if DB is unavailable.
    public List<UserModel> getAllUsers() throws SQLException {
        String sql = """
                SELECT username, email, password_hash, salt, creation_date, is_admin
                FROM app_user
                ORDER BY username;
                """;

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            List<UserModel> users = new ArrayList<>();
            try (PreparedStatement stmt = db.prepareStatement(sql);
                 ResultSet rs = db.executeQuery(stmt)) {
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
            }
            return users;
        } catch (SQLException e) {
            InMemoryCache cache = InMemoryCache.getInstance();
            if (cache.isPopulated()) {
                System.err.println("DB unavailable, falling back to cache (getAllUsers): " + e.getMessage());
                return cache.getUsers();
            }
            throw e;
        }
    }

    // Checks whether an email address is already registered. Falls back to cache.
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM app_user WHERE email = ?;";

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            try (PreparedStatement stmt = db.prepareStatement(sql)) {
                stmt.setString(1, email);
                try (ResultSet rs = db.executeQuery(stmt)) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            InMemoryCache cache = InMemoryCache.getInstance();
            if (cache.isPopulated()) {
                System.err.println("DB unavailable, falling back to cache (emailExists): " + e.getMessage());
                return cache.emailExists(email);
            }
            throw e;
        }
    }

    // Checks whether a username is already taken. Falls back to cache.
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM app_user WHERE username = ?;";

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            try (PreparedStatement stmt = db.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = db.executeQuery(stmt)) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            InMemoryCache cache = InMemoryCache.getInstance();
            if (cache.isPopulated()) {
                System.err.println("DB unavailable, falling back to cache (usernameExists): " + e.getMessage());
                return cache.usernameExists(username);
            }
            throw e;
        }
    }

    // Replaces a user's password with a freshly hashed value.
    public boolean updatePassword(String email, String newPassword) throws Exception {
        String newSalt = SaltUtil.generateSalt();
        String newHash = PasswordHasher.hashPassword(newPassword, newSalt);

        String sql = """
                UPDATE app_user
                SET password_hash = ?, salt = ?
                WHERE email = ?;
                """;

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            try (PreparedStatement stmt = db.prepareStatement(sql)) {
                stmt.setString(1, newHash);
                stmt.setString(2, newSalt);
                stmt.setString(3, email);
                return db.executeUpdate(stmt) > 0;
            }
        }
    }

    // Permanently deletes a user account by email.
    public boolean deleteUser(String email) throws SQLException {
        String sql = "DELETE FROM app_user WHERE email = ?;";

        try (DatabaseConnection db = new DatabaseConnection()) {
            db.openConnection();
            try (PreparedStatement stmt = db.prepareStatement(sql)) {
                stmt.setString(1, email);
                return db.executeUpdate(stmt) > 0;
            }
        }
    }

    // Maps the current ResultSet row to a UserModel. Handles nullable creation_date.
    private UserModel mapRow(ResultSet rs) throws SQLException {
        var tsCreated = rs.getTimestamp("creation_date");
        return new UserModel(
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("salt"),
                tsCreated != null ? tsCreated.toLocalDateTime() : null,
                rs.getBoolean("is_admin")
        );
    }
}