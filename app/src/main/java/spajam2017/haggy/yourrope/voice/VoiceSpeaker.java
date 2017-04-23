package spajam2017.haggy.yourrope.voice;

import android.content.Context;

import java.io.IOException;

import spajam2017.haggy.yourrope.R;
import spajam2017.haggy.yourrope.api.SpeechAPIWrapper;
import spajam2017.haggy.yourrope.util.SoundPlayer;

/**
 * 人工音声で話すクラス
 */

public class VoiceSpeaker {

    private final SpeechAPIWrapper speechApi;

    private Context context;

    public VoiceSpeaker(final Context context) {
        this.context = context;
        speechApi = new SpeechAPIWrapper(context.getString(R.string.speech_api_key));
    }

    public void speakConnectedCount(final int count) {
        (new Thread(() -> {
            try {

                // sound
                SoundPlayer soundPlayer = new SoundPlayer();
                soundPlayer.playConnectionSound(context);

                speechApi.talk("" + count + "台目の車両が連結しました", SpeechAPIWrapper.HAPPINESS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).start();
    }

    public void speakConnectionSuccess() {
        (new Thread(() -> {
            try {

                // sound
                SoundPlayer soundPlayer = new SoundPlayer();
                soundPlayer.playConnectionSound(context);

                // speak
                speechApi.talk("連結に成功しました！", SpeechAPIWrapper.HAPPINESS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).start();
    }

    public void speakConnectionCompleted() {
        (new Thread(() -> {
            try {

                // sound
                SoundPlayer soundPlayer = new SoundPlayer();
                soundPlayer.playConnectionSound(context);

                // speak
                speechApi.talk("君の縄！", SpeechAPIWrapper.HAPPINESS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).start();
    }

    public void speakDisconnected() {
        (new Thread(() -> {
            try {
                speechApi.talk("連結が外れてしまいました...", SpeechAPIWrapper.SAD);
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).start();
    }

}
