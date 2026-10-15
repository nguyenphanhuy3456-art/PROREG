package com.proreg;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import java.io.File;

public final class AntiOB55 {
    private static final String TAG = "PROREG.AntiOB55";
    private static boolean enabled = true;

    private AntiOB55() {}

    private static final String[] PATHS = {
        "/data/local/tmp/frida-server",
        "/data/local/bin/frida-server",
        "/system/bin/frida-server",
        "/system/xbin/su", "/system/bin/su", "/sbin/su",
        "/data/adb/magisk", "/data/adb/modules",
        "/system/framework/XposedBridge.jar",
        "/system/lib64/libxposed_art.so"
    };

    public static void init(Context c) {
        enabled = AnoApplication.get().meta("antiban.enabled", "true").equals("true");
        Log.i(TAG, "init enabled=" + enabled);
        purge();
        NativeBridge.setAntiban(enabled);
    }

    public static void setEnabled(boolean e) {
        enabled = e;
        NativeBridge.setAntiban(e);
    }

    public static boolean isEnabled() { return enabled; }

    public static boolean isClean() {
        if (!enabled) return true;
        for (String p : PATHS) {
            if (new File(p).exists()) {
                Log.w(TAG, "detect: " + p);
                return false;
            }
        }
        return true;
    }

    private static void purge() {
        if (!enabled) return;
        for (String p : PATHS) {
            File f = new File(p);
            if (f.exists() && f.canWrite()) {
                Log.i(TAG, "purge " + p + " => " + f.delete());
            }
        }
    }

    public static String deviceFingerprint() {
        return Build.MANUFACTURER + "|" + Build.MODEL + "|" + Build.FINGERPRINT;
    }
}
