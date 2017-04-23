package spajam2017.haggy.yourrope.api;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Wrapper of VoiceText api.
 * <p>
 * https://cloud.voicetext.jp/webapi/docs/api
 * </p>
 */
public class SpeechAPIWrapper {

    public static final int HAPPINESS = 0;

    public static final int SAD = 1;

    public static final int ANGER = 2;

    private static final String END_POINT = "https://api.voicetext.jp";

    private static final String VOICE_API = "/v1/tts";

    private static final String UTF8 = "UTF-8";

    private static final String TEXT = "text=";

    private OkHttpClient okHttpClient = new OkHttpClient();

    private String apiKey;

    public SpeechAPIWrapper(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * 指定テキストを合成音声で再生する
     * <p>
     * 非UIスレッドで呼び出すこと
     * </p>
     *
     * @param text    しゃべらせるテキスト
     * @param emotion {@link #HAPPINESS} : 喜, {@link #SAD} : 悲, {@link #ANGER} : 怒
     * @throws IOException
     */
    public void talk(final String text, final int emotion) throws IOException {

        final Request.Builder builder = new Request.Builder().url(END_POINT + VOICE_API);

        final String basic = Credentials.basic(apiKey, "");

        builder.addHeader("Authorization", basic);
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        builder.addHeader("User-Agent", "VoiceText4J");

        final FormBody.Builder bodyBuilder = new FormBody.Builder();
        bodyBuilder.addEncoded("speaker", "hikari");
        switch (emotion) {
            default:
            case HAPPINESS:
                bodyBuilder.addEncoded("emotion", "happiness");
                break;

            case SAD:
                bodyBuilder.addEncoded("emotion", "sadness");
                break;

            case ANGER:
                bodyBuilder.addEncoded("emotion", "anger");
                break;
        }
        bodyBuilder.addEncoded("emotion-level", "4");
        bodyBuilder.addEncoded("text", text);

        builder.post(bodyBuilder.build());

        final Request request = builder.build();
        final Response response = okHttpClient.newCall(request).execute();

        final byte[] byteData = response.body().bytes();
        final int responseCode = response.code();
        if (responseCode == 400) {
            String error = new String(byteData);
            Log.e("error", error);
        } else if (responseCode == 200) {
            // バッファサイズを取得
            int bufSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
            // AudioTrackインスタンスを生成
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufSize, AudioTrack.MODE_STREAM);
            // 再生
            audioTrack.play();
            audioTrack.write(byteData, 0, byteData.length);
        }
    }
}
