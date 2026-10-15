package com.proreg;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import java.io.InputStream;
import java.util.Properties;

public class AnoApplication extends Application {
    private static final String TAG = "PROREG";
    private static AnoApplication sInstance;
    private Properties metadata = new Properties();

    public static AnoApplication get() { return sInstance; }
    public static Context ctx() { return sInstance.getApplicationContext(); }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        loadMetadata();
        AntiOB55.init(this);
        CoreModule.init(this);
        DataModule.init(this);
        Log.i(TAG, "PROREG started pkg=" + getPackageName());
    }

    private void loadMetadata() {
        try (InputStream is = getAssets().open("app-metadata.properties")) {
            metadata.load(is);
        } catch (Exception e) {
            Log.w(TAG, "metadata load fail", e);
        }
    }

    public String meta(String k, String def) { return metadata.getProperty(k, def); }
    public int metaInt(String k, int def) {
        try { return Integer.parseInt(metadata.getProperty(k, String.valueOf(def))); }
        catch (Exception e) { return def; }
    }

    @Override public void onConfigurationChanged(Configuration c) { super.onConfigurationChanged(c); }
    @Override public void onLowMemory() { super.onLowMemory(); System.gc(); }
}
