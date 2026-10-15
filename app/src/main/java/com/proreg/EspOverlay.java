package com.proreg;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class EspOverlay extends View {
    private final Paint box  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean enabled = false;

    public EspOverlay(Context c) {
        super(c);
        box.setStyle(Paint.Style.STROKE);
        box.setStrokeWidth(3f);
        box.setColor(Color.GREEN);
        line.setStrokeWidth(1.5f);
        line.setColor(Color.GREEN);
    }

    public void setEnabled(boolean on) { enabled = on; invalidate(); }

    @Override
    protected void onDraw(Canvas cv) {
        super.onDraw(cv);
        if (!enabled) return;
        float[] rects = NativeBridge.getEspRects();
        if (rects == null || rects.length == 0) { postInvalidateOnAnimation(); return; }
        int cx = getWidth()/2, cy = getHeight()/2;
        for (int i = 0; i + 3 < rects.length; i += 4) {
            cv.drawRect(rects[i], rects[i+1], rects[i+2], rects[i+3], box);
            float tx = (rects[i] + rects[i+2]) / 2f;
            float ty = (rects[i+1] + rects[i+3]) / 2f;
            cv.drawLine(cx, cy, tx, ty, line);
        }
        postInvalidateOnAnimation();
    }
}
