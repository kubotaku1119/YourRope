package spajam2017.haggy.yourrope.util;

import android.net.wifi.WifiManager;

import spajam2017.haggy.yourrope.R;

/**
 * Utility function implements class
 */

public class RopeUtils {

    /**
     * Wifiの信号強度のレベル別画像を取得する.
     * <p>
     * 0〜4の5段階
     * </p>
     *
     * @param rssi RSSI.
     * @return 信号強度画像ID
     */
    public static int getWifiLevelResourceId(int rssi) {
        final int level = WifiManager.calculateSignalLevel(rssi, 5);
        int resourceId = R.mipmap.ic_wifi_0;
        switch (level) {
            case 4:
                resourceId = R.mipmap.ic_wifi_4;
                break;
            case 3:
                resourceId = R.mipmap.ic_wifi_3;
                break;
            case 2:
                resourceId = R.mipmap.ic_wifi_2;
                break;
            case 1:
                resourceId = R.mipmap.ic_wifi_1;
                break;
            case 0:
                resourceId = R.mipmap.ic_wifi_0;
                break;
        }

        return resourceId;
    }

}
