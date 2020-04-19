package com.company.preferences;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javafx.scene.paint.Color;

public class PrefsData {
    private String fontName;
    private int fontSize;
    private String fontColor;
    private boolean isTab;
    private int numberOfSpaces;


    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String colorToString(Color fontColor) {
        System.out.println(fontColor.getBlue() * 256);
        System.out.println("here");

        String color = "rgb(%s, %s, %s)".formatted((int) (fontColor.getRed() * 255), (int) (fontColor.getGreen() * 255), (int) (fontColor.getBlue() * 255));
        System.out.println(color);
        return color;
    }


    public boolean isTab() {
        return isTab;
    }

    public void setTab(boolean tab) {
        isTab = tab;
    }

    public int getNumberOfSpaces() {
        return numberOfSpaces;
    }

    public void setNumberOfSpaces(int numberOfSpaces) {
        this.numberOfSpaces = numberOfSpaces;
    }

    @JsonIgnore
    public String getCSS() {
        return "-fx-text-fill: %s; -fx-font-family: %s; -fx-font-size: %d px;"
                .formatted(fontColor, fontName, fontSize);
    }
}
