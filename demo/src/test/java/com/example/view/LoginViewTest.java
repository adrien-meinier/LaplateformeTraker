package com.example.view;

import com.example.TestFXInitializer;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginViewTest extends TestFXInitializer {

    private Stage stage;
    private LoginView view;

    @BeforeEach
    void setup() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            stage = new Stage();
            view = new LoginView(stage);
            view.show();
            latch.countDown();
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS),
                "JavaFX n'a pas fini l'initialisation");
    }

    private Object getField(String name) throws Exception {
        Field f = LoginView.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(view);
    }

    // ---------------------------------------------------------
    // 1. Structure
    // ---------------------------------------------------------

    @Test
    @Order(1)
    void testSceneExists() {
        assertNotNull(stage.getScene());
        assertInstanceOf(HBox.class, stage.getScene().getRoot());
    }

    @Test
    @Order(2)
    void testFieldsExist() throws Exception {
        assertNotNull(getField("tfUser"));
        assertNotNull(getField("pfPass"));
        assertNotNull(getField("lblError"));
        assertNotNull(getField("btnLogin"));
    }

    // ---------------------------------------------------------
    // 2. Erreurs (UI uniquement)
    // ---------------------------------------------------------

    @Test
    @Order(3)
    void testEmptyFieldsShowsError() throws Exception {
        Button btn = (Button) getField("btnLogin");
        Label lbl = (Label) getField("lblError");

        Platform.runLater(btn::fire);
        Thread.sleep(200);

        assertTrue(lbl.isVisible());
    }

    @Test
    @Order(4)
    void testWrongCredentialsShowsError() throws Exception {
        TextField tf = (TextField) getField("tfUser");
        PasswordField pf = (PasswordField) getField("pfPass");
        Button btn = (Button) getField("btnLogin");
        Label lbl = (Label) getField("lblError");

        Platform.runLater(() -> {
            tf.setText("wrong");
            pf.setText("wrong");
            btn.fire();
        });

        Thread.sleep(250);

        assertTrue(lbl.isVisible());
    }
}
//     @Test
//     @Order(5)
//     void testThreeAttemptsDisablesButton() throws Exception {
//         TextField tf = (TextField) getField("tfUser");
//         PasswordField pf = (PasswordField) getField("pfPass");
//         Button btn = (Button) getField("btnLogin");

//         for (int i = 0; i < 3; i++) {
//             Platform.runLater(() -> {
//                 tf.setText("wrong");
//                 pf.setText("wrong");
//                 btn.fire();
//             });
//             Thread.sleep(250);
//         }

//         assertTrue(btn.isDisable());
//     }
// }
