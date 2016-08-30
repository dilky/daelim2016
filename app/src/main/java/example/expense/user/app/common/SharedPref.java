package example.expense.user.app.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dilky on 2016-08-25.
 */
public class SharedPref {

    private static void setString(Context context, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences("config", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static String getString(Context context, String key, String defaultVal) {
        SharedPreferences pref = context.getSharedPreferences("config", context.MODE_PRIVATE);
        return pref.getString(key, defaultVal);
    }


    public static void putUserId(Context context, String userId) {
        setString(context, "USER_ID", userId);
    }

    public static String getUserId(Context context) {
        return getString(context, "USER_ID", "");
    }

    public static void putPwd(Context context, String userId) {
        setString(context, "PWD", userId);
    }

    public static String getPwd(Context context) {
        return getString(context, "PWD", "");
    }

}
