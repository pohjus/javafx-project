package com.company.util;

import javafx.application.Platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class FileHandler {

    private String filePath;

    private boolean isFileOpen = false;

    public boolean getIsFileOpen() {
        return isFileOpen;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void close() {
        filePath = null;
        isFileOpen = false;
    }

    public void open(CallbackOpen callback) {
        Thread t = new Thread(() -> {
            try {
                String content = Files.readString(Paths.get(this.getFilePath()));
                this.isFileOpen = true;
                Platform.runLater(() -> callback.received(content, Optional.empty()));
            } catch(IOException e) {
                Platform.runLater(() -> callback.received("", Optional.of(e.getMessage())));
            }
        });
        t.start();
    }

    public void save(String content, CallbackSave callback) {
        Thread t = new Thread(() -> {
            try {
                Files.writeString(Paths.get(this.getFilePath()), content);
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
