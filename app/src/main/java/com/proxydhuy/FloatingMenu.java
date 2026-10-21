package com.proxydhuy;

import android.app.*;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.*;
import android.widget.*;

public class FloatingMenu extends Service {
    private WindowManager wm;
    private View menuView;
    private View bubbleView;
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

        // 1. Tạo lớp vẽ vòng tròn xanh
        aimOverlay = new AimOverlay(this);
        WindowManager.LayoutParams olp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
        wm.addView(aimOverlay, olp);

        // 2. Tạo Menu chính
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

        // Nút Thu nhỏ Menu
        Button btnHide = new Button(this);
        btnHide.setText("Thu nhỏ Menu");
        btnHide.setBackgroundColor(0xFFFF3D71);
        btnHide.setTextColor(Color.WHITE);
        btnHide.setOnClickListener(v -> {
            ll.setVisibility(View.GONE);          // Ẩn menu chính
            bubbleView.setVisibility(View.VISIBLE); // Hiện bong bóng nhỏ
        });
        ll.addView(btnHide);

        WindowManager.LayoutParams mlp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        mlp.gravity = Gravity.TOP | Gravity.START;
        mlp.x = 50; mlp.y = 200;
        wm.addView(ll, mlp);
        menuView = ll;

        // 3. Tạo Bong bóng nhỏ (+) để mở lại menu
        TextView bubble = new TextView(this);
        bubble.setText("+");
        bubble.setTextSize(30);
        bubble.setTextColor(Color.WHITE);
        bubble.setGravity(Gravity.CENTER);
        bubble.setBackgroundColor(0xCC00E5FF); // Màu xanh
        bubble.setWidth(120);
        bubble.setHeight(120);
        
        WindowManager.LayoutParams blp = new WindowManager.LayoutParams(
                120, 120, type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        blp.gravity = Gravity.TOP | Gravity.START;
        blp.x = 50; blp.y = 200;
        wm.addView(bubble, blp);
        bubbleView = bubble;
        
        // Mặc định ẩn bong bóng, hiện menu
        bubbleView.setVisibility(View.GONE);

        // Bấm vào bong bóng để mở lại menu
        bubble.setOnClickListener(v -> {
            ll.setVisibility(View.VISIBLE);
            bubbleView.setVisibility(View.GONE);
        });

        // Hỗ trợ kéo thả menu bằng cách chạm vào TextView tiêu đề
        final int[] start = new int[2];
        tv.setOnTouchListener((v, e) -> {
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
        if (bubbleView != null) wm.removeView(bubbleView);
        if (aimOverlay != null) wm.removeView(aimOverlay);
        super.onDestroy();
    }
}
