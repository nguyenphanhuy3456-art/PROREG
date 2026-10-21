package com.aim;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;

public class CrosshairAim extends AccessibilityService {
    @Override public void onServiceConnected() { super.onServiceConnected(); }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            applyAim();
        }
    }

    private void applyAim() {
        float w = getResources().getDisplayMetrics().widthPixels;
        float h = getResources().getDisplayMetrics().heightPixels;
        float cx = w / 2;
        float cy = h / 2;
        boolean isHead = Math.random() <= 0.90f;
        float targetX = cx + (float)(Math.random() * 10 - 5);
        float targetY = cy - (isHead ? 150 : 50) + (float)(Math.random() * 10 - 5);

        Path path = new Path();
        path.moveTo(cx, cy);
        path.lineTo(targetX, targetY);
        GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(path, 0, 50);
        dispatchGesture(new GestureDescription.Builder().addStroke(stroke).build(), null, null);
    }

    @Override public void onInterrupt() {}
}
