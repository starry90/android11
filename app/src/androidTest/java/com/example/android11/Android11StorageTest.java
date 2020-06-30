package com.example.android11;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.android11.util.FileUtils;
import com.example.android11.util.Utils;
import com.example.android11.util.StorageUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Android11StorageTest {

    static final String TAG = "ExampleInstrumentedTest";

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.android11", appContext.getPackageName());
    }

    private Context getContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        return appContext;
    }

    /**
     * 查看外部存储根目录文件列表
     */
    @Test
    public void listExternalStorageDirectory() {
        Context context = getContext();
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File[] files = externalStorageDirectory.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        for (File file : files) {
            Log.e(TAG, file.getAbsolutePath());
        }
    }

    @Test
    public void isExternalStorageManager() {
        boolean externalStorageManager = Environment.isExternalStorageManager();
        Log.e(TAG, "manager: " + externalStorageManager);

        File downloadCacheDirectory = Environment.getDownloadCacheDirectory();
        Log.e(TAG, "downloadCacheDirectory: " + downloadCacheDirectory.getAbsolutePath());
    }

    /**
     * MediaStore方式写入文件到公共目录中的文件
     */
    @Test
    public void mediaStoreWrite() {
        ContentResolver resolver = getContext().getContentResolver();
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

    /**
     * MediaStore方式读取公共目录中的文件
     */
    @Test
    public void mediaStoreRead() {
        ContentResolver resolver = getContext().getContentResolver();
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

    /**
     * App-specific 目录写文件
     * Android11无需权限
     * Android11之前需要拥有WRITE_EXTERNAL_STORAGE权限
     */
    @Test
    public void appSpecificWrite() {
        Context context = getContext();
        // /storage/emulated/0/Android/data/<package-name>/files/Download
        File downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadDir, "App-specific.txt");
        StorageUtils.writeText(file, "App-specific 目录读写文件");
    }

    /**
     * App-specific 目录读取文件
     * Android11无需权限
     * Android11之前需要拥有READ_EXTERNAL_STORAGE权限
     */
    @Test
    public void appSpecificRead() {
        Context context = getContext();
        // /storage/emulated/0/Android/data/<package-name>/files/Download
        File downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadDir, "App-specific.txt");
        StorageUtils.readText(file);

    }


    /**
     * App私有目录写文件
     * Android11无需权限
     * Android11之前需要拥有WRITE_EXTERNAL_STORAGE权限
     */
    @Test
    public void appPrivateWrite() {
        Context context = getContext();
        // /data/data/<package-name>/files
        File externalFilesDir = context.getFilesDir();
        File file = new File(externalFilesDir, "app-private.txt");
        StorageUtils.writeText(file, "App私有目录读写文件");

    }

    /**
     * App私有目录读取文件
     * Android11无需权限
     * Android11之前需要拥有READ_EXTERNAL_STORAGE权限
     */
    @Test
    public void appPrivateRead() {
        Context context = getContext();
        // /data/data/<package-name>/files
        File filesDir = context.getFilesDir();
        File file = new File(filesDir, "app-private.txt");
        StorageUtils.readText(file);
    }

    /**
     * 外部存储目录写文件
     * Android11需要拥有 MANAGE_EXTERNAL_STORAGE权限
     * Android11之前需要拥有WRITE_EXTERNAL_STORAGE权限
     */
    @Test
    public void externalStorageWrite() {
        // /data/data/<package-name>/files
        File externalFilesDir = Environment.getExternalStorageDirectory();
        File file = new File(externalFilesDir, "External-Storage.txt");

        StorageUtils.writeText(file, "外部目录读写文件");
    }

    /**
     * 外部存储目录读取文件
     * Android11需要拥有 MANAGE_EXTERNAL_STORAGE权限
     * Android11之前需要拥有READ_EXTERNAL_STORAGE权限
     */
    @Test
    public void externalStorageRead() {
        // /data/data/<package-name>/files
        File filesDir = Environment.getExternalStorageDirectory();
        File file = new File(filesDir, "External-Storage.txt");
        StorageUtils.readText(file);
    }

    @Test
    public void testMAC() {
        String addressMAC = Utils.getAddressMAC(getContext());
        Log.e(TAG, "mac=" + addressMAC);
    }
}