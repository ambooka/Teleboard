package com.msah.teleboard.colorpicker;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

import com.msah.teleboard.R;


public class ColorCheckedViewCheckmark extends AppCompatImageView {

    private Context mContext;
    private int mSize;

    public ColorCheckedViewCheckmark(Context context, int size) {
        super(context);
        this.mContext = context;
        this.mSize = size;
        initView();
    }

    private void initView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mSize, mSize);
        layoutParams.gravity = Gravity.CENTER;
        this.setLayoutParams(layoutParams);
        this.setImageResource(R.drawable.ic_baseline_check_24);
    }
}
