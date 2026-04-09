package com.example.controller;

import java.sql.*;

import com.example.model.DatabaseInitializer;
import com.example.model.PasswordHasher;
import com.example.model.PasswordVerifier;
import com.example.model.SaltUtil;
import com.example.model.UserModel;

/*
 * Data Access Object pour la table app_user.
 *
 * Gère l'inscription (register) et l'authentification (login) en utilisant
 * les utilitaires de hachage existants 

 */
public class UserDAO {

    /**
    Inscrit un nouvel utilisateur.
     *
     * @param email    adresse e-mail (clé primaire)
     * @param password mot de passe en clair (jamais stocké)
     * @param isAdmin  vrai si le compte doit avoir les droits admin
     * @return true si l'insertion a réussi
     * @throws SQLException si une erreur SQL survient (ex. email déjà pris)
     * @throws Exception    si le hachage du mot de passe échoue
     */
    public boolean register(String email, String password, boolean isAdmin)
            throws Exception {

        String salt         = SaltUtil.generateSalt();
        String passwordHash = PasswordHasher.hashPassword(password, salt);

        String sql = """
                INSERT INTO app_user (email, password_hash, salt, is_admin)
                VALUES (?, ?, ?, ?);
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, passwordHash);
            stmt.setString(3, salt);
            stmt.setBoolean(4, isAdmin);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Vérifie les identifiants d'un utilisateur.
     *
     * @param email    adresse e-mail
     * @param password mot de passe en clair à vérifier
     * @return le UserModel correspondant si les identifiants sont corrects,
     *         null sinon (email inexistant ou mot de passe incorrect)
     * @throws SQLException si une erreur SQL survient
     * @throws Exception    si la vérification du hash échoue
     */
    public UserModel login(String email, String password) throws Exception {

        UserModel user = getUserByEmail(email);

        if (user == null) {
            return null;   // email inconnu
        }

        boolean valid = PasswordVerifier.verify(password, user.getSalt(), user.getPasswordHash());
        return valid ? user : null;
    }

    /**
     * Récupère un utilisateur par son e-mail.
     *
     * @param email adresse e-mail à rechercher
     * @return le UserModel, ou null si introuvable
     * @throws SQLException si une erreur SQL survient
     */
    public UserModel getUserByEmail(String email) throws SQLException {

        String sql = """
                SELECT email, password_hash, salt, creation_date, is_admin
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

    /**
     * Vérifie si une adresse e-mail est déjà enregistrée.
     *
     * @param email adresse e-mail à tester
     * @return true si l'e-mail existe en base
     * @throws SQLException si une erreur SQL survient
     */
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

    /**
     * Met à jour le mot de passe d'un utilisateur existant.
     * Un nouveau sel est généré à chaque changement de mot de passe.
     *
     * @param email       adresse e-mail de l'utilisateur
     * @param newPassword nouveau mot de passe en clair
     * @return true si la mise à jour a réussi
     * @throws SQLException si une erreur SQL survient
     * @throws Exception    si le hachage du nouveau mot de passe échoue
     */
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

    /**
     * Supprime un utilisateur par son e-mail.
     *
     * @param email adresse e-mail à supprimer
     * @return true si la suppression a réussi
     * @throws SQLException si une erreur SQL survient
     */
    public boolean deleteUser(String email) throws SQLException {

        String sql = "DELETE FROM app_user WHERE email = ?;";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            return stmt.executeUpdate() > 0;
        }
    }

    /** Construit un UserModel à partir d'une ligne ResultSet. */
    private UserModel mapRow(ResultSet rs) throws SQLException {
        return new UserModel(
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("salt"),
                rs.getTimestamp("creation_date").toLocalDateTime(),
                rs.getBoolean("is_admin")
        );
    }
}