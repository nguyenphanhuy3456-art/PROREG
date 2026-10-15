package com.proreg;

import android.app.*;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.*;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class FloatingService extends Service {
    private WindowManager wm;
    private View menuView;
    private EspOverlay espView;

    @Override public IBinder onBind(Intent i) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationChannel ch = new NotificationChannel(
            getString(R.string.notif_channel), getString(R.string.notif_title),
            NotificationManager.IMPORTANCE_LOW);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
        startForeground(1, new Notification.Builder(this, getString(R.string.notif_channel))
            .setContentTitle(getString(R.string.notif_title))
            .setSmallIcon(android.R.drawable.ic_menu_manage).build());

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        int type = Build.VERSION.SDK_INT >= 26
            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            : WindowManager.LayoutParams.TYPE_PHONE;

        espView = new EspOverlay(this);
        WindowManager.LayoutParams elp = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT);
        wm.addView(espView, elp);

        menuView = LayoutInflater.from(this).inflate(R.layout.floating_menu, null);
        WindowManager.LayoutParams mlp = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
        mlp.gravity = Gravity.TOP | Gravity.START;
        mlp.x = 40; mlp.y = 160;
        wm.addView(menuView, mlp);

        bind();
        NativeBridge.init();
    }

    private void bind() {
        TextView tv = menuView.findViewById(R.id.tvHs);
        SeekBar  sb = menuView.findViewById(R.id.sbHs);
        Switch  sA = menuView.findViewById(R.id.swAim);
        Switch  sE = menuView.findViewById(R.id.swEsp);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean u) {
                int pct = p + 10;
                tv.setText("HS: " + pct + "%");
                Aimhead.setHeadshotPercent(pct);
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });
        sA.setOnCheckedChangeListener((v,c) -> Aimhead.setEnabled(c));
        sE.setOnCheckedChangeListener((v,c) -> {
            NativeBridge.setEsp(c);
            espView.setEnabled(c);
        });

        final int[] start = new int[2];
        menuView.findViewById(R.id.tvTitle).setOnTouchListener((v,e) -> {
            WindowManager.LayoutParams lp = (WindowManager.LayoutParams) menuView.getLayoutParams();
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    start[0] = (int)e.getRawX() - lp.x;
                    start[1] = (int)e.getRawY() - lp.y;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    lp.x = (int)e.getRawX() - start[0];
                    lp.y = (int)e.getRawY() - start[1];
                    wm.updateViewLayout(menuView, lp);
                    return true;
            }
            return false;
        });
    }

    @Override
    public void onDestroy() {
        if (menuView != null) wm.removeView(menuView);
        if (espView  != null) wm.removeView(espView);
        super.onDestroy();
    }
}
