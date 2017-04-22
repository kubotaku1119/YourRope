package spajam2017.haggy.yourrope.bluetooth;

import java.util.UUID;

/**
 * GATT Attribute defines.
 */

public class MyGattAttribute {

    public static final String MY_ROPE_DEVICE_NAME = "Your Rope";

    public static final String TARGET_ROPE_DEVICE_NAME = "Your Rope";

    public static final String MANUFACTURER_NAME = "00002a29-0000-1000-8000-00805f9b34fb";

    public static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public static final String ROPE_SERVICE = "19B10000-E8F2-537E-4F6C-D104768A1214";

    public static final String ROPE_CHARA = "19B10001-E8F2-537E-4F6C-D104768A1214";

    public static final UUID UUID_KASA_SERVICE = UUID.fromString(ROPE_SERVICE);

    public static final UUID UUID_KASA_CHARA = UUID.fromString(ROPE_CHARA);

    public static final String HEART_RATE_SERVICE = "0000180d-0000-1000-8000-00805f9b34fb";

    public static final String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";

    public static final UUID UUID_HEART_RATE_SERVICE = UUID.fromString(HEART_RATE_SERVICE);

    public static final UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(HEART_RATE_MEASUREMENT);

}
