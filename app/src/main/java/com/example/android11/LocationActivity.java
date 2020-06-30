package com.example.android11;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android11.util.PermissionUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/**
 * 位置信息更新
 */
public class LocationActivity extends AppCompatActivity {

    private static final String TAG = "LocationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
    }


    public void requestForegroundLocation(View view) {
        PermissionUtils.requestForegroundLocationPermission(this);
    }

    public void requestBackgroundLocation(View view) {
        PermissionUtils.requestBackgroundLocationPermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionUtils.FOREGROUND_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "前台获取位置权限被拒绝，无法获取位置信息", Toast.LENGTH_LONG).show();
                return;
            }
        } else if (requestCode == PermissionUtils.BACKGROUND_LOCATION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT > 29) {
                //PackageManager.getBackgroundPermissionOptionLabel()获取选项标签内容
                boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                // Android11 如需启用后台位置信息访问权限，用户必须在设置页面上针对应用的位置权限设置一律允许选项
                if (shouldShow) {
                    //1.提示用户需要后台获取位置权限权限的具体原因
                    Toast.makeText(this, "本应用需要后台获取位置权限，请在设置页面启用", Toast.LENGTH_LONG).show();
                    //2.跳转到设置而让用户手动开启后台获取位置权限
                    PermissionUtils.showApplicationDetailsSettings(this);
                    return;
                }
            } else {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "后台获取位置权限被拒绝，无法获取位置信息", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        //授权成功
        //.....
    }

    private LocationManager lManager = null;

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.e(TAG, "location:" + location.toString());
        }

    };

    @Override
    protected void onStop() {
        super.onStop();
        if (lManager != null) {
            lManager.removeUpdates(locationListener);
        }
    }

    @SuppressLint("MissingPermission")
    public void getLocationInfo(View view) {
        //获取位置信息
        if (lManager == null) {
            lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, locationListener);
    }
}