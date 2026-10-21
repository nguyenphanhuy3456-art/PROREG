package com.proxydhuy;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;

public class AimService extends AccessibilityService {
    private boolean isAiming = false;
    private long lastAimTime = 0;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getPackageName() == null) return;
        String pkg = event.getPackageName().toString();
        if (!pkg.contains("com.dts.freefiremax") && !pkg.contains("com.dts.freefireth")) return;

        if (isAiming) return;
        long now = System.currentTimeMillis();
        if (now - lastAimTime < 400) return;

        if (event.getEventType() == AccessibilityEvent.TYPE_TOUCH_INTERACTION_START) {
            isAiming = true;
            lastAimTime = now;
            executeSmoothAim();
        } else if (event.getEventType() == AccessibilityEvent.TYPE_TOUCH_INTERACTION_END) {
            isAiming = false;
        }
    }

    private void executeSmoothAim() {
        float w = getResources().getDisplayMetrics().widthPixels;
        float h = getResources().getDisplayMetrics().heightPixels;
        float cx = w / 2;
        float cy = h / 2;
        
        boolean isHead = Math.random() <= 0.90f;
        float targetY = cy - (isHead ? 120 : 40);

        Path path = new Path();
        path.moveTo(cx, cy);
        path.lineTo(cx + (targetY - cy) * 0.3f, cy + (targetY - cy) * 0.3f);
        path.lineTo(cx + (targetY - cy) * 0.7f, cy + (targetY - cy) * 0.7f);
        path.lineTo(cx, targetY);

        GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(path, 0, 150);
        dispatchGesture(new GestureDescription.Builder().addStroke(stroke).build(), null, null);
    }

    @Override public void onInterrupt() {}
}
