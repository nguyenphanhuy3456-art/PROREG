package com.proreg;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.*;

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_login);

        EditText edt = findViewById(R.id.edtPass);
        Button   btn = findViewById(R.id.btnLogin);

        btn.setOnClickListener(v -> {
            String pass = AnoApplication.get().meta("app.password", "proreg2026");
            if (!pass.equals(edt.getText().toString().trim())) {
                Toast.makeText(this, R.string.login_wrong, Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())));
                Toast.makeText(this, R.string.login_overlay, Toast.LENGTH_LONG).show();
                return;
            }
            startForegroundService(new Intent(this, FloatingService.class));
            startActivity(new Intent(this, MenuActivity.class));
            finish();
        });
    }
}
