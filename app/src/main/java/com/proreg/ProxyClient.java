package com.proreg;

import android.util.Log;
import java.io.*;
import java.net.*;

public final class ProxyClient {
    private static final String TAG = "PROREG.Proxy";
    private static String host = "";
    private static int port = 0;
    private static boolean enabled = false;

    private ProxyClient() {}

    public static void configure(String h, int p) {
        host = h; port = p;
        enabled = (h != null && !h.isEmpty() && p > 0);
        Log.i(TAG, "configure " + h + ":" + p + " enabled=" + enabled);
    }

    public static boolean isEnabled() { return enabled; }

    public static HttpURLConnection open(String url) throws IOException {
        URL u = new URL(url);
        if (enabled) {
            Proxy pr = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
            return (HttpURLConnection) u.openConnection(pr);
        }
        return (HttpURLConnection) u.openConnection();
    }

    public static String get(String url, int timeoutMs) throws IOException {
        HttpURLConnection conn = open(url);
        conn.setConnectTimeout(timeoutMs);
        conn.setReadTimeout(timeoutMs);
        conn.setRequestMethod("GET");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String l;
            while ((l = r.readLine()) != null) sb.append(l);
        } finally { conn.disconnect(); }
        return sb.toString();
    }

    public static boolean ping(String host, int port, int timeoutMs) {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (Exception e) { return false; }
    }
}
