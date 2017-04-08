package james.wearlocker;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;

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

    public String getGestureTitle() {
        switch (getGesture()) {
            case GESTURE_DOUBLE_TAP:
                return getString(R.string.gesture_double_tap);
            case GESTURE_HOLD:
                return getString(R.string.gesture_hold);
            case GESTURE_SWIPE_UP:
                return getString(R.string.gesture_swipe_up);
            case GESTURE_SWIPE_DOWN:
                return getString(R.string.gesture_swipe_down);
            case GESTURE_SWIPE_LEFT:
                return getString(R.string.gesture_swipe_left);
            case GESTURE_SWIPE_RIGHT:
                return getString(R.string.gesture_swipe_right);
            default:
                return "";
        }
    }

    public void setGesture(int gesture) {
        prefs.edit().putInt(PREF_GESTURE, gesture).apply();
    }
}
