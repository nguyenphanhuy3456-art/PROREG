package com.modssensi;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.*;
import android.graphics.Color;

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(40, 100, 40, 40);
        ll.setBackgroundColor(0xFF121212);

        TextView title = new TextView(this);
        title.setText("MODS SENSI FFMAX");
        title.setTextColor(0xFF00E5FF);
        title.setTextSize(24);
        ll.addView(title);

        EditText et = new EditText(this);
        et.setHint("Nhập mật khẩu...");
        et.setTextColor(Color.WHITE);
        ll.addView(et);

        Button btnLogin = new Button(this);
        btnLogin.setText("ĐĂNG NHẬP");
        btnLogin.setOnClickListener(v -> {
            if (et.getText().toString().trim().equals("FFMAX V2")) {
                if (!Settings.canDrawOverlays(this)) {
                    startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
                    Toast.makeText(this, "Cấp quyền Overlay rồi đăng nhập lại", Toast.LENGTH_LONG).show();
                    return;
                }
                startForegroundService(new Intent(this, FloatingMenu.class));
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                Toast.makeText(this, "Bật AimService và AntiReportService", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Sai mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        });
        ll.addView(btnLogin);
        setContentView(ll);
    }
}
