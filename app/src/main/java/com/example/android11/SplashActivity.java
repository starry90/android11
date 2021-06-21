package com.example.android11;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        String androidId = getAndroidId();
        Log.e("SplashActivity", "androidId: " + androidId);
    }

    /**
     * Google 给出的解决方案是：如果您的应用有 追踪非登录用户重装 的需求，可用 ANDROID_ID 来标识设备。
     * <p>
     * ANDROID_ID 的生成规则为：签名 + 设备信息 + 设备用户
     * ANDROID_ID 重置规则：设备恢复出厂设置时，ANDROID_ID 将被重置
     * <p>
     * 也就是从 Android 10 开始已经无法完全标识一个设备，曾经用 mac 地址、IMEI 等设备信息标识设备的方法，从 Android 10 开始统统失效。
     *
     * @return
     */
    @SuppressLint("HardwareIds")
    private String getAndroidId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void showStorageUpdate(View view) {
        startActivity(new Intent(this, StorageActivity.class));
    }

    public void showLocationUpdate(View view) {
        startActivity(new Intent(this, LocationActivity.class));
    }

    public void showDataAccessAuditing(View view) {
        startActivity(new Intent(this, DataAccessAuditingActivity.class));
    }

    public void showForegroundService(View view) {
        startActivity(new Intent(this, ForegroundServiceActivity.class));
    }
}