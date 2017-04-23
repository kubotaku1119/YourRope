package spajam2017.haggy.yourrope.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences Wrapper
 */

public class Prefs {

    private static final String PREFS_NAME = "your_name_prefs";

    private static final String KEY_IS_FINISHED_HELLO = "is_finished_hello";

    private static final String KEY_TARGET_NAME = "target_mac_name";


    /**
     * 説明画面を表示したか
     *
     * @param context Context.
     * @return true : 表示済み
     */
    public static boolean isFinishedHelloActivity(final Context context) {
        final SharedPreferences prefs = getPrefs(PREFS_NAME, context);
        return prefs.getBoolean(KEY_IS_FINISHED_HELLO, false);
    }

    /**
     * 説明画面の表示済み状態を保存する
     *
     * @param context    Context.
     * @param isFinished true : 表示済み
     */
    public static void setFinishedHelloActivity(final Context context, boolean isFinished) {
        final SharedPreferences prefs = getPrefs(PREFS_NAME, context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_FINISHED_HELLO, isFinished);
        editor.apply();
    }

    /**
     * ターゲットの名前が保存済みか
     *
     * @param context Context.
     * @return true : 保存済み
     */
    public static boolean existTargetName(final Context context) {
        final SharedPreferences prefs = getPrefs(PREFS_NAME, context);
        final String targetName = prefs.getString(KEY_TARGET_NAME, null);
        if (targetName == null) {
            return false;
        }
        return true;
    }

    /**
     * ターゲットの名前を保存する
     *
     * @param context Context.
     * @param name    名前（Macアドレス）
     */
    public static void saveTargetName(final Context context, final String name) {
        final SharedPreferences prefs = getPrefs(PREFS_NAME, context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TARGET_NAME, name);
        editor.apply();
    }

    private static SharedPreferences getPrefs(final String name, final Context context) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }
}
