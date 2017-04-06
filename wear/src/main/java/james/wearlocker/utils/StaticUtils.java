package james.wearlocker.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import james.wearlocker.activities.SetupActivity;
import james.wearlocker.services.OverlayService;

/**
 * Created by james on 4/6/17.
 */

public class StaticUtils {

    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        else return Settings.canDrawOverlays(context);
    }

    public static boolean isPermissionsGranted(Context context) {
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
                    if (!permission.matches(Manifest.permission.SYSTEM_ALERT_WINDOW) && !permission.matches(Manifest.permission.GET_TASKS))
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
