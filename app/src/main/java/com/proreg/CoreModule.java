package com.proreg;

import android.content.Context;
import android.util.Log;

public final class CoreModule {
    private static final String TAG = "PROREG.Core";
    private static boolean initialized = false;

    private CoreModule() {}

    public static void init(Context c) {
        if (initialized) return;
        initialized = true;
        NativeBridge.init();
        Log.i(TAG, "core init");
    }

    public static boolean isInitialized() { return initialized; }
}
