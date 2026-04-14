package com.example.controller;

import com.example.model.DatabaseInitializer;
import com.example.model.UserModel;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDAOTest {

    private static final UserDAO dao = new UserDAO();
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "securePassword!1";
    private static final String USERNAME = "myUser";

    // Clean users before each test
    @BeforeEach
    void cleanUsers() throws SQLException {
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM app_user;");
        }
    }

    @Test
    @Order(1)
    void register_shouldReturnTrueOnSuccess() throws Exception {
        boolean result = dao.register(USERNAME, EMAIL, PASSWORD, false);
        assertTrue(result, "Registration must return true");
    }

    @Test
    @Order(2)
    void emailExists_shouldReturnTrueAfterRegister() throws Exception {
        dao.register(USERNAME, EMAIL, PASSWORD, false);
        assertTrue(dao.emailExists(EMAIL), "Email must be found after registration");
    }

    @Test
    @Order(3)
    void emailExists_shouldReturnFalseForUnknownEmail() throws SQLException {
        assertFalse(dao.emailExists("nobody@nowhere.com"), "Unknown email must return false");
    }

    @Test
    @Order(4)
    void getUserByEmail_shouldReturnCorrectUser() throws Exception {
        dao.register(USERNAME, EMAIL, PASSWORD, true);
        UserModel user = dao.getUserByEmail(EMAIL);

        assertNotNull(user);
        assertEquals(EMAIL, user.getEmail());
        assertTrue(user.isAdmin(), "User registered as admin must have isAdmin = true");
        assertNotNull(user.getSalt());
        assertNotNull(user.getPasswordHash());
        assertNotNull(user.getCreationDate());
    }

    @Test
    @Order(5)
    void getUserByEmail_shouldReturnNullForUnknownEmail() throws SQLException {
        UserModel user = dao.getUserByEmail("nobody@nowhere.com");
        assertNull(user, "Unknown email must return null");
    }

    @Test
    @Order(6)
    void login_shouldReturnNullOnWrongPassword() throws Exception {
        dao.register(USERNAME, EMAIL, PASSWORD, false);
        UserModel user = dao.login(EMAIL, "wrongPassword");
        assertNull(user, "Login must fail with wrong password");
    }

    @Test
    @Order(7)
    void login_shouldReturnNullForUnknownEmail() throws Exception {
        UserModel user = dao.login("ghost@nowhere.com", PASSWORD);
        assertNull(user, "Login must fail for unknown email");
    }

    @Test
    @Order(8)
    void updatePassword_shouldAllowLoginWithNewPassword() throws Exception {
        dao.register(USERNAME, EMAIL, PASSWORD, false);
        String newPassword = "newSecurePass!2";
        boolean updated = dao.updatePassword(EMAIL, newPassword);

        assertTrue(updated);
        // Old password must no longer work
        assertNull(dao.login(EMAIL, PASSWORD), "Old password must be rejected after update");
    }

    @Test
    @Order(9)
    void updatePassword_shouldReturnFalseForUnknownEmail() throws Exception {
        boolean updated = dao.updatePassword("nobody@nowhere.com", "anyPass");
        assertFalse(updated, "Update must return false for unknown email");
    }

    @Test
    @Order(10)
    void deleteUser_shouldRemoveUser() throws Exception {
        dao.register(USERNAME, EMAIL, PASSWORD, false);
        boolean deleted = dao.deleteUser(EMAIL);

        assertTrue(deleted);
        assertFalse(dao.emailExists(EMAIL), "User must not exist after deletion");
    }

    @Test
    @Order(11)
    void deleteUser_shouldReturnFalseForUnknownEmail() throws SQLException {
        boolean deleted = dao.deleteUser("ghost@nowhere.com");
        assertFalse(deleted, "Delete on unknown email must return false");
    }

}