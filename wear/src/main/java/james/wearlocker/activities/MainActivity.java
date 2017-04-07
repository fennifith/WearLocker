package james.wearlocker.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.NestedScrollView;
import android.support.wearable.view.drawer.WearableActionDrawer;
import android.support.wearable.view.drawer.WearableDrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import james.wearlocker.R;
import james.wearlocker.WearLocker;
import james.wearlocker.services.OverlayService;
import james.wearlocker.utils.StaticUtils;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final int REQUEST_SETUP = 524;

    private WearableDrawerLayout drawerLayout;
    private WearableActionDrawer actionDrawer;
    private NestedScrollView scrollView;

    private TextView enabled;

    private WearLocker wearLocker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wearLocker = (WearLocker) getApplicationContext();

        drawerLayout = (WearableDrawerLayout) findViewById(R.id.drawerLayout);
        actionDrawer = (WearableActionDrawer) findViewById(R.id.actionDrawer);
        scrollView = (NestedScrollView) findViewById(R.id.scrollView);
        enabled = (TextView) findViewById(R.id.enableSwitch);

        if (StaticUtils.arePermissionsGranted(this) && Settings.canDrawOverlays(this)) {
            if (wearLocker.isEnabled())
                startService(new Intent(this, OverlayService.class));
        } else startActivityForResult(new Intent(this, SetupActivity.class), REQUEST_SETUP);

        drawerLayout.peekDrawer(Gravity.BOTTOM);
        actionDrawer.lockDrawerClosed();
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0 || scrollY < oldScrollY)
                    drawerLayout.peekDrawer(Gravity.BOTTOM);
                else drawerLayout.closeDrawer(Gravity.BOTTOM);
            }
        });

        enabled.setText(String.format(Locale.getDefault(), getString(R.string.enabled), wearLocker.isEnabled() ? "Enabled" : "Disabled"));
        enabled.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETUP && resultCode == RESULT_OK && StaticUtils.arePermissionsGranted(this) && Settings.canDrawOverlays(this) && wearLocker.isEnabled())
            startService(new Intent(this, OverlayService.class));
    }

    @Override
    public void onClick(View v) {
        boolean isEnabled = !wearLocker.isEnabled();

        wearLocker.setEnabled(isEnabled);
        enabled.setText(String.format(Locale.getDefault(), getString(R.string.enabled), isEnabled ? "Enabled" : "Disabled"));

        if (isEnabled) {
            if (StaticUtils.arePermissionsGranted(this) && Settings.canDrawOverlays(this))
                startService(new Intent(this, OverlayService.class));
            else
                startActivityForResult(new Intent(this, SetupActivity.class), REQUEST_SETUP);
        } else stopService(new Intent(this, OverlayService.class));
    }
}
