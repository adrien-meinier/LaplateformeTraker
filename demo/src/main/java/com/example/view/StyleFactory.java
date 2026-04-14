package com.example.view;

import javafx.animation.*;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.effect.GaussianBlur;
import javafx.util.Duration;

/**
 * styleFactory — centralised factory for all colors, styles, buttons, and animations in the application.
 *
 * all styles, colors, and animations used across the application are defined in this class. This includes:
 *  - colors for light and dark themes (public static final String constants)
 *  - styles for backgrounds, text fields, labels, tables (public static methods returning style strings)
 *  - button factory methods for different types of buttons (primary, success, danger, sidebar, etc.) that return pre-styled Button instances with hover effects
 *  - animations such as fade-slide-in, shake, pulse, and animated orbs for decorative purposes. These methods encapsulate the animation logic and can be easily reused across different views to add visual feedback and interactivity.
 */
public final class StyleFactory {

    // ════════════════════════════════════════════════════════════════════ //
    //  ligtht theme palette (dashboard)
    // ════════════════════════════════════════════════════════════════════ //
    public static final String C_PRIMARY    = "#2c3e50";
    public static final String C_ACCENT     = "#3498db";
    public static final String C_SUCCESS    = "#27ae60";
    public static final String C_DANGER     = "#e74c3c";
    public static final String C_WARNING    = "#f39c12";
    public static final String C_LIGHT      = "#ecf0f1";
    public static final String C_WHITE      = "#ffffff";
    public static final String C_TEXT_DARK  = "#2c3e50";
    public static final String C_TEXT_GREY  = "#7f8c8d";
    public static final String C_BG         = "#f4f6f8";

    // ════════════════════════════════════════════════════════════════════ //
    //  dark theme palette (login / register)
    // ════════════════════════════════════════════════════════════════════ //
    public static final String D_BG        = "#0f1117";
    public static final String D_LEFT      = "#111420";
    public static final String D_ACCENT    = "#4f8ef7";
    public static final String D_ACCENT2   = "#7c4df8";
    public static final String D_SUCCESS   = "#22c55e";
    public static final String D_DANGER    = "#f43f5e";
    public static final String D_WARNING   = "#f59e0b";
    public static final String D_TEXT      = "#f1f5f9";
    public static final String D_SUBTEXT   = "#64748b";
    public static final String D_BORDER    = "#2a2d3a";
    public static final String D_INPUT     = "#1e2130";

    private static final String RADIUS = "8";
    private static final String BTN_H  = "42";

    private StyleFactory() {}

    // ════════════════════════════════════════════════════════════════════ //
    //  bACKGROUND & CONTAINERS
    // ════════════════════════════════════════════════════════════════════ //

    public static String rootBg() {
        return "-fx-background-color:" + C_BG + ";";
    }

    public static String cardBg() {
        return "-fx-background-color:" + C_WHITE + ";"
             + "-fx-background-radius:" + RADIUS + ";"
             + "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.08),10,0,0,2);";
    }

    public static String sidebarBg() {
        return "-fx-background-color:" + C_PRIMARY + ";";
    }

    public static String darkBg() {
        return "-fx-background-color:" + D_BG + ";";
    }

    public static String darkLeftBg() {
        return "-fx-background-color:" + D_LEFT + ";";
    }

    // ════════════════════════════════════════════════════════════════════ //
    //  light mode styles (dashboard)
    // ════════════════════════════════════════════════════════════════════ //

    public static String textFieldStyle() {
        return "-fx-background-color:" + C_WHITE + ";"
             + "-fx-border-color:#dce1e7;"
             + "-fx-border-radius:6;-fx-background-radius:6;"
             + "-fx-padding:8 12;-fx-font-size:13px;";
    }

    public static String textFieldFocusStyle() {
        return "-fx-background-color:" + C_WHITE + ";"
             + "-fx-border-color:" + C_ACCENT + ";"
             + "-fx-border-radius:6;-fx-background-radius:6;"
             + "-fx-padding:8 12;-fx-font-size:13px;";
    }

    // ════════════════════════════════════════════════════════════════════ //
    //  dark mode styles (login / register)
    // ════════════════════════════════════════════════════════════════════ //

    public static String darkInputStyle(boolean focused) {
        return "-fx-background-color:" + D_INPUT + ";"
             + "-fx-border-color:" + (focused ? D_ACCENT : D_BORDER) + ";"
             + "-fx-border-radius:10;-fx-background-radius:10;"
             + "-fx-text-fill:" + D_TEXT + ";-fx-prompt-text-fill:" + D_SUBTEXT + ";"
             + "-fx-padding:0 14;-fx-font-size:13px;";
    }

    // ════════════════════════════════════════════════════════════════════ //
    //  labels, tables, and other common styles
    // ════════════════════════════════════════════════════════════════════ //

    public static String titleStyle() {
        return "-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:" + C_TEXT_DARK + ";";
    }

    public static String subtitleStyle() {
        return "-fx-font-size:13px;-fx-text-fill:" + C_TEXT_GREY + ";";
    }

    public static String labelStyle() {
        return "-fx-font-size:12px;-fx-font-weight:bold;-fx-text-fill:" + C_TEXT_DARK + ";";
    }

    public static String tableStyle() {
        return "-fx-background-color:" + C_WHITE + ";"
             + "-fx-border-color:#dce1e7;"
             + "-fx-border-radius:8;-fx-background-radius:8;";
    }

    // ════════════════════════════════════════════════════════════════════ //
    //  buttons — light mode (dashboard)
    // ════════════════════════════════════════════════════════════════════ //

    /** main blue button. */
    public static Button primaryBtn(String text) {
        return lightBtn(text, C_ACCENT, C_WHITE);
    }

    /** green success button. */
    public static Button successBtn(String text) {
        return lightBtn(text, C_SUCCESS, C_WHITE);
    }

    /** red suppression button. */
    public static Button dangerBtn(String text) {
        return lightBtn(text, C_DANGER, C_WHITE);
    }

    /** orange warning button. */
    public static Button warningBtn(String text) {
        return lightBtn(text, C_WARNING, C_WHITE);
    }

    /** grey secondary button. */
    public static Button secondaryBtn(String text) {
        return lightBtn(text, C_LIGHT, C_TEXT_DARK);
    }

    /**
     * green button with "Export" text and specific padding. This button is designed for exporting data, such as a student's report card, and is styled with a green background and white text to indicate a positive action. The button includes hover effects that slightly change the opacity to provide visual feedback when the user interacts with it.
     * used in EtudiantsView.
     */
    public static Button exportBtn(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(Double.parseDouble(BTN_H));
        String base  = exportBtnStyle(C_SUCCESS, "0.90");
        String hover = exportBtnStyle(darken(C_SUCCESS), "1.0");
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited (e -> btn.setStyle(base));
        return btn;
    }

    /**
     * button for accessing the student's report card or bulletin. This button is styled with the application's accent color and includes hover effects to enhance interactivity. The button's design is consistent with the overall theme of the application, and it serves as a clear call-to-action for users to view detailed information about a student's academic performance.
     * use in cells of the "Bulletin" column in EtudiantsView's table.
     */
    public static Button bulletinBtn(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(30);
        String base  = bulletinBtnStyle(C_ACCENT, "0.90");
        String hover = bulletinBtnStyle(darken(C_ACCENT), "1.0");
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited (e -> btn.setStyle(base));
        return btn;
    }

    /** sidebar button. */
    public static Button sidebarBtn(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(Double.parseDouble(BTN_H));
        String normal  = sidebarBtnStyle("transparent");
        String hovered = sidebarBtnStyle("rgba(255,255,255,0.12)");
        String active  = sidebarBtnStyle("rgba(255,255,255,0.22)")
                       + "-fx-border-color:transparent transparent transparent " + C_ACCENT + ";"
                       + "-fx-border-width:0 0 0 4;";
        btn.setStyle(normal);
        btn.setUserData(new String[]{ normal, hovered, active });
        btn.setOnMouseEntered(e -> { if (!isActive(btn)) btn.setStyle(hovered); });
        btn.setOnMouseExited (e -> { if (!isActive(btn)) btn.setStyle(normal); });
        return btn;
    }

    public static void setSidebarActive(Button btn) {
        btn.setStyle(((String[]) btn.getUserData())[2]);
    }

    public static void setSidebarInactive(Button btn) {
        btn.setStyle(((String[]) btn.getUserData())[0]);
    }

    // ════════════════════════════════════════════════════════════════════ //
    //  buttons — gradient variants (login / register)
    // ════════════════════════════════════════════════════════════════════ //

    public static Button gradientBtn(String label, String c1, String c2) {
        Button btn = new Button(label);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(48);
        btn.setStyle(gradientBtnStyle(c1, c2, "0.95"));
        btn.setOnMouseEntered(e -> btn.setStyle(gradientBtnStyle(c1, c2, "1.0")));
        btn.setOnMouseExited (e -> btn.setStyle(gradientBtnStyle(c1, c2, "0.95")));
        return btn;
    }

    public static Button loginBtn(String label) {
        return gradientBtn(label, D_ACCENT, D_ACCENT2);
    }

    public static Button registerBtn(String label) {
        return gradientBtn(label, D_SUCCESS, "#16a34a");
    }

    // ════════════════════════════════════════════════════════════════════ //
    // animations
    // ════════════════════════════════════════════════════════════════════ //

    public static void animateFadeSlideIn(javafx.scene.Node node, double fromX, long durationMs) {
        node.setOpacity(0);
        node.setTranslateX(fromX);
        FadeTransition ft = new FadeTransition(Duration.millis(durationMs), node);
        ft.setToValue(1);
        TranslateTransition tt = new TranslateTransition(Duration.millis(durationMs), node);
        tt.setToX(0);
        tt.setInterpolator(Interpolator.EASE_OUT);
        new ParallelTransition(ft, tt).play();
    }

    public static void animateShake(javafx.scene.Node node, double amplitude) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(55), node);
        tt.setFromX(0);
        tt.setByX(amplitude);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.setOnFinished(e -> node.setTranslateX(0));
        tt.play();
    }

    public static void animateFadeIn(javafx.scene.Node node, long durationMs) {
        node.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(durationMs), node);
        ft.setToValue(1);
        ft.play();
    }

    public static void animatePulse(javafx.scene.Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(180), node);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.06);  st.setToY(1.06);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    public static Circle animatedOrb(double radius, String color, double opacity,
                                     double dx, double dy, long ms) {
        Circle c = new Circle(radius);
        c.setFill(Color.web(color, opacity));
        c.setEffect(new GaussianBlur(radius * 0.65));

        TranslateTransition tt = new TranslateTransition(Duration.millis(ms), c);
        tt.setByX(dx); tt.setByY(dy);
        tt.setAutoReverse(true);
        tt.setCycleCount(Animation.INDEFINITE);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();

        return c;
    }

    public static void floatOrb(Circle orb, double dx, double dy, long ms) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(ms), orb);
        tt.setByX(dx); tt.setByY(dy);
        tt.setAutoReverse(true);
        tt.setCycleCount(Animation.INDEFINITE);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();
    }

    public static void animateSuccess(Button btn, String successMsg,
                                      long delayMs, Runnable onDone) {
        btn.setText(successMsg);
        btn.setDisable(true);
        btn.setStyle("-fx-background-color:" + D_SUCCESS + ";-fx-text-fill:white;"
                + "-fx-font-weight:bold;-fx-font-size:14px;-fx-background-radius:10;");
        PauseTransition pause = new PauseTransition(Duration.millis(delayMs));
        pause.setOnFinished(e -> onDone.run());
        pause.play();
    }

    // ════════════════════════════════════════════════════════════════════ //
    //  private helpers for button factories
    // ════════════════════════════════════════════════════════════════════ //

    private static Button lightBtn(String text, String bg, String fg) {
        Button btn = new Button(text);
        btn.setPrefHeight(Double.parseDouble(BTN_H));
        String base  = lightBtnStyle(bg, fg, "0.85");
        String hover = lightBtnStyle(darken(bg), fg, "1");
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited (e -> btn.setStyle(base));
        return btn;
    }

    private static String lightBtnStyle(String bg, String fg, String opacity) {
        return "-fx-background-color:" + bg + ";"
             + "-fx-text-fill:" + fg + ";"
             + "-fx-font-size:13px;-fx-font-weight:bold;"
             + "-fx-background-radius:" + RADIUS + ";"
             + "-fx-cursor:hand;-fx-opacity:" + opacity + ";";
    }

    private static String exportBtnStyle(String bg, String opacity) {
        return "-fx-background-color:" + bg + ";"
             + "-fx-text-fill:" + C_WHITE + ";"
             + "-fx-font-size:13px;-fx-font-weight:bold;"
             + "-fx-background-radius:" + RADIUS + ";"
             + "-fx-padding:0 16;-fx-cursor:hand;-fx-opacity:" + opacity + ";";
    }

    private static String bulletinBtnStyle(String bg, String opacity) {
        return "-fx-background-color:" + bg + ";"
             + "-fx-text-fill:" + C_WHITE + ";"
             + "-fx-font-size:11px;-fx-font-weight:bold;"
             + "-fx-background-radius:4;"
             + "-fx-padding:4 8;-fx-cursor:hand;-fx-opacity:" + opacity + ";";
    }

    private static String sidebarBtnStyle(String bg) {
        return "-fx-background-color:" + bg + ";"
             + "-fx-text-fill:" + C_WHITE + ";"
             + "-fx-font-size:13px;-fx-font-weight:bold;"
             + "-fx-alignment:CENTER_LEFT;-fx-padding:0 0 0 20;"
             + "-fx-cursor:hand;-fx-background-radius:0;";
    }

    private static String gradientBtnStyle(String c1, String c2, String opacity) {
        return "-fx-background-color:linear-gradient(to right," + c1 + "," + c2 + ");"
             + "-fx-text-fill:white;-fx-font-size:14px;-fx-font-weight:bold;"
             + "-fx-background-radius:10;-fx-cursor:hand;-fx-opacity:" + opacity + ";";
    }

    private static boolean isActive(Button btn) {
        return btn.getStyle().contains("border-width:0 0 0 4");
    }

    private static String darken(String hex) {
        try {
            String h = hex.replace("#", "");
            int r = (int)(Integer.parseInt(h.substring(0,2), 16) * 0.85);
            int g = (int)(Integer.parseInt(h.substring(2,4), 16) * 0.85);
            int b = (int)(Integer.parseInt(h.substring(4,6), 16) * 0.85);
            return String.format("#%02x%02x%02x", r, g, b);
        } catch (Exception e) { return hex; }
    }

    public static Region spacer(double height) {
        Region r = new Region();
        r.setPrefHeight(height);
        return r;
    }
}