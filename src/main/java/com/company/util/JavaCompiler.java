package com.company.util;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

public class JavaCompiler {
    public static void compileAndRun(String file, Callback cb) {

        Thread t = new Thread(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("java", file);
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String str = "";
                sb.append("> javac " + file + "\n");
                while((str = reader.readLine()) != null){
                    sb.append(str);
                }

                Platform.runLater(() -> cb.received(sb.toString(), Optional.empty()));

            } catch(IOException e) {
                Platform.runLater(() -> cb.received("", Optional.of(e.getMessage())));
            }

        });
        t.start();
    }

    public interface Callback {
        public void received(String content, Optional<String> errorMsg);
    }
}
