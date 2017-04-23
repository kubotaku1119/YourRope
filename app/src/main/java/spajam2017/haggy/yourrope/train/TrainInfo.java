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
        position.vector = geoDirection(oldLatitude, oldLongitude, position.latitude, position.longitude);

        position.latitude = latitude;
        position.longitude = longitude;
    }

    /**
     * 緯度経度 lat1, lng1 の点を出発として、緯度経度 lat2, lng2 への方位
     * 北を０度で右回りの角度０～３６０度
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */

     public double geoDirection(double lat1, double lng1, double lat2, double lng2) {
        double Y = Math.cos(lng2 * Math.PI / 180) * Math.sin(lat2 * Math.PI / 180 - lat1 * Math.PI / 180);
        double X = Math.cos(lng1 * Math.PI / 180) * Math.sin(lng2 * Math.PI / 180) - Math.sin(lng1 * Math.PI / 180) * Math.cos(lng2 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180 - lat1 * Math.PI / 180);
        double dirE0 = 180 * Math.atan2(Y, X) / Math.PI; // 東向きが０度の方向
        if (dirE0 < 0) {
            dirE0 = dirE0 + 360; //0～360 にする。
        }

        return (dirE0 + 90) % 360; //(dirE0+90)÷360の余りを出力 北向きが０度の方向
    }
}
