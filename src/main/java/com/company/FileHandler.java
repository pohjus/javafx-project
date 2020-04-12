package com.company;

import javafx.application.Platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler {
    public static void open(String filePath, Callback callback) {
        Thread t = new Thread(() -> {
            try {
                String content = Files.readString(Paths.get(filePath));

                Platform.runLater(() -> callback.received(content));
            } catch(IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

    public interface Callback {
        public void received(String content);
    }
}
