package spajam2017.haggy.yourrope.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;

import spajam2017.haggy.yourrope.R;

/**
 * 効果音等を鳴らす
 */

public class SoundPlayer {

    public static int calcBeepVolume(int max, int min, int current) {
        int d = Math.abs(min) - Math.abs(max);
        double dt = 100.0 / d;

        int volume = (int) (dt * (Math.abs(current) - Math.abs(max)));
        return volume;
    }

    public void beep(int volume) {
        int streamType = AudioManager.STREAM_MUSIC;
        ToneGenerator toneGenerator = new ToneGenerator(streamType, volume);
        int toneType = ToneGenerator.TONE_DTMF_0;
        int durationMs = 200;
        toneGenerator.startTone(toneType, durationMs);
    }

    public void playConnectionSound(final Context context) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.se_maoudamashii_onepoint15);
        mediaPlayer.start();
    }

    // ---------------------------

    private MediaPlayer completeSoundPlayer;

    public void playCompleteSound(final Context context) {
        if (completeSoundPlayer != null) {
            completeSoundPlayer = MediaPlayer.create(context, R.raw.se_maoudamashii_onepoint16);
            completeSoundPlayer.setLooping(true);
            completeSoundPlayer.start();
        }
    }

    public void stopCompleteSound() {
        if (completeSoundPlayer != null) {
            completeSoundPlayer.stop();
            completeSoundPlayer.release();
            completeSoundPlayer = null;
        }
    }
}
