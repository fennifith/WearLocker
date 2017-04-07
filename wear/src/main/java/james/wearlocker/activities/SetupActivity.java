package james.wearlocker.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import james.wearlocker.R;
import james.wearlocker.utils.StaticUtils;

public class SetupActivity extends Activity {

    private static final int REQUEST_PERMISSIONS = 384;
    private static final int REQUEST_OVERLAY = 623;

    private TextView permissions;
    private TextView overlay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        permissions = (TextView) findViewById(R.id.permissionsCheckBox);
        overlay = (TextView) findViewById(R.id.overlayCheckBox);

        permissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageInfo info;
                try {
                    info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    return;
                }

                if (info.requestedPermissions != null) {
                    List<String> unrequestedPermissions = new ArrayList<>();
                    for (String permission : info.requestedPermissions) {
                        if (ContextCompat.checkSelfPermission(SetupActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                            Log.wtf("Permission", permission);
                            if (permission.length() > 0 && !permission.matches(Manifest.permission.SYSTEM_ALERT_WINDOW))
                                unrequestedPermissions.add(permission);
                        }
                    }

                    if (unrequestedPermissions.size() > 0)
                        ActivityCompat.requestPermissions(SetupActivity.this, unrequestedPermissions.toArray(new String[unrequestedPermissions.size()]), REQUEST_PERMISSIONS);
                }
            }
        });

        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), REQUEST_OVERLAY);
            }
        });

        setResult(StaticUtils.arePermissionsGranted(this) && Settings.canDrawOverlays(this) ? RESULT_OK : RESULT_CANCELED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    setResult(RESULT_CANCELED);
                    return;
                }
            }

            if (Settings.canDrawOverlays(this))
                setResult(RESULT_OK);
            else setResult(RESULT_CANCELED);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OVERLAY) {
            if (Settings.canDrawOverlays(this)) {
                if (StaticUtils.arePermissionsGranted(this))
                    setResult(RESULT_OK);
                else setResult(RESULT_CANCELED);
            } else setResult(RESULT_CANCELED);
        }
    }
}
