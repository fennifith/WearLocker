package james.wearlocker;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WearLocker extends Application {

    public static final String PREF_ENABLED = "enabled";
    public static final String PREF_VIBRATE = "vibrate";
    public static final String PREF_COLOR = "color";
    public static final String PREF_GESTURE = "gesture";

    public static final int GESTURE_DOUBLE_TAP = 0;
    public static final int GESTURE_HOLD = 1;
    public static final int GESTURE_SWIPE_UP = 2;
    public static final int GESTURE_SWIPE_DOWN = 3;
    public static final int GESTURE_SWIPE_LEFT = 4;
    public static final int GESTURE_SWIPE_RIGHT = 5;

    private SharedPreferences prefs;
    private List<OnPreferenceChangedListener> listeners;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        listeners = new ArrayList<>();
    }

    public boolean isEnabled() {
        return prefs.getBoolean(PREF_ENABLED, false);
    }

    public void setEnabled(boolean enabled) {
        prefs.edit().putBoolean(PREF_ENABLED, enabled).apply();
        onPreferenceChanged(PREF_ENABLED);
    }

    public boolean isVibrate() {
        return prefs.getBoolean(PREF_VIBRATE, true);
    }

    public void setVibrate(boolean vibrate) {
        prefs.edit().putBoolean(PREF_VIBRATE, vibrate).apply();
    }

    @ColorInt
    public int getColor() {
        return prefs.getInt(PREF_COLOR, Color.BLACK);
    }

    public void setColor(@ColorInt int color) {
        prefs.edit().putInt(PREF_COLOR, color).apply();
        onPreferenceChanged(PREF_COLOR);
    }

    public int getGesture() {
        return prefs.getInt(PREF_GESTURE, GESTURE_DOUBLE_TAP);
    }

    public List<String> getGestureTitles() {
        return new ArrayList<>(Arrays.asList(
                getString(R.string.gesture_double_tap),
                getString(R.string.gesture_hold),
                getString(R.string.gesture_swipe_up),
                getString(R.string.gesture_swipe_down),
                getString(R.string.gesture_swipe_left),
                getString(R.string.gesture_swipe_right)
        ));
    }

    public String getGestureTitle() {
        return getGestureTitles().get(getGesture());
    }

    public void setGesture(int gesture) {
        prefs.edit().putInt(PREF_GESTURE, gesture).apply();
        onPreferenceChanged(PREF_GESTURE);
    }

    public void addListener(OnPreferenceChangedListener listener) {
        listeners.add(listener);
    }

    public void removeListener(OnPreferenceChangedListener listener) {
        listeners.remove(listener);
    }

    private void onPreferenceChanged(String name) {
        for (OnPreferenceChangedListener listener : listeners) {
            listener.onPreferenceChanged(name);
        }
    }

    public interface OnPreferenceChangedListener {
        void onPreferenceChanged(String name);
    }
}
