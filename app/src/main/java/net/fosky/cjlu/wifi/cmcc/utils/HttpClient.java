package net.fosky.cjlu.wifi.cmcc.utils;

import android.annotation.SuppressLint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpClient {
    public static String Get(String url, Map<String, String> header) {
        URL request;
        StringBuilder result = new StringBuilder();
        try {
            request = new URL(url);

            HttpURLConnection conn = (HttpURLConnection) request.openConnection();

            boolean useHttps = url.startsWith("https");
            if (useHttps) {
                HttpsURLConnection https = (HttpsURLConnection) conn;
                trustAllHosts(https);
                https.setHostnameVerifier(DO_NOT_VERIFY);
            }

            conn.setRequestMethod("GET");
            conn.setReadTimeout(30000);
            conn.setRequestProperty("x-requested-with", "XMLHttpRequest");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            header.forEach(conn::setRequestProperty);

            conn.setDoInput(true);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }

            Log.add("Url:" + url);
            Log.add("Res:" + result);
        } catch (Exception e) {
            Log.add("Error:" + e.getMessage());
            e.printStackTrace();
        }

        return result.toString();
    }

    public static String Post(String url, String param, Map<String, String> header) {
        URL request;
        StringBuilder result = new StringBuilder();
        try {
            request = new URL(url);

            HttpURLConnection conn = (HttpURLConnection) request.openConnection();

            boolean useHttps = url.startsWith("https");
            if (useHttps) {
                HttpsURLConnection https = (HttpsURLConnection) conn;
                trustAllHosts(https);
                https.setHostnameVerifier(DO_NOT_VERIFY);
            }

            conn.setRequestMethod("POST");
            conn.setReadTimeout(30000);
            conn.setRequestProperty("x-requested-with", "XMLHttpRequest");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            header.forEach(conn::setRequestProperty);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }

            Log.add("Url:" + url);
            Log.add("Param:" + param);
            Log.add("Res:" + result);
            // System.out.println(result);
        } catch (Exception e) {
            Log.add("Error:" + e.getMessage());
            e.printStackTrace();
        }

        return result.toString();
    }

    /**
     * 覆盖java默认的证书验证
     */
    @SuppressLint("CustomX509TrustManager")
    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }

        @SuppressLint("TrustAllX509TrustManager")
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @SuppressLint("TrustAllX509TrustManager")
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    }};

    /**
     * 设置不验证主机
     */
    private static final HostnameVerifier DO_NOT_VERIFY = (hostname, session) -> true;

    /**
     * 信任所有
     * @param connection
     * @return
     */
    private static SSLSocketFactory trustAllHosts(HttpsURLConnection connection) {
        SSLSocketFactory oldFactory = connection.getSSLSocketFactory();
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();
            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oldFactory;
    }
}
