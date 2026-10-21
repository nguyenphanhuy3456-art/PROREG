package com.pixlemods;
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
    private View menuView, bubbleView;
    private AimOverlay aimOverlay;

    @Override public IBinder onBind(Intent i) { return null; }

    @Override public void onCreate() {
        super.onCreate();
        NotificationChannel ch = new NotificationChannel("pixle", "PIXLE Mods", NotificationManager.IMPORTANCE_LOW);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
        startForeground(1, new Notification.Builder(this, "pixle").setContentTitle("PIXLE Mods Active").setSmallIcon(android.R.drawable.ic_menu_compass).build());

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        int type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        // Lớp vẽ vòng tròn Aim
        aimOverlay = new AimOverlay(this);
        wm.addView(aimOverlay, new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT));

        // 1. Tạo Menu chính
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(0xCC000000);
        ll.setPadding(30, 30, 30, 30);

        TextView tv = new TextView(this);
        tv.setText("PIXLE MODS\nBám Đầu 100%\nSensi: Mượt");
        tv.setTextColor(0xFF00E5FF);
        tv.setPadding(10, 10, 10, 20); // Vùng chạm rộng hơn để dễ kéo
        ll.addView(tv);

        Switch swAim = new Switch(this);
        swAim.setText("Bật Bám Đầu");
        swAim.setTextColor(0xFFFFFFFF);
        swAim.setOnCheckedChangeListener((v, c) -> aimOverlay.setEnabled(c));
        ll.addView(swAim);

        Button btnHide = new Button(this);
        btnHide.setText("Thu nhỏ");
        btnHide.setBackgroundColor(0xFFFF3D71);
        ll.addView(btnHide);

        WindowManager.LayoutParams mlp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        mlp.gravity = Gravity.TOP | Gravity.START; mlp.x = 50; mlp.y = 200;
        wm.addView(ll, mlp);
        menuView = ll;

        // 2. Tạo Nút nổi (Bong bóng)
        TextView bubble = new TextView(this);
        bubble.setText("+"); bubble.setTextSize(30); bubble.setTextColor(Color.WHITE);
        bubble.setGravity(Gravity.CENTER);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL); shape.setColor(0xCC00E5FF);
        bubble.setBackground(shape);

        WindowManager.LayoutParams blp = new WindowManager.LayoutParams(
                120, 120, type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        blp.gravity = Gravity.TOP | Gravity.START; blp.x = 100; blp.y = 500;
        wm.addView(bubble, blp);
        bubbleView = bubble;
        bubbleView.setVisibility(View.GONE);

        // Sự kiện ẩn menu
        btnHide.setOnClickListener(v -> { 
            ll.setVisibility(View.GONE); 
            bubbleView.setVisibility(View.VISIBLE); 
        });

        // 3. Logic Kéo thả Nút nổi & Mở lại Menu
        bubble.setOnTouchListener(new View.OnTouchListener() {
            int initialX, initialY; float initialTouchX, initialTouchY; boolean isDragging = false;
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = blp.x; initialY = blp.y;
                        initialTouchX = event.getRawX(); initialTouchY = event.getRawY();
                        isDragging = false; return true;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) (event.getRawX() - initialTouchX);
                        int dy = (int) (event.getRawY() - initialTouchY);
                        if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {
                            isDragging = true; blp.x = initialX + dx; blp.y = initialY + dy;
                            wm.updateViewLayout(bubbleView, blp);
                        } return true;
                    case MotionEvent.ACTION_UP:
                        // Nếu chỉ chạm (không kéo) thì mở lại menu
                        if (!isDragging) { 
                            ll.setVisibility(View.VISIBLE); 
                            bubbleView.setVisibility(View.GONE); 
                        }
                        return true;
                } return false;
            }
        });

        // 4. Logic Kéo thả Menu chính (Chạm vào dòng chữ PIXLE MODS để kéo)
        tv.setOnTouchListener(new View.OnTouchListener() {
            int initialX, initialY; float initialTouchX, initialTouchY;
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = mlp.x; initialY = mlp.y;
                        initialTouchX = event.getRawX(); initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        mlp.x = initialX + (int) (event.getRawX() - initialTouchX);
                        mlp.y = initialY + (int) (event.getRawY() - initialTouchY);
                        wm.updateViewLayout(menuView, mlp);
                        return true;
                }
                return false;
            }
        });
    }

    @Override public void onDestroy() {
        if (menuView != null) wm.removeView(menuView);
        if (bubbleView != null) wm.removeView(bubbleView);
        if (aimOverlay != null) wm.removeView(aimOverlay);
        super.onDestroy();
    }
}
