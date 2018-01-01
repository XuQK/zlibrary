package xuqk.github.zlibrary.basekit.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * ClassName: SharedPref <br/>
 * PackageName: xuqk.github.zlibrary.basekit.cache <br/>
 * Create On: 12/24/17 4:26 PM <br/>
 * Site:https://github.XuQK <br/>
 * @author 徐乾琨 <br/>
 */

public class SharedPref implements ICache {

    private static SharedPreferences sharedPreferences;

    private static String SP_NAME = "config";

    private static SharedPref instance;

    public static void init(String spName) {
        if (TextUtils.isEmpty(spName)) {
            SP_NAME = spName;
        }
    }

    private SharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPref getInstance(Context context) {
        if (instance == null) {
            synchronized (SharedPref.class) {
                if (instance == null) {
                    instance = new SharedPref(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    @Override
    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }


    @Override
    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    @Override
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }


    public void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public void putLong(String key, Long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }


    public void putBoolean(String key, Boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }


    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public void put(String key, Object value) {

    }
}
