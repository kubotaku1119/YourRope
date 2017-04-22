package spajam2017.haggy.yourrope.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.List;
import java.util.UUID;

/**
 * BLE Wrapper class.
 */

public class BleWrapper {

    private static final String TAG = BleWrapper.class.getSimpleName();

    /**
     * BLEデバイススキャン結果リスナー.
     */
    public interface IBleScannerListener {
        /**
         * BLEデバイスを発見した.
         *
         * @param device デバイス.
         * @param rssi   RSSI値.
         * @param data   ScanResultデータ.
         */
        void onScanResult(BluetoothDevice device, int rssi, byte[] data);
    }

    public interface IBleGattListener {
        void onConnected(BluetoothDevice device);

        void onDisconnected(BluetoothDevice device);

        void onServiceDiscovered(BluetoothDevice device, List<BluetoothGattService> supportedGattServices);

        void onReadCharacteristic(BluetoothDevice device, BluetoothGattCharacteristic characteristic);

        void onWriteCharacteristic(BluetoothDevice device);
    }

    public static final int STATE_DISCONNECTED = 0;

    public static final int STATE_CONNECTING = 1;

    public static final int STATE_CONNECTED = 2;

    private IBleScannerListener scannerListener;

    private IBleGattListener gattListener;

    private Context context;

    private BluetoothManager bluetoothManager;

    private BluetoothAdapter bluetoothAdapter;

    private BluetoothLeScanner bluetoothLeScanner;

    private String bluetoothDeviceAddress;

    private BluetoothGatt bluetoothGatt;

    private int mConnectionState = STATE_DISCONNECTED;

    private static BleWrapper sInstance;

    public static BleWrapper getsInstance(Context context) throws Exception {
        if ((sInstance == null) && (context == null)) {
            throw new Exception("Must set context if need create instance.");
        }

        if (sInstance == null) {
            sInstance = new BleWrapper(context);
        }
        return sInstance;
    }

    private BleWrapper(Context context) {
        this.context = context;
    }

    /**
     * 初期化処理.
     *
     * @return true : 成功, false : 失敗
     */
    public boolean initialize() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return initializeOld();
        } else {
            return initializeNew();
        }
    }

    public void terminate() {
        if (context != null) {
            context = null;
        }
    }

    /**
     * 初期化処理.
     * <p/>
     * KitKat以前のバージョン向け
     * <p/>
     *
     * @return true : 成功, false : 失敗
     */
    private boolean initializeOld() {
        if (context == null) {
            return false;
        }

        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            return false;
        }

        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            return false;
        }

        return true;
    }

    /**
     * 初期化処理.
     * <p/>
     * Lollipop以降のバージョン向け
     * <p/>
     *
     * @return true : 成功, false : 失敗
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean initializeNew() {
        if (context == null) {
            return false;
        }

        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            return false;
        }

        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            return false;
        }

        // Lollipop以降はBluetoothScannerを利用する
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner == null) {
            return false;
        }

        return true;
    }

    /**
     * Bluetoothデバイスのスキャンを開始する.
     *
     * @param listener リスナー.
     */
    public void startScan(IBleScannerListener listener) {
        scannerListener = listener;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            startScanOld();
        } else {
            startScanNew();
        }
    }

    /**
     * Bluetoothデバイスのスキャンを開始する.
     * <p/>
     * KitKat以前のバージョン向け
     * <p/>
     */
    private void startScanOld() {
        bluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (scannerListener != null) {
                    scannerListener.onScanResult(device, rssi, scanRecord);
                }
            }
        });
    }

    /**
     * Bluetoothデバイスのスキャンを開始する.
     * <p/>
     * Lollipop以降のバージョン向け
     * <p/>
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startScanNew() {
        bluetoothLeScanner.startScan(new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                final BluetoothDevice device = result.getDevice();
                final int rssi = result.getRssi();
                byte[] data = null;
                ScanRecord record = result.getScanRecord();
                if (record != null) {
                    data = record.getBytes();
                }

                if (scannerListener != null) {
                    scannerListener.onScanResult(device, rssi, data);
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
            }

            @Override
            public void onScanFailed(int errorCode) {
            }
        });
    }

    /**
     * Bluetoothデバイスのスキャンを停止する.
     */
    public void stopScan() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            stopScanOld();
        } else {
            stopScanNew();
        }
    }

    /**
     * Bluetoothデバイスのスキャンを停止する.
     * <p/>
     * KitKat以前のバージョン向け
     * <p/>
     */
    private void stopScanOld() {
        bluetoothAdapter.stopLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (scannerListener != null) {
                    scannerListener.onScanResult(device, rssi, scanRecord);
                }
            }
        });
    }

    /**
     * Bluetoothデバイスのスキャンを停止する.
     * <p/>
     * Lollipop以降のバージョン向け
     * <p/>
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void stopScanNew() {
        bluetoothLeScanner.stopScan(new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                final BluetoothDevice device = result.getDevice();
                final int rssi = result.getRssi();
                byte[] data = null;
                ScanRecord record = result.getScanRecord();
                if (record != null) {
                    data = record.getBytes();
                }

                if (scannerListener != null) {
                    scannerListener.onScanResult(device, rssi, data);
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
            }

            @Override
            public void onScanFailed(int errorCode) {
            }
        });
    }

    // -----------------------------------------------------------------

    /**
     * デバイスと接続する.
     *
     * @param device   対象のBluetoothデバイス.
     * @param listener リスナー.
     * @return 成否
     */
    public boolean connect(BluetoothDevice device, IBleGattListener listener) {
        if ((device == null) || (listener == null)) {
            return false;
        }

        bluetoothGatt = device.connectGatt(context, true, mGattCallback);
        bluetoothDeviceAddress = device.getAddress();
        mConnectionState = STATE_CONNECTING;
        gattListener = listener;

        return true;
    }

    public void addGattListener(IBleGattListener listener) {
        gattListener = listener;
    }

    public void removeGattListener() {
        gattListener = null;
    }

    /**
     * Bluetoothデバイスとの接続を切断する.
     */
    public void disconnect() {
        gattListener = null;
        if ((bluetoothAdapter == null) || (bluetoothGatt == null)) {
            return;
        }
        bluetoothGatt.disconnect();
    }

    /**
     * Bluetoothの処理を終了し、メモリーを解放する.
     */
    public void close() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
    }

    /**
     * サポートしているGATTサービスを取得する.
     *
     * @return 成否.
     */
    public boolean discoverServices() {
        if (bluetoothGatt == null) {
            return false;
        }

        return bluetoothGatt.discoverServices();
    }

    /**
     * 対象のCharacteristic情報を読み取る.
     *
     * @param characteristic Characteristic.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * 対象のCharacteristicを書き込む
     *
     * @param characteristic Characteristic.
     * @param value          書き込むデータ
     */
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] value) {
        if (bluetoothGatt == null) {
            return;
        }
        characteristic.setValue(value);
        bluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * 対象のCharacteristicを書き込む
     *
     * @param characteristic Characteristic.
     * @param value          書き込むデータ.
     * @param format         データフォーマット
     * @param offset         データオフセット
     */
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic, int value, int format, int offset) {
        if (bluetoothGatt == null) {
            return;
        }
        characteristic.setValue(value, format, 0);
        bluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * 対象のCharacteristicからの通知受信設定を行う.
     *
     * @param characteristic Characteristic.
     * @param enabled        true:有効, false:無効.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                UUID.fromString(MyGattAttribute.CLIENT_CHARACTERISTIC_CONFIG)
        );

        if (descriptor != null) {
            descriptor.setValue(enabled ?
                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * GATTサーバーコールバック実装.
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionState = STATE_CONNECTED;
                if (gattListener != null) {
                    gattListener.onConnected(gatt.getDevice());
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
                if (gattListener != null) {
                    gattListener.onDisconnected(gatt.getDevice());
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // 対応しているGATTサービスのリストを取得
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> supportedServices = bluetoothGatt.getServices();
                if (gattListener != null) {
                    gattListener.onServiceDiscovered(gatt.getDevice(), supportedServices);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            // 読み込みが完了した
            // TODO:statusをチェックする
            if ((gattListener != null && (status == BluetoothGatt.GATT_SUCCESS))) {
                gattListener.onReadCharacteristic(gatt.getDevice(), characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (gattListener != null) {
                gattListener.onWriteCharacteristic(gatt.getDevice());
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // 変更通知を有効にした場合、変更があった際の通知
            if (gattListener != null) {
                Log.d(TAG, "onCharacteristicChanged()");
                gattListener.onReadCharacteristic(gatt.getDevice(), characteristic);
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    public static int getCarryStatusChangedEvent(BluetoothGattCharacteristic characteristic) {
        int flag = characteristic.getProperties();
        int format = -1;
        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
        }
        int event = characteristic.getIntValue(format, 1);
        return event;
    }

    /**
     * Bluetoothが有効になっているか.
     *
     * @param context コンテキスト.
     * @return true ; 有効, false: 無効
     */
    public static boolean isBluetoothEnable(Context context) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            return false;
        }

        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            return false;
        }

        return bluetoothAdapter.isEnabled();
    }

}
