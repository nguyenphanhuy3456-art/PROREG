package com.proxydhuy;

import android.app.*;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.*;
import android.widget.*;

public class FloatingMenu extends Service {
    private WindowManager wm;
    private View menuView;
    private AimOverlay aimOverlay;

    @Override public IBinder onBind(Intent i) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationChannel ch = new NotificationChannel("dhuy", "Proxy aim Dhuy", NotificationManager.IMPORTANCE_LOW);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
        startForeground(1, new Notification.Builder(this, "dhuy").setContentTitle("Proxy aim Dhuy Active").setSmallIcon(android.R.drawable.ic_menu_compass).build());

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        int type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        aimOverlay = new AimOverlay(this);
        WindowManager.LayoutParams olp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
        wm.addView(aimOverlay, olp);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(0xCC000000);
        ll.setPadding(30, 30, 30, 30);

        TextView tv = new TextView(this);
        tv.setText("PROXY AIM DHUY\nHeadshot: 90%\nSmooth OB55: ON");
        tv.setTextColor(0xFF00E5FF);
        ll.addView(tv);

        Switch swAim = new Switch(this);
        swAim.setText("Aim Head (90%)");
        swAim.setTextColor(0xFFFFFFFF);
        swAim.setOnCheckedChangeListener((v, c) -> aimOverlay.setEnabled(c));
        ll.addView(swAim);

        WindowManager.LayoutParams mlp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        mlp.gravity = Gravity.TOP | Gravity.START;
        mlp.x = 50; mlp.y = 200;
        wm.addView(ll, mlp);
        
        menuView = ll;
    }

    @Override
    public void onDestroy() {
        if (menuView != null) wm.removeView(menuView);
        if (aimOverlay != null) wm.removeView(aimOverlay);
        super.onDestroy();
    }
}
