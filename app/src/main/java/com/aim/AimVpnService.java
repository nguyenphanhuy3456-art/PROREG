package com.aim;

import android.app.*;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import java.io.FileInputStream;

public class AimVpnService extends VpnService implements Runnable {
    private ParcelFileDescriptor vpnInterface;
    private Thread worker;
    private volatile boolean running = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationChannel ch = new NotificationChannel("aim_vpn", "AIM VPN", NotificationManager.IMPORTANCE_LOW);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
        startForeground(1, new Notification.Builder(this, "aim_vpn").setContentTitle("AIM VPN Active").setSmallIcon(android.R.drawable.ic_menu_compass).build());

        Builder builder = new Builder();
        builder.setSession("AIM VPN").setMtu(1500).addAddress("10.8.0.2", 32).addRoute("0.0.0.0", 0).addDnsServer("1.1.1.1");
        try {
            vpnInterface = builder.establish();
            running = true;
            worker = new Thread(this, "aim-vpn-thread");
            worker.start();
        } catch (Exception ignored) {}
        return START_STICKY;
    }

    @Override public void run() {
        try (FileInputStream in = new FileInputStream(vpnInterface.getFileDescriptor())) {
            byte[] buf = new byte[32767];
            while (running) { if (in.read(buf) <= 0) break; }
        } catch (Exception ignored) {}
    }

    @Override public void onDestroy() {
        running = false;
        if (vpnInterface != null) try { vpnInterface.close(); } catch (Exception ignored) {}
        super.onDestroy();
    }
}
