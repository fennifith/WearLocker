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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import james.wearlocker.R;
import james.wearlocker.utils.StaticUtils;

public class SetupActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 384;
    private static final int REQUEST_OVERLAY = 623;
    private static final int REQUEST_ACCESSIBILITY = 635;

    private AppCompatCheckBox permissionsCheckBox;
    private AppCompatCheckBox overlayCheckBox;
    private AppCompatCheckBox accessibilityCheckBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        permissionsCheckBox = (AppCompatCheckBox) findViewById(R.id.permissionsCheckBox);
        overlayCheckBox = (AppCompatCheckBox) findViewById(R.id.overlayCheckBox);
        accessibilityCheckBox = (AppCompatCheckBox) findViewById(R.id.accessibilityCheckBox);

        permissionsCheckBox.setChecked(arePermissionsGranted());
        permissionsCheckBox.setOnClickListener(new View.OnClickListener() {
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

        overlayCheckBox.setChecked(Settings.canDrawOverlays(this));
        overlayCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), REQUEST_OVERLAY);
            }
        });

        accessibilityCheckBox.setChecked(StaticUtils.isServiceRunning(this));
        accessibilityCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), REQUEST_ACCESSIBILITY);
            }
        });
    }

    private boolean arePermissionsGranted() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        if (info.requestedPermissions != null) {
            for (String permission : info.requestedPermissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.wtf("Permission", permission);
                    if (!permission.matches(Manifest.permission.SYSTEM_ALERT_WINDOW))
                        return false;
                }
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionsCheckBox.setChecked(false);
                    return;
                }
            }

            permissionsCheckBox.setChecked(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_OVERLAY:
                break;
            case REQUEST_ACCESSIBILITY:
                break;
        }
    }
}
