package com.example.android11.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class Utils {

    private static final String TAG = "PhoneUtil";

    private static final String MARSHMALLOW_MAC_ADDRESS = "02:00:00:00:00:00";
    private static final String FILE_ADDRESS_MAC = "/sys/class/net/wlan0/address";

    public static String getAddressMAC(Context context) {
        WifiManager wifiMan = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();

        String macAddress = wifiInf.getMacAddress();
        if (MARSHMALLOW_MAC_ADDRESS.equals(macAddress)) {
            try {
                String result = getAddressMacByWLAN();
                if (result == null) {
                    result = getAddressMacByFile(wifiMan);
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (macAddress != null) {
            return macAddress;
        }
        return MARSHMALLOW_MAC_ADDRESS;
    }

    private static String getAddressMacByWLAN() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            List<NetworkInterface> all = Collections.list(networkInterfaces);
            for (NetworkInterface nif : all) {
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:", b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getAddressMacByFile(WifiManager wifiMan) throws Exception {
        String ret;
        int wifiState = wifiMan.getWifiState();

        wifiMan.setWifiEnabled(true);
        File fl = new File(FILE_ADDRESS_MAC);
        FileInputStream fin = new FileInputStream(fl);
        ret = getStringFromStream(fin);
        fin.close();

        boolean enabled = WifiManager.WIFI_STATE_ENABLED == wifiState;
        wifiMan.setWifiEnabled(enabled);

        return ret;
    }

    private static String getStringFromStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }

        Writer stringWriter = new StringWriter();
        char[] crunchifyBuffer = new char[2048];
        try {
            Reader crunchifyReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int counter;
            while ((counter = crunchifyReader.read(crunchifyBuffer)) != -1) {
                stringWriter.write(crunchifyBuffer, 0, counter);
            }
        } finally {
            inputStream.close();
        }
        return stringWriter.toString();
    }

}
