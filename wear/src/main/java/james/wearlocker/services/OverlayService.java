package james.wearlocker.services;

import android.animation.Animator;
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

    private SleepReciever reciever;

    private WearLocker wearLocker;

    @Override
    public void onCreate() {
        super.onCreate();
        wearLocker = (WearLocker) getApplicationContext();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        windowView = new View(this);
        windowView.setBackgroundColor(Color.argb(50, 0, 0, 0));
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

        reciever = new SleepReciever(this);
        reciever.register();
    }

    private void addWindowView() {
        if (windowManager != null) {
            try {
                windowManager.addView(windowView, new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                        PixelFormat.TRANSPARENT
                ));
                windowView.animate().alpha(1).start();
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
        if (windowManager != null && windowView.getParent() != null) {
            windowView.animate().alpha(0).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    windowManager.removeViewImmediate(windowView);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            }).start();
        }
    }

    @Override
    public void onDestroy() {
        removeWindowView();
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

    private class SleepReciever extends BroadcastReceiver {

        private WeakReference<OverlayService> serviceReference;

        public SleepReciever(OverlayService service) {
            serviceReference = new WeakReference<OverlayService>(service);
        }

        public void register() {
            OverlayService service = serviceReference.get();
            if (service != null) {
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_SCREEN_ON);
                filter.addAction(Intent.ACTION_SCREEN_OFF);
                filter.addAction(Intent.ACTION_USER_PRESENT);
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
            if (service != null && intent != null && intent.getAction() != null) {
                switch (intent.getAction()) {
                    case Intent.ACTION_SCREEN_ON:
                        service.addWindowView();
                        break;
                    case Intent.ACTION_SCREEN_OFF:
                        if (service.windowView.getParent() != null)
                            service.windowManager.removeViewImmediate(service.windowView);
                        break;
                }
            }

            Log.d("OverlayService", "Something happened.");
        }
    }
}
