package net.fosky.cjlu.wifi.cmcc.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Wifi {
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            int type = networkInfo.getType();

            if (type == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ssid = wifiInfo.getSSID();
                if (ssid.equals("<unknown ssid>")) {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    networkInfo = cm.getActiveNetworkInfo();
                    ssid = networkInfo.getExtraInfo();
                }
                return ssid.contains("CMCC");
            }
        }
        return false;
    }

    public static String testPage() {
        try {
            URL url = new URL("http://connectivitycheck.platform.hicloud.com/generate_204");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            conn.setUseCaches(false);
            // conn.getInputStream();
            return String.valueOf(conn.getResponseCode());
            // return conn.getHeaderField("Location");
        } catch (IOException e) {
            //   e.printStackTrace();
            return "";
        }
    }
    public static boolean isPortal() {
        String url = "http://connectivitycheck.platform.hicloud.com/generate_204";
        try {
            URL request = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) request.openConnection();
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(500);
            conn.setReadTimeout(500);

            int statusCode = conn.getResponseCode();
            if (conn.getResponseCode() == 302 || conn.getResponseCode() == 301) {
                return true;
            }
            return !(statusCode == 204);
        } catch (Exception e) {
            return false;
        }
    }

    public static String getPortalPage() {
        String url = "http://connectivitycheck.platform.hicloud.com/generate_204";
        try {
            URL request = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) request.openConnection();
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(500);
            conn.setReadTimeout(500);

            if (conn.getResponseCode() == 302 || conn.getResponseCode() == 301) {
                return conn.getHeaderField("Location");
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }
}
