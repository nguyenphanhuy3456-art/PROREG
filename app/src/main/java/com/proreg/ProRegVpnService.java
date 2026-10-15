package com.proreg;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.FileInputStream;

public class ProRegVpnService extends VpnService implements Runnable {
    private static final String TAG = "PROREG.Vpn";
    public static final String ACTION_START = "com.proreg.VPN_START";
    public static final String ACTION_STOP  = "com.proreg.VPN_STOP";

    private ParcelFileDescriptor vpnInterface;
    private Thread worker;
    private volatile boolean running = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent != null ? intent.getAction() : null;
        if (ACTION_START.equals(action)) startVpn();
        else if (ACTION_STOP.equals(action)) stopVpn();
        return START_STICKY;
    }

    private void startVpn() {
        if (running) return;
        NotificationChannel ch = new NotificationChannel(
            getString(R.string.notif_channel), getString(R.string.notif_title),
            NotificationManager.IMPORTANCE_LOW);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
        Notification n = new Notification.Builder(this, getString(R.string.notif_channel))
            .setContentTitle(getString(R.string.vpn_session_name))
            .setSmallIcon(android.R.drawable.ic_lock_lock).build();
        startForeground(2, n);

        Builder b = new Builder();
        b.setSession(getString(R.string.vpn_session_name))
         .setMtu(1500)
         .addAddress("10.66.0.2", 32)
         .addRoute("0.0.0.0", 0)
         .addDnsServer("8.8.8.8")
         .addDnsServer("1.1.1.1");

        try {
            vpnInterface = b.establish();
            if (vpnInterface == null) { Log.e(TAG, "establish fail"); return; }
            running = true;
            worker = new Thread(this, "proreg-vpn");
            worker.start();
            Log.i(TAG, "VPN started");
        } catch (Exception e) { Log.e(TAG, "startVpn error", e); }
    }

    private void stopVpn() {
        running = false;
        try { if (vpnInterface != null) vpnInterface.close(); } catch (Exception ignored) {}
        vpnInterface = null;
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void run() {
        try (FileInputStream in = new FileInputStream(vpnInterface.getFileDescriptor())) {
            byte[] buf = new byte[32767];
            while (running) {
                int len = in.read(buf);
                if (len <= 0) break;
            }
        } catch (Exception e) { Log.e(TAG, "vpn loop error", e); }
    }

    @Override public void onRevoke() { stopVpn(); super.onRevoke(); }
    @Override public void onDestroy() { stopVpn(); super.onDestroy(); }
}
