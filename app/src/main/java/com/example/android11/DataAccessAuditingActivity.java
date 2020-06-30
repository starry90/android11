package com.example.android11;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.AsyncNotedAppOp;
import android.app.SyncNotedAppOp;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.android11.util.PermissionUtils;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 数据访问审核
 */
public class DataAccessAuditingActivity extends AppCompatActivity {

    public static boolean hasOpNotedCallback = false;

    public final String TAG = "DataAccessAuditing";

    private Context attributionContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_access_auditing);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            finish();
            return;
        }

        //创建归因标记 example:照片位置信息分享
        attributionContext = createAttributionContext("sharePhotos");

        if (hasOpNotedCallback) {
            return;
        }

        AppOpsManager.OnOpNotedCallback appOpsCallback =
                new AppOpsManager.OnOpNotedCallback() {
                    private void logPrivateDataAccess(String opCode,
                                                      String attributionTag, String trace) {
                        Log.i(TAG, "Private data accessed. " +
                                "Operation: " + opCode +
                                "\nAttribution Tag:" + attributionTag +
                                "\nStack Trace:\n " + trace);
                    }

                    @Override
                    public void onNoted(@NonNull SyncNotedAppOp syncNotedAppOp) {
                        logPrivateDataAccess(syncNotedAppOp.getOp(),
                                syncNotedAppOp.getAttributionTag(),
                                Arrays.toString(new Throwable().getStackTrace()));
                    }

                    @Override
                    public void onSelfNoted(@NonNull SyncNotedAppOp syncNotedAppOp) {
                        logPrivateDataAccess(syncNotedAppOp.getOp(),
                                syncNotedAppOp.getAttributionTag(),
                                Arrays.toString(new Throwable().getStackTrace()));
                    }

                    @Override
                    public void onAsyncNoted(@NonNull AsyncNotedAppOp asyncNotedAppOp) {
                        logPrivateDataAccess(asyncNotedAppOp.getOp(),
                                asyncNotedAppOp.getAttributionTag(),
                                asyncNotedAppOp.getMessage());
                    }
                };

        AppOpsManager appOpsManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            appOpsManager = getSystemService(AppOpsManager.class);
            if (appOpsManager != null) {
                //只能设置一次Callback
                //设置多次会抛出异常：IllegalStateException: Another callback is already registered
                //最好放在Application中设置，或者放在单例中
                appOpsManager.setOnOpNotedCallback(getMainExecutor(), appOpsCallback);
                hasOpNotedCallback = true;
            }
        }
    }

    private LocationManager locationManager = null;

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.e(TAG, "location:" + location.toString());
        }

    };

    public void requestLocation(View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (!PermissionUtils.checkPermission(this, permissions)) {
            //提示用户权限授予
            return;
        }

        locationManager =
                attributionContext.getSystemService(LocationManager.class);
        if (locationManager != null) {
            // Use "locationManager" to access device location information.

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, locationListener);
        }
    }

    public void removeLocation(View view) {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeLocation(null);
    }
}