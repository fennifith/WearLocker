package james.wearlocker;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class WearLocker extends Application {

    private static final String PREF_ENABLED = "enabled";

    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public boolean isEnabled() {
        return prefs.getBoolean(PREF_ENABLED, false);
    }

    public void setEnabled(boolean enabled) {
        prefs.edit().putBoolean(PREF_ENABLED, enabled).apply();
    }
}
