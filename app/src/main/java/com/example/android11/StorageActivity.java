package com.example.android11;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android11.util.FileUtils;
import com.example.android11.util.PermissionUtils;
import com.example.android11.util.StorageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 存储机制更新和分区存储
 */
public class StorageActivity extends AppCompatActivity {

    private static final String TAG = "StorageActivity";

    ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        executorService = Executors.newSingleThreadExecutor();
    }

    public void requestStoragePermission(View view) {
        if (PermissionUtils.requestStoragePermission(this)) {
            Toast.makeText(this, "已拥有所有文件访问权限", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 打开应用的权限列表设置界面
     */
    public void requestApplicationDetailsSettings(View view) {
        PermissionUtils.showApplicationDetailsSettings(this);
    }

    /**
     * MediaStore方式写入文件到公共目录中的文件
     */
    public void mediaStoreWrite(View view) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT < 29) {
                    return;
                }

                ContentResolver resolver = getContentResolver();
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, "test");
                //设置文件类型（有哪些类型网上很容易查到，如果不设置的话，就是默认没有扩展名的文件）
                values.put(MediaStore.Downloads.MIME_TYPE, "text/plain");
                Uri external = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                // 写入文件
                Uri insertUri = resolver.insert(external, values);

                // io写入
                try {
                    final OutputStream outputStream = resolver.openOutputStream(insertUri);
                    FileUtils.writeText(outputStream, "MediaStore方式写入文件到外部目录");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * MediaStore方式读取公共目录中的文件
     */
    public void mediaStoreRead(View view) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT < 29) {
                    return;
                }

                ContentResolver resolver = getContentResolver();
                Uri external = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                Cursor query = resolver.query(external, null, MediaStore.Downloads.DISPLAY_NAME + "=?"
                        , new String[]{"test.txt"}, null);

                while (query != null && query.moveToNext()) {
                    int ID = query.getInt(query.getColumnIndex(MediaStore.Downloads._ID));
                    String displayName = query.getString(query.getColumnIndex(MediaStore.Downloads.DISPLAY_NAME));
                    String mimeType = query.getString(query.getColumnIndex(MediaStore.Downloads.MIME_TYPE));
                    Log.e(TAG, "NAME:" + displayName + " MIME:" + mimeType);

                    Uri uri = Uri.withAppendedPath(external, String.valueOf(ID));
                    try {
                        InputStream inputStream = resolver.openInputStream(uri);
                        Log.e(TAG, "content: " + FileUtils.readText(inputStream));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * App-specific 目录写文件
     * Android11无需权限
     * Android11之前需要拥有WRITE_EXTERNAL_STORAGE权限
     */
    public void appSpecificWrite(View view) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // /storage/emulated/0/Android/data/<package-name>/files/Download
                File downloadDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(downloadDir, "App-specific.txt");
                StorageUtils.writeText(file, "App-specific 目录读写文件");
            }
        });
    }

    /**
     * App-specific 目录读取文件
     * Android11无需权限
     * Android11之前需要拥有READ_EXTERNAL_STORAGE权限
     */
    public void appSpecificRead(View view) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // /storage/emulated/0/Android/data/<package-name>/files/Download
                File downloadDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(downloadDir, "App-specific.txt");
                StorageUtils.readText(file);
            }
        });
    }

    public void openSpecificFile(View view) {
        File downloadDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadDir, "App-specific.txt");
        FileUtils.openFileByPath(this, file.getAbsolutePath());
    }

    /**
     * App私有目录写文件
     * Android11无需权限
     * Android11之前需要拥有WRITE_EXTERNAL_STORAGE权限
     */
    public void appPrivateWrite(View view) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // /data/data/<package-name>/files
                File externalFilesDir = getFilesDir();
                File file = new File(externalFilesDir, "app-private.txt");
                StorageUtils.writeText(file, "App私有目录读写文件");
            }
        });
    }

    /**
     * App私有目录读取文件
     * Android11无需权限
     * Android11之前需要拥有READ_EXTERNAL_STORAGE权限
     */
    public void appPrivateRead(View view) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // /data/data/<package-name>/files
                File filesDir = getFilesDir();
                File file = new File(filesDir, "app-private.txt");
                StorageUtils.readText(file);
            }
        });
    }

    /**
     * 外部存储目录写文件
     * Android11需要拥有 MANAGE_EXTERNAL_STORAGE权限
     * Android11之前需要拥有WRITE_EXTERNAL_STORAGE权限
     */
    public void externalStorageWrite(View view) {
        // /storage/emulated/0
        File externalFilesDir = Environment.getExternalStorageDirectory();
        File file = new File(externalFilesDir, "External-Storage.txt");

        StorageUtils.writeText(file, "外部目录读写文件");
    }

    /**
     * 外部存储目录读取文件
     * Android11需要拥有 MANAGE_EXTERNAL_STORAGE权限
     * Android11之前需要拥有READ_EXTERNAL_STORAGE权限
     */
    public void externalStorageRead(View view) {
        // /storage/emulated/0
        File filesDir = Environment.getExternalStorageDirectory();
        File file = new File(filesDir, "External-Storage.txt");
        StorageUtils.readText(file);
    }

}