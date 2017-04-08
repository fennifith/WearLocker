package james.wearlocker.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.NestedScrollView;
import android.support.wearable.view.drawer.WearableActionDrawer;
import android.support.wearable.view.drawer.WearableDrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import james.wearcolorpicker.WearColorPickerActivity;
import james.wearlocker.R;
import james.wearlocker.WearLocker;
import james.wearlocker.services.OverlayService;
import james.wearlocker.utils.StaticUtils;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final int REQUEST_SETUP = 524;
    private static final int REQUEST_COLOR = 837;
    private static final int REQUEST_GESTURE = 284;

    private WearableDrawerLayout drawerLayout;

    private View enable;
    private TextView enableText;
    private View color;
    private View gesture;
    private TextView gestureText;

    private WearLocker wearLocker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wearLocker = (WearLocker) getApplicationContext();

        drawerLayout = (WearableDrawerLayout) findViewById(R.id.drawerLayout);
        WearableActionDrawer actionDrawer = (WearableActionDrawer) findViewById(R.id.actionDrawer);
        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.scrollView);
        enable = findViewById(R.id.enable);
        enableText = (TextView) findViewById(R.id.enableText);
        color = findViewById(R.id.color);
        gesture = findViewById(R.id.gesture);
        gestureText = (TextView) findViewById(R.id.gestureText);
        View preview = findViewById(R.id.preview);

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

        enableText.setText(wearLocker.isEnabled() ? R.string.status_enabled : R.string.status_disabled);
        enable.setOnClickListener(this);

        color.setBackgroundColor(StaticUtils.getAlphaColor(wearLocker.getColor(), 100));
        color.setOnClickListener(this);

        gestureText.setText(wearLocker.getGestureTitle());
        gesture.setOnClickListener(this);

        preview.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enable:
                boolean isEnabled = !wearLocker.isEnabled();

                wearLocker.setEnabled(isEnabled);
                enableText.setText(isEnabled ? R.string.status_enabled : R.string.status_disabled);

                if (isEnabled) {
                    if (StaticUtils.arePermissionsGranted(this) && Settings.canDrawOverlays(this))
                        startService(new Intent(this, OverlayService.class));
                    else
                        startActivityForResult(new Intent(this, SetupActivity.class), REQUEST_SETUP);
                } else stopService(new Intent(this, OverlayService.class));
                break;
            case R.id.color:
                Intent colorIntent = new Intent(this, WearColorPickerActivity.class);
                colorIntent.putExtra(WearColorPickerActivity.EXTRA_COLOR, wearLocker.getColor());
                startActivityForResult(colorIntent, REQUEST_COLOR);
                break;
            case R.id.gesture:
                Intent gestureIntent = new Intent(this, OptionActivity.class);
                gestureIntent.putExtra(OptionActivity.EXTRA_TITLE, getString(R.string.title_gesture));
                gestureIntent.putExtra(OptionActivity.EXTRA_OPTIONS, (ArrayList<String>) wearLocker.getGestureTitles());
                startActivityForResult(gestureIntent, REQUEST_GESTURE);
                break;
            case R.id.preview:
                Intent previewIntent = new Intent(this, OverlayService.class);
                previewIntent.setAction(OverlayService.ACTION_SHOW_OVERLAY);
                startService(previewIntent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SETUP:
                if (resultCode == RESULT_OK && StaticUtils.arePermissionsGranted(this) && Settings.canDrawOverlays(this) && wearLocker.isEnabled())
                    startService(new Intent(this, OverlayService.class));
                break;
            case REQUEST_COLOR:
                if (resultCode == RESULT_OK && data != null && data.hasExtra(WearColorPickerActivity.EXTRA_COLOR)) {
                    int color = data.getIntExtra(WearColorPickerActivity.EXTRA_COLOR, Color.BLACK);
                    wearLocker.setColor(color);
                    this.color.setBackgroundColor(StaticUtils.getAlphaColor(color, 100));
                }
                break;
            case REQUEST_GESTURE:
                if (resultCode == RESULT_OK && data != null && data.hasExtra(OptionActivity.EXTRA_INDEX)) {
                    wearLocker.setGesture(data.getIntExtra(OptionActivity.EXTRA_INDEX, wearLocker.getGesture()));
                    gestureText.setText(wearLocker.getGestureTitle());
                }
                break;
        }
    }
}
