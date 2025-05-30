package com.msah.teleboard.colorpicker;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;


public class ColorCheckedView extends View {

    private Context mContext;

    private int mSize;

    public ColorCheckedView(Context context, int size) {
        super(context);
        this.mContext = context;
        this.mSize = size;
        initView();
    }

    private void initView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mSize, mSize);
        layoutParams.gravity = Gravity.CENTER;
        this.setLayoutParams(layoutParams);
        this.setBackgroundColor(Color.WHITE);
    }
}
