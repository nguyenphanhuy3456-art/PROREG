package com.modssensi;

import android.app.*;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
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
        NotificationChannel ch = new NotificationChannel("sensi", "Mods Sensi", NotificationManager.IMPORTANCE_LOW);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
        startForeground(1, new Notification.Builder(this, "sensi").setContentTitle("Mods Sensi FFMAX Active").setSmallIcon(android.R.drawable.ic_menu_compass).build());

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        int type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        // 1. Lớp vẽ vòng tròn Aim
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
        tv.setText("MODS SENSI FFMAX\nAim Head: 90%\nSensi: ON");
        tv.setTextColor(0xFF00E5FF);
        ll.addView(tv);

        Switch swAim = new Switch(this);
        swAim.setText("Bật Aim & Nhạy Tâm");
        swAim.setTextColor(0xFFFFFFFF);
        swAim.setOnCheckedChangeListener((v, c) -> aimOverlay.setEnabled(c));
        ll.addView(swAim);

        Button btnHide = new Button(this);
        btnHide.setText("Thu nhỏ Menu");
        btnHide.setBackgroundColor(0xFFFF3D71);
        btnHide.setTextColor(Color.WHITE);
        ll.addView(btnHide);

        WindowManager.LayoutParams mlp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        mlp.gravity = Gravity.TOP | Gravity.START;
        mlp.x = 50; mlp.y = 200;
        wm.addView(ll, mlp);
        menuView = ll;

        // 3. Tạo Nút nổi (Bong bóng)
        TextView bubble = new TextView(this);
        bubble.setText("+");
        bubble.setTextSize(30);
        bubble.setTextColor(Color.WHITE);
        bubble.setGravity(Gravity.CENTER);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(0xCC00E5FF); // Màu xanh
        bubble.setBackground(shape);
        
        WindowManager.LayoutParams blp = new WindowManager.LayoutParams(
                120, 120, // Kích thước 120x120 pixel
                type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        blp.gravity = Gravity.TOP | Gravity.START;
        blp.x = 100; blp.y = 500;
        wm.addView(bubble, blp);
        bubbleView = bubble;
        
        // Mặc định ẩn bong bóng
        bubbleView.setVisibility(View.GONE);

        // Logic ẩn/hiện
        btnHide.setOnClickListener(v -> {
            ll.setVisibility(View.GONE);
            bubbleView.setVisibility(View.VISIBLE);
        });

        // Logic kéo thả bong bóng
        bubble.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private boolean isDragging = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = blp.x;
                        initialY = blp.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        isDragging = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) (event.getRawX() - initialTouchX);
                        int dy = (int) (event.getRawY() - initialTouchY);
                        // Nếu di chuyển quá 10 pixel thì tính là kéo thả
                        if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {
                            isDragging = true;
                            blp.x = initialX + dx;
                            blp.y = initialY + dy;
                            wm.updateViewLayout(bubbleView, blp);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Nếu không kéo thả (chỉ chạm) -> Mở lại menu
                        if (!isDragging) {
                            ll.setVisibility(View.VISIBLE);
                            bubbleView.setVisibility(View.GONE);
                        }
                        return true;
                }
                return false;
            }
        });

        // Hỗ trợ kéo thả menu chính bằng cách chạm vào dòng chữ tiêu đề
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
