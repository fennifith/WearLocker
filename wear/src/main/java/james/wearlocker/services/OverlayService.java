package james.wearlocker.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import james.wearlocker.WearLocker;

public class OverlayService extends Service {

    private WindowManager windowManager;

    private WearLocker wearLocker;

    @Override
    public void onCreate() {
        super.onCreate();
        wearLocker = (WearLocker) getApplicationContext();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
