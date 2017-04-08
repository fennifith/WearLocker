package james.wearlocker.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;

import james.wearlocker.services.OverlayService;

public class StaticUtils {

    @ColorInt
    public static int getAlphaColor(@ColorInt int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (OverlayService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
