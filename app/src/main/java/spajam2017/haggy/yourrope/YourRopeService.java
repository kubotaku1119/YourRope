package spajam2017.haggy.yourrope;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import spajam2017.haggy.yourrope.api.TiamatAccessor;
import spajam2017.haggy.yourrope.train.TrainInfo;

/**
 * バックグランド動作用サービス
 */
public class YourRopeService extends Service
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = YourRopeService.class.getSimpleName();

    private GoogleApiClient googleAPIClient;

    private LocationRequest locationRequest;

    private Location lastLocation;

    private TrainInfo trainInfo;

    public YourRopeService() {
        trainInfo = new TrainInfo();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(100);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (googleAPIClient == null) {
            googleAPIClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        googleAPIClient.connect();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (googleAPIClient != null) {
            googleAPIClient.disconnect();
            googleAPIClient = null;
        }

        super.onDestroy();
    }

    /**
     * 自分の電車位置情報を更新
     *
     * @param location 位置情報
     */
    private void updateTrainInfo(Location location) {

        // 位置情報の更新
        trainInfo.updatePosition(location);

        // サーバーに通知
        final TiamatAccessor tiamatAccessor = new TiamatAccessor();
        tiamatAccessor.updateTrainStatus(trainInfo);
    }

    private void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleAPIClient, locationRequest, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");

        if (location != null) {
            updateTrainInfo(location);
            lastLocation = location;
        }
    }

    // -------------------------

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.d(TAG, "onConnection");

        try {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleAPIClient);
            if (lastLocation != null) {
                updateTrainInfo(lastLocation);
            }

            // 位置情報の定期更新通知をリクエスト
            startLocationUpdates();

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

}
