package com.example.android11;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class ForegroundService extends Service {

    private static final String TAG = "ForegroundService";

    private static final int ONGOING_NOTIFICATION_ID = 1100;

    private static final String CHANNEL_ONE_ID = "id_001";
    private static final String CHANNEL_ONE_NAME = "name_001";

    private static final String EXTRA_ACTION = "extra_action";

    private static final String ACTION_LOCATION = "action_location";

    public static void startLocationUpdate(Context context) {
        Intent intent = new Intent(context, ForegroundService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_LOCATION);
        context.startService(intent);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Notification.Builder builder = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //如果没有通知渠道ID，会抛出异常
            // android.app.RemoteServiceException: Bad notification for startForeground
            builder.setChannelId(CHANNEL_ONE_ID);
        }

        Intent notificationIntent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = builder
                .setContentTitle("前台服务通知的标题")
                .setContentText("前台服务通知的内容")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        //成为前台服务
        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String stringExtra = intent.getStringExtra(EXTRA_ACTION);
            if (TextUtils.equals(stringExtra, ACTION_LOCATION)) {
                lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, locationListener);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private LocationManager lManager = null;

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.e(TAG, "location:" + location.toString());
        }

    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lManager != null) {
            lManager.removeUpdates(locationListener);
        }
        // 停止前台服务--参数：表示是否移除之前的通知
        stopForeground(true);
    }
}
