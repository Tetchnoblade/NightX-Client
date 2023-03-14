package net.aspw.client.utils.misc.sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

public class TipSoundPlayer {
    private final File file;

    public TipSoundPlayer(File file) {
        this.file = file;
    }

    public void asyncPlay(float volume) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                playSound(volume / 100F);
            }
        };
        thread.start();
    }

    public void playSound(float volume) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            FloatControl controller = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = controller.getMaximum() - controller.getMinimum();
            float value = (range * volume) + controller.getMinimum();

            controller.setValue(value);

            clip.start();
        } catch (Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }
}
