package com.synaric.mutinodeprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 *
 * Created by Synaric on 2016/5/20 0020.
 */
public class SimpleProgressBar extends View {

    private float progress;
    private final Paint paint;
    private int foreColor;
    private int backColor;
    private int stroke;

    public SimpleProgressBar(Context context) {
        super(context);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public void setForeColor(int foreColor) {
        this.foreColor = foreColor;
        paint.setColor(foreColor);
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
        setBackgroundColor(backColor);
    }

    public void setStroke(int stroke) {
        this.stroke = stroke;
        paint.setStrokeWidth(stroke);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawLine(0, getHeight() / 2.0f, getWidth() * progress, getHeight() / 2.0f, paint);
    }
}
