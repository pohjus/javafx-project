package com.company.preferences;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.awt.*;

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
        return "RGB(%s, %s, %s)".formatted(fontColor.getRed(), fontColor.getGreen(), fontColor.getBlue());
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
        return "-fx-font-family: %s; -fx-font-size: %d px; -fx-font-color: %s"
                .formatted(fontName, fontSize, fontColor);
    }
}
