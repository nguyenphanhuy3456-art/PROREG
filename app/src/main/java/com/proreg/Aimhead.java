package com.proreg;

import android.util.Log;

public final class Aimhead {
    private static final String TAG = "PROREG.Aimhead";
    private static boolean enabled = false;
    private static int headshotPercent = 50;

    private Aimhead() {}

    public static void setEnabled(boolean on) {
        enabled = on;
        NativeBridge.setAim(on);
        Log.i(TAG, "enabled=" + on);
    }

    public static void setHeadshotPercent(int pct) {
        if (pct < 10) pct = 10;
        if (pct > 100) pct = 100;
        headshotPercent = pct;
        NativeBridge.setHeadshot(pct);
        Log.i(TAG, "headshot=" + pct + "%");
    }

    public static boolean isEnabled() { return enabled; }
    public static int getHeadshotPercent() { return headshotPercent; }
}
