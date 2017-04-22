package spajam2017.haggy.yourrope;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import spajam2017.haggy.yourrope.R;
import spajam2017.haggy.yourrope.bluetooth.BleWrapper;
import spajam2017.haggy.yourrope.fragments.PairingFragment;
import spajam2017.haggy.yourrope.util.Prefs;

/**
 * 端末ペアリング画面
 */
public class PairingActivity extends AppCompatActivity
        implements PairingFragment.OnRopeSelectedListener {

    /**
     * リクエストコード：Bluetoothの有効化
     */
    private static final int REQUEST_ENABLE_BT = 100;

    /**
     * リクエストコード：位置情報許可
     */
    private static final int REQUEST_LOCATION_PERMISSIONS = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ActionBarにアイコン表示
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        setContentView(R.layout.activity_pairing);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Bluetoothが無効か確認
        if (!BleWrapper.isBluetoothEnable(this)) {
            requestEnableBT();
            return;
        }

        // AndroidM対応、位置情報が有効か確認（BLEのスキャンに位置情報パーミッションが必要なため）
        if (!checkPermissions()) {
            requestPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSIONS);
            return;
        }

        initViews();
    }

    private void initViews() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(PairingFragment.TAG) == null) {
            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.view_holder, PairingFragment.newInstance(), PairingFragment.TAG);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onRopeSelected(String name) {
        // 自分のデバイスが選択された
        Prefs.saveTargetName(this, name);

        Toast toast = Toast.makeText(this, "選択しました", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        // サービスを起動
        startService();

    }

    private void startService() {
        Intent intent = new Intent(this, YourRopeService.class);
        startService(intent);
    }

    // --------------------------------------------


    /**
     * 必要なパーミッションの確認を行う
     * <p>
     * AndroidM対応
     * </p>
     *
     * @return パーミッションが与えられている
     */
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    /**
     * 指定したパーミッションをユーザーにリクエストする.
     *
     * @param permissions パーミッションリスト
     * @param requestCode リクエエストコード
     */
    private void requestPermission(String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(
                this,
                permissions,
                requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            if (!checkPermissions()) {
                Toast.makeText(this, "位置情報のリクエストを許可してください", Toast.LENGTH_LONG).show();
                finish();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Bluetooth設定用画面を起動する.
     */
    private void requestEnableBT() {
        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(i, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // まだOFFになっているなら、再度呼び出し
                if (resultCode == Activity.RESULT_CANCELED) {
                    requestEnableBT();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
