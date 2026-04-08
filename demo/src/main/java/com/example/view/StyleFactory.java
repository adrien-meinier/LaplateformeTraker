package com.example.view;


import javafx.scene.control.Button;
import javafx.scene.layout.Region;

/**
 * StyleFactory — fabrique de styles CSS JavaFX.
 *
 * Centralise toutes les constantes de couleur et les méthodes
 * qui créent des boutons stylisés, évitant toute duplication.
 */
public final class StyleFactory {

    // ── Palette ─
    public static final String C_PRIMARY    = "#2c3e50";   // bleu marine foncé
    public static final String C_ACCENT     = "#3498db";   // bleu vif
    public static final String C_SUCCESS    = "#27ae60";   // vert
    public static final String C_DANGER     = "#e74c3c";   // rouge
    public static final String C_WARNING    = "#f39c12";   // orange
    public static final String C_LIGHT      = "#ecf0f1";   // gris clair
    public static final String C_WHITE      = "#ffffff";
    public static final String C_TEXT_DARK  = "#2c3e50";
    public static final String C_TEXT_GREY  = "#7f8c8d";
    public static final String C_BG         = "#f4f6f8";   // fond général

    // ── Radius / taille
    private static final String RADIUS = "8";
    private static final String BTN_H  = "42";             // hauteur bouton px

    private StyleFactory() {}

    // ── Styles de fond 

    public static String rootBg() {
        return "-fx-background-color: " + C_BG + ";";
    }

    public static String cardBg() {
        return "-fx-background-color: " + C_WHITE + ";"
             + "-fx-background-radius: " + RADIUS + ";"
             + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);";
    }

    public static String sidebarBg() {
        return "-fx-background-color: " + C_PRIMARY + ";";
    }

    // ── Boutons ─

    /** Bouton plein couleur accent (bleu). */
    public static Button primaryBtn(String text) {
        return styledBtn(text, C_ACCENT, C_WHITE);
    }

    /** Bouton vert (succès / ajout). */
    public static Button successBtn(String text) {
        return styledBtn(text, C_SUCCESS, C_WHITE);
    }

    /** Bouton rouge (danger / suppression). */
    public static Button dangerBtn(String text) {
        return styledBtn(text, C_DANGER, C_WHITE);
    }

    /** Bouton orange (avertissement / export). */
    public static Button warningBtn(String text) {
        return styledBtn(text, C_WARNING, C_WHITE);
    }

    /** Bouton gris clair (secondaire). */
    public static Button secondaryBtn(String text) {
        return styledBtn(text, C_LIGHT, C_TEXT_DARK);
    }

    /** Bouton sidebar (fond transparent, texte blanc). */
    public static Button sidebarBtn(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(Double.parseDouble(BTN_H));
        btn.setStyle(
            "-fx-background-color: transparent;"
          + "-fx-text-fill: " + C_WHITE + ";"
          + "-fx-font-size: 13px;"
          + "-fx-font-weight: bold;"
          + "-fx-alignment: CENTER_LEFT;"
          + "-fx-padding: 0 0 0 20;"
          + "-fx-cursor: hand;"
          + "-fx-background-radius: 0;"
        );
        // Hover effect
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.12);"
          + "-fx-text-fill: " + C_WHITE + ";"
          + "-fx-font-size: 13px;"
          + "-fx-font-weight: bold;"
          + "-fx-alignment: CENTER_LEFT;"
          + "-fx-padding: 0 0 0 20;"
          + "-fx-cursor: hand;"
          + "-fx-background-radius: 0;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent;"
          + "-fx-text-fill: " + C_WHITE + ";"
          + "-fx-font-size: 13px;"
          + "-fx-font-weight: bold;"
          + "-fx-alignment: CENTER_LEFT;"
          + "-fx-padding: 0 0 0 20;"
          + "-fx-cursor: hand;"
          + "-fx-background-radius: 0;"
        ));
        return btn;
    }

    // ── Champs de saisie 

    public static String textFieldStyle() {
        return "-fx-background-color: " + C_WHITE + ";"
             + "-fx-border-color: #dce1e7;"
             + "-fx-border-radius: 6;"
             + "-fx-background-radius: 6;"
             + "-fx-padding: 8 12;"
             + "-fx-font-size: 13px;";
    }

    public static String textFieldFocusStyle() {
        return "-fx-background-color: " + C_WHITE + ";"
             + "-fx-border-color: " + C_ACCENT + ";"
             + "-fx-border-radius: 6;"
             + "-fx-background-radius: 6;"
             + "-fx-padding: 8 12;"
             + "-fx-font-size: 13px;";
    }

    // ── Labels 

    public static String titleStyle() {
        return "-fx-font-size: 22px;"
             + "-fx-font-weight: bold;"
             + "-fx-text-fill: " + C_TEXT_DARK + ";";
    }

    public static String subtitleStyle() {
        return "-fx-font-size: 13px;"
             + "-fx-text-fill: " + C_TEXT_GREY + ";";
    }

    public static String labelStyle() {
        return "-fx-font-size: 12px;"
             + "-fx-font-weight: bold;"
             + "-fx-text-fill: " + C_TEXT_DARK + ";";
    }

    // ── TableView ─

    public static String tableStyle() {
        return "-fx-background-color: " + C_WHITE + ";"
             + "-fx-border-color: #dce1e7;"
             + "-fx-border-radius: 8;"
             + "-fx-background-radius: 8;";
    }

    // ── Helpers privés 

    private static Button styledBtn(String text, String bg, String fg) {
        Button btn = new Button(text);
        btn.setPrefHeight(Double.parseDouble(BTN_H));
        String base = buildBtnStyle(bg, fg, "0.85");
        String hover = buildBtnStyle(darken(bg), fg, "1");
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    private static String buildBtnStyle(String bg, String fg, String opacity) {
        return "-fx-background-color: " + bg + ";"
             + "-fx-text-fill: " + fg + ";"
             + "-fx-font-size: 13px;"
             + "-fx-font-weight: bold;"
             + "-fx-background-radius: " + RADIUS + ";"
             + "-fx-cursor: hand;"
             + "-fx-opacity: " + opacity + ";";
    }

    /** Assombrit légèrement une couleur hex (#rrggbb) de 15 %. */
    private static String darken(String hex) {
        try {
            hex = hex.replace("#", "");
            int r = (int)(Integer.parseInt(hex.substring(0,2),16) * 0.85);
            int g = (int)(Integer.parseInt(hex.substring(2,4),16) * 0.85);
            int b = (int)(Integer.parseInt(hex.substring(4,6),16) * 0.85);
            return String.format("#%02x%02x%02x", r, g, b);
        } catch (Exception e) {
            return "#" + hex;
        }
    }
}