package com.proreg;

import android.content.Context;
import android.content.Intent;
import android.content.pm.*;
import android.graphics.drawable.Drawable;
import java.util.*;

public final class DataModule {

    public static final class AppEntry {
        public final String label;
        public final String pkg;
        public final Drawable icon;
        AppEntry(String l, String p, Drawable d) { label = l; pkg = p; icon = d; }
    }

    private DataModule() {}

    public static void init(Context c) { }

    public static List<AppEntry> listLaunchable() {
        Context c = AnoApplication.ctx();
        PackageManager pm = c.getPackageManager();
        Intent main = new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> ris = pm.queryIntentActivities(main, 0);
        List<AppEntry> out = new ArrayList<>(ris.size());
        for (ResolveInfo ri : ris) {
            String pkg = ri.activityInfo.packageName;
            String lbl = ri.loadLabel(pm).toString();
            Drawable ic = ri.loadIcon(pm);
            out.add(new AppEntry(lbl, pkg, ic));
        }
        Collections.sort(out, (a, b) -> a.label.compareToIgnoreCase(b.label));
        return out;
    }

    public static void launch(Context c, String pkg) {
        Intent i = c.getPackageManager().getLaunchIntentForPackage(pkg);
        if (i != null) {
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);
        }
    }
}
