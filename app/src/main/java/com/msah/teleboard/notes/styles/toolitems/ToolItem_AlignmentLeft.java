package com.msah.teleboard.notes.styles.toolitems;

import android.content.Context;
import android.text.Layout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.msah.teleboard.R;
import com.msah.teleboard.notes.styles.IStyle;
import com.msah.teleboard.notes.styles.toolitems.styles.Style_Alignment;
import com.msah.teleboard.utils.Util;
import com.msah.teleboard.notes.views.CustomEditText;


public class ToolItem_AlignmentLeft extends ToolItem_Abstract {

    @Override
    public IToolItem_Updater getToolItemUpdater() {
        return null;
    }

    @Override
    public IStyle getStyle() {
        if (mStyle == null) {
            CustomEditText editText = this.getEditText();
            mStyle = new Style_Alignment(editText, (ImageView) mToolItemView, Layout.Alignment.ALIGN_NORMAL);
        }
        return mStyle;
    }

    @Override
    public View getView(Context context) {
        if (null == context) {
            return mToolItemView;
        }
        if (mToolItemView == null) {
            ImageView imageView = new ImageView(context);
            int size = Util.getPixelByDp(context, 40);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.drawable.ic_baseline_format_align_left_24);
            imageView.bringToFront();
            mToolItemView = imageView;
        }

        return mToolItemView;
    }

    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
        return;
    }
}

