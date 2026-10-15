package com.proreg;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.util.List;

public class AppListAdapter extends BaseAdapter {
    private final Context ctx;
    private final List<DataModule.AppEntry> items;

    public AppListAdapter(Context c, List<DataModule.AppEntry> it) { ctx = c; items = it; }

    @Override public int getCount() { return items.size(); }
    @Override public Object getItem(int p) { return items.get(p); }
    @Override public long getItemId(int p) { return p; }

    @Override
    public View getView(int pos, View v, ViewGroup parent) {
        if (v == null) v = LayoutInflater.from(ctx).inflate(R.layout.appitem, parent, false);
        DataModule.AppEntry e = items.get(pos);
        ((ImageView) v.findViewById(R.id.imgIcon)).setImageDrawable(e.icon);
        ((TextView)  v.findViewById(R.id.txtLabel)).setText(e.label);
        ((TextView)  v.findViewById(R.id.txtPackage)).setText(e.pkg);
        v.findViewById(R.id.btnLaunch).setOnClickListener(x -> DataModule.launch(ctx, e.pkg));
        return v;
    }
}
