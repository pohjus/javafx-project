package com.company.util;

import javafx.scene.media.*;

import java.io.File;


public class SoundPlayer {
    public void playSound(String file) {

        String mediaURI = getClass().getResource("/" + file).toExternalForm();
        System.out.println(mediaURI);
        AudioClip plonkSound = new AudioClip(mediaURI);
        plonkSound.play();
    }
}
