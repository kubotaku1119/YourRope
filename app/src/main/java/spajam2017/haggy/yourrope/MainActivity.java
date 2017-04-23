package spajam2017.haggy.yourrope;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import java.io.IOException;

import spajam2017.haggy.yourrope.api.TiamatAccessor;
import spajam2017.haggy.yourrope.train.TrainInfo;

/**
 * メイン画面
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPOSTTest = (Button) findViewById(R.id.btn_post_test);
        btnPOSTTest.setOnClickListener(v -> {

            (new Thread(() -> {
                TiamatAccessor accessor = new TiamatAccessor();

                TrainInfo info = new TrainInfo();
                info.status = false;
                info.my_name = "11:11:11:11:11:11";
                info.your_name = "22:22:22:22:22:22";
                info.position.longitude = 138.123456;
                info.position.latitude = 38.12313;
                info.position.vector = 123;

                try {
                    accessor.updateTrainStatus(info);

                    TrainInfo newStatus = accessor.getTrainStatus(info.my_name);

                    int count = accessor.getCurrentConnectedTrainCount(info.my_name);

                    boolean completedConnection = accessor.isCompletedConnection();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            })).start();
        });

    }
}
