package com.company.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JavaCompiler {
    public static void compile(String file) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("javac", file);
        Process process = processBuilder.start();

    }
    public static void run(String file) throws IOException {
        // java -cp /Users/pohjus/Desktop Main
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("java", "-cp", "/Users/pohjus/Desktop", "Main");
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuffer sb = new StringBuffer();
        String str = "";

        while((str = reader.readLine())!= null){
            sb.append(str);
        }
        System.out.println(sb.toString());

    }
}
