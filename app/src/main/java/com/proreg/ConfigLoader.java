package com.proreg;

import android.content.Context;
import android.util.Log;
import java.io.*;

public final class ConfigLoader {
    private static final String TAG = "PROREG.Config";

    private ConfigLoader() {}

    public static String loadAsset(Context c, String name) {
        try (InputStream is = c.getAssets().open(name)) {
            byte[] buf = new byte[is.available()];
            is.read(buf);
            return new String(buf, "UTF-8");
        } catch (Exception e) {
            Log.w(TAG, "load fail " + name, e);
            return "";
        }
    }

    public static boolean save(Context c, String fileName, String data) {
        try {
            File f = new File(c.getFilesDir(), fileName);
            try (FileOutputStream fos = new FileOutputStream(f)) {
                fos.write(data.getBytes("UTF-8"));
            }
            return true;
        } catch (Exception e) {
            Log.w(TAG, "save fail", e);
            return false;
        }
    }

    public static String load(Context c, String fileName) {
        try {
            File f = new File(c.getFilesDir(), fileName);
            if (!f.exists()) return "";
            byte[] buf = new byte[(int) f.length()];
            try (FileInputStream fis = new FileInputStream(f)) { fis.read(buf); }
            return new String(buf, "UTF-8");
        } catch (Exception e) { return ""; }
    }
}
