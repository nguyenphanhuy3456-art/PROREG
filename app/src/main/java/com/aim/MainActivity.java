package com.aim;

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
        title.setText("AIM MENU (NON-ROOT)");
        title.setTextColor(0xFF00E5FF);
        title.setTextSize(24);
        ll.addView(title);

        Button btnOverlay = new Button(this);
        btnOverlay.setText("1. Cấp quyền Overlay");
        btnOverlay.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()))));
        ll.addView(btnOverlay);

        Button btnVpn = new Button(this);
        btnVpn.setText("2. Bật VPN");
        btnVpn.setOnClickListener(v -> {
            Intent intent = android.net.VpnService.prepare(MainActivity.this);
            if (intent != null) startActivityForResult(intent, 1);
            else startVpn();
        });
        ll.addView(btnVpn);

        Button btnAccess = new Button(this);
        btnAccess.setText("3. Cấp quyền Accessibility");
        btnAccess.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        ll.addView(btnAccess);

        TextView note = new TextView(this);
        note.setText("\nHướng dẫn:\n- Cấp đủ 3 quyền trên.\n- Vào game kéo tâm để Aim 90%.\n- Anti-report tự động chặn nút Report.");
        note.setTextColor(0xFFFFFFFF);
        ll.addView(note);

        setContentView(ll);
    }

    private void startVpn() {
        startForegroundService(new Intent(this, AimVpnService.class));
        Toast.makeText(this, "VPN Started", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) startVpn();
    }
}
