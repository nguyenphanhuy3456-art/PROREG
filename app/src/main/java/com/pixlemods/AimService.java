package com.pixlemods;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;

public class AimService extends AccessibilityService {
    private boolean isAiming = false;
    private long lastAimTime = 0;

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getPackageName() == null) return;
        if (!event.getPackageName().toString().contains("com.dts.freefiremax")) return;

        if (isAiming) return;
        long now = System.currentTimeMillis();
        if (now - lastAimTime < 150) return; // Nhạy màn hình (150ms)

        if (event.getEventType() == AccessibilityEvent.TYPE_TOUCH_INTERACTION_START) {
            isAiming = true; lastAimTime = now;
            executeAimHead();
        } else if (event.getEventType() == AccessibilityEvent.TYPE_TOUCH_INTERACTION_END) {
            isAiming = false;
        }
    }

    private void executeAimHead() {
        float w = getResources().getDisplayMetrics().widthPixels;
        float h = getResources().getDisplayMetrics().heightPixels;
        float cx = w / 2, cy = h / 2;
        
        // Bám đầu 100% (Khoảng cách Y - 150)
        float targetY = cy - 150;

        // Fix rung tâm, lố đầu: Chia làm 4 điểm (Ease-out) để dừng mượt
        Path path = new Path();
        path.moveTo(cx, cy);
        path.lineTo(cx, cy + (targetY - cy) * 0.4f); // Đoạn 1
        path.lineTo(cx, cy + (targetY - cy) * 0.8f); // Đoạn 2 (giảm tốc)
        path.lineTo(cx, targetY);                    // Điểm cuối (ghim)

        GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(path, 0, 120);
        dispatchGesture(new GestureDescription.Builder().addStroke(stroke).build(), null, null);
    }

    @Override public void onInterrupt() {}
}
