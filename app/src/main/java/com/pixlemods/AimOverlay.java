package com.pixlemods;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class AimOverlay extends View {
    private Paint circlePaint;
    private boolean enabled = false;

    public AimOverlay(Context c) {
        super(c);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(4f);
        circlePaint.setColor(Color.GREEN);
    }

    public void setEnabled(boolean on) { enabled = on; invalidate(); }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!enabled) return;
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        // Vẽ vòng tròn Aim (bán kính 20%)
        canvas.drawCircle(cx, cy, Math.min(getWidth(), getHeight()) * 0.20f, circlePaint);
        postInvalidateOnAnimation();
    }
}
