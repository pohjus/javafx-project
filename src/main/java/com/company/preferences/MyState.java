package com.company.preferences;

import java.io.File;

public class MyState {
    private File file;
    private String terminal;

    private static MyState state = null;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    private MyState() {}

    public static MyState getInstance() {
        if (state == null) {
            state = new MyState();
        }
        return state;
    }

}
