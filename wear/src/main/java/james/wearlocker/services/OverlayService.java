package james.wearlocker.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import james.wearlocker.WearLocker;
import james.wearlocker.utils.OnDoubleTapListener;

public class OverlayService extends Service implements View.OnTouchListener {

    private WindowManager windowManager;
    private View windowView;

    private SleepReceiver reciever;

    private WearLocker wearLocker;

    @Override
    public void onCreate() {
        super.onCreate();
        wearLocker = (WearLocker) getApplicationContext();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        windowView = new View(this);
        windowView.setBackgroundColor(Color.argb(100, 0, 0, 0));
        windowView.setAlpha(0);
        windowView.setOnTouchListener(this);
        windowView.setOnClickListener(new OnDoubleTapListener() {
            @Override
            public void onSingleClick(View v) {
            }

            @Override
            public void onDoubleClick(View v) {
                removeWindowView();
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }
        });

        reciever = new SleepReceiver(this);
        reciever.register();
    }

    private void addWindowView() {
        if (windowManager != null) {
            try {
                if (windowView.getParent() == null) {
                    windowManager.addView(windowView, new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                            PixelFormat.TRANSPARENT
                    ));
                }

                windowView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                if (!Settings.canDrawOverlays(getApplicationContext()))
                    startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
                else if (!(e instanceof IllegalStateException)) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void removeWindowView() {
        if (windowManager != null && windowView.getParent() != null)
            windowView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        Log.d("OverlayService", "Destroyed");
        if (windowView.getParent() != null)
            windowManager.removeViewImmediate(windowView);

        reciever.unregister();
        super.onDestroy();
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    private static class SleepReceiver extends BroadcastReceiver {

        private WeakReference<OverlayService> serviceReference;

        public SleepReceiver(OverlayService service) {
            serviceReference = new WeakReference<OverlayService>(service);
        }

        public void register() {
            OverlayService service = serviceReference.get();
            if (service != null) {
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_SCREEN_ON);
                service.registerReceiver(this, filter);
            }
        }

        public void unregister() {
            OverlayService service = serviceReference.get();
            if (service != null)
                service.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            OverlayService service = serviceReference.get();
            if (service != null && intent != null && intent.getAction() != null)
                service.addWindowView();
            else if (service == null && ((WearLocker) context.getApplicationContext()).isEnabled())
                context.startService(new Intent(context, OverlayService.class));
        }
    }
}
