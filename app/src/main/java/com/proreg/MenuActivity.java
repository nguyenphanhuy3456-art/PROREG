package com.proreg;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

public class MenuActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_menu);

        TextView tv = findViewById(R.id.txtHeadshot);
        SeekBar  sb = findViewById(R.id.sbHeadshot);
        Switch   swAim = findViewById(R.id.swAim);
        Switch   swEsp = findViewById(R.id.swEsp);
        Switch   swVpn = findViewById(R.id.swVpn);
        Switch   swAb  = findViewById(R.id.swAntiban);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean u) {
                int pct = p + 10;
                tv.setText(getString(R.string.menu_headshot, pct));
                Aimhead.setHeadshotPercent(pct);
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });

        swAim.setOnCheckedChangeListener((v,c) -> Aimhead.setEnabled(c));
        swEsp.setOnCheckedChangeListener((v,c) -> NativeBridge.setEsp(c));
        swVpn.setOnCheckedChangeListener((v,c) -> NativeBridge.setVpn(c));
        swAb .setOnCheckedChangeListener((v,c) -> AntiOB55.setEnabled(c));
    }
}
