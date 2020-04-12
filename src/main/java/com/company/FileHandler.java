package com.company;

import javafx.application.Platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class FileHandler {
    public static void open(String filePath, CallbackOpen callback) {
        Thread t = new Thread(() -> {
            try {
                String content = Files.readString(Paths.get(filePath));
                Platform.runLater(() -> callback.received(content, Optional.empty()));
            } catch(IOException e) {
                Platform.runLater(() -> callback.received("", Optional.of(e.getMessage())));
            }
        });
        t.start();
    }

    public static void save(String content, String filePath, CallbackSave callback) {
        Thread t = new Thread(() -> {
            try {
                Files.writeString(Paths.get(filePath), content);
                Platform.runLater(() -> callback.done(Optional.empty()));
            } catch(IOException e) {
                Platform.runLater(() -> callback.done(Optional.of(e.getMessage())));
            }
        });
        t.start();
    }

    public interface CallbackOpen {
        public void received(String content, Optional<String> errorMsg);
    }
    public interface CallbackSave {
        public void done(Optional<String> errorMsg);
    }

}
