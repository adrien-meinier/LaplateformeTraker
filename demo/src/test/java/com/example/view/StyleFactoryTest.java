package com.example.view;

import com.example.TestFXInitializer;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class StyleFactoryTest extends TestFXInitializer {

    @BeforeEach
    void initFX() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(latch::countDown);
        latch.await(1, TimeUnit.SECONDS);
    }

    // ---------------------------------------------------------
    // 1. Test des constantes
    // ---------------------------------------------------------

    @Test
    void testColorConstantsNotNull() {
        assertNotNull(StyleFactory.C_PRIMARY);
        assertNotNull(StyleFactory.C_ACCENT);
        assertNotNull(StyleFactory.C_SUCCESS);
        assertNotNull(StyleFactory.C_DANGER);
        assertNotNull(StyleFactory.C_WARNING);
        assertNotNull(StyleFactory.C_LIGHT);
        assertNotNull(StyleFactory.C_WHITE);
        assertNotNull(StyleFactory.C_TEXT_DARK);
        assertNotNull(StyleFactory.C_TEXT_GREY);
        assertNotNull(StyleFactory.C_BG);
    }

    // ---------------------------------------------------------
    // 2. Test des styles simples
    // ---------------------------------------------------------

    @Test
    void testRootBg() {
        assertTrue(StyleFactory.rootBg().contains(StyleFactory.C_BG));
    }

    @Test
    void testCardBg() {
        assertTrue(StyleFactory.cardBg().contains(StyleFactory.C_WHITE));
    }

    @Test
    void testSidebarBg() {
        assertTrue(StyleFactory.sidebarBg().contains(StyleFactory.C_PRIMARY));
    }

    @Test
    void testTextFieldStyle() {
        assertTrue(StyleFactory.textFieldStyle().contains("-fx-background-color"));
    }

    @Test
    void testDarkInputStyle() {
        assertTrue(StyleFactory.darkInputStyle(false).contains(StyleFactory.D_BORDER));
        assertTrue(StyleFactory.darkInputStyle(true).contains(StyleFactory.D_ACCENT));
    }

    // ---------------------------------------------------------
    // 3. Test des boutons
    // ---------------------------------------------------------

    @Test
    void testPrimaryBtn() {
        Button b = StyleFactory.primaryBtn("Test");
        assertEquals("Test", b.getText());
    }

    @Test
    void testSuccessBtn() {
        Button b = StyleFactory.successBtn("OK");
        assertEquals("OK", b.getText());
    }

    @Test
    void testDangerBtn() {
        Button b = StyleFactory.dangerBtn("X");
        assertEquals("X", b.getText());
    }

    @Test
    void testExportBtn() {
        Button b = StyleFactory.exportBtn("Export");
        assertEquals("Export", b.getText());
    }

    @Test
    void testBulletinBtn() {
        Button b = StyleFactory.bulletinBtn("Bulletin");
        assertEquals("Bulletin", b.getText());
    }

    @Test
    void testSidebarBtn() {
        Button b = StyleFactory.sidebarBtn("Menu");
        assertEquals("Menu", b.getText());
        assertNotNull(b.getUserData());
    }

    @Test
    void testGradientButtons() {
        assertEquals("Login", StyleFactory.loginBtn("Login").getText());
        assertEquals("Register", StyleFactory.registerBtn("Register").getText());
    }

    // ---------------------------------------------------------
    // 4. Test des animations
    // ---------------------------------------------------------

    @Test
    void testAnimateFadeSlideIn() {
        Node n = new Button("X");
        assertDoesNotThrow(() -> Platform.runLater(() ->
                StyleFactory.animateFadeSlideIn(n, 50, 200)
        ));
    }

    @Test
    void testAnimateShake() {
        Node n = new Button("X");
        assertDoesNotThrow(() -> Platform.runLater(() ->
                StyleFactory.animateShake(n, 5)
        ));
    }

    @Test
    void testAnimateFadeIn() {
        Node n = new Button("X");
        assertDoesNotThrow(() -> Platform.runLater(() ->
                StyleFactory.animateFadeIn(n, 200)
        ));
    }

    @Test
    void testAnimatePulse() {
        Node n = new Button("X");
        assertDoesNotThrow(() -> Platform.runLater(() ->
                StyleFactory.animatePulse(n)
        ));
    }

    @Test
    void testAnimatedOrb() {
        Circle c = StyleFactory.animatedOrb(20, "#ff0000", 0.5, 10, 10, 500);
        assertNotNull(c);
        assertEquals(20, c.getRadius());
        assertTrue(c.getFill() instanceof Color);
    }

    @Test
    void testFloatOrb() {
        Circle c = new Circle(10);
        assertDoesNotThrow(() -> Platform.runLater(() ->
                StyleFactory.floatOrb(c, 5, 5, 300)
        ));
    }

    // ---------------------------------------------------------
    // 5. Test helpers
    // ---------------------------------------------------------

    @Test
    void testSpacer() {
        Region r = StyleFactory.spacer(50);
        assertEquals(50, r.getPrefHeight());
    }
}
