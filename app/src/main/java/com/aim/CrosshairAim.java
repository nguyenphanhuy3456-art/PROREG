package com.aim;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;

public class CrosshairAim extends AccessibilityService {
    private boolean isAiming = false;
    private long lastAimTime = 0;

    @Override public void onServiceConnected() { super.onServiceConnected(); }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Chỉ chạy khi app đang mở là FFMax
        if (event.getPackageName() == null || !event.getPackageName().toString().contains("com.dts.freefiremax")) {
            return;
        }

        // Ngăn chặn vòng lặp vô hạn
        if (isAiming) return;

        // Giới hạn tần suất (tối đa 1 lần mỗi 0.5 giây)
        long now = System.currentTimeMillis();
        if (now - lastAimTime < 500) return;

        // Kích hoạt khi bắt đầu chạm (kéo tâm)
        if (event.getEventType() == AccessibilityEvent.TYPE_TOUCH_INTERACTION_START) {
            isAiming = true;
            lastAimTime = now;
            applyAim();
        } else if (event.getEventType() == AccessibilityEvent.TYPE_TOUCH_INTERACTION_END) {
            isAiming = false;
        }
    }

    private void applyAim() {
        float w = getResources().getDisplayMetrics().widthPixels;
        float h = getResources().getDisplayMetrics().heightPixels;
        float cx = w / 2;
        float cy = h / 2;
        boolean isHead = Math.random() <= 0.90f;
        
        // Vuốt khoảng cách ngắn để tránh giật màn hình
        float targetX = cx + (float)(Math.random() * 4 - 2);
        float targetY = cy - (isHead ? 20 : 5) + (float)(Math.random() * 4 - 2);

        Path path = new Path();
        path.moveTo(cx, cy);
        path.lineTo(targetX, targetY);
        GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(path, 0, 20);
        dispatchGesture(new GestureDescription.Builder().addStroke(stroke).build(), null, null);
    }

    @Override public void onInterrupt() {}
}
