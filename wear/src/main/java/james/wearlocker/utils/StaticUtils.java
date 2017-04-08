package james.wearlocker.utils;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import james.wearlocker.services.OverlayService;

public class StaticUtils {

    @ColorInt
    public static int getAlphaColor(@ColorInt int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.blue(color), Color.green(color));
    }

    public static boolean arePermissionsGranted(Context context) {
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        if (info.requestedPermissions != null) {
            for (String permission : info.requestedPermissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.wtf("Permission", permission);
                    if (!permission.matches(Manifest.permission.SYSTEM_ALERT_WINDOW))
                        return false;
                }
            }
        }

        return true;
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
