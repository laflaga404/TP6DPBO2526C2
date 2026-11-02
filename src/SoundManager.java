import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class SoundManager {

    // audio clips
    private Clip bgmClip;
    private Clip flapClip;
    private Clip scoreClip;
    private Clip gameOverClip;

    // constructor
    public SoundManager() {
        bgmClip = loadClip("/assets/audio/bgm.wav");
        flapClip = loadClip("/assets/audio/flap.wav");
        scoreClip = loadClip("/assets/audio/score.wav");
        gameOverClip = loadClip("/assets/audio/gameover.wav");
    }

    // memuat audio
    private Clip loadClip(String path) {
        URL resource = getClass().getResource(path);

        if (resource == null) {
            System.err.println("Audio tidak ditemukan: " + path);
            return null;
        }

        try (AudioInputStream audioStream =
                     AudioSystem.getAudioInputStream(resource)) {

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;

        } catch (
                UnsupportedAudioFileException |
                IOException |
                LineUnavailableException e
        ) {
            System.err.println("Gagal memuat audio: " + path);
            e.printStackTrace();
            return null;
        }
    }

    // mengatur volume audio
    private void setVolume(Clip clip, float volume) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gain = (FloatControl) clip.getControl(
                    FloatControl.Type.MASTER_GAIN
            );

            float min = gain.getMinimum();
            float max = gain.getMaximum();

            gain.setValue(Math.max(min, Math.min(max, volume)));
        }
    }

    // memutar background music
    public void playBgm() {
        if (bgmClip != null && !bgmClip.isRunning()) {
            setVolume(bgmClip, -15.0f);
            bgmClip.setFramePosition(0);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // menghentikan background music
    public void stopBgm() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.setFramePosition(0);
        }
    }

    // memutar sound effect
    private void playEffect(Clip clip) {
        if (clip == null) {
            return;
        }

        if (clip.isRunning()) {
            clip.stop();
        }

        clip.setFramePosition(0);
        clip.start();
    }

    // suara ketika player terbang
    public void playFlap() {
        playEffect(flapClip);
    }

    // suara ketika skor bertambah
    public void playScore() {
        playEffect(scoreClip);
    }

    // suara ketika game over
    public void playGameOver() {
        playEffect(gameOverClip);
    }

    // membersihkan audio
    public void close() {
        Clip[] clips = {
                bgmClip,
                flapClip,
                scoreClip,
                gameOverClip
        };

        for (Clip clip : clips) {
            if (clip != null) {
                clip.stop();
                clip.close();
            }
        }
    }
}
