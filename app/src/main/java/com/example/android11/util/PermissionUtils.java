package com.example.android11.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * 权限工具类
 */

public class PermissionUtils {

    public static final int STORAGE_REQUEST_CODE = 1100;

    public static final int FOREGROUND_LOCATION_REQUEST_CODE = 1101;

    public static final int BACKGROUND_LOCATION_REQUEST_CODE = 1102;

    /**
     * 检查是否有指定权限权限
     *
     * @param activity    Activity
     * @param permissions 权限组
     * @return true表示有权限
     */
    public static boolean checkPermission(Activity activity, String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return false;
        }
        for (String permission : permissions) {
            if (TextUtils.isEmpty(permission)) {
                return false;
            }
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 请求所有文件访问权限
     */
    public static boolean requestStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT > 29) {
            boolean externalStorageManager = Environment.isExternalStorageManager();
            if (!externalStorageManager) {
                // <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivity(intent);
            } else {
                return true;
            }
        } else {
            String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};
            if (checkPermission(activity, permissions)) {
                ActivityCompat.requestPermissions(activity, permissions, STORAGE_REQUEST_CODE);
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取前台获取位置权限
     */
    public static void requestForegroundLocationPermission(Activity activity) {
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (!checkPermission(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, FOREGROUND_LOCATION_REQUEST_CODE);
        }
    }

    /**
     * 获取后台获取位置权限
     * 在 Android 10（API 级别 29）及以上版本中，您必须在应用的清单中声明 ACCESS_BACKGROUND_LOCATION 权限，以便请求在运行时于后台访问位置信息。
     * 在较低版本的 Android 系统中，当应用获得前台位置信息访问权限时，也会自动获得后台位置信息访问权限。
     * <p>
     * 如果应用以 Android 11 为目标平台，系统会强制执行此最佳做法。如果您同时请求前台位置信息和后台位置信息，系统会抛出异常。
     */
    public static void requestBackgroundLocationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT < 29) {
            return;
        }

        String[] permissions = new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        if (!checkPermission(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, BACKGROUND_LOCATION_REQUEST_CODE);
        }
    }

    /**
     * 进入到应用设置页面
     */
    public static void showApplicationDetailsSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        try {
            activity.startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
