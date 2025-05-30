package com.msah.teleboard.notes.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.msah.teleboard.utils.Constants;

public class HrSpan extends ReplacementSpan implements Span {

    private int mScreenWidth;

    private static final float p = 1;

    public HrSpan() {
        mScreenWidth = Constants.SCREEN_WIDTH;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        return (int) (mScreenWidth * p);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        paint.setStrokeWidth(1);
        int lineY = top + (bottom - top) / 2;
        canvas.drawLine(x + (int) (mScreenWidth * (1 - p) / 2), lineY, x + (int) (mScreenWidth * p), lineY, paint);
    }

    @Override
    public String getHtml() {
        return "<hr />";
    }
}

