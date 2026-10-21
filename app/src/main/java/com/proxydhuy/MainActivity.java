package com.proxydhuy;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.*;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(40, 100, 40, 40);
        ll.setBackgroundColor(0xFF121212);

        TextView title = new TextView(this);
        title.setText("Proxy aim Dhuy\nCA Pass: DhuyV1");
        title.setTextColor(0xFF00E5FF);
        title.setTextSize(22);
        ll.addView(title);

        Button btnOverlay = new Button(this);
        btnOverlay.setText("1. Cấp quyền Overlay");
        btnOverlay.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()))));
        ll.addView(btnOverlay);

        Button btnAccess = new Button(this);
        btnAccess.setText("2. Cấp quyền Accessibility");
        btnAccess.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        ll.addView(btnAccess);

        Button btnStart = new Button(this);
        btnStart.setText("3. BẬT MENU");
        btnStart.setBackgroundColor(0xFF00E5FF);
        btnStart.setOnClickListener(v -> {
            startForegroundService(new Intent(this, FloatingMenu.class));
            finish();
        });
        ll.addView(btnStart);

        setContentView(ll);
    }
}
