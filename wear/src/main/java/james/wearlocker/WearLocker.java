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

    private static final String PREF_ENABLED = "enabled";
    private static final String PREF_COLOR = "color";
    private static final String PREF_GESTURE = "gesture";

    public static final int GESTURE_DOUBLE_TAP = 0;
    public static final int GESTURE_HOLD = 5;
    public static final int GESTURE_SWIPE_UP = 1;
    public static final int GESTURE_SWIPE_DOWN = 2;
    public static final int GESTURE_SWIPE_LEFT = 3;
    public static final int GESTURE_SWIPE_RIGHT = 4;

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

    @ColorInt
    public int getColor() {
        return prefs.getInt(PREF_COLOR, Color.BLACK);
    }

    public void setColor(@ColorInt int color) {
        prefs.edit().putInt(PREF_COLOR, color).apply();
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
    }
}
