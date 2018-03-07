package timetracker.com.storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class for handling read/write actions
 * to SharedPreferences
 */
public class SharedPreferencesManager {

    public static final String PREF_LAT = "lat";
    public static final String PREF_LON = "lon";
    public static final String PREF_ADDRESS_SET = "address";
    public static final String PREF_GEOFENCE_ON = "PREF_GEOFENCE_ON";

    private static final String PREF_NAME = "time.tracker.prefs";

    private final SharedPreferences preferences;

    public SharedPreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }


    public void setValue(String key, String value) {
        preferences.edit()
                .putString(key, value)
                .commit();
    }

    public void setValue(String key, float value) {
        preferences.edit()
                .putFloat(key, value)
                .commit();
    }

    public void setValue(String key, boolean value) {
        preferences.edit()
                .putBoolean(key, value)
                .commit();
    }

    public String getValueString(String key) {
        return preferences.getString(key, "");
    }

    public float getValueFloat(String key) {
        return preferences.getFloat(key, 0);
    }

    public boolean getValueBoolean(String key) {
        return preferences.getBoolean(key, false);
    }


    public void remove(String key) {
        preferences.edit()
                .remove(key)
                .commit();
    }

    public boolean clear() {
        return preferences.edit()
                .clear()
                .commit();
    }
}