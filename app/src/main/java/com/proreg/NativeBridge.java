package com.proreg;

public class NativeBridge {
    static { System.loadLibrary("proreg"); }
    public static native void init();
    public static native void setHeadshot(int pct);
    public static native void setAim(boolean on);
    public static native void setEsp(boolean on);
    public static native void setVpn(boolean on);
    public static native void setAntiban(boolean on);
    public static native float[] getEspRects();
}
