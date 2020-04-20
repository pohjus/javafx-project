package com.company.preferences;


import com.company.preferences.PrefsData;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class PreferencesHandler {
    private PrefsData preferencesData;
    private String PATH = "./preferences.json";

    public void restorePreferences() {
        try {
            // Read saved preferences
            String preferencesFile = Files.readString(Paths.get(PATH));
            ObjectMapper objectMapper = new ObjectMapper();
            preferencesData = objectMapper.readValue(preferencesFile, PrefsData.class);
        } catch(IOException e) {
            // if something goes wrong, create default preference file
            e.printStackTrace();

            preferencesData = new PrefsData();
            preferencesData.setFontName("Monaco");
            preferencesData.setFontColor(preferencesData.colorToString(Color.BLUE));
            preferencesData.setFontSize(12);
            preferencesData.setTab(false);
            preferencesData.setNumberOfSpaces(4);

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                objectMapper.writeValue(new File(PATH), preferencesData);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void savePreferences() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(PATH), preferencesData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PrefsData getPreferencesData() {
        return preferencesData;
    }

}
