package spajam2017.haggy.yourrope;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

import spajam2017.haggy.yourrope.api.TiamatAccessor;
import spajam2017.haggy.yourrope.bluetooth.BleWrapper;
import spajam2017.haggy.yourrope.bluetooth.MyGattAttribute;
import spajam2017.haggy.yourrope.train.TrainInfo;
import spajam2017.haggy.yourrope.util.Prefs;
import spajam2017.haggy.yourrope.voice.VoiceSpeaker;

/**
 * バックグランド動作用サービス
 */
public class YourRopeService extends Service
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, BleWrapper.IBleScannerListener, BleWrapper.IBleGattListener {

    private static final String TAG = YourRopeService.class.getSimpleName();

    private GoogleApiClient googleAPIClient;

    private LocationRequest locationRequest;

    private Location lastLocation;

    private TrainInfo trainInfo;

    private BleWrapper bleWrapper;

    private Handler handler;

    private Timer trainStatusUpdateTimer;

    private int connectedCount = 0;

    private boolean completedConnection = false;

    public YourRopeService() {
        trainInfo = new TrainInfo();

        try {
            bleWrapper = BleWrapper.getsInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        handler = new Handler(Looper.getMainLooper());

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

        trainStatusUpdateTimer = new Timer(true);
        trainStatusUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // get status from server
                Log.d(TAG, "--- get status from server ---");

                try {
                    // 接続先更新
                    TiamatAccessor tiamatAccessor = new TiamatAccessor();
                    synchronized (trainInfo) {
                        trainInfo = tiamatAccessor.getTrainStatus(trainInfo.my_name);
                    }

                    // 接続台数更新
                    int newCount = tiamatAccessor.getCurrentConnectedTrainCount(trainInfo.my_name);
                    if ((newCount != connectedCount) && (newCount > connectedCount)) {

                        // 接続数の更新を話す
                        VoiceSpeaker speaker = new VoiceSpeaker(YourRopeService.this);
                        speaker.speakConnectedCount(newCount);
                        connectedCount = newCount;
                    }

                    // 全台数接続成功確認
                    boolean newCompleteStatus = tiamatAccessor.isCompletedConnection();
                    if (newCompleteStatus != completedConnection) {
                        if (newCompleteStatus) {
                            // 全数接続成功!!
                            VoiceSpeaker speaker = new VoiceSpeaker(YourRopeService.this);
                            speaker.speakConnectionCompleted();
                        }
                        completedConnection = newCompleteStatus;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 1000, 1000);

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        if (googleAPIClient != null) {
            googleAPIClient.disconnect();
            googleAPIClient = null;
        }

        if (trainStatusUpdateTimer != null) {
            trainStatusUpdateTimer.cancel();
            trainStatusUpdateTimer = null;
        }

        if (bleWrapper != null) {
            bleWrapper.stopScan();
            bleWrapper.disconnect();
            bleWrapper.terminate();
            bleWrapper = null;
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
        try {
            tiamatAccessor.updateTrainStatus(trainInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 電車の接続情報を更新
     *
     * @param connectionState true : 接続中
     */
    private void updateTrainInfo(boolean connectionState) {
        // 接続状態の更新
        trainInfo.status = connectionState;

        // サーバーに通知
        final TiamatAccessor tiamatAccessor = new TiamatAccessor();
        try {
            tiamatAccessor.updateTrainStatus(trainInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 位置情報の定期更新を開始
     */
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

    // -----------------------------------
    // for BLE Wrapper

    private static final int CONNECTABLE_RSSI = -40;

    private boolean connected = false;


    @Override
    public void onScanResult(BluetoothDevice device, int rssi, byte[] data) {
        if (device == null) {
            return;
        }

        String name = device.getName();
        Log.d(TAG, "device name : " + name);
        if (!name.equals(MyGattAttribute.TARGET_ROPE_DEVICE_NAME)) {
            Log.d(TAG, "unknown device");
            return;
        }

        final String yourName = trainInfo.your_name;

        String address = device.getAddress();
        Log.d(TAG, "device address : " + address);
        if (!address.equals(yourName)) {
            Log.d(TAG, "Not your name...");
            return;
        }

        Log.d(TAG, "rssi : " + rssi);
        if (rssi <= CONNECTABLE_RSSI) {
            // 接続できるよ！！
            Log.d(TAG, "OK! Connectable!!");
            tryConnection(device);
        } else {
            // RSSIが想定未満なので切断とみなす
            Log.d(TAG, "Ops! Too far...");

            if (connected) {
                updateTrainInfo(false);
                connected = false;
            }
        }
    }

    private void tryConnection(BluetoothDevice target) {
        if (bleWrapper != null) {
            bleWrapper.connect(target, this);
        }
    }

    private void notifyConnection() {
        // サーバーに連結を通知
        updateTrainInfo(true);

        // 音


        // 音声
        VoiceSpeaker speaker = new VoiceSpeaker(this);
        speaker.speakConnectionSuccess();
    }

    @Override
    public void onConnected(BluetoothDevice device) {
        Log.d(TAG, "Connected!");

        connected = true;

        notifyConnection();

        // 切断してRSSIを監視し続ける
        handler.postDelayed(() -> {
            if (bleWrapper != null) {
                bleWrapper.disconnect();
            }
        }, 100);
    }

    @Override
    public void onDisconnected(BluetoothDevice device) {
        Log.d(TAG, "Disconnected");
        if (bleWrapper != null) {
            bleWrapper.stopScan();
            bleWrapper.startScan(this);
        }
    }

    @Override
    public void onServiceDiscovered(BluetoothDevice device, List<BluetoothGattService> supportedGattServices) {
    }

    @Override
    public void onReadCharacteristic(BluetoothDevice device, BluetoothGattCharacteristic characteristic) {
    }

    @Override
    public void onWriteCharacteristic(BluetoothDevice device) {
    }
}
