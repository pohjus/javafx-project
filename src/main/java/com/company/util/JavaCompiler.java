package com.company.util;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

public class JavaCompiler {

    private String file;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    private static JavaCompiler javaCompiler;

    private JavaCompiler() {}


    public static JavaCompiler getInstance() {
        if(javaCompiler == null) {
            javaCompiler = new JavaCompiler();
        }
        return javaCompiler;
    }

    public void compile(Callback cb) {
        Thread t = new Thread(() -> {
            String output = "";
            try {
                Path p = Paths.get(file);
                String name = p.getFileName().toString();
                String path = p.getParent().toString();

                System.out.println(name);
                System.out.println(path);

                String [] compile = {"javac", path + "/" + name};

                // Compile
                var compileProcess = Runtime.getRuntime().exec(compile);
                if( compileProcess.getErrorStream().read() != -1 ) {
                    System.out.println("error");
                    new SoundPlayer().playSound("error.wav");
                    output = getOutput(compileProcess.getErrorStream());
                    System.out.println(output);
                }

                compileProcess.waitFor();

                System.out.println("output = " + output);

                final String finalOutput = output;
                Platform.runLater(() -> cb.received(finalOutput, Optional.empty()));
            } catch(Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> cb.received("", Optional.of(e.toString())));
            }
        });
        t.start();
    }

    public void run(Callback cb) {
        Thread t = new Thread(() -> {
            String output = "";

            try {
                String file = FileHandler.getInstance().getFilePath();
                Path p = Paths.get(file);
                String name = p.getFileName().toString();
                String path = p.getParent().toString();
                name = name.substring(0, name.length() - 5);

                String [] compile = {"java", "-cp", path, name};

                // Run
                var runProcess = Runtime.getRuntime().exec(compile);
                if( runProcess.getErrorStream().read() != -1 ){
                    output = getOutput(runProcess.getErrorStream());
                } else {
                    new SoundPlayer().playSound("success.wav");

                    output = getOutput(runProcess.getInputStream());
                }

                runProcess.waitFor();

                final String finalOutput = output;
                Platform.runLater(() -> {
                    cb.received(finalOutput, Optional.empty());
                });
            } catch(Exception e) {
                Platform.runLater(() -> {
                    cb.received("", Optional.of(e.toString()));
                });
            }
        });
        t.start();
    }

    public String getOutput(InputStream stream) throws IOException {
        String result = "";
        try(BufferedReader bufferedReader
                    = new BufferedReader(new InputStreamReader(stream))) {
            result = bufferedReader.lines().collect(Collectors.joining("\n"));
        }
        return result;
    }


    public interface Callback {
        public void received(String content, Optional<String> errorMsg);
    }
}
