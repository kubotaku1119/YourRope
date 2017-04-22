package spajam2017.haggy.yourrope;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import spajam2017.haggy.yourrope.util.Prefs;

/**
 * 起動画面
 * <p>
 * デバッグのための画面遷移処理含む
 * </p>
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                // TODO:テスト
                Prefs.setFinishedHelloActivity(SplashActivity.this, false);
                Prefs.saveTargetName(SplashActivity.this, null);

                Class clazz;
                if (Prefs.isFinishedHelloActivity(SplashActivity.this)) {
                    if (Prefs.existTargetName(SplashActivity.this)) {
                        // メイン画面へ
                        clazz = MainActivity.class;
                    } else {
                        // 接続対象検索へ
                        clazz = PairingActivity.class;
                    }
                } else {
                    // 使い方表示へ
                    clazz = HelloActivity.class;
                }

                // TODO:テスト
                //clazz = MainActivity.class;

                final Intent intent = new Intent(SplashActivity.this, clazz);
                startActivity(intent);
                finish();
            }
        }, 2000);

    }
}
