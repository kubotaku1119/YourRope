package spajam2017.haggy.yourrope;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * バックグランド動作用サービス
 */
public class YourRopeService extends Service {

    public YourRopeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
