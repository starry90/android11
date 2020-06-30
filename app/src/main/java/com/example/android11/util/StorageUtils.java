package com.example.android11.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class StorageUtils {

    private static final String TAG = "StorageUtils";

    /**
     * 往文件写内容
     *
     * @param file    文件
     * @param content 文件内容
     */
    public static void writeText(File file, String content) {
        try {
            Log.e(TAG, file.getAbsolutePath());
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            FileUtils.writeText(fileOutputStream, content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件内容
     *
     * @param file 文件
     * @return 文件内容
     */
    public static String readText(File file) {
        try {
            FileInputStream fileOutputStream = new FileInputStream(file);
            String s = FileUtils.readText(fileOutputStream);
            Log.e(TAG, file.getAbsolutePath() + " \n  >>" + s);
            return s;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
