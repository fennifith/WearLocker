package james.wearlocker.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import james.wearlocker.WearLocker;
import james.wearlocker.utils.OnDoubleTapListener;
import james.wearlocker.utils.StaticUtils;

public class OverlayService extends Service implements View.OnTouchListener, GestureDetector.OnGestureListener, WearLocker.OnPreferenceChangedListener {

    public static final String ACTION_SHOW_OVERLAY = "james.wearlocker.ACTION_SHOW_OVERLAY";

    private WindowManager windowManager;
    private GestureDetector gestureDetector;
    private View windowView;

    private SleepReceiver reciever;
    private Vibrator vibrator;

    private WearLocker wearLocker;

    @Override
    public void onCreate() {
        super.onCreate();
        wearLocker = (WearLocker) getApplicationContext();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        windowView = new View(this);
        windowView.setBackgroundColor(StaticUtils.getAlphaColor(wearLocker.getColor(), 100));
        windowView.setOnTouchListener(this);
        windowView.setOnClickListener(new OnDoubleTapListener() {
            @Override
            public void onSingleClick(View v) {
            }

            @Override
            public void onDoubleClick(View v) {
                if (wearLocker.getGesture() == WearLocker.GESTURE_DOUBLE_TAP)
                    hideWindowView();
            }
        });

        gestureDetector = new GestureDetector(this, this);

        reciever = new SleepReceiver(this);
        reciever.register();

        wearLocker.addListener(this);
    }

    private void showWindowView() {
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

    private void hideWindowView() {
        if (windowManager != null && windowView.getParent() != null) {
            windowView.setVisibility(View.GONE);
            if (wearLocker.isVibrate())
                vibrator.vibrate(100);
        }
    }

    @Override
    public void onDestroy() {
        if (windowView.getParent() != null)
            windowManager.removeViewImmediate(windowView);

        reciever.unregister();
        wearLocker.removeListener(this);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_SHOW_OVERLAY:
                    showWindowView();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (wearLocker.getGesture() == WearLocker.GESTURE_HOLD) {
            hideWindowView();
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(velocityX) > Math.abs(velocityY)) {
            Log.d("Gesture", "X: " + String.valueOf(velocityX) + " Y: " + String.valueOf(velocityY) + " (horizontal) " + wearLocker.getGestureTitle());

            if (velocityX > 1800 && wearLocker.getGesture() == WearLocker.GESTURE_SWIPE_RIGHT)
                hideWindowView();
            else if (velocityX < -1800 && wearLocker.getGesture() == WearLocker.GESTURE_SWIPE_LEFT)
                hideWindowView();
        } else {
            Log.d("Gesture", "X: " + String.valueOf(velocityX) + " Y: " + String.valueOf(velocityY) + " (vertical) " + wearLocker.getGestureTitle());

            if (velocityY > 1800 && wearLocker.getGesture() == WearLocker.GESTURE_SWIPE_DOWN)
                hideWindowView();
            else if (velocityY < -1800 && wearLocker.getGesture() == WearLocker.GESTURE_SWIPE_UP)
                hideWindowView();
        }
        return false;
    }

    @Override
    public void onPreferenceChanged(String name) {
        switch (name) {
            case WearLocker.PREF_COLOR:
                windowView.setBackgroundColor(StaticUtils.getAlphaColor(wearLocker.getColor(), 100));
                break;
        }
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
                service.showWindowView();
            else if (service == null && ((WearLocker) context.getApplicationContext()).isEnabled())
                context.startService(new Intent(context, OverlayService.class));
        }
    }
}
