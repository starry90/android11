package com.example.android11;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
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