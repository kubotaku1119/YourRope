package spajam2017.haggy.yourrope.train;

import android.location.Location;

/**
 * Train Info class
 */
public class TrainInfo {

    /**
     * ターゲット名
     */
    public String target_name;

    /**
     * 自分の名前
     */
    public String my_name;

    /**
     * 接続状態
     */
    public boolean status;

    /**
     * 位置情報
     */
    public TrainPosition position;

    public TrainInfo() {
        position = new TrainPosition();
    }

    /**
     * 位置情報の更新
     *
     * @param location 位置情報
     */
    public void updatePosition(Location location) {

        // 緯度
        final double latitude = location.getLatitude();

        // 経度
        final double longitude = location.getLongitude();

        double oldLatitude = position.latitude;
        double oldLongitude = position.longitude;

        // Vectorの計算

        position.latitude = latitude;
        position.longitude = longitude;
    }
}
