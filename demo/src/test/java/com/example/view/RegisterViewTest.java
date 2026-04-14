package com.example.view;

import com.example.TestFXInitializer;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegisterViewTest extends TestFXInitializer {

    private Stage stage;
    private RegisterView view;

    @BeforeEach
    void setup() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            stage = new Stage();
            view = new RegisterView(stage);
            view.show();
            latch.countDown();
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS),
                "JavaFX n'a pas fini l'initialisation");
    }

    // ---------------------------------------------------------
    // 1. Structure UI
    // ---------------------------------------------------------

    @Test
    @Order(1)
    void testSceneExists() {
        assertNotNull(stage.getScene());
    }

    @Test
    @Order(2)
    void testFieldsExist() throws Exception {
        assertNotNull(getField("tfUsername"));
        assertNotNull(getField("tfEmail"));
        assertNotNull(getField("pfPass"));
        assertNotNull(getField("pfConfirm"));
    }

    @Test
    @Order(3)
    void testRegisterButtonExists() throws Exception {
        assertNotNull(getField("btnRegister"));
    }

    @Test
    @Order(4)
    void testErrorLabelExists() throws Exception {
        assertNotNull(getField("lblError"));
    }

    // ---------------------------------------------------------
    // 2. Validation
    // ---------------------------------------------------------

    @Test
    @Order(5)
    void testEmptyFieldsError() throws Exception {
        Button btn = (Button) getField("btnRegister");

        Platform.runLater(btn::fire);
        Thread.sleep(150);

        Label lbl = (Label) getField("lblError");
        assertTrue(lbl.isVisible());
    }

    @Test
    @Order(6)
    void testInvalidEmail() throws Exception {
        TextField user = (TextField) getField("tfUsername");
        TextField email = (TextField) getField("tfEmail");
        PasswordField pass = (PasswordField) getField("pfPass");
        PasswordField confirm = (PasswordField) getField("pfConfirm");
        Button btn = (Button) getField("btnRegister");

        Platform.runLater(() -> {
            user.setText("bob");
            email.setText("invalid");
            pass.setText("Password!");
            confirm.setText("Password!");
            btn.fire();
        });

        Thread.sleep(150);

        Label lbl = (Label) getField("lblError");
        assertTrue(lbl.getText().toLowerCase().contains("mail"));
    }

    // ---------------------------------------------------------
    // Utilitaire
    // ---------------------------------------------------------

    private Object getField(String name) throws Exception {
        Field f = RegisterView.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(view);
    }
}
