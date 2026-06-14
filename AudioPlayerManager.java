package com.example;

import javax.sound.sampled.*;
import java.io.File;
import java.net.URL;

public class AudioPlayerManager {
    private static AudioPlayerManager instance;
    private Clip currentClip;
    private FloatControl gainControl;
    private int[] equalizerBands = new int[10];

    private AudioPlayerManager() {}

    public static AudioPlayerManager getInstance() {
        if (instance == null) {
            instance = new AudioPlayerManager();
        }
        return instance;
    }

    public void playTrack(String sourcePath) {
        new Thread(() -> {
            try {
                stopTrack();
                AudioInputStream audioStream;
                if (sourcePath.startsWith("http")) {
                    audioStream = AudioSystem.getAudioInputStream(new URL(sourcePath));
                } else {
                    audioStream = AudioSystem.getAudioInputStream(new File(sourcePath));
                }
                currentClip = AudioSystem.getClip();
                currentClip.open(audioStream);
                if (currentClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    gainControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
                }
                currentClip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stopTrack() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            currentClip.close();
        }
    }

    public void setVolume(float volume) {
        if (gainControl != null) {
            float dB = (float) (Math.log(volume <= 0.0 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }

    public void setEqualizerBand(int band, int value) {
        if (band >= 0 && band < 10) {
            equalizerBands[band] = value;
        }
    }

    public int getEqualizerBand(int band) {
        return (band >= 0 && band < 10) ? equalizerBands[band] : 0;
    }
}
